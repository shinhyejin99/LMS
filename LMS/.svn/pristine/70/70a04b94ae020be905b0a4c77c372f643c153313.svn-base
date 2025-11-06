<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/professor/profGradReviewList.css" />
<style>
    .status-badge {
        color: white;
        border-radius: 12px;
        font-size: 13px;
        font-weight: 600;
        padding: 4px 12px;
        display: inline-block;
        text-align: center;
    }
    .status-pending {
        background-color: #28a745; /* green */
    }
    .status-approved {
        background-color: #007bff; /* blue */
    }
    .status-rejected {
        background-color: #dc3545; /* red */
    }
</style>
<div class="graduation-review-page">
    <div class="graduation-container">
        <!-- 페이지 헤더 추가 -->
        <div class="page-header">
            <h1>승인문서 조회</h1>
        </div>
        <div class="py-4">
            <div class="row justify-content-center"> <!-- Center the card horizontally -->                             
                        <div class="card-body">
                            <form id="approvalSearchForm" action="${pageContext.request.contextPath}/lms/professor/approvals" method="GET">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <div class="row mb-3">
                                    <div class="col-md-4">
                                        <label for="approvalTypeSelect" class="form-label">결재 유형 선택</label>
                                        <select class="form-select" id="approvalTypeSelect" name="approvalType">
                                            <option value="">전체</option>
                                            <option value="UNIV_RECORD_CHANGE" <c:if test="${approvalType == 'UNIV_RECORD_CHANGE'}">selected</c:if>>재학상태변경</option>
                                            <option value="UNIV_AFFIL_CHANGE" <c:if test="${approvalType == 'UNIV_AFFIL_CHANGE'}">selected</c:if>>소속변경신청</option>
                                        </select>
                                    </div>
                                    <div class="col-md-4">
                                        <label for="processStatusSelect" class="form-label">처리상태</label>
                                        <select class="form-select" id="processStatusSelect" name="processStatus">
                                            <option value="">전체</option>
                                            <option value="Y" <c:if test="${processStatus == 'Y'}">selected</c:if>>승인</option>
                                            <option value="W" <c:if test="${processStatus == 'W'}">selected</c:if>>대기</option>
                                            <option value="N" <c:if test="${processStatus == 'N'}">selected</c:if>>반려</option>
                                        </select>
                                    </div>
                                    <div class="col-md-4 d-flex align-items-end">
                                        <button type="submit" class="btn btn-primary">검색</button>
                                    </div>
                                </div>

                                <div class="mt-4 table-responsive">
                                    <table class="table table-centered table-hover mb-0" id="approvalListTable">
                                        <thead class="thead-light">
                                            <tr>
                                                <th class="border-0">접수유형</th>
                                                <th class="border-0">신청구분</th>
                                                <th class="border-0">신청일자</th>
                                                <th class="border-0">승인일자</th>
                                                <th class="border-0">처리상태</th>
                                                <th class="border-0">신청자</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${empty approvalList}">
                                                    <tr>
                                                        <td colspan="6" class="text-center text-muted">데이터가 없습니다.</td>
                                                    </tr>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="approval" items="${approvalList}">
                                                        <tr class="student-row"
                                                            data-approve-id="${approval.APPROVE_ID}"
                                                            data-student-no="${approval.APPLICANTSTUDENTNO}"
                                                            data-name="${approval.APPLICANTNAME}"
                                                            data-dept="${approval.APPLICANTDEPTNAME}"
                                                            data-grade="${approval.APPLICANTGRADENAME}"
                                                            data-mobile-no="${approval.APPLICANTMOBILENO}"
                                                            data-email="${approval.APPLICANTEMAIL}"
                                                            data-base-addr="${approval.APPLICANTBASEADDR}"
                                                            data-detail-addr="${approval.APPLICANTDETAILADDR}">
                                                            <td>${approval.GENERAL_DOCUMENT_TYPE}</td>
                                                            <td>${approval.SPECIFIC_DOCUMENT_TYPE}</td>
                                                            <td><fmt:formatDate value="${approval.APPLICATION_DATE}" pattern="yyyy-MM-dd"/></td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${empty approval.APPROVE_AT}">-</c:when>
                                                                    <c:otherwise>${approval.APPROVE_AT}</c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${approval.APPROVE_YNNULL == 'Y'}">
                                                                        <span class="status-badge status-approved">승인</span>
                                                                    </c:when>
                                                                    <c:when test="${approval.APPROVE_YNNULL == 'N'}">
                                                                        <span class="status-badge status-rejected">반려</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="status-badge status-pending">대기</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>${approval.APPLICANTNAME}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
							
                                <!-- Pagination -->
                                <div class="card-footer pb-0 pt-3">
                                    <nav aria-label="Page navigation example">
                                        <ul class="pagination justify-content-center">
                                            <c:if test="${pagingInfo.currentPage > 1}">
                                                <li class="page-item">
                                                    <a class="page-link" href="?currentPage=${pagingInfo.currentPage - 1}&approvalType=${approvalType}&processStatus=${processStatus}" aria-label="Previous">
                                                        <span aria-hidden="true">&laquo;</span>
                                                    </a>
                                                </li>
                                            </c:if>
                                            <c:forEach begin="${pagingInfo.startPage}" end="${pagingInfo.endPage}" var="pageNo">
                                                <li class="page-item ${pageNo == pagingInfo.currentPage ? 'active' : ''}">
                                                    <a class="page-link" href="?currentPage=${pageNo}&approvalType=${approvalType}&processStatus=${processStatus}">${pageNo}</a>
                                                </li>
                                            </c:forEach>
                                            <c:if test="${pagingInfo.currentPage < pagingInfo.totalPage}">
                                                <li class="page-item">
                                                    <a class="page-link" href="?currentPage=${pagingInfo.currentPage + 1}&approvalType=${approvalType}&processStatus=${processStatus}" aria-label="Next">
                                                        <span aria-hidden="true">&raquo;</span>
                                                    </a>
                                                </li>
                                            </c:if>
                                        </ul>
                                    </nav>
                                </div>

                            </form>
                        </div>
                    </div>
                </div>
            </div>
        <div class="modal fade" id="approvalDetailModal" tabindex="-1" aria-labelledby="approvalDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="approvalDetailModalLabel">승인문서 상세 정보</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="approvalDetailBody">
                <!-- Details will be populated by JavaScript -->
            </div>
            <div class="modal-footer">
                <button type="button" id="approveBtn" class="btn btn-success" data-approve-id="">승인</button>
                <button type="button" id="rejectBtn" class="btn btn-danger" data-approve-id="">반려</button>
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    const approvalDetailModalElement = document.getElementById('approvalDetailModal');
    const approvalDetailModal = new bootstrap.Modal(approvalDetailModalElement);
    const modalApproveBtn = document.getElementById('approveBtn');
    const modalRejectBtn = document.getElementById('rejectBtn');

    // 테이블의 각 행(row)을 클릭했을 때 모달을 띄우는 이벤트
    document.querySelectorAll('.student-row').forEach(row => {
        row.addEventListener('click', async () => {
            const approveId = row.getAttribute('data-approve-id');
            console.log("Row clicked. approveId from row:", approveId); // Debug log
            if (!approveId) {
                console.error('Approve ID not found on clicked row.');
                return;
            }

            // 승인/반려 버튼에 현재 문서의 ID를 설정
            modalApproveBtn.setAttribute('data-approve-id', approveId);
            modalRejectBtn.setAttribute('data-approve-id', approveId);
            console.log("approveId set on modal buttons:", approveId); // Debug log

            const modalBody = document.getElementById('approvalDetailBody');
            modalBody.innerHTML = '<p>로딩 중...</p>';
            approvalDetailModal.show();

            try {
                const response = await fetch(`/lms/professor/approvals/api/detail/` + approveId);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const approvalData = await response.json();

                let content = `
                    <h6>학생 정보</h6>
                    <table class='table table-bordered table-sm'>
                        <tbody>
                            <tr><th class='student-detail-label'>이름</th><td class='student-detail-value'>\${approvalData.APPLICANTNAME}</td><th class='student-detail-label'>학번</th><td class='student-detail-value'>\${approvalData.APPLICANTSTUDENTNO}</td></tr>
                            <tr><th class='student-detail-label'>학과</th><td class='student-detail-value'>\${approvalData.APPLICANTDEPTNAME}</td><th class='student-detail-label'>학년</th><td class='student-detail-value'>\${approvalData.APPLICANTGRADENAME}</td></tr>
                            <tr><th class='student-detail-label'>연락처</th><td class='student-detail-value'>\${approvalData.APPLICANTMOBILENO}</td><th class='student-detail-label'>이메일</th><td class='student-detail-value'>\${approvalData.APPLICANTEMAIL}</td></tr>
                            <tr><th class='student-detail-label'>주소</th><td class='student-detail-value' colspan='3'>(\${approvalData.APPLICANTZIPCODE}) \${approvalData.APPLICANTBASEADDR} \${approvalData.APPLICANTDETAILADDR}</td></tr>
                        </tbody>
                    </table>
                    <hr>
                    <h6>신청 정보</h6>
                    <table class='table table-bordered table-sm'>
                        <tbody>
                            <tr><th class='student-detail-label' style='width: 25%;'>신청 유형</th><td class='student-detail-value'>\${approvalData.DOCUMENTTYPE}</td></tr>
                            <tr><th class='student-detail-label'>신청 사유</th><td class='student-detail-value'>\${approvalData.APPLY_REASON}</td></tr>
                        </tbody>
                    </table>
                `;

                if (approvalData.attachments && approvalData.attachments.length > 0) {
                    content += `
                        <hr>
                        <h6>첨부 파일</h6>
                        <ul>
                    `;
                    approvalData.attachments.forEach(file => {
                        content += `<li><a href="/lms/file/download/\${file.id}" target="_blank">\${file.name}</a></li>`;
                    });
                    content += `</ul>`;
                }

                modalBody.innerHTML = content;

            } catch (error) {
                console.error('Error processing approval details:', error);
                modalBody.innerHTML = '<p>데이터 처리 중 오류가 발생했습니다.</p>';
            }
        });
    });

    // 모달의 '반려' 버튼 클릭 이벤트
    modalRejectBtn.addEventListener('click', async function() {
        const approveId = this.getAttribute('data-approve-id');
        if (!approveId) {
            alert('오류: 결재 문서 ID를 찾을 수 없습니다.');
            return;
        }

        const { value: comments } = await Swal.fire({
            target: '#approvalDetailModal',
            title: '반려 사유 입력',
            input: 'textarea',
            inputLabel: '반려 사유를 구체적으로 작성해주세요.',
            inputPlaceholder: '반려 사유...',
            showCancelButton: true,
            confirmButtonText: '반려 확정',
            cancelButtonText: '취소',
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d',
            inputValidator: (value) => {
                if (!value) {
                    return '반려 사유를 반드시 입력해야 합니다.'
                }
            }
        });

        if (comments) {
            const csrfToken = document.querySelector('input[name="${_csrf.parameterName}"]').value;
        	const csrfHeader = "X-CSRF-TOKEN";

            fetch(`/lms/professor/approvals/api/reject`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                	[csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    approveId: approveId,
                    comments: comments
                })
            })
            .then(response => response.json())
            .then(data => {
                if(data.success) {
                    Swal.fire({
                        target: '#approvalDetailModal',
                        title: '성공',
                        text: '성공적으로 반려 처리되었습니다.',
                        icon: 'success'
                    }).then(() => window.location.reload());
                } else {
                    throw new Error(data.message || '서버 응답에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    target: '#approvalDetailModal',
                    title: '오류',
                    text: '반려 처리에 실패했습니다. 다시 시도해주세요.',
                    icon: 'error'
                });
            });
        }
    });

    // '승인하기' 버튼 클릭 이벤트
    modalApproveBtn.addEventListener('click', async function() {
        const approveId = this.getAttribute('data-approve-id');
        if (!approveId) {
            alert('오류: 결재 문서 ID를 찾을 수 없습니다.');
            return;
        }

        const confirmation = await Swal.fire({
            target: '#approvalDetailModal',
            title: '승인 확인',
            text: "해당 문서를 승인하시겠습니까?",
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '승인',
            cancelButtonText: '취소'
        });

        if (confirmation.isConfirmed) {
            const comments = ""; // 의견 입력 없이 빈 문자열로 처리
            const csrfToken = document.querySelector('input[name="${_csrf.parameterName}"]').value;
            const csrfHeader = "X-CSRF-TOKEN";

            fetch(`/lms/professor/approvals/api/approve`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    approveId: approveId,
                    comments: comments
                })
            })
            .then(response => response.json())
            .then(data => {
                if(data.success) {
                    Swal.fire({
                        target: '#approvalDetailModal',
                        title: '성공',
                        text: '성공적으로 승인 처리되었습니다.',
                        icon: 'success'
                    }).then(() => window.location.reload());
                } else {
                    throw new Error(data.message || '서버 응답에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    target: '#approvalDetailModal',
                    title: '오류',
                    text: '승인 처리에 실패했습니다. 다시 시도해주세요.',
                    icon: 'error'
                });
            });
        }
    });
});
</script>