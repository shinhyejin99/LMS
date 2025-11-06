<%-- 
 * == 개정이력(Modification Information) ==
 *   수정일            수정자     수정내용
 *  ============      =======    =======================
 *  2025. 10. 5.      송태호     게시글 수정 폼 생성
 --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 게시글 수정</title>

<style>
.ck.ck-editor {
	max-width: 100%;
}

.ck-editor__editable {
	min-height: 400px;
}
</style>

<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>

<!-- 공용 업로드 모듈 -->
<script type="module"
	src="${pageContext.request.contextPath}/js/app/classroom/common/uploadFiles.js"></script>
<!-- 이 페이지 전용 스크립트(아래 JS 코드 블록을 prfBoardPostEdit.js로 저장) -->
<script type="module" src="${pageContext.request.contextPath}/js/app/classroom/professor/prfBoardPostEdit.js"></script>

</head>
<body>
	<section id="post-edit-root" class="container py-4"
		data-ctx="${pageContext.request.contextPath}"
		data-lecture-id="<c:out value='${lectureId}'/>"
		data-post-id="<c:out value='${lctPostId}'/>"
		data-api-base="${pageContext.request.contextPath}/classroom/api/v1/professor"
		data-redirect-detail="${pageContext.request.contextPath}/classroom/professor/${lectureId}/board/<c:out value='${lctPostId}'/>">

		<c:set var="activeTab" value="board" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<!-- CSRF -->
		<c:if test="${not empty _csrf}">
			<input type="hidden" id="csrfParam" value="${_csrf.parameterName}" />
			<input type="hidden" id="csrfToken" value="${_csrf.token}" />
		</c:if>

		<div class="d-flex align-items-center justify-content-between mb-3">
			<h1 class="h3 mb-0">게시글 수정</h1>
			<div class="d-flex gap-2">
				<a class="btn btn-outline-secondary btn-sm" id="btn-detail"
					href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/board/<c:out value='${lctPostId}'/>">
					상세로 </a> <a class="btn btn-outline-secondary btn-sm" id="btn-list"
					href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/board">
					목록으로 </a>
			</div>
		</div>

		<div id="error-box" class="alert alert-danger d-none" role="alert"></div>
		<div id="toast-box" class="alert alert-success d-none" role="alert">저장되었습니다.</div>

		<form id="edit-form" class="vstack gap-3"
			enctype="multipart/form-data">
			<input type="hidden" id="lectureId" name="lectureId"
				value="<c:out value='${lectureId}'/>" /> <input type="hidden"
				id="postId" name="postId" value="<c:out value='${lctPostId}'/>" /> <input
				type="hidden" id="fileId" name="fileId" />
			<!-- 업로드 성공시 교체 -->

			<!-- 타입 + 제목 -->
			<div class="row g-3 align-items-end">
				<div class="col-12 col-md-4 col-lg-2">
					<label for="postType" class="form-label">타입<span
						class="text-danger">*</span></label> <select id="postType" name="postType"
						class="form-select">
						<option value="NOTICE">공지</option>
						<option value="MATERIAL">자료</option>
					</select>
				</div>
				<div class="col-12 col-md-8 col-lg-6">
					<label for="title" class="form-label">제목<span
						class="text-danger">*</span></label> <input type="text" id="title"
						name="title" class="form-control" maxlength="100"
						placeholder="제목을 입력하세요" />
				</div>
			</div>

			<!-- 첨부파일(교체 업로드) -->
			<div>
				<label for="files" class="form-label">첨부파일(업로드 시 기존 첨부를
					대체합니다)</label> <input type="file" id="files" name="files"
					class="form-control" multiple />
				<div id="file-list" class="form-text">현재 첨부: 불러오는 중…</div>
				<div id="file-hint" class="form-text">최대 5개. 새로 업로드하면 기존 첨부는
					대체됩니다.</div>
			</div>

			<!-- 내용 -->
			<div>
				<label for="ck-editor" class="form-label">내용 <span
					class="text-danger">*</span></label>
				<textarea id="ck-editor" name="content" class="form-control"
					rows="12"></textarea>
				<div class="form-text">서버 제한: 최대 1000자</div>
			</div>

			<!-- 상태 라디오 + 예약 시각 -->
			<div
				class="d-flex flex-column flex-md-row justify-content-end align-items-start align-items-md-center gap-3">
				<fieldset class="flex-grow-1">
					<legend class="col-form-label pt-0 mb-2">
						게시 상태 <span class="text-danger">*</span>
					</legend>
					<div class="d-flex flex-wrap gap-3">
						<div class="form-check">
							<input class="form-check-input" type="radio" name="status"
								id="stPublished" value="PUBLISHED"> <label
								class="form-check-label" for="stPublished">즉시 게시</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="status"
								id="stDraft" value="DRAFT"> <label
								class="form-check-label" for="stDraft">임시저장</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="status"
								id="stScheduled" value="SCHEDULED"> <label
								class="form-check-label" for="stScheduled">예약</label>
						</div>
					</div>

					<div id="schedule-row" class="row g-2 align-items-end mt-2 d-none">
						<div class="col-12 col-sm-6 col-md-4">
							<label for="publishDate" class="form-label">게시 예정일</label> <input
								type="date" id="publishDate" class="form-control" />
						</div>
						<div class="col-12 col-sm-6 col-md-3">
							<label for="publishTime" class="form-label">게시 예정시각</label> <input
								type="time" id="publishTime" class="form-control" step="60" />
						</div>
					</div>
				</fieldset>

				<div class="ms-md-auto d-flex gap-2">
					<button type="button" class="btn btn-outline-secondary"
						id="btn-cancel">취소</button>
					<button type="submit" class="btn btn-primary" id="btn-save">저장</button>
				</div>
			</div>
		</form>
	</section>
</body>
</html>
