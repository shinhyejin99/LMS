/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      수정자           수정내용
 *  -----------   -------------    ---------------------------
 * 2025. 10. 23.     정태일           증명서 신청 폼, 미리보기 모달 수정
 *
 * </pre>
 */

// 증명서 신청 폼 제출 이벤트 리스너
document.getElementById('certificate-form').addEventListener('submit', function(event) {
    event.preventDefault(); // 기본 폼 제출 동작 방지

    // 폼 필드 값 가져오기
    const certType = document.getElementById('cert-type').value;
    const copies = document.getElementById('cert-copies').value;
    const submissionPlace = document.getElementById('cert-submission').value;
    const purpose = document.getElementById('cert-purpose').value;

    // 유효성 검사: 증명서 종류 선택 여부
    if (!certType) {
        Swal.fire({
            icon: 'warning',
            title: '선택 필요',
            text: '증명서 종류를 선택해주세요.',
            confirmButtonText: '확인'
        });
        return;
    }
    // 유효성 검사: 발급 부수 1부 이상 입력 여부
    if (!copies || parseInt(copies, 10) < 1) {
        Swal.fire({
            icon: 'warning',
            title: '입력 필요',
            text: '발급 부수를 1부 이상 입력해주세요.',
            confirmButtonText: '확인'
        });
        return;
    }

    // 백엔드로 전송할 요청 데이터 객체 생성 (CertificateReqVO 구조에 맞춤)
    const requestData = {
        certificateCd: certType,
        copies: parseInt(copies, 10), // 숫자로 변환하여 전송
        submissionPlace: submissionPlace,
        purpose: purpose
        // userId, requestAt, expireAt, statusCd는 백엔드(Service)에서 설정
    };

    // 증명서 신청 API 호출
    fetch('/portal/certificate', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json' // JSON 형태로 데이터 전송 명시
        },
        body: JSON.stringify(requestData) // JavaScript 객체를 JSON 문자열로 변환
    })
    .then(response => {
        // HTTP 응답 상태 확인
        if (response.ok) {
            return response.text(); // 성공 시 응답 텍스트 반환
        }
        // 오류 발생 시 에러 메시지 파싱 및 throw
        return response.text().then(text => { throw new Error(text || '신청 처리 중 오류가 발생했습니다.'); });
    })
    .then(message => {
        // 신청 성공 시 SweetAlert2로 메시지 표시 후 페이지 새로고침
        Swal.fire({
            icon: 'success',
            title: '신청 완료',
            text: message,
            confirmButtonText: '확인'
        }).then(() => {
            window.location.reload(); // 발급 내역 갱신을 위해 페이지 새로고침
        });
    })
    .catch(error => {
        // 신청 실패 시 SweetAlert2로 오류 메시지 표시
        console.error('Error:', error);
        Swal.fire({
            icon: 'error',
            title: '신청 실패',
            text: error.message,
            confirmButtonText: '확인'
        });
    });
});

// DOMContentLoaded 이벤트: 문서 로드 완료 후 실행
document.addEventListener('DOMContentLoaded', () => {
    // PDF 미리보기 모달 관련 요소 가져오기
    const pdfPreviewModal = document.getElementById('pdfPreviewModal');
    const pdfViewer = document.getElementById('pdfViewer');
    const closeButton = pdfPreviewModal.querySelector('.close-button');

    // 모달 닫기 버튼 클릭 이벤트 리스너
    closeButton.addEventListener('click', () => {
        pdfPreviewModal.style.display = 'none'; // 모달 숨기기
        pdfViewer.src = ''; // iframe src 초기화하여 PDF 로딩 중지 및 메모리 해제
    });

    // 모달 외부 클릭 시 모달 닫기 이벤트 리스너
    window.addEventListener('click', (event) => {
        if (event.target === pdfPreviewModal) {
            pdfPreviewModal.style.display = 'none'; // 모달 숨기기
            pdfViewer.src = ''; // iframe src 초기화
        }
    });

    // 발급 내역 테이블의 미리보기 버튼 이벤트 리스너
    // 각 버튼에 data-cert-req-id 속성으로 요청 ID가 저장되어 있음
    document.querySelectorAll('.btn-preview-history').forEach(button => {
        button.addEventListener('click', function(event) {
            const certReqId = event.target.dataset.certReqId; // 버튼의 data-cert-req-id 값 가져오기
            
            // 유효성 검사: 요청 ID 존재 여부
            if (!certReqId) {
                Swal.fire({
                    icon: 'error',
                    title: '오류',
                    text: '증명서 요청 ID를 찾을 수 없습니다.',
                    confirmButtonText: '확인'
                });
                return;
            }

            // 발급된 증명서 미리보기 API 호출
            fetch(`/portal/certificate/preview-issued/${certReqId}`, { // 새로운 엔드포인트 사용
                method: 'GET'
            })
            .then(response => {
                // HTTP 응답 상태 확인
                if (response.ok) {
                    return response.blob(); // PDF 데이터를 Blob 형태로 받음
                }
                // 오류 발생 시 에러 메시지 파싱 및 throw
                return response.text().then(text => { throw new Error(text || '미리보기 데이터를 불러오는 중 오류가 발생했습니다.'); });
            })
            .then(blob => {
                // Blob 데이터를 URL로 변환하여 iframe에 로드
                const url = URL.createObjectURL(blob);
                pdfViewer.src = url; // iframe의 src 속성에 PDF URL 설정
                pdfPreviewModal.style.display = 'block'; // 모달 표시
                // 참고: URL.revokeObjectURL(url)은 모달이 닫힐 때 호출하는 것이 더 적절합니다.
            })
            .catch(error => {
                // 미리보기 실패 시 SweetAlert2로 오류 메시지 표시
                console.error('Error:', error);
                Swal.fire({
                    icon: 'error',
                    title: '미리보기 실패',
                    text: error.message,
                    confirmButtonText: '확인'
                });
            });
        });
    });
});