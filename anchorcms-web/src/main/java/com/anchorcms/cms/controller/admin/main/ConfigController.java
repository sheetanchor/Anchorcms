package com.anchorcms.cms.controller.admin.main;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anchorcms.cms.service.main.ContentQueryFreshTimeCacheService;
import com.anchorcms.common.web.RequestUtils;
import com.anchorcms.common.web.ResponseUtils;
import com.anchorcms.core.model.*;
import com.anchorcms.core.service.*;
import com.anchorcms.core.web.WebErrors;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.anchorcms.common.constants.Constants.MESSAGE;


@Controller
public class ConfigController {
	private static final Logger log = LoggerFactory
			.getLogger(ConfigController.class);

	@RequiresPermissions("config:v_system_edit")
	@RequestMapping("/config/v_system_edit.do")
	public String systemEdit(HttpServletRequest request, ModelMap model) {
		model.addAttribute("cmsConfig", manager.get());
		return "config/system_edit";
	}

	@RequiresPermissions("config:o_system_update")
	@RequestMapping("/config/o_system_update.do")
	public String systemUpdate(CmsConfig bean, Integer pageNo,
							   HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateSystemUpdate(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		bean = manager.update(bean);
		model.addAttribute("message", "global.success");
		log.info("update systemConfig of CmsConfig.");
		logService.operating(request, "cmsConfig.log.systemUpdate", null);
		return systemEdit(request, model);
	}

	@RequiresPermissions("config:v_mark_edit")
	@RequestMapping("/config/v_mark_edit.do")
	public String markEdit(HttpServletRequest request, ModelMap model) {
		model.addAttribute("markConfig", manager.get().getMarkConfig());
		return "config/mark_edit";
	}

	@RequiresPermissions("config:o_mark_update")
	@RequestMapping("/config/o_mark_update.do")
	public String markUpdate(MarkConfig bean, Integer pageNo,
							 HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateMarkUpdate(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		bean = manager.updateMarkConfig(bean);
		model.addAttribute("message", "global.success");
		log.info("update markConfig of CmsConfig.");
		logService.operating(request, "cmsConfig.log.markUpdate", null);
		return markEdit(request, model);
	}

	@RequiresPermissions("config:v_member_edit")
	@RequestMapping("/config/v_member_edit.do")
	public String memberEdit(HttpServletRequest request, ModelMap model) {
		model.addAttribute("memberConfig", manager.get().getMemberConfig());
		return "config/member_edit";
	}

	@RequiresPermissions("config:o_member_update")
	@RequestMapping("/config/o_member_update.do")
	public String memberUpdate(MemberConfig bean, Config.ConfigLogin configLogin,
							   Config.ConfigEmailSender emailSender, Config.ConfigMessageTemplate msgTpl,
							   HttpServletRequest request, ModelMap model) {
		WebErrors errors = validateMemberUpdate(bean, request);
		if (errors.hasErrors()) {
			return errors.showErrorPage(model);
		}
		manager.updateMemberConfig(bean);
		model.addAttribute("message", "global.success");
		log.info("update memberConfig of CmsConfig.");
		logService.operating(request, "cmsConfig.log.memberUpdate", null);
		return memberEdit(request, model);
	}
	
	@RequiresPermissions("config:v_api_edit")
	@RequestMapping("/config/v_api_edit.do")
	public String apiEdit(HttpServletRequest request, ModelMap model) {
		model.addAttribute("configAttr", manager.get().getConfigAttr());
		return "config/api_edit";
	}
	
	@RequiresPermissions("config:o_aip_update")
	@RequestMapping("/config/o_aip_update.do")
	public String apiUpdate(CmsConfigAttr configAttr, HttpServletRequest request, ModelMap model) {
		manager.updateConfigAttr(configAttr);
		model.addAttribute("message", "global.success");
		log.info("update attrs of CmsConfig.");
		return apiEdit(request, model);
	}
	
	@RequiresPermissions("config:v_attr_edit")
	@RequestMapping("/config/v_attr_edit.do")
	public String attrEdit(HttpServletRequest request, ModelMap model) {
		model.addAttribute("configAttr", manager.get().getConfigAttr());
		return "config/attr_edit";
	}

	@RequiresPermissions("config:o_attr_update")
	@RequestMapping("/config/o_attr_update.do")
	public String attrUpdate(CmsConfigAttr configAttr,HttpServletRequest request, ModelMap model) {
		manager.updateConfigAttr(configAttr);
		model.addAttribute("message", "global.success");
		log.info("update attrs of CmsConfig.");
		return attrEdit(request, model);
	}
	
	@RequiresPermissions("config:o_clear_fresh_time_cache")
	@RequestMapping("/config/o_clear_fresh_time_cache.do")
	public String clearContentQueryFreshTimeCache(HttpServletRequest request, ModelMap model) {
		contentQueryFreshTimeCacheService.clearCache();
		return attrEdit(request, model);
	}
	
	@RequiresPermissions("config:v_sso_edit")
	@RequestMapping("/config/v_sso_edit.do")
	public String ssoAuthenticateEdit(HttpServletRequest request, ModelMap model) {
		CmsConfig config=manager.get();
		model.addAttribute("ssoMap", config.getSsoAttr());
		model.addAttribute("configAttr", config.getConfigAttr());
		return "config/sso_edit";
	}
	
	@RequiresPermissions("config:o_sso_update")
	@RequestMapping("/config/o_sso_update.do")
	public String ssoAuthenticateUpdate(HttpServletRequest request, ModelMap model) {
		Map<String,String>ssoMap= RequestUtils.getRequestMap(request, "attr_");
		manager.updateSsoAttr(ssoMap);
		model.addAttribute("message", "global.success");
		log.info("update attrs of CmsConfig.");
		return ssoAuthenticateEdit(request, model);
	}
	
	@RequiresPermissions("config:register_item_list")
	@RequestMapping("/config/register_item_list.do")
	public String registerItemList(HttpServletRequest request, ModelMap model) {
		model.addAttribute("registerItems", configItemService.getList(manager.get().getConfigId(), CmsConfigItem.CATEGORY_REGISTER));
		return "config/register_item_list";
	}
	
	@RequiresPermissions("config:register_item_add")
	@RequestMapping("/config/register_item_add.do")
	public String registerItemAdd(HttpServletRequest request, ModelMap model) {
		return "config/register_item_add";
	}
	
	@RequiresPermissions("config:register_item_save")
	@RequestMapping("/config/register_item_save.do")
	public String registerItemSave(CmsConfigItem bean,HttpServletRequest request, ModelMap model) {
		bean.setConfig(manager.get());
		bean.setCategory(CmsConfigItem.CATEGORY_REGISTER);
		configItemService.save(bean);
		return registerItemList(request, model);
	}
	
	@RequiresPermissions("config:register_item_edit")
	@RequestMapping("/config/register_item_edit.do")
	public String registerItemEdit(Integer id,HttpServletRequest request, ModelMap model) {
		CmsConfigItem item = configItemService.findById(id);
		model.addAttribute("cmsConfigItem", item);
		return "config/register_item_edit";
	}
	
	@RequiresPermissions("config:register_item_update")
	@RequestMapping("/config/register_item_update.do")
	public String registerItemUpdate(CmsConfigItem bean,HttpServletRequest request, ModelMap model) {
		configItemService.update(bean);
		return registerItemList(request, model);
	}
	
	@RequiresPermissions("config:register_item_priority")
	@RequestMapping("/config/register_item_priority.do")
	public String priority(Integer[] wids, Integer[] priority, String[] label,
			Boolean[] single, Boolean[] display, Integer modelId,
			Boolean isChannel, HttpServletRequest request, ModelMap model) {
		if (wids != null && wids.length > 0) {
			configItemService.updatePriority(wids, priority, label);
		}
		model.addAttribute(MESSAGE, "global.success");
		return registerItemList(request, model);
	}
	
	@RequiresPermissions("config:register_item_delete")
	@RequestMapping("/config/register_item_delete.do")
	public String delete(Integer[] ids, Integer modelId, Boolean isChannel,
			HttpServletRequest request, ModelMap model) {
		CmsConfigItem[] beans = configItemService.deleteByIds(ids);
		for (CmsConfigItem bean : beans) {
			log.info("delete CmsConfigItem id={}", bean.getModelitemId());
		}
		return registerItemList(request, model);
	}

	@RequiresPermissions("config:v_login_edit")
	@RequestMapping("/config/v_login_edit.do")
	public String loginEdit(HttpServletRequest request, ModelMap model) {
		model.addAttribute("configLogin", sysConfigService.getConfigLogin());
		model.addAttribute("emailSender", sysConfigService.getEmailSender());
		model.addAttribute("forgotPasswordTemplate", sysConfigService.getForgotPasswordMessageTemplate());
		model.addAttribute("registerTemplate", sysConfigService.getRegisterMessageTemplate());
		return "config/login_edit";
	}

	@RequiresPermissions("config:o_login_update")
	@RequestMapping("/config/o_login_update.do")
	public String loginUpdate(Config.ConfigLogin configLogin,
							  Config.ConfigEmailSender emailSender, Config.ConfigMessageTemplate msgTpl,
							  HttpServletRequest request, ModelMap model) {
		//留空则默认原有密码
		if(StringUtils.isBlank(emailSender.getPassword())){
			emailSender.setPassword(sysConfigService.getEmailSender().getPassword());
		}
		sysConfigService.updateOrSave(configLogin.getAttr());
		sysConfigService.updateOrSave(emailSender.getAttr());
		sysConfigService.updateOrSave(msgTpl.getAttr());
		model.addAttribute("message", "global.success");
		log.info("update loginCoinfig of Config.");
		logService.operating(request, "cmsConfig.log.loginUpdate", null);
		return loginEdit(request, model);
	}
	
	
	@RequiresPermissions("config:v_checkAccessPath")
	@RequestMapping("/config/v_checkAccessPath.do")
	public void checkAccessPathJson( HttpServletResponse response) {
		String pass=siteMng.hasRepeatByProperty("accessPath")?"false":"true";
		ResponseUtils.renderJson(response, pass);
	}
	
	@RequiresPermissions("config:v_checkDomain")
	@RequestMapping("/config/v_checkDomain.do")
	public void checkDomainJson( HttpServletResponse response) {
		String pass=siteMng.hasRepeatByProperty("domain")?"false":"true";
		ResponseUtils.renderJson(response, pass);
	}

	private WebErrors validateSystemUpdate(CmsConfig bean,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}

	private WebErrors validateMarkUpdate(MarkConfig bean,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}

	private WebErrors validateMemberUpdate(MemberConfig bean,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		return errors;
	}

	@Autowired
	private SysConfigService sysConfigService;
	@Autowired
	private ConfigItemService configItemService;
	@Autowired
	private LogService logService;
	@Autowired
	private ConfigService manager;
	@Autowired
	private SiteService siteMng;
	@Autowired
	private ContentQueryFreshTimeCacheService contentQueryFreshTimeCacheService;
}