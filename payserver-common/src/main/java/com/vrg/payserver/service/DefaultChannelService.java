package com.vrg.payserver.service;

import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSON;
import com.vrg.payserver.service.vo.ChannelData;
import com.vrg.payserver.service.vo.ChannelNotifyRequest;
import com.vrg.payserver.service.vo.ChannelRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeResponse;
import com.vrg.payserver.service.vo.CreateChannelOrderRequest;
import com.vrg.payserver.service.vo.CreateChannelOrderResponse;
import com.vrg.payserver.service.vo.RechargeRecordBase;
import com.vrg.payserver.service.vo.RechargeRequestLog;
import com.vrg.payserver.service.vo.VerifyChannelOrderRequest;
import com.vrg.payserver.service.vo.VerifyChannelOrderResponse;
import com.vrg.payserver.util.DateUtil;
import com.vrg.payserver.util.ErrorCode;
import com.vrg.payserver.util.IPUtils;
import com.vrg.payserver.util.Log;
import com.vrg.payserver.util.RequestType;
import com.vrg.payserver.util.Util;

public final class DefaultChannelService implements IChannel {
	public static final String URL_PATTERN = "{0}://{1}:{2}{3}";
	public static final String URL_PATTERN80 = "{0}://{1}{2}";

	public static final String NOT_CHECK_PRODUCT_ID = "NOT_CHECK_PRODUCT_ID";
	public static final String NOT_CHECK_PAID_AMOUNT = "NOT_CHECK_PAID_AMOUNT";

	private IServerCoreService serverCoreService;

	private IChannelAdapter adapter;

	/**
	 * @param adapter
	 */
	public DefaultChannelService(IChannelAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void setServerCoreService(IServerCoreService serverCoreService) {
		this.serverCoreService = serverCoreService;
		this.adapter.setServerCoreService(serverCoreService);
	}

	@Override
	public ResponseEntity<?> notifyRechargeResult(HttpServletRequest hRequest, String channelId, String partnerId) {
		boolean isMultipleChannelTradeNo = false;
		Date requestTime = new Date();
		ChannelRequest request = new ChannelRequest();
		request.setChannelId(channelId);
		request.setPartnerId(partnerId);
//		int serverPort = hRequest.getServerPort();
//		if (serverPort == 80) {
//			xgRequest.setServerVersion(MessageFormat.format(URL_PATTERN80, hRequest.getScheme(), hRequest.getServerName(), hRequest.getServletPath()));
//		} else {
//			xgRequest.setServerVersion(MessageFormat.format(URL_PATTERN, hRequest.getScheme(), hRequest.getServerName(), String.valueOf(serverPort), hRequest.getServletPath()));
//		}
		request.setRequestIp(IPUtils.getRemoteAddr(hRequest));
		request.setStateCode(ChannelRequest.SUCCESS);
		request.setPayStatus(ChannelRequest.PAY_STATUS_SUCCESS);


		RechargeRecordBase rechargeRecord = null;
		Object responseString = null;
		ChannelData channelRequestData = null;
		try {
			Log.changeLogContextTypeToAppErr();
			// 解析渠道请求参数
			Log.enterStep("解析渠道请求参数");

			channelRequestData = adapter.parsePayNotice(hRequest, channelId);
			// 如果为空，则直接返回
			if (channelRequestData == null) {
				request.setStateCode(ChannelRequest.ERR_SYSTEM);
				request.setStateMsg("The pay notice is empty.");
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), null, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}

			request.setPayNoticeRequestData(channelRequestData);
			request.putChannelData(channelRequestData);

			if (!StringUtils.equals(ChannelRequest.SUCCESS, request.getStateCode())) {
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), null, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}

			// 查询订单
			Log.enterStep("查询订单");
			rechargeRecord = serverCoreService.getRechargeRecordByTradeNo(request.getTradeNo());
			if (rechargeRecord == null) {
				rechargeRecord = serverCoreService.getRechargeRecordByChannelTradeNo(request.getChannelTradeNo(), channelId);
			}
			// 订单是否存在
			if (rechargeRecord == null) {
				// 订单不存在，返回错误
				request.setStateCode(ChannelNotifyRequest.ERR_ORDERID_NOTEXIST);
				request.setStateMsg(MessageFormat.format("The order [{0}] can not be found.", request.getTradeNo()));
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), null, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}

			// 验证订单状态
			request.setTradeNo(rechargeRecord.getTradeNo());

			// 验证签名
			Log.enterStep("验证签名");
			if (!adapter.verifyPayNoticeSign(request.getPayNoticeRequestData(), partnerId, channelId)) {
				// 签名失败，返回成功
				request.setStateCode(ChannelNotifyRequest.ERR_SIGN);
				request.setStateMsg(ErrorCode.ERR_SIGN_MSG);
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				Log.supplementMessage("签名验证不通过");
				return responseEntity;
			}
			Log.supplementMessage("验证签名通过");

			Log.enterStep("检查充值记录状态");
			if (serverCoreService.checkRechargeRecordStatus(rechargeRecord.getStatus())) {
				if (!StringUtils.equals(rechargeRecord.getChannelTradeNo(), request.getChannelTradeNo())) {
					// 渠道查询，如果订单真实，则自动创建一个新的西瓜订单，并自动通知游戏；
					// 如果渠道没有提供查询接口或查询失败，则自动创建一个新西瓜订单，但不自动通知游戏，由人工确定后再决定是否补单
					// 以上2种情况都在订单的备注里边添加“多个渠道订单对应西瓜订单[原西瓜订单号]的补单”
					String mulExceptionInfo = "此订单是由于渠道重复通知所创建，原始订单号[{0}]";
					isMultipleChannelTradeNo = true;
					ClientNewRechargeRequest newRechargeReq = new ClientNewRechargeRequest();
					BeanUtils.copyProperties(rechargeRecord, newRechargeReq);
//					newRechargeReq.setExceptionInfo(MessageFormat.format(mulExceptionInfo,rechargeRecord.getTradeNo()));
					ClientNewRechargeResponse secondRecharegeRes = serverCoreService.createOrder(newRechargeReq);
					String secondXGTradeNo = secondRecharegeRes.getData().getTradeNo();
					// 如果xg订单号一致，但渠道订单号不一致，有可能是一个xg订单号对应多个渠道订单号的情况，先记录，后续人工跟进
					saveMultipleChannelTradeNo(hRequest, channelId, partnerId, request, rechargeRecord, secondXGTradeNo);
					rechargeRecord = serverCoreService.getRechargeRecordByTradeNo(secondXGTradeNo);
					Log.supplementMessage("重复通知 " + MessageFormat.format("西瓜订单号{0}对应多个渠道订单号{1}，已记录到RECHARGE_MULTI_CHLNO表，需要由人工识别订单是否真实。", request.getTradeNo(), rechargeRecord.getChannelTradeNo() + "," + request.getChannelTradeNo()));
				} else {
					// 已收到过通知，返回成功
					request.setStateCode(ChannelNotifyRequest.ERR_REPEAT);
					request.setStateMsg(MessageFormat.format("The order [{0}] is notified again.", request.getTradeNo()));
					ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
					responseString = responseEntity.getBody();
//					Log.supplementMessage("重复通知 " + MessageFormat.format("西瓜订单号{0}对应的渠道订单号{1}之前已经在{2}通知成功，忽略本次通知。", rechargeRecord.getTradeNo(), rechargeRecord.getChannelTradeNo(), rechargeRecord.getFinishTime()));
					return responseEntity;
				}

			}

			// 如果支付状态是失败，则可以直接待通知失败
			if (StringUtils.equals(request.getPayStatus(), ChannelRequest.PAY_STATUS_FAIL)) {
				serverCoreService.onPayException(request);
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				Log.supplementMessage("支付失败 " + MessageFormat.format("{0}渠道通知用户支付失败，作废订单[{1}]", channelId, request.getTradeNo()));
				return responseEntity;
			}

			// 校验渠道请求参数的一致性
			if (!this.checkSame(rechargeRecord, request, request.getPayNoticeRequestData())) {
				// 一致性失败，记录异常请求表，返回错误
				serverCoreService.onPayException(request);
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}
			// 查询渠道订单
			Log.enterStep("查询渠道订单");
			ChannelData channelResponseData = new ChannelData();
			SearchChannelOrderType searchChannelOrderResult = adapter.searchChannelOrder(channelRequestData, partnerId, channelId, channelResponseData);
			if (isMultipleChannelTradeNo && !SearchChannelOrderType.OK.equals(searchChannelOrderResult)) {
				request.setStateCode(ChannelRequest.SUCCESS);
				request.setStateMsg(MessageFormat.format("渠道不支持自动查询，需要手动补单。", request.getTradeNo(), request.getChannelTradeNo()));
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}
			if (searchChannelOrderResult != null && !SearchChannelOrderType.NOTSUPPORT.equals(searchChannelOrderResult)) {
				// 如果是一对多的渠道订单，直接返回渠道成功。
				request.setSearchChannelOrderResponseData(channelResponseData);
				request.putChannelData(channelResponseData);
			}
			if (SearchChannelOrderType.NOK.equals(searchChannelOrderResult)) {
				// 查询超时，查询订单不存在，查询响应失败，查询响应无法解析，查询响应验签不通过，返回错误
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}

			// 如果查询不成功
			if (!StringUtils.equals(ChannelRequest.SUCCESS, request.getStateCode())) {
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}

			// 校验渠道响应参数的一致性
			Log.enterStep("校验渠道响应参数的一致性");
			if (!this.checkSame(rechargeRecord, request, request.getSearchChannelOrderResponseData())) {
				// 一致性失败，记录异常请求表，返回错误
				serverCoreService.onPayException(request);
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}
			// 前后渠道订单号校验
			String requestChannelTradeNo = request.getPayNoticeRequestData().getChannelTradeNo();
			String responseChannelTradeNo = request.getSearchChannelOrderResponseData().getChannelTradeNo();
			if (!StringUtils.isEmpty(requestChannelTradeNo) && !StringUtils.isEmpty(responseChannelTradeNo) && !StringUtils.equals(requestChannelTradeNo, responseChannelTradeNo)) {
				// 一致性失败，记录异常请求表，返回错误
				request.setStateCode(ChannelNotifyRequest.ERR_CHANNEL_TRADE_NO_NOTSAME);
				request.setStateMsg(MessageFormat.format("The channel_trade_no[{0}] in request is not same with the channel_trade_no[{1}] in channel response.", requestChannelTradeNo, responseChannelTradeNo));
				serverCoreService.onPayException(request);
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}

			// 渠道订单号排重
			Log.enterStep("渠道订单号排重");
			if (!serverCoreService.checkChannelTradeNo(rechargeRecord.getTradeNo(), requestChannelTradeNo, channelId, null, request.getPayNoticeRequestData().getPaidTime())) {
				request.setStateCode(ChannelRequest.ERR_EXCEPTION);
				request.setStateMsg(MessageFormat.format("It is repeat request. ChannelTradeNo:[{0}].TradeNo:[{1}].", requestChannelTradeNo, rechargeRecord.getTradeNo()));
				// 渠道订单号排重失败，返回错误
				serverCoreService.onPayException(request);
				ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
				responseString = responseEntity.getBody();
				return responseEntity;
			}

			// 调用onPaySuccess
			serverCoreService.onPaySuccess(request);
			ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, channelRequestData);
			responseString = responseEntity.getBody();
			Log.changeLogContextTypeToInfo();
			return responseEntity;
		} catch (Throwable t) {
			String msg = MessageFormat.format("error in notifyRechargeResult, requestData={0}, channelId={1}, xgAppId={2}, serverVersion={3}, error={4}", request, channelId, partnerId, hRequest.getQueryString(), t.getMessage());
			Log.supplementExceptionMessage(t);
//			xgRequest.setStateCode(ChannelRequest.ERR_SYSTEM);
			request.setStateMsg(msg);
			ResponseEntity<?> responseEntity = this.getPayNoticeResponse(hRequest, request.getStateCode(), request.getStateMsg(), partnerId, channelId, request.getPayNoticeRequestData());
			responseString = responseEntity.getBody();
			return responseEntity;
		} finally {
			if (channelRequestData != null) {
//				Log.supplementBizInfo(null, null, xgRequest.getPlanId(), channelId, channelRequestData.getUid(), null,
//				channelRequestData.getTradeNo(), channelRequestData.getChannelTradeNo(), channelRequestData.getProductId(), channelRequestData.getProductName(), null, channelRequestData.getPayStatus(), xgRequest.getStateCode() + " " + xgRequest.getStateMsg(), null, null);
			} else {
				Log.supplementBizInfo(partnerId, channelId, null, null, null, null, request.getStateCode() + request.getStateMsg());
			}
			String channelTradeNo = null;
			ChannelData payNoticeRequestData = request.getPayNoticeRequestData();
			if (payNoticeRequestData != null) {
				channelTradeNo = payNoticeRequestData.getChannelTradeNo();
			} else {
				payNoticeRequestData = new ChannelData();
			}
			if (StringUtils.isEmpty(channelTradeNo)) {
				channelTradeNo = request.getSearchChannelOrderResponseData().getChannelTradeNo();
			}
			if (StringUtils.isEmpty(channelTradeNo) && rechargeRecord != null) {
				channelTradeNo = rechargeRecord.getChannelTradeNo();
			}
			String tradeNo = payNoticeRequestData.getTradeNo();
			if (StringUtils.isEmpty(tradeNo) && rechargeRecord != null) {
				tradeNo = rechargeRecord.getTradeNo();
			}
//			String uid = payNoticeRequestData.getUid();
//			if (StringUtils.isEmpty(uid) && rechargeRecord != null) {
//				uid = rechargeRecord.getUid();
//			}
			RechargeRequestLog requestLog = new RechargeRequestLog();
			requestLog.setChannelId(channelId);
			requestLog.setChannelTradeNo(channelTradeNo);
//			requestLog.setEventType(RequestType.CHANNEL_NOTIFY);
			requestLog.setHttpServletRequest(hRequest);
			requestLog.setRequestTime(requestTime);
			requestLog.setRequestValue(request.toString());
			requestLog.setResponseTime(new Date());
			if (responseString != null) {
				requestLog.setResponseValue(JSON.toJSONString(responseString));
			}
			requestLog.setTradeNo(tradeNo);
//			requestLog.setUid(uid);
			requestLog.setPartnerId(partnerId);
			serverCoreService.saveRequestLog(requestLog);
		}
	}

	private void saveMultipleChannelTradeNo(HttpServletRequest hRequest, String channelId, String xgAppId, ChannelRequest xgRequest, RechargeRecordBase rechargeRecord, String secondXGTradeNo) {
//		RechargeMultipleChannelTradeNo rechargeMultipleChannelOrderNo = new RechargeMultipleChannelTradeNo();
//		rechargeMultipleChannelOrderNo.setTradeNo(xgRequest.getTradeNo());
//		rechargeMultipleChannelOrderNo.setNewTradeNo(secondXGTradeNo);
//		rechargeMultipleChannelOrderNo.setChannelTradeNo(xgRequest.getChannelTradeNo());
//		rechargeMultipleChannelOrderNo.setXgAppId(xgAppId);
//		rechargeMultipleChannelOrderNo.setChannelId(channelId);
//		rechargeMultipleChannelOrderNo.setPaidAmount(xgRequest.getPayNoticeRequestData().getPaidAmount());
//		rechargeMultipleChannelOrderNo.setUid(StringUtils.defaultString(xgRequest.getPayNoticeRequestData().getUid(), rechargeRecord.getUid()));
//		rechargeMultipleChannelOrderNo.setDeviceId(StringUtils.defaultString(xgRequest.getDeviceId(), rechargeRecord.getDeviceId()));
//		rechargeMultipleChannelOrderNo.setRequestIp(IPUtils.getRemoteAddr(hRequest));
//		rechargeMultipleChannelOrderNo.setOrderCreateTime(rechargeRecord.getCreateTime());
//		rechargeMultipleChannelOrderNo.setOrderNotifyTime(new Date());
//		rechargeMultipleChannelOrderNo.setStatus(RechargeMultipleChannelTradeNo.STATUS_NEW);
//		serverCoreService.saveMultipleChannelTradeNo(rechargeMultipleChannelOrderNo);
	}

	/**
	 * 获取渠道返回值
	 *
	 * @param xgRequest
	 * @return
	 */
	private ResponseEntity<?> getPayNoticeResponse(HttpServletRequest hRequest, String stateCode, String stateMsg, String partnerId, String channelId, ChannelData channelRequestData) {
		return adapter.getPayNoticeResponse(stateCode, stateMsg, partnerId, channelId, channelRequestData);
	}

	@Override
	public VerifyChannelOrderResponse queryRechargeResult(VerifyChannelOrderRequest request) {
		Date requestTime = new Date();
		ChannelRequest prequest = new ChannelRequest();
		ChannelData channelRequestData = new ChannelData();
		prequest.setPayNoticeRequestData(channelRequestData);
//		channelRequestData.setChannelAppId(request.getChannelAppId());
		channelRequestData.setChannelTradeNo(request.getChannelTradeNo());
		// channelRequestData.setChargeChannelId(chargeChannelId);
		// channelRequestData.setChargeChannelInst(chargeChannelInst);
		// channelRequestData.setChargeChannelType(chargeChannelType);
		channelRequestData.setPaidAmount(request.getPaidAmount());
		// channelRequestData.setPaidTime(paidTime);
		// channelRequestData.setPayStatus(payStatus);
//		channelRequestData.setProductId(request.getProductId());
//		channelRequestData.setProductName(request.getProductName());
//		channelRequestData.setProductQuantity(request.getProductQuantity());
		// channelRequestData.setStateCode(stateCode);
		// channelRequestData.setStateMsg(stateMsg);
		channelRequestData.setTradeNo(request.getTradeNo());
//		channelRequestData.setUid(request.getUid());
		channelRequestData.setCustomInfo(request.getCustomInfo());
		// channelRequestData.setVoucherAmount(voucherAmount);
		prequest.putChannelData(channelRequestData);

		try {
			// 查询渠道订单
			ChannelData channelResponseData = new ChannelData();
			SearchChannelOrderType searchChannelOrderResult = adapter.searchChannelOrder(channelRequestData, request.getPlanId(), request.getChannelId(), channelResponseData);
			if (searchChannelOrderResult == null || SearchChannelOrderType.NOTSUPPORT.equals(searchChannelOrderResult)) {
				return null;
			}

			if (SearchChannelOrderType.NOK.equals(searchChannelOrderResult)) {
				// 查询超时，查询订单不存在，查询响应失败，查询响应无法解析，查询响应验签不通过，返回错误
				String msg = MessageFormat.format("Can not access channel server. TradeNo is [{1}]", channelRequestData.getTradeNo());
				VerifyChannelOrderResponse response = new VerifyChannelOrderResponse();
				response.setCode(ErrorCode.ERR_CHANNELVERIFY_FAIL);
				response.setMsg(msg);
				return response;
			}

			// Usecase: 客户端上报支付凭证，后台schedule job查询时，渠道服务器返回请求参数无效时（如应用宝渠道）
			if (SearchChannelOrderType.BAD_REQUEST.equals(searchChannelOrderResult)) {
				VerifyChannelOrderResponse response = new VerifyChannelOrderResponse();
				response.setCode(ErrorCode.ERR_BAD_REQUEST);
				response.setMsg("fail");
				return response;
			}

			// 校验渠道响应参数的一致性
			prequest.setSearchChannelOrderResponseData(channelResponseData);
			prequest.putChannelData(channelResponseData);
			RechargeRecordBase rechargeRecord = serverCoreService.getRechargeRecordByTradeNo(channelRequestData.getTradeNo());
			if (!this.checkSame(rechargeRecord, prequest, channelResponseData)) {
				// 一致性失败，记录异常请求表，返回错误
				VerifyChannelOrderResponse response = new VerifyChannelOrderResponse();
				response.setCode(prequest.getStateCode());
				response.setMsg(prequest.getStateMsg());
				return response;
			}

			VerifyChannelOrderResponse response = new VerifyChannelOrderResponse();
			response.setChannelTradeNo(prequest.getChannelTradeNo());
			response.setCode(ErrorCode.SUCCESS);
			response.setMsg(ErrorCode.MSG_OK);
			response.setPayStatus(prequest.getPayStatus());
			return response;
		} finally {
			String channelTradeNo = prequest.getChannelTradeNo();
			if (StringUtils.isEmpty(channelTradeNo)) {
				channelTradeNo = prequest.getSearchChannelOrderResponseData().getChannelTradeNo();
			}
			RechargeRequestLog requestLog = new RechargeRequestLog();
			requestLog.setChannelId(prequest.getChannelId());
			requestLog.setChannelTradeNo(channelTradeNo);
			requestLog.setEventType(RequestType.VERIFY_CHANNEL_ORDER);
			// requestLog.setHttpServletRequest(hRequest);
			requestLog.setRequestTime(requestTime);
			requestLog.setRequestValue(prequest.toString());
			requestLog.setResponseTime(new Date());
			if (prequest.getSearchChannelOrderResponseData() != null) {
				requestLog.setResponseValue(JSON.toJSONString(prequest.getSearchChannelOrderResponseData()));
			}
			requestLog.setTradeNo(request.getTradeNo());
			requestLog.setUid(request.getUid());
			requestLog.setPartnerId(prequest.getPartnerId());
			serverCoreService.saveRequestLog(requestLog);
		}
	}

	private boolean checkSame(RechargeRecordBase rechargeRecord, ChannelRequest channelRequest, ChannelData requestData) {
		// 验证请求参数的一致性
		// private String customInfo; // 渠道返回的用户自定义字段是否和订单号一致，为空则说明渠道不支持自定义字段
		if (StringUtils.isNotEmpty(requestData.getCustomInfo()) && !StringUtils.equals(requestData.getTradeNo(), requestData.getCustomInfo())) {
			// 设置错误码
			channelRequest.setStateCode(ChannelNotifyRequest.ERR_CHANNEL_TRADE_NO_NOTSAME);
			channelRequest.setStateMsg(MessageFormat.format("Channel custom info[{0}] of the order[{1}] is not match with request information.", requestData.getCustomInfo(), rechargeRecord.getTradeNo()));
			return false;
		}
		
		// private String partnerId; // 上渠道渠道的唯一标识
		if (!StringUtils.isEmpty(requestData.getPartnerId()) && !StringUtils.isEmpty(rechargeRecord.getPartnerId()) && !StringUtils.equals(requestData.getPartnerId(), rechargeRecord.getPartnerId())) {
			// 设置错误码
			channelRequest.setStateCode(ChannelNotifyRequest.ERR_CHANNEL_APP_ID_NOTSAME);
			channelRequest.setStateMsg(MessageFormat.format("PartnerId [{0}] of the order[{1}] is not match with request information.", requestData.getPartnerId(), rechargeRecord.getTradeNo()));
			return false;
		}
		
		// private Date paidTime; // 订单支付时间，和xg订单中的创建时间相差不能超过一天
		if (requestData.getPaidTime() != null && rechargeRecord.getCreateTime() != null && !DateUtil.withinSevenDay(rechargeRecord.getCreateTime(), requestData.getPaidTime())) {
			// 设置错误码
			channelRequest.setStateCode(ChannelNotifyRequest.ERR_PAID_TIME_OUT);
			channelRequest.setStateMsg(MessageFormat.format("The start time[{0}] of the request is timeout 7 day with the create time[{1}] of the order[{2}].", requestData.getPaidTime(),
				rechargeRecord.getCreateTime(),
				rechargeRecord.getTradeNo()));
			return false;
		}
		
		// private int paidAmount = -1;
		if (requestData.getPaidAmount() >= 0 && rechargeRecord.getPaidAmount() >= 0 && (requestData.getPaidAmount() < rechargeRecord.getPaidAmount())) {
			// 设置错误码
			channelRequest.setStateCode(ChannelNotifyRequest.ERR_PAID_AMOUNT_NOTSAME);
			channelRequest.setStateMsg(MessageFormat.format("channel notify amount[{0}] of the order[{1}] is not match with order information created by vrg.", requestData.getPaidAmount(), rechargeRecord.getTradeNo()));
			return false;
		}

		return true;
	}

	@Override
	public CreateChannelOrderResponse createChannelOrder(CreateChannelOrderRequest request) {
		return adapter.createChannelOrder(request);
	}

	public IChannelAdapter getChannelAdapter() {
		return this.adapter;
	}
}