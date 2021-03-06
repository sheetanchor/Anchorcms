package com.anchorcms.cms.service.assist;

import com.anchorcms.cms.model.assist.CmsUserMenu;
import com.anchorcms.common.page.Pagination;

import java.util.List;

public interface UserMenuService {
	public Pagination getPage(Integer userId, int pageNo, int pageSize);
	
	public List<CmsUserMenu> getList(Integer userId, int cout);

	public CmsUserMenu findById(Integer id);

	public CmsUserMenu save(CmsUserMenu bean);

	public CmsUserMenu update(CmsUserMenu bean);

	public CmsUserMenu deleteById(Integer id);
	
	public CmsUserMenu[] deleteByIds(Integer[] ids);
}