<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
 	<meta charset="UTF-8">
    <title>알림 발신 내역</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff/userNotificationHistoryList.css">
</head>
<body>
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div class="notification-history-page">
        <div class="notification-container">

            <div class="page-header">
                <h1><i class="bi bi-bell-fill"></i> 알림 발신 내역</h1>
                <c:url var="createUrl" value="/lms/notifications/create" />
                <a href="${createUrl}" class="btn btn-success">
                    <i class="bi bi-person-plus"></i> 알림 발신
                </a>
            </div>

            <form id="historyForm" action="<c:url value='/lms/notifications/history' />" method="get">
                <input type="hidden" name="senderId" value="${senderId}">
                <input type="hidden" name="currentPage" id="currentPage" value="${pagingInfo.currentPage}">
            </form>

            <div class="history-card">
                <div class="card-header">
                    <h5>${senderName} 님의 전체 발송 기록 (총 ${pagingInfo.totalRecord}건)</h5>
                </div>

                <div class="table-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th style="width: 5%;">No.</th>
                                <th style="width: 15%;">발신자 이름</th>
                                <th style="width: 15%;">수신자/대상 그룹</th>
                                <th style="width: 30%;">알림 내용</th>
                                <th style="width: 10%;">대상 유형</th>
                                <th style="width: 15%;">전송 일시</th>
                                <th style="width: 10%;">상태</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${historyList}" varStatus="status">
                                <tr class="clickable-row" onclick="openDetailModal(${status.index})">
                                    <td>
                                        <c:set var="recordsSkipped" value="${(pagingInfo.currentPage - 1) * pagingInfo.screenSize}" />
                                        <c:set var="startNo" value="${pagingInfo.totalRecord - recordsSkipped}" />
                                        ${startNo - status.index}
                                    </td>
                                    <td>${item.senderLastName}${item.senderFirstName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.recipientInfo == '그룹 대상자'}">
                                                <span class="badge badge-secondary">그룹 대상자</span>
                                            </c:when>
                                            <c:otherwise>
                                                ${item.recipientInfo}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-start notification-preview">
                                        ${fn:substring(item.pushDetail, 0, 50)}...
                                        <i class="bi bi-eye"></i>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.recipientInfo == '그룹 대상자'}">
                                                <span class="badge badge-warning">그룹 발송</span>
                                            </c:when>
                                            <c:otherwise>
                                                개별 발송
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${fn:substring(item.createAt, 0, 10)}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.isRead == 'Y'}">
                                                <span class="badge badge-success">읽음</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-danger">안 읽음</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty historyList}">
                                <tr>
                                    <td colspan="7" class="text-center">발신 내역이 없습니다.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <div class="pagination-area">
                    <c:set var="baseStyle" value="
                        padding: 4px 10px;
                        margin: 0 4px;
                        border-radius: 4px;
                        font-weight: 500;
                        min-width: 30px;
                        text-align: center;
                        font-size: 0.9rem;
                        text-decoration: none;
                        display: inline-block;
                        cursor: pointer;
                        transition: all 0.2s;
                        color: #212529;
                        background-color: #ffffff;
                        border: 1px solid #dee2e6;
                    " />

                    <c:if test="${pagingInfo.startPage > 1}">
                        <a href="javascript:void(0);" onclick="pageing(${pagingInfo.startPage - 1});" style="${baseStyle}">&#9664;</a>
                    </c:if>

                    <c:forEach begin="${pagingInfo.startPage}" end="${pagingInfo.endPage}" var="p">
                        <c:choose>
                            <c:when test="${pagingInfo.currentPage eq p}">
                                <a href="javascript:void(0);" onclick="pageing(${p});" class="active" style="${baseStyle} color: #ffffff; background-color: #007bff; border-color: #007bff;">${p}</a>
                            </c:when>
                            <c:otherwise>
                                <a href="javascript:void(0);" onclick="pageing(${p});" style="${baseStyle}">${p}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${pagingInfo.endPage < pagingInfo.totalPage}">
                        <a href="javascript:void(0);" onclick="pageing(${pagingInfo.endPage + 1});" style="${baseStyle}">&#9654;</a>
                    </c:if>
                </div>
            </div>

        </div>
    </div>

    <!-- 알림 상세 모달 -->
	<div id="notificationDetailModal" class="notification-modal">
	    <div class="modal-content">
	        <div class="modal-header">
	            <div class="modal-title">
	                <h3 id="modal-title-text">알림 상세 내역</h3>
	            </div>
	            <div class="modal-date" id="modal-send-date"></div>
	            <button class="modal-close" onclick="closeDetailModal()">&times;</button>
	        </div>

	        <div class="modal-body">
	            <!-- 발신자 정보 -->
	            <div class="info-section">
	                <div class="info-grid">
	                    <div class="info-item">
	                        <span class="info-label">보낸 사람</span>
	                        <span class="info-value" id="modal-sender-name"></span>
	                    </div>
	                </div>
	            </div>

	            <!-- 수신자 정보 -->
	            <div class="info-section">
	                <div class="info-grid">
	                    <div class="info-item">
	                        <span class="info-label">받는 사람</span>
	                        <span class="info-value" id="modal-recipient"></span>
	                    </div>
	                </div>
	            </div>

	            <div class="divider"></div>

	            <!-- 알림 내용 -->
	            <div class="info-section">
	                <div class="notification-content">
	                    <p id="modal-content"></p>
	                </div>
	            </div>

	            <div class="divider"></div>

	            <!-- 추가 정보 -->
	            <div class="info-section">
	                <div class="info-grid">
	                    <div class="info-item">
	                        <span class="info-label">상태</span>
	                        <span class="info-value" id="modal-read-status"></span>
	                    </div>
	                    <div class="info-item">
	                        <span class="info-label">발송 유형</span>
	                        <span class="info-value" id="modal-send-type"></span>
	                    </div>
	                </div>
	            </div>
	        </div>

	        <div class="modal-footer">
	            <button class="btn btn-secondary" onclick="closeDetailModal()">닫기</button>
	        </div>
	    </div>
	</div>

    <!-- 데이터를 JavaScript로 전달 -->
    <script>
        const notificationData = [
            <c:forEach var="item" items="${historyList}" varStatus="status">
            {
                senderName: "${item.senderLastName}${item.senderFirstName}",
                recipientInfo: "${item.recipientInfo}",
                pushDetail: "${fn:escapeXml(item.pushDetail)}",
                createAt: "${item.createAt}",
                isRead: "${item.isRead}",
                sendType: "${item.recipientInfo == '그룹 대상자' ? '그룹 발송' : '개별 발송'}"
            }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        function openDetailModal(index) {
            const data = notificationData[index];

         	// 날짜 포맷 변경 => T를 공백으로 바꾸기
            const formattedDate = data.createAt.replace('T', ' ');

            document.getElementById('modal-sender-name').textContent = data.senderName;
            document.getElementById('modal-send-date').textContent = formattedDate;
            document.getElementById('modal-recipient').textContent = data.recipientInfo;
            document.getElementById('modal-send-type').innerHTML =
                data.sendType === '그룹 발송'
                ? '<span class="badge-warning">그룹 발송</span>'
                : '<span class="badge-info">개별 발송</span>';
            document.getElementById('modal-read-status').innerHTML =
                data.isRead === 'Y'
                ? '<span class="badge-success">읽음</span>'
                : '<span class="badge-danger">안 읽음</span>';
            document.getElementById('modal-content').textContent = data.pushDetail;

            document.getElementById('notificationDetailModal').style.display = 'flex';
        }

        function closeDetailModal() {
            document.getElementById('notificationDetailModal').style.display = 'none';
        }

        // 모달 외부 클릭 시 닫기
        window.onclick = function(event) {
            const modal = document.getElementById('notificationDetailModal');
            if (event.target === modal) {
                closeDetailModal();
            }
        }

        function pageing(page) {
            document.getElementById('currentPage').value = page;
            document.getElementById('historyForm').submit();
        }
    </script>

    <script src="<c:url value='/js/app/staff/native.js' />"></script>
</body>
</html>