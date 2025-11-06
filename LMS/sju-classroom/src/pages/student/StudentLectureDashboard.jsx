import React, { Fragment, useEffect, useMemo, useState } from 'react'
import { createPortal } from 'react-dom'
import { useParams } from 'react-router-dom'
import { useLecture } from '../../context/LectureContext'
import '../../styles/studentClassroom.css'

const TEXT = {
  defaultTitle: '\uac15\uc758 \ud648',
  subtitleLoading: '\uac15\uc758 \uc815\ubcf4\ub97c \ubd88\ub7ec\uc624\ub294 \uc911\uc785\ub2c8\ub2e4...',
  planHeading: '\uc8fc\ucc28\ubcc4 \uc218\uc5c5 \uacc4\ud68d',
  planLoadingTitle: '\uc8fc\ucc28 \uc815\ubcf4\ub97c \ubd88\ub7ec\uc624\ub294 \uc911\uc785\ub2c8\ub2e4...',
  planLoadingBody: '\uae30\ub2e4\ub824 \uc8fc\uc138\uc694.',
  planEmpty: '\ub4f1\ub85d\ub41c \uc218\uc5c5 \uacc4\ud68d\uc774 \uc5c6\uc2b5\ub2c8\ub2e4.',
  planGoalLabel: '\uc218\uc5c5 \ubaa9\ud45c',
  planDescLabel: '\uc218\uc5c5 \ub0b4\uc6a9',
  overviewHeading: '\uac15\uc758 \uac1c\uc694',
  overviewLoading: '\uac15\uc758 \uc815\ubcf4\ub97c \ubd88\ub7ec\uc624\ub294 \uc911\uc785\ub2c8\ub2e4...',
  overviewProfessorLabel: '\ub2f4\ub2f9 \uad50\uc218',
  overviewIndexLabel: '\ud559\uc815\ubc88\ud638',
  overviewGoalLabel: '\uc218\uc5c5 \ubaa9\ud45c',
  overviewPrereqLabel: '\uc120\uc218 \uacfc\ubaa9',
  overviewCapacityLabel: '\uc815\uc6d0',
  overviewEndLabel: '\uc885\uac15\uc77c',
  overviewGoalFallback: '\ub4f1\ub85d\ub41c \uc815\ubcf4\uac00 \uc5c6\uc2b5\ub2c8\ub2e4.',
  overviewPrereqFallback: '\uc815\ud655\ud55c \uc815\ubcf4\uac00 \uc5c6\uc2b5\ub2c8\ub2e4.',
  overviewCapacityFallback: '\ub4f1\ub85d\ub41c \uc815\uc6d0\uc774 \uc5c6\uc2b5\ub2c8\ub2e4.',
  overviewProfessorFallback: '\uc54c \uc218 \uc5c6\uc74c',
  overviewIndexFallback: '\uc815\ubcf4 \uc5c6\uc74c',
  overviewEndFallback: '\ubbf8\uc815',
  scheduleHeading: '\uac15\uc758 \uc2dc\uac04/\uac15\uc758\uc2e4',
  scheduleLoading: '\uc2dc\uac04\ud45c \uc815\ubcf4\ub97c \ubd88\ub7ec\uc624\ub294 \uc911\uc785\ub2c8\ub2e4...',
  scheduleEmpty: '\ub4f1\ub85d\ub41c \uc2dc\uac04\ud45c\uac00 \uc5c6\uc2b5\ub2c8\ub2e4.',
  scheduleDay: '\uc694\uc77c',
  scheduleTime: '\uc2dc\uac04',
  schedulePlace: '\uac15\uc758\uc2e4',
  classmateHeading: '\ud568\uaed8 \ub4e3\ub294 \ud559\uc6b0',

  classmateLoading: '\ud559\uc6b0 \uc815\ubcf4\ub97c \ubd88\ub7ec\uc624\ub294 \uc911\uc785\ub2c8\ub2e4...',
  classmateEmpty: '\ud558\uaed8 \uc218\uac15\ud558\ub294 \ud559\uc6b0 \uc815\ubcf4\uac00 \uc5c6\uc2b5\ub2c8\ub2e4.',
  classmateFallback: '\ud559\uc6b0 \uc815\ubcf4\ub97c \ud655\uc778\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.',
  classmateModalTitle: '\ud568\uaed8 \ub4e3\ub294 \ud559\uc6b0',
  modalClose: '\ub2eb\uae30',
  tableStudentNo: '\ud559\ubc88',
  tableName: '\uc774\ub984',
  tableDept: '\ud559\uacfc',
  tableStatus: '\uc0c1\ud0dc',
  summaryExtraPrefix: '\uc678 ',
  summaryExtraSuffix: '\uba85',
  unknownTerm: '\ud559\uae30 \uc815\ubcf4 \uc5c6\uc74c',
  unknownProfessor: '\ubb38\uc758',
  unknownStudentName: '\uc774\ub984 \ubb38\uc758',
  countSuffix: '\uba85',
  genericError: '\ub370\uc774\ud130\ub97c \ubd88\ub7ec\uc624\ub294 \uc911 \ubb38\uc81c\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4.',
  missingLecture: '\uac15\uc758 \uc815\ubcf4\uac00 \ud544\uc694\uc2ed\ub2c8\ub2e4.',
}

const DAY_LABEL = {
  MO: '\uc6d4',
  TU: '\ud654',
  WE: '\uc218',
  TH: '\ubaa9',
  FR: '\uae08',
  SA: '\ud1a0',
  SU: '\uc77c',
}

const commonApiBase = '/classroom/api/v1/common'
const studentApiBase = '/classroom/api/v1/student'

const fmtTime = (value) => {
  if (!value || String(value).length < 4) return '--:--'
  const v = String(value)
  return `${v.slice(0, 2)}:${v.slice(2, 4)}`
}

const formatDateOnly = (value) => {
  if (!value) return '-'
  if (typeof value === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(value)) return value
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '-'
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const safeParseSchedule = (value) => {
  if (!value) return []
  if (Array.isArray(value)) return value
  if (typeof value === 'string') {
    try {
      const parsed = JSON.parse(value)
      return Array.isArray(parsed) ? parsed : []
    } catch {
      return []
    }
  }
  return []
}

const renderMultiline = (text, emptyLabel) => {
  const source = typeof text === 'string' ? text.trim() : ''
  if (!source) {
    return <span className="text-body-secondary">{emptyLabel || '-'}</span>
  }
  const parts = source.split(/\r?\n/)
  return parts.map((line, idx) => (
    <Fragment key={`${idx}-${line}`}>
      {line || '\u00a0'}
      {idx < parts.length - 1 ? <br /> : null}
    </Fragment>
  ))
}

export default function StudentLectureDashboard() {
  const { lectureId } = useParams()
  const { setLecture, setStudents, formatYearTerm } = useLecture()
  const [title, setTitle] = useState(TEXT.defaultTitle)
  const [subtitle, setSubtitle] = useState(TEXT.subtitleLoading)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [overview, setOverview] = useState(null)
  const [planList, setPlanList] = useState([])
  const [activeTab, setActiveTab] = useState(1)
  const [scheduleList, setScheduleList] = useState([])
  const [classmates, setClassmates] = useState([])


  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        if (!lectureId) throw new Error(TEXT.missingLecture)
        setLoading(true)
        setError(null)
        const encoded = encodeURIComponent(lectureId)
        const options = { headers: { Accept: 'application/json' }, credentials: 'include' }
        const requests = [
          fetch(`${commonApiBase}/${encoded}`, options),
          fetch(`${commonApiBase}/${encoded}/plan`, options),
          fetch(`${commonApiBase}/${encoded}/schedule`, options),
          fetch(`${studentApiBase}/${encoded}/students`, options),
        ]

        const parseResponse = async (res) => {
          if (res.status === 404) return { data: null, notFound: true }
          if (res.status === 204) return { data: [] }
          if (!res.ok) {
            const text = await res.text().catch(() => '')
            const detail = text ? ` - ${text}` : ''
            throw new Error(`HTTP ${res.status} ${res.statusText}${detail}`)
          }
          const contentType = res.headers.get('content-type') || ''
          if (contentType.includes('application/json')) {
            return { data: await res.json() }
          }
          const text = await res.text()
          try {
            return { data: JSON.parse(text) }
          } catch {
            return { data: text }
          }
        }

        const [infoPayload, planPayload, schedulePayload, matesPayload] = await Promise.all(
          requests.map((promise) => promise.then(parseResponse)),
        )
        if (!alive) return

        const info = infoPayload.notFound ? null : infoPayload.data
        setOverview(info || null)
        setLecture(info || null)
        if (info) {
          setTitle(info.subjectName || TEXT.defaultTitle)
          const prof = info.professorName || TEXT.overviewProfessorFallback
          const term = formatYearTerm(info.yeartermCd) || TEXT.unknownTerm
          setSubtitle(`${prof} \u00b7 ${term}`)
        } else {
          setTitle(TEXT.defaultTitle)
          setSubtitle(TEXT.subtitleLoading)
        }

        const plansRaw = planPayload.notFound ? [] : planPayload.data
        const plans = Array.isArray(plansRaw) ? plansRaw.filter((item) => item && item.lectureWeek !== undefined) : []
        plans.sort((a, b) => (Number(a.lectureWeek) || 0) - (Number(b.lectureWeek) || 0))
        setPlanList(plans)

        const scheduleRaw = schedulePayload.notFound ? [] : schedulePayload.data
        setScheduleList(safeParseSchedule(scheduleRaw))

        const matesRaw = matesPayload.notFound ? [] : matesPayload.data
        const matesList = Array.isArray(matesRaw) ? matesRaw.slice() : []
        matesList.sort((a, b) => {
          const gradeA = String(a?.gradeCd || '').localeCompare(String(b?.gradeCd || ''))
          if (gradeA !== 0) return gradeA
          const nameA = `${a?.lastName || ''}${a?.firstName || ''}`
          const nameB = `${b?.lastName || ''}${b?.firstName || ''}`
          return nameA.localeCompare(nameB, 'ko')
        })
        setClassmates(matesList)
        setStudents(matesList)
      } catch (err) {
        if (!alive) return
        setError(err instanceof Error ? err.message : TEXT.genericError)
      } finally {
        if (alive) setLoading(false)
      }
    })()

    return () => {
      alive = false
    }
  }, [lectureId, setLecture, setStudents, formatYearTerm])





  const classmateCountLabel = `${classmates.length}${TEXT.countSuffix}`

  const professorPhotoUrl = useMemo(() => {
    if (!overview?.professorNo) return null
    return `${commonApiBase}/photo/professor/${encodeURIComponent(String(overview.professorNo).trim())}`
  }, [overview?.professorNo])

  const planContent = useMemo(() => {
    if (loading) {
      return (
        <div className="text-center py-4">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">{TEXT.planLoadingTitle}</span>
          </div>
          <p className="text-muted mt-2 mb-0">{TEXT.planLoadingBody}</p>
        </div>
      );
    }

    if (!planList.length) {
      return <div className="text-muted text-center py-4">{TEXT.planEmpty}</div>;
    }

    return (
      <>
        <ul className="nav nav-tabs mb-3" id="planTabs" role="tablist">
          {planList.map((plan) => {
            const week = Number(plan.lectureWeek)
            return (
              <li className="nav-item" role="presentation" key={week}>
                <button
                  className={`nav-link ${activeTab === week ? 'active' : ''}`}
                  id={`week-${week}-tab`}
                  type="button"
                  role="tab"
                  aria-controls={`#week-${week}-tab-pane`}
                  aria-selected={activeTab === week}
                  onClick={() => setActiveTab(week)}
                >
                  {`${week}주차`}
                </button>
              </li>
            )
          })}
        </ul>
        <div className="tab-content flex-grow-1 border border-top-0 rounded-bottom p-3" id="planTabContent">
          {planList.map((plan) => {
            const week = Number(plan.lectureWeek)
            return (
              <div
                className={`tab-pane fade ${activeTab === week ? 'show active' : ''}`}
                id={`week-${week}-tab-pane`}
                role="tabpanel"
                aria-labelledby={`week-${week}-tab`}
                tabIndex="0"
                key={week}
              >
                <div className="card-body bg-light-subtle">
                  <div className="mb-2">
                    <div className="fw-semibold mb-1 text-primary">
                      <i className="bi bi-bullseye me-2"></i>{TEXT.planGoalLabel}
                    </div>
                    <p className="mb-0">{renderMultiline(plan.weekGoal, TEXT.overviewGoalFallback)}</p>
                  </div>
                  <div className="fw-semibold mb-1">
                    <i className="bi bi-journal-text me-2"></i>{TEXT.planDescLabel}
                  </div>
                  <p className="mb-0">{renderMultiline(plan.weekDesc, TEXT.overviewGoalFallback)}</p>

                </div>
              </div>
            )
          })}
        </div>
      </>
    )
  }, [planList, activeTab, loading])

  const scheduleContent = useMemo(() => {
    if (loading) {
      return <div className="text-body-secondary">{TEXT.scheduleLoading}</div>
    }
    if (!scheduleList.length) {
      return <div className="text-body-secondary">{TEXT.scheduleEmpty}</div>
    }

    return (
      <div className="table-responsive border rounded"> {/* Added border and rounded */}
        <table className="table table-sm align-middle mb-0">
          <thead className="table-light">
            <tr>
              <th className="text-center" style={{ width: '5.5rem' }}>{TEXT.scheduleDay}</th>
              <th className="text-center" style={{ width: '9rem' }}>{TEXT.scheduleTime}</th>
            </tr>
          </thead>
          <tbody>
            {scheduleList.map((s, idx) => (
              s.slots.map((slot, i) => (
                <tr key={`${idx}-${i}`}>
                  <td className="text-center">{DAY_LABEL[slot.day] || slot.day || '-'}</td>
                  <td className="text-center">
                    <div>{slot.start ? `${fmtTime(slot.start)} ~ ${fmtTime(slot.end)}` : '-'}</div>
                    {s.placeName === '공과대학본부관' ? (
                      <div className="small text-muted">
                        {s.placeName}
                        <br />
                        중레 강의실 209
                      </div>
                    ) : (
                      <div className="small text-muted">{s.placeName || s.placeCd || '강의실 미정'}</div>
                    )}
                  </td>
                </tr>
              ))
            ))}
          </tbody>
        </table>
      </div>
    )
  }, [scheduleList, loading])



  return (
    <section
      id="stu-lecture-root"
      className="container py-4"
      data-lecture-id={lectureId || ''}
      data-common-api={commonApiBase}
      data-student-api={studentApiBase}
    >
      <header className="mb-4">
        <h1 className="h3 mb-1 text-primary" id="stu-lecture-title">{title}</h1>
        <p className="text-muted mb-0" id="stu-lecture-subtitle">{subtitle}</p>
      </header>

      {error && (
        <div id="stu-lecture-alert" className="alert alert-danger" role="alert">
          {error || TEXT.genericError}
        </div>
      )}

      <div className="row g-4 align-items-stretch mb-4">
        {/* Left Column for Plan and Classmates */}
        <div className="col-12 col-xl-8 d-flex flex-column gap-4">
          <section id="plan-section" className="card h-100 flex-fill" aria-labelledby="plan-heading" style={{ height: '350px !important' }}>
            <div className="card-header d-flex align-items-center justify-content-between">
              <h2 className="h5 mb-0" id="plan-heading">🗓️ {TEXT.planHeading}</h2>
            </div>
            <div className="card-body d-flex flex-column">
              {planContent}
              <div id="plan-empty" className={`text-body-secondary small mt-3${planList.length || loading ? ' d-none' : ''}`}>
                {TEXT.planEmpty}
              </div>
            </div>
          </section>

          <section id="classmate-section" className="card h-100 flex-fill" aria-labelledby="classmate-heading">
            <div className="card-header d-flex flex-wrap align-items-center justify-content-between gap-2">
              <h2 className="h6 mb-0" id="classmate-heading">🧑‍🤝‍🧑 {TEXT.classmateHeading}</h2>
              <span className="badge bg-secondary-subtle text-secondary border border-secondary-subtle" id="classmate-total-badge">
                {classmates.length}{TEXT.countSuffix}
              </span>
            </div>
            <div className="card-body p-0"> {/* Removed padding to ensure table takes full space */}
              {loading && <div className="text-body-secondary text-center py-4">{TEXT.classmateLoading}</div>}
              {!loading && classmates.length === 0 ? (
                <div className="text-body-secondary text-center py-4">{TEXT.classmateEmpty}</div>
              ) : (
                <div className="p-3">
                  <div className="table-responsive" style={{ maxHeight: '240px', overflowY: 'auto' }}>
                    <table className="table table-sm table-hover align-middle mb-0">
                      <thead className="table-primary sticky-top">
                        <tr>
                          <th className="text-center">{TEXT.tableStudentNo}</th>
                          <th className="text-center">{TEXT.tableName}</th>
                          <th className="text-center">{TEXT.tableDept}</th>
                        </tr>
                      </thead>
                    <tbody>
                      {classmates.map((mate, idx) => {
                        const fullName = `${mate?.lastName || ''}${mate?.firstName || ''}`.trim() || mate?.studentName || TEXT.unknownStudentName;
                        const grade = mate?.gradeName || mate?.grade || '';
                        return (
                          <tr key={`${mate?.enrollId || mate?.studentNo || idx}`}>
                            <td className="text-center">{mate?.studentNo || '-'}</td>
                            <td className="text-center">
                              {fullName}
                              {grade ? (
                                <span className="badge bg-primary-subtle text-primary-emphasis border-primary-subtle ms-2">
                                  {grade}
                                </span>
                              ) : null}
                            </td>
                            <td className="text-center">{mate?.univDeptName || mate?.deptName || '-'}</td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
              )}
            </div>
          </section>
        </div>

        {/* Right Column for Overview and Schedule */}
        <div className="col-12 col-xl-4 d-flex flex-column gap-4">
          <section id="overview-section" className="card h-100 flex-fill" aria-labelledby="overview-heading" style={{ height: '350px !important' }}>
            <div className="card-header">
              <h2 className="h6 mb-0" id="overview-heading">📋 {TEXT.overviewHeading}</h2>
            </div>
            <div className="card-body px-3 d-flex flex-column" id="overview-body">
              {loading && <div className="text-body-secondary">{TEXT.overviewLoading}</div>}
              {!loading && !overview && <div className="text-body-secondary">{TEXT.classmateFallback}</div>}
              {!loading && overview && (
                <>
                  <div className="d-flex gap-3">
                    {professorPhotoUrl ? (
                      <img
                        src={professorPhotoUrl}
                        alt={`${overview.professorName || TEXT.overviewProfessorFallback}`}
                        className="overview-photo flex-shrink-0"
                      />
                    ) : (
                      <div className="overview-photo-placeholder flex-shrink-0">
                        {TEXT.overviewProfessorFallback}
                      </div>
                    )}
                    <div className="flex-grow-1">
                      <div className="text-muted small mb-1">
                        {formatYearTerm(overview.yeartermCd) || TEXT.unknownTerm}
                      </div>
                      <div className="fw-semibold">{overview.subjectName || TEXT.defaultTitle}</div>
                      <div className="small text-body-secondary">
                        {`${TEXT.overviewProfessorLabel}: ${overview.professorName || TEXT.overviewProfessorFallback}`}
                      </div>
                      <div className="small text-body-secondary">
                        {`${TEXT.overviewIndexLabel}: ${overview.lectureIndex || TEXT.overviewIndexFallback}`}
                      </div>
                    </div>
                  </div>
                  <div className="d-flex flex-column gap-0 mt-3 mb-0 border-top pt-3"> {/* Removed small class */}
                    <div className="d-flex">
                      <dt className="text-primary col-4" style={{ fontSize: '0.9rem' }}>{TEXT.overviewGoalLabel}</dt> {/* Increased font size */}
                      <dd className="mb-0 text-end col-8" style={{ fontSize: '0.9rem' }}>{renderMultiline(overview.lectureGoal, TEXT.overviewGoalFallback)}</dd> {/* Increased font size */}
                    </div>
                    <div className="d-flex">
                      <dt className="text-info col-4" style={{ fontSize: '0.9rem' }}>{TEXT.overviewPrereqLabel}</dt> {/* Increased font size */}
                      <dd className="mb-0 text-end col-8" style={{ fontSize: '0.9rem' }}>{overview.prereqSubject || TEXT.overviewPrereqFallback}</dd> {/* Increased font size */}
                    </div>
                    <div className="d-flex">
                      <dt className="text-success col-4" style={{ fontSize: '0.9rem' }}>{TEXT.overviewCapacityLabel}</dt> {/* Increased font size */}
                      <dd className="mb-0 text-end col-8" style={{ fontSize: '0.9rem' }}>
                        {Number.isFinite(Number(overview.maxCap))
                          ? `${overview.maxCap}${TEXT.countSuffix}`
                          : TEXT.overviewCapacityFallback}
                      </dd>
                    </div>
                    <div className="d-flex">
                      <dt className="text-danger col-4" style={{ fontSize: '0.9rem' }}>{TEXT.overviewEndLabel}</dt> {/* Increased font size */}
                      <dd className="mb-0 text-end col-8" style={{ fontSize: '0.9rem' }}>{overview.endAt ? formatDateOnly(overview.endAt) : TEXT.overviewEndFallback}</dd> {/* Increased font size */}
                    </div>
                  </div>
                </>
              ) }
            </div>
          </section>

          <section id="schedule-section" className="card h-100 flex-fill" aria-labelledby="schedule-heading">
            <div className="card-header">
              <h2 className="h6 mb-0" id="schedule-heading">🕒 {TEXT.scheduleHeading}</h2>
            </div>
            <div className="card-body" id="schedule-body">
              {scheduleContent}
            </div>
          </section>
        </div>
      </div>

    </section>
  )
}