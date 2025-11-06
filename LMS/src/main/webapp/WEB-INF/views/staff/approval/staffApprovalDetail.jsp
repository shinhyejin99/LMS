<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>결재 문서 상세 (${approval.approveId})</title>
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
<style>
    /* 공통 스타일 */
    body { background-color: #f8f9fa; }
    .card { border: none; border-radius: 10px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); margin-bottom: 30px; }
    .card-header { border-bottom: 1px solid #e9ecef; }

    /* 결재 상태 배지 스타일 */
    .status-badge {
        padding: 0.5em 1em;
        font-size: 90%;
        font-weight: 700;
        border-radius: 0.5rem;
        display: inline-block;
        min-width: 70px;
        text-align: center;
        line-height: 1;
    }
    /* 상태별 색상 정의 */
    .status-badge.bg-green-100 { color: #0a3622; background-color: #d1e7dd; }
    .status-badge.bg-red-100 { color: #58151c; background-color: #f8d7da; }
    .status-badge.bg-yellow-100 { color: #664d03; background-color: #fff3cd; }

    /* 문서 정보 레이아웃 */
    .document-info-section { padding-right: 15px; }
    .document-info-row { display: flex; justify-content: space-between; align-items: flex-start; padding-top: 0; margin-bottom: 15px; }
    .document-meta-label { width: 100px; display: inline-block; font-weight: 500; color: #495057; text-align: left; }
    .document-meta-info { font-weight: bold; }
    .document-applicant-info { margin-top: 20px; font-size: 0.95rem; text-align: left; }
    .document-applicant-info p { margin-bottom: 5px; }

    /* LCT_OPEN 상세 정보 스타일 */
    .lecture-detail-box {
        margin-top: 30px;
        border: 1px solid #ced4da;
        border-radius: 5px;
        padding: 20px;
        background-color: #ffffff;
    }
    .lecture-detail-item {
        margin-bottom: 10px;
    }
    .lecture-detail-label {
        font-weight: 600;
        color: #343a40;
        min-width: 120px;
        display: inline-block;
    }
    .lecture-content-box {
        border: 1px solid #ced4da;
        padding: 15px;
        min-height: 100px;
        margin-top: 10px;
        white-space: pre-wrap;
        background-color: #f8f9fa;
        font-size: 0.95rem;
    }

    /* -------------------------------------------------------------------- */
    /* !!! 결재선 영역 스타일 (테두리 최종 강조) !!! */
    /* -------------------------------------------------------------------- */

    /* 개별 결재 박스 스타일 */
    .approval-line-box {
        flex: 1;
        /* 테두리 최종 강조 */
        border: 2px solid black !important;
        border-radius: 0.25rem;
        background-color: #fff;
        text-align: center;
        padding: 1.5rem 1rem;
        min-height: 150px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        position: relative;
    }

    /* 서명 영역 스타일 (height: 60px) */
    .signature-area {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-top: 0.5rem;
        margin-bottom: 1rem;
        color: #6c757d;
    }
    /* 승인/반려 도장 스타일 */
    .signature-area .signed {
        font-size: 1.1rem;
        font-weight: 900;
        line-height: 1.2;
    }

    /* 강의 신청 PDF 버튼 영역 (이제는 배정 링크 영역) */
    .pdf-section {
        padding: 30px;
        text-align: center;
        border: 2px dashed #007bff;
        border-radius: 8px;
        background-color: #e6f7ff;
        margin-top: 30px;
    }
</style>
</head>
<body class="bg-light">
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div id="main-content-wrapper">
        <c:set var="approval" value="${approval}" />
        <c:set var="isFinalized" value="${not empty approval.approveYnnull}" />

        <div class="container-fluid py-4" style="max-width: 1200px;">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="mb-0">결재 문서 상세</h1>
                <button type="button" class="btn btn-outline-secondary" onclick="location.href='<c:url value="/lms/staff/approvals"/>'">목록으로</button>
            </div>

            <div id="approval-document-view" class="card">
                <div class="card-header bg-white">
                    <%-- 강의 개설 신청 문서인 경우 제목 표시 --%>
                    <c:choose>
                        <c:when test="${approval.applyTypeCd == 'LCT_OPEN'}">
                            <h5 class="card-title mb-0">강의 개설 신청: ${approval.subjectName}</h5>
                        </c:when>
                        <c:otherwise>
                            <h5 class="card-title mb-0">${approval.applyTypeName} 문서 ${approval.approveId}</h5>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="card-body">
                    <div class="row">

                        <%-- 문서 기본 정보 및 내용 (col-md-8) - 왼쪽 --%>
                        <div class="col-md-8 document-info-section">
                            <div class="document-info-row">
                                <div>
                                    <p class="mb-1"><span class="document-meta-label">신청서:</span> <span class="document-meta-info">${approval.applyTypeName}</span></p>
                                </div>
                            </div>

                            <div class="document-applicant-info">
                                <p><span class="document-meta-label">신청자:</span> ${approval.applicantLastName}${approval.applicantFirstName}</p>
                                <p><span class="document-meta-label">신청일:</span> ${fn:substring(approval.approveAt, 0, 10)}</p>
                                <p><span class="document-meta-label">결재 상태:</span>
                                    <span class="status-badge
                                        <c:choose>
                                            <c:when test="${approval.approveYnnull eq 'Y'}">bg-green-100">승인</c:when>
                                            <c:when test="${approval.approveYnnull eq 'N'}">bg-red-100">반려</c:when>
                                            <c:otherwise>bg-yellow-100">대기</c:otherwise>
                                        </c:choose>
                                    </span>
                                </p>
                            </div>

                            <%-- 반려 사유 별도 표시 (반려 시) --%>
                            <c:if test="${approval.approveYnnull eq 'N' and not empty approval.rejectionReason}">
                                <div class="mt-3">
                                    <p class="font-weight-bold text-danger mb-1">반려 사유:</p>
                                    <div class="alert alert-danger">${approval.rejectionReason}</div>
                                </div>
                            </c:if>

                            <%-- 💡 강의 개설 신청 (LCT_OPEN) 상세 내용 표시 --%>
                            <c:if test="${approval.applyTypeCd == 'LCT_OPEN'}">
                                <h5 class="mt-4 mb-3">강의 개설 상세 정보</h5>
                                <div class="lecture-detail-box">
                                    <div class="row">
                                        <div class="col-md-6 lecture-detail-item">
                                            <span class="lecture-detail-label">과목명:</span> ${approval.subjectName}
                                        </div>
                                        <div class="col-md-6 lecture-detail-item">
                                            <span class="lecture-detail-label">교수명:</span> ${approval.professorName}
                                        </div>
                                        <div class="col-md-6 lecture-detail-item">
                                            <span class="lecture-detail-label">학점/이수구분:</span> ${approval.credit}학점 / ${approval.completionCdName}
                                        </div>
                                        <div class="col-md-6 lecture-detail-item">
                                            <span class="lecture-detail-label">희망정원:</span> ${approval.expectCap}명
                                        </div>
                                    </div>
                                    <div class="lecture-detail-item mt-3">
                                        <p class="font-weight-bold mb-1">강의 목표:</p>
                                        <div class="lecture-content-box">${approval.lectureGoal || '제출된 강의 목표가 없습니다.'}</div>
                                    </div>
                                    <div class="lecture-detail-item">
                                        <p class="font-weight-bold mb-1">선수 과목:</p>
                                        <div class="lecture-content-box">${approval.prereqSubject || '선수 과목 정보가 없습니다.'}</div>
                                    </div>
                                </div>
                            </c:if>
                        </div>

                        <%-- 결재선 영역 (col-md-4) - 오른쪽 --%>
                        <div class="col-md-4" id="approval-line-wrapper">
                            <div class="approval-line-header">결재선</div>

                            <div class="d-flex">

                                <%-- 💡 결재선 1 (학과장 - 이전 단계) --%>
                                <div class="approval-line-box flex-fill mr-3">
                                    <h5>${approval.prevLastName}${approval.prevFirstName} 학과장</h5>

                                    <div class="signature-area">
                                        <c:choose>
                                            <c:when test="${approval.approveYnnull eq 'Y'}">
                                                <span class="signed text-success">승인</span>
                                            </c:when>
                                            <c:when test="${approval.approveYnnull eq 'N'}">
                                                <span class="signed text-danger">반려</span>
                                            </c:when>
                                            <%-- 최종 상태가 '대기'인 경우, 이전 단계는 이미 통과했거나 동시에 처리되지 않으므로 '결재 완료'로 가정합니다. --%>
                                            <c:otherwise>
                                                <span class="signed text-primary">결재 완료</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <p class="text-muted small">
                                        <c:if test="${approval.approveYnnull eq 'Y' or approval.approveYnnull eq 'N'}">
                                            <i class="far fa-clock"></i> 
                                            <fmt:parseDate value="${approval.approveAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedApproveAt" type="both" />
                                            <fmt:formatDate value="${parsedApproveAt}" pattern="MM/dd HH:mm"/>
                                        </c:if>
                                        <c:if test="${not isFinalized}">
                                            <span class="text-primary font-weight-bold">다음 단계 이관</span>
                                        </c:if>
                                    </p>
                                </div>

                                <%-- 💡 결재선 2 (교직원 - 최종 단계) --%>
                                <div class="approval-line-box flex-fill mr-0">
                                    <h5>${approval.currentLastName}${approval.currentFirstName} 교직원</h5>

                                    <div class="signature-area">
                                        <c:choose>
                                            <c:when test="${approval.approveYnnull eq 'Y'}">
                                                <span class="signed text-success">승인</span>
                                            </c:when>
                                            <c:when test="${approval.approveYnnull eq 'N'}">
                                                <span class="signed text-danger">반려</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">서명 대기</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <p class="text-muted small">
                                        <c:if test="${isFinalized}">
                                            <i class="far fa-clock"></i> 
                                            <fmt:parseDate value="${approval.approveAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedApproveAt" type="both" />
                                            <fmt:formatDate value="${parsedApproveAt}" pattern="MM/dd HH:mm"/>
                                        </c:if>
                                        <c:if test="${not isFinalized}">
                                            <span class="text-warning font-weight-bold">현재 결재 단계</span>
                                        </c:if>
                                    </p>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

                <%-- 하단 승인/반려 버튼 --%>
                <div class="card-footer bg-white">
                    <div class="d-flex justify-content-end">
                        <c:if test="${empty approval.approveYnnull}">
                            <button class="btn btn-success mr-2 font-weight-bold" onclick="approveDocument('${approval.approveId}')">✅ 승인</button>
                            <button class="btn btn-danger font-weight-bold" onclick="rejectDocument('${approval.approveId}')">❌ 반려</button>
                        </c:if>
                        <c:if test="${approval.approveYnnull eq 'Y'}">
                            <p class="text-success text-right font-weight-bold my-2">🎉 최종 승인 완료</p>
                        </c:if>
                        <c:if test="${approval.approveYnnull eq 'N'}">
                            <p class="text-danger text-right font-weight-bold my-2">⚠️ 문서 반려됨</p>
                        </c:if>
                    </div>
                </div>
            </div>

            <%-- 💡 강의실 배정 페이지 링크 (LCT_OPEN 타입일 경우 추가) --%>
           <c:if test="${approval.applyTypeCd == 'LCT_OPEN' and not empty approval.lctApplyId}">
			<div id="lct-assignment-link" class="card border-info">
			        <a href="<c:url value="/lms/staff/classroom/assignment/form">
				        <c:param name="lctApplyId" value="${approval.lctApplyId}"/>
				        <c:param name="approvalId" value="${approval.approveId}"/>
				    </c:url>"
				    class="btn btn-primary btn-lg mt-3">
			            <i class="fas fa-chalkboard-teacher"></i> 강의실 배정 페이지로 이동
			        </a>
			    </div>
			</c:if>
			<c:if test="${approval.applyTypeCd == 'LCT_OPEN' and empty approval.lctApplyId}">
			    <div class="alert alert-warning mt-3">
			        ⚠️ 강의 신청 ID(LCT_APPLY_ID)가 누락되어 배정 페이지로 이동할 수 없습니다. DB 확인이 필요합니다.
    </div>
</c:if>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script>
        function approveDocument(approvalId) {
            if (confirm(approvalId + "번 문서를 승인하시겠습니까?")) {
                alert("승인 요청을 서버로 전송합니다. (ID: " + approvalId + ")");
                // TODO: 실제 승인 처리 로직: location.href = '/lms/staff/approvals/process/' + approvalId + '?action=Y';
            }
        }

        function rejectDocument(approvalId) {
            const reason = prompt(approvalId + "번 문서의 반려 사유를 입력하세요:");
            if (reason) {
                alert("반려 요청을 서버로 전송합니다. 사유: " + reason + " (ID: " + approvalId + ")");
                // TODO: 실제 반려 처리 로직: location.href = '/lms/staff/approvals/process/' + approvalId + '?action=N&comments=' + encodeURIComponent(reason);
            } else if (reason !== null && reason.trim() === "") {
                alert("반려 사유를 입력해야 합니다.");
            }
        }

        document.addEventListener('DOMContentLoaded', () => {
             const approveId = '${approval.approveId}';
             if (approveId) {
                 document.title = '결재 상세: ' + approveId;
             }
        });
    </script>
</body>
</html>