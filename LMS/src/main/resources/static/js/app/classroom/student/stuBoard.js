(function() {
	"use strict";

	const $root = document.querySelector("#board-root");
	if (!$root) {
		console.warn("[stuBoard] #board-root not found");
		return;
	}

	const ctx = $root.dataset.ctx || "";
	const lectureId = $root.dataset.lectureId || "";
	const apiBase =
		$root.dataset.apiBase || `${ctx}/classroom/api/v1/student/board`;

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

	const $filterType = document.querySelector("#filter-type");
	const $searchInput = document.querySelector("#search-q");
	const $searchBtn = document.querySelector("#btn-search");
	const $pagination = document.querySelector("#pagination");
	const $notfound = document.querySelector("#notfound-box");

	const state = {
		loading: false,
		error: null,
		rawAll: [],
		byType: {
			"": null,
			NOTICE: null,
			MATERIAL: null,
		},
		currentType: "",
		q: "",
		page: 1,
		size: 10,
		currentTabKey: "all",
	};

	const fmtDateTime = (iso) => {
		if (!iso) return "";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return iso;
		const pad = (n) => String(n).padStart(2, "0");
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};

	const fmtShort = (iso) => {
		if (!iso) return "";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return "";
		const pad = (n) => String(n).padStart(2, "0");
		return `${String(d.getFullYear()).slice(-2)}${pad(d.getMonth() + 1)}${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};

	const badgeForType = (t) => {
		if (t === "NOTICE")
			return `<span class="badge bg-warning text-dark border border-warning-subtle">공지</span>`;
		if (t === "MATERIAL")
			return `<span class="badge bg-info text-dark border border-info-subtle">자료</span>`;
		return `<span class="badge text-bg-secondary">기타</span>`;
	};

	const showLoading = (container) => {
		if (container) container.innerHTML = `<div class="text-muted">로딩 중...</div>`;
	};

	const showNotFound = (msg = "조건에 맞는 게시글이 없습니다.") => {
		if (!$notfound) return;
		$notfound.classList.remove("d-none");
		$notfound.textContent = msg;
	};

	const hideNotFound = () => {
		if (!$notfound) return;
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
			const normalized = Array.isArray(data) ? data : [];
			state.rawAll = normalized;
			state.byType[""] = normalized;
		} catch (e) {
			state.error = e;
			console.error("[stuBoard] fetch error:", e);
		} finally {
			state.loading = false;
		}
	}

	function ensureTypeCache() {
		const t = state.currentType;
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

		if (state.q) {
			const q = state.q.toLowerCase();
			arr = arr.filter((p) => {
				const title = String(p.title || "").toLowerCase();
				const content = String(p.content || p.postContent || "").toLowerCase();
				return title.includes(q) || content.includes(q);
			});
		}

		arr.sort((a, b) => {
			const aTime = new Date(a.createAt || a.createdAt || 0).getTime();
			const bTime = new Date(b.createAt || b.createdAt || 0).getTime();
			return bTime - aTime;
		});

		return arr;
	}

	function paginate(list) {
		const size = state.size;
		const totalItems = list.length;
		const totalPages = Math.max(1, Math.ceil(totalItems / size));
		let page = Math.min(Math.max(1, state.page), totalPages);
		const start = (page - 1) * size;
		const end = start + size;
		return { slice: list.slice(start, end), page, totalPages };
	}

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
		if (!container) return;
		if (state.loading) {
			showLoading(container);
			return;
		}
		if (state.error) {
			container.innerHTML = `<div class="alert alert-danger">게시글을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.</div>`;
			return;
		}
		if (!items.length) {
			container.innerHTML = `<div class="text-muted py-3">표시할 게시글이 없습니다.</div>`;
			return;
		}

		const rows = items.map((p) => {
			const postId = p.lctPostId || p.postId || p.id || "";
			const viewUrl = `${ctx}/classroom/student/${lectureId}/board/${encodeURIComponent(postId)}`;
			const typeBadge = badgeForType(p.postType);
			const createdStr = fmtShort(p.createAt || p.createdAt);
			const revealStr = fmtShort(p.revealAt);

			return `
      <li class="list-group-item py-2">
        <div class="d-flex justify-content-between align-items-center gap-2">
          <div class="d-flex align-items-center gap-2 flex-wrap">
            ${typeBadge}
            <a href="${viewUrl}"
               class="fw-semibold text-decoration-none link-primary text-truncate"
               style="max-width: 48ch;">
              ${escapeHtml(p.title || "(제목 없음)")}
            </a>
          </div>
          <div class="text-muted small d-flex align-items-center gap-2 text-nowrap">
            ${revealStr ? `<span>공개예정 : ${revealStr}</span>` : ""}
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
		if (!$pagination) return;

		const makePageItem = (label, page, disabled = false, active = false, aria = "") => `
      <li class="page-item ${disabled ? "disabled" : ""} ${active ? "active" : ""}">
        <a class="page-link" href="#" data-page="${page}" ${aria ? `aria-label="${aria}"` : ""}>${label}</a>
      </li>`;

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

		items.push(makePageItem("&raquo;", current + 1, current === totalPages, false, "다음"));

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

	$tabs.all?.addEventListener("click", () => {
		state.currentTabKey = "all";
		state.currentType = "";
		if ($filterType) $filterType.value = "";
		state.page = 1;
		refresh();
	});

	$tabs.notice?.addEventListener("click", () => {
		state.currentTabKey = "notice";
		state.currentType = "NOTICE";
		if ($filterType) $filterType.value = "NOTICE";
		state.page = 1;
		refresh();
	});

	$tabs.material?.addEventListener("click", () => {
		state.currentTabKey = "material";
		state.currentType = "MATERIAL";
		if ($filterType) $filterType.value = "MATERIAL";
		state.page = 1;
		refresh();
	});

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

	$searchBtn?.addEventListener("click", () => {
		state.q = $searchInput?.value || "";
		state.page = 1;
		refresh();
	});

	let typingTimer = null;
	$searchInput?.addEventListener("input", () => {
		clearTimeout(typingTimer);
		typingTimer = setTimeout(() => {
			state.q = $searchInput.value || "";
			state.page = 1;
			refresh();
		}, 250);
	});

	$pagination?.addEventListener("click", (e) => {
		const a = e.target.closest("a.page-link");
		if (!a) return;
		e.preventDefault();
		const page = parseInt(a.dataset.page, 10);
		if (!Number.isFinite(page) || page < 1) return;
		state.page = page;
		refresh();
	});

	(async function init() {
		showLoading($lists.all);
		showLoading($lists.notice);
		showLoading($lists.material);

		state.currentTabKey = "all";
		state.currentType = "";
		if ($filterType) $filterType.value = "";

		await fetchAllOnce();
		await refresh();
	})();
})();
