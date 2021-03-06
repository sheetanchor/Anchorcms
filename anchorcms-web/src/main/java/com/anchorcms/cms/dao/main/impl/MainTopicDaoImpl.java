package com.anchorcms.cms.dao.main.impl;

import java.util.List;

import com.anchorcms.cms.dao.main.MainTopicDao;
import com.anchorcms.cms.model.main.CmsTopic;
import com.anchorcms.common.hibernate.Finder;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.common.page.Pagination;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;


@Repository
public class MainTopicDaoImpl extends HibernateBaseDao<CmsTopic, Integer>
		implements MainTopicDao {
	@SuppressWarnings("unchecked")
	public List<CmsTopic> getList(Integer channelId, boolean recommend,
			Integer count, boolean cacheable) {
		Finder f = Finder.create("select bean from CmsTopic bean ");
		if (channelId != null) {
			f.append(" join bean.channels channel where channel.channelId=:channelId");
			f.setParam("channelId", channelId);
		}else{
			f.append(" where 1=1 ");
		}
		if (recommend) {
			f.append(" and bean.isRecommend=true");
		}
		f.append(" order by bean.priority asc,bean.topicId desc");
		if (count != null) {
			f.setMaxResults(count);
		}
		f.setCacheable(cacheable);
		return find(f);
	}

	public Pagination getPage(Integer channelId, boolean recommend, int pageNo,
							  int pageSize, boolean cacheable) {
		Finder f = Finder.create("select bean from CmsTopic bean ");
		if (channelId != null) {
			f.append(" join bean.channels channel where channel.channelId=:channelId");
			f.setParam("channelId", channelId);
		}else{
			f.append(" where 1=1 ");
		}
		if (recommend) {
			f.append(" and bean.isRecommend=true");
		}
		f.append(" order by bean.priority asc,bean.topicId desc");
		return find(f, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<CmsTopic> getListByChannelIds(Integer[] channelIds) {
		String hql = "select bean from CmsTopic bean join bean.channels channel where channel.channelId in (:ids) order by bean.topicId asc";
		return getSession().createQuery(hql)
				.setParameterList("ids", channelIds).list();
	}

	@SuppressWarnings("unchecked")
	public List<CmsTopic> getListByChannelId(Integer channelId) {
		String hql = "select bean from CmsTopic bean inner join bean.channels as node,Channel parent"
				+ " where node.lft between parent.lft and parent.rgt"
				+ " and parent.channelId=?"
				+ " order by bean.priority asc,bean.topicId desc";
		return find(hql, channelId);
	}

	@SuppressWarnings("unchecked")
	public List<CmsTopic> getGlobalTopicList() {
		String hql = "select bean from CmsTopic bean left join bean.channels channel where  channel is null"
				+ " order by bean.priority asc,bean.topicId desc";
		return find(hql);
	}

	public CmsTopic findById(Integer id) {
		CmsTopic entity = get(id);
		return entity;
	}

	public CmsTopic save(CmsTopic bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsTopic deleteById(Integer id) {
		CmsTopic entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public int deleteContentRef(Integer id) {
		String hql = "DELETE FROM ContentTopic WHERE topicId = ?";
		Query query = getSession().createQuery(hql);
		return query.setParameter(0, id).executeUpdate();
	}

	public int countByChannelId(Integer channelId) {
		String hql = "select count(*) from CmsTopic bean join bean.channels channel"
				+ " where channel.channelId=:channelId";
		Query query = getSession().createQuery(hql);
		query.setParameter("channelId", channelId);
		return ((Number) query.iterate().next()).hashCode();
	}

	@Override
	protected Class<CmsTopic> getEntityClass() {
		return CmsTopic.class;
	}
}