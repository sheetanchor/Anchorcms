package com.anchorcms.cms.service.main.impl;

import java.util.List;
import java.util.Map;

import com.anchorcms.cms.dao.main.ChannelDao;
import com.anchorcms.cms.service.main.*;
import com.anchorcms.cms.model.main.*;
import com.anchorcms.cms.service.main.ChannelDeleteCheckerService;
import com.anchorcms.common.hibernate.Updater;
import com.anchorcms.common.page.Pagination;
import com.anchorcms.core.service.GroupService;
import com.anchorcms.core.service.SiteService;
import com.anchorcms.core.service.UserService;
import com.anchorcms.core.model.CmsGroup;
import com.anchorcms.core.model.CmsUser;
import com.anchorcms.core.model.CmsUserSite;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("ChannelService")
@Transactional
public class ChannelServiceImpl implements ChannelService {
	@Transactional(readOnly = true)
	public List<Channel> getTopList(Integer siteId, boolean hasContentOnly) {
		return dao.getTopList(siteId, hasContentOnly, false, false);
	}

	@Transactional(readOnly = true)
	public List<Channel> getTopListByRigth(Integer userId, Integer siteId,
			boolean hasContentOnly) {
		CmsUser user = userService.findById(userId);
		CmsUserSite us = user.getUserSite(siteId);
		if (us.getIsAllChannel()) {
			return getTopList(siteId, hasContentOnly);
		} else {
			return dao.getTopListByRigth(userId, siteId, hasContentOnly);
		}
	}

	@Transactional(readOnly = true)
	public List<Channel> getTopListForTag(Integer siteId, boolean hasContentOnly) {
		return dao.getTopList(siteId,hasContentOnly, true, true);
	}

	@Transactional(readOnly = true)
	public Pagination getTopPageForTag(Integer siteId, boolean hasContentOnly,
									   int pageNo, int pageSize) {
		return dao.getTopPage(siteId, hasContentOnly, false, false, pageNo,
				pageSize);
	}
	
	

	@Transactional(readOnly = true)
	public List<Channel> getChildList(Integer parentId, boolean hasContentOnly) {
		return dao.getChildList(parentId, hasContentOnly, false, false);
	}

	@Transactional(readOnly = true)
	public List<Channel> getChildListByRight(Integer userId, Integer siteId,
			Integer parentId, boolean hasContentOnly) {
		CmsUser user = userService.findById(userId);
		CmsUserSite us = user.getUserSite(siteId);
		if (us.getIsAllChannel()) {
			return getChildList(parentId, hasContentOnly);
		} else {
			return dao.getChildListByRight(userId, parentId, hasContentOnly);
		}
	}

	@Transactional(readOnly = true)
	public List<Channel> getChildListForTag(Integer parentId,
			boolean hasContentOnly) {
		return dao.getChildList(parentId, hasContentOnly, true, true);
	}
	
	@Transactional(readOnly = true)
	public List<Channel> getBottomList(Integer siteId,boolean hasContentOnly){
		return dao.getBottomList(siteId,hasContentOnly);
	}

	@Transactional(readOnly = true)
	public Pagination getChildPageForTag(Integer parentId,
			boolean hasContentOnly, int pageNo, int pageSize) {
		return dao.getChildPage(parentId, hasContentOnly, true, true, pageNo,
				pageSize);
	}
	
	@Transactional(readOnly = true)
	public Channel findById(Integer id) {
		Channel entity = dao.findById(id);
		return entity;
	}

	@Transactional(readOnly = true)
	public Channel findByPath(String path, Integer siteId) {
		return dao.findByPath(path, siteId, false);
	}

	@Transactional(readOnly = true)
	public Channel findByPathForTag(String path, Integer siteId) {
		return dao.findByPath(path, siteId, true);
	}

	public Channel save(Channel bean, ChannelExt ext, ChannelTxt txt,
						Integer[] viewGroupIds, Integer[] contriGroupIds,
						Integer[] userIds, Integer siteId, Integer parentId, Integer modelId,
						Integer[]modelIds, String[] tpls, String[] mtpls) {
		if (parentId != null) {
			bean.setParent(findById(parentId));
		}
		bean.setSite(siteService.findById(siteId));
		CmsModel model = modelService.findById(modelId);
		bean.setModel(model);
		bean.setHasContent(model.getHasContent());
		bean.init();
		dao.save(bean);
		channelExtService.save(ext, bean);
		channelTxtService.save(txt, bean);
		channelCountService.save(new ChannelCount(), bean);
		CmsGroup g;
		if (viewGroupIds != null && viewGroupIds.length > 0) {
			for (Integer gid : viewGroupIds) {
				g = groupService.findById(gid);
				bean.addToViewGroups(g);
			}
		}
		if (contriGroupIds != null && contriGroupIds.length > 0) {
			for (Integer gid : contriGroupIds) {
				g = groupService.findById(gid);
				bean.addToContriGroups(g);
			}
		}
		if (modelIds != null && modelIds.length > 0) {
			for (int i=0;i<modelIds.length;i++) {
				CmsModel m = modelService.findById(modelIds[i]);
				bean.addToChannelModels(m, tpls[i],mtpls[i]);
			}
		}
		CmsUser u;
		if (userIds != null && userIds.length > 0) {
			for (Integer uid : userIds) {
				u = userService.findById(uid);
				bean.addToUsers(u);
			}
		}
		return bean;
	}

	public Channel update(Channel bean, ChannelExt ext, ChannelTxt txt,
			Integer[] viewGroupIds, Integer[] contriGroupIds,
			Integer[] userIds, Integer parentId, Integer modelId,
			Map<String, String> attr,Integer[]modelIds,String[] tpls,String[] mtpls) {
		// 更新主表
		Updater<Channel> updater = new Updater<Channel>(bean);
		bean = dao.updateByUpdater(updater);
		if(modelId!=null){
			CmsModel model = modelService.findById(modelId);
			bean.setModel(model);
			bean.setHasContent(model.getHasContent());
		}
		// 更新父栏目
		Channel parent;
		if (parentId != null) {
			parent = findById(parentId);
		} else {
			parent = null;
		}
		bean.setParent(parent);
		// 更新扩展表
		channelExtService.update(ext);
		// 更新文本表
		channelTxtService.update(txt, bean);
		// 更新属性表
		Map<String, String> attrOrig = bean.getAttr();
		attrOrig.clear();
		attrOrig.putAll(attr);
		// 更新浏览会员组关联
		for (CmsGroup g : bean.getViewGroups()) {
			g.getViewChannels().remove(bean);
		}
		bean.getViewGroups().clear();
		if (viewGroupIds != null && viewGroupIds.length > 0) {
			CmsGroup g;
			for (Integer gid : viewGroupIds) {
				g = groupService.findById(gid);
				bean.addToViewGroups(g);
			}
		}
		// 更新投稿会员组关联
		for (CmsGroup g : bean.getContriGroups()) {
			g.getContriChannels().remove(bean);
		}
		bean.getContriGroups().clear();
		if (contriGroupIds != null && contriGroupIds.length > 0) {
			CmsGroup g;
			for (Integer gid : contriGroupIds) {
				g = groupService.findById(gid);
				bean.addToContriGroups(g);
			}
		}
		bean.getChannelModels().clear();
		if (modelIds != null && modelIds.length > 0) {
			for (int i=0;i<modelIds.length;i++) {
				CmsModel m = modelService.findById(modelIds[i]);
				bean.addToChannelModels(m, tpls[i],mtpls[i]);
			}
		}
		// 更新管理员关联
		for (CmsUser u : bean.getUsers()) {
			u.getChannels().remove(bean);
		}
		bean.getUsers().clear();
		if (userIds != null && userIds.length > 0) {
			CmsUser u;
			for (Integer uid : userIds) {
				u = userService.findById(uid);
				bean.addToUsers(u);
			}
		}
		return bean;
	}

	public Channel deleteById(Integer id) {
		Channel entity = dao.findById(id);
		for (CmsGroup group : entity.getViewGroups()) {
			group.getViewChannels().remove(entity);
		}
		for (CmsGroup group : entity.getContriGroups()) {
			group.getContriChannels().remove(entity);
		}
		entity = dao.deleteById(id);
		return entity;
	}

	public Channel[] deleteByIds(Integer[] ids) {
		Channel[] beans = new Channel[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	public String checkDelete(Integer id) {
		String msg = null;
		for (ChannelDeleteCheckerService checker : deleteCheckerList) {
			msg = checker.checkForChannelDelete(id);
			if (msg != null) {
				return msg;
			}
		}
		return msg;
	}

	public Channel[] updatePriority(Integer[] ids, Integer[] priority) {
		int len = ids.length;
		Channel[] beans = new Channel[len];
		for (int i = 0; i < len; i++) {
			beans[i] = findById(ids[i]);
			beans[i].setPriority(priority[i]);
		}
		return beans;
	}

	private List<ChannelDeleteCheckerService> deleteCheckerList;

	public void setDeleteCheckerList(
			List<ChannelDeleteCheckerService> deleteCheckerList) {
		this.deleteCheckerList = deleteCheckerList;
	}
	@Resource
	private SiteService siteService;
	@Resource
	private ModelService modelService;
	@Resource
	private ChannelExtService channelExtService;
	@Resource
	private ChannelTxtService channelTxtService;
	@Resource
	private ChannelCountService channelCountService;
	@Resource
	private UserService userService;
	@Resource
	private GroupService groupService;
	@Resource
	private ChannelDao dao;

}