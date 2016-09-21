package com.vrg.payserver.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrg.payserver.dao.ChannelParamValueMapper;


@Service
public class ChannelParamRepository {
	@Autowired
	private ChannelParamValueMapper mapper;
	
	public String getParamValue(int planId, String channelId, String paramName) {
		return mapper.getChannelParamValue(planId, channelId, paramName);
	}
}
