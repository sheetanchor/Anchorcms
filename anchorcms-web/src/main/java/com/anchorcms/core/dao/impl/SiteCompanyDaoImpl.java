package com.anchorcms.core.dao.impl;

import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.core.dao.SiteCompanyDao;
import com.anchorcms.core.model.CmsSiteCompany;
import org.springframework.stereotype.Repository;


@Repository
public class SiteCompanyDaoImpl extends
		HibernateBaseDao<CmsSiteCompany, Integer> implements SiteCompanyDao {

	public CmsSiteCompany save(CmsSiteCompany bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<CmsSiteCompany> getEntityClass() {
		return CmsSiteCompany.class;
	}
}