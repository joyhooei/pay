package com.vrg.payserver.dao.model;

import com.alibaba.fastjson.JSONObject;

public class ChannelParamValue {
	private String partnerId;
	private String channelId;
	private String paramName;
	private String paramValue;

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String value) {
		this.paramValue = value;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
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

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
