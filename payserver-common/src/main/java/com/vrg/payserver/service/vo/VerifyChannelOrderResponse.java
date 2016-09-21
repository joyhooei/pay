package com.vrg.payserver.service.vo;

import java.util.Date;

import com.alibaba.fastjson.JSON;

public class VerifyChannelOrderResponse {
  public static final String PAY_STATUS_SUCCESS = "1";
  public static final String PAY_STATUS_FAIL = "0";
  private String channelTradeNo = "";
  private String payStatus = PAY_STATUS_SUCCESS;
  private String chargeChannelId;
  private String chargeChannelType;
  private String chargeChannelInst;
  private String uid;
  private String serverId = "";
  private String roleId;
  private String roleName;
  private String productId = "";
  private int productQuantity = 0;
  private int paidAmount = 0;
  private Date paidTime;
  private int voucherAmount = 0;
  private int channelBonusAmount = 0;

  private String productName;
  private String productDesc;
  private int totalAmount = 0;
  private String customInfo;
  // GAME_TRADE_NO VARCHAR2(64) 游戏订单号
  private String gameTradeNo;
  // GAME_CALLBACK_URL VARCHAR2(128) 游戏回调地址
  private String gameCallbackUrl;

  private String code = "1";
  private String msg = "success";
  private String requestValue;
  private String responseValue;
  private String currencyName;

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }

  /**
   * @return the channelTradeNo
   */
  public String getChannelTradeNo() {
    return channelTradeNo;
  }

  /**
   * @param channelTradeNo
   *          the channelTradeNo to set
   */
  public void setChannelTradeNo(String channelTradeNo) {
    this.channelTradeNo = channelTradeNo;
  }

  /**
   * @return the payStatus
   */
  public String getPayStatus() {
    return payStatus;
  }

  /**
   * @param payStatus
   *          the payStatus to set
   */
  public void setPayStatus(String payStatus) {
    this.payStatus = payStatus;
  }

  /**
   * @return the chargeChannelId
   */
  public String getChargeChannelId() {
    return chargeChannelId;
  }

  /**
   * @param chargeChannelId
   *          the chargeChannelId to set
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
   *          the chargeChannelType to set
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
   *          the chargeChannelInst to set
   */
  public void setChargeChannelInst(String chargeChannelInst) {
    this.chargeChannelInst = chargeChannelInst;
  }

  /**
   * @return the uid
   */
  public String getUid() {
    return uid;
  }

  /**
   * @param uid
   *          the uid to set
   */
  public void setUid(String uid) {
    this.uid = uid;
  }

  /**
   * @return the serverId
   */
  public String getServerId() {
    return serverId;
  }

  /**
   * @param serverId
   *          the serverId to set
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
   *          the roleId to set
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
   *          the roleName to set
   */
  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  /**
   * @return the productId
   */
  public String getProductId() {
    return productId;
  }

  /**
   * @param productId
   *          the productId to set
   */
  public void setProductId(String productId) {
    this.productId = productId;
  }

  /**
   * @return the productQuantity
   */
  public int getProductQuantity() {
    return productQuantity;
  }

  /**
   * @param productQuantity
   *          the productQuantity to set
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
   *          the paidAmount to set
   */
  public void setPaidAmount(int paidAmount) {
    this.paidAmount = paidAmount;
  }

  /**
   * @return the paidTime
   */
  public Date getPaidTime() {
    return paidTime;
  }

  /**
   * @param paidTime
   *          the paidTime to set
   */
  public void setPaidTime(Date paidTime) {
    this.paidTime = paidTime;
  }

  /**
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * @param code
   *          the code to set
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * @return the msg
   */
  public String getMsg() {
    return msg;
  }

  /**
   * @param msg
   *          the msg to set
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * @return the voucherAmount
   */
  public int getVoucherAmount() {
    return voucherAmount;
  }

  /**
   * @param voucherAmount
   *          the voucherAmount to set
   */
  public void setVoucherAmount(int voucherAmount) {
    this.voucherAmount = voucherAmount;
  }

  /**
   * @return the productName
   */
  public String getProductName() {
    return productName;
  }

  /**
   * @param productName
   *          the productName to set
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
   *          the productDesc to set
   */
  public void setProductDesc(String productDesc) {
    this.productDesc = productDesc;
  }

  /**
   * @return the totalAmount
   */
  public int getTotalAmount() {
    return totalAmount;
  }

  /**
   * @param totalAmount
   *          the totalAmount to set
   */
  public void setTotalAmount(int totalAmount) {
    this.totalAmount = totalAmount;
  }

  /**
   * @return the customInfo
   */
  public String getCustomInfo() {
    return customInfo;
  }

  /**
   * @param customInfo
   *          the customInfo to set
   */
  public void setCustomInfo(String customInfo) {
    this.customInfo = customInfo;
  }

  /**
   * @return the channelBonusAmount
   */
  public int getChannelBonusAmount() {
    return channelBonusAmount;
  }

  /**
   * @param channelBonusAmount
   *          the channelBonusAmount to set
   */
  public void setChannelBonusAmount(int channelBonusAmount) {
    this.channelBonusAmount = channelBonusAmount;
  }

  /**
   * @return the requestValue
   */
  public String getRequestValue() {
    return requestValue;
  }

  /**
   * @param requestValue
   *          the requestValue to set
   */
  public void setRequestValue(String requestValue) {
    this.requestValue = requestValue;
  }

  /**
   * @return the responseValue
   */
  public String getResponseValue() {
    return responseValue;
  }

  /**
   * @param responseValue
   *          the responseValue to set
   */
  public void setResponseValue(String responseValue) {
    this.responseValue = responseValue;
  }

  /**
   * @return the gameTradeNo
   */
  public String getGameTradeNo() {
    return gameTradeNo;
  }

  /**
   * @param gameTradeNo
   *          the gameTradeNo to set
   */
  public void setGameTradeNo(String gameTradeNo) {
    this.gameTradeNo = gameTradeNo;
  }

  /**
   * @return the gameCallbackUrl
   */
  public String getGameCallbackUrl() {
    return gameCallbackUrl;
  }

  /**
   * @param gameCallbackUrl
   *          the gameCallbackUrl to set
   */
  public void setGameCallbackUrl(String gameCallbackUrl) {
    this.gameCallbackUrl = gameCallbackUrl;
  }

  public String getCurrencyName() {
    return currencyName;
  }

  public void setCurrencyName(String currencyName) {
    this.currencyName = currencyName;
  }

}
