package com.anchorcms.cms.dao.assist.impl;

import java.util.List;

import com.anchorcms.cms.dao.assist.GuestbookCtgDao;
import com.anchorcms.cms.model.assist.CmsGuestbookCtg;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import org.springframework.stereotype.Repository;


@Repository
public class GuestbookCtgDaoImpl extends
		HibernateBaseDao<CmsGuestbookCtg, Integer> implements
		GuestbookCtgDao {
	@SuppressWarnings("unchecked")
	public List<CmsGuestbookCtg> getList(Integer siteId) {
		String hql = "from CmsGuestbookCtg bean"
				+ " where bean.site.id=? order by bean.priority asc";
		return find(hql, siteId);
	}

	public CmsGuestbookCtg findById(Integer id) {
		CmsGuestbookCtg entity = get(id);
		return entity;
	}

	public CmsGuestbookCtg save(CmsGuestbookCtg bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsGuestbookCtg deleteById(Integer id) {
		CmsGuestbookCtg entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<CmsGuestbookCtg> getEntityClass() {
		return CmsGuestbookCtg.class;
	}
}