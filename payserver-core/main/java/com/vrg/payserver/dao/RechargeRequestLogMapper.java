/**
 * 
 */
package com.vrg.payserver.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import com.vrg.payserver.service.vo.RechargeRequestLog;
import com.vrg.payserver.vo.ClearRechargeRequestLog;


/**
 * @author LUCHUNLIANG
 *
 */
public interface RechargeRequestLogMapper {
	@Insert("insert into recharge_request_log_${tableNamePostfix} (event_type,trade_no,channel_trade_no,xg_app_id,channel_id,uid,device_id,request_time,response_time,request_ip,request_header,request_value1,request_value2,response_value1,response_value2) "
			+ " values (#{eventType,jdbcType=VARCHAR},#{tradeNo,jdbcType=VARCHAR},#{channelTradeNo,jdbcType=VARCHAR},#{xgAppId,jdbcType=VARCHAR},#{channelId,jdbcType=VARCHAR},"
			+ "#{uid,jdbcType=VARCHAR},#{deviceId,jdbcType=VARCHAR},#{requestTime},#{responseTime},#{requestIp,jdbcType=VARCHAR},#{requestHeader,jdbcType=VARCHAR},#{requestValue1,jdbcType=VARCHAR},#{requestValue2,jdbcType=VARCHAR},"
			+ "#{responseValue1,jdbcType=VARCHAR},#{responseValue2,jdbcType=VARCHAR})")
	int create(RechargeRequestLog rechargeRequestLog);

	@Delete("truncate table recharge_request_log_${tableNamePostfix}")
	int delete(ClearRechargeRequestLog clearLog);
}
