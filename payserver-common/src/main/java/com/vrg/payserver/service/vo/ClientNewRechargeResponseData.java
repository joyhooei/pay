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
	// channelAppId 否 String 渠道分配的游戏编号
	private String channelAppId;
	// xgAppId 是 String Xgsdk分配的游戏编号
	private String xgAppId;
	// channelId 是 String 运营渠道编号
	private String channelId;
	// planId 否 String 发布计划编号
	private String planId;
	// uid 是 String 渠道用户编号
	private String uid;
	// tradeNo 是 String 订单号
	private String tradeNo;
	// channelTradeNo 否 String 渠道订单号
	private String channelTradeNo;
	// prepayId 否 String 预支付订单号
	private String prepayId;
	// nonceStr 否 String 32位内的随机串，防重发
	private String nonceStr;
	// customInfo 否 String 自定义字段，原样返回
	private String customInfo;
	// gameTradeNo 否 String 游戏侧订单号
	private String gameTradeNo;
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
	 * @return the channelAppId
	 */
	public String getChannelAppId() {
		return channelAppId;
	}

	/**
	 * @param channelAppId
	 *            the channelAppId to set
	 */
	public void setChannelAppId(String channelAppId) {
		this.channelAppId = channelAppId;
	}

	/**
	 * @return the xgAppId
	 */
	public String getXgAppId() {
		return xgAppId;
	}

	/**
	 * @param xgAppId
	 *            the xgAppId to set
	 */
	public void setXgAppId(String xgAppId) {
		this.xgAppId = xgAppId;
	}

	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId
	 *            the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

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
	public String getChannelTradeNo() {
		return channelTradeNo;
	}

	/**
	 * @param channelTradeNo
	 *            the channelTradeNo to set
	 */
	public void setChannelTradeNo(String channelTradeNo) {
		this.channelTradeNo = channelTradeNo;
	}

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
	 * @return the gameTradeNo
	 */
	public String getGameTradeNo() {
		return gameTradeNo;
	}

	/**
	 * @param gameTradeNo
	 *            the gameTradeNo to set
	 */
	public void setGameTradeNo(String gameTradeNo) {
		this.gameTradeNo = gameTradeNo;
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

	/**
	 * @return the planId
	 */
	public String getPlanId() {
		return planId;
	}

	/**
	 * @param planId
	 *            the planId to set
	 */
	public void setPlanId(String planId) {
		this.planId = planId;
	}

}
