package com.vrg.payserver.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrg.payserver.util.logging.LogAction;
import com.vrg.payserver.util.logging.LogContext;
import com.vrg.payserver.util.logging.LogType;

public class Log {
	private static final Logger bizLog = LoggerFactory.getLogger("BIZ_LOGGER");
	private static final ThreadLocal<LogContext> logContext = new ThreadLocal<>();
	private static String LOCAL_SERVER_NAME = "";

	static {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			LOCAL_SERVER_NAME = addr.getHostName();
		} catch (Exception exa) {
			LOCAL_SERVER_NAME = "Unknown";
		}
		if ("Unknown".equals(LOCAL_SERVER_NAME)) {
			try {
				StringBuilder textBuilder = new StringBuilder();
				InputStream is = Runtime.getRuntime().exec("hostname").getInputStream();
				Reader reader = new BufferedReader(
						new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())));
				int c = 0;
				while ((c = reader.read()) != -1) {
					textBuilder.append((char) c);
				}
				LOCAL_SERVER_NAME = textBuilder.toString().replace("\n", "");
			} catch (Exception ex) {
				LOCAL_SERVER_NAME = "Unknown";
			}
		}
	}

	private static String getTransactionId() {
		RequestAttributes currentRequestAttributes = RequestContextHolder.getRequestAttributes();
		if (currentRequestAttributes != null) {
			String txId = (String) currentRequestAttributes.getAttribute("_txId", RequestAttributes.SCOPE_REQUEST);
			if (txId == null) {
				txId = UUID.randomUUID().toString().replace("-", "");
				currentRequestAttributes.setAttribute("_txId", txId, RequestAttributes.SCOPE_REQUEST);
			}
			return txId;
		} else {
			return "-";
		}
	}

	private static LogContext getCurrentOrCreateXGLogContext() {
		LogContext context = (LogContext) logContext.get();
		if (context == null) {
			context = new LogContext();
			context._txId = UUID.randomUUID().toString().replace("-", "");
			context.threadId = Thread.currentThread().getName();
			context.serverId = LOCAL_SERVER_NAME;
			logContext.set(context);
		}
		return context;
	}

	private static LogContext createXGLogContext() {
		LogContext context = new LogContext();
		context.actionTime = new Date();
		context._ts = System.currentTimeMillis();
		context._txId = UUID.randomUUID().toString().replace("-", "");
		context.threadId = Thread.currentThread().getName();
		context.serverId = LOCAL_SERVER_NAME;
		logContext.remove();
		logContext.set(context);
		return context;
	}

	public static void startAction(String actionName, String partnerId, String channelId, String remoteIp, Object request) {
		LogContext context = createXGLogContext();
		context.seq++;
		context.action = actionName;
		context.partnerId = partnerId;
		context.channelId = channelId;
		context.remoteIp = remoteIp;
		context.request = convertObjectToString(request);
		try {
			if (StringUtils.isBlank(context.request_p)) {
				context.request_p = Util.getParameterString(request);
			}
		} catch (Throwable t) {
			context.request_p = "";
		}
		context.addActionStartTime(context.action, System.currentTimeMillis());
		context.addStep(LogAction.ACTION_START);
	}

	public static void endAction(String actionName, String status, Object request, Object response) {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {
			context.seq++;
			context.action = actionName;
			if (StringUtils.isNotEmpty(status))
				context.status = status;
			if (request != null) {
				context.request = convertObjectToString(request);
				try {
					if (StringUtils.isBlank(context.request_p)) {
						context.request_p = Util.getParameterString(request);
					}
				} catch (Throwable t) {
					context.request_p = "";
				}
			}
			if (response != null) {
				context.response = convertObjectToString(response);
			}
			context._endTs = System.currentTimeMillis();
			context.calculateCostTime(actionName);
			context.addStep(LogAction.ACTION_END);
			Log.info(context);
			logContext.remove();
		}
	}

	public static void endActionWithError(String actionName, Throwable exception, Object request, Object response) {
		endActionWithWarnOrError(LogType.ERR_SYS, actionName, exception, request, response);
	}

	public static void endActionWithWarn(String actionName, Throwable exception, Object request, Object response) {
		endActionWithWarnOrError(LogType.ERR_WARN, actionName, exception, request, response);
	}

	private static void endActionWithWarnOrError(String xgLogType, String actionName, Throwable exception,
			Object request, Object response) {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {
			context.seq++;
			context.action = actionName;
			context.exception = getExceptionDetail(exception);
			if (StringUtils.isNotEmpty(xgLogType)) {
				context.type = xgLogType;
			} else {
				context.type = LogType.ERR_SYS;
			}
			if (request != null) {
				context.request = convertObjectToString(request);
				try {
					if (StringUtils.isBlank(context.request_p)) {
						context.request_p = Util.getParameterString(request);
					}
				} catch (Throwable t) {
					context.request_p = "";
				}
			}
			if (response != null) {
				context.response = convertObjectToString(response);
			}
			context._endTs = System.currentTimeMillis();
			context.calculateCostTime(actionName);
			context.addStep(LogAction.ACTION_END + "_" + xgLogType);
			Log.info(context);
			logContext.remove();
		}
	}

	public static void supplementRequestContent(Object request) {
		LogContext context = (LogContext) logContext.get();
		if (context != null && request != null) {
			context.request = convertObjectToString(request);
			try {
				if (StringUtils.isBlank(context.request_p)) {
					context.request_p = Util.getParameterString(request);
				}
			} catch (Throwable t) {
				if (request instanceof String) {
					try {
						getParameterString("{" + request + "}");
					} catch (Throwable t1) {
						context.request_p = "";
					}
				} else {
					context.request_p = "";
				}
			}
		}
	}

	public static void supplementBizInfo(String partnerId, String channelId, String tradeNo, String channelTradeNo, String sign, String paymentStatus, String status) {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {

			if (StringUtils.isNotEmpty(channelId))
				context.channelId = channelId;
			if (StringUtils.isNotEmpty(tradeNo))
				context.tradeNo = tradeNo;
			if (StringUtils.isNotEmpty(channelTradeNo))
				context.channelTradeNo = channelTradeNo;
			if (StringUtils.isNotEmpty(sign))
				context.sign = sign;
			if (StringUtils.isNotEmpty(paymentStatus))
				context.paymentStatus = paymentStatus;
			if (StringUtils.isNotEmpty(status))
				context.status = status;
		}
	}

	public static void logHttpRequest(String method, String url, String header, Object requestContent, String cookie,
			int status, Object response, long beginTime, Throwable exception) {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {
			context.seq++;
			context.addMessage(constructHttpMessage(method, url, header, requestContent, cookie, status, response,
					System.currentTimeMillis() - beginTime, exception));
		}
	}

	private static String constructHttpMessage(String method, String url, String header, Object requestContent,
			String cookie, int status, Object responseContent, long costTime, Throwable exception) {
		StringBuffer sb = new StringBuffer();
		String request = convertObjectToString(requestContent);
		String response = convertObjectToString(responseContent);
		sb.append("Method:");
		if (StringUtils.isNotBlank(method)) {
			sb.append(method);
		}
		sb.append(" URL:");
		if (StringUtils.isNotBlank(url)) {
			sb.append(url);
		}
		sb.append(" Status:");
		sb.append(String.valueOf(status));
		sb.append(" Cost:");
		sb.append(String.valueOf(costTime));
		sb.append(" Header:");
		if (StringUtils.isNotBlank(header)) {
			sb.append(header);
		}
		sb.append(" Request:");
		if (StringUtils.isNotBlank(request)) {
			sb.append(request);
		}
		sb.append(" Response:");
		if (StringUtils.isNotBlank(response)) {
			sb.append(response);
		}
		sb.append(" Cookie:");
		if (StringUtils.isNotBlank(cookie)) {
			sb.append(cookie);
		}
		if (exception != null) {
			sb.append(" Exception:");
			sb.append(exception.toString());
		}
		return sb.toString();
	}

	public static void supplementMessage(String msg) {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {
			context.addMessage(msg);
		} else {
			logSingleAction("Single log", LogType.INFO, msg, null);
		}
	}

	public static void supplementMessage(String format, Object... arguments) {
		try {
			String formattedMsg = MessageFormatter.arrayFormat(format, arguments).getMessage();
			supplementMessage(formattedMsg);
		} catch (Throwable t) {
			supplementExceptionMessage(t);
		}
	}

	public static void supplementExceptionMessage(Throwable exception) {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {
			context.exception = StringUtils.join(context.exception, getExceptionDetail(exception));
			context.type = LogType.ERR_SYS;
		} else {
			logSingleAction("Single log", LogType.ERR_SYS, null, exception);
		}
	}

	public static void supplementExceptionMessage(String message, Throwable exception) {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {
			supplementMessage(message);
			context.exception = StringUtils.join(context.exception, getExceptionDetail(exception));
			context.type = LogType.ERR_SYS;
		} else {
			logSingleAction("Single log", LogType.ERR_SYS, message, exception);
		}
	}

	/*
	 * 注意：changeLogContextTypeToAppErr应和changeLogContextTypeToInfo配对使用，不能用于多层嵌套！
	 * ！！
	 */
	public static void changeLogContextTypeToAppErr() {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {
			context.type = LogType.ERR_APP;
		}
	}

	public static void changeLogContextTypeToInfo() {
		LogContext context = (LogContext) logContext.get();
		if (context != null) {
			context.type = LogType.INFO;
		}
	}

	public static void logSingleAction(String actionName, String xgLogType, Object messageObject, Throwable exception) {
		LogContext context = createXGLogContext();
		if (StringUtils.isEmpty(xgLogType)) {
			if (exception != null) {
				context.type = LogType.ERR_SYS;
				context.exception = getExceptionDetail(exception);
			} else {
				context.type = LogType.INFO;
			}
		} else {
			context.type = xgLogType;
		}
		context.action = actionName;
		context.addMessage(convertObjectToString(messageObject));
		context.seq++;
		Log.info(context);
		logContext.remove();
	}

	public static void enterStep(String step) {
		LogContext context = (LogContext) logContext.get();
		if (context != null && StringUtils.isNotBlank(step)) {
			context.addStep(step);
		}
	}

	public static void info(JSONObject info) {
		supplementLogObject(info);
		info.put("_logLevel", "INFO");
		bizLog.info(JSONObject.toJSONString(info));
	}

	public static void info(LogContext context) {
		bizLog.info(context.toJSONString());
	}

	public static void info(String message) {
		JSONObject info = new JSONObject(true);
		supplementLogObject(info);
		info.put("message", message);
		info.put("_logLevel", "INFO");
		bizLog.info(JSONObject.toJSONString(info));
	}

	public static void warn(JSONObject warn) {
		supplementLogObject(warn);
		warn.put("_logLevel", "WARN");
		bizLog.warn(JSONObject.toJSONString(warn));
	}

	public static void error(JSONObject error) {
		supplementLogObject(error);
		error.put("_logLevel", "ERR");
		bizLog.error(JSONObject.toJSONString(error));
	}

	public static void error(String message) {
		JSONObject err = new JSONObject(true);
		supplementLogObject(err);
		err.put("message", message);
		err.put("_logLevel", "ERR");
		bizLog.error(JSONObject.toJSONString(err));
	}

	public static void error(Throwable t) {
		JSONObject err = new JSONObject(true);
		supplementLogObject(err);
		err.put("message", t.getMessage());
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bao);
		t.printStackTrace(ps);
		err.put("stack", new String(bao.toByteArray()));
		err.put("_logLevel", "ERR");
		bizLog.error(JSONObject.toJSONString(err));
	}

	private static JSONObject supplementLogObject(JSONObject log) {
		log.put("_txId", getTransactionId());
		log.put("_ts", System.currentTimeMillis());
		return log;
	}

	private static String convertObjectToString(Object maybeString) {
		String result = null;
		if (maybeString instanceof String) {
			result = ((String) maybeString).replace("{", "").replace("}", "").replace("\\", "").replace("\n", "");
		} else if (maybeString != null) {
			try {
				result = JSON.toJSONString(maybeString).replace("{", "").replace("}", "").replace("\\", "");
			} catch (Exception e) {
				result = maybeString.toString();
				// bizLog.error(e.getMessage(), e);
			}
		}
		return result;
	}

	private static String getExceptionDetail(Throwable exception) {
		StringBuffer sb = new StringBuffer();
		String exptMsg = StringUtils.isEmpty(exception.getMessage()) ? exception.getClass().getName()
				: exception.getMessage();
		sb.append("Exception message: " + exptMsg + "\n");
		StackTraceElement[] trace = exception.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			sb.append(trace[i] + "\n");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String raw = "{\"sign\":\"881288517ea61bb00bf5dfad8cbba4c61ad78337\",\"roleId\":\"88536\",\"uid\":\"ios_jinshanApple__77d0cad78b0ade4d698442__EXP_.\",\"channelId\":\"ios_jinshanApple\",\"planId\":\"956\",\"bundleReceiptData\":\"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\",\"deviceId\":\"936E1A9F-39BC-411C-8181-D654F84CB693\",\"xgAppId\":\"10042\",\"deviceModel\":\"iPhone\",\"deviceBrand\":\"iPhone OS_9.3.1\",\"sdkCustData\":\"ewoJInNpZ25hdHVyZSIgPSAiQW5HZUM5dldwSVl2UXNscHUwUFF6Q3A5MzQ2SzZOYTN1eVdvNUhWQUVHa3RJQ2JwWWxERzZFM3hsb1ZzMXVUWGVDY1NOWndsZnlIN1IvYzQzTU5JNXp3cVBNYzRkU3hYS1gwai9uUDBNSG9pK3o0cXRDNzdhS3RPUG9yNEV2UlBZL3oxT2ZPT2JHY1lmSlZ6cGthTWhlRkk4SE9zc0VuSm5WcU1oUkptc2FKd0FBQURWekNDQTFNd2dnSTdvQU1DQVFJQ0NCdXA0K1BBaG0vTE1BMEdDU3FHU0liM0RRRUJCUVVBTUg4eEN6QUpCZ05WQkFZVEFsVlRNUk13RVFZRFZRUUtEQXBCY0hCc1pTQkpibU11TVNZd0pBWURWUVFMREIxQmNIQnNaU0JEWlhKMGFXWnBZMkYwYVc5dUlFRjFkR2h2Y21sMGVURXpNREVHQTFVRUF3d3FRWEJ3YkdVZ2FWUjFibVZ6SUZOMGIzSmxJRU5sY25ScFptbGpZWFJwYjI0Z1FYVjBhRzl5YVhSNU1CNFhEVEUwTURZd056QXdNREl5TVZvWERURTJNRFV4T0RFNE16RXpNRm93WkRFak1DRUdBMVVFQXd3YVVIVnlZMmhoYzJWU1pXTmxhWEIwUTJWeWRHbG1hV05oZEdVeEd6QVpCZ05WQkFzTUVrRndjR3hsSUdsVWRXNWxjeUJUZEc5eVpURVRNQkVHQTFVRUNnd0tRWEJ3YkdVZ1NXNWpMakVMTUFrR0ExVUVCaE1DVlZNd2daOHdEUVlKS29aSWh2Y05BUUVCQlFBRGdZMEFNSUdKQW9HQkFNbVRFdUxnamltTHdSSnh5MW9FZjBlc1VORFZFSWU2d0Rzbm5hbDE0aE5CdDF2MTk1WDZuOTNZTzdnaTNvclBTdXg5RDU1NFNrTXArU2F5Zzg0bFRjMzYyVXRtWUxwV25iMzRucXlHeDlLQlZUeTVPR1Y0bGpFMU93QytvVG5STStRTFJDbWVOeE1iUFpoUzQ3VCtlWnRERWhWQjl1c2szK0pNMkNvZ2Z3bzdBZ01CQUFHamNqQndNQjBHQTFVZERnUVdCQlNKYUVlTnVxOURmNlpmTjY4RmUrSTJ1MjJzc0RBTUJnTlZIUk1CQWY4RUFqQUFNQjhHQTFVZEl3UVlNQmFBRkRZZDZPS2RndElCR0xVeWF3N1hRd3VSV0VNNk1BNEdBMVVkRHdFQi93UUVBd0lIZ0RBUUJnb3Foa2lHOTJOa0JnVUJCQUlGQURBTkJna3Foa2lHOXcwQkFRVUZBQU9DQVFFQWVhSlYyVTUxcnhmY3FBQWU1QzIvZkVXOEtVbDRpTzRsTXV0YTdONlh6UDFwWkl6MU5ra0N0SUl3ZXlOajVVUllISytIalJLU1U5UkxndU5sMG5rZnhxT2JpTWNrd1J1ZEtTcTY5Tkluclp5Q0Q2NlI0Szc3bmI5bE1UQUJTU1lsc0t0OG9OdGxoZ1IvMWtqU1NSUWNIa3RzRGNTaVFHS01ka1NscDRBeVhmN3ZuSFBCZTR5Q3dZVjJQcFNOMDRrYm9pSjNwQmx4c0d3Vi9abEwyNk0ydWVZSEtZQ3VYaGRxRnd4VmdtNTJoM29lSk9PdC92WTRFY1FxN2VxSG02bTAzWjliN1BSellNMktHWEhEbU9Nazd2RHBlTVZsTERQU0dZejErVTNzRHhKemViU3BiYUptVDdpbXpVS2ZnZ0VZN3h4ZjRjemZIMHlqNXdOelNHVE92UT09IjsKCSJwdXJjaGFzZS1pbmZvIiA9ICJld29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVXRjSE4wSWlBOUlDSXlNREUyTFRBMExUQTJJREF6T2pFeE9qSXhJRUZ0WlhKcFkyRXZURzl6WDBGdVoyVnNaWE1pT3dvSkluQjFjbU5vWVhObExXUmhkR1V0YlhNaUlEMGdJakUwTlRrNU16YzBPREV3TmpFaU93b0pJblZ1YVhGMVpTMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJM016WmlNR1prTm1JM01UazFOakJrTkRsaU1EQmhNbUV6WmprMFlqZGpNamxrWmprME1tSXlJanNLQ1NKdmNtbG5hVzVoYkMxMGNtRnVjMkZqZEdsdmJpMXBaQ0lnUFNBaU16a3dNREF3TVRBeU16ZzBOall4SWpzS0NTSmlkbkp6SWlBOUlDSXhNalk0TXlJN0Nna2lZWEJ3TFdsMFpXMHRhV1FpSUQwZ0lqRXdNelF5TWpNd05UVWlPd29KSW5SeVlXNXpZV04wYVc5dUxXbGtJaUE5SUNJek9UQXdNREF4TURJek9EUTJOakVpT3dvSkluRjFZVzUwYVhSNUlpQTlJQ0l4SWpzS0NTSnZjbWxuYVc1aGJDMXdkWEpqYUdGelpTMWtZWFJsTFcxeklpQTlJQ0l4TkRVNU9UTTNORGd4TURZeElqc0tDU0oxYm1seGRXVXRkbVZ1Wkc5eUxXbGtaVzUwYVdacFpYSWlJRDBnSWprek5rVXhRVGxHTFRNNVFrTXROREV4UXkwNE1UZ3hMVVEyTlRSR09EUkRRalk1TXlJN0Nna2lhWFJsYlMxcFpDSWdQU0FpTVRBNE9EVXlPVFUzT1NJN0Nna2lkbVZ5YzJsdmJpMWxlSFJsY201aGJDMXBaR1Z1ZEdsbWFXVnlJaUE5SUNJNE1UWTNOVEl3TnpjaU93b0pJbkJ5YjJSMVkzUXRhV1FpSUQwZ0ltTnZiUzVuWVcxbExubDNNeTQySWpzS0NTSndkWEpqYUdGelpTMWtZWFJsSWlBOUlDSXlNREUyTFRBMExUQTJJREV3T2pFeE9qSXhJRVYwWXk5SFRWUWlPd29KSW05eWFXZHBibUZzTFhCMWNtTm9ZWE5sTFdSaGRHVWlJRDBnSWpJd01UWXRNRFF0TURZZ01UQTZNVEU2TWpFZ1JYUmpMMGROVkNJN0Nna2lZbWxrSWlBOUlDSmpiMjB1ZDNwNmVDNTVaWGRsYmpNaU93b0pJbkIxY21Ob1lYTmxMV1JoZEdVdGNITjBJaUE5SUNJeU1ERTJMVEEwTFRBMklEQXpPakV4T2pJeElFRnRaWEpwWTJFdlRHOXpYMEZ1WjJWc1pYTWlPd3A5IjsKCSJwb2QiID0gIjM5IjsKCSJzaWduaW5nLXN0YXR1cyIgPSAiMCI7Cn0=\",\"buildNumber\":\"1.0.0\",\"roleName\":\"熊枫\",\"tokenId\":\"b9e52b86f4229da3\",\"channelAppId\":\"com.wzzx.yewen3\",\"serverId\":\"4\",\"paySN\":\"C3054EED-C6F1-420A-86C7-8A255B9B926F-464-000000E84DC92ADE\"}";
		String s = getParameterString(raw);
		System.out.println(s);
	}

	public static String getParameterString(String request) {
		String REQUEST_EQUAL = "=";
		String REQUEST_SEPARATOR = "&";
		JSONObject jsonObject = JSON.parseObject(request);
		List<String> keyList = new ArrayList<>(jsonObject.keySet());
		Collections.sort(keyList);
		StringBuilder builder = new StringBuilder();
		for (String key : keyList) {
			String value = jsonObject.getString(key);
			if (!StringUtils.isEmpty(value)) {
				try {
					value = URLEncoder.encode(value, Util.ENCODING).replace("+", "%20");
				} catch (UnsupportedEncodingException e) {
				}
			}
			if (!StringUtils.isEmpty(value)) {
				builder.append(key).append(REQUEST_EQUAL).append(value).append(REQUEST_SEPARATOR);
			}
		}
		builder.delete(builder.length() - REQUEST_SEPARATOR.length(), builder.length());
		return builder.toString();
	}
}
