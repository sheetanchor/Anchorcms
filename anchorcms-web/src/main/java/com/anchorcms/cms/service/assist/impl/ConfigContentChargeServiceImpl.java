package com.anchorcms.cms.service.assist.impl;

import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;

import com.anchorcms.cms.dao.assist.ConfigContentChargeDao;
import com.anchorcms.cms.model.assist.CmsConfigContentCharge;
import com.anchorcms.cms.service.assist.ConfigContentChargeService;
import com.anchorcms.common.hibernate.Updater;
import com.anchorcms.common.security.encoder.Md5PwdEncoder;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class ConfigContentChargeServiceImpl implements ConfigContentChargeService {
	
	@Transactional(readOnly = true)
	public CmsConfigContentCharge findById(Integer id) {
		CmsConfigContentCharge entity = dao.findById(id);
		return entity;
	}
	
	public CmsConfigContentCharge getDefault() {
		return findById(1);
	}

	public CmsConfigContentCharge update(CmsConfigContentCharge bean,
			String payTransferPassword,Map<String,String> keys) {
		Updater<CmsConfigContentCharge> updater = new Updater<CmsConfigContentCharge>(bean);
		for(Entry<String, String> att:keys.entrySet()){
			if(StringUtils.isBlank(att.getValue())){
				updater.exclude(att.getKey());
			}
		}
		if(StringUtils.isBlank(payTransferPassword)){
			updater.exclude("payTransferPassword");
		}else{
			bean.setPayTransferPassword(pwdEncoder.encodePassword(payTransferPassword));;
		}
		bean = dao.updateByUpdater(updater);
		return bean;
	}
	
	public CmsConfigContentCharge afterUserPay(Double payAmout){
		CmsConfigContentCharge config=getDefault();
		Calendar curr = Calendar.getInstance();
		Calendar last = Calendar.getInstance();
		if(config.getLastBuyTime()!=null){
			last.setTime(config.getLastBuyTime());
			int currDay = curr.get(Calendar.DAY_OF_YEAR);
			int lastDay = last.get(Calendar.DAY_OF_YEAR);
			int currYear=curr.get(Calendar.YEAR);
			int lastYear=last.get(Calendar.YEAR);
			int currMonth = curr.get(Calendar.MONTH);
			int lastMonth = last.get(Calendar.MONTH);
			if(lastYear!=currYear){
				config.setCommissionYear(0d);
				config.setCommissionMonth(0d);
				config.setCommissionDay(0d);
			}else{
				if(currMonth!=lastMonth){
					config.setCommissionMonth(0d);
					config.setCommissionDay(0d);
				}else{
					if (currDay != lastDay) {
						config.setCommissionDay(0d);
					}
				}
			}
		}
		config.setCommissionDay(config.getCommissionDay()+payAmout);
		config.setCommissionMonth(config.getCommissionMonth()+payAmout);
		config.setCommissionYear(config.getCommissionYear()+payAmout);
		config.setCommissionTotal(config.getCommissionTotal()+payAmout);
		config.setLastBuyTime(curr.getTime());
		return config;
	}

	@Resource
	private ConfigContentChargeDao dao;
	@Resource
	private Md5PwdEncoder pwdEncoder;

}