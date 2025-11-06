import React, { useEffect, useMemo, useRef, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import Swal from 'sweetalert2'
import '../../styles/avatars.css'
import { useLecture } from '../../context/LectureContext'
import NotFound from '../../components/NotFound'

const pad = (n) => String(n).padStart(2, '0')
const fmtDateTime = (iso) => {
  if (!iso) return '-'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

const truncateFileName = (name, maxLength = 50) => {
  if (typeof name !== 'string') return ''
  if (name.length <= maxLength) return name
  return `${name.substring(0, maxLength)}...`
}

const formatFileSize = (bytes) => {
  if (typeof bytes !== 'number' || bytes < 0) return ''
  const mb = 1024 * 1024
  const kb = 1024
  if (bytes >= mb) {
    return `(${(bytes / mb).toFixed(2)} MB)`
  }
  if (bytes >= kb) {
    return `(${(bytes / kb).toFixed(2)} KB)`
  }
  return `(${bytes} Bytes)`
}
const phaseBadge = (startAt, endAt) => {
  const now = Date.now()
  const s = startAt ? new Date(startAt).getTime() : null
  const e = endAt ? new Date(endAt).getTime() : null
  if (s && now < s) return <span className="badge text-bg-secondary badge-type">시작 전</span>
  if (e && now > e) return <span className="badge text-bg-dark badge-type">종료</span>
  return <span className="badge text-bg-success badge-type">진행 중</span>
}

export default function TaskGroup() {
  const { lectureId, grouptaskId } = useParams()
  const navigate = useNavigate()
  const listUrl = `/classroom/professor/${encodeURIComponent(lectureId || '')}/task`

  const profApiBase = '/classroom/api/v1/professor'
  const taskCommonBase = '/classroom/api/v1/common/task'
  const taskProfBase = '/classroom/api/v1/professor/task'
  const photoFallbackBase = '/classroom/api/v1/common'

  const [task, setTask] = useState(null)
  const [students, setStudents] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [notFound, setNotFound] = useState(false)
  const [deleting, setDeleting] = useState(false)

  const { getStudentPhotoUrl, getStudentPhotoBlobUrl, cacheStudentPhotoBlobUrl } = useLecture()
  const [photoUrls, setPhotoUrls] = useState({})
  const fetchingRef = useRef(new Set())

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        setNotFound(false)
        const [taskRes, joRes, stuRes] = await Promise.all([
          fetch(`${taskProfBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${taskProfBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}/jo`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${profApiBase}/${encodeURIComponent(lectureId)}/students`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
        ])
        if (taskRes.status === 404 || taskRes.status === 500) {
          if (!alive) return
          setNotFound(true)
          return
        }
        if (!taskRes.ok) throw new Error(`(${taskRes.status}) 조별과제 조회 실패`)
        if (!joRes.ok) throw new Error(`(${joRes.status}) 조별과제 조 편성 조회 실패`)
        const [taskData, joData, studentData] = await Promise.all([
          taskRes.json(),
          joRes.json(),
          stuRes.ok ? stuRes.json() : Promise.resolve([]),
        ])
        if (!alive) return

        const finalTask = {
          ...taskData,
          groupList: joData,
        }
        setTask(finalTask)
        setStudents(Array.isArray(studentData) ? studentData : [])
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, grouptaskId, profApiBase, taskProfBase])

  const studentMap = useMemo(() => new Map(students.map(s => [s.enrollId, s])), [students])

  const allStudentNos = useMemo(() => {
    const nos = new Set()
    if (Array.isArray(task?.groupList)) {
      task.groupList.forEach(group => {
        const crew = Array.isArray(group?.crewList) ? group.crewList : []
        crew.forEach(member => {
          const info = studentMap.get(member.enrollId)
          if (info?.studentNo) nos.add(info.studentNo)
        })
        const leaderEnroll = group?.leaderEnrollId
        if (leaderEnroll) {
          const leaderInfo = studentMap.get(leaderEnroll)
          if (leaderInfo?.studentNo) nos.add(leaderInfo.studentNo)
        }
      })
    }
    return Array.from(nos)
  }, [task, studentMap])

  useEffect(() => {
    allStudentNos.forEach(no => {
      if (!no || fetchingRef.current.has(no)) return
      const cached = getStudentPhotoBlobUrl(no)
      if (cached) {
        setPhotoUrls(prev => (prev[no] ? prev : { ...prev, [no]: cached }))
        return
      }
      const url = getStudentPhotoUrl(no, lectureId)
      if (!url) return
      fetchingRef.current.add(no)
      fetch(url, { credentials: 'include' })
        .then(res => (res.ok ? res.blob() : null))
        .then(blob => {
          if (!blob) return
          const objectUrl = URL.createObjectURL(blob)
          cacheStudentPhotoBlobUrl(no, objectUrl)
          setPhotoUrls(prev => ({ ...prev, [no]: objectUrl }))
        })
        .catch(() => {})
        .finally(() => {
          fetchingRef.current.delete(no)
        })
    })
  }, [allStudentNos, lectureId, getStudentPhotoBlobUrl, getStudentPhotoUrl, cacheStudentPhotoBlobUrl])

  const handleDelete = async () => {
    const result = await Swal.fire({
      title: '삭제 확인',
      text: '이 조별과제를 삭제하시겠습니까?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      confirmButtonColor: '#dc3545'
    })
    if (!result.isConfirmed) return
    try {
      setDeleting(true)
      const res = await fetch(`${taskProfBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}`, { method: 'DELETE', credentials: 'include', headers: { Accept: 'application/json' } })
      if (!(res.status === 204 || res.ok)) throw new Error(`(${res.status}) 삭제 실패`)
      await Swal.fire({
        title: '삭제 완료',
        text: '조별과제가 삭제되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      })
      navigate(listUrl)
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

  const resolvePhoto = (student) => {
    if (!student?.studentNo) return null
    return photoUrls[student.studentNo] || getStudentPhotoBlobUrl(student.studentNo) || getStudentPhotoUrl(student.studentNo, lectureId) || `${photoFallbackBase}/photo/student/${encodeURIComponent(lectureId)}/${encodeURIComponent(student.studentNo)}`
  }

  const renderGroupAccordion = (groupList) => {
    if (!Array.isArray(groupList) || groupList.length === 0) {
      return <p className="text-muted mb-0">조 편성이 없습니다.</p>
    }
    return groupList.map((group, index) => {
      const crewList = Array.isArray(group?.crewList) ? group.crewList : []
      const leaderEnroll = group.leaderEnrollId
      const leader = leaderEnroll ? (studentMap.get(leaderEnroll) || null) : null
      const leaderName = leader ? (`${leader.lastName || ''}${leader.firstName || ''}` || leader.name || leader.userId || '-') : '-'
      const leaderInitial = (leader?.lastName || leader?.firstName || leaderName || '?').charAt(0)
      const leaderPhoto = resolvePhoto(leader)
      const memberCount = crewList.length
      const submittedAt = group.submitAt ? fmtDateTime(group.submitAt) : ''
      const submitBadge = group.submitAt ? <span className="badge text-bg-success">제출 완료</span> : <span className="badge text-bg-secondary">미제출</span>
      const submitStatus = group.submitAt ? `제출 ${submittedAt}` : '미제출'
      const groupName = group.groupName || `${index + 1}조`
      const leaderMeta = leader?.studentNo ? ` (${leader.studentNo})` : ''
      const submitFileHref = (group?.submitFileId && group?.groupId)
        ? `${taskProfBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}/submit/${encodeURIComponent(group.groupId)}`
        : null

      const submittedFiles = group.attachFileList
      const filesArea = (
        <div className="mt-3">
          <div className="small text-muted">제출 파일</div>
          {(Array.isArray(submittedFiles) && submittedFiles.length > 0) ? (
            <ul className="list-group list-group-flush mt-1">
              {submittedFiles.map((file, idx) => {
                const order = file.fileOrder ?? idx + 1
                const fileName = file.originalFileName || file.originName || `파일 ${order}`
                const formattedFileName = truncateFileName(fileName, 20)
                const sizeText = formatFileSize(file.fileSize)
                const href = `${taskProfBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}/submit/${encodeURIComponent(group.groupId)}/${encodeURIComponent(order)}`
                return (
                  <li key={order} className="list-group-item ps-0">
                    <a href={href} className="text-decoration-none text-body" title={fileName}>
                      <i className="bi bi-paperclip" /> {formattedFileName} {sizeText && <small className="text-muted">{sizeText}</small>}
                    </a>
                  </li>
                )
              })}
            </ul>
          ) : (
            <div className="mt-1">
              <span className="text-muted">제출된 파일이 없습니다.</span>
            </div>
          )}
        </div>
      )

      return (
        <div className="accordion-item" key={index}>
          <h2 className="accordion-header" id={`group-acc-head-${index}`}>
            <button className="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target={`#group-acc-body-${index}`}>
              <div className="group-summary">
                {leaderPhoto ? (
                  <img className="avatar-56 me-2" alt={`${leaderName} 조장`} src={leaderPhoto} />
                ) : (
                  <span className="avatar-fallback-56 me-2">{leaderInitial}</span>
                )}
                <div className="group-summary-text">
                  <div className="group-summary-title">
                    <span className="fw-semibold">{groupName}</span>
                    {submitBadge}
                  </div>
                  <div className="group-summary-meta">
                    <span><i className="bi bi-person-badge" /> 조장 {leaderName}{leaderMeta}</span>
                    <span><i className="bi bi-people" /> 조원 {memberCount}명</span>
                    <span><i className="bi bi-clock-history" /> {submitStatus}</span>
                  </div>
                </div>
              </div>
            </button>
          </h2>
          <div id={`group-acc-body-${index}`} className="accordion-collapse collapse" data-bs-parent="#group-accordion">
            <div className="accordion-body">
              <div className="mb-4">
                <h3 className="h6 mb-3">제출 요약</h3>
                <div className="row g-3">
                  <div className="col-12 col-md-4">
                    <div className="small text-muted">제출 시간</div>
                    <div>{group.submitAt ? submittedAt : <span className="text-muted">미제출</span>}</div>
                  </div>
                  <div className="col-12 col-md-8">
                    <div className="small text-muted">제출 설명</div>
                    <div className="mt-1" dangerouslySetInnerHTML={{ __html: group?.submitDesc || '<span class="text-muted">제출 설명이 없습니다.</span>' }} />
                  </div>
                </div>
                {filesArea}
              </div>
              <div>
                <h3 className="h6 mb-3">조원 현황</h3>
                <div className="row g-3">
                  {crewList.length === 0 ? (
                    <p className="text-muted mb-0">조원 정보가 없습니다.</p>
                  ) : (
                    crewList.map((crew, ci) => {
                      const student = crew?.enrollId ? (studentMap.get(crew.enrollId) || null) : null
                      const name = student ? (`${student.lastName || ''}${student.firstName || ''}` || student.name || student.userId || '-') : '-'
                      const isLeader = leaderEnroll && crew?.enrollId === leaderEnroll
                      const studentNo = student?.studentNo || '학번 정보 없음'
                      const hasScore = crew?.evaluScore !== '' && crew?.evaluScore != null
                      const scoreText = hasScore ? `${crew.evaluScore} / 10점` : '미평가'
                      const evalAt = crew?.evaluAt ? fmtDateTime(crew.evaluAt) : '미평가'
                      const studentInitial = (student?.lastName || student?.firstName || name || '?').charAt(0)
                      const studentPhoto = resolvePhoto(student)
                      return (
                        <div className="col-12 col-md-6 col-lg-4" key={crew?.enrollId || ci}>
                          <div className="member-card h-100">
                            <div className="d-flex align-items-center gap-3">
                              {studentPhoto ? (
                                <img className="avatar-48" alt={`${name} 사진`} src={studentPhoto} />
                              ) : (
                                <span className="avatar-fallback-48">{studentInitial}</span>
                              )}
                              <div>
                                <div className="fw-semibold">{name}</div>
                                <div className="member-meta">{studentNo}</div>
                                {isLeader && <span className="badge text-bg-primary mt-2">조장</span>}
                              </div>
                            </div>
                            <div className="evaluation-box mt-3">
                              <div><span className="fw-semibold">개인 평가 :</span> {scoreText}</div>
                              <div className="evaluation-note mt-2">{crew?.evaluDesc || ''}</div>
                              <div className="evaluation-note mt-2">평가 일시: {evalAt}</div>
                            </div>
                          </div>
                        </div>
                      )
                    })
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      )
    })
  }

  const groupCount = (task?.groupList || []).length
  const memberCount = (task?.groupList || []).reduce((acc, g) => acc + (g?.crewList?.length || 0), 0)
  const submittedGroupCount = (task?.groupList || []).filter(g => g?.submitAt).length
  const editUrl = useMemo(() => `${listUrl}/group/${encodeURIComponent(grouptaskId || '')}/edit`, [listUrl, grouptaskId])

  if (notFound) {
    return <NotFound message="조별과제를 찾을 수 없습니다" resourceType="조별과제" />
  }

  return (
    <section id="grouptask-post-root" className="container-fluid" style={{ display: 'flex', flexDirection: 'column', padding: '1rem', maxHeight: '100vh' }}>
      <div className="d-flex align-items-center justify-content-between mb-2">
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb mb-0">
            <li className="breadcrumb-item"><Link id="bc-task" to={listUrl}>과제</Link></li>
            <li className="breadcrumb-item active" aria-current="page">조별과제</li>
          </ol>
        </nav>
        <div className="d-flex gap-2">
          <Link
            className="btn btn-sm btn-outline-secondary"
            id="btn-edit"
            to={`/classroom/professor/${encodeURIComponent(lectureId)}/task/group/${encodeURIComponent(grouptaskId)}/edit`}
          >
            수정
          </Link>
          <button
            className="btn btn-sm btn-outline-danger"
            id="btn-delete"
            onClick={handleDelete}
            disabled={deleting}
          >
            {deleting ? '삭제중...' : '삭제'}
          </button>
          <Link className="btn btn-sm btn-outline-secondary" id="btn-list" to={listUrl}>목록</Link>
        </div>
      </div>

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
                  <span className="badge text-bg-primary">조별과제</span>
                  <h1 className="h5 mb-0">{task.grouptaskName || '(제목 없음)'}</h1>
                </div>
                <div className="d-flex gap-2">
                  <a className="btn btn-sm btn-primary" id="btn-edit-task" href={editUrl} role="button">편집</a>
                  <button className="btn btn-sm btn-danger" id="btn-delete-task" type="button" onClick={handleDelete} disabled={deleting}>{deleting ? '삭제중…' : '삭제'}</button>
                </div>
              </div>
              <div className="card-body" style={{ overflow: 'auto', flex: 1, display: 'flex', flexDirection: 'column' }}>
                <div className="d-flex flex-wrap gap-3 text-muted small mb-3">
                  <div>작성: <span className="text-body-secondary">{fmtDateTime(task.createAt)}</span></div>
                  <div>시작: <span className="text-body-secondary">{fmtDateTime(task.startAt)}</span></div>
                  <div>마감: <span className="text-body-secondary">{fmtDateTime(task.endAt)}</span></div>
                </div>
                <hr className="my-3" />
                <article className="task-content mb-4" style={{ flex: 1 }} dangerouslySetInnerHTML={{ __html: task.grouptaskDesc || '' }} />
              </div>
              {Array.isArray(task.attachFileList) && task.attachFileList.length > 0 && (
                <div className="card-footer" id="attach-area">
                  <h2 className="h6 mb-2">첨부파일</h2>
                  <ul className="list-group list-group-flush" id="attach-list">
                    {task.attachFileList.map((file, idx) => {
                      const order = file.fileOrder ?? file.order ?? idx
                      const fileName = file.originalFileName || file.originName || `파일 ${order ?? idx}`
                      const formattedFileName = truncateFileName(fileName)
                      const sizeText = formatFileSize(file.fileSize)
                      const href = `${taskCommonBase}/${encodeURIComponent(lectureId)}/group/${encodeURIComponent(grouptaskId)}/attach/${encodeURIComponent(order)}`
                      return (
                        <li key={`${order ?? idx}`} className="list-group-item">
                          <a href={href} className="text-decoration-none text-body" title={fileName}>
                            <i className="bi bi-paperclip" /> {formattedFileName} {sizeText && <small className="text-muted">{sizeText}</small>}
                          </a>
                        </li>
                      )
                    })}
                  </ul>
                </div>
              )}
            </div>

            {/* 조 제출 현황 카드 - 가로 2/5 */}
            <div className="card shadow-sm" style={{ flex: '2', minHeight: 0, display: 'flex', flexDirection: 'column' }}>
              <div className="card-header d-flex align-items-center justify-content-between">
                <h3 className="h5 mb-0">조 제출 현황</h3>
                <span className="text-muted">{submittedGroupCount} / {groupCount}</span>
              </div>
              <div className="card-body" style={{ overflow: 'auto', padding: 0 }}>
                <div id="group-accordion" className="accordion accordion-flush">
                  {renderGroupAccordion(task?.groupList || [])}
                </div>
              </div>
            </div>
          </>
        ) : null}
      </div>

      {error && <div id="error-box" className="alert alert-danger mt-3" role="alert">{error}</div>}
    </section>
  )
}
