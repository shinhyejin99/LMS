import React, { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import ClassroomLayout from '../components/layout/ClassroomLayout'
import '../styles/studentMain.css'

const DAY_LABEL = {
  MO: '월',
  TU: '화',
  WE: '수',
  TH: '목',
  FR: '금',
  SA: '토',
  SU: '일',
}

const TERM_LABEL = {
  REG1: '1학기',
  REG2: '2학기',
  SUB1: '여름학기',
  SUB2: '겨울학기',
}

const TERM_ORDER = {
  REG1: 1,
  REG2: 2,
  SUB1: 3,
  SUB2: 4,
}

const TEXT = {
  pageTitle: '내 강의 현황',
  summary: (ongoing, ended) => `진행중 ${ongoing}개 · 종료 ${ended}개`,
  ongoingTitle: '진행중인 강의',
  endedTitle: '종료된 강의',
  loading: '강의 목록을 불러오는 중입니다...',
  empty: '등록된 강의가 없습니다.',
  error: '강의 정보를 불러오는 중 문제가 발생했습니다.',
  cannotDisplay: '강의 정보를 표시할 수 없습니다.',
  professorFallback: '담당 교수 미정',
  deptFallback: '-',
  completionFallback: '-',
  creditUnknown: '학점 정보 없음',
  hourUnknown: '시수 정보 없음',
  capacityLabel: (current, max) => (max > 0 ? `${current} / ${max}` : `${current}`),
  scheduleHeading: '시간표',
  scheduleEmpty: '시간표 정보가 없습니다.',
  endInfo: (date) => `종료일: ${date}`,
  statusEnded: '종료',
  statusOngoing: '진행중',
}

const fmtTime = (value) => {
  if (!value || String(value).length < 4) return '--:--'
  const v = String(value)
  const hour = parseInt(v.slice(0, 2), 10)
  const minute = v.slice(2, 4)
  return `${hour}:${minute}` // Removes leading zero for hour
}

const safeParseSchedule = (raw) => {
  if (!raw) return []
  if (Array.isArray(raw)) return raw
  if (typeof raw === 'string') {
    try {
      const parsed = JSON.parse(raw)
      return Array.isArray(parsed) ? parsed : []
    } catch {
      return []
    }
  }
  return []
}

const parseYearTermForSort = (code) => {
  if (!code) return { year: 0, order: 99 }
  const [yearStr = '0', termCd = ''] = String(code).split('_')
  return {
    year: Number(yearStr) || 0,
    order: TERM_ORDER[termCd] ?? 99,
  }
}

const formatYearTerm = (code) => {
  if (!code) return '학기 정보 없음'
  const [year, termCd] = String(code).split('_')
  const label = TERM_LABEL[termCd] || termCd || ''
  return label ? `${year}년 ${label}` : `${year}년`
}

const formatDate = (iso) => {
  if (!iso) return '날짜 정보 없음'
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return '날짜 정보 없음'
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const sortLectures = (lectures) => lectures
  .slice()
  .sort((a, b) => {
    const pa = parseYearTermForSort(a.yeartermCd)
    const pb = parseYearTermForSort(b.yeartermCd)
    if (pa.year !== pb.year) return pb.year - pa.year
    if (pa.order !== pb.order) return pa.order - pb.order
    return String(a.subjectName || '').localeCompare(String(b.subjectName || ''), 'ko')
  })

const makeLectureLink = (lectureId) => {
  if (!lectureId) return '#'
  return `/classroom/student/${encodeURIComponent(lectureId)}`
}

const buildProfessorPhoto = (professorNo) => {
  if (!professorNo && professorNo !== 0) return null
  const raw = String(professorNo).trim()
  if (!raw) return null
  return `/classroom/api/v1/common/photo/professor/${encodeURIComponent(raw)}`
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

const LectureCard = ({ lecture }) => {
  const schedule = safeParseSchedule(lecture?.scheduleJson)
  const lectureLink = makeLectureLink(lecture?.lectureId)
  const statusEnded = Boolean(lecture?.ended)
  const professorPhoto = buildProfessorPhoto(lecture?.professorNo)
  const professorName = lecture?.professorName || TEXT.professorFallback
  const deptName = lecture?.univDeptName || TEXT.deptFallback
  const completion = lecture?.completionName || TEXT.completionFallback
  const subjectTypeName = lecture?.subjectTypeName || '-'
  const credit = Number.isFinite(Number(lecture?.credit))
    ? `${lecture.credit}학점`
    : TEXT.creditUnknown
  const hour = Number.isFinite(Number(lecture?.hour))
    ? `${lecture.hour}시간`
    : TEXT.hourUnknown
  const currentCap = Number.isFinite(Number(lecture?.currentCap)) ? Number(lecture.currentCap) : 0
  const maxCap = Number.isFinite(Number(lecture?.maxCap)) ? Number(lecture.maxCap) : 0
  const capacity = TEXT.capacityLabel(currentCap, maxCap)
  const endInfo = statusEnded ? TEXT.endInfo(formatDate(lecture?.endAt)) : null

  return (
    <div className="col-12 col-lg-6 col-xl-4">
      <article className="card shadow-sm h-100">
        <div className="card-body d-flex flex-column">
          <div className="d-flex align-items-start justify-content-between mb-2">
            <h2 className="h5 lh-base mb-0">
              <Link className="stretched-link text-decoration-none" to={lectureLink}>
                {lecture?.subjectName || '강의명 미정'}
              </Link>
            </h2>
            <span
              className={`badge ${statusEnded
                ? 'bg-secondary-subtle text-secondary border border-secondary-subtle'
                : 'bg-success-subtle text-success border border-success-subtle'}`}
            >
              {statusEnded ? TEXT.statusEnded : TEXT.statusOngoing}
            </span>
          </div>
          <div className="flex-grow-1">
            <div className="clearfix">
              {professorPhoto ? (
                <img
                  src={professorPhoto}
                  alt={`${professorName} 교수 사진`}
                  className="stu-card-professor float-end ms-3 mb-2"
                />
              ) : null}
              <div className="mb-2 small text-body-secondary">{deptName}</div>
              <div className="text-muted small mb-1">{formatYearTerm(lecture?.yeartermCd)}</div>
              <ul className="list-unstyled small mb-0">
                <li>담당 교수: {professorName}</li>
                <li>이수 구분: {completion}</li>
                <li>평가 방식: {subjectTypeName}</li>
                <li>학점 / 시수: {credit} · {hour}</li>
                <li>수강 정원: {capacity}</li>
              </ul>
            </div>
            <div className="mt-3">
              <strong className="d-block mb-1">{TEXT.scheduleHeading}</strong>
              {renderSchedule(schedule)}
            </div>
          </div>
          {endInfo ? (
            <div className="text-muted small mt-auto pt-3 border-top">
              {endInfo}
            </div>
          ) : null}
        </div>
      </article>
    </div>
  )
}

const LectureAccordion = ({
  idPrefix,
  title,
  count,
  open,
  onToggle,
  children,
  badgeClassName = 'bg-secondary-subtle text-secondary border border-secondary-subtle',
}) => (
  <div className="accordion mt-2" id={`${idPrefix}-accordion`}>
    <div className="accordion-item">
      <h2 className="accordion-header" id={`${idPrefix}-heading`}>
        <button
          className={`accordion-button${open ? '' : ' collapsed'}`}
          type="button"
          aria-expanded={open}
          aria-controls={`${idPrefix}-collapse`}
          onClick={onToggle}
        >
          {title}
          <span className={`badge ${badgeClassName} ms-2`}>
            {count}개
          </span>
        </button>
      </h2>
      <div
        id={`${idPrefix}-collapse`}
        className={`accordion-collapse collapse${open ? ' show' : ''}`}
        aria-labelledby={`${idPrefix}-heading`}
      >
        <div className="accordion-body">
          {children}
        </div>
      </div>
    </div>
  </div>
)

export default function StudentDashboard() {
  const [lectures, setLectures] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [openAccordion, setOpenAccordion] = useState('ongoing')

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError('')
        const res = await fetch('/classroom/api/v1/student/mylist', {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) {
          throw new Error(`(${res.status}) ${TEXT.error}`)
        }
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
      } catch (err) {
        if (!alive) return
        setError(err instanceof Error ? err.message : TEXT.error)
        setLectures([])
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [])

  const sortedOngoing = useMemo(() => sortLectures(lectures.filter((item) => !item?.ended)), [lectures])
  const sortedEnded = useMemo(() => sortLectures(lectures.filter((item) => !!item?.ended)), [lectures])

  const summaryText = TEXT.summary(sortedOngoing.length, sortedEnded.length)

  const renderLectureGrid = (items) => {
    if (loading) {
      return <div className="text-center text-muted py-4">{TEXT.loading}</div>
    }
    if (error) {
      return <div className="text-center text-muted py-4">{TEXT.cannotDisplay}</div>
    }
    if (!items.length) {
      return <div className="text-muted text-center py-4">{TEXT.empty}</div>
    }
    return (
      <div className="row g-3">
        {items.map((lecture) => (
          <LectureCard key={lecture?.lectureId || lecture?.subjectName} lecture={lecture} />
        ))}
      </div>
    )
  }

  return (
    <ClassroomLayout role="student">
      <div
        id="stu-main-root"
        className="container my-4"
        data-api="/classroom/api/v1/student/mylist"
      >
        <div className="d-flex align-items-center justify-content-between mb-3">
          <h1 className="h3 mb-0">{TEXT.pageTitle}</h1>
          <div className="text-muted small" id="stu-lecture-summary" aria-live="polite">
            {!error ? summaryText : ''}
          </div>
        </div>

        {error && (
          <div id="stu-error-box" className="alert alert-danger" role="alert">
            {error}
          </div>
        )}

        <section className="accordion-section mb-4">
          <div className="d-flex align-items-center justify-content-between">
            <h2 className="h5 mb-0">{TEXT.ongoingTitle}</h2>
            <span className="badge bg-success-subtle text-success border border-success-subtle" id="stu-ongoing-count">
              {sortedOngoing.length}개
            </span>
          </div>
          <LectureAccordion
            idPrefix="stu-ongoing"
            title={TEXT.ongoingTitle}
            count={sortedOngoing.length}
            open={openAccordion === 'ongoing'}
            onToggle={() => setOpenAccordion(openAccordion === 'ongoing' ? null : 'ongoing')}
            badgeClassName="bg-success-subtle text-success border border-success-subtle"
          >
            {renderLectureGrid(sortedOngoing)}
          </LectureAccordion>
        </section>

        <section className="accordion-section">
          <div className="d-flex align-items-center justify-content-between">
            <h2 className="h5 mb-0">{TEXT.endedTitle}</h2>
            <span className="badge bg-secondary-subtle text-secondary border border-secondary-subtle" id="stu-ended-count">
              {sortedEnded.length}개
            </span>
          </div>
          <LectureAccordion
            idPrefix="stu-ended"
            title={TEXT.endedTitle}
            count={sortedEnded.length}
            open={openAccordion === 'ended'}
            onToggle={() => setOpenAccordion(openAccordion === 'ended' ? null : 'ended')}
          >
            {renderLectureGrid(sortedEnded)}
          </LectureAccordion>
        </section>
      </div>
    </ClassroomLayout>
  )
}

