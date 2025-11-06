// /js/app/classroom/common/lectureInfoTooltip.js
(() => {
	"use strict";

	const trigger = document.querySelector("[data-lecture-info-trigger]");
	if (!trigger) return;

	const ctx = trigger.dataset.ctx || "";
	const lectureId = trigger.dataset.lectureId || "";
	const apiAttr = trigger.dataset.api || "";
	const apiBase = (apiAttr.trim().length ? apiAttr : `${ctx}/classroom/api/v1/common`).replace(/\/$/, "");
	const apiUrl = `${apiBase}/${encodeURIComponent(lectureId)}`;
	const scheduleUrl = `${apiUrl}/schedule`;

	let cache = null;
	let fetchPromise = null;
	let tooltipEl = null;
	let hideTimer = null;

	const termLabelMap = {
		REG1: "1학기",
		REG2: "2학기",
		SUB1: "여름학기",
		SUB2: "겨울학기",
	};

	const subjectLabel = document.querySelector("[data-lecture-subject-label]");

	const pad = (num) => String(num).padStart(2, "0");

	const dayLabelMap = {
		MO: "월",
		TU: "화",
		WE: "수",
		TH: "목",
		FR: "금",
		SA: "토",
		SU: "일",
	};

	const formatDate = (iso) => {
		if (!iso) return "미정";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return "미정";
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
	};

	const formatYearTerm = (code) => {
		if (!code) return "-";
		const parts = String(code).split("_");
		if (parts.length !== 2) return code;
		const [year, term] = parts;
		const label = termLabelMap[term] || term;
		return `${year}년 ${label}`;
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

	const formatTime = (value) => {
		if (!value) return "-";
		const str = String(value).trim();
		if (str.length !== 4) return str;
		return `${str.slice(0, 2)}:${str.slice(2)}`;
	};

	const formatSlots = (slots = []) => {
		if (!Array.isArray(slots) || slots.length === 0) return "시간 정보 없음";
		return slots
			.map((slot) => {
				const day = dayLabelMap[slot.day] || slot.day || "-";
				const start = formatTime(slot.start);
				const end = formatTime(slot.end);
				return `${day} ${start}~${end}`;
			})
			.join(", ");
	};

	const ensureTooltip = () => {
		if (tooltipEl) return tooltipEl;
		const el = document.createElement("div");
		el.className = "lecture-info-tooltip shadow-lg border rounded bg-white p-3 small";
		el.style.position = "absolute";
		el.style.minWidth = "280px";
		el.style.maxWidth = "360px";
		el.style.zIndex = "1050";
		el.style.pointerEvents = "auto";
		el.style.transition = "opacity 0.12s ease-in-out";
		el.style.opacity = "0";
		el.setAttribute("role", "dialog");
		el.setAttribute("aria-live", "polite");

		el.addEventListener("mouseenter", () => {
			if (hideTimer) {
				clearTimeout(hideTimer);
				hideTimer = null;
			}
		});
		el.addEventListener("mouseleave", () => scheduleHide(80));

		tooltipEl = el;
		document.body.appendChild(el);
		return el;
	};

	const renderLoading = () => {
		const el = ensureTooltip();
		el.innerHTML = `<div class="d-flex align-items-center"><div class="spinner-border spinner-border-sm text-primary me-2" role="status" aria-hidden="true"></div><span>불러오는 중...</span></div>`;
	};

	const renderError = (message) => {
		const el = ensureTooltip();
		el.innerHTML = `<div class="text-danger">${escapeHtml(message || "정보를 가져오지 못했습니다.")}</div>`;
	};

	const renderSchedule = (schedule = []) => {
		if (!Array.isArray(schedule) || schedule.length === 0) {
			return `<div class="text-muted">시간표 정보가 없습니다.</div>`;
		}
		return schedule
			.map((entry) => {
				const place = entry.placeName || entry.placeCd || "-";
				const slots = formatSlots(entry.slots);
				return `
					<div class="mb-2">
						<div class="fw-semibold">${escapeHtml(place)}</div>
						<div class="text-muted">${escapeHtml(slots)}</div>
					</div>
				`;
			})
			.join("");
	};

	const applyLectureHeader = (lecture) => {
		if (!subjectLabel || !lecture) return;
		if (lecture.subjectName) {
			subjectLabel.textContent = lecture.subjectName;
		}
	};

	const renderData = (data) => {
		const el = ensureTooltip();
		const lecture = data?.lecture || {};
		const schedule = data?.schedule || [];

		applyLectureHeader(lecture);

		const prereq = lecture.prereqSubject ? lecture.prereqSubject : "없음";
		const capacity = Number.isFinite(lecture.maxCap) ? `${lecture.maxCap}명` : "-";
		const info = `
			<div class="fw-semibold mb-1">${escapeHtml(lecture.subjectName || "-")}</div>
			<div class="text-muted mb-2">${escapeHtml(lecture.professorName || "-")} 교수</div>
			<dl class="row mb-2">
				<dt class="col-5">강의 ID</dt><dd class="col-7 text-end">${escapeHtml(lecture.lectureId || "-")}</dd>
				<dt class="col-5">년도/학기</dt><dd class="col-7 text-end">${escapeHtml(formatYearTerm(lecture.yeartermCd))}</dd>
				<dt class="col-5">수강 정원</dt><dd class="col-7 text-end">${escapeHtml(capacity)}</dd>
				<dt class="col-5">선행 과목</dt><dd class="col-7 text-end">${escapeHtml(prereq)}</dd>
				<dt class="col-5">종강 예정</dt><dd class="col-7 text-end">${escapeHtml(formatDate(lecture.endAt))}</dd>
			</dl>
			<div class="mb-2">
				<div class="fw-semibold mb-1">강의 개요</div>
				<div class="text-break">${escapeHtml(lecture.lectureIndex || "-")}</div>
			</div>
			<div>
				<div class="fw-semibold mb-1">강의 목표</div>
				<div class="text-break">${escapeHtml(lecture.lectureGoal || "-")}</div>
			</div>
			<div class="mt-3">
				<div class="fw-semibold mb-1">강의 시간표</div>
				${renderSchedule(schedule)}
			</div>
		`;
		el.innerHTML = info;
	};

	const positionTooltip = () => {
		if (!tooltipEl) return;
		const rect = trigger.getBoundingClientRect();
		const tooltipRect = tooltipEl.getBoundingClientRect();
		const offsetY = 10;
		const top = rect.bottom + offsetY + window.scrollY;
		let left = rect.left + window.scrollX - (tooltipRect.width / 2) + (rect.width / 2);

		const margin = 12;
		const maxLeft = document.documentElement.clientWidth - tooltipRect.width - margin;
		if (left < margin) left = margin;
		else if (left > maxLeft) left = maxLeft;

		tooltipEl.style.top = `${Math.max(top, margin)}px`;
		tooltipEl.style.left = `${left}px`;
		requestAnimationFrame(() => {
			if (tooltipEl) tooltipEl.style.opacity = "1";
		});
	};

	const scheduleHide = (delay = 0) => {
		if (hideTimer) clearTimeout(hideTimer);
		hideTimer = setTimeout(() => {
			if (tooltipEl) {
				tooltipEl.remove();
				tooltipEl = null;
			}
		}, delay);
	};

	const showTooltip = async () => {
		if (!lectureId) return;

		if (hideTimer) {
			clearTimeout(hideTimer);
			hideTimer = null;
		}

		if (cache) {
			renderData(cache);
			positionTooltip();
			return;
		}

		renderLoading();
		positionTooltip();

		try {
			const data = await retrieveData();
			renderData(data);
			positionTooltip();
		} catch (error) {
			console.error("[lectureInfoTooltip] failed to fetch lecture info:", error);
			renderError("강의 정보를 불러오는 데 실패했습니다.");
			positionTooltip();
		}
	};

	const hideTooltip = () => scheduleHide(80);

	const retrieveData = () => {
		if (!lectureId) return Promise.reject(new Error("lectureId is required"));
		if (cache) return Promise.resolve(cache);
		if (fetchPromise) return fetchPromise;

		fetchPromise = (async () => {
			const [lectureRes, scheduleRes] = await Promise.all([
				fetch(apiUrl, {
					headers: { Accept: "application/json" },
					credentials: "same-origin",
				}),
				fetch(scheduleUrl, {
					headers: { Accept: "application/json" },
					credentials: "same-origin",
				}),
			]);
			if (!lectureRes.ok) throw new Error(`HTTP ${lectureRes.status}`);
			if (!scheduleRes.ok) throw new Error(`HTTP ${scheduleRes.status}`);

			const lecturePayload = await lectureRes.json();
			const schedulePayload = await scheduleRes.json();

			cache = { lecture: lecturePayload, schedule: schedulePayload };
			applyLectureHeader(cache.lecture);
			fetchPromise = null;
			return cache;
		})().catch((error) => {
			fetchPromise = null;
			throw error;
		});

		return fetchPromise;
	};

	trigger.addEventListener("mouseenter", showTooltip);
	trigger.addEventListener("focus", showTooltip);
	trigger.addEventListener("mouseleave", hideTooltip);
	trigger.addEventListener("blur", hideTooltip);
	trigger.addEventListener("keydown", (event) => {
		if (event.key === "Escape" && tooltipEl) {
			event.preventDefault();
			scheduleHide();
		}
	});

	if (subjectLabel) {
		retrieveData().catch((error) => {
			console.error("[lectureInfoTooltip] prefetch failed:", error);
		});
	}
})();
