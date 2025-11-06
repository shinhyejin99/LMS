/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 9. 27.     	신혜진           최초 생성
 *
 * </pre>
 */


$(document).ready(function() {
	// Controller에서 정의된 JSON API 엔드포인트
	const PROFESSOR_SEARCH_API = '/lms/staff/students/professor/search';

	// 모달 관련 DOM 요소 전역 상수화
	const $modal = $('#professorSearchModal');
	const $resultsTable = $('#professor-search-results');

	const $professorIdInput = $('#professorIdInput');
	const $professorNameDisplay = $('#professorNameDisplay');
	// 1. 주소 검색 로직 (Daum Postcode API)
	$('#zipbtn').on('click', function() {
		// Daum Postcode API가 로드되었는지 확인
		if (typeof daum === 'undefined' || typeof daum.Postcode !== 'function') {

			return;
		}

		new daum.Postcode({
			oncomplete: function(data) {
				// 우편번호 필드 (zipCode / postcode)
				const zipCodeField = $('#zipCode, #postcode');
				if (zipCodeField.length) {
					zipCodeField.val(data.zonecode);
				}

				// 기본 주소 (baseAddr / add1)
				const baseAddrField = $('#baseAddr, #add1');
				if (baseAddrField.length) {
					baseAddrField.val(data.roadAddress);
				}

				// 상세 주소 필드에 포커스 (detailAddr / add2)
				const detailAddrField = $('#detailAddr, #add2');
				if (detailAddrField.length) {
					detailAddrField.focus();
				}
			}
		}).open();
	});

	// 학과-교수 연동 로직
	const $collegeSelect = $('#collegeSelect');
	const $deptSelect = $('#deptSelect');

	// 모든 학과 옵션을 저장 (필터링을 위해)
	const $allDeptOptions = $deptSelect.find('option').not('[value=""]');

	// 단과대학 변경 시 학과 필터링
	$collegeSelect.on('change', function() {

		const selectedCollegeCd = $(this).val();

		// 학과 드롭다운 초기화
		$deptSelect.val('');
		$allDeptOptions.hide(); // 모든 학과 옵션 숨기기

		if (selectedCollegeCd) {
			// 선택된 단과대학에 해당하는 학과만 표시
			$allDeptOptions.filter(`[data-college-cd="${selectedCollegeCd}"]`).show();
		} else {
			// "선택"이 선택되면 모든 학과 숨기기 (또는 필요에 따라 모두 표시)
			// 현재는 모두 숨기는 것이 논리적
		}

		// 교수 정보도 초기화
		$professorIdInput.val('');
		$professorNameDisplay.val('');
		$('#professor-search-results').empty().append('<tr><td colspan="3" class="text-center text-muted">학과를 선택해주세요.</td></tr>');
	});

	// 페이지 로드 시 초기 필터링 적용
	$collegeSelect.trigger('change');

	// 학과 변경 시 교수 목록 로드 (기존 로직 유지 및 개선)
	$deptSelect.on('change', function() {

		const selectedDeptCd = $(this).val();

		// 지도교수 정보 리셋
		$professorIdInput.val('');
		$professorNameDisplay.val('');

		if (selectedDeptCd) {
			loadProfessors(selectedDeptCd);
		} else {
			$('#professor-search-results').empty().append('<tr><td colspan="3" class="text-center text-muted">학과를 선택해주세요.</td></tr>');
		}
	});

	function loadProfessors(deptCd) {
		const PROFESSOR_SEARCH_API = '/lms/staff/students/professor/search';

		$('#professor-search-results').empty().append('<tr><td colspan="3" class="text-center text-primary">교수 목록을 조회 중입니다...</td></tr>');

		$.ajax({
			url: PROFESSOR_SEARCH_API,
			type: 'GET',
			data: { deptCd: deptCd },
			dataType: 'json',
			success: function(professorList) {
				const $professorSearchResults = $('#professor-search-results');
				$professorSearchResults.empty();

				if (!professorList || professorList.length === 0) {
					$professorSearchResults.append('<tr><td colspan="3" class="text-center text-danger">해당 조건의 교수 목록이 없습니다.</td></tr>');
					return;
				}

				professorList.forEach(function(professor) {
					const profId = professor.professorId || professor.professorNo || professor.professorid;
					const profName = professor.professorName || professor.professorname;
					const deptName = professor.univDeptName || professor.univdeptname;

					if (!profId || !profName || !deptName) {
						console.error("필드 추출 실패: JSON 키가 예상과 다릅니다. 실제 JSON 객체:", professor);
						return;
					}

					const row = `<tr style="cursor: pointer;" onclick="selectProfessor('${profId}', '${profName}')">
                                     <td>${profId}</td>
                                     <td>${profName}</td>
                                     <td>${deptName}</td>
                                 </tr>`;
					$professorSearchResults.append(row);
				});
			},
			error: function(xhr, status, error) {
				$professorSearchResults.empty().append('<tr><td colspan="3" class="text-danger text-center">조회 중 오류 발생: 서버 응답 또는 통신 문제</td></tr>');
				console.error("교수 목록 조회 실패:", xhr.responseText, status, error);
			}
		});
	}


	// ⭐️ 2. 이름 분리 로직 함수 정의 ⭐️
	function splitUserName() {
		var userName = $('#userName').val().trim();
		if (userName.length > 0) {
			$('#lastName').val(userName.substring(0, 1));
			$('#firstName').val(userName.substring(1));
		} else {
			$('#lastName').val('');
			$('#firstName').val('');
		}
	}

	// 3. 폼 제출 이벤트 리스너
	$('#info-form').submit(function(event) {
		splitUserName();
	});

	// 4. 주민번호 입력 필드 변경 시 성별 자동 업데이트 리스너 추가 (기존 로직 유지)
	$('#regiNo').on('change keyup', function() {
		if (typeof updateGenderFromRegiNo === 'function') {
			updateGenderFromRegiNo('#regiNo', 'input[name="gender"]');
		}
	});

	// =======================================================================
	// 🟢 5. 지도교수 전체 목록 로드 로직 (AJAX) 🟢
	// =======================================================================

	// 5.1. 교수 검색 버튼 클릭 이벤트: 모달 열기 및 초기 전체 목록 로드
	$('#professorSearchBtn').on('click', function() {
		// 현재 학생의 소속 학과 코드를 읽어와 필터링에 사용
		const currentDeptCd = $('#deptSelect').val();
		const currentDeptName = $('#deptSelect option:selected').text();

		// 학과 이름 표시 업데이트 (JSP 모달에 <span id="currentDeptNameDisplay"></span>가 있다고 가정)
		$('#currentDeptNameDisplay').text(currentDeptName);

		// 💡 초기 목록 로드: 키워드를 빈 문자열('')로 설정하여 해당 학과의 전체 목록을 조회
		performProfessorSearch(currentDeptCd, '');

		// 모달 표시 (Bootstrap 5 API 사용)
		if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
			new bootstrap.Modal($modal[0]).show();
		} else {
			console.error("Bootstrap Modal 라이브러리가 로드되지 않았습니다.");
		}
	});

	// 5.2. Ajax를 이용한 교수 목록 실행 함수 (디버깅 안전성 강화)
	function performProfessorSearch(deptCd, searchKeyword) {
		$resultsTable.empty().append('<tr><td colspan="3" class="text-center text-primary">교수 목록을 조회 중입니다...</td></tr>');

		$.ajax({
			url: PROFESSOR_SEARCH_API,
			type: 'GET',
			data: { deptCd: deptCd, searchKeyword: searchKeyword },
			dataType: 'json',
			success: function(professorList) {
				$resultsTable.empty();
				if (!professorList || professorList.length === 0) {
					$resultsTable.append('<tr><td colspan="3" class="text-center text-danger">해당 조건의 교수 목록이 없습니다.</td></tr>');
					return;
				}

				professorList.forEach(function(professor) {

					// 🚨 JSP의 name="professorId"에 매핑되도록, ID 필드는 professorId로 통일하여 추출
					const profId = professor.professorId || professor.professorNo || professor.professorid;
					const profName = professor.professorName || professor.professorname;
					const deptName = professor.univDeptName || professor.univdeptname;

					if (!profId || !profName || !deptName) {
						console.error("필드 추출 실패: JSON 키가 예상과 다릅니다. 실제 JSON 객체:", professor);
						return;
					}

					// <tr> 클릭 시 window.selectProfessor 함수 호출 (인수로 profId 전달)
					const row = `<tr style="cursor: pointer;" onclick="selectProfessor('${profId}', '${profName}')">
                                     <td>${profId}</td>
                                     <td>${profName}</td>
                                     <td>${deptName}</td>
                                 </tr>`;
					$resultsTable.append(row);
				});
			},
			error: function(xhr, status, error) {
				$resultsTable.empty().append('<tr><td colspan="3" class="text-danger text-center">조회 중 오류 발생: 서버 응답 또는 통신 문제</td></tr>');
				console.error("교수 목록 조회 실패:", xhr.responseText, status, error);
			}
		});
	}


	// 5.3. 교수 정보 설정 함수 (전역 함수: JSP의 name="professorId"에 맞게 ID를 설정)
	window.selectProfessor = function(professorId, professorName) {
		try {
			// ⭐️ 핵심 수정: $professorIdInput 변수 사용 ⭐️
			$professorIdInput.val(professorId);
			$professorNameDisplay.val(professorName);



			// Bootstrap 5 모달 닫기 로직
			const modalInstance = bootstrap.Modal.getInstance($modal[0]);
			if (modalInstance) {
				modalInstance.hide();
			}


		} catch (e) {
			console.error("교수 정보 설정 오류:", e);

		}
	};

	// =======================================================================
	// 🚀 6. 테스트 데이터 (더미 데이터) 정의 및 채우기 (기존 로직 유지) 🚀
	// =======================================================================
	const TEST_DATA = {
		userName: "김민수",
		regiNo: "9803011000000",
		mobileNo: "010-9876-5432",
		email: "test.student@jsu.or.kr",
		engLname: "KIM",
		engFname: "MINSU",
		guardPhone: "010-1111-2222",

		// 주소 데이터는 고정
		postcodeValue: "08505",
		baseAddrValue: "서울특별시 금천구 디지털로",
		detailAddrValue: "가산동 123",

		// Select 박스 공통 코드
		collegeCd: "CLG-ENGI",
		univDeptCd: "DEP-ENGI-CSE",
		stuStatusCd: "ENROLLED",
		bankCode: "BANK_KB",
		gradeCd: "2ND",
		entranceTypeCd: "SU-GC",
		militaryTypeCd: "ARMY",

		bankAccount: "110-123456-789",
		targetDept: "경영학과 (복수전공)",
		entranceDate: "2025-03-02",
		exitAt: "2025-12-31"
	};

	$('#fillTestBtn').on('click', function() {

		// 1. 일반 텍스트/날짜 입력 필드 채우기
		$('#userName').val(TEST_DATA.userName);
		$('#regiNo').val(TEST_DATA.regiNo);
		$('#mobileNo').val(TEST_DATA.mobileNo);
		$('#email').val(TEST_DATA.email);
		$('#engLname').val(TEST_DATA.engLname);
		$('#engFname').val(TEST_DATA.engFname);
		$('#guardPhone').val(TEST_DATA.guardPhone);

		// 주소 필드 통합 주입 및 디버깅 (기존 주소 로직 유지)
		const postcode = TEST_DATA.postcodeValue;
		const baseAddr = TEST_DATA.baseAddrValue;
		const detailAddr = TEST_DATA.detailAddrValue;

		const selectors = {
			postcode: { value: postcode, attempts: ['#zipCode', '#postcode', 'input[name="zipCode"]', 'input[name="postcode"]'] },
			baseAddr: { value: baseAddr, attempts: ['#baseAddr', '#add1', 'input[name="baseAddr"]', 'input[name="add1"]'] },
			detailAddr: { value: detailAddr, attempts: ['#detailAddr', '#add2', 'input[name="detailAddr"]', 'input[name="add2"]'] },
		};

		const applyValueAndDebug = (fieldName, data) => {
			let success = false;
			data.attempts.forEach(selector => {
				const $element = $(selector);
				if ($element.length > 0) {
					$element.val(data.value).css({
						'border': '2px solid red',
						'box-shadow': '0 0 5px rgba(255, 0, 0, 0.5)'
					});

					const actualName = $element.attr('name');
					if (!success && actualName) {
						console.log(`[DEBUG SUCCESS] ${fieldName} 필드가 ${selector}로 채워졌습니다. 실제 name: ${actualName}`);

						let desiredName = '';
						if (fieldName === 'postcode') desiredName = 'zip_code';
						if (fieldName === 'baseAddr') desiredName = 'base_addr';
						if (fieldName === 'detailAddr') desiredName = 'detail_addr';

						if (actualName !== desiredName && desiredName !== '') {
							$element.attr('name', desiredName);
							console.log(`[DEBUG NAME CHANGE] ${actualName}을(를) ${desiredName}로 변경했습니다.`);
						}
					}
					success = true;
				}
			});

			if (!success) {
				console.warn(`[DEBUG WARNING] ${fieldName} 필드 (${data.attempts.join(', ')})를 찾지 못했습니다.`);
			}
		};

		applyValueAndDebug('postcode', selectors.postcode);
		applyValueAndDebug('baseAddr', selectors.baseAddr);
		applyValueAndDebug('detailAddr', selectors.detailAddr);
		// -----------------------------------------------------------------

		$('#bankAccount').val(TEST_DATA.bankAccount);
		$('#targetDept').val(TEST_DATA.targetDept);
		$('#entranceDate').val(TEST_DATA.entranceDate);
		$('#exitAt').val(TEST_DATA.exitAt);

		splitUserName();

		if (typeof updateGenderFromRegiNo === 'function') {
			updateGenderFromRegiNo('#regiNo', 'input[name="gender"]');
		}

		// 2. 드롭다운 (Select) 필드 값 선택
		$('select[name="collegeCd"]').val(TEST_DATA.collegeCd).trigger('change');

		// 학과 목록이 비동기 로딩될 수 있으므로, 잠시 후 설정
		setTimeout(() => {
			$deptSelect.val(TEST_DATA.univDeptCd);
		}, 100);

		$('select[name="gradeCd"]').val(TEST_DATA.gradeCd);
		$('select[name="stuStatusCd"]').val(TEST_DATA.stuStatusCd);
		$('select[name="entranceTypeCd"]').val(TEST_DATA.entranceTypeCd);
		$('select[name="bankCode"]').val(TEST_DATA.bankCode);
		$('select[name="militaryTypeCd"]').val(TEST_DATA.militaryTypeCd);

		$('#full-name-display').text(TEST_DATA.userName);
	});
});