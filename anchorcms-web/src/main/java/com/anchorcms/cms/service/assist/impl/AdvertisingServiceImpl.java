package com.anchorcms.cms.service.assist.impl;

import java.util.List;
import java.util.Map;

import com.anchorcms.cms.dao.assist.AdvertisingDao;
import com.anchorcms.cms.model.assist.CmsAdvertising;
import com.anchorcms.cms.service.assist.AdvertisingService;
import com.anchorcms.cms.service.assist.AdvertisingSpaceService;
import com.anchorcms.common.hibernate.Updater;
import com.anchorcms.common.page.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class AdvertisingServiceImpl implements AdvertisingService {
	@Transactional(readOnly = true)
	public Pagination getPage(Integer siteId, Integer adspaceId,
							  Boolean enabled, int pageNo, int pageSize) {
		Pagination page = dao.getPage(siteId, adspaceId, enabled, pageNo,
				pageSize);
		return page;
	}

	@Transactional(readOnly = true)
	public List<CmsAdvertising> getList(Integer adspaceId, Boolean enabled) {
		return dao.getList(adspaceId, enabled);
	}

	@Transactional(readOnly = true)
	public CmsAdvertising findById(Integer id) {
		CmsAdvertising entity = dao.findById(id);
		return entity;
	}

	public CmsAdvertising save(CmsAdvertising bean, Integer adspaceId,
			Map<String, String> attr) {
		bean.setAdspace(advertisingSpaceService.findById(adspaceId));
		bean.setAttr(attr);
		bean.init();
		dao.save(bean);
		return bean;
	}

	public CmsAdvertising update(CmsAdvertising bean, Integer adspaceId,
			Map<String, String> attr) {
		Updater<CmsAdvertising> updater = new Updater<CmsAdvertising>(bean);
		updater.include(CmsAdvertising.PROP_CODE);
		updater.include(CmsAdvertising.PROP_START_TIME);
		updater.include(CmsAdvertising.PROP_END_TIME);
		bean = dao.updateByUpdater(updater);
		bean.setAdspace(advertisingSpaceService.findById(adspaceId));
		bean.getAttr().clear();
		bean.getAttr().putAll(attr);
		return bean;
	}

	public CmsAdvertising deleteById(Integer id) {
		CmsAdvertising bean = dao.deleteById(id);
		return bean;
	}

	public CmsAdvertising[] deleteByIds(Integer[] ids) {
		CmsAdvertising[] beans = new CmsAdvertising[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public void display(Integer id) {
		CmsAdvertising ad = findById(id);
		if (ad != null) {
			ad.setDisplayCount(ad.getDisplayCount() + 1);
		}
	}

	public void click(Integer id) {
		CmsAdvertising ad = findById(id);
		if (ad != null) {
			ad.setClickCount(ad.getClickCount() + 1);
		}
	}
	@Resource
	private AdvertisingSpaceService advertisingSpaceService;
	@Resource
	private AdvertisingDao dao;
}