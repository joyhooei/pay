/**
 * 
 */
package com.xgsdk.sdkserver.impl;

import java.util.HashMap;
import java.util.Map;

import com.vrg.payserver.service.DefaultChannelService;
import com.vrg.payserver.service.IChannel;
import com.vrg.payserver.service.IChannelFactory;

/**
 *
 */
public class ChannelFactory implements IChannelFactory {

	@Override
	public Map<String, IChannel> createChannelWithVersions() {
		Map<String, IChannel> channelImpls = new HashMap<>();
		channelImpls.put("v1", new DefaultChannelService(new KuBeiChannelAdapter()));
		return channelImpls;
	}

	@Override
	public String getChannelId() {
		return "kubei";
	}

}