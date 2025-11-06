<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 조별과제 작성</title>

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

/* 그룹 빌더 레이아웃 */
.group-builder {
	display: grid;
	grid-template-columns: 1fr 24px 2fr;
	gap: 1rem;
}

.group-list, .student-list {
	border: 1px solid #dee2e6;
	border-radius: .5rem;
	min-height: 300px;
	background: #fff;
}

.group-header {
	display: flex;
	gap: .5rem;
	align-items: center;
	justify-content: space-between;
}

.group-card {
	border: 1px solid #e9ecef;
	border-radius: .75rem;
	padding: .75rem;
	margin-bottom: .75rem;
	background: #fafafa;
}

.member-badge {
	display: inline-flex;
	align-items: center;
	gap: .35rem;
	border: 1px solid #dee2e6;
	border-radius: 999px;
	padding: .25rem .5rem;
	margin: .25rem;
	background: #fff;
}

.member-badge.leader {
	border-color: #0d6efd;
}

.member-actions .btn {
	margin-left: .25rem;
}

.list-scroll {
	max-height: 360px;
	overflow: auto;
}

#debug-json {
	white-space: pre-wrap;
	word-break: break-all;
	font-family: ui-monospace, SFMono-Regular, Menlo, Consolas,
		"Liberation Mono", monospace;
}
</style>

<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfGrouptaskPostForm.js" defer></script>
</head>

<body>
	<section id="grouptask-form-root" class="container py-4" data-ctx="${pageContext.request.contextPath}" data-lecture-id="<c:out value='${lectureId}'/>" data-common-base="${pageContext.request.contextPath}/classroom/api/v1/common" data-prof-base="${pageContext.request.contextPath}/classroom/api/v1/professor/task">

		<!-- 상단 내비 -->
		<c:set var="activeTab" value="task" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<!-- 액션바 -->
		<div class="d-flex align-items-center justify-content-between mb-3">
			<nav aria-label="breadcrumb">
				<ol class="breadcrumb mb-0">
					<li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/task/indiv">과제</a></li>
					<li class="breadcrumb-item active" aria-current="page">조별과제 작성</li>
				</ol>
			</nav>
			<div class="d-flex gap-2">
				<a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/task/group">조별과제 목록</a>
			</div>
		</div>

		<!-- 작성 카드 -->
		<form id="grouptask-form" class="card shadow-sm" autocomplete="off">
			<div class="card-body">
				<!-- 기본 정보 -->
				<div class="mb-3">
					<label for="grouptask-name" class="form-label required">과제명</label> <input type="text" id="grouptask-name" class="form-control" maxlength="50" required placeholder="예) 팀 프로젝트 1차 발표">
					<div class="form-hint">최대 50자</div>
				</div>

				<div class="mb-3">
					<label for="grouptask-desc" class="form-label required">과제설명</label>
					<textarea id="grouptask-desc" class="form-control" placeholder="과제 목적, 제출 형식, 평가 기준 등을 작성하세요."></textarea>
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

				<!-- 첨부파일 (간소화 동일) -->
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

				<hr class="my-4" />

				<!-- 그룹 빌더 -->
				<h2 class="h6 mb-2">조 구성</h2>
				<div class="form-hint mb-2">수강중인 학생을 불러와 조를 만들고, 조 이름/조장을 지정하세요.</div>

				<div class="group-builder">
					<!-- 학생 목록 -->
					<div>
						<div class="d-flex align-items-center justify-content-between mb-2">
							<strong>학생 목록</strong>
							<div class="d-flex gap-2">
								<input type="text" id="student-search" class="form-control form-control-sm" placeholder="이름/학번 검색" style="width: 180px;" />
								<button class="btn btn-sm btn-outline-secondary" type="button" id="btn-refresh-students">새로고침</button>
							</div>
						</div>
						<div class="student-list list-scroll" id="student-list">
							<!-- 동적 렌더링 -->
						</div>
						<div class="form-hint mt-1">체크박스로 여러 명 선택 후, 오른쪽 ‘선택한 학생 배치’ 버튼으로 조에 배치합니다.</div>
					</div>

					<!-- 가운데 이동 버튼 -->
					<div class="d-flex flex-column align-items-center justify-content-center">
						<button class="btn btn-sm btn-primary mb-2" type="button" id="btn-assign-selected">선택한 학생 배치 →</button>
						<button class="btn btn-sm btn-outline-secondary" type="button" id="btn-unassign-all">← 전체 배치 해제</button>
					</div>

					<!-- 그룹 목록 -->
					<div>
						<div class="d-flex align-items-center justify-content-between mb-2">
							<strong>그룹</strong>
							<div class="d-flex gap-2">
								<button class="btn btn-sm btn-outline-primary" type="button" id="btn-add-group">그룹 추가</button>
								<button class="btn btn-sm btn-outline-danger" type="button" id="btn-remove-empty-groups">빈 그룹 삭제</button>
							</div>
						</div>

						<div class="group-list list-scroll p-2" id="group-list">
							<!-- 동적 렌더링 (group-card 반복) -->
						</div>
					</div>
				</div>
			</div>

			<div class="card-footer d-flex justify-content-end gap-2">
				<a href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/task/group" class="btn btn-light">취소</a>
				<button type="submit" id="btn-submit" class="btn btn-primary">저장(로깅만)</button>
			</div>
		</form>

		<div id="error-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
		<div id="info-box" class="alert alert-success mt-3 d-none" role="alert"></div>

		<!-- 디버그(JSON 미리보기) -->
		<div class="card mt-3">
			<div class="card-header">제출 페이로드 프리뷰(로깅과 동일)</div>
			<div class="card-body">
				<pre id="debug-json" class="mb-0 small text-body-secondary">제출 시 이곳에 표시됩니다.</pre>
			</div>
		</div>
	</section>
</body>
</html>
