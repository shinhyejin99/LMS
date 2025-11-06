/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -----------   -------------    ---------------------------
 * 2025. 10. 13.     정태일            최초 생성
 * 2025. 10. 17.     정태일            swal.fire 변경
 * 2025. 10. 20.     정태일           탭 기능 이전 및 '+' 링크 동적 변경 기능 추가
 * 2025. 10. 30.     김수현           이미지 슬라이더 기능 추가
 *
 * </pre>
 */

document.addEventListener('DOMContentLoaded', function() {

    // ============== 이미지 슬라이더 기능  ================
    let slideIndex = 0; // 현재 표시되고 있는 슬라이드의 번호를 저장 (0=첫번째 슬라이드)
    let slideTimer; // 자동 슬라이드 전환을 위한 변수
    const slides = document.querySelectorAll('.slide'); // slide인 걸 찾아서 NodeList 형태로 저장
    const indicators = document.querySelectorAll('.indicator'); // indicator 를 찾아서 NodeList 형태로 저장

    // 슬라이더랑 인디케이터 요소가 모두 있을 때만 실행
    if (slides.length > 0 && indicators.length > 0) {

        // 슬라이드 표시 함수
        function showSlides(n) {
            if (n >= slides.length) { // 슬라이드의 총 개수와 같거나 크면 = 마지막 슬라이드를 지나친 경우임!
                slideIndex = 0; // 슬라이드 인덱스가 0. 즉, 1번째 슬라이드로 돌아가는 것
            }
            if (n < 0) { // 인덱스가 0보다 작다면 = 1번째 슬라이드보다 앞선 거임!
                slideIndex = slides.length - 1; // slideIndex를 마지막 슬라이드로 재설정함
            }

            slides.forEach(slide => slide.classList.remove('active')); // 모두 active 클래스 제거
            indicators.forEach(indicator => indicator.classList.remove('active')); // 모두 active 클래스 제거

            slides[slideIndex].classList.add('active'); // 현재 슬라이드에 active 클래스 추가
            indicators[slideIndex].classList.add('active'); // 현재 인디케이터에 active 클래스 추가
        }

        // 수동 슬라이드 변경 (좌우 화살표)
        window.changeSlide = function(n) {
            clearTimeout(slideTimer); // 현재 실행 중인 자동 슬라이드 타이머를 취소
            slideIndex += n;
            showSlides(slideIndex);
            autoSlide(); // 직접 클릭 후에 자동 슬라이드 타이머 다시 시작
        }

        // 특정 슬라이드로 이동 (인디케이터 클릭)
        window.currentSlide = function(n) {
            clearTimeout(slideTimer); // 현재 실행 중인 자동 슬라이드 타이머를 취소
            slideIndex = n;
            showSlides(slideIndex);
            autoSlide();
        }

        // 자동 슬라이드 재생
        function autoSlide() {
            slideTimer = setTimeout(() => {
                slideIndex++; // 다음 슬라이드
                showSlides(slideIndex); // 새로운 인덱스로 슬라이드 표시
                autoSlide(); // 재귀적 호출로 타이머 반복 설정(계속 자동으로 움직이도록)
            }, 5000); // 5초마다 자동 전환
        }

        // 초기 슬라이드 표시 및 자동 재생 시작
        showSlides(slideIndex);
        autoSlide();
    }
    // ============== 이미지 슬라이더 기능 끝!!!! ================

    // =============== 기존 ===================


    // === 탭 기능 (공지사항/학사공지) ===
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');
    const moreLink = document.querySelector('.section-notice-group .more-link');

    if (tabButtons.length > 0 && tabPanes.length > 0 && moreLink) {
        // 탭 버튼 클릭 이벤트 처리
        tabButtons.forEach(button => {
            button.addEventListener('click', function() {
                // 모든 버튼과 탭 창에서 'active' 클래스 제거
                tabButtons.forEach(btn => btn.classList.remove('active'));
                tabPanes.forEach(pane => pane.classList.remove('active'));

                // 클릭된 버튼과 해당하는 탭 창에 'active' 클래스 추가
                this.classList.add('active');
                const targetTabId = this.getAttribute('data-tab');
                const targetPane = document.getElementById(targetTabId);
                if (targetPane) {
                    targetPane.classList.add('active');
                }

                // '+' 링크 주소 변경
                if (targetTabId === 'academic-pane') {
                    moreLink.href = '/portal/academicnotice/list';
                } else {
                    moreLink.href = '/portal/notice/list';
                }
            });
        });

        // 페이지 로드 시 초기 활성 탭 설정
        const initialActiveButton = document.querySelector('.tab-btn.active');
        if (initialActiveButton) {
            const initialTabId = initialActiveButton.getAttribute('data-tab');
            const initialPane = document.getElementById(initialTabId);
            if (initialPane) {
                initialPane.classList.add('active');
            }
            // 초기 링크 설정
            if (initialTabId === 'academic-pane') {
                moreLink.href = '/portal/academicnotice/list';
            } else {
                moreLink.href = '/portal/notice/list';
            }
        }
    }

    // === 비밀번호 변경 모달 ===
    const modal = document.getElementById("pwChangeModal");
    const btn   = document.getElementById("openPwModal");
    const span  = document.getElementsByClassName("close-button")[0];
    const form  = document.getElementById('pwChangeForm');

    if (modal && btn && span && form) {

		// 페이지 로드 시 모달 숨김 처리
		modal.style.display = "none";

        // '비밀번호변경' 버튼 클릭 시 모달 열기
        btn.onclick = function() {
            form.reset();
            modal.style.display = "flex";
        }

        // 닫기 버튼 (x) 클릭 시 모달 닫기
        span.onclick = function() {
            modal.style.display = "none";
        }

        // 모달 바깥 클릭 시 닫기
        window.addEventListener('click', function(event) {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        });

        // === 비밀번호 변경 비동기 요청 ===
        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword     = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (newPassword !== confirmPassword) {
                Swal.fire({
                    icon: "error",
                    text: "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.",
                    confirmButtonText: '닫기',
                    confirmButtonColor: '#1E88E5'
                });
                return;
            }

            const formData = new URLSearchParams();
            formData.append('currentPassword', currentPassword);
            formData.append('newPassword', newPassword);

            // 🔐 CSRF 관련 주석 (현재는 비활성화 상태)
            // const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
            // const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;

            fetch('/portal/user/ajaxchangepassword', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    // [CSRF 활성화 시 아래 주석 해제]
                    // [csrfHeader]: csrfToken
                },
                body: formData
            })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => Promise.reject(err));
                }
                return response.json();
            })
            .then(data => {
                Swal.fire({
                    icon: "success",
                    text: data.message,
                    confirmButtonText: '닫기',
                    confirmButtonColor: '#1E88E5'
                });
                modal.style.display = "none";

                // 성공 시 2초 후 자동 로그아웃
                setTimeout(() => {
                    const logoutForm = document.createElement('form');
                    logoutForm.method = 'POST';
                    logoutForm.action = '/logout';
                    document.body.appendChild(logoutForm);
                    logoutForm.submit();
                }, 2000);
            })
            .catch(error => {
                Swal.fire({
                    icon: "error",
                    text: error.message || '비밀번호 변경 중 오류가 발생했습니다.',
                    confirmButtonText: '닫기',
                    confirmButtonColor: '#1E88E5'
                });
                console.error('Error:', error);
            });
        });
    }
});