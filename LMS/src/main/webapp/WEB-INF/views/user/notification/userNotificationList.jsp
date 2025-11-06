<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>알림 센터</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff/userNotificationList.css">
</head>

<body>
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div id="toast-container"></div>

    <!-- 외부 래퍼 -->
    <div class="notification-center-page">
        <div class="notification-center-container">

            <!-- 페이지 헤더 -->
            <div class="page-header">
                <div class="header-left">
                    <h1>알림 센터</h1>
                </div>
                <div class="header-right">
                    <!-- 알림벨 드롭다운 -->
                    <div class="notification-bell-wrapper">
                        <a href="#" class="bell-icon" id="notificationDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class='bx bx-bell'></i>
                            <span id="unread-count-badge" class="badge-count" style="display: none;">0</span>
                        </a>

                        <div id="notification-dropdown-menu" class="notification-dropdown" aria-labelledby="notificationDropdown">
                            <div class="dropdown-header">
                                <h6>알림 (<span id="dropdown-unread-count">0</span>)</h6>
                            </div>
                            <div id="dropdown-list-container" class="dropdown-list">
                                <div class="loading-message">로딩 중...</div>
                            </div>
                            <div class="dropdown-footer">
                                <a href='<c:url value="/lms/notifications"/>' class="view-all-link">
                                    모든 알림 보기 <i class='bx bx-chevron-right'></i>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- WebSocket 상태 표시 -->
            <!-- <div id="realtime-status" class="status-badge">
                <i class="bi bi-wifi"></i> WebSocket 연결 중...
            </div> -->

            <!-- 알림 목록 영역 -->
            <div class="notification-content">
                <div class="content-header">
                    <h5>전체 알림</h5>
                </div>
                <div id="notification-area" class="notification-list">
                    <div class="loading-state">
                        <i class="bi bi-hourglass-split"></i>
                        <p>알림 목록을 불러오는 중...</p>
                    </div>
                </div>
            </div>

        </div>
    </div>
</body>
</html>