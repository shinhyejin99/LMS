// /static/js/prfBoard.js
// 교수용 게시판 목록 스크립트 (단일 전체 조회 + 클라이언트 타입 분류)

(function() {
	"use strict";

	// --- DOM refs
	const $root = document.querySelector("#board-root");
	if (!$root) {
		console.warn("[prfBoard] #board-root not found");
		return;
	}

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId || "";
	const apiBase =
		$root.dataset.apiBase || `${ctx}/classroom/api/v1/professor/board`;

	// 탭/리스트 컨테이너
	const $tabs = {
		all: document.querySelector("#tab-all"),
		notice: document.querySelector("#tab-notice"),
		material: document.querySelector("#tab-material"),
	};
	const $lists = {
		all: document.querySelector("#list-all"),
		notice: document.querySelector("#list-notice"),
		material: document.querySelector("#list-material"),
	};

	// 필터/검색/페이지네이션
	const $filterType = document.querySelector("#filter-type");
	const $filterStatus = document.querySelector("#filter-status");
	const $searchInput = document.querySelector("#search-q");
	const $searchBtn = document.querySelector("#btn-search");
	const $pagination = document.querySelector("#pagination");
	const $notfound = document.querySelector("#notfound-box");

	// --- State
	const state = {
		loading: false,
		error: null,
		rawAll: [], // 서버에서 가져온 전체 목록
		byType: {
			"": null, // 전체
			NOTICE: null,
			MATERIAL: null,
		},
		currentType: "", // "", "NOTICE", "MATERIAL" (탭/드롭다운 연동)
		currentStatus: "", // "", "DRAFT", "SCHEDULED", "PUBLISHED"
		q: "", // 검색어
		page: 1,
		size: 10,
		currentTabKey: "all", // all | notice | material
	};

	// --- Utils
	const fmtDateTime = (iso) => {
		if (!iso) return "";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return iso;
		const pad = (n) => String(n).padStart(2, "0");
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};

	const computeStatus = (post) => {
		if (post.tempSaveYn === "Y") return "DRAFT";
		if (post.revealAt) {
			const now = new Date();
			const reveal = new Date(post.revealAt);
			if (reveal.getTime() > now.getTime()) return "SCHEDULED";
		}
		return "PUBLISHED";
	};

	const badgeForType = (t) => {
		if (t === "NOTICE")
			return `<span class="badge bg-warning text-dark border border-warning-subtle">공지</span>`;
		if (t === "MATERIAL")
			return `<span class="badge bg-info text-dark border border-info-subtle">자료</span>`;
		return `<span class="badge bg-secondary text-light">기타</span>`;
	};

	const badgeForStatus = (s) => {
		switch (s) {
			case "DRAFT":
				return `<span class="badge text-bg-secondary">임시</span>`;
			case "SCHEDULED":
				return `<span class="badge text-bg-primary">예약</span>`;
			case "PUBLISHED":
				return `<span class="badge text-bg-success">공개</span>`;
			default:
				return "";
		}
	};

	const showLoading = (container) => {
		container.innerHTML = `<div class="text-muted">로딩 중…</div>`;
	};

	const showNotFound = (msg = "검색 결과가 없습니다.") => {
		$notfound.classList.remove("d-none");
		$notfound.textContent = msg;
	};

	const hideNotFound = () => {
		$notfound.classList.add("d-none");
		$notfound.textContent = "";
	};

	const escapeHtml = (str) =>
		String(str ?? "")
			.replaceAll("&", "&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll('"', "&quot;")
			.replaceAll("'", "&#39;");

	// --- API (전체만 조회)
	const buildApiUrl = () =>
		`${apiBase}/${encodeURIComponent(lectureId)}/post`;

	async function fetchAllOnce() {
		if (Array.isArray(state.rawAll) && state.rawAll.length > 0) return;
		try {
			state.loading = true;
			state.error = null;
			const url = buildApiUrl();
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const data = await res.json();
			const enriched = (data || []).map((p) => ({
				...p,
				__status: computeStatus(p),
			}));
			state.rawAll = enriched;
			state.byType[""] = enriched;
		} catch (e) {
			state.error = e;
			console.error("[prfBoard] fetch error:", e);
		} finally {
			state.loading = false;
		}
	}

	// --- Filter / Paging
	function ensureTypeCache() {
		const t = state.currentType; // "", NOTICE, MATERIAL
		if (t === "") {
			state.byType[""] = state.rawAll;
			return;
		}
		if (state.byType[t] == null) {
			state.byType[t] = state.rawAll.filter((p) => p.postType === t);
		}
	}

	function getSourceForCurrentType() {
		return state.byType[state.currentType] || [];
	}

	function applyFilters(source) {
		let arr = source.slice();

		// 상태
		if (state.currentStatus) {
			arr = arr.filter((p) => p.__status === state.currentStatus);
		}

		// 검색(제목)
		const q = state.q.trim().toLowerCase();
		if (q) {
			arr = arr.filter((p) => (p.title || "").toLowerCase().includes(q));
		}

		return arr;
	}

	function paginate(arr) {
		const total = arr.length;
		const totalPages = Math.max(1, Math.ceil(total / state.size));
		const page = Math.min(Math.max(1, state.page), totalPages);
		const start = (page - 1) * state.size;
		const end = start + state.size;
		return { page, total, totalPages, slice: arr.slice(start, end) };
	}

	// --- Renderers
	function currentListContainer() {
		switch (state.currentTabKey) {
			case "notice":
				return $lists.notice;
			case "material":
				return $lists.material;
			default:
				return $lists.all;
		}
	}

	function renderList(container, items) {
		if (state.loading) {
			showLoading(container);
			return;
		}
		if (state.error) {
			container.innerHTML = `<div class="alert alert-danger">목록을 불러오지 못했습니다. 잠시 후 다시 시도하세요.</div>`;
			return;
		}
		if (!items.length) {
			container.innerHTML = `<div class="text-muted py-3">표시할 게시글이 없습니다.</div>`;
			return;
		}

		const fmtYYMMDD = (iso) => {
			if (!iso) return "";
			const d = new Date(iso);
			if (Number.isNaN(d.getTime())) return "";
			const pad = (n) => String(n).padStart(2, "0");
			const yy = pad(d.getFullYear() % 100);
			const mm = pad(d.getMonth() + 1);
			const dd = pad(d.getDate());
			const hh = pad(d.getHours());
			const mi = pad(d.getMinutes());
			return `${yy}${mm}${dd} ${hh}:${mi}`;
		};

		const rows = items.map((p) => {
			const viewUrl = `${ctx}/classroom/professor/${lectureId}/board/${encodeURIComponent(p.lctPostId)}`;
			const typeBadge = badgeForType(p.postType);
			const statusBadge = badgeForStatus(p.__status);
			const createdStr = fmtYYMMDD(p.createAt);
			const revealStr = p.revealAt ? fmtYYMMDD(p.revealAt) : "";

			return `
      <li class="list-group-item py-2">
        <div class="d-flex justify-content-between align-items-center gap-2">
          <!-- 왼쪽: 배지 + 제목 -->
          <div class="d-flex align-items-center gap-2 flex-wrap">
            ${typeBadge} ${statusBadge}
            <a href="${viewUrl}"
               class="fw-semibold text-decoration-none link-primary text-truncate"
               style="max-width: 48ch;">
              ${escapeHtml(p.title || "(제목 없음)")}
            </a>
          </div>

          <!-- 오른쪽: 시간들 -->
          <div class="text-muted small d-flex align-items-center gap-2 text-nowrap">
            ${revealStr ? `<span>예약공개시간 : ${revealStr}</span>` : ""}
            <span>${createdStr}</span>
          </div>
        </div>
      </li>
    `;
		}).join("");

		container.innerHTML = `
    <ul class="list-group list-group-flush">
      ${rows}
    </ul>
  `;
	}



	function renderPagination(totalPages) {
		const makePageItem = (
			label,
			page,
			disabled = false,
			active = false,
			aria = ""
		) => {
			return `
        <li class="page-item ${disabled ? "disabled" : ""} ${active ? "active" : ""
				}">
          <a class="page-link" href="#" data-page="${page}" ${aria ? `aria-label="${aria}"` : ""
				}>${label}</a>
        </li>`;
		};

		if (totalPages <= 1) {
			$pagination.innerHTML = "";
			return;
		}

		const current = state.page;
		const items = [];
		items.push(makePageItem("&laquo;", current - 1, current === 1, false, "이전"));

		const windowSize = 5;
		let start = Math.max(1, current - Math.floor(windowSize / 2));
		let end = Math.min(totalPages, start + windowSize - 1);
		if (end - start + 1 < windowSize) start = Math.max(1, end - windowSize + 1);

		for (let p = start; p <= end; p++) {
			items.push(makePageItem(String(p), p, false, p === current));
		}
		items.push(
			makePageItem("&raquo;", current + 1, current === totalPages, false, "다음")
		);

		$pagination.innerHTML = items.join("");
	}

	async function refresh() {
		hideNotFound();
		ensureTypeCache();

		const source = getSourceForCurrentType();
		const filtered = applyFilters(source);
		const { page, totalPages, slice } = paginate(filtered);
		state.page = page;

		if (filtered.length === 0) {
			showNotFound("조건에 맞는 게시글이 없습니다.");
		}

		renderList(currentListContainer(), slice);
		renderPagination(totalPages);
	}

	// --- Event handlers
	// 탭 클릭
	$tabs.all?.addEventListener("click", () => {
		state.currentTabKey = "all";
		state.currentType = "";
		syncTypeFilter("");
		state.page = 1;
		refresh();
	});

	$tabs.notice?.addEventListener("click", () => {
		state.currentTabKey = "notice";
		state.currentType = "NOTICE";
		syncTypeFilter("NOTICE");
		state.page = 1;
		refresh();
	});

	$tabs.material?.addEventListener("click", () => {
		state.currentTabKey = "material";
		state.currentType = "MATERIAL";
		syncTypeFilter("MATERIAL");
		state.page = 1;
		refresh();
	});

	function syncTypeFilter(v) {
		if ($filterType) $filterType.value = v;
	}

	// 타입 드롭다운 변경 시 탭 동기화
	$filterType?.addEventListener("change", (e) => {
		const v = e.target.value || "";
		state.currentType = v;
		state.page = 1;
		switch (v) {
			case "NOTICE":
				document.querySelector('[data-bs-target="#pane-notice"]')?.click();
				state.currentTabKey = "notice";
				break;
			case "MATERIAL":
				document.querySelector('[data-bs-target="#pane-material"]')?.click();
				state.currentTabKey = "material";
				break;
			default:
				document.querySelector('[data-bs-target="#pane-all"]')?.click();
				state.currentTabKey = "all";
		}
		refresh();
	});

	// 상태 필터
	$filterStatus?.addEventListener("change", (e) => {
		state.currentStatus = e.target.value || "";
		state.page = 1;
		refresh();
	});

	// 검색(버튼)
	$searchBtn?.addEventListener("click", () => {
		state.q = $searchInput.value || "";
		state.page = 1;
		refresh();
	});

	// 검색(디바운스)
	let typingTimer = null;
	$searchInput?.addEventListener("input", () => {
		clearTimeout(typingTimer);
		typingTimer = setTimeout(() => {
			state.q = $searchInput.value || "";
			state.page = 1;
			refresh();
		}, 250);
	});

	// 페이지네이션 클릭
	$pagination?.addEventListener("click", (e) => {
		const a = e.target.closest("a.page-link");
		if (!a) return;
		e.preventDefault();
		const page = parseInt(a.dataset.page, 10);
		if (!Number.isFinite(page) || page < 1) return;
		state.page = page;
		refresh();
	});

	// --- Init
	(async function init() {
		// 탭별 영역 선로딩 표시
		showLoading($lists.all);
		showLoading($lists.notice);
		showLoading($lists.material);

		// 초기 모드: 전체
		state.currentTabKey = "all";
		state.currentType = "";
		syncTypeFilter("");

		// 전체 1회 조회
		await fetchAllOnce();
		await refresh();
	})();
})();
