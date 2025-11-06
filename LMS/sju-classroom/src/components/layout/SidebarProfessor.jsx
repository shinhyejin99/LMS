import React, { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'

export default function SidebarProfessor() {
  const [expanded, setExpanded] = useState(false)
  const [loadingTasks, setLoadingTasks] = useState(false)
  const [taskError, setTaskError] = useState(null)
  const [rawTasks, setRawTasks] = useState([])

  const [expandedGroup, setExpandedGroup] = useState(false)
  const [loadingGroupTasks, setLoadingGroupTasks] = useState(false)
  const [groupTaskError, setGroupTaskError] = useState(null)
  const [rawGroupTasks, setRawGroupTasks] = useState([])

  const [expandedLectures, setExpandedLectures] = useState(false)
  const [loadingLectures, setLoadingLectures] = useState(false)
  const [lecturesError, setLecturesError] = useState(null)
  const [rawLectures, setRawLectures] = useState([])

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoadingTasks(true)
        setTaskError(null)
        const res = await fetch('/classroom/api/v1/professor/me/indivtask', {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) throw new Error(`(${res.status}) 개인과제 목록을 불러오지 못했습니다.`)
        const data = await res.json()
        if (!alive) return
        setRawTasks(Array.isArray(data) ? data : [])
      } catch (err) {
        if (!alive) return
        setTaskError(err)
      } finally {
        if (alive) setLoadingTasks(false)
      }
    })()
    return () => { alive = false }
  }, [])

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoadingGroupTasks(true)
        setGroupTaskError(null)
        const res = await fetch('/classroom/api/v1/professor/me/grouptask', {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) throw new Error(`(${res.status}) 조별과제 목록을 불러오지 못했습니다.`)
        const data = await res.json()
        if (!alive) return
        setRawGroupTasks(Array.isArray(data) ? data : [])
      } catch (err) {
        if (!alive) return
        setGroupTaskError(err)
      } finally {
        if (alive) setLoadingGroupTasks(false)
      }
    })()
    return () => { alive = false }
  }, [])

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoadingLectures(true)
        setLecturesError(null)
        const res = await fetch('/classroom/api/v1/professor/mylist', {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) throw new Error(`(${res.status}) 강의 목록을 불러오지 못했습니다.`)
        const data = await res.json()
        if (!alive) return
        setRawLectures(Array.isArray(data) ? data : [])
      } catch (err) {
        if (!alive) return
        setLecturesError(err)
      } finally {
        if (alive) setLoadingLectures(false)
      }
    })()
    return () => { alive = false }
  }, [])

  const reviewTasks = useMemo(() => rawTasks.map((item) => {
    const lecture = item?.lecture || {}
    const task = item?.indivtask || {}
    const lectureId = lecture.lectureId || ''
    const lectureLabel = lecture.subjectName || '강의 미정'
    const taskId = task.indivtaskId || ''
    const taskLabel = task.indivtaskName || '과제 미정'
    const submitCount = Number(item?.submitCount ?? 0)
    return {
      lectureId,
      lectureLabel,
      taskId,
      taskLabel,
      submitCount: Number.isFinite(submitCount) ? submitCount : 0,
    }
  }), [rawTasks])

  const reviewGroupTasks = useMemo(() => rawGroupTasks.map((item) => {
    const lecture = item?.lecture || {}
    const task = item?.grouptask || {}
    const lectureId = lecture.lectureId || ''
    const lectureLabel = lecture.subjectName || '강의 미정'
    const taskId = task.grouptaskId || ''
    const taskLabel = task.grouptaskName || '과제 미정'
    const submitCount = Number(item?.submitCount ?? 0)
    return {
      lectureId,
      lectureLabel,
      taskId,
      taskLabel,
      submitCount: Number.isFinite(submitCount) ? submitCount : 0,
    }
  }), [rawGroupTasks])

  const ongoingLectures = useMemo(() => {
    return rawLectures
      .filter(l => l.started && !l.ended)
      .map(lecture => ({
        lectureId: lecture.lectureId || '',
        subjectName: lecture.subjectName || '강의 미정',
        completionName: lecture.completionName || '',
      }))
  }, [rawLectures])

  const taskCount = reviewTasks.length
  const groupTaskCount = reviewGroupTasks.length
  const ongoingLectureCount = ongoingLectures.length

  const toggleExpanded = () => setExpanded((prev) => !prev)
  const toggleExpandedGroup = () => setExpandedGroup((prev) => !prev)
  const toggleExpandedLectures = () => setExpandedLectures((prev) => !prev)

  return (
    <nav className="list-group list-group-flush rounded-0">
      <button
        type="button"
        className="list-group-item list-group-item-action d-flex align-items-center justify-content-between py-2 ps-3"
        onClick={toggleExpandedLectures}
        aria-expanded={expandedLectures}
        aria-controls="sidebar-ongoing-lectures-panel"
      >
        <span className="fw-bold" style={{ fontSize: '1.3em' }}>진행중인 강의</span>
        <span className="d-flex align-items-center gap-2">
          <span className="badge text-bg-secondary">{ongoingLectureCount}</span>
          <span aria-hidden="true">{expandedLectures ? '^' : 'v'}</span>
        </span>
      </button>

      {expandedLectures && (
        <div id="sidebar-ongoing-lectures-panel" className="list-group list-group-flush border-bottom rounded-0">
          {loadingLectures && <div className="list-group-item small text-muted">불러오는 중...</div>}
          {lecturesError && !loadingLectures && (
            <div className="list-group-item small text-danger">
              {String(lecturesError.message || lecturesError)}
            </div>
          )}
          {!loadingLectures && !lecturesError && ongoingLectures.length === 0 && (
            <div className="list-group-item small text-muted">진행중인 강의가 없습니다.</div>
          )}
          {!loadingLectures && !lecturesError && ongoingLectures.map((lecture) => {
            const { lectureId, subjectName, completionName } = lecture
            if (!lectureId) return null
            const href = `/classroom/professor/${encodeURIComponent(lectureId)}`
            return (
              <Link
                key={lectureId}
                to={href}
                className="list-group-item list-group-item-action small py-2 bg-white"
              >
                <div className="fw-semibold text-truncate">{subjectName}</div>
                {completionName && <div className="text-muted small">{completionName}</div>}
              </Link>
            )
          })}
        </div>
      )}

      <button
        type="button"
        className="list-group-item list-group-item-action d-flex align-items-center justify-content-between py-2 ps-3"
        onClick={toggleExpanded}
        aria-expanded={expanded}
        aria-controls="sidebar-review-task-panel"
      >
        <span className="fw-bold" style={{ fontSize: '1.3em' }}>평가 대기 개인과제</span>
        <span className="d-flex align-items-center gap-2">
          <span className="badge text-bg-secondary">{taskCount}</span>
          <span aria-hidden="true">{expanded ? '^' : 'v'}</span>
        </span>
      </button>

      {expanded && (
        <div id="sidebar-review-task-panel" className="list-group list-group-flush border-bottom rounded-0">
          {loadingTasks && <div className="list-group-item small text-muted">불러오는 중...</div>}
          {taskError && !loadingTasks && (
            <div className="list-group-item small text-danger">
              {String(taskError.message || taskError)}
            </div>
          )}
          {!loadingTasks && !taskError && reviewTasks.length === 0 && (
            <div className="list-group-item small text-muted">평가할 제출물이 없습니다.</div>
          )}
          {!loadingTasks && !taskError && reviewTasks.map((item) => {
            const { lectureId, lectureLabel, taskId, taskLabel, submitCount } = item
            if (!lectureId || !taskId) return null
            const href = `/classroom/professor/${encodeURIComponent(lectureId)}/task/indiv/${encodeURIComponent(taskId)}`
            return (
              <Link
                key={`${lectureId}-${taskId}`}
                to={href}
                className="list-group-item list-group-item-action small py-2 bg-white"
              >
                <div className="text-muted small">[{lectureLabel}]</div>
                <div className="fw-semibold text-truncate">{taskLabel}</div>
                <div className="text-muted">평가 대기중인 수강생: {submitCount}명</div>
              </Link>
            )
          })}
        </div>
      )}

      <button
        type="button"
        className="list-group-item list-group-item-action d-flex align-items-center justify-content-between py-2 ps-3"
        onClick={toggleExpandedGroup}
        aria-expanded={expandedGroup}
        aria-controls="sidebar-review-grouptask-panel"
      >
        <span className="fw-bold" style={{ fontSize: '1.3em' }}>평가 대기 조별과제</span>
        <span className="d-flex align-items-center gap-2">
          <span className="badge text-bg-secondary">{groupTaskCount}</span>
          <span aria-hidden="true">{expandedGroup ? '^' : 'v'}</span>
        </span>
      </button>

      {expandedGroup && (
        <div id="sidebar-review-grouptask-panel" className="list-group list-group-flush border-bottom rounded-0">
          {loadingGroupTasks && <div className="list-group-item small text-muted">불러오는 중...</div>}
          {groupTaskError && !loadingGroupTasks && (
            <div className="list-group-item small text-danger">
              {String(groupTaskError.message || groupTaskError)}
            </div>
          )}
          {!loadingGroupTasks && !groupTaskError && reviewGroupTasks.length === 0 && (
            <div className="list-group-item small text-muted">평가할 제출물이 없습니다.</div>
          )}
          {!loadingGroupTasks && !groupTaskError && reviewGroupTasks.map((item) => {
            const { lectureId, lectureLabel, taskId, taskLabel, submitCount } = item
            if (!lectureId || !taskId) return null
            const href = `/classroom/professor/${encodeURIComponent(lectureId)}/task/group/${encodeURIComponent(taskId)}`
            return (
              <Link
                key={`${lectureId}-${taskId}`}
                to={href}
                className="list-group-item list-group-item-action small py-2 bg-white"
              >
                <div className="text-muted small">[{lectureLabel}]</div>
                <div className="fw-semibold text-truncate">{taskLabel}</div>
                <div className="text-muted">평가 대기중인 팀: {submitCount}팀</div>
              </Link>
            )
          })}
        </div>
      )}
    </nav>
  )
}
