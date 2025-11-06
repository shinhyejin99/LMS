/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 02.     	신혜진          주민등록번호를 분석하여 성별을 업데이트하는 함수
 *
 * </pre>
 */
// updateGenderFromRegiNo.js (⭐️ 최종 수정 버전 ⭐️)
/**
 * 주민등록번호를 분석하여 성별을 업데이트하는 함수
 * @param {string} regiNoSelector - 주민등록번호 필드 선택자 (예: '#regiNo')
 * @param {string} genderSelector - 성별 필드 선택자 (예: '#genderSelect')
 * @param {string} hiddenGenderSelector - 숨겨진 성별 코드 필드 선택자 (예: '#hiddenGender')
 */
function updateGenderFromRegiNo(regiNoSelector, genderSelector, hiddenGenderSelector) {
	const $regiNoElement = $(regiNoSelector);
	const $genderDisplayField = $(genderSelector); // 화면에 표시되는 성별 필드
    const $hiddenGenderField = $(hiddenGenderSelector); // 실제 전송되는 성별 코드 필드

	if (!$regiNoElement.length || !$genderDisplayField.length || !$hiddenGenderField.length) {
        console.error("Required elements not found for gender update.");
		return;
	}

	// --- 1. 주민등록번호 값 읽기 ---
	// .val()은 input에서 값을 읽는 표준 방법입니다.
	let regiNo = $regiNoElement.val() || '';

	// 하이픈 제거 및 공백 제거
	const cleanedRegiNo = regiNo.replace(/[^0-9]/g, '').trim();
	const genderDigitIndex = 6;
	let genderValue = ''; // 표시될 성별 (예: '남성', '여성')
    let genderCode = ''; // DTO에 바인딩될 코드 (예: 'M', 'F' 또는 'MALE', 'FEMALE')

	// 주민번호는 13자리가 되어야 성별 구분이 가능합니다.
	if (cleanedRegiNo.length < 13) {
		$genderDisplayField.val('');
        $hiddenGenderField.val('');
		return;
	}

	const genderIndicator = cleanedRegiNo.charAt(genderDigitIndex);

	// 4. 성별 코드에 따라 값 설정 (1, 3, 5: 남성 / 2, 4, 6: 여성)
	if (genderIndicator === '1' || genderIndicator === '3' || genderIndicator === '5') {
		genderValue = '남성';
        genderCode = 'M'; // 실제 DB 코드에 맞게 수정 필요 (예: 'MALE')
	} else if (genderIndicator === '2' || genderIndicator === '4' || genderIndicator === '6') {
		genderValue = '여성';
        genderCode = 'F'; // 실제 DB 코드에 맞게 수정 필요 (예: 'FEMALE')
	} else {
		genderValue = '확인 불가';
        genderCode = '';
	}

	// --- 5. 성별 필드에 결과 값 기입 ---
	// 화면 표시 필드 업데이트 (읽기 전용 input)
    $genderDisplayField.val(genderValue);

    // 숨겨진 필드 업데이트 (폼 전송용)
    $hiddenGenderField.val(genderCode);
}

// ----------------------------------------------------------------------------------
// ⭐⭐⭐ 페이지 로드 완료 후 이벤트 리스너 등록 ⭐⭐⭐
// ----------------------------------------------------------------------------------
$(document).ready(function() {
    // 주민등록번호 입력 필드
    const regiNoSelector = '#regiNo';

    // 화면에 표시되는 성별 필드 (JSP에서 path="gender"에 바인딩된 읽기 전용 input)
    const genderDisplaySelector = '#genderSelect';

    // 폼 전송용으로 사용되는 숨겨진 필드 (JSP에서 path="gender"에 바인딩된 hidden)
    const hiddenGenderSelector = '#hiddenGender';

    // 주민번호 입력 필드에 'input' 이벤트를 바인딩하여 값이 변경될 때마다 성별을 업데이트합니다.
    $(regiNoSelector).on('input', function() {
        updateGenderFromRegiNo(regiNoSelector, genderDisplaySelector, hiddenGenderSelector);
    });

    // 페이지 로드 시, 이미 값이 있다면 한 번 실행 (초기화)
    updateGenderFromRegiNo(regiNoSelector, genderDisplaySelector, hiddenGenderSelector);
});


// ----------------------------------------------------------------------------------
// calculateExpectedGradYear 함수는 건드리지 않고 그대로 유지합니다.
// ----------------------------------------------------------------------------------
function calculateExpectedGradYear(entranceDateSelector, gradYearSelector) {
    // jQuery 대신 순수 JavaScript를 사용하여 의존성 관리
    const entranceDateElement = document.querySelector(entranceDateSelector);
    const gradYearField = document.querySelector(gradYearSelector);

    if (!entranceDateElement || !gradYearField) {
        console.error("Entrance Date or Grad Year selector not found.");
        return;
    }

    // --- 1. 입학일 값 읽기 ---
    const entranceDateStr = entranceDateElement.value;

    let expectedYear = '';

    // YYYY-MM-DD 형식의 날짜가 유효한지 확인
    if (entranceDateStr && /^\d{4}-\d{2}-\d{2}$/.test(entranceDateStr)) {
        try {
            // 입학 연도 추출
            const entranceYear = parseInt(entranceDateStr.substring(0, 4), 10);

            // 4년제 학사 과정 기준으로 4년 추가
            // 계산식: expectedYear = entranceYear + 4
            expectedYear = (entranceYear + 4).toString();
        } catch (e) {
            console.warn("Failed to parse entrance year:", e);
        }
    }

    // --- 2. 졸업 연도 필드에 결과 값 기입 ---
    // Input 필드에 값을 설정 (HTML 구조상 input임을 가정)
    gradYearField.value = expectedYear;
}
