package com.anchorcms.cms.dao.assist.impl;

import java.util.List;

import com.anchorcms.cms.dao.assist.UserMenuDao;
import com.anchorcms.cms.model.assist.CmsUserMenu;
import com.anchorcms.common.hibernate.Finder;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.common.page.Pagination;
import org.springframework.stereotype.Repository;

@Repository
public class UserMenuDaoImpl extends HibernateBaseDao<CmsUserMenu, Integer> implements UserMenuDao {
	public Pagination getPage(Integer userId, int pageNo, int pageSize) {
		String hql="from CmsUserMenu menu ";
		Finder f=Finder.create(hql);
		if(userId!=null){
			f.append(" where menu.user.id=:userId").setParam("userId", userId);
		}
		f.append(" order by menu.priority asc");
		return find(f, pageNo, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	public List<CmsUserMenu> getList(Integer userId,int count){
		String hql="from CmsUserMenu menu ";
		Finder f=Finder.create(hql);
		if(userId!=null){
			f.append(" where menu.user.id=:userId").setParam("userId", userId);
		}
		f.append(" order by menu.priority asc");
		f.setFirstResult(0);
		f.setMaxResults(count);
		return find(f);
	}

	public CmsUserMenu findById(Integer id) {
		CmsUserMenu entity = get(id);
		return entity;
	}

	public CmsUserMenu save(CmsUserMenu bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsUserMenu deleteById(Integer id) {
		CmsUserMenu entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<CmsUserMenu> getEntityClass() {
		return CmsUserMenu.class;
	}
}