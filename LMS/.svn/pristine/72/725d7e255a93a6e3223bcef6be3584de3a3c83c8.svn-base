<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 9. 25.     	정태일            최초 생성
 *	2025. 9. 25.		김수현			style 삭제
 *	2025. 10. 24.		김수현			졸업 이수 차트 추가
-->

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8" />
<title>학생시스템</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentdashboard.css" />

<link rel="stylesheet"  href="${pageContext.request.contextPath}/css/chatbot.css">
</head>
<body class="student-dashboard">
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>

<div class="container-xxl flex-grow-1 container-p-y">
    <div class="student-profile-container">

        <!-- 3열 × 2행 그리드 -->
        <div class="dashboard-grid">
			<!-- ========== 1행: 3개 카드 ========== -->
            <div class="dashboard-row-1">
	            <!-- 1열 1행: 프로필 -->
	            <div class="profile-card">
	                <div class="profile-content">
	                    <div class="dash-info-photo">
	                        <c:choose>
	                            <c:when test="${not empty student.photoId}">
	                                <c:url var="photoUrl" value="/lms/student/info/photo"/>
	                                <img src="${photoUrl}" alt="증명사진" class="photo-img">
	                            </c:when>
	                            <c:otherwise>
	                                <div class="photo-placeholder">
	                                    <i class="bx bx-user"></i>
	                                    <span>증명사진 없음</span>
	                                </div>
	                            </c:otherwise>
	                        </c:choose>
	                    </div>
	                    <div class="profile-info">
	                        <h2 class="profile-name">${student.lastName}${student.firstName}</h2>
	                        <div class="profile-detail">
	                            <span class="detail-label">학번:</span>
	                            <span class="detail-value">${student.studentNo}</span>
	                        </div>
	                        <div class="profile-detail">
	                            <span class="detail-label">소속:</span>
	                            <span class="detail-value">${student.univDeptName}</span>
	                        </div>
	                        <div class="profile-detail">
	                            <span class="detail-label">학적상태:</span>
	                            <span class="detail-value">${student.stuStatusName}</span>
	                        </div>
	                    </div>
	                </div>
	            </div>

	            <!-- 2열 1행: 전체 학기 성적 -->
	            <div class="profile-card card-chart">
	                <div class="card-header-section">
	                    <h3 class="card-title">전체 학기 성적</h3>
	                    <p class="card-subtitle">평균 평점: ${totalGPA} / 4.5</p>
	                </div>
	                <div class="chart-container">
	                    <div id="gradeChart"></div>
	                </div>
	            </div>

	            <!-- 3열 1행: 졸업 이수 현황 -->
	            <div class="profile-card card-chart">
	                <div class="card-header-section">
	                    <h3 class="card-title">졸업 이수 현황</h3>
	                </div>
	                <div class="chart-container">
	                    <div id="graduationChart"></div>
	                </div>
	            </div>
			</div> <!-- dashboard-row-1 끝 -->

			<!-- ========== 2행: 4개 카드 ========== -->
    		<div class="dashboard-row-2">

	            <!-- 1열 2행: 지도교수 -->
	            <div class="profile-card">
	                <h3 class="card-title">나의 지도교수</h3>
	                <div class="advisor-content">
	                    <div class="advisor-avatar">
	                        <i class="bx bx-user-circle"></i>
	                    </div>
	                    <div class="advisor-info">
	                        <p class="advisor-name">${student.professorName}</p>
	                        <p class="advisor-detail">
	                            <i class="bx bx-buildings"></i>
	                            ${student.univDeptName}
	                        </p>
	                        <p class="advisor-detail">
	                            <i class="bx bx-envelope"></i>
	                            ${student.professorEmail}
	                        </p>
	                        <p class="advisor-detail">
	                            <i class="bx bx-map"></i>
	                            공학관 503호
	                        </p>
	                    </div>
	                </div>
	            </div>

	            <!-- 2열 2행: 수강중인 강의 -->
	            <div class="profile-card">
	                <h3 class="card-title">수강중인 강의</h3>
	                <ul class="course-list">
	                    <li class="course-item">
	                        <i class="bx bx-book course-icon"></i>
	                        <div class="course-info">
	                            <div class="course-name">자료구조</div>
	                            <div class="course-time">월1, 수2</div>
	                        </div>
	                    </li>
	                    <li class="course-item">
	                        <i class="bx bx-book course-icon"></i>
	                        <div class="course-info">
	                            <div class="course-name">운영체제</div>
	                            <div class="course-time">화3, 목4</div>
	                        </div>
	                    </li>
	                    <li class="course-item">
	                        <i class="bx bx-book course-icon"></i>
	                        <div class="course-info">
	                            <div class="course-name">데이터베이스</div>
	                            <div class="course-time">금1, 금2</div>
	                        </div>
	                    </li>
	                </ul>
	            </div>

	            <!-- 3열 2행: 제출할 과제 -->
	            <div class="profile-card">
	                <h3 class="card-title">제출할 과제</h3>
	                <ul class="assignment-list">
	                    <li class="assignment-item">
	                        <div class="assignment-info">
	                            <div class="assignment-course">[자료구조]</div>
	                            <div class="assignment-name">트리 순회 구현</div>
	                        </div>
	                        <span class="assignment-badge badge-danger">D-2</span>
	                    </li>
	                    <li class="assignment-item">
	                        <div class="assignment-info">
	                            <div class="assignment-course">[운영체제]</div>
	                            <div class="assignment-name">데드락 분석 보고서</div>
	                        </div>
	                        <span class="assignment-badge badge-warning">D-5</span>
	                    </li>
	                    <li class="assignment-item">
	                        <div class="assignment-info">
	                            <div class="assignment-course">[데이터베이스]</div>
	                            <div class="assignment-name">정규화 프로젝트</div>
	                        </div>
	                        <span class="assignment-badge badge-success">D-9</span>
	                    </li>
	                </ul>
	            </div>

				<!-- 4열 2행: 채용정보 -->
				<div class="profile-card">
				    <div class="card-header-flex">
				        <h3 class="card-title">💼 채용정보</h3>
				        <a href="/portal/job/student" class="card-link">더보기 +</a>
				    </div>
				    <ul class="job-list">
				        <c:forEach var="jobNotice" items="${jobNotices}" end="2">
				            <li class="job-item">
				                <a href="/portal/job/student/${jobNotice.recruitId}" class="job-link">
				                    <div class="job-info">
				                        <div class="job-title">${jobNotice.title}</div>
				                        <div class="job-date">${jobNotice.recStartDay}</div>
				                    </div>
				                </a>
				            </li>
				        </c:forEach>
				        <c:if test="${empty jobNotices}">
				            <li class="job-empty">등록된 채용정보가 없습니다.</li>
				        </c:if>
				    </ul>
				</div> <!-- 채용정보 카드 끝 -->

			</div> <!-- dashboard-row-2 끝 -->

        </div>



    </div> <!-- student-profile-container 끝-->
</div> <!-- container-xxl 끝 -->

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
	        <button class="quick-btn" data-question="수강신청할 수 있는 최대학점은 몇 점이야?">
	            📚 수강신청 최대 학점
	        </button>
	        <button class="quick-btn" data-question="그럼 4학년 2학기는?">
	            🎓 4학년 학점
	        </button>
	        <button class="quick-btn" data-question="휴학 신청 조건이 뭐야?">
	            📅 휴학 신청 조건
	        </button>
	        <button class="quick-btn" data-question="전과하고 싶은데 전과 조건이 뭐야?">
	            🔄 전과 신청 조건
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

	<!-- 챗봇 WebSocket 라이브러리 (chatbot.js 전) -->
	<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>

	<script src="/js/app/portal/chatbot.js"></script>

</body>
</html>