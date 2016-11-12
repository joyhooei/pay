/**
 * 
 */
package com.vrg.payserver.dao;

import com.vrg.payserver.service.vo.RechargeRecordBase;

public interface RechargeRecordStatusMapper {
	int create(RechargeRecordBase rechargeRecordStatus);

//	int createFromFailLog(RechargeRecordBase rechargeRecordBase);
	
	RechargeRecordBase queryByTradeNo(String tradeNo);
	
	int update(RechargeRecordBase rechargeRecordStatus);
	
	RechargeRecordBase queryByChannelTradeNo(String channelTradeNo, String channelId);
	
	int updateChannelTradeNoByTradeNo(String tradeNo, String channelTradeNo);
	
	int delete(long chargeLogId);
	
	long getTradeNoSegment();
	
	long getChargeLogId();
}
