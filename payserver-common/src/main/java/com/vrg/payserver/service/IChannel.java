package com.vrg.payserver.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.vrg.payserver.service.vo.CreateChannelOrderRequest;
import com.vrg.payserver.service.vo.CreateChannelOrderResponse;
import com.vrg.payserver.service.vo.VerifyChannelOrderRequest;
import com.vrg.payserver.service.vo.VerifyChannelOrderResponse;


public interface IChannel {
	void setServerCoreService(IServerCoreService serverCoreService);

	CreateChannelOrderResponse createChannelOrder(CreateChannelOrderRequest request);

	ResponseEntity<?> notifyRechargeResult(HttpServletRequest hRequest, String channelId, String xgAppId);

	VerifyChannelOrderResponse queryRechargeResult(VerifyChannelOrderRequest request);
}
