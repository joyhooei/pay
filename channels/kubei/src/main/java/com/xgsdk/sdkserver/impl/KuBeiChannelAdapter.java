package com.xgsdk.sdkserver.impl;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrg.payserver.service.IChannelAdapter;
import com.vrg.payserver.service.IServerCoreService;
import com.vrg.payserver.service.SearchChannelOrderType;
import com.vrg.payserver.service.vo.ChannelData;
import com.vrg.payserver.service.vo.ChannelRequest;
import com.vrg.payserver.service.vo.CreateChannelOrderRequest;
import com.vrg.payserver.service.vo.CreateChannelOrderResponse;
import com.vrg.payserver.service.vo.CreateChannelOrderResponseData;
import com.vrg.payserver.util.CurrencyUtil;
import com.vrg.payserver.util.DateUtil;
import com.vrg.payserver.util.ErrorCode;
import com.vrg.payserver.util.HttpUtils;
import com.vrg.payserver.util.Log;
import com.vrg.payserver.util.Util;
import com.xgsdk.sdkserver.impl.vo.KuBeiCreateOrderRequest;
import com.xgsdk.sdkserver.impl.vo.KuBeiNotifyRequest;
import com.xgsdk.sdkserver.impl.vo.KuBeiQueryOrderRequest;

/**
 *
 */
public class KuBeiChannelAdapter implements IChannelAdapter {
	private static final String PID = "160080";
	private static final String MACID = "0199800000000749";
	private static final String KEY = "BB1F64449C0FE32B81DFE37BBBFD288A";
	private static final String NOTIFY_URL = "http://121.201.65.16:8090//pay-notify/{0}/{1}";
	
	private static final String QUERY_ORDER_URL = "http://demo.counect.com/vcupe/queryPay.do?";
	private static final String CREATE_ORDER_URL = "http://demo.counect.com/vcupe/getPay.do?";
	private IServerCoreService serverCoreService;

	@Override
	public void setServerCoreService(IServerCoreService serverCoreService) {
		this.serverCoreService = serverCoreService;
	}

	@Override
	public ChannelData parsePayNotice(HttpServletRequest hRequest, String channelId) {
		ChannelData requestData = new ChannelData();
		try {
			KuBeiNotifyRequest channelData = Util.parseRequestParameter(hRequest, KuBeiNotifyRequest.class);
			requestData.setStateCode(ErrorCode.SUCCESS);
			requestData.setPayStatus(ErrorCode.SUCCESS);
			requestData.setTradeNo(channelData.getP7());
			requestData.setChannelTradeNo(channelData.getP0());
			requestData.setPaidAmount(CurrencyUtil.yuanToFenInt(channelData.getP3()));
			requestData.setPaidTime(DateUtil.parse(channelData.getP2()));
			requestData.setChannelObject(channelData);
		} catch (Throwable t) {
			Log.supplementExceptionMessage(t);
			requestData.setStateCode("-1");
			requestData.setStateMsg(t.getMessage());
		}
		return requestData;
	}

	@Override
	public boolean verifyPayNoticeSign(ChannelData xgRequestData, String planId, String channelId) {
		KuBeiNotifyRequest channelData = (KuBeiNotifyRequest) xgRequestData.getChannelObject();
		return channelData.checkSign(KEY);
	}

	@Override
	public ResponseEntity<?> getPayNoticeResponse(String stateCode, String stateMsg, String planId, String channelId, ChannelData channelRequestData) {
		if (ErrorCode.isPayNoticeResponseSuccess(stateCode)) {
			return ResponseEntity.ok("success");
		} else {
			return ResponseEntity.ok("fail");
		}
	}

	@Override
	public SearchChannelOrderType searchChannelOrder(ChannelData channelRequestData, String planId, String channelId, ChannelData channelResponseData) {
		
		KuBeiQueryOrderRequest queryOrderRequest = new KuBeiQueryOrderRequest();
		queryOrderRequest.setP(PID);
		queryOrderRequest.setP0(channelRequestData.getChannelTradeNo());
		queryOrderRequest.setP1(MACID);
		queryOrderRequest.setP2(DateUtil.format(channelRequestData.getPaidTime()));
		
		String response = HttpUtils.doGet(QUERY_ORDER_URL + queryOrderRequest.genURLParameter(KEY));
		if (StringUtils.isEmpty(response)) {
			return SearchChannelOrderType.NOK;
		}
		
		JSONObject responseObj = JSON.parseObject(response);
		if (!StringUtils.equals(responseObj.getString("RESULT"), "0")) {
			return SearchChannelOrderType.NOK;
		}
		
		JSONObject resultBody = JSON.parseObject(responseObj.getString("BODY"));
		if (StringUtils.equals(resultBody.getString("s"), "1")) {
			channelResponseData.setStateCode(ErrorCode.SUCCESS);
			channelResponseData.setPayStatus(ChannelRequest.PAY_STATUS_SUCCESS);
			channelResponseData.setChannelTradeNo(resultBody.getString("p0"));
			channelResponseData.setChannelId("kubei");
			return SearchChannelOrderType.OK;
		}
		
		return SearchChannelOrderType.NOK;
	}

	@Override
	public CreateChannelOrderResponse createChannelOrder(CreateChannelOrderRequest request) {
		CreateChannelOrderResponse response = new CreateChannelOrderResponse();
		CreateChannelOrderResponseData data = new CreateChannelOrderResponseData();

		KuBeiCreateOrderRequest kudongPay = new KuBeiCreateOrderRequest();
    	kudongPay.setN(MessageFormat.format(NOTIFY_URL, "kubei", request.getPartnerId()));
    	kudongPay.setP(PID);
    	kudongPay.setP1(MACID);
    	kudongPay.setP2(request.getTradeNo());
    	kudongPay.setP3(request.getPaidAmount());
    	
    	String doGet = HttpUtils.doGet(CREATE_ORDER_URL + kudongPay.genURLParameter(KEY));
    	
    	if (StringUtils.isEmpty(doGet)) {
			response.setCode(ErrorCode.ERR_CREATE_CHANNEL_ORDER_FAIL);
			response.setMsg(MessageFormat.format(
				"Can not connect to kubei channel server[{0}].", CREATE_ORDER_URL));

			return response;
		}
    	
    	JSONObject resultObject = JSON.parseObject(doGet);
    	if (!StringUtils.equals("0", resultObject.getString("RESULT"))) {
			response.setCode(ErrorCode.ERR_CREATE_CHANNEL_ORDER_FAIL);
			response.setMsg("The response of kubei channel is create order fail");

			return response;
		}
    	response.setCode(ErrorCode.SUCCESS);
    	data.setUrl(resultObject.getString("BODY"));
    	response.setData(data);
        return response;
	}

}
