<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>시설 예약</title>
    <style>
        /* Existing body and container styles, plus new styles */
        body {
            font-family: Arial, sans-serif;
            background: #f5f9f9;
            text-align: center;
            margin: 40px;
        }
        .container {
            max-width: 900px;
            margin: 0 auto;
        }
        /* New styles from user's input */
        .py-4 { padding-top: 1.5rem !important; padding-bottom: 1.5rem !important; }
        .card { border: 0; box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important; }
        .card-body { flex: 1 1 auto; padding: 1.5rem; }
        h3 { font-size: 1.75rem; margin-bottom: 1rem; margin: 0 auto; }
        .facility-categories {
            display: flex;
            flex-wrap: wrap;
            gap: 2rem; /* Increased gap */
            justify-content: center;
            margin-bottom: 2rem;
        }
        .category-card {
            background-color: #fff;
            border: 1px solid #e0e0e0;
            border-radius: .5rem;
            padding: 1.5rem;
            text-align: center;
            cursor: pointer;
            transition: all .2s ease-in-out;
            width: 150px;
            box-shadow: 0 .25rem 1rem rgba(0,0,0,.15)!important; /* Enhanced shadow */
        }
        .category-card:hover, .category-card.selected {
            border-color: #004a9e;
            box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
            transform: translateY(-.125rem);
        }
        .category-card .icon {
            font-size: 2.5rem;
            margin-bottom: .5rem;
        }
        .category-card div {
            font-weight: 600;
            color: #333;
        }

        #timetable-section {
            display: none; /* Hidden by default */
            margin-top: 2rem;
        }
        #timetable-title {
            margin-bottom: 1rem;
            color: #004a9e;
        }
        .timetable-container {
            overflow-x: auto; /* For responsive tables */
        }

        /* Modal styles */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
            display: none; /* Hidden by default */
        }
        .modal-content {
            background-color: #fff;
            padding: 2rem;
            border-radius: .5rem;
            box-shadow: 0 .5rem 1rem rgba(0,0,0,.15)!important;
            position: relative;
            width: 90%;
            max-width: 500px;
        }
        .modal-close-button {
            position: absolute;
            top: 1rem;
            right: 1rem;
            font-size: 1.5rem;
            cursor: pointer;
            color: #aaa;
        }
        .modal-close-button:hover {
            color: #333;
        }
        .form-group {
            margin-bottom: 1rem;
            text-align: left;
        }
        .form-group label {
            display: block;
            margin-bottom: .5rem;
            font-weight: 600;
        }
        .form-group input[type="text"],
        .form-group input[type="number"],
        .form-group textarea {
            width: 100%;
            padding: .5rem;
            border: 1px solid #ced4da;
            border-radius: .25rem;
        }
        .form-actions {
            text-align: right;
            margin-top: 1.5rem;
        }
        .btn-submit {
            background-color: #004a9e;
            color: #fff;
            padding: .75rem 1.5rem;
            border: none;
            border-radius: .25rem;
            cursor: pointer;
            font-size: 1rem;
        }
        .btn-submit:hover {
            background-color: #003a7e;
        }

        main {
            max-width: 900px; /* Same as container */
            margin: 0 auto;
        }

        /* New timetable CSS from user's input */
        :root {
            --primary-color: #004a9e;
            --secondary-color: #f0f4f8;
            --font-color: #333;
            --border-color: #e0e0e0;
            --available-color: #d4edda;
            --reserved-color: #f8d7da;
        }

        .timetable {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            table-layout: fixed;
        }

        .timetable th,
        .timetable td {
            border: 1px solid var(--border-color);
            padding: 10px;
            text-align: center;
            font-size: 0.85em;
        }

        .timetable th {
            background-color: var(--secondary-color);
        }

        .timeslot {
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .timeslot.available {
            background-color: var(--available-color);
        }

        .timeslot.available:hover {
            background-color: #b8e0c0;
        }

        .timeslot.reserved {
            background-color: var(--reserved-color);
            color: #721c24;
            cursor: not-allowed;
            text-decoration: line-through;
        }
    </style>
</head>
<body>
<%@ include file="/WEB-INF/views/user/common/userNotificationScript.jspf" %>
    <main>
        <div class="py-4">
            <div class="card border-0 shadow">
                <div class="card-body">
                    <h3>1. 시설 분류 선택</h3>
                    <div class="facility-categories">
                        <div class="category-card" data-facility="스터디룸">
                            <div class="icon">📚</div>
                            <div>스터디룸</div>
                        </div>
                        <div class="category-card" data-facility="세미나실">
                            <div class="icon">🖥️</div>
                            <div>세미나실</div>
                        </div>
                        <div class="category-card" data-facility="체육시설">
                            <div class="icon">🏀</div>
                            <div>체육시설</div>
                        </div>
                        <div class="category-card" data-facility="개인연습실">
                            <div class="icon">🎹</div>
                            <div>개인연습실</div>
                        </div>
                    </div>
    
                    <div id="timetable-section">
                        <h3 id="timetable-title">2. 시간대 선택</h3>
                        <div class="timetable-container"></div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <div id="reservation-modal" class="modal-overlay">
        <div class="modal-content">
            <span class="modal-close-button">&times;</span>
            <h3>3. 예약 신청</h3>
            <form id="reservation-form">
                <input type="hidden" id="selected-facility" name="facility">
                <input type="hidden" id="selected-slot" name="slot">
                <div class="form-group">
                    <label>선택 시설</label>
                    <input type="text" id="display-facility" disabled>
                </div>
                <div class="form-group">
                    <label>선택 시간</label>
                    <input type="text" id="display-slot" disabled>
                </div>
                <div class="form-group">
                    <label for="purpose">사용 목적</label>
                    <textarea id="purpose" name="purpose" rows="4" required></textarea>
                </div>
                <div class="form-group">
                    <label for="headcount">사용 인원</label>
                    <input type="number" id="headcount" name="headcount" min="1" required>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-submit">신청하기</button>
                </div>
            </form>
        </div>
    </div>

    <script src="vendor/@popperjs/core/dist/umd/popper.min.js"></script>
    <script src="vendor/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="js/volt.js"></script>
    <script>
    document.addEventListener('DOMContentLoaded', function () {
        const timetableSection = document.getElementById('timetable-section');
        const timetableContainer = timetableSection.querySelector('.timetable-container');
        const timetableTitle = document.getElementById('timetable-title');
        const categories = document.querySelectorAll('.category-card');
        const modal = document.getElementById('reservation-modal');
        const closeModalBtn = modal.querySelector('.modal-close-button');
        const form = document.getElementById('reservation-form');

        const reservedSlots = {
            '스터디룸': ['2025-09-16-10', '2025-09-17-14'],
            '세미나실': ['2025-09-18-11'],
            '체육시설': ['2025-09-16-18', '2025-09-19-17'],
            '개인연습실': []
        };

        categories.forEach(card => {
            card.addEventListener('click', () => {
                const selectedFacility = card.dataset.facility;
                categories.forEach(c => c.classList.remove('selected'));
                card.classList.add('selected');
                
                timetableTitle.textContent = `2. ${selectedFacility} 시간대 선택`;
                generateTimetable(selectedFacility);
                timetableSection.style.display = 'block';
                timetableSection.scrollIntoView({ behavior: 'smooth' });
            });
        });

        function generateTimetable(facility) {
            let html = '<table class="timetable"><thead><tr><th>시간</th>';
            const today = new Date('2025-09-16');
            const days = [];
            for(let i=0; i<5; i++) {
                const date = new Date(today);
                date.setDate(today.getDate() + i);
                const year = date.getFullYear();
                const month = (date.getMonth() + 1).toString().padStart(2, '0');
                const day = date.getDate().toString().padStart(2, '0');
                const dateString = `${year}-${month}-${day}`;
                days.push(dateString);
                html += `<th>${dateString.slice(5)}</th>`;
            }
            html += '</tr></thead><tbody>';

            for (let hour = 9; hour <= 18; hour++) {
                html += `<tr><td>${hour}:00 - ${hour+1}:00</td>`;
                days.forEach(day => {
                    const slotId = `${day}-${hour}`;r
                    const isReserved = reservedSlots[facility].includes(slotId);
                    const status = isReserved ? 'reserved' : 'available';
                    html += `<td class="timeslot ${status}" data-facility="${facility}" data-slot="${slotId}">${isReserved ? '예약중' : '사용가능'}</td>`;
                });
                html += '</tr>';
            }
            html += '</tbody></table>';
            timetableContainer.innerHTML = html;
        }

        timetableContainer.addEventListener('click', e => {
            if (e.target.classList.contains('available')) {
                const facility = e.target.dataset.facility;
                const slot = e.target.dataset.slot;
                openModal(facility, slot);
            }
        });

        function openModal(facility, slot) {
            form.reset();
            document.getElementById('selected-facility').value = facility;
            document.getElementById('selected-slot').value = slot;
            document.getElementById('display-facility').value = facility;
            const [year, month, day, hour] = slot.split('-');
            document.getElementById('display-slot').value = `${year}-${month}-${day} ${hour}:00`;
            modal.style.display = 'flex';
        }

        closeModalBtn.addEventListener('click', () => { modal.style.display = 'none'; });
        modal.addEventListener('click', e => { if (e.target === modal) modal.style.display = 'none'; });

        form.addEventListener('submit', e => {
            e.preventDefault();
            alert('시설 예약이 신청되었습니다.');
            modal.style.display = 'none';
        });
    });
    </script>
</body>
</html>