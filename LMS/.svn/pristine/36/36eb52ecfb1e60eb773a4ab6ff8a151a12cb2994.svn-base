/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 02.     	신혜진           최초 생성
 *
 * </pre>
 */


//  예금주(이름) 자동 업데이트 및 표시 함수
function updateDepositorName() {
	// 🟢 통합된 'userName'을 참조
	const userNameInput = document.getElementById('userName');
	const fullNameDisplay = document.getElementById('full-name-display');

    // ⭐ DTO 유효성 검사 통과를 위한 hidden 필드 참조 ⭐
    const lastNameInput = document.getElementById('lastName');
	const firstNameInput = document.getElementById('firstName');

	// 요소 유효성 검사
	if (!userNameInput || !fullNameDisplay || !lastNameInput || !firstNameInput) {
		return;
	}

	// 1. 값 추출 및 공백 제거
	const fullName = userNameInput.value.trim().replace(/\s/g, '') || ''; // 띄어쓰기 제거

	// 2. 예금주 표시 영역에 설정
	fullNameDisplay.textContent = fullName;

    // 3. ⭐ DTO 바인딩을 위해 성/이름을 분리하여 숨겨진 필드에 설정 ⭐
    // (첫 글자를 성으로, 나머지를 이름으로 가정하여 서버의 @NotBlank 통과 목적)
    if (fullName.length > 0) {
        // 첫 글자를 성으로 사용
        lastNameInput.value = fullName.charAt(0);
        // 나머지 글자를 이름으로 사용 (글자 하나만 입력된 경우 이름은 빈 문자열)
        firstNameInput.value = fullName.substring(1);
    } else {
        lastNameInput.value = '';
        firstNameInput.value = '';
    }
}


// 증명사진 파일 변경 시 미리보기 로직
document.getElementById('photoFile')?.addEventListener('change', function(event) {
	const file = event.target.files[0];
	const preview = document.getElementById('photo-preview');
    const previewText = document.getElementById('photo-preview-text');

	if (file && preview) {
		const reader = new FileReader();
		reader.onload = function(e) {
			preview.src = e.target.result;
            preview.style.display = 'block';
            if (previewText) previewText.style.display = 'none';
		};
		reader.readAsDataURL(file);
	}
});


//  ⭐ 최종: DOMContentLoaded 이벤트 내에서 모든 초기화 및 이벤트 연결 (로직 통합 관리) ⭐
document.addEventListener('DOMContentLoaded', function() {

	// 1. 이름 필드 참조 및 이벤트 연결 (예금주 및 hidden 필드 설정)
	const userNameInput = document.getElementById('userName');

	if (userNameInput) {
		// 입력(input) 이벤트에 업데이트 함수 연결
		userNameInput.addEventListener('input', updateDepositorName);

		// 페이지 로드 시 초기값 설정
		updateDepositorName();
	}

	// 2. 주민등록번호 & 성별 자동 입력 로직 (updateGenderFromRegiNo.js 의존성)
	const regiNoInput = document.getElementById('regiNo');
	const genderSelect = document.getElementById('genderSelect');

	if (regiNoInput && genderSelect && typeof updateGenderFromRegiNo === 'function') {
		const regiNoId = 'regiNo';
		const genderId = 'genderSelect';

		// 입력(input) 시 마다 실행
		regiNoInput.addEventListener('input', function() {
			updateGenderFromRegiNo(regiNoId, genderId);
		});
		// 필드에서 포커스를 잃었을 때 (change) 실행
		regiNoInput.addEventListener('change', function() {
			updateGenderFromRegiNo(regiNoId, genderId);
		});

		// 페이지 로드 시 초기값 설정
		updateGenderFromRegiNo(regiNoId, genderId);
	}
});