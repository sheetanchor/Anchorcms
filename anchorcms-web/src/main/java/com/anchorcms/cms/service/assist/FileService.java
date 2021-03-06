package com.anchorcms.cms.service.assist;

import com.anchorcms.cms.model.main.CmsFile;
import com.anchorcms.cms.model.main.Content;

import java.util.List;


public interface FileService {
	public List<CmsFile> getList(Boolean valid);

	public CmsFile findById(Integer id);
	
	public CmsFile findByPath(String path);

	public CmsFile save(CmsFile bean);

	public CmsFile update(CmsFile bean);

	public CmsFile deleteById(Integer id);
	
	public CmsFile deleteByPath(String path);
	
	public void deleteByContentId(Integer contentId);
	
	public void saveFileByPath(String filepath, String name, Boolean valid);
	
	public void updateFileByPath(String path, Boolean valid, Content c);
	
	public void updateFileByPaths(String[] attachmentPaths, String[] picPaths, String mediaPath,
								  String titleImg, String typeImg, String contentImg, Boolean valid, Content c);
}