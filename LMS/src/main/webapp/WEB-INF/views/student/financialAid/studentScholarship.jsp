<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>장학금 조회</title>
</head>
<body>

	<div class="container-xxl flex-grow-1 container-p-y">
    <h1 class="page-title">장학금 조회</h1>

    <!-- 총 수혜 금액 표시 -->
	<div class="card total-amount-section">
	    <div class="card-body total-amount-card">
	        <div class="total-label">총 장학금 수혜 금액</div>
	        <div class="total-value">
	            <fmt:formatNumber value="${totalScholarship}" type="number"/>원
	        </div>
	    </div>
	</div>

    <!-- 도넛 차트 섹션 -->
    <div class="chart-section">
        <h2 class="section-title">장학금 종류별 분포</h2>
        <div id="donutChart"></div>
        <div id="chartLegend"></div>
    </div>

    <!-- 장학금 수혜 내역 테이블 -->
    <div class="table-section">
        <h2 class="section-title">장학금 수혜 내역</h2>
        <div class="table-wrapper">
            <table class="scholarship-table">
                <thead>
                    <tr>
                        <th>학기</th>
                        <th>장학금 종류</th>
                        <th>금액</th>
                        <th>상태</th>
                    </tr>
                </thead>
                <tbody id="scholarshipTableBody">
                    <c:choose>
                        <c:when test="${not empty scholarshipHistory}">
                            <c:forEach var="scholarship" items="${scholarshipHistory}">
                                <tr>
                                    <td>${scholarship.semesterName}</td>
                                    <td>${scholarship.scholarshipName}</td>
                                    <td><fmt:formatNumber value="${scholarship.amount}" type="number"/>원</td>
                                    <td>
                                        <span class="status-badge ${scholarship.status == '지급완료' ? 'status-completed' : 'status-pending'}">
                                            ${scholarship.status}
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="4" class="no-data">장학금 수혜 내역이 없습니다.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
	        </div>
	    </div>
	</div>

	<!-- 데이터를 JavaScript로 전달 -->
	<script>
	// 차트용 데이터 (종류별 분포)
	const scholarshipDistribution = [
	    <c:forEach var="dist" items="${scholarshipDistribution}" varStatus="status">
	        {
	            scholarshipName: "${dist.scholarshipName}",
	            count: ${dist.count},
	            totalAmount: ${dist.totalAmount}
	        }<c:if test="${!status.last}">,</c:if>
	    </c:forEach>
	];
	</script>
		<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
</body>
</html>