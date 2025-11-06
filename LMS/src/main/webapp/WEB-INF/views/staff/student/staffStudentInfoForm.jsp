<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>학생 개인신상 수정/등록</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
</head>
<body class="bg-light">
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
	<div class="container-fluid" style="max-width: 1200px;">
		<div class="py-4">
			<h1 class="text-center text-dark mb-5 fw-semibold">학생 개인신상 등록
				페이지</h1>

            <button type="button" class="btn btn-warning btn-sm mb-3" id="fillTestBtn" style="float: right;">
                📋 테스트 데이터 자동 입력
            </button>
            <div style="clear: both;"></div>

            <form:form id="info-form" action="create" method="POST" modelAttribute="student" enctype="multipart/form-data">

                <div class="mb-5">
                    <h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">학적정보</h4>

                    <div class="card shadow-sm border-0">
                        <div class="card-body p-4">
                            <div class="row">
                                <div class="col-lg-9">
                                    <div class="row g-3">

                                        <div class="col-md-6">
                                            <div class="fw-bold text-dark mb-1"><span class="text-danger fw-bold">*</span> 이름</div>
                                            <input name="studentName" id="userName" type="text" class="form-control border-2" placeholder="예: 홍길동 (띄어쓰기 없이 입력 권장)">
                                        </div>
                                            <form:hidden path="lastName" id="lastName" />
                                            <form:hidden path="firstName" id="firstName" />

                                        <div class="col-md-6">
                                            <label class="form-label fw-semibold text-dark">학번</label>
                                                <form:input path="studentNo" class="form-control" readonly="true" placeholder="등록 시 자동 생성" />
                                        </div>
                                        <div class="col-md-6">
                                            <div class="fw-bold text-dark mb-1"><span class="text-danger fw-bold">*</span> 주민번호</div>
                                            <form:input path="regiNo" id="regiNo" type="text" class="form-control border-2" placeholder="예: 9012311234567" maxlength="14" />
                                        </div>
                                        <div class="col-md-6">
                                            <div class="fw-bold text-dark mb-1">성별</div>
                                            <div class="text-dark-emphasis">
                                             <form:input path="gender" id="gender" type="text" class="form-control border-2" readonly="true" />
                                            </div>
                                        </div>

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
                                            <label for="univDeptCd" class="form-label fw-semibold text-dark"><span class="text-danger fw-bold">*</span> 소속학과</label>
                                            <select name="univDeptCd" id="deptSelect" class="form-control"> <option value="" data-college-cd="">선택</option>
                                                <c:forEach var="univDept" items="${univDeptList}">
                                                    <option
                                                        value="${univDept.univDeptCd}"
                                                        data-college-cd="${univDept.collegeCd}"
                                                        <c:if test="${univDept.univDeptCd eq univDeptName}">selected</c:if>
                                                    >
                                                      ${univDept.univDeptName}
                                                </option>
                                              </c:forEach>
                                           </select>
                                        </div>


                                        <div class="col-md-6">
                                            <label for="gradeCd" class="form-label fw-semibold text-dark"><span class="text-danger fw-bold">*</span> 학년</label>
                                            <select name="gradeCd" class="form-control">
                                                <option value="">선택</option>
                                                <c:forEach var="grade" items="${gradeList}">
                                            <option value="${grade.commonCd}" <c:if test="${grade.commonCd eq gradeName}">selected</c:if>>
                                              ${grade.cdName}
                                        </option>
                                      </c:forEach>
                                       </select>

                                        </div>


                                        <div class="col-md-6">
                                            <label for="stuStatusCd" class="form-label fw-semibold text-dark"><span class="text-danger fw-bold">*</span> 학적상태</label>
                                               <select name="stuStatusCd" class="form-control">
                                                    <option value="">선택</option>
                                                <c:forEach var="status" items="${statusList}">
                                            <option value="${status.commonCd}" <c:if test="${status.commonCd eq userstatusCd}">selected</c:if>>
                                              ${status.cdName}
                                        </option>
                                      </c:forEach>
                                       </select>
                                        </div>

                                        <div class="col-md-6">
                                                <div class="fw-bold text-dark mb-1"><span class="text-danger fw-bold">*</span> 입학구분</div>
                                        <select name="entranceTypeCd" class="form-control">
                                                <option value="">선택</option>
                                                <c:forEach var="entranceType" items="${entranceTypeList}">
                                            <option value="${entranceType.commonCd}" <c:if test="${entranceType.commonCd eq entranceTypeName}">selected</c:if>>
                                              ${entranceType.cdName}
                                        </option>
                                      </c:forEach>
                                       </select>

                                        </div>
                                <div class="col-md-6">
                                        <label for="entranceDate" class="form-label fw-semibold text-dark">
                                            <span class="text-danger fw-bold">*</span> 입학일
                                        </label>
                                        <form:input path="entranceDate" id="entranceDate" type="date" class="form-control border-2" />
                                    </div>

                                    <div class="col-md-6">
                                        <label for="gradYear" class="form-label fw-semibold text-dark">졸업 연도</label>
                                        <form:input path="expectedYeartermCd" id="expectedYeartermCd" type="text" class="form-control border-2" readonly="true" />
                                    </div>



                                    <form:hidden path="gradExamYn" />

                                    </div>
                                </div>
                                <%-- 사진 영역 및 업로드 --%>
                                    <div class="col-lg-3">
							    <div class="text-center">
							        <c:choose>
							            <c:when test="${not empty student.photoId}">
							                <c:url var="photoUrl" value="/devtemp/files/idphoto">
							                    <c:param name="fileId" value="${student.photoId}"/>
							                </c:url>
							                <img src="${photoUrl}"
							                     alt="${student.lastName}${student.firstName} 증명사진"
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
							        <div class="fw-bold text-dark mt-2">${student.lastName}${student.firstName}</div>
							    </div>
							</div>
                        </div>
                    </div>
                </div>

                <div class="mb-5">
                    <h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">인적정보</h4>
                    <div class="card shadow-sm border-0">
                        <div class="card-body p-4">

                            <div class="row g-4">
                                <div class="col-md-6">
                                    <label for="engLname" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 이름(성 영문)
                                    </label>
                                    <form:input path="engLname" id="engLname" type="text" class="form-control border-2" />

                                </div>
                                <div class="col-md-6">
                                    <label for="engFname" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 이름(이름 영문)
                                    </label>
                                    <form:input path="engFname" id="engFname" type="text" class="form-control border-2" />

                                </div>

                                <div class="col-md-6">
                                    <label for="email" class="form-label fw-semibold text-dark"> <span class="text-danger fw-bold">*</span>이메일</label>
                                    <form:input path="email" id="email" type="email" class="form-control border-2"/>
                                </div>
                                <div class="col-md-6">
                                    <label for="guardPhone" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 비상연락처 (보호자)
                                    </label>
                                    <form:input path="guardPhone" id="guardPhone" class="form-control border-2" />
                                </div>

                                 <div class="col-md-6">
                                    <label for="zipCode" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 우편번호
                                    </label>

                                    <div class="d-flex gap-2">
                                        <form:input path="zipCode" id="postcode" class="form-control border-2"  />
                                        <button type="button" class="btn btn-primary me-2" id="zipbtn">검색</button>
                                    </div>
                                </div>

                                <div class="col-md-6">
                                    <label for="mobileNo" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 휴대전화
                                    </label>
                                    <form:input path="mobileNo" id="mobileNo" class="form-control border-2"/>
                                </div>

                                <div class="col-12">
                                    <label for="baseAddr" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 주소
                                    </label>
                                    <form:input path="baseAddr" id="add1" class="form-control border-2" />
                                </div>

                                <div class="col-12">
                                    <label for="detailAddr" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 상세주소
                                    </label>
                                    <form:input path="detailAddr" id="add2" class="form-control border-2" />
                                </div>


                                <div class="col-md-6">
                                    <label for="bankCode" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 은행구분
                                    </label>
                                    <form:select path="bankCode" class="form-control">
                                        <option value="">선택</option>
                                      <c:forEach var="bank" items="${bankList}">
                                        <option value="${bank.commonCd}" <c:if test="${bank.commonCd eq userBankCd}">selected</c:if>>
                                          ${bank.cdName}
                                    </option>
                                  </c:forEach>
                                </form:select>
                              </div>

                                <div class="col-md-6">
                                    <label for="bankAccount" class="form-label fw-semibold text-dark">
                                        <span class="text-danger fw-bold">*</span> 계좌번호
                                    </label>
                                    <form:input path="bankAccount" id="bankAccount" class="form-control border-2"/>
                                </div>

                                <div class="col-md-6">
                                        <label class="form-label fw-semibold text-dark">예금주 (이름과 동일)</label>
                                        <div class="form-control info-field" id="depositor-name">
                                            <span id="depositor-name-display"></span>
                                        </div>
                                    </div>

                                <div class="col-md-6">
                                    <label for="targetDept" class="form-label fw-semibold text-dark">복수전공</label>
                                    <form:input path="targetDept" id="targetDept" class="form-control border-2"
                                           placeholder="복수전공 학과명 (선택 사항)"/>
                                </div>

                            <div class="text-end mt-4">
                                <button type="submit" class="btn btn-primary me-2">등록</button>
                                <c:url var="listUrl" value="/lms/students/list" />
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
  <script src="<c:url value='/js/app/staff/staffstudentInfo.js' />"></script>
  <script src="<c:url value='/js/app/staff/photoFileAndDepositorName.js' />"></script>
  <script src="<c:url value='/js/app/staff/updateGenderFromRegiNo.js' />"></script>
  <script src="<c:url value='/js/app/staff/searchUI.js' />"></script>


</body>

</html>