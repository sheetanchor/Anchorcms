package com.anchorcms.core.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.anchorcms.cms.service.assist.ResourceService;
import com.anchorcms.common.hibernate.Updater;
import com.anchorcms.core.dao.SiteDao;
import com.anchorcms.core.service.*;
import com.anchorcms.core.model.CmsSite;
import com.anchorcms.core.model.CmsSiteCompany;
import com.anchorcms.core.model.CmsUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class SiteServiceImpl implements SiteService {
	private static final Logger log = LoggerFactory
			.getLogger(SiteServiceImpl.class);

	@Transactional(readOnly = true)
	public List<CmsSite> getList() {
		return dao.getList(false);
	}
	
	@Transactional(readOnly = true)
	public List<CmsSite> getListFromCache() {
		return dao.getList(true);
	}
	
	@Transactional(readOnly = true)
	public boolean hasRepeatByProperty(String property){
		return (getList().size()-dao.getCountByProperty(property))>0?true:false;
	}

	@Transactional(readOnly = true)
	public CmsSite findByDomain(String domain) {
		return dao.findByDomain(domain);
	}

	@Transactional(readOnly = true)
	public CmsSite findById(Integer id) {
		CmsSite entity = dao.findById(id);
		return entity;
	}
	
	public CmsSite save(CmsSite currSite, CmsUser currUser, CmsSite bean,
						Integer uploadFtpId, Integer syncPageFtpId) throws IOException {
		if (uploadFtpId != null) {
			bean.setUploadFtp(ftpService.findById(uploadFtpId));
		}
		if(syncPageFtpId!=null){
			bean.setSyncPageFtp(ftpService.findById(syncPageFtpId));
		}
		bean.init();
		dao.save(bean);
		// 复制本站模板
		resourceService.copyTplAndRes(currSite, bean);
		// 处理管理员
		userService.addSiteToUser(currUser, bean, bean.getFinalStep());
		//保存站点相关公司信息
		CmsSiteCompany company=new CmsSiteCompany();
		company.setName(bean.getSiteName());
		siteCompanyMng.save(bean,company);
		return bean;
	}

	public CmsSite update(CmsSite bean, Integer uploadFtpId,Integer syncPageFtpId) {
		CmsSite entity = findById(bean.getSiteId());
		if (uploadFtpId != null) {
			entity.setUploadFtp(ftpService.findById(uploadFtpId));
		} else {
			entity.setUploadFtp(null);
		}
		if (syncPageFtpId != null) {
			entity.setSyncPageFtp(ftpService.findById(syncPageFtpId));
		} else {
			entity.setSyncPageFtp(null);
		}
		Updater<CmsSite> updater = new Updater<CmsSite>(bean);
		entity = dao.updateByUpdater(updater);
		return entity;
	}

	public void updateTplSolution(Integer siteId, String solution,String mobileSol) {
		CmsSite site = findById(siteId);
		if(StringUtils.isNotBlank(solution)){
			site.setTplSolution(solution);
		}
		if(StringUtils.isNotBlank(mobileSol)){
			site.setTplMobileSolution(mobileSol);
		}
	}
	
	public void updateAttr(Integer siteId,Map<String,String>attr){
		CmsSite site = findById(siteId);
		site.getAttr().putAll(attr);
	}
	
	public void updateAttr(Integer siteId,Map<String,String>...attrs){
		CmsSite site = findById(siteId);
		for(Map<String,String>m:attrs){
			site.getAttr().putAll(m);
		}
	}

	public CmsSite deleteById(Integer id) {
		// 删除用户、站点关联
		userSiteService.deleteBySiteId(id);
		CmsSite bean = dao.findById(id);
		dao.deleteById(id);
		log.info("delete site "+id);
		// 删除模板
		/*
		try {
			resourceService.delTplAndRes(bean);
		} catch (IOException e) {
			log.error("delete Template and Resource fail!", e);
		}
		*/
		return bean;
	}

	public CmsSite[] deleteByIds(Integer[] ids) {
		CmsSite[] beans = new CmsSite[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	@Resource
	private UserService userService;
	@Resource
	private UserSiteService userSiteService;
	@Resource
	private ResourceService resourceService;
	@Resource
	private FtpService ftpService;
	@Resource
	private SiteDao dao;
	@Resource
	private SiteCompanyService siteCompanyMng;

}