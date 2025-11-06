<!--
 * == 개정이력(Modification Information) ==
 *
 *   수정일      			수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 14.     	김수현            최초 생성
 *
-->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>소속변경 신청</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentEnrollmentStatusChange.css" />
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>

<!-- 큰 wrapper -->
<div class="professor-lecture-page">
    <div class="container">

        <!-- 페이지 헤더 -->
        <div class="page-header">
            <h1>소속변경 신청</h1>
        </div>
		
        <!-- 메인 카드 -->
        <div class="form-section">

        <div style="display: flex; align-items: flex-start; gap: 10px;">
            <!-- 학생 기본정보 -->
            <div class="student-info-box" style="flex-grow: 1;">
                <h3>신청자 정보</h3>
                <div class="student-info-grid">
                    <div class="info-item">
                        <span class="info-label">총 이수학점:</span>
                        <span class="info-value">${totalCredit}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">총 평점:</span>
                        <span class="info-value">${gpa}</span>
                    </div>
                </div>
            </div>
            <button id="autoclick" type="button" class="btn-secondary auto-fill-btn" style="white-space: nowrap;">자동입력</button>
        </div>
                    

            <!-- 유의사항 (동적 표시) -->
            <div id="noticeBox" class="notice-box" style="display:none;">
                <!-- JavaScript로 동적 생성 -->
            </div>

            <!-- 통합 신청 폼 -->
            <form id="affilApplyForm" enctype="multipart/form-data">
                <input type="hidden" name="studentNo" value="${studentInfo.studentNo}">
                <input type="hidden" name="univDeptCd" value="${studentInfo.univDeptCd}">

                <!-- 2열 그리드 시작 -->
                <div class="form-grid-2col">
                    <!-- 신청 구분 -->
                    <div class="form-group">
                        <label for="affilChangeCd">신청 구분 <span class="required">*</span></label>
                        <select id="affilChangeCd" name="affilChangeCd" required>
                            <option value="">선택하세요</option>
                            <c:forEach var="affil" items="${affilList}">
                               <%-- 1. MJ_PRE 항목은 제외 --%>
                                <c:if test="${affil.commonCd != 'MJ_PRE'}">
                                    <option value="${affil.commonCd}">
                                        <%-- 2. MJ_TRF 항목은 이름 => '전과'로 변경 --%>
                                        <c:choose>
                                            <c:when test="${affil.commonCd == 'MJ_TRF'}">
                                                전과
                                            </c:when>
                                            <c:otherwise>
                                                ${affil.cdName}
                                            </c:otherwise>
                                        </c:choose>
                                    </option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- 목표 학과 선택 -->
                    <div class="form-group">
                        <label for="targetDeptCd">목표 학과 <span class="required">*</span></label>
                        <select id="targetDeptCd" name="targetDeptCd" required disabled>
                            <option value="">신청 구분을 먼저 선택하세요</option>
                        </select>
                        <small class="help-text" id="deptHelp"></small>
                    </div>
                </div>
                <!-- 2열 그리드 끝 -->

                <!-- 신청사유 - 전체 너비 -->
                <div class="form-group">
                    <label for="applyReason">신청사유 <span class="required">*</span></label>
                    <textarea id="applyReason" name="applyReason" rows="5"
                              placeholder="신청 사유를 입력해주세요" required></textarea>
                </div>

                <!-- 첨부파일 - 전체 너비 -->
                <div class="form-group">
                    <label for="attachFiles">첨부파일</label>
                    <input type="file" id="attachFiles" name="attachFiles" multiple>
                    <small class="help-text text-danger"><strong>※ 필수:</strong> 증빙 서류를 반드시 첨부해주세요.</small>
                </div>

                <div class="button-group">
                    <button type="reset" class="reset-btn">초기화</button>
                    <button type="submit" class="submit-btn">신청하기</button>
                </div>
            </form>

        </div>

    </div>
</div>
<!-- wrapper 닫기 -->

<script src="${pageContext.request.contextPath}/js/app/student/studentAffiliationChange.js"></script>
</body>
</html>