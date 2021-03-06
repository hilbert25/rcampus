<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!-- 红配蓝，狗都嫌 -->
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>课程管理</title>
<meta name="description" content="这是一个 index 页面">
<meta name="keywords" content="index">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="renderer" content="webkit">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<meta name="apple-mobile-web-app-title" content="Amaze UI" />
<link rel="stylesheet" href="../page/assets/css/amazeui.min.css" />
<link rel="stylesheet" href="../page/assets/css/admin.css">
<link rel="stylesheet" href="../page/assets/css/app.css">
<link rel="stylesheet" href="../page/assets/css/bootstrap.min.css">
<script src="../page/assets/js/jquery.min.js"></script>
<script src="../page/assets/js/amazeui.min.js"></script>
<script src="../page/assets/js/app.js"></script>
<script src="../page/assets/js/bootstrap.min.js"></script>
<script src="../page/assets/js/string-deal.js"></script>
<style>
</style>
<script type="text/javascript">
	function getUnDealCount() {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				var count = xmlhttp.responseText;
				if (count > 0) {
					var div = document.getElementById("undeal");
					var span = document.createElement("span");
					span.innerHTML = count;
					span.setAttribute("class",
							"am-badge tpl-badge-danger am-round");
					div.appendChild(span);

				}
			}
		};
		xmlhttp.open("POST", "/rcampus/message/getUnDealCount", true);
		xmlhttp.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xmlhttp.send();
	}
	/*添加题目*/
	function addJudge() {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				var data = xmlhttp.responseText;
				if (data['result'])
					alert(data['result']);
				else {
					window.location.reload();

				}
			}
		};
		xmlhttp.open("POST", "/rcampus/judge/add.do", true);
		xmlhttp.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		var courseId = "${course.courseId}";
		var judgeProblem = stringPredetail(document.getElementById("judgeProblem").value);
		var judgeAnswer = stringPredetail(document.getElementById("judgeAnswer").value);
		var judgeTips= stringPredetail(document.getElementById("judgeTips").value);
		xmlhttp.send("courseId=" + courseId + "&&judgeProblem=" + judgeProblem
				+ "&judgeAnswer=" + judgeAnswer + "&judgeTips=" + judgeTips);
	}

	function deleteJudge(judgeId) {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					window.location.reload();
			}
		};
		xmlhttp.open("POST", "/rcampus/judge/delete", true);
		xmlhttp.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xmlhttp.send("judgeId="+judgeId);
	}
	//设置修改模态框的数据
	function getJudgeById(judgeId) {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				var data = JSON.parse(xmlhttp.responseText);
				$('#judgeModal').modal('show');
				document.getElementById("judgeProblem").value=data['judgeProblem'];
				document.getElementById("judgeTips").value=data['judgeTips'];
				document.getElementById("judgeAnswer").value=data['judgeAnswer'];
				document.getElementById("submit").setAttribute("onclick",
						"modifyJudge(" + judgeId + ");");
			}
		};
		xmlhttp.open("POST", "/rcampus/judge/getJudgeById", true);
		xmlhttp.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xmlhttp.send("judgeId=" +judgeId);
	}
	function modifyJudge(judgeId) {
		var xmlhttp;
		if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp = new XMLHttpRequest();
		} else {// code for IE6, IE5
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					window.location.reload();
			}
		};
		var judgeProblem=stringPredetail(document.getElementById("judgeProblem").value);
		var judgeAnswer=stringPredetail(document.getElementById("judgeAnswer").value);
		var judgeTips=stringPredetail(document.getElementById("judgeTips").value);
		xmlhttp.open("POST", "/rcampus/judge/modifyJudge", true);
		xmlhttp.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xmlhttp.send("judgeProblem="+judgeProblem+"&judgeAnswer="+judgeAnswer+"&judgeTips="+judgeTips+"&judgeId="+judgeId);
	}
	function init() {
		getUnDealCount();
	}
</script>

</head>

<body data-type="generalComponents" onload="init();">
	<header class="am-topbar am-topbar-inverse admin-header">
		<div class="am-topbar-brand"></div>
		<div class="am-collapse am-topbar-collapse" id="topbar-collapse">
			<ul
				class="am-nav am-nav-pills am-topbar-nav am-topbar-right admin-header-list tpl-header-list">
				<li class="am-dropdown" data-am-dropdown data-am-dropdown-toggle>
					<a class="am-dropdown-toggle tpl-header-list-link"
					href="javascript:;" id="undeal"> <span
						class="am-icon-comment-o"></span> 消息
				</a>
					<ul class="am-dropdown-content tpl-dropdown-content"
						id="message_list">
						<li class="tpl-dropdown-content-external"><a href="###">全部</a>
						</li>

					</ul>
				</li>
				<li class="am-dropdown" data-am-dropdown data-am-dropdown-toggle>
					<a class="am-dropdown-toggle tpl-header-list-link"
					href="javascript:;"> <span class="tpl-header-list-user-nick">${user.userName }</span><span
						class="tpl-header-list-user-ico"> <img src="${user.photo }"></span>
				</a>
					<ul class="am-dropdown-content">
						<li><a href="#"><span class="am-icon-bell-o"></span> 资料</a></li>
						<li><a href="#"><span class="am-icon-cog"></span> 设置</a></li>
						<li><a href="#"><span class="am-icon-power-off"></span>
								退出</a></li>
					</ul>
				</li>
			</ul>
		</div>
	</header>
	<div class="tpl-page-container tpl-page-header-fixed">

		<div class="tpl-left-nav tpl-left-nav-hover">
			<div class="tpl-left-nav-list">
				<ul class="tpl-left-nav-menu">
					<li class="tpl-left-nav-item"><a href="../adminhome"
						class="nav-link active"> <i class="am-icon-home"></i> <span>用户管理</span>

					</a></li>
					<li class="tpl-left-nav-item"><a href="chapter_manage"
						class="nav-link active"> <i class="am-icon-home"></i> <span>课程管理</span><i
							class="am-icon-star tpl-left-nav-content-ico am-fr am-margin-right"></i>
					</a></li>

					<li><div class="text-primary">
							<p>第${chapter.chapterOrder}章 ${chapter.chapterName}</p>
						</div></li>
					<li><div class="text-primary">
							<p>第${course.courseOrder}节 ${course.courseName}</p>
						</div></li>
					<li><div class="text-success">
							<p>${course.courseNote}</p>
						</div></li>
				</ul>
			</div>
		</div>
	</div>
	<div class="tpl-content-wrapper">
		<div class="tpl-content-page-title">题目列表</div>
		<div class="tpl-portlet-components">
			<div class="portlet-title">
				<a style="text-align: center;"
					href="../../rcampus/chapter/getChapterList">R Campus</a> <a
					href="javascript:volid(0);"> >>> </a><a
					href="../../rcampus/course/getCourseList?chapterId=${chapter.chapterId }">${chapter.chapterName }</a>
				<a href="javascript:volid(0);"> >>> </a><a>${course.courseName }</a>
				<div class="tpl-portlet-input tpl-fz-ml">
					<div class="portlet-input input-small input-inline">
						<div class="input-icon right">
							<i class="am-icon-search"></i> <input type="text"
								class="form-control form-control-solid" placeholder="搜索...">
						</div>
					</div>
				</div>
			</div>
			<div class="tpl-block">
				<div class="am-g">
					<div class="am-u-sm-12 am-u-md-6">
						<div class="am-btn-toolbar">
							<div class="am-btn-group am-btn-group-xs">
								<button type="button"
									class="am-btn am-btn-default am-btn-success"
									data-toggle="modal" data-target="#judgeModal">
									<span class="am-icon-plus"></span> 新增
								</button>

								<!-- 模态框（Modal） -->
								<div class="modal fade" id="judgeModal" tabindex="-1"
									role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
									<div class="modal-dialog">
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal"></button>
												<h4 class="modal-title" id="myModalLabel"></h4>
											</div>

											<div class="modal-body">添加题目</div>
											<div class="modal-footer">
												<form class="am-form tpl-form-line-form"
													action="../course/add" method="post" id="courseForm"
													name="courseForm">

													<div class="am-form-group">
														<label for="user-name" class="am-u-sm-3 am-form-label">题目
															<span class="tpl-form-line-small-title">problem</span>
														</label>
														<div class="am-u-sm-9">
															<textarea class="" rows="1" id="judgeProblem"
																name="examPage" placeholder="请输入题目" required="required"></textarea>
														</div>
													</div>
													<div class="am-form-group">
														<label for="user-intro" class="am-u-sm-3 am-form-label">答案</label>
														<div class="am-u-sm-9">
															<textarea class="" rows="1" id="judgeAnswer"
																name="judgeItem" placeholder="请输入答案" required="required"></textarea>
														</div>
													</div>
													<div class="am-form-group">
														<label for="user-intro" class="am-u-sm-3 am-form-label">提示</label>
														<div class="am-u-sm-9">
															<textarea class="" rows="1" id="judgeTips"
																name="judgeTips" placeholder="请输入提示" required="required"></textarea>
														</div>
													</div>
													<div class="am-form-group" style="display: none">
														<textarea class="" rows="3" id="chapter" name="chapter"
															required="required">${chapter.chapterId }</textarea>
													</div>
													<button type="button" class="btn btn-default"
														data-dismiss="modal">关闭</button>
													<button type="submit" class="btn btn-primary" id="submit"
														name="submit" data-dismiss="modal" onclick="addJudge();">提交更改</button>

												</form>

											</div>
										</div>
										<!-- /.modal-content -->
									</div>
									<!-- /.modal -->
								</div>

							</div>
						</div>
					</div>
				</div>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>操作1</th>
							<th>操作2</th>
							<th>题目</th>
							<th>答案</th>
							<th>提示</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${fn:length(judgeList) gt 0}">
							<c:forEach var="i" begin="0" end="${fn:length(judgeList)-1}"
								step="1">
								<tr>
									<th><button class="am-btn am-btn-default am-btn-secondary"
											onclick="getJudgeById(${judgeList[i].judgeId})">修改</button></th>
									<th>
										<button class="am-btn am-btn-default am-btn-danger"
											onclick="deleteJudge(${judgeList[i].judgeId})">删除</button>
									</th>
									<th><a>${judgeList[i].judgeProblem}</a></th>
									<th><a>${judgeList[i].judgeAnswer }</a></th>
									<th><a>${judgeList[i].judgeTips  }</a></th>
								<tr>
							</c:forEach>
						</c:if>
					</tbody>
				</table>

			</div>
		</div>
	</div>
</body>

</html>