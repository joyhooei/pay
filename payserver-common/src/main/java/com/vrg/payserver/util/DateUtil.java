package com.vrg.payserver.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static Date parse(String dateStr) {
		try {
			return new SimpleDateFormat(DEFAULT_PATTERN).parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String format(Date date) {
		return new SimpleDateFormat(DEFAULT_PATTERN).format(date);
	}

	public static boolean withinOneMonth(Date earlyDate, Date laterDate) {
		if (earlyDate == null || laterDate == null) {
			return false;
		}
		return (Math.abs(earlyDate.getTime() - laterDate.getTime()) <= 30l * 24l * 60 * 60 * 1000);
	}

	public static boolean withinOneDay(Date orderDate, String requestDateString, String pattern) {
		if (orderDate == null || requestDateString == null) {
			return false;
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			Date requestDate = format.parse(requestDateString);
			return (Math.abs(orderDate.getTime() - requestDate.getTime()) <= 24l * 60 * 60 * 1000);
		} catch (Throwable t) {
			Log.supplementExceptionMessage(t);
			return false;
		}
	}

	public static boolean withinSevenDay(Date orderDate, Date requestDate) {
		if (orderDate == null || requestDate == null) {
			return false;
		}
		try {
			return (Math.abs(orderDate.getTime() - requestDate.getTime()) <= 7 * 24 * 60 * 60 * 1000L);
		} catch (Throwable t) {
		  Log.supplementExceptionMessage(t);
			return false;
		}
	}

	public static Date now() {
		return new Date();
	}
}
