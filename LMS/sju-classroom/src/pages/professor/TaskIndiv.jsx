import React, { useEffect, useMemo, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import Swal from 'sweetalert2'
import NotFound from '../../components/NotFound'
import '../../styles/customButtons.css'

const pad = (n) => String(n).padStart(2, '0')
const fmtDateTime = (iso) => {
  if (!iso) return '-'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso

  let hours = d.getHours()
  const ampm = hours >= 12 ? 'PM' : 'AM'
  hours = hours % 12
  hours = hours ? hours : 12 // the hour '0' should be '12'

  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${ampm} ${hours}:${pad(d.getMinutes())}`
}

const truncateFileName = (fileName, maxLength = 50) => {
  if (fileName.length <= maxLength) return fileName
  const lastDotIndex = fileName.lastIndexOf('.')
  if (lastDotIndex === -1) return fileName.substring(0, maxLength - 3) + '...'
  const extension = fileName.substring(lastDotIndex)
  const nameWithoutExt = fileName.substring(0, lastDotIndex)
  const availableLength = maxLength - extension.length - 3
  if (availableLength <= 0) return fileName.substring(0, maxLength - 3) + '...'
  return nameWithoutExt.substring(0, availableLength) + '...' + extension
}

export default function TaskIndiv() {
  const { lectureId, indivtaskId } = useParams()
  const navigate = useNavigate()

  const profApiBase = '/classroom/api/v1/professor'
  const taskApiBase = '/classroom/api/v1/professor/task'

  const [task, setTask] = useState(null)
  const [students, setStudents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [notFound, setNotFound] = useState(false)
  const [deleting, setDeleting] = useState(false)

  const [pendingExpanded, setPendingExpanded] = useState(false)
  const [modalSubmission, setModalSubmission] = useState(null)
  const [modalStudent, setModalStudent] = useState(null)

  const taskListUrl = useMemo(() => `/classroom/professor/${encodeURIComponent(lectureId || '')}/task`, [lectureId])
  const editUrl = useMemo(() => `${taskListUrl}/indiv/${encodeURIComponent(indivtaskId || '')}/edit`, [taskListUrl, indivtaskId])

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        setNotFound(false)
        const [studentsRes, taskRes] = await Promise.all([
          fetch(`${profApiBase}/${encodeURIComponent(lectureId)}/students`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${taskApiBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
        ])
        if (taskRes.status === 404 || taskRes.status === 500) {
          if (!alive) return
          setNotFound(true)
          return
        }
        if (!studentsRes.ok || !taskRes.ok) throw new Error('과제 상세 정보를 불러오지 못했습니다.')
        const [stu, tk] = await Promise.all([studentsRes.json(), taskRes.json()])
        if (!alive) return
        setStudents(Array.isArray(stu) ? stu : [])
        setTask(tk || null)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, indivtaskId])

  const submissionMap = useMemo(() => new Map((task?.studentSubmitList || []).map(s => [s.enrollId, s])), [task])
  const submittedEntries = useMemo(() => {
    const acc = []
    const seen = new Set()
    for (const st of students) {
      const sub = submissionMap.get(st.enrollId)
      if (sub?.submitAt) acc.push({ student: st, submission: sub })
      seen.add(st.enrollId)
    }
    for (const sub of (task?.studentSubmitList || [])) {
      if (!seen.has(sub.enrollId) && sub.submitAt) acc.push({ student: null, submission: sub })
    }
    return acc
  }, [students, submissionMap, task])
  const pendingEntries = useMemo(() => {
    const acc = []
    for (const st of students) {
      const sub = submissionMap.get(st.enrollId)
      if (!(sub?.submitAt)) acc.push({ student: st, submission: sub || null })
    }
    return acc
  }, [students, submissionMap])

  const totalStudents = students.length
  const submittedCount = submittedEntries.length

  const toStudentLabel = (student) => {
    if (!student) return { name: '(이름 없음)', number: '' }
    const name = `${student.lastName || ''}${student.firstName || ''}` || student.name || student.userId || '(이름 없음)'
    return { name, number: student.studentNo || '' }
  }


  const pendingPreview = useMemo(() => {
    if (pendingEntries.length === 0) return null
    const { name, number } = toStudentLabel(pendingEntries[0].student)
    const label = number ? `${name} (${number})` : name
    return { label, rest: pendingEntries.length - 1 }
  }, [pendingEntries])

  const pendingPreviewLabel = useMemo(() => {
    if (!pendingPreview) return ''
    const suffix = pendingPreview.rest > 0 ? ` 외 ${pendingPreview.rest}명` : ''
    return `${pendingPreview.label}${suffix}`
  }, [pendingPreview])

  const handleDelete = async () => {
    const result = await Swal.fire({
      title: '삭제 확인',
      text: '이 개인과제를 삭제하시겠습니까?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      confirmButtonColor: '#dc3545'
    })
    if (!result.isConfirmed) return
    try {
      setDeleting(true)
      const res = await fetch(`${taskApiBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}`, { method: 'DELETE', credentials: 'include' })
      if (!res.ok && res.status !== 204) throw new Error(`(${res.status}) 삭제 실패`)
      await Swal.fire({
        title: '삭제 완료',
        text: '개인과제가 삭제되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      })
      navigate(taskListUrl)
    } catch (e) {
      await Swal.fire({
        title: '삭제 실패',
        text: e.message || String(e),
        icon: 'error',
        confirmButtonText: '확인'
      })
    } finally {
      setDeleting(false)
    }
  }

  const handleEvalSubmit = async (enrollId, evaluScore, evaluDesc, setBusy) => {
    try {
      setBusy(true)
      const payload = { enrollId, evaluScore: Number(evaluScore), evaluDesc }
      if (!Number.isFinite(payload.evaluScore) || payload.evaluScore < 0 || payload.evaluScore > 100) {
        await Swal.fire({
          title: '입력 오류',
          text: '점수는 0~100 사이의 정수여야 합니다.',
          icon: 'error',
          confirmButtonText: '확인'
        })
        return
      }
      const url = `${taskApiBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}/eval`
      const res = await fetch(url, { method: 'PATCH', headers: { 'Content-Type': 'application/json' }, credentials: 'include', body: JSON.stringify(payload) })
      if (!res.ok) throw new Error(`(${res.status}) 저장 실패`)
      await Swal.fire({
        title: '저장 완료',
        text: '평가가 저장되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      })
      // 데이터 다시 로드
      const taskRes = await fetch(`${taskApiBase}/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}`, { headers: { Accept: 'application/json' }, credentials: 'include' })
      if (taskRes.ok) {
        const tk = await taskRes.json()
        setTask(tk || null)
      }
    } catch (e) {
      await Swal.fire({
        title: '저장 실패',
        text: e.message || String(e),
        icon: 'error',
        confirmButtonText: '확인'
      })
    } finally {
      setBusy(false)
    }
  }

  const openModal = (student, submission) => {
    setModalStudent(student)
    setModalSubmission(submission)
  }

  const closeModal = () => {
    setModalStudent(null)
    setModalSubmission(null)
  }

  if (notFound) {
    return <NotFound message="개인과제를 찾을 수 없습니다" resourceType="개인과제" />
  }

  return (
    <section id="indivtask-post-root" className="container-fluid" style={{ display: 'flex', flexDirection: 'column', padding: '1rem', maxHeight: '100vh' }}>

      <div style={{ display: 'flex', flexDirection: 'row', gap: '0.75rem', height: '70vh', minHeight: 0 }}>
        {loading ? (
          <div className="card shadow-sm" style={{ flex: 1 }}>
            <div className="card-body">
              <div className="d-flex align-items-start flex-wrap mb-2">
                <span className="badge skeleton" style={{ width: 64, height: 22 }} />
                <span className="badge skeleton ms-2" style={{ width: 56, height: 22 }} />
              </div>
              <h1 className="h4 mb-3 skeleton" style={{ width: '60%', height: '1.5rem' }} />
              <div className="d-flex flex-wrap meta-list mb-3">
                <div className="meta-item skeleton" style={{ width: 220, height: '1rem' }} />
                <div className="meta-item skeleton" style={{ width: 220, height: '1rem' }} />
                <div className="meta-item skeleton" style={{ width: 220, height: '1rem' }} />
              </div>
              <hr className="my-3" />
              <article className="task-content mb-4"><div className="skeleton" style={{ height: '8rem' }} /></article>
            </div>
          </div>
        ) : error ? (
          <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>
        ) : task ? (
          <>
            {/* 과제 정보 카드 - 가로 3/5 */}
            <div className="card shadow-sm" style={{ flex: '3', minHeight: 0, display: 'flex', flexDirection: 'column' }}>
              <div className="card-header d-flex align-items-center justify-content-between">
                <div className="d-flex align-items-center gap-2">
                  <span className="badge text-bg-primary">개인과제</span>
                  <h1 className="h5 mb-0">{task.indivtaskName || '(제목 없음)'}</h1>
                </div>
              </div>
              <div className="card-body" style={{ overflow: 'auto', flex: 1, display: 'flex', flexDirection: 'column' }}>
                {!loading && task && (
                  <div className="d-flex gap-3 small mb-3">
                    <div>시작: <span className="fw-bold text-primary">{fmtDateTime(task.startAt)}</span></div>
                    <div>마감: <span className="fw-bold text-danger">{fmtDateTime(task.endAt)}</span></div>
                  </div>
                )}
                <article className="task-content mb-4" style={{ flex: 1 }} dangerouslySetInnerHTML={{ __html: task.indivtaskDesc || '' }} />
              </div>
              {Array.isArray(task.attachFileList) && task.attachFileList.length > 0 && (
                <div className="card-footer p-1" id="attach-area">
                  <h2 className="h6 mb-2">첨부파일</h2>
                  <ul className="list-group list-group-flush" id="attach-list">
                    {task.attachFileList.map((file, idx) => {
                      const order = file.fileOrder ?? file.order
                      const fileName = file.originalFileName || file.originName || `파일 ${order ?? idx}`
                      const displayName = truncateFileName(fileName, 50)
                      const sizeText = typeof file.fileSize === 'number' ? `(${(file.fileSize / 1024).toFixed(2)} KB)` : ''
                      const href = order != null ? `/classroom/api/v1/common/task/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}/attach/${encodeURIComponent(order)}` : undefined
                      return (
                        <li key={`${order ?? idx}`} className="list-group-item d-flex justify-content-between align-items-center">
                          <div><i className="bi bi-paperclip" /> {displayName} {sizeText && <small className="text-muted">{sizeText}</small>}</div>
                          {href ? <a className="btn btn-sm btn-outline-secondary" href={href}><i className="bi bi-download" /></a> : <span className="text-muted small">다운로드 불가</span>}
                        </li>
                      )
                    })}
                  </ul>
                </div>
              )}
              <div className="card-footer d-flex justify-content-end gap-2">
                <a className="custom-btn custom-btn-sm custom-btn-primary" id="btn-edit-task" href={editUrl} role="button">수정</a>
                <button className="custom-btn custom-btn-sm custom-btn-danger" id="btn-delete-task" type="button" onClick={handleDelete} disabled={deleting}>{deleting ? '삭제중…' : '삭제'}</button>
              </div>
            </div>

            {/* 제출한 학생 카드 - 가로 2/5 */}
            <div className="card shadow-sm" style={{ flex: '2', minHeight: 0, display: 'flex', flexDirection: 'column' }}>
              <div className="card-header d-flex align-items-center justify-content-between">
                <h3 className="h5 mb-0">제출한 학생</h3>
                <span className="text-muted">{submittedCount} / {totalStudents}</span>
              </div>
              <div className="card-body" style={{ overflow: 'auto', padding: 0 }}>
                <div className="table-responsive">
                  <table className="table table-sm table-hover align-middle mb-0">
                    <thead className="table-light" style={{ position: 'sticky', top: 0, zIndex: 1 }}>
                      <tr>
                        <th style={{ width: '55%' }} className="text-center">학생</th>
                        <th style={{ width: '30%' }} className="text-center">제출일시</th>
                        <th style={{ width: '15%' }} className="text-center">점수</th>
                      </tr>
                    </thead>
                    <tbody>
                      {submittedEntries.length === 0 ? (
                        <tr><td colSpan={4} className="text-center text-muted py-3">제출한 학생이 없습니다.</td></tr>
                      ) : (
                        submittedEntries.map(({ student, submission }, index) => {
                          const name = `${student?.lastName || ''}${student?.firstName || ''}` || student?.name || student?.userId || '(이름 없음)'
                          const major = student?.univDeptName || ''
                          const grade = student?.gradeName || ''
                          const info = [major, grade].filter(Boolean).join(', ')
                          const studentLabel = info ? `${name} (${info})` : name
                          const submittedAt = submission?.submitAt ? fmtDateTime(submission.submitAt) : '-'
                          const files = submission?.submitFiles || []
                          const hasAttachment = files.some(file => file && (file.fileId || file.originName || file.originalFileName))
                          const scoreValue = (submission?.evaluScore != null) ? submission.evaluScore : '-'
                          return (
                            <tr key={`${submission?.enrollId || index}`} onClick={() => openModal(student, submission)} style={{ cursor: 'pointer' }}>
                              <td className="text-center">{studentLabel}</td>
                              <td className="text-center" style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{submittedAt}</td>
                              <td className="text-center">{scoreValue}</td>
                            </tr>
                          )
                        })
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
              <div className="card-footer">
                <div className="text-muted small">
                  제출하지 않은 학생: {pendingEntries.length === 0 ? '없음' : pendingPreviewLabel}
                </div>
              </div>
            </div>
          </>
        ) : null}
      </div>

      {/* 제출 상세 모달 */}
      {modalSubmission && (
        <SubmissionModal
          student={modalStudent}
          submission={modalSubmission}
          lectureId={lectureId}
          indivtaskId={indivtaskId}
          onClose={closeModal}
          onEvalSubmit={handleEvalSubmit}
          toStudentLabel={toStudentLabel}
          fmtDateTime={fmtDateTime}
        />
      )}

      {error && <div id="error-box" className="alert alert-danger mt-3" role="alert">{error}</div>}
    </section>
  )
}

function SubmissionModal({ student, submission, lectureId, indivtaskId, onClose, onEvalSubmit, toStudentLabel, fmtDateTime }) {
  const { name, number } = toStudentLabel(student)
  const studentKey = student?.studentNo || submission?.studentNo || submission?.enrollId || ''
  const submittedAt = fmtDateTime(submission?.submitAt)
  const files = submission?.submitFiles || []

  return (
    <div
      className="modal fade show"
      style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)' }}
      tabIndex={-1}
      role="dialog"
      onClick={onClose}
    >
      <div className="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable" onClick={(e) => e.stopPropagation()}>
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">
              <strong>{name}</strong>
              {number && <span className="text-muted small ms-2">({number})</span>}
            </h5>
            <button type="button" className="btn-close" onClick={onClose} aria-label="Close" />
          </div>
          <div className="modal-body">
            <div className="mb-3">
              <div className="text-muted small mb-1">제출일시</div>
              <div><span style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', display: 'inline-block', maxWidth: '100%' }}>{submittedAt}</span></div>
            </div>

            <div className="mb-3">
              <div className="text-muted small mb-1">제출 설명</div>
              <div className="p-3 bg-light rounded">{submission?.submitDesc || '-'}</div>
            </div>

            <div className="mb-3">
              <div className="text-muted small mb-2">제출 파일</div>
              <ul className="list-group">
                {files && files.length > 0 && files.some(f => f?.fileId) ? (
                  files.map((file, fi) => {
                    const order = file.fileOrder ?? file.order
                    const fileName = file.originalFileName || file.originName || `제출파일 ${order ?? fi}`
                    const sizeText = typeof file.fileSize === 'number' ? `(${(file.fileSize / 1024).toFixed(2)} KB)` : ''
                    const href = (order != null && studentKey)
                      ? `/classroom/api/v1/common/${encodeURIComponent(lectureId)}/indivtask/${encodeURIComponent(indivtaskId)}/submit/${encodeURIComponent(studentKey)}/attach/${encodeURIComponent(order)}`
                      : undefined
                    return (
                      <li key={`${order ?? fi}`} className="list-group-item d-flex justify-content-between align-items-center">
                        <div className="text-truncate" style={{ maxWidth: '70%' }}>
                          <i className="bi bi-paperclip" /> {fileName}
                        </div>
                        <div className="d-flex align-items-center gap-2">
                          {sizeText && <small className="text-muted">{sizeText}</small>}
                          {href && <a className="btn btn-sm btn-outline-secondary" href={href}>다운로드</a>}
                        </div>
                      </li>
                    )
                  })
                ) : (
                  <li className="list-group-item text-muted small">첨부파일 없음</li>
                )}
              </ul>
            </div>

            <div className="border-top pt-3">
              <h6 className="mb-3">평가</h6>
              <EvalForm enrollId={submission?.enrollId} defaultScore={submission?.evaluScore} defaultDesc={submission?.evaluDesc} onSubmit={onEvalSubmit} />
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>닫기</button>
          </div>
        </div>
      </div>
    </div>
  )
}

function EvalForm({ enrollId, defaultScore, defaultDesc, onSubmit }) {
  const [score, setScore] = useState(defaultScore ?? '')
  const [desc, setDesc] = useState(defaultDesc ?? '')
  const [busy, setBusy] = useState(false)
  return (
    <form className="eval-form" onSubmit={(e) => { e.preventDefault(); onSubmit(enrollId, score, desc, setBusy) }}>
      <div className="row g-2 align-items-center">
        <div className="col-sm-3">
          <label className="form-label">점수 (0-100)</label>
          <input type="number" className="form-control form-control-sm eval-score" min={0} max={100} value={score} onChange={(e) => setScore(e.target.value)} />
        </div>
        <div className="col-sm-9">
          <label className="form-label">평어</label>
          <textarea className="form-control form-control-sm eval-desc" rows={2} value={desc} onChange={(e) => setDesc(e.target.value)} />
        </div>
      </div>
      <div className="d-flex justify-content-end mt-2">
        <button type="submit" className="btn btn-sm btn-primary btn-save-eval" disabled={busy}>{busy ? '저장중…' : '저장'}</button>
      </div>
    </form>
  )
}
