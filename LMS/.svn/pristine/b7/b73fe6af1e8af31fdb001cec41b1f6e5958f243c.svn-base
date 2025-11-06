/** 
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 1.     	김수현            최초 생성
 *
 * </pre>
 */
// 폼 제출
const myForm = document.getElementById('info-form');
myForm.addEventListener('submit', function(e) {
    e.preventDefault();
    
    // 필수 입력 확인
    if (!this.checkValidity()) {
        this.reportValidity();
        return;
    }

    Swal.fire({
        title: '정보를 수정하시겠습니까?',
        icon: 'question',
        iconColor: '#7bcfe4',
        showCancelButton: true,
        confirmButtonText: '수정',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            this.submit(); // 폼 제출
        }
    });
});

// 전화번호 포맷팅
function formatPhone(input) {
    let value = input.value.replace(/[^0-9]/g, '');
    if (value.length > 11) value = value.slice(0, 11);
    
    if (value.length > 7) {
        input.value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7);
    } else if (value.length > 3) {
        input.value = value.slice(0, 3) + '-' + value.slice(3);
    } else {
        input.value = value;
    }
}


const mobile = document.getElementById('mobile')
mobile.addEventListener('input', function() {
    formatPhone(this);
});

const emergencyContact = document.getElementById('emergency_contact');
emergencyContact.addEventListener('input', function() {
    formatPhone(this);
});

// Redirect 후 메시지 표시
window.addEventListener('DOMContentLoaded', function() {
    const success = document.querySelector('[data-success-message]');
    const error = document.querySelector('[data-error-message]');
    
    if (success) {
        Swal.fire({
            icon: 'success',
            title: '수정 완료',
            text: success.dataset.successMessage
        });
    }
    
    if (error) {
        Swal.fire({
            icon: 'error',
            title: '수정 실패',
            text: error.dataset.errorMessage
        });
    }
});