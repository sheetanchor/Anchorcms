package com.anchorcms.cms.service.assist;

import com.anchorcms.cms.model.assist.CmsSiteAccessPages;
import com.anchorcms.common.page.Pagination;

import java.util.Date;


/**
 * @author Tom
 */
public interface SiteAccessPagesService {

	public CmsSiteAccessPages save(CmsSiteAccessPages access);
	
	public CmsSiteAccessPages update(CmsSiteAccessPages access);

	public CmsSiteAccessPages findAccessPage(String sessionId, Integer pageIndex);
	
	public Pagination findPages(Integer siteId, Integer orderBy, Integer pageNo, Integer pageSize);

	public void clearByDate(Date date);
}
