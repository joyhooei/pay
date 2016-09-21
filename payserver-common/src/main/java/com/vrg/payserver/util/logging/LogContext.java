package com.vrg.payserver.util.logging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class LogContext {

	@JSONField(ordinal = 1)
	public String _txId;
	@JSONField(ordinal = 2)
	public int seq = 0;
	@JSONField(ordinal = 3)
	public String action;
	@JSONField(ordinal = 4)
	public String type = LogType.INFO;
	@JSONField(ordinal = 5)
	public String partnerId;
	@JSONField(ordinal = 6)
	public String channelId;
	@JSONField(ordinal = 7)
	public String tradeNo;
	@JSONField(ordinal = 8)
	public String channelTradeNo;
	@JSONField(ordinal = 9)
	public long costTime;
	@JSONField(ordinal = 10)
	public String sign;
	@JSONField(ordinal = 11)
	public String remoteIp;
	@JSONField(ordinal = 12)
	public String paymentStatus;
	@JSONField(ordinal = 13)
	public String exception;
	@JSONField(ordinal = 14)
	public String url;
	@JSONField(ordinal = 15)
	public Object request;
	@JSONField(ordinal = 16)
	public String request_p;
	@JSONField(ordinal = 17)
	public Object response;
	@JSONField(ordinal = 18)
	public long _ts;
	@JSONField(ordinal = 19)
	public String threadId;
	@JSONField(ordinal = 20)
	public String serverId;
	@JSONField(ordinal = 21)
	public String status;
	@JSONField(ordinal = 22)
	public String cookie;
	@JSONField (format="yyyy-MM-dd HH:mm:ss.SSS")
	public Date actionTime;
	@JSONField(serialize=false)
	public Map<String, Long> actionStartTimeMap;
	public String messages = "";
	@JSONField(serialize=false)
	public int messageCount;
	public String stepTrace = "";
	@JSONField(serialize=false)
	public long _endTs;
	
	public void addActionStartTime(String action, long time) {
		if(actionStartTimeMap==null) actionStartTimeMap = new HashMap<String, Long>();
		actionStartTimeMap.put(action+LogAction.ACTION_START, time);
	}
	
	public long getActionStartTime(String action) {
		if(actionStartTimeMap==null || actionStartTimeMap.get(action+LogAction.ACTION_START)==null) {
			 return 0;
		} else {
			return (long)actionStartTimeMap.get(action+LogAction.ACTION_START);
		}
	}
	
	public void removeActionStartTime(String action) {
		if(actionStartTimeMap!=null && actionStartTimeMap.containsKey(action+LogAction.ACTION_START)) {
			actionStartTimeMap.remove(action+LogAction.ACTION_START);
		}
	}
	
	//TODO:对于同一action嵌套执行存在问题.1.使用栈，出栈时比对action是否相同。2.把seq作为mapkey的一部分，get的时候用action+seq--递减追溯回starttime
	public void calculateCostTime(String action) {
		long startTs = getActionStartTime(action);
		if (startTs > 0) {
			costTime = _endTs - startTs;
			removeActionStartTime(action);
		}
	}
	
	public void addMessage(String message) {
	  String msg = (++this.messageCount) + ". " + message;
	  messages = messages.trim()+"\n\n"+msg;
	}
	
	public void addStep(String step) {
	  if(step.startsWith(LogAction.ACTION_END)) {
	    this.stepTrace = this.stepTrace + step;
	  }else{
	    this.stepTrace = this.stepTrace + step + "->";
	  }
	}
	
	public String toJSONString() {
		return JSONObject.toJSONString(this);
	}
}
