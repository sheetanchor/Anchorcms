package com.anchorcms.cms.service.assist.impl;

import java.util.List;

import com.anchorcms.cms.dao.assist.FriendlinkDao;
import com.anchorcms.cms.model.assist.CmsFriendlink;
import com.anchorcms.cms.service.assist.FriendlinkCtgService;
import com.anchorcms.cms.service.assist.FriendlinkService;
import com.anchorcms.common.hibernate.Updater;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class FriendlinkServiceImpl implements FriendlinkService {
	@Transactional(readOnly = true)
	public List<CmsFriendlink> getList(Integer siteId, Integer ctgId,
									   Boolean enabled) {
		List<CmsFriendlink> list = dao.getList(siteId, ctgId, enabled);
		return list;
	}

	@Transactional(readOnly = true)
	public int countByCtgId(Integer ctgId) {
		return dao.countByCtgId(ctgId);
	}

	@Transactional(readOnly = true)
	public CmsFriendlink findById(Integer id) {
		CmsFriendlink entity = dao.findById(id);
		return entity;
	}

	public int updateViews(Integer id) {
		CmsFriendlink link = findById(id);
		if (link != null) {
			link.setViews(link.getViews() + 1);
		}
		return link.getViews();
	}

	public CmsFriendlink save(CmsFriendlink bean, Integer ctgId) {
		bean.setCategory(friendlinkCtgService.findById(ctgId));
		bean.init();
		dao.save(bean);
		return bean;
	}

	public CmsFriendlink update(CmsFriendlink bean, Integer ctgId) {
		Updater<CmsFriendlink> updater = new Updater<CmsFriendlink>(bean);
		bean = dao.updateByUpdater(updater);
		if (ctgId != null) {
			bean.setCategory(friendlinkCtgService.findById(ctgId));
		}
		bean.blankToNull();
		return bean;
	}

	public void updatePriority(Integer[] ids, Integer[] priorities) {
		if (ids == null || priorities == null || ids.length <= 0
				|| ids.length != priorities.length) {
			return;
		}
		CmsFriendlink link;
		for (int i = 0, len = ids.length; i < len; i++) {
			link = findById(ids[i]);
			link.setPriority(priorities[i]);
		}

	}

	public CmsFriendlink deleteById(Integer id) {
		CmsFriendlink bean = dao.deleteById(id);
		return bean;
	}

	public CmsFriendlink[] deleteByIds(Integer[] ids) {
		CmsFriendlink[] beans = new CmsFriendlink[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	@Resource
	private FriendlinkDao dao;
	@Resource
	private FriendlinkCtgService friendlinkCtgService;
}