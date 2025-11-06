// /js/app/classroom/student/stuTask.js
(() => {
	"use strict";

	// =========================
	// ① DOM & 상수
	// =========================
	const $root = document.querySelector("#task-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId;
	const apiBase = `${ctx}/classroom/api/v1/student/task/${lectureId}`;

	const $notice = document.getElementById("task-notice-box");

	// 개인과제
	const $indivListAll = document.getElementById("indiv-list-all");
	const $indivListUpcoming = document.getElementById("indiv-list-upcoming");
	const $indivListOngoing = document.getElementById("indiv-list-ongoing");
	const $indivListClosed = document.getElementById("indiv-list-closed");
	const $indivPgAll = document.getElementById("indiv-pagination-all");
	const $indivPgUpcoming = document.getElementById("indiv-pagination-upcoming");
	const $indivPgOngoing = document.getElementById("indiv-pagination-ongoing");
	const $indivPgClosed = document.getElementById("indiv-pagination-closed");
	const $indivCntAll = document.getElementById("indiv-count-all");
	const $indivCntUpcoming = document.getElementById("indiv-count-upcoming");
	const $indivCntOngoing = document.getElementById("indiv-count-ongoing");
	const $indivCntClosed = document.getElementById("indiv-count-closed");

	// 조별과제 (요소는 있으나 기능은 미구현)
	const $groupListAll = document.getElementById("group-list-all");
	const $groupListUpcoming = document.getElementById("group-list-upcoming");
	const $groupListOngoing = document.getElementById("group-list-ongoing");
	const $groupListClosed = document.getElementById("group-list-closed");
	const $groupPgAll = document.getElementById("group-pagination-all");
	const $groupPgUpcoming = document.getElementById("group-pagination-upcoming");
	const $groupPgOngoing = document.getElementById("group-pagination-ongoing");
	const $groupPgClosed = document.getElementById("group-pagination-closed");
	const $groupCntAll = document.getElementById("group-count-all");
	const $groupCntUpcoming = document.getElementById("group-count-upcoming");
	const $groupCntOngoing = document.getElementById("group-count-ongoing");
	const $groupCntClosed = document.getElementById("group-count-closed");

	const PAGE_SIZE = 10;
	const state = {
		indiv: {
			raw: [],
			filtered: { all: [], upcoming: [], ongoing: [], closed: [] },
			pages:    { all: 1,   upcoming: 1,   ongoing: 1,   closed: 1   },
		},
		group: {
			raw: [],
			filtered: { all: [], upcoming: [], ongoing: [], closed: [] },
			pages:    { all: 1,   upcoming: 1,   ongoing: 1,   closed: 1   },
		},
	};

	// =========================
	// ② 유틸
	// =========================
	const log = console; // 간단하게 console 사용
	const fmtDateTime = (isoStr) => {
		if (!isoStr) return "-";
		const d = new Date(isoStr);
		if (Number.isNaN(d.getTime())) return "-";
		const pad = (n) => String(n).padStart(2, "0");
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};

	const resolveStatus = (task) => {
		const now = Date.now();
		const start = task.startAt ? new Date(task.startAt).getTime() : null;
		const end = task.endAt ? new Date(task.endAt).getTime() : null;

		if (start && start > now) return "upcoming";
		if (end && end < now) return "closed";
		return "ongoing";
	};

	const showError = (msg) => {
		if (!$notice) return;
		$notice.textContent = msg;
		$notice.classList.remove("d-none", "alert-success");
		$notice.classList.add("alert-danger");
	};

	const chunk = (arr, page, size) => {
		const start = (page - 1) * size;
		return arr.slice(start, start + size);
	};

	const escapeHtml = (s) => String(s).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

	const normalizeIndiv = (r) => ({
		type: "indiv",
		id:   r.indivtaskId,
		name: r.indivtaskName,
		createAt: r.createAt,
		startAt:  r.startAt,
		endAt:    r.endAt,
		hasAttachFile: !!r.hasAttachFile,
		submitted: !!r.submitted,
	});

	const normalizeGroup = (r) => ({
		type: "group",
		id:   r.grouptaskId,
		name: r.grouptaskName,
		createAt: r.createAt,
		startAt:  r.startAt,
		endAt:    r.endAt,
		hasAttachFile: !!r.hasAttachFile,
		submitted: !!r.submitted,
	});

	// =========================
	// ③ API
	// =========================
	const api = {
		async fetchIndiv() {
			const url = `${apiBase}/indiv`;
			log.info("[API] GET", url);
			try {
				const res = await fetch(url, { method: "GET", headers: { "Accept": "application/json" } });
				if (!res.ok) throw new Error(`HTTP ${res.status}`);
				const data = await res.json();
				return Array.isArray(data) ? data : [];
			} catch (e) {
				log.error("fetchIndiv failed:", e);
				throw e;
			}
		},
		async fetchGroup() {
			const url = `${apiBase}/group`;
			log.info("[API] GET", url);
			try {
				const res = await fetch(url, { method: "GET", headers: { "Accept": "application/json" } });
				if (!res.ok) throw new Error(`HTTP ${res.status}`);
				const data = await res.json();
				return Array.isArray(data) ? data : [];
			} catch (e) {
				log.error("fetchGroup failed:", e);
				throw e;
			}
		},
	};

	// =========================
	// ④ 렌더러
	// =========================
	const render = {
		table(tasks) {
			if (!tasks || tasks.length === 0) {
				return `<div class="text-muted p-3 text-center">과제가 없습니다.</div>`;
			}
			const rows = tasks.map((t) => {
				const href = `${ctx}/classroom/student/${lectureId}/task/${t.type}/${t.id}`;
				const submittedBadge = t.submitted
					? `<span class="badge bg-primary">제출 완료</span>`
					: `<span class="badge bg-secondary">미제출</span>`;

				const nameCell = `
					<a href="${href}" class="text-decoration-none d-block">
						<div class="fw-bold">${escapeHtml(t.name || "")}</div>
						<div class="small text-muted">${fmtDateTime(t.startAt)} ~ ${fmtDateTime(t.endAt)}</div>
					</a>`;

				return `
					<tr>
						<td class="align-middle">${nameCell}</td>
						<td class="align-middle text-center">${submittedBadge}</td>
						<td class="align-middle text-center d-none d-md-table-cell">${fmtDateTime(t.createAt)}</td>
					</tr>`;
			}).join("");

			return `
				<table class="table table-hover align-middle mb-0">
					<thead>
						<tr class="table-light">
							<th style="width:60%">과제명</th>
							<th class="text-center" style="width:20%">제출여부</th>
							<th class="text-center d-none d-md-table-cell" style="width:20%">등록일</th>
						</tr>
					</thead>
					<tbody>${rows}</tbody>
				</table>`;
		},

		pagination(totalCount, currentPage, pageSize, onClickName) {
			const totalPages = Math.max(1, Math.ceil(totalCount / pageSize));
			if (totalPages <= 1) return "";
			const items = [];
			const mk = (page, label = page, disabled = false, active = false) => {
				const cls = ["page-item"];
				if (disabled) cls.push("disabled");
				if (active) cls.push("active");
				return `<li class="${cls.join(" ")}"><a class="page-link" href="#" data-page="${page}" data-onclick="${onClickName}">${label}</a></li>`;
			};

			items.push(mk(Math.max(1, currentPage - 1), "‹", currentPage === 1));
			for (let p = 1; p <= totalPages; p++) items.push(mk(p, String(p), false, p === currentPage));
			items.push(mk(Math.min(totalPages, currentPage + 1), "›", currentPage === totalPages));

			return items.join("");
		},

		indivSection(kind, page = 1) {
			const listEl = { all: $indivListAll, upcoming: $indivListUpcoming, ongoing: $indivListOngoing, closed: $indivListClosed }[kind];
			const pgEl   = { all: $indivPgAll,   upcoming: $indivPgUpcoming,   ongoing: $indivPgOngoing,   closed: $indivPgClosed   }[kind];
			if (!listEl || !pgEl) return;

			const data = state.indiv.filtered[kind] || [];
			const pageData = chunk(data, page, PAGE_SIZE);

			listEl.innerHTML = render.table(pageData);
			pgEl.innerHTML   = render.pagination(data.length, page, PAGE_SIZE, `indiv-${kind}`);
		},

		indivCounts() {
			if ($indivCntAll)      $indivCntAll.textContent = state.indiv.filtered.all.length;
			if ($indivCntUpcoming) $indivCntUpcoming.textContent = state.indiv.filtered.upcoming.length;
			if ($indivCntOngoing)  $indivCntOngoing.textContent = state.indiv.filtered.ongoing.length;
			if ($indivCntClosed)   $indivCntClosed.textContent = state.indiv.filtered.closed.length;
		},

		groupSection(kind, page = 1) {
			const listEl = { all: $groupListAll, upcoming: $groupListUpcoming, ongoing: $groupListOngoing, closed: $groupListClosed }[kind];
			const pgEl   = { all: $groupPgAll,   upcoming: $groupPgUpcoming,   ongoing: $groupPgOngoing,   closed: $groupPgClosed   }[kind];
			if (!listEl || !pgEl) return;

			const data = state.group.filtered[kind] || [];
			const pageData = chunk(data, page, PAGE_SIZE);

			listEl.innerHTML = render.table(pageData);
			pgEl.innerHTML   = render.pagination(data.length, page, PAGE_SIZE, `group-${kind}`);
		},

		groupCounts() {
			if ($groupCntAll)      $groupCntAll.textContent = state.group.filtered.all.length;
			if ($groupCntUpcoming) $groupCntUpcoming.textContent = state.group.filtered.upcoming.length;
			if ($groupCntOngoing)  $groupCntOngoing.textContent = state.group.filtered.ongoing.length;
			if ($groupCntClosed)   $groupCntClosed.textContent = state.group.filtered.closed.length;
		},
	};

	// =========================
	// ⑤ 데이터 로딩 & 필터링
	// =========================
	async function loadIndiv() {
		try {
			const list = await api.fetchIndiv();
			const normalized = list.map(normalizeIndiv);
			normalized.sort((a, b) => new Date(b.createAt) - new Date(a.createAt));
			state.indiv.raw = normalized;

			const buckets = { all: [], upcoming: [], ongoing: [], closed: [] };
			for (const t of normalized) {
				const st = resolveStatus(t);
				buckets.all.push(t);
				buckets[st]?.push(t);
			}
			state.indiv.filtered = buckets;

			render.indivCounts();
			state.indiv.pages = { all: 1, upcoming: 1, ongoing: 1, closed: 1 };
			Object.keys(buckets).forEach(kind => render.indivSection(kind, 1));

		} catch (e) {
			showError("개인과제 목록을 불러오는 중 오류가 발생했습니다.");
		}
	}

	async function loadGroup() {
		try {
			const list = await api.fetchGroup();
			const normalized = list.map(normalizeGroup);
			normalized.sort((a, b) => new Date(b.createAt) - new Date(a.createAt));
			state.group.raw = normalized;

			const buckets = { all: [], upcoming: [], ongoing: [], closed: [] };
			for (const t of normalized) {
				const st = resolveStatus(t);
				buckets.all.push(t);
				buckets[st]?.push(t);
			}
			state.group.filtered = buckets;

			render.groupCounts();
			state.group.pages = { all: 1, upcoming: 1, ongoing: 1, closed: 1 };
			Object.keys(buckets).forEach(kind => render.groupSection(kind, 1));

		} catch (e) {
			showError("조별과제 목록을 불러오는 중 오류가 발생했습니다.");
		}
	}

	// =========================
	// ⑥ 이벤트 바인딩
	// =========================
	$root.addEventListener("click", (ev) => {
		const a = ev.target.closest("a.page-link[data-page][data-onclick]");
		if (!a) return;
		ev.preventDefault();

		const page = Number(a.dataset.page || "1");
		const onclickName = a.dataset.onclick;

		const [scope, kind] = onclickName.split("-");
		if (scope === "indiv" && state.indiv.filtered[kind]) {
			state.indiv.pages[kind] = page;
			render.indivSection(kind, page);
		} else if (scope === "group" && state.group.filtered[kind]) {
			state.group.pages[kind] = page;
			render.groupSection(kind, page);
		}
	});

	// =========================
	// ⑦ 초기화
	// =========================
	(async function init() {
		log.info("Init stuTask.js", { lectureId, apiBase });
		if (!lectureId) {
			showError("강의 정보를 찾을 수 없습니다.");
			return;
		}
		await Promise.all([loadIndiv(), loadGroup()]);
	})();
})();