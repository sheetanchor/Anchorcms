package com.anchorcms.cms.controller.admin.main;


import com.anchorcms.cms.model.main.*;
import com.anchorcms.cms.model.main.ContentRecord.ContentOperateType;
import com.anchorcms.cms.service.assist.FileService;
import com.anchorcms.cms.service.assist.ImageService;
import com.anchorcms.cms.service.main.*;
import com.anchorcms.common.image.ImageUtils;
import com.anchorcms.common.page.Pagination;
import com.anchorcms.common.page.staticpage.impl.*;
import com.anchorcms.common.upload.FileRepository;
import com.anchorcms.common.utils.CmsUtils;
import com.anchorcms.common.utils.CoreUtils;
import com.anchorcms.common.utils.StrUtil;
import com.anchorcms.common.web.CookieUtils;
import com.anchorcms.common.web.RequestUtils;
import com.anchorcms.common.web.ResponseUtils;
import com.anchorcms.common.web.mvc.MessageResolver;
import com.anchorcms.core.model.CmsGroup;
import com.anchorcms.core.model.CmsSite;
import com.anchorcms.core.model.CmsUser;
import com.anchorcms.core.model.Ftp;
import com.anchorcms.core.service.*;
import com.anchorcms.core.web.WebErrors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.anchorcms.common.page.SimplePage.cpn;

/**
 * @Author 阁楼麻雀
 * @Date 2016/11/24 17:08
 * @Desc 内容管理
 */

@Controller
public class ContentController {
	private static final Logger log = LoggerFactory.getLogger(ContentController.class);

	@RequiresPermissions("content:v_left")
	@RequestMapping("/content/v_left.do")
	public String left(String source, ModelMap model) {
		model.addAttribute("source", source);
		return "content/left";
	}

	/**
	 * 栏目导航
	 * 
	 * @param root
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("content:v_tree")
	@RequestMapping(value = "/content/v_tree.do")
	public String tree(String root, HttpServletRequest request,
					   HttpServletResponse response, ModelMap model) {
		log.debug("tree path={}", root);
		boolean isRoot;
		// jquery treeview的根请求为root=source
		if (StringUtils.isBlank(root) || "source".equals(root)) {
			isRoot = true;
		} else {
			isRoot = false;
		}
		model.addAttribute("isRoot", isRoot);
		WebErrors errors = validateTree(root, request);
		if (errors.hasErrors()) {
			log.error(errors.getErrors().get(0));
			ResponseUtils.renderJson(response, "[]");
			return null;
		}
		List<Channel> list;
		Integer siteId = CmsUtils.getSiteId(request);
		Integer userId = CmsUtils.getUserId(request);
		CmsUser user= CmsUtils.getUser(request);
		if(user.getUserSite(siteId).getIsAllChannel()){
			if (isRoot) {
				list = channelService.getTopList( siteId, true);
			} else {
				list = channelService.getChildList(Integer.parseInt(root), true);
			}
		}else{
			if (isRoot) {
				list = channelService.getTopListByRigth(userId, siteId, true);
			} else {
				list = channelService.getChildListByRight(userId,siteId, Integer
						.parseInt(root), true);
			}
		}
		
		model.addAttribute("list", list);
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		return "content/tree";
	}

	/**
	 * 副栏目树
	 * 
	 * @param root
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("content:v_tree_channels")
	@RequestMapping(value = "/content/v_tree_channels.do")
	public String treeChannels(String root, HttpServletRequest request,
							   HttpServletResponse response, ModelMap model) {
		tree(root, request, response, model);
		return "content/tree_channels";
	}

	@RequiresPermissions("content:v_list")
	@RequestMapping("/content/v_list.do")
	public String list(String queryStatus, Integer queryTypeId,
					   Boolean queryTopLevel, Boolean queryRecommend,
					   Integer queryOrderBy, Integer cid, Integer pageNo,
					   HttpServletRequest request, ModelMap model) {
		long time = System.currentTimeMillis();
		String queryTitle = RequestUtils.getQueryParam(request, "queryTitle");
		queryTitle = StringUtils.trim(queryTitle);
		String queryInputUsername = RequestUtils.getQueryParam(request,
				"queryInputUsername");
		queryInputUsername = StringUtils.trim(queryInputUsername);
		if (queryTopLevel == null) {
			queryTopLevel = false;
		}
		if (queryRecommend == null) {
			queryRecommend = false;
		}
		if (queryOrderBy == null) {
			queryOrderBy = 4;
		}
		Content.ContentStatus status;
		if (!StringUtils.isBlank(queryStatus)) {
			status = Content.ContentStatus.valueOf(queryStatus);
		} else {
			status = Content.ContentStatus.all;
		}
		Integer queryInputUserId = null;
		if (!StringUtils.isBlank(queryInputUsername)) {
			CmsUser u = userService.findByUsername(queryInputUsername);
			if (u != null) {
				queryInputUserId = u.getUserId();
			} else {
				// 用户名不存在，清空。
				//queryInputUsername = null;
				queryInputUserId=null;
			}
		}else{
			queryInputUserId=0;
		}
		CmsSite site = CmsUtils.getSite(request);
		Integer siteId = site.getSiteId();
		CmsUser user = CmsUtils.getUser(request);
		Integer userId = user.getUserId();
		byte currStep = user.getCheckStep(siteId);
		Pagination p = manager.getPageByRight(queryTitle, queryTypeId,user.getUserId(),
				queryInputUserId, queryTopLevel, queryRecommend, status, user
						.getCheckStep(siteId), siteId, cid, userId,
				queryOrderBy, cpn(pageNo), CookieUtils.getPageSize(request));
		List<ContentType> typeList = contentTypeService.getList(true);
		List<CmsModel> models=modelService.getList(false, true,siteId);
		if(cid!=null){
			Channel c=channelService.findById(cid);
			models=c.getModels(models);
		}
		model.addAttribute("pagination", p);
		model.addAttribute("cid", cid);
		model.addAttribute("typeList", typeList);
		model.addAttribute("currStep", currStep);
		model.addAttribute("site", site);
		model.addAttribute("models", models);
		addAttibuteForQuery(model, queryTitle, queryInputUsername, queryStatus,
				queryTypeId, queryTopLevel, queryRecommend, queryOrderBy,
				pageNo);
		time = System.currentTimeMillis() - time;
		return "content/list";
	}

	@RequiresPermissions("content:v_add")
	@RequestMapping("/content/v_add.do")
	public String add(Integer cid, Integer modelId, HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateAdd(cid,modelId, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsSite site = CmsUtils.getSite(request);
		Integer siteId = site.getSiteId();
		CmsUser user = CmsUtils.getUser(request);
		Integer userId = user.getUserId();
		// 栏目
		Channel c;
		if (cid != null) {
			c = channelService.findById(cid);
		} else {
			c = null;
		}
		// 模型
		CmsModel m;
		if(modelId==null){
			if (c != null) {
				m = c.getModel();
			} else {
				m = modelService.getDefModel();
				// TODO m==null给出错误提示
				if (m == null) {
					throw new RuntimeException("default model not found!");
				}
			}
		}else{
			m=modelService.findById(modelId);
		}
		// 模型项列表
		List<CmsModelItem> itemList = modelItemService.getList(m.getModelId(), false,
				false);
		// 栏目列表
		List<Channel> channelList;
		Set<Channel> rights;
		if (user.getUserSite(siteId).getIsAllChannel()) {
			// 拥有所有栏目权限
			rights = null;
		} else {
			rights = user.getChannels(siteId);
		}
		if (c != null) {
			channelList = c.getListForSelect(rights, true);
		} else {
			List<Channel> topList = channelService.getTopListByRigth(userId,siteId, true);
			channelList = Channel.getListForSelect(topList, rights, true);
		}

		// 专题列表
		List<CmsTopic> topicList;
		if (c != null) {
			topicList = topicService.getListByChannel(c.getChannelId());
		} else {
			topicList = new ArrayList<CmsTopic>();
		}
		// 内容模板列表
		List<String> tplList = getTplContent(site, m, null);
		// 内容手机模板列表
		List<String> tplMobileList = getTplMobileContent(site, m, null);
		// 会员组列表
		List<CmsGroup> groupList = groupService.getList();
		// 内容类型
		List<ContentType> typeList = contentTypeService.getList(false);
		model.addAttribute("site", CmsUtils.getSite(request));
		model.addAttribute("model", m);
		model.addAttribute("itemList", itemList);
		model.addAttribute("channelList", channelList);
		model.addAttribute("topicList", topicList);
		model.addAttribute("tplList", tplList);
		model.addAttribute("tplMobileList", tplMobileList);
		model.addAttribute("groupList", groupList);
		model.addAttribute("typeList", typeList);
		if (cid != null) {
			model.addAttribute("cid", cid);
		}
		if (c != null) {
			model.addAttribute("channel", c);
		}
		model.addAttribute("sessionId",request.getSession().getId());
		return "content/add";
	}

	@RequiresPermissions("content:v_view")
	@RequestMapping("/content/v_view.do")
	public String view(String queryStatus, Integer queryTypeId,
					   Boolean queryTopLevel, Boolean queryRecommend,
					   Integer queryOrderBy, Integer pageNo, Integer cid, Integer id,
					   HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		byte currStep = user.getCheckStep(site.getSiteId());
		Content content = manager.findById(id);

		model.addAttribute("content", content);
		model.addAttribute("currStep", currStep);
		model.addAttribute("site", site);
		if (cid != null) {
			model.addAttribute("cid", cid);
		}
		String queryTitle = RequestUtils.getQueryParam(request, "queryTitle");
		String queryInputUsername = RequestUtils.getQueryParam(request,
				"queryInputUsername");
		addAttibuteForQuery(model, queryTitle, queryInputUsername, queryStatus,
				queryTypeId, queryTopLevel, queryRecommend, queryOrderBy,
				pageNo);
		return "content/view";
	}

	@RequiresPermissions("content:v_edit")
	@RequestMapping("/content/v_edit.do")
	public String edit(String queryStatus, Integer queryTypeId,
					   Boolean queryTopLevel, Boolean queryRecommend,
					   Integer queryOrderBy, Integer pageNo, Integer cid, Integer id,
					   HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateEdit(id, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsSite site = CmsUtils.getSite(request);
		Integer siteId = site.getSiteId();
		CmsUser user = CmsUtils.getUser(request);
		// 内容
		Content content = manager.findById(id);
		// 栏目
		Channel channel = content.getChannel();
		// 模型
		CmsModel m=content.getModel();
		// 模型项列表
		List<CmsModelItem> itemList = modelItemService.getList(m.getModelId(), false,
				false);
		// 栏目列表
		Set<Channel> rights;
		if (user.getUserSite(siteId).getIsAllChannel()) {
			// 拥有所有栏目权限
			rights = null;
		} else {
			//更改成部门获取栏目权限
			rights = user.getChannels(siteId);
		}

		List<Channel> topList = channelService.getTopList(site.getSiteId(), true);
		List<Channel> channelList = Channel.getListForSelect(topList, rights,
				true);

		// 专题列表
		List<CmsTopic> topicList = topicService
				.getListByChannel(channel.getChannelId());
		Set<CmsTopic> topics = content.getTopics();
		for (CmsTopic t : topics) {
			if (!topicList.contains(t)) {
				topicList.add(t);
			}
		}
		Integer[] topicIds = CmsTopic.fetchIds(content.getTopics());
		// 内容模板列表
		List<String> tplList = getTplContent(site, m, content.getTplContent());
		// 内容手机模板列表
		List<String> tplMobileList = getTplMobileContent(site, m, null);
		// 会员组列表
		List<CmsGroup> groupList = groupService.getList();
		Integer[] groupIds = CmsGroup.fetchIds(content.getViewGroups());
		// 内容类型
		List<ContentType> typeList = contentTypeService.getList(false);
		// 当前模板，去除基本路径
		int tplPathLength = site.getTplPath().length();
		String tplContent = content.getTplContent();
		if (!StringUtils.isBlank(tplContent)) {
			tplContent = tplContent.substring(tplPathLength);
		}
		String tplMobileContent = content.getMobileTplContent();
		if (!StringUtils.isBlank(tplMobileContent)) {
			tplMobileContent = tplMobileContent.substring(tplPathLength);
		}
		model.addAttribute("site", CmsUtils.getSite(request));
		model.addAttribute("content", content);
		model.addAttribute("channel", channel);
		model.addAttribute("model", m);
		model.addAttribute("itemList", itemList);
		model.addAttribute("channelList", channelList);
		model.addAttribute("topicList", topicList);
		model.addAttribute("topicIds", topicIds);
		model.addAttribute("tplList", tplList);
		model.addAttribute("tplMobileList", tplMobileList);
		model.addAttribute("groupList", groupList);
		model.addAttribute("groupIds", groupIds);
		model.addAttribute("typeList", typeList);
		model.addAttribute("tplContent", tplContent);
		model.addAttribute("tplMobileContent", tplMobileContent);
		if (cid != null) {
			model.addAttribute("cid", cid);
		}

		String queryTitle = RequestUtils.getQueryParam(request, "queryTitle");
		String queryInputUsername = RequestUtils.getQueryParam(request,
				"queryInputUsername");
		addAttibuteForQuery(model, queryTitle, queryInputUsername, queryStatus,
				queryTypeId, queryTopLevel, queryRecommend, queryOrderBy,
				pageNo);
		model.addAttribute("sessionId",request.getSession().getId());
		return "content/edit";
	}


	@RequiresPermissions("content:o_save")
	@RequestMapping("/content/o_save.do")
	public String save(Content bean, ContentExt ext, ContentTxt txt,
					   Boolean copyimg, Integer[] channelIds, Integer[] topicIds,
					   Integer[] viewGroupIds,
					   String[] attachmentPaths, String[] attachmentNames,
					   String[] attachmentFilenames, String[] picPaths, String[] picDescs,
					   Integer channelId, Integer typeId, String tagStr, Boolean draft,
					   Integer cid, Integer modelId, Short charge, Double chargeAmount,
					   HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		WebErrors errors = validateSave(bean, channelId, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		// 加上模板前缀
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String tplPath = site.getTplPath();
		if (!StringUtils.isBlank(ext.getTplContent())) {
			ext.setTplContent(tplPath + ext.getTplContent());
		}
		if (!StringUtils.isBlank(ext.getTplMobileContent())) {
			ext.setTplMobileContent(tplPath + ext.getTplMobileContent());
		}
		bean.setAttr(RequestUtils.getRequestMap(request, "attr_"));
		String[] tagArr = StrUtil.splitAndTrim(tagStr, ",", MessageResolver
				.getMessage(request, "content.tagStr.split"));
		if(txt!=null&&copyimg!=null&&copyimg){
			txt=copyContentTxtImg(txt, site);
		}
		bean = manager.save(bean, ext, txt,channelIds, topicIds, viewGroupIds,
				tagArr, attachmentPaths, attachmentNames, attachmentFilenames,
				picPaths, picDescs, channelId, typeId, draft,false,
				charge,chargeAmount, user, false);
		//处理附件
		fileService.updateFileByPaths(attachmentPaths,picPaths,ext.getMediaPath(),ext.getTitleImg(),ext.getTypeImg(),ext.getContentImg(),true,bean);
		log.info("save Content id={}", bean.getContentId());
		logService.operating(request, "content.log.save", "id=" + bean.getContentId()
				+ ";title=" + bean.getTitle());
		if (cid != null) {
			model.addAttribute("cid", cid);
		}
		model.addAttribute("message", "global.success");
		return add(cid,modelId, request, model);
	}

	@RequiresPermissions("content:o_update")
	@RequestMapping("/content/o_update.do")
	public String update(String queryStatus, Integer queryTypeId,
						 Boolean queryTopLevel, Boolean queryRecommend,
						 Integer queryOrderBy, Content bean, ContentExt ext, ContentTxt txt,
						 Boolean copyimg, Integer[] channelIds, Integer[] topicIds,
						 Integer[] viewGroupIds, String[] attachmentPaths,
						 String[] attachmentNames, String[] attachmentFilenames, String[] picPaths, String[] picDescs,
						 Integer channelId, Integer typeId, String tagStr, Boolean draft,
						 Integer cid, String[]oldattachmentPaths, String[] oldpicPaths,
						 String oldTitleImg, String oldContentImg, String oldTypeImg,
						 Short charge, Double chargeAmount,
						 Integer pageNo, HttpServletRequest request,
						 ModelMap model) {
		WebErrors errors = validateUpdate(bean.getContentId(), request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		// 加上模板前缀
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String tplPath = site.getTplPath();
		if (!StringUtils.isBlank(ext.getTplContent())) {
			ext.setTplContent(tplPath + ext.getTplContent());
		}
		if (!StringUtils.isBlank(ext.getTplMobileContent())) {
			ext.setTplMobileContent(tplPath + ext.getTplMobileContent());
		}
		String[] tagArr = StrUtil.splitAndTrim(tagStr, ",", MessageResolver
				.getMessage(request, "content.tagStr.split"));
		Map<String, String> attr = RequestUtils.getRequestMap(request, "attr_");
		if(txt!=null&&copyimg!=null&&copyimg){
			txt=copyContentTxtImg(txt, site);
		}
		bean = manager.update(bean, ext, txt,tagArr, channelIds, topicIds,
				viewGroupIds, attachmentPaths, attachmentNames,
				attachmentFilenames, picPaths, picDescs, attr, channelId,
				typeId, draft, charge,chargeAmount,user, false);
		//处理之前的附件有效性
		fileService.updateFileByPaths(oldattachmentPaths,oldpicPaths,null,oldTitleImg,oldTypeImg,oldContentImg,false,bean);
		//处理更新后的附件有效性
		fileService.updateFileByPaths(attachmentPaths,picPaths,ext.getMediaPath(),ext.getTitleImg(),ext.getTypeImg(),ext.getContentImg(),true,bean);
		log.info("update Content id={}.", bean.getContentId());
		logService.operating(request, "content.log.update", "id=" + bean.getContentId()
				+ ";title=" + bean.getTitle());
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}

	@RequiresPermissions("content:o_delete")
	@RequestMapping("/content/o_delete.do")
	public String delete(String queryStatus, Integer queryTypeId,
						 Boolean queryTopLevel, Boolean queryRecommend,
						 Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
						 HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Content[] beans;
		// 是否开启回收站
		if (site.getIsRecycleOn()) {
			beans = manager.cycle(CmsUtils.getUser(request),ids);
			for (Content bean : beans) {
				log.info("delete to cycle, Content id={}", bean.getContentId());
			}
		} else {
			for(Integer id:ids){
				Content c=manager.findById(id);
				//处理附件
				manager.updateFileByContent(c, false);
			}
			beans = manager.deleteByIds(ids);
			for (Content bean : beans) {
				log.info("delete Content id={}", bean.getContentId());
				logService.operating(request, "content.log.delete", "id="
						+ bean.getContentId() + ";title=" + bean.getTitle());
			}
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:o_check")
	@RequestMapping("/content/o_check.do")
	public String check(String queryStatus, Integer queryTypeId,
						Boolean queryTopLevel, Boolean queryRecommend,
						Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
						HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateCheck(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser user = CmsUtils.getUser(request);
		Content[] beans = manager.check(ids, user);
		for (Content bean : beans) {
			log.info("check Content id={}", bean.getContentId());
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:o_check")
	@RequestMapping("/content/o_ajax_check.do")
	public void ajaxCheck(Integer[] ids, HttpServletRequest request, HttpServletResponse response,
						  ModelMap model) throws JSONException {
		WebErrors errors = validateCheck(ids, request);
		JSONObject json=new JSONObject();
		if (errors.hasErrors()) {
			json.put("error", errors.getErrors().get(0));
			json.put("success", false);
		}
		CmsUser user = CmsUtils.getUser(request);
		manager.check(ids, user);
		json.put("success", true);
		ResponseUtils.renderJson(response, json.toString());
	}

	@RequiresPermissions("content:o_static")
	@RequestMapping("/content/o_static.do")
	public String contentStatic(String queryStatus, Integer queryTypeId,
								Boolean queryTopLevel, Boolean queryRecommend,
								Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
								HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateStatic(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		try {
			Content[] beans = manager.contentStatic(CmsUtils.getUser(request),ids);
			for (Content bean : beans) {
				log.info("static Content id={}", bean.getContentId());
			}
			model.addAttribute("message", errors.getMessage(
					"content.staticGenerated", beans.length));
		} catch (TemplateNotFoundException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(),
					new Object[] { e.getErrorTitle(), e.getGenerated() }));
		} catch (TemplateParseException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(),
					new Object[] { e.getErrorTitle(), e.getGenerated() }));
		} catch (GeneratedZeroStaticPageException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(), e
					.getGenerated()));
		} catch (StaticPageNotOpenException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(),
					new Object[] { e.getErrorTitle(), e.getGenerated() }));
		} catch (ContentNotCheckedException e) {
			model.addAttribute("message", errors.getMessage(e.getMessage(),
					new Object[] { e.getErrorTitle(), e.getGenerated() }));
		} catch (freemarker.template.TemplateNotFoundException e) {
			e.printStackTrace();
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}

	@RequiresPermissions("content:o_reject")
	@RequestMapping("/content/o_reject.do")
	public String reject(String queryStatus, Integer queryTypeId,
						 Boolean queryTopLevel, Boolean queryRecommend,
						 Integer queryOrderBy, Integer[] ids, Integer cid, Byte rejectStep,
						 String rejectOpinion, Integer pageNo, HttpServletRequest request,
						 ModelMap model) {
		WebErrors errors = validateReject(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser user = CmsUtils.getUser(request);
		Content[] beans = manager.reject(ids, user,rejectStep, rejectOpinion);
		for (Content bean : beans) {
			log.info("reject Content id={}", bean.getContentId());
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:o_reject")
	@RequestMapping("/content/o_ajax_reject.do")
	public void ajaxReject(Integer[] ids, Byte rejectStep,
						   String rejectOpinion,
						   HttpServletRequest request, HttpServletResponse response,
						   ModelMap model) throws JSONException {
		WebErrors errors = validateReject(ids, request);
		JSONObject json=new JSONObject();
		if (errors.hasErrors()) {
			json.put("error", errors.getErrors().get(0));
			json.put("success", false);
		}
		CmsUser user = CmsUtils.getUser(request);
		manager.reject(ids, user,rejectStep, rejectOpinion);
		json.put("success", true);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequiresPermissions("content:o_submit")
	@RequestMapping("/content/o_submit.do")
	public String submit(String queryStatus, Integer queryTypeId,
						 Boolean queryTopLevel, Boolean queryRecommend,
						 Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
						 HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateCheck(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		CmsUser user = CmsUtils.getUser(request);
		Content[] beans = manager.submit(ids, user);
		for (Content bean : beans) {
			log.info("submit Content id={}", bean.getContentId());
		}
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:v_tree_radio")
	@RequestMapping(value = "/content/v_tree_radio.do")
	public String move_tree(String root, HttpServletRequest request,
							HttpServletResponse response, ModelMap model) {
		tree(root, request, response, model);
		return "content/tree_move";
	}
	
	@RequiresPermissions("content:o_move")
	@RequestMapping("/content/o_move.do")
		public void move(Integer contentIds[], Integer channelId,
						 HttpServletRequest request, HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		Boolean pass = true;
		if (contentIds != null && channelId != null) {
			Channel channel=channelService.findById(channelId);
			for(Integer contentId:contentIds){
				Content bean=manager.findById(contentId);
				if(bean!=null&&channel!=null){
					bean.removeSelfAddToChannels(channelService.findById(channelId));
					bean.setChannel(channel);
					manager.update(CmsUtils.getUser(request), bean, ContentOperateType.move);
				}
			}
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequiresPermissions("content:o_copy")
	@RequestMapping("/content/o_copy.do")
		public void copy(Integer contentIds[], Integer channelId,
						 Integer siteId, HttpServletRequest request,
						 HttpServletResponse response)throws JSONException {
		JSONObject json = new JSONObject();
		CmsUser user= CmsUtils.getUser(request);
		Boolean pass = true;
		if (contentIds != null) {
			for(Integer contentId:contentIds){
				Content bean=manager.findById(contentId);
				Content beanCopy= new Content();
				ContentExt extCopy=new ContentExt();
				ContentTxt txtCopy=new ContentTxt();
				beanCopy=bean.cloneWithoutSet();
				beanCopy.setChannel(channelService.findById(channelId));
				//复制到别站
				if(siteId!=null){
					beanCopy.setSite(siteService.findById(siteId));
				}
				boolean draft=false;
				if(bean.getStatus().equals(ContentCheck.DRAFT)){
					draft=true;
				}
				BeanUtils.copyProperties(bean.getContentExt(), extCopy);
				if(bean.getContentTxt()!=null){
					BeanUtils.copyProperties(bean.getContentTxt(), txtCopy);
				}
				manager.save(beanCopy, extCopy, txtCopy,null,
						bean.getTopicIds(), bean.getViewGroupIds(), 
						bean.getTagArray(), bean.getAttachmentPaths(),
						bean.getAttachmentNames(),bean.getAttachmentFileNames(),
						bean.getPicPaths(), bean.getPicDescs(),
						channelId, bean.getType().getTypeId(), draft,false,
						bean.getChargeModel(),bean.getChargeAmount(),user, false);
			}
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequiresPermissions("content_reuse:o_copy")
	@RequestMapping("/content_reuse/o_copy.do")
	public void contentCopy(Integer contentIds[], Integer channelId, Integer siteId, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		copy(contentIds, channelId, siteId, request, response);
	}
	/**
	 * 引用
	 * @param contentIds
	 * @param channelId
	 */
	@RequiresPermissions("content:o_refer")
	@RequestMapping("/content/o_refer.do")
		public void refer(Integer contentIds[], Integer channelId, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		CmsUser user= CmsUtils.getUser(request);
		Boolean pass = true;
		if(user==null){
			ResponseUtils.renderJson(response, "false");
		}
		if (contentIds != null) {
			for(Integer contentId:contentIds){
				manager.updateByChannelIds(contentId, new Integer[]{channelId});
			}
		}else{
			ResponseUtils.renderJson(response, "false");
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequiresPermissions("content:o_priority")
	@RequestMapping("/content/o_priority.do")
	public String priority(Integer[] wids, Byte[] topLevel,
						   String queryStatus, Integer queryTypeId,
						   Boolean queryTopLevel, Boolean queryRecommend,
						   Integer queryOrderBy, Integer cid, Integer pageNo,
						   HttpServletRequest request, ModelMap model) {
		for(int i=0;i<wids.length;i++){
			Content c=manager.findById(wids[i]);
			c.setTopLevel(topLevel[i]);
			manager.update(c);
		}
		log.info("update content priority.");
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend, queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:o_pigeonhole")
	@RequestMapping("/content/o_pigeonhole.do")
	public String pigeonhole(Integer[] ids,
							 String queryStatus, Integer queryTypeId,
							 Boolean queryTopLevel, Boolean queryRecommend,
							 Integer queryOrderBy, Integer cid, Integer pageNo,
							 HttpServletRequest request, ModelMap model) {
		for(int i=0;i<ids.length;i++){
			Content c=manager.findById(ids[i]);
			c.setStatus(ContentCheck.PIGEONHOLE);
			manager.update(CmsUtils.getUser(request), c, ContentRecord.ContentOperateType.pigeonhole);
		}
		log.info("update CmsFriendlink priority.");
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend, queryOrderBy, cid, pageNo, request, model);
	}
	
	@RequiresPermissions("content:o_unpigeonhole")
	@RequestMapping("/content/o_unpigeonhole.do")
	public String unpigeonhole(Integer[] ids,
							   String queryStatus, Integer queryTypeId,
							   Boolean queryTopLevel, Boolean queryRecommend,
							   Integer queryOrderBy, Integer cid, Integer pageNo,
							   HttpServletRequest request, ModelMap model) {
		for(int i=0;i<ids.length;i++){
			Content c=manager.findById(ids[i]);
			c.setStatus(ContentCheck.CHECKED);
			manager.update(CmsUtils.getUser(request), c, ContentOperateType.reuse);
		}
		log.info("update CmsFriendlink priority.");
		return list(queryStatus, queryTypeId, queryTopLevel, queryRecommend, queryOrderBy, cid, pageNo, request, model);
	}
	
	/**
	 * 推送至专题
	 * @param contentIds
	 * @param topicIds
	 */
	@RequiresPermissions("content:o_send_to_topic")
	@RequestMapping("/content/o_send_to_topic.do")
		public void sendToTopic(Integer contentIds[], Integer[] topicIds, HttpServletRequest request, HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		CmsUser user= CmsUtils.getUser(request);
		Boolean pass = true;
		if(user==null){
			ResponseUtils.renderJson(response, "false");
		}
		if (contentIds != null) {
			for(Integer contentId:contentIds){
				manager.addContentToTopics(contentId,topicIds);
			}
		}else{
			ResponseUtils.renderJson(response, "false");
		}
		json.put("pass", pass);
		ResponseUtils.renderJson(response, json.toString());
	}

	@RequiresPermissions("content:o_upload_attachment")
	@RequestMapping("/content/o_upload_attachment.do")
	public String uploadAttachment(
			@RequestParam(value = "attachmentFile", required = false) MultipartFile file,
			String attachmentNum, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user= CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file,request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return "content/attachment_iframe";
		}
		// TODO 检查允许上传的后缀
		try {
			String fileUrl;
			if (site.getConfig().getIsUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				fileUrl = dbFileService.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上访问地址
				fileUrl = request.getContextPath() + dbFilePath + fileUrl;
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上url前缀
				fileUrl = ftpUrl + fileUrl;
			} else {
				String ctx = request.getContextPath();
				fileUrl = fileRepository.storeByExt(site.getUploadPath(), ext,
						file);
				// 加上部署路径
				fileUrl = ctx + fileUrl;
			}
			userService.updateUploadSize(user.getUserId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			fileService.saveFileByPath(fileUrl, origName, false);
			model.addAttribute("attachmentPath", fileUrl);
			model.addAttribute("attachmentName", origName);
			model.addAttribute("attachmentNum", attachmentNum);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		}
		return "content/attachment_iframe";
	}

	@RequiresPermissions("content:o_upload_media")
	@RequestMapping("/content/o_upload_media.do")
	public void uploadMedia(
			@RequestParam(value = "mediaFile", required = false) MultipartFile file,
			String filename, HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file, request);
		JSONObject jsonArray = new JSONObject();
		JSONObject jsonObt = new JSONObject();
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			try {
				jsonObt.put("error", "file error");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// TODO 检查允许上传的后缀
		String fileUrl="";
		try {
			if (site.getConfig().getIsUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(dbFilePath.length());
					fileUrl = dbFileService.storeByFilename(filename, file
							.getInputStream());
				} else {
					fileUrl = dbFileService.storeByExt(site.getUploadPath(), ext,
							file.getInputStream());
					// 加上访问地址
					fileUrl = request.getContextPath() + dbFilePath + fileUrl;
				}
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(ftpUrl.length());
					fileUrl = ftp.storeByFilename(filename, file
							.getInputStream());
				} else {
					fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
							.getInputStream());
					// 加上url前缀
					fileUrl = ftpUrl + fileUrl;
				}
			} else {
				String ctx = request.getContextPath();
				if (!StringUtils.isBlank(filename)
						&& FilenameUtils.getExtension(filename).equals(ext)) {
					filename = filename.substring(ctx.length());
					fileUrl = fileRepository.storeByFilename(filename, file);
				} else {
					fileUrl = fileRepository.storeByExt(site.getUploadPath(),
							ext, file);
				}
				// 加上部署路径
				fileUrl = ctx + fileUrl;
			}

			try {
				jsonObt.put("url", fileUrl);
			} catch (JSONException e) {
				//e.printStackTrace();
			}
			userService.updateUploadSize(user.getUserId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			if(fileService.findByPath(fileUrl)!=null){
				fileService.saveFileByPath(fileUrl, origName, false);
			}
			model.addAttribute("mediaPath", fileUrl);
			model.addAttribute("mediaExt", ext);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		}
		try {
			jsonObt.put("name", origName);
			jsonObt.put("size", file.getSize());
			jsonArray.put("files", jsonObt);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ResponseUtils.renderJson(response, jsonArray.toString());
	}
	
	@RequiresPermissions("content_cycle:v_list")
	@RequestMapping("/content_cycle/v_list.do")
	public String cycleList(Integer queryTypeId, Boolean queryTopLevel,
							Boolean queryRecommend, Integer queryOrderBy, Integer cid,
							Integer pageNo, HttpServletRequest request, ModelMap model) {
		list(Content.ContentStatus.recycle.toString(), queryTypeId, queryTopLevel,
				queryRecommend, queryOrderBy, cid, pageNo, request, model);
		return "content/cycle_list";
	}

	@RequiresPermissions("content_cycle:o_recycle")
	@RequestMapping("/content_cycle/o_recycle.do")
	public String cycleRecycle(String queryStatus, Integer queryTypeId,
							   Boolean queryTopLevel, Boolean queryRecommend,
							   Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
							   HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		Content[] beans = manager.recycle(ids);
		for (Content bean : beans) {
			log.info("delete Content id={}", bean.getContentId());
		}
		return cycleList(queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}

	@RequiresPermissions("content_cycle:o_delete")
	@RequestMapping("/content_cycle/o_delete.do")
	public String cycleDelete(String queryStatus, Integer queryTypeId,
							  Boolean queryTopLevel, Boolean queryRecommend,
							  Integer queryOrderBy, Integer[] ids, Integer cid, Integer pageNo,
							  HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateDelete(ids, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		for(Integer id:ids){
			Content c=manager.findById(id);
			//处理附件
			manager.updateFileByContent(c, false);
		}
		Content[] beans = manager.deleteByIds(ids);
		for (Content bean : beans) {
			log.info("delete Content id={}", bean.getContentId());
		}
		return cycleList(queryTypeId, queryTopLevel, queryRecommend,
				queryOrderBy, cid, pageNo, request, model);
	}

	@RequiresPermissions("content:o_generateTags")
	@RequestMapping("/content/o_generateTags.do")
	public void generateTags(String title, HttpServletResponse response) throws JSONException {
		JSONObject json = new JSONObject();
		String tags="";
		if(StringUtils.isNotBlank(title)){
			tags=StrUtil.getKeywords(title, true);
		}
		json.put("tags", tags);
		ResponseUtils.renderJson(response, json.toString());
	}
	
	
	@RequiresPermissions("content:rank_list")
	@RequestMapping(value = "/content/rank_list.do")
	public String contentRankList(Integer orderBy, Integer pageNo, HttpServletRequest request,
								  HttpServletResponse response, ModelMap model) {
		model.addAttribute("orderBy", orderBy);
		model.addAttribute("pageNo", cpn(pageNo));
		model.addAttribute("pageSize", CookieUtils.getPageSize(request));
		model.addAttribute("site", CmsUtils.getSite(request));
		return "content/ranklist";
	}
	
	@RequiresPermissions("content:o_upload_docs")
	@RequestMapping("/content/o_upload_docs.do")
	public String uploadDocs(
			@RequestParam(value = "docFile", required = false) MultipartFile file,
			String docNum, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String origName = file.getOriginalFilename();
		String ext = FilenameUtils.getExtension(origName).toLowerCase(
				Locale.ENGLISH);
		WebErrors errors = validateUpload(file, request);
		if (errors.hasErrors()) {
			model.addAttribute("error", errors.getErrors().get(0));
			return "content/doc_iframe";
		}
		// TODO 检查允许上传的后缀
		try {
			String fileUrl;
			if (site.getConfig().getIsUploadToDb()) {
				String dbFilePath = site.getConfig().getDbFileUri();
				fileUrl = dbFileService.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上访问地址
				fileUrl = request.getContextPath() + dbFilePath + fileUrl;
			} else if (site.getUploadFtp() != null) {
				Ftp ftp = site.getUploadFtp();
				String ftpUrl = ftp.getUrl();
				fileUrl = ftp.storeByExt(site.getUploadPath(), ext, file
						.getInputStream());
				// 加上url前缀
				fileUrl = ftpUrl + fileUrl;
			} else {
				//String ctx = request.getContextPath();
				fileUrl = fileRepository.storeByExt(site.getUploadPath(), ext,
						file);
				// 加上部署路径
				//fileUrl = ctx + fileUrl;
			}
			userService.updateUploadSize(user.getUserId(), Integer.parseInt(String.valueOf(file.getSize()/1024)));
			model.addAttribute("docPath", fileUrl);
			model.addAttribute("docName", origName);
			model.addAttribute("docNum", docNum);
		} catch (IllegalStateException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		} catch (IOException e) {
			model.addAttribute("error", e.getMessage());
			log.error("upload file error!", e);
		}
		return "content/doc_iframe";
	}

	@RequiresPermissions("content:import_docs")
	@RequestMapping(value = "/content/import_docs.do", method = RequestMethod.GET)
	public String importDocs(HttpServletRequest request, ModelMap model) {
		CmsSite site= CmsUtils.getSite(request);
		CmsUser user= CmsUtils.getUser(request);
		Integer siteId = site.getSiteId();
		// 栏目列表
		List<Channel> channelList;
		Set<Channel> rights;
		if (user.getUserSite(siteId).getIsAllChannel()) {
			// 拥有所有栏目权限
			rights = null;
		} else {
			rights = user.getChannels(siteId);
		}
		List<Channel> topList = channelService.getTopListByRigth(user.getUserId(), siteId, true);
		channelList = Channel.getListForSelect(topList, rights, true);
		CmsModel m;
		m = modelService.getDefModel();
		// TODO m==null给出错误提示
		if (m == null) {
			throw new RuntimeException("default model not found!");
		}// 内容类型
		List<ContentType> typeList = contentTypeService.getList(false);
		model.addAttribute("typeList",typeList);
		model.addAttribute("channelList",channelList);
		model.addAttribute("model",m);
		return "content/import";
	}

	private void addAttibuteForQuery(ModelMap model, String queryTitle,
									 String queryInputUsername, String queryStatus, Integer queryTypeId,
									 Boolean queryTopLevel, Boolean queryRecommend,
									 Integer queryOrderBy, Integer pageNo) {
		if (!StringUtils.isBlank(queryTitle)) {
			model.addAttribute("queryTitle", queryTitle);
		}
		if (!StringUtils.isBlank(queryInputUsername)) {
			model.addAttribute("queryInputUsername", queryInputUsername);
		}
		if (queryTypeId != null) {
			model.addAttribute("queryTypeId", queryTypeId);
		}
		if (queryStatus != null) {
			model.addAttribute("queryStatus", queryStatus);
		}
		if (queryTopLevel != null) {
			model.addAttribute("queryTopLevel", queryTopLevel);
		}
		if (queryRecommend != null) {
			model.addAttribute("queryRecommend", queryRecommend);
		}
		if (queryOrderBy != null) {
			model.addAttribute("queryOrderBy", queryOrderBy);
		}
		if (pageNo != null) {
			model.addAttribute("pageNo", pageNo);
		}
	}

	private List<String> getTplContent(CmsSite site, CmsModel model, String tpl) {
		String sol = site.getSolutionPath();
		String tplPath = site.getTplPath();
		List<String> tplList = tplService.getNameListByPrefix(model
				.getTplContent(sol, false));
		tplList = CoreUtils.tplTrim(tplList, tplPath, tpl);
		return tplList;
	}
	
	private List<String> getTplMobileContent(CmsSite site, CmsModel model, String tpl) {
		String sol = site.getMobileSolutionPath();
		String tplPath = site.getTplPath();
		List<String> tplList = tplService.getNameListByPrefix(model
				.getTplContent(sol, false));
		tplList = CoreUtils.tplTrim(tplList, tplPath, tpl);
		return tplList;
	}
	
	private WebErrors validateTree(String path, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		// if (errors.ifBlank(path, "path", 255)) {
		// return errors;
		// }
		return errors;
	}

	private WebErrors validateAdd(Integer cid, Integer modelId, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (cid == null) {
			return errors;
		}
		Channel c = channelService.findById(cid);
		if (errors.ifNotExist(c, Channel.class, cid)) {
			return errors;
		}
		//所选发布内容模型不在栏目模型范围内
		if(modelId!=null){
			CmsModel m=modelService.findById(modelId);
			if(errors.ifNotExist(m, CmsModel.class, modelId)){
				return errors;
			}
			//默认没有配置的情况下modelIds为空 则允许添加
			if(c.getModelIds().size()>0&&!c.getModelIds().contains(modelId.toString())){
				errors.addErrorCode("channel.modelError", c.getName(),m.getModelName());
			}
		}
		Integer siteId = CmsUtils.getSiteId(request);
		if (!c.getSite().getSiteId().equals(siteId)) {
			errors.notInSite(Channel.class, cid);
			return errors;
		}
		return errors;
	}

	private WebErrors validateSave(Content bean, Integer channelId,
								   HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		bean.setSite(site);
		if (errors.ifNull(channelId, "channelId")) {
			return errors;
		}
		Channel channel = channelService.findById(channelId);
		if (errors.ifNotExist(channel, Channel.class, channelId)) {
			return errors;
		}
		if (channel.getChild().size() > 0) {
			errors.addErrorCode("content.error.notLeafChannel");
		}
		//所选发布内容模型不在栏目模型范围内
		if(bean.getModel().getModelId()!=null){
			CmsModel m=bean.getModel();
			if(errors.ifNotExist(m, CmsModel.class, bean.getModel().getModelId())){
				return errors;
			}
			//默认没有配置的情况下modelIds为空 则允许添加
			if(channel.getModelIds().size()>0&&!channel.getModelIds().contains(bean.getModel().getModelId().toString())){
				errors.addErrorCode("channel.modelError", channel.getName(),m.getModelName());
			}
		}
		return errors;
	}

	private WebErrors validateEdit(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getSiteId(), errors)) {
			return errors;
		}
		// Content content = manager.findById(id);
		// TODO 是否有编辑的数据权限。
		return errors;
	}

	private WebErrors validateUpdate(Integer id, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		if (vldExist(id, site.getSiteId(), errors)) {
			return errors;
		}
		Content content = manager.findById(id);
		// TODO 是否有编辑的数据权限。
		// 是否有审核后更新权限。
		if (!content.isHasUpdateRight()) {
			errors.addErrorCode("content.error.afterCheckUpdate");
			return errors;
		}
		return errors;
	}

	private WebErrors validateDelete(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
//		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		if(ids!=null&&ids.length>0){
			for (Integer id : ids) {
				/*
				if (vldExist(id, site.getId(), errors)) {
					return errors;
				}
				*/
				Content content = manager.findById(id);
				// TODO 是否有编辑的数据权限。
				// 是否有审核后删除权限。
				if (!content.isHasDeleteRight()) {
					errors.addErrorCode("content.error.afterCheckDelete");
					return errors;
				}

			}
		}
		return errors;
	}

	private WebErrors validateCheck(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, site.getSiteId(), errors);
		}
		return errors;
	}

	private WebErrors validateStatic(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, site.getSiteId(), errors);
		}
		return errors;
	}

	private WebErrors validateReject(Integer[] ids, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		errors.ifEmpty(ids, "ids");
		for (Integer id : ids) {
			vldExist(id, site.getSiteId(), errors);
		}
		return errors;
	}

	private WebErrors validateUpload(MultipartFile file,
									 HttpServletRequest request) {
		String origName = file.getOriginalFilename();
		CmsUser user= CmsUtils.getUser(request);
		String ext = FilenameUtils.getExtension(origName).toLowerCase(Locale.ENGLISH);
		int fileSize = (int) (file.getSize() / 1024);
		WebErrors errors = WebErrors.create(request);
		if (errors.ifNull(file, "file")) {
			return errors;
		}
		if(origName!=null&&(origName.contains("/")||origName.contains("\\")||origName.indexOf("\0")!=-1)){
			errors.addErrorCode("upload.error.filename", origName);
		}
		//非允许的后缀
		if(!user.isAllowSuffix(ext)){
			errors.addErrorCode("upload.error.invalidsuffix", ext);
			return errors;
		}
		//超过附件大小限制
		if(!user.isAllowMaxFile((int)(file.getSize()/1024))){
			errors.addErrorCode("upload.error.toolarge",origName,user.getGroup().getAllowMaxFile());
			return errors;
		}
		//超过每日上传限制
		if (!user.isAllowPerDay(fileSize)) {
			long laveSize=user.getGroup().getAllowPerDay()-user.getUploadSize();
			if(laveSize<0){
				laveSize=0;
			}
			errors.addErrorCode("upload.error.dailylimit", laveSize);
		}
		return errors;
	}

	private boolean vldExist(Integer id, Integer siteId, WebErrors errors) {
		if (errors.ifNull(id, "id")) {
			return true;
		}
		Content entity = manager.findById(id);
		if (errors.ifNotExist(entity, Content.class, id)) {
			return true;
		}
		if (!entity.getSite().getSiteId().equals(siteId)) {
			errors.notInSite(Content.class, id);
			return true;
		}
		return false;
	}
	
	private ContentTxt copyContentTxtImg(ContentTxt txt, CmsSite site){
		if(StringUtils.isNotBlank(txt.getTxt())){
			txt.setTxt(copyTxtHmtlImg(txt.getTxt(), site));
		}
		if(StringUtils.isNotBlank(txt.getTxt1())){
			txt.setTxt1(copyTxtHmtlImg(txt.getTxt1(), site));
		}	
		if(StringUtils.isNotBlank(txt.getTxt2())){
			txt.setTxt2(copyTxtHmtlImg(txt.getTxt2(), site));
		}
		if(StringUtils.isNotBlank(txt.getTxt3())){
			txt.setTxt3(copyTxtHmtlImg(txt.getTxt3(), site));
		}
		return txt;
	}
	
	private String copyTxtHmtlImg(String txtHtml, CmsSite site){
		List<String> imgUrls= ImageUtils.getImageSrc(txtHtml);
		for(String img:imgUrls){
			txtHtml=txtHtml.replace(img, imageService.crawlImg(img,site));
		}
		return txtHtml;
	}
	@Resource
	private ChannelService channelService;
	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;
	@Resource
	private ModelItemService modelItemService;
	@Resource
	private TopicService topicService;
	@Resource
	private GroupService groupService;
	@Resource
	private ContentTypeService contentTypeService;
	@Resource
	private TplService tplService;
	@Resource
	private FileRepository fileRepository;
	@Resource
	private DbFileService dbFileService;
	@Resource
	private LogService logService;
	@Resource
	private ContentService manager;
	@Resource
	private FileService fileService;
	@Resource
	private SiteService siteService;
	@Resource
	private ImageService imageService;
}