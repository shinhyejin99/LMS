<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>신청 처리 현황</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentAcademicChangeStatus.css" />
</head>

<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>

<!-- 외부 wrapper -->
<div class="professor-lecture-page">
    <div class="ssc-wrapper">

        <!-- 페이지 헤더 -->
        <div class="page-header">
            <h1>신청 처리 현황</h1>
        </div>

        <div class="list-main-section">

            <!-- 상태 요약 카드 -->
            <div class="status-summary-container">
                <div id="statusSummary" class="status-cards-grid">
                    <!-- 동적 생성 -->
                </div>
            </div>

            <!-- 필터 영역 -->
            <div class="filter-section">
                <div class="filter-group">
                    <label for="filterType">신청 구분:</label>
                    <select id="filterType">
                        <option value="">전체</option>
                        <option value="DROP">자퇴</option>
                        <option value="REST">휴학</option>
                        <option value="RTRN">복학</option>
                        <option value="DEFR">졸업유예</option>
                        <c:forEach var="affil" items="${affilList}">
                            <c:if test="${affil.commonCd != 'MJ_PRE'}">
                                <option value="${affil.commonCd}">
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

                <div class="filter-group">
                    <label for="filterStatus">처리 상태:</label>
                    <select id="filterStatus">
                        <option value="">전체</option>
                        <c:forEach var="apply" items="${applyList}">
                            <option value="${apply.commonCd}" data-desc="${apply.cdDesc}">
                                ${apply.cdName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <!-- 테이블 -->
            <div class="ssc-table-container">
                <table class="ssc-table">
                    <thead class="ssc-thead">
                        <tr class="ssc-tr">
                            <th class="ssc-th">접수유형</th>
                            <th class="ssc-th">신청 구분</th>
                            <th class="ssc-th">신청일자</th>
                            <th class="ssc-th">승인일자</th>
                            <th class="ssc-th">처리상태</th>
                            <th class="ssc-th">승인자</th>
                        </tr>
                    </thead>
                    <tbody class="ssc-tbody" id="statusTableBody">
                        <!-- JavaScript로 동적 생성 -->
                    </tbody>
                </table>

                <!-- 데이터 없을 때 -->
                <div id="emptyMessage" class="empty-message" style="display: none;">
                    신청 내역이 없습니다.
                </div>
            </div>
        </div>
    </div>
</div>
<!-- wrapper 닫기 -->

<!-- 상세보기 모달 -->
<div id="sscDetailModal" class="ssc-modal">
    <div class="ssc-modal-content">
        <div class="ssc-modal-header">
            <h2 class="ssc-modal-title">학적변동 신청 상세정보</h2>
            <span class="ssc-close" onclick="closeSSCDetailModal()">&times;</span>
        </div>
        <div class="ssc-modal-body">
            <div class="ssc-detail-section">
                <div class="ssc-detail-row">
                    <span class="ssc-detail-label">접수번호</span>
                    <span class="ssc-detail-value" id="sscModalReceiptNo"></span>
                </div>
                <div class="ssc-detail-row">
                    <span class="ssc-detail-label">신청 구분</span>
                    <span class="ssc-detail-value" id="sscModalCategory"></span>
                </div>
                <div class="ssc-detail-row">
                    <span class="ssc-detail-label">신청일자</span>
                    <span class="ssc-detail-value" id="sscModalDate"></span>
                </div>
                <div class="ssc-detail-row">
                    <span class="ssc-detail-label">처리상태</span>
                    <span class="ssc-detail-value" id="sscModalStatus"></span>
                </div>
                <div class="ssc-detail-row">
                    <span class="ssc-detail-label">승인자</span>
                    <span class="ssc-detail-value" id="sscModalApprover"></span>
                </div>
            </div>

            <!-- 휴학일 때만 표시 -->
            <!-- (오른쪽 상단 - 필요시 표시) -->
		    <div class="ssc-detail-section" id="leaveDetailSection" style="display:none;">
		        <h4>휴학 상세</h4>
		        <div class="ssc-detail-row">
		            <span class="ssc-detail-label">휴학 종류</span>
		            <span class="ssc-detail-value" id="sscModalLeaveType"></span>
		        </div>
		        <div class="ssc-detail-row">
		            <span class="ssc-detail-label">휴학 기간</span>
		            <span class="ssc-detail-value" id="sscModalLeaveDuration"></span>
		        </div>
		    </div>

            <!-- 소속변경 상세 정보-->
            <!-- 소속변경 상세 (2열 전체 - 필요시 표시) -->
		    <div id="affilDetailSection" class="ssc-detail-section" style="display:none;">
		        <h4>소속변경 상세</h4>
		        <div class="detail-grid">
		            <div class="detail-item">
		                <span class="detail-label">목표 학과:</span>
		                <span class="detail-value" id="sscModalTargetDept">-</span>
		            </div>
		            <div class="detail-item">
		                <span class="detail-label">단과대학:</span>
		                <span class="detail-value" id="sscModalTargetCollege">-</span>
		            </div>
		        </div>
		    </div>

            <!-- 신청사유 (2열 전체) -->
		    <div class="ssc-detail-section ssc-detail-row ssc-full-width">
		        <span class="ssc-detail-label">📝 신청사유</span>
		        <div class="ssc-reason-box" id="sscModalReason"></div>
		    </div>

		    <!-- 반려사유 (2열 전체 - 필요시 표시) -->
		    <div class="ssc-detail-section ssc-detail-row ssc-full-width" id="rejectionReasonSection" style="display: none;">
		        <span class="ssc-detail-label">⚠️ 반려사유</span>
		        <div class="ssc-reason-box" id="sscModalRejectionReason"></div>
		    </div>

		    <!-- 첨부파일 (2열 전체) -->
		    <div class="ssc-detail-section ssc-detail-row ssc-full-width">
		        <span class="ssc-detail-label">📎 첨부파일</span>
		        <div class="ssc-file-box" id="sscModalFile"></div>
		    </div>
        </div>
        <div class="ssc-modal-footer">
            <!-- 취소 버튼-->
            <button class="ssc-btn-cancel" id="sscModalCancelBtn" style="display:none;" onclick="cancelApply()">
                신청 취소
            </button>
            <button class="ssc-btn-close" onclick="closeSSCDetailModal()">닫기</button>
        </div>
    </div>
</div>

<script>
    <c:if test="${not empty applyList}">
        var applyStatusCodes = [
            <c:forEach var="apply" items="${applyList}" varStatus="status">
                { commonCd: "${apply.commonCd}", cdName: "${apply.cdName}" }${!status.last ? ',' : ''}
            </c:forEach>
        ];
    </c:if>

    if (typeof applyStatusCodes === 'undefined') {
        var applyStatusCodes = [];
    }
</script>

<script src="${pageContext.request.contextPath}/js/app/student/studentAcademicChangeStatus.js"></script>

</body>
</html>