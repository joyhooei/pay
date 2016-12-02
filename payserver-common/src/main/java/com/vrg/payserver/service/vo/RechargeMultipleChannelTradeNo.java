package com.vrg.payserver.service.vo;

import java.util.Date;

public class RechargeMultipleChannelTradeNo {
	public static final String STATUS_NEW = "NEW";
	public static final String STATUS_COMPLETED = "COMPLETED";
	public static final String STATUS_DISCARD = "DISCARD";
	private String tradeNo;
	private String newTradeNo;
	private String channelTradeNo;
	private String partnerId;
	private String channelId;
	private int paidAmount;
	private String requestIp;
	private Date orderCreateTime;
	private Date orderNotifyTime;
	private String status; // NEW, COMPLETED, DISCARD
	private String reason;
	private String updateBy;
	private Date updateTime;

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getNewTradeNo() {
		return newTradeNo;
	}

	public void setNewTradeNo(String newTradeNo) {
		this.newTradeNo = newTradeNo;
	}

	public String getChannelTradeNo() {
		return channelTradeNo;
	}

	public void setChannelTradeNo(String channelTradeNo) {
		this.channelTradeNo = channelTradeNo;
	}

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

	public int getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(int paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getRequestIp() {
		return requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	public Date getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public Date getOrderNotifyTime() {
		return orderNotifyTime;
	}

	public void setOrderNotifyTime(Date orderNotifyTime) {
		this.orderNotifyTime = orderNotifyTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
