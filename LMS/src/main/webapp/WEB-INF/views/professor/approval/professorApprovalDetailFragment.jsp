<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${not empty approvalDetail}">
    <p><strong>결재 ID:</strong> ${approvalDetail.APPROVE_ID}</p>
    <p><strong>문서 종류:</strong> ${approvalDetail.documentType}</p>
    <p><strong>신청자:</strong> ${approvalDetail.FIRST_NAME}${approvalDetail.LAST_NAME}</p>
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