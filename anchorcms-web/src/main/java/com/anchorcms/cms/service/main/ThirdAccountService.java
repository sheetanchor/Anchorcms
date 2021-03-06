package com.anchorcms.cms.service.main;


import com.anchorcms.cms.model.main.CmsThirdAccount;
import com.anchorcms.common.page.Pagination;

public interface ThirdAccountService {
	public Pagination getPage(String username, String source, int pageNo, int pageSize);

	public CmsThirdAccount findById(Long id);
	
	public CmsThirdAccount findByKey(String key);

	public CmsThirdAccount save(CmsThirdAccount bean);

	public CmsThirdAccount update(CmsThirdAccount bean);

	public CmsThirdAccount deleteById(Long id);
	
	public CmsThirdAccount[] deleteByIds(Long[] ids);
}