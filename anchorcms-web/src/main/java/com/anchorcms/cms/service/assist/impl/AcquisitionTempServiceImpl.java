package com.anchorcms.cms.service.assist.impl;

import java.util.List;

import com.anchorcms.cms.dao.assist.AcquisitionTempDao;
import com.anchorcms.cms.model.assist.CmsAcquisitionTemp;
import com.anchorcms.cms.service.assist.AcquisitionTempService;
import com.anchorcms.common.hibernate.Updater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class AcquisitionTempServiceImpl implements AcquisitionTempService {
	@Transactional(readOnly = true)
	public List<CmsAcquisitionTemp> getList(Integer siteId) {
		return dao.getList(siteId);
	}

	@Transactional(readOnly = true)
	public CmsAcquisitionTemp findById(Integer id) {
		CmsAcquisitionTemp entity = dao.findById(id);
		return entity;
	}

	public CmsAcquisitionTemp save(CmsAcquisitionTemp bean) {
		clear(bean.getSite().getSiteId(), bean.getChannelUrl());
		dao.save(bean);
		return bean;
	}

	public CmsAcquisitionTemp update(CmsAcquisitionTemp bean) {
		Updater<CmsAcquisitionTemp> updater = new Updater<CmsAcquisitionTemp>(
				bean);
		bean = dao.updateByUpdater(updater);
		return bean;
	}

	public CmsAcquisitionTemp deleteById(Integer id) {
		CmsAcquisitionTemp bean = dao.deleteById(id);
		return bean;
	}

	public CmsAcquisitionTemp[] deleteByIds(Integer[] ids) {
		CmsAcquisitionTemp[] beans = new CmsAcquisitionTemp[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public Integer getPercent(Integer siteId) {
		return dao.getPercent(siteId);
	}

	public void clear(Integer siteId) {
		dao.clear(siteId, null);
	}

	public void clear(Integer siteId, String channelUrl) {
		dao.clear(siteId, channelUrl);
	}

	private AcquisitionTempDao dao;

	@Autowired
	public void setDao(AcquisitionTempDao dao) {
		this.dao = dao;
	}
}