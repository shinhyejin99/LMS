/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 02.     	신혜진       단과대학을 변경할 때마다 소속학과 목록이 즉시 동적으로 필터링
 *
 * </pre>
 */

  // 1. DOM 요소와 전체 옵션 목록을 가져옵니다. (ready 밖으로 이동)
let $collegeSelect;
let $deptSelect;
let $allDeptOptions;

/**
 * 단과대학 선택 시 소속학과 목록을 필터링하는 함수 (⭐️ 전역 함수 ⭐️)
 */
function filterDepartments() {
    if (!$collegeSelect || !$deptSelect) return; // DOM 요소가 준비되지 않았으면 종료

    const selectedCollegeCd = $collegeSelect.val();
    const currentDeptValue = $deptSelect.val();

    // 1. 소속학과 select를 '선택' 옵션만 남기고 초기화합니다.
    $deptSelect.empty().append($('<option value="" data-college-cd="">선택</option>'));

    if (selectedCollegeCd) {
        // 2. 선택된 단과대학 코드와 일치하는 옵션만 필터링합니다.
        const $filteredOptions = $allDeptOptions.filter(function() {
            return $(this).data('college-cd') === selectedCollegeCd;
        });

        // 3. 필터링된 옵션을 소속학과 select에 추가합니다.
        $deptSelect.append($filteredOptions);
    } else {
        // '선택'이 된 경우, 모든 학과 옵션을 보여줍니다.
        $deptSelect.append($allDeptOptions);
    }

    // 4. 필터링 후, 이전에 선택된 값이 목록에 있다면 다시 선택합니다.
    if ($deptSelect.find(`option[value="${currentDeptValue}"]`).length) {
        $deptSelect.val(currentDeptValue);
    } else {
        $deptSelect.val('');
    }
}


$(document).ready(function() {

    // ⭐️ DOM 요소 초기화 (변수에 값 할당) ⭐️
    $collegeSelect = $('#collegeSelect');
    $deptSelect = $('#deptSelect');
    $allDeptOptions = $deptSelect.children().not(':first'); // '선택' 옵션 제외

    // ⭐️ 단과대학 select에 change 이벤트 리스너 연결
    $collegeSelect.on('change', function() {
        filterDepartments();
    });

    // ⭐️ 페이지 로드 시 초기 상태 적용
    filterDepartments();
});