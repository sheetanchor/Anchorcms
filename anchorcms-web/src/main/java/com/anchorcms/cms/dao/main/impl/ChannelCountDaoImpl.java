package com.anchorcms.cms.dao.main.impl;

import java.util.List;

import com.anchorcms.cms.dao.main.ChannelCountDao;
import com.anchorcms.cms.model.main.ChannelCount;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;


@Repository
public class ChannelCountDaoImpl extends
		HibernateBaseDao<ChannelCount, Integer> implements ChannelCountDao {
	@SuppressWarnings("unchecked")
	public int freshCacheToDB(Ehcache cache) {
		List<Integer> keys = cache.getKeys();
		if (keys.size() <= 0) {
			return 0;
		}
		Element e;
		Integer views;
		int i = 0;
		String hql = "update ChannelCount bean"
				+ " set bean.views=bean.views+:views"
				+ ",bean.viewsMonth=bean.viewsMonth+:views"
				+ ",bean.viewsWeek=bean.viewsWeek+:views"
				+ ",bean.viewsDay=bean.viewsDay+:views" + " where bean.channelId=:id";
		Query query = getSession().createQuery(hql);
		for (Integer id : keys) {
			e = cache.get(id);
			if (e != null) {
				views = (Integer) e.getObjectValue();
				if (views != null) {
					query.setParameter("views", views);
					query.setParameter("id", id);
					i += query.executeUpdate();
				}
			}
		}
		return i;
	}

	public int clearCount(boolean week, boolean month) {
		StringBuilder hql = new StringBuilder("update ChannelCount bean");
		hql.append(" set bean.viewsDay=0");
		if (week) {
			hql.append(",bean.viewsWeek=0");
		}
		if (month) {
			hql.append(",bean.viewsMonth=0");
		}
		return getSession().createQuery(hql.toString()).executeUpdate();
	}
	
	public int clearContentCount(boolean day,boolean week, boolean month,boolean year){
		StringBuilder hql = new StringBuilder("update ChannelCount bean set bean.channelId=bean.channelId ");
		if(day){
			hql.append(",bean.contentCountDay=0");
		}
		if (week) {
			hql.append(",bean.contentCountWeek=0");
		}
		if (month) {
			hql.append(",bean.contentCountMonth=0");
		}
		if (year) {
			hql.append(",bean.contentCountYear=0");
		}
		return getSession().createQuery(hql.toString()).executeUpdate();
	}

	public ChannelCount findById(Integer id) {
		ChannelCount entity = get(id);
		return entity;
	}

	public ChannelCount save(ChannelCount bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<ChannelCount> getEntityClass() {
		return ChannelCount.class;
	}
}