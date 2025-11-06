<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>학생 개인신상 정보 수정 (교직원)</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentInfo.css" />
</head>

<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<div class="student-personal-info-page">
		<div class="student-info-container">
<div class="student-info-container">
    <h1 class="info-page-title">학생 개인신상 정보</h1>

    <form id="info-form" action="<c:url value='/lms/staff/students/modify/${student.studentNo}'/>" method="post">
<ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="/staff">Home</a></li>
        <li class="breadcrumb-item">인사업무</li>
        <li class="breadcrumb-item">
            <a href="/lms/staff/students/">
               학생 목록
            </a>
        </li>
        <li class="breadcrumb-item">
             <a href="/lms/staff/students/${student.studentNo }">
               학생 상세 정보
            </a>
        </li>
        <li class="breadcrumb-item active" aria-current="page">학생 정보 수정</li>
    </ol>
        <%-- 필수 hidden 필드 --%>
        <input type="hidden" name="userId" value="${student.userId}"/>
        <input type="hidden" name="studentNo" value="${student.studentNo}"/>
        <input type="hidden" name="addrId" value="${student.addrId}"/>

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
                        <div class="stu-item-value">${student.lastName}${student.firstName} / ${student.engLname} ${student.engFname}</div>
                    </div>

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label">주민번호</div>
                            <div class="stu-item-value">
                                <c:choose>
                                    <c:when test="${not empty student.regiNo and fn:length(student.regiNo) ge 7}">
                                        ${fn:substring(student.regiNo, 0, 6)}-${fn:substring(student.regiNo, 6, 7)}******
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
                                <c:choose>
                                    <c:when test="${student.gender == 2 or student.gender == 4}">여자</c:when>
                                    <c:otherwise>남자</c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label"><span class="required">*</span>단과대학</div>
                            <select name="collegeCd" id="collegeSelect" class="form-control">
                                <option value="">선택</option>
                                <c:forEach var="college" items="${collegeList}">
                                    <option value="${college.collegeCd}"
                                            <c:if test="${college.collegeCd eq student.collegeCd}">selected</c:if>>
                                        ${college.collegeName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label"><span class="required">*</span>소속학과</div>
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

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label"><span class="required">*</span>학년</div>
                            <select name="gradeCd" class="form-control">
                                <option value="">선택</option>
                                <c:forEach var="grade" items="${gradeList}">
                                    <option value="${grade.commonCd}"
                                            <c:if test="${grade.commonCd eq student.gradeCd}">selected</c:if>>
                                        ${grade.cdName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label">복수전공</div>
                              <input type="affilChagneCd" name="affilChagneCd" class="form-input" value="-">
                        </div>

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label"><span class="text-danger fw-bold">*</span> 학적상태</div>
                            <select name="stuStatusCd" class="form-control">
                                <option value="">선택</option>
                                <c:forEach var="status" items="${statusList}">
                                    <option value="${status.commonCd}"
                                            <c:if test="${status.commonCd eq student.stuStatusCd}">selected</c:if>>
                                        ${status.cdName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label">입학구분</div>
                            <div class="stu-item-value">${student.entranceTypeName}</div>
                        </div>

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label">입학년도</div>
                            <div class="stu-item-value">${student.entranceDate}</div>
                        </div>

                        <div class="stu-item-wrapper">
                            <div class="stu-item-label">졸업예정일</div>
                            <div class="stu-item-value">${student.expectedYeartermCd }</div>
                        </div>

                    </div>
                    <div class="info-photo">
                        <c:choose>
                            <c:when test="${not empty student.photoId}">
                                <c:url var="photoUrl" value="/lms/student/info/photo"/>
                                <img src="${photoUrl}" alt="증명사진" class="photo-img">
                            </c:when>
                            <c:otherwise>
                                <div class="photo-placeholder">
                                    <i class="bx bx-user"></i>
                                    <span>증명사진 없음</span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div class="photo-name">${student.lastName}${student.firstName}</div>
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
                    <div class="form-item">
                        <label class="form-label">
                            <span class="required">*</span> 이름(한글/영문)
                        </label>
                        <input type="text" name="name" class="form-input"
                                value="${student.lastName} ${student.firstName} / ${student.engLname} ${student.engFname}" required>
                    </div>

                    <div class="form-item">
                        <label class="form-label"><span class="required">*</span>이메일</label>
                        <input type="email" name="email" class="form-input" value="${student.email}">
                    </div>

                    <div class="form-item">
                        <label class="form-label"><span class="required">*</span> 휴대전화</label>
                        <input type="tel" name="mobileNo" class="form-input" value="${student.mobileNo}" required>
                    </div>

                    <div class="form-item">
                        <label class="form-label"><span class="required">*</span> 우편번호</label>
                        <div class="input-with-button">
                            <input type="text" id="postcode" name="zipCode" class="form-input" value="${student.zipCode}" required>
                            <button type="button" id="zipbtn" class="btn-search">주소검색</button>
                        </div>
                    </div>

                    <div class="form-item">
                        <label class="form-label"><span class="required">*</span> 주소</label>
                        <input type="text" id="baseAddr" name="baseAddr" class="form-input" value="${student.baseAddr}" required>
                    </div>

                    <div class="form-item">
                        <label class="form-label"><span class="required">*</span> 상세주소</label>
                        <input type="text" name="detailAddr" class="form-input" value="${student.detailAddr}" required>
                    </div>
					<input type="hidden" name="professorId" id="professorIdInput" value="${student.professorId}"/>

						<div class="form-item">
						    <label class="form-label">지도교수</label>
						    <div class="input-with-button">
						        <input type="text"
						               id="professorNameDisplay"
						               class="form-input"
						               value="${student.professorName}"
						               readonly>

						        <button type="button" id="professorSearchBtn" class="btn-search">교수검색</button>
						    </div>
						</div>

                    <div class="form-item">
						    <label class="form-label">비상연락처 (보호자 연락처)</label>
						    <input type="tel" name="guardPhone" class="form-input" value="${student.guardPhone}">
						</div>


                    <div class="form-item">
                        <label class="form-label"><span class="required">*</span> 은행구분</label>
                        <select name="bankCode" class="form-select" required>
                            <option value="">선택하세요</option>
                            <c:forEach var="bank" items="${bankList}">
                                <option value="${bank.commonCd}"
                                        <c:if test="${bank.commonCd eq student.bankCode}">selected</c:if>>
                                    ${bank.cdName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-item">
                        <label class="form-label"><span class="required">*</span> 계좌번호</label>
                        <input type="account_number" name="bankAccount" class="form-input" value="${student.bankAccount}" >


                    </div>

                    <div class="form-item">
                        <label class="form-label"><span class="required">*</span> 예금주</label>
                        <input type="text" name="studentName" class="form-input" value="${student.studentName}">
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-submit">저장</button>
                     <c:url var="detailUrl" value="/lms/staff/students/${student.studentNo}" />
                                <a href="${detailUrl}" class="btn btn-outline-secondary">취소</a>

                </div>
            </div>
        </div>
    </form>
</div>

<div class="modal fade" id="professorSearchModal" tabindex="-1" aria-labelledby="professorSearchModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="professorSearchModalLabel">지도교수 지정 (학과 목록)</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">

          <p class="text-muted mb-3">
              현재 학생 소속 학과(<span id="currentDeptNameDisplay"></span>)의 재직 교수 전체 목록입니다.
          </p>

          <table class="table table-bordered table-hover">
              <thead class="table-light">
                  <tr>
                      <th style="width: 20%;">ID</th>
                      <th>이름</th>
                      <th style="width: 30%;">소속학과</th>
                  </tr>
              </thead>
              <tbody id="professor-search-results">
                  <tr><td colspan="3" class="text-center text-muted">교수 목록을 로드 중입니다.</td></tr>
              </tbody>
          </table>

      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
      </div>
    </div>
  </div>
</div>
</div>
</div>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src="<c:url value='/js/app/staff/staffInfo.js' />"></script>
<script src="<c:url value='/js/app/staff/staffInfoList.js' />"></script>

</body>
</html>