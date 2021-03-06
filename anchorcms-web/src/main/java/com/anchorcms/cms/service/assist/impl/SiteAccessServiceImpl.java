package com.anchorcms.cms.service.assist.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.anchorcms.cms.dao.assist.SiteAccessDao;
import com.anchorcms.cms.model.assist.CmsSiteAccess;
import com.anchorcms.cms.model.assist.CmsSiteAccessStatistic;
import com.anchorcms.cms.service.assist.SiteAccessService;
import com.anchorcms.cms.service.assist.SiteAccessStatisticService;
import com.anchorcms.common.page.Pagination;
import com.anchorcms.core.service.SiteService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.anchorcms.cms.model.assist.CmsSiteAccessStatistic.STATISTIC_ALL;
import static com.anchorcms.cms.model.assist.CmsSiteAccessStatistic.STATISTIC_SOURCE;
import static com.anchorcms.cms.model.assist.CmsSiteAccessStatistic.STATISTIC_ENGINE;
import static com.anchorcms.cms.model.assist.CmsSiteAccessStatistic.STATISTIC_LINK;
import static com.anchorcms.cms.model.assist.CmsSiteAccessStatistic.STATISTIC_KEYWORD;
import static com.anchorcms.cms.model.assist.CmsSiteAccessStatistic.STATISTIC_AREA;

/**
 * @author Tom
 */
@Service
@Transactional
public class SiteAccessServiceImpl implements SiteAccessService {
	public void clearByDate(Date date) {
		dao.clearByDate(date);
	}

	public CmsSiteAccess saveOrUpdate(CmsSiteAccess access) {
		return dao.saveOrUpdate(access);
	}
	
	public Pagination findEnterPages(Integer siteId, Integer orderBy, Integer pageNo, Integer pageSize){
		return dao.findEnterPages(siteId, orderBy, pageNo, pageSize);
	}
	
	public CmsSiteAccess findAccessBySessionId(String sessionId) {
		return dao.findAccessBySessionId(sessionId);
	}
	
	public CmsSiteAccess findRecentAccess(Date  date,Integer siteId){
		return dao.findRecentAccess(date, siteId);
	}
	
	public void statisticByProperty(String property,Date date,Integer siteId){
		List<Object[]> resultes=new ArrayList<Object[]>();
		if(StringUtils.isBlank(property)){
			property=STATISTIC_ALL;
		}
		if(property.equals(STATISTIC_ALL)){
			resultes=dao.statisticByDay(date,siteId);
		}else if(property.equals(STATISTIC_AREA)){
			resultes=dao.statisticByArea(date,siteId);
		}else if(property.equals(STATISTIC_SOURCE)){
			resultes=dao.statisticBySource(date,siteId);
		}else if(property.equals(STATISTIC_ENGINE)){
			resultes=dao.statisticByEngine(date,siteId);
		}else if(property.equals(STATISTIC_LINK)){
			resultes=dao.statisticByLink(date,siteId);
		}else if(property.equals(STATISTIC_KEYWORD)){
			resultes=dao.statisticByKeyword(date,siteId);
		}
		for(Object object[]:resultes){
			CmsSiteAccessStatistic s=new CmsSiteAccessStatistic();
			Integer visitors=((Long)object[2]).intValue();
			s.setSite(siteService.findById(siteId));
			s.setStatisticDate(new java.sql.Date(date.getTime()));
			s.setStatisitcType(property);
			s.setPv(((Long)object[0]).intValue());
			s.setIp(((Long)object[1]).intValue());
			s.setVisitors(visitors);
			s.setPagesAver(((Long)object[0]).intValue()/visitors);
			s.setVisitSecondAver(((Long)object[3]).intValue()/visitors);
			s.setStatisticColumnValue((String)object[4]);
			cmsAccessStatisticMng.save(s);
		}
	}
	
	public List<String> findPropertyValues(String property,Integer siteId){
		return dao.findPropertyValues(property, siteId);
	}
	
	public List<Object[]> statisticToday(Integer siteId,String area){
		return dao.statisticToday(siteId,area);
	}
	
	public List<Object[]> statisticVisitorCount(Date date,Integer siteId){
		return dao.statisticByPageCount(date,siteId);
	}
	
	public List<Object[]> statisticTodayByTarget(Integer siteId,Integer target,String statisticColumn,String statisticValue){
		return dao.statisticTodayByTarget(siteId, target, statisticColumn, statisticValue);
	}

	@Autowired
	private SiteAccessDao dao;
	@Autowired
	private SiteService siteService;
	@Autowired
	private SiteAccessStatisticService cmsAccessStatisticMng;
}
