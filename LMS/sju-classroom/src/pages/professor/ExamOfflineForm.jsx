import React, { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import Swal from 'sweetalert2'
import { useLecture } from '../../context/LectureContext'
import { fetchLectureStudents } from '../../lib/api/student'
import '../../styles/customButtons.css'

const pad = (value) => String(value).padStart(2, '0')
const toLocalInputValue = (date) => {
  if (!(date instanceof Date) || Number.isNaN(date.getTime())) return ''
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}
const normalizeLocalDateTime = (value) => {
  if (!value) return ''
  return value.length === 16 ? `${value}:00` : value
}
const buildFullName = (student) => {
  const lastName = student?.lastName ?? ''
  const firstName = student?.firstName ?? ''
  const composed = `${lastName} ${firstName}`.trim()
  return composed || student?.name || student?.userName || student?.userId || '(이름 정보 없음)'
}
const pickStudentMeta = (student) => {
  const meta = []
  if (student?.univDeptName) meta.push(student.univDeptName)
  if (student?.gradeName) meta.push(student.gradeName)
  return meta.join(' · ')
}

export default function ExamOfflineForm() {
  const { lectureId } = useParams()
  const { students: lectureStudents, setStudents } = useLecture()
  const navigate = useNavigate()

  const [examName, setExamName] = useState('')
  const [examDesc, setExamDesc] = useState('')
  const [startAt, setStartAt] = useState('')
  const [endAt, setEndAt] = useState('')
  const [weightValue, setWeightValue] = useState('')
  const [scores, setScores] = useState({})
  const [invalidScoreKeys, setInvalidScoreKeys] = useState([])
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [studentsLoading, setStudentsLoading] = useState(true)
  const [studentsError, setStudentsError] = useState('')
  const [studentsData, setStudentsData] = useState([])

  useEffect(() => {
    const now = new Date()
    now.setSeconds(0, 0)
    setStartAt(toLocalInputValue(now))
    const plusOneHour = new Date(now.getTime() + 60 * 60 * 1000)
    setEndAt(toLocalInputValue(plusOneHour))
  }, [])

  useEffect(() => {
    if (Array.isArray(lectureStudents)) {
      setStudentsData(lectureStudents)
      setStudentsLoading(false)
      setStudentsError('')
      return
    }
    if (lectureStudents === null && lectureId) {
      let alive = true
      setStudentsLoading(true)
      setStudentsError('')
      ;(async () => {
        try {
          const fetched = await fetchLectureStudents(lectureId)
          if (!alive) return
          setStudentsData(fetched)
          setStudents(fetched)
          setStudentsError('')
        } catch (err) {
          if (!alive) return
          setStudentsData([])
          setStudentsError(err instanceof Error ? err.message : '학생 목록을 불러오지 못했습니다.')
        } finally {
          if (alive) setStudentsLoading(false)
        }
      })()
      return () => { alive = false }
    }
    if (!lectureId) setStudentsLoading(false)
  }, [lectureId, lectureStudents, setStudents])

  useEffect(() => {
    setScores((prev) => {
      const next = {}
      studentsData.forEach((student, index) => {
        const key = String(student?.enrollId ?? student?.enrollID ?? index)
        next[key] = Object.prototype.hasOwnProperty.call(prev, key) ? prev[key] : ''
      })
      return next
    })
    setInvalidScoreKeys([])
  }, [studentsData])

  const studentCount = useMemo(() => studentsData.length, [studentsData])
  const randomDisabled = submitting || studentsLoading || studentCount === 0

  const performReset = (clearMessages = true) => {
    setExamName('')
    setExamDesc('')
    setWeightValue('')
    const now = new Date()
    now.setSeconds(0, 0)
    setStartAt(toLocalInputValue(now))
    const plusOneHour = new Date(now.getTime() + 60 * 60 * 1000)
    setEndAt(toLocalInputValue(plusOneHour))
    setScores(() => {
      const next = {}
      studentsData.forEach((student, index) => {
        const key = String(student?.enrollId ?? student?.enrollID ?? index)
        next[key] = ''
      })
      return next
    })
    setInvalidScoreKeys([])
    if (clearMessages) {
      setError('')
    }
  }

  const handleReset = (event) => {
    event?.preventDefault?.()
    performReset(true)
  }

  const handleRandomFill = () => {
    if (randomDisabled) return
    setScores((prev) => {
      const next = { ...prev }
      studentsData.forEach((student, index) => {
        const key = String(student?.enrollId ?? student?.enrollID ?? index)
        next[key] = String(Math.floor(Math.random() * 101))
      })
      return next
    })
    setInvalidScoreKeys([])
    setError('')
  }

  const handleScoreChange = (key, value) => {
    setScores((prev) => ({ ...prev, [key]: value }))
    setInvalidScoreKeys((prev) => (prev.includes(key) ? prev.filter((k) => k !== key) : prev))
  }

  const clearMessages = () => {
    setError('')
  }

  const buildPayload = () => {
    const name = examName.trim()
    if (!name) throw new Error('시험명을 입력해주세요.')

    const desc = examDesc.trim()
    if (!desc) throw new Error('시험 안내 내용을 입력해주세요.')

    const normalizedStart = normalizeLocalDateTime(startAt)
    const normalizedEnd = normalizeLocalDateTime(endAt)
    if (!normalizedStart) throw new Error('시험 시작 일시를 입력해주세요.')
    if (!normalizedEnd) throw new Error('시험 종료 일시를 입력해주세요.')

    const startDate = new Date(normalizedStart)
    const endDate = new Date(normalizedEnd)
    if (Number.isNaN(startDate.getTime()) || Number.isNaN(endDate.getTime())) {
      throw new Error('시험 일시 형식이 올바르지 않습니다.')
    }
    if (startDate > endDate) throw new Error('시험 종료 일시는 시작 일시 이후여야 합니다.')

    let weight = null
    if (weightValue.trim().length > 0) {
      const parsed = Number.parseInt(weightValue, 10)
      if (!Number.isInteger(parsed) || parsed < 0 || parsed > 100) {
        throw new Error('반영 비율은 0 이상 100 이하의 정수로 입력해주세요.')
      }
      weight = parsed
    }

    if (studentsData.length === 0) {
      throw new Error('등록 가능한 학생 정보가 없습니다.')
    }

    const nextInvalidKeys = []
    const eachResultList = studentsData.map((student, index) => {
      const enrollId = student?.enrollId ?? student?.enrollID
      if (enrollId === undefined || enrollId === null) {
        throw new Error('학생 정보(enrollId)가 누락되어 있어 저장할 수 없습니다.')
      }
      const key = String(enrollId ?? index)
      const rawScore = (scores[key] ?? '').trim()
      if (!rawScore) {
        nextInvalidKeys.push(key)
        throw new Error('모든 학생의 점수를 입력해주세요.')
      }
      const parsed = Number.parseInt(rawScore, 10)
      if (!Number.isInteger(parsed) || parsed < 0 || parsed > 100) {
        nextInvalidKeys.push(key)
        throw new Error('점수는 0 이상 100 이하의 정수로 입력해주세요.')
      }
      return { enrollId, score: parsed }
    })
    setInvalidScoreKeys(nextInvalidKeys)

    const payload = {
      examName: name,
      examDesc: desc,
      startAt: normalizedStart,
      endAt: normalizedEnd,
      eachResultList,
    }
    if (weight !== null) payload.weightValue = weight
    return payload
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    clearMessages()
    setInvalidScoreKeys([])

    if (!lectureId) {
      setError('강의 ID가 유효하지 않습니다.')
      return
    }

    let payload
    try {
      payload = buildPayload()
    } catch (err) {
      const message = err instanceof Error ? err.message : '입력값을 확인해주세요.'
      setError(message)
      return
    }

    const submitUrl = `/classroom/api/v1/professor/exam/${encodeURIComponent(lectureId)}/offline`
    try {
      setSubmitting(true)
      const response = await fetch(submitUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload),
      })
      if (!response.ok) {
        const message = await response.text().catch(() => '')
        throw new Error(message || `(${response.status}) 등록에 실패했습니다.`)
      }

      await Swal.fire({
        title: '등록 완료',
        text: '시험이 등록되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      })

      const locationHeader = response.headers.get('location')
      if (locationHeader) {
        navigate(locationHeader)
      } else {
        navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/exam`)
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : '등록 처리 중 오류가 발생했습니다.'
      setError(message)
    } finally {
      setSubmitting(false)
    }
  }

  const studentsPlaceholder = useMemo(() => {
    if (studentsLoading) {
      return (
        <div className="text-center text-muted py-4">
          <div className="spinner-border" role="status"><span className="visually-hidden">Loading...</span></div>
          <p className="mt-2 mb-0">학생 목록을 불러오는 중입니다...</p>
        </div>
      )
    }
    if (studentsError) {
      return <div className="alert alert-danger mb-0" role="alert">{studentsError}</div>
    }
    if (studentCount === 0) {
      return <div className="text-center text-muted py-4">등록된 수강 학생이 없습니다.</div>
    }
    return null
  }, [studentCount, studentsError, studentsLoading])

  const handleDemoFill = () => {
    setExamName('2차 중간평가')
    setWeightValue('20')
    setExamDesc('웹 프로그래밍 2차 중간평가입니다.\n\n시험 범위: React Hooks, Context API, 상태관리\n지참물: 없음 (온라인 시험)\n배점: 100점 만점')
    const start = new Date('2025-11-04T15:00:00')
    const end = new Date('2025-11-04T17:00:00')
    setStartAt(toLocalInputValue(start))
    setEndAt(toLocalInputValue(end))
    setError('')
  }

  return (
    <section id="exam-offline-root" className="container py-4">
      {error && <div id="alert-box" className="alert alert-danger" role="alert">{error}</div>}

      <form id="exam-offline-form" className="vstack gap-4" onSubmit={handleSubmit} onReset={handleReset}>
        <div className="card shadow-sm p-0">
          <div className="card-header d-flex justify-content-between align-items-center">
            <strong>시험 정보</strong>
            <button type="button" className="btn btn-sm btn-outline-secondary" onClick={handleDemoFill} disabled={submitting}>시연</button>
          </div>
          <div className="card-body p-3 vstack gap-3">
            <div className="row g-3">
              <div className="col-12 col-lg-3">
                <label htmlFor="exam-name" className="form-label d-block text-start">시험명 <span className="text-danger">*</span></label>
                <input id="exam-name" type="text" className="form-control" required maxLength={100} disabled={submitting} value={examName} onChange={(event) => setExamName(event.target.value)} placeholder="예: 2차 중간평가" />
              </div>
              <div className="col-12 col-lg-3">
                <label htmlFor="exam-weight" className="form-label d-block text-start">반영 비율(%) <span className="text-muted">(선택)</span></label>
                <input id="exam-weight" type="number" className="form-control" min={0} max={100} step={1} disabled={submitting} value={weightValue} onChange={(event) => setWeightValue(event.target.value)} placeholder="0~100" />
              </div>
              <div className="col-12 col-lg-3">
                <label htmlFor="exam-start" className="form-label d-block text-start">시험 시작 일시 <span className="text-danger">*</span></label>
                <input id="exam-start" type="datetime-local" className="form-control" required disabled={submitting} value={startAt} onChange={(event) => setStartAt(event.target.value)} />
              </div>
              <div className="col-12 col-lg-3">
                <label htmlFor="exam-end" className="form-label d-block text-start">시험 종료 일시 <span className="text-danger">*</span></label>
                <input id="exam-end" type="datetime-local" className="form-control" required disabled={submitting} value={endAt} onChange={(event) => setEndAt(event.target.value)} />
              </div>
            </div>
            <div>
              <label htmlFor="exam-desc" className="form-label d-block text-start">시험 설명 <span className="text-danger">*</span></label>
              <textarea id="exam-desc" className="form-control" rows={5} required disabled={submitting} value={examDesc} onChange={(event) => setExamDesc(event.target.value)} placeholder="시험 범위, 지참물, 배점 등 안내를 입력해주세요." />
            </div>
          </div>
        </div>

        <div className="card shadow-sm p-0">
          <div className="card-header d-flex align-items-center justify-content-between flex-wrap gap-2">
            <div>
              <strong>학생별 점수 입력</strong>
              <small className="text-muted ms-2">*점수는 0~100점 사이로 입력하세요</small>
            </div>
            <button type="button" className="btn btn-sm btn-outline-secondary" onClick={handleRandomFill} disabled={randomDisabled}>무작위 점수 채우기</button>
          </div>
          <div className="card-body p-3">
            {studentsPlaceholder || (
              <div className="row row-cols-1 row-cols-md-3 row-cols-lg-5 g-3">
                {studentsData.map((student, index) => {
                  const key = String(student?.enrollId ?? student?.enrollID ?? index)
                  const isInvalid = invalidScoreKeys.includes(key)
                  const meta = pickStudentMeta(student)
                  return (
                    <div className="col" key={key}>
                      <div className="card h-100 shadow-sm">
                        <div className="card-body p-2">
                          <div className="fw-semibold">#{index + 1} {buildFullName(student)}</div>
                          <div className="text-muted small">({student?.studentNo || '학번 없음'})</div>
                          {meta && <div className="text-muted small">{meta}</div>}
                          <div className="mt-2 d-flex align-items-center gap-2">
                            <label htmlFor={`score-${key}`} className="form-label mb-0 small fw-bold">점수:</label>
                            <input
                              id={`score-${key}`}
                              type="number"
                              min={0}
                              max={100}
                              className={`form-control form-control-sm${isInvalid ? ' is-invalid' : ''}`}
                              style={{ width: '80px', padding: '6px' }}
                              disabled={submitting}
                              value={scores[key] ?? ''}
                              onChange={(event) => handleScoreChange(key, event.target.value)}
                            />
                          </div>
                          {isInvalid && <div className="invalid-feedback d-block small mt-1">0-100 사이 정수</div>}
                        </div>
                      </div>
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        </div>

        <div className="d-flex justify-content-end gap-2">
          <button type="reset" className="custom-btn custom-btn-outline-secondary custom-btn-md" disabled={submitting}>초기화</button>
          <button type="submit" className="custom-btn custom-btn-primary custom-btn-md" disabled={submitting || studentCount === 0}>
            {submitting ? '등록 중...' : '시험 등록'}
          </button>
        </div>
      </form>
    </section>
  )
}
