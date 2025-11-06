(function() {
	"use strict";

	const $root = document.querySelector("#board-post-root");
	if (!$root) return;

	const ctx = $root.dataset.ctx || "";
	const apiBase = $root.dataset.apiBase || `${ctx}/classroom/api/v1/student/board`;

	const $bcBoard = document.getElementById("bc-board");
	const $btnList = document.getElementById("btn-list");
	const $postBody = document.getElementById("post-body");
	const $errorBox = document.getElementById("error-box");

	const $replyForm = document.getElementById("reply-form");
	const $replyContent = document.getElementById("reply-content");
	const $replySubmit = document.getElementById("reply-submit");
	const $replyList = document.getElementById("reply-list");
	const $replyEmpty = document.getElementById("reply-empty");
	const $replyAlert = document.getElementById("reply-alert");
	const $replySortNewest = document.getElementById("reply-sort-newest");
	const $replySortOldest = document.getElementById("reply-sort-oldest");

	const CURRENT = {
		lectureId: "",
		postId: "",
		boardUrl: "",
		professorInfo: null,
		studentMap: new Map(),
		replyIndex: new Map(),
		replies: [],
		replySortOrder: "desc",
	};

	const setHref = (el, url) => {
		if (el && typeof url === "string" && url) el.href = url;
	};

	const showError = (msg) => {
		if (!$errorBox) return;
		$errorBox.classList.remove("d-none");
		$errorBox.textContent = msg;
	};

	const hideError = () => {
		if (!$errorBox) return;
		$errorBox.classList.add("d-none");
		$errorBox.textContent = "";
	};

	const setReplyAlert = (variant, message) => {
		if (!$replyAlert) return;
		$replyAlert.className = `alert mt-4 alert-${variant}`;
		$replyAlert.textContent = message;
	};

	const clearReplyAlert = () => {
		if (!$replyAlert) return;
		$replyAlert.className = "alert mt-4 d-none";
		$replyAlert.textContent = "";
	};

	const setSortButtonState = () => {
		if (!$replySortNewest || !$replySortOldest) return;
		const applyState = (btn, active) => {
			if (!btn) return;
			btn.classList.remove("btn-primary", "btn-outline-secondary", "active");
			btn.setAttribute("aria-pressed", active ? "true" : "false");
			if (active) {
				btn.classList.add("btn-primary", "active");
			} else {
				btn.classList.add("btn-outline-secondary");
			}
		};
		const isDesc = CURRENT.replySortOrder !== "asc";
		applyState($replySortNewest, isDesc);
		applyState($replySortOldest, !isDesc);
	};

	const pad = (n) => String(n).padStart(2, "0");
	const fmtDateTime = (iso) => {
		if (!iso) return "";
		const d = new Date(iso);
		if (Number.isNaN(d.getTime())) return iso;
		return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
	};

	const escapeHtml = (s) =>
		String(s ?? "")
			.replaceAll("&", "&amp;")
			.replaceAll("<", "&lt;")
			.replaceAll(">", "&gt;")
			.replaceAll('"', "&quot;")
			.replaceAll("'", "&#39;");

	const decodeIfEscaped = (html) => {
		if (!html) return "";
		const looksEscaped = /&lt;|&gt;|&amp;|&quot;|&#39;/.test(html);
		if (!looksEscaped) return html;
		const ta = document.createElement("textarea");
		ta.innerHTML = html;
		return ta.value;
	};

	const sanitizeBasic = (html) => {
		try {
			const parser = new DOMParser();
			const doc = parser.parseFromString(html, "text/html");
			doc.querySelectorAll("script, style, iframe, object, embed, link[rel='import']").forEach((el) => el.remove());
			doc.querySelectorAll("*").forEach((el) => {
				[...el.attributes].forEach((attr) => {
					if (/^on/i.test(attr.name)) el.removeAttribute(attr.name);
				});
			});
			return doc.body.innerHTML;
		} catch (err) {
			console.warn("[stuBoardPost] sanitize failed", err);
			return html;
		}
	};

	const formatReplyHtml = (text) => {
		const escaped = escapeHtml(text || "");
		if (!escaped) return "";
		return escaped.replace(/\r?\n/g, "<br />");
	};

	const formatFileSize = (bytes) => {
		if (!Number.isFinite(bytes) || bytes <= 0) return "";
		const KB = 1024;
		const MB = KB * 1024;
		if (bytes >= MB) {
			const mb = bytes / MB;
			return `${mb >= 10 ? Math.round(mb) : Math.round(mb * 10) / 10} mb`;
		}
		const kb = bytes / KB;
		return `${kb >= 10 ? Math.round(kb) : Math.round(kb * 10) / 10} kb`;
	};

	const formatStudentName = (student) => {
		if (!student) return "";
		const last = student.lastName || "";
		const first = student.firstName || "";
		const fullName = `${last}${first}`.trim();
		return fullName || student.userName || student.userId || "";
	};

	const formatProfessorName = (professor) => {
		if (!professor) return "";
		const last = professor.lastName || "";
		const first = professor.firstName || "";
		const fullName = `${last}${first}`.trim();
		return fullName || professor.userName || professor.professorName || professor.userId || "";
	};

	const formatReplyAuthor = (reply) => {
		if (!reply) return "익명";
		const userId = (reply.userId || "").trim();
		if (
			userId &&
			CURRENT.professorInfo &&
			CURRENT.professorInfo.userId &&
			CURRENT.professorInfo.userId === userId
		) {
			const profName = formatProfessorName(CURRENT.professorInfo);
			return profName ? `${profName} 교수` : "담당 교수";
		}
		if (userId && CURRENT.studentMap instanceof Map && CURRENT.studentMap.has(userId)) {
			const info = CURRENT.studentMap.get(userId);
			const name = formatStudentName(info) || userId;
			const dept = info?.univDeptName || "";
			return dept ? `${name}(${dept})` : name;
		}
		return reply.writerName || reply.authorName || reply.studentName || userId || "익명";
	};

	const getParentReplyId = (reply) => {
		if (!reply) return "";
		if (reply.parentReplyId) return reply.parentReplyId;
		const parent = reply.parentReply || reply.parent;
		if (!parent) return "";
		if (typeof parent === "string") return parent;
		if (typeof parent === "object") {
			return parent.lctReplyId || parent.replyId || parent.id || parent.parentReplyId || "";
		}
		return "";
	};

	const replyTimestamp = (reply) => {
		if (reply?.createAt || reply?.createdAt) {
			const ts = Date.parse(reply.createAt || reply.createdAt);
			if (!Number.isNaN(ts)) return ts;
		}
		const fallback = Number(String(reply?.lctReplyId || reply?.replyId || reply?.id || "").replace(/\D/g, ""));
		return Number.isNaN(fallback) ? 0 : fallback;
	};

	const compareReplies = (a, b) => {
		const diff = replyTimestamp(a) - replyTimestamp(b);
		if (diff !== 0) return diff;
		return String(a?.lctReplyId || a?.replyId || a?.id || "").localeCompare(String(b?.lctReplyId || b?.replyId || b?.id || ""));
	};

	const badgeForType = (type) => {
		if (type === "NOTICE") return `<span class="badge bg-warning text-dark border border-warning-subtle me-2">공지</span>`;
		if (type === "MATERIAL") return `<span class="badge bg-info text-dark border border-info-subtle me-2">자료</span>`;
		return `<span class="badge bg-secondary text-light me-2">기타</span>`;
	};

	const computeStatus = (post) => {
		if (post.tempSaveYn === "Y") return "DRAFT";
		if (post.revealAt) {
			const reveal = new Date(post.revealAt);
			if (!Number.isNaN(reveal.getTime()) && reveal.getTime() > Date.now()) {
				return "SCHEDULED";
			}
		}
		return "PUBLISHED";
	};

	const badgeForStatus = (status) => {
		switch (status) {
			case "DRAFT":
				return `<span class="badge text-bg-secondary">임시저장</span>`;
			case "SCHEDULED":
				return `<span class="badge text-bg-primary">예약</span>`;
			default:
				return `<span class="badge text-bg-success">게시</span>`;
		}
	};

	const renderAttachments = (files = []) => {
		const attachArea = document.getElementById("attach-area");
		const attachList = document.getElementById("attach-list");
		if (!attachArea || !attachList) return;

		if (!files.length) {
			attachArea.classList.add("d-none");
			attachList.innerHTML = "";
			return;
		}

		const items = files.map((file, index) => {
			const fallbackName = `첨부파일 ${file.fileOrder || index + 1}`;
			const baseName =
				file.originName ||
				file.originalFilename ||
				file.originalFileName ||
				file.fileName ||
				file.fileNm ||
				fallbackName;
			const extension = (file.extension || file.fileExtension || "").replace(/^\./, "");
			let displayName = typeof baseName === "string" ? baseName : String(baseName ?? fallbackName);
			if (
				extension &&
				typeof displayName === "string" &&
				!displayName.toLowerCase().endsWith(`.${extension.toLowerCase()}`)
			) {
				displayName = `${displayName}.${extension}`;
			}
			const order = file.fileOrder ?? file.atchFileSn ?? file.fileId ?? file.id ?? index;
			const hrefRaw =
				(typeof file.downloadUrl === "string" && file.downloadUrl) ||
				(typeof file.url === "string" && file.url) ||
				(typeof file.fileUrl === "string" && file.fileUrl) ||
				`${ctx}/classroom/api/v1/common/board/${CURRENT.lectureId}/post/${CURRENT.postId}/attach/${encodeURIComponent(order)}`;
			const sizeLabel = formatFileSize(Number(file.fileSize ?? file.size ?? file.fileLength ?? 0));
			return `<li class="list-group-item d-flex align-items-center gap-3">
				<a class="d-flex align-items-center gap-2 text-decoration-none flex-grow-1" href="${escapeHtml(hrefRaw)}" download>
					<i class="bi bi-paperclip text-secondary"></i>
					<span class="text-truncate">${escapeHtml(displayName)}</span>
				</a>
				${sizeLabel ? `<span class="text-muted small flex-shrink-0">${escapeHtml(sizeLabel)}</span>` : ""}
			</li>`;
		});

		attachArea.classList.remove("d-none");
		attachList.innerHTML = items.join("");
	};

	const renderPost = (post = {}) => {
		if (!$postBody) return;

		const badgeRow = $postBody.querySelector("#post-badge-row");
		if (badgeRow) {
			const status = computeStatus(post);
			badgeRow.innerHTML = `${badgeForType(post.postType)} ${badgeForStatus(status)}`;
		}

		const titleEl = $postBody.querySelector("#post-title");
		if (titleEl) {
			titleEl.classList.remove("skeleton");
			titleEl.textContent = (post.title || post.postTitle || "").trim() || "(제목 없음)";
		}

		const metaRow = $postBody.querySelector("#post-meta-row");
		if (metaRow) {
			const metaParts = [];
			if (post.writerName || post.professorName || post.authorName) {
				metaParts.push(`<div class="meta-item">작성자: <span class="text-body-secondary">${escapeHtml(post.writerName || post.professorName || post.authorName)}</span></div>`);
			}
			metaParts.push(`<div class="meta-item">작성일: <span class="text-body-secondary">${fmtDateTime(post.createAt || post.createdAt)}</span></div>`);
			if (post.revealAt) {
				metaParts.push(`<div class="meta-item">공개 예정: <span class="text-body-secondary">${fmtDateTime(post.revealAt)}</span></div>`);
			}
			if (post.viewCount != null) {
				metaParts.push(`<div class="meta-item">조회수: <span class="text-body-secondary">${post.viewCount}</span></div>`);
			}
			metaRow.innerHTML = metaParts.join("");
		}

		const raw = post.content || post.postContent || "";
		const decoded = decodeIfEscaped(raw);
		const safe = sanitizeBasic(decoded);
		const contentEl = $postBody.querySelector("#post-content");
		if (contentEl) {
			contentEl.innerHTML = safe || '<p class="text-muted">내용이 없습니다.</p>';
		}

		const files = Array.isArray(post.attachFileList) ? post.attachFileList : [];
		renderAttachments(files);
	};

	const buildReplyNode = (reply, depth, childrenMap) => {
		const replyId = String(reply?.lctReplyId || reply?.replyId || reply?.id || "");
		const item = document.createElement("div");
		item.className = "reply-item border rounded px-3 py-2";
		if (replyId) item.dataset.replyId = replyId;

		const userId = (reply?.userId || "").trim();
		const isProfessor =
			userId &&
			CURRENT.professorInfo &&
			CURRENT.professorInfo.userId &&
			CURRENT.professorInfo.userId === userId;
		if (isProfessor) item.classList.add("reply-professor");
		if (depth > 0) {
			item.classList.add("reply-child", "ms-4");
			if (!isProfessor) item.classList.add("bg-light");
		}

		const header = document.createElement("div");
		header.className = "d-flex align-items-center justify-content-between gap-2 flex-wrap";

		const info = document.createElement("div");
		info.className = "d-flex align-items-center gap-2 flex-wrap";

		const name = document.createElement("span");
		name.className = "fw-semibold text-body";
		name.textContent = formatReplyAuthor(reply);
		const time = document.createElement("span");
		time.className = "text-muted small";
		time.textContent = fmtDateTime(reply?.createAt || reply?.createdAt);
		info.appendChild(name);
		info.appendChild(time);

		header.appendChild(info);

		const actionBox = document.createElement("div");
		actionBox.className = "d-flex align-items-center gap-2 flex-shrink-0";

		const deleteBtn = document.createElement("button");
		deleteBtn.type = "button";
		deleteBtn.className = "btn btn-link btn-sm text-danger text-decoration-none px-0";
		deleteBtn.dataset.action = "delete";
		if (replyId) deleteBtn.dataset.replyId = replyId;
		deleteBtn.textContent = "삭제";
		actionBox.appendChild(deleteBtn);

		header.appendChild(actionBox);
		item.appendChild(header);

		const body = document.createElement("div");
		body.className = "reply-body text-break small mt-1";
		const replyHtml = formatReplyHtml(reply?.replyContent || reply?.content || "");
		if (replyHtml) {
			body.innerHTML = replyHtml;
		} else {
			const placeholder = document.createElement("span");
			placeholder.className = "text-muted fst-italic small";
			placeholder.textContent = "내용 없음";
			body.appendChild(placeholder);
		}
		item.appendChild(body);

		const children = (replyId && childrenMap.get(replyId)) || [];
		if (children.length) {
			const childContainer = document.createElement("div");
			childContainer.className = "mt-2 vstack gap-1";
			children.forEach((child) => {
				childContainer.appendChild(buildReplyNode(child, depth + 1, childrenMap));
			});
			item.appendChild(childContainer);
		}

		return item;
	};

	const renderReplies = (replies, options = {}) => {
		if (!$replyList) return;
		const list = Array.isArray(replies) ? [...replies] : [];
		CURRENT.replies = [...list];

		let index = CURRENT.replyIndex;
		if (!(index instanceof Map)) {
			index = new Map();
			CURRENT.replyIndex = index;
		} else {
			index.clear();
		}

		setSortButtonState();

		const childrenMap = new Map();
		const topLevel = [];

		for (const reply of list) {
			if (!reply) continue;
			const replyId = String(reply.lctReplyId || reply.replyId || reply.id || "");
			if (replyId) index.set(replyId, reply);
			const parentIdRaw = getParentReplyId(reply);
			const parentId = parentIdRaw ? String(parentIdRaw) : "";
			if (parentId && parentId !== replyId) {
				if (!childrenMap.has(parentId)) childrenMap.set(parentId, []);
				childrenMap.get(parentId).push(reply);
			} else {
				topLevel.push(reply);
			}
		}

		const danglingParents = [];
		childrenMap.forEach((_, parentId) => {
			if (!index.has(parentId)) danglingParents.push(parentId);
		});
		danglingParents.forEach((parentId) => {
			const orphanChildren = childrenMap.get(parentId) || [];
			orphanChildren.forEach((child) => topLevel.push(child));
			childrenMap.delete(parentId);
		});

		topLevel.sort(compareReplies);
		if (CURRENT.replySortOrder === "desc") topLevel.reverse();
		childrenMap.forEach((children) => children.sort(compareReplies));

		$replyList.innerHTML = "";
		const hasReplies = topLevel.length > 0;

		if ($replyEmpty) {
			if (hasReplies) {
				$replyEmpty.classList.add("d-none");
				$replyEmpty.classList.add("text-muted");
				$replyEmpty.classList.remove("text-danger");
				$replyEmpty.textContent = "댓글이 아직 없습니다. 첫 댓글을 남겨보세요.";
			} else {
				$replyEmpty.classList.remove("d-none");
				if (options.errorMessage) {
					$replyEmpty.classList.remove("text-muted");
					$replyEmpty.classList.add("text-danger");
					$replyEmpty.textContent = options.errorMessage;
				} else {
					$replyEmpty.classList.add("text-muted");
					$replyEmpty.classList.remove("text-danger");
					$replyEmpty.textContent = "댓글이 아직 없습니다. 첫 댓글을 남겨보세요.";
				}
			}
		}

		if (!hasReplies) return;

		const frag = document.createDocumentFragment();
		topLevel.forEach((reply) => {
			frag.appendChild(buildReplyNode(reply, 0, childrenMap));
		});

		$replyList.appendChild(frag);
	};

	const fetchPost = async () => {
		if (!CURRENT.lectureId || !CURRENT.postId) return;
		const url = `${apiBase}/${encodeURIComponent(CURRENT.lectureId)}/post/${encodeURIComponent(CURRENT.postId)}`;
		try {
			hideError();
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const post = await res.json();
			renderPost(post || {});
		} catch (err) {
			console.error("[stuBoardPost] load post error", err);
			showError("게시글을 불러오는 중 문제가 발생했습니다.");
		}
	};

	const loadReplies = async () => {
		if (!CURRENT.lectureId || !CURRENT.postId) {
			renderReplies([]);
			return [];
		}
		const url = `${apiBase}/${encodeURIComponent(CURRENT.lectureId)}/post/${encodeURIComponent(CURRENT.postId)}/reply`;
		try {
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const data = await res.json();
			const replies = Array.isArray(data) ? data : [];
			renderReplies(replies);
			return replies;
		} catch (err) {
			console.error("[stuBoardPost] load replies error", err);
			renderReplies([], { errorMessage: "댓글을 불러오는 중 문제가 발생했습니다." });
			return [];
		}
	};

	const handleReplySubmit = async (event) => {
		event?.preventDefault();
		if (!$replyContent) return;

		const content = ($replyContent.value || "").trim();
		if (!content) {
			setReplyAlert("warning", "댓글 내용을 입력해 주세요.");
			$replyContent.focus();
			return;
		}

		if (!CURRENT.lectureId || !CURRENT.postId) {
			setReplyAlert("danger", "강의 정보가 올바르지 않습니다.");
			return;
		}

		const payload = { replyContent: content };
		const url = `${apiBase}/${encodeURIComponent(CURRENT.lectureId)}/post/${encodeURIComponent(CURRENT.postId)}/reply`;

		const prevLabel = $replySubmit?.textContent;
		if ($replySubmit) {
			$replySubmit.disabled = true;
			$replySubmit.textContent = "등록 중...";
		}

		try {
			const res = await fetch(url, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
					Accept: "application/json",
				},
				credentials: "same-origin",
				body: JSON.stringify(payload),
			});

			if (!res.ok) {
				const body = await res.text();
				console.warn("[stuBoardPost] reply error", res.status, body);
				throw new Error(`HTTP ${res.status}`);
			}

			setReplyAlert("success", "댓글이 등록되었습니다.");
			$replyForm?.reset();
			await loadReplies();
		} catch (err) {
			console.error("[stuBoardPost] reply submit error", err);
			setReplyAlert("danger", "댓글 등록에 실패했습니다.");
		} finally {
			if ($replySubmit) {
				$replySubmit.disabled = false;
				$replySubmit.textContent = prevLabel || "등록";
			}
		}
	};

	const handleReplyDelete = async (replyId, trigger) => {
		if (!replyId) return;
		if (!CURRENT.lectureId || !CURRENT.postId) {
			setReplyAlert("danger", "강의 정보가 올바르지 않습니다.");
			return;
		}

		const confirmed = window.confirm("댓글을 삭제하시겠습니까?");
		if (!confirmed) return;

		const url = `${apiBase}/${encodeURIComponent(CURRENT.lectureId)}/post/${encodeURIComponent(CURRENT.postId)}/reply/${encodeURIComponent(replyId)}`;
		try {
			if (trigger) trigger.disabled = true;
			const res = await fetch(url, {
				method: "DELETE",
				headers: { Accept: "application/json" },
				credentials: "same-origin",
			});
			if (!res.ok) {
				const body = await res.text();
				console.warn("[stuBoardPost] delete error", res.status, body);
				throw new Error(`HTTP ${res.status}`);
			}
			setReplyAlert("info", "댓글이 삭제되었습니다.");
			await loadReplies();
		} catch (err) {
			console.error("[stuBoardPost] delete reply error", err);
			setReplyAlert("danger", "댓글 삭제를 완료할 수 없습니다.");
		} finally {
			if (trigger) trigger.disabled = false;
		}
	};

	const handleReplyListClick = (event) => {
		const btn = event.target.closest("[data-action='delete']");
		if (!btn || !$replyList?.contains(btn)) return;
		const replyId = btn.dataset.replyId;
		handleReplyDelete(replyId, btn);
	};

	const parseIdsFromPath = () => {
		const segs = window.location.pathname.split("/").filter(Boolean);
		const idx = segs.findIndex((s) => s === "student");
		if (idx >= 0 && segs[idx + 2] === "board") {
			const lectureId = decodeURIComponent(segs[idx + 1] || "");
			const postId = decodeURIComponent(segs[idx + 3] || "");
			return { lectureId, postId };
		}
		const url = new URL(window.location.href);
		return {
			lectureId: url.searchParams.get("lectureId") || "",
			postId: url.searchParams.get("postId") || "",
		};
	};

	const linkNavigation = (lectureId, postId) => {
		const boardUrl = `${ctx}/classroom/student/${encodeURIComponent(lectureId)}/board`;
		CURRENT.boardUrl = boardUrl;
		setHref($bcBoard, boardUrl);
		setHref($btnList, boardUrl);
		return boardUrl;
	};

	const loadProfessorInfo = async (lectureId) => {
		if (!lectureId) {
			CURRENT.professorInfo = null;
			return null;
		}
		const encodedId = encodeURIComponent(lectureId);
		const url = `${ctx}/classroom/api/v1/common/${encodedId}/professor`;
		try {
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const data = await res.json();
			CURRENT.professorInfo = data || null;
			return data;
		} catch (err) {
			console.warn("[stuBoardPost] professor info load failed", err);
			CURRENT.professorInfo = null;
			return null;
		}
	};

	const loadStudentRoster = async (lectureId) => {
		const map = new Map();
		if (!lectureId) {
			CURRENT.studentMap = map;
			return map;
		}
		const encodedId = encodeURIComponent(lectureId);
		const url = `${ctx}/classroom/api/v1/student/${encodedId}/students`;
		try {
			const res = await fetch(url, { headers: { Accept: "application/json" } });
			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			const data = await res.json();
			(Array.isArray(data) ? data : []).forEach((student) => {
				if (!student) return;
				const userId = (student.userId || "").trim();
				if (!userId) return;
				map.set(userId, student);
			});
		} catch (err) {
			console.warn("[stuBoardPost] student roster load failed", err);
		}
		CURRENT.studentMap = map;
		return map;
	};

	const init = async () => {
		const { lectureId, postId } = parseIdsFromPath();
		if (!lectureId || !postId) {
			showError("잘못된 접근입니다. (lectureId/postId)");
			return;
		}

		CURRENT.lectureId = lectureId;
		CURRENT.postId = postId;

		const boardUrl = linkNavigation(lectureId, postId);

		const professorPromise = loadProfessorInfo(lectureId);
		const rosterPromise = loadStudentRoster(lectureId);

		setSortButtonState();
		if ($replySortNewest) {
			$replySortNewest.addEventListener("click", () => {
				if (CURRENT.replySortOrder === "desc") return;
				CURRENT.replySortOrder = "desc";
				setSortButtonState();
				renderReplies(CURRENT.replies);
			});
		}
		if ($replySortOldest) {
			$replySortOldest.addEventListener("click", () => {
				if (CURRENT.replySortOrder === "asc") return;
				CURRENT.replySortOrder = "asc";
				setSortButtonState();
				renderReplies(CURRENT.replies);
			});
		}

		await fetchPost();
		await Promise.all([professorPromise, rosterPromise]);
		await loadReplies();

		if ($replyForm) {
			$replyForm.addEventListener("submit", handleReplySubmit);
			$replyForm.addEventListener("reset", () => {
				clearReplyAlert();
				$replyContent?.focus();
			});
		}

		if ($replyList) {
			$replyList.addEventListener("click", handleReplyListClick);
		}

		if (boardUrl) {
			setHref($btnList, boardUrl);
			setHref($bcBoard, boardUrl);
		}
	};

	if (document.readyState === "loading") {
		document.addEventListener("DOMContentLoaded", init);
	} else {
		init();
	}
})();
