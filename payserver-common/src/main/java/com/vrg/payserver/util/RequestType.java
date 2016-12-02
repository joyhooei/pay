/**
 * 
 */
package com.vrg.payserver.util;

public interface RequestType {
	String TYPE_FIELD = "type";
	// 事件类型
	// 1 create-order
	String CREATE_ORDER = "create-order";
	// 2 update-order
	String UPDATE_ORDER = "update-order";
	// 3 cancel-order
	String CANCEL_ORDER = "cancel-order";
	// 4 testchannel-notify
	String TESTCHANNEL_NOTIFY = "testchannel-notify";
	// 5 notify-game
	String NOTIFY_GAME = "notify-game";
	// 6 verify-order
	String VERIFY_ORDER = "verify-order";
	// 7 query-order-status
	String QUERY_ORDER_STATUS = "query-order-status";
	// 8 channel-notify
	String CHANNEL_NOTIFY = "channel-notify";
	//
	// 9 verify-session
	String VERIFY_SESSION = "verify-session";
	// 10 get-channel-param
	String GET_CHANNEL_PARAM = "get-channel-param";
	//11 search-channel-order
	String SEARCH_CHANNEL_ORDER = "search-channel-order";
	//12 create-channel-order
	String CREATE_CHANNEL_ORDER = "create-channel-order";
	//13 verify-channel-session
	String VERIFY_CHANNEL_SESSION = "verify-channel-session";
	//13 verify-channel-order
	String VERIFY_CHANNEL_ORDER = "verify-channel-order";
}
