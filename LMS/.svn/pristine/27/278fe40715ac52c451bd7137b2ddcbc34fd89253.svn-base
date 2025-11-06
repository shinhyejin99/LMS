<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>교수 상세 수정</title>
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
        rel="stylesheet">
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css"
        rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentInfo.css" />
    <style>
        .input-group-text-custom { width: 100px; }
        .form-control-static {
            padding-top: 0.375rem;
            padding-bottom: 0.375rem;
            min-height: calc(1.5em + 0.75rem + 2px);
        }
    </style>
</head>
<body class="bg-light">
  <%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div class="container-fluid" id="main-content-area" style="max-width: 1200px;">
        <h3 class="mb-4 text-center">교수 상세 수정</h3>

        <%-- 폼 시작: professor 객체를 ModelAttribute로 사용 --%>
        <form:form modelAttribute="professor" action="/lms/staff/professor/modify/${professor.professorNo}" method="post" enctype="multipart/form-data">

        <form:hidden path="professorNo" />
        <form:hidden path="photoId" />
        <%-- 주민번호, 주소 등 UsersVO/AddressVO에 필요한 Hidden Field는 DTO에 포함되어 있다고 가정 --%>

        <div class="py-4">
            <div class="row">
                <div class="col-12 col-lg-6 mb-4">
                    <div class="card border-0 shadow h-100">
                        <div class="card-header">
                            <h2 class="fs-5 fw-bold mb-0">기본정보 (인적정보)</h2>
                        </div>
                        <div class="card-body">
                            <h5>기본정보 </h5>
                            <div class="row mb-3 align-items-center">
                                <div class="col-md-4 text-center">
                                    <div class="text-center mb-2">
                                        <img id="photo-preview" src="/lms/files/${professor.photoId}"
                                            onerror="this.onerror=null;this.src='/lms/assets/img/default-profile.png';"
                                            class="rounded-circle border-white" alt="증명사진" width="100" height="100" style="object-fit: cover;">
                                    </div>
                                    <input type="file" id="photoFile" name="photoFile" class="form-control form-control-sm" accept="image/*">
                                    <small class="text-muted">사진 변경 (선택)</small>
                                </div>
                                <div class="col-md-8">
                                    <div class="mb-2">
                                        <label class="form-label fw-semibold">성/이름(한글)</label>
                                        <div class="input-group">
                                            <form:input path="lastName" class="form-control" placeholder="성" />
                                            <form:input path="firstName" class="form-control" placeholder="이름" />
                                        </div>
                                    </div>
                                    <div class="mb-2">
                                        <label class="form-label fw-semibold">성/이름(영문)</label>
                                        <div class="input-group">
                                            <form:input path="engLname" class="form-control" placeholder="Last Name" />
                                            <form:input path="engFname" class="form-control" placeholder="First Name" />
                                        </div>
                                    </div>
                                    <div class="mb-2">
                                        <label class="form-label fw-semibold">주민등록번호</label>
                                        <%-- 주민번호는 보안상 수정이 불가능하며, 초기 등록 시 생성되거나 입력된다고 가정하고 읽기 전용으로 표시 --%>
                                        <div class="form-control form-control-static">
                                            ${fn:substring(professor.regiNo, 0, 6)}-${fn:substring(professor.regiNo, 6, 7)}******
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <hr>

                            <h5>주소정보 </h5>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">우편번호</label>
                                <div class="input-group">
                                    <form:input path="zipCode" id="postcode" class="form-control" readonly="true"/>
                                    <button type="button" class="btn btn-primary" id="zipbtn">검색</button>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">기본주소</label>
                                <form:input path="baseAddr" id="add1" class="form-control" readonly="true"/>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">상세주소</label>
                                <form:input path="detailAddr" id="add2" class="form-control"/>
                            </div>
                            <hr>

                            <h5>연락정보 </h5>
                            <div class="row g-3">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-semibold">휴대전화</label>
                                    <form:input path="mobileNo" class="form-control" placeholder="010-XXXX-XXXX"/>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-semibold">연구실 전화번호</label>
                                    <form:input path="officeNo" class="form-control" placeholder="042-XXX-XXXX"/>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">이메일</label>
                                <form:input path="email" type="email" class="form-control"/>
                            </div>
                            <hr>

                            <h5>금융정보 </h5>
                            <div class="row g-3">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-semibold">은행구분</label>
                                    <%-- 은행 목록 (bankList)은 컨트롤러에서 전달받아야 합니다. --%>
                                    <form:select path="bankCode" class="form-select">
                                        <form:option value="">-- 은행 선택 --</form:option>
                                        <c:forEach var="bank" items="${bankList}">
                                            <form:option value="${bank.commonCd}">
                                                ${bank.cdName}
                                            </form:option>
                                        </c:forEach>
                                    </form:select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-semibold">예금주</label>
                                    <div class="form-control form-control-static">
                                        ${professor.lastName}${professor.firstName}
                                    </div>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">계좌번호</label>
                                <form:input path="bankAccount" class="form-control"/>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-12 col-lg-6 mb-4">
                    <div class="card border-0 shadow h-100">
                        <div class="card-header"><h2 class="fs-5 fw-bold mb-0">재직정보 </h2></div>
                        <div class="card-body">
                            <h5 class="mb-3">인사/임용정보</h5>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">교번</label>
                                <form:input path="professorNo" class="form-control" readonly="true"/>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">직급 (보직명)</label>
                                <%-- 보직명 목록 (prfPositList)는 컨트롤러에서 전달받아야 합니다. --%>
                                <form:select path="prfPositCd" class="form-select">
                                    <form:option value="">-- 선택 --</form:option>
                                    <c:forEach var="posit" items="${prfPositList}">
                                        <form:option value="${posit.commonCd}">
                                            ${posit.cdName}
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">임용일자</label>
                                <form:input path="hireDateString" type="date" class="form-control"/>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">임용구분</label>
                                <%-- 임용구분 목록 (profAppntList)는 컨트롤러에서 전달받아야 합니다. --%>
                                <form:select path="profAppntCd" class="form-select">
                                    <form:option value="">-- 선택 --</form:option>
                                    <c:forEach var="appnt" items="${profAppntList}">
                                        <form:option value="${appnt.commonCd}">
                                            ${appnt.cdName}
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">재직상태</label>
                                <%-- 재직상태 목록 (prfStatusList)는 컨트롤러에서 전달받아야 합니다. --%>
                                <form:select path="prfStatusCd" class="form-select">
                                    <form:option value="">-- 선택 --</form:option>
                                    <c:forEach var="status" items="${prfStatusList}">
                                        <form:option value="${status.commonCd}">
                                            ${status.cdName}
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                            <hr>

                            <h5 class="mb-3">소속정보</h5>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">단과대학</label>
                                <%-- 단과대학 목록 (collegeList)는 컨트롤러에서 전달받아야 합니다. --%>
                                <select name="collegeCd" id="collegeSelect" class="form-select">
                                    <option value="">선택</option>
                                    <c:forEach var="college" items="${collegeList}">
                                        <option value="${college.collegeCd}" ${college.collegeCd eq professor.collegeCd ? 'selected' : ''}>
                                          ${college.collegeName}
                                    </option>
                                  </c:forEach>
                               </select>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">소속학과(전공)</label>
                                <%-- 소속학과 목록 (univDeptList)는 컨트롤러에서 전달받아야 합니다. --%>
                                <form:select path="deptCd" id="deptSelect" class="form-select">
                                    <form:option value="" data-college-cd="">선택</form:option>
                                    <c:forEach var="univDept" items="${univDeptList}">
                                        <form:option
                                            value="${univDept.univDeptCd}"
                                            data-college-cd="${univDept.collegeCd}"
                                            ${univDept.univDeptCd eq professor.deptCd ? 'selected' : ''}
                                        >
                                          ${univDept.univDeptName}
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">연구실</label>
                                <form:input path="researchRoom" class="form-control" placeholder="건물명 및 호수"/>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">담당 교과목 (현재 학기)</label>
                                <div class="form-control form-control-static">
                                    ${professor.currentCourses}
                                    <small class="text-muted">(교과목 정보는 별도의 시스템에서 관리)</small>
                                </div>
                            </div>
                            <hr>

                            <h5 class="mb-3">학위/연구정보</h5>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">최종학위</label>
                                <form:input path="finalDegree" class="form-control" placeholder="최종학위명 및 취득 대학"/>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">연구분야</label>
                                <form:input path="researchArea" class="form-control" placeholder="주요 연구 분야"/>
                            </div>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">연구실적 요약</label>
                                <form:textarea path="researchSummary" rows="3" class="form-control" placeholder="연구실적 요약 (간략히)"/>
                            </div>
                            <hr>

                            <h5 class="mb-3">보직/행정정보</h5>
                            <div class="mb-2">
                                <label class="form-label fw-semibold">학과장</label>
                                <div class="form-control form-control-static">
                                    ${professor.departmentHeadPosition}
                                    <small class="text-muted">(보직 정보는 별도의 시스템에서 관리)</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="text-center mt-4 mb-5">
                <button type="submit" class="btn btn-primary btn-lg me-3">
                    <i class="bi bi-save me-2"></i> 정보 수정 완료
                </button>
                <c:url var="detailUrl" value="/lms/staff/professors/${professor.professorNo}" />
                <a href="${detailUrl}" class="btn btn-outline-secondary btn-lg">
                    <i class="bi bi-x-circle me-2"></i> 취소 및 상세 보기
                </a>
            </div>

        </div>
        </form:form>
    </div>

    <%-- 필요한 스크립트 추가 --%>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
  <script src="<c:url value='/js/app/staff/staffInfo.js' />"></script>
  <script src="<c:url value='/js/app/staff/photoFileAndDepositorName.js' />"></script>
  <script src="<c:url value='/js/app/staff/updateGenderFromRegiNo.js' />"></script>
  <script src="<c:url value='/js/app/staff/searchUI.js' />"></script>

    <script>

        // 단과대학-학과 연동 로직 (선택적)
        document.getElementById('collegeSelect').addEventListener('change', function() {
            const selectedCollegeCd = this.value;
            const deptSelect = document.getElementById('deptSelect');
            const deptOptions = deptSelect.querySelectorAll('option');

            // 첫 번째 '선택' 옵션을 제외하고 모두 숨김
            deptOptions.forEach(option => {
                if (option.value === "") return;

                const collegeCd = option.getAttribute('data-college-cd');
                if (selectedCollegeCd === "" || collegeCd === selectedCollegeCd) {
                    option.style.display = '';
                } else {
                    option.style.display = 'none';
                }
            });
            // 선택된 학과가 현재 단과대학에 속하지 않으면 초기화
            const currentDeptCollege = deptSelect.options[deptSelect.selectedIndex].getAttribute('data-college-cd');
            if (selectedCollegeCd !== "" && currentDeptCollege !== selectedCollegeCd) {
                deptSelect.value = "";
            }
        });

        // 페이지 로드 시 학과 필터링 초기 실행 (선택 상태 유지)
        document.getElementById('collegeSelect').dispatchEvent(new Event('change'));

    </script>
</body>
</html>