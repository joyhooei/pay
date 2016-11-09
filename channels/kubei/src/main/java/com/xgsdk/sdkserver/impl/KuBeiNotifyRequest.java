package com.xgsdk.sdkserver.impl;

import com.alibaba.fastjson.JSON;
import com.vrg.payserver.util.Util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class KuBeiNotifyRequest {
	private String outTradeNo;// 商户唯一订单号
	private String cooperatorTradeNo; // 合作方订单号
	private String applicationName; // 应用名称
	private String packageName; // 应用包名
	private String productName; // 用户购买产品名称
	private String productCount;// 用户购买产品数量
	private String payFee;// 交易金额,单位:元
	private String signType;// 签名类型
	private String sign;// 签名
	private String tradeStatus;// 交 易 状 态 ， 成 功 为TRADE_SUCCESS，其它失败
	public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String NOTIFY_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String SIGN_ERR = "-2";
	public static final String USER_NO_EXIST = "-3";

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getCooperatorTradeNo() {
		return cooperatorTradeNo;
	}

	public void setCooperatorTradeNo(String cooperatorTradeNo) {
		this.cooperatorTradeNo = cooperatorTradeNo;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCount() {
		return productCount;
	}

	public void setProductCount(String productCount) {
		this.productCount = productCount;
	}

	public String getPayFee() {
		return payFee;
	}

	public void setPayFee(String payFee) {
		this.payFee = payFee;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public boolean checkSign(String key) {
		String okSign = generateSign(key);
		return StringUtils.equalsIgnoreCase(okSign, sign);
	}

	public String generateSign(String key) {
		String okSign = Hex.encodeHexString(DigestUtils.md5(Util.getSigningString(this, "signType,sign", false) + key));
		return okSign;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
