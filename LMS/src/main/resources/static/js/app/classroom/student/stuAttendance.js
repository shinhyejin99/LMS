(() => {
    "use strict";

    const root = document.getElementById("attendance-root");
    if (!root) return;

    const ctx = root.dataset.ctx || "";
    const lectureId = (root.dataset.lectureId || "").trim();
    if (!lectureId) return;

    const studentApiBase = root.dataset.studentApi || `${ctx}/classroom/api/v1/student`;

    const $ = (selector) => root.querySelector(selector);

    const alertBox = $("#attendance-alert");
    const attendanceBody = $("#attendance-body");
    const attendanceBadge = $("#attendance-total-badge");

    const STATUS_BADGE_CLASS = {
        ATTD_OK: "bg-success-subtle text-success border border-success-subtle",
        ATTD_LATE: "bg-warning-subtle text-warning border border-warning-subtle",
        ATTD_NO: "bg-danger-subtle text-danger border border-danger-subtle",
        ATTD_EARLY: "bg-info-subtle text-info border border-info-subtle",
        ATTD_EXCP: "bg-primary-subtle text-primary-emphasis border border-primary-subtle",
        ATTD_TBD: "bg-secondary-subtle text-secondary border border-secondary-subtle"
    };

    function showAlert(message) {
        if (!alertBox) return;
        alertBox.classList.remove("d-none");
        alertBox.textContent = message || "출결 정보를 불러오는 중 오류가 발생했습니다.";
    }

    async function fetchJSON(url) {
        const res = await fetch(url, { headers: { Accept: "application/json" } });
        if (res.status === 204) return [];
        if (!res.ok) {
            const text = await res.text().catch(() => "");
            throw new Error(`HTTP ${res.status} ${res.statusText} - ${text}`);
        }
        return res.json();
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }
    
    function formatMultiline(value, fallback = "-") {
        if (!value) return fallback;
        return escapeHtml(value).replace(/\r?\n/g, "<br>");
    }

    function formatDateOnly(value) {
        if (!value) return "-";
        if (typeof value === "string" && /^\d{4}-\d{2}-\d{2}$/.test(value)) return value;
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return "-";
        const y = date.getFullYear();
        const m = String(date.getMonth() + 1).padStart(2, "0");
        const d = String(date.getDate()).padStart(2, "0");
        return `${y}-${m}-${d}`;
    }

    function formatTimeOnly(value) {
        if (!value) return "-";
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) return "-";
        const h = String(date.getHours()).padStart(2, "0");
        const m = String(date.getMinutes()).padStart(2, "0");
        return `${h}:${m}`;
    }

    function renderAttendance(records) {
        if (!attendanceBody || !attendanceBadge) return;
        const list = Array.isArray(records) ? records.slice() : [];
        list.sort((a, b) => (a.lctPrintRound ?? a.lctRound ?? 0) - (b.lctPrintRound ?? b.lctRound ?? 0));

        attendanceBadge.textContent = `총 ${list.length}회`;

        if (list.length === 0) {
            attendanceBody.innerHTML = '<div class="text-center text-muted py-5">출결 정보가 아직 없습니다.</div>';
            return;
        }

        const recordedList = list.filter((item) => item.record);
        
        const validAttendanceCount = recordedList.filter(item => 
			item.attStatusCd && 
			item.attStatusCd.toUpperCase() !== 'ATTD_NO'
		).length;

        const statusMap = new Map();
        recordedList.forEach((item) => {
            const key = item.attStatusName || "기록 없음";
            statusMap.set(key, (statusMap.get(key) || 0) + 1);
        });

        const summaryChips = [
            `<li class="list-inline-item">
                <div class="border rounded-3 px-3 py-2 text-center">
                    <div class="text-muted small mb-1">출석률</div>
                    <div class="fw-semibold">${validAttendanceCount} / ${recordedList.length}</div>
                </div>
            </li>`
        ].concat(
            Array.from(statusMap.entries()).map(([name, count]) => `
                <li class="list-inline-item">
                    <div class="border rounded-3 px-3 py-2 text-center">
                        <div class="text-muted small mb-1">${escapeHtml(name)}</div>
                        <div class="fw-semibold">${count}</div>
                    </div>
                </li>
            `)
        );

        const rows = list.map((item) => {
            const round = item.lctPrintRound ?? item.lctRound ?? "-";
            const day = formatDateOnly(item.attDay);
            const recordedAt = item.record ? formatTimeOnly(item.attAt) : "-";
            const statusName = escapeHtml(item.attStatusName || (item.record ? "출결 처리" : "미기록"));
            const statusCd = String(item.attStatusCd || "").toUpperCase();
            const badgeClass = STATUS_BADGE_CLASS[statusCd] || "bg-secondary-subtle text-secondary border border-secondary-subtle";
            const comment = item.attComment ? formatMultiline(item.attComment, "") : "";

            return `
                <tr>
                    <td class="text-center">${escapeHtml(round)}</td>
                    <td class="text-center">${escapeHtml(day)}</td>
                    <td class="text-center">${escapeHtml(recordedAt)}</td>
                    <td class="text-center"><span class="badge ${badgeClass}">${statusName}</span></td>
                    <td>${comment || "-"}</td>
                </tr>
            `;
        }).join("");

        attendanceBody.innerHTML = `
            <div>
                <ul class="list-inline mb-4">${summaryChips.join("")}</ul>
                <div class="table-responsive">
                    <table class="table table-sm table-striped align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="text-center" style="width:4.5rem;">회차</th>
                                <th class="text-center" style="width:6.5rem;">일자</th>
                                <th class="text-center" style="width:6.5rem;">기록시간</th>
                                <th class="text-center" style="width:6.5rem;">상태</th>
                                <th>비고</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            </div>
        `;
    }

    async function init() {
        try {
            const attendance = await fetchJSON(`${studentApiBase}/${encodeURIComponent(lectureId)}/attendance`);
            renderAttendance(attendance);
        } catch (err) {
            console.error("[stuAttendance] init error", err);
            showAlert(err?.message);
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init, { once: true });
    } else {
        init();
    }
})();
