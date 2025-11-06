<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 13.     		정태일           최초 생성
 *  2025. 10. 13.     		신혜진           알림 기능 추가
 *  2025. 10. 13.     		정태일           공지사항 학사공지 및 프로필 기능 추가
 *	2025. 10. 14.			김수현		   네비게이션 채용 탭 active 설정 변경
 *	2025. 10. 14.			정태일	       공지사항 학사공지 추가
 *	2025. 10. 14.			정태일	       css, navbar 정리
 *	2025. 10. 20.			정태일	       대시보드 피드백 수정
 *  2025. 10. 30.     		김수현           포털 UI 구조 조정
 *
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JSU LMS에 오신 것을 환영합니다</title>

<%-- 	<link rel="stylesheet"  href="${pageContext.request.contextPath}/css/potalBoard.css"> --%>
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/css/portalDashBoard.css">
	<link rel="stylesheet"  href="${pageContext.request.contextPath}/css/chatbot.css">



 	<link rel='stylesheet'  href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' >
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/swal2.min.css">

    <script src="${pageContext.request.contextPath}/js/app/portal/portalDashboard.js"></script>
</head>
<body>
<jsp:include page="/WEB-INF/fragments/navbar-portal.jsp" />
<jsp:include page="/WEB-INF/fragments/preStyle-portal.jsp" />

<!-- 이미지 슬라이더 영역 -->
<div class="hero-slider">
    <div class="slider-container">
        <div class="slide active">
            <img src="${pageContext.request.contextPath}/images/slider1.jpg" alt="캠퍼스 이미지">
            <div class="slide-content">
                <h2>미래를 설계하는 지식의 허브,</h2>
                <h2>변화를 주도하는 인재의 요람</h2>
                <p>JSU대학교</p>
            </div>
        </div>
        <div class="slide">
            <img src="${pageContext.request.contextPath}/images/slider2.jpg" alt="도서관 이미지">
            <div class="slide-content">
                <h2>지식의 전당</h2>
                <h2>JSU 중앙도서관</h2>
                <p>24시간 열린 학습 공간</p>
            </div>
        </div>
        <div class="slide">
            <img src="${pageContext.request.contextPath}/images/slider3.jpg" alt="연구실 이미지">
            <div class="slide-content">
                <h2>미래를 여는 연구</h2>
                <h2>혁신의 중심</h2>
                <p>세계적 수준의 연구 환경</p>
            </div>
        </div>
    </div>

    <!-- 슬라이더 컨트롤 -->
    <button class="slider-btn prev" onclick="changeSlide(-1)">&#10094;</button>
    <button class="slider-btn next" onclick="changeSlide(1)">&#10095;</button>

    <!-- 인디케이터 -->
    <div class="slider-indicators">
        <span class="indicator active" onclick="currentSlide(0)"></span>
        <span class="indicator" onclick="currentSlide(1)"></span>
        <span class="indicator" onclick="currentSlide(2)"></span>
    </div>
</div>

<!-- 🔗 바로가기 메뉴 섹션 -->
<div class="quick-menu-section">
    <div class="quick-menu-container">
        <security:authorize access="hasRole('STUDENT')">
            <a href="/student" class="quick-menu-item">
                <div class="quick-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                        <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                    </svg>
                </div>
                <span>학사관리시스템</span>
            </a>
        </security:authorize>
        <security:authorize access="hasRole('PROFESSOR')">
            <a href="/professor" class="quick-menu-item">
                <div class="quick-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                        <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                    </svg>
                </div>
                <span>학사관리시스템</span>
            </a>
        </security:authorize>
        <security:authorize access="hasAnyRole('STAFF', 'ADMIN')">
            <a href="/staff" class="quick-menu-item">
                <div class="quick-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                        <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                    </svg>
                </div>
                <span>학사관리시스템</span>
            </a>
        </security:authorize>

        <a href="/portal/univcalendar" class="quick-menu-item">
            <div class="quick-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
            </div>
            <span>학사일정</span>
        </a>

        <security:authorize access="hasRole('STUDENT')">
            <a href="/portal/job/student" class="quick-menu-item">
                <div class="quick-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                        <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                        <line x1="8" y1="11" x2="8" y2="16"></line>
                        <line x1="12" y1="11" x2="12" y2="16"></line>
                        <line x1="16" y1="11" x2="16" y2="16"></line>
                    </svg>
                </div>
                <span>채용정보</span>
            </a>
        </security:authorize>
        <security:authorize access="!hasRole('STUDENT')">
            <a href="/portal/job/internal" class="quick-menu-item">
                <div class="quick-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                        <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                        <line x1="8" y1="11" x2="8" y2="16"></line>
                        <line x1="12" y1="11" x2="12" y2="16"></line>
                        <line x1="16" y1="11" x2="16" y2="16"></line>
                    </svg>
                </div>
                <span>채용정보</span>
            </a>
        </security:authorize>

        <a href="/portal/facility/reservation" class="quick-menu-item">
            <div class="quick-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                    <polyline points="9 22 9 12 15 12 15 22"></polyline>
                </svg>
            </div>
            <span>시설예약</span>
        </a>

        <a href="/portal/certificate" class="quick-menu-item">
            <div class="quick-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                    <polyline points="14 2 14 8 20 8"></polyline>
                    <line x1="16" y1="13" x2="8" y2="13"></line>
                    <line x1="16" y1="17" x2="8" y2="17"></line>
                    <polyline points="10 9 9 9 8 9"></polyline>
                </svg>
            </div>
            <span>증명서</span>
        </a>
    </div>
</div>

<div class="container">
<!-- 	<aside class="sidebar"> -->
<!-- 		<div class="profile-box"> -->
<!-- 			<div class="profile-icon"><i class='bx bxs-user'></i></div> -->
<%-- 			<strong><security:authentication --%>
<%-- 					property="principal.realUser.lastName" /> --%>
<%-- 				<security:authentication property="principal.realUser.firstName" /></strong> --%>

<!-- 			<p> -->
<%--                 <security:authorize access="hasRole('STUDENT')"> --%>
<!--                     학번: -->
<%--                 </security:authorize> --%>
<%--                 <security:authorize access="hasRole('PROFESSOR')"> --%>
<!--                     교번: -->
<%--                 </security:authorize> --%>
<%--                 <security:authorize access="hasAnyRole('STAFF', 'ADMIN')"> --%>
<!--                     사번: -->
<%--                 </security:authorize> --%>
<%--                 <security:authentication property="principal.realUser.userNo"/> --%>
<!-- 			</p> -->

<!-- 			<div class="profile-actions"> -->
<!-- 				<button type="button" class="btn btn-primary" id="openPwModal">비밀번호변경</button> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 	</aside> -->

    <div class="main">
        <div class="content-grid-2col">

            <div class="col-left-notice-group">

					<!-- 학사일정 캘린더 -->
                    <section class="calendar-section-full">

                        <h3><i class='bx bx-calendar'></i> 학사일정 <a href="/portal/univcalendar" class="more-link">+</a></h3>
                        <div class="academic-calendar-container">
                            <div id="dashboard-calendar-wrapper">
                                <div id="dashboard-calendar"></div>
                            </div>
                        </div>
                    </section>


                <section class="section section-notice-group">
                    <div class="tab-menu">
                        <button class="tab-btn active" data-tab="notice-pane">
                            📢 공지사항
                        </button>
                        <button class="tab-btn" data-tab="academic-pane">
                            🎓 학사공지
                        </button>
                        <a href="/portal/notice/list" class="more-link">+</a>
                    </div>

                    <div class="tab-content-area">
                        <div class="tab-pane tab-pane-notice active" id="notice-pane">
                            <ul>
                                <c:forEach var="notice" items="${generalNotices}" end="5">
                                    <li>
                                        <a href="/portal/notice/detail/${notice.noticeId}">
                                          <c:if test="${notice.isUrgent == 'Y'}">
											   <span class="badge-urgent">긴급</span>
											</c:if>
                                        <span class="title">${notice.title}</span>
                                        </a>
                                        <span class="date">${notice.createAt != null ? notice.createAt.toLocalDate() : ''}</span>
                                    </li>
                                </c:forEach>
                                <c:if test="${empty generalNotices}"><li>등록된 공지사항이 없습니다.</li></c:if>
                                <c:if test="${fn:length(generalNotices) < 4}"><c:forEach begin="${fn:length(generalNotices)}" end="3"><li class="empty-list-item">.</li></c:forEach></c:if>
                            </ul>
                        </div>

                        <div class="tab-pane tab-pane-academic" id="academic-pane">
                            <ul>
                                <c:forEach var="notice" items="${academicNotices}" end="5">
                                    <li>
                                        <a href="/portal/academicnotice/detail/${notice.noticeId}">
                                        <span class="title">${notice.title}</span>
                                        </a>
                                        <span class="date">${notice.createAt != null ? notice.createAt.toLocalDate() : ''}</span>
                                    </li>
                                </c:forEach>
                                <c:if test="${empty academicNotices}"><li>등록된 학사공지가 없습니다.</li></c:if>
                                <c:if test="${fn:length(academicNotices) < 4}"><c:forEach begin="${fn:length(academicNotices)}" end="3"><li class="empty-list-item">.</li></c:forEach></c:if>
                            </ul>
                        </div>
                    </div>
                </section>
            </div>

            <div class="col-right-3row">

                <section class="section section-job">
					<h3>
						<i class='bx bx-briefcase'></i> 채용정보
						<security:authorize access="hasRole('STUDENT')">
							<a href="/portal/job/student" class="more-link">+</a>
						</security:authorize>
						<security:authorize access="!hasRole('STUDENT')">
							<a href="/portal/job/internal" class="more-link">+</a>
						</security:authorize>
					</h3>

                    <ul>
                        <c:forEach var="jobNotice" items="${jobNotices}" end="4">
                            <li>
                                <security:authorize access="hasRole('STUDENT')">
                                    <a href="/portal/job/student/${jobNotice.recruitId}"><span class="title">${jobNotice.title}</span></a>
                                </security:authorize>
                                <security:authorize access="!hasRole('STUDENT')">
                                    <a href="/portal/job/internal/${jobNotice.recruitId}"><span class="title">${jobNotice.title}</span></a>
                                </security:authorize>
                                <span class="date">${jobNotice.recStartDay}</span>
                            </li>
                        </c:forEach>
                        <c:if test="${empty jobNotices}"><li>등록된 채용정보가 없습니다.</li></c:if>
                    </ul>
                </section>

                <section class="section section-reservation">
                    <h3><i class='bx bx-calendar-check'></i> 나의 시설예약 <a href="/portal/facility/my-reservations" class="more-link">+</a></h3>
                    <ul>
                        <c:forEach var="reservation" items="${myReservations}" end="5">
                            <li>
                                <a href="/portal/facility/my-reservations"><span class="title">${reservation.placeName}</span></a>
                                <span class="date">${fn:substring(reservation.startAt, 0, 10)}</span>
                            </li>
                        </c:forEach>
                        <c:if test="${empty myReservations}">
                            <li>예약된 시설이 없습니다.</li>
                        </c:if>
                    </ul>
                </section>


                <section class="section section-cert">
                    <h3><i class='bx bx-file'></i> 증명서 발급내역 <a href="/portal/certificate" class="more-link">+</a></h3>
                    <ul>
                        <c:forEach var="cert" items="${certList}" end="5">
                            <li>
                                <a href="/portal/certificate"><span class="title">${cert.certificateName}</span></a>
                                <span class="date">${cert.formattedRequestAt}</span>
                            </li>
                        </c:forEach>
                        <c:if test="${empty certList}">
                            <li>발급된 증명서가 없습니다.</li>
                        </c:if>
                    </ul>
                </section>
            </div>
        </div>
    </div>
</div>


<div class="footer">
    Copyright &copy; 2025 JSU University. All Rights Reserved.
</div>


	<div id="pwChangeModal" class="pwC-modal">
<div class="modal-content">
    <span class="close-button">&times;</span>
    <h3 class="modal-title">비밀번호 변경</h3>
    <form id="pwChangeForm" action="/portal/changePassword" method="post">
        <div class="form-group">
            <label for="currentPassword">현재 비밀번호</label>
            <input type="password" id="currentPassword" name="currentPassword" required>
        </div>
        <div class="form-group">
            <label for="newPassword">새 비밀번호</label>
            <input type="password" id="newPassword" name="newPassword" required>
        </div>
        <div class="form-group">
            <label for="confirmPassword">새 비밀번호 확인</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
        </div>
        <button type="submit" class="modal-submit-btn">변경</button>
    </form>
</div>
</div>

<!-- 챗봇 -->
<a href="#" id="chatbot-button" class="chatbot-icon">
    <div>🤖</div>
    <span>도우미</span>
</a>

<!-- 챗봇 창 -->
<div id="chatbot-container" style="display: none;">
    <div id="chatbot-header">
        <div class="chatbot-title">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            JSU대학교 AI 학사 도우미
        </div>
        <button id="chatbot-close">×</button>
    </div>

    <div id="chatbot-messages">
        <div class="message bot-message">
            <div class="message-content">
                👋 안녕하세요! <br>
                🤖 JSU대학교 학사 정보 AI 도우미입니다. <br>
                궁금하신 점을 물어보세요!
            </div>
        </div>
    </div>

    <!-- 자주 묻는 질문 -->
    <div id="quick-questions-container" class="quick-questions-hidden">
        <button class="quick-btn" data-question="수강신청 최대 학점?">
            📚 수강신청 최대 학점
        </button>
        <button class="quick-btn" data-question="2학년은?">
            🎓 2학년 학점
        </button>
        <button class="quick-btn" data-question="3학년은?">
            📖 3학년 학점
        </button>
        <button class="quick-btn" data-question="2학기는?">
            📅 2학기 학점
        </button>
        <button class="quick-btn" data-question="휴학 신청 기간은?">
            🏖️ 휴학 신청
        </button>
        <button class="quick-btn" data-question="전과 조건은?">
            🔄 전과 조건
        </button>
    </div>

    <!-- 토글 버튼 -->
    <div id="quick-toggle-container">
        <button id="quick-toggle-btn" class="quick-toggle">
            <svg id="toggle-icon" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="18 15 12 9 6 15"></polyline>
            </svg>
        </button>
    </div>

    <div id="chatbot-input-container">
        <input type="text" id="chatbot-input" placeholder="질문을 입력하세요..." />
        <button id="chatbot-send">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="22" y1="2" x2="11" y2="13"></line>
                <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
            </svg>
        </button>
    </div>
</div>

<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %> <!--  알림 웹소켓 js -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<!-- 챗봇 WebSocket 라이브러리 (chatbot.js 전) -->
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>

<script src="/js/app/portal/chatbot.js"></script>
<script src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.8/index.global.min.js"></script>
<script src="${pageContext.request.contextPath}/js/app/portal/portalDashboardCalendar.js"></script>

<!-- 슬라이더 기능은 portalDashboard.js에서 처리됩니다 -->

</body>
</html>
