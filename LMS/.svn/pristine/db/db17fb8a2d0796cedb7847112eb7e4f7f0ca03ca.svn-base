/** * @author 정태일
 * @since 2025. 10. 16.
 * @description 학사공지 등록/수정 폼 페이지의 UI 로직 및 유효성 검사 담당
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 * * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 10. 16.     	정태일            최초 생성 (portalNoticeForm.js 복사)
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
	        
	        document.querySelectorAll('.keep-file-checkbox:checked').forEach(cb => {
	            formData.append('keepFiles', cb.value);
	        });
	        
	        const noticeIdInput = document.querySelector('input[name="noticeId"]');
	        const isEditMode = noticeIdInput && noticeIdInput.value;
	        
	        let url;
	        if (isEditMode) {
	            url = `/portal/academicnotice/modify/${noticeIdInput.value}`;
	        } else {
	            url = `/portal/academicnotice/create`;
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
    
    const existingFileId = document.getElementById('existingFileId')?.value;
    if (existingFileId) {
        loadExistingFiles(existingFileId);
    }
});

function loadExistingFiles(fileId) {
    fetch(`/portal/file/list/${fileId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('파일 목록을 불러올 수 없습니다.');
            }
            return response.json();
        })
        .then(files => {
            const listContainer = document.getElementById('existingFilesList');
            if (!listContainer || !files || files.length === 0) return;
            let html = '<ul class="existing-files-list">';
            files.forEach(file => {
                const fileSizeMB = (file.fileSize / 1024 / 1024).toFixed(2);
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
}

function updateFileChangeStatus() {
    const checkboxes = document.querySelectorAll('.keep-file-checkbox');
    const checkedCount = Array.from(checkboxes).filter(cb => cb.checked).length;
    const totalCount = checkboxes.length;
    
    if (checkedCount < totalCount) {
        console.log(`${totalCount - checkedCount}개 파일이 삭제 예정입니다.`);
    }
}

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
