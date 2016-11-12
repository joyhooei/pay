package com.xgsdk.sdkserver.impl.vo;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.vrg.payserver.util.Util;

public class KuBeiQueryOrderRequest {
	String p;
	int t;
	String r;
	String p0;
	String p1;
	String p2;
	String s;
	
	public void CalSign(String key)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(p).append(p0).append(p1).append(p2).append(r).append(t).append(key);
		s = StringUtils.upperCase(Util.getMD5Str(sb.toString()));
	}
	
	public String genURLParameter(String key) {
		StringBuilder sb = new StringBuilder();
		sb.append("p=").append(p);
    	sb.append("&&p0=").append(p0);
    	sb.append("&&p1=").append(p1);
    	sb.append("&&p2=").append(p2);
    	r = String.valueOf((int)(1+Math.random()*100));
    	sb.append("&&r=").append(r);
    	t = (int) (System.currentTimeMillis() / 1000);
    	sb.append("&&t=").append(t);
    	CalSign(key);
    	sb.append("&&s=").append(s);
		return sb.toString();
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getP0() {
		return p0;
	}

	public void setP0(String p0) {
		this.p0 = p0;
	}

	public String getP1() {
		return p1;
	}

	public void setP1(String p1) {
		this.p1 = p1;
	}

	public String getP2() {
		return p2;
	}

	public void setP2(String p2) {
		this.p2 = p2;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
