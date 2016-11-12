/**
 *
 */
package com.vrg.payserver.service.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.vrg.payserver.util.DateUtil;
import com.vrg.payserver.util.SignCore;

/**
 * @author LUCHUNLIANG
 *
 */
public class NotifySubAgentRequest {
	// partnerId 是 String vrg分配的partnerId
	private String partnerId;
	// channelId 是 String 运营渠道编号
	private String channelId;
	// uid 是 String 渠道的用户编号
	private String uid;
	// zoneId 否 String 游戏区编号
	private String zoneId;
	// serverId 否 String 游戏服编号
	private String serverId;
	// roleId 是 String 角色编号
	private String roleId;
	// roleName 否 String 角色名称
	private String roleName;
	// roleLevel 否 String 角色等级
	private String roleLevel;
	// roleVipLevel 否 String 角色VIP等级
	private String roleVipLevel;
	// currencyName 否 String 支付货币名称
	private String currencyName;
	// productId 是 String 商品编号
	private String productId;
	// productName 否 String 商品名称
	private String productName;
	// productDesc 否 String 商品描述
	private String productDesc;
	// productQuantity 否 String 商品数量
	private String productQuantity;
	// productUnitPrice 否 String 商品单价(单位分)
	private String productUnitPrice;
	// totalAmount 是 String 总面额(单位分)
	private String totalAmount;
	// paidAmount 是 String 总支付金额(单位分)
	private String paidAmount;
	// customInfo 否 String 游戏方自定义字段，支付成功后回调的时候，透传原样返回
	private String customInfo;
	// ts 是 String 当前时间戳，秒级，如20150723150028对应2015/7/23 15:00:28
	private String ts;
	// gameTradeNo 否 String 游戏侧订单号[]
	private String gameTradeNo;
	// sign 是 String 签名，签名算法参见签名章节，使用游戏服务端密钥
	private String sign;
	// tradeNo 是 String Xgsdk分配的订单号
	private String tradeNo;
	// paidTime 是 String 支付时间 yyyyMMddHHmmss
	private String paidTime;
	// InAppPay,WalletPay, CardPay, PlatformPay。通知游戏的时候，值为  InAppPay,WalletPay 才通知游戏
	@JsonInclude(Include.NON_NULL)
	private String payType;
	//渠道订单号
	@JsonInclude(Include.NON_NULL)
	private String channelTradeNo;

	// payStatus 是 String 订单支付状态
	// 1 支付成功
	// 2 支付失败
	public static final String PAY_STATUS_SUCCESS = "1";
	public static final String PAY_STATUS_FAIL = "2";
	private String payStatus = PAY_STATUS_SUCCESS;

	private String ext;

	@JSONField(serialize = false)
	private String originalTradeNo;
	@JSONField(serialize = false)
	private String originalChannelTradeNo;
	@JSONField(serialize = false)
	private Date originalPurchaseDate;
	@JSONField(serialize = false)
	private Date expiresDate;//订阅过期时间
	@JSONField(serialize = false)
	private Date cancellationDate;//订阅取消时间，取消后等于没有订阅过

	public void buildRechargeOrderExt() {
		if ( this.getExpiresDate() != null || this.getCancellationDate() != null) {
			JSONObject extObj = new JSONObject();
			extObj.put("expiresDate", DateUtil.format(this.expiresDate));
			extObj.put("cancellationDate", DateUtil.format(this.cancellationDate));
			extObj.put("originalTradeNo", this.getOriginalTradeNo());
			this.ext = extObj.toJSONString();
		}
	}

	public String getOriginalTradeNo() {
		return originalTradeNo;
	}

	public void setOriginalTradeNo(String originalTradeNo) {
		this.originalTradeNo = originalTradeNo;
	}

	public String getOriginalChannelTradeNo() {
		return originalChannelTradeNo;
	}

	public void setOriginalChannelTradeNo(String originalChannelTradeNo) {
		this.originalChannelTradeNo = originalChannelTradeNo;
	}

	public Date getOriginalPurchaseDate() {
		return originalPurchaseDate;
	}

	public void setOriginalPurchaseDate(Date originalPurchaseDate) {
		this.originalPurchaseDate = originalPurchaseDate;
	}

	public Date getExpiresDate() {
		return expiresDate;
	}

	public void setExpiresDate(Date expiresDate) {
		this.expiresDate = expiresDate;
	}

	public Date getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(Date cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public static NotifySubAgentRequest create(RechargeRecordBase rechargeRecord) {
		NotifySubAgentRequest data = new NotifySubAgentRequest();
		String payStatus = PAY_STATUS_FAIL;
		if (RechargeRecordBase.STATUS_NOTIFY_SUCCESS == rechargeRecord.getStatus()) {
			payStatus = PAY_STATUS_SUCCESS;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		data.setChannelId(rechargeRecord.getChannelId());
		data.setCustomInfo(rechargeRecord.getCustomInfo());
		data.setGameTradeNo(rechargeRecord.getSubAgentTradeNo());
		data.setPaidAmount(String.valueOf(rechargeRecord.getPaidAmount()));
		data.setTradeNo(rechargeRecord.getTradeNo());
		data.setUid(rechargeRecord.getUid());
		data.setPartnerId(rechargeRecord.getPartnerId());
		data.setTs(SignCore.getTs());
		data.setPaidTime(format.format(rechargeRecord.getPaidTime()));
		data.setPayStatus(payStatus);
		return data;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
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
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the roleLevel
	 */
	public String getRoleLevel() {
		return roleLevel;
	}

	/**
	 * @param roleLevel
	 *            the roleLevel to set
	 */
	public void setRoleLevel(String roleLevel) {
		this.roleLevel = roleLevel;
	}

	/**
	 * @return the roleVipLevel
	 */
	public String getRoleVipLevel() {
		return roleVipLevel;
	}

	/**
	 * @param roleVipLevel
	 *            the roleVipLevel to set
	 */
	public void setRoleVipLevel(String roleVipLevel) {
		this.roleVipLevel = roleVipLevel;
	}

	/**
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}

	/**
	 * @param currencyName
	 *            the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
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

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName
	 *            the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	/**
	 * @return the productDesc
	 */
	public String getProductDesc() {
		return productDesc;
	}

	/**
	 * @param productDesc
	 *            the productDesc to set
	 */
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	/**
	 * @return the productQuantity
	 */
	public String getProductQuantity() {
		return productQuantity;
	}

	/**
	 * @param productQuantity
	 *            the productQuantity to set
	 */
	public void setProductQuantity(String productQuantity) {
		this.productQuantity = productQuantity;
	}

	/**
	 * @return the productUnitPrice
	 */
	public String getProductUnitPrice() {
		return productUnitPrice;
	}

	/**
	 * @param productUnitPrice
	 *            the productUnitPrice to set
	 */
	public void setProductUnitPrice(String productUnitPrice) {
		this.productUnitPrice = productUnitPrice;
	}

	/**
	 * @return the totalAmount
	 */
	public String getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount
	 *            the totalAmount to set
	 */
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the paidAmount
	 */
	public String getPaidAmount() {
		return paidAmount;
	}

	/**
	 * @param paidAmount
	 *            the paidAmount to set
	 */
	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
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

	/**
	 * @return the ts
	 */
	public String getTs() {
		return ts;
	}

	/**
	 * @param ts
	 *            the ts to set
	 */
	public void setTs(String ts) {
		this.ts = ts;
	}

	/**
	 * @return the gameTradeNo
	 */
	public String getGameTradeNo() {
		return gameTradeNo;
	}

	/**
	 * @param gameTradeNo
	 *            the gameTradeNo to set
	 */
	public void setGameTradeNo(String gameTradeNo) {
		this.gameTradeNo = gameTradeNo;
	}

	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * @param sign
	 *            the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
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
	 * @return the paidTime
	 */
	public String getPaidTime() {
		return paidTime;
	}

	/**
	 * @param paidTime
	 *            the paidTime to set
	 */
	public void setPaidTime(String paidTime) {
		this.paidTime = paidTime;
	}

	/**
	 * @return the payStatus
	 */
	public String getPayStatus() {
		return payStatus;
	}

	/**
	 * @param payStatus
	 *            the payStatus to set
	 */
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getChannelTradeNo() {
		return channelTradeNo;
	}

	public void setChannelTradeNo(String channelTradeNo) {
		this.channelTradeNo = channelTradeNo;
	}

}
