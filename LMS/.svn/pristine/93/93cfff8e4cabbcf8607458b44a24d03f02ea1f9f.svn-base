// /js/app/classroom/professor/prfExam.js
(() => {
	"use strict";

	const $root = document.querySelector("#exam-root");
	if (!$root) {
		console.warn("[prfExam] #exam-root not found. Abort.");
		return;
	}

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId;
	const apiAttr = $root.dataset.api || "";
	const apiBase = (apiAttr.trim().length ? apiAttr : `${ctx}/classroom/api/v1/professor/exam`).replace(/\/$/, "");
	const apiUrl = `${apiBase}/${lectureId}`;
	const weightApiUrl = `${apiBase}/${lectureId}/weightValue`;

	const $notice = document.getElementById("exam-notice-box");
	const $reloadBtn = document.getElementById("btn-reload-exam");
	const $weightBtn = document.getElementById("btn-weight-bulk");

	const tableBodies = {
		all: document.getElementById("exam-table-all"),
		upcoming: document.getElementById("exam-table-upcoming"),
		ongoing: document.getElementById("exam-table-ongoing"),
		closed: document.getElementById("exam-table-closed"),
	};

	const countBadges = {
		all: document.getElementById("exam-count-all"),
		upcoming: document.getElementById("exam-count-upcoming"),
		ongoing: document.getElementById("exam-count-ongoing"),
		closed: document.getElementById("exam-count-closed"),
	};

	const messages = {
		allEmpty: "등록된 시험이 없습니다.",
		upcomingEmpty: "예정된 시험이 없습니다.",
		ongoingEmpty: "진행중인 시험이 없습니다.",
		closedEmpty: "마감된 시험이 없습니다.",
		loading: "불러오는 중입니다...",
		error: "시험 목록을 불러오는 데 실패했습니다.",
	};

	const statusMeta = {
		upcoming: { label: "응시대기", klass: "bg-info text-dark" },
		ongoing: { label: "진행중", klass: "bg-success" },
		closed: { label: "마감", klass: "bg-secondary" },
		unknown: { label: "미정", klass: "bg-light text-dark" },
	};

	const state = {
		exams: [],
		buckets: {
			all: [],
			upcoming: [],
			ongoing: [],
			closed: [],
		},
	};

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

	const resolveStatus = (startAt, endAt) => {
		const now = Date.now();
		const start = startAt ? new Date(startAt).getTime() : undefined;
		const end = endAt ? new Date(endAt).getTime() : undefined;

		if (typeof start === "number" && start > now) return "upcoming";
		if (typeof start === "number" && start <= now) {
			if (typeof end !== "number" || end >= now) return "ongoing";
			if (end < now) return "closed";
		}
		if (typeof end === "number" && end < now) return "closed";
		return "unknown";
	};

	const updateWeightButtonState = () => {
		if ($weightBtn) {
			$weightBtn.disabled = state.exams.length === 0;
		}
	};

	const showNotice = (message, variant) => {
		if (!$notice) return;
		$notice.textContent = message;
		$notice.classList.remove("d-none", "alert-danger", "alert-success");
		if (variant) {
			$notice.classList.add(variant);
		}
	};

	const showErrorNotice = (message) => showNotice(message, "alert-danger");
	const showSuccessNotice = (message) => showNotice(message, "alert-success");

	const clearNotice = () => {
		if (!$notice) return;
		$notice.textContent = "";
		$notice.classList.add("d-none");
		$notice.classList.remove("alert-danger", "alert-success");
	};

	const setTableMessage = (tbody, message, tone = "muted") => {
		if (!tbody) return;
		tbody.textContent = "";
		const tr = document.createElement("tr");
		const td = document.createElement("td");
		td.colSpan = 7;
		td.className = `text-center py-4${tone ? ` text-${tone}` : ""}`;
		td.textContent = message;
		tr.appendChild(td);
		tbody.appendChild(tr);
	};

	const buildExamRow = (exam) => {
		const tr = document.createElement("tr");
		tr.dataset.examId = exam.lctExamId || "";

		const statusKey = resolveStatus(exam.startAt, exam.endAt);
		const status = statusMeta[statusKey] || statusMeta.unknown;

		const cells = [
			exam.examName || "-",
			exam.examType || "-",
			`<span class="badge ${status.klass}">${status.label}</span>`,
			formatPeriod(exam.startAt, exam.endAt),
			`${exam.submitCount ?? 0} / ${exam.targetCount ?? 0}`,
			String(exam.weightValue ?? 0),
			formatDateTime(exam.createAt),
		];

		cells.forEach((value, index) => {
			const td = document.createElement("td");
			if (index === 2) {
				td.innerHTML = value;
			} else {
				td.textContent = value;
			}
			tr.appendChild(td);
		});

		return tr;
	};

	const renderTable = (tbody, list, emptyMessage) => {
		if (!tbody) return;
		if (!Array.isArray(list) || list.length === 0) {
			setTableMessage(tbody, emptyMessage);
			return;
		}
		const fragment = document.createDocumentFragment();
		list.forEach((exam) => fragment.appendChild(buildExamRow(exam)));
		tbody.textContent = "";
		tbody.appendChild(fragment);
	};

	const renderBuckets = () => {
		renderTable(tableBodies.all, state.buckets.all, messages.allEmpty);
		renderTable(tableBodies.upcoming, state.buckets.upcoming, messages.upcomingEmpty);
		renderTable(tableBodies.ongoing, state.buckets.ongoing, messages.ongoingEmpty);
		renderTable(tableBodies.closed, state.buckets.closed, messages.closedEmpty);
	};

	const updateCounts = () => {
		if (countBadges.all) countBadges.all.textContent = String(state.buckets.all.length);
		if (countBadges.upcoming) countBadges.upcoming.textContent = String(state.buckets.upcoming.length);
		if (countBadges.ongoing) countBadges.ongoing.textContent = String(state.buckets.ongoing.length);
		if (countBadges.closed) countBadges.closed.textContent = String(state.buckets.closed.length);
	};

	const setLoadingState = () => {
		Object.values(tableBodies).forEach((tbody) => setTableMessage(tbody, messages.loading));
	};

	const setErrorState = () => {
		Object.values(tableBodies).forEach((tbody) => setTableMessage(tbody, messages.error, "danger"));
	};

	async function loadExams() {
		if (!lectureId) {
			setErrorState();
			showErrorNotice("lectureId가 존재하지 않습니다.");
			return;
		}

		state.exams = [];
		state.buckets = { all: [], upcoming: [], ongoing: [], closed: [] };
		updateWeightButtonState();
		updateCounts();
		setLoadingState();
		clearNotice();

		try {
			const response = await fetch(apiUrl, {
				headers: { Accept: "application/json" },
				credentials: "same-origin",
			});
			if (!response.ok) throw new Error(`HTTP ${response.status}`);

			const payload = await response.json();
			if (!Array.isArray(payload)) throw new Error("Unexpected payload");

			const sorted = [...payload].sort((a, b) => {
				const aTime = new Date(a.createAt || 0).getTime();
				const bTime = new Date(b.createAt || 0).getTime();
				return bTime - aTime;
			});

			state.exams = sorted;
			state.buckets = {
				all: sorted,
				upcoming: [],
				ongoing: [],
				closed: [],
			};

			for (const exam of sorted) {
				const status = resolveStatus(exam.startAt, exam.endAt);
				if (status === "upcoming") state.buckets.upcoming.push(exam);
				else if (status === "ongoing") state.buckets.ongoing.push(exam);
				else if (status === "closed") state.buckets.closed.push(exam);
				else state.buckets.ongoing.push(exam);
			}

			updateWeightButtonState();
			updateCounts();
			renderBuckets();
			weightModal.refresh();
		} catch (error) {
			console.error("[prfExam] Failed to load exams:", error);
			state.exams = [];
			state.buckets = { all: [], upcoming: [], ongoing: [], closed: [] };
			updateWeightButtonState();
			updateCounts();
			setErrorState();
			showErrorNotice("시험 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
			weightModal.refresh();
		}
	}

	const weightModal = createWeightModal();

	if ($reloadBtn) {
		$reloadBtn.addEventListener("click", (event) => {
			event.preventDefault();
			loadExams();
		});
	}

	if ($weightBtn) {
		$weightBtn.addEventListener("click", (event) => {
			event.preventDefault();
			if (!state.exams.length) {
				showErrorNotice("가중치를 수정할 시험이 없습니다.");
				return;
			}
			weightModal.open();
		});
	}

	loadExams();

	function createWeightModal() {
		const modalEl = document.getElementById("exam-weight-modal");
		const formEl = document.getElementById("exam-weight-form");
		const listEl = document.getElementById("exam-weight-list");
		const totalEl = document.getElementById("exam-weight-total");
		const errorEl = document.getElementById("exam-weight-error");
		const saveBtn = document.getElementById("exam-weight-save");
		const closeBtns = modalEl ? modalEl.querySelectorAll("[data-action='close-weight-modal']") : [];

		if (!modalEl || !formEl || !listEl || !totalEl || !errorEl || !saveBtn) {
			return {
				open() {},
				close() {},
				refresh() {},
			};
		}

		let bsModal = null;
		let fallbackBackdrop = null;
		let isOpen = false;
		let saving = false;

		if (window.bootstrap?.Modal) {
			bsModal = new bootstrap.Modal(modalEl, {
				backdrop: "static",
				keyboard: false,
			});
		}

		const ensureBackdrop = () => {
			if (fallbackBackdrop) return;
			const div = document.createElement("div");
			div.className = "modal-backdrop fade show";
			document.body.appendChild(div);
			fallbackBackdrop = div;
		};

		const removeBackdrop = () => {
			if (!fallbackBackdrop) return;
			fallbackBackdrop.remove();
			fallbackBackdrop = null;
		};

		const fallbackShow = () => {
			modalEl.style.display = "block";
			modalEl.removeAttribute("aria-hidden");
			modalEl.classList.add("show");
			document.body.classList.add("modal-open");
			document.body.style.overflow = "hidden";
			ensureBackdrop();
		};

		const fallbackHide = () => {
			modalEl.style.display = "none";
			modalEl.setAttribute("aria-hidden", "true");
			modalEl.classList.remove("show");
			document.body.classList.remove("modal-open");
			document.body.style.removeProperty("overflow");
			removeBackdrop();
		};

		const showModal = () => {
			if (bsModal) bsModal.show();
			else fallbackShow();
		};

		const hideModal = () => {
			if (bsModal) bsModal.hide();
			else fallbackHide();
		};

		const clearError = () => {
			errorEl.textContent = "";
			errorEl.classList.add("d-none");
		};

		const setError = (message) => {
			errorEl.textContent = message;
			errorEl.classList.remove("d-none");
		};

		const getInputs = () => Array.from(listEl.querySelectorAll("input[data-exam-id]"));

		const updateTotal = () => {
			const inputs = getInputs();
			let sum = 0;
			for (const input of inputs) {
				const value = Number.parseInt(input.value, 10);
				if (!Number.isNaN(value)) sum += value;
			}
			totalEl.textContent = String(sum);
			return sum;
		};

		const populateList = () => {
			listEl.textContent = "";

			if (!state.exams.length) {
				const tr = document.createElement("tr");
				const td = document.createElement("td");
				td.colSpan = 4;
				td.className = "text-center text-muted py-4";
				td.textContent = "시험 목록이 없습니다.";
				tr.appendChild(td);
				listEl.appendChild(tr);
				saveBtn.disabled = true;
				updateTotal();
				return;
			}

			state.exams.forEach((exam, index) => {
				const tr = document.createElement("tr");

				const orderTd = document.createElement("td");
				orderTd.textContent = String(index + 1);
				tr.appendChild(orderTd);

				const nameTd = document.createElement("td");
				nameTd.textContent = exam.examName || "-";
				tr.appendChild(nameTd);

				const periodTd = document.createElement("td");
				periodTd.className = "text-center";
				periodTd.textContent = formatPeriod(exam.startAt, exam.endAt);
				tr.appendChild(periodTd);

				const weightTd = document.createElement("td");
				weightTd.className = "text-end";

				const input = document.createElement("input");
				input.type = "number";
				input.min = "0";
				input.max = "100";
				input.step = "1";
				input.className = "form-control form-control-sm text-end";
				input.value = Number.isFinite(exam.weightValue) ? String(exam.weightValue) : "0";
				input.dataset.examId = exam.lctExamId || "";
				input.dataset.examName = exam.examName || "";

				weightTd.appendChild(input);
				tr.appendChild(weightTd);
				listEl.appendChild(tr);
			});

			saveBtn.disabled = saving;
			updateTotal();
		};

		const setSaving = (flag) => {
			saving = flag;
			saveBtn.disabled = flag || !state.exams.length;
			getInputs().forEach((input) => {
				input.disabled = flag;
			});
		};

		const open = () => {
			if (!state.exams.length) return;
			populateList();
			clearError();
			isOpen = true;
			showModal();
		};

		const close = () => {
			isOpen = false;
			setSaving(false);
			clearError();
			hideModal();
		};

		if (bsModal) {
			modalEl.addEventListener("hidden.bs.modal", () => {
				isOpen = false;
				setSaving(false);
				clearError();
			});
		}

		closeBtns.forEach((btn) => {
			btn.addEventListener("click", (event) => {
				event.preventDefault();
				close();
			});
		});

		listEl.addEventListener("input", (event) => {
			const target = event.target;
			if (!(target instanceof HTMLInputElement)) return;
			if (!target.hasAttribute("data-exam-id")) return;
			if (target.value.length > 3) {
				target.value = target.value.slice(0, 3);
			}
			clearError();
			updateTotal();
		});

		formEl.addEventListener("submit", async (event) => {
			event.preventDefault();
			if (saving) return;

			const inputs = getInputs();
			if (!inputs.length) {
				setError("조정할 시험이 없습니다.");
				return;
			}

			const payload = [];
			let sum = 0;

			for (const input of inputs) {
				const examId = input.dataset.examId;
				const examName = input.dataset.examName || "시험";
				const value = Number.parseInt(input.value, 10);

				if (!examId) {
					setError("시험 정보가 올바르지 않습니다.");
					return;
				}
				if (!Number.isInteger(value) || value < 0 || value > 100) {
					setError(`${examName}의 가중치는 0 이상 100 이하의 정수여야 합니다.`);
					return;
				}

				sum += value;
				payload.push({ lctExamId: examId, weightValue: value });
			}

			if (sum !== 100) {
				setError(`가중치 합계가 ${sum}입니다. 100이 되도록 조정해 주세요.`);
				return;
			}

			try {
				setSaving(true);
				const response = await fetch(weightApiUrl, {
					method: "PUT",
					headers: {
						"Content-Type": "application/json",
						Accept: "application/json",
					},
					credentials: "same-origin",
					body: JSON.stringify(payload),
				});
				if (!response.ok) throw new Error(`HTTP ${response.status}`);

				close();
				showSuccessNotice("시험 가중치를 저장했습니다.");
				await loadExams();
			} catch (error) {
				console.error("[prfExam] Failed to update weight values:", error);
				setError("가중치 저장 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
			} finally {
				setSaving(false);
			}
		});

		return {
			open,
			close,
			refresh() {
				if (!isOpen) return;
				populateList();
				updateTotal();
			},
		};
	}
})();
