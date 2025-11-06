/**
 * <pre>
 * << 개정이력(Modification Information) >>
 *   수정일        수정자   수정내용
 *  2025.10.01.   송태호   최초 생성
 *  2025.10.01.   ChatGPT  Bootstrap 테마/아코디언/열 제거 반영
 * </pre>
 */
(function() {
	const root = document.getElementById('lecture-root');
	if (!root) return;

	const ctx = root.dataset.ctx || '';
	const lectureId = (root.dataset.lectureId || '').trim();
	const $ = (sel) => root.querySelector(sel);

	const dayMap = { MO: '월', TU: '화', WE: '수', TH: '목', FR: '금', SA: '토', SU: '일' };
	let planData = []; // [{lectureWeek, weekGoal, weekDesc}, ...]

	function showAlert(msg) {
		const box = $('#notfound-box');
		if (!box) return;
		box.classList.remove('d-none');
		box.textContent = msg || '오류가 발생했습니다.';
	}

	function setLoading() {
		const setCardBody = (sel, text) => {
			const el = $(sel);
			if (!el) return;
			const body = el.classList.contains('card') ? el.querySelector('.card-body') : el;
			if (body) body.textContent = text;
		};
		setCardBody('#schedule-box', '로딩 중…');
		setCardBody('#trainee-box', '로딩 중…');
		const sel = $('#plan-select');
		if (sel) sel.innerHTML = '<option>로딩 중…</option>';
		const det = $('#plan-detail .card-body');
		if (det) det.textContent = '로딩 중…';
		const empty = $('#plan-empty');
		if (empty) empty.classList.add('d-none');
	}

	async function fetchJSON(url) {
		const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
		if (res.status === 404) return { __notFound: true };
		if (res.status === 204) return [];
		if (!res.ok) {
			const t = await res.text().catch(() => '');
			throw new Error(`HTTP ${res.status} ${res.statusText} - ${t}`);
		}
		return res.json();
	}

	// ====== 렌더러 ======

	// 시간표: 강의실 "코드" 열 제거
	function renderSchedule(data = []) {
		const box = $('#schedule-box');
		if (!box) return;
		const body = box.querySelector('.card-body') || box;

		if (!Array.isArray(data) || data.length === 0) {
			body.innerHTML = '<div class="text-body-secondary">등록된 강의 시간표가 없습니다.</div>';
			return;
		}

		const rows = [];
		data.forEach(({ placeName, slots }) => {
			(slots || []).forEach(({ day, start, end }) => {
				rows.push(`
          <tr>
            <td class="text-center">${dayMap[day] || day}</td>
            <td class="text-center">${fmtTime(start)}–${fmtTime(end)}</td>
            <td>${escapeHtml(placeName || '')}</td>
          </tr>
        `);
			});
		});

		body.innerHTML = `
      <div class="table-responsive">
        <table class="table table-sm table-striped align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th class="text-center" style="width:6rem;">요일</th>
              <th class="text-center" style="width:10rem;">시간</th>
              <th>강의실</th>
            </tr>
          </thead>
          <tbody>${rows.join('')}</tbody>
        </table>
      </div>
    `;
	}

	function populatePlanSelect() {
		const sel = $('#plan-select');
		const detBody = $('#plan-detail .card-body');
		const empty = $('#plan-empty');
		if (!sel || !detBody) return;

		sel.innerHTML = '';
		detBody.innerHTML = '';

		if (!Array.isArray(planData) || planData.length === 0) {
			empty && empty.classList.remove('d-none');
			sel.innerHTML = '<option value="">주차 없음</option>';
			detBody.innerHTML = '';
			return;
		}

		planData.sort((a, b) => (a.lectureWeek ?? 0) - (b.lectureWeek ?? 0));

		sel.innerHTML = planData
			.map(p => `<option value="${p.lectureWeek}">${p.lectureWeek}주차</option>`)
			.join('');

		sel.value = String(planData[0].lectureWeek);
		renderPlanDetail(planData[0].lectureWeek);

		sel.addEventListener('change', (e) => {
			const w = Number(e.target.value);
			renderPlanDetail(w);
		});
	}

	function renderPlanDetail(weekNum) {
		const detBody = $('#plan-detail .card-body');
		const empty = $('#plan-empty');
		if (!detBody) return;

		if (!Array.isArray(planData) || planData.length === 0) {
			detBody.innerHTML = '';
			empty && empty.classList.remove('d-none');
			return;
		}

		const item = planData.find(p => Number(p.lectureWeek) === Number(weekNum));
		if (!item) {
			detBody.innerHTML = '<div class="text-danger">선택한 주차 정보를 찾을 수 없습니다.</div>';
			return;
		}

		empty && empty.classList.add('d-none');
		detBody.innerHTML = `
      <div class="vstack gap-2">
        <div class="fw-semibold">${item.lectureWeek}주차</div>
        <div><span class="fw-semibold">목표</span><br/>${escapeHtml(item.weekGoal || '')}</div>
        <div><span class="fw-semibold">내용</span><br/>${escapeHtml(item.weekDesc || '')}</div>
      </div>
    `;
	}

	function renderTrainees(data = []) {
		const modalBody = root.querySelector('#trainee-modal-body');
		const openBtn = root.querySelector('#open-trainee-modal-btn');

		if (!modalBody) return;

		if (!Array.isArray(data) || data.length === 0) {
			modalBody.innerHTML = '<div class="text-body-secondary">수강생이 없습니다.</div>';
			return;
		}

		// 버튼 라벨에 총원 표시
		if (openBtn) openBtn.textContent = `수강생 보기 (${data.length}명)`;

		// 컬럼 구성: 학번 | 이름(+학년 배지) | 학년 | 학과 | 연락처 | 이메일
		// - 재학 상태(stuStatusName) 컬럼 제거
		// - 학년을 이름 옆 배지로도 강조
		const rowsHtml = data.map(t => {
			const fullName = `${t.lastName || ''}${t.firstName || ''}`;
			const grade = t.gradeName || ''; // 예: "1학년"
			const badge = grade ? ` <span class="badge text-bg-secondary">${escapeHtml(grade)}</span>` : '';
			return `
      <tr>
        <td>${escapeHtml(t.studentNo || '')}</td>
        <td>${escapeHtml(fullName)}${badge}</td>
        <td>${escapeHtml(grade)}</td>
        <td>${escapeHtml(t.univDeptName || '')}</td>
        <td>${escapeHtml(t.mobileNo || '')}</td>
        <td>${escapeHtml(t.email || '')}</td>
      </tr>
    `;
		}).join('');

		modalBody.innerHTML = `
    <div class="table-responsive">
      <table class="table table-sm table-striped align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th>학번</th>
            <th>이름</th>
            <th>학년</th>
            <th>학과</th>
            <th>연락처</th>
            <th>이메일</th>
          </tr>
        </thead>
        <tbody>${rowsHtml}</tbody>
      </table>
    </div>
  `;
	}

	// ====== 유틸 ======
	function fmtTime(hhmm) {
		if (!hhmm || hhmm.length !== 4) return hhmm || '';
		return `${hhmm.slice(0, 2)}:${hhmm.slice(2)}`;
	}
	function escapeHtml(s) {
		return String(s ?? '')
			.replace(/&/g, '&amp;').replace(/</g, '&lt;')
			.replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
	}

	// ====== 초기화 ======
	async function init() {
		if (!lectureId) {
			showAlert('lectureId 파라미터가 없습니다.');
			return;
		}
		setLoading();

		const encodedLectureId = encodeURIComponent(lectureId);
		const commonBase = `${ctx}/classroom/api/v1/common/${encodedLectureId}`;
		const professorStudentsUrl = `${ctx}/classroom/api/v1/professor/${encodedLectureId}/students`;

		try {
			const [schedule, plan, trainees] = await Promise.all([
				fetchJSON(`${commonBase}/schedule`),
				fetchJSON(`${commonBase}/plan`),
				fetchJSON(professorStudentsUrl)
			]);

			if (schedule?.__notFound && plan?.__notFound && trainees?.__notFound) {
				['#schedule-box', '#trainee-box'].forEach(sel => {
					const el = $(sel);
					if (el) {
						const body = el.querySelector('.card-body') || el;
						body.innerHTML = '';
					}
				});
				const det = $('#plan-detail .card-body'); if (det) det.innerHTML = '';
				showAlert('잘못된 강의번호입니다. 강의가 존재하지 않습니다.');
				return;
			}

			// 시간표
			schedule?.__notFound
				? ($('#schedule-box .card-body').innerHTML = '<div class="text-danger">시간표 정보를 찾을 수 없습니다.</div>')
				: renderSchedule(schedule);

			// 주차 계획
			planData = plan?.__notFound ? [] : (Array.isArray(plan) ? plan : []);
			populatePlanSelect();

			// 수강생
			if (trainees?.__notFound) {
				renderTrainees([]); // 모달에는 "수강생이 없습니다." 노출
			} else {
				renderTrainees(trainees);
			}

		} catch (e) {
			console.error(e);
			showAlert('데이터를 불러오는 중 오류가 발생했습니다.');
		}
	}

	document.addEventListener('DOMContentLoaded', init);
})();
