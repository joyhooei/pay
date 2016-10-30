/**
 * 
 */
package com.vrg.payserver.dao;

import com.vrg.payserver.service.vo.RechargeRequestLog;
import com.vrg.payserver.vo.ClearRechargeRequestLog;

public interface RechargeRequestLogMapper {
	int create(RechargeRequestLog rechargeRequestLog);

	int delete(ClearRechargeRequestLog clearLog);
}
