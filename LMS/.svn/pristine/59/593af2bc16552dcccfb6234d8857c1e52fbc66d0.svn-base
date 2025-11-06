import React, { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'

const pad = (n) => String(n).padStart(2, '0')
const fmtDateTime = (iso) => {
  if (!iso) return '-'
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return '-'
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}
const formatPeriod = (start, end) => {
  const startLabel = fmtDateTime(start)
  const endLabel = fmtDateTime(end)
  if (startLabel === '-' && endLabel === '-') return '-'
  if (endLabel === '-') return startLabel
  return `${startLabel} ~ ${endLabel}`
}
const toExamTypeLabel = (examType) => {
  if (examType === 'OFF') return '오프라인'
  if (examType === 'ON') return '온라인'
  return examType || '구분 없음'
}

const TYPE_LABEL = {
  OFF: '오프라인',
  ON: '온라인',
}

export default function StudentExamDetail() {
  const { lectureId, examId } = useParams()
  const [exam, setExam] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let alive = true
    const load = async () => {
      if (!lectureId || !examId) {
        setError('시험 정보를 확인할 수 없습니다.')
        setLoading(false)
        return
      }
      try {
        setLoading(true)
        setError('')
        const res = await fetch(`/classroom/api/v1/student/${encodeURIComponent(lectureId)}/exam/${encodeURIComponent(examId)}`, {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) {
          const text = await res.text().catch(() => '')
          throw new Error(text || `(${res.status}) 시험 정보를 불러오지 못했습니다.`)
        }
        const data = await res.json()
        if (!alive) return
        setExam(data || null)
      } catch (err) {
        if (!alive) return
        setError(err instanceof Error ? err.message : '시험 정보를 불러오는 중 문제가 발생했습니다.')
        setExam(null)
      } finally {
        if (alive) setLoading(false)
      }
    }
    load()
    return () => { alive = false }
  }, [lectureId, examId])

  const submission = useMemo(() => {
    if (!exam?.submitList || !Array.isArray(exam.submitList)) return null
    const entry = exam.submitList.find(item => item?.submit) || exam.submitList[0]
    return entry?.submit || null
  }, [exam])

  const submittedAt = submission?.submitAt ? fmtDateTime(submission.submitAt) : null
  const autoScore = submission?.autoScore
  const modifiedScore = submission?.modifiedScore
  const finalScore = modifiedScore ?? autoScore
  const hasModified = typeof modifiedScore === 'number' && !Number.isNaN(modifiedScore) && modifiedScore !== autoScore

  const detailListUrl = `/classroom/student/${encodeURIComponent(lectureId || '')}/exam`

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
        <dd className="col-sm-10">{fmtDateTime(exam.createAt || exam.createdAt)}</dd>

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

  return (
    <section id="student-exam-detail-root" className="container py-0" data-lecture-id={lectureId} data-exam-id={examId}>
      <div className="d-flex align-items-center justify-content-end mb-3">
        <Link to={detailListUrl} className="btn btn-sm btn-outline-secondary">목록으로</Link>
      </div>

      {error ? (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      ) : null}

      <div className="row">
        <div className="mb-4" style={{ width: '60%' }}>
          <div className="card shadow p-0">
            <div className="card-header">
              <h2 className="h5 mb-0">시험 정보</h2>
            </div>
            <div className="card-body p-3" id="exam-info-body">
              {renderExamInfo()}
            </div>
          </div>
        </div>

        <div className="mb-4" style={{ width: '40%' }}>
          <div className="card shadow p-0">
            <div className="card-header">
              <h2 className="h5 mb-0">내 응시 기록</h2>
            </div>
            <div className="card-body p-3">
              {loading ? (
                <div className="text-center text-muted py-4">
                  <div className="spinner-border" role="status">
                    <span className="visually-hidden">Loading...</span>
                  </div>
                  <p className="mt-2 mb-0">응시 기록을 불러오는 중입니다...</p>
                </div>
              ) : !submission ? (
                <div className="text-center text-muted py-4">응시 기록이 없습니다.</div>
              ) : (
                <dl className="row mb-0 fs-5">
                  <dt className="col-sm-4">응시일</dt>
                  <dd className="col-sm-8">{submittedAt || '응시 정보 없음'}</dd>

                  <dt className="col-sm-4">점수</dt>
                  <dd className="col-sm-8">
                    {typeof finalScore === 'number' ? `${finalScore}/100점` : '점수 정보 없음'}
                    {hasModified && typeof autoScore === 'number' && (
                      <span className="text-muted small d-block">(원점수 {autoScore}/100점)</span>
                    )}
                  </dd>

                  {hasModified && (
                    <>
                      <dt className="col-sm-5">점수 수정 사유</dt>
                      <dd className="col-sm-7">{submission.modifyReason || '사유가 기록되지 않았습니다.'}</dd>
                    </>
                  )}
                </dl>
              )}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

