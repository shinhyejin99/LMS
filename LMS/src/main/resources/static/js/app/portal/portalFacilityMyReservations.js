/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      수정자           수정내용
 *  -----------   -------------    ---------------------------
 * 2025. 10. 22.     정태일            최초 생성
 *
 * </pre>
 */
document.addEventListener('DOMContentLoaded', function() {
    const cancelButtons = document.querySelectorAll('.cancel-btn');
    cancelButtons.forEach(button => {
        button.addEventListener('click', function() {
            const reserveId = this.dataset.reserveId;
            const placeCd = this.dataset.placeCd;
            const clickedButton = this; // `this`를 변수에 저장

            clickedButton.disabled = true; // 버튼 비활성화

            Swal.fire({
                title: '예약 취소',
                text: "정말로 이 예약을 취소하시겠습니까?",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '네, 취소합니다.',
                cancelButtonText: '아니요'
            }).then((result) => {
                if (result.isConfirmed) {
                    fetch(`/api/facilities/${placeCd}/reservations/${reserveId}`, {
                        method: 'DELETE',
                    })
                    .then(response => {
                        if (response.ok) {
                            Swal.fire(
                                '취소 완료!',
                                '예약이 성공적으로 취소되었습니다.',
                                'success'
                            ).then(() => {
                                location.reload();
                            });
                        } else {
                            response.text().then(text => {
                                Swal.fire(
                                    '취소 실패',
                                    text || '예약 취소에 실패했습니다.',
                                    'error'
                                );
                                clickedButton.disabled = false; // 오류 발생 시 버튼 다시 활성화
                            });
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        Swal.fire(
                            '오류',
                            '예약 취소 중 오류가 발생했습니다.',
                            'error'
                        );
                        clickedButton.disabled = false; // 오류 발생 시 버튼 다시 활성화
                    });
                } else {
                    clickedButton.disabled = false; // 취소 시 버튼 다시 활성화
                }
            });
        });
    });
});