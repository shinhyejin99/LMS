<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>강의 상세 정보</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/profLectureDetail.css">
</head>
<body>

<!-- 외부 래퍼 -->
<div class="prof-lecture-detail-page">
	<div class="detail-container">

        <!-- 페이지 헤더 -->
        <div class="page-header">
            <h1 id="pageTitle">강의 상세 정보</h1>
            <a href="<c:url value='/lms/professor/lecture/list'/>" class="back-btn">← 목록으로</a>
        </div>

        <!-- 에러 메시지 -->
        <div id="errorMessage" class="error-message" style="display: none;"></div>

        <!-- 로딩 -->
        <div id="loading" class="loading">
            <p>강의 정보를 불러오는 중...</p>
        </div>

        <!-- 메인 컨텐츠 -->
        <div id="mainContent" class="content-wrapper" style="display: none;">
            <!-- 왼쪽 영역 -->
            <div class="left-section">
                <!-- 강의 개요 -->
                <div class="lecture-overview card">
                    <h2 class="card-title" id="subjectName">-</h2>
                    <div class="lecture-info-grid">
                        <div class="info-item">
                            <span class="info-label">학과:</span>
                            <span class="info-value" id="univDeptName">-</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">학기:</span>
                            <span class="info-value" id="yeartermCd">-</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">학점/시수:</span>
                            <span class="info-value" id="creditHour">-</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">평가기준:</span>
                            <span class="info-value" id="subjectTypeName">-</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">수강인원:</span>
                            <span class="info-value" id="capacity">-</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">상태:</span>
                            <span class="info-value" id="lectureStatus">-</span>
                        </div>
                    </div>
                </div>

                <!-- 주차 계획 -->
                <div class="week-plan-section card">
                    <div class="week-plan-header">
                        <h3 class="section-title">주차별 강의 계획</h3>
                    </div>
                    <div class="week-plan-content" id="weekPlanContent">
                        <!-- JavaScript로 동적 생성 -->
                    </div>
                </div>
            </div>

            <!-- 오른쪽 영역 - 성적 현황 -->
            <div class="right-section">
                <div id="scoreSection" class="score-section card" style="display: none;">
                    <div class="score-header">
                        <h3 class="section-title">학생별 성적 현황</h3>
                        <div class="score-controls">
                            <div class="search-box">
                                <input type="text" id="searchInput" placeholder="학번, 이름, 학과로 검색">
                            </div>
                            <div class="sort-box">
                                <label>정렬:</label>
                                <select id="sortSelect">
                                    <option value="finalGrade-desc">성적 높은 순</option>
                                    <option value="finalGrade-asc">성적 낮은 순</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="score-table-wrapper">
                        <table class="score-table">
                            <thead>
                                <tr>
                                    <th>학번</th>
                                    <th>이름</th>
                                    <th>학과</th>
                                    <th>성적</th>
                                </tr>
                            </thead>
                            <tbody id="scoreTableBody">
                                <!-- 동적 생성 -->
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- 학생이 수강하기 전 메시지 -->
                <div id="noScoreSection" class="no-score-section card" style="display: none;">
                    <h3 class="section-title">학생별 성적 현황</h3>
                    <div class="no-data-message">
                        <p>신청한 학생이 없습니다.</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<input type="hidden" id="lectureId" value="${lectureId}">
<script src="${pageContext.request.contextPath}/js/app/professor/profLectureDetail.js"></script>
</body>
</html>