<%--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      		수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 28.     		송태호            최초 생성
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
<title>강의 개설 신청</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/lectureApplyForm.css">
</head>

<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>

<!-- 외부 wrapper -->
<div class="professor-lecture-page">
    <div class="ssc-wrapper">

        <!-- 페이지 헤더 -->
        <div class="page-header">
            <h1>강의 개설 신청</h1>
            <div class="header-right">
                <div class="semester-select-group">
                    <label for="yearTermSelect">대상학기 :</label>
                    <select class="form-control-inline" id="yearTermSelect" required>
                        <option value="">학기를 선택하세요</option>
                    </select>
                </div>
            </div>
        </div>

        <form id="lectureApplyForm">
            <!-- 좌우 2단 레이아웃 -->
            <div class="apply-form-layout">

                <!-- 왼쪽 섹션 -->
                <div class="left-section">

                    <!-- 과목 선택 -->
                    <div class="form-section">
                        <div class="section-header-with-actions">
                            <h3 class="section-title">과목 선택</h3>
                            <button type="button" class="btn-debug" id="debugFillBtn">디버깅: 자동입력</button>
                        </div>

                        <div class="form-row-group">
                            <div class="form-group-inline">
                                <label for="collegeSelect" class="form-label">단과대학 <span class="required">*</span></label>
                                <select class="form-control" id="collegeSelect" required>
                                    <option value="">단과대학 선택</option>
                                </select>
                            </div>

                            <div class="form-group-inline">
                                <label for="deptSelect" class="form-label">학과 <span class="required">*</span></label>
                                <select class="form-control" id="deptSelect" required disabled>
                                    <option value="">학과 선택</option>
                                </select>
                            </div>

                            <div class="form-group-inline">
                                <label for="completionSelect" class="form-label">이수구분 <span class="required">*</span></label>
                                <select class="form-control" id="completionSelect" required disabled>
                                    <option value="">이수구분 선택</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="subjectSelect" class="form-label">과목명 <span class="required">*</span></label>
                            <select class="form-control" id="subjectSelect" required disabled>
                                <option value="">과목을 선택하세요</option>
                            </select>
                        </div>
                    </div>

                    <!-- 강의 정보 -->
                    <div class="form-section">
                        <h3 class="section-title">강의 정보</h3>

                        <div class="form-row-group-2">
                            <div class="form-group-inline">
                                <label for="prerequisite" class="form-label">선수학습과목</label>
                                <input type="text" class="form-control" id="prerequisite" placeholder="예: 프로그래밍 기초">
                            </div>

                            <div class="form-group-inline">
                                <label for="capacity" class="form-label">예상정원 <span class="required">*</span></label>
                                <input type="number" class="form-control" id="capacity" min="1" max="200" required placeholder="30">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="overview" class="form-label">강의개요 <span class="required">*</span></label>
                            <textarea class="form-control" id="overview" rows="4" required placeholder="강의의 전반적인 내용을 입력하세요"></textarea>
                        </div>

                        <div class="form-group">
                            <label for="objective" class="form-label">강의목표 <span class="required">*</span></label>
                            <textarea class="form-control" id="objective" rows="4" required placeholder="강의를 통해 달성하고자 하는 목표를 입력하세요"></textarea>
                        </div>

                        <div class="form-group">
                            <label for="preferences" class="form-label">희망사항</label>
                            <textarea class="form-control" id="preferences" rows="3" placeholder="강의실, 시간대 등 희망사항을 자유롭게 입력하세요"></textarea>
                        </div>
                    </div>

                </div>

                <!-- 오른쪽 섹션 -->
                <div class="right-section">

                    <!-- 주차별 계획 -->
                    <div class="form-section form-weekby">
                        <div class="section-header-with-actions">
                            <h3 class="section-title">주차별 계획 <small class="section-hint">(정규학기: 15주차 ± / 계절학기: 3~6주차)</small></h3>
                            <button type="button" class="btn-add-week" id="addWeekBtn">+ 주차 추가</button>
                        </div>

                        <div id="weeklyPlanContainer" class="weekly-plan-list"></div>
                    </div>

                    <!-- 성적 산출 비율 -->
                    <div class="form-section">
                        <h3 class="section-title">성적 산출 비율</h3>

                        <div id="gradingCriteriaContainer" class="grading-criteria-compact"></div>

                        <div class="grading-summary">
                            <strong>총 합계: <span id="totalPercentage">0</span>%</strong>
                            <span id="percentageMessage" class="percentage-msg"></span>
                        </div>
                    </div>

                </div>

            </div>

            <!-- 버튼 영역 -->
            <div class="form-actions">
                <button type="submit" class="btn-submit">신청</button>
                <button type="button" class="btn-cancel" id="cancelBtn">취소</button>
            </div>

        </form>

    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app/professor/lecture/applyForm.js"></script>

</body>
</html>
