<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>교수 개인신상 정보</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentInfo.css" />
</head>
	<link rel="stylesheet" href="personal_info.css">

<body>

<div class="student-personal-info-page">
		<div class="student-info-container">
	<c:set var="professor" value="${professor}" />


		<div class="student-info-container">
			<h1 class="info-page-title">교수 개인신상 정보</h1>
				<ol class="breadcrumb">
			        <li class="breadcrumb-item"><a href="/staff">Home</a></li>
			        <li class="breadcrumb-item">인사업무</li>
			        <li class="breadcrumb-item"><a href="/lms/staff/professors">교수 목록</a></li>
			        <li class="breadcrumb-item active" aria-current="page">교수 상세 정보</li>
			    </ol>

			<div class="info-card">
		    <div class="info-card-header">
		        <h2 class="info-card-title">교원정보</h2>
				</div>
				<div class="info-card-body">
					<div class="stu-info-layout">
						<div class="stu-info-flex-container">

									<div class="stu-item-wrapper">

											<div class="stu-item-label">교번</div>
											<div class="stu-item-value">${professor.professorNo}</div>
										</div>

									<div class="stu-item-wrapper">

											<div class="stu-item-label">이름</div>
											<div class="stu-item-value">${professor.lastName}${professor.firstName}
												/ ${professor.engLname} ${professor.engFname}</div>
										</div>

									<div class="stu-item-wrapper">

									        <div class="stu-item-label">주민번호</div>
									        <div class="stu-item-value">
									             <c:choose>
								                    <c:when test="${not empty professor.regiNo and fn:length(professor.regiNo) ge 7}">
								                        ${fn:substring(professor.regiNo, 0, 6)}-${fn:substring(professor.regiNo, 7, 8)}******
								                    </c:when>
								                    <c:otherwise>
								                        정보 없음
								                    </c:otherwise>
								                </c:choose>

									    </div>
									</div>

									<div class="stu-item-wrapper">

									        <div class="stu-item-label">성별</div>
									        <div class="stu-item-value">

									            <c:set var="genderCode" value="${fn:substring(professor.regiNo, 6, 7)}" />
									            <c:choose>
									                <c:when test="${genderCode eq '1' or genderCode eq '3' or genderCode eq '5'}"> 남성 </c:when>
									                <c:when test="${genderCode eq '2' or genderCode eq '4' or genderCode eq '6'}"> 여성 </c:when>
									                <c:otherwise> 정보 없음 </c:otherwise>
									            </c:choose>

									    </div>
									</div>
									<div class="stu-item-wrapper">

											<div class="stu-item-label">단과대학</div>
											<div class="stu-item-value">${empty professor.collegeName ? '정보 없음' : professor.collegeName}</div>
										</div>


									<div class="stu-item-wrapper">

											<div class="stu-item-label">소속학과</div>
											<div class="stu-item-value">${professor.departmentName}</div>
										</div>


									<div class="stu-item-wrapper">

											<div class="stu-item-label">직위</div>
											<div class="stu-item-value">${empty professor.positionName ? '정보 없음' : professor.positionName}</div>
										</div>


									<div class="stu-item-wrapper">

											<div class="stu-item-label">임용상태</div>
											<div class="stu-item-value">${empty professor.employmentType ? '정보 없음' : professor.employmentType}</div>
										</div>


									<div class="stu-item-wrapper">

											<div class="stu-item-label">재직상태</div>
											<div class="stu-item-value">${empty professor.employmentStatus ? '정보 없음' : professor.employmentStatus}</div>
										</div>


									 <div class="stu-item-wrapper">
									    <div class="stu-item-label">입용일자</div>
									    <div class="stu-item-value">
									         <c:choose>
									            <c:when test="${not empty professor.professorNo and fn:length(professor.professorNo) ge 4}">
									                ${fn:substring(professor.professorNo, 0, 4)}
									            </c:when>
									            <c:otherwise>
									                 정보 없음 (N/A)
									            </c:otherwise>
									         </c:choose>
									    </div>
									</div>


									<div class="stu-item-wrapper">

											<div class="stu-item-label">최종학위</div>
											<div class="stu-item-value">${empty professor.finalDegree ? '정보 없음' : professor.finalDegree}</div>
										</div>

									<div class="stu-item-wrapper">

											<div class="stu-item-label">학사전화번호</div>
											<div class="stu-item-value">${empty professor.officeNo ? '정보 없음' : professor.officeNo}</div>

									</div>
								</div>
							</div>

							<%-- Photo Area (Right 3 columns) --%>
							<div class="col-lg-3">
							<div class="text-center">
                                <c:choose>
						            <c:when test="${not empty professor.photoId}">
						                <c:url var="photoUrl" value="/devtemp/files/idphoto">
						                    <c:param name="fileId" value="${professor.photoId}"/>
						                </c:url>
						                <img src="${photoUrl}"
						                     alt="${professor.lastName}${professor.firstName} 증명사진"
						                     class="border border-3 border-secondary rounded  mb-1 bg-light"
						                     style="width: 200px; height: 240px; margin: 0 auto; object-fit: cover;">

						            </c:when>
						            <c:otherwise>
							                <div class="border border-2 border-secondary rounded p-4 mb-3 bg-light d-flex align-items-center justify-content-center" style="width: 200px; height: 240px; margin: 0 auto;">
							                    <span class="text-muted">사진 없음</span>
							                </div>
							            </c:otherwise>
							        </c:choose>
                                <div class="fw-bold text-dark">${professor.lastName}${professor.firstName}</div>
								</div>
							</div>
						</div>
					</div>





		<%-- ========================================================= --%>
		<%--                ⭐ 인적정보 (조회 전용) 섹션 ⭐              --%>
		<%-- ========================================================= --%>
		<div class="info-card">
				<div class="info-card-header">
					<h2 class="info-card-title">인적정보</h2>
				</div>
				<div class="info-card-body">
					<div class="form-grid">

						<%-- 이름 (정보 확인용, 수정 불가) --%>
							<div class="form-item">
								<label for="name" class="form-label fw-semibold text-dark">
									 이름(한글/영문)
								</label>
								<div id="name" class="form-control border-2 bg-light info-field"
									data-value="${professor.lastName}${professor.firstName} / ${professor.engLname} ${professor.engFname}">
									${professor.lastName}${professor.firstName} / ${professor.engLname}
									${professor.engFname}</div>
							</div>

						<%-- 이메일 --%>
							<div class="form-item">
							<label class="form-label fw-semibold text-dark">이메일</label>
							<div class="form-control border-2 bg-white info-field">
								${professor.email}
							</div>
						</div>

						<%-- 우편번호 --%>
							<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								 우편번호
							</label>
							<div class="form-control border-2 bg-white info-field">
								${professor.zipCode}
							</div>
						</div>

						<%-- 휴대전화 --%>
							<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								 휴대전화
							</label>
							<div class="form-control border-2 bg-white info-field">
								${professor.mobileNo}
							</div>
						</div>

						<%-- 주소 (Full Width) --%>
							<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								 주소
							</label>
							<div class="form-control border-2 bg-white info-field">
								${professor.baseAddr}
							</div>
						</div>

						<%-- 상세 주소 (Full Width) --%>
							<div class="form-item">
							<label class="form-label fw-semibold text-dark">
								 상세주소
							</label>
							<div class="form-control border-2 bg-white info-field">
								${professor.detailAddr}
							</div>
						</div>
						</div>
						</div>
						</div>


				<div class="info-card">
				<div class="info-card-header">
					<h2 class="info-card-title">금융 정보</h2>
				</div>
				<div class="info-card-body">
					<div class="row g-3">
						<div class="col-lg-12">
							<div class="row g-3">

						<%-- 은행 구분 (1/3 너비) --%>
							<div class="col-md-3">
								<label class="form-label"><span class="required">*</span> 은행구분</label>
								<div id="bank_name" class="form-control border-2"
									data-value="${professor.bankName}">${professor.bankName}
								</div>
							</div>

							<div class="col-md-3">
								<label class="form-label"><span class="required">*</span> 예금주</label>
								<div id="account_holder" class="form-control border-2"
									data-value="${student.lastName}${student.firstName}">
									${professor.lastName}${professor.firstName}
								</div>
						</div>
						<%-- 계좌번호 (1/3 너비) --%>
							<div class="col-md-6">
								<label class="form-label"><span class="required">*</span> 계좌번호</label>
								<div id="account_number" class="form-control border-2"
									data-value="${professor.bankAccount}">
									${professor.bankAccount}
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