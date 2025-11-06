<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 21.     		정태일           최초 생성
 *  2025. 10. 31.		김수현			css 추가, 브래드크럼 조정
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>시설 예약 캘린더</title>
    <!-- css 적용 : 게시판 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/potalBoard.css">

	<!-- 리뉴얼된 CSS 사용 : 네비게이션 스타일 적용 -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalDashBoard.css">

<%--     <link rel="stylesheet" href="${pageContext.request.contextPath}/css/portalFacilityReservation.css"> --%>
<style>
#calendar-view {
    width: 100%;
    max-width: 1200px;
    margin: auto;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 0 15px rgba(0,0,0,0.1);
    padding: 20px;
}
.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.calendar-header .date-nav {
    display: flex;
    align-items: center;
    gap: 10px;
}
.calendar-header .date-nav button {
    background: none;
    border: 1px solid #ccc;
    padding: 5px 10px;
    cursor: pointer;
    border-radius: 4px;
}
.calendar-grid-weekly {
    display: grid;
    grid-template-columns: 60px repeat(7, 1fr);
    border-top: 2px solid #dee2e6;
    box-sizing: border-box;
}
.grid-header {
    text-align: center;
    padding: 10px 0;
    font-weight: bold;
    border-bottom: 2px solid #dee2e6;
    border-left: 1px solid #dee2e6;
}
/* 코너 셀(시간 라벨 위 헤더) 높이 고정 */
.grid-header.corner {
	  border-left: none;
}

.time-labels-column {
    padding-top: 0;
}
.time-label {
    height: 40px;
    text-align: right;
    padding-right: 10px;
    font-size: 12px;
    color: #6c757d;
    border-top: 1px solid #e9ecef;
}
.day-column {
    border-left: 1px solid #dee2e6;
    position: relative;
    min-height: calc(9 * 40px);
    background-image: repeating-linear-gradient(to bottom, #f1f1f1, #f1f1f1 1px, transparent 1px, transparent 40px);
    background-size: 100% 40px;
    cursor: pointer;
}
.day-column:hover {
    background-color: rgba(0, 123, 255, 0.05);
}
.reservation-block {
    position: absolute;
    left: 2px;
    right: 2px;
    background-color: #ffc107;
    border: 1px solid #d39e00;
    border-radius: 4px;
    padding: 5px;
    font-size: 12px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    color: #333;

    /* 1. 텍스트 가운데 정렬 */
    text-align: center;
    line-height: 1.2;

    /* 2. Flexbox를 사용하여 수직/수평 중앙 정렬 시도 */
    display: flex;
    flex-direction: column; /* 내용을 세로로 쌓음 */
    justify-content: center; /* 수직(세로) 가운데 정렬 */
    align-items: center; /* 수평(가로) 가운데 정렬 */

    /* 3. 텍스트가 짤리는 것을 방지하기 위해 white-space: normal로 변경 */
    white-space: normal;
}

/* 예약 블록 내부의 자식 div 요소에도 중앙 정렬을 강제 적용 */
.reservation-block div {
    text-align: center !important;
    /* width: 100%; 제거 (Flexbox가 크기를 자동으로 조정하도록 함) */
    margin: 0;
}
.my-reservation {
    background-color: #a0c4ff; /* Light blue */
    border-color: #6b9bed;
}

/* JSP <style> 태그 내부에 추가 */
.button-container {
    text-align: center;
    margin-top: 30px;
    margin-bottom: 30px;
}

.btn-list {
    background-color: #f8f9fa;
    color: #343a40;
    border: 1px solid #ced4da;
    padding: 8px 20px;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 500;
    transition: background-color 0.2s;
}
.btn-list:hover {
    background-color: #e2e6ea;
}
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/fragments/navbar-portal.jsp"%>
    <%@ include file="/WEB-INF/fragments/preStyle-portal.jsp"%>

    <div class="main-container">
    	<div class="breadcrumb">
            <a href="/portal">
	        	<i class='bx bx-home-alt'></i>
	        	홈
	        </a>
            <span>&gt;</span>
            <a href="/portal/facility/reservation">시설예약</a>
            <span>&gt;</span>
<!--             <a href="/portal/facility/reservation">스터디룸</a> -->
<!--             <span>&gt;</span> -->
            <span>${place.placeName}</span>
        </div>

        <h1 class="page-title">시설 예약</h1>


	    <div class="tab-navigation">
	        <a href="/portal/facility/reservation" class="tab-item active">시설 예약하기</a>
	        <a href="/portal/facility/my-reservations" class="tab-item">나의 예약 현황</a>
	    </div>

		<!-- 상태 안내 -->
		<div class="notice-banner" style="display:flex;gap:12px;align-items:flex-start;padding:12px 16px;background:#f0f9ff;border:1px solid #d0ebff;color:#0b7285;border-radius:12px;margin:12px 0;">
		  <span style="width:8px;height:8px;border-radius:999px;background:#22b8cf;margin-top:6px;"></span>
		  <div><strong>※ 선택하신 시설은 현재 예약접수가 가능상태</strong>입니다.</div>
		</div>
		<div class="guide-box" style="padding:12px 16px;background:#fff8e1;border:1px solid #ffe8a1;border-radius:12px;color:#7f6b00;margin-bottom:16px;line-height:1.5;">
		  <ul style="margin:0;padding-left:18px;">
		    <li>예약신청은 <strong>06:00 ~ 18:00</strong>까지만 가능합니다. (당일 신청 가능)</li>
		    <li>동일 신청 내 <strong>최대 7일</strong>까지 연속 예약할 수 있습니다.</li>
		    <li>자세한 사항은 대관 관련 문의 <strong>(042-222-8201~2)</strong>로 연락해 주십시오.</li>
		  </ul>
		</div>

        <main class="main-content">
            <div id="calendar-view" class="view-container active">
                <div class="calendar-header">
                    <div class="date-nav">
                        <button id="prev-week-btn">&lt; 이전</button>
                        <h3 id="current-week-display"></h3>
                        <button id="next-week-btn">다음 &gt;</button>
                    </div>
                    <h3 id="current-facility-display" style="text-align: center; flex-grow: 1;">${place.placeName}</h3>
                    <button type="button" class="btn-list" onclick="history.back()">목록</button>
                </div>

                <div class="calendar-grid-weekly">
                    <!-- Grid Headers -->
                    <div class="grid-header corner"></div>
                    <div class="grid-header"></div>
                    <div class="grid-header"></div>
                    <div class="grid-header"></div>
                    <div class="grid-header"></div>
                    <div class="grid-header"></div>
                    <div class="grid-header"></div>
                    <div class="grid-header"></div>

                    <!-- Time Labels Column -->
                    <div class="time-labels-column" id="time-labels">
                        <div class="time-label">09:00</div>
                        <div class="time-label">10:00</div>
                        <div class="time-label">11:00</div>
                        <div class="time-label">12:00</div>
                        <div class="time-label">13:00</div>
                        <div class="time-label">14:00</div>
                        <div class="time-label">15:00</div>
                        <div class="time-label">16:00</div>
                        <div class="time-label">17:00</div>
                    </div>

                    <!-- Day Columns -->
                    <div class="day-column"></div>
                    <div class="day-column"></div>
                    <div class="day-column"></div>
                    <div class="day-column"></div>
                    <div class="day-column"></div>
                    <div class="day-column"></div>
                    <div class="day-column"></div>
                </div>
            </div>
        </main>
    </div>

    <!-- Reservation Modal -->
    <div class="modal fade" id="reservation-modal" tabindex="-1" aria-labelledby="reservationModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="reservationModalLabel">시설 예약</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <form id="reservation-form">
            <div class="modal-body">
                <div class="mb-3">
                  <label for="res-date" class="form-label">예약 날짜</label>
                  <input type="text" class="form-control" id="res-date" name="date" readonly>
                </div>
                <div class="mb-3">
                  <label for="res-end-date" class="form-label">종료 날짜 (최대 7일)</label>
                  <input type="date" class="form-control" id="res-end-date" name="endDate" required>
                </div>
                <div class="mb-3">
                    <label for="start-time" class="form-label">시작 시간</label>
                    <select class="form-select" id="start-time" name="startTime" required>
                        <!-- Options will be populated by JavaScript -->
                    </select>
                </div>
                <div class="mb-3">
                    <label for="end-time" class="form-label">종료 시간</label>
                    <select class="form-select" id="end-time" name="endTime" required>
                        <!-- Options will be populated by JavaScript -->
                    </select>
                </div>
                <div class="mb-3">
                    <label for="res-reason" class="form-label">예약 사유</label>
                    <textarea class="form-control" id="res-reason" name="reason" rows="3" required></textarea>
                </div>
                <div class="mb-3">
                    <label for="res-headcount" class="form-label">사용 인원</label>
                    <input type="number" class="form-control" id="res-headcount" name="headcount" min="1" required>
                </div>
            </div>
            <div class="modal-footer">
              <button type="submit" class="btn btn-primary">예약 신청</button>
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <footer class="footer">
        Copyright © 2025 JSU University. All Rights Reserved.
    </footer>

    <script>const C_PATH = "${pageContext.request.contextPath}";</script>
    <script>const PLACE_CD = "${place.placeCd}";</script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/app/portal/portalFacilityCalendar.js"></script>
</body>
</html>