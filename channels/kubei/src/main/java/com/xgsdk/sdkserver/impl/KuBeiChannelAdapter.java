package com.xgsdk.sdkserver.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.vrg.payserver.service.IChannelAdapter;
import com.vrg.payserver.service.IServerCoreService;
import com.vrg.payserver.service.SearchChannelOrderType;
import com.vrg.payserver.service.vo.ChannelData;
import com.vrg.payserver.service.vo.CreateChannelOrderRequest;
import com.vrg.payserver.service.vo.CreateChannelOrderResponse;
import com.vrg.payserver.service.vo.CreateChannelOrderResponseData;
import com.vrg.payserver.util.CurrencyUtil;
import com.vrg.payserver.util.DateUtil;
import com.vrg.payserver.util.ErrorCode;
import com.vrg.payserver.util.HttpUtils;
import com.vrg.payserver.util.Util;
import com.xgsdk.sdkserver.impl.vo.KuBeiCreateOrderRequest;
import com.xgsdk.sdkserver.impl.vo.KuBeiNotifyRequest;

/**
 *
 */
public class KuBeiChannelAdapter implements IChannelAdapter {
	private static final String PID = "160080";
	private static final String MACID = "0199800000000749";
	private static final String KEY = "BB1F64449C0FE32B81DFE37BBBFD288A";
	private static final String NOTIFY_URL = "http://121.201.65.16:18888/GetRequest";
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
			requestData.setStateCode("0");
			requestData.setTradeNo(channelData.getP7());
			requestData.setChannelTradeNo(channelData.getP0());
			requestData.setPaidAmount(CurrencyUtil.yuanToFenInt(channelData.getP3()));
			requestData.setPaidTime(DateUtil.parse(channelData.getP2()));
		} catch (Throwable t) {
//			XGLog.supplementExceptionMessage(t);
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
		return SearchChannelOrderType.NOTSUPPORT;
	}

	@Override
	public CreateChannelOrderResponse createChannelOrder(CreateChannelOrderRequest request) {
		CreateChannelOrderResponse response = new CreateChannelOrderResponse();
		CreateChannelOrderResponseData data = new CreateChannelOrderResponseData();
		response.setData(data);
		KuBeiCreateOrderRequest kudongPay = new KuBeiCreateOrderRequest();
    	kudongPay.setN(NOTIFY_URL);
    	kudongPay.setP(PID);
    	kudongPay.setP1(MACID);
    	kudongPay.setP2(request.getTradeNo());
    	kudongPay.setP3(request.getPaidAmount());
    	
    	String doGet = HttpUtils.doGet("http://demo.counect.com/vcupe/getPay.do?" + kudongPay.genURLParameter(KEY));

    	data.setUrl(doGet);
        return response;
	}

}
