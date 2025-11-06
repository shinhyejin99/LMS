/**
 * 재학 상태 변경 신청 - 통합 버전
 */

document.addEventListener('DOMContentLoaded', function() {

	// ================================
	// 신청 구분 선택 시 필드 동적 표시
	// ================================

	const recordChangeCdSelect = document.getElementById('recordChangeCd');
	const noticeBox = document.getElementById('noticeBox');
	const leaveFields = document.getElementById('leaveFields');
	const returnFields = document.getElementById('returnFields');
	const deferFields = document.getElementById('deferFields');
	const fileHelp = document.getElementById('fileHelp');

	// 유의사항 내용
	const NOTICES = {
		'DROP': {
			title: '⚠️ 자퇴 신청 시 유의사항',
			items: [
				'자퇴 신청 후에는 취소가 불가능하며, 승인 즉시 학적이 삭제됩니다.',
				'등록금 환불은 학칙에 따라 처리되며, 학기 중 자퇴 시 환불액이 제한될 수 있습니다.',
				'자퇴 처리 완료 후에는 재입학 절차를 통해서만 복학이 가능합니다.',
				'장학금 수혜자의 경우, 자퇴 시 장학금 반환 의무가 발생할 수 있습니다.',
				'신청 후 처리까지 평균 3~5일 소요됩니다.'
			]
		},
		'REST': {
			title: '⚠️ 휴학 신청 시 유의사항',
			items: [
				'휴학은 학기 단위로 신청 가능하며, 최대 연속 2학기까지 가능합니다.',
				'군휴학은 입영통지서 제출이 필수입니다.',
				'질병휴학은 의사 진단서(4주 이상 요양 필요) 제출이 필수입니다.',
				'복학 예정 학기 개강 전까지 복학 신청을 완료해야 합니다.'
			]
		},
		'RTRN': {
			title: '⚠️ 복학 신청 시 유의사항',
			items: [
				'복학은 학기 개강 1개월 전부터 개강 1주일 전까지 신청 가능합니다.',
				'군휴학 복학 시 전역증 또는 군복무확인서 제출이 필수입니다.',
				'신청 후 처리까지 평균 2~3일 소요됩니다.'
			]
		},
		'DEFR': {
			title: '⚠️ 졸업유예 신청 시 유의사항',
			items: [
				'졸업유예는 졸업요건을 모두 충족한 학생만 신청 가능합니다.',
				'유예 기간은 최대 2학기까지 가능하며, 학기별로 신청해야 합니다.',
				'유예 기간 중 추가 학점 이수 및 학위 성적 향상이 가능합니다.',
				'신청 후 처리까지 평균 3~5일 소요됩니다.'
			]
		}
	};

	if (recordChangeCdSelect) {
		recordChangeCdSelect.addEventListener('change', function() {
			const recordType = this.value;

			// 모든 필드 숨김
			if (leaveFields) leaveFields.style.display = 'none';
			if (returnFields) returnFields.style.display = 'none';
			if (deferFields) deferFields.style.display = 'none';
			if (noticeBox) noticeBox.style.display = 'none';

			// 필수 필드 초기화
			resetRequiredFields();

			// 파일 안내 초기화
			if (fileHelp) fileHelp.innerHTML = '첨부파일은 선택사항입니다.';

			// 타입별 처리
			if (recordType === 'DROP') {
				// 자퇴
				showNotice(recordType);

			} else if (recordType === 'REST') {
				// 휴학
				if (leaveFields) leaveFields.style.display = 'block';
				setRequired(['leaveType', 'leaveStartTerm'], true);
				showNotice(recordType);

			} else if (recordType === 'RTRN') {
				// 복학
				if (returnFields) returnFields.style.display = 'block';
				setRequired(['returnTerm'], true);
				showReturnFields();
				showNotice(recordType);

			} else if (recordType === 'DEFR') {
				// 졸업유예
				if (deferFields) deferFields.style.display = 'block';
				setRequired(['deferTerm'], true);
				showNotice(recordType);
			}
		});
	}

	// 유의사항 표시
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

	// 필수 필드 설정
	function setRequired(ids, required) {
		ids.forEach(id => {
			const element = document.getElementById(id);
			if (element) element.required = required;
		});
	}

	// 모든 필수 필드 초기화
	function resetRequiredFields() {
		const allFields = [
			'leaveType', 'leaveStartTerm', 'militaryType', 'joinAt',
			'returnTerm', 'deferTerm'
		];
		setRequired(allFields, false);

		// 라디오 버튼 초기화
		document.querySelectorAll('input[name="leaveDuration"]').forEach(input => {
			input.required = false;
		});
	}

	// ================================
	// 휴학 종류 선택 시 하위 필드 표시
	// ================================

	const leaveTypeSelect = document.getElementById('leaveType');
	const generalLeaveFields = document.getElementById('generalLeaveFields');
	const militaryFields = document.getElementById('militaryFields');
	const returnTermPreview = document.getElementById('returnTermPreview');
	const exitDatePreview = document.getElementById('exitDatePreview');

	if (leaveTypeSelect) {
		leaveTypeSelect.addEventListener('change', function() {
		    const leaveType = this.value;

		    // 모든 하위 필드 숨김
		    if (generalLeaveFields) generalLeaveFields.style.display = 'none';
		    if (militaryFields) militaryFields.style.display = 'none';
		    if (returnTermPreview) returnTermPreview.style.display = 'none';
		    if (exitDatePreview) exitDatePreview.style.display = 'none';

		    // 필수 필드 초기화
		    setRequired(['militaryType', 'joinAt'], false);
		    document.querySelectorAll('input[name="leaveDuration"]').forEach(input => {
		        input.required = false;
		    });

		    // 파일 안내 초기화
		    if (fileHelp) fileHelp.innerHTML = '첨부파일은 선택사항입니다.';

		    // 휴학 시작 학기 자동 계산 (모든 휴학 종류 공통)
		    if (leaveType) {
		        calculateLeaveStartTerm();
		    }

		    // 휴학 종류별 처리 (코드 변경)
		    if (leaveType === 'REST_MIL') {
		        // 군입대 휴학
		        if (militaryFields) militaryFields.style.display = 'block';
		        setRequired(['militaryType', 'joinAt'], true);
		        if (fileHelp) {
		            fileHelp.innerHTML = '<span class="text-danger"><strong>※ 필수:</strong> 입영통지서를 반드시 첨부해주세요.</span>';
		        }

		    } else if (leaveType === 'REST_MED') {
		        // 질병 휴학
		        if (generalLeaveFields) generalLeaveFields.style.display = 'block';
		        document.querySelectorAll('input[name="leaveDuration"]').forEach(input => {
		            input.required = true;
		        });
		        if (fileHelp) {
		            fileHelp.innerHTML = '<span class="text-danger"><strong>※ 필수:</strong> 의사 진단서(4주 이상 요양 필요)를 반드시 첨부해주세요.</span>';
		        }

		    } else if (leaveType === 'REST_PARENT') {
		        // 출산육아 휴학
		        if (generalLeaveFields) generalLeaveFields.style.display = 'block';
		        document.querySelectorAll('input[name="leaveDuration"]').forEach(input => {
		            input.required = true;
		        });
		        if (fileHelp) {
		            fileHelp.innerHTML = '<span class="text-danger"><strong>※ 필수:</strong> 출산 또는 육아 관련 증빙서류를 반드시 첨부해주세요.</span>';
		        }

		    } else if (leaveType === 'REST_GEN') {
		        // 일반 휴학
		        if (generalLeaveFields) generalLeaveFields.style.display = 'block';
		        document.querySelectorAll('input[name="leaveDuration"]').forEach(input => {
		            input.required = true;
		        });
		        if (fileHelp) {
		            fileHelp.innerHTML = '첨부파일은 선택사항입니다.';
		        }
		    }
		});
	}

	// 휴학 시작 학기 자동 계산 함수
	function calculateLeaveStartTerm() {
	    const today = new Date();
	    const currentYear = today.getFullYear();
	    const currentMonth = today.getMonth() + 1;

	    let leaveYear, leaveTerm, leaveTermName;

	    if (currentMonth >= 9) {
	        // 9월 이후: 다음 해 1학기
	        leaveYear = currentYear + 1;
	        leaveTerm = "REG1";
	        leaveTermName = "1학기";
	    } else if (currentMonth >= 3) {
	        // 3~8월: 올해 2학기
	        leaveYear = currentYear;
	        leaveTerm = "REG2";
	        leaveTermName = "2학기";
	    } else {
	        // 1~2월: 올해 1학기
	        leaveYear = currentYear;
	        leaveTerm = "REG1";
	        leaveTermName = "1학기";
	    }

	    const leaveStartTermValue = `${leaveYear}_${leaveTerm}`;
	    const leaveStartTermText = `${leaveYear}학년도 ${leaveTermName}`;

	    // hidden field에 값 설정
	    const leaveStartTermInput = document.getElementById('leaveStartTerm');
	    if (leaveStartTermInput) {
	        leaveStartTermInput.value = leaveStartTermValue;
	    }

	    // 화면에 표시
	    const leaveStartTermDisplay = document.getElementById('leaveStartTermDisplay');
	    if (leaveStartTermDisplay) {
	        leaveStartTermDisplay.textContent = leaveStartTermText;
	    }
	}

	// ================================
	// 군휴학: 예상 전역일 자동 계산
	// ================================

	const MILITARY_DURATIONS = {
		'ARMY': 18, 'NAVY': 20, 'AIRF': 21, 'MARN': 18, 'PBLC': 21
	};

	function calculateMilitaryExitDate() {
		const militaryType = document.getElementById('militaryType');
		const joinDate = document.getElementById('joinAt');
		const exitPreview = document.getElementById('exitDatePreview');
		const exitAtHidden = document.getElementById('exitAt');

		if (!militaryType || !joinDate || !exitPreview || !exitAtHidden) return;

		const militaryTypeValue = militaryType.value;
		const joinDateValue = joinDate.value;

		if (!militaryTypeValue || !joinDateValue) {
			exitPreview.style.display = 'none';
			exitAtHidden.value = '';
			return;
		}

		const months = MILITARY_DURATIONS[militaryTypeValue];
		if (!months) {
			exitPreview.style.display = 'none';
			exitAtHidden.value = '';
			return;
		}

		const join = new Date(joinDateValue);
		const exit = new Date(join);
		exit.setMonth(exit.getMonth() + months);
		const exitDateStr = exit.toISOString().split('T')[0];

		const selectedOption = militaryType.querySelector('option:checked');
		const desc = selectedOption.getAttribute('data-desc') || `복무 기간: ${months}개월`;

		exitPreview.innerHTML = `
            <div class="alert alert-info">
                <strong>예상 전역일:</strong> ${exitDateStr}
                <br><small>${desc}</small>
                <br><small class="enroll-text-warning">
                    ※ 예상 전역일은 참고용이며, 실제 전역일은 복학 시점에 전역증 등으로 최종 확인됩니다.
                </small>
            </div>
        `;
		exitPreview.style.display = 'block';
		exitAtHidden.value = exitDateStr;
	}

	const militaryTypeInput = document.getElementById('militaryType');
	const joinAtInput = document.getElementById('joinAt');

	if (militaryTypeInput) militaryTypeInput.addEventListener('change', calculateMilitaryExitDate);
	if (joinAtInput) joinAtInput.addEventListener('change', calculateMilitaryExitDate);

	// ================================
	// 일반휴학: 복학 예정 학기 계산
	// ================================

	function calculateReturnTerm() {
		const startTermSelect = document.getElementById('leaveStartTerm');
		const durationChecked = document.querySelector('input[name="leaveDuration"]:checked');
		const returnPreview = document.getElementById('returnTermPreview');

		if (!startTermSelect || !returnPreview) return;

		const startTerm = startTermSelect.value;
		const duration = durationChecked ? durationChecked.value : null;

		if (!startTerm || !duration) {
			returnPreview.style.display = 'none';
			return;
		}

		const [year, term] = startTerm.split('_');
		const yearNum = parseInt(year);
		const termNum = term === 'REG1' ? 1 : 2;
		const durationNum = parseInt(duration);

		let returnYear = yearNum;
		let returnTerm = termNum + durationNum;

		if (returnTerm > 2) {
			returnYear += 1;
			returnTerm -= 2;
		}

		const returnTermName = `${returnYear}학년도 ${returnTerm}학기`;
		const startTermName = `${yearNum}학년도 ${termNum}학기`;

		returnPreview.innerHTML = `
            <div class="term-preview">
                <div class="preview-item"><strong>휴학 시작:</strong> ${startTermName}</div>
                <div class="preview-item"><strong>복학 예정:</strong> ${returnTermName}</div>
            </div>
        `;
		returnPreview.style.display = 'block';
	}

	const leaveStartTermSelect = document.getElementById('leaveStartTerm');
	if (leaveStartTermSelect) {
		leaveStartTermSelect.addEventListener('change', calculateReturnTerm);
	}

	document.querySelectorAll('input[name="leaveDuration"]').forEach(input => {
		input.addEventListener('change', calculateReturnTerm);
	});

	// ================================
	// 복학: 예정 학기 자동 계산
	// ================================

	function showReturnFields() {
	    const today = new Date();
	    const currentYear = today.getFullYear();
	    const currentMonth = today.getMonth() + 1;

	    let returnYear, returnTerm, returnTermName;

	    if (currentMonth >= 9) {
	        // 9월 이후: 다음 해 1학기
	        returnYear = currentYear + 1;
	        returnTerm = "REG1";
	        returnTermName = "1학기";
	    } else if (currentMonth >= 3) {
	        // 3~8월: 올해 2학기
	        returnYear = currentYear;
	        returnTerm = "REG2";
	        returnTermName = "2학기";
	    } else {
	        // 1~2월: 올해 1학기
	        returnYear = currentYear;
	        returnTerm = "REG1";
	        returnTermName = "1학기";
	    }

	    const returnTermValue = `${returnYear}_${returnTerm}`;
	    const returnTermText = `${returnYear}학년도 ${returnTermName}`;

	    // hidden field에 값 설정
	    const returnTermInput = document.getElementById('returnTerm');
	    if (returnTermInput) {
	        returnTermInput.value = returnTermValue;
	    }

	    // 화면에 표시
	    const returnTermDisplay = document.getElementById('returnTermDisplay');
	    if (returnTermDisplay) {
	        returnTermDisplay.textContent = returnTermText;
	    }
	}

	// 졸업유예 희망 학기 선택 시
	document.querySelectorAll('input[name="deferTermChoice"]').forEach(input => {
	    input.addEventListener('change', function() {
	        const today = new Date();
	        const currentYear = today.getFullYear();
	        const currentMonth = today.getMonth() + 1;

	        const selectedTerm = this.value; // "1" or "2"

	        // 년도 자동 계산
	        let deferYear = currentYear;
	        if (currentMonth >= 9) {
	            // 9월 이후면 다음 해
	            deferYear = currentYear + 1;
	        }

	        // 선택한 학기가 이미 지났으면 다음 해로
	        if (currentMonth >= 9 && selectedTerm === "1") {
	            deferYear = currentYear + 1;
	        } else if (currentMonth >= 3 && currentMonth < 9 && selectedTerm === "2") {
	            // 3~8월에 2학기 선택 시 올해
	            deferYear = currentYear;
	        } else if (currentMonth < 3 && selectedTerm === "1") {
	            // 1~2월에 1학기 선택 시 올해
	            deferYear = currentYear;
	        }

	        const termCode = selectedTerm === "1" ? "REG1" : "REG2";
	        const termName = selectedTerm === "1" ? "1학기" : "2학기";

	        // 졸업 시기 계산
	        // 1학기 유예 → 같은 해 8월 (후기 졸업)
	        // 2학기 유예 → 다음 해 2월 (전기 졸업)
	        let graduationYear, graduationType;

	        if (selectedTerm === "1") {
	            // 1학기 유예 → 같은 해 8월 후기 졸업
	            graduationYear = deferYear;
	            graduationType = "후기 졸업 (8월)";
	        } else {
	            // 2학기 유예 → 다음 해 2월 전기 졸업
	            graduationYear = deferYear + 1;
	            graduationType = "전기 졸업 (2월)";
	        }

	        // hidden field에 "2026_REG1" 형식으로 저장
	        document.getElementById('deferTerm').value = `${deferYear}_${termCode}`;

	        // 화면에 표시
	        document.getElementById('deferTermDisplay').textContent =
	            `${graduationYear}년 ${graduationType} (${deferYear}학년도 ${termName} 수료)`;
	        document.getElementById('deferTermPreview').style.display = 'block';
	    });
	});


	// ================================
	// 폼 제출 (통합)
	// ================================

	const recordApplyForm = document.getElementById('recordApplyForm');

	if (recordApplyForm) {
	    recordApplyForm.addEventListener('submit', async function(e) {
	        e.preventDefault();

	        const recordChangeCdElement = document.getElementById('recordChangeCd');
	        if (!recordChangeCdElement) {
	            console.warn("Element with ID 'recordChangeCd' not found. Skipping record type processing.");
	            return;
	        }
	        const recordType = recordChangeCdElement.value;

	        // 휴학 신청 시 파일 첨부 필수 검증 추가
	        if (recordType === 'REST') {
	            const leaveType = document.getElementById('leaveType')?.value;
	            const fileInput = document.getElementById('attachFiles');
	            const fileRequiredTypes = ['REST_MIL', 'REST_MED', 'REST_PARENT']; // 군휴학, 질병, 출산

	            if (fileRequiredTypes.includes(leaveType)) {
	                if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
	                    let typeName = '';
	                    switch(leaveType) {
	                        case 'REST_MIL': typeName = '군입대 휴학'; break;
	                        case 'REST_MED': typeName = '질병휴학'; break;
	                        case 'REST_PARENT': typeName = '출산/육아휴학'; break;
	                    }

	                    Swal.fire({
	                        icon: 'error',
	                        title: '파일 첨부 필수',
	                        text: `${typeName}은 증빙 서류 첨부가 필수입니다.`,
	                        confirmButtonText: '확인',
	                        confirmButtonColor: '#007bff'
	                    });
	                    return;
	                }
	            }
	        }

	        const typeNames = {
	            'DROP': '자퇴',
	            'REST': '휴학',
	            'RTRN': '복학',
	            'DEFR': '졸업유예'
	        };

	        const result = await Swal.fire({
	            title: `${typeNames[recordType]} 신청`,
	            text: `${typeNames[recordType]} 신청을 제출하시겠습니까?`,
	            icon: 'question',
	            showCancelButton: true,
	            confirmButtonText: '제출',
	            cancelButtonText: '취소',
	            confirmButtonColor: '#007bff',
	            cancelButtonColor: '#6c757d'
	        });

	        if (!result.isConfirmed) return;

	        const formData = new FormData(this);

	        fetch('/lms/student/rest/record/apply', {
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
	                    }).then((result) => {
	                        if (result.isConfirmed) {
	                            location.href = '/lms/student/academic-change/status';
	                        }
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

	// ===============================
	// 자동입력 버튼 기능
	// ===============================
	const autoClickButton = document.getElementById('autoclick');
	if (autoClickButton) {
		autoClickButton.addEventListener('click', function() {
			const recordChangeSelect = document.getElementById('recordChangeCd');
			if (recordChangeSelect) {
				recordChangeSelect.value = 'REST';
				// change 이벤트를 수동으로 발생시켜 관련 UI가 업데이트되도록 함
				recordChangeSelect.dispatchEvent(new Event('change'));

				// 휴학 종류를 '일반휴학'으로 설정
				const leaveTypeSelect = document.getElementById('leaveType');
				if (leaveTypeSelect) {
					leaveTypeSelect.value = 'REST_GEN';
					// change 이벤트를 수동으로 발생시켜 관련 UI가 업데이트되도록 함
					leaveTypeSelect.dispatchEvent(new Event('change'));
				}

				// 휴학 기간을 '2학기'로 선택
				const leaveDurationRadio = document.querySelector('input[name="leaveDuration"][value="2"]');
				if (leaveDurationRadio) {
					leaveDurationRadio.checked = true;
					// change 이벤트를 수동으로 발생시켜 관련 UI가 업데이트되도록 함
					leaveDurationRadio.dispatchEvent(new Event('change'));
				}

				// 신청 사유 입력
				const applyReasonTextarea = document.getElementById('applyReason');
				if (applyReasonTextarea) {
					applyReasonTextarea.value = '전공 관련 경험을 쌓기 위해 인턴십 프로그램에 참여하고자 함';
				}
			}
		});
	}
});