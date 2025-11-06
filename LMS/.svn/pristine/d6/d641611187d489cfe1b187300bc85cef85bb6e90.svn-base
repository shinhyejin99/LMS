<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>

<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 27.     	김수현            최초 생성
 *	2025. 10. 1.		김수현			파일 처리 추가
 * 	2025. 10. 14.		김수현			네비게이션 채용 탭 active 설정 변경
 * 	2025. 10. 15.		정태일			navbar 통합(중복제거)
-->


<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>학내채용 정보 수정</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Public+Sans:wght@300;400;500;600;700&display=swap">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoardForm.css">

    <!-- css 적용 : 게시판 스타일 적용 -->
<%-- 	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css"> --%>

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">


	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>
    <script src="${pageContext.request.contextPath}/js/app/portal/portalJobForm.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div class="main-container">
        <h1 class="page-title">
            <c:choose>
                <c:when test="${not empty job}">학내채용 정보 수정</c:when>
                <c:otherwise>학내채용 정보 등록</c:otherwise>
            </c:choose>
        </h1>

		<form action="/portal/job/internal/modify/${job.recruitId}" method="post" enctype="multipart/form-data" id="jobForm" novalidate>

            <c:if test="${not empty job}">
                <input type="hidden" name="recruitId" value="${job.recruitId}">
            </c:if>

            <div class="form-group">
                <label for="title" class="form-label">제목 <span class="required">*</span></label>
                <input type="text" class="form-control" id="title" name="title"
                       value="${job.title}" placeholder="채용공고 제목을 입력하세요" required maxlength="200">
            </div>

            <div class="form-group">
			    <label for="content" class="form-label">내용 <span class="required">*</span></label>

			    <textarea class="form-control textarea"
			              id="content"
			              name="content"
			              placeholder="채용공고 상세 내용을 입력하세요"
			              required
			              maxlength="4000">${job.content}</textarea>

			    <div class="form-help">최대 4000자까지 입력 가능합니다.</div>
			</div>

            <div class="form-group">
                <label for="stfDeptName" class="form-label">작성부서 <span class="required">*</span></label>
                <input type="text" class="form-control" id="stfDeptName" name="stfDeptName"
                       value="${job.stfDeptName}" placeholder="작성부서명을 입력하세요" required maxlength="50">
            </div>

            <div class="form-row">
                <div class="form-col">
                    <div class="form-group">
                        <label for="agencyName" class="form-label">채용주체 <span class="required">*</span></label>
                        <input type="text" class="form-control" id="agencyName" name="agencyName"
                               value="${job.agencyName}" placeholder="예: JSU대학교" required maxlength="200">
                    </div>
                </div>
                <div class="form-col">
                    <div class="form-group">
                        <label for="recTarget" class="form-label">채용대상</label>
                        <input type="text" class="form-control" id="recTarget" name="recTarget"
                               value="${job.recTarget}" placeholder="예: 연구교수, 조교, 직원" maxlength="200">
                    </div>
                </div>
            </div>

            <div class="form-row">
                <div class="form-col">
                    <div class="form-group">
                        <label for="recStartDay" class="form-label">접수시작일 <span class="required">*</span></label>
                        <input type="date" class="form-control" id="recStartDay" name="recStartDay"
                               value="${job.recStartDay}" required>
                    </div>
                </div>
                <div class="form-col">
                    <div class="form-group">
                        <label for="recEndDay" class="form-label">접수마감일 <span class="required">*</span></label>
                        <input type="date" class="form-control" id="recEndDay" name="recEndDay"
                               value="${job.recEndDay}" required>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">첨부파일</label>

                <!-- 기존 파일 영역 -->
				<c:if test="${not empty job.attachFileId}">
				    <div class="existing-files-wrapper">
				        <div class="existing-files-header">
				            <span>기존 첨부 파일</span>
				            <small class="existing-files-hint">유지할 파일을 선택하세요</small>
				        </div>
				        <div id="existingFilesList"></div>
				        <input type="hidden" id="existingFileId" value="${job.attachFileId}">
				    </div>
				</c:if>

			    <!-- 새 파일 추가 -->
			    <div class="file-input-wrapper">
			        <input type="file" class="file-input" id="files" name="files" multiple
			               accept=".pdf,.doc,.docx,.hwp,.txt,.zip,.jpg,.png">
			        <label for="files" class="file-input-label">
			            <c:choose>
			                <c:when test="${not empty job.attachFileId}">새 파일 추가 (선택사항)</c:when>
			                <c:otherwise>파일 선택 (선택사항)</c:otherwise>
			            </c:choose>
			        </label>
			        <div class="selected-file" id="selectedFileName">선택된 파일 없음</div>
			    </div>

			    <div class="form-help">
			        <c:choose>
			            <c:when test="${not empty job.attachFileId}">
			                첨부파일은 최대 5개이며, 각 파일당 20MB를 초과할 수 없고, 전체 파일 합산 크기는 100MB를 넘을 수 없습니다.
			            </c:when>
			        </c:choose>
			    </div>
            </div>

            <div class="button-group">
                <button type="button" class="btn btn-secondary" id="edit-cancelBtn">취소</button>
                <button type="submit" class="btn btn-primary">수정</button>
            </div>
        </form>
    </div>

    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>



</body>
</html>