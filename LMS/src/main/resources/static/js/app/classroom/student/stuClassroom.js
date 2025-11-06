(() => {
    "use strict";

    const root = document.getElementById("stu-lecture-root");
    if (!root) return;

    const ctx = root.dataset.ctx || "";
    const lectureId = (root.dataset.lectureId || "").trim();
    if (!lectureId) return;

    const commonApiBase = root.dataset.commonApi || `${ctx}/classroom/api/v1/common`;
    const studentApiBase = root.dataset.studentApi || `${ctx}/classroom/api/v1/student`;

    const $ = (selector) => root.querySelector(selector);

    const alertBox = $("#stu-lecture-alert");
    const titleEl = $("#stu-lecture-title");
    const subtitleEl = $("#stu-lecture-subtitle");

    const planAccordion = $("#plan-accordion");
    const planEmpty = $("#plan-empty");

    const overviewBody = $("#overview-body");
    const scheduleBody = $("#schedule-body");

    const classmateSummary = $("#classmate-summary");
    const classmateBadge = $("#classmate-total-badge");
    const classmateModalBody = document.querySelector("#classmate-modal-body");
    const classmateButton = document.getElementById("open-classmate-modal-btn");

    const DAY_LABEL = { MO: "월", TU: "화", WE: "수", TH: "목", FR: "금", SA: "토", SU: "일" };
    const TERM_LABEL = { REG1: "1학기", REG2: "2학기", SUB1: "하계계절학기", SUB2: "동계계절학기" };
    
    let planData = [];

    function showAlert(message) {
        if (!alertBox) return;
        alertBox.classList.remove("d-none");
        alertBox.textContent = message || "강의 정보를 불러오는 중 오류가 발생했습니다.";
    }

    function clearAlert() {
        if (!alertBox) return;
        alertBox.classList.add("d-none");
        alertBox.textContent = "";
    }

    async function fetchJSON(url) {
        const res = await fetch(url, { headers: { Accept: "application/json" } });
        if (res.status === 404) return { __notFound: true };
        if (res.status === 204) return [];
        if (!res.ok) {
            const text = await res.text().catch(() => "");
            throw new Error(`HTTP ${res.status} ${res.statusText} - ${text}`);
        }
        const contentType = res.headers.get("content-type") || "";
        if (contentType.includes("application/json")) {
            return res.json();
        }
        const text = await res.text();
        try {
            return JSON.parse(text);
        } catch (err) {
            return text;
        }
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function formatMultiline(value, fallback = "등록된 내용이 없습니다.") {
        if (!value) return fallback;
        return escapeHtml(value).replace(/\r?\n/g, "<br>");
    }

    function formatYearTerm(code) {
        if (!code) return "학기 정보 없음";
        const [year, termCd] = String(code).split("_");
        const label = TERM_LABEL[termCd] || termCd || "";
        return label ? `${year}년 ${label}` : `${year}년`;
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

    function fmtTime(hhmm) {
        if (!hhmm || String(hhmm).length < 4) return "--:--";
        const value = String(hhmm);
        return `${value.slice(0, 2)}:${value.slice(2, 4)}`;
    }

    function safeParseSchedule(raw) {
        if (!raw) return [];
        if (Array.isArray(raw)) return raw;
        if (typeof raw === "string") {
            try {
                const parsed = JSON.parse(raw);
                return Array.isArray(parsed) ? parsed : [];
            } catch (err) {
                console.warn("[stuClassroom] schedule parse error", err);
            }
        }
        return [];
    }

    function renderPlanAccordion() {
        if (!planAccordion) return;

        if (!Array.isArray(planData) || planData.length === 0) {
            planAccordion.innerHTML = `
                <div class="text-body-secondary small">등록된 강의 계획이 없습니다.</div>
            `;
            if (planEmpty) planEmpty.classList.remove("d-none");
            return;
        }

        planData.sort((a, b) => (a.lectureWeek ?? 0) - (b.lectureWeek ?? 0));
        if (planEmpty) planEmpty.classList.add("d-none");

        const accordionItems = planData.map((plan, index) => {
            const headingId = `plan-week-${plan.lectureWeek}-heading`;
            const collapseId = `plan-week-${plan.lectureWeek}-body`;
            const isFirst = index === 0;
            const expandedAttr = isFirst ? "true" : "false";
            const collapsedClass = isFirst ? "" : " collapsed";
            const showClass = isFirst ? " show" : "";

            return `
                <div class="accordion-item">
                    <h2 class="accordion-header" id="${headingId}">
                        <button class="accordion-button${collapsedClass}" type="button"
                                data-bs-toggle="collapse" data-bs-target="#${collapseId}"
                                aria-expanded="${expandedAttr}" aria-controls="${collapseId}">
                            ${escapeHtml(`${plan.lectureWeek}주차`)}
                        </button>
                    </h2>
                    <div id="${collapseId}" class="accordion-collapse collapse${showClass}"
                         aria-labelledby="${headingId}" data-bs-parent="#plan-accordion">
                        <div class="accordion-body">
                            <div class="mb-3">
                                <div class="fw-semibold mb-1">학습 목표</div>
                                <p class="mb-0">${formatMultiline(plan.weekGoal)}</p>
                            </div>
                            <div>
                                <div class="fw-semibold mb-1">학습 내용</div>
                                <p class="mb-0">${formatMultiline(plan.weekDesc)}</p>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }).join("");

        planAccordion.innerHTML = accordionItems;
    }

    function renderOverview(info) {
        if (!overviewBody) return;
        if (!info || info.__notFound) {
            overviewBody.innerHTML = '<div class="text-body-secondary">강의 개요 정보를 확인할 수 없습니다.</div>';
            return;
        }

        const subjectName = escapeHtml(info.subjectName || "강의명 미정");
        const professorName = escapeHtml(info.professorName || "담당 교수 미등록");
        const termLabel = escapeHtml(formatYearTerm(info.yeartermCd));
        const goalHtml = formatMultiline(info.lectureGoal, "등록된 강의 목표가 없습니다.");
        const prereq = escapeHtml(info.prereqSubject || "해당 없음");
        const lectureIndex = escapeHtml(info.lectureIndex || "-");
        const capacity = Number.isFinite(Number(info.maxCap)) ? `${info.maxCap}명` : "정보 없음";
        const endDate = info.endAt ? formatDateOnly(info.endAt) : "진행 중";

        if (titleEl) titleEl.textContent = info.subjectName || "강의 홈";
        if (subtitleEl) subtitleEl.textContent = `${info.professorName || "담당 교수 미등록"} · ${formatYearTerm(info.yeartermCd)}`;

        const professorNo = info.professorNo ? encodeURIComponent(String(info.professorNo).trim()) : "";
        const photoUrl = professorNo ? `${commonApiBase}/photo/professor/${professorNo}` : "";
        const photoAlt = `${professorName} 교수 사진`;

        const photoBlock = photoUrl
            ? `<img src="${photoUrl}" alt="${photoAlt}" class="rounded-3 border bg-light flex-shrink-0" style="width:96px;height:128px;object-fit:cover;">`
            : `<div class="rounded-3 border bg-light d-flex align-items-center justify-content-center flex-shrink-0" style="width:96px;height:128px;"><span class="text-muted small">사진 없음</span></div>`;

        overviewBody.innerHTML = `
            <div class="d-flex gap-3">
                ${photoBlock}
                <div class="flex-grow-1">
                    <div class="text-muted small mb-1">${termLabel}</div>
                    <div class="fw-semibold">${subjectName}</div>
                    <div class="small text-body-secondary">담당 교수: ${professorName}</div>
                    <div class="small text-body-secondary">강의 번호: ${lectureIndex}</div>
                </div>
            </div>
            <dl class="row row-cols-1 g-3 small mt-3 mb-0">
                <div class="col">
                    <dt class="text-muted">강의 목표</dt>
                    <dd class="mb-0">${goalHtml}</dd>
                </div>
                <div class="col">
                    <dt class="text-muted">선수 과목</dt>
                    <dd class="mb-0">${prereq}</dd>
                </div>
                <div class="col">
                    <dt class="text-muted">정원</dt>
                    <dd class="mb-0">${capacity}</dd>
                </div>
                <div class="col">
                    <dt class="text-muted">종료 예정일</dt>
                    <dd class="mb-0">${escapeHtml(endDate)}</dd>
                </div>
            </dl>
        `;
    }

    function renderSchedule(scheduleRaw) {
        if (!scheduleBody) return;
        const schedule = safeParseSchedule(scheduleRaw);
        if (!Array.isArray(schedule) || schedule.length === 0) {
            scheduleBody.innerHTML = '<div class="text-body-secondary">등록된 시간표가 없습니다.</div>';
            return;
        }

        const rows = [];
        schedule.forEach((item) => {
            const place = escapeHtml(item.placeName || item.placeCd || "-");
            const slots = Array.isArray(item.slots) && item.slots.length ? item.slots : [{}];
            slots.forEach((slot) => {
                const day = DAY_LABEL[slot.day] || slot.day || "-";
                const time = slot.start ? `${fmtTime(slot.start)} ~ ${fmtTime(slot.end)}` : "-";
                rows.push(`
                    <tr>
                        <td class="text-center">${escapeHtml(day)}</td>
                        <td class="text-center">${escapeHtml(time)}</td>
                        <td>${place}</td>
                    </tr>
                `);
            });
        });

        scheduleBody.innerHTML = `
            <div class="table-responsive">
                <table class="table table-sm table-striped align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="text-center" style="width:5.5rem;">요일</th>
                            <th class="text-center" style="width:9rem;">시간</th>
                            <th>강의실</th>
                        </tr>
                    </thead>
                    <tbody>${rows.join("")}</tbody>
                </table>
            </div>
        `;
    }

    function renderClassmates(list) {
        if (!classmateSummary || !classmateBadge || !classmateModalBody) return;
        const classmates = Array.isArray(list) ? list.slice() : [];
        classmateBadge.textContent = `${classmates.length}명`;

        if (classmateButton) {
            classmateButton.disabled = classmates.length === 0;
        }

        if (classmates.length === 0) {
            classmateSummary.textContent = "같이 듣는 학우 정보를 확인할 수 없습니다.";
            classmateModalBody.innerHTML = '<div class="text-body-secondary">같이 듣는 학우가 아직 없습니다.</div>';
            return;
        }

        classmates.sort((a, b) => {
            const gradeA = a.gradeCd || "";
            const gradeB = b.gradeCd || "";
            if (gradeA !== gradeB) return gradeA.localeCompare(gradeB);
            const nameA = `${a.lastName || ""}${a.firstName || ""}`;
            const nameB = `${b.lastName || ""}${b.firstName || ""}`;
            return nameA.localeCompare(nameB, "ko");
        });

        const summaryNames = classmates.slice(0, 3).map((mate) => {
            const fullName = `${mate.lastName || ""}${mate.firstName || ""}` || mate.firstName || "-";
            return escapeHtml(fullName);
        });
        const extraCount = classmates.length > 3 ? ` 외 ${classmates.length - 3}명` : "";
        classmateSummary.textContent = summaryNames.length
            ? `${summaryNames.join(", ")}${extraCount}`
            : "같이 듣는 학우 정보를 확인할 수 없습니다.";

        const rows = classmates.map((mate) => {
            const fullName = `${mate.lastName || ""}${mate.firstName || ""}` || mate.firstName || "-";
            const gradeName = mate.gradeName
                ? `<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle ms-2">${escapeHtml(mate.gradeName)}</span>`
                : "";
            const deptName = escapeHtml(mate.univDeptName || "-");
            const status = escapeHtml(mate.enrollStatusCd || "-");

            return `
                <tr>
                    <td class="text-center">${escapeHtml(mate.studentNo || "-")}</td>
                    <td>${escapeHtml(fullName)}${gradeName}</td>
                    <td>${deptName}</td>
                    <td class="text-center">${status}</td>
                </tr>
            `;
        }).join("");

        classmateModalBody.innerHTML = `
            <div class="table-responsive">
                <table class="table table-sm table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr>
                            <th class="text-center" style="width:7rem;">학번</th>
                            <th style="width:10rem;">이름</th>
                            <th>학과</th>
                            <th class="text-center" style="width:6rem;">상태</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    }

    async function init() {
        try {
            clearAlert();
            const encodedId = encodeURIComponent(lectureId);

            const [
                lectureInfo,
                plan,
                schedule,
                classmates
            ] = await Promise.all([
                fetchJSON(`${commonApiBase}/${encodedId}`),
                fetchJSON(`${commonApiBase}/${encodedId}/plan`),
                fetchJSON(`${commonApiBase}/${encodedId}/schedule`),
                fetchJSON(`${studentApiBase}/${encodedId}/students`)
            ]);

            renderOverview(lectureInfo);

            planData = plan?.__notFound ? [] : (Array.isArray(plan) ? plan : []);
            renderPlanAccordion();

            renderSchedule(schedule?.__notFound ? [] : schedule);
            renderClassmates(classmates?.__notFound ? [] : classmates);
        } catch (err) {
            console.error("[stuClassroom] init error", err);
            showAlert(err?.message || "강의 정보를 불러오는 중 오류가 발생했습니다.");
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init, { once: true });
    } else {
        init();
    }
})();