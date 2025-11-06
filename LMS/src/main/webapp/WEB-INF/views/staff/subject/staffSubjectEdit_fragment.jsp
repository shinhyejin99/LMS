<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<form id="modifySubjectForm" action="${pageContext.request.contextPath}/lms/staff/staffSubjects/modify" method="POST">

    <input type="hidden" name="subjectCd" value="${subject.subjectCd}">

    <div class="card shadow-lg border-0">

        <div class="card-body">

            <%-- 교과목명 (100% 폭) --%>
            <div class="row mb-3">
                <label for="subjectName" class="col-sm-3 col-form-label fw-bold">교과목명</label>
                <div class="col-sm-9">
                    <input type="text" class="form-control" id="subjectName" name="subjectName" value="${subject.subjectName}" readonly>
                </div>
            </div>

            <hr class="my-4">

            <div class="row g-3">

                <%-- 좌측 열 (소속/이수/교수) --%>
                <div class="col-md-6 border-end">

                    <h6 class="text-primary mb-3"><i class="bi bi-info-circle-fill me-1"></i> 기본 정보</h6>

                    <%-- 학부/단과대학 (읽기 전용) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label class="col-sm-3 col-form-label text-muted small">단과대학</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <input type="text" class="form-control form-control-sm bg-light" value="${subject.collegeName}" disabled>
                        </div>
                    </div>

                    <%-- 소속 학과 (select) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label for="univDeptCd" class="col-sm-3 col-form-label fw-semibold">소속 학과</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <select class="form-select form-select-sm" id="univDeptCd" name="univDeptCd">
                                <c:forEach items="${univDeptList}" var="dept">
                                    <option value="${dept.univDeptCd}" <c:if test="${subject.univDeptCd eq dept.univDeptCd}">selected</c:if>>
                                        ${dept.univDeptName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <%-- 이수 구분 (select) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label for="completionCd" class="col-sm-3 col-form-label fw-semibold">이수 구분</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <select class="form-select form-select-sm" id="completionCd" name="completionCd">
                                <c:forEach items="${completionList}" var="code">
                                    <option value="${code.commonCd}" <c:if test="${subject.completionCd eq code.commonCd}">selected</c:if>>
                                        ${code.cdName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <%-- 담당 교수 (읽기 전용) --%>
                    <div class="row mb-0">
                        <!-- 4 -> 3으로 변경 -->
                        <label class="col-sm-3 col-form-label text-muted small">담당 교수</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <input type="text" class="form-control form-control-sm bg-light" value="${subject.professorName}" disabled>
                            <c:if test="${not empty subject.officeNo}">
                                <small class="text-secondary mt-1 d-block">사무실: ${subject.officeNo}</small>
                            </c:if>
                        </div>
                    </div>
                    <%-- 상태 (select) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label for="deleteStatus" class="col-sm-3 col-form-label fw-semibold">상태</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <select class="form-select form-select-sm" id="deleteStatus" name="deleteStatus">
                                <option value="ACTIVE" <c:if test="${empty subject.deleteAt}">selected</c:if>>활성</option>
                                <option value="DELETED" <c:if test="${not empty subject.deleteAt}">selected</c:if>>폐지</option>
                            </select>
                        </div>
                    </div>
                </div>

                <%-- 우측 열 (학기/학년/학점/시수) --%>
                <div class="col-md-6">

                    <h6 class="text-primary mb-3"><i class="bi bi-calendar-event me-1"></i> 시간/대상 정보</h6>

                    <%-- 대상 학기 (select) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label for="termCd" class="col-sm-3 col-form-label fw-semibold">대상 학기</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <select class="form-select form-select-sm" id="termCd" name="termCd">
                                <c:forEach items="${termList}" var="code">
                                    <option value="${code.commonCd}" <c:if test="${subject.termCd eq code.commonCd}">selected</c:if>>
                                        ${code.cdName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <%-- 대상 학년 (select) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label for="gradeCd" class="col-sm-3 col-form-label fw-semibold">대상 학년</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <select class="form-select form-select-sm" id="gradeCd" name="gradeCd">
                                <option value="">모든 학년</option>
                                <c:forEach items="${gradeList}" var="code">
                                    <option value="${code.commonCd}" <c:if test="${subject.gradeCd eq code.commonCd}">selected</c:if>>
                                        ${code.cdName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <%-- 정원 (input number) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label for="maxCap" class="col-sm-3 col-form-label fw-semibold">정원</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <input type="number" class="form-control form-control-sm text-end" id="maxCap" name="maxCap" value="${subject.maxCap}"  required>
                        </div>
                    </div>
                    <%-- 학점 (input number) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label for="credit" class="col-sm-3 col-form-label fw-semibold">학점</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <input type="number" class="form-control form-control-sm text-end" id="credit" name="credit" value="${subject.credit}" min="1" max="6" required>
                        </div>
                    </div>

                    <%-- 시수 (input number) --%>
                    <div class="row mb-3">
                        <!-- 4 -> 3으로 변경 -->
                        <label for="hour" class="col-sm-3 col-form-label fw-semibold">시수</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <input type="number" class="form-control form-control-sm text-end" id="hour" name="hour" value="${subject.hour}" min="1" max="10" required>
                        </div>
                    </div>



                    <%-- 폐지일 표시 (수정 모달에서는 수정할 수 없으므로 비활성화) --%>
                    <c:if test="${not empty subject.deleteAt}">
                        <div class="row mb-3">
                            <!-- 4 -> 3으로 변경 -->
                            <label class="col-sm-3 col-form-label text-danger small fw-bold">폐지일</label>
                            <!-- 8 -> 9로 변경 -->
                            <div class="col-sm-9">
                                <input type="text" class="form-control form-control-sm bg-warning-subtle text-danger" value="${subject.deleteAt}" disabled>
                            </div>
                        </div>
                    </c:if>

                    <%-- 등록일 (읽기 전용) --%>
                    <div class="row">
                        <!-- 4 -> 3으로 변경 -->
                        <label class="col-sm-3 col-form-label text-muted small">등록일</label>
                        <!-- 8 -> 9로 변경 -->
                        <div class="col-sm-9">
                            <input type="text" class="form-control form-control-sm bg-light" value="${subject.createAt}" disabled>
                        </div>
                    </div>

                </div>
            </div>

        </div>

       <div class="card-footer d-flex justify-content-end">
        <button type="submit" class="btn btn-primary me-2"><i class="bi bi-save-fill me-1"></i> 변경 사항 저장</button>
       <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                <i class="bi bi-x-circle-fill me-1"></i> 취소
            </button>
    </div>
</div>
</form>
<style>
.swal2-container {
  z-index: 20000 !important;
}
</style>
<script>

$(document).ready(function() {
    $('#modifySubjectForm').on('submit', function(e) {
        // 폐지 시 경고 메시지 (유지)
        if ($('#deleteStatus').val() === 'DELETED') {
            if (!confirm('교과목을 폐지(DELETED) 상태로 변경합니다. 이 작업을 진행하면 폐지일이 기록됩니다. 계속하시겠습니까?')) {
                e.preventDefault(); // 경고에서 '취소'를 누르면 폼 제출을 막음
            }
        }

    });
});
</script>
