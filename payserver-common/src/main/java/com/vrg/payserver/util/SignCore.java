/**
 *
 */
package com.vrg.payserver.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author LUCHUNLIANG
 *
 */
public class SignCore {

	public static final String GAME_CHARSET = "UTF-8";
	public static final String REQUEST_EQUAL = "=";
	public static final String REQUEST_SEPARATOR = "&";
	public static final String SIGN_FIELD_NAME = "sign";

	public static String md5Game(Object request, String signFieldName, String appId, String appSecret,
			boolean urlEncodeValue) {
		try {
			String signingString = getSigningString(request, signFieldName, false, urlEncodeValue, false);
			StringBuilder builder = new StringBuilder(signingString);
			builder.append(appId).append(appSecret);
			Log.supplementMessage("begin to verify the SignCore sign.");
			Log.supplementMessage(builder.toString());
			String sign = Hex.encodeHexString(DigestUtils.md5(builder.toString()));
			Log.supplementMessage(sign);
			Log.supplementMessage("end to verify the SignCore sign.");
			return sign;
		} catch (Throwable e) {
			Log.supplementExceptionMessage(e);
		}
		return "";
	}

	public static String md5GameServer(Object request, String signFieldName, String appSecret) {
		JSONObject jsonObject = (JSONObject) JSON.toJSON(request);
		jsonObject.remove(signFieldName);
		List<String> keyList = new ArrayList<>(jsonObject.keySet());
		Collections.sort(keyList);
		StringBuilder builder = new StringBuilder();
		for (String key : keyList) {
			String value = jsonObject.getString(key);
			if (!StringUtils.isEmpty(value)) {
				builder.append(key).append(REQUEST_EQUAL).append(value);
			}
		}
		builder.append(appSecret);
		Log.supplementMessage(builder.toString());
		return Hex.encodeHexString(DigestUtils.md5(builder.toString()));
	}

	public static String md5(String paramStr) {
	  return Hex.encodeHexString(DigestUtils.md5(paramStr));
	}

	public static String xgSign(Object request, String signFieldName, String appSecret) {
		try {
			String signingString = getSigningString(request, signFieldName, false, false, false);
			return Util.HmacSHA1Encrypt(signingString, appSecret);
		} catch (Throwable t) {
		  Log.supplementExceptionMessage(t);
		}
		return "";
	}

  public static void main(String[] args) {
    try {
      String str = "{\"channelAppId\":\"22320125\",\"channelId\":\"xgtest\",\"currencyName\":\"CNY\",\"customInfo\":\"customInfo\",\"gameCallbackUrl\":\"http://localhost:8090/internal/sdkclient/interface-test/internal/sdkserver/receivePayResult\",\"gameTradeNo\":\"XGCocosdemo\",\"planId\":\"824\",\"productDesc\":\"元宝\",\"productId\":\"payment017\",\"productName\":\"元宝\",\"productQuantity\":\"1\",\"productUnitPrice\":\"1\",\"roleId\":\"1000\",\"roleLevel\":\"1\",\"roleName\":\"xgdemo\",\"roleVipLevel\":\"8\",\"serverId\":\"0\",\"sign\":\"e294da082d3df2dff600728fe4c47b8cec33f454\",\"tokenId\":\"-5481828500802072018\",\"totalAmount\":\"100\",\"type\":\"create-order\",\"uid\":\"xgtest__debug_b87ebae8898347d3ac21fe0b1a2c79c9\",\"xgAppId\":\"22320125\",\"zoneId\":\"1\"}";
      JSONObject jsonObject = JSON.parseObject(str);
      System.out.println(xgSign(jsonObject, "sign", "7f5d85a4ba8a4cc99b6634f6259aae9e"));

      System.out.println(Util.HmacSHA1Encrypt("appId=2882303761517385431&cpOrderId=11602c1000000004&orderId=11602c1000000004&orderStatus=TRADE_SUCCESS&partnerGiftConsume=0&payFee=100&payTime=2016-01-20 00:00:00&uid=10001", "hxb9BBGDaKZZYUocC/CA8A=="));
    
      String str2 = "{\"channelAppId\":\"90000096\",\"channelId\":\"xgtest\",\"currencyName\":\"CNY\",\"customInfo\":\"{\"channelId\":\"xgtest\",\"planId\":\"1599\"}\",\"gameCallbackUrl\":\"http://doc.xgsdk.com:18090/internal/sdkserver/receivePayResult\",\"gameTradeNo\":\"1001\",\"paidAmount\":\"100\",\"planId\":\"1599\",\"productDesc\":\"Description\",\"productId\":\"111\",\"productName\":\"gift\",\"productQuantity\":\"1\",\"productUnitPrice\":\"100\",\"roleId\":\"1000_unity\",\"roleLevel\":\"12\",\"roleName\":\"xgdemo\",\"roleVipLevel\":\"3\",\"serverId\":\"1\",\"sign\":\"067eca52819b19103e5f5f15ad8c2c4996b9fde6\",\"tokenId\":\"token_4a5b274c06f1451b96eafa892b61eac6\",\"totalAmount\":\"100\",\"type\":\"create-order\",\"uid\":\"debug_832b68f9b5a54956a00489f31ca10d2f\",\"xgAppId\":\"90000096\",\"zoneId\":\"1025\"}";
      JSONObject jsonObject2 = JSON.parseObject(str);
      System.out.println(xgSign(jsonObject2, "sign", "3d92cbe971e1454ab8b436602ad4b104"));
    
    
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

	public static String signUC(Object request, String signFieldName, String cpId, String appSecret) {
		try {
			JSONObject jsonObject = (JSONObject) JSON.toJSON(request);
			jsonObject.remove(signFieldName);
			List<String> keyList = new ArrayList<>(jsonObject.keySet());
			Collections.sort(keyList);
			StringBuilder builder = new StringBuilder(cpId);
			for (String key : keyList) {
				String value = jsonObject.getString(key);
				// if (!StringUtils.isEmpty(value)) {
				builder.append(key).append(REQUEST_EQUAL).append(value);
				// }
			}
			builder.append(appSecret);
			String signingString = builder.toString();
			signingString.replaceAll(REQUEST_SEPARATOR, "");
			signingString.replaceAll("/n", "");
			signingString.replaceAll("/r", "");
			return Hex.encodeHexString(DigestUtils.md5(signingString));
		} catch (Throwable t) {
		  Log.supplementExceptionMessage(t);
		}
		return "";
	}

	/**
	* RSA验签名检�?
	* @param content 待签名数�?
	* @param sign 签名�?
	* @param publicKey 分配给开发商公钥
	* @param encode 字符集编�?
	* @return 布尔�?
	*/
	public static boolean doCheckRSA(String content, String sign, String publicKey,String encode)
	{
	    String charset = "utf-8";
        if(StringUtils.isNoneEmpty(encode)){
            charset=encode;
        }
		try
		{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        byte[] encodedKey = Base64.getDecoder().decode(publicKey);
	        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");
			signature.initVerify(pubKey);
			signature.update( content.getBytes(charset) );
			boolean bverify = signature.verify( Base64.getDecoder().decode(sign) );
			return bverify;
		}
		catch (Exception e)
		{
		  Log.supplementExceptionMessage(e);
		}

		return false;
	}

	/**
	 * HmacMD5算法
	 *
	 * @param msg
	 *            加密信息
	 * @param keyString
	 *            秘钥
	 * @return digest 结果
	 */
	public static String hmacMD5(String msg, String keyString) {
		String digest = null;
		try {
			SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacMD5");
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(key);

			byte[] bytes = mac.doFinal(msg.getBytes("UTF-8"));

			StringBuffer hash = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					hash.append('0');
				}
				hash.append(hex);
			}
			digest = hash.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return digest;
	}

	public static String getSigningString(Object request, String signFieldName) {
		String signingString = getSigningString(request, signFieldName, false, true, false);

		return signingString;
	}

	public static String getSigningString(Object request, String signFieldName, boolean withEmpty) {
		return getSigningString(request, signFieldName, withEmpty, true, false);
	}

	public static String getSigningString(Object request, String signFieldName, boolean withEmpty,
			boolean urlEncodeValue, boolean lowerName) {
		return Util.getSigningString(request, signFieldName, withEmpty, urlEncodeValue, lowerName);
	}

	public static String format(final List<? extends NameValuePair> parameters, final String charset,
			String signFieldName, boolean withEmpty, boolean lowerName, boolean urlEncodeValue) {
		return Util.format(parameters, charset, signFieldName, withEmpty, lowerName, urlEncodeValue);
	}

	public static String getTs() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(new Date());
	}
}
