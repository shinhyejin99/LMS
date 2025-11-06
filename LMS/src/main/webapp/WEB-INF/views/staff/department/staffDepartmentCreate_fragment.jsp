<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>

<div class="modal fade" id="univDeptRegistrationModal" tabindex="-1">
     <div class="modal-dialog modal-md modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title fw-bold"><i class="bi bi-journal-plus me-1"></i> 신규 학과 등록</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="deptCreateForm">
                        <div class="col-md-6">
                            <label for="create-collegeName" class="form-label fw-bold">소속 단과대 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="create-collegeName" placeholder="예: IT대학">
                        </div>
                    <div class="row g-3">
                        <div class="col-md-12">
                            <label for="create-univDeptName" class="form-label fw-bold">학과명 <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="create-univDeptName">
                        </div>
                        <div class="col-md-6">
                            <label for="create-deptHeadName" class="form-label fw-bold">학과장 (선택)</label>
                            <input type="text" class="form-control" id="create-deptHeadName">
                        </div>
                        <div class="col-md-6">
                            <label for="create-contact" class="form-label fw-bold">학사 연락처</label>
                            <input type="officeNo" class="form-control" id="create-officeNo" placeholder="02-1234-5678">
                        </div>

                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                <button type="button" class="btn btn-success" id="btn-create-dept"><i class="bi bi-check-circle me-1"></i> 등록</button>
            </div>
        </div>
    </div>
</div>