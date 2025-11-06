import React, { Fragment, useEffect, useMemo, useState } from 'react'
import { useParams } from 'react-router-dom'
import { useLecture } from '../context/LectureContext'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Tooltip, Legend } from 'chart.js'
import { Bar } from 'react-chartjs-2'
import '../styles/classroom.css'


ChartJS.register(CategoryScale, LinearScale, BarElement, Tooltip, Legend)

const TEXT = {
  defaultTitle: '강의 홈',
  subtitleLoading: '강의 정보를 불러오는 중입니다...', 
  planHeading: '주차별 수업 계획',
  planLoadingTitle: '주차 정보를 불러오는 중입니다...', 
  planLoadingBody: '기다려 주세요.',
  planEmpty: '등록된 수업 계획이 없습니다.',
  planGoalLabel: '수업 목표',
  planDescLabel: '수업 내용',
  overviewHeading: '강의 개요',
  overviewLoading: '강의 정보를 불러오는 중입니다...', 
  overviewProfessorLabel: '담당 교수',
  overviewIndexLabel: '학정번호',
  overviewGoalLabel: '수업 목표',
  overviewPrereqLabel: '선수 과목',
  overviewCapacityLabel: '정원',
  overviewEndLabel: '종강일',
  overviewGoalFallback: '등록된 정보가 없습니다.',
  overviewPrereqFallback: '정확한 정보가 없습니다.',
  overviewCapacityFallback: '등록된 정원이 없습니다.',
  overviewProfessorFallback: '알 수 없음',
  overviewIndexFallback: '정보 없음',
  overviewEndFallback: '미정',
  scheduleHeading: '강의 시간/강의실',
  scheduleLoading: '시간표 정보를 불러오는 중입니다...', 
  scheduleEmpty: '등록된 시간표가 없습니다.',
  scheduleDay: '요일',
  scheduleTime: '시간',
  schedulePlace: '강의실',
  gradeRatioHeading: '성적 산출 비율',
  gradeRatioLoading: '성적 산출 비율을 불러오는 중입니다...', 
  gradeRatioEmpty: '등록된 성적 산출 비율이 없습니다.',
  gradeRatioLabel: '성적 산출 비율',
  genericError: '데이터를 불러오는 중 문제가 발생했습니다.',
  missingLecture: '강의 정보가 필요합니다.',
}

const DAY_LABEL = {
  MO: '월',
  TU: '화',
  WE: '수',
  TH: '목',
  FR: '금',
  SA: '토',
  SU: '일',
}

const commonApiBase = '/classroom/api/v1/common'

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

export default function ProfessorLectureSummary() {
  const { lectureId } = useParams()
  const [info, setInfo] = useState(null)
  const [schedule, setSchedule] = useState([])
  const [planList, setPlanList] = useState([])
  const [activeTab, setActiveTab] = useState(1) // Renamed from planWeek to activeTab
  const [ratioData, setRatioData] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const { setLecture, formatYearTerm } = useLecture()
  const [title, setTitle] = useState(TEXT.defaultTitle)
  const [subtitle, setSubtitle] = useState(TEXT.subtitleLoading)

  const fmtTime = (hhmm) => {
    if (!hhmm || String(hhmm).length < 4) return '--:--'
    const v = String(hhmm)
    return `${v.slice(0, 2)}:${v.slice(2, 4)}`
  }

  const professorPhotoUrl = useMemo(() => {
    if (!info?.professorNo) return null
    return `${commonApiBase}/photo/professor/${encodeURIComponent(String(info.professorNo).trim())}`
  }, [info?.professorNo])

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const base = `/classroom/api/v1/common/${encodeURIComponent(lectureId)}`
        const [infoRes, schRes, planRes, ratioRes] = await Promise.all([
          fetch(base, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/schedule`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/plan`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/ratio`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
        ])
        if (!infoRes.ok) throw new Error(`(${infoRes.status}) 강의 정보를 불러오지 못했습니다.`)
        if (!schRes.ok) throw new Error(`(${schRes.status}) 강의 시간표를 불러오지 못했습니다.`)
        if (!planRes.ok) throw new Error(`(${planRes.status}) 주차계획을 불러오지 못했습니다.`)
        const infoJson = await infoRes.json()
        const schJson = await schRes.json()
        const planJson = await planRes.json()
        const ratioJson = ratioRes.ok ? await ratioRes.json() : []
        if (!alive) return
        setInfo(infoJson)
        setLecture(infoJson)
        if (infoJson) {
          setTitle(infoJson.subjectName || TEXT.defaultTitle)
          const prof = infoJson.professorName || TEXT.overviewProfessorFallback
          const term = formatYearTerm(infoJson.yeartermCd) || TEXT.unknownTerm
          setSubtitle(`${prof} \u00b7 ${term}`)
        } else {
          setTitle(TEXT.defaultTitle)
          setSubtitle(TEXT.subtitleLoading)
        }
        setSchedule(Array.isArray(schJson) ? schJson : [])
        setRatioData(Array.isArray(ratioJson) ? ratioJson : [])
        const list = Array.isArray(planJson) ? planJson : []
        setPlanList(list)
        if (list.length) {
          const minWeek = list.reduce((m, it) => {
            const w = Number(it.lectureWeek)
            return Number.isFinite(w) ? Math.min(m, w) : m
          }, Number(list[0].lectureWeek) || 1)
          setActiveTab(minWeek)
        } else {
          setActiveTab(null)
        }
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, setLecture, formatYearTerm])

  const totalRatio = ratioData.reduce((sum, item) => sum + (item.ratio || 0), 0)
  const isValidRatio = totalRatio === 100

  const colors = [
    'rgba(54, 162, 235, 0.8)',
    'rgba(255, 99, 132, 0.8)',
    'rgba(255, 206, 86, 0.8)',
    'rgba(75, 192, 192, 0.8)',
    'rgba(153, 102, 255, 0.8)',
  ]

  const barChartData = {
    labels: ['성적 산출 비율'],
    datasets: ratioData.map((item, idx) => ({
      label: item.gradeCriteriaName || item.gradeCriteriaCd,
      data: [item.ratio || 0],
      backgroundColor: colors[idx % colors.length],
      borderWidth: 0,
    })),
  }

  const barChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    indexAxis: 'y',
    scales: {
      x: {
        stacked: true,
        beginAtZero: true,
        max: 100,
        ticks: {
          callback: (value) => `${value}%`,
        },
      },
      y: {
        stacked: true,
      },
    },
    plugins: {
      legend: {
        display: true, // Enable legend
        position: 'bottom', // Position legend at the bottom
      },
      tooltip: {
        enabled: true, // Enable tooltip
        callbacks: {
          label: function(context) {
            let label = context.dataset.label || '';
            if (label) {
              label += ': ';
            }
            if (context.parsed.x !== null) {
              label += context.parsed.x + '%';
            }
            return label;
          }
        }
      },
    },
  }

  return (
    <section
      id="prof-lecture-root"
      className="container py-4"
      data-lecture-id={lectureId || ''}
      data-common-api={commonApiBase}
    >
      <header className="mb-4">
        <h1 className="h3 mb-1 text-primary" id="prof-lecture-title">{title}</h1>
        <p className="text-muted mb-0" id="prof-lecture-subtitle">{subtitle}</p>
      </header>

      {error && (
        <div id="prof-lecture-alert" className="alert alert-danger" role="alert">
          {error || TEXT.genericError}
        </div>
      )}

      {loading && <div className="text-muted text-center py-4">불러오는 중…</div>}

      {!loading && !error && (
        <div className="row g-4 align-items-stretch mb-4">
          {/* Left Column for Plan and Grade Ratio */}
          <div className="col-12 col-xl-8 d-flex flex-column gap-4">
            {/* 주차 계획 */}
            <section id="plan-section" className="card h-100 flex-fill" aria-labelledby="plan-heading" style={{ height: '350px !important' }}>
              <div className="card-header d-flex align-items-center justify-content-between">
                <h2 className="h5 mb-0" id="plan-heading">🗓️ {TEXT.planHeading}</h2>
              </div>
              <div className="card-body d-flex flex-column">
                <ul className="nav nav-tabs mb-3">
                  {planList
                    .slice()
                    .sort((a, b) => Number(a.lectureWeek || 0) - Number(b.lectureWeek || 0))
                    .map((p) => (
                      <li className="nav-item" key={p.lectureWeek}>
                        <button
                          className={`nav-link ${activeTab === p.lectureWeek ? 'active' : ''}`}
                          onClick={() => setActiveTab(p.lectureWeek)}
                        >
                          {p.lectureWeek}주차
                        </button>
                      </li>
                    ))}
                </ul>
                <div className="tab-content flex-grow-1 border border-top-0 rounded-bottom p-3">
                  {!activeTab ? (
                    <div className="text-muted">{TEXT.planEmpty}</div>
                  ) : (
                    (() => {
                      const cur = planList.find((p) => Number(p.lectureWeek) === Number(activeTab));
                      if (!cur) return <div className="text-muted">{TEXT.planEmpty}</div>;
                      return (
                        <div className="tab-pane fade show active">
                          <div className="mb-2">
                            <div className="fw-semibold mb-1 text-primary">
                              <i className="bi bi-bullseye me-2"></i>{TEXT.planGoalLabel}
                            </div>
                            <p className="mb-0">{renderMultiline(cur.weekGoal, TEXT.overviewGoalFallback)}</p>
                          </div>
                          <div className="fw-semibold mb-1">
                            <i className="bi bi-journal-text me-2"></i>{TEXT.planDescLabel}
                          </div>
                          <p className="text-break mb-0">{renderMultiline(cur.weekDesc, TEXT.overviewGoalFallback)}</p>
                        </div>
                      );
                    })()
                  )}
                </div>
              </div>
            </section>

            {/* 성적 산출 비율 */}
            {ratioData.length > 0 && (
              <section id="grade-ratio-section" className="card h-100 flex-fill" aria-labelledby="grade-ratio-heading">
                <div className="card-header">
                  <h2 className="h6 mb-0" id="grade-ratio-heading">📊 {TEXT.gradeRatioHeading}</h2>
                </div>
                <div className="card-body">
                  <div style={{ height: '200px' }}>
                    <Bar data={barChartData} options={barChartOptions} />
                  </div>
                </div>
              </section>
            )}
          </div>

          {/* Right Column for Overview and Schedule */}
          <div className="col-12 col-xl-4 d-flex flex-column gap-4">
            {/* 강의 정보 */}
            {info && (
              <section id="overview-section" className="card h-100 flex-fill" aria-labelledby="overview-heading" style={{ height: '350px !important' }}>
                <div className="card-header">
                  <h2 className="h6 mb-0" id="overview-heading">📋 {TEXT.overviewHeading}</h2>
                </div>
                <div className="card-body px-3 d-flex flex-column" id="overview-body">
                  <div className="d-flex gap-3">
                    {professorPhotoUrl ? (
                      <img
                        src={professorPhotoUrl}
                        alt={`${info.professorName || TEXT.overviewProfessorFallback}`}
                        className="overview-photo flex-shrink-0"
                      />
                    ) : (
                      <div className="overview-photo-placeholder flex-shrink-0">
                        {TEXT.overviewProfessorFallback}
                      </div>
                    )}
                    <div className="flex-grow-1">
                      <div className="text-muted small mb-1">
                        {formatYearTerm(info.yeartermCd) || TEXT.unknownTerm}
                      </div>
                      <div className="fw-semibold">{info.subjectName || TEXT.defaultTitle}</div>
                      <div className="small text-body-secondary">
                        {`${TEXT.overviewProfessorLabel}: ${info.professorName || TEXT.overviewProfessorFallback}`}
                      </div>
                      <div className="small text-body-secondary">
                        {`${TEXT.overviewIndexLabel}: ${info.lectureIndex || TEXT.overviewIndexFallback}`}
                      </div>
                    </div>
                  </div>
                  <div className="d-flex flex-column gap-0 mt-3 mb-0 border-top pt-3">
                    <div className="d-flex">
                      <dt className="text-primary col-4" style={{ fontSize: '0.9rem' }}>{TEXT.overviewGoalLabel}</dt>
                      <dd className="mb-0 text-end col-8" style={{ fontSize: '0.9rem' }}>{renderMultiline(info.lectureGoal, TEXT.overviewGoalFallback)}</dd>
                    </div>
                    <div className="d-flex">
                      <dt className="text-info col-4" style={{ fontSize: '0.9rem' }}>{TEXT.overviewPrereqLabel}</dt>
                      <dd className="mb-0 text-end col-8" style={{ fontSize: '0.9rem' }}>{info.prereqSubject || TEXT.overviewPrereqFallback}</dd>
                    </div>
                    <div className="d-flex">
                      <dt className="text-success col-4" style={{ fontSize: '0.9rem' }}>{TEXT.overviewCapacityLabel}</dt>
                      <dd className="mb-0 text-end col-8" style={{ fontSize: '0.9rem' }}>
                        {Number.isFinite(Number(info.maxCap))
                          ? `${info.maxCap}명`
                          : TEXT.overviewCapacityFallback}
                      </dd>
                    </div>
                    <div className="d-flex">
                      <dt className="text-danger col-4" style={{ fontSize: '0.9rem' }}>{TEXT.overviewEndLabel}</dt>
                      <dd className="mb-0 text-end col-8" style={{ fontSize: '0.9rem' }}>{info.endAt ? formatDateOnly(info.endAt) : TEXT.overviewEndFallback}</dd>
                    </div>
                  </div>
                </div>
              </section>
            )}

            {/* 강의 시간표 */}
            <section id="schedule-section" className="card h-100 flex-fill" aria-labelledby="schedule-heading">
              <div className="card-header">
                <h2 className="h6 mb-0" id="schedule-heading">🕒 {TEXT.scheduleHeading}</h2>
              </div>
              <div className="card-body">
                {schedule.length === 0 ? (
                  <div className="text-body-secondary">{TEXT.scheduleEmpty}</div>
                ) : (
                  <div className="table-responsive border rounded">
                    <table className="table table-sm align-middle mb-0">
                      <thead className="table-light">
                        <tr>
                          <th className="text-center" style={{ width: '5.5rem' }}>{TEXT.scheduleDay}</th>
                          <th className="text-center" style={{ width: '9rem' }}>{TEXT.scheduleTime}</th>
                          <th>{TEXT.schedulePlace}</th>
                        </tr>
                      </thead>
                      <tbody>
                        {schedule.map((s, idx) => (
                          s.slots.map((slot, i) => (
                            <tr key={`${idx}-${i}`}>
                              <td className="text-center">{DAY_LABEL[slot.day] ?? slot.day ?? ''}</td>
                              <td className="text-center">{fmtTime(slot.start)} ~ {fmtTime(slot.end)}</td>
                              <td>
                                {s.placeName === '공과대학본부관' ? (
                                  <>
                                    {s.placeName}
                                    <br />
                                    중레 강의실 209
                                  </>
                                ) : (
                                  s.placeName || s.placeCd || '강의실 미정'
                                )}
                              </td>
                            </tr>
                          ))
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </section>
          </div>
        </div>
      )}
    </section>
  );

}
