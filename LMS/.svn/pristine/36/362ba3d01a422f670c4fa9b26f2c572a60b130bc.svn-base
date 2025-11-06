<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 29.     	정태일            최초 생성
 * 	2025. 10. 14.		김수현			네비게이션 채용 탭 active 설정 변경
 * 	2025. 10. 15.		정태일			navbar,css 통합(중복제거)
  *  2025. 10. 31. 		김수현 			css 추가
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공공채용 상세 - ${job.title}</title>

    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Public+Sans:wght@300;400;500;600;700&display=swap">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalPubBoardDetail.css">
    <!-- css 적용 : 게시판 스타일 적용 -->
<%-- 	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css"> --%>

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">

    <script src="${pageContext.request.contextPath}/js/app/portal/portalJob.js"></script>

</head>
<body>
<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<div class="main-container">

	<!-- 브래드크럼 -->
	<div class="breadcrumb">
        <a href="/portal">홈</a>
        <span>&gt;</span>
        <a href="/portal/job/public">공공 채용</a>
        <span>&gt;</span>
        <span>상세보기</span>
    </div>

    <!-- 제목 영역 -->
    <div class="title-section">
        <h1 class="job-title">${job.title}</h1>
    </div>

    <!-- 정보 카드 -->
	<div class="info-card">
	    <!-- 왼쪽: 정보 영역 -->
	    <div class="info-left">
	        <div class="info-item">
	            <span class="info-label">기업명</span>
	            <span class="info-value" id="organization">${job.organization}</span>
	        </div>

	        <div class="info-item">
	            <span class="info-label">기업규모</span>
	            <span class="info-value" id="companyScale">${job.companyScale}</span>
	        </div>

	        <div class="info-item">
	            <span class="info-label">고용형태</span>
	            <span class="info-value" id="employmentType">${job.employmentType}</span>
	        </div>

	        <div class="info-item">
	            <span class="info-label">접수기간</span>
	            <span class="info-value" id="recDay">
	                ${job.recStartDay} ~ ${job.recEndDay}
	            </span>
	        </div>
	    </div>

	    <!-- 오른쪽: 로고 영역 -->
	    <c:if test="${not empty job.logoUrl}">
	        <div class="logo-section">
	            <img src="${job.logoUrl}" alt="${job.organization} 로고" class="company-logo">
	        </div>
	    </c:if>

	    <!-- 로고가 없을 경우 빈 공간 -->
	    <c:if test="${empty job.logoUrl}">
	        <div class="logo-section">
	            <span style="color: #adb5bd;">로고 없음</span>
	        </div>
	    </c:if>
	</div>

    <!-- 채용 내용 -->
    <div class="content-section">
        <h2 class="section-title">채용 내용</h2>
        <div class="content-box" id="jobDetailContent">

            <!-- 자격요건 -->
            <c:if test="${not empty job.recrCommCont}">
                <div class="content-item">
                    <h3>자격요건</h3>
                    <div class="content-text">${job.recrCommCont}</div>
                </div>
            </c:if>

            <!-- 제출서류 -->
            <c:if test="${not empty job.empSubmitDocCont}">
                <div class="content-item">
                    <h3>제출서류</h3>
                    <div class="content-text">${job.empSubmitDocCont}</div>
                </div>
            </c:if>

            <!-- 접수방법 -->
            <c:if test="${not empty job.empRcptMthdCont}">
                <div class="content-item">
                    <h3>접수방법</h3>
                    <div class="content-text">${job.empRcptMthdCont}</div>
                </div>
            </c:if>

            <!-- 문의처 -->
            <c:if test="${not empty job.inqryCont}">
                <div class="content-item">
                    <h3>문의처</h3>
                    <div class="content-text">${job.inqryCont}</div>
                </div>
            </c:if>

            <!-- 기타사항 -->
            <c:if test="${not empty job.empnEtcCont}">
                <div class="content-item">
                    <h3>기타사항</h3>
                    <div class="content-text">${job.empnEtcCont}</div>
                </div>
            </c:if>

            <c:if test="${empty job.recrCommCont and empty job.empSubmitDocCont and empty job.empRcptMthdCont and empty job.inqryCont and empty job.empnEtcCont}">
                <p class="no-content">등록된 상세 내용이 없습니다.</p>
            </c:if>
        </div>
    </div>

    <!-- 관련 링크 -->
    <div class="link-section" id="linkSection">
        <h2 class="section-title">관련 링크</h2>
        <div class="link-buttons">
            <c:if test="${not empty job.homepageUrl}">
                <a href="${job.homepageUrl}" target="_blank" class="btn btn-outline">
                    🏢 기업 홈페이지
                </a>
            </c:if>
            <c:if test="${not empty job.recruitUrl}">
                <a href="${job.recruitUrl}" target="_blank" class="btn btn-primary">
                    📋 채용공고 바로가기
                </a>
            </c:if>
        </div>
    </div>

    <!-- 버튼 영역 -->
    <div class="button-group">
        <button type="button" class="btn btn-secondary" id="listJobBtn">목록</button>
    </div>
</div>

<footer class="footer">
    Copyright © 2025 JSU University. All Rights Reserved.
</footer>
</body>
</html>