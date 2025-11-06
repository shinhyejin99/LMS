<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>페이지 준비중</title>
    <style>
        body {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100vh;
            font-family: sans-serif;
            background-color: #f5f5f9;
            color: #566a7f;
            text-align: center;
        }

        h1 {
            font-size: 2em;
            margin-bottom: 0.5em;
        }

        p {
            font-size: 1.2em;
        }

        .uri {
            font-family: monospace;
            background-color: #e0e0e0;
            padding: 0.2em 0.5em;
            border-radius: 4px;
        }
    </style>
    <!-- Bootstrap CSS (필요 시 CDN 유지) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <div class="container">
        <div class="py-4">
            <div class="row">
                <div class="col-12">
                    <div class="card border-0 shadow">
                        <div class="card-header"><h2 class="fs-5 fw-bold mb-0">강의 등록 신청</h2></div>
                        <hr>
                        <div class="card-body">
                            <form id="lectureRequestForm" action="/lms/professor/lectureRegist/regist">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="academicYear" class="form-label">학년도</label>
                                        <input type="text" class="form-control" id="academicYear" name="academicYear" readonly>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="semester" class="form-label">학기</label>
                                        <select class="form-select" id="semester" name="semester" required>
                                            <option value="" disabled selected>학기를 선택하세요</option>
                                            <option value="1">1학기</option>
                                            <option value="2">2학기</option>
                                            <option value="sumseasonal">여름계절학기</option>
                                            <option value="winseasonal">겨울계절학기</option>
                                        </select>
                                    </div>
                                </div>                            
                                <div class="mb-3">
                                    <label for="subjectCode" class="form-label">과목 선택 <span class="text-danger">*</span></label>
                                    <select class="form-select" id="subjectCode" name="subjectCode" required>
                                        <option value="">과목을 선택하세요</option>
                                        <!-- Subject codes will be populated here dynamically -->
                                    </select>
                                </div>
                                                                <input type="hidden" id="lectureName" name="lectureName">
                                                                <input type="hidden" id="courseType" name="courseType">
                                                                <div class="row">                                    <div class="col-md-6 mb-3">
                                        <label for="credits" class="form-label">학점 <span class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="credits" name="credits" min="1" max="6" required readonly>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="hours" class="form-label">시수 <span class="text-danger">*</span></label>
                                        <input type="number" class="form-control" id="hours" name="hours" min="1" max="9" required readonly>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="prerequisiteCourse" class="form-label">선수학습과목</label>
                                    <input type="text" class="form-control" id="prerequisiteCourse" name="prerequisiteCourse" placeholder="예: 기본적인 학문적 배경이 필요 ">
                                </div>
                                <div class="mb-3">
                                    <label for="courseObjectives" class="form-label">강의목표 <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="courseObjectives" name="courseObjectives" required placeholder="예: 해당 분야의 핵심 개념을 이해하고 이를 실제 상황에 적용할 수 있는 능력을 배양한다.">
                                </div>
                                <div class="mb-3">
                                    <label for="courseOverview" class="form-label">강의개요 <span class="text-danger">*</span></label>
                                    <textarea class="form-control" id="courseOverview" name="courseOverview" rows="3" required placeholder="예: 이론적 배경을 학습하고 이를 통해 문제 해결 능력을 키운다"></textarea>
                                </div>
                                <div class="mb-3">
                                    <label for="expectedCapacity" class="form-label">예상정원 <span class="text-danger">*</span></label>
                                    <input type="number" class="form-control" id="expectedCapacity" name="expectedCapacity" min="10" max="100" value="30" step="5" required>
                                </div>
                                <div class="mb-3 card p-3 bg-light">
                                    <h5 class="mb-3">주차별 강의계획 <span class="text-danger">*</span></h5>
                                    <div id="weekly-plan-container">
                                        <!-- Weekly items are now generated by JavaScript -->
                                    </div>
                                    <div class="mt-2">
                                        <button type="button" id="add-week-btn" class="btn btn-outline-primary w-100">[+ 주차 추가]</button>
                                    </div>
                                </div>

                                <div class="mb-3 card p-3 bg-light">
                                    <h5 class="mb-3">성적 산출 비율 <span class="text-danger">*</span></h5>
                                    <div id="grading-criteria-container">
                                        <!-- Statically creating grading items -->
                                        <div class="row mb-2 grading-item">
                                            <div class="col-md-5">
                                                <input type="text" class="form-control" value="시험" readonly>
                                                <input type="hidden" name="gradingItemType" value="EXAM">
                                            </div>
                                            <div class="col-md-5">
                                                <div class="input-group">
                                                    <input type="number" class="form-control grading-item-percentage" name="gradingItemPercentage" min="0" max="100" value="0" step="5">
                                                    <span class="input-group-text">%</span>
                                                </div>
                                            </div>
                                            <div class="col-md-2 d-flex align-items-end">
                                                <button type="button" class="btn btn-outline-danger btn-sm remove-grading-item w-100">삭제</button>
                                            </div>
                                        </div>
                                        <div class="row mb-2 grading-item">
                                            <div class="col-md-5">
                                                <input type="text" class="form-control" value="과제" readonly>
                                                <input type="hidden" name="gradingItemType" value="TASK">
                                            </div>
                                            <div class="col-md-5">
                                                <div class="input-group">
                                                    <input type="number" class="form-control grading-item-percentage" name="gradingItemPercentage" min="0" max="100" value="0" step="5">
                                                    <span class="input-group-text">%</span>
                                                </div>
                                            </div>
                                            <div class="col-md-2 d-flex align-items-end">
                                                <button type="button" class="btn btn-outline-danger btn-sm remove-grading-item w-100">삭제</button>
                                            </div>
                                        </div>
                                        <div class="row mb-2 grading-item">
                                            <div class="col-md-5">
                                                <input type="text" class="form-control" value="출석" readonly>
                                                <input type="hidden" name="gradingItemType" value="ATTD">
                                            </div>
                                            <div class="col-md-5">
                                                <div class="input-group">
                                                    <input type="number" class="form-control grading-item-percentage" name="gradingItemPercentage" min="0" max="100" value="0" step="5">
                                                    <span class="input-group-text">%</span>
                                                </div>
                                            </div>
                                            <div class="col-md-2 d-flex align-items-end">
                                                <button type="button" class="btn btn-outline-danger btn-sm remove-grading-item w-100">삭제</button>
                                            </div>
                                        </div>
                                        <div class="row mb-2 grading-item">
                                            <div class="col-md-5">
                                                <input type="text" class="form-control" value="기타" readonly>
                                                <input type="hidden" name="gradingItemType" value="MISC">
                                            </div>
                                            <div class="col-md-5">
                                                <div class="input-group">
                                                    <input type="number" class="form-control grading-item-percentage" name="gradingItemPercentage" min="0" max="100" value="0" step="5">
                                                    <span class="input-group-text">%</span>
                                                </div>
                                            </div>
                                            <div class="col-md-2 d-flex align-items-end">
                                                <button type="button" class="btn btn-outline-danger btn-sm remove-grading-item w-100">삭제</button>
                                            </div>
                                        </div>
                                        <div class="row mb-2 grading-item">
                                            <div class="col-md-5">
                                                <input type="text" class="form-control" value="실습" readonly>
                                                <input type="hidden" name="gradingItemType" value="PRAC">
                                            </div>
                                            <div class="col-md-5">
                                                <div class="input-group">
                                                    <input type="number" class="form-control grading-item-percentage" name="gradingItemPercentage" min="0" max="100" value="0" step="5">
                                                    <span class="input-group-text">%</span>
                                                </div>
                                            </div>
                                            <div class="col-md-2 d-flex align-items-end">
                                                <button type="button" class="btn btn-outline-danger btn-sm remove-grading-item w-100">삭제</button>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="mt-2">
                                        <button type="button" id="add-grading-item-btn" class="btn btn-outline-primary w-100">[+ 항목 추가]</button>
                                    </div>
                                    <div class="mt-3">
                                        <strong>총 합계: <span id="total-percentage">0</span>%</strong>
                                        <span id="percentage-validation-message" class="text-danger ms-2"></span>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="preferredDayTime" class="form-label">희망 요일·교시 <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="preferredDayTime" name="preferredDayTime" placeholder="예: 월 1-2교시, 수 3-4교시" required>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="preferredClassroom" class="form-label">희망 강의실 <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="preferredClassroom" name="preferredClassroom" placeholder="예: 공학관 301호" required>
                                    </div>
                                </div>
                                <button type="submit" class="btn btn-primary">신청</button>
                                <button type="button" class="btn btn-secondary" id="cancelRequestBtn">취소</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Auto-populate Academic Year
            const academicYearInput = document.getElementById('academicYear');
            const today = new Date();
            academicYearInput.value = today.getFullYear();

            // Weekly Plan Logic
            const weeklyPlanContainer = document.getElementById('weekly-plan-container');

            const weeklyItemTemplate = `
                <div class="row mb-2 weekly-item">
                    <div class="col-md-2">
                        <label class="form-label">주차</label>
                        <input type="text" class="form-control weekly-item-week" name="weeklyItemWeek" value="1" readonly required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">학습목표</label>
                        <input type="text" class="form-control weekly-item-objective" name="weeklyItemObjective" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">학습내용</label>
                        <textarea class="form-control weekly-item-content" name="weeklyItemContent" rows="2" required></textarea>
                    </div>
                    <div class="col-md-2 d-flex align-items-end">
                        <button type="button" class="btn btn-outline-danger btn-sm remove-weekly-item w-100">삭제</button>
                    </div>
                </div>
            `;



            function renumberWeeklyItems() {
                const weeklyItems = weeklyPlanContainer.querySelectorAll('.weekly-item');
                weeklyItems.forEach((item, index) => {
                    const weekNumber = index + 1;
                    const weekInput = item.querySelector('.weekly-item-week');
                    if (weekInput) {
                        weekInput.value = weekNumber;
                    }
                });
            }

            // New listener for the single "Add Week" button
            document.getElementById('add-week-btn').addEventListener('click', function() {
                weeklyPlanContainer.insertAdjacentHTML('beforeend', weeklyItemTemplate);
                renumberWeeklyItems();
            });

            // Listener for remove buttons inside the container
            weeklyPlanContainer.addEventListener('click', function(event) {
                const target = event.target;

                if (target.classList.contains('remove-weekly-item')) {
                    const itemToRemove = target.closest('.weekly-item');

                    
                    itemToRemove.remove();
                    renumberWeeklyItems();
                }
            });

            // Initialize with 12 items if container is empty
            if (weeklyPlanContainer.children.length === 0) {
                let initialItems = '';
                // Add 12 weeks without buttons
                for (let i = 0; i < 12; i++) {
                    initialItems += weeklyItemTemplate;
                }
                weeklyPlanContainer.innerHTML = initialItems;
            }
            renumberWeeklyItems();

                // Grading Criteria Logic
                const gradingCriteriaContainer = document.getElementById('grading-criteria-container');
                const totalPercentageSpan = document.getElementById('total-percentage');
                const percentageValidationMessage = document.getElementById('percentage-validation-message');

                function updatePercentages() {
                    let total = 0;
                    gradingCriteriaContainer.querySelectorAll('.grading-item-percentage').forEach(input => {
                        total += parseInt(input.value) || 0;
                    });
                    totalPercentageSpan.textContent = total;

                    if (total !== 100) {
                        percentageValidationMessage.textContent = '성적 비율의 합계는 100%여야 합니다.';
                        percentageValidationMessage.style.color = 'red';
                    } else {
                        percentageValidationMessage.textContent = '합계 100% 일치';
                        percentageValidationMessage.style.color = 'green';
                    }
                }

                gradingCriteriaContainer.addEventListener('input', function(event) {
                    if (event.target.classList.contains('grading-item-percentage')) {
                        updatePercentages();
                    }
                });
                
                // Listener for remove buttons inside the grading criteria container
                gradingCriteriaContainer.addEventListener('click', function(event) {
                    const target = event.target;

                    if (target.classList.contains('remove-grading-item')) {
                        const itemToRemove = target.closest('.grading-item');
                        itemToRemove.remove();
                        updatePercentages(); // Recalculate percentages after removal
                    }
                });

                updatePercentages(); // Initial call

                const newGradingItemTemplate = `
                    <div class="row mb-2 grading-item">
                        <div class="col-md-5">
                            <input type="text" class="form-control grading-item-name" name="gradingItemName" placeholder="항목명" required>
                            <input type="hidden" name="gradingItemType" value="CUSTOM">
                        </div>
                        <div class="col-md-5">
                            <div class="input-group">
                                <input type="number" class="form-control grading-item-percentage" name="gradingItemPercentage" min="0" max="100" value="0" step="5">
                                <span class="input-group-text">%</span>
                            </div>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button type="button" class="btn btn-outline-danger btn-sm remove-grading-item w-100">삭제</button>
                        </div>
                    </div>
                `;

                // Listener for the "Add Grading Item" button
                document.getElementById('add-grading-item-btn').addEventListener('click', function() {
                    gradingCriteriaContainer.insertAdjacentHTML('beforeend', newGradingItemTemplate);
                    updatePercentages(); // Update percentages to reflect the new item (initially 0%)
                });

                        // --- New Client-Side Logic based on User Suggestion ---

            const completionCodeMap = {
                'MAJ_BASIC': '전공-기초',
                'MAJ_CORE': '전공-핵심',
                'MAJ_ELEC': '전공-선택',
                'GE_BASIC': '교양-기초',
                'GE_CORE': '교양-핵심',
                'GE_ELEC': '교양-선택'
            };
            
            // Form field elements
            
            // Form field elements
            const subjectCodeSelect = document.getElementById('subjectCode');
            const lectureNameInput = document.getElementById('lectureName');
            const creditsInput = document.getElementById('credits');
            const hoursInput = document.getElementById('hours');
            const courseTypeSelect = document.getElementById('courseType');

            // To store all subject data fetched initially
            let allSubjectsData = [];

            // Function to fetch all subject data at once and populate the dropdown
            function fetchAllSubjects() {
                fetch('/lms/professor/lectureRegist/getSubjectCodes')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    allSubjectsData = data; // Store all data globally
                    
                    // Clear existing options except the first one
                    while (subjectCodeSelect.options.length > 1) {
                        subjectCodeSelect.remove(1);
                    }

                    // Populate dropdown
                    allSubjectsData.forEach(subject => {
                        const option = document.createElement('option');
                        
                        // Handle potential case differences for keys from backend
                        const subjectCd = subject.SUBJECT_CD || subject.subject_cd;
                        const subjectName = subject.SUBJECT_NAME || subject.subject_name;
                        const credit = subject.CREDIT || subject.credit;
                        const hour = subject.HOUR || subject.hour;

                        option.value = subjectCd;
                        option.dataset.credit = credit; // Store in data attribute
                        option.dataset.hour = hour;     // Store in data attribute

                        const subjectNameMatch = subjectName.match(/^(.*)\(([^)]+)\)$/);
                        if (subjectNameMatch) {
                            const namePart = subjectNameMatch[1].trim();
                            const codePart = subjectNameMatch[2];
                            const translatedCode = completionCodeMap[codePart] || codePart;
                            option.textContent = namePart + ' (' + translatedCode + ')';
                        } else {
                            option.textContent = subjectName;
                        }
                        subjectCodeSelect.appendChild(option);
                    });
                })
                .catch(error => console.error('Error fetching subject codes:', error));
            }

            // Event listener for when a subject code is selected
            subjectCodeSelect.addEventListener('change', function() {
                const selectedOption = this.options[this.selectedIndex];
                const selectedCode = this.value;

                if (selectedCode && selectedOption) {
                    // Populate credits and hours from data attributes for reliability
                    creditsInput.value = selectedOption.dataset.credit || '';
                    hoursInput.value = selectedOption.dataset.hour || '';

                    // Find the selected subject from the stored data for other details
                    const selectedSubject = allSubjectsData.find(s => (s.SUBJECT_CD || s.subject_cd) === selectedCode);
                    if (selectedSubject) {
                        const subjectName = selectedSubject.SUBJECT_NAME || selectedSubject.subject_name;
                        
                        // Populate Lecture Name if the element exists
                        if (lectureNameInput) {
	                        let lectureName = subjectName.replace(/\s*\([^)]*\)$/, '');
	                        lectureNameInput.value = lectureName;
                        }

                        // Populate Course Type if the element exists
                        if (courseTypeSelect) {
	                        const match = subjectName.match(/\(([^)]*)\)$/);
	                        if (match && match[1]) {
	                            courseTypeSelect.value = match[1];
	                        } else {
	                            courseTypeSelect.value = '';
	                        }
                        }
                    }
                } else {
                    // Clear all fields if the default "Select..." option is chosen
                    clearDetails();
                }
            });

            function clearDetails() {
                lectureNameInput.value = '';
                creditsInput.value = '';
                hoursInput.value = '';
                courseTypeSelect.value = '';
            }

            // Initial fetch of all subject data when page loads
            fetchAllSubjects();

            // Handle form submission via AJAX
            const lectureRequestForm = document.getElementById('lectureRequestForm');
            const contextPath = "${pageContext.request.contextPath}"; // Define contextPath here

            lectureRequestForm.addEventListener('submit', async function(event) {
                event.preventDefault(); // Prevent default form submission

                // Validate grading percentages
                const totalPercentage = parseInt(totalPercentageSpan.textContent);
                if (totalPercentage !== 100) {
                    alert('성적 비율의 합계는 100%여야 합니다.');
                    return;
                }

                // Collect form data
                const formData = new FormData(this);
                const csrfToken = document.querySelector('input[name="${_csrf.parameterName}"]').value; // Get CSRF token

                // FormData automatically collects data from elements with 'name' attributes.
                // The dynamic fields (weeklyItemWeek, weeklyItemObjective, weeklyItemContent,
                // gradingItemType, gradingItemPercentage) already have correct 'name' attributes.
                // No manual appending is needed here.

                try {
                    const response = await fetch(this.action, {
                        method: 'POST',
                        headers: {
                            'X-CSRF-TOKEN': csrfToken // Include CSRF token in headers
                        },
                        body: new URLSearchParams(formData) // FormData needs to be converted for x-www-form-urlencoded
                    });

                    const result = await response.json();

                    if (result.status === 'success') {
                        alert(result.message);
                        window.location.href = `${contextPath}/lms/professor/lectureRegist/list`; // Redirect on success
                    } else {
                        alert(result.message);
                    }
                } catch (error) {
                    console.error('Error submitting lecture registration:', error);
                    alert('강의 등록 신청 중 오류가 발생했습니다.');
                }
            });
        });
    </script>
</body>
</html>