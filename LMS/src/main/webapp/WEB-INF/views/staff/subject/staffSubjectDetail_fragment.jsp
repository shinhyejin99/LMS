<%-- staffSubjectDetail_fragment.jsp (정보 추가 및 디자인 최종 버전) --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<div class="card shadow-sm mb-3 border-0">
    <div class="card-body p-0">
        <h4 class="card-title fw-bold text-dark mb-4">
            ${subject.subjectName}
            <span class="small text-muted fw-normal ms-2"></span>
        </h4>

        <div class="row g-2">

            <div class="col-6">
                <div class="list-group list-group-flush small border rounded">

                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">단과대학</span>
                        <div class="col-8 text-end">
                            <c:choose>
                                <c:when test="${not empty subject.collegeName}">
                                    <span class="fw-medium">${subject.collegeName}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-secondary">정보 없음</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </li>

                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">소속 학과</span>
                        <div class="col-8 text-end">
                            <c:choose>
                                <c:when test="${not empty subject.univDeptName}">
                                    <span class="fw-medium">${subject.univDeptName} <c:if test="${not empty subject.univDeptCd}"></c:if></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-secondary">학과 코드: ${subject.univDeptCd}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </li>

                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">담당 교수</span>
                        <div class="col-8 text-end">
                            <c:choose>
                                <c:when test="${not empty subject.professorName}">
                                    <span class="fw-medium">${subject.professorName} <c:if test="${not empty subject.officeNo}">(사무실: ${subject.officeNo})</c:if></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-warning">정보 없음</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </li>

                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">이수 구분</span>
                        <div class="col-8 text-end">
                            <c:choose>
                                <c:when test="${not empty subject.completionName}">
                                    <span class="badge bg-primary py-1">${subject.completionName}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-primary py-1">${subject.completionCd}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </li>
                    <%-- 상태 표시 --%>
                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">상태</span>
                        <div class="col-8 text-end">
                            <c:choose>
                                <c:when test="${empty subject.deleteAt}">
                                    <span class="badge bg-success">활성</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">폐지</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </li>
                </div>
            </div>

            <div class="col-6">
                <div class="list-group list-group-flush small border rounded">

                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">학년도 / 학기</span>
                        <div class="col-8 text-end">
                            <c:choose>
                                <c:when test="${not empty subject.termCd}">
                                    <span class="text-secondary">${subject.termCd}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-secondary">모든 학기 대상</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </li>

                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">대상 학년</span>
                        <div class="col-8 text-end">
                            <c:choose>
                                <c:when test="${not empty subject.gradeCd}">
                                    <span class="badge bg-info">${subject.gradeCd}학년</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-secondary">모든 학년 대상</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </li>

                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">정원</span>
                        <div class="col-8 text-end">
                            <span class="text-danger fw-bold">${subject.maxCap}</span>
                        </div>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">학점 / 시수</span>
                        <div class="col-8 text-end">
                            <span class="text-danger fw-bold">${subject.credit}학점 / ${subject.hour}시수</span>
                        </div>
                    </li>



                    <%-- ✅ 수정: 폐지일 (폐지 상태일 때만 list-group-item 형태로 표시) --%>
                    <c:if test="${not empty subject.deleteAt}">
                        <li class="list-group-item d-flex justify-content-between align-items-center py-2 bg-danger-subtle">
                            <span class="fw-bold text-danger col-4">폐지일</span>
                            <div class="col-8 text-end">
                                <span class="fw-bold text-danger">${subject.deleteAt}</span>
                            </div>
                        </li>
                    </c:if>
                    <%-- ---------------------------------------------------- --%>

                    <%-- 등록일은 폐지일 유무와 상관없이 마지막에 표시 --%>
                    <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                        <span class="fw-semibold text-muted col-4">등록일</span>
                        <div class="col-8 text-end">
                            <span class="text-secondary">${subject.createAt}</span>
                        </div>
                    </li>

                </div>
            </div>
        </div>

        <div class="d-flex justify-content-end mt-4">

            <button type="button"
                    class="btn btn-warning me-2"
                    data-bs-toggle="modal"
                    data-bs-target="#subjectModifyModal"
                    data-subject-cd="${subject.subjectCd}"
                    data-bs-dismiss="modal">
                <i class="bi bi-pencil-fill me-1"></i> 수정
            </button>

            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                <i class="bi bi-x-circle-fill me-1"></i> 닫기
            </button>
        </div>
    </div>
</div>