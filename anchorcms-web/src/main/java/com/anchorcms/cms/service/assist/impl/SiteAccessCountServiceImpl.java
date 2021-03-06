package com.anchorcms.cms.service.assist.impl;

import java.util.Date;
import java.util.List;

import com.anchorcms.cms.dao.assist.SiteAccessCountDao;
import com.anchorcms.cms.dao.assist.SiteAccessDao;
import com.anchorcms.cms.model.assist.CmsSiteAccessCount;
import com.anchorcms.cms.service.assist.SiteAccessCountService;
import com.anchorcms.core.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Tom
 */
@Service
@Transactional
public class SiteAccessCountServiceImpl implements SiteAccessCountService {

	public List<Object[]> statisticVisitorCountByDate(Integer siteId,Date begin, Date end) {
		return dao.statisticVisitorCountByDate(siteId, begin, end);
	}

	public List<Object[]> statisticVisitorCountByYear(Integer siteId,Integer year) {
		return dao.statisticVisitorCountByYear(siteId, year);
	}

	public CmsSiteAccessCount save(CmsSiteAccessCount count) {
		return dao.save(count);
	}

	public void statisticCount(Date date, Integer siteId) {
		List<Object[]> pageCounts = cmsAccessDao.statisticByPageCount(date,siteId);
		for (Object[] pageCount : pageCounts) {
			CmsSiteAccessCount bean = new CmsSiteAccessCount();
			bean.setSite(siteMng.findById(siteId));
			bean.setStatisticDate(new java.sql.Date(date.getTime()));
			Long visitors = (Long) pageCount[0];
			bean.setVisitors(visitors.intValue());
			bean.setPageCount((Integer) pageCount[1]);
			save(bean);
		}
	}

	@Autowired
	private SiteAccessCountDao dao;
	@Autowired
	private SiteAccessDao cmsAccessDao;
	@Autowired
	private SiteService siteMng;

}
