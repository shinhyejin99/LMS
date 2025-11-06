(() => {
	"use strict";

	const $root = document.getElementById("exam-detail-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId;
	const examId = $root.dataset.examId;
	const apiBase = $root.dataset.apiBase;

	// --- DOM Hooks ---
	const $examDetailTitle = document.getElementById("exam-detail-title");
	const $examInfoBody = document.getElementById("exam-info-body");
	const $studentResultsBody = document.getElementById("student-results-body");
	const $alertBox = document.getElementById("alert-box");

	// --- Helper Functions ---
	const showAlert = (message) => {
		if (!$alertBox) return;
		$alertBox.textContent = message;
		$alertBox.classList.remove("d-none");
	};

	const clearAlert = () => {
		if (!$alertBox) return;
		$alertBox.textContent = "";
		$alertBox.classList.add("d-none");
	};

	const fetchJSON = async (url) => {
		const res = await fetch(url, { headers: { Accept: "application/json" } });
		if (!res.ok) {
			const text = await res.text().catch(() => "");
			throw new Error(`HTTP ${res.status} - ${text}`);
		}
		return res.json();
	};

	const escapeHtml = (str) =>
		String(str ?? "").replace(/[&<>"']/g, (match) => {
			return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[match];
		});

	const pad = (num) => String(num).padStart(2, "0");

	const formatDateTime = (isoStr) => {
		if (!isoStr) return "-";
		const d = new Date(isoStr);
		if (Number.isNaN(d.getTime())) return "-";
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};

	const formatPeriod = (start, end) => {
		const startLabel = formatDateTime(start);
		const endLabel = formatDateTime(end);
		if (startLabel === "-" && endLabel === "-") return "-";
		if (endLabel === "-") return startLabel;
		return `${startLabel} ~ ${endLabel}`;
	};

	const buildFullName = (student) => {
		const lastName = student?.lastName ?? "";
		const firstName = student?.firstName ?? "";
		const fullName = `${lastName} ${firstName}`.trim();
		return fullName || student?.userId || "";
	};

	// --- Render Functions ---
	const renderExamDetails = (exam) => {
		if (!$examInfoBody) return;

		$examDetailTitle.textContent = exam.examName || "시험 상세";

		const examTypeLabel = exam.examType === "OFF" ? "오프라인" : exam.examType === "ON" ? "온라인" : "알 수 없음";

		$examInfoBody.innerHTML = `
			<dl class="row mb-0">
				<dt class="col-sm-3">시험명</dt>
				<dd class="col-sm-9">${escapeHtml(exam.examName || "-")}</dd>

				<dt class="col-sm-3">유형</dt>
				<dd class="col-sm-9">${examTypeLabel}</dd>

				<dt class="col-sm-3">설명</dt>
				<dd class="col-sm-9">${escapeHtml(exam.examDesc || "-")}</dd>

				<dt class="col-sm-3">시험 기간</dt>
				<dd class="col-sm-9">${formatPeriod(exam.startAt, exam.endAt)}</dd>

				<dt class="col-sm-3">가중치</dt>
				<dd class="col-sm-9">${exam.weightValue ?? "-"}</dd>

				<dt class="col-sm-3">등록일</dt>
				<dd class="col-sm-9">${formatDateTime(exam.createAt)}</dd>
			</dl>
		`;
	};

	const renderStudentResults = (exam, students) => {
		if (!$studentResultsBody) return;

		const studentMap = new Map(students.map(s => [s.enrollId, s]));
		const submitList = exam.submitList || [];

		if (submitList.length === 0) {
			$studentResultsBody.innerHTML = `<div class="text-center text-muted py-4">제출된 시험 결과가 없습니다.</div>`;
			return;
		}

		const rows = submitList.map(submitEntry => {
			const student = studentMap.get(submitEntry.enrollId);
			const fullName = student ? buildFullName(student) : "(알 수 없음)";
			const studentNo = student ? student.studentNo : "-";
			const score = submitEntry.submit?.modifiedScore ?? submitEntry.submit?.autoScore ?? "-";
			const submitAt = submitEntry.submit?.submitAt ? formatDateTime(submitEntry.submit.submitAt) : "미제출";

			return `
				<tr>
					<td>${escapeHtml(studentNo)}</td>
					<td>${escapeHtml(fullName)}</td>
					<td class="text-center">${score}</td>
					<td class="text-center">${submitAt}</td>
					<td>
						<button type="button" class="btn btn-sm btn-outline-primary" data-enroll-id="${submitEntry.enrollId}">점수 수정</button>
					</td>
				</tr>
			`;
		}).join("");

		$studentResultsBody.innerHTML = `
			<div class="table-responsive">
				<table class="table table-hover table-sm align-middle mb-0">
					<thead class="table-light">
						<tr>
							<th scope="col" style="width: 100px;">학번</th>
							<th scope="col">이름</th>
							<th scope="col" class="text-center" style="width: 80px;">점수</th>
							<th scope="col" class="text-center" style="width: 160px;">제출일시</th>
							<th scope="col" class="text-center" style="width: 100px;">관리</th>
						</tr>
					</thead>
					<tbody>
						${rows}
					</tbody>
				</table>
			</div>
		`;
	};

	// --- Data Fetching ---
	const fetchExamDetails = async () => {
		const url = `${apiBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(examId)}`;
		return fetchJSON(url);
	};

	const fetchStudentsForLecture = async () => {
		const url = `${ctx}/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/students`;
		return fetchJSON(url);
	};

	// --- Initialization ---
	const init = async () => {
		if (!lectureId || !examId) {
			showAlert("강의 ID 또는 시험 ID가 누락되었습니다.");
			return;
		}
		clearAlert();

		try {
			const [exam, students] = await Promise.all([
				fetchExamDetails(),
				fetchStudentsForLecture()
			]);

			renderExamDetails(exam);
			renderStudentResults(exam, students);
		} catch (error) {
			console.error("[prfExamDetail] Error loading data:", error);
			showAlert(`데이터를 불러오는 데 실패했습니다: ${error.message}`);
			$examInfoBody.innerHTML = `<div class="text-center text-danger py-4">시험 정보를 불러오지 못했습니다.</div>`;
			$studentResultsBody.innerHTML = `<div class="text-center text-danger py-4">학생 제출 결과를 불러오지 못했습니다.</div>`;
		}
	};

	init();
})();
