/**
 * 수강신청 관리
 * @author 김수현
 * @since 2025.10.17
 */
// 소켓 구독할 때 setTimeout() 5초로 설정했던 거 => 즉시 연결하는 걸로 수정함.
// 그리고 특정 행만 업데이트되도록 로직 수정 및 함수 추가함


// =========================
// 페이지 로드
// =========================
document.addEventListener('DOMContentLoaded', function() {
    console.log('[수강신청] 페이지 로드 완료');
    console.log('현재 탭:', currentTab);

    // 신청 현황 업데이트
    updateApplyStatus();

    // 알림 드롭다운 수동 처리 (수강신청 페이지만)
    initNotificationDropdown();

    // 웹소켓 즉시 연결하도록 (setTime 5000 안 쓰고 즉시 연결하도록 바꿈)
    if (typeof connectWishlistWebSocket === 'function') {
        connectWishlistWebSocket();
    }
});

/**
 * 알림 드롭다운 초기화 (수강신청 페이지 전용)
 */
function initNotificationDropdown() {
    setTimeout(function() {
        const dropdownEl = document.getElementById('notificationDropdown');
        const dropdownMenu = document.getElementById('notification-dropdown-menu');

        if (!dropdownEl || !dropdownMenu) {
            console.log('===> 알림 드롭다운 없음 (정상)');
            return;
        }

        console.log('===> 알림 드롭다운 수동 설정...');

        // Bootstrap 인스턴스 생성
        let dropdownInstance = bootstrap.Dropdown.getInstance(dropdownEl);
        if (!dropdownInstance) {
            dropdownInstance = new bootstrap.Dropdown(dropdownEl);
        }

        // 클릭 이벤트 직접 처리
        $(dropdownEl).off('click').on('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            const isVisible = $(dropdownMenu).hasClass('show');

            if (isVisible) {
                $(dropdownMenu).removeClass('show');
                $(dropdownEl).attr('aria-expanded', 'false');
                if (typeof fetchUnreadCount === 'function') {
                    fetchUnreadCount();
                }
            } else {
                $(dropdownMenu).addClass('show');
                $(dropdownEl).attr('aria-expanded', 'true');
                $('#unread-count-badge').hide().text(0);
                if (typeof fetchNotificationsForDropdown === 'function') {
                    fetchNotificationsForDropdown();
                }
            }
        });

        // 외부 클릭 시 닫기
        $(document).on('click', function(e) {
            if (!$(e.target).closest('#notificationDropdown, #notification-dropdown-menu').length) {
                if ($(dropdownMenu).hasClass('show')) {
                    $(dropdownMenu).removeClass('show');
                    $(dropdownEl).attr('aria-expanded', 'false');
                    if (typeof fetchUnreadCount === 'function') {
                        fetchUnreadCount();
                    }
                }
            }
        });

        console.log('===> 알림 드롭다운 수동 설정 완료!');
    }, 500);
}

// =========================
// 탭 전환
// =========================
function switchTab(tab) {
    if (tab === 'search') {
        window.location.href = CONTEXT_PATH + '/classregist/student/wish?tab=search&page=1';
    } else if (tab === 'applied') {
        window.location.href = CONTEXT_PATH + '/classregist/student/wish?tab=applied&page=1';
    } else {
        window.location.href = CONTEXT_PATH + '/classregist/student/wish?tab=wishlist&page=1';
    }
}

// =========================
// 필터링
// =========================
function filterByGrade(grade) {
    const urlParams = new URLSearchParams();
    urlParams.set('tab', 'search');
    urlParams.set('gradeFilter', grade);
    urlParams.set('page', '1');
    window.location.href = CONTEXT_PATH + '/classregist/student/wish?' + urlParams.toString();
}

function filterSearchResult(grade) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('gradeFilter', grade);
    urlParams.set('page', '1');
    window.location.href = CONTEXT_PATH + '/classregist/student/wish?' + urlParams.toString();
}

function resetSearch() {
    window.location.href = CONTEXT_PATH + '/classregist/student/wish?tab=search';
}

// =========================
// 신청 현황 업데이트
// =========================
function updateApplyStatus() {
    console.log('===> 신청 현황 업데이트 시작, 현재 탭:', currentTab);

    fetch(CONTEXT_PATH + '/classregist/student/rest/wish/apply-status')
        .then(response => response.json())
        .then(data => {
            console.log('===> 신청 현황 데이터:', data);

            let currentCredits, currentSubjects;

            if (currentTab === 'wishlist') {
                console.log('===> 찜 목록 탭 - 찜 기준');
                currentCredits = data.wishlistCredits || 0;
                currentSubjects = data.wishlistSubjects || 0;
            } else {
                console.log('===> 수강신청 탭 - 수강신청 기준');
                currentCredits = data.appliedCredits || 0;
                currentSubjects = data.appliedSubjects || 0;
            }

            document.getElementById('currentCredits').textContent = currentCredits;
            document.getElementById('currentSubjects').textContent = currentSubjects;
            document.getElementById('maxCredits').textContent = data.maxCredit || 24;

            console.log(`===> 업데이트 완료: ${currentSubjects}과목, ${currentCredits}학점`);
        })
        .catch(error => {
            console.error('===> 신청 현황 조회 실패:', error);
        });
}

// =========================
// 찜하기
// =========================
function addWishlist(lectureId) {
    fetch(CONTEXT_PATH + `/classregist/student/rest/wish/${lectureId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            Swal.fire({
                icon: 'success',
                title: '찜 완료!',
                text: '찜 목록에 추가되었습니다.',
                confirmButtonColor: '#4CAF50'
            }).then(() => {
				updateApplyStatus();
                setTimeout(() => location.reload(), 300);  // 약간 지연
			});
        } else {
            let warningText = '';
            if (data.isGradeRestricted) warningText = '해당 학년은 수강할 수 없는 과목입니다.';
            else if (data.isDuplicate) warningText = '이미 같은 과목을 찜했습니다.';
            else if (data.isTimeConflict) warningText = '시간표가 겹칩니다.';
            else if (data.isCreditOver) warningText = '학점이 초과되었습니다.';
            else warningText = data.message || '찜할 수 없습니다.';

            Swal.fire({
                icon: 'error',
                title: '찜 실패',
                text: warningText,
                confirmButtonColor: '#f44336'
            });
        }
    })
    .catch(error => {
        console.error('Error:', error);
        Swal.fire({
            icon: 'error',
            title: '오류',
            text: '서버 오류가 발생했습니다.',
            confirmButtonColor: '#f44336'
        });
    });
}

// =========================
// 찜 취소
// =========================
function removeWishlist(lectureId) {
    Swal.fire({
        title: '취소',
        text: '찜 목록에서 삭제하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#f44336',
        cancelButtonColor: '#757575',
        confirmButtonText: '삭제',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(CONTEXT_PATH + `/classregist/student/rest/wish/${lectureId}`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '삭제 완료',
                        text: '찜 목록에서 삭제되었습니다.',
                        confirmButtonColor: '#4CAF50'
                    }).then(() => {
						updateApplyStatus();
                		setTimeout(() => location.reload(), 300);  // 약간 지연 (지연시간 짧게)
					});
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '삭제 실패',
                        text: data.message || '삭제할 수 없습니다.',
                        confirmButtonColor: '#f44336'
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    icon: 'error',
                    title: '오류',
                    text: '서버 오류가 발생했습니다.',
                    confirmButtonColor: '#f44336'
                });
            });
        }
    });
}

// =========================
// 수강신청
// =========================
function applyLecture(lectureId) {
    Swal.fire({
        title: '수강신청',
        text: '수강신청 하시겠습니까?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#4CAF50',
        cancelButtonColor: '#757575',
        confirmButtonText: '신청',
        cancelButtonText: '취소'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(CONTEXT_PATH + `/classregist/student/rest/wish/apply/${lectureId}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '신청 완료!',
                        text: '수강신청이 완료되었습니다.',
                        confirmButtonColor: '#4CAF50'
                    }).then(() => {
						updateApplyStatus();
                		// 새로고침 X => 대신 특정 행만 업데이트하도록 수정함
                        updateLectureRow(lectureId, true);
					});
                } else {
                    let warningText = '';
                    if (data.isFullCapacity) warningText = '정원이 마감되었습니다.';
                    else if (data.isGradeRestricted) warningText = '해당 학년은 수강할 수 없는 과목입니다.';
                    else if (data.isDuplicate) warningText = '같은 과목을 이미 신청했습니다.';
                    else if (data.isTimeConflict) warningText = '시간표가 겹칩니다.';
                    else if (data.isCreditOver) warningText = '학점이 초과되었습니다.';
                    else warningText = data.message || '신청할 수 없습니다.';

                    Swal.fire({
                        icon: 'error',
                        title: '신청 실패',
                        text: warningText,
                        confirmButtonColor: '#f44336'
                    });
                    // 실패해도 새로고침 안 함!! (웹소켓이 정원 업데이트 처리하기 때문)
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    icon: 'error',
                    title: '오류',
                    text: '서버 오류가 발생했습니다.',
                    confirmButtonColor: '#f44336'
                });
            });
        }
    });
}

// =========================
// 수강신청 취소
// =========================
function cancelApplyLecture(lectureId) {
    Swal.fire({
        title: '수강신청 취소',
        text: '수강신청을 취소하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#f44336',
        cancelButtonColor: '#757575',
        confirmButtonText: '취소하기',
        cancelButtonText: '돌아가기'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(CONTEXT_PATH + `/classregist/student/rest/wish/apply/${lectureId}`, {
                method: 'DELETE',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: '취소 완료',
                        text: '수강신청이 취소되었습니다.',
                        confirmButtonColor: '#4CAF50'
                    }).then(() => {
						updateApplyStatus();
                		// 신청(applied) 탭에서는 새로고침 (행 삭제 필요함)
                        if (currentTab === 'applied') {
                            setTimeout(() => location.reload(), 300);
                        }
					});
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '취소 실패',
                        text: data.message || '취소할 수 없습니다.',
                        confirmButtonColor: '#f44336'
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                Swal.fire({
                    icon: 'error',
                    title: '오류',
                    text: '서버 오류가 발생했습니다.',
                    confirmButtonColor: '#f44336'
                });
            });
        }
    });
}


// 특정 행만 업데이트
function updateLectureRow(lectureId, isApplied) {
    const rows = document.querySelectorAll('.lecture-row');
    rows.forEach(row => {
        const onclickAttr = row.getAttribute('onclick');
        if (onclickAttr && onclickAttr.includes(lectureId)) {
            const actionCell = row.querySelector('td:last-child');
            if (actionCell && currentTab === 'search') {
                const actionButtons = actionCell.querySelector('.action-buttons');
                if (actionButtons) {
                    const applyBtn = actionButtons.querySelector('.btn-apply');
                    if (applyBtn && isApplied) {
                        // 신청 완료 버튼으로 변경
                        applyBtn.className = 'btn-applied';
                        applyBtn.disabled = true;
                        applyBtn.innerHTML = '<span>신청완료</span>';
                    }
                }
            }
        }
    });
}

// =========================
// 강의 상세 모달
// =========================
function showDetail(lectureId) {
    const modal = document.getElementById('detailModal');
    const content = document.getElementById('detailContent');

    // 로딩 메시지
    content.innerHTML = '<p class="loading-message">로딩 중...</p>';
    modal.style.display = 'block';

    fetch(CONTEXT_PATH + `/classregist/student/rest/wish/lecture/detail?lectureId=${lectureId}`)
        .then(response => response.json())
        .then(data => {
            content.innerHTML = `
                <div class="detail-info">
                    <table class="detail-table">
                        <tr>
                            <th>과목명</th>
                            <td>${data.subjectName}</td>
                            <th>담당교수</th>
                            <td>${data.professorName}</td>
                        </tr>
                        <tr>
                            <th>학점/시수</th>
                            <td>${data.credit}/${data.hour}</td>
                            <th>이수구분</th>
                            <td>${data.completionName}</td>
                        </tr>
                        <tr>
                            <th>강의실</th>
                            <td>${data.placeName}</td>
                            <th>강의시간</th>
                            <td>${data.timeInfo}</td>
                        </tr>
                        <tr>
                            <th>정원</th>
                            <td colspan="3">${data.maxCap}명</td>
                        </tr>
                    </table>
                    <div class="detail-section">
                        <h3>강의 개요</h3>
                        <p>${data.lectureIndex || '등록된 강의 개요가 없습니다.'}</p>
                    </div>
                    <div class="detail-section">
                        <h3>강의 목표</h3>
                        <p>${data.lectureGoal || '등록된 강의 목표가 없습니다.'}</p>
                    </div>
                    ${data.prereqSubject ? `
                    <div class="detail-section">
                        <h3>선수과목</h3>
                        <p>${data.prereqSubject}</p>
                    </div>
                    ` : ''}
                </div>
            `;
        })
        .catch(error => {
            console.error('Error:', error);
            // 에러 메시지
            content.innerHTML = '<p class="error-message">정보를 불러올 수 없습니다.</p>';
        });
}

function closeModal() {
    const modal = document.getElementById('detailModal');
    const modalContent = modal.querySelector('.modal-content');

    // 페이드아웃 애니메이션
    modalContent.style.animation = 'modalFadeOut 0.3s ease-out';

    setTimeout(() => {
        modal.style.display = 'none';
        modalContent.style.animation = ''; // 애니메이션 초기화
    }, 300);
}

window.onclick = function(event) {
    const modal = document.getElementById('detailModal');
    if (event.target == modal) {
        closeModal();
    }
};