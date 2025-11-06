import React, { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import ClassroomLayout from '../components/layout/ClassroomLayout'

export default function ProfessorDashboard() {
  const [lectures, setLectures] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Utilities migrated from legacy JS (간단화/방어 처리)
  const DAY_LABEL = useMemo(() => ({
    MO: '월', TU: '화', WE: '수', TH: '목', FR: '금', SA: '토', SU: '일',
  }), [])
  const TERM_LABEL = useMemo(() => ({ REG1: '1학기', REG2: '2학기', SUB1: '여름계절학기', SUB2: '겨울계절학기' }), [])
  const TERM_ORDER = useMemo(() => ({ REG1: 1, REG2: 2, SUB1: 3, SUB2: 4 }), [])

  const TEXT = useMemo(() => ({
    scheduleHeading: '시간표',
    scheduleEmpty: '시간표 정보가 없습니다.',
  }), [])

  const fmtTime = (hhmm) => {
    if (!hhmm || String(hhmm).length < 4) return '--:--'
    const v = String(hhmm)
    return `${v.slice(0, 2)}:${v.slice(2, 4)}`
  }
  const escapeHtml = (v) => String(v ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

  const safeParseSchedule = (raw) => {
    if (!raw) return []
    try {
      if (Array.isArray(raw)) return raw
      if (typeof raw === 'string') return JSON.parse(raw)
    } catch {}
    return []
  }
  const parseYearTermForSort = (code) => {
    if (!code) return { year: 0, order: 99, termCd: '' }
    const [yearStr = '0', termCd = ''] = String(code).split('_')
    return { year: Number(yearStr) || 0, order: TERM_ORDER[termCd] ?? 99, termCd }
  }
  const sortLectures = (arr) => arr.slice().sort((a, b) => {
    const pa = parseYearTermForSort(a.yeartermCd)
    const pb = parseYearTermForSort(b.yeartermCd)
    if (pa.year !== pb.year) return pb.year - pa.year
    if (pa.order !== pb.order) return pa.order - pb.order
    return (a.subjectName || '').localeCompare(b.subjectName || '', 'ko')
  })
  const formatYearTerm = (code) => {
    if (!code) return '학기 정보 없음'
    const [year, termCd] = String(code).split('_')
    const t = TERM_LABEL[termCd] ?? termCd ?? ''
    return t ? `${year}년 ${t}` : `${year}`
  }
  const formatDate = (iso) => {
    if (!iso) return '미정'
    const d = new Date(iso)
    if (Number.isNaN(d.getTime())) return '미정'
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${d.getFullYear()}-${m}-${day}`
  }

  const renderSchedule = (scheduleList) => {
    if (!scheduleList.length) {
      return <p className="text-muted mb-0">{TEXT.scheduleEmpty}</p>
    }
    return scheduleList.map((sch, idx) => {
      const place = sch?.placeName || sch?.placeCd || '강의실 정보 없음'
      const slots = Array.isArray(sch?.slots) ? sch.slots : []
      return (
        <div className="mb-2" key={`${place}-${idx}`}>
          <div className="badge bg-light text-dark border me-2">{place}</div>
          <ul className="mb-0 ps-3">
            {slots.length === 0 ? (
              <li className="text-muted">{TEXT.scheduleEmpty}</li>
            ) : (
              slots.map((slot, slotIdx) => {
                const day = DAY_LABEL[slot?.day] ?? slot?.day ?? ''
                return (
                  <li key={`${place}-${idx}-${slotIdx}`}>
                    {day} {fmtTime(slot?.start)}-{fmtTime(slot?.end)}
                  </li>
                )
              })
            )}
          </ul>
        </div>
      )
    })
  }

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const res = await fetch('/classroom/api/v1/professor/mylist', {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) throw new Error(`(${res.status}) 강의 목록을 불러오지 못했습니다.`)

        // 응답 본문이 비어있는 경우 처리
        const text = await res.text()
        if (!alive) return

        let data = []
        if (text && text.trim()) {
          try {
            data = JSON.parse(text)
          } catch (parseErr) {
            console.warn('JSON 파싱 실패:', parseErr)
            // 파싱 실패 시 빈 배열로 처리
            data = []
          }
        }

        if (!Array.isArray(data)) {
          data = []
        }
        setLectures(data)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [])

  const upcoming = useMemo(() => {
    const upcomingList = lectures.filter(l => !l.started && !l.ended)
    return sortLectures(upcomingList)
  }, [lectures])

  const ongoing = useMemo(() => {
    const ongoingList = lectures.filter(l => l.started && !l.ended)
    return sortLectures(ongoingList)
  }, [lectures])

  const ended = useMemo(() => sortLectures(lectures.filter(l => !!l.ended)), [lectures])

  const LectureCard = ({ lecture }) => {
    const schedules = safeParseSchedule(lecture.scheduleJson)

    let statusBadge
    if (lecture.ended) {
      statusBadge = <span className="badge bg-secondary-subtle text-secondary border border-secondary-subtle">종강</span>
    } else if (!lecture.started) {
      statusBadge = <span className="badge bg-info-subtle text-info border border-info-subtle">예정</span>
    } else {
      statusBadge = <span className="badge bg-success-subtle text-success border border-success-subtle">진행중</span>
    }
    const yearTerm = formatYearTerm(lecture.yeartermCd)
    const deptName = lecture.deptName || '-'
    const subjectName = lecture.subjectName || '-'
    const professorName = lecture.professorName || '-' // 필드명이 다르면 서버 응답에 맞춰 자동 대체됨
    const completion = lecture.completion || lecture.completionName || '-'
    const subjectTypeName = lecture.subjectTypeName || '-'
    const credit = lecture.credit ?? lecture.creditPoint ?? '-'
    const hour = lecture.hour ?? lecture.classHour ?? '-'
    const capacityText = lecture.capacityText || lecture.capacity || ''
    const endInfo = lecture.ended ? (lecture.endDate ? `종강일: ${formatDate(lecture.endDate)}` : '종강됨') : ''
    const lectureId = lecture.lectureId || lecture.id || lecture.lectureNo

    return (
      <div className="col-12 col-md-6 col-lg-4">
        <article className="card h-100 position-relative">
          <div className="card-body d-flex flex-column">
            <div className="d-flex align-items-center justify-content-between mb-2">
              <h3 className="h6 mb-0 text-truncate" title={subjectName}>{subjectName}</h3>
              {statusBadge}
            </div>
            <div className="text-muted small mb-2">{yearTerm}</div>
            <div className="mb-2 small text-body-secondary">{deptName}</div>
            <ul className="list-unstyled small mb-3">
              <li>담당: {escapeHtml(professorName)}</li>
              <li>이수구분: {escapeHtml(completion)}</li>
              <li>평가방식: {escapeHtml(subjectTypeName)}</li>
              <li>학점 / 시수: {escapeHtml(String(credit))} · {escapeHtml(String(hour))}</li>
              {capacityText ? <li>수강정원: {escapeHtml(String(capacityText))}</li> : null}
            </ul>
            <div className="mb-2">
              <strong className="d-block mb-1">시간표</strong>
              {renderSchedule(schedules)}
            </div>
            {endInfo && <div className="mt-auto text-muted small">{endInfo}</div>}
            {lectureId && (
              <Link
                to={`/classroom/professor/${encodeURIComponent(lectureId)}`}
                className="stretched-link"
                aria-label={`${subjectName} 상세 보기`}
              />
            )}
          </div>
        </article>
      </div>
    )
  }

  const GroupAccordion = ({ id, title, items, defaultOpen }) => (
    <div className="accordion-item">
      <h2 className="accordion-header" id={`${id}-heading`}>
        <button className={`accordion-button ${defaultOpen ? '' : 'collapsed'}`} type="button"
          data-bs-toggle="collapse" data-bs-target={`#${id}-collapse`}
          aria-expanded={defaultOpen ? 'true' : 'false'} aria-controls={`${id}-collapse`}
          data-bs-parent="#lectures-accordion">
          {title}
          <span className="badge bg-secondary-subtle text-secondary border border-secondary-subtle ms-2">{items.length}개</span>
        </button>
      </h2>
      <div id={`${id}-collapse`} className={`accordion-collapse collapse ${defaultOpen ? 'show' : ''}`}
        aria-labelledby={`${id}-heading`} data-bs-parent="#lectures-accordion">
        <div className="accordion-body">
          {items.length ? (
            <div className="row g-3">
              {items.map((lec) => (
                <LectureCard key={lec.lectureId || `${lec.subjectName}-${lec.yeartermCd}`} lecture={lec} />
              ))}
            </div>
          ) : (
            <div className="text-muted text-center py-4">해당 강의가 없습니다.</div>
          )}
        </div>
      </div>
    </div>
  )

  return (
    <ClassroomLayout role="professor">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h4 mb-0">내 강의</h1>
        <div className="text-muted small" aria-live="polite">
          {loading ? '불러오는 중…' : `예정 ${upcoming.length}개 · 진행중 ${ongoing.length}개 · 종강 ${ended.length}개`}
        </div>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>
      )}

      {!error && (
        <div className="accordion" id="lectures-accordion">
          <GroupAccordion id="prof-ongoing" title="진행중인 강의" items={ongoing} defaultOpen={true} />
          <GroupAccordion id="prof-upcoming" title="예정된 강의" items={upcoming} defaultOpen={false} />
          <GroupAccordion id="prof-ended" title="종강된 강의" items={ended} defaultOpen={false} />
        </div>
      )}
    </ClassroomLayout>
  )
}
