package com.vrg.payserver.dao.model;

import com.alibaba.fastjson.JSONObject;

public class ChannelParamValue {
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

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
