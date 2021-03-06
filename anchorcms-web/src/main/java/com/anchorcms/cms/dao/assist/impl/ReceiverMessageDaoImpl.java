package com.anchorcms.cms.dao.assist.impl;

import java.util.Date;
import java.util.List;

import com.anchorcms.cms.dao.assist.ReceiverMessageDao;
import com.anchorcms.cms.model.assist.CmsReceiverMessage;
import com.anchorcms.common.hibernate.Finder;
import com.anchorcms.common.hibernate.HibernateBaseDao;
import com.anchorcms.common.page.Pagination;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ReceiverMessageDaoImpl extends
		HibernateBaseDao<CmsReceiverMessage, Integer> implements
		ReceiverMessageDao {

	public Pagination getPage(Integer siteId, Integer sendUserId,
							  Integer receiverUserId, String title, Date sendBeginTime,
							  Date sendEndTime, Boolean status, Integer box, Boolean cacheable,
							  int pageNo, int pageSize) {
		String hql = " select msg from CmsReceiverMessage msg where 1=1 ";
		Finder finder = Finder.create(hql);
		if (siteId != null) {
			finder.append(" and msg.site.siteId=:siteId")
					.setParam("siteId", siteId);
		}
		// 垃圾箱
		if (sendUserId != null && receiverUserId != null) {
			finder
					.append(
							" and ((msg.msgReceiverUser=:receiverUserId  and msg.msgBox =:box) or (msg.msgSendUser=:sendUserId  and msg.msgBox =:box) )")
					.setParam("sendUserId", sendUserId).setParam(
							"receiverUserId", receiverUserId).setParam("box",
							box);
		} else {
			if (sendUserId != null) {
				finder.append(" and msg.msgSendUser=:sendUserId").setParam(
						"sendUserId", sendUserId);
			}
			if (receiverUserId != null) {
				finder.append(" and msg.msgReceiverUser=:receiverUserId")
						.setParam("receiverUserId", receiverUserId);
			}
			if (box != null) {
				finder.append(" and msg.msgBox =:box").setParam("box", box);
			}
		}
		if (StringUtils.isNotBlank(title)) {
			finder.append(" and msg.msgTitle like:title").setParam("title",
					"%" + title + "%");
		}
		if (sendBeginTime != null) {
			finder.append(" and msg.sendTime >=:sendBeginTime").setParam(
					"sendBeginTime", sendBeginTime);
		}
		if (sendEndTime != null) {
			finder.append(" and msg.sendTime <=:sendEndTime").setParam(
					"sendEndTime", sendEndTime);
		}
		if (status != null) {
			if (status) {
				finder.append(" and msg.msgStatus =true");
			} else {
				finder.append(" and msg.msgStatus =false");
			}
		}
		finder.append(" order by msg.msgReId desc");

		return find(finder, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public List<CmsReceiverMessage> getList(Integer siteId, Integer sendUserId,
			Integer receiverUserId, String title, Date sendBeginTime,
			Date sendEndTime, Boolean status, Integer box, Boolean cacheable) {
		String hql = " select msg from CmsReceiverMessage msg where 1=1  ";
		Finder finder = Finder.create(hql);
		if (siteId != null) {
			finder.append(" and msg.site.siteId=:siteId")
					.setParam("siteId", siteId);
		}
		// 垃圾箱
		if (sendUserId != null && receiverUserId != null) {
			finder
					.append(
							" and ((msg.msgReceiverUser=:receiverUserId  and msg.msgBox =:box) or (msg.msgSendUser=:sendUserId  and msg.msgBox =:box) )")
					.setParam("sendUserId", sendUserId).setParam(
							"receiverUserId", receiverUserId).setParam("box",
							box);
		}  else {
			if (sendUserId != null) {
				finder.append(" and msg.msgSendUser=:sendUserId").setParam(
						"sendUserId", sendUserId);
			}
			if (receiverUserId != null) {
				finder.append(" and msg.msgReceiverUser=:receiverUserId")
						.setParam("receiverUserId", receiverUserId);
			}
			if (box != null) {
				finder.append(" and msg.msgBox =:box").setParam("box", box);
			}
		}
		if (StringUtils.isNotBlank(title)) {
			finder.append(" and msg.msgTitle like:title").setParam("title",
					"%" + title + "%");
		}
		if (sendBeginTime != null) {
			finder.append(" and msg.sendTime >=:sendBeginTime").setParam(
					"sendBeginTime", sendBeginTime);
		}
		if (sendEndTime != null) {
			finder.append(" and msg.sendTime <=:sendEndTime").setParam(
					"sendEndTime", sendEndTime);
		}
		if (status != null) {
			if (status) {
				finder.append(" and msg.msgStatus =true");
			} else {
				finder.append(" and msg.msgStatus =false");
			}
		}
		finder.append(" order by msg.msgReId desc");
		return find(finder);
	}

	@SuppressWarnings("unchecked")
	public CmsReceiverMessage find(Integer messageId,Integer box){
		String hql = " select msg from CmsReceiverMessage msg where 1=1  ";
		Finder finder = Finder.create(hql);
		if(messageId!=null){
			finder.append(" and msg.message.msgId=:messageId").setParam("messageId", messageId);
		}
		if (box != null) {
			finder.append(" and msg.msgBox =:box").setParam("box", box);
		}
		finder.append(" order by msg.msgReId desc");
		List<CmsReceiverMessage>list= find(finder);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}

	public CmsReceiverMessage findById(Integer id) {
		return super.get(id);
	}

	public CmsReceiverMessage save(CmsReceiverMessage bean) {
		getSession().save(bean);
		return bean;
	}

	public CmsReceiverMessage update(CmsReceiverMessage bean) {
		getSession().update(bean);
		return bean;
	}

	public CmsReceiverMessage deleteById(Integer id) {
		CmsReceiverMessage entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public CmsReceiverMessage[] deleteByIds(Integer[] ids) {
		CmsReceiverMessage[] messages = new CmsReceiverMessage[ids.length];
		for (int i = 0; i < ids.length; i++) {
			messages[i] = get(ids[i]);
			deleteById(ids[i]);
		}
		return messages;
	}

	@Override
	protected Class<CmsReceiverMessage> getEntityClass() {
		return CmsReceiverMessage.class;
	}

}
