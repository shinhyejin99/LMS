import React, { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'

const pad = (value) => String(value).padStart(2, '0')
const toLocalInputValue = (date) => {
  if (!(date instanceof Date) || Number.isNaN(date.getTime())) return ''
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}
const normalizeLocalDateTime = (value) => {
  if (!value) return ''
  return value.length === 16 ? `${value}:00` : value
}

export default function ExamEditForm() {
  const { lectureId, examId } = useParams()
  const navigate = useNavigate()

  const [examName, setExamName] = useState('')
  const [examDesc, setExamDesc] = useState('')
  const [startAt, setStartAt] = useState('')
  const [endAt, setEndAt] = useState('')
  const [weightValue, setWeightValue] = useState('')
  const [info, setInfo] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!lectureId || !examId) {
      setError('강의 ID 또는 시험 ID가 유효하지 않습니다.')
      setLoading(false)
      return
    }
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError('')
        const url = `/classroom/api/v1/professor/exam/${encodeURIComponent(lectureId)}/${encodeURIComponent(examId)}`
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`(${res.status}) 시험 정보를 불러오지 못했습니다.`)
        const data = await res.json()
        if (!alive) return

        setExamName(data.examName || '')
        setExamDesc(data.examDesc || data.notice || '')
        setWeightValue(typeof data.weightValue === 'number' ? String(data.weightValue) : '')

        if (data.startAt) {
          const startDate = new Date(data.startAt)
          if (!Number.isNaN(startDate.getTime())) {
            setStartAt(toLocalInputValue(startDate))
          }
        }
        if (data.endAt) {
          const endDate = new Date(data.endAt)
          if (!Number.isNaN(endDate.getTime())) {
            setEndAt(toLocalInputValue(endDate))
          }
        }
      } catch (e) {
        if (!alive) return
        setError(e instanceof Error ? e.message : '시험 정보를 불러오는 중 오류가 발생했습니다.')
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, examId])

  const clearMessages = () => {
    setInfo('')
    setError('')
  }

  const buildPayload = () => {
    const name = examName.trim()
    if (!name) throw new Error('시험명을 입력해주세요.')

    const desc = examDesc.trim()
    if (!desc) throw new Error('시험 설명을 입력해주세요.')

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

    const payload = {
      examName: name,
      examDesc: desc,
      startAt: normalizedStart,
      endAt: normalizedEnd,
    }
    if (weight !== null) payload.weightValue = weight
    return payload
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    clearMessages()

    if (!lectureId || !examId) {
      setError('강의 ID 또는 시험 ID가 유효하지 않습니다.')
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

    const submitUrl = `/classroom/api/v1/professor/exam/${encodeURIComponent(lectureId)}/${encodeURIComponent(examId)}`
    try {
      setSubmitting(true)
      const response = await fetch(submitUrl, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(payload),
      })
      if (!response.ok) {
        const message = await response.text().catch(() => '')
        throw new Error(message || `(${response.status}) 수정에 실패했습니다.`)
      }
      setInfo('시험이 수정되었습니다.')
      setTimeout(() => {
        navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/exam/${encodeURIComponent(examId)}`)
      }, 1000)
    } catch (err) {
      const message = err instanceof Error ? err.message : '수정 처리 중 오류가 발생했습니다.'
      setError(message)
    } finally {
      setSubmitting(false)
    }
  }

  const backUrl = `/classroom/professor/${encodeURIComponent(lectureId || '')}/exam/${encodeURIComponent(examId || '')}`

  return (
    <section id="exam-edit-root" className="container py-4">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <nav aria-label="breadcrumb">
          <ol className="breadcrumb mb-0">
            <li className="breadcrumb-item">
              <Link to={`/classroom/professor/${encodeURIComponent(lectureId || '')}/exam`}>시험</Link>
            </li>
            <li className="breadcrumb-item">
              <Link to={backUrl}>시험 상세</Link>
            </li>
            <li className="breadcrumb-item active" aria-current="page">수정</li>
          </ol>
        </nav>
        <div className="d-flex gap-2">
          <Link className="btn btn-sm btn-outline-secondary" to={backUrl}>취소</Link>
        </div>
      </div>

      {error && <div id="alert-box" className="alert alert-danger" role="alert">{error}</div>}
      {info && <div id="toast-box" className="alert alert-success" role="alert">{info}</div>}

      {loading ? (
        <div className="text-center text-muted py-4">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p className="mt-2 mb-0">시험 정보를 불러오는 중입니다...</p>
        </div>
      ) : (
        <form id="exam-edit-form" className="vstack gap-4" onSubmit={handleSubmit}>
          <div className="card shadow-sm p-0">
            <div className="card-header">
              <strong>시험 정보</strong>
            </div>
            <div className="card-body p-3 vstack gap-3">
              <div className="row g-3">
                <div className="col-12 col-lg-6">
                  <label htmlFor="exam-name" className="form-label d-block text-start">시험명 <span className="text-danger">*</span></label>
                  <input id="exam-name" type="text" className="form-control" required maxLength={100} disabled={submitting} value={examName} onChange={(event) => setExamName(event.target.value)} placeholder="예: 2차 중간평가" />
                </div>
                <div className="col-12 col-lg-6">
                  <label htmlFor="exam-weight" className="form-label d-block text-start">반영 비율(%) <span className="text-muted">(선택)</span></label>
                  <input id="exam-weight" type="number" className="form-control" min={0} max={100} step={1} disabled={submitting} value={weightValue} onChange={(event) => setWeightValue(event.target.value)} placeholder="0~100 사이의 정수" />
                  <div className="form-text">총합 100%를 넘지 않도록 주의해주세요.</div>
                </div>
              </div>
              <div>
                <label htmlFor="exam-desc" className="form-label d-block text-start">시험 설명 <span className="text-danger">*</span></label>
                <textarea id="exam-desc" className="form-control" rows={3} required disabled={submitting} value={examDesc} onChange={(event) => setExamDesc(event.target.value)} placeholder="시험 범위, 지참물, 배점 등 안내를 입력해주세요." />
              </div>
              <div className="row g-3">
                <div className="col-12 col-md-6">
                  <label htmlFor="exam-start" className="form-label d-block text-start">시험 시작 일시 <span className="text-danger">*</span></label>
                  <input id="exam-start" type="datetime-local" className="form-control" required disabled={submitting} value={startAt} onChange={(event) => setStartAt(event.target.value)} />
                </div>
                <div className="col-12 col-md-6">
                  <label htmlFor="exam-end" className="form-label d-block text-start">시험 종료 일시 <span className="text-danger">*</span></label>
                  <input id="exam-end" type="datetime-local" className="form-control" required disabled={submitting} value={endAt} onChange={(event) => setEndAt(event.target.value)} />
                  <div className="form-text">종료 일시는 시작 일시 이후여야 합니다.</div>
                </div>
              </div>
            </div>
          </div>

          <div className="d-flex justify-content-end gap-2">
            <Link to={backUrl} className="btn btn-outline-secondary" disabled={submitting}>취소</Link>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? '수정 중...' : '시험 수정'}
            </button>
          </div>
        </form>
      )}
    </section>
  )
}
