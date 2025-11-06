<%-- 
 * == 변경 이력(Modification Information) ==
 *   
 *   수정일자      		수정자           수정내용
 *  ============   	============== =======================
 *  2025. 10. 13.     	장승호            최초 생성
 *
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8" />
	<title>JSU 클래스룸 - 오프라인 시험 기록</title>
	<c:url value="/js/app/classroom/professor/prfExamOfflineForm.js" var="offlineExamJs" />
	<script src="${offlineExamJs}" defer></script>
</head>
<body>
	<c:url value="/classroom/api/v1/professor/${lectureId}/students" var="studentsApi" />
	<c:url value="/classroom/api/v1/professor/exam/${lectureId}/offline" var="submitApi" />

	<section id="exam-offline-root" class="container py-4"
			 data-ctx="${pageContext.request.contextPath}"
			 data-lecture-id="<c:out value='${lectureId}' />"
			 data-student-api="${studentsApi}"
			 data-submit-api="${submitApi}">

		<c:set var="activeTab" value="exam" />
		<%@ include file="/WEB-INF/fragments/classroom/professor/nav.jspf" %>

		<div class="d-flex align-items-center justify-content-between mb-4">
			<h1 class="h3 mb-0">오프라인 시험 기록</h1>
		</div>

		<div id="alert-box" class="alert alert-danger d-none" role="alert"></div>
		<div id="toast-box" class="alert alert-success d-none" role="alert">등록이 완료되었습니다.</div>

		<form id="exam-offline-form" class="vstack gap-4">
			<c:if test="${not empty _csrf}">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			</c:if>

			<div class="card">
				<div class="card-header">
					<strong>시험 정보</strong>
				</div>
				<div class="card-body vstack gap-3">
					<div class="row g-3">
						<div class="col-12 col-lg-6">
							<label for="examName" class="form-label">
								시험명 <span class="text-danger">*</span>
							</label>
							<input type="text" id="examName" name="examName" class="form-control" maxlength="100"
								   placeholder="예: 철학과 윤리 중간고사" autocomplete="off" required />
						</div>
						<div class="col-12 col-lg-6">
							<label for="weightValue" class="form-label">
								가중치(%) <span class="text-muted">(선택)</span>
							</label>
							<input type="number" id="weightValue" name="weightValue" class="form-control" min="0" max="100" step="1"
								   placeholder="0~100 사이, 비워두면 미설정" />
							<div class="form-text">현재 단계에서는 최대값 100만 확인합니다.</div>
						</div>
					</div>

					<div>
						<label for="examDesc" class="form-label">
							시험 설명 <span class="text-danger">*</span>
						</label>
						<textarea id="examDesc" name="examDesc" class="form-control" rows="3"
								  placeholder="예: 중간고사 반영 비율은 35%입니다." required></textarea>
					</div>

					<div class="row g-3">
						<div class="col-12 col-md-6">
							<label for="startAt" class="form-label">
								시험 시작 일시 <span class="text-danger">*</span>
							</label>
							<input type="datetime-local" id="startAt" name="startAt" class="form-control" required />
						</div>
						<div class="col-12 col-md-6">
							<label for="endAt" class="form-label">
								시험 종료 일시 <span class="text-danger">*</span>
							</label>
							<input type="datetime-local" id="endAt" name="endAt" class="form-control" required />
							<div class="form-text">종료 일시는 시작 일시 이후여야 합니다.</div>
						</div>
					</div>
				</div>
			</div>

			<div class="card">
				<div class="card-header d-flex align-items-center justify-content-between gap-2 flex-wrap">
					<strong>학생별 점수 입력</strong>
					<button type="button" id="random-score-btn" class="btn btn-sm btn-outline-secondary">
						랜덤 점수 채우기
					</button>
				</div>
				<div class="card-body">
					<div id="students-placeholder" class="text-center text-muted py-4">
						학생 정보를 불러오는 중입니다...
					</div>
					<div id="student-score-list" class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-3 d-none"></div>
				</div>
			</div>

			<div class="d-flex justify-content-end gap-2">
				<button type="reset" class="btn btn-outline-secondary">초기화</button>
				<button type="submit" id="submit-btn" class="btn btn-primary">시험 기록 저장</button>
			</div>
		</form>
	</section>
</body>
</html>
