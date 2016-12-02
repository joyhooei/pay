/**
 * 
 */
package com.vrg.payserver.service.vo;

import com.alibaba.fastjson.JSON;

public class CreateChannelOrderResponse {
	/**
	 * 接口调用结果代码
	 */
	private String code;
	/**
	 * 接口调用信息提示
	 */
	private String msg;
	/**
	 * 接口返回数据
	 */
	private CreateChannelOrderResponseData data;
	private String requestValue;
	private String responseValue;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
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
	 *            the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the data
	 */
	public CreateChannelOrderResponseData getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(CreateChannelOrderResponseData data) {
		this.data = data;
	}

	/**
	 * @return the requestValue
	 */
	public String getRequestValue() {
		return requestValue;
	}

	/**
	 * @param requestValue
	 *            the requestValue to set
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
	 *            the responseValue to set
	 */
	public void setResponseValue(String responseValue) {
		this.responseValue = responseValue;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
