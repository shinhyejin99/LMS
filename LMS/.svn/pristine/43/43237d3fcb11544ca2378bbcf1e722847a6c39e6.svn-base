import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

const PAGE_TEXT = {
  breadcrumbLabel: '과제',
  detailLabel: '개인과제',
  back: '목록',
  taskLoading: '불러오는 중입니다...',
  taskLoadFailed: '과제 정보를 불러오지 못했습니다.',
  attachments: '첨부파일',
  submitStatus: '제출 현황',
  submitHistoryLoading: '제출 이력을 불러오는 중입니다...',
  submitNotFound: '제출 이력이 없습니다.',
  submitButtonNew: '제출하기',
  submitButtonEdit: '제출 수정',
  formTitleNew: '과제 제출',
  formTitleEdit: '제출 수정',
  formDescription: '설명',
  formFiles: '파일 첨부',
  existingFiles: '기존 제출 파일',
  newFiles: '새로 업로드된 파일',
  fileGuide: '새 파일을 첨부하면 기존 제출 파일은 교체됩니다.',
  submit: '제출',
  cancel: '취소',
  scoreLabel: '점수',
  feedbackLabel: '평가 코멘트',
  evaluatedAtLabel: '평가일',
  submittedAtLabel: '제출일',
  descriptionLabel: '제출 설명',
  noFiles: '첨부된 파일이 없습니다.',
  download: '다운로드',
  uploadSuccess: '파일을 업로드했습니다.',
  uploadError: '파일 업로드에 실패했습니다.',
  submitSuccess: '제출이 저장되었습니다.',
  submitError: '제출 처리 중 오류가 발생했습니다.',
  invalidLecture: '강의 또는 과제 정보를 확인할 수 없습니다.',
  fileLimit: '최대 5개까지 선택할 수 있습니다.',
}

const pad = (n) => String(n).padStart(2, '0')
const fmtDate = (value) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '-'
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}
const formatDateTime = (value) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '-'

  let hours = date.getHours()
  const ampm = hours >= 12 ? 'PM' : 'AM'
  hours = hours % 12
  hours = hours ? hours : 12 // the hour '0' should be '12'

  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${ampm} ${hours}:${pad(date.getMinutes())}`
}

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

  return { fileId: fileId || '', details }
}

const buildTaskFileHref = (lectureId, indivtaskId, order) =>
  `/classroom/api/v1/common/task/${encodeURIComponent(lectureId)}/indiv/${encodeURIComponent(indivtaskId)}/attach/${encodeURIComponent(String(order))}`

const buildSubmissionFileHref = (lectureId, indivtaskId, studentNo, order) =>
  studentNo
    ? `/classroom/api/v1/common/${encodeURIComponent(lectureId)}/indivtask/${encodeURIComponent(indivtaskId)}/submit/${encodeURIComponent(studentNo)}/attach/${encodeURIComponent(String(order))}`
    : '#'

export default function StudentTaskIndiv() {
  const { lectureId: rawLectureId, indivtaskId: rawTaskId } = useParams()
  const lectureId = rawLectureId || ''
  const indivtaskId = rawTaskId || ''

  const [task, setTask] = useState(null)
  const [student, setStudent] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [showForm, setShowForm] = useState(false)
  const [submitDesc, setSubmitDesc] = useState('')
  const [currentFileId, setCurrentFileId] = useState('')
  const [existingFiles, setExistingFiles] = useState([])
  const [uploadedFiles, setUploadedFiles] = useState([])
  const [submitLoading, setSubmitLoading] = useState(false)
  const [submitMessage, setSubmitMessage] = useState('')
  const [submitError, setSubmitError] = useState('')
  const [statusMessage, setStatusMessage] = useState('')

  const fileInputRef = useRef(null)

  const encodedLectureId = encodeURIComponent(lectureId)
  const encodedTaskId = encodeURIComponent(indivtaskId)

  const apiBaseUrl = useMemo(() => {
    if (!lectureId || !indivtaskId) return null
    return `/classroom/api/v1/student/${encodedLectureId}/task/indiv/${encodedTaskId}`
  }, [lectureId, indivtaskId, encodedLectureId, encodedTaskId])

  const lastSubmission = useMemo(() => {
    if (!task?.studentSubmitList || task.studentSubmitList.length === 0) return null
    const sorted = [...task.studentSubmitList].sort((a, b) => {
      const timeA = a.submitAt ? new Date(a.submitAt).getTime() : 0
      const timeB = b.submitAt ? new Date(b.submitAt).getTime() : 0
      return timeA - timeB
    })
    return sorted[sorted.length - 1]
  }, [task])

  const fetchData = useCallback(async () => {
    if (!apiBaseUrl) {
      setError(PAGE_TEXT.invalidLecture)
      setLoading(false)
      return
    }
    try {
      setLoading(true)
      setError('')
      const [taskRes, studentRes] = await Promise.all([
        fetch(apiBaseUrl, { headers: { Accept: 'application/json' }, credentials: 'include' }),
        fetch(`/classroom/api/v1/student/${encodedLectureId}/me`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
      ])

      if (!taskRes.ok) {
        const msg = await taskRes.text().catch(() => '')
        throw new Error(`(${taskRes.status}) ${msg || PAGE_TEXT.taskLoadFailed}`)
      }
      if (!studentRes.ok) {
        const msg = await studentRes.text().catch(() => '')
        throw new Error(`(${studentRes.status}) ${msg || PAGE_TEXT.taskLoadFailed}`)
      }

      const taskJson = await taskRes.json()
      const studentJson = await studentRes.json()
      setTask(taskJson)
      setStudent(studentJson)
    } catch (err) {
      setError(err instanceof Error ? err.message : PAGE_TEXT.taskLoadFailed)
      setTask(null)
    } finally {
      setLoading(false)
    }
  }, [apiBaseUrl, encodedLectureId])

  useEffect(() => {
    fetchData()
  }, [fetchData])

  const handleOpenForm = () => {
    setSubmitMessage('')
    setSubmitError('')
    setStatusMessage('')
    if (lastSubmission) {
      setSubmitDesc(lastSubmission.submitDesc || '')
      const existingId =
        (Array.isArray(lastSubmission.submitFiles) && lastSubmission.submitFiles[0]?.fileId) || ''
      setCurrentFileId(existingId)
      setExistingFiles(Array.isArray(lastSubmission.submitFiles) ? lastSubmission.submitFiles : [])
    } else {
      setSubmitDesc('')
      setCurrentFileId('')
      setExistingFiles([])
    }
    setUploadedFiles([])
    if (fileInputRef.current) fileInputRef.current.value = ''
    setShowForm(true)
  }

  const handleCancelForm = () => {
    setShowForm(false)
    setSubmitError('')
    setSubmitMessage('')
    setStatusMessage('')
    setUploadedFiles([])
    if (fileInputRef.current) fileInputRef.current.value = ''
  }

  const handleDebugFill = () => {
    const today = new Date()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')
    setSubmitDesc(`${month}월 ${day}일 제출했습니다`)
  }

  const handleFileChange = async (event) => {
    const files = Array.from(event.target.files || [])
    setSubmitError('')
    setSubmitMessage('')
    if (files.length === 0) return
    if (!lectureId) {
      setSubmitError(PAGE_TEXT.invalidLecture)
      return
    }
    if (files.length > 5) {
      setSubmitError(PAGE_TEXT.fileLimit)
      event.target.value = ''
      return
    }

    try {
      setSubmitLoading(true)
      const formData = new FormData()
      files.forEach((file) => formData.append('files', file))
      const uploadUrl = `/classroom/api/v1/common/${encodedLectureId}/upload?type=task`
      const resp = await fetch(uploadUrl, {
        method: 'POST',
        body: formData,
        credentials: 'include',
      })
      if (!resp.ok) {
        const msg = await resp.text().catch(() => '')
        throw new Error(msg || PAGE_TEXT.uploadError)
      }
      const data = await resp.json()
      const { fileId, details } = extractUploadResult(data)
      setCurrentFileId(fileId)
      setUploadedFiles(details)
      setSubmitMessage(PAGE_TEXT.uploadSuccess)
      setExistingFiles([])
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : PAGE_TEXT.uploadError)
    } finally {
      setSubmitLoading(false)
      if (event.target) event.target.value = ''
    }
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    if (!apiBaseUrl) {
      setSubmitError(PAGE_TEXT.invalidLecture)
      return
    }
    try {
      setSubmitLoading(true)
      setSubmitError('')
      setSubmitMessage('')

      const payload = {
        submitDesc,
        submitFileId: currentFileId || null,
      }

      const resp = await fetch(apiBaseUrl, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload),
      })

      if (resp.status !== 204) {
        const msg = await resp.text().catch(() => '')
        throw new Error(msg || PAGE_TEXT.submitError)
      }

      setSubmitMessage(PAGE_TEXT.submitSuccess)
      setStatusMessage(PAGE_TEXT.submitSuccess)
      setShowForm(false)
      setUploadedFiles([])
      if (fileInputRef.current) fileInputRef.current.value = ''
      await fetchData()
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : PAGE_TEXT.submitError)
    } finally {
      setSubmitLoading(false)
    }
  }

  const renderTaskSkeleton = () => (
    <>
      <div className="d-flex align-items-start flex-wrap mb-2">
        <span className="badge skeleton" style={{ width: 64, height: 22 }} />
        <span className="badge skeleton ms-2" style={{ width: 56, height: 22 }} />
      </div>
      <h1 className="h4 mb-3 skeleton" style={{ width: '60%', height: '1.5rem' }} />
      <article className="task-content mb-4"><div className="skeleton" style={{ height: '8rem' }} /></article>
    </>
  )

  const taskHasAttachments = Boolean(task?.attachFileList && task.attachFileList.length > 0)

  // 마감 여부 확인
  const isExpired = useMemo(() => {
    if (!task?.endAt) return false
    const endDate = new Date(task.endAt)
    return endDate.getTime() < Date.now()
  }, [task])

  // 제출 여부 확인 (submitAt이 null이면 미제출)
  const hasSubmitted = useMemo(() => {
    return Boolean(lastSubmission?.submitAt)
  }, [lastSubmission])

  const statusButtonLabel = hasSubmitted ? PAGE_TEXT.submitButtonEdit : PAGE_TEXT.submitButtonNew
  const formTitle = hasSubmitted ? PAGE_TEXT.formTitleEdit : PAGE_TEXT.formTitleNew

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
          <div className="alert alert-danger" role="alert">{error}</div>
        ) : task ? (
          <>
            {/* 과제 정보 카드 - 가로 3/5 */}
            <div className="card shadow-sm" style={{ flex: '3', minHeight: 0, display: 'flex', flexDirection: 'column' }}>
            <div className="card-header d-flex align-items-center justify-content-between">
              <div className="d-flex align-items-center gap-2">
                <span className="badge text-bg-primary">개인과제</span>
                <h1 className="h5 mb-0">{!loading && task ? task.indivtaskName : '과제 정보'}</h1>
              </div>
              {!loading && task && (
                <div className="d-flex gap-3 small text-nowrap">
                  <span className="small">시작: <span className="fw-bold text-primary">{fmtDate(task.startAt)}</span></span>
                  <span className="small">마감: <span className="fw-bold text-danger">{fmtDate(task.endAt)}</span></span>
                </div>
              )}
            </div>
              <div className="card-body" style={{ overflow: 'auto', flex: 1, display: 'flex', flexDirection: 'column' }}>
                                  <article
                                    className="task-content mb-4"
                                    dangerouslySetInnerHTML={{ __html: task.indivtaskDesc || '(내용 없음)' }}
                                  />
                                                </div>
              {taskHasAttachments && (
                <div className="card-footer" id="attach-area">
                  <h2 className="h6 mb-2">{PAGE_TEXT.attachments}</h2>
                  <ul className="list-group list-group-flush" id="attach-list">
                    {task.attachFileList.map((file, index) => {
                      const name =
                        file.originalFileName ||
                        file.originName ||
                        `첨부 ${file.fileOrder ?? index + 1}`
                      const size =
                        typeof file.fileSize === 'number'
                          ? ` (${(file.fileSize / 1024).toFixed(2)} KB)`
                          : ''
                      const href = buildTaskFileHref(lectureId, indivtaskId, file.fileOrder ?? index + 1)
                      return (
                        <li
                          key={`task-attach-${file.fileOrder ?? index}`}
                          className="list-group-item d-flex justify-content-between align-items-center"
                        >
                          <div className="text-truncate" style={{ maxWidth: '70%' }}>
                            <i className="bi bi-paperclip me-1" />
                            {name}
                            <small className="text-muted">{size}</small>
                          </div>
                          <a className="btn btn-sm btn-outline-secondary" href={href}>
                            {PAGE_TEXT.download}
                          </a>
                        </li>
                      )
                    })}
                  </ul>
                </div>
              )}
            </div>

            {/* 제출 현황/폼 카드 - 가로 2/5 */}
            <div className="card shadow-sm" style={{ flex: '2', minHeight: 0, display: 'flex', flexDirection: 'column' }}>
              <div className="card-header d-flex justify-content-between align-items-center">
                <h2 className="h5 mb-0">{PAGE_TEXT.submitStatus}</h2>
                {!showForm && (
                  <button
                    type="button"
                    id="show-submit-form-btn"
                    className={isExpired ? 'attendance-btn attendance-btn-default' : 'attendance-btn attendance-btn-save'}
                    onClick={handleOpenForm}
                    disabled={loading || submitLoading}
                    style={{ minWidth: '100px' }}
                  >
                    {statusButtonLabel}
                  </button>
                )}
                {showForm && (
                  <button type="button" className="btn-close" onClick={handleCancelForm} aria-label="Close" />
                )}
              </div>
              {!showForm && (
                <>
                  <div className="card-body" id="submission-history-body" style={{ overflow: 'auto', flex: 1 }}>
                    {statusMessage && (
                      <div className="alert alert-success" role="alert">
                        {statusMessage}
                      </div>
                    )}
                    {loading && <p className="text-muted">{PAGE_TEXT.submitHistoryLoading}</p>}
                    {!loading && !lastSubmission && (
                      <div className="text-center p-4">
                        <p className="text-muted">{PAGE_TEXT.submitNotFound}</p>
                      </div>
                    )}
                    {!loading && lastSubmission && (
                      <div>
                        <dl className="row">
                          <dt className="col-sm-3">{PAGE_TEXT.submittedAtLabel}</dt>
                          <dd className="col-sm-9">{formatDateTime(lastSubmission.submitAt)}</dd>

                          <dt className="col-sm-3">{PAGE_TEXT.descriptionLabel}</dt>
                          <dd className="col-sm-9">{lastSubmission.submitDesc || '-'}</dd>
                        </dl>
                        
                        <h6 className="mt-3 mb-2">{PAGE_TEXT.attachments}</h6>
                        <ul className="list-group">
                          {Array.isArray(lastSubmission.submitFiles) && lastSubmission.submitFiles.length > 0 ? (
                            lastSubmission.submitFiles.map((file, index) => {
                              const name =
                                file.originalFileName ||
                                file.originName ||
                                `첨부 ${file.fileOrder ?? index + 1}`
                              const size =
                                typeof file.fileSize === 'number'
                                  ? ` (${(file.fileSize / 1024).toFixed(2)} KB)`
                                  : ''
                              const href = buildSubmissionFileHref(
                                lectureId,
                                indivtaskId,
                                student?.studentNo,
                                file.fileOrder ?? index + 1,
                              )
                              return (
                                <li
                                  key={`submit-file-${file.fileOrder ?? index}`}
                                  className="list-group-item d-flex justify-content-between align-items-center"
                                >
                                  <div className="text-truncate" style={{ maxWidth: '70%' }}>
                                    <i className="bi bi-paperclip me-1" />
                                    {name}
                                    <small className="text-muted">{size}</small>
                                  </div>
                                  <a className="btn btn-sm btn-outline-secondary" href={href}>
                                    {PAGE_TEXT.download}
                                  </a>
                                </li>
                              )
                            })
                          ) : (
                            <li className="list-group-item text-muted">{PAGE_TEXT.noFiles}</li>
                          )}
                        </ul>
                      </div>
                    )}
                  </div>
                  {!loading && lastSubmission?.evaluAt && (
                    <div className="card-footer bg-success-subtle">
                        <h2 className="h6 mb-2 text-success-emphasis">교수님 피드백</h2>
                        <dl className="row mb-0">
                          <dt className="col-sm-3">{PAGE_TEXT.scoreLabel}</dt>
                          <dd className="col-sm-9 fw-bold">{lastSubmission.evaluScore ?? '-'}</dd>

                          <dt className="col-sm-3">{PAGE_TEXT.feedbackLabel}</dt>
                          <dd className="col-sm-9">{lastSubmission.evaluDesc || '-'}</dd>

                          <dt className="col-sm-3 text-muted">{PAGE_TEXT.evaluatedAtLabel}</dt>
                          <dd className="col-sm-9 text-muted">{formatDateTime(lastSubmission.evaluAt)}</dd>
                        </dl>
                    </div>
                  )}
                </>
              )}

              {showForm && (
                <>
                  <div className="card-body" id="submission-form-body" style={{ overflow: 'auto', flex: 1 }}>
                    {submitError && (
                      <div className="alert alert-danger" role="alert">
                        {submitError}
                      </div>
                    )}
                    {submitMessage && (
                      <div className="alert alert-success" role="alert">
                        {submitMessage}
                      </div>
                    )}
                    <form id="submit-form" onSubmit={handleSubmit}>
                      <div className="mb-3">
                        <div className="d-flex justify-content-between align-items-center mb-2">
                          <label htmlFor="submit-desc" className="form-label mb-0">
                            {PAGE_TEXT.formDescription}
                          </label>
                          <button
                            type="button"
                            className="btn btn-sm btn-outline-secondary"
                            onClick={handleDebugFill}
                            disabled={submitLoading}
                          >
                            디버깅
                          </button>
                        </div>
                        <textarea
                          className="form-control"
                          id="submit-desc"
                          rows={4}
                          placeholder="제출한 내용을 간단히 설명해주세요."
                          value={submitDesc}
                          onChange={(e) => setSubmitDesc(e.target.value)}
                        />
                      </div>
                      <div className="mb-3">
                        <label htmlFor="submit-file-input" className="form-label">
                          {PAGE_TEXT.formFiles}
                        </label>
                        {existingFiles.length > 0 && uploadedFiles.length === 0 && (
                          <div className="mb-3">
                            <label className="form-label text-muted small">
                              {PAGE_TEXT.existingFiles}
                            </label>
                            <ul className="list-group">
                              {existingFiles.map((file, index) => {
                                const name =
                                  file.originalFileName ||
                                  file.originName ||
                                  `첨부 ${file.fileOrder ?? index + 1}`
                                const size =
                                  typeof file.fileSize === 'number'
                                    ? ` (${(file.fileSize / 1024).toFixed(2)} KB)`
                                    : ''
                                return (
                                  <li
                                    key={`existing-${file.fileOrder ?? index}`}
                                    className="list-group-item list-group-item-light d-flex justify-content-between align-items-center"
                                  >
                                    <span>{name}</span>
                                    <small className="text-muted">{size}</small>
                                  </li>
                                )
                              })}
                            </ul>
                          </div>
                        )}
                        {uploadedFiles.length > 0 && (
                          <div className="mb-3">
                            <label className="form-label text-muted small">
                              {PAGE_TEXT.newFiles}
                            </label>
                            <ul className="list-group">
                              {uploadedFiles.map((file, index) => {
                                const name =
                                  file.originalName ||
                                  file.originName ||
                                  file.originalFileName ||
                                  `첨부 ${file.fileOrder ?? index + 1}`
                                const size =
                                  typeof file.fileSize === 'number'
                                    ? ` (${(file.fileSize / 1024).toFixed(2)} KB)`
                                    : ''
                                return (
                                  <li
                                    key={`uploaded-${file.fileId ?? index}`}
                                    className="list-group-item list-group-item-light d-flex justify-content-between align-items-center"
                                  >
                                    <span>{name}</span>
                                    <small className="text-muted">{size}</small>
                                  </li>
                                )
                              })}
                            </ul>
                          </div>
                        )}
                        <input
                          className="form-control"
                          type="file"
                          id="submit-file-input"
                          ref={fileInputRef}
                          multiple
                          onChange={handleFileChange}
                          disabled={submitLoading}
                        />
                        <div className="form-text">{PAGE_TEXT.fileGuide}</div>
                      </div>
                    </form>
                  </div>
                  <div className="card-footer d-flex justify-content-end gap-2">
                    <button
                      type="button"
                      id="cancel-submit-btn"
                      className="attendance-btn attendance-btn-default"
                      onClick={handleCancelForm}
                      disabled={submitLoading}
                      style={{ minWidth: '100px' }}
                    >
                      {PAGE_TEXT.cancel}
                    </button>
                    <button
                      type="submit"
                      className="attendance-btn attendance-btn-save"
                      id="submit-button"
                      disabled={submitLoading}
                      form="submit-form"
                      style={{ minWidth: '100px' }}
                    >
                      {submitLoading ? PAGE_TEXT.taskLoading : PAGE_TEXT.submit}
                    </button>
                  </div>
                </>
              )}
            </div>
          </>
        ) : null}
      </div>

      

      {error && <div id="error-box" className="alert alert-danger mt-3" role="alert">{error}</div>}
    </section>
  )
}
