/**
 * 
 */
package com.vrg.payserver.vo;

/**
 * @author LUCHUNLIANG
 *
 */
public class RechargeRequestObject {
	private String requestType;
	private Object request;
	private Object response;
	private Object bizObject;

	/**
	 * @return the requestType
	 */
	public String getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType
	 *            the requestType to set
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return the request
	 */
	public Object getRequest() {
		return request;
	}

	/**
	 * @param request
	 *            the request to set
	 */
	public void setRequest(Object request) {
		this.request = request;
	}

	/**
	 * @return the response
	 */
	public Object getResponse() {
		return response;
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(Object response) {
		this.response = response;
	}

	/**
	 * @return the bizObject
	 */
	public Object getBizObject() {
		return bizObject;
	}

	/**
	 * @param bizObject
	 *            the bizObject to set
	 */
	public void setBizObject(Object bizObject) {
		this.bizObject = bizObject;
	}

}
