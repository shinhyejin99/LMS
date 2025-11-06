<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>강의 목록</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/profLectureList.css">
</head>
<body>
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>
	<div class="prof-lecture-list-page">
		<div class="lecture-container">

			<div class="page-header">
				<h1>내 강의 목록</h1>
			</div>

			<div id="errorMessage" class="error-message" style="display: none;"></div>

			<div id="loading" class="loading" style="display: none;">
				<p>강의 목록을 불러오는 중...</p>
			</div>

			<div class="table-wrapper">
				<table class="lecture-table" id="lectureTable"
					style="display: none;">
					<thead>
						<tr>
							<th>강의명</th>
							<th>이수구분</th>
							<th>학년도학기</th>
							<th>학점/시수</th>
							<th>평가방식</th>
							<th>수강인원</th>
							<th>상태</th>
						</tr>
					</thead>
					<tbody id="lectureTableBody">
						<!-- JavaScript로 동적 생성 -->
					</tbody>
				</table>
			</div>

			<div id="noData" class="no-data" style="display: none;">
				<p>등록된 강의가 없습니다.</p>
			</div>
		</div>
	</div>


	<script src="${pageContext.request.contextPath}/js/app/professor/profLectureList.js"></script>
</body>
</html>