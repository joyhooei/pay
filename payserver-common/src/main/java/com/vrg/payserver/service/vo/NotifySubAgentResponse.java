/**
 * 
 */
package com.vrg.payserver.service.vo;

import com.alibaba.fastjson.JSON;

/**
 * @author LUCHUNLIANG
 *
 */
public class NotifySubAgentResponse {			
	private String code;
	private String msg;

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

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
}
