package com.vrg.payserver.service.vo;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.vrg.payserver.util.Util;

public class RechargeRecordBase {
	// status
	public static final int STATUS_INIT = -1;
	public static final int STATUS_WAITPAY = 0;
	public static final int STATUS_NOTIFY_SUCCESS = 1;
	public static final int STATUS_BOOKED = 2;
	public static final int STATUS_FAIL = 3;
	public static final int STATUS_CREATED = 4;
	public static final int STATUS_GAME_REJECT = 5;
	public static final int STATUS_NOTIFY_FAIL = 6;
	// 98，产品编号不一致，有可能被攻击
	public static final int STATUS_EXCEPTION = 98;
	public static final int STATUS_DISCARD = 99;
	
	// TRADE_NO VARCHAR2(64) 支付网关订单号 主键
	private String tradeNo;
	
	// 接入商的身份标识
	private String partnerId;
	
	//支付渠道ID
	private String channelId;
	
	// CHARGE_LOG_ID NUMBER(20) 充值流水ID 主键
	private long chargeLogId;

	// UID VARCHAR2(128) 渠道的用户编号
	private String uid;
	
	// DEVICE_ID VARCHAR2(100) 设备编号
	private String deviceId;
	
	// PAID_AMOUNT NUMBER(10) 总支付金额(单位分)
	private int paidAmount;
	// PAID_TIME DATE 订单支付时间
	private Date paidTime = new Date();
	// CHANNEL_TRADE_NO VARCHAR(64) 渠道订单号
	private String channelTradeNo;
	// CHARGE_CHANNEL_INST VARCHAR2(128) 支付渠道详细信息(中国工商银行)
	private String channelTradeInst;
	// SEARCH_CHANNEL_ORDER_TIMES NUMBER(2)
	// 查询渠道订单状态次数(以1分钟为基数，次方递增，1分钟轮询一次，2、4、8、16...分钟继续轮询）
	private int searchChannelOrderTimes;
	// STATUS NUMBER(2) 交易状态
	// -1 初始状态(client还未返回支付成功更新，超过1天后，将会搬迁到作废日志表)
	// 0 等待支付(client已经返回支付成功更新)
	// 1 待通知成功
	// 2 支付成功，已入账
	// 3 支付失败
	// 4 生成二维码
	// 5 游戏拒绝接收
	// 6 待通知失败
	// 98 异常，疑似被攻击
	// 99 作废
	private int status;
	// CUSTOM_INFO VARCHAR2(2000) 游戏方自定义字段
	private String customInfo;
	// CREATE_TIME DATE 订单创建时间
	private Date createTime = new Date();	
	// EXCEPTION_INFO VARCHAR2(256) 异常信息
	private String exceptionInfo;
	// STATE_CODE VARCHAR2(16) 异常错误码
	private String stateCode;
	
	/**
	 * vrg在支付渠道的唯一标识
	 */
	private String channelPartnerId;
	
	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public long getChargeLogId() {
		return chargeLogId;
	}

	public void setChargeLogId(long chargeLogId) {
		this.chargeLogId = chargeLogId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(int paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Date getPaidTime() {
		return paidTime;
	}

	public void setPaidTime(Date paidTime) {
		this.paidTime = paidTime;
	}

	public String getChannelTradeNo() {
		return channelTradeNo;
	}

	public void setChannelTradeNo(String channelTradeNo) {
		this.channelTradeNo = channelTradeNo;
	}

	public String getChannelTradeInst() {
		return channelTradeInst;
	}

	public void setChannelTradeInst(String channelTradeInst) {
		this.channelTradeInst = channelTradeInst;
	}

	public int getSearchChannelOrderTimes() {
		return searchChannelOrderTimes;
	}

	public void setSearchChannelOrderTimes(int searchChannelOrderTimes) {
		this.searchChannelOrderTimes = searchChannelOrderTimes;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCustomInfo() {
		return customInfo;
	}

	public void setCustomInfo(String customInfo) {
		this.customInfo = customInfo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * @return the exceptionInfo
	 */
	public String getExceptionInfo() {
		return exceptionInfo;
	}

	/**
	 * @param exceptionInfo
	 *            the exceptionInfo to set
	 */
	public void setExceptionInfo(String exceptionInfo) {
		this.exceptionInfo = exceptionInfo;
	}

	public void setExceptionInfoTrimedWhenExtendLength(String exceptionInfo) {
		if (StringUtils.isNotEmpty(exceptionInfo) && exceptionInfo.length() > 256) {
			this.exceptionInfo = exceptionInfo.substring(0, 256);
		} else {
			this.exceptionInfo = exceptionInfo;
		}
	}

	/**
	 * @param tableNamePostfix
	 *            the tableNamePostfix to set
	 */
	public void setTableNamePostfix(String tableNamePostfix) {
		this.tableNamePostfix = tableNamePostfix;
	}

	private static final char[] MONTH_CHAR = { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c' };

	public static String getCurrentTradeNo(long chargeLogId) {
		int month = Calendar.getInstance().get(Calendar.MONTH);
		return "" + MONTH_CHAR[month] + chargeLogId;
	}

	public static String getTradeNoPrefix(int tableNamePostfix) {
		return "" + MONTH_CHAR[tableNamePostfix - 1];
	}

	private String tableNamePostfix;

	public String getTableNamePostfix() {
		return Util.getRechargeTableNamePostfix(tradeNo);
	}

	public void setTableNamePostfix(int tableNamePostfix) {
	}
	
	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	public String getChannelPartnerId() {
		return channelPartnerId;
	}

	public void setChannelPartnerId(String channelPartnerId) {
		this.channelPartnerId = channelPartnerId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
