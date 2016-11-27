package com.vrg.payserver.dao.model;

import java.math.BigDecimal;

public class RechargeClientNotifyRequest {
	private BigDecimal id;
	private String xgAppId;
	private String channelId;
	private String planId;
	private String uid;
	private String bizKey;
	private String requestValue1;
	private String requestValue2;

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getXgAppId() {
		return xgAppId;
	}

	public void setXgAppId(String xgAppId) {
		this.xgAppId = xgAppId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getBizKey() {
		return bizKey;
	}

	public void setBizKey(String bizKey) {
		this.bizKey = bizKey;
	}

	public String getRequestValue1() {
		return requestValue1;
	}

	public void setRequestValue1(String requestValue1) {
		this.requestValue1 = requestValue1;
	}

	public String getRequestValue2() {
		return requestValue2;
	}

	public void setRequestValue2(String requestValue2) {
		this.requestValue2 = requestValue2;
	}

}
