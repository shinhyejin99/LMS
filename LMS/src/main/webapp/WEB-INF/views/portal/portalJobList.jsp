<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"  %>
<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 26.     	김수현            css 분리
 *	2025. 9. 27.		정태일			pagination 부분 수정
 *	2025. 10. 1.		김수현			학생 맞춤 탭 부분 권한 설정
 * 	2025. 10. 14.		김수현			네비게이션 채용 탭 active 설정 변경
 * 	2025. 10. 15.		정태일			navbar, css 통합(중복제거)
 *  2025. 10. 31.		김수현			게시판 네비게이션 스타일 적용위해 css 추가
-->

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>채용정보</title>

	<!-- css 적용 : 게시판 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css">

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">


	<script src="${pageContext.request.contextPath}/js/app/core/paging.js"></script>
	<script src="/js/app/portal/portalJob.js"></script>

</head>
<body>
<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>


    <div class="main-container">
    	<!-- 브래드크럼 -->
		<div class="breadcrumb">
	        <a href="/portal">
	        	<i class='bx bx-home-alt'></i>
	        	홈
	        </a>
	        <span>&gt;</span>
	        <sec:authorize access="hasRole('STUDENT')">
                <%-- 학생이라면 '맞춤형 채용'을 기본 랜딩 페이지로 설정 (student) --%>
                <a href="/portal/job/student" class="breadcrumb-current">채용정보</a>
            </sec:authorize>
            <sec:authorize access="!hasRole('STUDENT')">
                <%-- 학생이 아니라면 '공공 채용' (또는 '학내 채용' internal)을 기본 랜딩 페이지로 설정 --%>
                <a href="/portal/job/internal" class="breadcrumb-current">채용정보</a>
            </sec:authorize>
	    </div>

        <h1 class="page-title">채용정보</h1>

	    <!-- 검색 영역 -->
        <div class="search-container">
            <input type="text" class="search-input" placeholder="제목명 또는 기업명(부서명)을 입력하세요" name="searchWord">
            <button class="search-btn" id="searchBtn">검색</button>
        </div>

        <!-- 탭 네비게이션 -->
        <div class="tab-navigation">
        	<sec:authorize access="hasRole('STUDENT')">
            	<div class="tab-item  active tab-special" data-type="student">
		            <span class="sparkle">✨</span>
		            <sec:authentication property="principal.realUser.lastName" /><sec:authentication property="principal.realUser.firstName" />님 맞춤형 채용
		            <span class="badge-new">NEW</span>
		        </div>
            </sec:authorize>
            <div class="tab-item" data-type="internal">🏫 학내 채용</div>
            <div class="tab-item" data-type="public">🏢 공공 채용</div>
        </div>

        <!-- 공공채용 안내 추가 -->
		<c:if test="${jobType == 'public'}">
		    <div class="notice-public">
		        <strong>📢 안내 :</strong> 공공채용은 최근 200개 공고 내에서 검색됩니다.
		    </div>
		</c:if>

        <!-- 채용정보 목록 컨테이너 (동적 생성) -->
        <div class="job-list" id="jobList">
            <!-- 게시글 동적 추가 부분 -->
        </div>
        <div id="paginationContainer" class="mt-3">
			<!-- 페이징 -->
        </div>
        <!-- 교직원만 등록 버튼 표시 -->
		<sec:authorize access="hasRole('STAFF')">
		    <button type="button" class="btn btn-primary" id="registJobBtn">
		        등록하기(교직원만)
		    </button>
		</sec:authorize>
    </div>

    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>
</body>
</html>