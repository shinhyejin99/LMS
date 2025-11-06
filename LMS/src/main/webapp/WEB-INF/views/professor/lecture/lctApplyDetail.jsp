<%--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      		수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 29.     		송태호            강의 신청 상세 조회 화면
 *
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>강의 신청 상세</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lctApplyDetail.css">
</head>

<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>

<!-- 외부 wrapper -->
<div class="professor-lecture-page">
    <div class="ssc-wrapper">

        <!-- 페이지 헤더 -->
        <div class="page-header">
            <h1>강의 신청 상세</h1>
            <button type="button" id="backBtn" class="btn-back">목록으로</button>
        </div>

        <!-- 로딩/에러 메시지 -->
        <div id="loadingMessage" class="message-box loading" style="display: none;">
            <p>데이터를 불러오는 중...</p>
        </div>

        <div id="errorMessage" class="message-box error" style="display: none;">
            <p>데이터를 불러오는 중 오류가 발생했습니다.</p>
        </div>

        <!-- 메인 컨텐츠 -->
        <div id="detailContent" class="detail-content" style="display: none;">

            <!-- 과목 정보 + 승인 정보 (같은 줄) -->
            <div class="top-sections">
                <!-- 과목 정보 -->
                <div class="detail-section">
                    <h3 class="section-title">과목 정보</h3>
                    <div class="info-grid-subject-row1">
                        <div class="info-item">
                            <label>과목명</label>
                            <span id="subjectName">-</span>
                        </div>
                        <div class="info-item">
                            <label>학과</label>
                            <span id="univDeptName">-</span>
                        </div>
                    </div>
                    <div class="info-grid-3">
                        <div class="info-item">
                            <label>이수구분</label>
                            <span id="completionName">-</span>
                        </div>
                        <div class="info-item">
                            <label>평가유형</label>
                            <span id="subjectTypeName">-</span>
                        </div>
                        <div class="info-item">
                            <label>학점/시수</label>
                            <span id="creditHour">-</span>
                        </div>
                    </div>
                </div>

                <!-- 승인 정보 -->
                <div class="detail-section">
                    <h3 class="section-title">승인 정보</h3>
                    <div class="info-grid-3">
                        <div class="info-item">
                            <label>승인상태</label>
                            <span id="approveStatus" class="status-badge">-</span>
                        </div>
                        <div class="info-item">
                            <label>승인자</label>
                            <span id="approver">-</span>
                        </div>
                        <div class="info-item">
                            <label>승인일시</label>
                            <span id="approveAt">-</span>
                        </div>
                    </div>
                    <div class="info-full">
                        <label>코멘트</label>
                        <span id="comments">-</span>
                    </div>
                </div>
            </div>

            <!-- 신청 정보 -->
            <div class="detail-section">
                <h3 class="section-title">신청 정보</h3>
                <div class="info-grid-4">
                    <div class="info-item">
                        <label>대상학기</label>
                        <span id="yeartermName">-</span>
                    </div>
                    <div class="info-item">
                        <label>예상정원</label>
                        <span id="expectCap">-</span>
                    </div>
                    <div class="info-item">
                        <label>신청자</label>
                        <span id="applicant">-</span>
                    </div>
                    <div class="info-item">
                        <label>신청일시</label>
                        <span id="applyAt">-</span>
                    </div>
                </div>
                <div class="info-grid-2">
                    <div class="info-full">
                        <label>강의개요</label>
                        <p id="lectureIndex">-</p>
                    </div>
                    <div class="info-full">
                        <label>선수학습과목</label>
                        <p id="prereqSubject">-</p>
                    </div>
                </div>
                <div class="info-full">
                    <label>강의목표</label>
                    <p id="lectureGoal">-</p>
                </div>
                <div class="info-full">
                    <label>희망사항</label>
                    <p id="desireOption">-</p>
                </div>
            </div>

            <!-- 주차별 계획 + 성적 산출 비율 (같은 줄) -->
            <div class="bottom-sections">
                <!-- 주차별 계획 -->
                <div class="detail-section">
                    <div class="section-header-with-select">
                        <h3 class="section-title">주차별 계획</h3>
                        <div class="week-info">
                            <span class="week-count" id="weekCount">총 0주</span>
                            <select id="weekSelect" class="week-select">
                                <option value="">주차 선택</option>
                            </select>
                        </div>
                    </div>
                    <div id="weeklyPlanContainer" class="weekly-plan-container">
                        <!-- JS로 렌더링됨 -->
                    </div>
                </div>

                <!-- 성적 산출 비율 -->
                <div class="detail-section">
                    <h3 class="section-title">성적 산출 비율</h3>
                    <div id="gradeRatioContainer" class="grade-ratio-container">
                        <!-- JS로 렌더링됨 -->
                    </div>
                </div>
            </div>

        </div>

    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app/professor/lecture/lctApplyDetail.js"></script>

</body>
</html>
