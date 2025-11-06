<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>교직원 정보 등록</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<style>
/* 폼 페이지의 스타일은 그대로 유지하거나 필요에 따라 조정 */
.info-field {
	min-height: 48px;
	display: flex;
	align-items: center;
}
</style>
</head>
<body class="bg-light">
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
	<div class="container-fluid" style="max-width: 1200px;">
		<div class="py-4">
			<h1 class="text-center text-dark mb-5 fw-semibold">교직원 신규 등록 페이지</h1>

			<form:form modelAttribute="staff" action="/lms/staffs/create" method="post" enctype="multipart/form-data">

				<%-- ✅ hidden 필드로 성별 값 전송을 위한 필드 추가 --%>
				<form:hidden path="gender" id="hiddenGender" />

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
											<label class="form-label fw-semibold text-dark">사번</label>
											<form:input path="staffInfo.staffNo" class="form-control" readonly="true" placeholder="등록 시 자동 생성" />
										</div>

										<div class="col-md-6">

                                        <div class="fw-bold text-dark mb-1"><span class="text-danger fw-bold">*</span>주민번호</div>
                                        <form:input path="userInfo.regiNo" id="regiNo" type="text" class="form-control border-2" placeholder="예: 9012311234567" maxlength="14" />

                                </div>

										 <div class="col-md-6">

                                        <div class="fw-bold text-dark mb-1">성별</div>
                                        <div class="text-dark-emphasis">
                                         <form:input path="gender" id="genderSelect" type="text" class="form-control border-2" readonly="true" />
 				                        </div>
                                </div>

										<%-- 5. 소속 부서 --%>
										<div class="col-md-6">
										    <label class="form-label fw-semibold text-dark" for="stfDeptCd"> <span class="text-danger fw-bold">*</span>소속 부서</label>

										    <form:select path="staffDeptInfo.stfDeptCd" class="form-select">
										        <form:option value="">-- 선택 --</form:option>

										        <form:options items="${staffDeptList}" itemValue="stfDeptCd" itemLabel="stfDeptName"/>

										    </form:select>
										</div>

									</div>
								</div>

								<%-- 사진 영역 (파일 업로드 필드로 변경) --%>
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
					<h4
						class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">연락 및 인적정보
						</h4>
					<div class="card shadow-sm border-0">
						<div class="card-body p-4">
							<div class="row g-4">

								<form:hidden path="userInfo.lastName" />
								<form:hidden path="userInfo.firstName" />

								<%-- 이메일 --%>
								<div class="col-md-12">
									<label class="form-label fw-semibold text-dark"><span class="text-danger fw-bold">*</span>이메일</label>
									<form:input path="userInfo.email" type="email" class="form-control" />
								</div>


								<%-- 휴대전화 --%>
								<div class="col-md-6">
									<label class="form-label fw-semibold text-dark">
										 <span class="text-danger fw-bold">*</span>휴대전화
									</label>
									<form:input path="userInfo.mobileNo" class="form-control" placeholder="010-XXXX-XXXX"  />
								</div>

                                <%-- 직통 전화 --%>
								<div class="col-md-6">
									<label class="form-label fw-semibold text-dark">
										 <span class="text-danger fw-bold">*</span>직통 전화번호
									</label>
									<form:input path="staffInfo.teleNo" class="form-control" placeholder="042-XXX-XXXX" />
								</div>
								<%-- 우편번호 (주소 검색 버튼 필요) --%>
								 <div class="col-md-6">
					                        <label for="zipCode" class="form-label fw-semibold text-dark">
					                            <span class="text-danger fw-bold">*</span> 우편번호
					                        </label>

					                        <div class="d-flex gap-2">
					                            <form:input path="addressInfo.zipCode" id="postcode" class="form-control border-2"  />
					                            <button type="button" class="btn btn-primary me-2" id="zipbtn">검색</button>
					                        </div>
					                    </div>

								<%-- 기본 주소 --%>
								<div class="col-12">
				                        <label for="baseAddr" class="form-label fw-semibold text-dark">
				                            <span class="text-danger fw-bold">*</span> 주소
				                        </label>
				                        <form:input path="addressInfo.baseAddr" id="add1" class="form-control border-2" />
				                    </div>



								<%-- 상세 주소 --%>
								<div class="col-12">
				                        <label for="detailAddr" class="form-label fw-semibold text-dark">
				                            <span class="text-danger fw-bold">*</span> 상세주소
				                        </label>
				                        <form:input path="addressInfo.detailAddr" id="add2" class="form-control border-2" />
				                    </div>

								<%-- 은행 구분 --%>
								<div class="col-md-6">
                                <label for="bankCode" class="form-label fw-semibold text-dark">
                                    <span class="text-danger fw-bold">*</span> 은행구분
                                </label>
                                <form:select path="userInfo.bankCode" class="form-control">
				                  <option value="">선택</option>
				                  <c:forEach var="bank" items="${bankList}">
				                    <option value="${bank.commonCd}" <c:if test="${bank.commonCd eq userBankCd}">selected</c:if>>
				                      ${bank.cdName}
				                </option>
			                  </c:forEach>
			                </form:select>
			              </div>

								<%-- 예금주 --%>
                             <div class="col-md-6">
									<label class="form-label fw-semibold text-dark">예금주 (이름과 동일)</label>
									<div class="form-control info-field" id="depositor-name">
										<span id="full-name-display"></span>
									</div>
								</div>

							<%-- 계좌번호 --%>
								 <div class="col-md-6">
                                <label for="bankAccount" class="form-label fw-semibold text-dark">
                                    <span class="text-danger fw-bold">*</span> 계좌번호
                                </label>
                                <form:input path="userInfo.bankAccount" id="bankAccount" class="form-control border-2"/>
                            </div>


							<div class="text-end mt-4">
								<button type="submit" class="btn btn-primary me-2">등록</button>
                                <c:url var="listUrl" value="/lms/staffs/list" />
								<a href="${listUrl}" class="btn btn-outline-secondary">목록</a>
							</div>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>

     <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
  <script src="<c:url value='/js/app/staff/staffInfo.js' />"></script>
  <script src="<c:url value='/js/app/staff/photoFileAndDepositorName.js' />"></script>
  <script src="<c:url value='/js/app/staff/updateGenderFromRegiNo.js' />"></script>
  <script src="<c:url value='/js/app/staff/searchUI.js' />"></script>

<script>

    const HIDDEN_LAST_NAME_ID = 'userInfo.lastName';
    const HIDDEN_FIRST_NAME_ID = 'userInfo.firstName';
    const HIDDEN_GENDER_ID = 'hiddenGender';


    function updateDepositorName() {
        const userNameInput = document.getElementById('userName');
        const fullNameDisplay = document.getElementById('full-name-display');

        // JSP가 생성하는 실제 ID를 참조합니다.
        const lastNameInput = document.getElementById(HIDDEN_LAST_NAME_ID);
        const firstNameInput = document.getElementById(HIDDEN_FIRST_NAME_ID);

        // 필수 요소가 없으면 종료
        if (!userNameInput || !fullNameDisplay || !lastNameInput || !firstNameInput) {
            return;
        }

        // 1. 값 추출 및 공백 제거
        // 사용자가 띄어쓰기 없이 입력하도록 유도하고, 입력된 값의 공백을 제거합니다.
        const fullName = userNameInput.value.trim().replace(/\s/g, '') || '';

        // 2. 예금주 표시 영역에 설정 (화면에 표시)
        fullNameDisplay.textContent = fullName;

        // 3. DTO 바인딩을 위해 성/이름을 분리하여 숨겨진 필드에 설정
        // (첫 글자를 성으로, 나머지를 이름으로 가정하여 서버의 DTO 유효성 검사 통과 목적)
        if (fullName.length > 0) {
            // 첫 글자를 성으로 사용 (예: 홍)
            lastNameInput.value = fullName.charAt(0);
            // 나머지 글자를 이름으로 사용 (예: 길동)
            firstNameInput.value = fullName.substring(1);
        } else {
            lastNameInput.value = '';
            firstNameInput.value = '';
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        // 1. 이름 필드 이벤트 연결 (예금주 자동 설정)
        const userNameInput = document.getElementById('userName');
        if (userNameInput) {
            userNameInput.addEventListener('input', updateDepositorName);
            updateDepositorName(); // 페이지 로드 시 초기값 설정
        }
    });
</script>

</body>
</html>