/**
 * 학생 메인 강의 목록
 */
(() => {
    "use strict";

    const DAY_LABEL = { MO: "월", TU: "화", WE: "수", TH: "목", FR: "금", SA: "토", SU: "일" };
    const TERM_LABEL = { REG1: "1학기", REG2: "2학기", SUB1: "하계계절학기", SUB2: "동계계절학기" };
    const TERM_ORDER = { REG1: 1, REG2: 2, SUB1: 3, SUB2: 4 };

    const fmtTime = (hhmm) => {
        if (!hhmm || String(hhmm).length < 4) return "--:--";
        const value = String(hhmm);
        return `${value.slice(0, 2)}:${value.slice(2, 4)}`;
    };

    const escapeHtml = (value) => String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const safeParseSchedule = (raw) => {
        if (!raw) return [];
        try {
            if (Array.isArray(raw)) return raw;
            if (typeof raw === "string") return JSON.parse(raw);
        } catch (err) {
            console.warn("[stuMain] 시간표 파싱 실패", err);
        }
        return [];
    };

    const renderSchedule = (schedules) => {
        if (!Array.isArray(schedules) || schedules.length === 0) {
            return '<p class="text-muted mb-0">시간표 정보가 없습니다.</p>';
        }

        return schedules.map((sch) => {
            const place = escapeHtml(sch.placeName || sch.placeCd || "강의실 미정");
            const slotLines = (Array.isArray(sch.slots) ? sch.slots : []).map((slot) => {
                const day = DAY_LABEL[slot.day] ?? slot.day ?? "";
                return `<li>${escapeHtml(day)} ${fmtTime(slot.start)} ~ ${fmtTime(slot.end)}</li>`;
            }).join("");

            return `
                <div class="mb-2">
                    <div class="badge bg-light text-dark border me-2">${place}</div>
                    <ul class="mb-0 ps-3">${slotLines}</ul>
                </div>
            `;
        }).join("");
    };

    const parseYearTermForSort = (code) => {
        if (!code) return { year: 0, order: 99, termCd: "" };
        const [yearStr = "0", termCd = ""] = String(code).split("_");
        return {
            year: Number(yearStr) || 0,
            order: TERM_ORDER[termCd] ?? 99,
            termCd,
        };
    };

    const formatYearTerm = (code) => {
        if (!code) return "학기 정보 없음";
        const [year, termCd] = String(code).split("_");
        const termLabel = TERM_LABEL[termCd] ?? termCd ?? "";
        return termLabel ? `${year}년 ${termLabel}` : `${year}년`;
    };

    const formatDate = (iso) => {
        if (!iso) return "종료일 미정";
        const date = new Date(iso);
        if (Number.isNaN(date.getTime())) return "종료일 미정";
        const m = String(date.getMonth() + 1).padStart(2, "0");
        const d = String(date.getDate()).padStart(2, "0");
        return `${date.getFullYear()}-${m}-${d}`;
    };

    const sortLectures = (lectures) => lectures.slice().sort((a, b) => {
        const pa = parseYearTermForSort(a.yeartermCd);
        const pb = parseYearTermForSort(b.yeartermCd);
        if (pa.year !== pb.year) return pb.year - pa.year;
        if (pa.order !== pb.order) return pa.order - pb.order;
        return (a.subjectName || "").localeCompare(b.subjectName || "", "ko");
    });

    const init = () => {
        const $root = document.querySelector("#stu-main-root");
        if (!$root) return;

        const ctx = $root.dataset.ctx || "";
        const apiUrl = $root.dataset.api || `${ctx}/classroom/api/v1/student/mylist`;
        const $accordionOngoing = $root.querySelector("#stu-accordion-ongoing");
        const $accordionEnded = $root.querySelector("#stu-accordion-ended");
        const $countOngoing = $root.querySelector("#stu-ongoing-count");
        const $countEnded = $root.querySelector("#stu-ended-count");
        const $summary = $root.querySelector("#stu-lecture-summary");
        const $error = $root.querySelector("#stu-error-box");

        const updateSummary = (ongoingCount, endedCount) => {
            if ($summary) {
                $summary.textContent = `진행중 ${ongoingCount}개 · 종료 ${endedCount}개`;
            }
        };

        const getLectureUrl = (lectureId) => `${ctx}/classroom/student/${encodeURIComponent(lectureId)}`;

        const buildStatusBadge = (ended) => {
            if (ended) {
                return '<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle">종료</span>';
            }
            return '<span class="badge bg-success-subtle text-success border border-success-subtle">진행중</span>';
        };

        const createLectureCard = (lecture) => {
            const schedules = safeParseSchedule(lecture.scheduleJson);
            const lectureUrl = getLectureUrl(lecture.lectureId);
            const statusBadge = buildStatusBadge(lecture.ended);
            const yearTerm = formatYearTerm(lecture.yeartermCd);
            const professorNoRaw = typeof lecture.professorNo === "string" ? lecture.professorNo.trim() : "";
            const professorNo = professorNoRaw ? encodeURIComponent(professorNoRaw) : "";
            const professorPhoto = professorNo ? `${ctx}/classroom/api/v1/common/photo/professor/${professorNo}` : "";
            const professorName = escapeHtml(lecture.professorName || "교수 미등록");
            const deptName = escapeHtml(lecture.univDeptName || "-");
            const completion = escapeHtml(lecture.completionName || "-");
            const credit = Number.isFinite(Number(lecture.credit)) ? `${lecture.credit}학점` : "학점 정보 없음";
            const hour = Number.isFinite(Number(lecture.hour)) ? `${lecture.hour}시간` : "시간 정보 없음";
            const currentCap = Number.isFinite(Number(lecture.currentCap)) ? Number(lecture.currentCap) : 0;
            const maxCap = Number.isFinite(Number(lecture.maxCap)) ? Number(lecture.maxCap) : 0;
            const capacityText = maxCap > 0 ? `${currentCap} / ${maxCap}` : `${currentCap}`;
            const endInfo = lecture.ended ? `종료일: ${formatDate(lecture.endAt)}` : "";
            const photoHtml = professorPhoto
                ? `<img src="${professorPhoto}" alt="${professorName} 교수 사진" class="stu-card-professor float-end ms-3 mb-2">`
                : "";
            const endInfoHtml = endInfo
                ? `<div class="text-muted small mt-auto pt-3 border-top">${escapeHtml(endInfo)}</div>`
                : "";

            return `
                <div class="col-12 col-lg-6 col-xl-4">
                    <article class="card shadow-sm h-100">
                        <div class="card-body d-flex flex-column">
                            <div class="d-flex align-items-start justify-content-between mb-2">
                                <h2 class="h5 lh-base mb-0">
                                    <a class="stretched-link text-decoration-none" href="${lectureUrl}">${escapeHtml(lecture.subjectName || "과목명 미정")}</a>
                                </h2>
                                ${statusBadge}
                            </div>
                            <div class="flex-grow-1">
                                <div class="clearfix">
                                    ${photoHtml}
                                    <div class="text-muted small mb-1">${escapeHtml(yearTerm)}</div>
                                    <div class="mb-2 small text-body-secondary">${deptName}</div>
                                    <ul class="list-unstyled small mb-0">
                                        <li>담당 교수: ${professorName}</li>
                                        <li>이수구분: ${completion}</li>
                                        <li>학점 / 시간: ${credit} · ${hour}</li>
                                        <li>수강인원: ${escapeHtml(capacityText)}</li>
                                    </ul>
                                </div>
                                <div class="mt-3">
                                    <strong class="d-block mb-1">시간표</strong>
                                    ${renderSchedule(schedules)}
                                </div>
                            </div>
                            ${endInfoHtml}
                        </div>
                    </article>
                </div>
            `;
        };

        const renderGroup = ({ container, countEl, title, lectures, collapsed, key }) => {
            if (!container) return;
            if (countEl) countEl.textContent = `${lectures.length}개`;

            const headingId = `${key}-heading`;
            const collapseId = `${key}-collapse`;
            const bodyHtml = lectures.length
                ? `<div class="row g-3">${lectures.map(createLectureCard).join("")}</div>`
                : '<div class="text-muted text-center py-4">등록된 강의가 없습니다.</div>';

            container.innerHTML = `
                <div class="accordion-item">
                    <h2 class="accordion-header" id="${headingId}">
                        <button class="accordion-button ${collapsed ? "collapsed" : ""}" type="button"
                            data-bs-toggle="collapse" data-bs-target="#${collapseId}"
                            aria-expanded="${collapsed ? "false" : "true"}" aria-controls="${collapseId}">
                            ${title}
                            <span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle ms-2">${lectures.length}개</span>
                        </button>
                    </h2>
                    <div id="${collapseId}" class="accordion-collapse collapse ${collapsed ? "" : "show"}" aria-labelledby="${headingId}">
                        <div class="accordion-body">${bodyHtml}</div>
                    </div>
                </div>
            `;
        };

        const clearError = () => {
            if ($error) {
                $error.classList.add("d-none");
                $error.textContent = "";
            }
        };

        const showError = (message) => {
            if ($error) {
                $error.textContent = message;
                $error.classList.remove("d-none");
            }
        };

        const renderLectures = (lectures) => {
            const ongoing = sortLectures(lectures.filter((item) => !item.ended));
            const ended = sortLectures(lectures.filter((item) => !!item.ended));

            updateSummary(ongoing.length, ended.length);
            renderGroup({
                container: $accordionOngoing,
                countEl: $countOngoing,
                title: "진행중인 강의",
                lectures: ongoing,
                collapsed: false,
                key: "stu-ongoing",
            });
            renderGroup({
                container: $accordionEnded,
                countEl: $countEnded,
                title: "종료된 강의",
                lectures: ended,
                collapsed: true,
                key: "stu-ended",
            });
        };

        const fetchLectures = async () => {
            clearError();
            try {
                const res = await fetch(apiUrl, { headers: { Accept: "application/json" } });
                if (!res.ok) {
                    throw new Error(`(${res.status}) 강의 목록을 불러오지 못했습니다.`);
                }
                const data = await res.json();
                if (!Array.isArray(data)) {
                    throw new Error("강의 목록 형식이 올바르지 않습니다.");
                }
                renderLectures(data);
            } catch (err) {
                console.error("[stuMain] fetch error", err);
                showError(err?.message || "강의 목록을 불러오는 중 오류가 발생했습니다.");
                if ($summary) $summary.textContent = "";
                if ($accordionOngoing) {
                    $accordionOngoing.innerHTML = '<div class="text-center text-muted py-4">강의 정보를 표시할 수 없습니다.</div>';
                }
                if ($accordionEnded) {
                    $accordionEnded.innerHTML = "";
                }
                updateSummary(0, 0);
            }
        };

        fetchLectures();
    };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init, { once: true });
    } else {
        init();
    }
})();
