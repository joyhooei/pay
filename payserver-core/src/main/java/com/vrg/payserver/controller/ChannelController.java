package com.vrg.payserver.controller;

import java.text.MessageFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrg.payserver.ChannelService;
import com.vrg.payserver.RechargeRequestLogService;
import com.vrg.payserver.dao.RechargeRecordStatusMapper;
import com.vrg.payserver.repository.ChannelRepository;
import com.vrg.payserver.service.ClientService;
import com.vrg.payserver.service.IChannel;
import com.vrg.payserver.service.IChannelAdapter;
import com.vrg.payserver.service.ParamRepository;
import com.vrg.payserver.service.vo.ClientNewRechargeRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeResponse;
import com.vrg.payserver.service.vo.CreateChannelOrderRequest;
import com.vrg.payserver.service.vo.CreateChannelOrderResponse;
import com.vrg.payserver.service.vo.CreateChannelOrderResponseData;
import com.vrg.payserver.service.vo.RechargeRequestLog;
import com.vrg.payserver.util.ErrorCode;
import com.vrg.payserver.util.IPUtils;
import com.vrg.payserver.util.Log;
import com.vrg.payserver.util.RequestType;
import com.vrg.payserver.util.SignCore;
import com.vrg.payserver.util.Util;
import com.vrg.payserver.util.logging.LogAction;

@RestController
public class ChannelController {

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private RechargeRequestLogService rechargeRequestLogService;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private ParamRepository paramRepository;

	@Autowired
	private ClientService clientService;

	@Autowired
	private RechargeRecordStatusMapper rechargeRecordStatusMapper;

	@RequestMapping(value = "/pay/create-order/{channelId}/{partnerId}")
	public ResponseEntity<ClientNewRechargeResponse> createOrder(HttpServletRequest hRequest, @PathVariable String channelId, @PathVariable String partnerId) {
		Log.startAction(LogAction.CREATE_ORDER, partnerId, channelId, IPUtils.getRemoteAddr(hRequest), null);
		ClientNewRechargeResponse response = null;
		ClientNewRechargeRequest request = null;
		try {
			request = Util.parseRequestParameter(hRequest, ClientNewRechargeRequest.class);
			request.setPartnerId(partnerId);
			request.setChannelId(channelId);
			Log.changeLogContextTypeToAppErr();

			// check sign
			Log.enterStep("验签");
			String type = RequestType.CREATE_ORDER;
			if (!StringUtils.equalsIgnoreCase(type, request.getType()) || !clientService.verifySign(request, request.getSign(), request.getPartnerId(), channelId)) {
				response = clientService.createNewRechargeResponse(request);
				response.setCode(ErrorCode.ERR_SIGN);
				response.setMsg(ErrorCode.ERR_SIGN_MSG);
				return ResponseEntity.ok(response);
			}

			// 创建订单
			Log.enterStep("创建订单");
			response = clientService.createOrder(request);
			if (!StringUtils.equals(ErrorCode.SUCCESS, response.getCode())) {
				Log.supplementMessage("createOrder failed, channelId = {}, partnerId = {}, request data = {}, response data = {}.", channelId, partnerId, request, response);
				return ResponseEntity.ok(response);
			}

			// create channel order
			Log.enterStep("创建渠道订单");
			request.setTradeNo(response.getData().getTradeNo());
			Log.supplementBizInfo(partnerId, channelId, response.getData().getTradeNo(), null, response.getData().getSign(), null, null);
			CreateChannelOrderResponse createChannelResponse = createChannelOrder(request);
			if (createChannelResponse != null && !StringUtils.equalsIgnoreCase(createChannelResponse.getCode(), ErrorCode.SUCCESS)) {
				// 渠道订单创建失败
				Log.supplementMessage("create channel order failed, channelId = {}, partnerId = {}, request data = {}, response data = {}.", channelId, partnerId, request, createChannelResponse);
				response = new ClientNewRechargeResponse();
				response.setCode(createChannelResponse.getCode());
				response.setMsg(createChannelResponse.getMsg());
				return ResponseEntity.ok(response);
			}

			if (createChannelResponse != null && createChannelResponse.getData() != null) {
				// 获取渠道创建的订单号
				CreateChannelOrderResponseData channelResponseData = createChannelResponse.getData();
				String channelTradeNo = channelResponseData.getChannelTradeNo();
				Log.supplementBizInfo(null, null, null, null,channelTradeNo, null, null);
				if (!StringUtils.isEmpty(channelTradeNo)) {
					rechargeRecordStatusMapper.updateChannelTradeNoByTradeNo(response.getData().getTradeNo(), channelTradeNo);
				}
				
				response.getData().setChannelTradeNo(channelTradeNo);
				response.getData().setNonceStr(channelResponseData.getNonceStr());
				response.getData().setPrepayId(channelResponseData.getPrepayId());
				response.getData().setSubmitTime(channelResponseData.getSubmitTime());
				response.getData().setTokenUrl(channelResponseData.getTokenUrl());
			}
			// 补齐签名
			response.getData().setSign(SignCore.xgSign(response.getData(), SignCore.SIGN_FIELD_NAME, paramRepository.getClientAppKey(request.getPartnerId())));
			Log.changeLogContextTypeToInfo();
			return ResponseEntity.ok(response);
		} catch (Throwable t) {
			response = clientService.createNewRechargeResponse(request);
			response.setCode(ErrorCode.ERR_SYSTEM);
			response.setMsg(t.getMessage());
			Log.endActionWithError(LogAction.CREATE_ORDER, t, request, response);
			return ResponseEntity.ok(response);
		} finally {
			Log.endAction(LogAction.CREATE_ORDER, response == null ? "" : response.getCode(), request, response);
		}
	}

	private CreateChannelOrderResponse createChannelOrder(ClientNewRechargeRequest cRequest) {
		// 获取渠道版本
		String channelId = cRequest.getChannelId();

		// 获取渠道实现对象
		IChannel channelImpl = channelRepository.getChannelImpl(channelId);
		// 获取渠道返回值，并返回
		if (channelImpl != null) {
			CreateChannelOrderRequest request = new CreateChannelOrderRequest();
			BeanUtils.copyProperties(cRequest, request);
			Date requestTime = new Date();
			CreateChannelOrderResponse response = channelImpl.createChannelOrder(request);
			if (response != null) {
				saveCreateChannelOrderLog(request, response, requestTime);
			}
			return response;
		}
		return null;
	}

	/**
	 * 保存会话验证请求
	 *
	 * @param request
	 * @param response
	 * @param requestTime
	 * @param requestValue
	 * @param responseValue
	 */
	private void saveCreateChannelOrderLog(CreateChannelOrderRequest request, CreateChannelOrderResponse response, Date requestTime) {
		if (response == null) {
			response = new CreateChannelOrderResponse();
		}
		RechargeRequestLog requestLog = new RechargeRequestLog();
		requestLog.setChannelId(request.getChannelId());
		requestLog.setChannelTradeNo("");
		requestLog.setEventType(RequestType.CREATE_CHANNEL_ORDER);
		requestLog.setRequestIp("127.0.0.1");
		requestLog.setRequestTime(requestTime);
		requestLog.setTradeNo("");
		requestLog.setPartnerId(request.getPartnerId());
		requestLog.setRequestValue(response.getRequestValue());
		requestLog.setResponseValue(response.getResponseValue());
		requestLog.setResponseTime(new Date());
		rechargeRequestLogService.push(requestLog);
	}

	@RequestMapping(value = "/pay-notify/{channelId}/{partnerId}")
	public ResponseEntity<?> notifyRechargeResult(HttpServletRequest originalRequest, @PathVariable String channelId, @PathVariable String partnerId) {
		String errorMessage = "";
		JSONObject requestData = null;
		String postData = null;

		//记录开始日志
		Log.startAction(LogAction.PAY_NOTIFY, partnerId, channelId, IPUtils.getRemoteAddr(originalRequest), null);
		try {

			MockHttpServletRequest hRequest = new MockHttpServletRequest(originalRequest);
			requestData = Util.parseRequestParameter(hRequest);
			postData = Util.getRequestStream(hRequest);

			originalRequest.setAttribute(IChannelAdapter.PARTNER_ID, partnerId);
			originalRequest.setAttribute(IChannelAdapter.CHANNEL_ID, channelId);

			ResponseEntity<?> ret = null;
			Log.supplementMessage(MessageFormat.format("start to notifyRechargeResult, channelId={0}, partnerId={1}, requestData={2}, postData={3}", channelId, partnerId, requestData, postData));
			Log.supplementRequestContent(StringUtils.isEmpty(postData) ? requestData : postData);
			IChannel channel = channelRepository.getChannelImpl(channelId);
			if (channel == null) {
				Log.supplementMessage(MessageFormat.format("<==notifyRechargeResult, can not find channel impl, channelId={0}", channelId));
				Log.endActionWithWarn(LogAction.PAY_NOTIFY, new Throwable("Can not find channel impl"), StringUtils.isEmpty(postData) ? requestData : postData, null);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			ret = channel.notifyRechargeResult(hRequest, channelId, partnerId);
			Log.supplementMessage(MessageFormat.format("end to notifyRechargeResult, channelId={0}, partnerId={1}, requestData={2}, response={3}", channelId, partnerId, requestData, ret.getBody()));
			Log.endAction(LogAction.PAY_NOTIFY, null, StringUtils.isEmpty(postData) ? requestData : postData, ret);
			return ret;
		} catch (Throwable t) {
			errorMessage = MessageFormat.format("error in notifyRechargeResult, channelId={0}, partnerId={1}, requestData={2}, error={3}", channelId, partnerId, requestData, t.getMessage());
			Log.supplementMessage(errorMessage);
			Log.supplementExceptionMessage(t);
			Log.endActionWithError(LogAction.PAY_NOTIFY, t, StringUtils.isEmpty(postData) ? requestData : postData, null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@RequestMapping(value = "/search-channel-order")
	public ResponseEntity<?> searchChannelOrder(HttpServletRequest hRequest) {
		JSONObject ret = new JSONObject();
		String errorMessage = "";
		String partnerId = hRequest.getParameter("partnerId");
		String tradeNo = hRequest.getParameter("tradeNo");
		String ts = hRequest.getParameter("ts");
		String sign = hRequest.getParameter("sign");
		try {
			Log.startAction(LogAction.SEARCH_CHANNEL_ORDER, null, null, IPUtils.getRemoteAddr(hRequest), null);
			Log.supplementBizInfo(partnerId, null, tradeNo, null, null, sign, null);
			Log.changeLogContextTypeToAppErr();
			String okSign = Util.HmacSHA1Encrypt(MessageFormat.format("partnerId={0}&tradeNo={1}&ts={2}", partnerId, tradeNo, ts), paramRepository.getClientAppKey(partnerId));
			if (StringUtils.equals(sign, okSign)) {
				if (channelService.searchChannelOrder(tradeNo)) {
					ret.put("code", "0");
					ret.put("msg", "查询成功");
					Log.changeLogContextTypeToInfo();
				} else {
					// 请求重发
					ret.put("code", "1");
					ret.put("msg", "fail");
				}
			} else {
				ret.put("code", "-1");
				ret.put("msg", "签名失败");
			}
			Log.supplementMessage("end to searchChannelOrder, partnerId={}, tradeNo={}, response={}", partnerId, tradeNo, ret);
			Log.endAction(LogAction.SEARCH_CHANNEL_ORDER, (String) ret.get("code"), null, ret);
			return ResponseEntity.ok(JSON.toJSON(ret));
		} catch (Throwable t) {
			errorMessage = MessageFormat.format("error in searchChannelOrder, partnerId={0}, tradeNo={1}, error={2}", partnerId, tradeNo, t.getMessage());
			Log.supplementMessage(errorMessage);
			Log.supplementExceptionMessage(t);
			Log.endActionWithError(LogAction.SEARCH_CHANNEL_ORDER, t, null, null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

}
