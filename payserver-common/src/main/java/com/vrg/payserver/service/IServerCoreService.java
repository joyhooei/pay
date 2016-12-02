package com.vrg.payserver.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.vrg.payserver.service.vo.ChannelNotifyRequest;
import com.vrg.payserver.service.vo.ChannelRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeResponse;
import com.vrg.payserver.service.vo.RechargeRecordBase;
import com.vrg.payserver.service.vo.RechargeRequestException;
import com.vrg.payserver.service.vo.RechargeRequestLog;


public interface IServerCoreService {

	String getParamValue(String planId, String channelId, String paramName);

	List<String> getParamValues(String xgAppId, String channelId, String paramName);

	List<RechargeRecordBase> getUnPaidRechargeRecordByUid(String xgAppId, String channelId, String uid);

	List<RechargeRecordBase> getWaitPayRechargeRecordByUid(String xgAppId, String channelId, String uid);

	RechargeRecordBase getRechargeRecordByProductId(String xgAppId, String channelId, String uid, String productId);

	RechargeRecordBase getRechargeRecordByTradeNo(String tradeNo);

	RechargeRecordBase getRechargeRecordByChannelTradeNo(String channelTadeNo, String channelId);

	boolean checkRechargeRecordStatus(int status);

	/**
	 * 渠道订单号排重，serverCoreService
	 *
	 * @param channelTradeNo
	 * @param channelId
	 * @return
	 */
	boolean checkChannelTradeNo(String tradeNo, String channelTradeNo, String originalChannelTradeNo, String channelId, Date paidTime);

	void onPayException(ChannelNotifyRequest xgRequest);

	void onPaySuccess(ChannelRequest xgRequest);

	void onPayException(ChannelRequest xgRequest);
	
	void onPayFail(RechargeRecordBase rechargeRecord);

	/**
	 * 获取游戏服务端密钥
	 *
	 * @param xgAppId
	 * @return
	 */
	public String getServerAppSecret(String planId);

	/**
	 * 创建订单
	 *
	 * @param request
	 * @return
	 */
	public ClientNewRechargeResponse createOrder(ClientNewRechargeRequest request);

	void saveRechargeRequestLog(HttpServletRequest hRequest, String requestType, Date requestTime, Object request, Object response, Object bizObject);

	/**
	 * 记录请求日志
	 *
	 * @param rechargeRequestLog
	 */
	void saveRequestLog(String requestType, Date requestTime, Object request, Object response, Object bizObject, String requestIp, String tradeNo);

	/**
	 * 记录请求日志
	 *
	 * @param rechargeRequestLog
	 */
	void saveRequestLog(RechargeRequestLog rechargeRequestLog);

	/**
	 * 记录异常请求日志
	 */
	void saveRequestException(RechargeRequestException rechargeRequestException);
	
	void saveRequestException(ChannelRequest channelRequest, RechargeRecordBase rechargeRecord);
	
	/**
	 * 取消订单
	 *
	 * @param rechargeRecord
	 */
	public void cancelRechargeRecordStatus(RechargeRecordBase rechargeRecord);

	public void deleteRechargeFailLog(RechargeRecordBase rechargeFailLog);

}
