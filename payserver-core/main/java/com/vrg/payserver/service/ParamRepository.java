/**
 *
 */
package com.vrg.payserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.vrg.payserver.repository.ChannelParamRepository;
import com.vrg.payserver.repository.PartnerRespository;

@Service
public class ParamRepository {

	@Autowired
	private ChannelParamRepository channelParamRepository;
	
	@Autowired
	private PartnerRespository partnerRespository;

	/**
	 * 获取支付渠道参数
	 * @param channelId
	 * @return
	 */
	public JSONObject getChannelParams(String channelId) {
//		return channelParamRepository.getChannelParams(channelId);
		return null;
	}

	/**
	 * 获取游戏客户端密钥
	 * @param xgAppId
	 * @return
	 */
	public String getClientAppKey(String planId) {
		return "";
	}

	/**
	 * 获取游戏服务端密钥
	 * @param xgAppId
	 * @return
	 */
	public String getServerSecret(String partnerId) {
		return partnerRespository.getSecretKey(partnerId);
	}
}