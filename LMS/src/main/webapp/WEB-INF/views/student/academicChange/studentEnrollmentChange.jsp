<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>재학 상태 변경 신청</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/studentEnrollmentStatusChange.css" />
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
<div class="professor-lecture-page">
	<div class="container">

		<!-- 페이지 헤더 추가 -->
        <div class="page-header">
            <h1>재학 상태 변경 신청</h1>
        </div>

	    <!-- 메인 카드 -->
	    <div class="form-section">
	        <!-- 학생 기본정보 -->
	        <!-- 유의사항 (동적 표시) -->
	        <div id="noticeBox" class="notice-box" style="display:none;">
	            <!-- 동적 생성 -->
	        </div>

	        <!-- ================================ -->
	        <!-- 통합 신청 폼 -->
	        <!-- ================================ -->
	        <form id="recordApplyForm" enctype="multipart/form-data">
	            <input type="hidden" name="studentNo" value="${studentInfo.studentNo}">

	            <!-- 신청 구분 -->
	            <div class="form-group main-select">
	                <label for="recordChangeCd">신청 구분 <span class="required">*</span></label>
	                <div style="display: flex; align-items: center; gap: 10px;">
	                    <select id="recordChangeCd" name="recordChangeCd" required>
	                        <option value="">선택하세요</option>
	                        <option value="DROP">자퇴</option>
	                        <option value="REST">휴학</option>
	                        <option value="RTRN">복학</option>
	                        <option value="DEFR">졸업유예</option>
	                    </select>
	                    <button type="button" id="autoclick" class="btn-secondary auto-fill-btn" style="white-space: nowrap;">시연용</button>
	                </div>
	            </div>

	            <!-- ================================ -->
	            <!-- 휴학 전용 필드 -->
	            <!-- ================================ -->
	            <div id="leaveFields" style="display:none;">
	            	<!-- 2열 그리드 시작 -->
    				<div class="form-grid-2col">

	                <div class="form-group">
	                    <label for="leaveType">휴학 종류 <span class="required">*</span></label>
	                    <select id="leaveType" name="leaveType">
	                        <option value="">선택하세요</option>
	                        <c:forEach var="code" items="${recordChangeList}">
							    <%-- code.commonCd가 "REST_"로 시작하는 경우에만 옵션을 렌더링 --%>
							    <c:if test="${fn:startsWith(code.commonCd, 'REST_')}">
							        <option value="${code.commonCd}" data-desc="${code.cdDesc}">
							            ${code.cdName}
							        </option>
							    </c:if>
							</c:forEach>
	                    </select>
	                </div>

	                <!-- 휴학 시작 학기 (info-box도 그리드 안에) -->
			        <div class="form-group">
			            <label style="visibility: hidden;">공간 확보용</label>
			            <div class="info-box">
			                <strong>휴학 시작 학기:</strong>
			                <span id="leaveStartTermDisplay">-</span>
			            </div>
			            <input type="hidden" id="leaveStartTerm" name="leaveStartTerm">
			        </div>
			    </div>
			    <!-- 2열 그리드 끝 -->

	                <!-- 일반휴학 전용 -->
	                <div id="generalLeaveFields" style="display:none;">
	                    <div class="form-group">
	                        <label>휴학 기간 <span class="required">*</span></label>
	                        <div class="radio-group">
	                            <label>
	                                <input type="radio" name="leaveDuration" value="1">
	                                1학기
	                            </label>
	                            <label>
	                                <input type="radio" name="leaveDuration" value="2">
	                                2학기 (1년)
	                            </label>
	                        </div>
	                        <small class="help-text">최대 연속 2학기까지 신청 가능합니다.</small>
	                    </div>

	                    <!-- 복학 예정 학기 미리보기 -->
	                    <div id="returnTermPreview" class="term-preview-container" style="display:none;">
	                        <!-- 동적 생성 -->
	                    </div>
	                </div>

	                <!-- 군입대 휴학 전용 -->
					<div id="militaryFields" style="display:none;">
					<!-- 2열 그리드 시작 -->
        			<div class="form-grid-2col">
					    <div class="form-group">
					        <label for="militaryType">입대구분 <span class="required">*</span></label>
					        <select id="militaryType" name="militaryTypeCd">
					            <option value="">선택하세요</option>
					            <c:forEach var="code" items="${milType}">
					                <%-- 면제(EXMP) 제외 --%>
					                <c:if test="${code.commonCd ne 'EXMP'}">
					                    <option value="${code.commonCd}" data-desc="${code.cdDesc}">
					                        ${code.cdName}
					                    </option>
					                </c:if>
					            </c:forEach>
					        </select>
					        <small class="help-text">※ 복무 기간은 입대구분에 따라 자동 계산됩니다.</small>
					    </div>

					    <div class="form-group">
					        <label for="joinAt">입영일 <span class="required">*</span></label>
					        <input type="date" id="joinAt" name="joinAt">
					    </div>
					    </div>
       					 <!-- 2열 그리드 끝 -->

					    <!-- 예상 전역일 미리보기 -->
					    <div id="exitDatePreview" style="display:none;">
					        <!-- JavaScript로 동적 생성 -->
					    </div>

					    <input type="hidden" id="exitAt" name="exitAt">
					</div>
	            </div>

	            <!-- ================================ -->
	            <!-- 복학 전용 필드 -->
	            <!-- ================================ -->
	            <div id="returnFields" style="display:none;">
				    <div class="info-box">
				        <strong>복학 예정 학기:</strong>
				        <span id="returnTermDisplay">2025학년도 2학기</span>
				    </div>
				    <input type="hidden" id="returnTerm" name="returnTerm" value="2025_REG2">
				</div>

	            <!-- ================================ -->
	            <!-- 졸업유예 전용 필드 -->
	            <!-- ================================ -->
	            <div id="deferFields" style="display:none;">
				    <div class="form-group">
				        <label>희망 졸업 학기 <span class="required">*</span></label>
				        <div class="radio-group">
				            <label>
				                <input type="radio" name="deferTermChoice" value="1">
				                1학기
				            </label>
				            <label>
				                <input type="radio" name="deferTermChoice" value="2">
				                2학기
				            </label>
				        </div>
				    </div>

				    <div class="info-box" id="deferTermPreview" style="display:none;">
				        <small>예상 졸업 시기: <strong id="deferTermDisplay"></strong></small>
				    </div>

				    <!-- 실제 전송될 값 (2026_REG1 형식) -->
				    <input type="hidden" id="deferTerm" name="deferTerm">
				</div>

	            <!-- ================================ -->
	            <!-- 공통 필드 -->
	            <!-- ================================ -->
	            <div class="form-group">
	                <label for="applyReason">신청사유 <span class="required">*</span></label>
	                <textarea id="applyReason" name="applyReason" rows="5"
	                          placeholder="신청 사유를 입력해주세요" required></textarea>
	            </div>

	            <div class="form-group">
	                <label for="attachFiles">첨부파일</label>
	                <input type="file" id="attachFiles" name="attachFiles" multiple>
	                <small class="help-text" id="fileHelp">
	                    첨부파일은 선택사항입니다.
	                </small>
	            </div>

	            <div class="button-group">
	                <button type="reset" class="reset-btn">초기화</button>
	                <button type="submit" class="submit-btn">신청하기</button>
	            </div>
	        </form>
	    </div>
	</div>
</div>

<script src="${pageContext.request.contextPath}/js/app/student/studentEnrollmentStatusChange.js"></script>

</body>
</html>