/**
 * 
 */
package com.vrg.payserver.service.vo;

import com.alibaba.fastjson.JSON;

/**
 * @author LUCHUNLIANG
 *
 */
public class ClientNewRechargeResponse {
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
	private ClientNewRechargeResponseData data;

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
	public ClientNewRechargeResponseData getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(ClientNewRechargeResponseData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
