/**
 *
 */
package com.vrg.payserver.service;

/**
 *
 */
public enum SearchChannelOrderType {
	// 渠道不支持主动查询订单接口
	NOTSUPPORT,
	// 查询超时，查询订单不存在，查询响应失败，查询响应无法解析，查询响应验签不通过
	NOK,
	// 成功得到渠道返回，并解析成功
	OK,
	// 请求的token或者参数无效时
	BAD_REQUEST;
}
