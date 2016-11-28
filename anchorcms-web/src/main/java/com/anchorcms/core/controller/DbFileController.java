package com.anchorcms.core.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anchorcms.core.model.DbFile;
import com.anchorcms.core.service.DbFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


@SuppressWarnings("serial")
public class DbFileController extends HttpServlet {
	/**
	 * 参数名称
	 */
	public static final String PARAM_NAME = "n";

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter(PARAM_NAME);
		if (StringUtils.isBlank(name)) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		DbFile file = dbFileService.findById(name);
		if (file == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String mimeType = getServletContext().getMimeType(name);
		if (mimeType != null) {
			response.setContentType(mimeType);
		}
		String filename = file.getFilename();
		int index = filename.lastIndexOf("/");
		if (index != -1) {
			filename = filename.substring(index + 1);
		}
		response.addHeader("Content-disposition", "filename=" + filename);
		response.setContentLength(file.getLength());
		ServletOutputStream out = response.getOutputStream();
		out.write(file.getContent());
		out.flush();
		out.close();
	}

	@Override
	public void init() throws ServletException {
		WebApplicationContext appCtx = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		dbFileService = BeanFactoryUtils.beanOfTypeIncludingAncestors(appCtx,
				DbFileService.class);
	}

	@Resource
	private DbFileService dbFileService;
}
