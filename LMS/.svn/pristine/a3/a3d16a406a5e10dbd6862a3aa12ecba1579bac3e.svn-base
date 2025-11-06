/** * @author 정태일
 * @since 2025. 9. 30.
 * @description 공지사항 등록/수정 폼 페이지의 UI 로직 및 유효성 검사 담당
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 * * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 9. 30.     	정태일            최초 생성
 * 2025.10. 01.			정태일			첨부파일 기능 추가
 * 2025.11. 03.			정태일			시연용 긴급게시글 스크립트 추가
 * </pre>
 */

var noticeEditorInstance; // CKEditor 인스턴스를 저장할 전역 변수

document.addEventListener('DOMContentLoaded', () => {
	
		// 등록폼, 수정폼 - 취소 버튼
	const cancelButton = document.getElementById('cancelButton');
	if (cancelButton) {
	    cancelButton.addEventListener('click', goBack);
	}
	
	const editCancelButton = document.getElementById('edit-cancelBtn');
	if(editCancelButton) {
		editCancelButton.addEventListener('click', goBack);
	}
	
	
	
	
	

    // CKEditor 초기화
    const contentTextarea = document.querySelector('#content');
    if (contentTextarea && typeof ClassicEditor !== 'undefined') {
        ClassicEditor
            .create(contentTextarea)
            .then(editor => {
                noticeEditorInstance = editor;
            })
            .catch(error => console.error('CKEditor 초기화 오류:', error));
    }


    // 폼 버튼 및 UI 이벤트
    // 파일 선택 시 파일명 표시 - 파일 기능 구현 시 수정 
    const fileInput = document.querySelector('#files');
    const fileNameDiv = document.getElementById('selectedFileName');

    fileInput?.addEventListener('change', function(e) {
        const files = e.target.files;
        fileNameDiv.innerHTML = ''; // 기존 목록 초기화
        
        if (files.length > 0) {
            let listHtml = '<ul style="margin: 0; padding-left: 20px;">';
            for (let i = 0; i < files.length; i++) {
                // 파일 크기를 MB 단위로 표시
                const fileSizeMB = (files[i].size / 1024 / 1024).toFixed(2);
                listHtml += `<li>${files[i].name} (${fileSizeMB} MB)</li>`;
            }
            listHtml += '</ul>';
            fileNameDiv.innerHTML = listHtml;
        } else {
            fileNameDiv.textContent = '선택된 파일 없음';
        }
    });



//-----------------------------------------------------------------




    // ============================================
	// 폼 제출 시 유효성 검사 및 AJAX 전송 - 등록, 수정
	// ============================================
	const noticeForm = document.getElementById('noticeForm');
	if (noticeForm) {
	    noticeForm.addEventListener('submit', function(e) {
	        e.preventDefault();
	        
	        // CKEditor 내용 가져오기
	        const contentRaw = noticeEditorInstance 
	            ? noticeEditorInstance.getData() 
	            : document.getElementById('content').value;
	
	        const contentText = contentRaw.replace(/<[^>]*>/g, '').trim();
	        const title = document.getElementById('title').value.trim();
//	        const stfDeptName = document.getElementById('stfDeptName').value.trim();
	        
	        // 필수 항목 검사
	        if (!title || contentText.length === 0 ) { 
	            Swal.fire({
			        title: '필수 항목을 모두 입력하세요.', 
			        icon: 'warning',
			        iconColor: '#7bcfe4',
			        showCancelButton: true, 
			        confirmButtonColor: '#EF5350',
			        cancelButtonColor: '#1E88E5',
			        confirmButtonText: '취소', 
			        cancelButtonText: '계속 작성' 
			    }).then((result) => {
			        if (result.isConfirmed) {
			            history.back();
			        } 
			    });
	            if (contentText.length === 0 && noticeEditorInstance) {
	                noticeEditorInstance.editing.view.focus();
	            }
	            return;
	        }
	        
	        // 내용 최대 길이 검사
	        if (contentRaw.length > 4000) {
	            Swal.fire({
			        title: '내용은 4000자를 초과할 수 없습니다.', 
			        text: `(현재: ${contentRaw.length}자)`, 
			        icon: 'warning',
			        iconColor: '#7bcfe4',
			        showCancelButton: true, 
			        confirmButtonColor: '#EF5350',
			        cancelButtonColor: '#1E88E5',
			        confirmButtonText: '취소', 
			        cancelButtonText: '계속 작성' 
			    }).then((result) => {
			        if (result.isConfirmed) {
			            history.back();
			        } 
			    });
	            return;
	        }
	        
	        // FormData 생성
	        const formData = new FormData(noticeForm);
	        
	        // CKEditor 내용 덮어쓰기
	        formData.set('content', contentRaw);
	        
	        // 유지할 기존 파일들 추가 (체크된 것만 - 수정 폼에만 해당) - 파일부분
	        document.querySelectorAll('.keep-file-checkbox:checked').forEach(cb => {
	            formData.append('keepFiles', cb.value);
	        });
	        
	        // 등록 vs 수정 구분
	        const noticeIdInput = document.querySelector('input[name="noticeId"]');
	        const isEditMode = noticeIdInput && noticeIdInput.value;
	        
	        // URL 결정
	        let url;
	        if (isEditMode) {
	            // 수정 모드
	            url = `/portal/notice/modify/${noticeIdInput.value}`;
	        } else {
	            // 등록 모드
	            url = `/portal/notice/create`;
	        }
	        
	        fetch(url, {
	            method: 'post',
	            body: formData
	        })
	        .then(response => response.json())
	        .then(data => {
	            if (data.status === 'success') {
			        window.location.href = data.redirectUrl;
			    } else {
			        alert(data.message);
			    }
	        })
	        .catch(error => {
	            console.error('폼 전송 오류:', error);
	            Swal.fire({
				  icon: "error",
				  text: '처리 중 오류가 발생했습니다. 다시 시도해주세요.',
				  confirmButtonText: '닫기', 
				  confirmButtonColor: '#1E88E5'
				});
    			
	        });
	    });
	}
    
    // ============================================
    // 기존 파일 목록 로드 (수정 모드일 때)
    // ============================================
    const existingFileId = document.getElementById('existingFileId')?.value;
    if (existingFileId) {
        loadExistingFiles(existingFileId);
    }

    const fillButton = document.getElementById('fillUrgentNoticeBtn');
    if (fillButton) {
        fillButton.addEventListener('click', () => {
            // 제목 필드 채우기
            const titleInput = document.getElementById('title');
            if (titleInput) {
                titleInput.value = '[긴급] 전체 서버 점검 안내 (오늘 23:00 ~ 24:00)';
            }

            // 긴급 체크박스 선택
            const urgentCheckbox = document.getElementById('isUrgent');
            if (urgentCheckbox) {
                urgentCheckbox.checked = true;
            }

            // CKEditor 내용 채우기 (noticeEditorInstance 변수 사용)
            if (typeof noticeEditorInstance !== 'undefined' && noticeEditorInstance) {
                const content = `
                    <p>안녕하세요, JSU 대학 정보처입니다.</p>
                    <p>보다 안정적인 서비스 제공을 위해 아래와 같이 전체 서버 점검을 실시할 예정입니다.</p>
                    <p>&nbsp;</p>
                    <ul>
                        <li><strong>점검 일시:</strong> 오늘 23:00 ~ 24:00 (약 1시간)</li>
                        <li><strong>점검 내용:</strong> 시스템 안정성 강화를 위한 데이터베이스 및 네트워크 장비 업데이트</li>
                        <li><strong>서비스 영향:</strong> 점검 시간 동안 포털 및 LMS 시스템 접속이 일시적으로 중단될 수 있습니다.</li>
                    </ul>
                    <p>&nbsp;</p>
                    <p>이용에 불편을 드려 죄송하며, 최대한 신속하게 점검을 완료하도록 노력하겠습니다.</p>
                    <p>감사합니다.</p>
                `;
                noticeEditorInstance.setData(content);
            } else {
                // CKEditor가 로드되지 않았을 경우를 대비한 폴백
                const contentTextarea = document.getElementById('content');
                if (contentTextarea) {
                    contentTextarea.value = "긴급 공지 내용 예시입니다.";
                }
                alert("에디터가 아직 로드되지 않았습니다. 잠시 후 다시 시도해주세요.");
            }
        });
    }
}); // DOMContentLoaded 끝

/**
 * 기존 파일 목록을 서버에서 가져와 화면에 표시하는 함수 - 수정 모드일 때
 */
function loadExistingFiles(fileId) {
    fetch(`/portal/file/list/${fileId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('파일 목록을 불러올 수 없습니다.');
            }
            return response.json();
        })
        .then(files => {
            const listContainer = document.getElementById('existingFilesList'); // 기존 파일 목록 요소 가져오기
            if (!listContainer || !files || files.length === 0) return;
            // 기존 파일이 존재할 경우
            let html = '<ul class="existing-files-list">';
            files.forEach(file => {
                const fileSizeMB = (file.fileSize / 1024 / 1024).toFixed(2); // 파일 사이즈 표시
                html += `
                    <li class="existing-file-item">
                        <label class="file-checkbox-label">
                            <input type="checkbox" 
                                   class="keep-file-checkbox" 
                                   value="${file.fileOrder}"
                                   checked>
                            <span class="file-info">
                                📎 ${file.originName}.${file.extension}
                                <span class="file-size">(${fileSizeMB} MB)</span>
                            </span>
                        </label>
                    </li>
                `;
            });
            html += '</ul>';
            
            listContainer.innerHTML = html;
            
            document.querySelectorAll('.keep-file-checkbox').forEach(checkbox => {
                checkbox.addEventListener('change', updateFileChangeStatus);
            });
        })
        .catch(error => {
            console.error('파일 목록 로드 오류:', error);
        });
} // loadExistingFiles() 끝

/**
 * 파일 변경 상태 표시 (console.log로 확인용)
 */
function updateFileChangeStatus() {
    const checkboxes = document.querySelectorAll('.keep-file-checkbox');
    const checkedCount = Array.from(checkboxes).filter(cb => cb.checked).length;
    const totalCount = checkboxes.length;
    
    if (checkedCount < totalCount) {
        console.log(`${totalCount - checkedCount}개 파일이 삭제 예정입니다.`);
    }
}


/*

// 폼 제출 유효성 검사 (CKEditor 데이터 추출 포함)
document.getElementById('noticeForm')?.addEventListener('submit', function(e) {
    
    // CKEditor 내용
    const contentRaw = noticeEditorInstance ? noticeEditorInstance.getData() : document.getElementById('content').value;
    // HTML 태그를 제거한 순수 텍스트로 빈 값 확인
    const contentText = contentRaw.replace(/<[^>]*>/g, '').trim(); 
    
    const title = document.getElementById('title').value.trim();
    
//    const stfDeptName = document.getElementById('stfDeptName').value.trim();
    
    // 필수 항목 검사
    if (!title || contentText.length === 0) { 
        e.preventDefault();
        // 커스텀 모달 UI or sweetalert
        alert('필수 항목을 모두 입력해주세요. (내용 포함)');

        // CKEditor 필드가 비었을 경우
        if (contentText.length === 0 && noticeEditorInstance) {
             noticeEditorInstance.editing.view.focus();
        }
        return;
    }
    
    // 내용 최대 길이 검사
    if (contentRaw.length > 4000) {
        e.preventDefault();
        // 커스텀 모달 UI or sweetalert
        alert(`내용은 4000자를 초과할 수 없습니다. (현재: ${contentRaw.length}자)`);
        return;
    }

    // 폼 제출 직전에 <textarea>에 CKEditor의 최종 HTML 내용 넣기
    document.getElementById('content').value = contentRaw; 
});


*/


// 전역 유틸리티 함수
// 취소 버튼 로직
function goBack() {
    Swal.fire({
        title: '정말 취소하시겠습니까?', 
        text: '작성 중인 내용이 저장되지 않고 사라집니다.', 
        icon: 'warning',
        iconColor: '#7bcfe4',
        showCancelButton: true, 
        confirmButtonColor: '#EF5350',
        cancelButtonColor: '#1E88E5',
        confirmButtonText: '취소', 
        cancelButtonText: '계속 작성' 
    }).then((result) => {
        if (result.isConfirmed) {
            history.back();
        } 
    });
}

