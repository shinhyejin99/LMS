// /js/app/classroom/professor/prfTask.js
// 모듈 스코프 IIFE
(() => {
	"use strict";

	// =========================
	// ① DOM 훅 & 상수
	// =========================
	const $root = document.querySelector("#task-root");
	if (!$root) {
		console.warn("[prfTask] #task-root not found. Abort.");
		return;
	}

	// data-* 속성
	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId;
	const apiAttr = $root.dataset.api;

	// 공통 과제 API 베이스 (/classroom/api/v1/professor/task/{lectureId})
	const apiBase =
		apiAttr && apiAttr.trim().length > 0
			? `${apiAttr.replace(/\/$/, "")}/${lectureId}`
			: `${ctx}/classroom/api/v1/professor/task/${lectureId}`;

	// 공용 알림 박스
	const $notice = document.getElementById("task-notice-box");

	// 개인과제 탭/컨테이너
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

	// 조별과제 탭/컨테이너
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

	// 상수
	const PAGE_SIZE = 10;

	// 상태 보관
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
	const log = {
		group(label) { console.group(`[prfTask] ${label}`); },
		groupEnd() { console.groupEnd(); },
		info(...args) { console.info("[prfTask]", ...args); },
		warn(...args) { console.warn("[prfTask]", ...args); },
		error(...args) { console.error("[prfTask]", ...args); },
	};

	const fmtDateTime = (isoStr) => {
		if (!isoStr) return "-";
		const d = new Date(isoStr);
		if (Number.isNaN(d.getTime())) return "-";
		const pad = (n) => String(n).padStart(2, "0");
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};

	const nowSeoul = () => Date.now();

	/**
	 * 상태 계산:
	 * - upcoming: startAt > now
	 * - ongoing: startAt <= now AND (endAt == null OR endAt >= now)
	 * - closed : endAt != null AND endAt < now
	 */
	const resolveStatus = (task) => {
		const now = nowSeoul();
		const start = task.startAt ? new Date(task.startAt).getTime() : null;
		const end = task.endAt ? new Date(task.endAt).getTime() : null;

		if (start && start > now) return "upcoming";
		if (start && start <= now) {
			if (!end || end >= now) return "ongoing";
			if (end < now) return "closed";
		}
		// startAt이 없으면 등록일 기준 ongoing 처리
		return "ongoing";
	};

	const showError = (msg) => {
		if (!$notice) return;
		$notice.textContent = msg;
		$notice.classList.remove("d-none", "alert-success");
		$notice.classList.add("alert-danger");
	};

	const clearError = () => {
		if (!$notice) return;
		$notice.textContent = "";
		$notice.classList.add("d-none");
	};

	const chunk = (arr, page, size) => {
		const start = (page - 1) * size;
		return arr.slice(start, start + size);
	};

	// 작은 HTML escape
	function escapeHtml(s) {
		return String(s)
			.replaceAll("&", "&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll('"', "&quot;")
			.replaceAll("'", "&#39;");
	}

	// 표준 Task DTO로 정규화
	const normalizeIndiv = (r) => ({
		type: "indiv",
		id:   r.indivtaskId,
		name: r.indivtaskName,
		createAt: r.createAt,
		startAt:  r.startAt,
		endAt:    r.endAt,
		hasAttachFile: !!r.hasAttachFile,
		done: r.submittedTask,
		all: r.targetStudentCnt,
	});
	const normalizeGroup = (r) => ({
		type: "group",
		id:   r.grouptaskId,
		name: r.grouptaskName,
		createAt: r.createAt,
		startAt:  r.startAt,
		endAt:    r.endAt,
		hasAttachFile: !!r.hasAttachFile,
		done: r.submittedTask,
		all: r.targetJoCnt,
	});

	// =========================
	// ③ API
	// =========================
	const api = {
		async fetchIndiv() {
			log.group("API.fetchIndiv");
			const url = `${apiBase}/indiv`;
			log.info("GET", url);
			try {
				const res = await fetch(url, { method: "GET", headers: { "Accept": "application/json" } });
				if (!res.ok) throw new Error(`HTTP ${res.status}`);
				const data = await res.json();
				log.info("response items:", data?.length ?? 0);
				log.groupEnd();
				return Array.isArray(data) ? data : [];
			} catch (e) {
				log.error("fetchIndiv failed:", e);
				log.groupEnd();
				throw e;
			}
		},
		async fetchGroup() {
			log.group("API.fetchGroup");
			const url = `${apiBase}/group`;
			log.info("GET", url);
			try {
				const res = await fetch(url, { method: "GET", headers: { "Accept": "application/json" } });
				if (!res.ok) throw new Error(`HTTP ${res.status}`);
				const data = await res.json();
				log.info("response items:", data?.length ?? 0);
				log.groupEnd();
				return Array.isArray(data) ? data : [];
			} catch (e) {
				log.error("fetchGroup failed:", e);
				log.groupEnd();
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
				return `<div class="text-muted small">자료가 없습니다.</div>`;
			}
			const rows = tasks.map((t) => {
				const href =
					t.type === "indiv"
						? `${ctx}/classroom/professor/${lectureId}/task/indiv/${t.id}`
						: `${ctx}/classroom/professor/${lectureId}/task/group/${t.id}`;

				const nameCell = `
          <div class="d-flex align-items-center">
            <span class="me-2 ${t.hasAttachFile ? "bi bi-paperclip" : ""}" aria-hidden="true"></span>
            <a href="${href}" class="text-decoration-none">
              ${escapeHtml(t.name || "")}
            </a>
          </div>`;

				return `
          <tr>
            <td class="align-middle">${nameCell}</td>
            <td class="align-middle text-center">${t.done}/${t.all}</td>
            <td class="align-middle text-center">${fmtDateTime(t.startAt)}</td>
            <td class="align-middle text-center">${fmtDateTime(t.endAt)}</td>
            <td class="align-middle text-center">${fmtDateTime(t.createAt)}</td>
          </tr>`;
			}).join("");

			return `
        <table class="table table-sm table-hover align-middle mb-0">
          <thead>
            <tr class="table-light">
              <th style="width:27%">과제명</th>
              <th class="text-center" style="width:18%">제출율</th>
              <th class="text-center" style="width:18%">시작일</th>
              <th class="text-center" style="width:18%">마감일</th>
              <th class="text-center" style="width:19%">등록일</th>
            </tr>
          </thead>
          <tbody>${rows}</tbody>
        </table>`;
		},

		pagination(totalCount, currentPage, pageSize, onClickName) {
			const totalPages = Math.max(1, Math.ceil(totalCount / pageSize));
			const items = [];
			const mk = (page, label = page, disabled = false, active = false) => {
				const cls = ["page-item"];
				if (disabled) cls.push("disabled");
				if (active) cls.push("active");
				return `
          <li class="${cls.join(" ")}">
            <a class="page-link" href="#" data-page="${page}" data-onclick="${onClickName}">${label}</a>
          </li>`;
			};

			items.push(mk(Math.max(1, currentPage - 1), "«", currentPage === 1, false));
			for (let p = 1; p <= totalPages; p++) items.push(mk(p, String(p), false, p === currentPage));
			items.push(mk(Math.min(totalPages, currentPage + 1), "»", currentPage === totalPages, false));

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

		groupSection(kind, page = 1) {
			const listEl = { all: $groupListAll, upcoming: $groupListUpcoming, ongoing: $groupListOngoing, closed: $groupListClosed }[kind];
			const pgEl   = { all: $groupPgAll,   upcoming: $groupPgUpcoming,   ongoing: $groupPgOngoing,   closed: $groupPgClosed   }[kind];
			if (!listEl || !pgEl) return;

			const data = state.group.filtered[kind] || [];
			const pageData = chunk(data, page, PAGE_SIZE);

			listEl.innerHTML = render.table(pageData);
			pgEl.innerHTML   = render.pagination(data.length, page, PAGE_SIZE, `group-${kind}`);
		},

		indivCounts() {
			if ($indivCntAll)      $indivCntAll.textContent      = state.indiv.filtered.all.length;
			if ($indivCntUpcoming) $indivCntUpcoming.textContent = state.indiv.filtered.upcoming.length;
			if ($indivCntOngoing)  $indivCntOngoing.textContent  = state.indiv.filtered.ongoing.length;
			if ($indivCntClosed)   $indivCntClosed.textContent   = state.indiv.filtered.closed.length;
		},
		groupCounts() {
			if ($groupCntAll)      $groupCntAll.textContent      = state.group.filtered.all.length;
			if ($groupCntUpcoming) $groupCntUpcoming.textContent = state.group.filtered.upcoming.length;
			if ($groupCntOngoing)  $groupCntOngoing.textContent  = state.group.filtered.ongoing.length;
			if ($groupCntClosed)   $groupCntClosed.textContent   = state.group.filtered.closed.length;
		},
	};

	// =========================
	// ⑤ 데이터 로딩 & 필터링
	// =========================
	async function loadIndiv() {
		clearError();
		try {
			log.group("loadIndiv");
			const list = await api.fetchIndiv();
			const normalized = list.map(normalizeIndiv);
			normalized.sort((a, b) => new Date(b.createAt) - new Date(a.createAt));
			state.indiv.raw = normalized;

			const buckets = { all: [], upcoming: [], ongoing: [], closed: [] };
			for (const t of normalized) {
				const st = resolveStatus(t);
				buckets.all.push(t);
				if (st === "upcoming") buckets.upcoming.push(t);
				else if (st === "ongoing") buckets.ongoing.push(t);
				else if (st === "closed") buckets.closed.push(t);
			}
			state.indiv.filtered = buckets;

			render.indivCounts();
			state.indiv.pages = { all: 1, upcoming: 1, ongoing: 1, closed: 1 };
			render.indivSection("all", 1);
			render.indivSection("upcoming", 1);
			render.indivSection("ongoing", 1);
			render.indivSection("closed", 1);

			log.info("Loaded indiv:", { total: normalized.length, counts: {
				all: buckets.all.length, upcoming: buckets.upcoming.length, ongoing: buckets.ongoing.length, closed: buckets.closed.length,
			}});
			log.groupEnd();
		} catch (e) {
			showError("개인과제 목록을 불러오는 중 오류가 발생했습니다.");
			log.error(e);
		}
	}

	async function loadGroup() {
		clearError();
		try {
			log.group("loadGroup");
			const list = await api.fetchGroup();
			const normalized = list.map(normalizeGroup);
			normalized.sort((a, b) => new Date(b.createAt) - new Date(a.createAt));
			state.group.raw = normalized;

			const buckets = { all: [], upcoming: [], ongoing: [], closed: [] };
			for (const t of normalized) {
				const st = resolveStatus(t);
				buckets.all.push(t);
				if (st === "upcoming") buckets.upcoming.push(t);
				else if (st === "ongoing") buckets.ongoing.push(t);
				else if (st === "closed") buckets.closed.push(t);
			}
			state.group.filtered = buckets;

			render.groupCounts();
			state.group.pages = { all: 1, upcoming: 1, ongoing: 1, closed: 1 };
			render.groupSection("all", 1);
			render.groupSection("upcoming", 1);
			render.groupSection("ongoing", 1);
			render.groupSection("closed", 1);

			log.info("Loaded group:", { total: normalized.length, counts: {
				all: buckets.all.length, upcoming: buckets.upcoming.length, ongoing: buckets.ongoing.length, closed: buckets.closed.length,
			}});
			log.groupEnd();
		} catch (e) {
			showError("조별과제 목록을 불러오는 중 오류가 발생했습니다.");
			log.error(e);
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
		const onclickName = a.dataset.onclick; // 'indiv-all' | 'group-all' | ...

		log.group("pagination click");
		log.info("target:", onclickName, "page:", page);

		const [scope, kind] = onclickName.split("-");
		if (scope === "indiv" && ["all", "upcoming", "ongoing", "closed"].includes(kind)) {
			state.indiv.pages[kind] = page;
			render.indivSection(kind, page);
		} else if (scope === "group" && ["all", "upcoming", "ongoing", "closed"].includes(kind)) {
			state.group.pages[kind] = page;
			render.groupSection(kind, page);
		} else {
			log.warn("Unknown pagination scope:", scope, kind);
		}
		log.groupEnd();
	});

	const indivTabs = document.getElementById("indiv-status-tabs");
	if (indivTabs) {
		indivTabs.addEventListener("shown.bs.tab", (e) => {
			const targetId = e.target?.getAttribute("data-bs-target");
			log.info("Indiv Tab shown:", targetId);
		});
	}
	const groupTabs = document.getElementById("group-status-tabs");
	if (groupTabs) {
		groupTabs.addEventListener("shown.bs.tab", (e) => {
			const targetId = e.target?.getAttribute("data-bs-target");
			log.info("Group Tab shown:", targetId);
		});
	}

	// =========================
	// ⑦ 초기화
	// =========================
	(async function init() {
		log.info("Init prfTask.js", { lectureId, apiBase });
		if (!lectureId) {
			showError("lectureId가 비어있습니다.");
			return;
		}
		await Promise.all([
			loadIndiv(),
			loadGroup(),
		]);
	})();
})();
