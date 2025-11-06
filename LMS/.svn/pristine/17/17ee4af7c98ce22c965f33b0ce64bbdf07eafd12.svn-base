<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="jakarta.tags.core" prefix="c"%>

<!-- =========================
     학과 정보 수정 모달
========================= -->
<div class="modal fade" id="univDeptModifyModal" tabindex="-1" aria-labelledby="univDeptModifyModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header bg-warning text-dark">
        <h5 class="modal-title fw-bold" id="univDeptModifyModalLabel">
          학과 정보 수정 (<span id="modal-dept-name-title-modify"></span>)
        </h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>

      <div class="modal-body p-4">
        <form id="deptModifyForm">
          <input type="hidden" id="modal-univDeptCd-hidden-modify">

          <div class="row g-3">
            <div class="col-6">
              <label class="form-label small text-muted">소속 단과대</label>
              <input type="text" class="form-control bg-light" id="modal-collegeName-modify" readonly>
            </div>

            <div class="col-6">
              <label class="form-label small text-muted">학과명</label>
              <input type="text" class="form-control bg-light" id="modal-univDeptName-modify" readonly>
            </div>

            <div class="col-6">
              <label class="form-label small text-muted">설립일</label>
              <input type="text" class="form-control bg-light" id="modal-createAt-modify" readonly>
            </div>

            <div class="col-6"></div>

            <hr class="mt-4 mb-3">

            <div class="col-6">
              <label class="form-label small text-dark fw-bold">학과장</label>
              <input type="text" class="form-control" id="modal-deptHeadName-modify">
            </div>

            <div class="col-6">
              <label class="form-label small text-dark fw-bold">학사 연락처</label>
              <input type="text" class="form-control" id="modal-contact-modify">
            </div>

            <div class="col-6">
              <label class="form-label small text-dark fw-bold">상태</label>
              <select class="form-select" id="modal-status-modify">
                <option value="ACTIVE">활성</option>
                <option value="DELETED">폐지</option>
              </select>
            </div>

            <div class="col-6">
              <label class="form-label small text-muted">교과목 수(개)</label>
              <input type="number" class="form-control bg-light" id="modal-subjectsCount-modify" readonly>
            </div>

            <div class="col-6">
              <label class="form-label small text-muted">교수 인원(명)</label>
              <input type="number" class="form-control bg-light" id="modal-professorCount-modify" readonly>
            </div>

            <div class="col-6">
              <label class="form-label small text-muted">학생 인원(명)</label>
              <input type="number" class="form-control bg-light" id="modal-studentCount-modify" readonly>
            </div>
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary me-auto" data-bs-dismiss="modal">닫기</button>
        <button type="button" class="btn btn-primary" id="btn-save-dept">
          <i class="bi bi-floppy-fill me-1"></i> 변경 내용 저장
        </button>
      </div>
    </div>
  </div>
</div>

<style>
  /* SweetAlert2 모달이 항상 Bootstrap 모달보다 위에 오도록 */
  .swal2-container {
    z-index: 20000 !important;
  }
</style>s