package com.vrg.payserver.controller;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrg.payserver.ChannelService;
import com.vrg.payserver.repository.ChannelRepository;
import com.vrg.payserver.service.IChannel;
import com.vrg.payserver.service.IChannelAdapter;
import com.vrg.payserver.service.ParamRepository;
import com.vrg.payserver.util.IPUtils;
import com.vrg.payserver.util.Log;
import com.vrg.payserver.util.Util;
import com.vrg.payserver.util.logging.LogAction;


@RestController
public class ChannelController {

	@Autowired
	private ChannelRepository channelRepository;
//	@Autowired
//	private RechargeRequestLogService rechargeRequestLogService;
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private ParamRepository paramRepository;

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
