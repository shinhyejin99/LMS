
document.addEventListener('DOMContentLoaded', function() {
    loadLectureWithdrawalList();
});

function loadLectureWithdrawalList() {
    const url = '/lms/student/rest/lecture/withdrawal/list';

    fetch(url, { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            renderLectureWithdrawalTable(data);
        })
        .catch(error => {
            console.error('수강 철회 목록 조회 실패:', error);
            document.getElementById('emptyMessage').style.display = 'block';
        });
}

function renderLectureWithdrawalTable(dataList) {
    console.log("Received data for lecture withdrawal table:", dataList);
    const tbody = document.getElementById('statusTableBody');
    const emptyMessage = document.getElementById('emptyMessage');

    tbody.innerHTML = ''; // Always clear the table body
    emptyMessage.style.display = 'block'; // Always show the empty message

    // Data rendering logic removed as per user's request.
}
