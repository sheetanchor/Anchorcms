package com.anchorcms.cms.dao.main.impl;

import com.anchorcms.cms.dao.main.WebserviceAuthDao;
import com.anchorcms.cms.model.assist.CmsWebserviceAuth;
import com.anchorcms.common.hibernate.Finder;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.common.page.Pagination;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WebserviceAuthDaoImpl extends HibernateBaseDao<CmsWebserviceAuth, Integer> implements WebserviceAuthDao {
	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}
	
	@SuppressWarnings("unchecked")
	public CmsWebserviceAuth findByUsername(String username){
		String hql="from CmsWebserviceAuth bean where bean.username=:username";
		Finder f= Finder.create(hql).setParam("username", username);
		f.setCacheable(true);
		List<CmsWebserviceAuth> list=find(f);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	public CmsWebserviceAuth findById(Integer id) {
		CmsWebserviceAuth entity = get(id);
		return entity;
	}

	public CmsWebserviceAuth save(CmsWebserviceAuth bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsWebserviceAuth deleteById(Integer id) {
		CmsWebserviceAuth entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<CmsWebserviceAuth> getEntityClass() {
		return CmsWebserviceAuth.class;
	}
}