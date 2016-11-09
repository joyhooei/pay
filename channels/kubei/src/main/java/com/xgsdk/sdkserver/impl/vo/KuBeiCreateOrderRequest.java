package com.xgsdk.sdkserver.impl.vo;

import com.alibaba.fastjson.JSON;
import com.vrg.payserver.util.Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class KuBeiCreateOrderRequest {
	String p;
	int t;
	String r;
	String p0;
	String p1;
	String p2;
	String p3;
	String n;
	String s;
	
	public void CalSign(String key)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(n).append(p).append(p1).append(p2).append(p3).append(r).append(t).append(key);
		s = StringUtils.upperCase(Util.getMD5Str(sb.toString()));
	}
	
	public String genURLParameter(String key) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("n=").append(URLEncoder.encode(n,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	sb.append("&&p=").append(p);
    	sb.append("&&p1=").append(p1);
    	sb.append("&&p2=").append(p2);
    	sb.append("&&p3=").append(p3);
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

	public String getP3() {
		return p3;
	}

	public void setP3(String p3) {
		this.p3 = p3;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
