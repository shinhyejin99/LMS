<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 14.     		정태일           최초 생성
 * 	2025. 10. 15.		정태일			navbar 통합(중복제거)
 * 	2025. 10. 31.		정태일			학사일정 ui 수정
 * 	2025. 11. 03.		정태일			학사일정 자동완성 추가
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"  %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>학사일정</title>

    <!-- CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
<!--     <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.8/index.global.min.css" > -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalUnivCalendar.css">

    <!-- css 적용 : 게시판 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css">

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">

    <!-- FullCalendar CDN -->
    <script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.8/index.global.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/app/portal/portalUnivCalendar.js"></script>

</head>
<body>
<%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
<%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <!-- ====== 본문 ====== -->
	<div class="main-container">
		<!-- 브래드크럼 -->
		<div class="breadcrumb">
	        <a href="/portal">
	        	<i class='bx bx-home-alt'></i>
	        	홈
	        </a>
	        <span>&gt;</span>
            <a href="/portal/univcalendar" class="breadcrumb-current">학사일정</a>
	    </div>

	    <h1 class="page-title">학사일정</h1>

	   <script>
		// 이 변수는 STAFF 역할이 있을 때만 true로 설정됩니다.
		var isStaff = false;
	</script>
	<sec:authorize access="hasRole('STAFF')">
		<script>
			isStaff = true;
		</script>
	</sec:authorize>




		<!-- 필터  -->
	    <div class="toolbar">
	        <select id="termFilter" class="search-select">
	            <option value="">전체 학기</option>
	            <option value="2025_REG1">2025-1학기</option>
	            <option value="2025_REG2">2025-2학기</option>
	            <option value="2025_SUB1">2025-계절학기</option>
<!-- 	            <option value="2025_SUB2">2025-겨울계절학기</option> -->
	        </select>
	        <select id="categoryFilter" class="search-select">
	            <option value="">전체 분류</option>
	            <option value="등록">등록</option>
	            <option value="시험">시험</option>
	            <option value="방학">방학</option>
	            <option value="행사">행사</option>
	            <option value="수강신청">수강신청</option>
	        </select>

	        <div class="spacer"></div>

	        <sec:authorize access="hasRole('STAFF')">
	            <button id="createEventBtn" class="btn btn-primary">일정 등록</button>
	        </sec:authorize>
<!-- 			    <button type="button" id="viewAllEventsLink" class="view-more-button"> -->
<!-- 			        전체 일정<i class="fas fa-chevron-right"></i> -->
<!-- 			    </button>    -->
	    </div>

	    <div class="academic-calendar-container">
	              <div class="calendar-legend">
	                    <span class="legend-item"><span class="legend-color-box" style="background-color: #007bff;"></span> 등록</span>
	                    <span class="legend-item"><span class="legend-color-box" style="background-color: #ffc107;"></span> 시험</span>
	                    <span class="legend-item"><span class="legend-color-box" style="background-color: #28a745;"></span> 방학</span>
	                    <span class="legend-item"><span class="legend-color-box" style="background-color: #6c757d;"></span> 행사</span>
	                    <span class="legend-item"><span class="legend-color-box" style="background-color: #6f42c1;"></span> 수강신청</span>
	                </div>
	        <div id="calendar-wrapper">
	            <div id="calendar"></div>
	        </div>

            <div class="upcoming-events-panel">
                <h2 style="font-size: 1.25rem;">📝 금월 주요 일정</h2>
                <div id="upcomingEventsList" class="event-list">
                    <p class="no-events">이번 달에 예정된 주요 일정이 없습니다.</p>
                </div>
                
<!--                 <a href="#" class="view-more-link">전체 일정 목록보기</a> -->
<!-- 			    <button type="button" id="viewAllEventsLink" class="view-more-button"> -->
<!-- 			        전체 일정<i class="fas fa-chevron-right"></i> -->
<!-- 			    </button> -->
			    <a href="#" id="viewAllEventsLink" class="view-more-link-style" role="button">
			        연간 학사일정<i class="fas fa-chevron-right"></i>
			    </a>   
            </div>
        </div>

                <div id="allEventsListContainer" class="all-events-list-container" style="display:none;">
            <h2>2025년도 학사일정</h2>
            <div id="allEventsList" class="event-list"></div>
            <button id="backToCalendarBtn" class="btn btn-secondary btn-sm mt-3">▲ 캘린더로 돌아가기</button>
        </div>
    </div>

    <!-- ====== 푸터 ====== -->
    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>


    <!-- ====== 일정 등록/수정 모달 ====== -->
    <div id="eventModal" class="modal" style="display:none;">
        <div class="modal-content">
            <h2 id="modalTitle">일정 등록/수정</h2>
            <form id="eventForm">
                <input type="hidden" id="calendarId" name="calendarId">
                <div class="form-group">
                    <label for="eventTitle">제목</label>
                    <input type="text" id="eventTitle" name="scheduleName" required>
                </div>
                <div class="form-group">
                    <label for="eventStart">시작일시</label>
                    <input type="datetime-local" id="eventStart" name="startAt" required>
                </div>
                <div class="form-group">
                    <label for="eventEnd">종료일시</label>
                    <input type="datetime-local" id="eventEnd" name="endAt">
                </div>
                <div class="form-group">
                    <label for="scheduleCd">일정 분류</label>
                    <select id="scheduleCd" name="scheduleCd" class="form-control" required>
                        <option value="">선택</option>
                        <option value="등록">등록</option>
                        <option value="시험">시험</option>
                        <option value="방학">방학</option>
                        <option value="행사">행사</option>
                        <option value="수강신청">수강신청</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="yeartermCd">학년도학기</label>
                    <select id="yeartermCd" name="yeartermCd" class="form-control" required>
                        <option value="">선택</option>
                        <option value="2025_REG1">2025-1학기</option>
                        <option value="2025_REG2">2025-2학기</option>
			            <option value="2025_SUB1">2025-계절학기</option>
<!-- 			            <option value="2025_SUB2">2025-겨울계절학기</option> -->
                    </select>
                </div>
                <div class="form-group">
                    <label for="shareScopeCd">공개 범위</label>
                    <select id="shareScopeCd" name="shareScopeCd" class="form-control" required>
                        <option value="">선택</option>
                        <option value="ALL">전체</option>
                        <option value="STUDENT">학생</option>
                        <option value="PROFESSOR">교수</option>
                        <option value="STAFF">교직원</option>
                    </select>
                </div>
                <div class="modal-actions">
                	<button type="button" id="fillAcademicEventBtn" class="btn btn-info" style="margin-right: 5px;">예시 일정 채우기</button>
                    <button type="submit" class="btn btn-primary">저장</button>
                    <button type="button" id="deleteEventBtn" class="btn btn-danger" style="display:none;">삭제</button>
                    <button type="button" id="closeModalBtn" class="btn btn-secondary">닫기</button>
                </div>
            </form>
        </div>
    </div>
    <!-- ======================================= -->

    <!-- ====== 일정 상세 정보 모달 (읽기 전용) ====== -->
    <div id="eventDetailModal" class="modal" style="display:none;">
        <div class="modal-content">
            <h2>일정 상세 정보</h2>
            <div class="detail-group">
                <p class="detail-label">제목</p>
                <p id="detail-event-title" class="detail-value"></p>
            </div>
            <div class="detail-group">
                <p class="detail-label">기간</p>
                <p id="detail-event-period" class="detail-value"></p>
            </div>
            <div class="detail-group">
                <p class="detail-label">분류</p>
                <p id="detail-event-category" class="detail-value"></p>
            </div>
            <div class="modal-actions">
                <button type="button" id="closeDetailModalBtn" class="btn btn-primary">확인</button>
            </div>
        </div>
    </div>

    <!-- ======================================= -->

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</body>
</html>
