<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>알림 상세</title>

    <style>
    /* 전체 페이지 */
    body {
        background-color: #f5f7fa;
        margin: 0;
        padding: 0;
    }

    /* 컨테이너 - 크기 줄임 */
    .notification-detail-container {
	    max-width: 700px !important;  /* 800px에서 이미 !important 붙였는데도 안 먹혔다면 */
	    width: 700px !important;      /* width도 추가 */
	    margin: 50px auto !important;
	    background: white;
	    border-radius: 12px;
	    box-shadow: 0 2px 12px rgba(91, 124, 235, 0.15);
	    overflow: hidden;
	}

    /* 헤더 */
    .notification-detail-container .detail-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 30px;  /* 패딩 줄임 */
        background: linear-gradient(135deg, #5b7ceb 0%, #7b68c4 100%);
        color: white;
    }

    .notification-detail-container .detail-header h1 {
        font-size: 20px;
        font-weight: 600;
        color: white;
        margin: 0;
    }

    /* 상태 배지 */
    .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 5px;
        padding: 6px 14px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
    }

    .status-badge.read {
        background: rgba(255, 255, 255, 0.25);
        color: white;
    }

    .status-badge.unread {
        background: rgba(255, 255, 255, 0.25);
        color: white;
    }

    /* 본문 - 패딩 줄임 */
    .detail-body {
        padding: 30px;  /* 40px → 30px */
    }

    /* 필드 그룹 - 한 줄로 */
    .field-group {
        display: flex;
        align-items: center;
        margin-bottom: 20px;  /* 30px → 20px */
        gap: 15px;
    }

    .field-group:last-child {
        margin-bottom: 0;
    }

    .field-group.vertical {
        flex-direction: column;
        align-items: flex-start;
    }

    /* 라벨 - 고정 너비 */
    .field-label {
        font-size: 17px;
        font-weight: 600;
        color: #333;
        min-width: 100px;
        flex-shrink: 0;
    }

    .field-label .required {
        color: #ff4757;
        margin-left: 3px;
    }

    /* 값 */
    .field-value {
        flex: 1;
	    padding: 10px 0;  /* 15px → 0 (좌우 패딩 제거) */
	    background: transparent;  /* #f8f9fa → transparent */
	    border: none;  /* 1px solid #e0e0e0 → none */
	    border-radius: 6px;
	    font-size: 17px;
	    color: #333;
	    line-height: 1.5;
    }

    .field-value.large {
        min-height: 120px;
	    white-space: pre-wrap;
	    word-wrap: break-word;
	    padding: 10px 15px;  /* 큰 텍스트는 패딩 유지 */
	    background: transparent;  /* #f8f9fa → transparent */
	    border: 1px solid #e0e0e0;
    }

    .field-value.empty {
        color: #999;
        font-style: italic;
    }

    /* 구분선 */
    .divider {
        height: 1px;
        background: linear-gradient(90deg, transparent, #e0e0e0, transparent);
        margin: 25px 0;  /* 35px → 25px */
    }

    /* 푸터 */
    .detail-footer {
        padding: 15px 30px;  /* 20px 40px → 15px 30px */
        background: #f8f9fa;
        border-top: 1px solid #e0e0e0;
        display: flex;
        justify-content: flex-end;
    }

    /* 버튼 */
    .btn {
        padding: 10px 20px;
        border: none;
        border-radius: 8px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s;
    }

    .btn-back {
        background-color: #6c757d;
        color: white;
    }

    .btn-back:hover {
        background-color: #5a6268;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
    }

    /* 반응형 */
    @media (max-width: 768px) {
        body {
            padding: 10px;
        }

        .notification-detail-container {
            border-radius: 8px;
        }

        .detail-header {
            flex-direction: column;
            align-items: flex-start;
            gap: 15px;
            padding: 15px 20px;
        }

        .detail-body {
            padding: 20px;
        }

        .field-group {
            flex-direction: column;
            align-items: flex-start;
            gap: 8px;
        }

        .field-label {
            min-width: auto;
        }

        .detail-footer {
            padding: 15px 20px;
        }
    }
</style>
</head>
<body>
<c:set var="noti" value="${notification}" />

    <div class="notification-detail-container">

        <!-- 헤더 -->
        <div class="detail-header">
            <h1>알림 상세</h1>
            <div>
                <c:choose>
                    <c:when test="${not empty noti.checkAt}">
                        <span class="status-badge read">읽음</span>
                    </c:when>
                    <c:otherwise>
                        <span class="status-badge unread">안 읽음</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- 본문 -->
		<div class="detail-body">

		    <!-- 발신자 -->
		    <div class="field-group">
		        <label class="field-label">발신자 </label>
		        <div class="field-value">
		            <c:choose>
		                <c:when test="${noti.stfDeptName eq '시스템'}">
		                    🤖 LMS 시스템
		                </c:when>
		                <c:otherwise>
		                    <c:out value="${noti.senderLastName}${noti.senderFirstName}" default="정보 없음"/>
		                </c:otherwise>
		            </c:choose>
		        </div>
		    </div>

		    <!-- 소속 부서 -->
		    <div class="field-group">
		        <label class="field-label">소속 부서 </label>
		        <div class="field-value">
		            <c:out value="${noti.stfDeptName}" default="정보 없음"/>
		        </div>
		    </div>

		    <!-- 발송 시각 -->
		    <div class="field-group">
		        <label class="field-label">발송 시각 </label>
		        <div class="field-value">${fn:replace(noti.createAt, 'T', ' ')}</div>
		    </div>

		    <!-- 확인 시각 -->
			<div class="field-group">
			    <label class="field-label">확인 시각</label>
			    <div class="field-value ${empty noti.checkAt ? 'empty' : ''}">
			        <c:choose>
			            <c:when test="${not empty noti.checkAt}">
			                ${fn:replace(noti.checkAt, 'T', ' ')}
			            </c:when>
			            <c:otherwise>
			                아직 확인하지 않았습니다.
			            </c:otherwise>
			        </c:choose>
			    </div>
			</div>

		    <div class="divider"></div>

		    <!-- 알림 내용 - 세로 배치 -->
		    <div class="field-group vertical">
		        <label class="field-label">메시지</label>
		        <div class="field-value large" style="width: 100%;">${noti.pushDetail}</div>
		    </div>

		</div>

        <!-- 푸터 -->
        <div class="detail-footer">
            <button onclick="location.href='<c:url value="/lms/notifications"/>'" class="btn btn-back">
                목록으로 돌아가기
            </button>
        </div>

    </div>


     <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
</body>
</html>
