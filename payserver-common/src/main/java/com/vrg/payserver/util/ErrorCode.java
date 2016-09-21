package com.vrg.payserver.util;

/**
 * @author LUCHUNLIANG
 *
 */
public interface ErrorCode {
  // 0 成功
  String SUCCESS = "0";
  // 1 请求重发
  String ERR_RESEND = "1";
  // 2 重复订单
  String ERR_REPEAT = "2";
  // 3 渠道通知会话返回
  String ERR_CHANNEL_SESSION_NOTIFY = "3";
  // 4 拒绝重发
  String ERR_GAME_REJECT = "4";
  // 5 token已满，无法获�?
  String ERR_TOKEN_OVER = "5";

  // -1 签名失败
  String ERR_SIGN = "-1";
  // -2 xgAppId不存�?
  String ERR_SDKAPPID_NOTEXIST = "-2";
  // -3 channelId不存�?
  String ERR_CHANNELID_NOTEXIST = "-3";
  // -4 区服不存�?
  String ERR_SERVER_NOTEXIST = "-4";
  // -5 账号不存�?
  String ERR_UID_NOTEXIST = "-5";
  // -6 订单不存�?
  String ERR_ORDERID_NOTEXIST = "-6";
  // -7 渠道编号和xgAppId不相�?
  String ERR_CHANNEL_ID_XG_APPID_NOTSAME = "-7";
  // -8 planId不存�?
  String ERR_PLANID_NOTEXIST = "-8";
  // -9 paidAmount小于等于0
  String ERR_PAIDAMOUNT = "-9";
  // -10 游戏已经停服
  String ERR_GAME_SERVER_OFFLINE = "-10";

  // -40 渠道参数为配置或配置错误
  String ERR_CHANNEL_PARAM = "-40";

  // -98 系统被攻�?
  String ERR_EXCEPTION = "-98";
  // -99 系统错误
  String ERR_SYSTEM = "-99";

  // -100 获取登录验证参数失败
  String ERR_GET_LOGINPARAM = "-100";
  // -101 获取渠道参数失败
  String ERR_GET_CHANNELPARAM = "-101";
  // -102 连接渠道登陆验证接口失败
  String ERR_HTTP_CHANNELLOGIN = "-102";
  // -103 渠道登陆验证结果失败
  String ERR_CHANNELOGIN_FAIL = "-103";

  // -200 前后渠道订单号不�?�?
  String ERR_CHANNEL_TRADE_NO_NOTSAME = "-200";
  // -201 商品不一�?
  String ERR_PRODUCTID_NOTSAME = "-201";
  // -202 金额不一�?
  String ERR_REALVALUE_NOTSAME = "-202";
  // -203 渠道验证订单失败
  String ERR_CHANNELVERIFY_FAIL = "-203";
  // -204 渠道分配的游戏编号不�?�?
  String ERR_CHANNEL_APP_ID_NOTSAME = "-204";
  // -205 渠道分配的用户编号不�?�?
  String ERR_UID_NOTSAME = "-205";
  // -206 订单支付时间，和xg订单中的创建时间相差不能超过�?�?
  String ERR_PAID_TIME_OUT = "-206";
  // -207 商品编号不一�?
  String ERR_PRODUCT_ID_NOTSAME = "-207";
  // -208 商品名称不一�?
  String ERR_PRODUCT_NAME_NOTSAME = "-208";
  // -209 商品数量不一�?
  String ERR_PRODUCT_QUANTITY_NOTSAME = "-209";
  // -210 支付金额不一�?
  String ERR_PAID_AMOUNT_NOTSAME = "-210";
  // -211 创建渠道订单失败
  String ERR_CREATE_CHANNEL_ORDER = "-211";
  // -212 解析渠道通知参数失败
  String ERR_PARSE_PAY_NOTICE = "-212";
  // -213 游戏商品与金额不�?�?
  String ERR_PAIDAMOUNT_PRODUCT_NOTSAME = "-213";
  // -214 不支持的支付币种
  String ERR_NOT_ALLOWED_CURRENCY = "-214";
  // -215 通知来自黑名单IP
  String ERR_BLACKLIST_IP = "-215";
  // -216 请求无效（给客户端上报支付凭证，去渠道查询无效时使用�?
  String ERR_BAD_REQUEST = "-216";
  // -217 用于后台自动清理超过�?段时间的订单recharge_fail_log的status_code
  String ERR_ORDER_OVERTIME = "-217";

  // -301 查询渠道订单超时
  String ERR_SEARCH_CHANNEL_ORDER_TIMEOUT = "-301";
  // -302 查询渠道订单失败
  String ERR_SEARCH_CHANNEL_ORDER_FAIL = "-302";
  // -401渠道创建订单失败
  String ERR_CREATE_CHANNEL_ORDER_FAIL = "-401";
  // -501通知游戏的url为空
  String ERR_NOTIFY_GAME_URL_EMPTY = "-501";
  // -502通知游戏超时没有响应
  String ERR_NOTIFY_GAME_OVERTIME = "-502";
  // -503通知游戏返回值不能解�?
  String ERR_NOTIFY_GAME_RESPONSE_ERROR = "-503";
  // -504通知游戏返回失败
  String ERR_NOTIFY_GAME_RESPONSE_FAIL = "-504";

  String ERR_GAME_SERVER_OFFLINE_MSG = "the game server is offline";
  String ERR_SIGN_MSG = "The signature is not matched";
  String ERR_ORDERID_NOTEXIST_MSG = "The order not exists";
  String ERR_UID_NOTSAME_MSG = "The uid not match";
  String MSG_OK = "success";

  public static boolean isPayNoticeResponseSuccess(String stateCode) {
    return ErrorCode.SUCCESS.equals(stateCode) || ErrorCode.ERR_REPEAT.equals(stateCode);
  }

}
