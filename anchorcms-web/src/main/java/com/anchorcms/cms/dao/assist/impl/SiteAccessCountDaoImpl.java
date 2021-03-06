package com.anchorcms.cms.dao.assist.impl;

import java.util.Date;
import java.util.List;

import com.anchorcms.cms.dao.assist.SiteAccessCountDao;
import com.anchorcms.cms.model.assist.CmsSiteAccessCount;
import com.anchorcms.common.hibernate.Finder;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import org.springframework.stereotype.Repository;


/**
 * @author Tom
 */
@Repository
public class SiteAccessCountDaoImpl extends
		HibernateBaseDao<CmsSiteAccessCount, Integer> implements
		SiteAccessCountDao {


	@SuppressWarnings("unchecked")
	public List<Object[]> statisticVisitorCountByDate(Integer siteId,Date begin,Date end){
		String hql="select sum(bean.visitors),bean.pageCount from CmsSiteAccessCount bean where bean.site.id=:siteId";
		Finder f= Finder.create(hql).setParam("siteId", siteId);
		if(begin!=null){
			f.append(" and bean.statisticDate>=:begin").setParam("begin", begin);
		}
		if(end!=null){
			f.append(" and bean.statisticDate<=:end").setParam("end", end);
		}
		f.append(" group by  bean.pageCount");
		return find(f);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> statisticVisitorCountByYear(Integer siteId,Integer year) {
		String hql="select sum(bean.visitors),bean.pageCount from CmsSiteAccessCount bean where bean.site.id=:siteId";
		Finder f=Finder.create(hql).setParam("siteId", siteId);
		if(year!=null){
			f.append(" and  year(bean.statisticDate)=:year").setParam("year", year);
		}
		f.append(" group by  bean.pageCount");
		return find(f);
	}
	
	

	public CmsSiteAccessCount save(CmsSiteAccessCount bean) {
		getSession().save(bean);
		return bean;
	}

	protected Class<CmsSiteAccessCount> getEntityClass() {
		return CmsSiteAccessCount.class;
	}

}
