package com.anchorcms.cms.dao.assist.impl;

import com.anchorcms.cms.dao.assist.GuestbookExtDao;
import com.anchorcms.cms.model.assist.CmsGuestbookExt;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import org.springframework.stereotype.Repository;


@Repository
public class GuestbookExtDaoImpl extends
		HibernateBaseDao<CmsGuestbookExt, Integer> implements
		GuestbookExtDao {

	public CmsGuestbookExt findById(Integer id) {
		CmsGuestbookExt entity = get(id);
		return entity;
	}

	public CmsGuestbookExt save(CmsGuestbookExt bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsGuestbookExt deleteById(Integer id) {
		CmsGuestbookExt entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	@Override
	protected Class<CmsGuestbookExt> getEntityClass() {
		return CmsGuestbookExt.class;
	}
}