import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { createPortal } from 'react-dom'
import { Link, useParams } from 'react-router-dom'
import '../../styles/customButtons.css'

const pad = (value) => String(value).padStart(2, '0')
const fmtDateTime = (iso) => {
  if (!iso) return '-'
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return '-'
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}
const resolveStatus = (startAt, endAt) => {
  const now = Date.now()
  const start = startAt ? new Date(startAt).getTime() : undefined
  const finish = endAt ? new Date(endAt).getTime() : undefined
  if (typeof start === 'number' && start > now) return 'upcoming'
  if (typeof start === 'number' && start <= now) {
    if (typeof finish !== 'number' || finish >= now) return 'ongoing'
    if (finish < now) return 'closed'
  }
  if (typeof finish === 'number' && finish < now) return 'closed'
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

function WeightModal({ open, entries, total, error, saving, onChange, onSubmit, onClose }) {
  useEffect(() => {
    if (!open) return
    const previous = document.body.style.overflow
    const handleKey = (event) => {
      if (event.key === 'Escape') onClose()
    }
    document.body.style.overflow = 'hidden'
    document.addEventListener('keydown', handleKey)
    return () => {
      document.body.style.overflow = previous
      document.removeEventListener('keydown', handleKey)
    }
  }, [open, onClose])

  if (!open) return null

  return createPortal(
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="exam-weight-modal-title"
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.45)',
        zIndex: 1050,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '1.5rem',
      }}
    >
      <div
        className="card shadow-lg"
        style={{ width: 'min(720px, 100%)', maxHeight: '90vh', overflow: 'hidden' }}
      >
        <form onSubmit={onSubmit} className="d-flex flex-column" style={{ height: '100%' }}>
          <div className="card-header d-flex align-items-center justify-content-between">
            <h2 className="h5 mb-0" id="exam-weight-modal-title">시험 가중치 일괄 설정</h2>
            <button type="button" className="btn-close" aria-label="닫기" onClick={onClose} disabled={saving} />
          </div>
          <div className="card-body p-0" style={{ overflowY: 'auto' }}>
            {error && <div className="alert alert-danger" role="alert">{error}</div>}
            <div className="table-responsive">
              <table className="table table-sm align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th scope="col" style={{ width: 56 }}>#</th>
                    <th scope="col">시험명</th>
                    <th scope="col" className="text-center" style={{ width: 200 }}>시험 기간</th>
                    <th scope="col" className="text-end" style={{ width: 160 }}>반영 비율</th>
                  </tr>
                </thead>
                <tbody>
                  {entries.length === 0 ? (
                    <tr><td colSpan={4} className="text-center text-muted py-4">표시할 시험이 없습니다.</td></tr>
                  ) : entries.map((entry, index) => {
                    const start = entry.startAt ? fmtDateTime(entry.startAt) : '-'
                    const end = entry.endAt ? fmtDateTime(entry.endAt) : '-'
                    return (
                      <tr key={entry.id || index}>
                        <td className="text-muted">#{index + 1}</td>
                        <td>
                          <div className="fw-semibold text-truncate" title={entry.examName}>{entry.examName}</div>
                          <div className="text-muted small">{entry.examTypeLabel}</div>
                        </td>
                        <td className="text-center text-muted small" dangerouslySetInnerHTML={{ __html: `${start}<br>~ ${end}` }}></td>
                        <td className="text-end">
                          <input
                            type="text"
                            className="form-control form-control-sm text-end"
                            style={{ width: '50%', marginLeft: 'auto' }}
                            value={entry.value}
                            onChange={(event) => onChange(entry.id, event.target.value)}
                            disabled={saving}
                            required
                          />
                        </td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          </div>
          <div className="card-footer d-flex flex-column flex-sm-row align-items-sm-center justify-content-between gap-2">
            <div className="fw-semibold">
              반영 비율 합계:&nbsp;
              <span className={total === 100 ? 'text-success' : 'text-danger'}>{total}</span>
            </div>
            <div className="text-muted small flex-grow-1 text-sm-end">모든 시험 합계가 100이 되도록 설정하세요.</div>
            <div className="d-flex gap-2">
              <button type="submit" className="btn btn-primary" disabled={saving || entries.length === 0} style={{ padding: '6px 12px' }}>
                {saving ? '저장 중...' : '저장'}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>,
    document.body
  )
}

export default function Exam() {
  const { lectureId } = useParams()
  const [exams, setExams] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [notice, setNotice] = useState(null)
  const [weightModalOpen, setWeightModalOpen] = useState(false)
  const [weightEntries, setWeightEntries] = useState([])
  const [weightError, setWeightError] = useState('')
  const [weightSaving, setWeightSaving] = useState(false)

  const loadExams = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const url = `/classroom/api/v1/professor/exam/${encodeURIComponent(lectureId)}`
      const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
      if (!res.ok) throw new Error(`(${res.status}) 시험 목록을 불러오지 못했습니다.`)
      const data = await res.json()
      if (!Array.isArray(data)) throw new Error('시험 데이터 형식이 올바르지 않습니다.')
      setExams(data)
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }, [lectureId])

  useEffect(() => {
    loadExams()
  }, [loadExams])

  const buckets = useMemo(() => {
    const all = []
    const upcoming = []
    const ongoing = []
    const closed = []
    exams.forEach((exam) => {
      const status = resolveStatus(exam.startAt, exam.endAt)
      if (status === 'upcoming') {
        upcoming.push(exam)
      } else if (status === 'ongoing') {
        ongoing.push(exam)
      } else if (status === 'closed') {
        closed.push(exam)
      }
      all.push(exam)
    })
    return { all, upcoming, ongoing, closed }
  }, [exams])

  const Row = (record) => {
    const examId = record.lctExamId ?? record.id
    const examName = record.examName || record.title || '(제목 없음)'
    const detailUrl = lectureId && examId ? `/classroom/professor/${encodeURIComponent(lectureId)}/exam/${encodeURIComponent(examId)}` : null
    const status = resolveStatus(record.startAt, record.endAt)
    const period = `${fmtDateTime(record.startAt)} ~ ${fmtDateTime(record.endAt)}`
    return (
      <tr>
        <td>
          <div className="fw-semibold">{examName}</div>
          <div className="text-muted small">등록일 {fmtDateTime(record.createAt || record.createdAt)}</div>
        </td>
        <td>{TYPE_LABEL[record.examType] || record.examTypeName || record.examType || '기타'}</td>
        <td className="text-muted small">{period}</td>
        <td>{Number.isFinite(Number(record.weightValue)) ? `${record.weightValue}%` : '-'}</td>
        <td>{statusBadge(status)}</td>
        <td className="text-center">
          {detailUrl ? (
            <Link className="btn btn-sm btn-outline-primary" to={detailUrl}>
              보기
            </Link>
          ) : (
            '-'
          )}
        </td>
      </tr>
    )
  }

  useEffect(() => {
    if (!weightModalOpen) {
      setWeightEntries(exams.map((exam) => ({
        id: exam.lctExamId ?? exam.id,
        examName: exam.examName || exam.title || '(제목 없음)',
        examTypeLabel: exam.examTypeName || exam.examType || '구분 없음',
        startAt: exam.startAt,
        endAt: exam.endAt,
        value: exam.weightValue != null ? String(exam.weightValue) : '',
      })).filter((entry) => entry.id != null))
    }
  }, [exams, weightModalOpen])

  const handleOpenWeightModal = useCallback(() => {
    if (loading || exams.length === 0) return
    setWeightError('')
    setWeightEntries(exams.map((exam) => ({
      id: exam.lctExamId ?? exam.id,
      examName: exam.examName || exam.title || '(제목 없음)',
      examTypeLabel: exam.examTypeName || exam.examType || '구분 없음',
      startAt: exam.startAt,
      endAt: exam.endAt,
      value: exam.weightValue != null ? String(exam.weightValue) : '',
    })).filter((entry) => entry.id != null))
    setWeightModalOpen(true)
  }, [exams, loading])

  const handleCloseWeightModal = useCallback(() => {
    if (weightSaving) return
    setWeightModalOpen(false)
    setWeightError('')
  }, [weightSaving])

  const handleWeightChange = useCallback((examId, nextValue) => {
    setWeightEntries((prev) => prev.map((entry) => {
      if (entry.id !== examId) return entry
      const digits = nextValue.replace(/[^0-9]/g, '').slice(0, 3)
      if (digits === '') return { ...entry, value: '' }
      const numeric = Math.min(Number.parseInt(digits, 10) || 0, 100)
      return { ...entry, value: String(numeric) }
    }))
    setWeightError('')
  }, [])

  const weightTotal = useMemo(
    () => weightEntries.reduce((sum, entry) => sum + (Number.parseInt(entry.value, 10) || 0), 0),
    [weightEntries],
  )

  const handleWeightSubmit = useCallback(async (event) => {
    event.preventDefault()
    if (weightSaving) return

    if (!weightEntries.length) {
      setWeightError('저장할 시험 정보가 없습니다.')
      return
    }

    const payload = []
    let sum = 0
    for (const entry of weightEntries) {
      const value = Number.parseInt(entry.value, 10)
      if (!entry.id) {
        setWeightError('시험 ID가 누락되었습니다.')
        return
      }
      if (!Number.isInteger(value) || value < 0 || value > 100) {
        setWeightError(`${entry.examName}의 반영 비율을 0 이상 100 이하로 입력해주세요.`)
        return
      }
      sum += value
      payload.push({ lctExamId: entry.id, weightValue: value })
    }

    if (sum !== 100) {
      setWeightError(`반영 비율 합계가 ${sum}입니다. 100이 되도록 조정해주세요.`)
      return
    }

    try {
      setWeightSaving(true)
      setWeightError('')
      const url = `/classroom/api/v1/professor/exam/${encodeURIComponent(lectureId)}/weightValue`
      const res = await fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload),
      })
      if (!res.ok) throw new Error(`(${res.status}) 반영 비율 저장에 실패했습니다.`)
      setNotice({ type: 'success', message: '시험 가중치가 저장되었습니다.' })
      setWeightModalOpen(false)
      await loadExams()
    } catch (err) {
      console.error('[Exam] Failed to update weight values:', err)
      setWeightError(err instanceof Error ? err.message : '가중치 저장 중 오류가 발생했습니다.')
    } finally {
      setWeightSaving(false)
    }
  }, [weightEntries, weightSaving, lectureId, loadExams])

  useEffect(() => {
    if (!notice) return
    const timer = window.setTimeout(() => setNotice(null), 4000)
    return () => window.clearTimeout(timer)
  }, [notice])

  const weightButtonDisabled = loading || exams.length === 0 || weightSaving

  return (
    <section id="exam-root" className="container py-0" data-lecture-id={lectureId}>
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h3 mb-0">시험</h1>
        <div className="d-flex align-items-center gap-2">
          <Link to={`/classroom/professor/${encodeURIComponent(lectureId)}/exam/offline/new`} className="custom-btn custom-btn-primary custom-btn-sm">시험 등록</Link>
          <button type="button" className="custom-btn custom-btn-outline-primary custom-btn-sm" onClick={handleOpenWeightModal} disabled={weightButtonDisabled}>가중치 일괄설정</button>
        </div>
      </div>

      {loading && <div className="text-muted">불러오는 중...</div>}
      {error && <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>}
      {notice && !error && <div className={`alert mt-3 ${notice.type === 'success' ? 'alert-success' : 'alert-danger'}`} role="alert">{notice.message}</div>}

      {!loading && !error && (
        <div className="card shadow-sm p-0">
          <div className="card-header">
            <h2 className="h5 mb-0">시험 목록</h2>
          </div>
          <div className="card-body p-3">
            <ul className="nav nav-pills mb-3" id="exam-status-tabs" role="tablist">
              <li className="nav-item" role="presentation">
                <button className="nav-link active" id="exam-tab-all-btn" data-bs-toggle="tab" data-bs-target="#exam-tab-all" type="button" role="tab">
                  전체 <span className="badge bg-primary ms-1" id="exam-count-all">{buckets.all.length}</span>
                </button>
              </li>
              <li className="nav-item" role="presentation">
                <button className="nav-link" id="exam-tab-upcoming-btn" data-bs-toggle="tab" data-bs-target="#exam-tab-upcoming" type="button" role="tab">
                  예정 <span className="badge bg-info ms-1" id="exam-count-upcoming">{buckets.upcoming.length}</span>
                </button>
              </li>
              <li className="nav-item" role="presentation">
                <button className="nav-link" id="exam-tab-ongoing-btn" data-bs-toggle="tab" data-bs-target="#exam-tab-ongoing" type="button" role="tab">
                  진행중 <span className="badge bg-success text-white ms-1" id="exam-count-ongoing">{buckets.ongoing.length}</span>
                </button>
              </li>
              <li className="nav-item" role="presentation">
                <button className="nav-link" id="exam-tab-closed-btn" data-bs-toggle="tab" data-bs-target="#exam-tab-closed" type="button" role="tab">
                  종료 <span className="badge bg-secondary ms-1" id="exam-count-closed">{buckets.closed.length}</span>
                </button>
              </li>
            </ul>

            <div className="tab-content border-top" id="exam-status-content">
              <div className="tab-pane fade show active" id="exam-tab-all" role="tabpanel" aria-labelledby="exam-tab-all-btn">
                <div className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th style={{ width: '30%' }}>시험명</th>
                        <th style={{ width: '10%' }}>유형</th>
                        <th style={{ width: '20%' }}>응시 기간</th>
                        <th style={{ width: '10%' }}>가중치</th>
                        <th style={{ width: '10%' }}>진행 상태</th>
                        <th style={{ width: '10%' }} className="text-center">상세</th>
                      </tr>
                    </thead>
                    <tbody>
                      {buckets.all.length === 0 ? (
                        <tr><td colSpan={6} className="text-center text-muted py-4">등록된 시험이 없습니다.</td></tr>
                      ) : buckets.all.map((exam) => <Row key={exam.lctExamId || exam.id} {...exam} />)}
                    </tbody>
                  </table>
                </div>
              </div>
              <div className="tab-pane fade" id="exam-tab-upcoming" role="tabpanel" aria-labelledby="exam-tab-upcoming-btn">
                <div className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th style={{ width: '30%' }}>시험명</th>
                        <th style={{ width: '10%' }}>유형</th>
                        <th style={{ width: '20%' }}>응시 기간</th>
                        <th style={{ width: '10%' }}>가중치</th>
                        <th style={{ width: '10%' }}>진행 상태</th>
                        <th style={{ width: '10%' }} className="text-center">상세</th>
                      </tr>
                    </thead>
                    <tbody>
                      {buckets.upcoming.length === 0 ? (
                        <tr><td colSpan={6} className="text-center text-muted py-4">예정된 시험이 없습니다.</td></tr>
                      ) : buckets.upcoming.map((exam) => <Row key={exam.lctExamId || exam.id} {...exam} />)}
                    </tbody>
                  </table>
                </div>
              </div>
              <div className="tab-pane fade" id="exam-tab-ongoing" role="tabpanel" aria-labelledby="exam-tab-ongoing-btn">
                <div className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th style={{ width: '30%' }}>시험명</th>
                        <th style={{ width: '10%' }}>유형</th>
                        <th style={{ width: '20%' }}>응시 기간</th>
                        <th style={{ width: '10%' }}>가중치</th>
                        <th style={{ width: '10%' }}>진행 상태</th>
                        <th style={{ width: '10%' }} className="text-center">상세</th>
                      </tr>
                    </thead>
                    <tbody>
                      {buckets.ongoing.length === 0 ? (
                        <tr><td colSpan={6} className="text-center text-muted py-4">진행중인 시험이 없습니다.</td></tr>
                      ) : buckets.ongoing.map((exam) => <Row key={exam.lctExamId || exam.id} {...exam} />)}
                    </tbody>
                  </table>
                </div>
              </div>
              <div className="tab-pane fade" id="exam-tab-closed" role="tabpanel" aria-labelledby="exam-tab-closed-btn">
                <div className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th style={{ width: '30%' }}>시험명</th>
                        <th style={{ width: '10%' }}>유형</th>
                        <th style={{ width: '20%' }}>응시 기간</th>
                        <th style={{ width: '10%' }}>가중치</th>
                        <th style={{ width: '10%' }}>진행 상태</th>
                        <th style={{ width: '10%' }} className="text-center">상세</th>
                      </tr>
                    </thead>
                    <tbody>
                      {buckets.closed.length === 0 ? (
                        <tr><td colSpan={6} className="text-center text-muted py-4">종료된 시험이 없습니다.</td></tr>
                      ) : buckets.closed.map((exam) => <Row key={exam.lctExamId || exam.id} {...exam} />)}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      <WeightModal
        open={weightModalOpen}
        entries={weightEntries}
        total={weightTotal}
        error={weightError}
        saving={weightSaving}
        onChange={handleWeightChange}
        onSubmit={handleWeightSubmit}
        onClose={handleCloseWeightModal}
      />
    </section>
  )
}
