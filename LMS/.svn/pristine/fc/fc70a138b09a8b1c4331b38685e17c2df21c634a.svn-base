<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <title>LMS 교직원 포털 - 내 정보 수정</title>
 <!--  <style>
    /* 1. 전체 페이지 상하 패딩/마진 극단적 감소 */
    body {
        padding-top: 5px;
        font-size: 0.9rem;
    }
    .py-4 {
        padding-top: 0.5rem !important;
        padding-bottom: 0.5rem !important;
    }
    .container-fluid {
        padding: 0 5px;
    }
    /* 2. 섹션 제목 및 간격 축소 */
    h1 {
        font-size: 1.75rem;
        margin-bottom: 0.5rem !important;
    }
    h4 {
        font-size: 1.1rem;
        margin-bottom: 0.5rem !important;
    }
    .mb-5 {
        margin-bottom: 1rem !important; /* 섹션 마진 축소 */
    }
    .mb-3 {
        margin-bottom: 0.5rem !important;
    }
    .pb-2 {
        padding-bottom: 0.3rem !important;
    }

    /* 3. 카드 및 그리드 간격 최소화 */
    .card-body {
        padding: 0.75rem !important; /* 카드 내부 패딩 축소 */
    }
    .row.g-3, .row.g-4 {
        --bs-gutter-x: 0.5rem;
        --bs-gutter-y: 0.5rem; /* 수직 간격 최소화 */
    }

    /* 4. 정보 필드 (읽기 전용) 내부 간격 최소화 */
    .info-field {
        background-color: #f8f9fa !important;
        border: 1px solid #e9ecef !important;
        padding: 0.5rem !important;
    }
    /* 읽기 전용 필드의 텍스트 간격 */
    .text-dark-emphasis {
        font-size: 0.9rem;
        padding-top: 0.1rem;
        padding-bottom: 0.1rem;
    }
    /* 5. 입력 필드 및 레이블 간격/크기 최소화 */
    .form-label {
        font-weight: 600;
        color: #343a40;
        margin-bottom: 0.1rem !important; /* 레이블 아래 마진 최소화 */
        font-size: 0.85rem;
    }
    .form-control, .form-select {
        height: calc(1.5em + 0.5rem + 2px); /* 입력 필드 높이 축소 (기본 1.5em + 0.75rem + 2px) */
        padding: 0.25rem 0.5rem; /* 입력 필드 내부 패딩 축소 */
        font-size: 0.9rem;
    }
    .input-group > .form-control, .input-group > .btn {
        height: calc(1.5em + 0.5rem + 2px); /* 입력 그룹 내부 버튼/필드 높이 일치 */
        padding: 0.25rem 0.5rem;
        font-size: 0.9rem;
    }

    /* 6. 프로필 사진 영역 크기 및 마진 축소 */
    .col-lg-3 .text-center img {
        width: 120px !important;
        height: 150px !important;
        padding: 0.5rem !important;
        margin-bottom: 0.5rem !important;
    }
    .col-lg-3 .text-center .form-control {
        margin-top: 0.3rem !important; /* 파일 입력 필드 상단 마진 */
    }
    .col-lg-3 .text-center div.fw-bold {
        font-size: 0.9rem;
        margin-top: 0.3rem !important; /* 이름 필드 상단 마진 */
    }

    /* 버튼 마진 축소 */
    .text-end.mt-4 {
        margin-top: 0.5rem !important;
    }

    /* 구분선 마진 축소 */
    .col-12 hr {
        margin-top: 0.5rem !important;
        margin-bottom: 0.5rem !important;
    }
  </style> -->
</head>
<body class="bg-light">
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
	<div class="container-fluid" style="max-width: 1200px;">
		<div class="py-4">
			<h1 class="text-center text-dark mb-5 fw-semibold">내 정보 관리 수정 페이지</h1>
			<nav aria-label="breadcrumb">
			    <ol class="breadcrumb">
			        <li class="breadcrumb-item"><a href="/staff">Home</a></li>
			        <li class="breadcrumb-item">개인정보</li>
			        <li class="breadcrumb-item"><a href="/lms/staff/mypage">내 정보</a></li>
			        <li class="breadcrumb-item active" aria-current="page">수정</li>
			    </ol>
</nav>
<form id="staffModifyForm" action="/lms/staff/mypage/modify" method="post" enctype="multipart/form-data">
    <%-- 서버로 전송할 UserID와 AddrID를 hidden 필드에 추가 --%>
    <input type="hidden" name="userInfo.userId" value="${userStaffDTO.userInfo.userId}" />
    <input type="hidden" name="addressInfo.addrId" value="${userStaffDTO.addressInfo.addrId}" />
    <input type="hidden" name="staffInfo.staffNo" value="${userStaffDTO.staffInfo.staffNo}" />

			<div class="mb-5">
				<h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">기본정보</h4>
				<div class="card shadow-sm border-0">
					<div class="card-body p-4">
						<div class="row">
							<div class="col-lg-9">
								<div class="row g-3">

									<%-- 1. 사번 (읽기 전용) --%>
									<div class="col-md-6">
										<div class="border rounded p-3 bg-light info-field">
											<div class="fw-bold text-dark mb-0 me-3">사번</div>
											<div class="text-dark-emphasis">${userStaffDTO.staffInfo.staffNo}</div>
										</div>
									</div>

									<%-- 2. 이름 (읽기 전용) --%>
									<div class="col-md-6">
										<div class="border rounded p-3 bg-light info-field">
											<div class="fw-bold text-dark mb-0 me-3">이름</div>
											<div class="text-dark-emphasis">${userStaffDTO.userInfo.lastName}${userStaffDTO.userInfo.firstName}</div>
										</div>
									</div>

									<%-- 3. 주민번호 (읽기 전용, 마스킹) --%>
									<div class="col-md-6">
									    <div class="border rounded p-3 bg-light info-field">
									        <div class="fw-bold text-dark mb-0 me-3">주민번호</div>
									        <div id="regiNo" class="text-dark-emphasis">
									             <c:choose>
								                    <c:when test="${not empty userStaffDTO.userInfo.regiNo and fn:length(userStaffDTO.userInfo.regiNo) ge 7}">
								                        ${fn:substring(userStaffDTO.userInfo.regiNo, 0, 6)}-${fn:substring(userStaffDTO.userInfo.regiNo, 6, 7)}******
								                    </c:when>
								                    <c:otherwise>
								                        정보 없음
								                    </c:otherwise>
								                </c:choose>
									        </div>
									    </div>
									</div>

									<%-- 4. 성별 (읽기 전용) --%>
									<div class="col-md-6">
									    <div class="border rounded p-3 bg-light info-field">
									        <div class="fw-bold text-dark mb-0 me-3">성별</div>
									        <div id="genderSelect" class="text-dark-emphasis">
									            <c:set var="genderCode" value="${fn:substring(userStaffDTO.userInfo.regiNo, 6, 7)}" />
									            <c:choose>
									                <c:when test="${genderCode eq '1' or genderCode eq '3' or genderCode eq '5'}"> 남성 </c:when>
									                <c:when test="${genderCode eq '2' or genderCode eq '4' or genderCode eq '6'}"> 여성 </c:when>
									                <c:otherwise> 정보 없음 </c:otherwise>
									            </c:choose>
									        </div>
									    </div>
									</div>

									<%-- 5. 소속 부서 (읽기 전용) --%>
									<div class="col-md-6">
							            <div class="border rounded p-3 bg-light info-field">
							              <div class="fw-bold text-dark mb-0 me-3">소속부서</div>
							                <div class="text-dark-emphasis">${userStaffDTO.staffDeptInfo.stfDeptName}</div>
							              </div>
									</div>

									<%-- 6. 학내일반전화 (읽기 전용) --%>
									<div class="col-md-6">
							            <div class="border rounded p-3 bg-light info-field">
							              <div class="fw-bold text-dark mb-0 me-3">학내일반전화</div>
							                <div class="text-dark-emphasis">${userStaffDTO.staffInfo.teleNo}</div>
							              </div>
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
						                     class="border border-2 border-secondary rounded p-4 mb-3 bg-light"
						                     style="width: 200px; height: 240px; margin: 0 auto; object-fit: cover;">

						            </c:when>
							            <c:otherwise>
							                <div class="border border-2 border-secondary rounded p-4 mb-3 bg-light d-flex align-items-center justify-content-center" style="width: 120px; height: 150px; margin: 0 auto;">
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
				<h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">연락 및 금융 정보</h4>
				<div class="card shadow-sm border-0">
					<div class="card-body p-4">
						<div class="row g-3">

							<%-- 1. 이메일 (수정 없음) --%>
							<div class="col-md-6">
								<label for="email" class="form-label">이메일</label>
								<input type="email" name="userInfo.email" id="email" class="form-control" value="${userStaffDTO.userInfo.email}">
							</div>

							<%-- 2. 휴대전화 (★★★ name, maxlength 수정 ★★★) --%>
							<div class="col-md-6">
								<label for="mobileNo" class="form-label">휴대전화</label>
								<input type="text" name="userInfo.mobileNo" id="mobileNo" class="form-control"
                                       value="${userStaffDTO.userInfo.mobileNo}" maxlength="15">
							</div>

							<%-- 3. 우편번호/주소 검색 버튼 (수정 없음) --%>
							<div class="col-md-3">
    							<label for="postcode" class="form-label">우편번호</label>
   								<div class="input-group">
    								<input type="text" name="addressInfo.zipCode" id="postcode" class="form-control"
                                        value="${userStaffDTO.addressInfo.zipCode}" readonly="readonly" required="required" />
										<button type="button" class="btn btn-primary" id="zipbtn" onclick="execDaumPostcode()">검색</button>
								</div>
							</div>

							<%-- 4. 기본 주소 (수정 없음) --%>
							<div class="col-md-9">
								<label for="add1" class="form-label">주소</label>
								<input type="text" name="addressInfo.baseAddr" id="add1" class="form-control"
                                    value="${userStaffDTO.addressInfo.baseAddr}" readonly="readonly" required="required" />
							</div>

							<%-- 5. 상세 주소 (수정 없음) --%>
							<div class="col-12">
								<label for="add2" class="form-label">상세주소</label>
								<input type="text" name="addressInfo.detailAddr" id="add2" class="form-control" value="${userStaffDTO.addressInfo.detailAddr}">
							</div>

                            <%-- 금융 정보 섹션을 위해 row를 새로 시작 (수정 없음) --%>
                            <div class="col-12"><hr class="my-0 mt-2 mb-2"></div>


							<%-- 6. 예금주 (수정 없음) --%>
							<div class="col-md-3">
								<label class="form-label">예금주</label>
								<div class="form-control border-2 bg-light info-field">
									${userStaffDTO.userInfo.lastName}${userStaffDTO.userInfo.firstName}
								</div>
							</div>

							<%-- 7. 은행구분 (★★★ name 수정 ★★★) --%>
							<div class="col-md-3">
								<label for="bankCode" class="form-label">은행구분</label>
								<select name="userInfo.bankCode" id="bankCode" class="form-select">
									 <option value="">은행 선택</option>
									<c:forEach var="bank" items="${bankList}">
				                    <option value="${bank.commonCd}" <c:if test="${bank.commonCd eq userStaffDTO.userInfo.bankCode}">selected</c:if>>
				                      ${bank.cdName}
				                </option>
			                  </c:forEach>
			                </select>
							</div>

							<%-- 8. 계좌번호 (★★★ name 수정 ★★★) --%>
							<div class="col-md-6">
								<label for="bankAccount" class="form-label">계좌번호</label>
								<input type="text" name="userInfo.bankAccount" id="bankAccount" class="form-control"
                                       value="${userStaffDTO.userInfo.bankAccount}">
							</div>

						</div>

						<div class="text-end mt-4">
							<%-- 수정 버튼을 submit으로 변경하고 취소 버튼 추가 --%>
							<a href="<c:url value='/lms/staff/mypage'/>" class="btn btn-sm btn-outline-secondary me-2">취소</a>
							<button type="submit" class="btn btn-sm btn-primary">저장</button>
						</div>
					</div>
				</div>
			</div>
		</form>
		</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
    <script src="<c:url value='/js/app/staff/staffInfo.js' />"></script>
    <script src="<c:url value='/js/app/staff/photoFileAndDepositorName.js' />"></script>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('staffModifyForm');
            const mobileNoInput = document.getElementById('mobileNo');

            if (form) {
                form.addEventListener('submit', function(event) {
                    if (mobileNoInput) {
                        // 1. 입력된 값에서 모든 공백을 제거 (하이픈은 유지)
                        let rawMobileNo = mobileNoInput.value;
                        // 정규식: \s는 공백 문자(스페이스, 탭, 줄 바꿈)를 의미
                        let cleanMobileNo = rawMobileNo.replaceAll(/\s/g, '');

                        // 2. 제거된 값으로 입력 필드의 값을 갱신하여 서버로 전송
                        mobileNoInput.value = cleanMobileNo;

                        console.log("폼 제출 전 MobileNo 공백 제거 완료:", cleanMobileNo);
                    }
                    // 폼 전송을 계속 진행
                });
            }
        });
    </script>
</body>
</html>