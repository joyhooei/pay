/**
 *
 */
package com.vrg.payserver.service.vo;

import java.util.Date;

import com.alibaba.fastjson.JSON;

/**
 *
 */
public class VerifyChannelOrderRequest {
	// TRADE_NO VARCHAR2(64) XGSDK订单号 主键
	private String tradeNo;
	// CHANNEL_APP_ID VARCHAR2(64) 运营渠道分配的游戏ID
	private String channelAppId;
	// UID VARCHAR2(128) 渠道的用户编号
	private String uid;
	// ZONE_ID VARCHAR2(32) 玩家游戏区ID
	private String zoneId;
	// SERVER_ID VARCHAR2(32) 玩家游戏服ID
	private String serverId;
	// ROLE_ID VARCHAR2(32) 游戏角色编号
	private String roleId;
	// CHANNEL_ID VARCHAR2(32) 运营渠道编号
	private String channelId;
	// PRODUCT_ID VARCHAR2(64) 商品编号
	private String productId;
	// PRODUCT_NAME VARCHAR2(64) 商品名称
	private String productName;
	// PRODUCT_QUANTITY NUMBER(10) 商品数量
	private int productQuantity;
	// PAID_AMOUNT NUMBER(10) 总支付金额(单位分)
	private int paidAmount;
	// CHANNEL_TRADE_NO VARCHAR(64) 渠道订单号
	private String channelTradeNo;
	// XG_APP_ID VARCHAR2(64) XGSDK分配的游戏编号
	private String xgAppId;
	// PLAN_ID VARCHAR(20) 发布计划编号 必输
	private String planId;
	// BUILD_NUMBER VARCHAR2(64) XGSDK发布小版本号
	private String buildNumber;
	// DEVICE_ID VARCHAR2(100) 设备编号
	private String deviceId;
	// CUSTOM_INFO VARCHAR2(2000) 游戏方自定义字段
	private String customInfo;
	// CREATE_TIME DATE 订单创建时间
	private Date createTime;
	// 客户端上报的支付凭证请求内容，只用于客户端上报支付凭证的渠道（如：应用宝，Google，Apple等）
	private String payNotifyRequest;

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
	 * @return the channelAppId
	 */
	public String getChannelAppId() {
		return channelAppId;
	}

	/**
	 * @param channelAppId
	 *            the channelAppId to set
	 */
	public void setChannelAppId(String channelAppId) {
		this.channelAppId = channelAppId;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the zoneId
	 */
	public String getZoneId() {
		return zoneId;
	}

	/**
	 * @param zoneId
	 *            the zoneId to set
	 */
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	/**
	 * @return the serverId
	 */
	public String getServerId() {
		return serverId;
	}

	/**
	 * @param serverId
	 *            the serverId to set
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the roleId
	 */
	public String getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId
	 *            the roleId to set
	 */
	public void setRoleId(String roleId) {
		this.roleId = roleId;
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
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the productQuantity
	 */
	public int getProductQuantity() {
		return productQuantity;
	}

	/**
	 * @param productQuantity
	 *            the productQuantity to set
	 */
	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

	/**
	 * @return the paidAmount
	 */
	public int getPaidAmount() {
		return paidAmount;
	}

	/**
	 * @param paidAmount
	 *            the paidAmount to set
	 */
	public void setPaidAmount(int paidAmount) {
		this.paidAmount = paidAmount;
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

	/**
	 * @return the xgAppId
	 */
	public String getXgAppId() {
		return xgAppId;
	}

	/**
	 * @param xgAppId
	 *            the xgAppId to set
	 */
	public void setXgAppId(String xgAppId) {
		this.xgAppId = xgAppId;
	}

	/**
	 * @return the planId
	 */
	public String getPlanId() {
		return planId;
	}

	/**
	 * @param planId
	 *            the planId to set
	 */
	public void setPlanId(String planId) {
		this.planId = planId;
	}

	/**
	 * @return the buildNumber
	 */
	public String getBuildNumber() {
		return buildNumber;
	}

	/**
	 * @param buildNumber
	 *            the buildNumber to set
	 */
	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the customInfo
	 */
	public String getCustomInfo() {
		return customInfo;
	}

	/**
	 * @param customInfo
	 *            the customInfo to set
	 */
	public void setCustomInfo(String customInfo) {
		this.customInfo = customInfo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPayNotifyRequest() {
		return payNotifyRequest;
	}

	public void setPayNotifyRequest(String payNotifyRequest) {
		this.payNotifyRequest = payNotifyRequest;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
