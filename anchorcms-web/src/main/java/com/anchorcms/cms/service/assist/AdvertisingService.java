package com.anchorcms.cms.service.assist;

import com.anchorcms.cms.model.assist.CmsAdvertising;
import com.anchorcms.common.page.Pagination;

import java.util.List;
import java.util.Map;


public interface AdvertisingService {
	public Pagination getPage(Integer siteId, Integer adspaceId,
							  Boolean enabled, int pageNo, int pageSize);

	public List<CmsAdvertising> getList(Integer adspaceId, Boolean enabled);

	public CmsAdvertising findById(Integer id);

	public CmsAdvertising save(CmsAdvertising bean, Integer adspaceId,
							   Map<String, String> attr);

	public CmsAdvertising update(CmsAdvertising bean, Integer adspaceId,
								 Map<String, String> attr);

	public CmsAdvertising deleteById(Integer id);

	public CmsAdvertising[] deleteByIds(Integer[] ids);

	public void display(Integer id);

	public void click(Integer id);
}