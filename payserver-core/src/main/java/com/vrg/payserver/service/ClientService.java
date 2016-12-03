package com.vrg.payserver.service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.vrg.payserver.ServerCoreService;
import com.vrg.payserver.dao.RechargeFailLogMapper;
import com.vrg.payserver.dao.RechargeLogMapper;
import com.vrg.payserver.dao.RechargeRecordStatusMapper;
import com.vrg.payserver.service.vo.ClientNewRechargeRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeResponse;
import com.vrg.payserver.service.vo.ClientNewRechargeResponseData;
import com.vrg.payserver.service.vo.RechargeRecordBase;
import com.vrg.payserver.util.ErrorCode;
import com.vrg.payserver.util.HttpUtils;
import com.vrg.payserver.util.Log;
import com.vrg.payserver.util.SignCore;

@Service
public class ClientService {
	private static final Integer TRADE_LOCK = 0;
	private static final Integer LOG_LOCK = 1;
	private static final String ALLOWED_PAY_CURRENCY = "okPayCurrency";
	private static final String NOT_ALLOWED_PAY_CURRENCY = "nokPayCurrency";
	@Autowired
	private RechargeLogMapper rechargeLogMapper;
	@Autowired
	private RechargeFailLogMapper rechargeFailLogMapper;
	@Autowired
	private RechargeRecordStatusMapper rechargeRecordStatusMapper;
	@Autowired
	private ParamRepository paramRepository;

	@Autowired
	private ServerCoreService serverCoreService;

	@Value("${xgsdk.server.envId:0}")
	private String envId;

	private static AtomicLong tradeNoSegmentStart = new AtomicLong(0);
	private static volatile long tradeNoSegmentEnd = 0;
	private static AtomicLong chargeLogIdStart = new AtomicLong(0);
	private static volatile long chargeLogIdEnd = 0;
	private static final long TOTAL_STEP = 100;

	public String getEnvId() {
		return envId;
	}

	/*
	 * 订单号分段，数据库中每次增长100，此分段数值每次使用自增1，自增达到100之后重新从数据库中获取新值
	 */
	private long getTradeNoSegment() {
		long segment;
		while ((segment = tradeNoSegmentStart.incrementAndGet()) > tradeNoSegmentEnd) {
			synchronized (TRADE_LOCK) {
				if (tradeNoSegmentStart.get() > tradeNoSegmentEnd) {
					tradeNoSegmentStart.set(rechargeRecordStatusMapper.getTradeNoSegment());
					tradeNoSegmentEnd = tradeNoSegmentStart.get() + TOTAL_STEP;
				}
			}
		}
		return segment;
	}

	/*
	 * 充值日志id，数据库中每次增长100，此分段数值每次使用自增1，自增达到100之后重新从数据库中获取新值
	 */
	private long getChargeLogId() {
		long logId;
		while ((logId = chargeLogIdStart.incrementAndGet()) > chargeLogIdEnd) {
			synchronized (LOG_LOCK) {
				if (chargeLogIdStart.get() > chargeLogIdEnd) {
					chargeLogIdStart.set(rechargeRecordStatusMapper.getChargeLogId());
					chargeLogIdEnd = chargeLogIdStart.get() + TOTAL_STEP;
				}
			}
		}
		return logId;
	}

	private String getTradeNo() {
		StringBuffer sb = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		int mm = cal.get(Calendar.MONTH) + 1; // Make prefix start at 1, not 0.
		String prefix;
		switch (mm) {
			case 10:
				prefix = "a";
				break;
			case 11:
				prefix = "b";
				break;
			case 12:
				prefix = "c";
				break;
			default:
				prefix = String.valueOf(mm);
				break;
		}

		String year = String.valueOf(cal.get(Calendar.YEAR)).substring(2);

		String sdkVersion = "2";
		String nowTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cal.getTime());
		String md = Hex.encodeHexString(DigestUtils.md5(nowTime));

		return sb.append(prefix).append(year).append(envId).append(sdkVersion).append(md.substring(13, 14)).append(String.valueOf(getTradeNoSegment())).toString();
	}

	/**
	 * 一般用户从数据库复制订单的情况，好比苹果自动续订
	 */
	public RechargeRecordBase createOrder(RechargeRecordBase rechargeRecord) {
		// 获取订单号
		long chargeLogId = getChargeLogId();
		String tradeNo = getTradeNo();
		rechargeRecord.setTradeNo(tradeNo);
		rechargeRecord.setChargeLogId(chargeLogId);
		rechargeRecord.setStatus(RechargeRecordBase.STATUS_INIT);
		rechargeRecord.setSearchChannelOrderTimes(0);
		rechargeRecord.setExceptionInfo(null);
		rechargeRecord.setStateCode(null);
		// 创建订单
		rechargeRecordStatusMapper.create(rechargeRecord);
		return rechargeRecord;
	}

	public ClientNewRechargeResponse createOrder(ClientNewRechargeRequest request) {
		ClientNewRechargeResponse response = this.createNewRechargeResponse(request);

		// 构建充值交流状态
		RechargeRecordBase rechargeRecordStatus = new RechargeRecordBase();
		this.parseNewRechargeRequest(rechargeRecordStatus, request);
		rechargeRecordStatus.setStatus(RechargeRecordBase.STATUS_INIT);

		// 获取订单号
		long chargeLogId = getChargeLogId();
		String tradeNo = getTradeNo();
		rechargeRecordStatus.setTradeNo(tradeNo);
		rechargeRecordStatus.setChargeLogId(chargeLogId);

		// 创建订单
		rechargeRecordStatusMapper.create(rechargeRecordStatus);

		// 构建返回值
		response.setCode(ErrorCode.SUCCESS);
		response.setMsg(ErrorCode.MSG_OK);
		response.getData().setTradeNo(rechargeRecordStatus.getTradeNo());
		return response;
	}

	/**
	 * 记录充值交易状态.
	 *
	 * @param request
	 * @return
	 */
	@Transactional
	public ClientNewRechargeResponse updateOrder(ClientNewRechargeRequest request) {
		ClientNewRechargeResponse response = this.createNewRechargeResponse(request);
		// 验证订单是否存在
		String tradeNo = request.getTradeNo();
		RechargeRecordBase rechargeRecordStatus = rechargeRecordStatusMapper.queryByTradeNo(tradeNo);
		Log.changeLogContextTypeToAppErr();
		if (rechargeRecordStatus == null) {
			response.setCode(ErrorCode.ERR_ORDERID_NOTEXIST);
			response.setMsg(MessageFormat.format("The order[{0}] has finished, or it do not exist.", tradeNo));
			return response;
		}

		int status = rechargeRecordStatus.getStatus();
		if (status != RechargeRecordBase.STATUS_INIT && status != RechargeRecordBase.STATUS_WAITPAY) {
			response.setCode(ErrorCode.ERR_SYSTEM);
			response.setMsg(MessageFormat.format("The order[{0}] is not init status or waitpay status, or it is [{1}].", tradeNo, status));
			return response;
		}

		// 构建充值交流状态
		this.parseNewRechargeRequest(rechargeRecordStatus, request);
		rechargeRecordStatus.setStatus(RechargeRecordBase.STATUS_WAITPAY);
		rechargeRecordStatus.setPaidTime(new Date());
		rechargeRecordStatusMapper.update(rechargeRecordStatus);

		// 构建返回值
		response.setCode(ErrorCode.SUCCESS);
		response.setMsg(MessageFormat.format("The order[{0}] has been updated", tradeNo));
		response.getData().setTradeNo(rechargeRecordStatus.getTradeNo());
		Log.changeLogContextTypeToInfo();
		return response;
	}

	public ClientNewRechargeResponse createNewRechargeResponse(ClientNewRechargeRequest request) {
		ClientNewRechargeResponse response = new ClientNewRechargeResponse();
		response.setCode(ErrorCode.SUCCESS);
		ClientNewRechargeResponseData data = new ClientNewRechargeResponseData();
//		data.setChannelPartnerId(request.getChannelPartnerId());
//		data.setChannelId(request.getChannelId());
//		data.setChannelTradeNo(request.getChannelTradeNo());
		data.setCustomInfo(request.getCustomInfo());
		data.setPartnerId(request.getPartnerId());
		response.setData(data);
		return response;
	}

	private void parseNewRechargeRequest(RechargeRecordBase rechargeRecordStatus, ClientNewRechargeRequest request) {
//		 rechargeRecordStatus.setChargeLogId(chargeLogId);
		 rechargeRecordStatus.setTradeNo(request.getTradeNo());
//		if (!StringUtils.isEmpty(request.getChannelPartnerId())) {
//			rechargeRecordStatus.setChannelPartnerId(request.getChannelPartnerId());
//		}
		if (!StringUtils.isEmpty(request.getChannelId())) {
			rechargeRecordStatus.setChannelId(request.getChannelId());
		}
		if (!StringUtils.isEmpty(request.getPaidAmount())) {
			rechargeRecordStatus.setPaidAmount(NumberUtils.toInt(request.getPaidAmount(), 0));
		}
//		if (!StringUtils.isEmpty(request.getChannelTradeNo())) {
//			rechargeRecordStatus.setChannelTradeNo(request.getChannelTradeNo());
//		}
		if (!StringUtils.isEmpty(request.getPartnerId())) {
			rechargeRecordStatus.setPartnerId(request.getPartnerId());
		}
		if (!StringUtils.isEmpty(request.getCustomInfo())) {
			rechargeRecordStatus.setCustomInfo(request.getCustomInfo());
		}
		
		rechargeRecordStatus.setSubAgentNotifyUrl(request.getNotifyUrl());
	}

//	public ClientQueryOrderStatusResponse queryOrderStatus(ClientQueryOrderStatusRequest request) {
//		String orderId = request.getTradeNo();
//		// 查询订单
//		RechargeRecordBase rechargeRecordStatus = rechargeRecordStatusMapper.queryByTradeNo(orderId);
//		// 如果存在，则获取状态后返回
//		if (rechargeRecordStatus != null) {
//			ClientQueryOrderStatusResponse response = new ClientQueryOrderStatusResponse();
//			response.setCode(ErrorCode.SUCCESS);
//			response.setMsg("success");
//			ClientQueryOrderStatusResponseData data = new ClientQueryOrderStatusResponseData();
//			data.setChannelId(rechargeRecordStatus.getChannelId());
//			data.setGameTradeNo(rechargeRecordStatus.getGameTradeNo());
//			data.setTradeNo(orderId);
//			data.setXgAppId(rechargeRecordStatus.getXgAppId());
//			data.setStatus(String.valueOf(rechargeRecordStatus.getStatus()));
//			response.setData(data);
//			return response;
//		}
//		// 如果不存在，则查询日志表
//		rechargeRecordStatus = new RechargeRecordBase();
//		rechargeRecordStatus.setTradeNo(orderId);
//		RechargeRecordBase rechargeLog = rechargeLogMapper.queryByTradeNo(rechargeRecordStatus);
//		// 如果存在，则返回订单状态
//		if (rechargeLog != null) {
//			ClientQueryOrderStatusResponse response = new ClientQueryOrderStatusResponse();
//			response.setCode(ErrorCode.SUCCESS);
//			response.setMsg("success");
//			ClientQueryOrderStatusResponseData data = new ClientQueryOrderStatusResponseData();
//			data.setChannelId(rechargeLog.getChannelId());
//			data.setGameTradeNo(rechargeLog.getGameTradeNo());
//			data.setTradeNo(orderId);
//			data.setXgAppId(rechargeLog.getXgAppId());
//			data.setStatus(String.valueOf(rechargeLog.getStatus()));
//			response.setData(data);
//			return response;
//		}
//		// 如果仍不存在，则查询废弃表
//		RechargeRecordBase rechargeFailLog = rechargeFailLogMapper.queryByTradeNo(rechargeRecordStatus);
//		// 如果存在，则返回订单状态
//		if (rechargeFailLog != null) {
//			ClientQueryOrderStatusResponse response = new ClientQueryOrderStatusResponse();
//			response.setCode(ErrorCode.SUCCESS);
//			response.setMsg("success");
//			ClientQueryOrderStatusResponseData data = new ClientQueryOrderStatusResponseData();
//			data.setChannelId(rechargeFailLog.getChannelId());
//			data.setGameTradeNo(rechargeFailLog.getGameTradeNo());
//			data.setTradeNo(orderId);
//			data.setXgAppId(rechargeFailLog.getXgAppId());
//			data.setStatus(String.valueOf(rechargeFailLog.getStatus()));
//			response.setData(data);
//			return response;
//		} else {
//			// 如果还不存在，则返回订单不存在
//			ClientQueryOrderStatusResponse response = new ClientQueryOrderStatusResponse();
//			response.setCode(ErrorCode.ERR_ORDERID_NOTEXIST);
//			response.setMsg(MessageFormat.format("The order[{0}] can not be found.", orderId));
//			Log.changeLogContextTypeToAppErr();
//			return response;
//		}
//	}
	
	public JSONObject doGetForClient(String url, Map<String, String> paramsMap, String appKey, String ipAddress) {
		String orginString = SignCore.getSigningString(paramsMap, "sign", false, false, false) + ":" + "createOrder" + ":" + appKey;
		String sign = Base64.getEncoder().encodeToString(DigestUtils.md5(orginString)).replaceAll("\n", "").replaceAll("\r", "");
		paramsMap.put("sign", sign);

		JSONObject resultObject = new JSONObject();
		StringBuilder stringBuilder = new StringBuilder(url);
		if (paramsMap != null) {
			boolean firstFlag = true;
			for(Map.Entry<String, String> entry: paramsMap.entrySet()) {
				if (firstFlag) {
					stringBuilder.append("?");
					firstFlag = false;
				} else {
					stringBuilder.append("&");
				}
				
				stringBuilder.append(entry.getKey());
				stringBuilder.append("=");
	
					stringBuilder.append(entry.getValue());
	
			}
		}
		
		Map<String, String> headers = new HashMap<>();
		String result = HttpUtils.doGet(stringBuilder.toString(), headers);
		
		if (StringUtils.isEmpty(result)) {
			resultObject.put("code", "2");
			resultObject.put("msg", "系统繁忙");
			return resultObject;
		}
		
		return JSONObject.parseObject(result);
	}
	
	public boolean verifySign(Object request, String sign, String partnerId, String channelId) {
		String appKey = paramRepository.getServerSecret(partnerId);
		String okSign = SignCore.xgSign(request, SignCore.SIGN_FIELD_NAME, appKey);
		System.out.println(okSign);
		boolean verifyResult = StringUtils.equalsIgnoreCase(sign, okSign);
		return verifyResult;
	}
	
}
