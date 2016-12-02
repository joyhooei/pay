package com.vrg.payserver;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrg.payserver.dao.RechargeRecordStatusMapper;
import com.vrg.payserver.dao.model.RechargeClientNotifyRequest;
import com.vrg.payserver.repository.ChannelRepository;
import com.vrg.payserver.service.IChannel;
import com.vrg.payserver.service.ParamRepository;
import com.vrg.payserver.service.vo.RechargeRecordBase;
import com.vrg.payserver.service.vo.VerifyChannelOrderRequest;
import com.vrg.payserver.service.vo.VerifyChannelOrderResponse;
import com.vrg.payserver.util.ErrorCode;
import com.vrg.payserver.util.Log;

@Service
public class ChannelService {

	@Autowired
	private RechargeRecordStatusMapper rechargeRecordStatusMapper;
	
	@Autowired
	private ParamRepository paramRepository;
	
	@Autowired
	private ChannelRepository channelRepository;
	
	@Autowired
	private ServerCoreService serverCoreService;

	public boolean searchChannelOrder(String tradeNo) {
		if (StringUtils.isEmpty(tradeNo)) {
			return false;
		}
		RechargeRecordBase record = rechargeRecordStatusMapper.queryByTradeNo(tradeNo);
		if (record == null) {
			return true;
		}
		this.searchChannelOrder(record);
		return true;
	}

	public void searchChannelOrderForClientNotifyRecharge(RechargeClientNotifyRequest clientRequest) {
		try {
			Log.supplementMessage(MessageFormat.format("Start to search order by [{0}] on channel[{1}].", clientRequest.getBizKey(), clientRequest.getChannelId()));
			String channelId = clientRequest.getChannelId();
			IChannel channelImpl = channelRepository.getChannelImpl(channelId);
			if (channelImpl == null) {
				Log.supplementMessage(MessageFormat.format("Failed to search order by [{0}] on channel[{1}], reason: can't find the channel impl of [{1}]", clientRequest.getBizKey(), clientRequest.getChannelId()));
				return;
			}
			VerifyChannelOrderRequest request = new VerifyChannelOrderRequest();
			request.setChannelId(channelId);
			request.setUid(clientRequest.getUid());
			request.setXgAppId(clientRequest.getXgAppId());
			request.setPlanId(clientRequest.getPlanId());
			request.setCustomInfo(clientRequest.getRequestValue1() + StringUtils.defaultString(clientRequest.getRequestValue2()));
			VerifyChannelOrderResponse response = channelImpl.queryRechargeResult(request);
			
			if (ErrorCode.ERR_BAD_REQUEST.equals(response.getCode())) {
//				rechargeClientNotifyRequestMapper.delete(clientRequest);
				return;
			}
//			rechargeClientNotifyRequestMapper.increaseChannelSearchTime(clientRequest);
		} catch (Throwable t) {
			Log.supplementExceptionMessage(MessageFormat.format("Error on search order by [{0}] on channel[{1}], reason: exception encountered.", clientRequest.getBizKey(), clientRequest.getChannelId()), t);
		}
	}

	public void searchChannelOrder(RechargeRecordBase record) {
		VerifyChannelOrderRequest request = null;
		VerifyChannelOrderResponse response = null;
		// Date requestTime = new Date();
		try {
			String partnerId = record.getPartnerId();
			String channelId = record.getChannelId();
			Log.supplementBizInfo(partnerId, channelId, null, null, null, null, null);
			// 获取渠道实现
			IChannel channelImpl = channelRepository.getChannelImpl(channelId);
			if (channelImpl == null) {
				this.increaseTimes(record, null, 30);
				return;
			}

			// 调用渠道实现
			channelImpl.setServerCoreService(serverCoreService);
			request = this.getVerifyChannelOrderRequest(record);
			response = channelImpl.queryRechargeResult(request);

			// 如果渠道不支持主动查询渠道订单
			if (response == null) {
				this.increaseTimes(record, null, 30);
				return;
			}

			// 如果验证异常
			if (StringUtils.equalsIgnoreCase(ErrorCode.ERR_EXCEPTION, response.getCode())) {
				record.setStatus(RechargeRecordBase.STATUS_EXCEPTION);
				record.setStateCode(response.getCode());
				record.setExceptionInfoTrimedWhenExtendLength(response.getMsg());
				rechargeRecordStatusMapper.update(record);
				Log.supplementMessage(MessageFormat.format("end to search the order[{0}] of partnerId[{1}] from channel[{2}] server. The result is [{3}]", record.getTradeNo(), record.getPartnerId(), record.getChannelId(), response));
				return;
			}
			// 如果验证不成功
			if (!StringUtils.equalsIgnoreCase(ErrorCode.SUCCESS, response.getCode())) {
				this.increaseTimes(record, response, 1);
				Log.supplementMessage(MessageFormat.format("end to search the order[{0}] of product[{1}] from channel[{2}] server. The result is [{3}]", record.getTradeNo(), record.getPartnerId(), record.getChannelId(), response));
				return;
			}
			this.setRechargeRecordBase(response, record);
			// 支付成功
			if (StringUtils.equalsIgnoreCase(response.getPayStatus(), VerifyChannelOrderResponse.PAY_STATUS_SUCCESS)) {
				record.setStatus(RechargeRecordBase.STATUS_NOTIFY_SUCCESS);
				Log.enterStep("更新订单状态为1");
				rechargeRecordStatusMapper.update(record);
				Log.enterStep("通知游戏");
			} else {
				// 支付失败不做处理，避免订单在渠道还在处理中，当主动查询先于渠道通知造成异常处理
				Log.enterStep("增加SearchChannelOrderTimes");
				this.increaseTimes(record, response, 1);
				// //支付失败
				 record.setStatus(RechargeRecordBase.STATUS_NOTIFY_FAIL);
				 rechargeRecordStatusMapper.update(record);
				 serverCoreService.onPayFail(record);
			}
			Log.supplementMessage(MessageFormat.format("end to search the order[{0}] of partner[{1}] from channel[{2}] server. The result is [{3}]", record.getTradeNo(), record.getPartnerId(), record.getChannelId(), response));
			return;
		} catch (Throwable t) {
			String msg = MessageFormat.format("error in search the order[{0}] of product[{1}] from channel[{2}] server. The error is [{3}].", record.getTradeNo(), record.getPartnerId(), record.getChannelId(), t.getMessage());
			Log.supplementMessage(msg);
			Log.supplementExceptionMessage(t);
			record.setSearchChannelOrderTimes(record.getSearchChannelOrderTimes() + 1);
			rechargeRecordStatusMapper.update(record);
		}
	}

	private void increaseTimes(RechargeRecordBase record, VerifyChannelOrderResponse response, int addTime) {
		record.setSearchChannelOrderTimes(record.getSearchChannelOrderTimes() + addTime);
		rechargeRecordStatusMapper.update(record);
	}

	private VerifyChannelOrderRequest getVerifyChannelOrderRequest(RechargeRecordBase record) {
		VerifyChannelOrderRequest request = new VerifyChannelOrderRequest();
//		request.setBuildNumber(record.getBuildNumber());
		request.setChannelAppId(record.getPartnerId());
		request.setChannelId(record.getChannelId());
		request.setChannelTradeNo(record.getChannelTradeNo());
		request.setCustomInfo(record.getCustomInfo());
		request.setDeviceId(record.getDeviceId());
		request.setPaidAmount(record.getPaidAmount());
		request.setTradeNo(record.getTradeNo());
		request.setUid(record.getUid());
		request.setCreateTime(record.getCreateTime());
		return request;
	}

	private void setRechargeRecordBase(VerifyChannelOrderResponse response, RechargeRecordBase record) {
		if (!StringUtils.isEmpty(response.getChannelTradeNo())) {
			record.setChannelTradeNo(response.getChannelTradeNo());
		}
		if (!StringUtils.isEmpty(response.getUid())) {
			record.setUid(response.getUid());
		}
		if (response.getPaidAmount() > 0) {
			record.setPaidAmount(response.getPaidAmount());
		}
		if (response.getPaidTime() != null) {
			record.setPaidTime(response.getPaidTime());
		}
		if (!StringUtils.isEmpty(response.getCustomInfo())) {
			record.setCustomInfo(response.getCustomInfo());
		}
	}
}
