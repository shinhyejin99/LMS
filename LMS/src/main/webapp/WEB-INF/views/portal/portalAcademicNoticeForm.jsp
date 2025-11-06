<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 16.     	정태일            최초 생성 (portalNoticeForm.jsp 복사)
  *  2025. 10. 31.       김수현			css 추가 및 브래드크럼 조정
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"  %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>학사공지 정보 등록</title>
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

        <c:choose>
		    <c:when test="${not empty notice}">
		        <c:url var="formAction" value="/portal/academicnotice/modify/${notice.noticeId}"/>
		    </c:when>
		    <c:otherwise>
		        <c:url var="formAction" value="/portal/academicnotice/create"/>
		    </c:otherwise>
        </c:choose>

        <form action="${formAction}"
              method="post" enctype="multipart/form-data" id="noticeForm" novalidate>


            <input type="hidden" name="deleteYn" value="N">

            <div class="form-group">
                <label for="title" class="form-label">제목 <span class="required">*</span></label>
                <input type="text" class="form-control" id="title" name="title"
                       value="" placeholder="학사공지 제목을 입력하세요" required maxlength="200">
            </div>

            <div class="form-group">
			    <label for="content" class="form-label">내용 <span class="required">*</span></label>

			    <textarea class="form-control textarea"
			              id="content"
			              name="content"
			              placeholder="학사공지 상세 내용을 입력하세요"
			              required
			              maxlength="4000"></textarea>

			    <div class="form-help">최대 4000자까지 입력 가능합니다.</div>
			</div>

            <div class="form-group">
                <label class="form-label">첨부파일</label>
                <div class="file-input-wrapper">
                    <input type="file" class="file-input" id="files" name="files" multiple
                           accept=".pdf,.doc,.docx,.hwp,.txt,.zip,.jpg,.jpeg,.png">
                    <label for="files" class="file-input-label">
                        파일 선택 (선택사항)
                    </label>
                    <%-- selectedFileName은 선택된 파일 목록을 표시하는 컨테이너로 사용 --%>
                    <div class="selected-file" id="selectedFileName"></div>
                </div>
                <div class="form-help">첨부파일은 최대 5개이며, 각 파일당 **20MB**를 초과할 수 없고, 전체 파일 합산 크기는 **100MB**를 넘을 수 없습니다. (허용 형식: PDF, DOC, HWP, TXT, ZIP, 이미지 파일)</div>

            </div>

            <div class="button-group">
                <button type="button" class="btn btn-secondary" id="cancelButton">취소</button>
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