/**
 *
 */
package com.vrg.payserver.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrg.payserver.service.vo.RechargeRequestException;

@Service
public class RechargeRequestExceptionService {

//	@Autowired
//	private RechargeRequestExceptionMapper rechargeRequestExceptionMapper;

	public void create(String requestType, Date requestTime, Object request, Object response, String requestIp,
			String tradeNo, String remarks) {
//		RechargeRequestException rechargeRequestLog = RechargeRequestException
//				.getRechargeRequestExceptionFromClientRequest(requestIp, request, requestTime);
//		rechargeRequestLog.putClientResponseData(response, new Date());
//		rechargeRequestLog.setEventType(requestType);
//		rechargeRequestLog.setTradeNo(tradeNo);
//		rechargeRequestLog.setRemarks(remarks);
//		rechargeRequestExceptionMapper.create(rechargeRequestLog);
	}

	public void create(RechargeRequestException rechargeRequestException) {
//		rechargeRequestExceptionMapper.create(rechargeRequestException);
	}

}
