/**
 *
 */
package com.vrg.payserver.service.vo;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.vrg.payserver.util.ErrorCode;

/**
 *
 */
public class ChannelNotifyRequest {
	//成功
	public static final String SUCCESS = ErrorCode.SUCCESS;
	// -1 签名失败
	public static final String ERR_SIGN = ErrorCode.ERR_SIGN;
	// 2 重复订单
	public static final String ERR_REPEAT = ErrorCode.ERR_REPEAT;
	// -6 订单不存在
	public static final String ERR_ORDERID_NOTEXIST = ErrorCode.ERR_ORDERID_NOTEXIST;
	// -7 渠道编号和xgAppId不相同
	public static final String ERR_CHANNEL_ID_XG_APPID_NOTSAME = ErrorCode.ERR_CHANNEL_ID_XG_APPID_NOTSAME;
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
	// -215 通知来自黑名单IP
	public static final String ERR_BLACKLIST_IP = ErrorCode.ERR_BLACKLIST_IP;
	// -301 查询渠道订单超时
	public static final String ERR_SEARCH_CHANNEL_ORDER_TIMEOUT = ErrorCode.ERR_SEARCH_CHANNEL_ORDER_TIMEOUT;
	// -302 查询渠道订单失败
	public static final String ERR_SEARCH_CHANNEL_ORDER_FAIL = ErrorCode.ERR_SEARCH_CHANNEL_ORDER_FAIL;
	// 处理状态
//	private String stateCode = SUCCESS;
	private String stateMsg = "success";
	// 渠道请求的原始解析信息
	private String xgAppId;
	private String channelId;
	private String requestIp;
	private Object channelRequest;
	private Object searchChannelOrderResponse;
//	// 返回渠道的渠道错误码和错误信息
//	private String channelCode;
//	private String channelMsg;
	// 渠道请求中解析参数
	private String tradeNo; // 部分渠道通过XG订单号回调
	private String channelTradeNo; // 部分渠道通过渠道订单号回调，通常会要求在创建订单时要求先创建渠道订单号
	private String channelAppId; // 渠道分配的游戏编号，为空则不做校验
	private String uid; // 渠道分配的用户编号，为空则不做校验
	private Date paidTime; // 订单支付时间，和xg订单中的创建时间相差不能超过一天
	private String productId; // 商品编号，为空则不做校验
	private String productName; // 商品名称，为空则不做校验
	private int productQuantity = -1; // 商品数量，小于等于0则不做校验
	private int paidAmount = -1;
	// 查询渠道订单时解析返回值
	private String responseTradeNo; // 部分渠道通过XG订单号回调
	private String responseChannelTradeNo; // 部分渠道通过渠道订单号回调，通常会要求在创建订单时要求先创建渠道订单号
	private String responseChannelAppId; // 渠道分配的游戏编号，为空则不做校验
	private String responseUid; // 渠道分配的用户编号，为空则不做校验
	private Date responsePaidTime; // 订单支付时间，和xg订单中的创建时间相差不能超过一天
	private String responseProductId; // 商品编号，为空则不做校验
	private String responseProductName; // 商品名称，为空则不做校验
	private int responseProductQuantity = -1; // 商品数量，小于等于0则不做校验
	private int responsePaidAmount = -1;
	// XG订单所得数据
	private String planId;
	private String deviceId;

	// 回填
	// 支付状态是否成功，仅stateCode=STATE_SUCCESS时有效
//	public static final String PAY_STATUS_SUCCESS = NotifyGameRequest.PAY_STATUS_SUCCESS;
//	public static final String PAY_STATUS_FAIL = NotifyGameRequest.PAY_STATUS_FAIL;
//	private String payStatus = PAY_STATUS_SUCCESS;
	private String chargeChannelId;
	private String chargeChannelType;
	private String chargeChannelInst;
	private int voucherAmount = 0;

	@Override
	public String toString() {
		return JSON.toJSONString(this);
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

//	/**
//	 * @return the stateCode
//	 */
//	public String getStateCode() {
//		return stateCode;
//	}
//
//	/**
//	 * @param stateCode
//	 *            the stateCode to set
//	 */
//	public void setStateCode(String stateCode) {
//		this.stateCode = stateCode;
//	}

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
	public void setStateMsg(String stateMsg) {
		this.stateMsg = stateMsg;
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
	 * @return the channelRequest
	 */
	public Object getChannelRequest() {
		return channelRequest;
	}

	/**
	 * @param channelRequest
	 *            the channelRequest to set
	 */
	public void setChannelRequest(Object channelRequest) {
		this.channelRequest = channelRequest;
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
	 * @return the paidTime
	 */
	public Date getPaidTime() {
		return paidTime;
	}

	/**
	 * @param paidTime
	 *            the paidTime to set
	 */
	public void setPaidTime(Date paidTime) {
		this.paidTime = paidTime;
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
	 * @return the responseTradeNo
	 */
	public String getResponseTradeNo() {
		return responseTradeNo;
	}

	/**
	 * @param responseTradeNo
	 *            the responseTradeNo to set
	 */
	public void setResponseTradeNo(String responseTradeNo) {
		this.responseTradeNo = responseTradeNo;
	}

	/**
	 * @return the responseChannelTradeNo
	 */
	public String getResponseChannelTradeNo() {
		return responseChannelTradeNo;
	}

	/**
	 * @param responseChannelTradeNo
	 *            the responseChannelTradeNo to set
	 */
	public void setResponseChannelTradeNo(String responseChannelTradeNo) {
		this.responseChannelTradeNo = responseChannelTradeNo;
	}

	/**
	 * @return the responseChannelAppId
	 */
	public String getResponseChannelAppId() {
		return responseChannelAppId;
	}

	/**
	 * @param responseChannelAppId
	 *            the responseChannelAppId to set
	 */
	public void setResponseChannelAppId(String responseChannelAppId) {
		this.responseChannelAppId = responseChannelAppId;
	}

	/**
	 * @return the responseUid
	 */
	public String getResponseUid() {
		return responseUid;
	}

	/**
	 * @param responseUid
	 *            the responseUid to set
	 */
	public void setResponseUid(String responseUid) {
		this.responseUid = responseUid;
	}

	/**
	 * @return the responsePaidTime
	 */
	public Date getResponsePaidTime() {
		return responsePaidTime;
	}

	/**
	 * @param responsePaidTime
	 *            the responsePaidTime to set
	 */
	public void setResponsePaidTime(Date responsePaidTime) {
		this.responsePaidTime = responsePaidTime;
	}

	/**
	 * @return the responseProductId
	 */
	public String getResponseProductId() {
		return responseProductId;
	}

	/**
	 * @param responseProductId
	 *            the responseProductId to set
	 */
	public void setResponseProductId(String responseProductId) {
		this.responseProductId = responseProductId;
	}

	/**
	 * @return the responseProductName
	 */
	public String getResponseProductName() {
		return responseProductName;
	}

	/**
	 * @param responseProductName
	 *            the responseProductName to set
	 */
	public void setResponseProductName(String responseProductName) {
		this.responseProductName = responseProductName;
	}

	/**
	 * @return the responseProductQuantity
	 */
	public int getResponseProductQuantity() {
		return responseProductQuantity;
	}

	/**
	 * @param responseProductQuantity
	 *            the responseProductQuantity to set
	 */
	public void setResponseProductQuantity(int responseProductQuantity) {
		this.responseProductQuantity = responseProductQuantity;
	}

	/**
	 * @return the responsePaidAmount
	 */
	public int getResponsePaidAmount() {
		return responsePaidAmount;
	}

	/**
	 * @param responsePaidAmount
	 *            the responsePaidAmount to set
	 */
	public void setResponsePaidAmount(int responsePaidAmount) {
		this.responsePaidAmount = responsePaidAmount;
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

//	/**
//	 * @return the payStatus
//	 */
//	public String getPayStatus() {
//		return payStatus;
//	}
//
//	/**
//	 * @param payStatus
//	 *            the payStatus to set
//	 */
//	public void setPayStatus(String payStatus) {
//		this.payStatus = payStatus;
//	}

	/**
	 * @return the chargeChannelId
	 */
	public String getChargeChannelId() {
		return chargeChannelId;
	}

	/**
	 * @param chargeChannelId
	 *            the chargeChannelId to set
	 */
	public void setChargeChannelId(String chargeChannelId) {
		this.chargeChannelId = chargeChannelId;
	}

	/**
	 * @return the chargeChannelType
	 */
	public String getChargeChannelType() {
		return chargeChannelType;
	}

	/**
	 * @param chargeChannelType
	 *            the chargeChannelType to set
	 */
	public void setChargeChannelType(String chargeChannelType) {
		this.chargeChannelType = chargeChannelType;
	}

	/**
	 * @return the chargeChannelInst
	 */
	public String getChargeChannelInst() {
		return chargeChannelInst;
	}

	/**
	 * @param chargeChannelInst
	 *            the chargeChannelInst to set
	 */
	public void setChargeChannelInst(String chargeChannelInst) {
		this.chargeChannelInst = chargeChannelInst;
	}

	/**
	 * @return the voucherAmount
	 */
	public int getVoucherAmount() {
		return voucherAmount;
	}

	/**
	 * @param voucherAmount
	 *            the voucherAmount to set
	 */
	public void setVoucherAmount(int voucherAmount) {
		this.voucherAmount = voucherAmount;
	}

	/**
	 * @return the searchChannelOrderResponse
	 */
	public Object getSearchChannelOrderResponse() {
		return searchChannelOrderResponse;
	}

	/**
	 * @param searchChannelOrderResponse
	 *            the searchChannelOrderResponse to set
	 */
	public void setSearchChannelOrderResponse(Object searchChannelOrderResponse) {
		this.searchChannelOrderResponse = searchChannelOrderResponse;
	}

}
