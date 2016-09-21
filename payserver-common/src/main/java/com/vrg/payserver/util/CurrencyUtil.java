package com.vrg.payserver.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author DENGKUADONG
 *
 */
public class CurrencyUtil {
	private static final int DEFAULT_SCALE = 2;
	private static BigDecimal DEFAULT_RATE = new BigDecimal(100);

	public static String fenToYuan(String fen) {
		if (fen == null || fen.trim().length() == 0) {
			return "0.00";
		}
		BigDecimal yuan = new BigDecimal(fen).divide(DEFAULT_RATE);
		return yuan.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP).toString();
	}

	public static String fenToYuan(int fen) {
	  BigDecimal yuan = new BigDecimal(fen).divide(DEFAULT_RATE);
	  return yuan.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP).toString();
	}

	public static String yuanToFen(String yuan) {
		if (yuan == null || yuan.trim().length() == 0) {
			return "0";
		}
		BigDecimal fen = new BigDecimal(yuan).multiply(DEFAULT_RATE);
		return fen.setScale(0, RoundingMode.HALF_UP).toString();
	}

	public static int yuanToFenInt(String yuan) {
		if (yuan == null || yuan.trim().length() == 0) {
			return 0;
		}
		BigDecimal fen = new BigDecimal(yuan).multiply(DEFAULT_RATE);
		return fen.intValue();
	}

	public static int fenToFenInt(String fen) {
		if (fen == null || fen.trim().length() == 0)
			return 0;
		try {
			return Integer.parseInt(fen);
		} catch (NumberFormatException nfe) {
		}
		return 0;
	}

}
