<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *	2025. 9. 27.		정태일			pagination 부분 수정
 * 	2025. 10. 14.		김수현			네비게이션 채용 탭 active 설정 변경
 * 	2025. 10. 15.		정태일			navbar,css 통합(중복제거)
 *  2025. 10. 31.		김수현			css 추가
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"  %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>공지사항</title>
    <!-- SweetAlert2 추가 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

	<!-- css 적용 : 게시판 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css">

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">

	<script>
	    let isStaff = false;
	</script>
	<sec:authorize access="hasRole('STAFF')">
	    <script>
	        isStaff = true;
	    </script>
	</sec:authorize>


	<script src="${pageContext.request.contextPath}/js/app/core/paging.js"></script>
	<script src="${pageContext.request.contextPath}/js/app/portal/portalNotice.js"></script>

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
                <a href="/portal/notice/list" class="breadcrumb-current">공지사항</a>
	    </div>

        <h1 class="page-title">공지사항</h1>

<!--         탭 네비게이션 -->
<!--         <div class="tab-navigation"> -->
<!--             <div class="tab-item active" data-type="general">공지사항</div> -->
<!--             <div class="tab-item" data-type="academic">학사공지 [수정중] </div> -->
<!--         </div> -->

        <!-- 검색 영역 -->
        <div class="search-container">
            <select class="search-select" name="searchType">
                <option value="title">제목</option>
                <option value="content">내용</option>
                <option value="company">작성부서</option>
            </select>
            <input type="text" class="search-input" placeholder="검색어 입력" name="searchWord">
            <button class="search-btn" id="searchBtn">검색</button>

        </div>

        <!-- 공지사항 목록 컨테이너 (동적 생성) -->
        <div class="notice-list" id="noticeList">
            <!-- 게시글 동적 추가 부분 -->
        </div>
        <div id="paginationContainer" class="mt-3">
			<!-- 페이징 -->
        </div>
        <!-- 교직원만 등록 버튼 표시 -->
		<sec:authorize access="hasRole('STAFF')">
        <button type="button" class="btn btn-primary" id="registNoticeBtn">등록하기</button>
		</sec:authorize>


</div>


    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>
</body>
</html>