package com.anchorcms.cms.controller.member;

import com.anchorcms.common.utils.CmsUtils;
import com.anchorcms.common.utils.FrontUtils;
import com.anchorcms.common.web.ResponseUtils;
import com.anchorcms.core.model.CmsSite;
import com.anchorcms.core.model.CmsUser;
import com.anchorcms.core.model.CmsUserExt;
import com.anchorcms.core.model.MemberConfig;
import com.anchorcms.core.service.UserAccountService;
import com.anchorcms.core.service.UserExtService;
import com.anchorcms.core.service.UserService;
import com.anchorcms.core.web.WebErrors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.anchorcms.common.constants.Constants.TPLDIR_MEMBER;

/**
 * @Author 阁楼麻雀
 * @Date 2016/12/15 16:04
 * @Desc 用户中心
 */

@Controller
public class MemberController {
	private static final Logger log = LoggerFactory.getLogger(MemberController.class);

	public static final String MEMBER_CENTER = "tpl.memberCenter";
	public static final String MEMBER_PROFILE = "tpl.memberProfile";
	public static final String MEMBER_PORTRAIT = "tpl.memberPortrait";
	public static final String MEMBER_PASSWORD = "tpl.memberPassword";
	public static final String MEMBER_ACCOUNT = "tpl.memberAccount";
	
	/**
	 * 会员中心页
	 * 
	 * 如果没有登录则跳转到登陆页
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/index.jspx", method = RequestMethod.GET)
	public String index(HttpServletRequest request,
						HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_CENTER);
	}

	/**
	 * 个人资料输入页
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/profile.jspx", method = RequestMethod.GET)
	public String profileInput(HttpServletRequest request,
							   HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_PROFILE);
	}
	/**
	 * 更换头像
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/portrait.jspx", method = RequestMethod.GET)
	public String portrait(HttpServletRequest request,
						   HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_PORTRAIT);
	}

	/**
	 * 个人资料提交页
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/member/profile.jspx", method = RequestMethod.POST)
	public String profileSubmit(CmsUserExt ext, String nextUrl,
								HttpServletRequest request, HttpServletResponse response,
								ModelMap model) throws IOException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		ext.setUserId(user.getUserId());
		userExtService.update(ext, user);
		log.info("update CmsUserExt success. id={}", user.getUserId());
		return FrontUtils.showSuccess(request, model, nextUrl);
	}

	/**
	 * 密码修改输入页
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/pwd.jspx", method = RequestMethod.GET)
	public String passwordInput(HttpServletRequest request,
								HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_PASSWORD);
	}

	/**
	 * 密码修改提交页
	 * 
	 * @param origPwd
	 *            原始密码
	 * @param newPwd
	 *            新密码
	 * @param email
	 *            邮箱
	 * @param nextUrl
	 *            下一个页面地址
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/member/pwd.jspx", method = RequestMethod.POST)
	public String passwordSubmit(String origPwd, String newPwd, String email,
								 String nextUrl, HttpServletRequest request,
								 HttpServletResponse response, ModelMap model) throws IOException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		WebErrors errors = validatePasswordSubmit(user.getUserId(), origPwd,
				newPwd, email, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		userService.updatePwdEmail(user.getUserId(), newPwd, email);
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
	/**
	 * 完善账户资料
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/member/account.jspx", method = RequestMethod.GET)
	public String accountInput(HttpServletRequest request,
							   HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, MEMBER_ACCOUNT);
	}
	
	//完善用户账户资料
	@RequestMapping(value = "/member/account.jspx", method = RequestMethod.POST)
	public String accountSubmit(String accountWeiXin, String accountAlipy,
								Short drawAccount, String nextUrl, HttpServletRequest request,
								HttpServletResponse response, ModelMap model) throws IOException {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		MemberConfig mcfg = site.getConfig().getMemberConfig();
		// 没有开启会员功能
		if (!mcfg.isMemberOn()) {
			return FrontUtils.showMessage(request, model, "member.memberClose");
		}
		if (user == null) {
			return FrontUtils.showLogin(request, model, site);
		}

		WebErrors errors= WebErrors.create(request);
		if(drawAccount==null)
		{
			errors.addErrorCode("error.needParams");
		}else{
			if(!(drawAccount==0&&StringUtils.isNotBlank(accountWeiXin)
					||drawAccount==1&&StringUtils.isNotBlank(accountAlipy))){
				errors.addErrorCode("error.needParams");
			}
		}
		if(errors.hasErrors()){
			return FrontUtils.showError(request, response, model, errors);
		}
		userAccountService.updateAccountInfo(accountWeiXin, accountAlipy, drawAccount,user);
		log.info("update CmsUserExt success. id={}", user.getUserId());
		return FrontUtils.showSuccess(request, model, nextUrl);
	}
	
	

	/**
	 * 验证密码是否正确
	 * 
	 * @param origPwd
	 *            原密码
	 * @param request
	 * @param response
	 */
	@RequestMapping("/member/checkPwd.jspx")
	public void checkPwd(String origPwd, HttpServletRequest request,
						 HttpServletResponse response) {
		CmsUser user = CmsUtils.getUser(request);
		boolean pass = userService.isPasswordValid(user.getUserId(), origPwd);
		ResponseUtils.renderJson(response, pass ? "true" : "false");
	}

	private WebErrors validatePasswordSubmit(Integer id, String origPwd,
											 String newPwd, String email, HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifBlank(origPwd, "origPwd", 100)) {
			return errors;
		}
		if (errors.ifMaxLength(newPwd, "newPwd", 100)) {
			return errors;
		}
		if (errors.ifNotEmail(email, "email", 100)) {
			return errors;
		}
		if (!userService.isPasswordValid(id, origPwd)) {
			errors.addErrorCode("member.origPwdInvalid");
			return errors;
		}
		return errors;
	}

	@Resource
	private UserService userService;
	@Resource
	private UserExtService userExtService;
	@Resource
	private UserAccountService userAccountService;
}
