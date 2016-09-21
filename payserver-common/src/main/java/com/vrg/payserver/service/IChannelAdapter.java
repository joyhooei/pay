/**
 * 
 */
package com.vrg.payserver.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.vrg.payserver.service.vo.ChannelData;
import com.vrg.payserver.service.vo.CreateChannelOrderRequest;
import com.vrg.payserver.service.vo.CreateChannelOrderResponse;

/**
 *
 */
public interface IChannelAdapter {
	
	public static final String PARTNER_ID = "partnerId";
	public static final String CHANNEL_ID = "channelId";

	void setServerCoreService(IServerCoreService serverCoreService);

	/**
	 * 解析原始支付渠道请求，如果有二级参数，也需要进行解析
	 *
	 * @param hRequest
	 * @param xgRequest
	 */
	ChannelData parsePayNotice(HttpServletRequest hRequest, String channelId);

	/**
	 * 验证请求签名
	 * 
	 * @param channelRequestData
	 * @param 
	 * @return
	 */
	boolean verifyPayNoticeSign(ChannelData channelRequestData,  String partnerId, String channelId);

	/**
	 * 获取渠道返回值
	 *
	 * @param equest
	 * @return
	 */
	ResponseEntity<?> getPayNoticeResponse(String stateCode, String stateMsg, String planId, String channelId, ChannelData channelRequestData);

	/**
	 * 完成渠道订单查询、响应消息验签、响应消息解析并回填到request中。<br>
	 * 如果不支持则直接返回SEARCH_CHANNEL_ORDER_NOTSUPPORT
	 *
	 * @param xgRequest
	 * @return
	 */
	SearchChannelOrderType searchChannelOrder(ChannelData channelRequestData, String partnerId, String channelId, ChannelData channelResponseData);


	/**
	 * 创建渠道订单
	 * 
	 * @param request
	 * @return
	 */
	CreateChannelOrderResponse createChannelOrder(CreateChannelOrderRequest request);
}
