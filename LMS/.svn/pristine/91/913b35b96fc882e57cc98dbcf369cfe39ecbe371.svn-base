
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!--
   * == 개정이력(Modification Information) ==
   *
   *   수정일      	수정자           	수정내용
   *  ============   ============== =======================
   *  2025. 9. 25.     정태일            최초 생성
   *  2025. 9. 25.		김수현			회원 정보 넣기
  -->

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>학생 개인신상 정보</title>
<!-- <link rel="stylesheet" href="personal_info.css"> -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentInfo.css" />
</head>
<body>
	<%@ include
		file="/WEB-INF/views/user/common/userNotificationScript.jspf"%>

	<!-- 외부 래퍼 -->
	<div class="student-personal-info-page">
		<div class="student-info-container">

			<!-- 페이지 헤더 -->
			<div class="page-header">
				<h1>개인정보</h1>
			</div>

			<!-- 학적정보 섹션 -->
			<div class="info-card">
				<div class="info-card-header">
					<h2 class="info-card-title">학적정보</h2>
				</div>
				<div class="info-card-body">
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
								<div class="stu-item-value">${student.regiNo}</div>
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
								<div class="stu-item-value">${student.gradYear}</div>
							</div>

							<div class="stu-item-wrapper">
								<div class="stu-item-label">졸업예정일</div>
								<div class="stu-item-value">${student.expectedYeartermCd }</div>
							</div>

						</div>
						<div class="info-photo">
							<c:choose>
								<c:when test="${not empty student.photoId}">
									<c:url var="photoUrl" value="/lms/student/info/photo" />
									<img src="${photoUrl}" alt="증명사진" class="photo-img">
								</c:when>
								<c:otherwise>
									<div class="photo-placeholder">
										<i class="bx bx-user"></i> <span>증명사진 없음</span>
									</div>
								</c:otherwise>
							</c:choose>
							<div class="photo-name">${student.lastName}${student.firstName}</div>
						</div>
					</div>
				</div>
			</div>

			<!-- 인적정보 섹션 -->
			<div class="info-card">
				<div class="info-card-header">
					<h2 class="info-card-title">인적정보</h2>
				</div>
				<div class="info-card-body">
					<form id="info-form" action="/lms/student/info/modify"
						method="post">
						<input type="hidden" name="userId" value="${student.userId}" /> <input
							type="hidden" name="studentNo" value="${student.studentNo}" /> <input
							type="hidden" name="addrId" value="${student.addrId}" />

						<div class="form-grid">
							<div class="form-item">
								<label class="form-label"> <span class="required">*</span>
									이름(한글/영문)
								</label> <input type="text" name="name" class="form-input"
									value="${student.lastName} ${student.firstName} / ${student.engLname} ${student.engFname}"
									required>
							</div>

							<div class="form-item">
								<label class="form-label">이메일</label> <input type="email"
									name="email" class="form-input" value="${student.email}">
							</div>

							<div class="form-item">
								<label class="form-label"><span class="required">*</span>
									휴대전화</label> <input type="tel" name="mobileNo" class="form-input"
									value="${student.mobileNo}" required>
							</div>

							<div class="form-item">
								<label class="form-label"><span class="required">*</span>
									우편번호</label>
								<div class="input-with-button">
									<input type="text" id="postcode" name="zipCode"
										class="form-input" value="${student.zipCode}" required>
									<button type="button" id="zipbtn" class="btn-search">주소검색</button>
								</div>
							</div>

							<div class="form-item form-item-full">
								<label class="form-label"><span class="required">*</span>
									주소</label> <input type="text" id="baseAddr" name="baseAddr"
									class="form-input" value="${student.baseAddr}" required>
							</div>

							<div class="form-item form-item-full">
								<label class="form-label"><span class="required">*</span>
									상세주소</label> <input type="text" name="detailAddr" class="form-input"
									value="${student.detailAddr}" required>
							</div>

							<div class="form-item">
								<label class="form-label">비상연락처</label> <input type="tel"
									name="emergency_contact" class="form-input" value="">
							</div>

							<div class="form-item">
								<label class="form-label"><span class="required">*</span>
									은행구분</label> <select name="bankCode" class="form-select" required>
									<option value="">선택하세요</option>
									<c:forEach var="bank" items="${bankList}">
										<option value="${bank.commonCd}"
											<c:if test="${bank.commonCd eq student.bankCode}">selected</c:if>>
											${bank.cdName}</option>
									</c:forEach>
								</select>
							</div>

							<div class="form-item">
								<label class="form-label"><span class="required">*</span>
									계좌번호</label> <input type="text" name="bankAccount" class="form-input"
									value="${student.bankAccount}" required>
							</div>

							<div class="form-item">
								<label class="form-label"><span class="required">*</span>
									예금주</label> <input type="text" name="accountHolder" class="form-input"
									value="${student.lastName}${student.firstName}" required>
							</div>

							<div class="form-item">
								<label class="form-label">병역구분</label> <input type="text"
									name="militaryTypeName" class="form-input"
									value="${student.militaryTypeName}">
							</div>

						</div>

						<div class="form-actions">
							<button type="submit" class="btn-submit">저장</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- 스크립트 -->
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
	<script
		src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
	<script src="<c:url value='/js/app/staff/staffInfo.js' />"></script>

</body>
</html>