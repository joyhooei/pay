package com.vrg.payserver;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vrg.payserver.dao.RechargeFailLogMapper;
import com.vrg.payserver.dao.RechargeLogMapper;
import com.vrg.payserver.dao.RechargeRecordStatusMapper;
import com.vrg.payserver.repository.ChannelParamRepository;
import com.vrg.payserver.service.IServerCoreService;
import com.vrg.payserver.service.ParamRepository;
import com.vrg.payserver.service.vo.ChannelNotifyRequest;
import com.vrg.payserver.service.vo.ChannelRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeRequest;
import com.vrg.payserver.service.vo.ClientNewRechargeResponse;
import com.vrg.payserver.service.vo.RechargeRecordBase;
import com.vrg.payserver.service.vo.RechargeRequestException;
import com.vrg.payserver.service.vo.RechargeRequestLog;

@Service
public class ServerCoreService implements IServerCoreService {
	private static final Logger rechargeLog = LoggerFactory.getLogger("RECHARGE_LOGGER");
	private static final String GET_GAME_WEBPAY_PRODUCTS_URL = "GET_GAME_WEBPAY_PRODUCTS_URL";
	public static final String NOTIFY_GAME_WITH_CHANNEL_TRADE_NO = "NOTIFY_GAME_WITH_CHANNEL_TRADE_NO";
	public static final String NOTIFY_GAME_WITH_PAY_TYPE = "NOTIFY_GAME_WITH_PAY_TYPE";

//	static {
//		Arrays.sort(EXCEPTION_CODE);
//	}

	@Value("${xgsdk.recharge_request_log.save:true}")
	private boolean saveRechargeRequestLog;
	@Autowired
	private ChannelParamRepository channelParamRepository;
	
	@Autowired
	private RechargeRecordStatusMapper rechargeRecordStatusMapper;
	
	@Autowired
	private RechargeLogMapper rechargeLogMapper;
	
	@Autowired
	private RechargeFailLogMapper rechargeFailLogMapper;

	@Autowired
	private ParamRepository paramRepository;

	@Override
	public String getParamValue(String planId, String channelId, String paramName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getParamValues(String xgAppId, String channelId, String paramName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RechargeRecordBase> getUnPaidRechargeRecordByUid(String xgAppId, String channelId, String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RechargeRecordBase> getWaitPayRechargeRecordByUid(String xgAppId, String channelId, String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RechargeRecordBase getRechargeRecordByProductId(String xgAppId, String channelId, String uid,
			String productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RechargeRecordBase getRechargeRecordByTradeNo(String tradeNo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RechargeRecordBase getRechargeRecordByChannelTradeNo(String channelTadeNo, String channelId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkRechargeRecordStatus(int status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkChannelTradeNo(String tradeNo, String channelTradeNo, String channelId, Date paidTime) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPayException(ChannelNotifyRequest xgRequest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaySuccess(ChannelRequest xgRequest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPayException(ChannelRequest xgRequest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getServerAppSecret(String planId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientNewRechargeResponse createOrder(ClientNewRechargeRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveRechargeRequestLog(HttpServletRequest hRequest, String requestType, Date requestTime,
			Object request, Object response, Object bizObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveRequestLog(String requestType, Date requestTime, Object request, Object response, Object bizObject,
			String requestIp, String tradeNo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveRequestLog(RechargeRequestLog rechargeRequestLog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveRequestException(RechargeRequestException rechargeRequestException) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelRechargeRecordStatus(RechargeRecordBase rechargeRecord) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteRechargeFailLog(RechargeRecordBase rechargeFailLog) {
		// TODO Auto-generated method stub
		
	}

}
