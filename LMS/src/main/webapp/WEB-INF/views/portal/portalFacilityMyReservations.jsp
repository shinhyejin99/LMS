<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 16.     	정태일           최초 생성, UI 구조 변경
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>나의 예약 현황</title>
	<!-- css 적용 : 게시판 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css">

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalFacility.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalFacilityMyReservation.css">


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
        <a href="/portal/facility/reservation" >시설예약</a>
        <span>&gt;</span>
        <a href="/portal/facility/my-reservations" class="breadcrumb-current">나의 예약 현황</a>
    </div>

    <h1 class="page-title">시설 예약</h1>


    <div class="tab-navigation">
<!--         <a href="/portal/facility/list" class="tab-item">시설 목록</a> -->
        <a href="/portal/facility/reservation" class="tab-item">시설 예약하기</a>
        <a href="/portal/facility/my-reservations" class="tab-item active">나의 예약 현황</a>
    </div>

    <main class="main-content">

        <section class="content-section">
            <c:set var="hasActiveReservations" value="${false}" />
            <c:forEach var="reservation" items="${myReservations}">
                <c:if test="${reservation.cancelYn == 'N'}">
                    <c:set var="hasActiveReservations" value="${true}" />
                </c:if>
            </c:forEach>

            <c:choose>
                <c:when test="${not hasActiveReservations}">
                    <div class="no-reservations">
                        <p>현재 예약된 시설이 없습니다.</p>
                        <p>새로운 시설을 예약하려면 아래 버튼을 클릭해주세요.</p>
                        <a href="/portal/facility/reservation">시설 예약하기</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="job-table">
                        <thead>
                                <tr>
                                    <th>시설명</th>
                                    <th>예약 날짜</th>
                                    <th>예약 시간</th>
                                    <th>예약 인원</th>
                                    <th>예약 관리</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="reservation" items="${myReservations}">
                                    <c:if test="${reservation.cancelYn == 'N'}">
                                        <tr>
                                            <td><span style="text-align: center;">${reservation.placeName}</span></td>
                                            <td><span style="text-align: center;">${fn:substring(reservation.startAt, 0, 10)}</span></td>
                                            <td><span style="text-align: center;">${fn:substring(reservation.startAt, 11, 16)} - ${fn:substring(reservation.endAt, 11, 16)}</span></td>
                                            <td><span style="text-align: center;">${reservation.headcount}명</span></td>
                                            <td><span style="text-align: center;">
                                                <button class="btn btn-danger btn-sm cancel-btn" data-reserve-id="${reservation.reserveId}" data-place-cd="${reservation.placeCd}">예약 취소</button>
                                            </span></td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>
    </main>
</div>

<footer class="footer">
    Copyright © 2025 JSU University. All Rights Reserved.
</footer>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="${pageContext.request.contextPath}/js/app/portal/portalFacilityMyReservations.js"></script>

</body>
</html>
