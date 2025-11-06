import React, { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
// 📌 아이콘 임포트
import {BsBookFill,  BsClipboard2Check, BsBellFill,} from 'react-icons/bs'; // Bootstrap Icons 사용 예시

const LABELS = {
  toggleOpen: '열기',
  toggleClose: '닫기',
  loading: '불러오는 중...',
  loadError: '목록을 불러오는 중 오류가 발생했습니다.',
  ongoingLectures: '수강중인 강의',
  ongoingEmpty: '수강 중인 강의가 없습니다.',
  tasks: '제출할 과제',
  tasksEmpty: '제출할 과제가 없습니다.',
  reviews: '강의평가 작성',
  reviewsEmpty: '작성해야 할 강의평가가 없습니다.',
}

const API_LECTURES = '/classroom/api/v1/student/mylist'
const API_TASKS = '/classroom/api/v1/student/me/indivtask'
const API_REVIEWS = '/classroom/api/v1/student/review'

const pad = (value) => String(value).padStart(2, '0')
const formatDateTime = (value) => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export default function SidebarStudent() {
  const [lecturesOpen, setLecturesOpen] = useState(false)
  const [lectures, setLectures] = useState([])
  const [lecturesLoading, setLecturesLoading] = useState(false)
  const [lecturesError, setLecturesError] = useState('')

  const [tasksOpen, setTasksOpen] = useState(false)
  const [tasks, setTasks] = useState([])
  const [tasksLoading, setTasksLoading] = useState(false)
  const [tasksError, setTasksError] = useState('')

  const [reviewsOpen, setReviewsOpen] = useState(false)
  const [reviewLectureIds, setReviewLectureIds] = useState([])
  const [reviewsLoading, setReviewsLoading] = useState(false)
  const [reviewsError, setReviewsError] = useState('')

  useEffect(() => {
    let cancelled = false
    async function fetchLectures() {
      try {
        setLecturesLoading(true)
        setLecturesError('')
        const res = await fetch(API_LECTURES, {
          headers: { Accept: 'application/json' },
          credentials: 'same-origin',
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        if (!cancelled) {
          setLectures(Array.isArray(data) ? data : [])
        }
      } catch (err) {
        console.error('[SidebarStudent] failed to load lectures', err)
        if (!cancelled) {
          setLectures([])
          setLecturesError(LABELS.loadError)
        }
      } finally {
        if (!cancelled) setLecturesLoading(false)
      }
    }
    fetchLectures()
    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    let cancelled = false
    async function fetchTasks() {
      try {
        setTasksLoading(true)
        setTasksError('')
        const res = await fetch(API_TASKS, {
          headers: { Accept: 'application/json' },
          credentials: 'same-origin',
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        if (!cancelled) {
          setTasks(Array.isArray(data) ? data : [])
        }
      } catch (err) {
        console.error('[SidebarStudent] failed to load tasks', err)
        if (!cancelled) {
          setTasks([])
          setTasksError(LABELS.loadError)
        }
      } finally {
        if (!cancelled) setTasksLoading(false)
      }
    }
    fetchTasks()
    return () => {
      cancelled = true
    }
  }, [])

  useEffect(() => {
    let cancelled = false
    async function fetchReviews() {
      try {
        setReviewsLoading(true)
        setReviewsError('')
        const res = await fetch(API_REVIEWS, {
          headers: { Accept: 'application/json' },
          credentials: 'same-origin',
        })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const data = await res.json()
        if (!cancelled) {
          setReviewLectureIds(Array.isArray(data) ? data : [])
        }
      } catch (err) {
        console.error('[SidebarStudent] failed to load reviews', err)
        if (!cancelled) {
          setReviewLectureIds([])
          setReviewsError(LABELS.loadError)
        }
      } finally {
        if (!cancelled) setReviewsLoading(false)
      }
    }
    fetchReviews()
    return () => {
      cancelled = true
    }
  }, [])

  const ongoingLectures = useMemo(
    () => lectures.filter((lecture) => lecture && lecture.ended === false),
    [lectures]
  )

  const reviewLectures = useMemo(() => {
    if (reviewLectureIds.length === 0) return []
    return reviewLectureIds
      .map(lectureId => lectures.find(lecture => lecture.lectureId === lectureId))
      .filter(Boolean)
  }, [reviewLectureIds, lectures])

  const taskCountLabel = tasksLoading ? '...' : tasks.length
  const reviewCountLabel = reviewsLoading ? '...' : reviewLectureIds.length

  return (
    <nav className="list-group list-group-flush rounded-0">
      <button
        type="button"
        className={`list-group-item list-group-item-action d-flex justify-content-between align-items-center fw-semibold ${
          lecturesOpen ? 'bg-primary-subtle text-primary-emphasis' : 'bg-body-tertiary'
        }`}
        onClick={() => setLecturesOpen((prev) => !prev)}
      >
        {/* 📌 아이콘 추가 */}
        <span className="d-flex align-items-center">
          <BsBookFill className="me-2 fs-5" /> {/* 아이콘 */}
          {LABELS.ongoingLectures}
        </span>
        {/* <span className="small text-muted">{lecturesOpen ? LABELS.toggleClose : LABELS.toggleOpen}</span> */}
      </button>
      {lecturesOpen && (
        <div className="list-group list-group-flush bg-primary-subtle border-top border-primary-subtle">
          {lecturesLoading && (
            <div className="list-group-item small text-muted bg-transparent border-0">{LABELS.loading}</div>
          )}
          {!lecturesLoading && lecturesError && (
            <div className="list-group-item small text-danger bg-transparent border-0">{lecturesError}</div>
          )}
          {!lecturesLoading && !lecturesError && ongoingLectures.length === 0 && (
            <div className="list-group-item small text-muted bg-transparent border-0">{LABELS.ongoingEmpty}</div>
          )}
          {!lecturesLoading &&
            !lecturesError &&
            ongoingLectures.map((lecture) => (
              <Link
                key={lecture.lectureId}
                to={`/classroom/student/${encodeURIComponent(lecture.lectureId || '')}`}
                className="list-group-item list-group-item-action ps-4 bg-transparent border-0"
              >
                <div className="fw-semibold text-truncate text-primary-emphasis">
                  {lecture.subjectName || '-'}
                </div>
                <div className="text-muted small text-truncate">{lecture.completionName || ''}</div>
              </Link>
            ))}
        </div>
      )}

      <button
        type="button"
        className={`list-group-item list-group-item-action d-flex justify-content-between align-items-center fw-semibold ${
          tasksOpen ? 'bg-primary-subtle text-primary-emphasis' : 'bg-body-tertiary'
        }`}
        onClick={() => setTasksOpen((prev) => !prev)}
      >
        {/* 📌 아이콘 추가 */}
        <span className="d-flex align-items-center">
          <BsClipboard2Check className="me-2 fs-5" /> {/* 아이콘 */}
          {LABELS.tasks}
          <span className="ms-2 badge text-bg-secondary">{taskCountLabel}</span>
        </span>
        {/* <span className="small text-muted">{tasksOpen ? LABELS.toggleClose : LABELS.toggleOpen}</span> */}
      </button>
      {tasksOpen && (
        <div
          className="list-group list-group-flush bg-primary-subtle border-top border-primary-subtle"
        >
          {tasksLoading && (
            <div className="list-group-item small text-muted bg-transparent border-0">{LABELS.loading}</div>
          )}
          {!tasksLoading && tasksError && (
            <div className="list-group-item small text-danger bg-transparent border-0">{tasksError}</div>
          )}
          {!tasksLoading && !tasksError && tasks.length === 0 && (
            <div className="list-group-item small text-muted bg-transparent border-0">{LABELS.tasksEmpty}</div>
          )}
          {!tasksLoading &&
            !tasksError &&
            tasks.map((task) => (
              <Link
                key={task.indivtaskId}
                to={`/classroom/student/${encodeURIComponent(task.lectureId || '')}/task/indiv/${encodeURIComponent(task.indivtaskId || '')}`}
                className="list-group-item list-group-item-action ps-4 bg-transparent border-0"
              >
                <div
                  className="fw-semibold text-truncate text-primary-emphasis"
                >
                  {task.indivtaskName || '-'}
                </div>
                <div className="text-muted small text-truncate">
                  {(task.subjectName || '') + (task.subjectName && task.endAt ? ' · ' : '') + (formatDateTime(task.endAt) || '')}
                </div>
              </Link>
            ))}
        </div>
      )}

      {reviewLectureIds.length > 0 && (
        <>
          <button
            type="button"
            className={`list-group-item list-group-item-action d-flex justify-content-between align-items-center fw-semibold ${
              reviewsOpen ? 'bg-primary-subtle text-primary-emphasis' : 'bg-body-tertiary'
            }`}
            onClick={() => setReviewsOpen((prev) => !prev)}
          >
            {/* 📌 아이콘 추가 */}
            <span className="d-flex align-items-center">
              <BsBellFill className="me-2 fs-5" /> {/* 아이콘 */}
              {LABELS.reviews}
              <span className="ms-2 badge text-bg-secondary">{reviewCountLabel}</span>
            </span>
            {/* <span className="small text-muted">{reviewsOpen ? LABELS.toggleClose : LABELS.toggleOpen}</span> */}
          </button>
          {reviewsOpen && (
            <div
              className="list-group list-group-flush bg-primary-subtle border-top border-primary-subtle"
            >
              {reviewsLoading && (
                <div className="list-group-item small text-muted bg-transparent border-0">{LABELS.loading}</div>
              )}
              {!reviewsLoading && reviewsError && (
                <div className="list-group-item small text-danger bg-transparent border-0">{reviewsError}</div>
              )}
              {!reviewsLoading && !reviewsError && reviewLectures.length === 0 && (
                <div className="list-group-item small text-muted bg-transparent border-0">{LABELS.reviewsEmpty}</div>
              )}
              {!reviewsLoading &&
                !reviewsError &&
                reviewLectures.map((lecture) => (
                  <Link
                    key={lecture.lectureId}
                    to={`/classroom/student/${encodeURIComponent(lecture.lectureId || '')}/review`}
                    className="list-group-item list-group-item-action ps-4 bg-transparent border-0"
                  >
                    <div
                      className="fw-semibold text-truncate text-primary-emphasis"
                    >
                      {lecture.subjectName || '-'}
                    </div>
                    <div className="text-muted small text-truncate">{lecture.completionName || ''}</div>
                  </Link>
                ))}
            </div>
          )}
        </>
      )}
    </nav>
  )
}