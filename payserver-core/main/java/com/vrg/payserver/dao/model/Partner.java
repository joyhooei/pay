package com.vrg.payserver.dao.model;

import com.alibaba.fastjson.JSONObject;

public class Partner {
	
	private long id;
	private String partnerId;
	private String secretKey;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
