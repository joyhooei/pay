package com.vrg.payserver.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.NameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * 工具类�??
 */
public class Util {

	protected static final String MAC_NAME = "HmacSHA1";
	public static final String ENCODING = "UTF-8";
	public static final String URL_PARAM_START = "?";
	public static final String URL_PARAM_SEPARATOR = "&";
	public static final String URL_PARAM_EQUAL = "=";

	public static final String GAME_CHARSET = "UTF-8";
	public static final String REQUEST_EQUAL = "=";
	public static final String REQUEST_SEPARATOR = "&";
	public static final String REQUEST_CONTENTTYPE = "Content-Type";
	public static final String SEPARATOR = "__";

	public static String doGet(String url) {
		return HttpUtils.doGet(url);
	}

	public static String doGet(String url, Map<String, String> header) {
		return HttpUtils.doGet(url, header);
	}

	public static String doPostJson(String url, String body, Map<String, String> headers) {
		return HttpUtils.doPostJson(url, body, headers);
	}

	/**
	 * MD5 加密
	 */
	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));

			byte[] byteArray = messageDigest.digest();

			StringBuffer md5StrBuff = new StringBuffer();

			for (byte element : byteArray) {
				if (Integer.toHexString(0xFF & element).length() == 1) {
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & element));
				} else {
					md5StrBuff.append(Integer.toHexString(0xFF & element));
				}
			}

			return md5StrBuff.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 使用 HMAC-SHA1 签名方法对对 encryptText 进行签名
	 *
	 * @param encryptText
	 *            被签名的字符�?
	 * @param encryptKey
	 *            密钥
	 * @return 返回被加密后的字符串
	 * @throws Exception
	 */
	public static String HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
		byte[] digest = HmacSHA1EncryptByte(encryptText, encryptKey);
		StringBuilder sBuilder = bytesToHexString(digest);
		return sBuilder.toString();
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
			for (byte b : bytes) {
				String hex = Integer.toHexString(0xFF & b);
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

	/**
	 * 使用 HMAC-SHA1 签名方法对对 encryptText 进行签名
	 *
	 * @param encryptText
	 *            被签名的字符�?
	 * @param encryptKey
	 *            密钥
	 * @return 返回被加密后的字符串
	 * @throws Exception
	 */
	public static byte[] HmacSHA1EncryptByte(String encryptText, String encryptKey) throws Exception {
		byte[] data = encryptKey.getBytes(ENCODING);
		SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
		Mac mac = Mac.getInstance(MAC_NAME);
		mac.init(secretKey);
		byte[] text = encryptText.getBytes(ENCODING);
		byte[] digest = mac.doFinal(text);
		return digest;
	}

	/**
	 * 转换成Hex
	 *
	 * @param bytesArray
	 */
	public static StringBuilder bytesToHexString(byte[] bytesArray) {
		if (bytesArray == null) {
			return null;
		}
		StringBuilder sBuilder = new StringBuilder();
		for (byte b : bytesArray) {
			String hv = String.format("%02x", b);
			sBuilder.append(hv);
		}
		return sBuilder;
	}

	/**
	 * 使用 HMAC-SHA1 签名方法对对 encryptText 进行签名
	 *
	 * @param encryptData
	 *            被签名的字符�?
	 * @param encryptKey
	 *            密钥
	 * @return 返回被加密后的字符串
	 * @throws Exception
	 */
	public static String HmacSHA1Encrypt(byte[] encryptData, String encryptKey) throws Exception {
		byte[] data = encryptKey.getBytes(ENCODING);
		SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
		Mac mac = Mac.getInstance(MAC_NAME);
		mac.init(secretKey);
		byte[] digest = mac.doFinal(encryptData);
		StringBuilder sBuilder = bytesToHexString(digest);
		return sBuilder.toString();
	}

	public static String cent2yuan(String centString) {
		if (StringUtils.isEmpty(centString)) {
			return centString;
		}
		String newCentString = centString;
		// 考虑0.01分场�?
		if (!NumberUtils.isDigits(newCentString)) {
			double centDouble = NumberUtils.toDouble(newCentString);
			newCentString = String.valueOf(Math.round(Math.abs(centDouble)));
		}

		int length = newCentString.length();
		StringBuilder builder = new StringBuilder(newCentString);
		if (length > 2) {
			builder.insert(length - 2, ".");
			return builder.toString();
		}
		for (int i = 0; i <= 2 - length; i++) {
			builder.insert(0, "0");
		}
		builder.insert(1, ".");
		return builder.toString();
	}

	public static String yuan2cent(String yuanString) {
		if (StringUtils.isEmpty(yuanString)) {
			return yuanString;
		}
		int index = yuanString.indexOf(".");
		int length = yuanString.length();
		if (index >= 0) {
			StringBuilder builder = new StringBuilder(yuanString);
			// 多于两位小数
			if (index + 3 < length) {
				builder.insert(index + 3, ".");
				builder.deleteCharAt(index);
				return builder.toString();
			}
			// 两位小数
			else if (index + 3 == length) {
				builder.deleteCharAt(index);
				return builder.toString();
			}
			// �?位小�?
			builder.deleteCharAt(index);
			builder.append("0");
			return builder.toString();
		}
		return yuanString + "00";
	}

	public static DateFormat getDateTimeFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public static Date parseDate(String dateStr) throws ParseException {
		return getDateTimeFormat().parse(dateStr);
	}

	public static <T> T parseRequestParameter(HttpServletRequest request, Class<T> returnClass) {
		return parseRequestParameter(request, returnClass, false);
	}

	public static <T> T parseRequestParameter(HttpServletRequest request, Class<T> returnClass, boolean charsetTransfer) {
		Enumeration<String> names = request.getParameterNames();
		JSONObject jsonObject = new JSONObject();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			jsonObject.put(name, request.getParameter(name));
		}
		String jsonString = jsonObject.toJSONString();
		T obj = JSON.parseObject(jsonString, returnClass);
		return obj;
	}

	public static <T> T parseRequestParameter(String request, Class<T> returnClass) {
		JSONObject jsonObject = new JSONObject();
		String[] split = request.split("&");
		for (String string : split) {
			String[] splits = string.split("=");
			if (splits.length == 1) {
				jsonObject.put(splits[0], "");
			} else if (splits.length == 2) {
				jsonObject.put(splits[0], splits[1]);
			}
		}
		String jsonString = jsonObject.toJSONString();
		T obj = JSON.parseObject(jsonString, returnClass);
		return obj;
	}

	public static JSONObject parseRequestParameter(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		JSONObject jsonObject = new JSONObject();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			jsonObject.put(name, request.getParameter(name));
		}
		return jsonObject;
	}

	public static JSONObject parseRequestStream(HttpServletRequest request) {
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int length = inputStream.read(bytes);
			while (length > 0) {
				outputStream.write(bytes, 0, length);
				length = inputStream.read(bytes);
			}
			// String jsonString = StreamUtils.copyToString(inputStream,
			// Charset.forName(ENCODING));
			String jsonString = new String(outputStream.toByteArray(), ENCODING);
			//log.info(jsonString);
			// JSONObject jsonObject = JSON.parseObject(jsonString);
			return JSON.parseObject(jsonString);
		} catch (Throwable t) {
			Log.supplementExceptionMessage(t);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException t) {
					Log.supplementExceptionMessage(t);
				}
			}
		}
		return null;
	}

	public static <T> T parseRequestStreamByJson(HttpServletRequest request, Class<T> returnClass) {
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int length = inputStream.read(bytes);
			while (length > 0) {
				outputStream.write(bytes, 0, length);
				length = inputStream.read(bytes);
			}
			// String jsonString = StreamUtils.copyToString(inputStream,
			// Charset.forName(ENCODING));
			String jsonString = new String(outputStream.toByteArray(), ENCODING).trim();
			//log.info("[" + jsonString + "]");
			// JSONObject jsonObject = JSON.parseObject(jsonString);
			T obj = JSON.parseObject(jsonString, returnClass);
			return obj;
		} catch (Throwable t) {
			if (t instanceof JSONException) {
				Log.supplementExceptionMessage(t);
			} else {
				Log.supplementExceptionMessage(t);
			}
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException t) {
					Log.supplementExceptionMessage(t);
				}
			}
		}
		return null;
	}

	public static <T> T parseRequestStreamByXml(HttpServletRequest request, Class<T> returnClass) {
		try {
			byte[] bytes = Util.getRequestBytes(request);
			String xmlString = new String(bytes, ENCODING).trim();
			T obj = XmlUtils.parseObject(xmlString, returnClass);
			return obj;
		} catch (Throwable t) {
			Log.supplementExceptionMessage(t);
		}
		return null;
	}

	public static <T> T parseRequestStreamByUrlParam(HttpServletRequest request, Class<T> returnClass) {
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int length = inputStream.read(bytes);
			while (length > 0) {
				outputStream.write(bytes, 0, length);
				length = inputStream.read(bytes);
			}
			// String jsonString = StreamUtils.copyToString(inputStream,
			// Charset.forName(ENCODING));
			String jsonString = new String(outputStream.toByteArray(), ENCODING);
			JSONObject jsonObject = new JSONObject();
			if (!StringUtils.isEmpty(jsonString)) {
				String[] pairs = jsonString.split(URL_PARAM_SEPARATOR);
				for (String pair : pairs) {
					String[] namevalue = pair.split(URL_PARAM_EQUAL);
					if (namevalue.length == 2) {
						jsonObject.put(namevalue[0], namevalue[1]);
					}
				}
			}
			T obj = JSON.parseObject(jsonObject.toJSONString(), returnClass);
			return obj;
		} catch (Throwable t) {
			Log.supplementExceptionMessage(t);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException t) {
					Log.supplementExceptionMessage(t);
				}
			}
		}
		return null;
	}

	public static String getRequestStream(HttpServletRequest request) {
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int length = inputStream.read(bytes);
			while (length > 0) {
				outputStream.write(bytes, 0, length);
				length = inputStream.read(bytes);
			}
			// String jsonString = StreamUtils.copyToString(inputStream,
			// Charset.forName(ENCODING));
			String jsonString = new String(outputStream.toByteArray(), ENCODING);
			return jsonString;
		} catch (Throwable t) {
			Log.supplementExceptionMessage(t);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException t) {
					Log.supplementExceptionMessage(t);
				}
			}
		}
		return "";
	}

	public static byte[] getRequestBytes(HttpServletRequest request) {
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int length = inputStream.read(bytes);
			while (length > 0) {
				outputStream.write(bytes, 0, length);
				length = inputStream.read(bytes);
			}
			return outputStream.toByteArray();
		} catch (Throwable t) {
			Log.supplementExceptionMessage(t);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException t) {
					Log.supplementExceptionMessage(t);
				}
			}
		}
		return new byte[0];
	}

	public static <T> T parseObject(String jsonString, Class<T> returnClass) {
		try {
			if (StringUtils.isEmpty(jsonString)) {
				T obj = returnClass.newInstance();
				return obj;
			}
			T obj = JSON.parseObject(jsonString, returnClass);
			return obj;
		} catch (Throwable t) {
			Log.supplementExceptionMessage(t);
		}
		return null;
	}

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * RSA签名
	 *
	 * @param content
	 *            待签名数�?
	 * @param privateKey
	 *            商户私钥
	 * @param encode
	 *            字符集编�?
	 * @return 签名�?
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	public static String rsaSign(String content, String privateKey, String encode) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
		String charset = CharEncoding.UTF_8;
		if (!StringUtils.isEmpty(encode)) {
			charset = encode;
		}
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(priPKCS8);

		java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
		signature.initSign(priKey);
		signature.update(content.getBytes(charset));
		byte[] signed = signature.sign();
		return Base64.getEncoder().encodeToString(signed);
	}

	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(62);// [0,62)
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 获取对象参数
	 *
	 * @param detailRequest
	 * @return
	 */
	public static Map<String, String> getObjectParam(Object detailRequest) {
		Map<String, String> params = new HashMap<>();
		String jsonString = JSON.toJSONString(detailRequest);
		JSONObject jsonobject = JSON.parseObject(jsonString);
		Set<Entry<String, Object>> entrySet = jsonobject.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			params.put(entry.getKey(), String.valueOf(entry.getValue()));
		}
		return params;
	}

	public static int getCurrentRechargeTableNamePostfix() {
		return (Calendar.getInstance().get(Calendar.MONTH) + 2) % 6;
	}

	public static String utf8LuanFormat(String strString) throws DecoderException, UnsupportedEncodingException {
		if (strString.indexOf("\\u00") >= 0) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			char[] tempChars = new char[2];
			for (int i = 0; i < strString.length(); i++) {
				char ch = strString.charAt(i);
				if (ch == '\\') {
					if (strString.charAt(i + 1) == 'u' && strString.charAt(i + 2) == '0' && strString.charAt(i + 3) == '0') {
						tempChars[0] = strString.charAt(i + 4);
						tempChars[1] = strString.charAt(i + 5);
						baos.write(Hex.decodeHex(tempChars)[0]);
						i += 5;
						continue;
					}
				}
				baos.write((byte) ch);
			}
			return new String(baos.toByteArray(), Util.ENCODING);
		}
		return strString;
	}

	/**
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String postHttps(String url, String params) {
		InputStream urlStream = null;
		BufferedReader reader = null;
		try {
			verifierHostname();
			URLConnection urlCon = (new URL(url)).openConnection();
			urlCon.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(urlCon.getOutputStream(), "utf-8");
			out.write(params);
			// remember to clean up
			out.flush();
			out.close();
			// �?旦发送成功，用以下方法就可以得到服务器的回应�?
			StringBuilder sTotalString = new StringBuilder();
			String sCurrentLine = "";
			urlStream = urlCon.getInputStream();
			reader = new BufferedReader(new InputStreamReader(urlStream));
			while ((sCurrentLine = reader.readLine()) != null) {
				sTotalString.append(sCurrentLine);
			}
			return sTotalString.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (urlStream != null) {
				try {
					urlStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	private static void verifierHostname() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		X509TrustManager xtm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
		sslContext.init(null, xtmArray, new java.security.SecureRandom());
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		HostnameVerifier hnv = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}

	public static String getSigningString(Object request, String signFieldsName) {
		return getSigningString(request, signFieldsName, false, true, false);
	}

	public static String getSigningString(Object request, String signFieldsName, boolean withEmpty) {
		return getSigningString(request, signFieldsName, withEmpty, true, false);
	}

	public static String getSigningString(Object request, String signFieldsName, boolean withEmpty, boolean urlEncodeValue, boolean lowerName) {
		return getSigningString(request, signFieldsName, withEmpty, false, urlEncodeValue, lowerName, false);
	}

	public static String getSigningString(Object request, String signFieldsName, boolean withEmpty, boolean urlEncodeName, boolean urlEncodeValue, boolean lowerName, boolean upperCaseFirstChar) {
		String jsonString = JSON.toJSONString(request);
		JSONObject jsonObject = JSON.parseObject(jsonString);
		if (jsonObject == null || jsonObject.size() == 0) {
			return "";
		}
		if (StringUtils.isNotEmpty(signFieldsName)) {
			for (String signField : signFieldsName.split(",")) {
				jsonObject.remove(signField);
			}
		}
		List<String> keyList = new ArrayList<>(jsonObject.keySet());
		if (keyList.size() == 0) {
			return "";
		}
		Collections.sort(keyList);
		StringBuilder builder = new StringBuilder();
		for (String key : keyList) {
			String value = jsonObject.getString(key);
			if (lowerName) {
				key = StringUtils.lowerCase(key);
			}
			if (!StringUtils.isEmpty(value) && urlEncodeValue) {
				try {
					value = URLEncoder.encode(value, Util.ENCODING).replace("+", "%20");
				} catch (UnsupportedEncodingException e) {
					Log.supplementExceptionMessage(e);
				}
			}
			if (urlEncodeName) {
				try {
					key = URLEncoder.encode(key, Util.ENCODING).replace("+", "%20");
				} catch (UnsupportedEncodingException e) {
					Log.supplementExceptionMessage(e);
				}
			}
			if (upperCaseFirstChar) {
				if (key.length() == 1) {
					key = Character.toUpperCase(key.charAt(0)) + "";
				} else if (key.length() > 1) {
					key = Character.toUpperCase(key.charAt(0)) + key.substring(1);
				}
			}
			if (!StringUtils.isEmpty(value)) {
				builder.append(key).append(REQUEST_EQUAL).append(value).append(REQUEST_SEPARATOR);
			} else if (withEmpty) {
				builder.append(key).append(REQUEST_EQUAL).append("").append(REQUEST_SEPARATOR);
			}
		}
		if (builder.length() > 0) {
			builder.delete(builder.length() - REQUEST_SEPARATOR.length(), builder.length());
		}
		return builder.toString();
	}

	public static String format(final List<? extends NameValuePair> parameters, final String charset, String signFieldName, boolean withEmpty, boolean lowerName, boolean urlEncodeValue) {
		StringBuilder builder = new StringBuilder();
		for (NameValuePair parameter : parameters) {
			String key = parameter.getName();
			if (key.equalsIgnoreCase(signFieldName)) {
				continue;
			}
			if (lowerName) {
				key = key.toLowerCase();
			}
			String value = parameter.getValue();
			if (value != null) {
				value = value.trim();
			}
			if (urlEncodeValue && value != null && value.length() > 0) {
				try {
					value = URLEncoder.encode(value, charset).replace("+", "%20");
				} catch (UnsupportedEncodingException e) {
					Log.supplementExceptionMessage(e);
				}
			}
			if (value != null && value.length() > 0) {
				builder.append(key).append(REQUEST_EQUAL).append(value).append(REQUEST_SEPARATOR);
			} else if (withEmpty) {
				builder.append(key).append(REQUEST_EQUAL).append("").append(REQUEST_SEPARATOR);
			}
		}
		builder.delete(builder.length() - REQUEST_SEPARATOR.length(), builder.length());
		return builder.toString();
	}

	public static JSONObject splitUrlParams(String urlParams) {
		JSONObject returnValue = new JSONObject();
		if (StringUtils.isEmpty(urlParams)) {
			return returnValue;
		}
		String[] params = urlParams.split(Util.REQUEST_SEPARATOR);
		for (String param : params) {
			int index = param.indexOf(Util.REQUEST_EQUAL);
			if (index >= 0) {
				returnValue.put(param.substring(0, index), param.substring(index + Util.REQUEST_EQUAL.length()));
			}
		}
		return returnValue;
	}

	public static String formatChannelId(String originalChannelId) {
		String channelId = originalChannelId;
		// 判断channelId是否有前�?
		String prefix = "ios_";
		if (StringUtils.startsWithIgnoreCase(channelId, prefix)) {
			channelId = channelId.substring(prefix.length());
		}
		prefix = "IOS";
		if (StringUtils.startsWithIgnoreCase(channelId, prefix)) {
			channelId = channelId.substring(prefix.length());
		}
		if (StringUtils.endsWithIgnoreCase(channelId, prefix)) {
			channelId = channelId.substring(0, channelId.length() - prefix.length());
		}
		if (StringUtils.equalsIgnoreCase("weixin", channelId) || StringUtils.equalsIgnoreCase("QQ", channelId)) {
			channelId = "yingyongbao";
		}
		return channelId;
	}

	public static String getXgUid(String channelId, String channelUid) {
		return MessageFormat.format("{0}__{1}", channelId, channelUid);
	}

	public static String getChannelUid(String channelId, String xgUid) {
		if (channelId != null && xgUid != null && xgUid.startsWith(channelId + "__")) {
			return xgUid.substring(channelId.length() + "__".length());
		}
		return xgUid;
	}

	public static String sanitizeResponseText(String response) {
		return response.replaceAll("[\r\n]", "");
	}

	public static String getParameterString(Object request) {
		String jsonString = JSON.toJSONString(request);
		JSONObject jsonObject = JSON.parseObject(jsonString);
		List<String> keyList = new ArrayList<>(jsonObject.keySet());
		Collections.sort(keyList);
		StringBuilder builder = new StringBuilder();
		for (String key : keyList) {
			String value = jsonObject.getString(key);
			if (!StringUtils.isEmpty(value)) {
				try {
					value = URLEncoder.encode(value, Util.ENCODING).replace("+", "%20");
				} catch (UnsupportedEncodingException e) {
					Log.supplementExceptionMessage(e);
				}
			}
			if (!StringUtils.isEmpty(value)) {
				builder.append(key).append(REQUEST_EQUAL).append(value).append(REQUEST_SEPARATOR);
			}
		}
		builder.delete(builder.length() - REQUEST_SEPARATOR.length(), builder.length());
		return builder.toString();
	}

	public static String getRechargeTableNamePostfix(String tradeNo) {
		if (tradeNo == null || tradeNo.length() < 1) {
			return "1";
		}
		char first = tradeNo.charAt(0);

		if (first >= '1' && first <= '9') {
			return String.valueOf(first);
		}
		if (first >= 'a' && first <= 'c') {
			return String.valueOf(first - 'a' + 10);
		}
		if (first >= 'A' && first <= 'C') {
			return String.valueOf(first - 'A' + 10);
		}

		return "1";
	}

	public static boolean containsTarget(String preset, String target) {
		if (StringUtils.isNotEmpty(preset) && StringUtils.contains(preset, target)) {
			return true;
		}
		return false;
	}

}
