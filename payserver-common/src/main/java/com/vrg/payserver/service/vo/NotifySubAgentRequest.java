/**
 *
 */
package com.vrg.payserver.service.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.vrg.payserver.util.DateUtil;
import com.vrg.payserver.util.SignCore;

public class NotifySubAgentRequest {
	// partnerId 是 String vrg分配的partnerId
	private String partnerId;
	// paidAmount 是 String 总支付金额(单位分)
	private String paidAmount;
	// customInfo 否 String 游戏方自定义字段，支付成功后回调的时候，透传原样返回
	private String customInfo;
	// ts 是 String 当前时间戳，秒级，如20150723150028对应2015/7/23 15:00:28
	private String ts;
	// sign 是 String 签名，签名算法参见签名章节，使用游戏服务端密钥
	private String sign;
	// tradeNo 是 String 支付网关分配的订单号
	private String tradeNo;
	// paidTime 是 String 支付时间 yyyyMMddHHmmss
	private String paidTime;

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
		data.setCustomInfo(rechargeRecord.getCustomInfo());
		data.setPaidAmount(String.valueOf(rechargeRecord.getPaidAmount()));
		data.setTradeNo(rechargeRecord.getTradeNo());
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
}
