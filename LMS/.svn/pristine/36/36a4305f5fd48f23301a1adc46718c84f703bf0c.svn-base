<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>교수 승인문서 상세</title>
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<h1>교수 승인문서 상세</h1>

<c:if test="${not empty approvalDetail}">
    <p><strong>결재 ID:</strong> ${approvalDetail.APPROVE_ID}</p>
    <p><strong>문서 종류:</strong> ${approvalDetail.documentType}</p>
    <p><strong>신청자:</strong> ${approvalDetail.APPLICANTNAME}</p>
    
    <!-- 학생 정보 -->
    <h2>학생 정보</h2>
    <p><strong>이름:</strong> ${approvalDetail.APPLICANTNAME}</p>
    <p><strong>학번:</strong> ${approvalDetail.APPLICANTSTUDENTNO}</p>
    <p><strong>학과:</strong> ${approvalDetail.APPLICANTDEPTNAME}</p>
    <p><strong>학년:</strong> ${approvalDetail.APPLICANTGRADENAME}</p>
    <p><strong>전화번호:</strong> ${approvalDetail.APPLICANTMOBILENO}</p>
    <p><strong>이메일:</strong> ${approvalDetail.APPLICANTEMAIL}</p>
    <p><strong>주소:</strong> ${approvalDetail.APPLICANTBASEADDR} ${approvalDetail.APPLICANTDETAILADDR}</p>
    <p><strong>신청 사유:</strong> ${approvalDetail.APPLY_REASON}</p>
    <p><strong>결재 의견:</strong> ${approvalDetail.COMMENTS}</p>
    <p><strong>결재 여부:</strong> 
        <c:choose>
            <c:when test="${approvalDetail.APPROVE_YNNULL eq 'Y'}">
                승인
            </c:when>
            <c:when test="${approvalDetail.APPROVE_YN eq 'N'}">
                반려
            </c:when>
            <c:otherwise>
                대기중
            </c:otherwise>
        </c:choose>
    </p>
    <p><strong>결재 일시:</strong> ${approvalDetail.APPROVE_AT}</p>
    
    <!-- 학적변동 신청 상세 -->
    <c:if test="${not empty approvalDetail.RECORD_CHANGE_CD}">
        <h2>학적변동 신청 정보</h2>
        <p><strong>변동 코드:</strong> ${approvalDetail.RECORD_CHANGE_CD}</p>
        <p><strong>희망 학기:</strong> ${approvalDetail.DISIRE_TERM}</p>
        <p><strong>신청 일시:</strong> ${approvalDetail.URA_APPLY_AT}</p>
        <p><strong>신청 상태:</strong> ${approvalDetail.APPLY_STATUS_CD}</p>
    </c:if>
    
    <!-- 소속변동 신청 상세 -->
    <c:if test="${not empty approvalDetail.UAA_AFFIL_CHANGE_CD}">
        <h2>소속변동 신청 정보</h2>
        <p><strong>대상 학과 코드:</strong> ${approvalDetail.TARGET_DEPT_CD}</p>
        <p><strong>변동 코드:</strong> ${approvalDetail.UAA_AFFIL_CHANGE_CD}</p>
        <p><strong>신청 일시:</strong> ${approvalDetail.UAA_APPLY_AT}</p>
        <p><strong>신청 사유:</strong> ${approvalDetail.UAA_APPLY_REASON}</p>
        <p><strong>신청 상태:</strong> ${approvalDetail.UAA_APPLY_STATUS_CD}</p>
    </c:if>
    
</c:if>
<c:if test="${empty approvalDetail}">
    <p>결재 문서를 찾을 수 없습니다.</p>
</c:if>

</body>
</html>