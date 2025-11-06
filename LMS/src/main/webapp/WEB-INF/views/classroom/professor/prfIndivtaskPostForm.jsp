<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 개인과제 작성</title>
<style>
.form-hint {
	color: #6c757d;
	font-size: .9rem;
}

.required::after {
	content: " *";
	color: #dc3545;
}

.ck-editor__editable, .ck-content {
	min-height: 320px;
}
</style>
<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfIndivtaskPostForm.js" defer></script>
</head>
<body>
	<section id="task-form-root" class="container py-4" data-ctx="${pageContext.request.contextPath}" data-lecture-id="<c:out value='${lectureId}'/>" data-api-base="${pageContext.request.contextPath}/classroom/api/v1/professor/task">

		<!-- 상단 내비 (URL 채움) -->
		<c:set var="activeTab" value="task" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<!-- 액션바 -->
		<div class="d-flex align-items-center justify-content-between mb-3">
			<nav aria-label="breadcrumb">
				<ol class="breadcrumb mb-0">
					<li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/task/indiv">과제</a></li>
					<li class="breadcrumb-item active" aria-current="page">개인과제 작성</li>
				</ol>
			</nav>
			<div class="d-flex gap-2">
				<a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/task/indiv">목록</a>
			</div>
		</div>

		<!-- 작성 카드 -->
		<form id="task-form" class="card shadow-sm" autocomplete="off">
			<div class="card-body">
				<div class="mb-3">
					<label for="task-name" class="form-label required">과제명</label> <input type="text" id="task-name" class="form-control" maxlength="50" required placeholder="예) 5주차 보고서 제출">
					<div class="form-hint">최대 50자</div>
				</div>

				<div class="mb-3">
					<label for="task-desc" class="form-label required">과제설명</label>
					<textarea id="task-desc" class="form-control" placeholder="과제 목적, 제출 형식, 평가 기준 등을 작성하세요."></textarea>
					<div class="form-hint">최대 4000자 (필수)</div>
				</div>

				<div class="row g-3">
					<div class="col-12 col-md-6">
						<label for="start-at" class="form-label required">과제 시작일</label> <input type="datetime-local" id="start-at" class="form-control" required>
					</div>
					<div class="col-12 col-md-6">
						<label for="end-at" class="form-label">과제 마감일</label> <input type="datetime-local" id="end-at" class="form-control">
					</div>
				</div>

				<!-- 첨부파일 (간소화) -->
				<div class="mt-4">
					<label class="form-label">첨부파일</label> <input type="file" id="file-input" class="form-control" multiple>
					<div class="form-hint">파일을 선택하면 즉시 업로드되고 현재 첨부가 대체됩니다. (최대 5개)</div>

					<input type="hidden" id="attach-file-id" value="">
					<div id="current-attach-box" class="mt-2 d-none">
						<div class="alert alert-info py-2 mb-2">
							현재 첨부 파일ID: <span id="current-file-id"></span>
						</div>
						<ul id="current-file-list" class="list-group small"></ul>
					</div>
				</div>
			</div>

			<div class="card-footer d-flex justify-content-end gap-2">
				<a href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/task/indiv" class="btn btn-light">취소</a>
				<button type="submit" id="btn-submit" class="btn btn-primary">저장</button>
			</div>
		</form>

		<div id="error-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
		<div id="info-box" class="alert alert-success mt-3 d-none" role="alert"></div>
	</section>
</body>
</html>
