package com.anchorcms.cms.dao.main.impl;

import java.util.List;

import com.anchorcms.cms.dao.main.ThirdAccountDao;
import com.anchorcms.cms.model.main.CmsThirdAccount;
import com.anchorcms.common.hibernate.Finder;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.common.page.Pagination;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;


@Repository
public class ThirdAccountDaoImpl extends HibernateBaseDao<CmsThirdAccount, Long> implements ThirdAccountDao {
	public Pagination getPage(String username, String source, int pageNo, int pageSize) {
		String hql="from CmsThirdAccount bean where 1=1 ";
		Finder f=Finder.create(hql);
		if(StringUtils.isNotBlank(username)){
			f.append(" and bean.username like :username").setParam("username", "%"+username+"%");
		}
		if(StringUtils.isNotBlank(source)){
			f.append(" and bean.source=:source").setParam("source", source);
		}
		return find(f, pageNo, pageSize);
	}

	public CmsThirdAccount findById(Long id) {
		CmsThirdAccount entity = get(id);
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public CmsThirdAccount findByKey(String key){
		String hql="from CmsThirdAccount bean where bean.accountKey=:accountKey";
		Finder f=Finder.create(hql).setParam("accountKey", key);
		List<CmsThirdAccount>li= find(f);
		if(li.size()>0){
			return li.get(0);
		}else{
			return null;
		}
	}

	public CmsThirdAccount save(CmsThirdAccount bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsThirdAccount deleteById(Long id) {
		CmsThirdAccount entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<CmsThirdAccount> getEntityClass() {
		return CmsThirdAccount.class;
	}
}