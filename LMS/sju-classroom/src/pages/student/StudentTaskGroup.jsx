import React, { useEffect, useMemo, useRef, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useLecture } from '../../context/LectureContext'

const pad = (n) => String(n).padStart(2, '0')
const fmtDate = (iso) => {
  if (!iso) return '-'
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return '-'
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}
const fmtDateTime = (iso) => {
  if (!iso) return '-'
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return '-'
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export default function StudentTaskGroup() {
  const { lectureId, grouptaskId } = useParams()
  const { students: ctxStudents, setStudents: setCtxStudents } = useLecture()

  const [task, setTask] = useState(null)
  const [me, setMe] = useState(null)
  const [myGroupData, setMyGroupData] = useState(null)
  const [students, setStudents] = useState(Array.isArray(ctxStudents) ? ctxStudents : [])
  const [loading, setLoading] = useState(true)
  const [groupLoading, setGroupLoading] = useState(true)
  const [error, setError] = useState('')
  const [studentsLoading, setStudentsLoading] = useState(false)
  const [refreshKey, setRefreshKey] = useState(0)

  const [showSubmitForm, setShowSubmitForm] = useState(false)
  const [submitDescValue, setSubmitDescValue] = useState('')
  const [currentFileId, setCurrentFileId] = useState('')
  const [uploadedFiles, setUploadedFiles] = useState([])
  const [submitLoading, setSubmitLoading] = useState(false)
  const [submitMessage, setSubmitMessage] = useState('')
  const [submitError, setSubmitError] = useState('')

  const fileInputRef = useRef(null)

  const encodedLectureId = lectureId ? encodeURIComponent(lectureId) : ''
  const encodedTaskId = grouptaskId ? encodeURIComponent(grouptaskId) : ''

  const extractUploadResult = (data) => {
    const fileId =
      data.bundleId ||
      data.fileId ||
      (Array.isArray(data.fileIds) && data.fileIds[0]) ||
      (Array.isArray(data.files) && data.files[0]?.fileId) ||
      (Array.isArray(data.details) && data.details[0]?.fileId) ||
      ''
    const details =
      (Array.isArray(data.files) && data.files) ||
      (Array.isArray(data.details) && data.details) ||
      []
    return { fileId, details }
  }

  useEffect(() => {
    let alive = true
    const fetchBase = async () => {
      if (!lectureId || !grouptaskId) {
        setError('과제 정보를 확인할 수 없습니다.')
        setLoading(false)
        return
      }
      try {
        setLoading(true)
        setError('')
        const encodedLecture = encodeURIComponent(lectureId)
        const encodedTask = encodeURIComponent(grouptaskId)
        const [taskRes, meRes] = await Promise.all([
          fetch(`/classroom/api/v1/student/${encodedLecture}/task/group/${encodedTask}`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`/classroom/api/v1/student/${encodedLecture}/me`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
        ])
        if (!taskRes.ok) {
          const text = await taskRes.text().catch(() => '')
          throw new Error(text || `(${taskRes.status}) 조별과제 상세를 불러오지 못했습니다.`)
        }
        const taskJson = await taskRes.json()
        const meJson = meRes.ok ? await meRes.json() : null
        if (!alive) return
        setTask(taskJson || null)
        setMe(meJson || null)
      } catch (err) {
        if (!alive) return
        setError(err instanceof Error ? err.message : '과제 정보를 불러오는 중 문제가 발생했습니다.')
        setTask(null)
      } finally {
        if (alive) setLoading(false)
      }
    }
    fetchBase()
    return () => { alive = false }
  }, [lectureId, grouptaskId, refreshKey])

  useEffect(() => {
    let alive = true
    const fetchGroup = async () => {
      if (!lectureId || !grouptaskId) {
        setGroupLoading(false)
        return
      }
      try {
        setGroupLoading(true)
        const encodedLecture = encodeURIComponent(lectureId)
        const encodedTask = encodeURIComponent(grouptaskId)
        const res = await fetch(`/classroom/api/v1/student/${encodedLecture}/task/group/${encodedTask}/jo`, {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) {
          if (res.status === 404) {
            if (!alive) return
            setMyGroupData(null)
            return
          }
          const text = await res.text().catch(() => '')
          throw new Error(text || `(${res.status}) 조 정보를 불러오지 못했습니다.`)
        }
        const groupJson = await res.json()
        if (!alive) return
        setMyGroupData(groupJson || null)
      } catch (err) {
        if (!alive) return
        console.error(err)
        setMyGroupData(null)
      } finally {
        if (alive) setGroupLoading(false)
      }
    }
    fetchGroup()
    return () => { alive = false }
  }, [lectureId, grouptaskId, refreshKey])

  useEffect(() => {
    if (Array.isArray(students) && students.length > 0) return
    let alive = true
    const fetchStudents = async () => {
      if (!lectureId) return
      try {
        setStudentsLoading(true)
        const res = await fetch(`/classroom/api/v1/student/${encodeURIComponent(lectureId)}/students`, {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) {
          return
        }
        const data = await res.json()
        if (!alive) return
        const arr = Array.isArray(data) ? data : []
        setStudents(arr)
        if (setCtxStudents) setCtxStudents(arr)
      } finally {
        if (alive) setStudentsLoading(false)
      }
    }
    fetchStudents()
    return () => { alive = false }
  }, [lectureId, students, setCtxStudents])

  const studentMap = useMemo(() => new Map((students || []).map((student) => [student.enrollId, student])), [students])
  const myEnrollId = me?.enrollId

  const leaderEnrollId = myGroupData?.leaderEnrollId

  const crewEntries = useMemo(() => {
    if (!myGroupData?.crewList) return []
    return myGroupData.crewList.map((crew) => {
      const info = studentMap.get(crew.enrollId) || {}
      const name = `${info.lastName || ''}${info.firstName || ''}`.trim() || info.studentName || '-'
      const studentNo = info.studentNo || '-'
      return {
        enrollId: crew.enrollId,
        name,
        studentNo,
        isLeader: crew.enrollId === leaderEnrollId,
        isSelf: crew.enrollId === myEnrollId,
        evaluScore: crew.evaluScore,
        evaluDesc: crew.evaluDesc,
        evaluAt: crew.evaluAt,
        initial: (name || '?').charAt(0),
      }
    })
  }, [myGroupData, studentMap, leaderEnrollId, myEnrollId])

  const groupSubmit = {
    submitDesc: myGroupData?.submitDesc,
    submitFileId: myGroupData?.submitFileId,
    submitAt: myGroupData?.submitAt,
  }

  const assignmentAttachments = Array.isArray(task?.attachFileList) ? task.attachFileList : []
  const submitDescription = groupSubmit?.submitDesc
  const submitAt = groupSubmit?.submitAt ? fmtDateTime(groupSubmit.submitAt) : null
  const isLeader = Boolean(myEnrollId && leaderEnrollId && myEnrollId === leaderEnrollId)

  const taskCommonBase = '/classroom/api/v1/common/task'
  const taskStudentBase = '/classroom/api/v1/student'

  const assignmentDownload = (file, index) => {
    const order = file.fileOrder ?? index
    return `${taskCommonBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}/attach/${encodeURIComponent(order)}`
  }

  const submitDownload = (groupSubmit?.submitFileId && myGroupData?.groupId)
    ? `${taskStudentBase}/${encodedLectureId}/task/group/${encodedTaskId}/submit/${encodeURIComponent(myGroupData.groupId)}`
    : null

  const listUrl = `/classroom/student/${encodeURIComponent(lectureId || '')}/task`

  return (
    <section className="container py-4" id="student-group-task-detail">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb mb-0">
            <li className="breadcrumb-item">
              <Link to={listUrl}>과제</Link>
            </li>
            <li className="breadcrumb-item active" aria-current="page">조별과제 상세</li>
          </ol>
        </nav>
        <Link to={listUrl} className="btn btn-sm btn-outline-secondary">목록</Link>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      <div className="card shadow-sm mb-4">
        <div className="card-body">
          {loading ? (
            <>
              <h1 className="h4 placeholder-glow">
                <span className="placeholder col-6" />
              </h1>
              <div className="placeholder-glow d-flex gap-2 flex-wrap text-muted small mb-3">
                <span className="placeholder col-3" />
                <span className="placeholder col-3" />
                <span className="placeholder col-3" />
              </div>
            </>
          ) : !task ? (
            <p className="text-muted mb-0">과제 정보를 확인할 수 없습니다.</p>
          ) : (
            <>
              <h1 className="h4 mb-3">{task.grouptaskName || '조별과제 제목 없음'}</h1>
              <div className="d-flex flex-wrap gap-3 text-muted small mb-3">
                <span>등록일 {fmtDateTime(task.createAt)}</span>
                <span className="text-nowrap small">진행 기간 {fmtDate(task.startAt)} ~ {fmtDate(task.endAt)}</span>
              </div>
              <article
                className="task-content mb-3"
                dangerouslySetInnerHTML={{ __html: task.grouptaskDesc || '<p class="text-muted mb-0">설명이 없습니다.</p>' }}
              />
              {assignmentAttachments.length > 0 && (
                <div className="mt-4">
                  <h2 className="h6 mb-2">첨부 파일</h2>
                  <ul className="list-group">
                    {assignmentAttachments.map((file, index) => {
                      const name = file.originalFileName || file.originName || `첨부 ${file.fileOrder ?? index + 1}`
                      const size = typeof file.fileSize === 'number' ? `(${(file.fileSize).toLocaleString()} Byte)` : ''
                      return (
                        <li className="list-group-item d-flex justify-content-between align-items-center" key={file.fileId || index}>
                          <div className="text-truncate" style={{ maxWidth: '70%' }}>
                            <i className="bi bi-paperclip me-2" />
                            {name} <small className="text-muted">{size}</small>
                          </div>
                          <a className="btn btn-sm btn-outline-secondary" href={assignmentDownload(file, index)}>다운로드</a>
                        </li>
                      )
                    })}
                  </ul>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      <div className="card shadow-sm mb-4">
        <div className="card-header d-flex justify-content-between align-items-center">
          <h2 className="h6 mb-0">우리 조 제출 현황</h2>
        </div>
        <div className="card-body">
          {groupLoading ? (
            <p className="text-muted placeholder-glow"><span className="placeholder col-3" /></p>
          ) : !myGroupData ? (
            <p className="text-muted mb-0">조 편성 정보를 찾을 수 없습니다.</p>
          ) : (
            <>
              <div className="row g-3">
                <div className="col-12 col-md-4">
                  <div className="small text-muted">조 이름</div>
                  <div>{myGroupData.groupName || '조 이름 없음'}</div>
                </div>
                <div className="col-12 col-md-4">
                  <div className="small text-muted">조장</div>
                  <div>{crewEntries.find((crew) => crew.isLeader)?.name || '-'}</div>
                </div>
                <div className="col-12 col-md-4">
                  <div className="small text-muted">제출 상태</div>
                  <div>{groupSubmit?.submitAt ? `제출 완료 (${submitAt})` : '미제출'}</div>
                </div>
              </div>
              <hr className="my-3" />
              <div>
                <div className="small text-muted mb-1">제출 설명</div>
                <div className="p-2 bg-body-secondary rounded">
                  {submitDescription ? submitDescription : <span className="text-muted">등록된 설명이 없습니다.</span>}
                </div>
              </div>
              {submitDownload && (
                <div className="mt-3">
                  <a className="btn btn-sm btn-outline-primary" href={submitDownload}>조 제출 파일 다운로드</a>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {isLeader && myGroupData && (
        <div className="mb-4">
          <button
            type="button"
            className="btn btn-sm btn-primary"
            onClick={() => {
              setSubmitError('')
              setSubmitMessage('')
              setSubmitDescValue(submitDescription || '')
              setCurrentFileId(groupSubmit?.submitFileId || '')
              setUploadedFiles([])
              if (fileInputRef.current) fileInputRef.current.value = ''
              setShowSubmitForm(true)
            }}
            disabled={submitLoading}
          >
            제출/수정
          </button>
        </div>
      )}

      {isLeader && showSubmitForm && (
        <div className="card shadow-sm mb-4">
          <div className="card-header">
            <h2 className="h6 mb-0">조 제출</h2>
          </div>
          <div className="card-body">
            {submitError && <div className="alert alert-danger" role="alert">{submitError}</div>}
            {submitMessage && <div className="alert alert-success" role="alert">{submitMessage}</div>}
            <form
              onSubmit={async (event) => {
                event.preventDefault()
                if (!lectureId || !grouptaskId || !myGroupData?.groupId) {
                  setSubmitError('조 정보를 확인할 수 없습니다.')
                  return
                }
                try {
                  setSubmitLoading(true)
                  setSubmitError('')
                  setSubmitMessage('')
                  const payload = {
                    submitDesc: submitDescValue || '',
                    submitFileId: currentFileId || null,
                  }
                  const res = await fetch(
                    `/classroom/api/v1/student/${encodedLectureId}/task/group/${encodedTaskId}`,
                    {
                      method: 'PATCH',
                      headers: { 'Content-Type': 'application/json' },
                      credentials: 'include',
                      body: JSON.stringify(payload),
                    },
                  )
                  if (!res.ok) {
                    const text = await res.text().catch(() => '')
                    throw new Error(text || `(${res.status}) 제출을 저장하지 못했습니다.`)
                  }
                  setSubmitMessage('제출이 저장되었습니다.')
                  setShowSubmitForm(false)
                  setRefreshKey((prev) => prev + 1)
                } catch (err) {
                  setSubmitError(err instanceof Error ? err.message : '제출 처리 중 문제가 발생했습니다.')
                } finally {
                  setSubmitLoading(false)
                  setUploadedFiles([])
                  if (fileInputRef.current) fileInputRef.current.value = ''
                }
              }}
            >
              <div className="mb-3">
                <label htmlFor="group-submit-desc" className="form-label">제출 설명</label>
                <textarea
                  id="group-submit-desc"
                  className="form-control"
                  rows={4}
                  placeholder="조 제출 내용을 입력하세요."
                  value={submitDescValue}
                  onChange={(event) => setSubmitDescValue(event.target.value)}
                  disabled={submitLoading}
                />
              </div>
              <div className="mb-3">
                <label htmlFor="group-submit-file" className="form-label">파일 첨부</label>
                {currentFileId && uploadedFiles.length === 0 && (
                  <div className="alert alert-info small py-2">
                    현재 제출 파일 ID: {currentFileId}<br />
                    새 파일을 업로드하면 기존 제출 파일이 교체됩니다.
                  </div>
                )}
                {uploadedFiles.length > 0 && (
                  <ul className="list-group mb-2">
                    {uploadedFiles.map((file, index) => {
                      const name = file.originalName || file.originName || file.originalFileName || `첨부 ${file.fileOrder ?? index + 1}`
                      const size = typeof file.fileSize === 'number' ? ` (${(file.fileSize / 1024).toFixed(2)} KB)` : ''
                      return (
                        <li className="list-group-item" key={`${file.fileId || index}`}>
                          {name}
                          <small className="text-muted">{size}</small>
                        </li>
                      )
                    })}
                  </ul>
                )}
                <input
                  id="group-submit-file"
                  className="form-control"
                  type="file"
                  multiple
                  ref={fileInputRef}
                  onChange={async (event) => {
                    const files = Array.from(event.target.files || [])
                    if (!files.length) return
                    if (!lectureId) {
                      setSubmitError('강의 정보를 확인할 수 없습니다.')
                      return
                    }
                    if (files.length > 5) {
                      setSubmitError('파일은 최대 5개까지 업로드할 수 있습니다.')
                      event.target.value = ''
                      return
                    }
                    try {
                      setSubmitLoading(true)
                      setSubmitError('')
                      setSubmitMessage('')
                      const formData = new FormData()
                      files.forEach((file) => formData.append('files', file))
                      const uploadUrl = `/classroom/api/v1/common/${encodedLectureId}/upload?type=task`
                      const res = await fetch(uploadUrl, {
                        method: 'POST',
                        body: formData,
                        credentials: 'include',
                      })
                      if (!res.ok) {
                        const text = await res.text().catch(() => '')
                        throw new Error(text || `(${res.status}) 파일 업로드에 실패했습니다.`)
                      }
                      const data = await res.json()
                      const { fileId, details } = extractUploadResult(data)
                      setCurrentFileId(fileId || '')
                      setUploadedFiles(details)
                      setSubmitMessage('파일을 업로드했습니다.')
                    } catch (err) {
                      setSubmitError(err instanceof Error ? err.message : '파일 업로드 중 문제가 발생했습니다.')
                    } finally {
                      setSubmitLoading(false)
                      if (event.target) event.target.value = ''
                    }
                  }}
                  disabled={submitLoading}
                />
                <div className="form-text">새 파일을 업로드하면 기존 제출 파일은 교체됩니다.</div>
              </div>
              <div className="d-flex justify-content-end gap-2">
                <button
                  type="button"
                  className="btn btn-outline-secondary"
                  onClick={() => {
                    setShowSubmitForm(false)
                    setSubmitError('')
                    setSubmitMessage('')
                    setUploadedFiles([])
                    if (fileInputRef.current) fileInputRef.current.value = ''
                  }}
                  disabled={submitLoading}
                >
                  취소
                </button>
                <button type="submit" className="btn btn-primary" disabled={submitLoading}>
                  {submitLoading ? '제출 중...' : '제출하기'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="card shadow-sm">
        <div className="card-header">
          <h2 className="h6 mb-0">조원 목록</h2>
        </div>
        <div className="card-body">
          {groupLoading || studentsLoading ? (
            <p className="text-muted placeholder-glow"><span className="placeholder col-4" /></p>
          ) : crewEntries.length === 0 ? (
            <p className="text-muted mb-0">조원 정보를 확인할 수 없습니다.</p>
          ) : (
            <div className="row g-3">
              {crewEntries.map((crew) => (
                <div className="col-12 col-md-6 col-lg-4" key={crew.enrollId}>
                  <div className={`member-card h-100 ${crew.isSelf ? 'border-primary' : ''}`}>
                    <div className="d-flex align-items-center gap-3">
                      <div className="avatar-48 bg-light border d-flex align-items-center justify-content-center">
                        {crew.initial}
                      </div>
                      <div>
                        <div className="fw-semibold">{crew.name}</div>
                        <div className="member-meta">{crew.studentNo}</div>
                        {crew.isLeader && <span className="badge text-bg-primary mt-2">조장</span>}
                        
                      </div>
                    </div>
                    {crew.isSelf && (
                      <div className="evaluation-box mt-3">
                        <div><span className="fw-semibold">개인 평가 :</span> {typeof crew.evaluScore === 'number' ? `${crew.evaluScore}점` : '미평가'}</div>
                        <div className="evaluation-note mt-2">{crew.evaluDesc || '코멘트가 없습니다.'}</div>
                        <div className="evaluation-note mt-2">평가 일시: {crew.evaluAt ? fmtDateTime(crew.evaluAt) : '미평가'}</div>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </section>
  )
}
