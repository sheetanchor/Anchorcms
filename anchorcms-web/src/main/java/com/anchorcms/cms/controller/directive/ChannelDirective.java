package com.anchorcms.cms.controller.directive;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.anchorcms.cms.model.main.Channel;
import com.anchorcms.cms.service.main.ChannelService;
import com.anchorcms.common.utils.FrontUtils;
import com.anchorcms.common.web.freemarker.DefaultObjectWrapperBuilderFactory;
import com.anchorcms.common.web.freemarker.DirectiveUtils;
import com.anchorcms.core.model.CmsSite;
import org.apache.commons.lang.StringUtils;


import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import javax.annotation.Resource;

import static com.anchorcms.common.constants.Constants.UTF8;
import static com.anchorcms.common.web.freemarker.DirectiveUtils.OUT_BEAN;

/**
 * 栏目对象标签
 */
public class ChannelDirective implements TemplateDirectiveModel {
	
	/**
	 * 输入参数，栏目ID。
	 */
	public static final String PARAM_ID = "id";
	/**
	 * 输入参数，栏目路径。
	 */
	public static final String PARAM_PATH = "path";
	/**
	 * 输入参数，站点ID。存在时，获取该站点栏目，不存在时获取当前站点栏目。
	 */
	public static final String PARAM_SITE_ID = "siteId";
	/**
	 * 没有栏目id参数 模板路径
	 */
	public static final String TPL_CHANNEL_PARAMREQUIRED = "/WEB-INF/tpl/cms_sys_defined/channel/ParamsRequiredException.html";
	/**
	 * 没有找到栏目 模板路径
	 */
	public static final String TPL_CHANNEL_NOT_FOUND = "/WEB-INF/tpl/cms_sys_defined/channel/NotFoundChannelException.html";

	
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		CmsSite site = FrontUtils.getSite(env);
		Integer id = DirectiveUtils.getInt(PARAM_ID, params);
		Channel channel;
		boolean hasParam=true;
		if (id != null) {
			channel = channelService.findById(id);
		} else {
			String path = DirectiveUtils.getString(PARAM_PATH, params);
			if (StringUtils.isBlank(path)) {
				// 如果path不存在，那么id必须存在。
				hasParam=false;
				//throw new ParamsRequiredException(PARAM_ID);
			}
			Integer siteId = DirectiveUtils.getInt(PARAM_SITE_ID, params);
			if (siteId == null) {
				siteId = site.getSiteId();
			}
			channel = channelService.findByPathForTag(path, siteId);
		}
		Map<String, TemplateModel> paramWrap = new HashMap<String, TemplateModel>(
				params);
		if(channel!=null){
			paramWrap.put(OUT_BEAN, DefaultObjectWrapperBuilderFactory.getDefaultObjectWrapper().wrap(channel));
		}else{
			if(hasParam){
				env.include(TPL_CHANNEL_NOT_FOUND, UTF8, true);
			}else{
				env.include(TPL_CHANNEL_PARAMREQUIRED, UTF8, true);
			}
		}
		Map<String, TemplateModel> origMap = DirectiveUtils.addParamsToVariable(env, paramWrap);
	    if (body != null) {  
		    body.render(env.getOut());
        }  
		DirectiveUtils.removeParamsFromVariable(env, paramWrap, origMap);
	}

	@Resource
	private ChannelService channelService;
}
