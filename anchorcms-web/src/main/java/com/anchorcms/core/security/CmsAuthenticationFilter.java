package com.anchorcms.core.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anchorcms.common.security.CaptchaErrorException;
import com.anchorcms.common.security.CaptchaRequiredException;
import com.anchorcms.common.security.DisabledException;
import com.anchorcms.common.security.InactiveException;
import com.anchorcms.common.web.CookieUtils;
import com.anchorcms.common.web.RequestUtils;
import com.anchorcms.common.web.session.SessionProvider;
import com.anchorcms.core.model.CmsUser;
import com.anchorcms.core.model.UnifiedUser;
import com.anchorcms.core.service.LogService;
import com.anchorcms.core.service.UserService;
import com.anchorcms.core.service.UnifiedUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * CmsAuthenticationFilter自定义登录认证filter
 */
public class CmsAuthenticationFilter extends FormAuthenticationFilter {
	
	private Logger logger = LoggerFactory.getLogger(CmsAuthenticationFilter.class);
	
	public static final String COOKIE_ERROR_REMAINING = "_error_remaining";
	/**
	 * 验证码名称
	 */
	public static final String CAPTCHA_PARAM = "captcha";
	/**
	 * 返回URL
	 */
	public static final String RETURN_URL = "returnUrl";
	/**
	 * 登录错误地址
	 */
	public static final String FAILURE_URL = "failureUrl";

	protected boolean executeLogin(ServletRequest request,ServletResponse response) throws Exception {
		AuthenticationToken token = createToken(request, response);
		if (token == null) {
			String msg = "create AuthenticationToken error";
			throw new IllegalStateException(msg);
		}
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String username = (String) token.getPrincipal();
		boolean adminLogin=false;
		if (req.getRequestURI().startsWith(req.getContextPath() + getAdminPrefix())){
			adminLogin=true;
		}
		String failureUrl = req.getParameter(FAILURE_URL);
		//验证码校验
		if (isCaptchaRequired(username,req, res)) {
			String captcha = request.getParameter(CAPTCHA_PARAM);
			if (captcha != null) {
				if (!imageCaptchaService.validateResponseForID(session.getSessionId(req, res), captcha)) {
					return onLoginFailure(token,failureUrl,adminLogin,new CaptchaErrorException(), request, response);
				}
			} else {
				return onLoginFailure(token,failureUrl,adminLogin,new CaptchaRequiredException(),request, response);
			}
		}
		CmsUser user=userMng.findByUsername(username);
		if(user!=null){
			if(isDisabled(user)){
				return onLoginFailure(token,failureUrl,adminLogin,new DisabledException(),request, response);
			}
			if(!isActive(user)){
				return onLoginFailure(token,failureUrl,adminLogin,new InactiveException(),request, response);
			}
		}
		try {
			Subject subject = getSubject(request, response);
			subject.login(token);;
			return onLoginSuccess(token,adminLogin,subject, request, response);
		} catch (AuthenticationException e) {
			//e.printStackTrace();
			return onLoginFailure(token,failureUrl,adminLogin, e, request, response);
		}
	}

	public boolean onPreHandle(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		boolean isAllowed = isAccessAllowed(request, response, mappedValue);
		//登录跳转
		if (isAllowed && isLoginRequest(request, response)) {
			try {
				issueSuccessRedirect(request, response);
			} catch (Exception e) {
				logger.error("", e);
			}
			return false;
		}
		return isAllowed || onAccessDenied(request, response, mappedValue);
	}
	

	protected void issueSuccessRedirect(ServletRequest request, ServletResponse response)
			throws Exception {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String successUrl = req.getParameter(RETURN_URL);
		if (StringUtils.isBlank(successUrl)) {
			if (req.getRequestURI().startsWith(
					req.getContextPath() + getAdminPrefix())) {
				// 后台直接返回首页
				successUrl = getAdminIndex();
				// 清除SavedRequest
				WebUtils.getAndClearSavedRequest(request);
				WebUtils.issueRedirect(request, response, successUrl, null,true);
				return;
			} else {
				successUrl = getSuccessUrl();
			}
		}
		WebUtils.getAndClearSavedRequest(request);
		WebUtils.issueRedirect(request, response, successUrl, null,true);
		//WebUtils.redirectToSavedRequest(req, res, successUrl);
	}

	protected boolean isLoginRequest(ServletRequest req, ServletResponse resp) {
		return pathsMatch(getLoginUrl(), req)|| pathsMatch(getAdminLogin(), req);
	}

	/**
	 * 登录成功
	 */
	private boolean onLoginSuccess(AuthenticationToken token,boolean adminLogin,Subject subject,
			ServletRequest request, ServletResponse response)
			throws Exception {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String username = (String) subject.getPrincipal();
		CmsUser user = userService.findByUsername(username);
		String ip = RequestUtils.getIpAddr(req);
		Date now = new Timestamp(System.currentTimeMillis());
		String userSessionId=session.getSessionId((HttpServletRequest)request, (HttpServletResponse)response);
		userMng.updateLoginInfo(user.getUserId(), ip,now,userSessionId);
		//管理登录
		if(adminLogin){
			logService.loginSuccess(req, user);
		}
		unifiedUserService.updateLoginSuccess(user.getUserId(), ip);
		loginCookie(username, req, res);
		return super.onLoginSuccess(token, subject, request, response);
	}

	/**
	 * 登录失败
	 */
	private boolean onLoginFailure(AuthenticationToken token,String failureUrl,boolean adminLogin,AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		String username = (String) token.getPrincipal();
		HttpServletRequest req = (HttpServletRequest) request;
		String ip = RequestUtils.getIpAddr(req);
		CmsUser user = userMng.findByUsername(username);
		if(user!=null){
			unifiedUserService.updateLoginError(user.getUserId(), ip);
		}
		//管理登录
		if(adminLogin){
			logService.loginFailure(req,"username=" + username);
		}
		return onLoginFailure(failureUrl,token, e, request, response);
	}
	
	private boolean onLoginFailure(String failureUrl,AuthenticationToken token,
			AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		String className = e.getClass().getName();
        request.setAttribute(getFailureKeyAttribute(), className);
        if(failureUrl!=null||StringUtils.isNotBlank(failureUrl)){
        	try {
    			request.getRequestDispatcher(failureUrl).forward(request, response);
    		}  catch (Exception e1) {
    			//e1.printStackTrace();
    		}
        }
        return true;
	}
	
	private void loginCookie(String username,HttpServletRequest request,HttpServletResponse response){
		String domain = request.getServerName();
		if (domain.indexOf(".") > -1) {
			domain= domain.substring(domain.indexOf(".") + 1);
		}
		CookieUtils.addCookie(request, response,  "JSESSIONID",  session.getSessionId(request, response), null, domain,"/");
		try {
			//中文乱码
			CookieUtils.addCookie(request, response,   "username",  URLEncoder.encode(username,"utf-8"), null, domain,"/");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
		CookieUtils.addCookie(request, response,  "sso_logout",  null,0,domain,"/");
	}
	
	private boolean isCaptchaRequired(String username,HttpServletRequest request,
			HttpServletResponse response) {
		Integer errorRemaining = unifiedUserService.errorRemaining(username);
		String captcha=RequestUtils.getQueryParam(request, CAPTCHA_PARAM);
		// 如果输入了验证码，那么必须验证；如果没有输入验证码，则根据当前用户判断是否需要验证码。
		if (!StringUtils.isBlank(captcha)|| (errorRemaining != null && errorRemaining < 0)) {
			return true;
		}
		return false;
	}
	
	//用户禁用返回true 未查找到用户或者非禁用返回false
	private boolean isDisabled(CmsUser user){
		if(user.getIsDisabled()){
			return true;
		}else{
			return false;
		}
	}
	
	//用户激活了返回true 未查找到用户或者非禁用返回false
	private boolean isActive(CmsUser user){
		UnifiedUser unifiedUser= unifiedUserService.findById(user.getUserId());
		if(unifiedUser!=null){
			if(unifiedUser.getActivation()){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	@Resource
	private UserService userMng;
	@Resource
	private UnifiedUserService unifiedUserService;
	@Resource
	private UserService userService;

	@Resource(name = "httpSession")
	private SessionProvider session;
	@Resource
	private LogService logService;
	private ImageCaptchaService imageCaptchaService;


	
	private String adminPrefix;
	private String adminIndex;
	private String adminLogin;

	public String getAdminPrefix() {
		return adminPrefix;
	}

	public void setAdminPrefix(String adminPrefix) {
		this.adminPrefix = adminPrefix;
	}

	public String getAdminIndex() {
		return adminIndex;
	}

	public void setAdminIndex(String adminIndex) {
		this.adminIndex = adminIndex;
	}
	
	public String getAdminLogin() {
		return adminLogin;
	}

	public void setAdminLogin(String adminLogin) {
		this.adminLogin = adminLogin;
	}

	public ImageCaptchaService getImageCaptchaService() {
		return imageCaptchaService;
	}

	public void setImageCaptchaService(ImageCaptchaService imageCaptchaService) {
		this.imageCaptchaService = imageCaptchaService;
	}
}
