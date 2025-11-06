/** * @author 김수현
 * @since 2025. 9. 27.
 * @description 채용정보 등록/수정 폼 페이지의 UI 로직 및 유효성 검사 담당
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 * * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 9. 27.     	김수현            최초 생성
 * 2025. 9. 29.			김수현			파일 업로드
 * 2025. 10. 1. 		김수현			수정할 때 파일 처리
 * 2025. 10. 31.		김수현			sweetAlert 수정, 등록할 때 한번 더 나오도록 추가
 * </pre>
 */

let jobEditorInstance; // CKEditor 인스턴스를 저장할 전역 변수

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
	//=================================
    // CKEditor 초기화
    const contentTextarea = document.querySelector('#content');
    if (contentTextarea && typeof ClassicEditor !== 'undefined') {
        ClassicEditor
            .create(contentTextarea)
            .then(editor => {
                jobEditorInstance = editor;
            })
            .catch(error => console.error('CKEditor 초기화 오류:', error));
    }
	// ================================
    // 폼 버튼 및 UI 이벤트
    // 파일 선택 시 파일명 표시
    const fileInput = document.querySelector('#files');
    const fileNameDiv = document.getElementById('selectedFileName');

    if (fileInput && fileNameDiv) {
        fileInput.addEventListener('change', function(e) {
            const files = e.target.files;
            fileNameDiv.innerHTML = ''; // 기존 목록 초기화

            if (files.length > 0) {
                let listHtml = '<ul style="margin: 0; padding-left: 20px;">';
                for (let i = 0; i < files.length; i++) {
                    const fileSizeMB = (files[i].size / 1024 / 1024).toFixed(2);
                    listHtml += `<li>${files[i].name} (${fileSizeMB} MB)</li>`;
                }
                listHtml += '</ul>';
                fileNameDiv.innerHTML = listHtml;
            } else {
                fileNameDiv.textContent = '선택된 파일 없음';
            }
        });
    }

    // ============================================
    // 접수 시작일/마감일 유효성 검사
    // ============================================
    const recStartDayInput = document.getElementById('recStartDay');
    const recEndDayInput = document.getElementById('recEndDay');

    if (recStartDayInput) {
        recStartDayInput.addEventListener('change', validateDates);
    }

    if (recEndDayInput) {
        recEndDayInput.addEventListener('change', validateDates);
    }

    // ============================================
    // 페이지 로드 시 날짜 기본값 설정 (등록 시에만)
    // ============================================
    // 값이 비어있을 때만 기본값 설정 (수정 시에는 기존값 유지)
    if (recStartDayInput && !recStartDayInput.value) {
        const today = new Date().toISOString().split('T')[0];
        recStartDayInput.value = today;

        // 마감일은 시작일 + 7일로 설정
        if (recEndDayInput && !recEndDayInput.value) {
            const nextWeek = new Date();
            nextWeek.setDate(nextWeek.getDate() + 7);
            recEndDayInput.value = nextWeek.toISOString().split('T')[0];
        }
    }

    // ============================================
	// 폼 제출 시 유효성 검사 및 AJAX 전송 - 등록, 수정
	// ============================================
	const jobForm = document.getElementById('jobForm');
	if (jobForm) {
	    jobForm.addEventListener('submit', function(e) {
	        e.preventDefault();

	        // CKEditor 내용 가져오기
	        const contentRaw = jobEditorInstance
	            ? jobEditorInstance.getData()
	            : document.getElementById('content').value;

	        const contentText = contentRaw.replace(/<[^>]*>/g, '').trim();
	        const title = document.getElementById('title').value.trim();
	        const stfDeptName = document.getElementById('stfDeptName').value.trim();
	        const agencyName = document.getElementById('agencyName').value.trim();
	        const recStartDay = document.getElementById('recStartDay').value;
	        const recEndDay = document.getElementById('recEndDay').value;

	        // 필수 항목 검사
	        if (!title || contentText.length === 0 || !stfDeptName || !agencyName || !recStartDay || !recEndDay) {
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
	            if (contentText.length === 0 && jobEditorInstance) {
	                jobEditorInstance.editing.view.focus();
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
	        const formData = new FormData(jobForm);

	        // CKEditor 내용 덮어쓰기
	        formData.set('content', contentRaw);

	        // 유지할 기존 파일들 추가 (체크된 것만 - 수정 폼에만 해당) - 파일부분
	        document.querySelectorAll('.keep-file-checkbox:checked').forEach(cb => {
	            formData.append('keepFiles', cb.value);
	        });

	        // 등록 vs 수정 구분
	        const recruitIdInput = document.querySelector('input[name="recruitId"]');
	        const isEditMode = recruitIdInput && recruitIdInput.value;

	        // URL 결정
	        let url;
	        if (isEditMode) {
	            // 수정 모드
	            url = `/portal/job/internal/modify/${recruitIdInput.value}`;
	        } else {
	            // 등록 모드
	            url = `/portal/job/internal/write`;
	        }

			// 한 번 더 물어보기!
	        Swal.fire({
	            title: isEditMode ? '채용 공고를 수정하시겠습니까?' : '채용 공고를 등록하시겠습니까?',
	            text: isEditMode ? '수정된 내용이 저장됩니다.' : '작성한 내용이 등록됩니다.',
	            icon: 'question',
	            iconColor: '#7bcfe4',
	            showCancelButton: true,
	            confirmButtonColor: '#1E88E5',
	            cancelButtonColor: '#6c757d',
	            confirmButtonText: isEditMode ? '수정' : '등록',
	            cancelButtonText: '취소'
	        }).then((result) => {
	            if (result.isConfirmed) {
	                // "확인" 버튼을 눌렀을 때만 fetch 처리
	                fetch(url, {
	                    method: 'post',
	                    body: formData
	                })
	                .then(response => response.json())
	                .then(data => {
	                    if (data.status === 'success') {
	                        // 성공 시 목록으로 이동
	                        Swal.fire({
	                            title: isEditMode ? '수정 완료!' : '등록 완료!',
	                            text: isEditMode ? '채용 공고가 수정되었습니다.' : '채용 공고가 등록되었습니다.',
	                            icon: 'success',
	                            iconColor: '#4CAF50',
	                            confirmButtonColor: '#1E88E5',
	                            confirmButtonText: '확인'
	                        }).then(() => {
	                            window.location.href = data.redirectUrl;
	                        });
	                    } else {
	                        // 서버에서 에러 메시지 반환
	                        Swal.fire({
	                            icon: 'error',
	                            title: '처리 실패',
	                            text: data.message,
	                            confirmButtonText: '확인',
	                            confirmButtonColor: '#EF5350'
	                        });
	                    }
	                })
	                .catch(error => {
	                    console.error('폼 전송 오류:', error);
	                    Swal.fire({
	                        icon: 'error',
	                        title: '오류 발생',
	                        text: '처리 중 오류가 발생했습니다. 다시 시도해주세요.',
	                        confirmButtonText: '닫기',
	                        confirmButtonColor: '#1E88E5'
	                    });
	                });
	            } // "취소" 버튼을 누르면 그대로 유지
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


// 전역 유틸리티 함수
// 폼 작성 취소 버튼 로직
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

// 접수일자 유효성 검사 로직
function validateDates() {
    const startDate = document.getElementById('recStartDay')?.value;
    const endDate = document.getElementById('recEndDay')?.value;

    if (startDate && endDate && startDate > endDate) {

        Swal.fire({
		  icon: "error",
		  text: "접수 시작일이 마감일보다 늦을 수 없습니다.",
		  confirmButtonText: '닫기',
		  confirmButtonColor: '#1E88E5'
		});
        document.getElementById('recEndDay').value = '';
    }
}



