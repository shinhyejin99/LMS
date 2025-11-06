<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>전체 학사일정</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalUnivCalendar.css">

	<!-- css 적용 : 게시판 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css">

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">




</head>
<body>
    <%@ include file="/WEB-INF/fragments/navbar-portal.jsp" %>
    <%@ include file="/WEB-INF/fragments/preStyle-portal.jsp" %>

    <!-- ====== 본문 ====== -->
    <div class="main-container">

    	<!-- 브래드크럼 -->
        <div class="breadcrumb">
            <a href="/portal">홈</a>
            <span>&gt;</span>
            <a href="/portal/univcalendar" class="breadcrumb-current">학사일정</a>
            <span>&gt;</span>
            <a href="/portal/univcalendar/list" class="breadcrumb-current">전체 목록</a>
        </div>

        <h1 class="page-title">학사일정</h1>

        <div class="list-page-container card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">학사일정 전체 목록</h6>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                        <thead>
                            <tr>
                                <th>학기</th>
                                <th>구분</th>
                                <th>일정명</th>
                                <th>시작일시</th>
                                <th>종료일시</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${calendarList}" var="event">
                                <tr>
                                    <td>${event.yeartermCd}</td>
                                    <td>${event.scheduleCd}</td>
                                    <td>${event.scheduleName}</td>
                                    <td>
                                        <fmt:formatDate value="${event.startAt}" pattern="yyyy-MM-dd HH:mm" />
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${event.endAt}" pattern="yyyy-MM-dd HH:mm" />
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty calendarList}">
                                <tr>
                                    <td colspan="5" class="text-center">등록된 학사일정이 없습니다.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-end mt-3">
                    <a href="/portal/univcalendar" class="btn btn-secondary">달력으로 돌아가기</a>
                </div>
            </div>
        </div>
    </div>

    <!-- ====== 푸터 ====== -->
    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>

    <%@ include file="/WEB-INF/fragments/postScript-portal.jsp" %>
</body>
</html>