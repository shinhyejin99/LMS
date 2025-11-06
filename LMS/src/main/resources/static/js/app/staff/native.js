/**
 * 행 확장(열고 닫기) 기능
 */
 document.addEventListener('DOMContentLoaded', function() {
        // 부트스트랩 객체(bootstrap)가 로드되었는지 확인
        if (typeof bootstrap === 'undefined' || typeof bootstrap.Collapse === 'undefined') {
            console.error("Bootstrap 5 Collapse component is not loaded. Ensure bootstrap.bundle.min.js is included.");
            return;
        }

        const clickableRows = document.querySelectorAll('.clickable-row');

        clickableRows.forEach(row => {
            // detailId는 '#detail-1'과 같은 ID 셀렉터 문자열입니다.
            const detailId = row.dataset.bsTarget;
            const detailElement = document.querySelector(detailId);


            let collapseInstance = null;

            if (detailElement) {

                row.addEventListener('click', function() {

                    // Collapse 인스턴스가 없다면 새로 생성합니다.
                    if (!collapseInstance) {
                        collapseInstance = new bootstrap.Collapse(detailElement, {
                            toggle: false // 자동 토글 방지
                        });
                    }

                    // 인스턴스를 사용하여 열거나 닫습니다.
                    collapseInstance.toggle();
                });


                // 상세 행이 열릴 때 (시각적 피드백)
                detailElement.addEventListener('show.bs.collapse', function () {
                    row.classList.add('table-primary'); // 배경색 변경 적용
                    row.classList.remove('collapsed'); // 아이콘 회전을 위한 클래스 제거
                });

                // 상세 행이 닫힐 때 (시각적 피드백 복원)
                detailElement.addEventListener('hide.bs.collapse', function () {
                    row.classList.remove('table-primary'); // 배경색 복원
                    row.classList.add('collapsed'); // 아이콘 회전을 위한 클래스 추가
                });
            }
        });
    });