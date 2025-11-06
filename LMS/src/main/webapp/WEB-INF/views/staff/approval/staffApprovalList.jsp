<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>강의 개설 신청 관리</title>
<script src="https://d3js.org/d3.v7.min.js"></script>
</head>
<body>
<style>
    /* 1. 상단 대시보드 & 차트 스타일 */
    .dashboard-row {
        margin-bottom: 30px;
    }

    .chart-card-image2 {
        display: flex;
        align-items: center;
        padding: 20px;
    }

    /* D3 그래프 컨테이너 스타일 */
    .chart-container {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 200px; /* 그래프 크기 고정 */
        height: 200px;
        flex-shrink: 0;
    }

    /* 범례 스타일 */
    .chart-legend {
        list-style: none;
        padding-left: 0;
        margin-left: 25px; /* 그래프와의 간격 확대 */
        font-size: 0.95rem;
        flex-shrink: 0;
    }

    .chart-legend li {
        margin-bottom: 8px;
        display: flex;
        align-items: center;
        font-weight: 500;
    }

    .legend-color-box {
        width: 12px;
        height: 12px;
        border-radius: 3px;
        margin-right: 8px;
    }

    /* 총 신청 건수 카드 스타일 */
    .total-count-card-image2 {
        text-align: center;
        height: 100%;
    }

    .total-count-display-image2 {
        font-size: 3.5rem; /* 크기 키움 */
        font-weight: 700;
        color: var(--bs-primary);
        line-height: 1.2;
        margin-top: 10px;
    }

    /* 2. 검색 및 필터 블록 스타일 */
    .search-filter-block-custom {
        background-color: var(--bs-white);
        padding: 20px;
        border-radius: 6px;
        box-shadow: 0 2px 6px 0 rgba(0, 0, 0, .08);
        margin-bottom: 30px;
    }

    .search-filter-block-custom .input-group {
        width: 100%;
    }

    .search-filter-block-custom .btn-primary {
        min-width: 80px;
        font-weight: 600;
    }

    .filter-btn-group .btn {
        font-weight: 500;
        min-width: 80px;
    }

    /* 3. 카드 스타일 (높이 일정하게 유지) */
    .request-card-a77461 {
        border: 1px solid #e0e0e0;
        background-color: var(--bs-white);
        border-radius: 8px;
        transition: box-shadow 0.2s;
        height: 100%;
        min-height: 150px; /* 최소 높이 지정하여 빈 카드도 모양 유지 */
        position: relative;
        padding: 20px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
    }

    .card-content-body {
        flex-grow: 1; /* 본문 영역이 남은 공간을 모두 차지 */
        padding-top: 10px; /* 내용과 뱃지/이니셜 간격 확보 */
    }

    .status-badge-a77461 {
        position: absolute;
        top: 0;
        left: 0;
        font-size: 0.75rem;
        font-weight: 600;
        padding: 4px 8px;
        border-radius: 8px 0 8px 0;
        color: white;
    }

    .status-badge-pending {
        background-color: #ff9900;
    }

    .status-badge-approved {
        background-color: #28a745;
    }

    .status-badge-rejected {
        background-color: #dc3545;
    }

    .avatar-initials-a77461 {
        position: absolute;
        top: 20px;
        right: 20px;
        font-size: 1rem;
        color: #6c757d;
        font-weight: 500;
    }

    .card-title-main {
        font-size: 1.15rem; /* 제목 크기 약간 조정 */
        font-weight: 700;
        margin-top: 5px;
        margin-bottom: 5px;
        line-height: 1.4;
    }

    .card-subtitle-prof {
        font-size: 0.9rem;
        color: #6c757d;
        margin-bottom: 15px;
    }

    .card-footer-action {
        margin-top: auto; /* 버튼을 항상 하단에 배치 */
    }

    .btn-action-a77461 {
        font-weight: 600;
        padding: 8px 15px;
        border-radius: 4px;
        display: inline-flex;
        align-items: center;
        text-decoration: none;
    }

    .btn-assign {
        background-color: #008080;
        border-color: #008080;
        color: white;
    }

    .btn-view {
        background-color: var(--bs-info);
        border-color: var(--bs-info);
        color: white;
    }

    .btn-rejected-view {
        background-color: #dc3545;
        border-color: #dc3545;
        color: white;
    }
</style>
<div class="container-xxl flex-grow-1 container-p-y">
    <h4 class="fw-bold py-3 mb-4">
        <span class="text-muted fw-light">승인문서 /</span> 강의 개설 신청 관리
    </h4>

    <div class="row g-4 dashboard-row">
        <div class="col-lg-8 col-md-12">
            <div class="card h-100">
                <div class="card-header border-bottom">
                    <h5 class="card-title mb-0">신청 현황</h5>
                </div>
                <div class="card-body chart-card-image2">
                    <div id="status-donut-chart" class="chart-container">
                    </div>
                    <ul class="chart-legend">
                        <li class="text-pending"><span class="legend-color-box" style="background-color: #ff9900;"></span>대기: <strong>${statusCounts.pendingCount}건</strong></li>
                        <li class="text-approved"><span class="legend-color-box" style="background-color: #28a745;"></span>승인: <strong>${statusCounts.approvedCount}건</strong></li>
                        <li class="text-rejected"><span class="legend-color-box" style="background-color: #dc3545;"></span>반려: <strong>${statusCounts.rejectedCount}건</strong></li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="col-lg-4 col-md-12">
            <div class="card h-100 total-count-card-image2 d-flex flex-column justify-content-center">
                <div class="card-body d-flex flex-column justify-content-center">
                    <h5 class="card-title mb-2 fw-bold">총 신청 건수</h5>
                    <p class="total-count-display-image2 mb-0">${statusCounts.totalCount}</p>
                </div>
            </div>
        </div>
    </div>

    <div class="search-filter-block-custom">
        <form id="searchForm" method="GET" action="/lms/staff/approvals" class="mb-3">
            <div class="input-group">
                <input type="hidden" name="searchType" value="all" />
                <input type="text" name="searchWord" class="form-control" placeholder="과목명, 교수명으로 검색..." value="${param.searchWord}">
                <button class="btn btn-primary" type="submit">검색</button>
            </div>
        </form>

        <div id="filter-btns" class="btn-group filter-btn-group w-100" role="group">
            <a href="/lms/staff/approvals?"
               class="btn btn-outline-secondary ${empty param.statusCd || param.statusCd eq 'all' ? 'active' : ''}">전체</a>
            <a href="/lms/staff/approvals?statusCd=PENDING"
               class="btn btn-outline-secondary ${param.statusCd eq 'PENDING' ? 'active' : ''}">대기</a>
            <a href="/lms/staff/approvals?statusCd=APPROVED"
               class="btn btn-outline-secondary ${param.statusCd eq 'APPROVED' ? 'active' : ''}">승인</a>
            <a href="/lms/staff/approvals?statusCd=REJECTED"
               class="btn btn-outline-secondary ${param.statusCd eq 'REJECTED' ? 'active' : ''}">반려</a>
        </div>
    </div>

    <div class="row g-4 d-flex align-items-stretch" id="request-list">
        <c:forEach var="approval" items="${approvalList}">
            <c:set var="statusText">
                <c:choose>
                    <c:when test="${approval.approveYnnull eq 'Y'}">승인됨</c:when>
                    <c:when test="${approval.approveYnnull eq 'N'}">반려됨</c:when>
                    <c:when test="${approval.applyTypeCd eq 'LCT_OPEN'}">강의실 배정 대기</c:when>
                    <c:otherwise>대기</c:otherwise>
                </c:choose>
            </c:set>

            <c:set var="badgeClass">
                <c:choose>
                    <c:when test="${approval.approveYnnull eq 'Y'}">status-badge-approved</c:when>
                    <c:when test="${approval.approveYnnull eq 'N'}">status-badge-rejected</c:when>
                    <c:otherwise>status-badge-pending</c:otherwise>
                </c:choose>
            </c:set>

            <c:set var="applicantProfName" value="${approval.applicantLastName}${approval.applicantFirstName}" />
            <c:set var="profLabel" value="${fn:substring(applicantProfName, 0, 1)}교수" />

            <c:set var="actionButtonText">
                <c:choose>
                    <c:when test="${approval.approveYnnull eq 'Y'}">상세 보기</c:when>
                    <c:when test="${approval.approveYnnull eq 'N'}">반려 사유 보기</c:when>
                    <c:otherwise>배정하기</c:otherwise>
                </c:choose>
            </c:set>

            <c:set var="actionButtonLink">
                <c:choose>
                    <c:when test="${approval.approveYnnull eq 'Y' || approval.approveYnnull eq 'N'}">/lms/staff/approvals/${approval.approveId}</c:when>
                    <c:otherwise>/lms/staff/approvals/${approval.approveId}</c:otherwise>
                </c:choose>
            </c:set>

            <c:set var="actionButtonClass">
                <c:choose>
                    <c:when test="${approval.approveYnnull eq 'Y'}">btn-view</c:when>
                    <c:when test="${approval.approveYnnull eq 'N'}">btn-rejected-view</c:when>
                    <c:otherwise>btn-assign</c:otherwise>
                </c:choose>
            </c:set>

            <div class="col-lg-6">
                <div class="request-card-a77461 d-flex flex-column justify-content-between">

                    <span class="status-badge-a77461 ${badgeClass}">${statusText}</span>

                    <span class="avatar-initials-a77461">${profLabel}</span>

                    <div class="card-content-body flex-grow-1">
                        <%-- 🌟 수정: 과목명 (subjectName) 표시 🌟 --%>
                        <h5 class="card-title-main text-truncate">
                            ${fn:escapeXml(not empty approval.subjectName ? approval.subjectName : approval.applyTypeName)}
                        </h5>

                        <%-- 🌟 수정: 교수명, 단과대학, 학과 정보 표시 🌟 --%>
                        <p class="card-subtitle-prof mb-0">
                            ${fn:escapeXml(applicantProfName)} 교수
                            <c:if test="${not empty approval.collegeName}">
                                · ${fn:escapeXml(approval.collegeName)}
                            </c:if>
                            <c:if test="${not empty approval.departmentName}">
                                (${fn:escapeXml(approval.departmentName)})
                            </c:if>
                            <%-- 주차별 강의 시간 정보 (optional) --%>
                            <c:if test="${not empty approval.weeklyPlans}">
                                | ${fn:escapeXml(approval.weeklyPlans)}
                            </c:if>
                        </p>
                    </div>

                    <div class="card-footer-action mt-3">
                        <a href="${actionButtonLink}"
                           class="btn btn-action-a77461 ${actionButtonClass}">
                            ${actionButtonText}
                            <i class='bx bx-right-arrow-alt ms-1'></i>
                        </a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
 <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // JSTL 변수의 값을 JavaScript 변수에 할당
        const pendingCount = parseInt('${statusCounts.pendingCount}' || 0);
        const approvedCount = parseInt('${statusCounts.approvedCount}' || 0);
        const rejectedCount = parseInt('${statusCounts.rejectedCount}' || 0);
        const totalCount = parseInt('${statusCounts.totalCount}' || 0);

        const data = [
            { label: "대기", count: pendingCount, color: "#ff9900" },
            { label: "승인", count: approvedCount, color: "#28a745" },
            { label: "반려", count: rejectedCount, color: "#dc3545" }
        ];

        const width = 200,
            height = 200,
            margin = 5;

        const radius = Math.min(width, height) / 2 - margin;

        const svg = d3.select("#status-donut-chart")
            .append("svg")
            .attr("width", width)
            .attr("height", height)
            .append("g")
            // 그래프를 SVG 컨테이너의 중앙에 배치
            .attr("transform", `translate(${width / 2}, ${height / 2})`); // 중앙으로 이동

        const color = d3.scaleOrdinal()
            .domain(data.map(d => d.label))
            .range(data.map(d => d.color));

        const pie = d3.pie()
            .sort(null)
            .value(d => d.count);

        const data_ready = pie(data);

        const arc = d3.arc()
            .innerRadius(radius * 0.6)
            .outerRadius(radius);

        // 전체 건수가 0이 아닌 경우에만 그래프를 그립니다.
        if (totalCount > 0) {
            svg
                .selectAll('slices')
                .data(data_ready.filter(d => d.data.count > 0)) // 0건인 항목 제외
                .enter()
                .append('path')
                .attr('d', arc)
                .attr('fill', d => color(d.data.label))
                .attr("stroke", "white")
                .style("stroke-width", "2px")
                .style("opacity", 0.9);
        } else {
            // 전체 건수가 0일 경우 회색 원을 그려 그래프 컨테이너 크기를 유지
            svg.append("circle")
                .attr("r", radius)
                .attr("fill", "#f0f0f0");
        }

        // 중앙 텍스트 (총 건수)
        svg.append("text")
            .attr("text-anchor", "middle")
            .attr("dy", "0.3em")
            .style("font-size", "1.5rem")
            .style("font-weight", "bold")
            .text(totalCount);
    });
</script>
</body>
</html>