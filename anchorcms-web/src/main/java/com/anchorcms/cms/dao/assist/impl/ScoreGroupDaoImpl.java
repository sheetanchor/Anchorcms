package com.anchorcms.cms.dao.assist.impl;

import com.anchorcms.cms.dao.assist.ScoreGroupDao;
import com.anchorcms.cms.model.assist.CmsScoreGroup;
import com.anchorcms.common.hibernate.Finder;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.common.page.Pagination;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;


@Repository
public class ScoreGroupDaoImpl extends HibernateBaseDao<CmsScoreGroup, Integer> implements ScoreGroupDao {
	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}

	public CmsScoreGroup findById(Integer id) {
		CmsScoreGroup entity = get(id);
		return entity;
	}
	
	public CmsScoreGroup findDefault(Integer siteId){
		Finder f = Finder.create("from CmsScoreGroup bean where 1=1");
		if (siteId != null) {
			f.append(" and bean.site.id=:siteId");
			f.setParam("siteId", siteId);
		}
		f.append(" and bean.def=true");
		f.setMaxResults(1);
		return (CmsScoreGroup) f.createQuery(getSession()).uniqueResult();
	}

	public CmsScoreGroup save(CmsScoreGroup bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsScoreGroup deleteById(Integer id) {
		CmsScoreGroup entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<CmsScoreGroup> getEntityClass() {
		return CmsScoreGroup.class;
	}
}