package com.anchorcms.cms.dao.assist;

import com.anchorcms.cms.model.assist.CmsMessage;
import com.anchorcms.common.hibernate.Updater;
import com.anchorcms.common.page.Pagination;

import java.util.Date;
public interface MessageDao {

	public Pagination getPage(Integer siteId, Integer sendUserId,
							  Integer receiverUserId, String title, Date sendBeginTime,
							  Date sendEndTime, Boolean status, Integer box, Boolean cacheable,
							  int pageNo, int pageSize);

	public CmsMessage findById(Integer id);

	public CmsMessage save(CmsMessage bean);

	public CmsMessage updateByUpdater(Updater<CmsMessage> updater);
	
	public CmsMessage update(CmsMessage bean);

	public CmsMessage deleteById(Integer id);

	public CmsMessage[] deleteByIds(Integer[] ids);
}