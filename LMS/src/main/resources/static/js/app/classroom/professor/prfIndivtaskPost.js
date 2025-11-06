// /js/app/classroom/professor/prfIndivtaskPost.js
(function() {
	"use strict";

	const $root = document.querySelector("#indivtask-post-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const profBaseRaw = $root.dataset.profBase || "";
	const pivot = profBaseRaw.lastIndexOf("/task");
	const profApiBase = pivot >= 0 ? profBaseRaw.substring(0, pivot) : profBaseRaw;
	const taskApiBase = profBaseRaw;

	const $taskBody = document.getElementById("task-body");
	const $error = document.getElementById("error-box");
	const $btnList = document.getElementById("btn-list");
	const $btnEdit = document.getElementById("btn-edit-task");
	const $btnDelete = document.getElementById("btn-delete-task");
	const $submissionRate = document.getElementById("submission-rate");
	const $submissionAccordionContainer = document.getElementById("submission-accordion-container");
	const $submittedList = document.getElementById("submitted-list");
	const $pendingList = document.getElementById("pending-list");

	const CURRENT = {
		lectureId: "",
		indivtaskId: "",
		taskListUrl: "",
	};

	const showError = (msg) => {
		if (!$error) return;
		$error.classList.remove("d-none");
		$error.textContent = msg;
	};

	const fmtDateTime = (iso) => {
		if (!iso) return "-";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return iso;
		const pad2 = (n) => String(n).padStart(2, "0");
		return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}`;
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

	const parseIdsFromPath = () => {
		const segs = window.location.pathname.split("/").filter(Boolean);
		const idx = segs.findIndex((s) => s === "professor");
		if (idx >= 0 && segs[idx + 2] === "task" && segs[idx + 3] === "indiv") {
			return { lectureId: segs[idx + 1] || "", indivtaskId: segs[idx + 4] || "" };
		}
		return { lectureId: "", indivtaskId: "" };
	};

	const formatStudentLabel = (student) => {
		if (!student) {
			return { name: "(알 수 없음)", number: "" };
		}
		const name = [student.lastName, student.firstName].filter(Boolean).join("")
			|| student.name || student.userId || "(알 수 없음)";
		return { name, number: student.studentNo || "" };
	};

	const renderSummaryList = (target, entries, { emptyText, includeDate }) => {
		if (!target) return;
		if (!entries.length) {
			target.innerHTML = `<li class="list-group-item text-center text-muted">${emptyText}</li>`;
			return;
		}
		const items = entries.map(({ student, submission }) => {
			const { name, number } = formatStudentLabel(student);
			const left = number
				? `${escapeHtml(name)} <span class="text-muted small">(${escapeHtml(number)})</span>`
				: escapeHtml(name);
			const dateText = includeDate && submission?.submitAt ? fmtDateTime(submission.submitAt) : "";
			const right = dateText ? `<span class="text-muted small">${escapeHtml(dateText)}</span>` : "";
			const itemClass = right ? "list-group-item d-flex justify-content-between align-items-center" : "list-group-item";
			return `<li class="${itemClass}"><span>${left}</span>${right}</li>`;
		}).join("");
		target.innerHTML = items;
	};

	function renderTaskDetails(task) {
		const header = `
		  <div class="d-flex align-items-start flex-wrap mb-2">
			<span class="badge text-bg-primary me-2">개인과제</span>
		  </div>
		  <h1 class="h4 mb-3">${task.indivtaskName || "(제목 없음)"}</h1>
		  <div class="d-flex flex-wrap gap-3 text-muted small mb-3">
			<div>생성: <span class="text-body-secondary">${fmtDateTime(task.createAt)}</span></div>
			<div>시작: <span class="text-body-secondary">${fmtDateTime(task.startAt)}</span></div>
			<div>마감: <span class="text-body-secondary">${fmtDateTime(task.endAt)}</span></div>
		  </div>
		  <hr class="my-3"/>
		`;
		const article = `<article class="task-content mb-4">${task.indivtaskDesc || ""}</article>`;

		let filesHtml = "";
		const attachments = Array.isArray(task.attachFileList) ? task.attachFileList : [];
		if (attachments.length > 0) {
			const fileItems = attachments.map((file) => {
				const order = file.fileOrder ?? file.order;
				const fileName = file.originalFileName || file.originName || `파일 ${order ?? ""}`;
				const sizeText = typeof file.fileSize === "number" ? `(${(file.fileSize / 1024).toFixed(2)} KB)` : "";
				const sizeHtml = sizeText ? ` <small class="text-muted">${escapeHtml(sizeText)}</small>` : "";
				const hasDownload = order != null;
				const href = hasDownload
					? `${ctx}/classroom/api/v1/common/task/${encodeURIComponent(CURRENT.lectureId)}/indiv/${encodeURIComponent(CURRENT.indivtaskId)}/attach/${encodeURIComponent(order)}`
					: "#";
				const action = hasDownload
					? `<a class="btn btn-sm btn-outline-secondary" href="${href}">다운로드</a>`
					: `<span class="text-muted small">다운로드 불가</span>`;
				return `
					<li class="list-group-item d-flex justify-content-between align-items-center">
						<div class="text-truncate"><i class="bi bi-paperclip"></i> ${escapeHtml(fileName)}${sizeHtml}</div>
						${action}
					</li>`;
			}).join("");
			filesHtml = `<div class="mt-4"><h2 class="h6 mb-2">첨부파일</h2><ul class="list-group">${fileItems}</ul></div>`;
		}

		if ($taskBody) {
			$taskBody.innerHTML = header + article + filesHtml;
		}
	}

	function renderSubmissionStatus(students, task) {
		const studentList = Array.isArray(students) ? students : [];
		const submissions = Array.isArray(task.studentSubmitList) ? task.studentSubmitList : [];
		const submissionMap = new Map(submissions.map((s) => [s.enrollId, s]));
		const submittedEntries = [];
		const pendingEntries = [];
		const seenEnrollIds = new Set();

		studentList.forEach((student) => {
			const submission = submissionMap.get(student.enrollId);
			if (submission?.submitAt) {
				submittedEntries.push({ student, submission });
			} else {
				pendingEntries.push({ student, submission });
			}
			seenEnrollIds.add(student.enrollId);
		});

		submissions.forEach((submission) => {
			if (!seenEnrollIds.has(submission.enrollId) && submission.submitAt) {
				submittedEntries.push({ student: null, submission });
			}
		});

		const submittedCount = submittedEntries.length;
		const totalStudents = studentList.length;
		if ($submissionRate) {
			$submissionRate.textContent = `제출: ${submittedCount} / ${totalStudents}`;
		}

		renderSummaryList($submittedList, submittedEntries, {
			emptyText: "제출한 학생이 없습니다.",
			includeDate: true,
		});
		renderSummaryList($pendingList, pendingEntries, {
			emptyText: "제출하지 않은 학생이 없습니다.",
			includeDate: false,
		});

		if (!$submissionAccordionContainer) return;

		if (submittedCount === 0) {
			$submissionAccordionContainer.innerHTML = `<p class="text-muted text-center">제출한 학생이 없습니다.</p>`;
			return;
		}

		const accordionHtml = submittedEntries.map(({ student, submission }, index) => {
			const { name, number } = formatStudentLabel(student);
			const displayName = escapeHtml(name);
			const displayNumber = number ? `(${escapeHtml(number)})` : "";
			const submittedAt = fmtDateTime(submission?.submitAt);
			const description = submission?.submitDesc || "-";
			const studentKey = student?.studentNo || submission?.studentNo || submission?.enrollId || "";
			const filesHtml = (submission?.submitFiles && submission.submitFiles.length > 0 && submission.submitFiles.some((f) => f?.fileId))
				? submission.submitFiles.map((file) => {
					const order = file.fileOrder ?? file.order;
					const fileName = file.originalFileName || file.originName || `제출파일 ${order ?? ""}`;
					const sizeText = typeof file.fileSize === "number" ? `(${(file.fileSize / 1024).toFixed(2)} KB)` : "";
					const sizeHtml = sizeText ? `<small class="text-muted">${escapeHtml(sizeText)}</small>` : "";
					const hasDownload = order != null && studentKey;
					const href = hasDownload
						? `${ctx}/classroom/api/v1/common/${encodeURIComponent(CURRENT.lectureId)}/indivtask/${encodeURIComponent(CURRENT.indivtaskId)}/submit/${encodeURIComponent(studentKey)}/attach/${encodeURIComponent(order)}`
						: "#";
					const link = hasDownload
						? `<a href="${href}">${escapeHtml(fileName)}</a>`
						: `<span>${escapeHtml(fileName)}</span>`;
					return `<li class="list-group-item d-flex justify-content-between align-items-center">${link}${sizeHtml}</li>`;
				}).join("")
				: '<li class="list-group-item text-muted small">첨부파일 없음</li>';

			return `
				<div class="accordion-item">
					<h2 class="accordion-header" id="heading-${index}">
						<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse-${index}">
							<strong>${displayName}</strong><span class="text-muted small ms-2">${displayNumber}</span>
							<span class="ms-auto me-2">제출일: ${escapeHtml(submittedAt)}</span>
						</button>
					</h2>
					<div id="collapse-${index}" class="accordion-collapse collapse" data-bs-parent="#submission-accordion-container">
						<div class="accordion-body">
							<p><strong>학생 제출 설명:</strong></p>
							<div class="p-2 bg-light rounded mb-3">${description || "-"}</div>
							<p><strong>제출 파일:</strong></p>
							<ul class="list-group mb-3">${filesHtml}</ul>
							<hr>
							<form class="eval-form" data-enroll-id="${submission?.enrollId || ""}">
								<div class="row g-2 align-items-center">
									<div class="col-sm-3">
										<label class="form-label">점수 (0-100)</label>
										<input type="number" class="form-control form-control-sm eval-score" min="0" max="100" value="${submission?.evaluScore || ""}">
									</div>
									<div class="col-sm-9">
										<label class="form-label">피드백</label>
										<textarea class="form-control form-control-sm eval-desc" rows="2">${submission?.evaluDesc || ""}</textarea>
									</div>
								</div>
								<div class="d-flex justify-content-end mt-2">
									<button type="submit" class="btn btn-sm btn-primary btn-save-eval">평가 저장</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			`;
		}).join("");

		$submissionAccordionContainer.innerHTML = accordionHtml;
	}

	async function handleEvalSubmit(e) {
		e.preventDefault();
		const form = e.target;
		const enrollId = form.dataset.enrollId;
		const scoreInput = form.querySelector(".eval-score");
		const descInput = form.querySelector(".eval-desc");
		const button = form.querySelector(".btn-save-eval");

		if (!enrollId) {
			alert("수강 정보가 없어 평가를 저장할 수 없습니다.");
			return;
		}

		const payload = {
			enrollId,
			evaluScore: parseInt(scoreInput.value, 10),
			evaluDesc: descInput.value,
		};

		if (isNaN(payload.evaluScore) || payload.evaluScore < 0 || payload.evaluScore > 100) {
			alert("점수는 0에서 100 사이의 숫자여야 합니다.");
			return;
		}

		button.disabled = true;
		button.textContent = "저장 중...";

		const url = `${taskApiBase}/${CURRENT.lectureId}/indiv/${CURRENT.indivtaskId}/eval`;
		try {
			const res = await fetch(url, {
				method: "PATCH",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(payload),
			});
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			alert("평가가 저장되었습니다.");
		} catch (err) {
			console.error("Evaluation save error:", err);
			alert("평가 저장 중 오류가 발생했습니다.");
		} finally {
			button.disabled = false;
			button.textContent = "평가 저장";
		}
	}

	async function handleDeleteClick(e) {
		e.preventDefault();
		if (!CURRENT.lectureId || !CURRENT.indivtaskId) {
			showError("삭제할 과제 정보를 찾을 수 없습니다.");
			return;
		}
		if (!window.confirm("삭제하시겠습니까?")) {
			return;
		}

		try {
			const url = `${taskApiBase}/${CURRENT.lectureId}/indiv/${CURRENT.indivtaskId}`;
			const res = await fetch(url, { method: "DELETE" });
			if (!res.ok && res.status !== 204) {
				const message = await res.text().catch(() => `HTTP ${res.status}`);
				throw new Error(message || `HTTP ${res.status}`);
			}
			alert("삭제되었습니다.");
			const destination = CURRENT.taskListUrl || `${ctx}/classroom/professor/${encodeURIComponent(CURRENT.lectureId)}/task`;
			window.location.assign(destination);
		} catch (err) {
			console.error("Delete task error:", err);
			showError("과제를 삭제할 수 없습니다.");
		}
	}

	async function init() {
		const { lectureId, indivtaskId } = parseIdsFromPath();
		if (!lectureId || !indivtaskId) {
			showError("강의 또는 과제 정보를 찾을 수 없습니다.");
			return;
		}
		CURRENT.lectureId = lectureId;
		CURRENT.indivtaskId = indivtaskId;

		const taskListUrl = `${ctx}/classroom/professor/${encodeURIComponent(lectureId)}/task`;
		const editUrl = `${taskListUrl}/${encodeURIComponent(indivtaskId)}/edit`;
		CURRENT.taskListUrl = taskListUrl;
		if ($btnList) $btnList.href = taskListUrl;
		if ($btnEdit) $btnEdit.href = editUrl;

		if ($btnDelete) {
			$btnDelete.addEventListener("click", handleDeleteClick);
		}

		if ($submissionAccordionContainer) {
			$submissionAccordionContainer.addEventListener("submit", (event) => {
				if (event.target.classList.contains("eval-form")) {
					handleEvalSubmit(event);
				}
			});
		}

		try {
			const [students, task] = await Promise.all([
				fetch(`${profApiBase}/${lectureId}/students`, { headers: { "Accept": "application/json" } }).then((res) => res.json()),
				fetch(`${taskApiBase}/${lectureId}/indiv/${indivtaskId}`, { headers: { "Accept": "application/json" } }).then((res) => res.json()),
			]);

			renderTaskDetails(task);
			renderSubmissionStatus(students, task);

		} catch (err) {
			console.error("Initialization failed:", err);
			showError("데이터를 불러오는 중 오류가 발생했습니다.");
			if ($taskBody) $taskBody.innerHTML = "";
			if ($submissionAccordionContainer) $submissionAccordionContainer.innerHTML = "";
			renderSummaryList($submittedList, [], { emptyText: "데이터를 불러오지 못했습니다.", includeDate: false });
			renderSummaryList($pendingList, [], { emptyText: "데이터를 불러오지 못했습니다.", includeDate: false });
		}
	}

	init();
})();
