package com.anchorcms.cms.service.main;

import com.anchorcms.cms.model.main.CmsModelItem;

import java.util.List;

public interface ModelItemService {
	public List<CmsModelItem> getList(Integer modelId, boolean isChannel,
									  boolean hasDisabled);

	public CmsModelItem findById(Integer id);

	public CmsModelItem save(CmsModelItem bean);

	public CmsModelItem save(CmsModelItem bean, Integer modelId);

	public void saveList(List<CmsModelItem> list);

	public void updatePriority(Integer[] wids, Integer[] priority,
							   String[] label, Boolean[] single, Boolean[] display);

	public CmsModelItem update(CmsModelItem bean);

	public CmsModelItem deleteById(Integer id);

	public CmsModelItem[] deleteByIds(Integer[] ids);
}