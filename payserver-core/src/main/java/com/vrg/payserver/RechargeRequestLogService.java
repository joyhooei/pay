/**
 * 
 */
package com.vrg.payserver;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrg.payserver.dao.RechargeRequestLogMapper;
import com.vrg.payserver.service.vo.RechargeRequestLog;
import com.vrg.payserver.util.ChannelCode;
import com.vrg.payserver.util.ErrorCode;
import com.vrg.payserver.util.IPUtils;
import com.vrg.payserver.util.RequestType;
import com.vrg.payserver.util.Util;
import com.vrg.payserver.vo.RechargeRequestObject;

@Service
public class RechargeRequestLogService {
	private static final Logger requestLog = LoggerFactory.getLogger("REQUEST_LOGGER");
	private static final String DEBUG_UID_PREFIX = "debug_";
	private static final String DEBUG_XGTEST_UID_PREFIX = Util.getXgUid(ChannelCode.TESTCHANNEL, DEBUG_UID_PREFIX);
	@Value("${xgsdk.server.ip:127.0.0.1}")
	private String serverIp;
	private String localIp;
	private static final HashSet<String> LOCALIP_TYPESET = new HashSet<String>();
	static {
		LOCALIP_TYPESET.add(RequestType.NOTIFY_GAME);
		LOCALIP_TYPESET.add(RequestType.SEARCH_CHANNEL_ORDER);
		LOCALIP_TYPESET.add(RequestType.CREATE_CHANNEL_ORDER);
		LOCALIP_TYPESET.add(RequestType.VERIFY_CHANNEL_SESSION);
		LOCALIP_TYPESET.add(RequestType.VERIFY_CHANNEL_ORDER);
	}

	@Value("${xgsdk.recharge_request_log.create-order:file,db}")
	private String logCreateOrder;
	@Value("${xgsdk.recharge_request_log.update-order:file,db}")
	private String logUpdateOrder;
	@Value("${xgsdk.recharge_request_log.cancel-order:file,db}")
	private String logCancelOrder;
	@Value("${xgsdk.recharge_request_log.testchannel-notify:file,db}")
	private String logTestchannelNotify;
	@Value("${xgsdk.recharge_request_log.notify-game:file,db}")
	private String logNotifyGame;
	@Value("${xgsdk.recharge_request_log.verify-order:file,db}")
	private String logVerifyOrder;
	@Value("${xgsdk.recharge_request_log.query-order-status:file,db}")
	private String logQueryOrderStatus;
	@Value("${xgsdk.recharge_request_log.channel-notify:file,db}")
	private String logChannelNotify;
	@Value("${xgsdk.recharge_request_log.verify-session:file,db}")
	private String logVerifySession;
	@Value("${xgsdk.recharge_request_log.get-channel-param:file,db}")
	private String logGetChannelParam;
	@Value("${xgsdk.recharge_request_log.search-channel-order:file,db}")
	private String logSearchChannelOrder;
	@Value("${xgsdk.recharge_request_log.create-channel-order:file,db}")
	private String logCreateChannelOrder;
	@Value("${xgsdk.recharge_request_log.verify-channel-session:file,db}")
	private String logVerifyChannelSession;
	@Value("${xgsdk.recharge_request_log.verify-channel-order:file,db}")
	private String logVerifyChannelOrder;
	private Map<String, Boolean> logFileMap;
	private Map<String, Boolean> logDbMap;

	public static final String LOG_FILE = "file";
	public static final String LOG_DB = "db";

	@Autowired
	private RechargeRequestLogMapper rechargeRequestLogMapper;

	private ConcurrentLinkedQueue<RechargeRequestObject> queue = new ConcurrentLinkedQueue<>();

	private String getBizIP() {
		if (localIp == null) {
			String localIp = IPUtils.getLocalIP();
			if (localIp == null) {
				localIp = serverIp;
			}
		}
		return localIp;
	}

	private boolean isLogFile(String eventType) {
		if (eventType == null) {
			return false;
		}
		if (logFileMap == null) {
			logFileMap = new HashMap<String, Boolean>();
			logFileMap.put(RequestType.CREATE_ORDER, logCreateOrder.contains(LOG_FILE));
			logFileMap.put(RequestType.UPDATE_ORDER, logUpdateOrder.contains(LOG_FILE));
			logFileMap.put(RequestType.CANCEL_ORDER, logCancelOrder.contains(LOG_FILE));
			logFileMap.put(RequestType.TESTCHANNEL_NOTIFY, logTestchannelNotify.contains(LOG_FILE));
			logFileMap.put(RequestType.NOTIFY_GAME, logNotifyGame.contains(LOG_FILE));
			logFileMap.put(RequestType.VERIFY_ORDER, logVerifyOrder.contains(LOG_FILE));
			logFileMap.put(RequestType.QUERY_ORDER_STATUS, logQueryOrderStatus.contains(LOG_FILE));
			logFileMap.put(RequestType.CHANNEL_NOTIFY, logChannelNotify.contains(LOG_FILE));
			logFileMap.put(RequestType.VERIFY_SESSION, logVerifySession.contains(LOG_FILE));
			logFileMap.put(RequestType.GET_CHANNEL_PARAM, logGetChannelParam.contains(LOG_FILE));
			logFileMap.put(RequestType.SEARCH_CHANNEL_ORDER, logSearchChannelOrder.contains(LOG_FILE));
			logFileMap.put(RequestType.CREATE_CHANNEL_ORDER, logCreateChannelOrder.contains(LOG_FILE));
			logFileMap.put(RequestType.VERIFY_CHANNEL_SESSION, logVerifyChannelSession.contains(LOG_FILE));
			logFileMap.put(RequestType.VERIFY_CHANNEL_ORDER, logVerifyChannelOrder.contains(LOG_FILE));
		}
		Boolean returnValue = logFileMap.get(eventType);
		if (returnValue == null) {
			return false;
		}
		return returnValue.booleanValue();
	}

	private boolean isLogDb(String eventType) {
		if (eventType == null) {
			return false;
		}
		if (logDbMap == null) {
			logDbMap = new HashMap<String, Boolean>();
			logDbMap.put(RequestType.CREATE_ORDER, logCreateOrder.contains(LOG_DB));
			logDbMap.put(RequestType.UPDATE_ORDER, logUpdateOrder.contains(LOG_DB));
			logDbMap.put(RequestType.CANCEL_ORDER, logCancelOrder.contains(LOG_DB));
			logDbMap.put(RequestType.TESTCHANNEL_NOTIFY, logTestchannelNotify.contains(LOG_DB));
			logDbMap.put(RequestType.NOTIFY_GAME, logNotifyGame.contains(LOG_DB));
			logDbMap.put(RequestType.VERIFY_ORDER, logVerifyOrder.contains(LOG_DB));
			logDbMap.put(RequestType.QUERY_ORDER_STATUS, logQueryOrderStatus.contains(LOG_DB));
			logDbMap.put(RequestType.CHANNEL_NOTIFY, logChannelNotify.contains(LOG_DB));
			logDbMap.put(RequestType.VERIFY_SESSION, logVerifySession.contains(LOG_DB));
			logDbMap.put(RequestType.GET_CHANNEL_PARAM, logGetChannelParam.contains(LOG_DB));
			logDbMap.put(RequestType.SEARCH_CHANNEL_ORDER, logSearchChannelOrder.contains(LOG_DB));
			logDbMap.put(RequestType.CREATE_CHANNEL_ORDER, logCreateChannelOrder.contains(LOG_DB));
			logDbMap.put(RequestType.VERIFY_CHANNEL_SESSION, logVerifyChannelSession.contains(LOG_DB));
			logDbMap.put(RequestType.VERIFY_CHANNEL_ORDER, logVerifyChannelOrder.contains(LOG_DB));
		}
		Boolean returnValue = logDbMap.get(eventType);
		if (returnValue == null) {
			return false;
		}
		return returnValue.booleanValue();
	}

	public void push(HttpServletRequest hRequest, String requestType, Date requestTime, Object request, Object response, Object bizObject) {
		this.saveRechargeRequestLog(hRequest, requestType, requestTime, request, response);

		RechargeRequestObject obj = new RechargeRequestObject();
		obj.setRequestType(requestType);
		obj.setRequest(request);
		obj.setResponse(response);
		obj.setBizObject(bizObject);
		queue.add(obj);
	}

	public void push(RechargeRequestLog rechargeRequestLog) {
		if (rechargeRequestLog == null) {
			return;
		}
		if (LOCALIP_TYPESET.contains(rechargeRequestLog.getEventType())) {
			rechargeRequestLog.setRequestIp(getBizIP());
		}
		if (isLogDb(rechargeRequestLog.getEventType())) {
			rechargeRequestLogMapper.create(rechargeRequestLog);
		}
		if (isLogFile(rechargeRequestLog.getEventType())) {
			requestLog.info(JSON.toJSONString(rechargeRequestLog));
		}
	}

	public void push(String requestType, Date requestTime, Object request, Object response, Object bizObject, String requestIp, String tradeNo) {
		RechargeRequestLog rechargeRequestLog = RechargeRequestLog.getRechargeRequestLogFromClientRequest(requestIp, request, requestTime);
		rechargeRequestLog.putClientResponseData(response, new Date());
		rechargeRequestLog.setEventType(requestType);
		rechargeRequestLog.setTradeNo(tradeNo);
		this.push(rechargeRequestLog);

		RechargeRequestObject obj = new RechargeRequestObject();
		obj.setRequestType(requestType);
		obj.setRequest(request);
		obj.setResponse(response);
		obj.setBizObject(bizObject);
		queue.add(obj);
	}

	private void saveRechargeRequestLog(HttpServletRequest hRequest, String requestType, Date requestTime, Object request, Object response) {
		RechargeRequestLog rechargeRequestLog = RechargeRequestLog.getRechargeRequestLogFromClientRequest(hRequest, request, requestTime);
		rechargeRequestLog.putClientResponseData(response, new Date());
		rechargeRequestLog.setEventType(requestType);
		this.push(rechargeRequestLog);
	}

//	public void process() {
//		while (!queue.isEmpty()) {
//			RechargeRequestObject obj = queue.poll();
//			for (IRequestListener listener : listeners) {
//				try {
//					listener.process(obj);
//				} catch (Throwable t) {
//					XGLog.supplementExceptionMessage(t);
//				}
//			}
//		}
//		for (IRequestListener listener : listeners) {
//			try {
//				listener.check();
//			} catch (Throwable t) {
//			  XGLog.supplementExceptionMessage(t);
//			}
//		}
//	}
}
