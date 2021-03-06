package com.anchorcms.core.dao.impl;

import java.util.List;

import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.core.dao.SiteDao;
import com.anchorcms.core.model.CmsSite;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;


@Repository
public class SiteDaoImpl extends HibernateBaseDao<CmsSite, Integer>
		implements SiteDao {

	public int siteCount(boolean cacheable) {
		String hql = "select count(*) from CmsSite bean";
		return ((Number) getSession().createQuery(hql).setCacheable(cacheable)
				.iterate().next()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<CmsSite> getList(boolean cacheable) {
		String hql = "from CmsSite bean order by bean.siteId asc";
		return getSession().createQuery(hql).setCacheable(cacheable).list();
	}
	
	public int getCountByProperty(String property){
		String hql = "select count(distinct "+property+") from CmsSite bean ";
		Query query = getSession().createQuery(hql);
		return ((Number) query.iterate().next()).intValue();
	}

	public CmsSite findByDomain(String domain) {
		return findUniqueByProperty("domain",domain);
	}

	public CmsSite findById(Integer id) {
		CmsSite entity = get(id);
		return entity;
	}

	public CmsSite save(CmsSite bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsSite deleteById(Integer id) {
		CmsSite entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public CmsSite getByDomain(String domain) {
		String hql = "from CmsSite bean where bean.domain=?";
		return findUniqueByProperty(hql, domain);
	}

	@Override
	protected Class<CmsSite> getEntityClass() {
		return CmsSite.class;
	}
}