import React, { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useLecture } from '../../context/LectureContext'

const pad = (value) => String(value).padStart(2, '0')
const formatDateTime = (iso, includeSeconds = false) => {
  if (!iso) return '-'
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return '-'
  const datePart = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
  const timePart = `${pad(date.getHours())}:${pad(date.getMinutes())}${includeSeconds ? `:${pad(date.getSeconds())}` : ''}`
  return `${datePart} ${timePart}`
}
const formatDate = (iso) => {
  if (!iso) return '-'
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return '-'
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}
const formatExamType = (type) => {
  const value = (type || '').toUpperCase()
  if (value === 'OFF') return '오프라인'
  if (value === 'ON' || value === 'ONL') return '온라인'
  return '기타'
}
const toNumber = (value) => {
  if (value === null || value === undefined || value === '') return null
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : null
}
const resolveFinalScore = (record) => {
  const modified = toNumber(record?.modifiedScore)
  if (modified != null) return { score: modified, type: 'modified' }
  const auto = toNumber(record?.autoScore)
  if (auto != null) return { score: auto, type: 'auto' }
  return { score: null, type: null }
}
const statusBadgeClass = (code) => {
  switch (code) {
    case 'ATTD_OK':
      return 'badge bg-success-subtle text-success-emphasis'
    case 'ATTD_LATE':
      return 'badge bg-warning-subtle text-warning-emphasis'
    case 'ATTD_ABSENT':
      return 'badge bg-danger-subtle text-danger-emphasis'
    default:
      return 'badge bg-secondary-subtle text-secondary-emphasis'
  }
}
const booleanBadge = (flag, truthyLabel, falsyLabel) => (
  <span className={flag ? 'badge bg-success-subtle text-success-emphasis' : 'badge bg-secondary-subtle text-secondary-emphasis'}>
    {flag ? truthyLabel : falsyLabel}
  </span>
)

export default function StudentManageDetail() {
  const { lectureId, studentNo } = useParams()
  const { students, setStudents, getStudentPhotoUrl } = useLecture()

  const [student, setStudent] = useState(null)
  const [studentError, setStudentError] = useState(null)
  const [studentLoading, setStudentLoading] = useState(!Array.isArray(students))

  const [examState, setExamState] = useState({ loading: true, records: [], error: null })
  const [attendanceState, setAttendanceState] = useState({ loading: true, records: [], error: null })
  const [indivTaskState, setIndivTaskState] = useState({ loading: true, records: [], error: null })
  const [groupTaskState, setGroupTaskState] = useState({ loading: true, records: [], error: null })
  const [notice, setNotice] = useState(null)

  useEffect(() => {
    if (!lectureId || !studentNo) return

    if (Array.isArray(students)) {
      const found = students.find((s) => String(s.studentNo || s.userId || '') === String(studentNo))
      setStudent(found || null)
      setStudentLoading(false)
      if (!found) {
        setStudentError('수강생 정보를 찾을 수 없습니다.')
      } else {
        setStudentError(null)
      }
      return
    }

    let alive = true
    setStudentLoading(true)
    setStudentError(null)
    ;(async () => {
      try {
        const url = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/students`
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`(${res.status}) 수강생 정보를 불러오는 데 실패했습니다.`)
        const data = await res.json()
        if (!alive) return
        const arr = Array.isArray(data) ? data : []
        setStudents(arr)
        const found = arr.find((s) => String(s.studentNo || s.userId || '') === String(studentNo))
        setStudent(found || null)
        setStudentError(found ? null : '수강생 정보를 찾을 수 없습니다.')
      } catch (error) {
        if (!alive) return
        setStudent(null)
        setStudentError(error instanceof Error ? error.message : '수강생 정보를 불러오지 못했습니다.')
      } finally {
        if (alive) setStudentLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, studentNo, students, setStudents])

  useEffect(() => {
    if (!lectureId || !studentNo) return
    let alive = true
    setExamState((prev) => ({ ...prev, loading: true, error: null }))
    const url = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/${encodeURIComponent(studentNo)}/exam`
    ;(async () => {
      try {
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`(${res.status}) 시험 데이터를 불러오는 데 실패했습니다.`)
        const data = await res.json()
        if (!alive) return
        const list = Array.isArray(data) ? data : []
        list.sort((a, b) => {
          const aTime = new Date(a?.startAt || 0).getTime()
          const bTime = new Date(b?.startAt || 0).getTime()
          return bTime - aTime
        })
        setExamState({ loading: false, records: list, error: null })
      } catch (error) {
        if (!alive) return
        setExamState({ loading: false, records: [], error: error instanceof Error ? error : new Error('시험 데이터를 불러오는 중 오류가 발생했습니다.') })
      }
    })()
    return () => { alive = false }
  }, [lectureId, studentNo])

  useEffect(() => {
    if (!lectureId || !studentNo) return
    let alive = true
    setAttendanceState((prev) => ({ ...prev, loading: true, error: null }))
    const url = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/${encodeURIComponent(studentNo)}/attendance`
    ;(async () => {
      try {
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`(${res.status}) 출석 데이터를 불러오는 데 실패했습니다.`)
        const data = await res.json()
        if (!alive) return
        const list = Array.isArray(data) ? data : []
        list.sort((a, b) => {
          const aRound = a?.lctRound ?? a?.lctPrintRound ?? 0
          const bRound = b?.lctRound ?? b?.lctPrintRound ?? 0
          return aRound - bRound
        })
        setAttendanceState({ loading: false, records: list, error: null })
      } catch (error) {
        if (!alive) return
        setAttendanceState({ loading: false, records: [], error: error instanceof Error ? error : new Error('출석 데이터를 불러오는 중 오류가 발생했습니다.') })
      }
    })()
    return () => { alive = false }
  }, [lectureId, studentNo])

  useEffect(() => {
    if (!lectureId || !studentNo) return
    let alive = true
    setIndivTaskState((prev) => ({ ...prev, loading: true, error: null }))
    const url = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/${encodeURIComponent(studentNo)}/indivtask`
    ;(async () => {
      try {
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`(${res.status}) 개인과제 데이터를 불러오는 데 실패했습니다.`)
        const data = await res.json()
        if (!alive) return
        const list = Array.isArray(data) ? data : []
        list.sort((a, b) => {
          const aTime = new Date(a?.startAt || 0).getTime()
          const bTime = new Date(b?.startAt || 0).getTime()
          return bTime - aTime
        })
        setIndivTaskState({ loading: false, records: list, error: null })
      } catch (error) {
        if (!alive) return
        setIndivTaskState({ loading: false, records: [], error: error instanceof Error ? error : new Error('개인과제 데이터를 불러오는 중 오류가 발생했습니다.') })
      }
    })()
    return () => { alive = false }
  }, [lectureId, studentNo])

  useEffect(() => {
    if (!lectureId || !studentNo) return
    let alive = true
    setGroupTaskState((prev) => ({ ...prev, loading: true, error: null }))
    const url = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/${encodeURIComponent(studentNo)}/grouptask`
    ;(async () => {
      try {
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`(${res.status}) 조별과제 데이터를 불러오는 데 실패했습니다.`)
        const data = await res.json()
        if (!alive) return
        const list = Array.isArray(data) ? data : []
        list.sort((a, b) => {
          const aTime = new Date(a?.startAt || 0).getTime()
          const bTime = new Date(b?.startAt || 0).getTime()
          return bTime - aTime
        })
        setGroupTaskState({ loading: false, records: list, error: null })
      } catch (error) {
        if (!alive) return
        setGroupTaskState({ loading: false, records: [], error: error instanceof Error ? error : new Error('조별과제 데이터를 불러오는 중 오류가 발생했습니다.') })
      }
    })()
    return () => { alive = false }
  }, [lectureId, studentNo])

  useEffect(() => {
    if (examState.error) {
      setNotice({ variant: 'alert-danger', message: '시험 정보를 불러오는 중 오류가 발생했습니다.' })
      return
    }
    if (attendanceState.error) {
      setNotice({ variant: 'alert-danger', message: '출석 데이터를 불러오는 중 오류가 발생했습니다.' })
      return
    }
    if (!examState.loading && !attendanceState.loading && examState.records.length === 0 && attendanceState.records.length === 0) {
      setNotice({ variant: 'alert-info', message: '해당 학생의 상세 데이터가 아직 등록되지 않았습니다.' })
    } else {
      setNotice(null)
    }
  }, [examState.loading, examState.error, examState.records.length, attendanceState.loading, attendanceState.error, attendanceState.records.length])

  const studentName = useMemo(() => {
    if (!student) return studentNo || ''
    const name = [student.lastName, student.firstName].filter(Boolean).join('')
    return name || student.userName || student.userId || studentNo || ''
  }, [student, studentNo])

  const studentMeta = useMemo(() => {
    if (!student) return ''
    const dept = student.univDeptName || ''
    const grade = student.gradeName || ''
    return [dept, grade].filter(Boolean).join(' · ')
  }, [student])

  const studentEmail = student?.email || ''
  const studentStatus = student?.enrollStatusName || ''
  const photoUrl = student ? getStudentPhotoUrl(student.studentNo || student.userId || '', lectureId) : null

  const examSummary = useMemo(() => {
    const count = examState.records.length
    const weightSum = examState.records.reduce((acc, record) => acc + (toNumber(record?.weightValue) ?? 0), 0)
    if (!count) {
      return {
        count,
        weightSum,
        finalClass: 'alert alert-warning',
        finalText: '등록된 시험 기록이 없어 최종 점수를 계산할 수 없습니다.',
      }
    }
    let weightedScore = 0
    examState.records.forEach((record) => {
      const weight = toNumber(record?.weightValue) ?? 0
      const final = resolveFinalScore(record)
      const score = final.score != null && record?.record ? final.score : 0
      weightedScore += score * (weight / 100)
    })
    if (Math.abs(weightSum - 100) < 1e-6) {
      const displayScore = Number.isFinite(weightedScore) ? weightedScore.toFixed(2) : '0.00'
      return {
        count,
        weightSum,
        finalClass: 'alert alert-primary',
        finalText: `반영 비율 합계 100% 기준 최종 점수는 ${displayScore}점입니다.`,
      }
    }
    return {
      count,
      weightSum,
      finalClass: 'alert alert-warning',
      finalText: `현재 시험 반영 비율 합계가 ${weightSum}이어서 최종 점수를 계산할 수 없습니다.`,
    }
  }, [examState.records])

  const attendanceHeaders = useMemo(() => attendanceState.records.map((record) => {
    const round = record?.lctPrintRound ?? record?.lctRound
    const roundLabel = round != null ? `${round}회` : '-'
    const methodLabel = record?.qrcodeFileId ? 'QR' : '일반'
    return { roundLabel, methodLabel }
  }), [attendanceState.records])

  const attendanceRows = useMemo(() => {
    if (!attendanceState.records.length) return []
    return [
      {
        label: '출석일',
        cells: attendanceState.records.map((record, idx) => <td key={`date-${idx}`} className="text-center">{formatDate(record?.attDay)}</td>),
      },
      {
        label: '상태',
        cells: attendanceState.records.map((record, idx) => (
          <td key={`status-${idx}`} className="text-center">
            <span className={statusBadgeClass(record?.attStatusCd)}>{record?.attStatusName || '미정'}</span>
          </td>
        )),
      },
      {
        label: '체크 시간',
        cells: attendanceState.records.map((record, idx) => (
          <td key={`time-${idx}`} className="text-center">
            {record?.attAt ? formatDateTime(record.attAt, true) : '-'}
          </td>
        )),
      },
      {
        label: '비고',
        cells: attendanceState.records.map((record, idx) => (
          <td key={`note-${idx}`} className="text-center text-muted small">
            {record?.attComment || '-'}
          </td>
        )),
      },
    ]
  }, [attendanceState.records])

  const attendanceSummary = useMemo(() => {
    const totalLectures = attendanceState.records.length
    let attended = 0
    let tardy = 0
    let earlyLeave = 0
    let absent = 0
    let excused = 0

    attendanceState.records.forEach(record => {
      if (record.attStatusCd === 'ATTD_OK') {
        attended++
      } else if (record.attStatusCd === 'ATTD_LATE') {
        tardy++
      } else if (record.attStatusCd === 'ATTD_EARLY') {
        earlyLeave++
      } else if (record.attStatusCd === 'ATTD_NO' || record.attStatusCd === 'ATTD_ABSENT') {
        absent++
      } else if (record.attStatusCd === 'ATTD_EXCUSE') {
        excused++
      }
    })

    const attendanceRate = totalLectures > 0 ? ((attended / totalLectures) * 100).toFixed(1) : 0
    const tardyRate = totalLectures > 0 ? ((tardy / totalLectures) * 100).toFixed(1) : 0
    const earlyLeaveRate = totalLectures > 0 ? ((earlyLeave / totalLectures) * 100).toFixed(1) : 0
    const absentRate = totalLectures > 0 ? ((absent / totalLectures) * 100).toFixed(1) : 0
    const excusedRate = totalLectures > 0 ? ((excused / totalLectures) * 100).toFixed(1) : 0

    return {
      totalLectures,
      attended,
      tardy,
      earlyLeave,
      absent,
      excused,
      attendanceRate,
      tardyRate,
      earlyLeaveRate,
      absentRate,
      excusedRate,
    }
  }, [attendanceState.records])

  const examTableBody = useMemo(() => {
    if (examState.loading) {
      return (
        <tr>
          <td colSpan={6} className="text-center text-muted py-4">시험 정보를 불러오는 중입니다...</td>
        </tr>
      )
    }
    if (examState.error) {
      return (
        <tr>
          <td colSpan={6} className="text-center text-danger py-4">{examState.error.message || '시험 정보를 불러오지 못했습니다.'}</td>
        </tr>
      )
    }
    if (!examState.records.length) {
      return (
        <tr>
          <td colSpan={6} className="text-center text-muted py-4">등록된 시험이 없습니다.</td>
        </tr>
      )
    }
    return examState.records.map((record, index) => {
      const startLabel = record?.startAt ? formatDateTime(record.startAt) : ''
      const endLabel = record?.endAt ? formatDateTime(record.endAt) : ''
      const metaPieces = [formatExamType(record?.examType)]
      const schedule = [startLabel, endLabel ? `~ ${endLabel}` : ''].filter(Boolean).join(' ')
      if (schedule) metaPieces.push(schedule)
      const final = resolveFinalScore(record)
      const suffix = final.type === 'modified' ? ' (수정)' : ''
      return (
        <tr key={record?.examId || record?.lctExamId || index}>
          <td>
            <div className="fw-semibold">{record?.examName || '-'}</div>
            <div className="text-muted small">{metaPieces.join(' · ')}</div>
          </td>
          <td className="text-center">{record?.weightValue != null ? record.weightValue : '-'}</td>
          <td className="text-center">{booleanBadge(record?.record, '반영', '미반영')}</td>
          <td className="text-center">{record?.submitAt ? formatDateTime(record.submitAt, true) : '-'}</td>
          <td className="text-center">{final.score != null ? `${final.score}${suffix}` : '-'}</td>
          <td className="text-muted small">{record?.modifyReason || ''}</td>
        </tr>
      )
    })
  }, [examState.loading, examState.error, examState.records])

  const indivTaskTableBody = useMemo(() => {
    if (indivTaskState.loading) {
      return (
        <tr>
          <td colSpan={6} className="text-center text-muted py-4">개인과제 정보를 불러오는 중입니다...</td>
        </tr>
      )
    }
    if (indivTaskState.error) {
      return (
        <tr>
          <td colSpan={6} className="text-center text-danger py-4">{indivTaskState.error.message || '개인과제 정보를 불러오지 못했습니다.'}</td>
        </tr>
      )
    }
    if (!indivTaskState.records.length) {
      return (
        <tr>
          <td colSpan={6} className="text-center text-muted py-4">등록된 개인과제가 없습니다.</td>
        </tr>
      )
    }
    return indivTaskState.records.map((record, index) => {
      const startLabel = record?.startAt ? formatDateTime(record.startAt) : ''
      const endLabel = record?.endAt ? formatDateTime(record.endAt) : ''
      const schedule = [startLabel, endLabel ? `~ ${endLabel}` : ''].filter(Boolean).join(' ')
      const isSubmitted = !!record?.submitAt
      const isEvaluated = !!record?.evaluAt

      return (
        <tr key={record?.indivtaskId || index}>
          <td>
            <div className="fw-semibold">{record?.indivtaskName || '-'}</div>
            <div className="text-muted small">{schedule}</div>
          </td>
          <td className="text-center">{booleanBadge(record?.submitTarget, '대상', '면제')}</td>
          <td className="text-center">
            {record?.submitTarget ? (
              isSubmitted ? (
                <span className="badge bg-success-subtle text-success-emphasis">제출 완료</span>
              ) : (
                <span className="badge bg-warning-subtle text-warning-emphasis">미제출</span>
              )
            ) : (
              '-'
            )}
          </td>
          <td className="text-center">{isSubmitted ? formatDateTime(record.submitAt, true) : '-'}</td>
          <td className="text-center">{isEvaluated && record?.evaluScore != null ? record.evaluScore : '-'}</td>
          <td className="text-muted small">{record?.evaluDesc || ''}</td>
        </tr>
      )
    })
  }, [indivTaskState.loading, indivTaskState.error, indivTaskState.records])

  const groupTaskTableBody = useMemo(() => {
    if (groupTaskState.loading) {
      return (
        <tr>
          <td colSpan={7} className="text-center text-muted py-4">조별과제 정보를 불러오는 중입니다...</td>
        </tr>
      )
    }
    if (groupTaskState.error) {
      return (
        <tr>
          <td colSpan={7} className="text-center text-danger py-4">{groupTaskState.error.message || '조별과제 정보를 불러오지 못했습니다.'}</td>
        </tr>
      )
    }
    if (!groupTaskState.records.length) {
      return (
        <tr>
          <td colSpan={7} className="text-center text-muted py-4">등록된 조별과제가 없습니다.</td>
        </tr>
      )
    }
    return groupTaskState.records.map((record, index) => {
      const startLabel = record?.startAt ? formatDateTime(record.startAt) : ''
      const endLabel = record?.endAt ? formatDateTime(record.endAt) : ''
      const schedule = [startLabel, endLabel ? `~ ${endLabel}` : ''].filter(Boolean).join(' ')
      const isSubmitted = !!record?.submitAt
      const isEvaluated = !!record?.evaluAt

      return (
        <tr key={record?.grouptaskId || index}>
          <td>
            <div className="fw-semibold">{record?.grouptaskName || '-'}</div>
            <div className="text-muted small">{schedule}</div>
          </td>
          <td className="text-center">{record?.groupName || '-'}</td>
          <td className="text-center">{booleanBadge(record?.submitTarget, '대상', '면제')}</td>
          <td className="text-center">
            {record?.submitTarget ? (
              isSubmitted ? (
                <span className="badge bg-success-subtle text-success-emphasis">제출 완료</span>
              ) : (
                <span className="badge bg-warning-subtle text-warning-emphasis">미제출</span>
              )
            ) : (
              '-'
            )}
          </td>
          <td className="text-center">{isSubmitted ? formatDateTime(record.submitAt, true) : '-'}</td>
          <td className="text-center">{isEvaluated && record?.evaluScore != null ? record.evaluScore : '-'}</td>
          <td className="text-muted small">{record?.evaluDesc || ''}</td>
        </tr>
      )
    })
  }, [groupTaskState.loading, groupTaskState.error, groupTaskState.records])

  return (
    <section id="student-detail-root" className="container py-4">
      {studentLoading && <div className="text-muted mb-3">학생 정보를 불러오는 중입니다...</div>}
      {studentError && !studentLoading && <div className="alert alert-warning mb-3" role="alert">{studentError}</div>}

      {notice && (
        <div id="student-detail-notice" className={`alert ${notice.variant}`} role="alert">
          {notice.message}
        </div>
      )}

      <div className="row mb-4">
        <div className="col-12 col-lg-3 mb-3 mb-lg-0">
          <div className="card" style={{ boxShadow: '0 0.375rem 0.75rem rgba(0, 0, 0, 0.45)' }}>
            <div className="card-body p-3">
              {photoUrl && (
                <div className="mb-3">
                  <img src={photoUrl} alt={`${studentName} 증명사진`} className="w-100" style={{ borderRadius: '0.5rem' }} />
                </div>
              )}
              <div className="text-center">
                <div className="fw-bold fs-5">{studentName} ({studentNo || '-'})</div>
                {studentMeta && <div className="text-muted">{studentMeta}</div>}
              </div>
            </div>
          </div>
        </div>

        <div className="col-12 col-lg-9">
          <div className="card" style={{ boxShadow: '0 0.375rem 0.75rem rgba(0, 0, 0, 0.45)' }} id="student-attendance-card">
        <div className="card-header d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-2">
          <h2 className="h5 mb-0">출석 현황</h2>
          <div className="text-muted small">출석 기록 <span id="student-attendance-count">{attendanceState.records.length}</span>개</div>
        </div>
        <div className="card-body">
          <div id="student-attendance-pivot" className="table-responsive">
            {attendanceState.loading ? (
              <div className="text-center text-muted py-5">출석 데이터를 불러오는 중입니다...</div>
            ) : attendanceState.error ? (
              <div className="text-center text-danger py-5">{attendanceState.error.message || '출석 데이터를 불러오지 못했습니다.'}</div>
            ) : !attendanceState.records.length ? (
              <div className="text-center text-muted py-5">등록된 출석 데이터가 없습니다.</div>
            ) : (
              <table className="table table-sm table-bordered align-middle mb-0">
                <thead>
                  <tr>
                    <th scope="col" className="bg-light text-center" style={{ minWidth: '80px' }}>구분</th>
                    {attendanceHeaders.map(({ roundLabel, methodLabel }, idx) => (
                      <th key={`att-head-${idx}`} scope="col" className="text-center">
                        <div className="fw-semibold">{roundLabel}</div>
                        <div className="text-muted small">{methodLabel}</div>
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {attendanceRows.map((row, rowIndex) => (
                    <tr key={`att-row-${rowIndex}`}>
                      <th scope="row" className="bg-light text-center" style={{ minWidth: '80px' }}>{row.label}</th>
                      {row.cells}
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
          <div className="text-muted small mt-2">
            * 출석 회차는 진행 순서대로 표시됩니다.
          </div>
          {attendanceState.records.length > 0 && (
            <div className="mt-4 pt-3 border-top">
              <h5 className="mb-3">출석률</h5>
              <div className="d-flex align-items-center">
                <p className="fs-3 fw-bold mb-0 me-3">{attendanceSummary.attendanceRate}%</p>
                <div className="progress flex-grow-1" style={{ height: '25px' }}>
                  <div
                    className="progress-bar bg-success"
                    role="progressbar"
                    style={{ width: `${attendanceSummary.attendanceRate}%` }}
                    aria-valuenow={attendanceSummary.attendanceRate}
                    aria-valuemin="0"
                    aria-valuemax="100"
                    title={`출석: ${attendanceSummary.attendanceRate}%`}
                  ></div>
                  <div
                    className="progress-bar bg-warning"
                    role="progressbar"
                    style={{ width: `${attendanceSummary.tardyRate}%` }}
                    aria-valuenow={attendanceSummary.tardyRate}
                    aria-valuemin="0"
                    aria-valuemax="100"
                    title={`지각: ${attendanceSummary.tardyRate}%`}
                  ></div>
                  <div
                    className="progress-bar"
                    role="progressbar"
                    style={{ width: `${attendanceSummary.earlyLeaveRate}%`, backgroundColor: '#fd7e14' }}
                    aria-valuenow={attendanceSummary.earlyLeaveRate}
                    aria-valuemin="0"
                    aria-valuemax="100"
                    title={`조퇴: ${attendanceSummary.earlyLeaveRate}%`}
                  ></div>
                  <div
                    className="progress-bar bg-info"
                    role="progressbar"
                    style={{ width: `${attendanceSummary.excusedRate}%` }}
                    aria-valuenow={attendanceSummary.excusedRate}
                    aria-valuemin="0"
                    aria-valuemax="100"
                    title={`공결: ${attendanceSummary.excusedRate}%`}
                  ></div>
                  <div
                    className="progress-bar bg-danger"
                    role="progressbar"
                    style={{ width: `${attendanceSummary.absentRate}%` }}
                    aria-valuenow={attendanceSummary.absentRate}
                    aria-valuemin="0"
                    aria-valuemax="100"
                    title={`결석: ${attendanceSummary.absentRate}%`}
                  ></div>
                </div>
              </div>
            </div>
          )}
        </div>
          </div>
        </div>
      </div>

      <div className="card mt-4" style={{ boxShadow: '0 0.375rem 0.75rem rgba(0, 0, 0, 0.45)' }} id="student-exam-card">
        <div className="card-header d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-2">
          <h2 className="h5 mb-0">시험 응시 현황</h2>
          <div className="text-muted small d-flex flex-column flex-sm-row align-items-sm-center gap-2">
            <span>응시 시험 <span id="student-exam-count">{examSummary.count}</span>건</span>
            <span id="student-exam-weight-summary">반영 비율 합계: {examSummary.weightSum}점</span>
          </div>
        </div>
        <div className="card-body">
          <div className={`${examSummary.finalClass} d-flex align-items-center justify-content-between`} role="status" id="student-exam-final-score">
            <span>{examSummary.finalText}</span>
          </div>
          <div className="table-responsive mt-3">
            <table className="table table-sm align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th scope="col">시험명</th>
                  <th scope="col" className="text-center" style={{ width: 80 }}>반영비</th>
                  <th scope="col" className="text-center" style={{ width: 90 }}>성적 반영</th>
                  <th scope="col" className="text-center" style={{ width: 180 }}>응시 시간</th>
                  <th scope="col" className="text-center" style={{ width: 120 }}>점수</th>
                  <th scope="col">비고</th>
                </tr>
              </thead>
              <tbody id="student-exam-tbody">
                {examTableBody}
              </tbody>
            </table>
          </div>
          <div className="text-muted small mt-2">
            * 수동 점수가 있을 경우 자동 점수 대신 수동 점수가 반영됩니다.
          </div>
        </div>
      </div>

      <div className="card mt-4" style={{ boxShadow: '0 0.375rem 0.75rem rgba(0, 0, 0, 0.45)' }} id="student-indivtask-card">
        <div className="card-header d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-2">
          <h2 className="h5 mb-0">개인과제 제출 현황</h2>
          <div className="text-muted small">
            개인과제 <span id="student-indivtask-count">{indivTaskState.records.length}</span>건
          </div>
        </div>
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-sm align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th scope="col">과제명</th>
                  <th scope="col" className="text-center" style={{ width: 90 }}>제출 대상</th>
                  <th scope="col" className="text-center" style={{ width: 100 }}>제출 상태</th>
                  <th scope="col" className="text-center" style={{ width: 180 }}>제출 시간</th>
                  <th scope="col" className="text-center" style={{ width: 80 }}>점수</th>
                  <th scope="col">평가 의견</th>
                </tr>
              </thead>
              <tbody id="student-indivtask-tbody">
                {indivTaskTableBody}
              </tbody>
            </table>
          </div>
          <div className="text-muted small mt-2">
            * 제출 대상이 아닌 경우 면제된 과제입니다.
          </div>
        </div>
      </div>

      <div className="card mt-4" style={{ boxShadow: '0 0.375rem 0.75rem rgba(0, 0, 0, 0.45)' }} id="student-grouptask-card">
        <div className="card-header d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-2">
          <h2 className="h5 mb-0">조별과제 제출 현황</h2>
          <div className="text-muted small">
            조별과제 <span id="student-grouptask-count">{groupTaskState.records.length}</span>건
          </div>
        </div>
        <div className="card-body">
          <div className="table-responsive">
            <table className="table table-sm align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th scope="col">과제명</th>
                  <th scope="col" className="text-center" style={{ width: 120 }}>소속 조</th>
                  <th scope="col" className="text-center" style={{ width: 90 }}>제출 대상</th>
                  <th scope="col" className="text-center" style={{ width: 100 }}>제출 상태</th>
                  <th scope="col" className="text-center" style={{ width: 180 }}>제출 시간</th>
                  <th scope="col" className="text-center" style={{ width: 80 }}>점수</th>
                  <th scope="col">평가 의견</th>
                </tr>
              </thead>
              <tbody id="student-grouptask-tbody">
                {groupTaskTableBody}
              </tbody>
            </table>
          </div>
          <div className="text-muted small mt-2">
            * 제출 대상이 아닌 경우 조 편성에서 제외된 학생입니다.
          </div>
        </div>
      </div>
    </section>
  )
}
