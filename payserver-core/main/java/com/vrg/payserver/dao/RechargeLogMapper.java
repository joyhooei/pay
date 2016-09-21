package com.vrg.payserver.dao;

import java.util.List;

import com.vrg.payserver.service.vo.RechargeRecordBase;

public interface RechargeLogMapper {
	
	RechargeRecordBase queryByTradeNo(RechargeRecordBase rechargeLog);
	
	int create(RechargeRecordBase rechargeLog);

	List<RechargeRecordBase> queryByChannelIdDate(int month, String channelId, String xgAppId, String day);
}
