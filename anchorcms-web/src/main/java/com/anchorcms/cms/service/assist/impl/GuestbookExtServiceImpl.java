package com.anchorcms.cms.service.assist.impl;

import com.anchorcms.cms.dao.assist.GuestbookExtDao;
import com.anchorcms.cms.model.assist.CmsGuestbook;
import com.anchorcms.cms.model.assist.CmsGuestbookExt;
import com.anchorcms.cms.service.assist.GuestbookExtService;
import com.anchorcms.common.hibernate.Updater;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional
public class GuestbookExtServiceImpl implements GuestbookExtService {
	public CmsGuestbookExt save(CmsGuestbookExt ext, CmsGuestbook guestbook) {
		guestbook.setExt(ext);
		ext.setGuestbook(guestbook);
		ext.init();
		dao.save(ext);
		return ext;
	}

	public CmsGuestbookExt update(CmsGuestbookExt ext) {
		Updater<CmsGuestbookExt> updater = new Updater<CmsGuestbookExt>(ext);
		CmsGuestbookExt entity = dao.updateByUpdater(updater);
		entity.blankToNull();
		return entity;
	}

	@Resource
	private GuestbookExtDao dao;
}