package com.anchorcms.cms.dao.assist;

import com.anchorcms.cms.model.assist.CmsAcquisitionHistory;
import com.anchorcms.common.hibernate.Updater;
import com.anchorcms.common.page.Pagination;

import java.util.List;


public interface AcquisitionHistoryDao {
	public List<CmsAcquisitionHistory> getList(Integer siteId, Integer acquId);

	public Pagination getPage(Integer siteId, Integer acquId, Integer pageNo, Integer pageSize);

	public CmsAcquisitionHistory findById(Integer id);

	public CmsAcquisitionHistory save(CmsAcquisitionHistory bean);

	public CmsAcquisitionHistory updateByUpdater(Updater<CmsAcquisitionHistory> updater);

	public CmsAcquisitionHistory deleteById(Integer id);
	
	public void deleteByAcquisition(Integer acquId);

	public Boolean checkExistByProperties(Boolean title, String value);
}