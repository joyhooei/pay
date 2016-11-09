package com.vrg.payserver.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrg.payserver.dao.PartnerMapper;
import com.vrg.payserver.dao.model.Partner;


@Service
public class PartnerRespository {
	
	@Autowired
	private PartnerMapper partnerMapper;
	
	public String getSecretKey(String partnerId) {
		Partner partner = partnerMapper.queryByPartnerId(partnerId);
		if (partner == null) {
			return "";
		}
		
		return partner.getSecretKey();
	}
}
