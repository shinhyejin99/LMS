<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 시험 상세</title>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfExamDetail.js" defer></script>
</head>
<body>
	<section id="exam-detail-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-lecture-id="<c:out value='${lectureId}'/>"
			 data-exam-id="<c:out value='${lctExamId}'/>"
			 data-api-base="${pageContext.request.contextPath}/classroom/api/v1/professor/exam">

		<c:set var="activeTab" value="exam" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<div class="d-flex align-items-center justify-content-between mb-4">
			<h1 class="h3 mb-0" id="exam-detail-title">시험 상세</h1>
			<div class="d-flex gap-2">
				<a href="/classroom/professor/${lectureId}/exam" class="btn btn-sm btn-outline-secondary">목록으로</a>
				<button type="button" id="btn-edit-exam" class="btn btn-sm btn-primary">수정</button>
				<button type="button" id="btn-delete-exam" class="btn btn-sm btn-danger">삭제</button>
			</div>
		</div>

		<div id="alert-box" class="alert alert-danger d-none" role="alert"></div>

		<div class="card shadow-sm mb-4">
			<div class="card-header">
				<h2 class="h5 mb-0">시험 정보</h2>
			</div>
			<div class="card-body" id="exam-info-body">
				<div class="text-center text-muted py-4">
					<div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div>
					<p class="mt-2 mb-0">시험 정보를 불러오는 중입니다...</p>
				</div>
			</div>
		</div>

		<div class="card shadow-sm">
			<div class="card-header">
				<h2 class="h5 mb-0">학생별 제출 결과</h2>
			</div>
			<div class="card-body p-0">
				<div id="student-results-body">
					<div class="text-center text-muted py-4">
						<div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div>
						<p class="mt-2 mb-0">학생 제출 결과를 불러오는 중입니다...</p>
					</div>
				</div>
			</div>
		</div>

	</section>
</body>
</html>
