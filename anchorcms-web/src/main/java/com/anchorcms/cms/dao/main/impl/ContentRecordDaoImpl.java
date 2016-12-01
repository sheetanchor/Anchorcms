package com.anchorcms.cms.dao.main.impl;

import java.util.List;

import com.anchorcms.cms.dao.main.ContentRecordDao;
import com.anchorcms.cms.model.main.ContentRecord;
import com.anchorcms.common.hibernate.Finder;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.common.page.Pagination;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;


@Repository
public class ContentRecordDaoImpl extends HibernateBaseDao<ContentRecord, Long> implements ContentRecordDao {
	public Pagination getPage(int pageNo, int pageSize) {
		Criteria crit = createCriteria();
		Pagination page = findByCriteria(crit, pageNo, pageSize);
		return page;
	}

	public List<ContentRecord> getListByContentId(Integer contentId) {
		String hql=" select bean from ContentRecord bean where bean.content.contentId=:contentId";
		Finder f=Finder.create(hql).setParam("contentId", contentId);
		f.setCacheable(true);
		List<ContentRecord>list=find(f);
		return list;
	}

	public ContentRecord findById(Long id) {
		ContentRecord entity = get(id);
		return entity;
	}

	public ContentRecord save(ContentRecord bean) {
		getSession().save(bean);
		return bean;
	}

	public ContentRecord deleteById(Long id) {
		ContentRecord entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@Override
	protected Class<ContentRecord> getEntityClass() {
		return ContentRecord.class;
	}
}