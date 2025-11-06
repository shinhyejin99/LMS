<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>교수 신규 등록</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css"
	rel="stylesheet">
<style>
.info-field {
	min-height: calc(1.5em + 0.75rem + 2px);
	padding: 0.375rem 0.75rem;
	display: flex;
	align-items: center;
	border: 1px solid #ced4da;
	border-radius: 0.25rem;
	background-color: #e9ecef;
}
</style>
</head>
<body class="bg-light">

  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
	<div class="container-fluid" style="max-width: 1200px;">
		<div class="py-4">
			<h1 class="text-center text-dark mb-5 fw-semibold">교수 신규 등록 페이지</h1>

			<form:form modelAttribute="professor" action="/lms/staff/professors/create" method="post" enctype="multipart/form-data">

				<form:hidden path="gender" id="hiddenGender" />
				<form:hidden path="lastName" id="lastName" />
				<form:hidden path="firstName" id="firstName" />

				<div class="mb-5">
					<h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">인사정보</h4>
					<div class="card shadow-sm border-0">
						<div class="card-body p-4">
							<div class="row">

								<div class="col-lg-9">
									<div class="row g-3">

										<div class="col-md-6">
		                                    <div class="fw-bold text-dark mb-1"><span class="text-danger fw-bold">*</span>이름</div>
		                                    <input name="userName" id="userName" type="text" class="form-control border-2" placeholder="예: 홍길동 (띄어쓰기 없이 입력 권장)">
                              		  </div>
										<div class="col-md-6">
											<label class="form-label fw-semibold text-dark">교번</label>
											<form:input path="professorNo" class="form-control" readonly="true" placeholder="등록 시 자동 생성" />
										</div>


										<div class="col-md-6">
											<label class="form-label fw-semibold text-dark" for="regiNo"> <span class="text-danger fw-bold">*</span>주민번호</label>
											<form:input path="regiNo" class="form-control" id="regiNo" placeholder="9012311234567 형식 (하이픈 제외)" maxlength="13" />
										</div>


                                    	<div class="col-md-6">

                                        <div class="fw-bold text-dark mb-1">성별</div>
                                        <div class="text-dark-emphasis">
                                         <form:input path="gender" id="genderSelect" type="text" class="form-control border-2" readonly="true" />
 				                   	    	 </div>
                             			   </div>
										<div class="col-md-6">
											    <label class="form-label fw-semibold text-dark" for="prfAppntCd"> <span class="text-danger fw-bold">*</span>임용구분</label>
                                                 <%-- path는 prfAppntCd로 올바르게 사용 중 --%>
											    <form:select path="prfAppntCd" id="prfAppntCd" class="form-select">
											        <form:option value="">-- 임용구분 --</form:option>
											        <form:options items="${profAppntList}" itemValue="commonCd" itemLabel="cdName"/>
											    </form:select>
											</div>

										<div class="col-md-6">
											<label class="form-label fw-semibold text-dark" for="prfPositCd">보직명</label>
											<form:select path="prfPositCd" class="form-select">
												<form:option value="">-- 선택 --</form:option>
												<form:options items="${prfPositList}" itemValue="commonCd" itemLabel="cdName"/>
											</form:select>
										</div>

										<div class="col-md-6">
											<label class="form-label fw-semibold text-dark" for="hireDateString"> <span class="text-danger fw-bold">*</span>임용일자</label>
											<form:input path="hireDateString" type="date" class="form-control" />
										</div>

										<div class="col-md-6">
											<label class="form-label fw-semibold text-dark" for="prfStatusCd"> <span class="text-danger fw-bold">*</span>재직상태</label>
											<form:select path="prfStatusCd" class="form-select">
												<form:option value="">-- 선택 --</form:option>
												<form:options items="${prfStatusList}" itemValue="commonCd" itemLabel="cdName"/>
											</form:select>
										</div>
									</div>
								</div>

								<%-- 사진 영역 및 업로드 --%>
							<div class="col-lg-3">
							    <div class="text-center">
							        <c:choose>
							            <c:when test="${not empty userStaffDTO.userInfo.photoId}">
							                <c:url var="photoUrl" value="/devtemp/files/idphoto">
							                    <c:param name="fileId" value="${userStaffDTO.userInfo.photoId}"/>
							                </c:url>
							                <img src="${photoUrl}"
							                     alt="${userStaffDTO.userInfo.lastName}${userStaffDTO.userInfo.firstName} 증명사진"
							                     class="border border-2 border-secondary rounded p-2 mb-2 bg-light"
							                     style="width: 120px; height: 150px; margin: 0 auto; object-fit: cover;">
							            </c:when>
							            <c:otherwise>
							                <div class="border border-2 border-secondary rounded p-2 mb-2 bg-light d-flex align-items-center justify-content-center" style="width: 120px; height: 150px; margin: 0 auto;">
							                    <span class="text-muted">사진 없음</span>
							                </div>
							            </c:otherwise>
							        </c:choose>
							        <input type="file" id="photoFile" name="photoFile" class="form-control form-control-sm mt-2" accept="image/*">
							        <div class="fw-bold text-dark mt-2">${userStaffDTO.userInfo.lastName}${userStaffDTO.userInfo.firstName}</div>
							    </div>
							</div>
							</div>
						</div>
					</div>
				</div>
			<div class="mb-5">
					<h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">연락 및 인적정보</h4>
					<div class="card shadow-sm border-0">
						<div class="card-body p-4">
							<div class="row g-4">

								<div class="row g-4">
		                            <div class="col-md-6">
		                                <label for="engLname" class="form-label fw-semibold text-dark">
		                                    <span class="text-danger fw-bold">*</span> 이름(성 영문)
		                                </label>
		                                <form:input path="engLname" class="form-control" />

		                            </div>
		                            <div class="col-md-6">
		                                <label for="engFname" class="form-label fw-semibold text-dark">
		                                    <span class="text-danger fw-bold">*</span> 이름(이름 영문)
		                                </label>
		                                <form:input path="engFname" class="form-control" />

                            </div>

								<div class="col-md-12">
									<label class="form-label fw-semibold text-dark" for="email"> <span class="text-danger fw-bold">*</span>이메일</label>
									<form:input path="email" type="email" class="form-control" />
								</div>

								<div class="col-md-6">
									<label class="form-label fw-semibold text-dark" for="mobileNo"> <span class="text-danger fw-bold">*</span>휴대전화</label>
									<form:input path="mobileNo" class="form-control" placeholder="010-XXXX-XXXX" />
								</div>

								<div class="col-md-6">
									<label class="form-label fw-semibold text-dark" for="officeNo"> <span class="text-danger fw-bold">*</span>직통 전화번호 (연구실)</label>
									<form:input path="officeNo" class="form-control" placeholder="042-XXX-XXXX" />
								</div>

								<div class="col-md-6">
				                        <label for="postcode" class="form-label fw-semibold text-dark">
				                            <span class="text-danger fw-bold">*</span> 우편번호
				                        </label>

				                        <div class="d-flex gap-2">
				                            <form:input path="zipCode" id="postcode" class="form-control" readonly="true" />
				                            <button type="button" class="btn btn-primary me-2" id="zipbtn">검색</button>
				                        </div>
				                    </div>
								<div class="col-12">
									<label class="form-label fw-semibold text-dark" for="baseAddr"> <span class="text-danger fw-bold">*</span>주소</label>
									<form:input path="baseAddr" class="form-control" id="add1" readonly="true" />
								</div>

								<div class="col-12">
									<label class="form-label fw-semibold text-dark" for="detailAddr"> <span class="text-danger fw-bold">*</span>상세주소</label>
									<form:input path="detailAddr" class="form-control" id="add2" />
								</div>

								<div class="col-md-6">
									<label class="form-label fw-semibold text-dark" for="bankCode"> <span class="text-danger fw-bold">*</span>은행구분</label>
									<form:select path="bankCode" class="form-select" id="bankCodeSelect">
										<form:option value="">-- 선택 --</form:option>
										<form:options items="${bankList}" itemValue="commonCd" itemLabel="cdName"/>
									</form:select>
								</div>

								 <div class="col-md-6">
									<label class="form-label fw-semibold text-dark">예금주 (이름과 동일)</label>
									<div class="form-control info-field" id="depositor-name">
										<span id="full-name-display"></span>
									</div>
								</div>

								<div class="col-md-12">
									<label class="form-label fw-semibold text-dark" for="bankAccount"> <span class="text-danger fw-bold">*</span>계좌번호</label>
									<form:input path="bankAccount" class="form-control" required="true" />
								</div>

							</div> </div> </div> </div> <div class="mb-5">
					<h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">소속 및 연구 정보</h4>
					<div class="card shadow-sm border-0">
						<div class="card-body p-4">
							<div class="row g-4">

								    <div class="col-md-6">
								    <label for="collegeCd" class="form-label fw-semibold text-dark"><span class="text-danger fw-bold">*</span> 단과대학</label>
								     <select name="collegeCd" id="collegeSelect" class="form-control"> <option value="">선택</option>
								        <c:forEach var="college" items="${collegeList}">
								            <option value="${college.collegeCd}" <c:if test="${college.collegeCd eq collegeName}">selected</c:if>>
								              ${college.collegeName}
								        </option>
								      </c:forEach>
								   </select>
								</div>

								   <div class="col-md-6">
								    <label for="deptCd" class="form-label fw-semibold text-dark"><span class="text-danger fw-bold">*</span> 소속학과</label>
								     <form:select path="deptCd" id="deptSelect" class="form-control">
								         <form:option value="" data-college-cd="">선택</form:option>
								         <c:forEach var="univDept" items="${univDeptList}">
								             <form:option
								                 value="${univDept.univDeptCd}"
								                 data-college-cd="${univDept.collegeCd}"
								                 label="${univDept.univDeptName}"
								             />
								         </c:forEach>
								     </form:select>
								</div>

								<div class="col-12">
									<label class="form-label fw-semibold text-dark" for="researchRoom"> <span class="text-danger fw-bold">*</span>연구실</label>
									<form:input path="researchRoom" class="form-control" placeholder="건물명 및 호수" />
								</div>

								<hr class="section-divider">

								<div class="col-12">
									<label class="form-label fw-semibold text-dark" for="finalDegree"> <span class="text-danger fw-bold">*</span>최종학위</label>
									<form:input path="finalDegree" class="form-control" placeholder="최종학위명 및 취득 대학" />
								</div>



							</div> </div> </div> </div> <div class="text-center mt-5 mb-5">
					<button type="submit" class="btn btn-primary btn-lg me-3">
						<i class="bi bi-person-plus"></i> 신규 교수 등록
					</button>
					<c:url var="listUrl" value="/lms/staff/professors" />
					<a href="${listUrl}" class="btn btn-outline-secondary btn-lg">
						<i class="bi bi-x-circle"></i> 취소 및 목록으로
					</a>
				</div>
			</form:form>
		</div> </div>




   <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
  <script src="<c:url value='/js/app/staff/staffInfo.js' />"></script>
  <script src="<c:url value='/js/app/staff/photoFileAndDepositorName.js' />"></script>
  <script src="<c:url value='/js/app/staff/updateGenderFromRegiNo.js' />"></script>
  <script src="<c:url value='/js/app/staff/searchUI.js' />"></script>

</body>
</html>