/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 14.     	김수현            최초 생성
 *	2025. 10. 25.		김수현			첨부파일 필수 추가
 * </pre>
 */

document.addEventListener('DOMContentLoaded', () => {

	const affilChangeCdSelect = document.getElementById('affilChangeCd'); 	// 신청 구분 선택(전과/복수전공/부전공)
    const targetDeptCdSelect = document.getElementById('targetDeptCd');		// 목표학과
    const noticeBox = document.getElementById('noticeBox');					// 유의사항
    const deptHelp = document.getElementById('deptHelp');
    const affilApplyForm = document.getElementById('affilApplyForm');		// 신청 폼

	// 유의사항 내용
    const NOTICES = {
        'MJ_TRF': {
            title: '⚠️ 전과 신청 시 유의사항',
            items: [
                '전과는 같은 단과대학 내에서만 가능합니다.',
                '1학년과 4학년은 전과 신청이 불가능합니다.',
                '평균 학점 2.5 이상이어야 신청 가능합니다.',
                '전과 승인 후에는 이전 학과로 복귀할 수 없습니다.',
                '신청 후 처리까지 평균 5~7일 소요됩니다.'
            ]
        },
        'MJ_DBL': {
            title: '⚠️ 복수전공 신청 시 유의사항',
            items: [
                '복수전공은 본인의 전공 외 다른 학과를 추가로 이수하는 제도입니다.',
                '평균 학점 2.5 이상이어야 신청 가능합니다.',
                '복수전공 이수 학점은 36학점입니다.',
                '복수전공 이수 중에는 부전공 신청이 불가능합니다.',
                '졸업 시 복수전공 이수 요건을 충족해야 복수전공 학위를 받을 수 있습니다.'
            ]
        },
        'MJ_SUB': {
            title: '⚠️ 부전공 신청 시 유의사항',
            items: [
                '부전공은 본인의 전공 외 다른 학과의 일부 과목을 이수하는 제도입니다.',
                '평균 학점 2.5 이상이어야 신청 가능합니다.',
                '부전공 이수 학점은 21학점입니다.',
                '복수전공 이수 중에는 부전공 신청이 불가능합니다.',
                '부전공 이수 중에는 추가로 복수전공 신청이 불가능합니다.'
            ]
        }
    };

    // ================================
    // 신청 구분 선택 시
    // ================================
	if (affilChangeCdSelect) {
        affilChangeCdSelect.addEventListener('change', function() {
            const affilType = this.value;

            // 목표 학과 초기화
            targetDeptCdSelect.innerHTML = '<option value="">로딩 중...</option>';
            targetDeptCdSelect.disabled = true;
            deptHelp.innerHTML = '';

            if (!affilType) {
                noticeBox.style.display = 'none';
                targetDeptCdSelect.innerHTML = '<option value="">신청 구분을 먼저 선택하세요</option>';
                return;
            }

            // 유의사항 표시
            showNotice(affilType);

            // 학과 목록 조회
            loadDepartments(affilType);
        });
    }


	// ================================
    // 유의사항 표시
    // ================================
	function showNotice(type) {
        if (!noticeBox || !NOTICES[type]) return;

        const notice = NOTICES[type];
        const itemsHtml = notice.items.map(item => `<li>${item}</li>`).join('');

        noticeBox.innerHTML = `
            <h4>${notice.title}</h4>
            <ul>${itemsHtml}</ul>
        `;
        noticeBox.style.display = 'block';
    }

	// ================================
    // 학과 목록 조회 (동적)
    // ================================
	function loadDepartments(affilType) {
        let url;

        if (affilType === 'MJ_TRF') {
            // 전과: 같은 단과대학 내 학과만
            url = '/lms/student/rest/affiliation/depts/transfer';
            deptHelp.innerHTML = '<span class="affil-text-info">※ 같은 단과대학 내 학과만 표시됩니다.</span>';
        } else {
            // 복수전공/부전공: 전체 학과
            url = '/lms/student/rest/affiliation/depts/all';
            deptHelp.innerHTML = '<span class="affil-text-info">※ 현재 소속 학과를 제외한 전체 학과가 표시됩니다.</span>';
        }

        fetch(url, { method: 'GET' })
            .then(response => {
                if (!response.ok) {
                    throw new Error('학과 목록 조회 실패');
                }
                return response.json();
            })
            .then(depts => {
                renderDepartments(depts, affilType);
            })
            .catch(error => {
                console.error('학과 목록 조회 오류:', error);
                targetDeptCdSelect.innerHTML = '<option value="">학과 목록을 불러오는데 실패했습니다</option>';
                alert('학과 목록을 불러오는데 실패했습니다.');
            });
    }

	// ================================
    // 학과 목록 렌더링
    // ================================
	function renderDepartments(depts, affilType) {
        targetDeptCdSelect.innerHTML = '';

        if (!depts || depts.length === 0) {
            targetDeptCdSelect.innerHTML = '<option value="">선택 가능한 학과가 없습니다</option>';
            targetDeptCdSelect.disabled = true;
            return;
        }

        // 기본 옵션
        const defaultOption = document.createElement('option');
        defaultOption.value = '';
        defaultOption.textContent = '학과를 선택하세요';
        targetDeptCdSelect.appendChild(defaultOption);

        if (affilType === 'MJ_TRF') {
            // 전과: 단순 목록
            depts.forEach(dept => {
				// 공과대학 -> 같은 DEP-OOO-BASIC 처럼 맨뒤에 BASIC 이 붙은 애들은 제외하도록
                const deptCd = dept.univDeptCd;
	            if (typeof deptCd === 'string' && deptCd.endsWith('-BASIC')) {
	                return;
	            }

                const option = document.createElement('option');
                option.value = dept.univDeptCd;
                option.textContent = dept.univDeptName;

                targetDeptCdSelect.appendChild(option);
            });
        } else {
            // 복수전공/부전공: 단과대학별 그룹화
            const groupedDepts = {};

            depts.forEach(dept => {

				const deptCd = dept.univDeptCd;

                // '-BASIC'으로 끝나는 학과 코드 제외
                if (typeof deptCd === 'string' && deptCd.toUpperCase().endsWith('-BASIC')) {
                    return;
                }

                const collegeName = dept.collegeName || '기타';
                if (!groupedDepts[collegeName]) {
                    groupedDepts[collegeName] = [];
                }
                groupedDepts[collegeName].push(dept);
            });

            // optgroup으로 표시
            Object.keys(groupedDepts).sort().forEach(collegeName => {
                const optgroup = document.createElement('optgroup');
                optgroup.label = collegeName;

                groupedDepts[collegeName].forEach(dept => {
                    const option = document.createElement('option');
                    option.value = dept.univDeptCd;
                    option.textContent = dept.univDeptName;
                    optgroup.appendChild(option);
                });

                targetDeptCdSelect.appendChild(optgroup);
            });
        }

        targetDeptCdSelect.disabled = false;
    }

	// ================================
    // 폼 제출
    // ================================

	if (affilApplyForm) {
	    affilApplyForm.addEventListener('submit', async function(e) {
	        e.preventDefault();

	        const affilType = document.getElementById('affilChangeCd').value;

	        // 파일 첨부 필수 검증 추가
	        const fileInput = document.getElementById('attachFiles');
	        if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
	            const typeNames = {
	                'MJ_TRF': '전과',
	                'MJ_DBL': '복수전공',
	                'MJ_SUB': '부전공'
	            };

	            Swal.fire({
	                icon: 'error',
	                title: '파일 첨부 필수',
	                text: `${typeNames[affilType]} 신청은 증빙 서류 첨부가 필수입니다.`,
	                confirmButtonText: '확인',
	                confirmButtonColor: '#007bff'
	            });
	            return;
	        }

	        const typeNames = {
	            'MJ_TRF': '전과',
	            'MJ_DBL': '복수전공',
	            'MJ_SUB': '부전공'
	        };

	        const result = await Swal.fire({
	            title: `${typeNames[affilType]} 신청`,
	            text: `${typeNames[affilType]} 신청을 제출하시겠습니까?`,
	            icon: 'question',
	            showCancelButton: true,
	            confirmButtonText: '제출',
	            cancelButtonText: '취소',
	            confirmButtonColor: '#007bff',
	            cancelButtonColor: '#6c757d'
	        });

	        if (!result.isConfirmed) return;

	        const formData = new FormData(this);

	        fetch('/lms/student/rest/affiliation/apply', {
	            method: 'POST',
	            body: formData
	        })
	            .then(response => {
	                if (!response.ok) {
	                    return response.json().then(data => {
	                        throw new Error(data.message || '신청 처리 중 오류가 발생했습니다.');
	                    });
	                }
	                return response.json();
	            })
	            .then(data => {
	                if (data.success) {
	                    Swal.fire({
	                        icon: 'success',
	                        title: '완료',
	                        text: data.message,
	                        confirmButtonText: '확인',
	                        confirmButtonColor: '#007bff'
	                    }).then(() => {
	                        window.location.href = '/lms/student/academic-change/status';
	                    });
	                } else {
	                    Swal.fire({
	                        icon: 'error',
	                        title: '실패',
	                        text: data.message,
	                        confirmButtonText: '확인',
	                        confirmButtonColor: '#007bff'
	                    });
	                }
	            })
	            .catch(error => {
	                console.error('Error:', error);
	                Swal.fire({
	                    icon: 'error',
	                    title: '오류',
	                    text: error.message || '신청 처리 중 오류가 발생했습니다.',
	                    confirmButtonText: '확인',
	                    confirmButtonColor: '#007bff'
	                });
	            });
	    });
	}

    // 폼 초기화
    if (affilApplyForm) {
        affilApplyForm.addEventListener('reset', function() {
            setTimeout(function() {
                noticeBox.style.display = 'none';
                targetDeptCdSelect.innerHTML = '<option value="">신청 구분을 먼저 선택하세요</option>';
                targetDeptCdSelect.disabled = true;
                deptHelp.innerHTML = '';
            }, 0);
        	    });
        	}
        
        	// ===============================
	// 자동입력 버튼 기능
	// ===============================
	const autoClickButton = document.getElementById('autoclick');
	if (autoClickButton) {
		autoClickButton.addEventListener('click', function() {
			const affilChangeSelect = document.getElementById('affilChangeCd');
			const targetDeptSelect = document.getElementById('targetDeptCd');
			const applyReasonTextarea = document.getElementById('applyReason');

			if (affilChangeSelect && targetDeptSelect && applyReasonTextarea) {
				// 1. 신청 구분 '전과' 선택
				affilChangeSelect.value = 'MJ_TRF';
				affilChangeSelect.dispatchEvent(new Event('change'));

				// 2. 목표 학과 '산업공학과' 선택 (비동기 로딩 후)
				const observer = new MutationObserver((mutationsList, observer) => {
					// 옵션들이 추가되었는지 확인
					for(const mutation of mutationsList) {
						if (mutation.type === 'childList' && targetDeptSelect.options.length > 1) {
							// '산업공학과'에 해당하는 option value를 찾아 설정
                            // TODO: 'DEP-ENGI-IE'는 '산업공학과'의 실제 학과 코드로 변경해야 합니다.
							targetDeptSelect.value = 'DEP-ENGI-IE'; 
							
							// 3. 신청 사유 입력
							applyReasonTextarea.value = '산업공학과는 생산, 품질, 경영, 데이터 분석 등 공학과 경영의 융합적인 시각을 배울 수 있는 학문으로, 제가 관심 있는 스마트공장, 공급망 관리, 데이터 기반 최적화 등의 분야를 다루고 있습니다. 이러한 점에서 제 진로 목표와 더 잘 부합한다고 판단하여 산업공학과로 전과를 희망합니다.';
							
							// 작업 완료 후 observer 연결 해제
							observer.disconnect();
							return;
						}
					}
				});

				// 옵저버 설정: targetDeptSelect의 자식 노드 변경 감지
				observer.observe(targetDeptSelect, { childList: true });
			}
		});
	}
        })