package com.vrg.payserver.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author DENGKUADONG
 *
 */
public class StringUtil {

  public static boolean notEquals(String s1, String s2) {
    return !StringUtils.equals(s1, s2);
  }

}
