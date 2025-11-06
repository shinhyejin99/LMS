import React, { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useLecture } from '../../context/LectureContext'
import NotFound from '../../components/NotFound'

const pad = (value) => String(value).padStart(2, '0')
const formatDateTime = (iso) => {
  if (!iso) return '-'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return '-'
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}
const formatDateTimeMultiLine = (iso) => {
  if (!iso) return '-'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return '-'
  const date = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
  const time = `${pad(d.getHours())}:${pad(d.getMinutes())}`
  return { date, time }
}
const formatPeriod = (start, end) => {
  const startLabel = formatDateTime(start)
  const endLabel = formatDateTime(end)
  if (startLabel === '-' && endLabel === '-') return '-'
  if (endLabel === '-') return startLabel
  return `${startLabel} ~ ${endLabel}`
}
const toExamTypeLabel = (examType) => {
  if (examType === 'OFF') return '오프라인'
  if (examType === 'ON') return '온라인'
  return examType || '구분 없음'
}
const toFullName = (student) => {
  if (!student) return '(정보 없음)'
  const lastName = student.lastName || ''
  const firstName = student.firstName || ''
  const fullName = `${lastName} ${firstName}`.trim()
  if (fullName) return fullName
  return student.name || student.userName || student.userId || '(정보 없음)'
}

export default function ExamDetail() {
  const { lectureId, examId } = useParams()
  const { students } = useLecture()
  const navigate = useNavigate()

  const [exam, setExam] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [notFound, setNotFound] = useState(false)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    if (!lectureId || !examId) {
      setError(new Error('강의 ID 또는 시험 ID가 유효하지 않습니다.'))
      setLoading(false)
      return
    }
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        setNotFound(false)
        const url = `/classroom/api/v1/professor/exam/${encodeURIComponent(lectureId)}/${encodeURIComponent(examId)}`
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (res.status === 404 || res.status === 500) {
          if (!alive) return
          setNotFound(true)
          return
        }
        if (!res.ok) throw new Error(`(${res.status}) 시험 정보를 불러오지 못했습니다.`)
        const data = await res.json()
        if (!alive) return
        setExam(data || null)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, examId])

  const studentMap = useMemo(() => {
    const map = new Map()
    if (Array.isArray(students)) {
      for (const st of students) {
        if (st?.enrollId !== undefined && st?.enrollId !== null) {
          map.set(String(st.enrollId), st)
        }
      }
    }
    return map
  }, [students])

  const submitList = useMemo(() => Array.isArray(exam?.submitList) ? exam.submitList : [], [exam])
  const studentsLoading = students === null
  const submissionStats = useMemo(() => {
    let submitted = 0
    for (const entry of submitList) {
      if (entry?.submit?.submitAt) submitted += 1
    }
    return {
      total: submitList.length || (Array.isArray(students) ? students.length : 0),
      submitted,
    }
  }, [submitList, students])
  const tableLoading = loading || studentsLoading

  const backToListUrl = `/classroom/professor/${encodeURIComponent(lectureId || '')}/exam`

  const handleDelete = async () => {
    if (!confirm('정말로 이 시험을 삭제하시겠습니까?')) return

    try {
      setDeleting(true)
      const url = `/classroom/api/v1/professor/exam/${encodeURIComponent(lectureId)}/${encodeURIComponent(examId)}`
      const res = await fetch(url, {
        method: 'DELETE',
        credentials: 'include'
      })

      if (!res.ok) {
        const text = await res.text().catch(() => '')
        throw new Error(text || `(${res.status}) 시험 삭제에 실패했습니다.`)
      }

      alert('시험이 삭제되었습니다.')
      navigate(backToListUrl)
    } catch (err) {
      alert(err instanceof Error ? err.message : '시험 삭제 중 오류가 발생했습니다.')
    } finally {
      setDeleting(false)
    }
  }

  const renderExamInfo = () => {
    if (loading) {
      return (
        <div className="text-center text-muted py-4">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p className="mt-2 mb-0">시험 정보를 불러오는 중입니다...</p>
        </div>
      )
    }
    if (!exam) {
      return <div className="text-center py-4">표시할 시험 정보가 없습니다.</div>
    }
    return (
      <dl className="row mb-0">
        <dd className="col-sm-12 fs-3 fw-bold">{exam.examName || '-'}</dd>
        <dt className="col-sm-2">출제일</dt>
        <dd className="col-sm-10">{formatDateTime(exam.createAt || exam.createdAt)}</dd>

        <dt className="col-sm-2">구분</dt>
        <dd className="col-sm-10">{toExamTypeLabel(exam.examType)}</dd>

        <dt className="col-sm-2">시험 설명</dt>
        <dd className="col-sm-10">{exam.examDesc || exam.notice || '-'}</dd>

        <dt className="col-sm-2">진행 기간</dt>
        <dd className="col-sm-10">{formatPeriod(exam.startAt, exam.endAt)}</dd>

        <dt className="col-sm-2">반영 비율</dt>
        <dd className="col-sm-10">{typeof exam.weightValue === 'number' ? exam.weightValue : '-'}%</dd>
      </dl>
    )
  }

  if (notFound) {
    return <NotFound message="시험을 찾을 수 없습니다" resourceType="시험" />
  }

  return (
    <section id="exam-detail-root" className="container py-0" data-lecture-id={lectureId} data-exam-id={examId}>
      <div className="d-flex align-items-center justify-content-end mb-3 gap-2">
        <Link to={`/classroom/professor/${encodeURIComponent(lectureId || '')}/exam/${encodeURIComponent(examId || '')}/edit`} className="btn btn-sm btn-outline-primary">수정</Link>
        <button onClick={handleDelete} disabled={deleting} className="btn btn-sm btn-outline-danger">{deleting ? '삭제 중...' : '삭제'}</button>
        <Link to={backToListUrl} className="btn btn-sm btn-outline-secondary">목록으로</Link>
      </div>

      {error ? (
        <div className="alert alert-danger" role="alert">
          {error?.message || '시험 정보를 불러오는 중 오류가 발생했습니다.'}
        </div>
      ) : null}

      <div className="row">
        <div className="mb-4" style={{ width: '60%' }}>
          <div className="card shadow p-0">
            <div className="card-header">
              <h2 className="h5 mb-0">시험 정보</h2>
            </div>
            <div className="card-body p-3 overflow-auto" id="exam-info-body">
              {renderExamInfo()}
            </div>
          </div>
        </div>

        <div className="mb-4" style={{ width: '40%' }}>
          <div className="card shadow p-0" style={{ height: '580px' }}>
            <div className="card-header d-flex justify-content-between align-items-center">
              <h2 className="h5 mb-0">학생별 응시 기록</h2>
              <small className="text-muted">
                응시: {submissionStats.submitted} / {submissionStats.total}
              </small>
            </div>
            <div className="card-body p-0 overflow-auto" style={{ height: 'calc(580px - 56px)' }}>
              {tableLoading ? (
                <div className="text-center text-muted py-4">
                  <div className="spinner-border" role="status">
                    <span className="visually-hidden">Loading...</span>
                  </div>
                  <p className="mt-2 mb-0">응시 기록을 불러오는 중입니다...</p>
                </div>
              ) : submitList.length === 0 ? (
                <div className="text-center text-muted py-4">표시할 응시 기록이 없습니다.</div>
              ) : (
                <div className="table-responsive">
                  <table className="table table-hover table-sm align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        <th scope="col" className="text-center" style={{ width: 120 }}>학번</th>
                        <th scope="col" className="text-center">이름</th>
                        <th scope="col" className="text-center" style={{ width: 80 }}>점수</th>
                        <th scope="col" className="text-center" style={{ width: 140 }}>제출일시</th>
                      </tr>
                    </thead>
                    <tbody id="student-results-body">
                      {submitList.map((entry, index) => {
                        const student = studentMap.get(String(entry?.enrollId))
                        const rawScore = entry?.submit?.modifiedScore ?? entry?.submit?.autoScore ?? entry?.submit?.score
                        const scoreLabel = rawScore === undefined || rawScore === null || rawScore === '' ? '-' : rawScore
                        const dateTime = entry?.submit?.submitAt ? formatDateTimeMultiLine(entry.submit.submitAt) : null
                        return (
                          <tr key={entry?.enrollId ?? entry?.id ?? index}>
                            <td className="text-center">{student?.studentNo || '-'}</td>
                            <td className="text-center">{toFullName(student)}</td>
                            <td className="text-center">{scoreLabel}</td>
                            <td className="text-center">
                              {dateTime ? (
                                <>
                                  {dateTime.date}
                                  <br />
                                  {dateTime.time}
                                </>
                              ) : (
                                '미응시'
                              )}
                            </td>
                          </tr>
                        )
                      })}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
