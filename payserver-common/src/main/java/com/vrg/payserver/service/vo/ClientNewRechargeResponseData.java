/**
 * 
 */
package com.vrg.payserver.service.vo;

import com.alibaba.fastjson.JSON;

/**
 * @author LUCHUNLIANG
 *
 */
public class ClientNewRechargeResponseData {
	// channelPartnerId 否 String 渠道分配唯一标识
	//private String channelPartnerId;
	// partnerId 是 String vrg的唯一标识
	private String partnerId;
	// channelId 是 String 运营渠道编号
	//private String channelId;
	// tradeNo 是 String 订单号
	private String tradeNo;
	// channelTradeNo 否 String 渠道订单号
	//private String channelTradeNo;
	// prepayId 否 String 预支付订单号
	private String prepayId;
	// nonceStr 否 String 32位内的随机串，防重发
	private String nonceStr;
	// customInfo 否 String 自定义字段，原样返回
	private String customInfo;
	// tokenUrl 否 String
	// 目前用于腾讯渠道应用侧在唤起购买SDK前，应用后台支付服务器下的订单Url，也就是调用buy_goods_m支付Api时返回的url_params的值
	private String tokenUrl;
	// submitTime 否 String 订单创建时间，目前用于金立，时间格式是yyyyMMddHHmmss
	private String submitTime;
	// sign 是 String 签名，签名算法参见签名章节，使用游戏客户端密钥[]
	private String sign;

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	/**
	 * @return the channelId
	 */
//	public String getChannelId() {
//		return channelId;
//	}

	/**
	 * @param channelId
	 *            the channelId to set
	 */
//	public void setChannelId(String channelId) {
//		this.channelId = channelId;
//	}

	/**
	 * @return the tradeNo
	 */
	public String getTradeNo() {
		return tradeNo;
	}

	/**
	 * @param tradeNo
	 *            the tradeNo to set
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	/**
	 * @return the channelTradeNo
	 */
//	public String getChannelTradeNo() {
//		return channelTradeNo;
//	}

	/**
	 * @param channelTradeNo
	 *            the channelTradeNo to set
	 */
//	public void setChannelTradeNo(String channelTradeNo) {
//		this.channelTradeNo = channelTradeNo;
//	}

	/**
	 * @return the prepayId
	 */
	public String getPrepayId() {
		return prepayId;
	}

	/**
	 * @param prepayId
	 *            the prepayId to set
	 */
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}

	/**
	 * @return the nonceStr
	 */
	public String getNonceStr() {
		return nonceStr;
	}

	/**
	 * @param nonceStr
	 *            the nonceStr to set
	 */
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	/**
	 * @return the customInfo
	 */
	public String getCustomInfo() {
		return customInfo;
	}

	/**
	 * @param customInfo
	 *            the customInfo to set
	 */
	public void setCustomInfo(String customInfo) {
		this.customInfo = customInfo;
	}

	/**
	 * @return the tokenUrl
	 */
	public String getTokenUrl() {
		return tokenUrl;
	}

	/**
	 * @param tokenUrl
	 *            the tokenUrl to set
	 */
	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	/**
	 * @return the submitTime
	 */
	public String getSubmitTime() {
		return submitTime;
	}

	/**
	 * @param submitTime
	 *            the submitTime to set
	 */
	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * @param sign
	 *            the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

//	public String getChannelPartnerId() {
//		return channelPartnerId;
//	}
//
//	public void setChannelPartnerId(String channelPartnerId) {
//		this.channelPartnerId = channelPartnerId;
//	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

}
