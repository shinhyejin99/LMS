// /js/app/classroom/professor/prfStudentManageDetail.js
(() => {
	"use strict";

	const $root = document.querySelector("#student-detail-root");
	if (!$root) {
		console.warn("[prfStudentManageDetail] #student-detail-root not found. Abort.");
		return;
	}

	const lectureId = $root.dataset.lectureId;
	const studentNo = $root.dataset.studentNo;
	const examApi = $root.dataset.examApi;
	const attendanceApi = $root.dataset.attendanceApi;

	const $notice = document.getElementById("student-detail-notice");

	const $examTable = document.getElementById("student-exam-tbody");
	const $examCountSummary = document.getElementById("student-exam-count");
	const $examWeightSummary = document.getElementById("student-exam-weight-summary");
	const $examFinalScore = document.getElementById("student-exam-final-score");

	const $attendanceContainer = document.getElementById("student-attendance-pivot");
	const $attendanceCountSummary = document.getElementById("student-attendance-count");

	const state = {
		exams: [],
		attendance: [],
	};

	const showNotice = (message, variant = "alert-danger") => {
		if (!$notice) return;
		$notice.textContent = message;
		$notice.className = `alert ${variant}`;
	};

	const clearNotice = () => {
		if (!$notice) return;
		$notice.textContent = "";
		$notice.className = "alert d-none";
	};

	const setTableMessage = (tbody, message, tone = "muted") => {
		if (!tbody) return;
		tbody.textContent = "";
		const tr = document.createElement("tr");
		const td = document.createElement("td");
		const colSpan = Number(tbody.dataset.colspan) || 1;
		td.colSpan = colSpan;
		td.className = `text-center py-5 text-${tone}`;
		td.textContent = message;
		tr.appendChild(td);
		tbody.appendChild(tr);
	};

	const pad = (num) => String(num).padStart(2, "0");

	const formatDateTime = (iso, includeSeconds = false) => {
		if (!iso) return "-";
		const date = new Date(iso);
		if (Number.isNaN(date.getTime())) return "-";
		const datePart = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
		const timePart = `${pad(date.getHours())}:${pad(date.getMinutes())}${includeSeconds ? `:${pad(date.getSeconds())}` : ""}`;
		return `${datePart} ${timePart}`;
	};

	const formatDate = (iso) => {
		if (!iso) return "-";
		const date = new Date(iso);
		if (Number.isNaN(date.getTime())) return "-";
		return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
	};

	const formatExamType = (type) => {
		switch ((type || "").toUpperCase()) {
			case "OFF":
				return "오프라인";
			case "ONL":
			case "ON":
				return "온라인";
			default:
				return "기타";
		}
	};

	const toNumber = (value) => {
		if (value === null || value === undefined || value === "") return null;
		const num = Number(value);
		return Number.isFinite(num) ? num : null;
	};

	const escapeHtml = (value) => {
		if (value == null) return "";
		return String(value)
			.replace(/&/g, "&amp;")
			.replace(/</g, "&lt;")
			.replace(/>/g, "&gt;")
			.replace(/"/g, "&quot;")
			.replace(/'/g, "&#39;");
	};

	const formatBooleanBadge = (flag, truthyLabel = "있음", falsyLabel = "없음") => {
		const span = document.createElement("span");
		if (flag) {
			span.className = "badge bg-success-subtle text-success-emphasis";
			span.textContent = truthyLabel;
		} else {
			span.className = "badge bg-secondary-subtle text-secondary-emphasis";
			span.textContent = falsyLabel;
		}
		return span;
	};

	const resolveFinalScore = (record) => {
		const modified = toNumber(record.modifiedScore);
		if (modified != null) return { score: modified, type: "modified" };
		const auto = toNumber(record.autoScore);
		if (auto != null) return { score: auto, type: "auto" };
		return { score: null, type: null };
	};

	const buildExamRow = (record) => {
		const tr = document.createElement("tr");

		const nameTd = document.createElement("td");
		const startLabel = record.startAt ? formatDateTime(record.startAt, false) : "";
		const endLabel = record.endAt ? formatDateTime(record.endAt, false) : "";
		const scheduleLabel = [startLabel, endLabel ? `~ ${endLabel}` : ""].filter(Boolean).join(" ");
		const typeLabel = formatExamType(record.examType);
		const metaPieces = [typeLabel];
		if (scheduleLabel) metaPieces.push(scheduleLabel);
		nameTd.innerHTML = `<div class="fw-semibold">${record.examName || "-"}</div>
			<div class="text-muted small">${metaPieces.join(" · ")}</div>`;

		const weightTd = document.createElement("td");
		weightTd.className = "text-center";
		weightTd.textContent = record.weightValue != null ? `${record.weightValue}` : "-";

		const recordTd = document.createElement("td");
		recordTd.className = "text-center";
		recordTd.appendChild(formatBooleanBadge(record.record, "응시", "미응시"));

		const submitTd = document.createElement("td");
		submitTd.className = "text-center";
		submitTd.textContent = record.submitAt ? formatDateTime(record.submitAt, true) : "-";

		const final = resolveFinalScore(record);
		const scoreTd = document.createElement("td");
		scoreTd.className = "text-center";
		if (final.score != null) {
			const suffix = final.type === "modified" ? " (수정)" : "";
			scoreTd.textContent = `${final.score}${suffix}`;
		} else {
			scoreTd.textContent = "-";
		}

		const noteTd = document.createElement("td");
		noteTd.className = "text-muted small";
		noteTd.textContent = record.modifyReason || "";

		tr.appendChild(nameTd);
		tr.appendChild(weightTd);
		tr.appendChild(recordTd);
		tr.appendChild(submitTd);
		tr.appendChild(scoreTd);
		tr.appendChild(noteTd);

		return tr;
	};

	const renderExamSummary = () => {
		const count = state.exams.length;
		if ($examCountSummary) $examCountSummary.textContent = String(count);

		if ($examWeightSummary) {
			if (!count) {
				$examWeightSummary.textContent = "시험 정보가 없습니다.";
			} else {
				let weightSum = 0;
				state.exams.forEach((record) => {
					const weight = toNumber(record.weightValue) ?? 0;
					weightSum += weight;
				});
				$examWeightSummary.textContent = `가중치 합계: ${weightSum}점`;
			}
		}

		if (!$examFinalScore) return;

		if (!count) {
			$examFinalScore.className = "alert alert-warning d-flex align-items-center justify-content-between";
			$examFinalScore.textContent = "시험 정보가 없어 최종 점수를 계산할 수 없습니다.";
			return;
		}

		let weightSum = 0;
		let weightedScore = 0;

		state.exams.forEach((record) => {
			const weight = toNumber(record.weightValue) ?? 0;
			weightSum += weight;

			const final = resolveFinalScore(record);
			const score = final.score != null && record.record ? final.score : 0;
			weightedScore += score * (weight / 100);
		});

		if (Math.abs(weightSum - 100) < 1e-6) {
			const finalScore = Number.isFinite(weightedScore) ? weightedScore : 0;
			const displayScore = finalScore.toFixed(2);
			$examFinalScore.className = "alert alert-primary d-flex align-items-center justify-content-between";
			$examFinalScore.textContent = `가중치 합계 100점 · 최종 시험 점수 ${displayScore}점`;
		} else {
			$examFinalScore.className = "alert alert-warning d-flex align-items-center justify-content-between";
			$examFinalScore.textContent = `현재 가중치 합계가 ${weightSum}점이므로 최종 점수를 계산할 수 없습니다.`;
		}
	};

	const renderExamTable = () => {
		if (!$examTable) return;

		if (!state.exams.length) {
			setTableMessage($examTable, "응시 기록이 없습니다.");
			renderExamSummary();
			return;
		}

		const fragment = document.createDocumentFragment();
		state.exams.forEach((record) => fragment.appendChild(buildExamRow(record)));
		$examTable.textContent = "";
		$examTable.appendChild(fragment);
		renderExamSummary();
	};

	const renderAttendanceTable = () => {
		const count = state.attendance.length;
		if ($attendanceCountSummary) $attendanceCountSummary.textContent = String(count);

		if (!$attendanceContainer) return;

		if (!count) {
			$attendanceContainer.innerHTML = `<div class="text-center text-muted py-5">출결 기록이 없습니다.</div>`;
			return;
		}

		const table = document.createElement("table");
		table.className = "table table-sm table-bordered align-middle mb-0";

		const thead = document.createElement("thead");
		const headRow = document.createElement("tr");

		const headLabel = document.createElement("th");
		headLabel.scope = "col";
		headLabel.className = "bg-light text-center";
		headLabel.textContent = "";
		headRow.appendChild(headLabel);

		state.attendance.forEach((record) => {
			const th = document.createElement("th");
			th.scope = "col";
			th.className = "text-center";
			const roundNumber = record.lctPrintRound != null ? `${record.lctPrintRound}회` : "-";
			const methodLabel = record.qrcodeFileId ? "QR" : "일반";
			th.innerHTML = `<div class="fw-semibold">${roundNumber}</div><div class="text-muted small">${methodLabel}</div>`;
			headRow.appendChild(th);
		});

		thead.appendChild(headRow);

		const tbody = document.createElement("tbody");

		const rows = [
			{
				label: "출결일",
				render: (record) => formatDate(record.attDay),
			},
			{
				label: "상태",
				render: (record) => {
					const badge = document.createElement("span");
					badge.className = "badge";
					badge.textContent = record.attStatusName || "미기록";
					switch (record.attStatusCd) {
						case "ATTD_OK":
							badge.classList.add("bg-success-subtle", "text-success-emphasis");
							break;
						case "ATTD_LATE":
							badge.classList.add("bg-warning-subtle", "text-warning-emphasis");
							break;
						case "ATTD_ABSENT":
							badge.classList.add("bg-danger-subtle", "text-danger-emphasis");
							break;
						default:
							badge.classList.add("bg-secondary-subtle", "text-secondary-emphasis");
					}
					return badge.outerHTML;
				},
			},
			{
				label: "기록 시각",
				render: (record) => (record.attAt ? formatDateTime(record.attAt, true) : "-"),
			},
			{
				label: "비고",
				render: (record) => escapeHtml(record.attComment || ""),
			},
		];

		rows.forEach((row) => {
			const tr = document.createElement("tr");

			const labelTh = document.createElement("th");
			labelTh.scope = "row";
			labelTh.className = "bg-light text-center";
			labelTh.textContent = row.label;
			tr.appendChild(labelTh);

			state.attendance.forEach((record) => {
				const td = document.createElement("td");
				td.className = "text-center";
				const rendered = row.render(record);
				if (typeof rendered === "string") {
					td.innerHTML = rendered || "-";
				} else if (rendered instanceof HTMLElement) {
					td.appendChild(rendered);
				} else {
					td.textContent = "-";
				}
				tr.appendChild(td);
			});

			tbody.appendChild(tr);
		});

		table.appendChild(thead);
		table.appendChild(tbody);

		$attendanceContainer.textContent = "";
		$attendanceContainer.appendChild(table);
	};

	const loadExams = async () => {
		if (!examApi) {
			state.exams = [];
			renderExamTable();
			return;
		}

		if ($examCountSummary) $examCountSummary.textContent = "0";
		if ($examWeightSummary) $examWeightSummary.textContent = "가중치를 계산 중입니다...";
		if ($examFinalScore) {
			$examFinalScore.className = "alert alert-primary d-flex align-items-center justify-content-between";
			$examFinalScore.textContent = "최종 시험 점수를 계산하고 있습니다...";
		}
		setTableMessage($examTable, "시험 정보를 불러오는 중입니다...", "muted");
		try {
			const response = await fetch(examApi, {
				headers: { Accept: "application/json" },
				credentials: "same-origin",
			});
			if (!response.ok) throw new Error(`HTTP ${response.status}`);

			const payload = await response.json();
			if (!Array.isArray(payload)) throw new Error("Unexpected payload");

			state.exams = [...payload].sort((a, b) => {
				const aTime = new Date(a.startAt || 0).getTime();
				const bTime = new Date(b.startAt || 0).getTime();
				return bTime - aTime;
			});

			renderExamTable();
		} catch (error) {
			console.error("[prfStudentManageDetail] Failed to load exam data:", error);
			state.exams = [];
			setTableMessage($examTable, "시험 정보를 불러오지 못했습니다.", "danger");
			renderExamSummary();
			showNotice("시험 정보를 불러오는 중 오류가 발생했습니다.");
		}
	};

	const loadAttendance = async () => {
		if (!attendanceApi) {
			state.attendance = [];
			renderAttendanceTable();
			return;
		}

		if ($attendanceCountSummary) $attendanceCountSummary.textContent = "0";
		if ($attendanceContainer) {
			$attendanceContainer.innerHTML = `<div class="text-center text-muted py-5">출결 정보를 불러오는 중입니다...</div>`;
		}
		try {
			const response = await fetch(attendanceApi, {
				headers: { Accept: "application/json" },
				credentials: "same-origin",
			});
			if (!response.ok) throw new Error(`HTTP ${response.status}`);

			const payload = await response.json();
			if (!Array.isArray(payload)) throw new Error("Unexpected payload");

			state.attendance = [...payload].sort((a, b) => {
				const aRound = a.lctRound ?? a.lctPrintRound ?? 0;
				const bRound = b.lctRound ?? b.lctPrintRound ?? 0;
				return aRound - bRound;
			});

			renderAttendanceTable();
		} catch (error) {
			console.error("[prfStudentManageDetail] Failed to load attendance data:", error);
			state.attendance = [];
			if ($attendanceContainer) {
				$attendanceContainer.innerHTML = `<div class="text-center text-danger py-5">출결 정보를 불러오지 못했습니다.</div>`;
			}
			if ($attendanceCountSummary) $attendanceCountSummary.textContent = "0";
			showNotice("출결 정보를 불러오는 중 오류가 발생했습니다.");
		}
	};

	(async function init() {
		if (!lectureId || !studentNo) {
			showNotice("필수 정보가 누락되었습니다. 페이지를 새로고침 후 다시 시도해 주세요.");
			return;
		}

		clearNotice();
		await Promise.all([loadExams(), loadAttendance()]);

		if (!state.exams.length && !state.attendance.length) {
			showNotice("학생 응시 및 출결 기록이 없습니다.", "alert-info");
		}
	})();
})();
