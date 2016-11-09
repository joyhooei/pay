package com.xgsdk.sdkserver.impl.vo;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.vrg.payserver.util.Util;

public class KuBeiNotifyRequest {
	String c;
	Long t;
	String r;
	String p0;
	String p1;
	String p2;
	String p3;
	String p4;
	String p5;
	String p6;
	String p7;
	String s;
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public Long getT() {
		return t;
	}
	public void setT(Long t) {
		this.t = t;
	}
	public String getR() {
		return r;
	}
	public void setR(String r) {
		this.r = r;
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
	public String getP4() {
		return p4;
	}
	public void setP4(String p4) {
		this.p4 = p4;
	}
	public String getP5() {
		return p5;
	}
	public void setP5(String p5) {
		this.p5 = p5;
	}
	public String getP6() {
		return p6;
	}
	public void setP6(String p6) {
		this.p6 = p6;
	}
	public String getP7() {
		return p7;
	}
	public void setP7(String p7) {
		this.p7 = p7;
	}
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	
	public boolean isValid() {
		return true;
	}
	public boolean checkSign(String key)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(c).append(p0).append(p1).append(p2).append(p3).append(p4).append(p5).append(p6).append(p7).append(r).append(t).append(key);
		return StringUtils.upperCase(Util.getMD5Str(sb.toString())).equals(s);
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
