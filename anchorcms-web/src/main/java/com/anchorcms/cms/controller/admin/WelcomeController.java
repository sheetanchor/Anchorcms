package com.anchorcms.cms.controller.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.anchorcms.cms.model.assist.CmsUserMenu;
import com.anchorcms.cms.model.main.Channel;
import com.anchorcms.cms.model.main.Content;
import com.anchorcms.cms.model.main.ContentCheck;
import com.anchorcms.cms.service.assist.SiteAccessService;
import com.anchorcms.cms.service.assist.SiteAccessStatisticService;
import com.anchorcms.cms.service.assist.UserMenuService;
import com.anchorcms.cms.service.main.ChannelService;
import com.anchorcms.cms.service.main.ContentService;
import com.anchorcms.cms.statistic.CmsStatistic;
import com.anchorcms.cms.statistic.CmsStatisticSvc;
import com.anchorcms.cms.web.AdminContextInterceptor;
import com.anchorcms.common.utils.CmsUtils;
import com.anchorcms.common.utils.DateUtils;
import com.anchorcms.core.model.CmsSite;
import com.anchorcms.core.model.CmsUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.anchorcms.cms.model.assist.CmsSiteAccessStatistic.STATISTIC_ALL;
import static com.anchorcms.cms.statistic.CmsStatistic.SITEID;
import static com.anchorcms.cms.statistic.CmsStatistic.STATUS;
import static com.anchorcms.cms.statistic.CmsStatistic.TimeRange;
/**
 * @Author 阁楼麻雀
 * @Date 2016/11/24 17:19
 * @Desc 首页
 */

@Controller
public class WelcomeController {
	@RequiresPermissions("index")
	@RequestMapping("/index.do")
	public String index(HttpServletRequest request) {
		return "index";
	}

	@RequiresPermissions("map")
	@RequestMapping("/map.do")
	public String map() {
		return "map";
	}

	@RequiresPermissions("top")
	@RequestMapping("/top.do")
	public String top(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		// 需要获得站点列表
		Set<CmsSite> siteList = user.getSites();
		model.addAttribute("siteList", siteList);
		model.addAttribute("site", site);
		model.addAttribute("siteParam", AdminContextInterceptor.SITE_PARAM);
		model.addAttribute("user", user);
		return "top";
	}

	@RequiresPermissions("main")
	@RequestMapping("/main.do")
	public String main() {
		return "main";

	}

	@RequiresPermissions("left")
	@RequestMapping(value = "/left.do",method = RequestMethod.GET)
	public String left(HttpServletRequest request, ModelMap model) {
		CmsUser user = CmsUtils.getUser(request);
		List<CmsUserMenu>menus=userMenuMng.getList(user.getUserId(),10);
		model.addAttribute("menus", menus);
		return "left";
	}
	
	@RequiresPermissions("right")
	@RequestMapping(value = "/right.do",method = RequestMethod.GET)
	public String right(HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsUser user = CmsUtils.getUser(request);
		String version = site.getConfig().getVersion();
		Properties props = System.getProperties();
		Runtime runtime = Runtime.getRuntime();
		long freeMemoery = runtime.freeMemory();
		long totalMemory = runtime.totalMemory();
		long usedMemory = totalMemory - freeMemoery;
		long maxMemory = runtime.maxMemory();
		long useableMemory = maxMemory - totalMemory + freeMemoery;
		//最新10条待审内容
		@SuppressWarnings("unchecked")
		List<Content>contents=(List<Content>) contentService.getPageByRight(null, null, user.getUserId(), 0, false, false, Content.ContentStatus.prepared, user.getCheckStep(site.getSiteId()), site.getSiteId(), null, user.getUserId(), 0, 1, 10).getList();
		@SuppressWarnings("unchecked")
		List<Content>newcontents=(List<Content>) contentService.getPageByRight(null, null,  user.getUserId(), 0, false, false, Content.ContentStatus.checked,  user.getCheckStep(site.getSiteId()), site.getSiteId(), null,user.getUserId(), 0, 1, 10).getList();
		model.addAttribute("props", props);
		model.addAttribute("freeMemoery", freeMemoery);
		model.addAttribute("totalMemory", totalMemory);
		model.addAttribute("usedMemory", usedMemory);
		model.addAttribute("maxMemory", maxMemory);
		model.addAttribute("useableMemory", useableMemory);
		model.addAttribute("version", version);
		model.addAttribute("user", user);
		model.addAttribute("site", site);
		model.addAttribute("contents", contents);
		model.addAttribute("newcontents", newcontents);
		getChannelStatic(request, model);
		getContentStatic(request, model);
		getPvStatic(request, model);
		return "right";
	}
	
	private void getChannelStatic(HttpServletRequest request, ModelMap model){
		Integer siteId=CmsUtils.getSiteId(request);
		List<Channel>channelList=new ArrayList<Channel>();
		//顶层栏目
		channelList= channelService.getTopList(siteId, false);
		model.addAttribute("channelList", channelList);
	}
	
	private void getPvStatic(HttpServletRequest request, ModelMap model){
		Integer siteId=CmsUtils.getSiteId(request);
		List<Object[]> dayPvList,weekPvList,monthPvList,yearPvList;
		//小时pv
		dayPvList=cmsAccessMng.statisticToday(siteId,null);
		Date now=Calendar.getInstance().getTime();
		Date weekBegin= DateUtils.getSpecficWeekStart(now, 0);
		Date monthBegin=DateUtils.getSpecficMonthStart(now, 0);
		//本周PV
		weekPvList=cmsAccessStatisticMng.statistic(weekBegin, now, siteId, STATISTIC_ALL,null);
		//本月pv
		monthPvList=cmsAccessStatisticMng.statistic(monthBegin, now, siteId, STATISTIC_ALL,null);
		//本年pv
		yearPvList=cmsAccessStatisticMng.statisticByYear(Calendar.getInstance().get(Calendar.YEAR), siteId,STATISTIC_ALL,null,true,null);
		model.addAttribute("dayPvList", dayPvList);
		model.addAttribute("weekPvList", weekPvList);
		model.addAttribute("monthPvList", monthPvList);
		model.addAttribute("yearPvList", yearPvList);
	}
	
	private void getContentStatic(HttpServletRequest request, ModelMap model){
		Integer siteId=CmsUtils.getSiteId(request);
		Map<String, Object> restrictions = new HashMap<String, Object>();
		restrictions.put(SITEID, siteId);
		restrictions.put(STATUS, ContentCheck.CHECKED);
		Date now=Calendar.getInstance().getTime();
		Date dayBegin=DateUtils.getStartDate(now);
		Date weekBegin=DateUtils.getSpecficWeekStart(now, 0);
		Date monthBegin=DateUtils.getSpecficMonthStart(now, 0);
		TimeRange dayTimeRange=TimeRange.getInstance(dayBegin, now);
		TimeRange weekTimeRange=TimeRange.getInstance(weekBegin, now);
		TimeRange monthTimeRange=TimeRange.getInstance(monthBegin, now);
		TimeRange totalTimeRange=TimeRange.getInstance(null, now);
		long releaseDayCount=cmsStatisticSvc.statistic(CmsStatistic.CONTENT, dayTimeRange, restrictions);
		long releaseWeekCount=cmsStatisticSvc.statistic(CmsStatistic.CONTENT, weekTimeRange, restrictions);
		long releaseMonthCount=cmsStatisticSvc.statistic(CmsStatistic.CONTENT, monthTimeRange, restrictions);
		long releaseTotalCount=cmsStatisticSvc.statistic(CmsStatistic.CONTENT, totalTimeRange, restrictions);
		
		restrictions.put(STATUS, ContentCheck.CHECKING);
		long checkingDayCount=cmsStatisticSvc.statistic(CmsStatistic.CONTENT, dayTimeRange, restrictions);
		long checkingWeekCount=cmsStatisticSvc.statistic(CmsStatistic.CONTENT, weekTimeRange, restrictions);
		long checkingMonthCount=cmsStatisticSvc.statistic(CmsStatistic.CONTENT, monthTimeRange, restrictions);
		long checkingTotalCount=cmsStatisticSvc.statistic(CmsStatistic.CONTENT, totalTimeRange, restrictions);
		
		long commentDayCount=cmsStatisticSvc.statistic(CmsStatistic.COMMENT, dayTimeRange, restrictions);
		long commentWeekCount=cmsStatisticSvc.statistic(CmsStatistic.COMMENT, weekTimeRange, restrictions);
		long commentMonthCount=cmsStatisticSvc.statistic(CmsStatistic.COMMENT, monthTimeRange, restrictions);
		long commentTotalCount=cmsStatisticSvc.statistic(CmsStatistic.COMMENT, totalTimeRange, restrictions);
		
		long guestbookDayCount=cmsStatisticSvc.statistic(CmsStatistic.GUESTBOOK, dayTimeRange, restrictions);
		long guestbookWeekCount=cmsStatisticSvc.statistic(CmsStatistic.GUESTBOOK, weekTimeRange, restrictions);
		long guestbookMonthCount=cmsStatisticSvc.statistic(CmsStatistic.GUESTBOOK, monthTimeRange, restrictions);
		long guestbookTotalCount=cmsStatisticSvc.statistic(CmsStatistic.GUESTBOOK, totalTimeRange, restrictions);
		
		long memberDayCount=cmsStatisticSvc.statistic(CmsStatistic.MEMBER, dayTimeRange, restrictions);
		long memberWeekCount=cmsStatisticSvc.statistic(CmsStatistic.MEMBER, weekTimeRange, restrictions);
		long memberMonthCount=cmsStatisticSvc.statistic(CmsStatistic.MEMBER, monthTimeRange, restrictions);
		long memberTotalCount=cmsStatisticSvc.statistic(CmsStatistic.MEMBER, totalTimeRange, restrictions);
		
		
		model.addAttribute("releaseDayCount", releaseDayCount);
		model.addAttribute("releaseWeekCount", releaseWeekCount);
		model.addAttribute("releaseMonthCount", releaseMonthCount);
		model.addAttribute("releaseTotalCount", releaseTotalCount);
		
		model.addAttribute("checkingDayCount", checkingDayCount);
		model.addAttribute("checkingWeekCount", checkingWeekCount);
		model.addAttribute("checkingMonthCount", checkingMonthCount);
		model.addAttribute("checkingTotalCount", checkingTotalCount);
		
		model.addAttribute("commentDayCount", commentDayCount);
		model.addAttribute("commentWeekCount", commentWeekCount);
		model.addAttribute("commentMonthCount", commentMonthCount);
		model.addAttribute("commentTotalCount", commentTotalCount);
		
		model.addAttribute("guestbookDayCount", guestbookDayCount);
		model.addAttribute("guestbookWeekCount", guestbookWeekCount);
		model.addAttribute("guestbookMonthCount", guestbookMonthCount);
		model.addAttribute("guestbookTotalCount", guestbookTotalCount);
		
		model.addAttribute("memberDayCount", memberDayCount);
		model.addAttribute("memberWeekCount", memberWeekCount);
		model.addAttribute("memberMonthCount", memberMonthCount);
		model.addAttribute("memberTotalCount", memberTotalCount);
	}
	
	
	
	@Autowired
	private ContentService contentService;
	@Autowired
	private UserMenuService userMenuMng;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private CmsStatisticSvc cmsStatisticSvc;
	@Autowired
	private SiteAccessService cmsAccessMng;
	@Autowired
	private SiteAccessStatisticService cmsAccessStatisticMng;
}
