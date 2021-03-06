package com.anchorcms.cms.service.assist.impl;

import java.util.List;

import com.anchorcms.cms.dao.assist.UserMenuDao;
import com.anchorcms.cms.model.assist.CmsUserMenu;
import com.anchorcms.cms.service.assist.UserMenuService;
import com.anchorcms.common.hibernate.Updater;
import com.anchorcms.common.page.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserMenuServiceImpl implements UserMenuService {
	@Transactional(readOnly = true)
	public Pagination getPage(Integer userId, int pageNo, int pageSize) {
		Pagination page = dao.getPage(userId,pageNo, pageSize);
		return page;
	}
	
	@Transactional(readOnly = true)
	public List<CmsUserMenu> getList(Integer userId, int count){
		return dao.getList(userId,count);
	}

	@Transactional(readOnly = true)
	public CmsUserMenu findById(Integer id) {
		CmsUserMenu entity = dao.findById(id);
		return entity;
	}

	public CmsUserMenu save(CmsUserMenu bean) {
		dao.save(bean);
		return bean;
	}

	public CmsUserMenu update(CmsUserMenu bean) {
		Updater<CmsUserMenu> updater = new Updater<CmsUserMenu>(bean);
		bean = dao.updateByUpdater(updater);
		return bean;
	}

	public CmsUserMenu deleteById(Integer id) {
		CmsUserMenu bean = dao.deleteById(id);
		return bean;
	}
	
	public CmsUserMenu[] deleteByIds(Integer[] ids) {
		CmsUserMenu[] beans = new CmsUserMenu[ids.length];
		for (int i = 0,len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	private UserMenuDao dao;

	@Autowired
	public void setDao(UserMenuDao dao) {
		this.dao = dao;
	}
}