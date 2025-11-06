<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 16.     	정태일           최초 생성 (portalNoticeEdit.jsp 복사)
 *  2025. 10. 31.       김수현			css 추가 및 브래드크럼 조정
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>학사공지 정보 수정</title>
    <!-- css -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoardForm.css">
    <!-- css 적용 : 게시판 스타일 적용 -->
<%-- 	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css"> --%>

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">


	<!-- SweetAlert2 JS -->
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/39.0.1/classic/ckeditor.js"></script>
    <script src="${pageContext.request.contextPath}/js/app/portal/portalAcademicNoticeForm.js"></script>

</head>
<body>
<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div class="main-container">
        <h1 class="page-title">
            <c:choose>
                <c:when test="${not empty notice}">학사공지 정보 수정</c:when>
                <c:otherwise>학사공지 정보 등록</c:otherwise>
            </c:choose>
        </h1>

        <form action="/portal/academicnotice/<c:choose><c:when test="${not empty notice}">modify/${notice.noticeId}</c:when><c:otherwise>register</c:otherwise></c:choose>"
              method="post" enctype="multipart/form-data" id="noticeForm" novalidate>

            <c:if test="${not empty notice}">
                <input type="hidden" name="noticeId" value="${notice.noticeId}">
                <input type="hidden" name="noticeTypeCd" value="${notice.noticeTypeCd}">
                <input type="hidden" name="staffNo" value="${notice.staffNo}">
                <input type="hidden" name="createAt" value="${notice.createAt}">
                <input type="hidden" name="deleteYn" value="${notice.deleteYn}">
            </c:if>

            <div class="form-group">
                <label for="title" class="form-label">제목 <span class="required">*</span></label>
                <input type="text" class="form-control" id="title" name="title"
                       value="${notice.title}" placeholder="학사공지 제목을 입력하세요" required maxlength="200">
            </div>

            <div class="form-group">
			    <label for="content" class="form-label">내용 <span class="required">*</span></label>

			    <textarea class="form-control textarea"
			              id="content"
			              name="content"
			              placeholder="학사공지 상세 내용을 입력하세요"
			              required
			              maxlength="4000">${notice.content}</textarea>

			    <div class="form-help">최대 4000자까지 입력 가능합니다.</div>
			</div>

            <div class="form-group">
                <label class="form-label">첨부파일</label>

                <!-- 기존 파일 영역 -->
                <c:if test="${not empty notice.attachFileId}">
             		<div class="existing-files-wrapper">
				        <div class="existing-files-header">
				            <span>기존 첨부 파일</span>
				            <small class="existing-files-hint">유지할 파일을 선택하세요</small>
				        </div>
                        <div id="existingFilesList"></div>
                        <input type="hidden" id="existingFileId" value="${notice.attachFileId}">
                    </div>
                </c:if>

                <!-- 새 파일 추가 -->
			    <div class="file-input-wrapper">
			        <input type="file" class="file-input" id="files" name="files" multiple
			               accept=".pdf,.doc,.docx,.hwp,.txt,.zip,.jpg,.png">
			        <label for="files" class="file-input-label">
                   	    <c:choose>
			                <c:when test="${not empty notice.attachFileId}">새 파일 추가 (선택사항)</c:when>
			                <c:otherwise>파일 선택 (선택사항)</c:otherwise>
			            </c:choose>
			        </label>
			        <div class="selected-file" id="selectedFileName">선택된 파일 없음</div>
			    </div>

			    <div class="form-help">
			        <c:choose>
			            <c:when test="${not empty notice.attachFileId}">
			                첨부파일은 최대 5개이며, 각 파일당 20MB를 초과할 수 없고, 전체 파일 합산 크기는 100MB를 넘을 수 없습니다.
			            </c:when>
			        </c:choose>
			    </div>
            </div>

            <div class="button-group">
                <button type="button" class="btn btn-secondary" id="edit-cancelBtn">취소</button>
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${not empty notice}">수정</c:when>
                        <c:otherwise>등록</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </form>
    </div>

    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>
</body>
</html>