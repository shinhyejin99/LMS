<%-- /WEB-INF/views/staff/student/staffStudentInfoDetail.jsp (AJAX Fragment) --%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- ⭐️ 주의: HTML, HEAD, BODY, 라이브러리 로드 등 전체 레이아웃 요소는 모두 제거했습니다. ⭐️ --%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>학생 개인신상 정보</title>
<link rel="stylesheet" href="personal_info.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentInfo.css" />

</head>
<body>

	<div class="student-personal-info-page">
		<div class="student-info-container">
			<c:set var="student" value="${student}" />

			<div class="student-info-container">
				<h1 class="info-page-title">학생 개인신상 정보</h1>

				<%-- Breadcrumb: 목록 링크에 window.loadContent 바인딩. 목록 경로를 /lms/staff/students/ 로 통일했습니다. --%>
				<ol class="breadcrumb">
					<li class="breadcrumb-item"><a href="/staff">Home</a></li>
					<li class="breadcrumb-item">인사업무</li>
					<li class="breadcrumb-item">
						<%-- ⭐️ 수정: 목록으로 돌아가는 AJAX 함수 호출 (경로 통일) ⭐️ --%> <a
						href="javascript:void(0);"
						onclick="window.loadContent('/lms/staff/students/', '학생 목록', document.getElementById('student-tab'))">
							학생 목록 </a>
					</li>
					<li class="breadcrumb-item active" aria-current="page">학생 상세
						정보</li>
				</ol>

				<div class="info-card">
					<div class="info-card-header">
						<h2 class="info-card-title">학적정보</h2>
					</div>
					<div class="info-card-body">
						<%-- 학적 정보와 사진을 배치하기 위한 레이아웃 --%>
						<div class="stu-info-layout">
							<div class="stu-info-flex-container">
								<div class="stu-item-wrapper">
									<div class="stu-item-label">학번</div>
									<div class="stu-item-value">${student.studentNo}</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">이름</div>
									<div class="stu-item-value">${student.lastName}${student.firstName}
										/ ${student.engLname} ${student.engFname}</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">주민번호</div>
									<div class="stu-item-value">
										<c:choose>
											<c:when
												test="${not empty student.regiNo and fn:length(student.regiNo) ge 7}">
												<%-- 주민번호 7번째 자리부터 마스킹 --%>
                                    ${fn:substring(student.regiNo, 0, 6)}-${fn:substring(student.regiNo, 7, 8)}******
                                </c:when>
											<c:otherwise>정보 없음</c:otherwise>
										</c:choose>
									</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">성별</div>
									<div class="stu-item-value">
										<c:choose>
											<c:when test="${student.gender == 2 or student.gender == 4}">여자</c:when>
											<c:otherwise>남자</c:otherwise>
										</c:choose>
									</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">단과대학</div>
									<div class="stu-item-value">${student.collegeName}</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">소속학과</div>
									<div class="stu-item-value">${student.univDeptName}</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">학년</div>
									<div class="stu-item-value">${student.gradeName}</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">복수전공</div>
									<div class="stu-item-value">-</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">학적상태</div>
									<div class="stu-item-value">${student.stuStatusName}</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">입학구분</div>
									<div class="stu-item-value">${student.entranceTypeName}</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">입학년도</div>
									<div class="stu-item-value">
										<c:choose>
											<c:when
												test="${not empty student.entranceDate and fn:length(student.entranceDate) ge 4}">
                                    ${fn:substring(student.entranceDate, 0, 4)}
                                </c:when>
											<c:otherwise>정보 없음</c:otherwise>
										</c:choose>
									</div>
								</div>
								<div class="stu-item-wrapper">
									<div class="stu-item-label">졸업예정일</div>
									<div class="stu-item-value">
										<c:choose>
											<c:when test="${not empty student.expectedYeartermCd}">
                                    ${student.expectedYeartermCd}년
                                </c:when>
											<c:otherwise>정보 없음</c:otherwise>
										</c:choose>
									</div>
								</div>
							</div>

							<%-- 사진 영역 (width/height 200px/240px로 통일) --%>
							<div class="stu-info-photo-area">
								<div class="text-center">
									<c:set var="photoStyle"
										value="width: 200px; height: 240px; margin: 0 auto; object-fit: cover;" />
									<c:choose>
										<c:when test="${not empty student.photoId}">
											<c:url var="photoUrl" value="/devtemp/files/idphoto">
												<c:param name="fileId" value="${student.photoId}" />
											</c:url>
											<img src="${photoUrl}"
												alt="${student.lastName}${student.firstName} 증명사진"
												class="border border-2 border-secondary rounded p-4 mb-3 bg-light"
												style="${photoStyle}">
										</c:when>
										<c:otherwise>
											<%-- ⭐️ 수정: 사진 없을 경우 플레이스홀더 크기를 이미지와 동일하게 조정 ⭐️ --%>
											<div
												class="border border-2 border-secondary rounded p-4 mb-3 bg-light d-flex align-items-center justify-content-center"
												style="${photoStyle}">
												<span class="text-muted">사진 없음</span>
											</div>
										</c:otherwise>
									</c:choose>
									<div class="fw-bold text-dark">${student.lastName}${student.firstName}</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="info-card">
					<div class="info-card-header">
						<h2 class="info-card-title">인적정보</h2>
					</div>
					<div class="info-card-body">
						<form id="info-form" action="/lms/student/info/modify"
							method="post">
							<input type="hidden" name="userId" value="${student.userId}" />
							<input type="hidden" name="studentNo"
								value="${student.studentNo}" /> <input type="hidden"
								name="addrId" value="${student.addrId}" />

							<div class="form-grid">
								<div class="form-item">
									<label class="form-label"><span class="required">*</span>
										이름(한글/영문)</label>
									<div id="name" class="form-control border-2"
										data-value="${student.lastName}${student.firstName} / ${student.engLname} ${student.engFname}">
										${student.lastName}${student.firstName} / ${student.engLname}
										${student.engFname}</div>
								</div>
								<div class="form-item">
									<label class="form-label">이메일</label>
									<div id="email" class="form-control border-2"
										data-value="${student.email}">${student.email}</div>
								</div>
								<div class="form-item">
									<label class="form-label"><span class="required">*</span>
										휴대전화</label>
									<div id="mobile" class="form-control border-2"
										data-value="${student.mobileNo}">${student.mobileNo}</div>
								</div>
								<div class="form-item">
									<label class="form-label"><span class="required">*</span>
										우편번호</label>
									<div id="zipCode" class="form-control border-2"
										data-value="${student.zipCode}">${student.zipCode}</div>
								</div>
								<div class="form-item">
									<label class="form-label"><span class="required">*</span>
										주소</label>
									<div id="baseAddr" class="form-control border-2"
										data-value="${student.baseAddr}">${student.baseAddr}</div>
								</div>
								<div class="form-item">
									<label class="form-label"><span class="required">*</span>
										상세주소</label>
									<div id="detailAddr" class="form-control border-2"
										data-value="${student.detailAddr}">${student.detailAddr}</div>
								</div>
								<div class="form-item">
									<label class="form-label">지도교수</label>
									<div id="professor" class="form-control border-2"
										data-value="${student.professorName}">${student.professorName}</div>
								</div>
								<%-- <div class="form-item">
                        <label class="form-label">지도교수연락처</label>
                        <div id="officeNo" class="form-control border-2" data-value="${student.officeNo}">${student.officeNo}</div>
                    </div> --%>
								<div class="form-item">
									<label class="form-label">비상연락처</label>
									<div id="guardPhone" class="form-control border-2"
										data-value="${student.guardPhone}">${student.guardPhone}</div>
								</div>
								<div class="form-item">
									<label class="form-label"><span class="required">*</span>
										은행구분</label>
									<div id="bank_name" class="form-control border-2"
										data-value="${student.bankName}">${student.bankName}</div>
								</div>
								<div class="form-item">
									<label class="form-label"><span class="required">*</span>
										계좌번호</label>
									<div id="account_number" class="form-control border-2"
										data-value="${student.bankAccount}">${student.bankAccount}</div>
								</div>
								<div class="form-item">
									<label class="form-label"><span class="required">*</span>
										예금주</label>
									<div id="account_holder" class="form-control border-2"
										data-value="${student.lastName}${student.firstName}">${student.lastName}${student.firstName}</div>
								</div>
							</div>
						</form>
					</div>



					<div>
						<div>
							<c:url var="modifyUrl"
								value="/lms/staff/students/modify/${student.studentNo}" />
							<a href="${modifyUrl}" class="btn btn-success me-2" role="button"><i
								class="bi bi-person-plus me-2"></i> 학생 수정</a>

							<c:url var="listUrl" value="/lms/staff/students/" />
							<a href="${listUrl}" class="btn btn-success me-2" role="button"><i
								class="bi bi-person-plus me-2"></i> 목록으로</a> </a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<script>
    // ⭐️ 중요: 상세 페이지에서 필요한 스크립트만 남기고,
    // 페이지 이동 관련 로직은 모두 window.loadContent로 위임합니다.
    $(document).ready(function() {
        console.log("학생 상세 페이지 Fragment 로드 완료.");
        // 여기서 주소 검색 API 초기화 등 상세 페이지 고유의 기능을 추가합니다.
    });
</script>
</body>
</html>
