<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>알림 발송</title>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery.inputmask/3.3.4/jquery.inputmask.bundle.min.js"></script>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/staff/staffNotificationCreate.css">
</head>
<body>
	<!-- 외부 래퍼 -->
	<div class="notification-create-page">
		<div class="notification-create-container">

			<!-- 페이지 헤더 -->
			<div class="page-header">
				<div class="header-left">
					<i class="bi bi-envelope-paper"></i>
					<h1>새 알림 작성</h1>
				</div>
				<input type="hidden" name="senderDeptName" value="${senderDeptName}">
				<div class="header-right">
					<span class="sender-info">보낸 사람: ${senderName}
						(${requestScope.senderDeptName})</span>
				</div>
			</div>

			<!-- 알림 메시지 표시 -->
			<c:if test="${not empty message}">
				<div class="alert alert-success">
					<i class="bi bi-check-circle"></i> ${message}
				</div>
			</c:if>
			<c:if test="${not empty error || not empty groupError}">
				<div class="alert alert-danger">
					<i class="bi bi-exclamation-triangle"></i> ${error} ${groupError}
				</div>
			</c:if>

			<!-- 메일 작성 폼 -->
			<form id="notificationForm" method="POST"
				action="/lms/notifications/send-notification" class="mail-form">
				<input type="hidden" name="senderName"
					value="${senderName} (${senderDeptName})">

				<!-- 수신자 섹션 -->
				<div class="mail-field">
					<div class="field-header">
						<label class="field-label">받는사람</label> <span
							class="required-mark">*</span>
					</div>
					<div class="field-content">
						<select class="field-select" id="recipientType"
							name="recipientType" required>
							<option value="">수신 대상을 선택하세요</option>
							<option value="ALL">✅ 전체 학생</option>
							<option value="GRADE">🎓 학년별</option>
							<option value="DEPARTMENT">📚 학과별</option>
						</select>
					</div>
				</div>

				<!-- 단과대학 선택 (조건부) -->
				<div class="mail-field" id="collegeSelectGroup"
					style="display: none;">
					<div class="field-header">
						<label class="field-label">단과대학</label> <span
							class="required-mark">*</span>
					</div>
					<div class="field-content">
						<select name="collegeCode" id="collegeCode" class="field-select">
							<option value="">선택하세요</option>
							<c:forEach var="college" items="${collegeList}">
								<option value="${college.collegeCd}">
									${college.collegeName}</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<!-- 학과 선택 (조건부) -->
				<div class="mail-field" id="departmentSelectGroup"
					style="display: none;">
					<div class="field-header">
						<label class="field-label">소속학과</label> <span
							class="required-mark">*</span>
					</div>
					<div class="field-content">
						<select name="departmentCode" id="departmentCode"
							class="field-select">
							<option value="" data-college-cd="">선택하세요</option>
							<c:forEach var="univDept" items="${univDeptList}">
								<option value="${univDept.univDeptCd}"
									data-college-cd="${univDept.collegeCd}">
									${univDept.univDeptName}</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<!-- 학년 선택 (조건부) -->
				<div class="mail-field" id="gradeSelectGroup" style="display: none;">
					<div class="field-header">
						<label class="field-label">학년</label> <span class="required-mark">*</span>
					</div>
					<div class="field-content">
						<select id="gradeCode" name="gradeCode" class="field-select">
							<option value="">선택하세요</option>
							<c:forEach var="grade" items="${gradeList}">
								<option value="${grade.commonCd}">${grade.cdName}</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<div class="mail-divider"></div>

				<!-- 메시지 내용 -->
				<div class="mail-field message-field">
					<div class="field-header">
						<label class="field-label">메시지</label> <span class="required-mark">*</span>
					</div>
					<div class="field-content-full">
						<textarea class="message-textarea" name="messageContent"
							id="messageContent" placeholder="알림 내용을 입력하세요..." required></textarea>
					</div>
				</div>

				<!-- 버튼 영역 -->
				<div class="button-area">
					<!-- 시연용 버튼 추가 -->
				    <button type="button" class="btn btn-demo" id="demoButton">시연용</button>
					<button type="button" class="btn btn-cancel" onclick="history.back()">취소</button>
					<button type="submit" class="btn btn-send" id="submitButton">발송하기</button>
				</div>
			</form>

		</div>
		<!-- notification-create-container 끝 -->

		<!-- 발송 확인 모달 -->
		<div id="confirmationModal" class="confirm-modal"
			style="display: none;">
			<div class="modal-content">
				<div class="modal-header">
					<div class="modal-title">
						<i class="bi bi-bell"></i>
						<h3>알림 발송 확인</h3>
					</div>
					<button class="modal-close" type="button">&times;</button>
				</div>
				<div class="modal-body">
					<div class="confirm-info">
						<div class="confirm-text">
							<p class="recipient-count">
								총 <strong id="recipientCount">0</strong>명에게
							</p>
							<p class="confirm-message">알림을 발송하시겠습니까?</p>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-cancel">취소</button>
					<button type="button" class="btn btn-confirm"
						id="confirmSendButton">
						<i class="bi bi-send-check"></i> 발송 확인
					</button>
				</div>
			</div>
		</div>
		<!-- 모달 끝 -->
	</div>
	<!-- notification-create-page 끝 -->



	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

	<script>
		$(document)
				.ready(
						function() {

							const $recipientType = $('#recipientType');
							const $gradeSelectGroup = $('#gradeSelectGroup');
							const $collegeSelectGroup = $('#collegeSelectGroup');
							const $departmentSelectGroup = $('#departmentSelectGroup');

							const $gradeCode = $('#gradeCode');
							const $collegeCode = $('#collegeCode');
							const $departmentCode = $('#departmentCode');

							const $submitButton = $('#submitButton');
							const $form = $('#notificationForm');

							// 커스텀 모달 제어 함수
							function showConfirmModal() {
								$('#confirmationModal').css('display', 'flex');
							}

							function closeConfirmModal() {
								$('#confirmationModal').css('display', 'none');
							}

							// --- 학과 드롭다운 초기화 및 메시지 설정 함수 ---
							function resetDepartmentDropdown(message) {
								$departmentCode.find('option').hide();
								$departmentCode.find('option:first').show()
										.prop('selected', true);
								$departmentCode.find('option:first').text(
										message || '소속학과를 선택해주세요.');
								$departmentCode.prop('disabled', true).prop(
										'required', false);
							}

							// --- 단과 대학 변경 시 학과 목록 동적 업데이트 ---
							$collegeCode
									.on(
											'change',
											function() {
												const selectedCollegeCd = $(
														this).val();

												resetDepartmentDropdown("-- 해당 단과 대학에 소속된 학과가 없습니다 --");

												if (selectedCollegeCd) {
													const $targetOptions = $departmentCode
															.find('option[data-college-cd="'
																	+ selectedCollegeCd
																	+ '"]');

													if ($targetOptions.length > 0) {
														$targetOptions.show();
														$departmentCode.prop(
																'disabled',
																false).prop(
																'required',
																true);
														$departmentCode
																.find(
																		'option:first')
																.text(
																		'소속학과를 선택해주세요.');
													}
												}
											});

							// --- 대상 그룹 선택 변경 이벤트 ---
							function handleRecipientTypeChange() {
								const selectedType = $recipientType.val();

								$gradeSelectGroup.hide();
								$collegeSelectGroup.hide();
								$departmentSelectGroup.hide();

								$gradeCode.prop('required', false).prop(
										'disabled', true).val("");
								$collegeCode.prop('required', false).prop(
										'disabled', true).val("");
								resetDepartmentDropdown();

								if (selectedType === 'GRADE') {
									$gradeSelectGroup.show();
									$gradeCode.prop('required', true).prop(
											'disabled', false);
								} else if (selectedType === 'DEPARTMENT') {
									$collegeSelectGroup.show();
									$departmentSelectGroup.show();
									$gradeSelectGroup.show();
									$collegeCode.prop('required', true).prop(
											'disabled', false);
									$gradeCode.prop('required', true).prop(
											'disabled', false);
								}
							}

							$recipientType.on('change',
									handleRecipientTypeChange);

							// --- 유효성 검사 함수 ---
							function validateForm(selectedType) {
								if (!selectedType) {
									alert("수신 대상 그룹을 선택해주세요.");
									return false;
								} else if (!$('#messageContent').val().trim()) {
									alert("메시지 내용을 입력해주세요.");
									$('#messageContent').focus();
									return false;
								} else if (selectedType === 'DEPARTMENT') {
									if (!$collegeCode.val()) {
										alert("단과 대학을 선택해주세요.");
										$collegeCode.focus();
										return false;
									} else if ($departmentCode.prop('required') === true
											&& !$departmentCode.val()) {
										alert("학과를 선택해주세요.");
										$departmentCode.focus();
										return false;
									} else if (!$gradeCode.val()) {
										alert("학년을 선택해주세요.");
										$gradeCode.focus();
										return false;
									}
								} else if (selectedType === 'GRADE'
										&& !$gradeCode.val()) {
									alert("학년을 선택해주세요.");
									$gradeCode.focus();
									return false;
								}
								return true;
							}

							// --- 폼 제출 로직 ---
							$form
									.on(
											'submit',
											function(e) {
												e.preventDefault();

												const selectedType = $recipientType
														.val();

												if (!validateForm(selectedType)) {
													return;
												}

												const requestData = {
													recipientType : selectedType,
													collegeCode : $collegeCode
															.val(),
													departmentCode : $departmentCode
															.val(),
													gradeCode : $gradeCode
															.val()
												};

												$submitButton
														.prop('disabled', true)
														.html(
																'<i class="bi bi-hourglass-split"></i> 인원 확인 중...');

												$
														.ajax({
															url : '/lms/notifications/count-recipients',
															type : 'GET',
															data : requestData,
															success : function(
																	response) {
																const studentCount = response.count;

																if (studentCount > 0) {
																	$(
																			'#recipientCount')
																			.text(
																					studentCount
																							.toLocaleString());
																	showConfirmModal();
																} else {
																	alert("선택된 조건에 해당하는 수신 대상 학생이 없습니다. 조건을 다시 확인해주세요.");
																}
															},
															error : function() {
																alert("수신 인원 정보를 가져오는 중 서버 오류가 발생했습니다.");
															},
															complete : function() {
																$submitButton
																		.prop(
																				'disabled',
																				false)
																		.html(
																				'<i class="bi bi-send"></i> 발송하기');
															}
														});
											});

							// --- 모달의 '발송 확인' 버튼 클릭 시 폼 최종 제출 ---
							$('#confirmSendButton')
									.on(
											'click',
											function() {
												closeConfirmModal();

												const selectedType = $recipientType
														.val();

												$('#messageContent').prop(
														'disabled', false);
												$recipientType.prop('disabled',
														false);

												if (selectedType === 'GRADE'
														|| selectedType === 'DEPARTMENT') {
													$gradeCode.prop('disabled',
															false);
												}

												if (selectedType === 'DEPARTMENT') {
													$collegeCode.prop(
															'disabled', false);
													if ($departmentCode.val()) {
														$departmentCode.prop(
																'disabled',
																false);
													}
												}

												$submitButton
														.prop('disabled', true)
														.html(
																'<i class="bi bi-send-check"></i> 발송 중...');

												$form.off('submit').submit();
											});

							// 모달 닫기 이벤트
							$('.modal-close').on('click', closeConfirmModal);
							$('.confirm-modal .btn-cancel').on('click',
									closeConfirmModal);

							// 모달 외부 클릭시 닫기
							$(window)
									.on(
											'click',
											function(event) {
												if ($(event.target).attr('id') === 'confirmationModal') {
													closeConfirmModal();
												}
											});
							// --- 시연용 버튼 클릭 이벤트 ---
							$('#demoButton').on('click', function() {
							    const demoMessage = "취업 특강 [3학년 대상] 11월 12일(화) 오후 2시, '현직 개발자가 말하는 채용 트렌드와 포트폴리오 준비법' 특강이 진행됩니다. 본관 201호에서 진행되며, 신청은 선착순 50명입니다.";

							    $('#messageContent').val(demoMessage);
							    $('#messageContent').focus();
							});

							handleRecipientTypeChange();
						});
	</script>
</body>
</html>