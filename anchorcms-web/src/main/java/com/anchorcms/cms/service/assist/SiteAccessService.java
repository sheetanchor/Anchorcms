package com.anchorcms.cms.service.assist;

import com.anchorcms.cms.model.assist.CmsSiteAccess;
import com.anchorcms.common.page.Pagination;

import java.util.Date;
import java.util.List;


/**
 * @author Tom
 */
public interface SiteAccessService {
	public CmsSiteAccess saveOrUpdate(CmsSiteAccess access);
	
	public Pagination findEnterPages(Integer siteId, Integer orderBy, Integer pageNo, Integer pageSize);

	public CmsSiteAccess findAccessBySessionId(String sessionId);
	/**
	 * 查询date之前最近的访问记录
	 * @param date
	 * @return
	 */
	public CmsSiteAccess findRecentAccess(Date date, Integer siteId);
	
	/**
	 * 统计站点流量数据
	 * @param property
	 * @param date
	 * @param siteId
	 */
	public void statisticByProperty(String property, Date date, Integer siteId);
	
	public List<String> findPropertyValues(String property, Integer siteId);
	
	public List<Object[]> statisticToday(Integer siteId, String area);
	
	public List<Object[]> statisticVisitorCount(Date date, Integer siteId);
	
	public List<Object[]> statisticTodayByTarget(Integer siteId, Integer target, String statisticColumn, String statisticValue);

	public void clearByDate(Date date);
}
