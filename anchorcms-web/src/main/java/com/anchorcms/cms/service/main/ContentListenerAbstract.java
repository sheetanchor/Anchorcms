package com.anchorcms.cms.service.main;

import com.anchorcms.cms.model.main.Content;

import java.util.Map;


/**
 * ContentListener的抽象实现
 */
public class ContentListenerAbstract implements ContentListener {
	public void afterChange(Content content, Map<String, Object> map) {
	}

	public void afterDelete(Content content) {
	}

	public void afterSave(Content content) {
	}

	public Map<String, Object> preChange(Content content) {
		return null;
	}

	public void preDelete(Content content) {
	}

	public void preSave(Content content) {
	}
}
