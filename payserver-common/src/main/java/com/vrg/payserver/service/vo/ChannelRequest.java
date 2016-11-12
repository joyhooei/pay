/**
 * 
 */
package com.vrg.payserver.service.vo;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.vrg.payserver.util.ErrorCode;

/**
 *
 */
public class ChannelRequest {
	
	/*
	 * 支付结果 成功：SUCCESS，　失败：　FAIL
	 */
	private String payStatus = "FAIL";
	
	/*
	 * 支付时间
	 */
	private Long payTime;
	
	/*
	 * 随机字符串
	 */
	private String randomString;
	
	/*
	 * 当前系统的交易流水
	 */
	private String tradeNo;
	
	/*
	 * 渠道的交易号
	 */
	private String channelTradeNo; 
	
	/*
	 * 支付金额, 单位为分
	 */
	private int paidAmount;
	
	/*
	 * 折扣金额， 单位为分
	 */
	private int discountAmount;
	
	/*
	 * 设备id
	 */
	private String partnerId;
	
	/*
	 * 签名
	 */
	private String sign;
	
	// 成功
	public static final String SUCCESS = ErrorCode.SUCCESS;
	// -1 签名失败
	public static final String ERR_SIGN = ErrorCode.ERR_SIGN;
	// 2 重复订单
	public static final String ERR_REPEAT = ErrorCode.ERR_REPEAT;
	// -6 订单不存在
	public static final String ERR_ORDERID_NOTEXIST = ErrorCode.ERR_ORDERID_NOTEXIST;
	// -7 渠道编号和xgAppId不相同
	public static final String ERR_CHANNEL_ID_XG_APPID_NOTSAME = ErrorCode.ERR_CHANNEL_ID_XG_APPID_NOTSAME;
	// -98 系统被攻击
	public static final String ERR_EXCEPTION = ErrorCode.ERR_EXCEPTION;
	// -99 系统错误
	public static final String ERR_SYSTEM = ErrorCode.ERR_SYSTEM;
	// -200 前后渠道订单号
	public static final String ERR_CHANNEL_TRADE_NO_NOTSAME = ErrorCode.ERR_CHANNEL_TRADE_NO_NOTSAME;
	// -204 渠道分配的游戏编号不一致
	public static final String ERR_CHANNEL_APP_ID_NOTSAME = ErrorCode.ERR_CHANNEL_APP_ID_NOTSAME;
	// -205 渠道分配的用户编号不一致
	public static final String ERR_UID_NOTSAME = ErrorCode.ERR_UID_NOTSAME;
	// -206 订单支付时间，和xg订单中的创建时间相差不能超过一天
	public static final String ERR_PAID_TIME_OUT = ErrorCode.ERR_PAID_TIME_OUT;
	// -207 商品编号不一致
	public static final String ERR_PRODUCT_ID_NOTSAME = ErrorCode.ERR_PRODUCT_ID_NOTSAME;
	// -208 商品名称不一致
	public static final String ERR_PRODUCT_NAME_NOTSAME = ErrorCode.ERR_PRODUCT_NAME_NOTSAME;
	// -209 商品数量不一致
	public static final String ERR_PRODUCT_QUANTITY_NOTSAME = ErrorCode.ERR_PRODUCT_QUANTITY_NOTSAME;
	// -210 支付金额不一致
	public static final String ERR_PAID_AMOUNT_NOTSAME = ErrorCode.ERR_PAID_AMOUNT_NOTSAME;
	// -212 解析渠道通知参数失败
	public static final String ERR_PARSE_PAY_NOTICE = ErrorCode.ERR_PARSE_PAY_NOTICE;
	// -301 查询渠道订单超时
	public static final String ERR_SEARCH_CHANNEL_ORDER_TIMEOUT = ErrorCode.ERR_SEARCH_CHANNEL_ORDER_TIMEOUT;
	// -302 查询渠道订单失败
	public static final String ERR_SEARCH_CHANNEL_ORDER_FAIL = ErrorCode.ERR_SEARCH_CHANNEL_ORDER_FAIL;
	// 处理状态
	private String stateCode = ERR_SYSTEM;
	private String stateMsg = "success";
	// 渠道请求的原始解析信息
	private String channelId;
	private String serverVersion;
	private String requestIp;

	// 渠道请求中解析参数
	private ChannelData payNoticeRequestData;

	// 查询渠道订单时解析返回值
	private ChannelData searchChannelOrderResponseData = new ChannelData();


	// 支付状态是否成功，仅stateCode=STATE_SUCCESS时有效
	public static final String PAY_STATUS_SUCCESS = "1";
	public static final String PAY_STATUS_FAIL = "FAIL";

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public void putChannelData(ChannelData channelData) {
		this.stateCode = channelData.getStateCode();
		this.stateMsg = channelData.getStateMsg();
		
		if (!StringUtils.isEmpty(channelData.getTradeNo())) {
			this.tradeNo = channelData.getTradeNo();
		}

		if (!StringUtils.isEmpty(channelData.getChannelTradeNo())) {
			this.channelTradeNo = channelData.getChannelTradeNo();
		}

		if (!StringUtils.isEmpty(channelData.getPayStatus())) {
			this.payStatus = channelData.getPayStatus();
		}
	}

	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * @param stateCode
	 *            the stateCode to set
	 */
	public void setStateCode(String code) {
		this.stateCode = code;
	}

	/**
	 * @return the stateMsg
	 */
	public String getStateMsg() {
		return stateMsg;
	}

	/**
	 * @param stateMsg
	 *            the stateMsg to set
	 */
	public void setStateMsg(String msg) {
		this.stateMsg = msg;
	}

	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId
	 *            the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the serverVersion
	 */
	public String getServerVersion() {
		return serverVersion;
	}

	/**
	 * @param serverVersion
	 *            the serverVersion to set
	 */
	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	/**
	 * @return the requestIp
	 */
	public String getRequestIp() {
		return requestIp;
	}

	/**
	 * @param requestIp
	 *            the requestIp to set
	 */
	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	/**
	 * @return the payNoticeRequestData
	 */
	public ChannelData getPayNoticeRequestData() {
		return payNoticeRequestData;
	}

	/**
	 * @param payNoticeRequestData
	 *            the payNoticeRequestData to set
	 */
	public void setPayNoticeRequestData(ChannelData payNoticeRequestData) {
		this.payNoticeRequestData = payNoticeRequestData;
		if (payNoticeRequestData != null) {
			payNoticeRequestData.setPartnerId(partnerId);
			payNoticeRequestData.setChannelId(channelId);
		}
	}

	/**
	 * @return the searchChannelOrderResponseData
	 */
	public ChannelData getSearchChannelOrderResponseData() {
		return searchChannelOrderResponseData;
	}

	/**
	 * @param searchChannelOrderResponseData
	 *            the searchChannelOrderResponseData to set
	 */
	public void setSearchChannelOrderResponseData(ChannelData searchChannelOrderResponseData) {
		this.searchChannelOrderResponseData = searchChannelOrderResponseData;
		if (searchChannelOrderResponseData != null) {
			searchChannelOrderResponseData.setPartnerId(partnerId);
			searchChannelOrderResponseData.setChannelId(channelId);
		}
	}

	/**
	 * @return the tradeNo
	 */
	public String getTradeNo() {
		return tradeNo;
	}

	/**
	 * @param tradeNo
	 *            the tradeNo to set
	 */
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	/**
	 * @return the channelTradeNo
	 */
	public String getChannelTradeNo() {
		return channelTradeNo;
	}

	/**
	 * @param channelTradeNo
	 *            the channelTradeNo to set
	 */
	public void setChannelTradeNo(String channelTradeNo) {
		this.channelTradeNo = channelTradeNo;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public Long getPayTime() {
		return payTime;
	}

	public void setPayTime(Long payTime) {
		this.payTime = payTime;
	}

	public String getRandomString() {
		return randomString;
	}

	public void setRandomString(String randomString) {
		this.randomString = randomString;
	}

	public int getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(int paidAmount) {
		this.paidAmount = paidAmount;
	}

	public int getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(int discountAmount) {
		this.discountAmount = discountAmount;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
