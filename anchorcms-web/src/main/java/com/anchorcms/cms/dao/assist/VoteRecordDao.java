package com.anchorcms.cms.dao.assist;


import com.anchorcms.cms.model.assist.CmsVoteRecord;

public interface VoteRecordDao {
	public CmsVoteRecord save(CmsVoteRecord bean);

	public int deleteByTopic(Integer topicId);

	public CmsVoteRecord findByUserId(Integer userId, Integer topicId);

	public CmsVoteRecord findByIp(String ip, Integer topicId);

	public CmsVoteRecord findByCookie(String cookie, Integer topicId);
}