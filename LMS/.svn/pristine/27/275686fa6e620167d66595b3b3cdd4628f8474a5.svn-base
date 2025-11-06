/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 * 2025. 10. 28.     	송태호            최초 생성
 *
 * </pre>
 */

// 전역 데이터 저장소
let subjectsData = [];
let gradingCriteriaData = {};
let selectedSubject = null;

// DOM 요소들 (나중에 초기화)
let yearTermSelect;
let collegeSelect;
let deptSelect;
let completionSelect;
let subjectSelect;
let weeklyPlanContainer;
let addWeekBtn;
let gradingCriteriaContainer;
let totalPercentageSpan;
let percentageMessage;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // DOM 요소 초기화
    yearTermSelect = document.getElementById('yearTermSelect');
    collegeSelect = document.getElementById('collegeSelect');
    deptSelect = document.getElementById('deptSelect');
    completionSelect = document.getElementById('completionSelect');
    subjectSelect = document.getElementById('subjectSelect');
    weeklyPlanContainer = document.getElementById('weeklyPlanContainer');
    addWeekBtn = document.getElementById('addWeekBtn');
    gradingCriteriaContainer = document.getElementById('gradingCriteriaContainer');
    totalPercentageSpan = document.getElementById('totalPercentage');
    percentageMessage = document.getElementById('percentageMessage');

    initializePage();
});

// 초기화 함수
async function initializePage() {
    await loadSemesters();
    await loadSubjects();
    await loadGradingCriteria();
    initializeWeeks(); // 초기 3개 주차 생성
    setupEventListeners();
}

// 학기 정보 로드
async function loadSemesters() {
    try {
        const response = await fetch('/lms/api/v1/professor/lecture/apply/semester');
        const data = await response.json();

        // 2026년 1학기와 2학기 옵션 추가 (시연용)
        const semesters = [
            { yearTermCd: '2026_REG1', yearTermName: '2026년도 1학기' },
            { yearTermCd: '2026_REG2', yearTermName: '2026년도 2학기' }
        ];

        semesters.forEach(semester => {
            const option = document.createElement('option');
            option.value = semester.yearTermCd;
            option.textContent = semester.yearTermName;
            yearTermSelect.appendChild(option);
        });
    } catch (error) {
        console.error('학기 정보 로드 실패:', error);
    }
}

// 과목 정보 로드
async function loadSubjects() {
    try {
        const response = await fetch('/lms/api/v1/common/subject');
        subjectsData = await response.json();

        // 단과대학 옵션 채우기
        populateColleges();
    } catch (error) {
        console.error('과목 정보 로드 실패:', error);
    }
}

// 단과대학 목록 채우기
function populateColleges() {
    collegeSelect.innerHTML = '<option value="">단과대학을 선택하세요</option>';

    subjectsData.forEach(college => {
        const option = document.createElement('option');
        option.value = college.collegeCd;
        option.textContent = college.collegeName;
        option.dataset.college = JSON.stringify(college);
        collegeSelect.appendChild(option);
    });
}

// 학과 목록 채우기
function populateDepartments(collegeCd) {
    deptSelect.innerHTML = '<option value="">학과를 선택하세요</option>';
    completionSelect.innerHTML = '<option value="">이수구분을 선택하세요</option>';
    subjectSelect.innerHTML = '<option value="">과목을 선택하세요</option>';

    deptSelect.disabled = true;
    completionSelect.disabled = true;
    subjectSelect.disabled = true;
    clearSubjectDetails();

    const college = subjectsData.find(c => c.collegeCd === collegeCd);
    if (college && college.deptList) {
        deptSelect.disabled = false;
        college.deptList.forEach(dept => {
            const option = document.createElement('option');
            option.value = dept.univDeptCd;
            option.textContent = dept.univDeptName;
            option.dataset.dept = JSON.stringify(dept);
            deptSelect.appendChild(option);
        });
    }
}

// 이수구분 목록 채우기
function populateCompletions(collegeCd, deptCd) {
    completionSelect.innerHTML = '<option value="">이수구분을 선택하세요</option>';
    subjectSelect.innerHTML = '<option value="">과목을 선택하세요</option>';

    completionSelect.disabled = true;
    subjectSelect.disabled = true;
    clearSubjectDetails();

    const college = subjectsData.find(c => c.collegeCd === collegeCd);
    if (college) {
        const dept = college.deptList.find(d => d.univDeptCd === deptCd);
        if (dept && dept.subjectList) {
            // 이수구분 목록 추출 (중복 제거)
            const completions = new Map();
            dept.subjectList.forEach(subject => {
                if (!completions.has(subject.completionCd)) {
                    completions.set(subject.completionCd, subject.completionName);
                }
            });

            if (completions.size > 0) {
                completionSelect.disabled = false;
                completions.forEach((name, code) => {
                    const option = document.createElement('option');
                    option.value = code;
                    option.textContent = name;
                    completionSelect.appendChild(option);
                });
            }
        }
    }
}

// 과목 목록 채우기 (이수구분 필터링)
function populateSubjects(collegeCd, deptCd, completionCd) {
    subjectSelect.innerHTML = '<option value="">과목을 선택하세요</option>';
    clearSubjectDetails();

    const college = subjectsData.find(c => c.collegeCd === collegeCd);
    if (college) {
        const dept = college.deptList.find(d => d.univDeptCd === deptCd);
        if (dept && dept.subjectList) {
            // 선택한 이수구분에 해당하는 과목만 필터링
            const filteredSubjects = dept.subjectList.filter(
                subject => subject.completionCd === completionCd
            );

            if (filteredSubjects.length > 0) {
                subjectSelect.disabled = false;
                filteredSubjects.forEach(subject => {
                    const option = document.createElement('option');
                    option.value = subject.subjectCd;
                    // 과목명에 평가방식/학점/시수 추가
                    const displayText = `${subject.subjectName} (${subject.subjectTypeName} / ${subject.credit}학점 / ${subject.hour}시수)`;
                    option.textContent = displayText;
                    option.dataset.subject = JSON.stringify(subject);
                    subjectSelect.appendChild(option);
                });
            }
        }
    }
}

// 과목 상세 정보 표시
function displaySubjectDetails(subjectCd) {
    const option = subjectSelect.querySelector(`option[value="${subjectCd}"]`);
    if (option) {
        selectedSubject = JSON.parse(option.dataset.subject);
    }
}

// 과목 상세 정보 초기화
function clearSubjectDetails() {
    selectedSubject = null;
}

// 성적 산출 기준 로드
async function loadGradingCriteria() {
    try {
        const response = await fetch('/lms/api/v1/common/lecture/criteria');
        gradingCriteriaData = await response.json();

        // 성적 산출 항목 렌더링
        renderGradingCriteria();
    } catch (error) {
        console.error('성적 산출 기준 로드 실패:', error);
    }
}

// 성적 산출 항목 렌더링
function renderGradingCriteria() {
    gradingCriteriaContainer.innerHTML = '';

    Object.entries(gradingCriteriaData).forEach(([code, name]) => {
        const div = document.createElement('div');
        div.className = 'grading-item';
        div.innerHTML = `
            <input class="grading-toggle" type="checkbox" id="grading_${code}" data-code="${code}">
            <label for="grading_${code}">${name}</label>
            <div class="grading-percentage-group">
                <input type="text" class="grading-percentage" id="percentage_${code}"
                       value="0" disabled maxlength="3">
                <span>%</span>
            </div>
        `;
        gradingCriteriaContainer.appendChild(div);
    });

    // 입력 검증 이벤트 추가
    setupGradingValidation();
}

// 성적 비율 입력 검증
function setupGradingValidation() {
    document.querySelectorAll('.grading-percentage').forEach(input => {
        input.addEventListener('blur', function() {
            let value = this.value.trim();

            // 빈 값이면 0으로
            if (value === '') {
                this.value = '0';
                updatePercentageTotal();
                return;
            }

            // 숫자가 아니면 0으로
            if (isNaN(value)) {
                alert('숫자만 입력 가능합니다.');
                this.value = '0';
                updatePercentageTotal();
                return;
            }

            // 0~100 범위 체크
            let numValue = parseInt(value);
            if (numValue < 0 || numValue > 100) {
                alert('0에서 100 사이의 값을 입력하세요.');
                this.value = '0';
                updatePercentageTotal();
                return;
            }

            // 정수로 변환
            this.value = numValue.toString();
            updatePercentageTotal();
        });

        input.addEventListener('input', function() {
            // 숫자만 입력 허용
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    });
}

// 성적 비율 합계 계산
function updatePercentageTotal() {
    let total = 0;
    document.querySelectorAll('.grading-percentage:not(:disabled)').forEach(input => {
        total += parseInt(input.value) || 0;
    });

    totalPercentageSpan.textContent = total;

    if (total === 100) {
        percentageMessage.textContent = '✓ 합계가 100%입니다.';
        percentageMessage.className = 'percentage-msg success';
    } else if (total > 0) {
        percentageMessage.textContent = `⚠ 합계가 ${total}%입니다. 100%가 되어야 합니다.`;
        percentageMessage.className = 'percentage-msg error';
    } else {
        percentageMessage.textContent = '';
        percentageMessage.className = 'percentage-msg';
    }

    return total;
}

// 주차 추가
function addWeek() {
    const weekNumber = weeklyPlanContainer.children.length + 1;
    const div = document.createElement('div');
    div.className = 'week-item';
    div.innerHTML = `
        <div class="week-row">
            <label class="week-label">${weekNumber}주차</label>
            <div class="week-inputs-vertical">
                <input type="text" class="week-objective" data-week="${weekNumber}" placeholder="학습목표를 입력하세요">
                <textarea class="week-content" data-week="${weekNumber}" placeholder="학습내용을 입력하세요"></textarea>
            </div>
            <button type="button" class="btn-remove-week" onclick="removeWeek(this)">삭제</button>
        </div>
    `;
    weeklyPlanContainer.appendChild(div);
    updateWeekNumbers();
}

// 주차 삭제
function removeWeek(button) {
    const weekItem = button.closest('.week-item');
    weekItem.remove();
    updateWeekNumbers();
}

// 주차 번호 업데이트
function updateWeekNumbers() {
    const weekItems = weeklyPlanContainer.querySelectorAll('.week-item');
    weekItems.forEach((item, index) => {
        const weekNumber = index + 1;
        const label = item.querySelector('.week-label');
        if (label) {
            label.textContent = `${weekNumber}주차`;
        }
        item.querySelector('.week-objective').dataset.week = weekNumber;
        item.querySelector('.week-content').dataset.week = weekNumber;
    });
}

// 초기 3개 주차 생성
function initializeWeeks() {
    weeklyPlanContainer.innerHTML = '';
    for (let i = 0; i < 3; i++) {
        addWeek();
    }
}

// removeWeek를 전역으로 노출
window.removeWeek = removeWeek;

// 이벤트 리스너 설정
function setupEventListeners() {
    // 단과대학 선택
    collegeSelect.addEventListener('change', function() {
        if (this.value) {
            populateDepartments(this.value);
        } else {
            deptSelect.innerHTML = '<option value="">학과를 선택하세요</option>';
            deptSelect.disabled = true;
            completionSelect.innerHTML = '<option value="">이수구분을 선택하세요</option>';
            completionSelect.disabled = true;
            subjectSelect.innerHTML = '<option value="">과목을 선택하세요</option>';
            subjectSelect.disabled = true;
            clearSubjectDetails();
        }
    });

    // 학과 선택
    deptSelect.addEventListener('change', function() {
        if (this.value && collegeSelect.value) {
            populateCompletions(collegeSelect.value, this.value);
        } else {
            completionSelect.innerHTML = '<option value="">이수구분을 선택하세요</option>';
            completionSelect.disabled = true;
            subjectSelect.innerHTML = '<option value="">과목을 선택하세요</option>';
            subjectSelect.disabled = true;
            clearSubjectDetails();
        }
    });

    // 이수구분 선택
    completionSelect.addEventListener('change', function() {
        if (this.value && collegeSelect.value && deptSelect.value) {
            populateSubjects(collegeSelect.value, deptSelect.value, this.value);
        } else {
            subjectSelect.innerHTML = '<option value="">과목을 선택하세요</option>';
            subjectSelect.disabled = true;
            clearSubjectDetails();
        }
    });

    // 과목 선택
    subjectSelect.addEventListener('change', function() {
        if (this.value) {
            displaySubjectDetails(this.value);
        } else {
            clearSubjectDetails();
        }
    });

    // 주차 추가 버튼
    addWeekBtn.addEventListener('click', addWeek);

    // 성적 산출 항목 토글
    gradingCriteriaContainer.addEventListener('change', function(e) {
        if (e.target.classList.contains('grading-toggle')) {
            const code = e.target.dataset.code;
            const percentageInput = document.getElementById(`percentage_${code}`);
            percentageInput.disabled = !e.target.checked;
            if (!e.target.checked) {
                percentageInput.value = 0;
            }
            updatePercentageTotal();
        }

        if (e.target.classList.contains('grading-percentage')) {
            updatePercentageTotal();
        }
    });

    // 디버깅 자동입력 버튼
    document.getElementById('debugFillBtn').addEventListener('click', fillDebugData);

    // 신청 버튼
    document.getElementById('lectureApplyForm').addEventListener('submit', handleSubmit);

    // 취소 버튼
    document.getElementById('cancelBtn').addEventListener('click', function() {
        if (confirm('작성 중인 내용이 삭제됩니다. 취소하시겠습니까?')) {
            window.history.back();
        }
    });
}

// 디버깅용 자동입력
function fillDebugData() {
    // 학기 선택
    if (yearTermSelect.options.length > 1) {
        yearTermSelect.selectedIndex = 1;
    }

    // 단과대학 선택
    if (collegeSelect.options.length > 1) {
        collegeSelect.selectedIndex = 8;
        collegeSelect.dispatchEvent(new Event('change'));

        setTimeout(() => {
            // 학과 선택
            if (deptSelect.options.length > 1) {
                deptSelect.selectedIndex = 2;
                deptSelect.dispatchEvent(new Event('change'));

                setTimeout(() => {
                    // 이수구분 선택
                    if (completionSelect.options.length > 1) {
                        completionSelect.selectedIndex = 2;
                        completionSelect.dispatchEvent(new Event('change'));

                        setTimeout(() => {
                            // 과목 선택
                            if (subjectSelect.options.length > 1) {
                                subjectSelect.selectedIndex = 7;
                                subjectSelect.dispatchEvent(new Event('change'));
                            }
                        }, 100);
                    }
                }, 100);
            }
        }, 100);
    }

    // 강의 정보 입력
    document.getElementById('prerequisite').value = '프로그래밍 기초, 자료구조';
    document.getElementById('overview').value = '본 강의는 웹 프로그래밍의 기초부터 실전까지 다루는 종합 과정입니다. HTML, CSS, JavaScript를 학습하고 실제 웹 애플리케이션을 개발합니다.';
    document.getElementById('objective').value = '웹 프로그래밍의 핵심 개념을 이해하고, 실무에서 사용 가능한 웹 애플리케이션을 개발할 수 있는 능력을 배양합니다.';
    document.getElementById('capacity').value = '40';
    document.getElementById('preferences').value = '공학관 PC실 희망, 월/수 2-3교시 희망';

    // 주차별 계획 (기존 삭제 후 15주차 생성)
    setTimeout(() => {
        // 기존 주차 전체 삭제
        weeklyPlanContainer.innerHTML = '';

        // 15주차 생성
        for (let i = 0; i < 15; i++) {
            addWeek();
        }

        setTimeout(() => {
            document.querySelectorAll('.week-objective').forEach((input, index) => {
                input.value = `${index + 1}주차 학습목표 예시`;
            });

            document.querySelectorAll('.week-content').forEach((textarea, index) => {
                textarea.value = `${index + 1}주차 학습내용 예시입니다. 상세한 내용을 여기에 작성합니다.`;
            });
        }, 100);
    }, 100);

    
}

// 폼 제출 처리
async function handleSubmit(e) {
    e.preventDefault();

    // 유효성 검사
    if (!yearTermSelect.value) {
        Swal.fire('유효성 오류', '학년도/학기를 선택하세요.', 'warning');
        return;
    }
    if (!subjectSelect.value) {
        Swal.fire('유효성 오류', '과목을 선택하세요.', 'warning');
        return;
    }
    const total = updatePercentageTotal();
    if (total !== 100) {
        Swal.fire('유효성 오류', '성적 산출 비율의 합계가 100%가 되어야 합니다.', 'warning');
        return;
    }
    const weekObjectives = document.querySelectorAll('.week-objective');
    if (weekObjectives.length === 0) {
        Swal.fire('유효성 오류', '주차별 계획을 입력하세요.', 'warning');
        return;
    }

    // SweetAlert로 제출 확인
    const confirmation = await Swal.fire({
        title: '강의 개설 신청',
        text: "입력한 내용으로 강의 개설을 신청하시겠습니까?",
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: '신청',
        cancelButtonText: '취소'
    });

    if (confirmation.isConfirmed) {
        // 신청 객체 생성
        const applicationData = createApplicationObject();

        // 서버로 전송
        try {
            const response = await fetch('/lms/api/v1/professor/lecture/apply', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(applicationData)
            });

            if (response.status === 201 || response.ok) {
                const result = await response.json();
                const location = response.headers.get('Location');

                await Swal.fire({
                    title: '신청 완료',
                    text: '강의 개설 신청이 완료되었습니다.',
                    icon: 'success'
                });

                if (location) {
                    window.location.href = location;
                } else if (result.postId) {
                    window.location.href = `/lms/professor/lecture/apply/${result.postId}`;
                } else {
                    window.location.href = '/lms/professor/lecture/apply';
                }
            } else {
                const error = await response.text();
                throw new Error(error || '신청 처리 중 오류가 발생했습니다.');
            }
        } catch (error) {
            console.error('Error:', error);
            Swal.fire({
                title: '오류 발생',
                text: error.message || '서버와의 통신 중 오류가 발생했습니다.',
                icon: 'error'
            });
        }
    }
}

// 신청 객체 생성 (백엔드 DTO 구조에 맞춤)
function createApplicationObject() {
    // 주차별 계획 수집
    const weekbyList = [];
    document.querySelectorAll('.week-objective').forEach((input, index) => {
        const week = index + 1;
        const objective = input.value;
        const content = document.querySelector(`.week-content[data-week="${week}"]`).value;

        weekbyList.push({
            lectureWeek: week,
            weekGoal: objective,
            weekDesc: content
        });
    });

    // 성적 산출 항목 수집
    const graderatioList = [];
    document.querySelectorAll('.grading-toggle:checked').forEach(checkbox => {
        const code = checkbox.dataset.code;
        const percentage = parseInt(document.getElementById(`percentage_${code}`).value) || 0;

        graderatioList.push({
            gradeCriteriaCd: code,
            ratio: percentage
        });
    });

    // 최종 신청 객체 (LctOpenApplyReq 구조)
    return {
        subjectCd: subjectSelect.value,
        yeartermCd: yearTermSelect.value,
        lectureIndex: document.getElementById('overview').value,
        lectureGoal: document.getElementById('objective').value,
        prereqSubject: document.getElementById('prerequisite').value,
        expectCap: parseInt(document.getElementById('capacity').value),
        desireOption: document.getElementById('preferences').value,
        weekbyList: weekbyList,
        graderatioList: graderatioList
    };
}
