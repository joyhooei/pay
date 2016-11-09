package com.vrg.payserver.dao;

import com.vrg.payserver.dao.model.Partner;

public interface PartnerMapper {
	Partner queryByPartnerId(String partnerId);
}
