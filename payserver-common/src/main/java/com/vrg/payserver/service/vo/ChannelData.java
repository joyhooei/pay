package com.vrg.payserver.service.vo;

import java.util.Date;

import com.alibaba.fastjson.JSON;

public class ChannelData {
	
	/**
	 * 设备id
	 */
	private String partnerId;
	
	/**
	 * 渠道id
	 */
	private String channelId;
	
	/**
	 * 交易流水号
	 */
	private String tradeNo;
	
	/**
	 * 支付系统的流水号
	 */
	private String channelTradeNo;
	
	/**
	 * 支付时间
	 */
	private Date paidTime;
	
	/**
	 * 支付金额，单位为分
	 */
	private int paidAmount = -1; 
	
	/**
	 * 货币类型
	 */
	private String currencyCode; 
	
	/**
	 * 支付状态
	 */
	private String payStatus;

	/**
	 * 附加信息
	 */
	private String customInfo;
	
	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
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

	public Date getPaidTime() {
		return paidTime;
	}

	public void setPaidTime(Date paidTime) {
		this.paidTime = paidTime;
	}

	public int getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(int paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getCustomInfo() {
		return customInfo;
	}

	public void setCustomInfo(String customInfo) {
		this.customInfo = customInfo;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
}
