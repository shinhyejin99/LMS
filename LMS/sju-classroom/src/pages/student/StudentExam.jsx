import React, { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

const pad = (n) => String(n).padStart(2, '0')
const fmtDateTime = (iso) => {
  if (!iso) return '-'
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return '-'
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}
const resolveStatus = (startAt, endAt) => {
  const now = Date.now()
  const start = startAt ? new Date(startAt).getTime() : null
  const finish = endAt ? new Date(endAt).getTime() : null
  if (start && now < start) return 'upcoming'
  if (start && (!finish || now <= finish)) return 'ongoing'
  if (finish && now > finish) return 'closed'
  return 'unknown'
}
const statusBadge = (status) => {
  switch (status) {
    case 'ongoing':
      return <span className="badge text-bg-success">진행 중</span>
    case 'upcoming':
      return <span className="badge text-bg-secondary">예정</span>
    case 'closed':
      return <span className="badge text-bg-dark">종료</span>
    default:
      return <span className="badge text-bg-light text-muted">-</span>
  }
}

const TYPE_LABEL = {
  OFF: '오프라인',
  ON: '온라인',
}

const TAB_ORDER = ['all', 'ongoing', 'upcoming', 'closed']
const TAB_LABEL = {
  ongoing: '진행 중',
  upcoming: '예정',
  closed: '종료',
  all: '전체',
}
const TAB_COLOR = {
  all: 'primary',
  ongoing: 'success',
  upcoming: 'warning',
  closed: 'danger',
}

const EMPTY_TEXT = {
  ongoing: '진행 중인 시험이 없습니다.',
  upcoming: '예정된 시험이 없습니다.',
  closed: '종료된 시험이 없습니다.',
  all: '출제된 시험이 없습니다.',
}

export default function StudentExam() {
  const { lectureId } = useParams()
  const [exams, setExams] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [activeTab, setActiveTab] = useState('all')

  useEffect(() => {
    let alive = true
    const load = async () => {
      if (!lectureId) {
        setError('강의 정보를 확인할 수 없습니다.')
        setLoading(false)
        return
      }
      try {
        setLoading(true)
        setError('')
        const res = await fetch(`/classroom/api/v1/student/${encodeURIComponent(lectureId)}/exam`, {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) {
          const text = await res.text().catch(() => '')
          throw new Error(text || `(${res.status}) 시험 목록을 불러오지 못했습니다.`)
        }
        const data = await res.json()
        if (!alive) return
        setExams(Array.isArray(data) ? data : [])
      } catch (err) {
        if (!alive) return
        setError(err instanceof Error ? err.message : '시험 목록을 불러오는 중 문제가 발생했습니다.')
        setExams([])
      } finally {
        if (alive) setLoading(false)
      }
    }
    load()
    return () => { alive = false }
  }, [lectureId])

  const withStatus = useMemo(() => {
    return exams.map((exam) => ({
      ...exam,
      status: resolveStatus(exam.startAt, exam.endAt),
    }))
  }, [exams])

  const buckets = useMemo(() => {
    const map = {
      ongoing: [],
      upcoming: [],
      closed: [],
      all: [],
    }
    withStatus.forEach((exam) => {
      map.all.push(exam)
      if (map[exam.status]) map[exam.status].push(exam)
    })
    return map
  }, [withStatus])

  const counts = useMemo(() => {
    return Object.fromEntries(TAB_ORDER.map((tab) => [tab, buckets[tab]?.length || 0]))
  }, [buckets])

  const handleTabChange = (next) => {
    setActiveTab(next)
  }

  const rowsForTab = buckets[activeTab] || []
  const emptyMessage = EMPTY_TEXT[activeTab] || '표시할 시험이 없습니다.'
  const baseUrl = `/classroom/student/${encodeURIComponent(lectureId || '')}`

  return (
    <section className="container py-4">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h3 mb-0">시험</h1>
        <p className="text-muted small mb-0">시험 응시 상태 및 점수를 확인할 수 있습니다.</p>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      <ul className="nav nav-tabs mb-0" role="tablist">
        {TAB_ORDER.map((tab) => {
          const color = TAB_COLOR[tab] || 'secondary'
          return (
            <li className="nav-item" key={tab} role="presentation">
              <button
                className={`nav-link ${activeTab === tab ? 'active' : ''}`}
                type="button"
                role="tab"
                aria-selected={activeTab === tab}
                onClick={() => handleTabChange(tab)}
              >
                {TAB_LABEL[tab]} <span className={`badge text-bg-${color} ms-1`}>{counts[tab]}</span>
              </button>
            </li>
          )
        })}
      </ul>

      <div className="tab-content border border-top-0 rounded-bottom p-3">
        <div id={`pane-${activeTab}`} className={`tab-pane fade show active p-0`} role="tabpanel" aria-labelledby={`tab-${activeTab}`}>
          <div className="table-responsive">
            <table className="table table-hover table-sm align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th style={{ width: '30%' }}>시험명</th>
                  <th style={{ width: '10%' }}>유형</th>
                  <th style={{ width: '20%' }}>응시 기간</th>
                  <th style={{ width: '10%' }}>가중치</th>
                  <th style={{ width: '10%' }}>진행 상태</th>
                  <th style={{ width: '10%' }}>응시 여부</th>
                  <th style={{ width: '10%' }} className="text-center">상세</th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td colSpan={7} className="text-center text-muted py-4">불러오는 중입니다...</td></tr>
                ) : rowsForTab.length === 0 ? (
                  <tr><td colSpan={7} className="text-center text-muted py-4">{emptyMessage}</td></tr>
                ) : (
                  rowsForTab
                    .sort((a, b) => {
                      const aTime = a.startAt ? new Date(a.startAt).getTime() : 0
                      const bTime = b.startAt ? new Date(b.startAt).getTime() : 0
                      return bTime - aTime
                    })
                    .map((exam) => {
                      const period = `${fmtDateTime(exam.startAt)} ~ ${fmtDateTime(exam.endAt)}`
                      return (
                        <tr key={exam.lctExamId}>
                          <td>
                            <div className="fw-semibold">{exam.examName}</div>
                            <div className="text-muted small">등록일 {fmtDateTime(exam.createAt)}</div>
                          </td>
                          <td>{TYPE_LABEL[exam.examType] || '기타'}</td>
                          <td className="text-muted small">{period}</td>
                          <td>{Number.isFinite(Number(exam.weightValue)) ? `${exam.weightValue}%` : '-'}</td>
                          <td>{statusBadge(exam.status)}</td>
                          <td>
                            {exam.submitted
                              ? <span className="badge text-bg-success">응시 완료</span>
                              : <span className="badge text-bg-secondary">미응시</span>}
                          </td>
                          <td className="text-center">
                            <Link className="btn btn-sm btn-outline-primary" to={`${baseUrl}/exam/${encodeURIComponent(exam.lctExamId)}`}>
                              결과확인
                            </Link>
                          </td>
                        </tr>
                      )
                    })
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>
  )
}
