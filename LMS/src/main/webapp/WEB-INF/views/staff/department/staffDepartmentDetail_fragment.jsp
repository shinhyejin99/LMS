<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<div class="modal fade" id="univDeptDetailModal" tabindex="-1">
     <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title fw-bold">학과 상세 정보 (<span id="modal-dept-name-title-detail"></span>)</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">

                <div class="row g-4">

                    <div class="col-6">
                        <div class="list-group list-group-flush small border rounded shadow-sm">

                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">학과명</span>
                                <div class="col-7 text-end"><span class="fw-medium" id="modal-univDeptName-detail"></span></div>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">소속 단과대</span>
                                <div class="col-7 text-end"><span class="fw-medium" id="modal-collegeName-detail"></span></div>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">학과장</span>
                                <div class="col-7 text-end"><span class="fw-medium" id="modal-deptHeadName-detail"></span></div>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">연락처</span>
                                <div class="col-7 text-end"><span class="text-secondary" id="modal-officeNo-detail"></span></div>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">설립일</span>
                                <div class="col-7 text-end"><span class="text-secondary" id="modal-createAt-detail"></span></div>
                            </li>
                        </div>
                    </div>

                    <div class="col-6">
                        <div class="list-group list-group-flush small border rounded shadow-sm">

                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">교수 인원</span>
                                <div class="col-7 text-end">
                                    <span class="fw-bold text-primary" id="modal-professorCount-detail"></span>명
                                </div>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">학생 인원</span>
                                <div class="col-7 text-end">
                                    <span class="fw-bold text-danger" id="modal-studentCount-detail"></span>명
                                </div>
                            </li>


                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">교과목 수</span>
                                <div class="col-7 text-end">
                                    <span class="fw-bold text-info" id="modal-subjectsCount-detail"></span>개
                                </div>
                            </li>



                            <li class="list-group-item d-flex justify-content-between align-items-center py-2">
                                <span class="fw-semibold text-muted col-5">상태</span>
                                <div class="col-7 text-end"><span class="badge" id="modal-status-detail"></span></div>
                            </li>

                        </div>
                    </div>

                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary me-auto" data-bs-dismiss="modal">닫기</button>
                <button type="button" class="btn btn-warning" id="btn-open-modify-modal">
                    <i class="bi bi-pencil me-1"></i> 수정</button>
            </div>
        </div>
    </div>
</div>