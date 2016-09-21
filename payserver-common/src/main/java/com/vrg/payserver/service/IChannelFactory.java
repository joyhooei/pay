/**
 * 
 */
package com.vrg.payserver.service;

import java.util.Map;

public interface IChannelFactory {
	
	Map<String, IChannel> createChannelWithVersions();
	
	String getChannelId();
}
