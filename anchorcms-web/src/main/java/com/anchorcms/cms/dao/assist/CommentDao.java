package com.anchorcms.cms.dao.assist;

import com.anchorcms.cms.model.assist.CmsComment;
import com.anchorcms.common.hibernate.Updater;
import com.anchorcms.common.page.Pagination;

import java.util.List;


public interface CommentDao {
	public Pagination getPage(Integer siteId, Integer contentId,
							  Integer greaterThen, Boolean checked, Boolean recommend,
							  boolean desc, int pageNo, int pageSize, boolean cacheable);

	public List<CmsComment> getList(Integer siteId, Integer contentId,
									Integer parentId, Integer greaterThen, Boolean checked, Boolean recommend,
									boolean desc, int count, boolean cacheable);

	public CmsComment findById(Integer id);

	public int deleteByContentId(Integer contentId);

	public CmsComment save(CmsComment bean);

	public CmsComment updateByUpdater(Updater<CmsComment> updater);

	public CmsComment deleteById(Integer id);

	public Pagination getPageForMember(Integer siteId, Integer contentId, Integer toUserId, Integer fromUserId,
									   Integer greaterThen, Boolean checked, Boolean recommend,
									   boolean desc, int pageNo, int pageSize, boolean cacheable);

	public List<CmsComment> getListForDel(Integer siteId, Integer userId,
										  Integer commentUserId, String ip);
}