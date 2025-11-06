<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 * 	2025. 10. 15.		정태일			최초 생성
 *  2025. 10. 31.		김수현			css 추가
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>


<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>학사공지 상세</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoardDetail.css">

    <!-- css 적용 : 게시판 스타일 적용 -->
<%-- 	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css"> --%>

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">

	<!-- SweetAlert2 JS -->
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script src="${pageContext.request.contextPath}/js/app/portal/portalAcademicNotice.js"></script>
	</head>
<body>
<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div class="main-container" id="noticeDetailContainer">
    	 <div class="breadcrumb">
            <a href="/portal">홈</a>
            <span>&gt;</span>
            <a href="/portal/academicnotice/list">학사공지</a>
            <span>&gt;</span>
            <span>상세보기</span>
        </div>

<div class="title-section">
    <h1 class="notice-title"></h1>
</div>

<div class="notice-summary">
    <div class="summary-grid">
        <div class="summary-item">
            <span class="summary-label">작성부서:</span>
            <span class="summary-value" id="stfDeptName"></span>
        </div>
        <div class="summary-item">
            <span class="summary-label">작성일:</span>
            <span class="summary-value" id="createAt"></span>
        </div>
        <div class="summary-item">
            <span class="summary-label">조회수:</span>
            <span class="summary-value" id="viewCnt"></span>
        </div>
    </div>
</div>

<div class="content-section">
    <h2 class="section-title">공지 내용</h2>
    <div class="content-text" id="noticeDetailContent"></div>
</div>

<div class="attachment-section" id="attachmentSection">
    <h2 class="section-title">첨부파일</h2>
    <!-- JS에서 detail.attachFiles 있으면 여기에 렌더링 -->
    <c:choose>
        <c:when test="${not empty fileList}">
            <ul class="file-list">
                <c:forEach var="file" items="${fileList}">
                    <li class="file-item">
                        <a href="${pageContext.request.contextPath}/portal/file/download/${file.fileId}/${file.fileOrder}"
                           class="file-link" title="${file.originName}.${file.extension} 다운로드">
                            <!-- 파일 아이콘 또는 클립 아이콘 -->
                            <i class="fas fa-paperclip file-icon"></i>
                            <!-- 파일명.확장자 -->
                            <span class="file-name">
                                ${file.originName}.${file.extension}
                            </span>
                            <!-- 파일 크기 표시 (간단한 포맷팅 없이 바이트로 표시) -->
                            <span class="file-size text-gray-500 text-sm ml-2">
                                (<fmt:formatNumber value="${file.fileSize / 1024 / 1024}" pattern="0.00" /> MB)
                            </span>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </c:when>
        <c:otherwise>
            <p class="no-attachment text-gray-500">첨부된 파일이 없습니다.</p>
        </c:otherwise>
    </c:choose>
</div>






        <div class="action-buttons">
            <button type="button" class="btn btn-secondary" id="listNoticeBtn">목록으로</button>
<!--         	교직원만 (수정/삭제는 작성 부서에서만 가능) -->
    		<sec:authorize access="hasRole('STAFF')">
	            <button type="button" class="btn btn-primary" id="editNoticeBtn">수정</button>
	            <button type="button" class="btn btn-danger" id="deleteNoticeBtn">삭제</button>
       		</sec:authorize>
        </div>
    </div>

    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>


</body>
</html>