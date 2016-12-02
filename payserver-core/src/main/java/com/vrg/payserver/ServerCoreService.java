package com.vrg.payserver;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrg.payserver.dao.RechargeFailLogMapper;
import com.vrg.payserver.dao.RechargeLogMapper;
import com.vrg.payserver.dao.RechargeRecordStatusMapper;
import com.vrg.payserver.repository.ChannelParamRepository;
import com.vrg.payserver.repository.PartnerRespository;
import com.vrg.payserver.service.ClientService;
import com.vrg.payserver.service.IServerCoreService;
import com.vrg.payserver.service.ParamRepository;
import com.vrg.payserver.service.RechargeRequestExceptionService;
import com.vrg.payserver.service.vo.ChannelNotifyRequest;
import com.vrg.payserver.service.vo.ChannelRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeResponse;
import com.vrg.payserver.service.vo.NotifySubAgentRequest;
import com.vrg.payserver.service.vo.NotifySubAgentResponse;
import com.vrg.payserver.service.vo.RechargeRecordBase;
import com.vrg.payserver.service.vo.RechargeRequestException;
import com.vrg.payserver.service.vo.RechargeRequestLog;
import com.vrg.payserver.util.ErrorCode;
import com.vrg.payserver.util.HttpUtils;
import com.vrg.payserver.util.Log;
import com.vrg.payserver.util.SignCore;

@Service
public class ServerCoreService implements IServerCoreService {
	private static final Logger rechargeLog = LoggerFactory.getLogger("RECHARGE_LOGGER");
	private static final String GET_GAME_WEBPAY_PRODUCTS_URL = "GET_GAME_WEBPAY_PRODUCTS_URL";
	public static final String NOTIFY_GAME_WITH_CHANNEL_TRADE_NO = "NOTIFY_GAME_WITH_CHANNEL_TRADE_NO";
	public static final String NOTIFY_GAME_WITH_PAY_TYPE = "NOTIFY_GAME_WITH_PAY_TYPE";
	private static final String[] EXCEPTION_CODE = new String[] { ErrorCode.ERR_CHANNEL_TRADE_NO_NOTSAME, ErrorCode.ERR_CHANNEL_APP_ID_NOTSAME, ErrorCode.ERR_UID_NOTSAME, ErrorCode.ERR_PAID_TIME_OUT, ErrorCode.ERR_PRODUCT_ID_NOTSAME,
			ErrorCode.ERR_PRODUCT_NAME_NOTSAME, ErrorCode.ERR_PRODUCT_QUANTITY_NOTSAME, ErrorCode.ERR_PAID_AMOUNT_NOTSAME, ErrorCode.ERR_SEARCH_CHANNEL_ORDER_TIMEOUT, ErrorCode.ERR_SEARCH_CHANNEL_ORDER_FAIL, ErrorCode.ERR_BLACKLIST_IP };
	
//	static {
//		Arrays.sort(EXCEPTION_CODE);
//	}

	@Value("${xgsdk.recharge_request_log.save:true}")
	private boolean saveRechargeRequestLog;
	@Autowired
	private ChannelParamRepository channelParamRepository;
	
	@Autowired
	private RechargeRecordStatusMapper rechargeRecordStatusMapper;
	
	@Autowired
	private RechargeLogMapper rechargeLogMapper;
	
	@Autowired
	private RechargeFailLogMapper rechargeFailLogMapper;

	@Autowired
	private ParamRepository paramRepository;
	
	@Autowired
	private ClientService gameClientService;
	
	@Autowired
	private PartnerRespository partnerRespository;
	
	@Autowired
	private RechargeRequestLogService rechargeRequestLogService;
	
	@Autowired
	private RechargeRequestExceptionService rechargeRequestExceptionService;
	
	@Override
	public String getParamValue(String planId, String channelId, String paramName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getParamValues(String xgAppId, String channelId, String paramName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RechargeRecordBase> getUnPaidRechargeRecordByUid(String xgAppId, String channelId, String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RechargeRecordBase> getWaitPayRechargeRecordByUid(String xgAppId, String channelId, String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RechargeRecordBase getRechargeRecordByProductId(String xgAppId, String channelId, String uid,
			String productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RechargeRecordBase getRechargeRecordByTradeNo(String tradeNo) {
		if (StringUtils.isEmpty(tradeNo)) {
			return null;
		}
		RechargeRecordBase rechargeRecordBase = rechargeRecordStatusMapper.queryByTradeNo(tradeNo);
		if (rechargeRecordBase == null) {
			RechargeRecordBase conditions = new RechargeRecordBase();
			conditions.setTradeNo(tradeNo);
			rechargeRecordBase = rechargeLogMapper.queryByTradeNo(conditions);
			// TOTO 订单回捞流程需要考虑
//			if (rechargeRecordBase == null) {
//				// 查询作废日志表
//				rechargeRecordBase = rechargeFailLogMapper.queryByTradeNo(conditions);
//				if (rechargeRecordBase != null) {
//					// 如果存在，则执行回捞
//					rechargeRecordStatusMapper.createFromFailLog(conditions);
//					rechargeFailLogMapper.delete(conditions);
//				} else {
//					// 如果不存在，则返回错误
//					return null;
//				}
//			} else {
//				return rechargeRecordBase;
//			}
		}
		return rechargeRecordBase;
	}

	@Override
	public RechargeRecordBase getRechargeRecordByChannelTradeNo(String channelTadeNo, String channelId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkRechargeRecordStatus(int status) {
		return (status == RechargeRecordBase.STATUS_BOOKED || status == RechargeRecordBase.STATUS_NOTIFY_SUCCESS || status == RechargeRecordBase.STATUS_NOTIFY_FAIL || status == RechargeRecordBase.STATUS_FAIL || status == RechargeRecordBase.STATUS_GAME_REJECT);
	}

	@Override
	public boolean checkChannelTradeNo(String tradeNo, String channelTradeNo, String originalChannelTradeNo, String channelId, Date paidTime) {
		if (StringUtils.isEmpty(channelTradeNo)) {
			channelTradeNo = tradeNo;
		}
//		RechargeChannelTradeNo rechargeChannelTradeNo = new RechargeChannelTradeNo();
//		rechargeChannelTradeNo.setTradeNo(tradeNo);
//		rechargeChannelTradeNo.setChannelTradeNo(channelTradeNo);
//		rechargeChannelTradeNo.setOriginalChannelTradeNo(originalChannelTradeNo);
//		rechargeChannelTradeNo.setChannelId(channelId);
//		if (paidTime != null) {
//			rechargeChannelTradeNo.setPaidTime(paidTime);
//		}
//		try {
//			rechargeChannelTradeNoMapper.create(rechargeChannelTradeNo);
//			return true;
//		} catch (Throwable t) {
//			String dbTradeNo = rechargeChannelTradeNoMapper.queryTradeNo(rechargeChannelTradeNo);
//			return StringUtils.equals(dbTradeNo, tradeNo);
//		}
		return true;
	}

	@Override
	public void onPayException(ChannelNotifyRequest xgRequest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaySuccess(ChannelRequest channelRequest) {
		this.onPayFinish(channelRequest);
	}

	@Override
	public void onPayException(ChannelRequest xgRequest) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPayFail(RechargeRecordBase rechargeRecord) {
		Log.supplementMessage("order is paied failed, rechargeRecord={}", rechargeRecord);
//		rechargeRecordStatusMapper.updateStatusByChargeLogId(rechargeRecord.getChargeLogId(), RechargeRecordBase.STATUS_FAIL);
	}
	
	private void onPayFinish(ChannelRequest channelRequest) {
		// 修改订单
		// 如果是成功，则修改订单成功并发送通知给游戏
		if (StringUtils.equals(ChannelNotifyRequest.SUCCESS, channelRequest.getStateCode())) {
			RechargeRecordBase rechargeRecord = this.getRechargeRecordByTradeNo(channelRequest.getTradeNo());
			rechargeRecord.setChannelTradeNo(channelRequest.getChannelTradeNo());
			if (StringUtils.equals(ChannelNotifyRequest.PAY_STATUS_SUCCESS, channelRequest.getPayStatus())) {
				rechargeRecord.setStatus(RechargeRecordBase.STATUS_NOTIFY_SUCCESS);
			} else {
				rechargeRecord.setStatus(RechargeRecordBase.STATUS_NOTIFY_FAIL);
			}

			markEscapedInfo(channelRequest, rechargeRecord);

			rechargeRecordStatusMapper.update(rechargeRecord);
			// 通知游戏
			this.notifyGame(rechargeRecord);
		} else if (Arrays.binarySearch(EXCEPTION_CODE, channelRequest.getStateCode()) >= 0) {
			// 如果是异常错误码，则修改订单状态为异常订单
			RechargeRecordBase rechargeRecord = this.getRechargeRecordByTradeNo(channelRequest.getTradeNo());
			rechargeRecord.setChannelTradeNo(channelRequest.getChannelTradeNo());
			rechargeRecord.setStatus(RechargeRecordBase.STATUS_EXCEPTION);
			rechargeRecord.setStateCode(channelRequest.getStateCode());
			rechargeRecord.setExceptionInfoTrimedWhenExtendLength(channelRequest.getStateMsg());
			rechargeRecordStatusMapper.update(rechargeRecord);
			//
			this.saveRequestException(channelRequest, rechargeRecord);
		}
		// 如果是其他错误码，则直接返回
	}
	
	private void markEscapedInfo(ChannelRequest channelRequest, RechargeRecordBase rechargeRecord) {
		JSONObject customInfo = null;
		if (StringUtils.isEmpty(rechargeRecord.getCustomInfo())) {
			customInfo = new JSONObject();
		} else {
			customInfo = JSONObject.parseObject(rechargeRecord.getCustomInfo());
		}

		if (rechargeRecord.getPaidAmount() != channelRequest.getPaidAmount()) {
			customInfo.put("originalPaidAmount", rechargeRecord.getPaidAmount());
			rechargeRecord.setPaidAmount(channelRequest.getPaidAmount());
		}
		
		rechargeRecord.setCustomInfo(customInfo.toJSONString());
	}
	
	@Override
	public String getServerAppSecret(String planId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientNewRechargeResponse createOrder(ClientNewRechargeRequest request) {
		return gameClientService.createOrder(request);
	}

	@Override
	public void saveRechargeRequestLog(HttpServletRequest hRequest, String requestType, Date requestTime,
			Object request, Object response, Object bizObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveRequestLog(String requestType, Date requestTime, Object request, Object response, Object bizObject,
			String requestIp, String tradeNo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveRequestLog(RechargeRequestLog rechargeRequestLog) {
		rechargeRequestLogService.push(rechargeRequestLog);
	}

	@Override
	public void saveRequestException(RechargeRequestException rechargeRequestException) {
		rechargeRequestExceptionService.create(rechargeRequestException);
	}
	
	/**
	 * 记录异常请求日志
	 */
	@Override
	public void saveRequestException(ChannelRequest channelRequest, RechargeRecordBase rechargeRecord) {
		RechargeRequestException rechargeRequestException = new RechargeRequestException();
		rechargeRequestException.setChannelId(channelRequest.getChannelId());
		rechargeRequestException.setChannelTradeNo(channelRequest.getChannelTradeNo());
		rechargeRequestException.setDeviceId(rechargeRecord.getDeviceId());
//		rechargeRequestException.setEventType(RequestType.CHANNEL_NOTIFY);
		rechargeRequestException.setRemarks(channelRequest.getStateMsg());
		rechargeRequestException.setRequestIp(channelRequest.getRequestIp());
		rechargeRequestException.setRequestTime(new Date());
		rechargeRequestException.setRequestValue(JSON.toJSONString(channelRequest));
		rechargeRequestException.setResponseTime(new Date());
		if (channelRequest.getSearchChannelOrderResponseData() != null) {
			rechargeRequestException.setResponseValue(JSON.toJSONString(channelRequest.getSearchChannelOrderResponseData()));
		}
		rechargeRequestException.setTradeNo(channelRequest.getTradeNo());
		rechargeRequestException.setPartnerId(channelRequest.getPartnerId());
		this.saveRequestException(rechargeRequestException);
	}
	
	@Override
	public void cancelRechargeRecordStatus(RechargeRecordBase rechargeRecord) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteRechargeFailLog(RechargeRecordBase rechargeFailLog) {
		// TODO Auto-generated method stub
		
	}
	
	private void notifyGame(RechargeRecordBase rechargeRecord) {
		Log.changeLogContextTypeToAppErr();
		if (rechargeRecord.getStatus() == RechargeRecordBase.STATUS_NOTIFY_FAIL) {
			rechargeRecord.setStatus(RechargeRecordBase.STATUS_FAIL);
			rechargeFailLogMapper.create(rechargeRecord);
			rechargeRecordStatusMapper.delete(rechargeRecord.getChargeLogId());
			Log.supplementMessage(MessageFormat.format("notify fail message to game,rechargeRecord={0},  chargeLogId={1}", rechargeRecord, rechargeRecord.getChargeLogId()));
			return;
		}
		if (rechargeRecord.getStatus() != RechargeRecordBase.STATUS_NOTIFY_SUCCESS) {
			Log.supplementMessage("rechargeRecord not STATUS_NOTIFY_SUCCESS");
			return;
		}

		long chargeLogId = rechargeRecord.getChargeLogId();
		// 构建url
		String notifyUrl = rechargeRecord.getSubAgentNotifyUrl();

		if (StringUtils.isEmpty(notifyUrl)) {
			String msg = "notify game failed, can not find notify url";
//			 rechargeRecordStatusMapper.increaseGameNotifyTimesByChargeLogId(chargeLogId);
			rechargeRecord.setSubAgentNotifyTimes(rechargeRecord.getSubAgentNotifyTimes() + 1);
			rechargeRecord.setStateCode(ErrorCode.ERR_NOTIFY_GAME_URL_EMPTY);
			rechargeRecord.setExceptionInfoTrimedWhenExtendLength(msg);
			rechargeRecordStatusMapper.update(rechargeRecord);
			Log.supplementMessage(MessageFormat.format("notify game failed, can not find notify url,rechargeRecord={0}", rechargeRecord));
			return;
		}

		// 构建请求参数
		String jsonString = JSON.toJSONString(rechargeRecord);
		NotifySubAgentRequest notifySubAgentRequest = JSON.parseObject(jsonString, NotifySubAgentRequest.class);
		String payStatus = NotifySubAgentRequest.PAY_STATUS_FAIL;
		if (RechargeRecordBase.STATUS_NOTIFY_SUCCESS == rechargeRecord.getStatus()) {
			payStatus = NotifySubAgentRequest.PAY_STATUS_SUCCESS;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		notifySubAgentRequest.setTs(SignCore.getTs());
		notifySubAgentRequest.setPaidTime(format.format(rechargeRecord.getPaidTime()));
		notifySubAgentRequest.setPayStatus(payStatus);
		notifySubAgentRequest.setPaidAmount(String.valueOf(rechargeRecord.getPaidAmount()));
		notifySubAgentRequest.buildRechargeOrderExt();
		
		String secretKey = partnerRespository.getSecretKey(rechargeRecord.getPartnerId());
		notifySubAgentRequest.setSign(SignCore.xgSign(notifySubAgentRequest, SignCore.SIGN_FIELD_NAME, secretKey));
		String body = JSON.toJSONString(notifySubAgentRequest);

		// 发送请求
		Date requestTime = new Date();
		String responseString = HttpUtils.doPostJson(notifyUrl, body);
		RechargeRequestLog requestLog = new RechargeRequestLog();
		requestLog.setChannelId(rechargeRecord.getChannelId());
		requestLog.setChannelTradeNo(rechargeRecord.getChannelTradeNo());
		requestLog.setDeviceId(rechargeRecord.getDeviceId());
		requestLog.setRequestIp("127.0.0.1");
		requestLog.setRequestTime(requestTime);
		requestLog.setRequestValue(body);
		requestLog.setResponseTime(new Date());
		requestLog.setResponseValue(responseString);
		requestLog.setTradeNo(rechargeRecord.getTradeNo());
		requestLog.setUid(rechargeRecord.getUid());
		requestLog.setPartnerId(rechargeRecord.getPartnerId());
		rechargeRequestLogService.push(requestLog);

		// 判断请求是否可访问
		if (StringUtils.isEmpty(responseString)) {
			String msg = "notify game failed, can not get the response from game server.";
			// rechargeRecordStatusMapper.increaseGameNotifyTimesByChargeLogId(chargeLogId);
			rechargeRecord.setSubAgentNotifyTimes(rechargeRecord.getSubAgentNotifyTimes() + 1);
			rechargeRecord.setStateCode(ErrorCode.ERR_NOTIFY_GAME_OVERTIME);
			rechargeRecord.setExceptionInfoTrimedWhenExtendLength(msg);
			rechargeRecordStatusMapper.update(rechargeRecord);
			Log.supplementMessage(MessageFormat.format("notify sub-channel failed, can not get the response from sub-channel server,rechargeRecord={0}", rechargeRecord));
			return;
		}
		NotifySubAgentResponse notifySubAgentResponse = null;
		try {
			notifySubAgentResponse = JSON.parseObject(responseString, NotifySubAgentResponse.class);
		} catch (Throwable t) {
			String msg = MessageFormat.format("notify game failed, can not parse the response from game server,response={0}.", responseString);
			// rechargeRecordStatusMapper.increaseGameNotifyTimesByChargeLogId(chargeLogId);
			rechargeRecord.setSubAgentNotifyTimes(rechargeRecord.getSubAgentNotifyTimes() + 1);
			rechargeRecord.setStateCode(ErrorCode.ERR_NOTIFY_GAME_RESPONSE_ERROR);
			rechargeRecord.setExceptionInfoTrimedWhenExtendLength(msg);
			rechargeRecordStatusMapper.update(rechargeRecord);
			Log.supplementMessage(MessageFormat.format("sub-channel server response is not excepted, url={0}, request={1}, response={2}, rechargeRecord={3}", notifyUrl, body, responseString, rechargeRecord));
			return;
		}

		// 判断返回值是否成功或者重复
		String code = notifySubAgentResponse.getCode();
		if (StringUtils.equals(ErrorCode.SUCCESS, code) || StringUtils.equals(ErrorCode.ERR_REPEAT, code)) {
			// 移到log表中
			rechargeRecord.setStatus(RechargeRecordBase.STATUS_BOOKED);
			rechargeRecord.setFinishTime(new Date());
			rechargeLogMapper.create(rechargeRecord);
			rechargeRecordStatusMapper.delete(chargeLogId);
			rechargeLog.info(JSON.toJSONString(rechargeRecord));
			Log.changeLogContextTypeToInfo();
			Log.supplementMessage(MessageFormat.format(
				"notify sub-channel success,rechargeRecord={0}, notifyGameRequest={1}, notifyGameResponse={2}, notifyUrl={3}, chargeLogId={4}",
				rechargeRecord, notifySubAgentRequest, notifySubAgentResponse, notifyUrl, chargeLogId));
			return;
		}
		// 判断返回值是否重发
		if (StringUtils.equals(ErrorCode.ERR_RESEND, code)) {
			String msg = MessageFormat.format("notify game failed, game server response resend state,response={0}.", responseString);
			// rechargeRecordStatusMapper.increaseGameNotifyTimesByChargeLogId(chargeLogId);
			rechargeRecord.setSubAgentNotifyTimes(rechargeRecord.getSubAgentNotifyTimes() + 1);
			rechargeRecord.setStateCode(ErrorCode.ERR_RESEND);
			rechargeRecord.setExceptionInfoTrimedWhenExtendLength(msg);
			rechargeRecordStatusMapper.update(rechargeRecord);
			Log.changeLogContextTypeToInfo();
			Log.supplementMessage(MessageFormat.format(
				"notify sub-channel success,rechargeRecord={0}, notifyGameRequest={1}, notifyGameResponse={2}, notifyUrl={3}, chargeLogId={4}",
				rechargeRecord, notifySubAgentRequest, notifySubAgentResponse, notifyUrl, chargeLogId));
			return;
		}
		// 判断游戏是否拒绝
		if (StringUtils.equals(ErrorCode.ERR_GAME_REJECT, code)) {
			String msg = MessageFormat.format("notify game failed, game server reject this notice,response={0}.", responseString);
			// rechargeRecordStatusMapper.updateStatusByChargeLogId(chargeLogId, RechargeRecordBase.STATUS_GAME_REJECT);
			rechargeRecord.setStatus(RechargeRecordBase.STATUS_GAME_REJECT);
			rechargeRecord.setStateCode(ErrorCode.ERR_GAME_REJECT);
			rechargeRecord.setExceptionInfoTrimedWhenExtendLength(msg);
			rechargeRecordStatusMapper.update(rechargeRecord);
			Log.supplementMessage(MessageFormat.format(
				"sub-channel server response is not excepted, httpCode={0}, notifyGameRequest={1}, notifyGameResponse={2}, notifyUrl={3}, chargeLogId={4}",
				"200", notifySubAgentRequest, notifySubAgentResponse, notifyUrl, chargeLogId));
			return;
		}
		// 判断游戏是否异常
		if (StringUtils.equals(ErrorCode.ERR_EXCEPTION, code)) {
			String msg = MessageFormat.format("notify game failed, the order has some exception info,response={0}.", responseString);
			// rechargeRecordStatusMapper.updateStatusByChargeLogId(chargeLogId, RechargeRecordBase.STATUS_GAME_REJECT);
			rechargeRecord.setStatus(RechargeRecordBase.STATUS_EXCEPTION);
			rechargeRecord.setStateCode(ErrorCode.ERR_EXCEPTION);
			rechargeRecord.setExceptionInfoTrimedWhenExtendLength(msg);
			rechargeRecordStatusMapper.update(rechargeRecord);
			Log.supplementMessage(MessageFormat.format(
				"notify sub-channel failed, the order has some exception info, httpCode={0}, notifyGameRequest={1}, notifyGameResponse={2}, notifyUrl={3}, chargeLogId={4}",
				"200", notifySubAgentRequest, notifySubAgentResponse, notifyUrl, chargeLogId));
			return;
		}
		// 判断返回值是否其他错误
//		 rechargeRecordStatusMapper.updateStatusByChargeLogId(chargeLogId, RechargeRecordBase.STATUS_GAME_REJECT);
		// rechargeRecordStatusMapper.increaseGameNotifyTimesByChargeLogId(chargeLogId);
		String msg = MessageFormat.format("notify game failed, game server reject this notice,response={0}.", responseString);
		rechargeRecord.setSubAgentNotifyTimes(rechargeRecord.getSubAgentNotifyTimes() + 1);
		rechargeRecord.setStateCode(ErrorCode.ERR_NOTIFY_GAME_RESPONSE_FAIL);
		rechargeRecord.setExceptionInfoTrimedWhenExtendLength(msg);
		rechargeRecordStatusMapper.update(rechargeRecord);
		Log.supplementMessage(MessageFormat.format(
			"sub-channel server response is not excepted, httpCode={0}, notifySubChannelRequest={1}, notifySubChannelResponse={2}, notifyUrl={3}, chargeLogId={4}",
			"200", notifySubAgentRequest, notifySubAgentResponse, notifyUrl, chargeLogId));
		return;
	}
}
