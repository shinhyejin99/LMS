/**
 * 학과 목록 테이블 행 클릭 시 상세 모달을 띄우고,
 * 상세 모달 내의 '수정' 버튼 클릭 시 수정 모달로 전환하는 함수.
 * @param {string} univDeptCd 클릭된 학과의 코드
 */
function showDeptDetailModal(univDeptCd) {
    const deptData = JAVASCRIPT_DATA.staffunivDeptList.find(d => d.univDeptCd === univDeptCd);
    if (!deptData) {
        Swal.fire({
            icon: 'error',
            text: '학과 정보를 찾을 수 없습니다.',
            confirmButtonColor: '#1E88E5'
        });
        return;
    }

    const getValue = (v, f = 'N/A') => v ?? f;
    const getCountString = (v, f = 'N/A') => v ? Number(v).toLocaleString() : f;

    const isDeleted = String(deptData.deleteAt ?? '').trim() !== '';
    const subjectsCount = !isNaN(Number(deptData.subjectsCount)) ? Number(deptData.subjectsCount) : 0;

    // 상세모달 데이터 바인딩
    $('#modal-dept-name-title-detail').text(getValue(deptData.univDeptName));
    $('#modal-univDeptCd-detail').text(getValue(deptData.univDeptCd));
    $('#modal-univDeptName-detail').text(getValue(deptData.univDeptName));
    $('#modal-collegeName-detail').text(getValue(deptData.collegeName));
    $('#modal-deptHeadName-detail').text(getValue(deptData.deptHeadName));
    $('#modal-officeNo-detail').text(getValue(deptData.telNo));
    $('#modal-createAt-detail').text(getValue(deptData.createAt));

    $('#modal-professorCount-detail').text(getCountString(deptData.professorCount));
    $('#modal-studentCount-detail').text(getCountString(deptData.studentCount));
    $('#modal-subjectsCount-detail').text(subjectsCount.toLocaleString());
    $('#modal-officeLocation-detail').text(
        (deptData.officeCd && deptData.placeNo) ? `${deptData.officeCd} (${deptData.placeNo})` : '-'
    );

    const statusBadge = $('#modal-status-detail');
    const deleteAtText = getValue(deptData.deleteAt);
    statusBadge.text(isDeleted ? `폐지 (폐지일: ${deleteAtText})` : '활성');
    statusBadge.removeClass('bg-success bg-danger').addClass(isDeleted ? 'bg-danger' : 'bg-success');

    const detailModal = new bootstrap.Modal(document.getElementById('univDeptDetailModal'));
    detailModal.show();

    // 수정 모달 전환 버튼
    $('#btn-open-modify-modal').off('click').on('click', function () {
        detailModal.hide();

        $('#modal-dept-name-title-modify').text(getValue(deptData.univDeptName));
        $('#modal-univDeptCd-hidden-modify').val(getValue(deptData.univDeptCd));
        $('#modal-createAt-modify').val(getValue(deptData.createAt, ''));
        $('#modal-studentCount-modify').val(getValue(deptData.studentCount, 0));
        $('#modal-univDeptName-modify').val(getValue(deptData.univDeptName, ''));
        $('#modal-subjectsCount-modify').val(subjectsCount);
        $('#modal-professorCount-modify').val(getValue(deptData.professorCount, 0));
        $('#modal-collegeName-modify').val(getValue(deptData.collegeName, ''));
        $('#modal-deptHeadName-modify').val(getValue(deptData.deptHeadName, ''));
        $('#modal-contact-modify').val(getValue(deptData.telNo, ''));
        $('#modal-status-modify').val(isDeleted ? 'DELETED' : 'ACTIVE');

        new bootstrap.Modal(document.getElementById('univDeptModifyModal')).show();
    });
}

// ===============================================
// DOM Ready
// ===============================================
$(document).ready(function () {

    // ---------------------- 데이터 유효성 검사 ----------------------
    if (!JAVASCRIPT_DATA || !JAVASCRIPT_DATA.allChartDepts || !JAVASCRIPT_DATA.staffunivDeptList) {
        console.error("🚨 JAVASCRIPT_DATA 로딩 실패!");
        $('#capacityDoughnutChart').parent().html('<div class="alert alert-danger text-center">데이터 로딩 실패!</div>');
        $('#collegeBarChart').parent().html('<div class="alert alert-danger text-center">데이터 로딩 실패!</div>');
        return;
    }

    const allDepts = JAVASCRIPT_DATA.allChartDepts;
    const chartColors = ['#4e73df', '#f6c23e', '#36b9cc', '#1cc88a', '#e74a3b', '#6a6ad4', '#858796', '#5a5c69', '#3b5998', '#1da1f2'];

    const doughnutCanvas = document.getElementById('capacityDoughnutChart');
    const barCanvas = document.getElementById('collegeBarChart');

    // ---------------------------------------------
    // 1️⃣ 좌측 차트: 학생/교수 비율 (Doughnut)
    // ---------------------------------------------
    if (doughnutCanvas) {
        const $doughnutParent = $(doughnutCanvas).parent();
        $doughnutParent.find('.no-data-msg').remove();

        const activeDepts = allDepts.filter(dept => !dept.deleteAt);
        const totalStudents = activeDepts.reduce((sum, dept) => sum + (Number(dept.studentCount) || 0), 0);
        const totalProfessors = activeDepts.reduce((sum, dept) => sum + (Number(dept.professorCount) || 0), 0);

        const total = totalStudents + totalProfessors;
        if (total > 0) {
            if (window.capacityDoughnutChartInstance) window.capacityDoughnutChartInstance.destroy();
            window.capacityDoughnutChartInstance = new Chart(doughnutCanvas, {
                type: 'doughnut',
                data: {
                    labels: ['학생 인원', '교수 인원'],
                    datasets: [{
                        data: [totalStudents, totalProfessors],
                        backgroundColor: ['#4e73df', '#36b9cc'],
                        hoverBackgroundColor: ['#4e73dfcc', '#36b9cccc']
                    }]
                },
                options: {
                    maintainAspectRatio: false,
                    responsive: true,
                    legend: {
                        display: true,
                        position: 'bottom'
                    },
                    cutoutPercentage: 80,
                }
            });
        } else {
            $(doughnutCanvas).hide();
            $doughnutParent.append('<div class="text-center text-secondary p-5">활성 학과 인원 데이터가 부족합니다.</div>');
        }
    }

    // ---------------------------------------------
    // 2️⃣ 우측 차트: 단과대별 학과 수 (Bar)
    // ---------------------------------------------
    if (barCanvas) {
        const $barParent = $(barCanvas).parent();
        $barParent.find('.no-data-msg').remove();

        const collegeCounts = allDepts.reduce((acc, dept) => {
            const name = dept.collegeName || '미분류';
            acc[name] = (acc[name] || 0) + 1;
            return acc;
        }, {});
        const labels = Object.keys(collegeCounts);
        const values = Object.values(collegeCounts);

        if (labels.length > 0) {
            if (window.collegeBarChartInstance) window.collegeBarChartInstance.destroy();
            window.collegeBarChartInstance = new Chart(barCanvas, {
                type: 'bar',
                data: {
                    labels,
                    datasets: [{
                        label: '학과 수',
                        data: values,
                        backgroundColor: chartColors[0]
                    }]
                },
                options: {
                    maintainAspectRatio: false,
                    responsive: true,
                    legend: { display: false },
                    scales: {
                        xAxes: [{ gridLines: { display: false } }],
                        yAxes: [{
                            ticks: { min: 0, stepSize: 1 }
                        }]
                    }
                }
            });
        } else {
            $(barCanvas).hide();
            $barParent.append('<div class="text-center text-secondary p-5">단과대학 정보가 부족합니다.</div>');
        }
    }

    // ---------------------------------------------
    // 3️⃣ 학과 수정 (SweetAlert 적용)
    // ---------------------------------------------
    $('#btn-save-dept').on('click', function () {
        const univDeptCd = $('#modal-univDeptCd-hidden-modify').val();
        const dept = JAVASCRIPT_DATA.staffunivDeptList.find(d => d.univDeptCd === univDeptCd);
        const wasActive = !(dept?.deleteAt);

        const updateData = {
            univDeptCd,
            univDeptName: $('#modal-univDeptName-modify').val(),
            deptHeadName: $('#modal-deptHeadName-modify').val(),
            telNo: $('#modal-contact-modify').val(),
            status: $('#modal-status-modify').val(),
        };

        if (!univDeptCd || !updateData.univDeptName) {
            Swal.fire({ icon: 'warning', text: '필수 입력 항목을 확인해주세요.', confirmButtonColor: '#1E88E5' });
            return;
        }

        const toDelete = wasActive && updateData.status === 'DELETED';
        Swal.fire({
            title: '변경 확인',
            text: toDelete ? '이 학과를 폐지하시겠습니까?' : '학과 정보를 수정하시겠습니까?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '확인',
            cancelButtonText: '취소',
            confirmButtonColor: '#1E88E5',
            cancelButtonColor: '#6c757d'
        }).then(result => {
            if (!result.isConfirmed) return;

            $.ajax({
                url: `/lms/staff/departments/api/${univDeptCd}`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(updateData),
                dataType: 'text',
                success: function () {
                    const modalEl = document.getElementById('univDeptModifyModal');
                    const modal = bootstrap.Modal.getInstance(modalEl);
                    if (modal) modal.hide();

                    setTimeout(() => {
                        Swal.fire({
                            icon: 'success',
                            title: '저장 완료',
                            text: toDelete ? '학과가 폐지되었습니다.' : '학과 정보가 수정되었습니다.',
                            confirmButtonColor: '#1E88E5'
                        }).then(() => location.reload());
                    }, 300);
                },
                error: function (xhr) {
                    let msg = xhr.responseJSON?.message || xhr.responseText || '알 수 없는 오류가 발생했습니다.';
                    Swal.fire({ icon: 'error', title: '실패', text: msg, confirmButtonColor: '#1E88E5' });
                }
            });
        });
    });

    // ---------------------------------------------
    // 4️⃣ 학과 등록 (SweetAlert 적용)
    // ---------------------------------------------
    $('#btn-create-dept').on('click', function () {
        const createData = {
            univDeptCd: $('#create-univDeptCd').val(),
            univDeptName: $('#create-univDeptName').val(),
            collegeName: $('#create-collegeName').val(),
            capacity: $('#create-capacity').val(),
            deptHeadName: $('#create-deptHeadName').val(),
            telNo: $('#create-contact').val(),
            officeLocation: $('#create-officeLocation').val(),
            homepageUrl: $('#create-homepage').val(),
            description: $('#create-description').val()
        };
        const capacityValue = Number(createData.capacity);

        if (!createData.univDeptCd || !createData.univDeptName || !createData.collegeName || isNaN(capacityValue) || capacityValue <= 0) {
            Swal.fire({ icon: 'warning', text: '필수 항목을 입력해주세요.', confirmButtonColor: '#1E88E5' });
            return;
        }

        Swal.fire({
            title: '등록 확인',
            text: `새 학과 [${createData.univDeptName}]을 등록하시겠습니까?`,
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: '등록',
            cancelButtonText: '취소',
            confirmButtonColor: '#1E88E5',
            cancelButtonColor: '#6c757d'
        }).then(result => {
            if (!result.isConfirmed) return;

            $.ajax({
                url: `/lms/staff/departments/api`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(createData),
                dataType: 'text',
                success: function () {
                    Swal.fire({
                        icon: 'success',
                        title: '등록 완료!',
                        text: '새 학과가 성공적으로 등록되었습니다.',
                        confirmButtonColor: '#1E88E5'
                    }).then(() => location.reload());
                },
                error: function (xhr) {
                    let msg = xhr.responseJSON?.message || xhr.responseText || '등록 실패';
                    Swal.fire({ icon: 'error', title: '오류', text: msg, confirmButtonColor: '#1E88E5' });
                }
            });
        });
    });
});
