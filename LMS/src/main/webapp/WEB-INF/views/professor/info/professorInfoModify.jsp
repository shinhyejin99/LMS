<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>LMS 교수 포털 - 내 정보 수정</title>
    <style>
        /* 1. 전체 페이지 상하 패딩/마진 극단적 감소 */
        body {
            padding-top: 5px; /* body 상단 패딩 */
            font-size: 0.9rem; /* 전체 글꼴 크기 약간 축소 */
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
            font-size: 1.75rem; /* H1 크기 축소 */
            margin-bottom: 0.5rem !important;
        }
        h4 {
            font-size: 1.1rem; /* H4 크기 축소 */
            margin-bottom: 0.5rem !important;
        }
        .mb-5 {
            margin-bottom: 1rem !important; /* mb-5 (3rem) -> 1rem으로 줄임 */
        }
        .mb-3 {
            margin-bottom: 0.5rem !important;
        }
        .pb-2 {
            padding-bottom: 0.3rem !important;
        }

        /* 3. 카드 및 그리드 간격 최소화 */
        .card-body {
            padding: 0.75rem !important; /* 카드 내부 패딩 p-4(1.5rem) -> 0.75rem */
        }
        .row.g-3, .row.g-4 {
            --bs-gutter-x: 0.5rem; /* 수평 간격 0.75rem -> 0.5rem */
            --bs-gutter-y: 0.5rem; /* 수직 간격 0.75rem -> 0.5rem */
        }

        /* 4. 정보 필드 내부 간격 최소화 */
        .info-field {
            padding: 0.5rem !important; /* p-2(0.5rem) -> 0.5rem 유지 */
        }
        .info-field-text {
            font-size: 0.9rem; /* 정보 텍스트 크기 축소 */
            padding-top: 0.1rem;
            padding-bottom: 0.1rem;
        }
        .form-label {
            margin-bottom: 0.1rem !important;
            font-size: 0.85rem; /* 레이블 텍스트 크기 축소 */
        }

        /* 5. 프로필 사진 크기 극단적 축소 */
        .col-lg-3 .text-center img {
            width: 120px !important;
            height: 150px !important;
            padding: 0.5rem !important;
            margin-bottom: 0.5rem !important;
        }
        .col-lg-3 .text-center div.fw-bold {
            font-size: 0.9rem;
        }
        .text-end.mt-3 {
            margin-top: 0.5rem !important;
        }
    </style>
</head>
<body class="bg-light">
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<div class="container-fluid" style="max-width: 1200px;">
    <div class="py-4">
        <h1 class="text-center text-dark mb-3 fw-semibold">교수 개인 정보 수정 페이지</h1>



        <form id="professor-info-modify-form" action="/lms/professor/info" method="post">
            <div class="mb-5">
                <h4 class="text-secondary fw-medium mb-3 pb-2 border-bottom border-2">인사정보</h4>
                <div class="card shadow-sm border-0">
                    <div class="card-body p-4">
                        <div class="row">
                            <div class="col-lg-9">
                                <div class="row g-3">

                                    <%-- 1. 교번 --%>
                                    <div class="col-md-6">
                                        <div class="border rounded p-2 bg-light info-field">
                                            <div class="fw-bold text-dark mb-0 me-3">교번</div>
                                            <div class="info-field-text">${professor.professorNo}</div>
                                        </div>
                                    </div>

                                    <%-- 2. 이름 --%>
                                    <div class="col-md-6">
                                        <div class="border rounded p-2 bg-light info-field">
                                            <div class="fw-bold text-dark mb-0 me-3">이름</div>
                                            <div class="info-field-text">${professor.lastName}${professor.firstName}</div>
                                        </div>
                                    </div>

                                    <%-- 3. 주민번호 (마스킹) --%>
                                    <div class="col-md-6">
                                        <div class="border rounded p-2 bg-light info-field">
                                            <div class="fw-bold text-dark mb-0 me-3">주민번호</div>
                                            <div id="regiNo" class="info-field-text">
                                                <c:choose>
                                                    <c:when test="${not empty professor.regiNo and fn:length(professor.regiNo) ge 7}">
                                                        ${fn:substring(professor.regiNo, 0, 6)}-${fn:substring(professor.regiNo, 6, 7)}******
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
                                                <c:set var="genderCode" value="${fn:substring(professor.regiNo, 6, 7)}" />
                                                <c:choose>
                                                    <c:when test="${genderCode eq '1' or genderCode eq '3' or genderCode eq '5'}"> 남성 </c:when>
                                                    <c:when test="${genderCode eq '2' or genderCode eq '4' or genderCode eq '6'}"> 여성 </c:when>
                                                    <c:otherwise> 정보 없음 </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>

                                    <%-- 5. 소속 학과 --%>
                                    <div class="col-md-6">
                                        <div class="border rounded p-2 bg-light info-field">
                                            <div class="fw-bold text-dark mb-0 me-3">소속 학과</div>
                                            <div class="info-field-text">${professor.departmentName}</div>
                                        </div>
                                    </div>

                                    <%-- 6. 연구실 전화번호 --%>
                                    <div class="col-md-6">
                                        <div class="border rounded p-2 bg-light info-field">
                                            <div class="fw-bold text-dark mb-0 me-3">연구실 전화번호</div>
                                            <div class="info-field-text">${professor.officeNo}</div>
                                        </div>
                                    </div>

                                    <%-- 7. 직급 --%>
                                    <div class="col-md-6">
                                        <div class="border rounded p-2 bg-light info-field">
                                            <div class="fw-bold text-dark mb-0 me-3">직급</div>
                                            <div class="info-field-text">
                                            <c:choose>
                                                <c:when test="${not empty professor.prfPositCd}">
                                                    ${professor.positionName}
                                                </c:when>
                                                <c:otherwise>
                                                    ${professor.employmentType}
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        </div>
                                    </div>

                                    <%-- 8. 임용일자 --%>
                                    <div class="col-md-6">
                                        <div class="border rounded p-2 bg-light info-field">
                                            <div class="fw-bold text-dark mb-0 me-3">임용일자</div>
                                            <div class="info-field-text">
                                                <fmt:parseDate value="${professor.employmentDate}" pattern="yyyy-MM-dd HH:mm:ss" var="parsedEmploymentDate"/>
                                                <fmt:formatDate value="${parsedEmploymentDate}" pattern="yyyy-MM-dd"/>
                                            </div>
                                        </div>
                                    </div>

                                </div>
                            </div>

                            <%-- 사진 영역 --%>
                            <div class="col-lg-3">
                                <div class="text-center">
                                    <c:choose>
                                        <c:when test="${not empty professor.photoId}">
                                            <c:url var="photoUrl" value="/devtemp/files/idphoto">
                                                <c:param name="fileId" value="${professor.photoId}"/>
                                            </c:url>
                                            <img src="${photoUrl}"
                                                 alt="${professor.lastName}${professor.firstName} 증명사진"
                                                 class="border border-2 border-secondary rounded p-4 mb-3 bg-light"
                                                 style="width: 200px; height: 240px; margin: 0 auto; object-fit: cover;">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="border border-2 border-secondary rounded p-4 mb-3 bg-light d-flex align-items-center justify-content-center" style="width: 120px; height: 150px; margin: 0 auto;">
                                                <span class="text-muted">사진 없음</span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="fw-bold text-dark">${professor.lastName}${professor.firstName}</div>
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

                            <%-- 1. 이메일 --%>
                            <div class="col-md-6">
                                <label for="email" class="form-label fw-semibold text-dark">이메일</label>
                                <input type="email" class="form-control border-2 bg-white info-field-text p-2" id="email" name="email" value="${professor.email}" required>
                            </div>

                            <%-- 2. 휴대전화 --%>
                            <div class="col-md-6">
                                <label for="mobileNo" class="form-label fw-semibold text-dark">
                                    휴대전화
                                </label>
                                <input type="tel" class="form-control border-2 bg-white info-field-text p-2" id="mobileNo" name="mobileNo" value="${professor.mobileNo}" required>
                            </div>

                            <%-- 3. 우편번호 --%>
                            <div class="col-md-3">
                                <label for="zipCode" class="form-label fw-semibold text-dark">
                                    우편번호
                                </label>
                                <input type="text" class="form-control border-2 bg-white info-field-text p-2" id="zipCode" name="zipCode" value="${professor.zipCode}" placeholder="우편번호를 입력하세요" required>
                            </div>

                            <%-- 4. 기본 주소 --%>
                            <div class="col-md-9">
                                <label for="baseAddr" class="form-label fw-semibold text-dark">
                                    주소
                                </label>
                                <div class="input-group">
                                    <input type="text" class="form-control border-2 bg-white info-field-text p-2" id="baseAddr" name="baseAddr" value="${professor.baseAddr}" placeholder="주소를 입력하세요" required>
                                    <button type="button" class="btn btn-secondary" id="address-search-button" onclick="execDaumPostcode()">주소 검색</button>
                                </div>
                            </div>

                            <%-- 5. 상세 주소 (전체 너비 사용) --%>
                            <div class="col-12">
                                <label for="detailAddr" class="form-label fw-semibold text-dark">
                                    상세주소
                                </label>
                                <input type="text" class="form-control border-2 bg-white info-field-text p-2" id="detailAddr" name="detailAddr" value="${professor.detailAddr}" placeholder="상세주소를 입력하세요">
                            </div>

                            <div class="col-12"><hr class="my-0 mt-2 mb-2"></div>

                            <%-- 6. 예금주 --%>
                            <div class="col-md-3">
                                <label for="accountHolder" class="form-label fw-semibold text-dark">
                                    예금주
                                </label>
                                <input type="text" class="form-control border-2 bg-light info-field-text p-2" id="accountHolder" name="accountHolder" value="${professor.lastName}${professor.firstName}" readonly>
                            </div>

                            <%-- 7. 은행 구분 (Bank Code Name) --%>
                            <div class="col-md-3">
                                <label for="bankCode" class="form-label fw-semibold text-dark">
                                    은행구분
                                </label>
                                <select class="form-control border-2 bg-white info-field-text p-2" id="bankCode" name="bankCode" required>
                                    <option value="">은행 선택</option>
                                    <c:forEach var="bank" items="${bankList}">
                                        <option value="${bank.commonCd}" ${professor.bankCode eq bank.commonCd ? 'selected' : ''}>${bank.cdName}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <%-- 8. 계좌번호 --%>
                            <div class="col-md-6">
                                <label for="bankAccount" class="form-label fw-semibold text-dark">
                                    계좌번호
                                </label>
                                <input type="text" class="form-control border-2 bg-white info-field-text p-2" id="bankAccount" name="bankAccount" value="${professor.bankAccount}" required>
                            </div>

                        </div>

                        <div class="text-end mt-3">
                            <button type="submit" class="btn btn-primary">저장</button>
                            <a href="<c:url value='/lms/professor/info'/>" class="btn btn-secondary">취소</a>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <%-- 다음 주소 API 스크립트 --%>
    <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
    <script>
        function execDaumPostcode() {
            new daum.Postcode({
                oncomplete: function(data) {
                    var addr = ''; // 주소 변수
                    var extraAddr = ''; // 참고항목 변수

                    //사용자가 선택한 주소 타입에 따라 해당 값을 조합한다.
                    if (data.userSelectedType === 'R') { // 도로명 주소를 선택했을 경우
                        addr = data.roadAddress;
                    } else { // 지번 주소를 선택했을 경우(J)
                        addr = data.jibunAddress;
                    }

                    // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
                    if(data.userSelectedType === 'R'){
                        // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                        // 법정동의 경우 마지막 글자가 "동" "로" "가"로 끝나는 경우
                        if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                            extraAddr += data.bname;
                        }
                        // 건물명이 있고, 공동주택일 경우 추가한다.
                        if(data.buildingName !== '' && data.apartment === 'Y'){
                            extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                        }
                        // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                        if(extraAddr !== ''){
                            extraAddr = ' (' + extraAddr + ')';
                        }
                        // 조합된 참고항목을 주소에 추가한다.
                        addr += extraAddr;
                    }

                    // 우편번호와 주소 정보를 해당 필드에 넣는다.
                    document.getElementById('zipCode').value = data.zonecode;
                    document.getElementById("baseAddr").value = addr;
                    // 커서를 상세주소 필드로 이동한다.
                    document.getElementById("detailAddr").focus(); // 상세주소로 포커스 이동
                }
            }).open();
        }
    </script>
</body>
</html>