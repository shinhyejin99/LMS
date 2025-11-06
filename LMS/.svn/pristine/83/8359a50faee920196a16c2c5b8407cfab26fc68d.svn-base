<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>교직원 개인신상 정보</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
	<link rel="stylesheet" href="personal_info.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentInfo.css" />

</head>
<body>
<div class="student-personal-info-page">
		<div class="student-info-container">
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
	<%-- Controller에서 'staff'라는 이름으로 UserStaffDTO 객체를 전달받았다고 가정 --%>
	<c:set var="staff" value="${staff}" />



			<div class="student-info-container">
			<h1 class="info-page-title">교직원 개인신상 정보
				페이지</h1>
					<ol class="breadcrumb">
			        <li class="breadcrumb-item"><a href="/staff">Home</a></li>
			        <li class="breadcrumb-item">인사업무</li>
			        <li class="breadcrumb-item"><a href="/lms/staffs">교직원 목록</a></li>
			        <li class="breadcrumb-item active" aria-current="page">내 정보</li>

			    </ol>
			<div class="info-card">
		    <div class="info-card-header">
		        <h2 class="info-card-title">인사정보</h2>
				</div>
				<div class="info-card-body">
					<div class="row g-3">
						<div class="col-lg-9">
							<div class="row g-3">

								<%-- 1. 사번 --%>
								<div class="col-md-6">
									<div class="border rounded p-3 bg-light info-field">
										<div class="fw-bold text-dark mb-1 me-3">사번</div>
										<div class="text-dark-emphasis">${staff.staffInfo.staffNo}</div>
									</div>
								</div>

								<%-- 2. 이름 --%>
								<div class="col-md-6">
									<div class="border rounded p-3 bg-light info-field">
										<div class="fw-bold text-dark mb-1 me-3">이름</div>
										<div class="text-dark-emphasis">${staff.userInfo.lastName}${staff.userInfo.firstName}</div>
									</div>
								</div>

								<%-- 3. 주민번호 --%>
								<div class="col-md-6">
									<div class="border rounded p-2 bg-light info-field">
										<div class="fw-bold text-dark mb-0 me-3">주민번호</div>
										<div id="regiNo" class="info-field-text">
											<c:choose>
												<c:when test="${not empty staff.userInfo.regiNo and fn:length(staff.userInfo.regiNo) ge 7}">
													${fn:substring(staff.userInfo.regiNo, 0, 6)}-${fn:substring(staff.userInfo.regiNo, 7, 8)}******
												</c:when>
												<c:otherwise>
													정보 없음
												</c:otherwise>
											</c:choose>
										</div>
									</div>
								</div>

								<%-- 4. 성별 --%>
								<div class="col-md-6">
									<div class="border rounded p-2 bg-light info-field">
										<div class="fw-bold text-dark mb-0 me-3">성별</div>
										<div id="genderSelect" class="info-field-text">
											<c:set var="genderCode" value="${fn:substring(staff.userInfo.regiNo, 6, 7)}" />
											<c:choose>
												<c:when test="${genderCode eq '1' or genderCode eq '3' or genderCode eq '5'}"> 남성 </c:when>
												<c:when test="${genderCode eq '2' or genderCode eq '4' or genderCode eq '6'}"> 여성 </c:when>
												<c:otherwise> 정보 없음 </c:otherwise>
											</c:choose>
										</div>
									</div>
								</div>

								<%-- 5. 소속 부서 --%>
								<div class="col-md-6">
									<div class="border rounded p-3 bg-light info-field">
										<div class="fw-bold text-dark mb-1 me-3">소속 부서</div>
										<div class="text-dark-emphasis">${staff.staffDeptInfo.stfDeptName}</div>
									</div>
								</div>

								<%-- 6. 학내 일반전화 --%>
								<div class="col-md-6">
									<div class="border rounded p-3 bg-light info-field">
										<div class="fw-bold text-dark mb-1 me-3">학내 일반전화</div>
										<div class="text-dark-emphasis">${staff.staffInfo.teleNo}</div>
									</div>
								</div>

							</div>
						</div>

						<%-- 사진 영역 (Top Right) --%>
						<div class="col-lg-3 d-flex flex-column align-items-center justify-content-start">
							<div class="text-center">
								<c:choose>
									<c:when test="${not empty staff.userInfo.photoId}">
										<c:url var="photoUrl" value="/devtemp/files/idphoto">
											<c:param name="fileId" value="${staff.userInfo.photoId}"/>
										</c:url>
										<img src="${photoUrl}"
											alt="${staff.userInfo.lastName}${staff.userInfo.firstName} 증명사진"
											class="border border-2 border-secondary rounded p-4 mb-3 bg-light"
											style="width: 200px; height: 240px; object-fit: cover;">
									</c:when>
									<c:otherwise>
										<div class="border border-2 border-secondary rounded p-4 mb-3 bg-light d-flex align-items-center justify-content-center" style="width: 200px; height: 240px;">
											<span class="text-muted">사진 없음</span>
										</div>
									</c:otherwise>
								</c:choose>
								<div class="fw-bold text-dark">${staff.userInfo.lastName}${staff.userInfo.firstName}</div>
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
					<div class="form-grid">
						<%-- 이름 (정보 확인용, 수정 불가) --%>
						<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								이름(한글)
							</label>
							<div class="form-control border-2 bg-light info-field">
								${staff.userInfo.lastName}${staff.userInfo.firstName}
							</div>
						</div>

						<%-- 이메일 --%>
						<div class="form-item">
							<label class="form-label fw-semibold text-dark">이메일</label>
							<div class="form-control border-2 bg-white info-field">
								${staff.userInfo.email}
							</div>
						</div>

						<%-- 우편번호 --%>
						<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								우편번호
							</label>
							<div class="form-control border-2 bg-white info-field">
								${staff.addressInfo.zipCode}
							</div>
						</div>

						<%-- 휴대전화 --%>
						<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								휴대전화
							</label>
							<div class="form-control border-2 bg-white info-field">
								${staff.userInfo.mobileNo}
							</div>
						</div>

						<%-- 기본 주소 (Full Width) --%>
						<div class="form-item form-item-full">
							<label class="form-label fw-semibold text-dark">
								주소
							</label>
							<div class="form-control border-2 bg-white info-field">
								${staff.addressInfo.baseAddr}
							</div>
						</div>

						<%-- 상세 주소 (Full Width) --%>
						<div class="form-item form-item-full">
							<label class="form-label fw-semibold text-dark">
								상세주소
							</label>
							<div class="form-control border-2 bg-white info-field">
								${staff.addressInfo.detailAddr}
							</div>
						</div>

						<%-- 은행 구분 (Bank Code Name) --%>
						<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								은행구분
							</label>
							<div class="form-control border-2 bg-light info-field">
								<c:forEach var="bank" items="${bankList}">
									<c:if test="${bank.commonCd eq userBankCd}">
										<div class="form-control-plaintext">${bank.cdName}</div>
									</c:if>
								</c:forEach>
							</div>
						</div>

						<%-- 계좌번호 --%>
						<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								계좌번호
							</label>
							<div class="form-control border-2 bg-white info-field">
								${staff.userInfo.bankAccount}
							</div>
						</div>

						<%-- 예금주 --%>
						<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								예금주
							</label>
							<div class="form-control border-2 bg-light info-field">
								${staff.userInfo.lastName}${staff.userInfo.firstName}
							</div>
						</div>

					</div>


				</div>
			</div>
	</div>
	</div>
	</div>


	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
	<script src="<c:url value='/js/app/staff/staffInfo.js' />"></script>
	<script src="<c:url value='/js/app/staff/photoFileAndDepositorName.js' />"></script>
	<script src="<c:url value='/js/app/staff/searchUI.js' />"></script>
</body>
</html>