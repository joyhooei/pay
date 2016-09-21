/**
 *
 */
package com.vrg.payserver.service.vo;

import com.alibaba.fastjson.JSON;

public class ClientNewRechargeRequest {
	// type 是 String 接口类型，固定为update-order
	private String type;
	
	/*
	 * partnerId 设备编号
	 */
	private String partnerId;
	
	// uid 否 String 渠道的用户编号
	private String uid;

	/*
	 * currencyName 否 String 支付货币名称
	 */
	private String currencyName;
	
	/*
	 *  paidAmount 否 String 总支付金额(单位分)
	 */
	private String paidAmount;
	
	/*
	 *  customInfo 预留的扩展字段
	 */
	private String customInfo;
	
	/*
	 *  ts 是 String 当前时间戳，秒级，如20150723150028对应2015/7/23 15:00:28
	 */
	private String ts;

	/*
	 *  sign 是 String 签名，签名算法参见签名章节，使用游戏客户端密钥
	 */
	private String sign;
	
	/*
	 *  tradeNo 当前系统的订单号
	 */
	private String tradeNo;
	
	/*
	 *  channelTradeNo 支付渠道侧分配的订单号
	 */
	private String channelTradeNo;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getCustomInfo() {
		return customInfo;
	}

	public void setCustomInfo(String customInfo) {
		this.customInfo = customInfo;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getChannelTradeNo() {
		return channelTradeNo;
	}

	public void setChannelTradeNo(String channelTradeNo) {
		this.channelTradeNo = channelTradeNo;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
