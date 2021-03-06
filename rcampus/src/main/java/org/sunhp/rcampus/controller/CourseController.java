package org.sunhp.rcampus.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sunhp.rcampus.bean.Chapter;
import org.sunhp.rcampus.bean.Course;
import org.sunhp.rcampus.bean.HttpResult;
import org.sunhp.rcampus.bean.Judge;
import org.sunhp.rcampus.bean.Progress;
import org.sunhp.rcampus.bean.User;
import org.sunhp.rcampus.components.Constants;
import org.sunhp.rcampus.components.Order.Direction;
import org.sunhp.rcampus.components.Page;
import org.sunhp.rcampus.components.Pageable;
import org.sunhp.rcampus.dao.CourseDao;
import org.sunhp.rcampus.service.ApiService;
import org.sunhp.rcampus.service.ChapterService;
import org.sunhp.rcampus.service.CourseService;
import org.sunhp.rcampus.service.JudgeService;
import org.sunhp.rcampus.service.ProgressService;
import org.sunhp.rcampus.service.UserService;
import org.sunhp.rcampus.util.FileUtils;
import org.sunhp.rcampus.util.JspToHtml;
import org.sunhp.rcampus.vo.ExamResult;
import org.sunhp.rcampus.vo.OcpuResult;
import org.sunhp.rcampus.vo.SimpleCourse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("course")
public class CourseController {

	@Autowired
	CourseService courseService;
    @Autowired
    CourseDao courseDao;
	@Autowired
	ApiService apiService;

	@Autowired
	JudgeService judgeService;
	@Autowired
	ChapterService chapterService;
	@Autowired
	UserService userService;
	@Autowired
	ProgressService progressService;
	@Autowired
	JudgeController judgeController;
	@Autowired
	ChapterController chapterController;

	/**
	 * 获得某章节的课程列表
	 * 
	 * @param request
	 * @param response
	 * @param chapterId
	 * @return
	 */
	// @ResponseBody
	@RequestMapping("/")
	public String getCourseList(HttpServletRequest request,
			HttpServletResponse response, Long chapterId) {
		User user=(User) request.getSession().getAttribute("user");
		String icon=null;
		if(user!=null){
		String photo=user.getPhoto();
		if(photo!=null&&photo.length()>0){
            int pos=photo.lastIndexOf('.');
			if(pos!=-1&&pos<photo.length()-1)
				icon=String.valueOf(user.getUserId()) + "_head"+photo.substring(pos);
		}
		}
		System.out.println(icon);
		request.setAttribute("icon",icon);
		Course course = new Course();
		course.setChapter(chapterId);
		List<Course> list = courseService.getAll(course);
		List<SimpleCourse> sList = new ArrayList<>();
		for (Course co : list) {
			SimpleCourse sc = new SimpleCourse(co, 0.0);
			sList.add(sc);
		}
		// return JSON.toJSONString(sList);
		  return "courses";
	}

	@RequestMapping("/test")
	public String test(HttpServletRequest request) {
		if (request.getParameterMap() == null) {
			request.setAttribute("chapterName", "chapterOne");
			request.setAttribute("courseName", "courseOne");
		}
		String chapterName = request.getParameter("chapterName");
		String courseName = request.getParameter("courseName");
		request.setAttribute("chapterName", chapterName);
		request.setAttribute("courseName", courseName);
		return "test";
	}

	@RequestMapping("/courseIntro")
	public String courseIntro(HttpServletRequest request,
			HttpServletResponse response) {
		User user=(User) request.getSession().getAttribute("user");
		String photo=user.getPhoto();
		String icon=null;
		if(photo!=null&&photo.length()>0){
            int pos=photo.lastIndexOf('.');
			if(pos!=-1&&pos<photo.length()-1)
				icon=String.valueOf(user.getUserId()) + "_head"+photo.substring(pos);
		}
		request.setAttribute("icon",icon);
		long userId=user.getUserId();
		Pageable<Progress> progressPg=new Pageable<Progress>();
		progressPg.setSearchProperty("user_id");
		progressPg.setSearchValue(String.valueOf(userId));
		List<Progress> userProgress=progressService.findByPager(progressPg).getRows();
		Progress progress=null;
		int courseOrder=0;
		if(userProgress!=null&&userProgress.size()>0){
			progress=userProgress.get(0);
			Long courseId=progress.getCourseId();
			Course course=courseDao.find(courseId);
			courseOrder=course.getCourseOrder();
		}
		request.setAttribute("courseOrder",courseOrder);
		Pageable<Course> pageable = new Pageable<Course>();
		pageable.setOrderProperty("course_order");
		pageable.setOrderDirection(Direction.asc);
		Page<Course> page = courseService.findByPager(pageable);
		List<Course> courseList = page.getRows();
		Pageable<Chapter> chapterPg = new Pageable<Chapter>();
		chapterPg.setOrderProperty("chapter_order");
		Page<Chapter> chapterPage = chapterService.findByPager(chapterPg);
		List<Chapter> chapterList = chapterPage.getRows();
		Map<Chapter, List<Course>> ccMap = new HashMap<Chapter, List<Course>>();
		int loop1 = 0;
		int loop2 = 0;
		while (loop1 < chapterList.size()) {
			loop1++;
			List<Course> cList = new ArrayList<Course>();
			while (loop2 < courseList.size()) {
				if (loop2 < (courseList.size() - 1)
						&& courseList.get(loop2).getChapter() != courseList
								.get(loop2 + 1).getChapter()) {
					cList.add(courseList.get(loop2));
					loop2++;
					break;
				}
				cList.add(courseList.get(loop2));
				loop2++;
			}
			ccMap.put(chapterList.get(loop1 - 1), cList);
		}
		request.setAttribute("chapterList", chapterList);
		request.setAttribute("ccMap", ccMap);
		return "courseIntro";
	}

	/**
	 * 获得某一课程内容,这个是用户做题用的
	 * 
	 * @param request
	 * @param response
	 * @param courseId
	 * @return
	 */
	@RequestMapping("/getCourseById")
	public String getCourseDetail(HttpServletRequest request,
			HttpServletResponse response, Long courseId) {
		// 下边是用的静态页
		/*
		 * String page = courseId + ".html"; try { response.sendRedirect(page);
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute("userId");
		Course course = courseService.get(courseId);
		String isFinish = "isFinish";
		Course nextCourse = getNextCourse(courseId);
		course.setExamPage(judgeController.getExam(courseId));
		request.setAttribute("chapterList", chapterController.getAllChapter());
		request.setAttribute("course", course);
		/*
		String jspPath = "../WEB-INF/jsp/exercise.jsp";
		String target = request.getServletContext().getRealPath("\\")
				+ "WEB-INF\\courses\\"+course.getCourseId()+".html";
		JspToHtml.jsp2Html(jspPath, request, response, target);
		*/
		// 下边这段是获取当前课程的下一节课程
		request.setAttribute("nextCourse", nextCourse);
		request.setAttribute("staticPage","../courses/"+course.getCourseId()+".html");
		// 更新记录
		Progress progress = new Progress();
		progress.setUserId(userId);
		List<Progress> progressList = progressService.find(progress);
		if (progressList.size() == 0) {// 之前没有记录
			request.setAttribute(isFinish, false);
			request.setAttribute("latestCourseId", nextCourse.getCourseId());
		} else {
			long recordCourseId = progressList.get(0).getCourseId();
			if (courseId <= recordCourseId) {// 当前记录比最高记录的id小
				request.setAttribute(isFinish, true);
			} else {
				request.setAttribute(isFinish, false);
			}
			request.setAttribute("latestCourseId", nextCourse.getCourseId());
		}
		return "course_detail";
	}

	/**
	 * 提交课程作业
	 * 
	 * @param request
	 * @param response
	 * @param courseId
	 * @param content
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping("submit.do")
	public String getCourseSubmit(HttpServletRequest request,
			HttpServletResponse response, Long courseId, String code)
			throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute("userId");
		code = code.replaceAll("%2B", "+");
		code = code.replaceAll("%25", "%");
		code = code.replaceAll("%26", "&");
		ExamResult examResult = new ExamResult();
		OcpuResult ocpuResult;
		examResult.setJudgeStatus(true);
		examResult.setJudgeMsg("correct!");
		// 将用户输入存入temp.R文件中
		String excuteFilePath = FileUtils
				.saveStrToFile(request, "temp.R", code);
		// 将temp.R上传到OpenCPU
		String url = Constants.OPENCPU_HOST + "/" + Constants.OPENCPU_NAME
				+ "/library/utils/R/file.edit";
		HttpResult result = apiService.doPostFile(url, excuteFilePath);
		if (result.getCode() != 201) {
			ocpuResult = new OcpuResult(code, "R文件上传出错");
			examResult.setStatus(false);
			// examResult.setOcpuResult(ocpuResult);
			examResult.setOcpuJSON(JSON.toJSONString(ocpuResult));
			return JSON.toJSONString(examResult);
		}
		ocpuResult = new OcpuResult(result.getData());
		if (ocpuResult.getSessionID() == null) {
			ocpuResult = new OcpuResult(code, "R文件上传后openCPU结果解析出错");
			examResult.setStatus(false);
			// examResult.setOcpuResult(ocpuResult);
			examResult.setOcpuJSON(JSON.toJSONString(ocpuResult));
			return JSON.toJSONString(examResult);
		}
		// OpenCPU上执行emp.R
		String url2 = Constants.OPENCPU_HOST + "/ocpu/tmp/"
				+ ocpuResult.getSessionID() + "/files/temp.R";
		result = apiService.doPost(url2);
		// 编译出错
		if (result.getCode() == 400) {
			String resultData = result.getData();
			resultData = resultData.replace("In call:", "");
			resultData = resultData.replace("eval(expr, envir, enclos)", "");
			resultData = resultData.replace("parse(text = x, srcfile = src)",
					"");
			resultData = "编译错误: \n" + resultData;
			ocpuResult = new OcpuResult(code, resultData);
			examResult.setStatus(false);
			examResult.setOcpuJSON(JSON.toJSONString(ocpuResult));
			return JSON.toJSONString(examResult);
		}
		if (result.getCode() != 201 && result.getCode() != 200) {
			ocpuResult = new OcpuResult(code, "系统内部错误:" + result.getCode());
			examResult.setStatus(false);
			// examResult.setOcpuResult(ocpuResult);
			examResult.setOcpuJSON(JSON.toJSONString(ocpuResult));
			return JSON.toJSONString(examResult);
		}
		// 正常情况
		ocpuResult = new OcpuResult(result.getData());
		if (ocpuResult.getSessionID() == null) {
			ocpuResult = new OcpuResult(code, "R文件执行后openCPU结果解析出错结果解析出错");
			examResult.setStatus(false);
			// examResult.setOcpuResult(ocpuResult);
			examResult.setOcpuJSON(JSON.toJSONString(ocpuResult));
			return JSON.toJSONString(examResult);
		}
		// 获取执行结果数据
		String source = apiService.doGet(Constants.OPENCPU_HOST + "/ocpu/tmp/"
				+ ocpuResult.getSessionID() + "/source");
		String console = apiService.doGet(Constants.OPENCPU_HOST + "/ocpu/tmp/"
				+ ocpuResult.getSessionID() + "/console");
		String info = apiService.doGet(Constants.OPENCPU_HOST + "/ocpu/tmp/"
				+ ocpuResult.getSessionID() + "/info");
		String[] consoleArr = console.split("\n");
		List<String> out = new ArrayList<String>();// 需要的代码输出
		for (String str : consoleArr) {
			if (str.length() > 0) {
				if (str.charAt(0) != '>') {
					int start = str.indexOf(']');
					out.add(str.substring(start + 2, str.length()));
				}
			}
		}
		Pageable<Judge> judgePageable = new Pageable<Judge>();
		judgePageable.setSearchProperty("course_id");
		judgePageable.setSearchValue(String.valueOf(courseId));
		judgePageable.setPageNumber(Integer.MAX_VALUE);
		List<Judge> judgeList = judgeService.findByPager(judgePageable)
				.getRows();
		int pass = 0;// 记录通过的题目的数量
		for (int i = 0; i < judgeList.size(); i++) {
			if (i >= out.size()
					|| (!out.get(i).equals(judgeList.get(i).getJudgeAnswer()))) {// 答案错误
				System.out.println(out.get(i) + " "
						+ judgeList.get(i).getJudgeAnswer());
				console = console + "\n" + "未通过第" + String.valueOf(i + 1)
						+ "个题目,期望输出为：" + judgeList.get(i).getJudgeAnswer();
			} else {
				pass++;
			}
		}
		if (pass == judgeList.size()) {// 满分才记录
			Progress progress = new Progress();
			progress.setUserId(userId);
			List<Progress> progressList = progressService.find(progress);
			if (progressList.size() == 0) {// 该用户以前没有记录
				progress.setPoint(100D);
				progress.setCourseId(courseId);
				progress.setCreateTime(new Date());
				progressService.save(progress);
			} else {
				progress = progressList.get(0);
				if (progressList.get(0).getCourseId() < courseId) {// 当前courseId比原来的大才修改courseId否则不动
					progress.setCourseId(courseId);
					progress.setPoint(100D);
					progress.setUpdateTime(new Date());
					progressService.update(progress);
				}
			}
		}
		ocpuResult.setSource(source);
		ocpuResult.setInfo(info);
		ocpuResult.setConsole(console);
		examResult.setOcpuJSON(JSON.toJSONString(ocpuResult));
		examResult.setStatus(pass == judgeList.size());
		return JSON.toJSONString(examResult);
	}

	@RequestMapping("course_manage")
	public String addCourse(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute("userId");
		User user = userService.get(userId);
		request.setAttribute("user", user);
		request.setAttribute("course",
				courseService.get(request.getParameter("courseId")));
		return "course_manage";
	}

	@RequestMapping("generate")
	@ResponseBody
	public String generateCourseContent(HttpServletRequest request,
			HttpServletResponse response) {
		Pageable<Course> pageable = new Pageable<Course>();
		pageable.setOrderProperty("course_order");
		pageable.setOrderDirection(Direction.asc);
		Page<Course> page = courseService.findByPager(pageable);
		List<Course> courseList = page.getRows();
		Pageable<Chapter> chapterPg = new Pageable<Chapter>();
		chapterPg.setOrderProperty("chapter_order");
		Page<Chapter> chapterPage = chapterService.findByPager(chapterPg);
		List<Chapter> chapterList = chapterPage.getRows();
		Map<Chapter, List<Course>> ccMap = new HashMap<Chapter, List<Course>>();
		int loop1 = 0;
		int loop2 = 0;
		while (loop1 < chapterList.size()) {
			loop1++;
			List<Course> cList = new ArrayList<Course>();
			while (loop2 < courseList.size()) {
				if (loop2 < (courseList.size() - 1)
						&& courseList.get(loop2).getChapter() != courseList
								.get(loop2 + 1).getChapter()) {
					cList.add(courseList.get(loop2));
					loop2++;
					break;
				}
				cList.add(courseList.get(loop2));
				loop2++;
			}
			ccMap.put(chapterList.get(loop1 - 1), cList);
		}
		request.setAttribute("chapterList", chapterList);
		request.setAttribute("ccMap", ccMap);
		String jspPath = "../WEB-INF/jsp/courseContent.jsp";
		String target = request.getServletContext().getRealPath("\\")
				+ "page\\courseContent.html";
		String result = JspToHtml.jsp2Html(jspPath, request, response, target);
		return JSON.toJSONString(result);
	}

	@RequestMapping("add")
	@ResponseBody
	public String addCourse(HttpServletRequest request,
			HttpServletResponse response, String courseName, String courseNote,
			String courseOrder, String belongChapter) {
		Course course = new Course();
		course.setChapter(Long.valueOf(belongChapter));
		course.setCourseName(courseName);
		course.setCourseNote(courseNote);
		course.setCourseOrder(Integer.valueOf(courseOrder));
		Pageable<Course> tempCoursePageable = new Pageable<Course>();
		tempCoursePageable.setSearchProperty("course_order");
		tempCoursePageable.setSearchValue(courseOrder);
		Page<Course> tempCoursePage = courseService
				.findByPager(tempCoursePageable);
		List<Course> courseList = tempCoursePage.getRows();
		if (courseList.size() > 0
				&& courseList.get(0).getChapter() == Long
						.valueOf(belongChapter)) {// 存在重复章节号
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("result", "已经存在第" + courseOrder + "节,请勿重复添加");
			return JSON.toJSONString(jsonObject);
		}
		courseService.save(course);
		return JSON.toJSONString(course);
	}

	@ResponseBody
	@RequestMapping("delete")
	public String deleteCourse(HttpServletRequest request,
			HttpServletResponse response, Long courseId) {
		judgeController.deleteJudgeList(courseId);
		courseService.delete(courseId);
		return JSON.toJSONString("deleted");
	}

	@ResponseBody
	@RequestMapping("getCompleteRate")
	public String getCompleteRate(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute("userId");
		Pageable<Progress> progressPageable = new Pageable<Progress>();
		progressPageable.setSearchProperty("user_id");
		progressPageable.setSearchValue(String.valueOf(userId));
		progressPageable.setPageNumber(Integer.MAX_VALUE);
		Page<Progress> progressPage = progressService
				.findByPager(progressPageable);
		List<Progress> progressList = progressPage.getRows();
		int complete = 0;
		long total = 0;
		if (progressList.size() > 0
				&& progressList.get(progressList.size() - 1).getPoint() == 100) {
			complete = progressList.size();
		} else {
			complete = Math.min(progressList.size(), 0);
		}
		Pageable<Course> coursePageable = new Pageable<Course>();
		coursePageable.setPageSize(Integer.MAX_VALUE);
		total = courseService.count(coursePageable);
		double rate = ((double) 100 * complete / total);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("rate", (int) rate);
		return JSON.toJSONString(jsonObject);
	}

	/**
	 * 获取当前用户最新课程
	 * 
	 * @param userId
	 * @return
	 */
	public Long getUserLatestCourse(Long userId) {
		Pageable<Progress> progressPageable = new Pageable<Progress>();
		progressPageable.setSearchProperty("user_id");
		progressPageable.setSearchValue(String.valueOf(userId));
		progressPageable.setPageNumber(Integer.MAX_VALUE);
		Page<Progress> progressPage = progressService
				.findByPager(progressPageable);
		List<Progress> progressList = progressPage.getRows();
		if (progressList.size() > 0)
			return progressList.get(progressList.size() - 1).getCourseId();
		List<Course> courseList = courseService.getAll(new Course());
		return (long) courseList.get(0).getCourseId();
	}

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getCourseCount")
	public String getCourseCount(HttpServletRequest request,
			HttpServletResponse response) {
		Pageable<Course> coursePageable = new Pageable<Course>();
		coursePageable.setPageNumber(Integer.MAX_VALUE);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("courseCount", courseService.count(coursePageable));
		return JSON.toJSONString(jsonObject);
	}

	@RequestMapping("getCourseList")
	public String getCourseList(HttpServletRequest request,
			HttpServletResponse response) {
		String chapterId = request.getParameter("chapterId");
		Pageable<Course> coursePageable = new Pageable<Course>();
		coursePageable.setSearchProperty("chapter");
		coursePageable.setSearchValue(chapterId);
		coursePageable.setPageNumber(Integer.MAX_VALUE);
		request.setAttribute("chapter",
				chapterService.get(Long.valueOf(chapterId)));
		request.setAttribute("courseList",
				courseService.findByPager(coursePageable).getRows());
		return "chapter_detail_manage";
	}

	/**
	 * 这个是后台管理员用的
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getCourseInfo")
	@ResponseBody
	public String getCourseInfo(HttpServletRequest request,
			HttpServletResponse response) {
		Course course = courseService.get(Long.valueOf(request
				.getParameter("courseId")));
		return JSON.toJSONString(course);
	}

	/**
	 * 这个是后台管理员用的
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("modifyCourseInfo")
	@ResponseBody
	public String modifyCourseInfo(HttpServletRequest request,
			HttpServletResponse response) {
		int courseOrder = Integer.valueOf(request.getParameter("courseOrder"));
		long courseId = Long.valueOf(request.getParameter("courseId"));
		long chapterId = Long.valueOf(request.getParameter("chapterId"));
		String courseName = request.getParameter("courseName");
		String courseNote = request.getParameter("courseNote");
		Pageable<Course> coursePageable = new Pageable<Course>();
		coursePageable.setSearchProperty("chapter");// 查找与之course-order相同的节
		coursePageable.setSearchValue(String.valueOf(chapterId));
		List<Course> tempCourseList = courseService.findByPager(coursePageable)
				.getRows();
		for (Course course : tempCourseList) {
			if (course.getCourseOrder() == courseOrder
					&& course.getCourseId() != courseId) {// order相同而且id不一样那么显然重复
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("result", "已经存在第" + courseOrder + "节,请勿重复添加");
				return JSON.toJSONString(jsonObject);
			}
		}
		Course course = courseService.get(courseId);
		course.setCourseName(courseName);
		course.setCourseNote(courseNote);
		course.setCourseOrder(courseOrder);
		courseService.update(course);
		return JSON.toJSONString(course);
	}

	/**
	 * 级联删除，把下边的judge也删了
	 * 
	 * @param chapterId
	 */
	public void deleteCourseList(Long chapterId) {
		Pageable<Course> coursePageable = new Pageable<Course>();
		coursePageable.setSearchProperty("chapter");
		coursePageable.setSearchValue(String.valueOf(chapterId));
		coursePageable.setPageSize(Integer.MAX_VALUE);
		List<Course> courseList = courseService.findByPager(coursePageable)
				.getRows();
		for (Course course : courseList) {
			judgeController.deleteJudgeList(course.getCourseId());
			courseService.delete(course.getCourseId());
		}
	}

	public Course getNextCourse(long courseId) {
		Course course = new Course();
		course.setCourseId(courseId);
		course=courseService.find(course).get(0);
		long chapterId = course.getChapter();
		Chapter chapter = new Chapter();
		chapter.setChapterId(chapterId);
		chapter = chapterService.find(chapter).get(0);
		long chapterOrder = chapter.getChapterOrder();
		int courseOrder = course.getCourseOrder();
		Course nextCourse = new Course();
		nextCourse.setChapter(chapterId);
		nextCourse.setCourseOrder(courseOrder + 1);// 当前课程的order+1
		List<Course> nextCourseList = courseService.find(nextCourse);
		if (nextCourseList.size() != 0) {
			return nextCourseList.get(0);
		} else {
			nextCourse.setChapter(chapterOrder + 1);
			nextCourse.setCourseOrder(1);// 找下一章的第一节
			nextCourseList = courseService.find(nextCourse);
			if (nextCourseList.size() == 0) {// 下边没有了
				return course;
			} else {
				return nextCourseList.get(0);
			}
		}
	}
}