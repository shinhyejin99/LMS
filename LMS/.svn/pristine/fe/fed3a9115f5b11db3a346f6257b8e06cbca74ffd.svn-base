<%-- 
 * == 개정이력(Modification Information) ==
 *   
 *   수정일      		수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 1.     		송태호            최초 생성
 *
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>JSU 클래스룸 - 게시글 작성</title>
<style>
.ck.ck-editor {max-width: 100%;}
.ck-editor__editable {min-height: 400px;}
</style>
<script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>
<script src="${pageContext.request.contextPath}/js/app/classroom/professor/prfBoardPostForm.js"></script>
<script type="module">
import { uploadClassroomFiles } from "${pageContext.request.contextPath}/js/app/classroom/common/uploadFiles.js";

document.addEventListener("DOMContentLoaded", () => {
	const fileInput = document.getElementById("files");
	const uploadBtn = document.getElementById("uploadBtn");
	const fileList = document.getElementById("file-list");

	fileInput.addEventListener("change", async () => {
		fileList.textContent = "";
		if (fileInput.files.length > 0) {
			fileList.innerHTML = Array.from(fileInput.files)
					.map((f) => `• \${f.name} (\${(f.size / 1024).toFixed(1)} KB)<br>`)
					.join("\n");
		} else {
			fileList.textContent = "선택된 파일이 없습니다.";
		}
		
		try {
			const data = await uploadClassroomFiles(
				'${lectureId}'
				, 'board'
				, fileInput
				, 'fileId'
			);
			console.log("업로드 성공: " + data.fileId);
		} catch (err) {
			console.error("업로드 실패: " + err.message);
		}
	});

});
</script>



</head>
<body>
	<section id="post-root" class="container py-4"
			  data-ctx="${pageContext.request.contextPath}"
			  data-lecture-id="<c:out value='${lectureId}'/>" 
			  data-api-base="${pageContext.request.contextPath}/classroom/api/v1/professor/board" 
			  data-redirect="${pageContext.request.contextPath}/classroom/professor/${lectureId}/board">

		<!-- 강의 내 네비게이션 -->
		<c:set var="activeTab" value="board" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<div class="d-flex align-items-center justify-content-between mb-3">
			<h1 class="h3 mb-0">게시글 작성</h1>
			<a class="btn btn-outline-secondary btn-sm" href="${pageContext.request.contextPath}/classroom/professor/${lectureId}/board"> 목록으로 </a>
		</div>

		<!-- 작성 폼 -->
		<form id="post-form" class="vstack gap-3" enctype="multipart/form-data">
			<c:if test="${not empty _csrf}">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</c:if>
			<input type="hidden" name="lectureId" value="<c:out value='${lectureId}'/>" /> <input type="hidden" name="publishAt" id="publishAt" />

			<!-- 1) 제목(좁게) + 타입(오른쪽) -->
			<div class="row g-3 align-items-end">
				<div class="col-12 col-md-4 col-lg-1">
					<label for="postType" class="form-label">
						타입<span class="text-danger">*</span>
					</label>
					<select id="postType" name="postType" class="form-select">
						<option value="NOTICE">공지</option>
						<option value="MATERIAL">자료</option>
					</select>
				</div>
				<div class="col-12 col-md-8 col-lg-6">
					<label for="title" class="form-label">
						제목<span class="text-danger">*</span>
					</label>
					<input type="text" id="title" name="title" class="form-control" maxlength="200" placeholder="제목을 입력하세요" />
				</div>
			</div>

			<!-- 2) 첨부파일 -->
			<div>
				<label for="files" class="form-label">첨부파일</label>
				<input type="file" id="files" name="files" class="form-control" multiple />
				<input type="hidden" id="fileId" name="fileId">
				<div id="file-list" class="form-text">선택하신 파일이 여기에 표시됩니다.</div>
			</div>
			
			<!-- 알림 -->
			<div id="alert-box" class="alert alert-danger mt-3 d-none" role="alert"></div>
			<div id="toast-box" class="alert alert-success mt-3 d-none" role="alert">저장되었습니다.</div>

			<!-- 3) 내용 -->
			<div>
				<label for="content" class="form-label">내용 <span class="text-danger">*</span></label>
				<textarea id="ck-editor" name="content" class="form-control" rows="12"></textarea>
			</div>

			<!-- 4) 상태 라디오 (왼쪽) + 저장 버튼 (오른쪽 끝) -->
			<div class="d-flex flex-column flex-md-row justify-content-end align-items-start align-items-md-center gap-3">
				<!-- 상태 라디오 -->
				<fieldset class="flex-grow-1">
					<legend class="col-form-label pt-0 mb-2">
						게시 상태 <span class="text-danger">*</span>
					</legend>
					<div class="d-flex flex-wrap gap-3">
						<div class="form-check">
							<input class="form-check-input" type="radio" name="status" id="stPublished" value="PUBLISHED" checked>
							<label class="form-check-label" for="stPublished">즉시 게시</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="status" id="stDraft" value="DRAFT">
							<label class="form-check-label" for="stDraft">임시저장</label>
						</div>
						<div class="form-check">
							<input class="form-check-input" type="radio" name="status" id="stScheduled" value="SCHEDULED">
							<label class="form-check-label" for="stScheduled">예약</label>
						</div>
					</div>

					<!-- 예약 선택 시 노출되는 일시 입력 (라디오 바로 아래) -->
					<div id="schedule-row" class="row g-2 align-items-end mt-2 d-none">
						<div class="col-12 col-sm-6 col-md-4">
							<label for="publishDate" class="form-label">게시 예정일</label>
							<input type="date" id="publishDate" class="form-control" />
						</div>
						<div class="col-12 col-sm-6 col-md-3">
							<label for="publishTime" class="form-label">게시 예정시각</label>
							<input type="time" id="publishTime" class="form-control" step="60" />
						</div>
					</div>
					<div class="form-text">예약 게시를 선택하면 지정한 일시에 자동으로 공개 상태로 전환됩니다.</div>
				</fieldset>

				<!-- 저장 버튼: 맨 오른쪽 붙이기 -->
				<div class="ms-md-auto">
					<button type="submit" class="btn btn-primary">저장</button>
				</div>
			</div>
		</form>
	</section>
</body>
</html>