<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>채용정보 상세</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Public+Sans:wght@300;400;500;600;700&display=swap">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoardDetail.css">
	<!-- css 적용 : 게시판 스타일 적용 -->
<%-- 	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css"> --%>

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">



	<!-- SweetAlert2 JS -->
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script src="${pageContext.request.contextPath}/js/app/portal/portalJob.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div class="main-container" id="jobDetailContainer">
        <div class="breadcrumb">
            <a href="/portal">홈</a>
            <span>&gt;</span>
            <a href="/portal/job/internal">학내 채용</a>
            <span>&gt;</span>
            <span>상세보기</span>
        </div>

        <div class="title-section">
            <h1 class="job-title"></h1>
        </div>

        <div class="job-summary">
            <div class="summary-grid">
                <div class="summary-item">
                    <span class="summary-label">채용주체:</span>
                    <span class="summary-value" id="agencyName"></span>
                </div>
                <div class="summary-item">
                    <span class="summary-label">작성부서:</span>
                    <span class="summary-value" id="stfDeptName"></span>
                </div>
                <div class="summary-item">
                    <span class="summary-label">채용대상:</span>
                    <span class="summary-value" id="recTarget"></span>
                </div>

                <div class="summary-item">
                    <span class="summary-label">접수기간:</span>
                    <span class="summary-value" id="recDay"></span>
                </div>
                <div class="summary-item">
                    <span class="summary-label">조회수:</span>
                    <span class="summary-value" id="viewCnt"></span>
                </div>
            </div>
        </div>

        <div class="content-section">
            <h2 class="section-title">채용 공고 내용</h2>
            <div class="content-text" id="jobDetailContent">

            </div>
        </div>

        <div class="attachment-section" id="attachmentSection">
		    <!-- 동적 렌더링됨 -->
		    <h2 class="section-title">첨부파일</h2>
		    <p class="no-attachment text-gray-500">첨부된 파일이 없습니다.</p>
		</div>

        <div class="action-buttons">
            <button type="button" class="btn btn-secondary" id="listJobBtn">목록으로</button>
    		<!-- 교직원만 (수정/삭제는 작성 부서에서만 가능)-->
    		<sec:authorize access="hasRole('STAFF')">
	    		<c:choose>
	    			<c:when test="${canEdit }">
			    		<button type="button" class="btn btn-primary" id="editJobBtn">수정</button>
			            <button type="button" class="btn btn-danger" id="deleteJobBtn">삭제</button>
			        </c:when>
	    			<c:otherwise>
	    				<button type="button" class="btn rounded-pill btn-dark" disabled>수정</button>
			            <button type="button" class="btn rounded-pill btn-dark" disabled>삭제</button>
			            <p class="text-muted" style="margin-top: 10px; font-size: 0.9rem;">
			                <small>※ 작성 부서만 수정/삭제가 가능합니다.</small>
			            </p>
	    			</c:otherwise>
	    		</c:choose>
    		</sec:authorize>
        </div>
    </div>

    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>

    <script>
        // controller 에서 받은 값 할당
        const jobType = '${jobType}';
        const recruitId = '${recruitId}';

        console.log("상세 페이지 초기화: jobType=", jobType, ", recruitId=", recruitId);

        document.addEventListener('DOMContentLoaded', () => {
            // JS 파일에 정의된 함수 호출
            if (jobType && recruitId) {
                 loadJobDetail(jobType, recruitId);
            } else {
                 console.error("필수 파라미터(jobType, recruitId)가 JSP Model에서 누락되었습니다.");
            }
        });
    </script>

</body>
</html>