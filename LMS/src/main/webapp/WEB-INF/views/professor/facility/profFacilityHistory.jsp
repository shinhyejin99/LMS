<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>시설 이용 내역</title>
<style>
body {
    font-family: Arial, sans-serif;
    background: #f5f5f9;
    color: #333;
    padding: 20px;
}
.container {
    max-width: 900px;
    margin: 0 auto;
}

.history-table {
    display: grid;
    border: 1px solid #ddd;
    background: #fff;
    box-shadow: 0 0 10px rgba(0,0,0,0.1);
    margin-top: 20px; /* Add some space from the title */
}

.history-header, .history-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr); /* 4 columns, equal width */
    border-bottom: 1px solid #eee;
}

.history-header {
    background: #f0f4f8;
    font-weight: 700;
}

.history-cell {
    padding: 12px 20px;
    text-align: center;
}

.history-cell:last-child {
    border-right: none; /* No border on the last cell */
}

.history-row:nth-child(even) {
    background: #f9f9f9;
}

.history-row:hover {
    background: #e9f0ff;
}

.history-row.no-data .history-cell {
    padding: 12px 20px;
    text-align: center;
    color: #6c757d;
    font-weight: normal;
}.status {
    padding: 5px 10px;
    border-radius: 12px;
    color: white;
    font-weight: 700;
    display: inline-block;
}
.status.completed {
    background: #28a745;
}
.status.canceled {
    background: #dc3545;
}
.status.reserved {
    background: #ffc107;
    color: #333;
}
</style>
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<div class="container">
    <h2>시설 이용 내역</h2>
    <div class="history-table">
    <div class="history-header">
        <div class="history-cell">신청일자</div>
        <div class="history-cell">시설명</div>
        <div class="history-cell">이용일시</div>
        <div class="history-cell">처리상태</div>
    </div>
    <c:choose>
        <c:when test="${not empty historyList}">
            <c:forEach var="history" items="${historyList}">
                <div class="history-row">
                    <div class="history-cell"><fmt:formatDate value="${history.CREATE_AT}" pattern="yyyy-MM-dd HH:mm"/></div>
                    <div class="history-cell">${history.PLACE_NAME != null ? history.PLACE_NAME : history.PLACE_ID}</div>
                    <div class="history-cell">
                        <fmt:formatDate value="${history.START_AT}" pattern="yyyy-MM-dd HH:mm"/> ~ 
                        <fmt:formatDate value="${history.END_AT}" pattern="yyyy-MM-dd HH:mm"/>
                    </div>
                    <div class="history-cell">
                        <c:choose>
                            <c:when test="${history.CANCEL_YN == 'Y'}">
                                <span class="status canceled">취소됨</span>
                            </c:when>
                            <c:when test="${history.END_AT.time < now.time}">
                                <span class="status completed">사용완료</span>
                            </c:when>
                            <c:otherwise>
                                <span class="status reserved">예약됨</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div class="history-row no-data">
                <div class="history-cell" style="grid-column: 1 / -1;">시설 이용 내역이 없습니다.</div>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</div>
</body>
</html>
