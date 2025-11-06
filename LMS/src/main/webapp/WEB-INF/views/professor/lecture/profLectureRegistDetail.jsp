<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>강의 개설 신청 상세</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: sans-serif;
            background: #f5f5f9;
            color: #566a7f;
        }
        .container {
            margin-top: 30px;
            margin-bottom: 30px;
        }
        .card-header {
            background-color: #f8f9fa;
            border-bottom: 1px solid #e9ecef;
        }
        .detail-row {
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        .detail-row:last-child {
            border-bottom: none;
        }
        .detail-label {
            font-weight: bold;
            color: #343a40;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<div class="container">
    <div class="py-4">
        <div class="row">
            <div class="col-12">
                <div class="card border-0 shadow">
                    <div class="card-header">
                        <h2 class="fs-5 fw-bold mb-0">강의 개설 신청 상세</h2>
                    </div>
                    <div class="card-body">
                        <c:if test="${empty detail}">
                            <div class="alert alert-warning" role="alert">
                                해당 강의 신청 정보를 찾을 수 없습니다.
                            </div>
                        </c:if>
                        <c:if test="${not empty detail}">

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4 detail-label">이수구분</div>
                                        <div class="col-md-8">${detail.COMPLETION_CD_NAME}</div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4 detail-label">신청교수</div>
                                        <div class="col-md-8">${detail.PROFESSOR_NAME}</div>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4 detail-label">학년도/학기</div>
                                        <div class="col-md-8">
                                            <c:set var="yearterm" value="${detail.YEARTERM_CD}" />
                                            <c:set var="year" value="${fn:substring(yearterm, 0, 4)}" />
                                            <c:set var="term_code" value="${fn:substring(yearterm, 5, 8)}" />
                                            <c:set var="term_num" value="${fn:substring(yearterm, 8, 9)}" />

                                            <c:choose>
                                                <c:when test="${term_code == 'REG'}">
                                                    <c:set var="term_display" value="${term_num}학기" />
                                                </c:when>
                                                <c:when test="${term_code == 'SUB' and term_num == '1'}">
                                                    <c:set var="term_display" value="여름계절학기" />
                                                </c:when>
                                                <c:when test="${term_code == 'SUB' and term_num == '2'}">
                                                    <c:set var="term_display" value="겨울계절학기" />
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="term_display" value="${fn:substring(yearterm, 5, -1)}" />
                                                </c:otherwise>
                                            </c:choose>
                                            ${year}/${term_display}
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4 detail-label">신청일</div>
                                        <div class="col-md-8"><fmt:formatDate value="${detail.APPLY_AT}" pattern="yy-MM-dd"/></div>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4 detail-label">강의명(과목코드)</div>
                                        <div class="col-md-8">${detail.SUBJECT_NAME}(${detail.SUBJECT_CD})</div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4 detail-label">처리상태</div>
                                        <div class="col-md-8">${detail.APPROVE_STATUS_NAME}</div>
                                    </div>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4 detail-label">학점(시수)</div>
                                        <div class="col-md-8">${detail.CREDIT}학점(${detail.HOURS}시수)</div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="row">
                                        <div class="col-md-4 detail-label">예상 정원</div>
                                        <div class="col-md-8">${detail.EXPECT_CAP}명</div>
                                    </div>
                                </div>
                            </div>
                            <c:if test="${detail.APPROVE_STATUS_NAME == '반려'}">
                                <div class="row mb-3">
                                    <div class="col-md-12">
                                        <div class="row">
                                            <div class="col-md-2 detail-label">반려 사유</div>
                                            <div class="col-md-10">${detail.COMMENTS}</div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <div class="row">
                                        <div class="col-md-2 detail-label">선수학습과목</div>
                                        <div class="col-md-10">${detail.PREREQ_SUBJECT}</div>
                                    </div>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <div class="row">
                                        <div class="col-md-2 detail-label">성적 산출 비율</div>
                                        <div class="col-md-10">
                                            <c:if test="${empty detail.gradeRatios}">
                                                <p class="text-muted">등록된 성적 산출 비율이 없습니다.</p>
                                            </c:if>
                                            <c:if test="${not empty detail.gradeRatios}">
                                                <c:forEach var="ratio" items="${detail.gradeRatios}" varStatus="status">
                                                    ${ratio.GRADE_CRITEIRA_CD_NAME}(${ratio.RATIO}%)<c:if test="${!status.last}">, </c:if>
                                                </c:forEach>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <div class="row">
                                        <div class="col-md-2 detail-label">강의 개요</div>
                                        <div class="col-md-10">${detail.LECTURE_INDEX}</div>
                                    </div>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <div class="row">
                                        <div class="col-md-2 detail-label">강의 목표</div>
                                        <div class="col-md-10">${detail.LECTURE_GOAL}</div>
                                    </div>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-12">
                                    <div class="row">
                                        <div class="col-md-2 detail-label">주차별 학습 계획</div>
                                        <div class="col-md-10">
                                            <c:if test="${empty detail.weeklyPlans}">
                                                <p class="text-muted">등록된 주차별 학습 계획이 없습니다.</p>
                                            </c:if>
                                            <c:if test="${not empty detail.weeklyPlans}">
                                                <table class="table table-bordered table-striped">
                                                    <thead>
                                                        <tr>
                                                            <th>주차</th>
                                                            <th>학습 목표</th>
                                                            <th>설명</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="plan" items="${detail.weeklyPlans}">
                                                            <tr>
                                                                <td>${plan.LECTURE_WEEK}주차</td>
                                                                <td>${plan.WEEK_GOAL}</td>
                                                                <td>${plan.WEEK_DESC}</td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="d-flex justify-content-end mt-4">
                                <c:set var="isMyApplication" value="${loggedInProfessorNo == detail.PROFESSOR_NO}" />
                                <c:if test="${isDepartmentHead and not isMyApplication and detail.APPROVE_STATUS_NAME == '대기중'}">
                                    <button type="button" class="btn btn-success me-2" id="approveBtn">승인하기</button>
                                    <button type="button" class="btn btn-danger me-2" id="rejectBtn">반려하기</button>
                                </c:if>

                                <c:if test="${isMyApplication and detail.APPROVE_STATUS_NAME == '대기중'}">
                                    <button type="button" class="btn btn-warning me-2" id="cancelBtn">신청 취소</button>
                                </c:if>
                                <a href="${pageContext.request.contextPath}/lms/professor/lectureRegist/list" class="btn btn-secondary">목록으로</a>
                            </div>
                        </c:if>
                    </div><!-- card-body -->
                </div><!-- card -->
            </div><!-- col -->
        </div><!-- row -->
    </div><!-- py-4 -->
</div><!-- container -->

<script>
    document.addEventListener("DOMContentLoaded", function() {
        const contextPath = "${pageContext.request.contextPath}";
        const lctApplyId = "${detail.LCT_APPLY_ID}";
        const approveId = "${detail.APPROVE_ID}";

        const submitBtn = document.getElementById("submitForApprovalBtn");
        if (submitBtn) {
            submitBtn.addEventListener("click", async function() {
                if (!lctApplyId) {
                    alert("신청 ID를 찾을 수 없습니다.");
                    return;
                }

                if (!confirm("강의 신청을 승인 제출하시겠습니까?")) {
                    return;
                }

                try {
                    const formData = new URLSearchParams();
                    formData.append('lctApplyId', lctApplyId);

                    const response = await fetch(`${contextPath}/lms/professor/lectureRegist/submitForApproval`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: formData
                    });

                    const result = await response.json();

                    if (result.status === 'success') {
                        alert(result.message);
                        window.location.href = `${contextPath}/lms/professor/lectureRegist/list`;
                    } else {
                        alert(result.message);
                    }
                } catch (error) {
                    console.error("Error submitting for approval:", error);
                    alert("승인 제출 중 오류가 발생했습니다.");
                }
            });
        }

        const approveBtn = document.getElementById("approveBtn");
        if (approveBtn) {
            approveBtn.addEventListener("click", async function() {
                if (!confirm("이 강의 개설 신청을 승인하시겠습니까?")) {
                    return;
                }

                console.log("Approve - lctApplyId:", lctApplyId, "approveId:", approveId);
                try {
                    const formData = new URLSearchParams();
                    formData.append('lctApplyId', lctApplyId);
                    formData.append('approveId', approveId);

                    const response = await fetch(`${contextPath}/lms/professor/lectureRegist/approveApplication`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: formData
                    });

                    const result = await response.json();

                    if (result.status === 'success') {
                        alert(result.message);
                        window.location.href = `${contextPath}/lms/professor/lectureRegist/list`;
                    } else {
                        alert(result.message);
                    }
                } catch (error) {
                    console.error("Error during approval:", error);
                    alert("승인 처리 중 오류가 발생했습니다.");
                }
            });
        }

        const rejectBtn = document.getElementById("rejectBtn");
        if (rejectBtn) {
            rejectBtn.addEventListener("click", async function() {
                const rejectionReason = prompt("강의 개설 신청을 반려하시겠습니까? 반려 사유를 입력해주세요.");
                if (rejectionReason === null || rejectionReason.trim() === "") {
                    alert("반려 사유를 입력해야 합니다.");
                    return;
                }

                console.log("Reject - lctApplyId:", lctApplyId, "approveId:", approveId, "rejectionReason:", rejectionReason);
                try {
                    const formData = new URLSearchParams();
                    formData.append('lctApplyId', lctApplyId);
                    formData.append('approveId', approveId);
                    formData.append('rejectionReason', rejectionReason); // Add rejection reason

                    const response = await fetch(`${contextPath}/lms/professor/lectureRegist/rejectApplication`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: formData
                    });

                    const result = await response.json();

                    if (result.status === 'success') {
                        alert(result.message);
                        window.location.href = `${contextPath}/lms/professor/lectureRegist/list`;
                    } else {
                        alert(result.message);
                    }
                } catch (error) {
                    console.error("Error during rejection:", error);
                    alert("반려 처리 중 오류가 발생했습니다.");
                }
            });
        }

        const cancelBtn = document.getElementById("cancelBtn");
        if (cancelBtn) {
            cancelBtn.addEventListener("click", async function() {
                if (!confirm("강의 신청을 취소하시겠습니까?")) {
                    return;
                }

                try {
                    const formData = new URLSearchParams();
                    formData.append('lctApplyId', lctApplyId);

                    const response = await fetch(`${contextPath}/lms/professor/lectureRegist/cancelApplication`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: formData
                    });

                    const result = await response.json();

                    if (result.status === 'success') {
                        alert(result.message);
                        window.location.href = `${contextPath}/lms/professor/lectureRegist/list`;
                    } else {
                        alert(result.message);
                    }
                } catch (error) {
                    console.error("Error during cancellation:", error);
                    alert("신청 취소 중 오류가 발생했습니다.");
                }
            });
        }
    });
</script>
</body>
</html>