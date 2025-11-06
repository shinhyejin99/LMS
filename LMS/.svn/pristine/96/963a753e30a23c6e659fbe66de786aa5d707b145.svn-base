import React, { useEffect, useMemo, useState, useCallback } from 'react'
import { useParams, Link } from 'react-router-dom'
import { createPortal } from 'react-dom'

const fmtDateTime = (iso) => {
  if (!iso) return '-'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return '-'
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const fmtDate = (iso) => {
  if (!iso) return '-'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return '-'
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

const resolveStatus = (startAt, endAt) => {
  const now = Date.now()
  const s = startAt ? new Date(startAt).getTime() : null
  const e = endAt ? new Date(endAt).getTime() : null
  if (s && s > now) return 'upcoming'
  if (s && s <= now) {
    if (!e || e >= now) return 'ongoing'
    if (e < now) return 'closed'
  }
  return 'ongoing'
}

function TaskWeightModal({ open, entries, total, error, saving, onChange, onSubmit, onClose }) {
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
      aria-labelledby="task-weight-modal-title"
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
        style={{ width: 'min(800px, 100%)', maxHeight: '90vh' }}
      >
        <form onSubmit={onSubmit} style={{ display: 'flex', flexDirection: 'column', maxHeight: '90vh' }}>
          <div className="card-header d-flex align-items-center justify-content-between">
            <h2 className="h5 mb-0" id="task-weight-modal-title">과제 가중치 일괄 설정</h2>
            <button type="button" className="btn-close" aria-label="닫기" onClick={onClose} disabled={saving} />
          </div>
          {error && <div className="alert alert-danger m-3 mb-0" role="alert">{error}</div>}
          <div className="card-body p-0" style={{ overflowY: 'auto', flex: '1 1 auto' }}>
            <table className="table table-sm align-middle mb-0">
              <thead className="table-light" style={{ position: 'sticky', top: 0, zIndex: 1, backgroundColor: '#f8f9fa' }}>
                <tr>
                  <th scope="col" style={{ width: 56 }}>#</th>
                  <th scope="col">과제명</th>
                  <th scope="col" className="text-center" style={{ width: 200 }}>과제 기간</th>
                  <th scope="col" className="text-end" style={{ width: 160 }}>반영 비율</th>
                </tr>
              </thead>
              <tbody>
                {entries.length === 0 ? (
                  <tr><td colSpan={4} className="text-center text-muted py-4">표시할 과제가 없습니다.</td></tr>
                ) : entries.map((entry, index) => {
                  const start = entry.startAt ? fmtDateTime(entry.startAt) : '-'
                  const end = entry.endAt ? fmtDateTime(entry.endAt) : '-'
                  return (
                    <tr key={entry.taskId || index}>
                      <td className="text-muted">#{index + 1}</td>
                      <td>
                        <div className="fw-semibold text-truncate" title={entry.taskName}>{entry.taskName}</div>
                        <div className="text-muted small">{entry.taskType === 'INDIV' ? '개인' : '조별'}</div>
                      </td>
                      <td className="text-center text-muted small" dangerouslySetInnerHTML={{ __html: `${start}<br>~ ${end}` }}></td>
                      <td className="text-end">
                        <input
                          type="text"
                          className="form-control form-control-sm text-end"
                          style={{ width: '50%', marginLeft: 'auto' }}
                          value={entry.value ?? ''}
                          onChange={(event) => onChange(entry.taskId, event.target.value)}
                          disabled={saving}
                          placeholder="0"
                        />
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          </div>
          <div className="card-footer d-flex flex-column flex-sm-row align-items-sm-center justify-content-between gap-2">
            <div className="fw-semibold">
              반영 비율 합계:&nbsp;
              <span className={total === 100 ? 'text-success' : 'text-danger'}>{total}</span>
            </div>
            <div className="text-muted small flex-grow-1 text-sm-end">모든 과제 합계가 100이 되도록 설정하세요.</div>
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

// 공용 RowItem (개인/조별 과제 공통)
const RowItem = ({ t, lectureId, kind, weightValue }) => {
  const title = t.title || t.taskName || t.indivtaskName || t.grouptaskName || '(제목 없음)'
  const start = fmtDate(t.startAt)
  const end = fmtDate(t.endAt)
  const indivId = t.indivtaskId || t.id || t.taskId
  const groupId = t.grouptaskId || t.id || t.taskId
  const path = kind === 'indiv'
    ? `/classroom/professor/${encodeURIComponent(lectureId)}/task/indiv/${encodeURIComponent(indivId || '')}`
    : `/classroom/professor/${encodeURIComponent(lectureId)}/task/group/${encodeURIComponent(groupId || '')}`

  // Check if task has file attachments
  const hasAttachment = !!(t.attachFileId || t.fileId || (Array.isArray(t.attachFileList) && t.attachFileList.length > 0))

  // Determine status
  const status = resolveStatus(t.startAt, t.endAt)
  const statusDisplay = status === 'upcoming' ? (
    <span className="badge bg-info">예정</span>
  ) : status === 'ongoing' ? (
    <span className="badge bg-success text-white">진행중</span>
  ) : (
    <span className="badge bg-secondary">종료</span>
  )

  const weightDisplay = (weightValue !== null && weightValue !== undefined && weightValue !== 0) ? (
    <span className="badge bg-primary">{weightValue}</span>
  ) : (
    <span className="badge bg-secondary">반영X</span>
  )

  return (
    <tr>
      <td style={{ width: '250px', overflow: 'hidden', textOverflow: 'ellipsis' }}>
        <Link to={path} className="text-reset text-decoration-none">
          <div className="d-flex align-items-center gap-1">
            {hasAttachment && <i className="bx bx-paperclip text-muted"></i>}
            <div className="fw-semibold text-primary" title={title}>{title}</div>
          </div>
        </Link>
      </td>
      <td className="text-center" style={{ width: '80px' }}>{statusDisplay}</td>
      <td className="text-center" style={{ width: '100px' }}>{weightDisplay}</td>
      <td className="text-muted small text-center" style={{ width: '120px', lineHeight: '1.4' }}>
        <div>{start}</div>
        <div>~ {end}</div>
      </td>
    </tr>
  )
}

export default function Task() {
  const { lectureId } = useParams()
  const [indiv, setIndiv] = useState([])
  const [group, setGroup] = useState([])
  const [weights, setWeights] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [weightModalOpen, setWeightModalOpen] = useState(false)
  const [weightEntries, setWeightEntries] = useState([])
  const [weightError, setWeightError] = useState('')
  const [weightSaving, setWeightSaving] = useState(false)

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const base = `/classroom/api/v1/professor/task/${encodeURIComponent(lectureId)}`
        const [ri, rg, rw] = await Promise.all([
          fetch(`${base}/indiv`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/group`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/weight`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
        ])
        if (!ri.ok || !rg.ok) throw new Error('과제 목록을 불러오지 못했습니다.')
        const [li, lg, lw] = await Promise.all([ri.json(), rg.json(), rw.ok ? rw.json() : []])
        if (!alive) return
        setIndiv(Array.isArray(li) ? li : [])
        setGroup(Array.isArray(lg) ? lg : [])

        // Build weight map: taskId -> weightValue
        const weightMap = {}
        if (Array.isArray(lw)) {
          lw.forEach(item => {
            if (item.taskId) {
              weightMap[item.taskId] = item.weightValue
            }
          })
        }
        setWeights(weightMap)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId])

  const indivUpcoming = useMemo(() => indiv.filter(t => resolveStatus(t.startAt, t.endAt) === 'upcoming').length, [indiv])
  const indivOngoing  = useMemo(() => indiv.filter(t => resolveStatus(t.startAt, t.endAt) === 'ongoing').length,  [indiv])
  const indivClosed   = useMemo(() => indiv.filter(t => resolveStatus(t.startAt, t.endAt) === 'closed').length,   [indiv])

  const groupUpcoming = useMemo(() => group.filter(t => resolveStatus(t.startAt, t.endAt) === 'upcoming').length, [group])
  const groupOngoing  = useMemo(() => group.filter(t => resolveStatus(t.startAt, t.endAt) === 'ongoing').length,  [group])
  const groupClosed   = useMemo(() => group.filter(t => resolveStatus(t.startAt, t.endAt) === 'closed').length,   [group])

  const weightTotal = useMemo(() => {
    return weightEntries.reduce((sum, e) => sum + (parseInt(e.value) || 0), 0)
  }, [weightEntries])

  const handleOpenWeightModal = useCallback(() => {
    setWeightError('')
    const entries = []

    indiv.forEach(t => {
      const taskId = t.indivtaskId || t.id || t.taskId
      const taskName = t.indivtaskName || t.taskName || '(제목 없음)'
      entries.push({
        taskType: 'INDIV',
        taskId,
        taskName,
        startAt: t.startAt,
        endAt: t.endAt,
        value: weights[taskId] ?? null
      })
    })

    group.forEach(t => {
      const taskId = t.grouptaskId || t.id || t.taskId
      const taskName = t.grouptaskName || t.taskName || '(제목 없음)'
      entries.push({
        taskType: 'GROUP',
        taskId,
        taskName,
        startAt: t.startAt,
        endAt: t.endAt,
        value: weights[taskId] ?? null
      })
    })

    setWeightEntries(entries)
    setWeightModalOpen(true)
  }, [indiv, group, weights])

  const handleCloseWeightModal = useCallback(() => {
    setWeightModalOpen(false)
    setWeightError('')
  }, [])

  const handleWeightChange = useCallback((taskId, value) => {
    setWeightEntries(prev => prev.map(e =>
      e.taskId === taskId ? { ...e, value: value === '' ? null : parseInt(value) || 0 } : e
    ))
  }, [])

  const handleWeightSubmit = useCallback(async (e) => {
    e.preventDefault()
    setWeightError('')
    setWeightSaving(true)

    try {
      // Validate all entries are numeric
      for (const entry of weightEntries) {
        if (entry.value !== null && entry.value !== '') {
          const numValue = Number(entry.value)
          if (isNaN(numValue) || !Number.isInteger(numValue) || numValue < 0 || numValue > 100) {
            setWeightError('반영 비율은 0~100 사이의 정수여야 합니다.')
            setWeightSaving(false)
            return
          }
        }
      }

      const payload = weightEntries.map(e => ({
        taskType: e.taskType,
        taskId: e.taskId,
        weightValue: e.value
      }))

      console.log('Submitting task weights:', payload)

      const response = await fetch(`/classroom/api/v1/professor/task/${encodeURIComponent(lectureId)}/weight`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        throw new Error(`저장 실패 (HTTP ${response.status})`)
      }

      // Reload weights
      const rw = await fetch(`/classroom/api/v1/professor/task/${encodeURIComponent(lectureId)}/weight`, {
        headers: { Accept: 'application/json' },
        credentials: 'include'
      })
      const lw = rw.ok ? await rw.json() : []
      const weightMap = {}
      if (Array.isArray(lw)) {
        lw.forEach(item => {
          if (item.taskId) {
            weightMap[item.taskId] = item.weightValue
          }
        })
      }
      setWeights(weightMap)
      setWeightModalOpen(false)
    } catch (err) {
      setWeightError(err.message || '저장 중 오류가 발생했습니다.')
    } finally {
      setWeightSaving(false)
    }
  }, [weightEntries, lectureId])

  return (
    <section id="task-root" className="container py-0" data-lecture-id={lectureId}>
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h3 mb-0">과제</h1>
        <div className="d-flex align-items-center gap-2">
          <div className="btn-group">
            <button type="button" className="btn btn-sm btn-primary dropdown-toggle py-1 px-2" data-bs-toggle="dropdown" aria-expanded="false">새 과제</button>
            <ul className="dropdown-menu">
              <li><Link className="dropdown-item" to={`/classroom/professor/${encodeURIComponent(lectureId)}/task/indiv/new`}>개인 과제</Link></li>
              <li><Link className="dropdown-item" to={`/classroom/professor/${encodeURIComponent(lectureId)}/task/group/new`}>조별 과제</Link></li>
            </ul>
          </div>
          <button type="button" className="btn btn-sm btn-outline-primary" onClick={handleOpenWeightModal} disabled={indiv.length === 0 && group.length === 0}>
            가중치 일괄설정
          </button>
        </div>
      </div>

      {loading && <div className="text-muted">로딩 중…</div>}
      {error && <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>}

      {!loading && !error && (
        <div className="row g-4 align-items-start">
          {/* 개인 과제 */}
          <div className="col-12 col-xl-6">
            <div className="card shadow-sm">
              <div className="card-header d-flex align-items-center justify-content-between">
                <h2 className="h5 mb-0">개인 과제</h2>
              </div>
              <div className="card-body">
                <ul className="nav nav-pills mb-3" id="indiv-status-tabs" role="tablist">
              <li className="nav-item" role="presentation"><button className="nav-link active" id="indiv-all-tab" data-bs-toggle="tab" data-bs-target="#indiv-all" type="button" role="tab" aria-selected="true">전체 <span className="badge rounded-pill bg-primary ms-1" id="indiv-count-all" style={{ minWidth: '30px' }}>{indiv.length}</span></button></li>
              <li className="nav-item" role="presentation"><button className="nav-link" id="indiv-upcoming-tab" data-bs-toggle="tab" data-bs-target="#indiv-upcoming" type="button" role="tab">예정 <span className="badge rounded-pill bg-info ms-1" id="indiv-count-upcoming" style={{ minWidth: '30px' }}>{indivUpcoming}</span></button></li>
              <li className="nav-item" role="presentation"><button className="nav-link" id="indiv-ongoing-tab" data-bs-toggle="tab" data-bs-target="#indiv-ongoing" type="button" role="tab">진행중 <span className="badge rounded-pill bg-success text-white ms-1" id="indiv-count-ongoing" style={{ minWidth: '30px' }}>{indivOngoing}</span></button></li>
              <li className="nav-item" role="presentation"><button className="nav-link" id="indiv-closed-tab" data-bs-toggle="tab" data-bs-target="#indiv-closed" type="button" role="tab">종료 <span className="badge rounded-pill bg-secondary ms-1" id="indiv-count-closed" style={{ minWidth: '30px' }}>{indivClosed}</span></button></li>
            </ul>
            <div className="tab-content" id="indiv-status-content">
              <div id="indiv-all" className="tab-pane fade show active" role="tabpanel" aria-labelledby="indiv-all-tab">
                <div id="indiv-list-all" className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>과제명</th>
                        <th className="text-center" style={{ width: 80 }}>상태</th>
                        <th className="text-center" style={{ width: 100 }}>가중치</th>
                        <th className="text-center" style={{ width: 120 }}>과제 기간</th>
                      </tr>
                    </thead>
                    <tbody>
                      {indiv.length === 0 ? (
                        <tr><td colSpan={4} className="text-center text-muted py-4">등록된 과제가 없습니다.</td></tr>
                      ) : (
                        indiv.map((t, i) => {
                          const taskId = t.indivtaskId || t.id || t.taskId
                          return <RowItem key={taskId || i} t={t} lectureId={lectureId} kind="indiv" weightValue={weights[taskId]} />
                        })
                      )}
                    </tbody>
                  </table>
                </div>
                <nav className="mt-3" aria-label="개인 과제 전체 페이지 이동"><ul id="indiv-pagination-all" className="pagination pagination-sm mb-0"></ul></nav>
              </div>
              <div id="indiv-upcoming" className="tab-pane fade" role="tabpanel" aria-labelledby="indiv-upcoming-tab">
                <div id="indiv-list-upcoming" className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>과제명</th>
                        <th className="text-center" style={{ width: 80 }}>상태</th>
                        <th className="text-center" style={{ width: 100 }}>가중치</th>
                        <th className="text-center" style={{ width: 120 }}>과제 기간</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        const filtered = indiv.filter(t => resolveStatus(t.startAt, t.endAt) === 'upcoming')
                        return filtered.length === 0 ? (
                          <tr><td colSpan={4} className="text-center text-muted py-4">예정 상태인 개인과제가 없습니다.</td></tr>
                        ) : (
                          filtered.map((t, i) => {
                            const taskId = t.indivtaskId || t.id || t.taskId
                            return <RowItem key={taskId || i} t={t} lectureId={lectureId} kind="indiv" weightValue={weights[taskId]} />
                          })
                        )
                      })()}
                    </tbody>
                  </table>
                </div>
                <nav className="mt-3" aria-label="개인 과제 예정 페이지 이동"><ul id="indiv-pagination-upcoming" className="pagination pagination-sm mb-0"></ul></nav>
              </div>
              <div id="indiv-ongoing" className="tab-pane fade" role="tabpanel" aria-labelledby="indiv-ongoing-tab">
                <div id="indiv-list-ongoing" className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>과제명</th>
                        <th className="text-center" style={{ width: 80 }}>상태</th>
                        <th className="text-center" style={{ width: 100 }}>가중치</th>
                        <th className="text-center" style={{ width: 120 }}>과제 기간</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        const filtered = indiv.filter(t => resolveStatus(t.startAt, t.endAt) === 'ongoing')
                        return filtered.length === 0 ? (
                          <tr><td colSpan={4} className="text-center text-muted py-4">진행중 상태인 개인과제가 없습니다.</td></tr>
                        ) : (
                          filtered.map((t, i) => {
                            const taskId = t.indivtaskId || t.id || t.taskId
                            return <RowItem key={taskId || i} t={t} lectureId={lectureId} kind="indiv" weightValue={weights[taskId]} />
                          })
                        )
                      })()}
                    </tbody>
                  </table>
                </div>
                <nav className="mt-3" aria-label="개인 과제 진행중 페이지 이동"><ul id="indiv-pagination-ongoing" className="pagination pagination-sm mb-0"></ul></nav>
              </div>
              <div id="indiv-closed" className="tab-pane fade" role="tabpanel" aria-labelledby="indiv-closed-tab">
                <div id="indiv-list-closed" className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>과제명</th>
                        <th className="text-center" style={{ width: 80 }}>상태</th>
                        <th className="text-center" style={{ width: 100 }}>가중치</th>
                        <th className="text-center" style={{ width: 120 }}>과제 기간</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        const filtered = indiv.filter(t => resolveStatus(t.startAt, t.endAt) === 'closed')
                        return filtered.length === 0 ? (
                          <tr><td colSpan={4} className="text-center text-muted py-4">종료 상태인 개인과제가 없습니다.</td></tr>
                        ) : (
                          filtered.map((t, i) => {
                            const taskId = t.indivtaskId || t.id || t.taskId
                            return <RowItem key={taskId || i} t={t} lectureId={lectureId} kind="indiv" weightValue={weights[taskId]} />
                          })
                        )
                      })()}
                    </tbody>
                  </table>
                </div>
                <nav className="mt-3" aria-label="개인 과제 마감 페이지 이동"><ul id="indiv-pagination-closed" className="pagination pagination-sm mb-0"></ul></nav>
              </div>
            </div>
              </div>
            </div>
          </div>

          {/* 조별 과제 */}
          <div className="col-12 col-xl-6">
            <div className="card shadow-sm">
              <div className="card-header d-flex align-items-center justify-content-between">
                <h2 className="h5 mb-0">조별 과제</h2>
              </div>
              <div className="card-body">
                <ul className="nav nav-pills mb-3" id="group-status-tabs" role="tablist">
              <li className="nav-item" role="presentation"><button className="nav-link active" id="group-all-tab" data-bs-toggle="tab" data-bs-target="#group-all" type="button" role="tab" aria-selected="true">전체 <span className="badge rounded-pill bg-primary ms-1" id="group-count-all" style={{ minWidth: '30px' }}>{group.length}</span></button></li>
              <li className="nav-item" role="presentation"><button className="nav-link" id="group-upcoming-tab" data-bs-toggle="tab" data-bs-target="#group-upcoming" type="button" role="tab">예정 <span className="badge rounded-pill bg-info ms-1" id="group-count-upcoming" style={{ minWidth: '30px' }}>{groupUpcoming}</span></button></li>
              <li className="nav-item" role="presentation"><button className="nav-link" id="group-ongoing-tab" data-bs-toggle="tab" data-bs-target="#group-ongoing" type="button" role="tab">진행중 <span className="badge rounded-pill bg-success text-white ms-1" id="group-count-ongoing" style={{ minWidth: '30px' }}>{groupOngoing}</span></button></li>
              <li className="nav-item" role="presentation"><button className="nav-link" id="group-closed-tab" data-bs-toggle="tab" data-bs-target="#group-closed" type="button" role="tab">종료 <span className="badge rounded-pill bg-secondary ms-1" id="group-count-closed" style={{ minWidth: '30px' }}>{groupClosed}</span></button></li>
            </ul>
            <div className="tab-content" id="group-status-content">
              <div id="group-all" className="tab-pane fade show active" role="tabpanel" aria-labelledby="group-all-tab">
                <div id="group-list-all" className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>과제명</th>
                        <th className="text-center" style={{ width: 80 }}>상태</th>
                        <th className="text-center" style={{ width: 100 }}>가중치</th>
                        <th className="text-center" style={{ width: 120 }}>과제 기간</th>
                      </tr>
                    </thead>
                    <tbody>
                      {group.length === 0 ? (
                        <tr><td colSpan={4} className="text-center text-muted py-4">등록된 과제가 없습니다.</td></tr>
                      ) : (
                        group.map((t, i) => {
                          const taskId = t.grouptaskId || t.id || t.taskId
                          return <RowItem key={taskId || i} t={t} lectureId={lectureId} kind="group" weightValue={weights[taskId]} />
                        })
                      )}
                    </tbody>
                  </table>
                </div>
                <nav className="mt-3" aria-label="조별 과제 전체 페이지 이동"><ul id="group-pagination-all" className="pagination pagination-sm mb-0"></ul></nav>
              </div>
              <div id="group-upcoming" className="tab-pane fade" role="tabpanel" aria-labelledby="group-upcoming-tab">
                <div id="group-list-upcoming" className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>과제명</th>
                        <th className="text-center" style={{ width: 80 }}>상태</th>
                        <th className="text-center" style={{ width: 100 }}>가중치</th>
                        <th className="text-center" style={{ width: 120 }}>과제 기간</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        const filtered = group.filter(t => resolveStatus(t.startAt, t.endAt) === 'upcoming')
                        return filtered.length === 0 ? (
                          <tr><td colSpan={4} className="text-center text-muted py-4">예정 상태인 조별과제가 없습니다.</td></tr>
                        ) : (
                          filtered.map((t, i) => {
                            const taskId = t.grouptaskId || t.id || t.taskId
                            return <RowItem key={taskId || i} t={t} lectureId={lectureId} kind="group" weightValue={weights[taskId]} />
                          })
                        )
                      })()}
                    </tbody>
                  </table>
                </div>
                <nav className="mt-3" aria-label="조별 과제 예정 페이지 이동"><ul id="group-pagination-upcoming" className="pagination pagination-sm mb-0"></ul></nav>
              </div>
              <div id="group-ongoing" className="tab-pane fade" role="tabpanel" aria-labelledby="group-ongoing-tab">
                <div id="group-list-ongoing" className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>과제명</th>
                        <th className="text-center" style={{ width: 80 }}>상태</th>
                        <th className="text-center" style={{ width: 100 }}>가중치</th>
                        <th className="text-center" style={{ width: 120 }}>과제 기간</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        const filtered = group.filter(t => resolveStatus(t.startAt, t.endAt) === 'ongoing')
                        return filtered.length === 0 ? (
                          <tr><td colSpan={4} className="text-center text-muted py-4">진행중 상태인 조별과제가 없습니다.</td></tr>
                        ) : (
                          filtered.map((t, i) => {
                            const taskId = t.grouptaskId || t.id || t.taskId
                            return <RowItem key={taskId || i} t={t} lectureId={lectureId} kind="group" weightValue={weights[taskId]} />
                          })
                        )
                      })()}
                    </tbody>
                  </table>
                </div>
                <nav className="mt-3" aria-label="조별 과제 진행중 페이지 이동"><ul id="group-pagination-ongoing" className="pagination pagination-sm mb-0"></ul></nav>
              </div>
              <div id="group-closed" className="tab-pane fade" role="tabpanel" aria-labelledby="group-closed-tab">
                <div id="group-list-closed" className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th>과제명</th>
                        <th className="text-center" style={{ width: 80 }}>상태</th>
                        <th className="text-center" style={{ width: 100 }}>가중치</th>
                        <th className="text-center" style={{ width: 120 }}>과제 기간</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(() => {
                        const filtered = group.filter(t => resolveStatus(t.startAt, t.endAt) === 'closed')
                        return filtered.length === 0 ? (
                          <tr><td colSpan={4} className="text-center text-muted py-4">종료 상태인 조별과제가 없습니다.</td></tr>
                        ) : (
                          filtered.map((t, i) => {
                            const taskId = t.grouptaskId || t.id || t.taskId
                            return <RowItem key={taskId || i} t={t} lectureId={lectureId} kind="group" weightValue={weights[taskId]} />
                          })
                        )
                      })()}
                    </tbody>
                  </table>
                </div>
                <nav className="mt-3" aria-label="조별 과제 마감 페이지 이동"><ul id="group-pagination-closed" className="pagination pagination-sm mb-0"></ul></nav>
              </div>
            </div>
              </div>
            </div>
          </div>

        </div>
      )}

      <div id="task-notice-box" className="alert alert-danger mt-3 d-none" role="alert"></div>

      <TaskWeightModal
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
