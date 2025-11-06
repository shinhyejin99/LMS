<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 학생관리</title>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfStudentManage.js"></script>
</head>
<body>
	<section id="student-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-lecture-id="<c:out value='${lectureId}'/>"
			 data-student-api="${pageContext.request.contextPath}/classroom/api/v1/professor/${lectureId}/students"
			 data-photo-api="${pageContext.request.contextPath}/classroom/api/v1/common/${lectureId}">

		<c:set var="activeTab" value="students" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<div class="d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3 mb-3">
			<h1 class="h3 mb-0">학생 관리</h1>
			<div class="d-flex align-items-center gap-3 ms-md-auto">
				<div class="text-muted small">
					전체 학생: <span id="student-total-count">0</span>명
				</div>
				<div class="btn-group btn-group-sm" role="group" aria-label="카드 수 조절 버튼">
					<button type="button" class="btn btn-outline-secondary active" data-card-columns="3">3열</button>
					<button type="button" class="btn btn-outline-secondary" data-card-columns="4">4열</button>
					<button type="button" class="btn btn-outline-secondary" data-card-columns="5">5열</button>
				</div>
			</div>
		</div>

		<div id="student-notice-box" class="alert alert-danger d-none" role="alert"></div>

		<div class="row g-4 card-grid-3" id="student-card-container"
			 data-columns-default="3"
			 data-columns-classes='{"3":"row-cols-1 row-cols-sm-2 row-cols-lg-3","4":"row-cols-1 row-cols-sm-2 row-cols-lg-3 row-cols-xl-4","5":"row-cols-1 row-cols-sm-2 row-cols-lg-3 row-cols-xl-4 row-cols-xxl-5"}'>
			<div class="col-12">
				<div class="text-center text-muted py-5">학생 정보를 불러오는 중입니다...</div>
			</div>
		</div>
	</section>
</body>
</html>
