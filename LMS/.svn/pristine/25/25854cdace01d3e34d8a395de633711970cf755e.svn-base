/**
 *
 */

 document.addEventListener('DOMContentLoaded', () => {
	    document.getElementById('create-request').addEventListener('click', createTuitionRequest);


    /**
     * 등록금 납부요청 생성
     */
    async function createTuitionRequest() {
        const result = await Swal.fire({
            title: '등록금 납부요청 생성',
            text: '등록금 납부요청을 생성하시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: '네',
            cancelButtonText: '취소'
        });

        if (!result.isConfirmed) {
            return; // 사용자가 취소를 눌렀을 경우 함수 종료
        }

        // 로딩 상태 표시
        Swal.fire({
            title: '요청 중...',
            text: '데이터를 처리하고 있습니다. 잠시만 기다려 주세요.',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        try {
            const response = await fetch('/lms/staff/tuition/request/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            Swal.close();

            if (!response.ok) {
                throw new Error(`HTTP 오류: ${response.status}`);
            }

            const data = await response.json();

            if (data.success) {
                // 성공 알림
                await Swal.fire({
                    title: '성공!',
                    text: data.message,
                    icon: 'success'
                });

            } else {
                // 서버 처리 실패 알림
                await Swal.fire({
                    title: '오류',
                    text: '오류: ' + data.message,
                    icon: 'error'
                });
            }
        } catch (error) {
            // 시스템 또는 네트워크 오류 알림
            Swal.close();
            await Swal.fire({
                title: '시스템 오류',
                text: '시스템 오류가 발생했습니다. 자세한 내용은 콘솔을 확인해주세요.',
                icon: 'error'
            });
            console.error(error);
        }
    }
});