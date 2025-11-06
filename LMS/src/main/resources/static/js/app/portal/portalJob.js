/** * <pre>
 * << 개정이력(Modification Information) >>
 * * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 9. 26.     	김수현           최초 생성
 * 2025. 9. 27.			김수현			수정 및 삭제 기능 추가
 * 2025. 9. 27.			정태일			페이지네이션 부분 추가
 * 2025. 9. 29.			정태일			페이지네이션 번호(index) 오류 수정
 * 2025. 9. 29.			김수현			조회수 증가 기능, 검색 기능 추가
 * 2025. 9. 30.			김수현			select 옵션 변경 추가, student 추가
 * 2025. 10. 20.		김수현			formatDate() 이름 변경 -> formatJobDate()
 * 2025. 10. 21.		김수현			탭에 따라서 placeholder 변경 기능 추가
 * </pre>
 */

// URL에서 jobType을 추출하는 함수
const getJobTypeFromUrl = () => {
	const pathSegments = window.location.pathname.split('/'); // 나누는 기준
	// pathSegments[3]이 jobType이 되려면 URL이 /portal/job/internal 일 때
	const jobType = pathSegments[3];
	if (jobType && ['public', 'internal', 'student'].includes(jobType)) {
		return jobType;
	}

	const studentTab = document.querySelector('.tab-item[data-type="student"]');
	if (studentTab) {
        return 'student'; // '맞춤형 채용'을 기본값으로 설정
    }

	return 'public';
};

// URL에서 recruitId를 추출하는 함수
const getRecruitIdFromUrl = () => {
	const pathSegments = window.location.pathname.split('/');
	// /portal/job/public/SCRECR000000002
	const recruitId = pathSegments[4];
	return recruitId;
}

// 조회수 증가 호출 함수
const incrementJobViewCount = async (jobType, recruitId) => {
	const url = `/rest/portal/job/${jobType}/${recruitId}/view-count`;

	try {
		const response = await fetch(url, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({})
		});
		if (!response.ok) {
			console.error(`조회수 증가 실패: HTTP Status ${response.status}`);
		}
	} catch (error) {
		console.error('조회수 증가 중 통신 오류 발생:', error);
	}
};

document.addEventListener('DOMContentLoaded', () => {

	// 검색 버튼 이벤트
	const searchBtn = document.querySelector('#searchBtn');
	if (searchBtn) {
		searchBtn.addEventListener('click', searchJobs);
	}
	// enter 눌렀을 때 검색되도록 하는 이벤트
	const searchInput = document.querySelector('.search-input');
	if (searchInput) {
		searchInput.addEventListener('keyup', (e) => {
			if (e.key == 'Enter') {
				searchJobs();
			}
		})
	}

	// 플레이스 홀더 변경 (탭에 따라서)
    const placeholderDefault = "제목 또는 기업명을 입력하세요"; 			// 맞춤형, 공공 채용
    const placeholderInternal = "제목 또는 작성부서를 입력하세요"; 		// 학내 채용

    const setPlaceholder = (jobType) => {
        if (!searchInput) return;

        if (jobType === 'internal') {
            searchInput.placeholder = placeholderInternal;
        } else if (jobType === 'public' || jobType === 'student') {
            searchInput.placeholder = placeholderDefault;
        }
    };

	// 상세보기 페이지 - 목록, 수정, 삭제 버튼 이벤트
	const listBtn = document.getElementById('listJobBtn');
	const editBtn = document.getElementById('editJobBtn');
	const deleteBtn = document.getElementById('deleteJobBtn');

	// 상세보기 페이지에만 존재하기 때문에 있는지 확인함.
	if (listBtn) {
		// jobType을 URL에서 가져와야 함 (이 코드가 상세 페이지에 로드될 때만)
		const currentJobType = getJobTypeFromUrl();
		listBtn.addEventListener('click', () => {
			// 목록 페이지로 이동
			window.location.href = `/portal/job/${currentJobType}`;
		});
	}

	// 수정 버튼 이벤트 처리
	if (editBtn) { // listBtn과 별도로 확인
		editBtn.addEventListener('click', () => {
			// url에서 채용번호ID 가져오기
			const recruitId = getRecruitIdFromUrl();
			// 유효성 검증
			if (!recruitId) {
			    Swal.fire({
			        icon: 'error',
			        title: '작업 실패!',
			        text: '수정할 채용 정보 ID를 찾을 수 없습니다.',
			        confirmButtonText: '확인'
			    });
			    return;
			}
			// 수정 폼 URL로 이동
			window.location.href = `/portal/job/internal/modify/${recruitId}`;
		});
	}
	// 삭제 버튼 이벤트 처리
	if (deleteBtn) {
	    deleteBtn.addEventListener('click', () => {
	        // url에서 recruitId 가져오기
	        const recruitId = getRecruitIdFromUrl();

	        // 1. 유효성 검증
	        if (!recruitId) {
	            Swal.fire({
	              icon: "error",
	              title: "삭제 실패",
	              text: "삭제할 채용 정보의 ID를 찾을 수 없습니다. 페이지를 새로고침 후 다시 시도해 주세요.",
	              confirmButtonText: '확인',
	              confirmButtonColor: '#dc3545'
	            });
	            return;
	        }

	        // 2. 삭제 확인
	        Swal.fire({
	            title: `학내 채용공고를 삭제하시겠습니까?`,
	            text: '삭제된 내용은 복구할 수 없습니다.',
	            icon: 'warning',
	            iconColor: '#7bcfe4',
	            showCancelButton: true,
	            confirmButtonColor: '#EF5350',
	            cancelButtonColor: '#6c757d',
	            confirmButtonText: '삭제',
	            cancelButtonText: '취소'
	        }).then((result) => {
	            // 사용자가 '삭제' 버튼을 눌렀을 때
	            if (result.isConfirmed) {
	                // ⭐ 여기 수정! fetch( 괄호 추가
	                fetch(`/portal/job/internal/remove/${recruitId}`, {
	                    method: 'POST',
	                    headers: {
	                        'Content-Type': 'application/json'
	                    }
	                })
	                .then(response => response.json())
	                .then(data => {
	                    if (data.success || data.status === 'success') {
	                        // 3. 삭제 완료 메시지
	                        Swal.fire({
	                            title: '삭제 완료!',
	                            text: '채용 공고가 삭제되었습니다.',
	                            icon: 'success',
	                            iconColor: '#4CAF50',
	                            confirmButtonColor: '#1E88E5',
	                            confirmButtonText: '확인'
	                        }).then(() => {
	                            // 목록으로 이동
	                            window.location.href = '/portal/job/internal';
	                        });
	                    } else {
	                        // 삭제 실패
	                        Swal.fire({
	                            icon: 'error',
	                            title: '삭제 실패',
	                            text: data.message || '삭제 중 오류가 발생했습니다.',
	                            confirmButtonText: '확인',
	                            confirmButtonColor: '#EF5350'
	                        });
	                    }
	                })
	                .catch(error => {
	                    console.error('삭제 오류:', error);
	                    Swal.fire({
	                        icon: 'error',
	                        title: '오류 발생',
	                        text: '삭제 처리 중 오류가 발생했습니다.',
	                        confirmButtonText: '확인',
	                        confirmButtonColor: '#EF5350'
	                    });
	                });
	            }
	        });
	    })
	}

	// 학내 채용정보 등록 버튼
	const registBtn = document.getElementById('registJobBtn');
	if (registBtn) {
		registBtn.style.float = 'right';

        // 1. 탭 상태 확인
        const currentJobType = getJobTypeFromUrl();

        // 2. 'internal'이 아니면 버튼 숨기기
        if (currentJobType !== 'internal') {
            registBtn.style.display = 'none';
        }

        // 3. 클릭 이벤트 등록 (기존 로직)
        registBtn.addEventListener('click', () => {
            // 등록 ui 보여주는 controller로 보내기
            window.location.href = '/portal/job/internal/write';
        })
    }

	// 페이지 유형 구분 로직
	const path = window.location.pathname;
	const pathSegments = path.split('/').filter(s => s.length > 0);

	// 1. 목록 페이지 로직 (URL: /portal/job/{jobType})
	if (pathSegments.length === 3) {

		// 초기 로드시 jobType 가져오기
		const initialJobType = getJobTypeFromUrl();

		// 초기 로드 시 jobType이 있다면 목록 로직 실행
		if (initialJobType) {
			// 탭 active 설정
			document.querySelectorAll('.tab-item').forEach(t => t.classList.remove('active'));
			const initialTab = document.querySelector(`.tab-item[data-type="${initialJobType}"]`);
			if (initialTab) {
				initialTab.classList.add('active');
			}
			// 플레이스홀더 설정 후
            setPlaceholder(initialJobType);
			// 목록 로드
			loadJobList(initialJobType);
		}

		// 탭 전환 이벤트 처리
		document.querySelectorAll('.tab-item').forEach(tab => {
			tab.addEventListener('click', function() {
				document.querySelectorAll('.tab-item').forEach(t => t.classList.remove('active'));
				this.classList.add('active');

				const jobType = this.dataset.type;
				// 탭 클릭 시 플레이스홀더 설정
                setPlaceholder(jobType);
				const newUrl = `/portal/job/${jobType}`; // 목록 URL을 명확히 함
				window.location.href = newUrl;

				loadJobList(jobType);
			})
		})
	}

	// 탭에 따라 <select> 옵션 변경하기
	const currentJobType = getJobTypeFromUrl();
	const companyOption = document.querySelector('.search-select option[value="company"]');

	if (companyOption) { // 변수명 변경 적용
		if (currentJobType === 'internal') {
			// 학내 채용 (internal)
			companyOption.textContent = "작성 부서";
		} else if (currentJobType === 'public' || currentJobType === 'student') {
			// 공공 채용 (public) 또는 학생 채용 (student)
			companyOption.textContent = "기업명 ";
		} else {
			companyOption.textContent = "회사명/작성자";
		}
	}
}); // DOMContentLoaded 끝


// 분류에 따른 채용정보 게시글 불러오기 (목록 조회)
const loadJobList = async (jobType, currentPage = 1, searchWord = '', screenSize = 10) => {

	const blockSize = 5;       // 페이징 버튼 묶음 크기

	let url = `/rest/portal/job/${jobType}?currentPage=${currentPage}&screenSize=${screenSize}`;

	// 검색 조건이 있을 경우 URL에 추가
	if (searchWord) {
		const params = new URLSearchParams();
		params.append('simpleSearch.searchWord', searchWord);

		url += `&${params.toString()}`;
	}
	// 디버깅용!!!!!!
	console.log(`==> [최종 fetch url] : ${url}`);

	fetch(url, { method: 'get' })
		.then(response => {
			if (!response.ok) throw new Error(`HTTP Error! Status: ${response.status}`);
			return response.json();
		})
		.then(data => {
			console.log("API 응답 전체 데이터:", data);

			if (data.success && data.list && data.list.length > 0) {
				const totalCount = Number(data.totalCount ?? 0);
				let paging = data.paging;
				if (!paging) {
					const totalPage = Math.max(1, Math.ceil(totalCount / screenSize));
					const currentPage = Math.min(Math.max(1, page), totalPage);
					const currentBlock = Math.floor((currentPage - 1) / blockSize);
					const startPage = currentBlock * blockSize + 1;
					const endPage = Math.min(startPage + blockSize - 1, totalPage);

					paging = { totalPage, currentPage, startPage, endPage, pageSize, screenSize, totalCount };
				}

				// paging 확정 후에 테이블 렌더 (전역 번호 계산 일관성)
				displayJobTable(data.list, jobType, paging, totalCount, screenSize);

				// 페이징 렌더링
				displayPagination({
					totalPage: paging.totalPage,
					currentPage: paging.currentPage,
					startPage: paging.startPage,
					endPage: paging.endPage
				});

				// 클릭 시 재호출용
				window.loadListFn = (nextPage) => loadJobList(jobType, nextPage, searchWord, screenSize);

			} else {
				clearAndShowError('등록된 채용정보가 없습니다.');
				document.getElementById('paginationContainer').innerHTML = '';
			}
		})
		.catch(error => {
			console.error('오류 : ', error);
			clearAndShowError('데이터를 불러오는 중 오류가 발생했습니다.');
			document.getElementById('paginationContainer').innerHTML = '';
		});
}; // loadJobList() 끝


// 검색 조건 리턴
const collectSearch = () => {

	const searchWord = document.querySelector('.search-input').value.trim();

	return { searchWord };
};

// 심플 검색
const searchJobs = () => {
	const { searchWord } = collectSearch();

	// 현재 jobType 가져오기
	const jobType = getJobTypeFromUrl();

	// 검색 조건 포함 다시 게시글 로드
	loadJobList(jobType, 1, searchWord);
}



// 목록 컨테이너를 비우고 메시지를 표시하는 함수 - 아직 구현되지 않은 맞춤형 채용 정보 때문에 생성함
const clearAndShowError = (message) => {
	const container = document.getElementById('jobList');
	container.innerHTML = `<div class="no-data">${message}</div>`;
}


// 목록 테이블 생성 함수
// 목록 테이블 생성 함수 (수정)
const displayJobTable = (jobList, jobType, paging, totalCount, screenSize) => {
    const container = document.getElementById('jobList');

    if (!jobList || jobList.length === 0) {
        container.innerHTML = '<div class="no-data">등록된 채용정보가 없습니다.</div>';
        return;
    }

    const safeTotal = Number.isFinite(totalCount) ? totalCount : jobList.length;
    const safePage = Number(paging?.currentPage) > 0 ? Number(paging.currentPage) : 1;
    const safeSize = Number(screenSize) > 0 ? Number(screenSize) : jobList.length;
    const startNo = Math.max(0, safeTotal - (safePage - 1) * safeSize);

    let tableHtml;

    if (jobType === 'public' || jobType === 'student') {
        tableHtml = `
            <table class="job-table">
                <thead>
                    <tr>
                        <th class="col-num">번호</th>
                        <th class="col-title">제목</th>
                        <th class="col-dept">기업명</th>
                        <th class="col-period">접수기간</th>
                    </tr>
                </thead>
                <tbody>
        `;
    } else {
        tableHtml = `
            <table class="job-table">
                <thead>
                    <tr>
                        <th class="col-num">번호</th>
                        <th class="col-title">제목</th>
                        <th class="col-dept">작성부서</th>
                        <th class="col-period">접수기간</th>
                        <th class="col-date">작성일</th>
                        <th class="col-views">조회수</th>
                    </tr>
                </thead>
                <tbody>
        `;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    jobList.forEach((job, index) => {
        const rownum = startNo - index;

        // formatDate로 날짜 포맷팅
        const startDate = formatJobDate(job.recStartDay);
        const endDate = formatJobDate(job.recEndDay);

        // 마감일 체크
        let endDateObj = null;
        let dDayHtml = ''; // D-Day 뱃지를 저장할 변수

        if (job.recEndDay) {
            endDateObj = new Date(formatJobDate(job.recEndDay));
	        endDateObj.setHours(0, 0, 0, 0);

	        const today = new Date();
	        today.setHours(0, 0, 0, 0);

	        // D-Day 계산
	        const diffTime = endDateObj - today;
	        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

            // isEnd를 판단
	        const isEnd = diffDays < 0;

            if (!isEnd) { // 마감되지 않은 경우에만 D-day 표시
                if (diffDays === 0) {
                    dDayHtml = '<span class="dday-today">D-Day</span>';
                } else if (diffDays <= 7) {
                    dDayHtml = `<span class="dday-urgent">D-${diffDays}</span>`;
                } else {
                    dDayHtml = `<span class="dday-normal">D-${diffDays}</span>`;
                }
            } else {
                // 마감된 경우, D-Day 대신 '마감' 뱃지를 생성
                dDayHtml = '<span class="dday-closed">마감</span>';
            }
        }

        // 접수기간 문자열 생성 (시작일 ~ 마감일)
        const periodText = (startDate === endDate) ? startDate : `${startDate}~${endDate}`;

        // 접수기간 표시 로직 수정 (마감 여부에 관계없이 기간 + 뱃지 표시)
        let periodDisplay = `${periodText} ${dDayHtml}`;

        let deptOrCompany, createDate;

        if (jobType === 'public' || jobType === 'student') {
            deptOrCompany = job.organization || '-';

            tableHtml += `
                <tr>
                    <td>${rownum}</td>
                    <td>
                        <a href="/portal/job/${jobType}/${job.recruitId}" class="job-title-link"
                        data-recruit-id="${job.recruitId}">${job.title}</a>
                    </td>
                    <td>${deptOrCompany}</td>
                    <td>${periodDisplay}</td>
                </tr>
            `;
        } else {
            deptOrCompany = job.stfDeptName || '-';
            createDate = formatJobDate(job.createAt);

            tableHtml += `
                <tr>
                    <td>${rownum}</td>
                    <td>
                        <a href="/portal/job/${jobType}/${job.recruitId}" class="job-title-link"
                        data-recruit-id="${job.recruitId}">${job.title}</a>
                    </td>
                    <td>${deptOrCompany}</td>
                    <td>${periodDisplay}</td>
                    <td>${createDate}</td>
                    <td>${job.viewCnt}</td>
                </tr>
            `;
        }
    });

    tableHtml += '</tbody></table>';
    container.innerHTML = tableHtml;

    document.querySelectorAll('.job-title-link').forEach(link => {
        link.addEventListener('click', async (e) => {
            e.preventDefault();
            const recruitId = link.dataset.recruitId;
            const detailUrl = link.href;
            incrementJobViewCount(jobType, recruitId);
            window.location.href = detailUrl;
        });
    });
}

// 작성일 포맷팅 : 년월일시분초 -> 년-월-일
function formatJobDate(dateString) {
    if (!dateString) return '-';

    try {
        // 1. 이미 YYYY-MM-DD 형식이면 그대로 반환
        if (/^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
            return dateString;
        }

        // 2. ISO 형식이나 공백 포함 형식에서 날짜만 추출
        const match = dateString.match(/(\d{4}-\d{2}-\d{2})/);
        if (match && match[1]) {
            return match[1];
        }

        // 3. 그 외 처리
        return dateString.split('T')[0].split(' ')[0];

    } catch (error) {
        console.error('formatJobDate 파싱 오류:', error, '원본:', dateString);
        return '-';
    }
}

/**
 * 첨부파일 목록 렌더링 (학내 채용 전용)
 */
const renderAttachFiles = (files, recruitId) => {
    // 찾기
    const attachmentSection = document.getElementById('attachmentSection');

    if (!attachmentSection) {
        console.warn('첨부파일 섹션(#attachmentSection)을 찾을 수 없습니다.');
        return;
    }

    // 섹션 내용을 JavaScript로 대체
    if (files && files.length > 0) {
        let fileListHtml = '<h2 class="section-title">첨부파일</h2><ul class="file-list">';

        files.forEach(file => {
            const fullName = file.extension
                ? `${file.originName}.${file.extension}`
                : file.originName;

            // 파일 크기를 MB로 변환
            const fileSizeMB = (file.fileSize / 1024 / 1024).toFixed(2);

            fileListHtml += `
                <li class="file-item">
                    <a href="/rest/portal/job/internal/${recruitId}/file/${file.fileOrder}"
                       class="file-link"
                       title="${fullName} 다운로드">
                        <i class="fas fa-paperclip file-icon"></i>
                        <span class="file-name">${fullName}</span>
                        <span class="file-size text-gray-500 text-sm ml-2">
                            (${fileSizeMB} MB)
                        </span>
                    </a>
                </li>
            `;
        });

        fileListHtml += '</ul>';
        attachmentSection.innerHTML = fileListHtml;

    } else {
        attachmentSection.innerHTML = `
            <h2 class="section-title">첨부파일</h2>
            <p class="no-attachment text-gray-500">첨부된 파일이 없습니다.</p>
        `;
    }
};


// 채용공고 상세보기
const loadJobDetail = (jobType, recruitId) => {

	const url = `/rest/portal/job/${jobType}/${recruitId}`;

	fetch(url, {
		method: 'get'
	})
		.then(response => {
			if (!response.ok) {
				// HTTP 에러 시 catch로 넘겨 에러 메시지 표시
				throw new Error(`HTTP Error! Status: ${response.status}`);
			}
			console.log("상세 API 응답:", response);
			return response.json();
		})
		.then(data => {
			console.log("상세 데이터:", data);

			if (data.success && data.detail) {
				// 성공 시 상세 내용 표시 함수 호출
				displayJobDetail(data.detail, jobType);
			} else {
				// 데이터는 받았으나 실패/데이터 없음
				displayJobDetailError(data.message || '채용 정보를 불러올 수 없습니다.');
			}
		})
		.catch(error => {
			console.error("상세 로드 오류:", error);
			// 에러 발생 시 에러 메시지 표시
			displayJobDetailError('데이터를 불러오는 중 오류가 발생했습니다.');
		})
}




// 상세 데이터 표시 함수
const displayJobDetail = (detail, jobType) => {

	if (jobType === 'public' || jobType === 'student') {
		// 공공채용 상세 (JobDetailDTO)
		document.querySelector('.title-section .job-title').textContent = detail.title || '제목 없음';
		document.getElementById('organization').textContent = detail.organization || '정보 없음'; // 기업명
		document.getElementById('companyScale').textContent =
												    detail.companyScale && detail.companyScale.trim() !== ''
												    ? detail.companyScale
												    : '정보 미제공';
		document.getElementById('employmentType').textContent = detail.employmentType || '정보 없음'; // 고용형태

		// 접수기간
		document.getElementById('recDay').textContent =
			`${formatJobDate(detail.recStartDay)} ~ ${formatJobDate(detail.recEndDay)}`;

		// 공공채용 내용 (원본 데이터)
		let contentHtml = '';
		if (detail.recrCommCont) contentHtml += `<h3>자격요건</h3><p>${detail.recrCommCont}</p>`;
		if (detail.empSubmitDocCont) contentHtml += `<h3>제출서류</h3><p>${detail.empSubmitDocCont}</p>`;
		if (detail.empRcptMthdCont) contentHtml += `<h3>접수방법</h3><p>${detail.empRcptMthdCont}</p>`;
		if (detail.inqryCont) contentHtml += `<h3>문의처</h3><p>${detail.inqryCont}</p>`;
		if (detail.empnEtcCont) contentHtml += `<h3>기타사항</h3><p>${detail.empnEtcCont}</p>`;

		document.getElementById('jobDetailContent').innerHTML = contentHtml || '등록된 내용이 없습니다.';

		// 외부 링크 버튼 추가
		if (detail.homepageUrl || detail.recruitUrl) {
			const linkSection = document.getElementById('linkSection');
			let linkHtml = '<h2 class="section-title">관련 링크</h2>';
			if (detail.homepageUrl) {
				linkHtml += `<a href="${detail.homepageUrl}" target="_blank" class="btn btn-link">기업 홈페이지</a>`;
			}
			if (detail.recruitUrl) {
				linkHtml += `<a href="${detail.recruitUrl}" target="_blank" class="btn btn-primary">채용공고 바로가기</a>`;
			}
			linkSection.innerHTML = linkHtml;
		}

	} else {
		// 학내채용 상세
	    document.querySelector('.title-section .job-title').textContent = detail.title || '제목 없음';
	    document.getElementById('agencyName').textContent = detail.agencyName || '정보 없음';
	    document.getElementById('stfDeptName').textContent = detail.stfDeptName || '정보 없음';
	    document.getElementById('recTarget').textContent = detail.recTarget || '정보 없음';
	    document.getElementById('viewCnt').textContent = detail.viewCnt || 0;
	    document.getElementById('recDay').textContent =
	        `${formatJobDate(detail.recStartDay)} ~ ${formatJobDate(detail.recEndDay)}`;

	    // 게시글 내용 줄바꿈 치환
	    let contentToDisplay = detail.content || '등록된 내용이 없습니다.';
	    if (contentToDisplay !== '등록된 내용이 없습니다.') {
	        if (contentToDisplay.includes('<p>') || contentToDisplay.includes('<h3>')) {
	            // CKEditor로 저장된 데이터 - 그대로 사용
	        } else {
	            contentToDisplay = contentToDisplay
	                .replace(/\\r\\n/g, '<br>')
	                .replace(/\\n/g, '<br>')
	                .replace(/\\r/g, '<br>')
	                .replace(/(\r\n|\n|\r)/g, '<br>');
	        }
	    }
	    document.getElementById('jobDetailContent').innerHTML = contentToDisplay;

	    // 첨부파일 렌더링
	    renderAttachFiles(detail.attachFiles, detail.recruitId);
	}

	// 상세 페이지 에러 표시 함수
	const displayJobDetailError = (message) => {
		const mainContainer = document.querySelector('.main-container');
		if (mainContainer) {
			mainContainer.innerHTML = `<div class="error-page"><h1 class="page-title">오류 발생</h1><p>${message}</p></div>`;
		}
	}
}