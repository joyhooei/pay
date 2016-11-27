/**
 *
 */
package com.vrg.payserver.dao;

import com.vrg.payserver.service.vo.RechargeRecordBase;

public interface RechargeFailLogMapper {

	int create(RechargeRecordBase rechargeFailLog);

	int delete(RechargeRecordBase rechargeFailLog);

	RechargeRecordBase queryByTradeNo(RechargeRecordBase rechargeLog);

}
