package com.vrg.payserver.service.vo;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class RechargeRequestException {
	// ID NUMBER(20) 事件流水号 主键
	private long id;
	// EVENT_TYPE NUMBER(2) 事件类型，可配存DB or File
	// 1 create-order，默认File
	// 2 update-order，默认File
	// 3 cancel-order，默认File
	// 4 testchannel-notify，默认DB
	// 5 notify-game，默认DB
	// 6 verify-order，默认DB
	// 7 query-order-status，默认File
	// 8 channel-notify，默认DB
	// 9 verify-session，默认File
	// 10 get-channel-param，默认File
	private String eventType;
	// TRADE_NO VARCHAR2(64) XGSDK订单号
	private String tradeNo;
	// CHANNEL_TRADE_NO VARCHAR(64) 渠道订单号
	private String channelTradeNo;
	// XG_APP_ID VARCHAR2(64) XGSDK游戏编号
	private String partnerId;
	// CHANNEL_ID VARCHAR2(32) 运营商编号
	private String channelId;
	// DEVICE_ID VARCHAR2(100) 设备编号
	private String deviceId;
	// REQUEST_TIME DATE 请求发生时间
	private Date requestTime;
	// RESPONSE_TIME DATE 响应发生时间
	private Date responseTime;
	// REQUEST_IP VARCHAR2(128) 请求发起IP
	private String requestIp;
	// REQUEST_HEADER VARCHAR2(4000) 请求包头信息
	private String requestHeader;
	// REQUEST_VALUE1 VARCHAR2(4000) 接口请求参数1
	private String requestValue1;
	// REQUEST_VALUE2 VARCHAR2(4000) 接口请求参数2
	private String requestValue2;
	// RESPONSE_VALUE1 VARCHAR2(4000) 接口返回值1
	private String responseValue1;
	// RESPONSE_VALUE2 VARCHAR2(4000) 接口返回值2
	private String responseValue2;
	// REMARKS VARCHAR2(1000) 异常原因/备注
	private String remarks;

	public RechargeRequestException() {
	}

	public static RechargeRequestException getRechargeRequestExceptionFromClientRequest(String requestIp,
			Object request, Date requestTime) {
		String requestValue = JSON.toJSONString(request);
		JSONObject jsonObject = JSON.parseObject(requestValue);
		RechargeRequestException requestLog = new RechargeRequestException();
		parseFields(requestLog, jsonObject);
		if (requestTime != null) {
			requestLog.setRequestTime(requestTime);
		} else {
			requestLog.setRequestTime(new Date());
		}
		requestLog.setRequestIp(requestIp);
		//
		int index = Math.min(requestValue.length(), 4000);
		String requestValue1 = requestValue.substring(0, Math.min(requestValue.length(), index));
		String requestValue2 = requestValue.substring(index, Math.min(requestValue.length(), 8000));
		requestLog.setRequestValue1(requestValue1);
		requestLog.setRequestValue2(requestValue2);
		return requestLog;
	}

	private static void parseFields(RechargeRequestException requestLog, JSONObject jsonObject) {
		String channelId = jsonObject.getString("channelId");
		if (channelId != null) {
			requestLog.setChannelId(channelId);
		}
		String channelTradeNo = jsonObject.getString("channelTradeNo");
		if (channelTradeNo != null) {
			requestLog.setChannelTradeNo(channelTradeNo);
		}
		String deviceId = jsonObject.getString("deviceId");
		if (deviceId != null) {
			requestLog.setDeviceId(deviceId);
		}
		String eventType = jsonObject.getString("type");
		if (eventType != null) {
			requestLog.setEventType(eventType);
		}
		String partnerId = jsonObject.getString("partnerId");
		if (partnerId != null) {
			requestLog.setPartnerId(partnerId);
		}
		String tradeNo = jsonObject.getString("tradeNo");
		if (tradeNo != null) {
			requestLog.setTradeNo(tradeNo);
		}
	}

	public void putClientResponseData(Object response, Date responseTime) {
		if (responseTime != null) {
			this.setResponseTime(responseTime);
		} else {
			this.setResponseTime(new Date());
		}
		if (response == null) {
			return;
		}
		String responseValue = null;
		if (response instanceof String) {
			responseValue = response.toString();
		} else {
			responseValue = JSON.toJSONString(response);
		}
		JSONObject jsonObject = JSON.parseObject(responseValue);
		Object originalData = jsonObject.get("data");
		if (originalData != null) {
			if (originalData instanceof JSONObject) {
				JSONObject data = jsonObject.getJSONObject("data");
				parseFields(this, data);
			}
		}
		int index = Math.min(responseValue.length(), 4000);
		String responseValue1 = responseValue.substring(0, Math.min(responseValue.length(), index));
		String responseValue2 = responseValue.substring(index, Math.min(responseValue.length(), 8000));
		this.setResponseValue1(responseValue1);
		this.setResponseValue2(responseValue2);
		this.setResponseTime(new Date());
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
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
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
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
	 * @return the requestHeader
	 */
	public String getRequestHeader() {
		return requestHeader;
	}

	/**
	 * @param requestHeader
	 *            the requestHeader to set
	 */
	public void setRequestHeader(String requestHeader) {
		this.requestHeader = requestHeader;
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
	 * @return the requestTime
	 */
	public Date getRequestTime() {
		return requestTime;
	}

	/**
	 * @param requestTime
	 *            the requestTime to set
	 */
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	/**
	 * @return the responseTime
	 */
	public Date getResponseTime() {
		return responseTime;
	}

	/**
	 * @param responseTime
	 *            the responseTime to set
	 */
	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}

	/**
	 * @return the requestValue1
	 */
	public String getRequestValue1() {
		return requestValue1;
	}

	/**
	 * @param requestValue1
	 *            the requestValue1 to set
	 */
	public void setRequestValue1(String requestValue1) {
		this.requestValue1 = requestValue1;
	}

	/**
	 * @return the requestValue2
	 */
	public String getRequestValue2() {
		return requestValue2;
	}

	/**
	 * @param requestValue2
	 *            the requestValue2 to set
	 */
	public void setRequestValue2(String requestValue2) {
		this.requestValue2 = requestValue2;
	}

	/**
	 * @return the responseValue1
	 */
	public String getResponseValue1() {
		return responseValue1;
	}

	/**
	 * @param responseValue1
	 *            the responseValue1 to set
	 */
	public void setResponseValue1(String responseValue1) {
		this.responseValue1 = responseValue1;
	}

	/**
	 * @return the responseValue2
	 */
	public String getResponseValue2() {
		return responseValue2;
	}

	/**
	 * @param responseValue2
	 *            the responseValue2 to set
	 */
	public void setResponseValue2(String responseValue2) {
		this.responseValue2 = responseValue2;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setRequestValue(String requestValue) {
		if (requestValue == null) {
			return;
		}
		int index = Math.min(requestValue.length(), 4000);
		String requestValue1 = requestValue.substring(0, Math.min(requestValue.length(), index));
		String requestValue2 = requestValue.substring(index, Math.min(requestValue.length(), 8000));
		this.setRequestValue1(requestValue1);
		this.setRequestValue2(requestValue2);
	}
	
	public void setResponseValue(String responseValue) {
		if (responseValue == null) {
			return;
		}
		int index = Math.min(responseValue.length(), 4000);
		String responseValue1 = responseValue.substring(0, Math.min(responseValue.length(), index));
		String responseValue2 = responseValue.substring(index, Math.min(responseValue.length(), 8000));
		this.setResponseValue1(responseValue1);
		this.setResponseValue2(responseValue2);
	}
	
}
