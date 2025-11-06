import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { useParams } from 'react-router-dom'

const STRINGS = {
  heading: '성적조회/이의제기',
  intro: '1차 성적 확정이 완료되었습니다. 버튼을 눌러 결과를 확인해 보세요.',
  cta: '결과 확인',
  loading: '결과를 불러오는 중입니다...',
  error: '성적 정보를 불러오는 데 실패했습니다. 잠시 후 다시 시도해주세요.',
  missingLecture: '강의 정보를 확인할 수 없습니다.',
  gradeLabel: '학점',
  gradeFallback: '-',
  gradeSectionTitle: '성적 조회',
  gradeSectionDescription: '아래 버튼을 눌러 현재 성적과 세부 정보를 확인하세요.',
  gradePlaceholder: '아직 성적을 조회하지 않았습니다.',
  professorTitle: '담당 교수 연락처',
  professorPlaceholder: '등록된 담당 교수 정보가 없습니다.',
  professorLoading: '담당 교수 정보를 불러오는 중입니다.',
  professorError: '담당 교수 정보를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.',
  nameLabel: '이름',
  deptLabel: '학과',
  emailLabel: '이메일',
  phoneLabel: '연락처(개인)',
  officeLabel: '연구실',
  emptyData: '성적 정보가 비어 있습니다.',
}

const formatPoint = (value) => {
  if (value === null || value === undefined) return ''
  const num = Number(value)
  if (!Number.isFinite(num)) return ''
  return num.toFixed(2).replace(/\.?0+$/, '')
}

export default function StudentGradeAppeal() {
  const { lectureId } = useParams()
  const [status, setStatus] = useState('idle') // idle | loading | done | error
  const [errorMessage, setErrorMessage] = useState('')
  const [score, setScore] = useState(null)
  const [professor, setProfessor] = useState(null)
  const [photoError, setPhotoError] = useState(false)
  const [professorStatus, setProfessorStatus] = useState('loading') // loading | done | error

  const gradeText = useMemo(() => {
    if (!score) return null
    const gradeCode = score.gpaCd || STRINGS.gradeFallback
    const point = score.autoGrade
    const pointText = formatPoint(point)
    return pointText ? `${gradeCode}(${pointText})` : gradeCode
  }, [score])

  const professorName = useMemo(() => {
    if (!professor) return ''
    const lastName = professor.lastName || ''
    const firstName = professor.firstName || ''
    return `${lastName}${firstName}`.trim()
  }, [professor])

  const professorPhotoUrl = useMemo(() => {
    if (!professor?.professorNo || photoError) return null
    return `/classroom/api/v1/common/photo/professor/${encodeURIComponent(professor.professorNo)}`
  }, [photoError, professor])

  const handleCheckClick = useCallback(async () => {
    if (!lectureId) {
      setErrorMessage(STRINGS.missingLecture)
      setStatus('error')
      return
    }

    setStatus('loading')
    setErrorMessage('')
    setScore(null)
    setPhotoError(false)

    try {
      const encodedLectureId = encodeURIComponent(lectureId)
      const scoreRes = await fetch(`/classroom/api/v1/student/${encodedLectureId}/score`, {
        headers: { Accept: 'application/json' },
        credentials: 'include',
      })

      if (!scoreRes.ok) {
        throw new Error(`${scoreRes.status} ${scoreRes.statusText}`)
      }

      const scoreData = await scoreRes.json()

      const normalizedScore = Array.isArray(scoreData) ? scoreData[0] || null : scoreData || null
      if (!normalizedScore) {
        throw new Error(STRINGS.emptyData)
      }

      setScore(normalizedScore)
      setStatus('done')
    } catch (error) {
      setStatus('error')
      setErrorMessage(error?.message || STRINGS.error)
    }
  }, [lectureId])

  const professorDetails = useMemo(() => {
    if (!professor) return []
    return [
      { label: STRINGS.emailLabel, value: professor.email || STRINGS.gradeFallback },
      { label: STRINGS.phoneLabel, value: professor.mobileNo || STRINGS.gradeFallback },
      { label: STRINGS.officeLabel, value: professor.officeNo || STRINGS.gradeFallback },
    ]
  }, [professor])

  useEffect(() => {
    if (!lectureId) {
      setProfessor(null)
      setProfessorStatus('error')
      return
    }

    let active = true

    const loadProfessor = async () => {
      setProfessorStatus('loading')
      setPhotoError(false)

      try {
        const encodedLectureId = encodeURIComponent(lectureId)
        const res = await fetch(`/classroom/api/v1/common/${encodedLectureId}/professor`, {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })

        if (!res.ok) {
          throw new Error(`${res.status} ${res.statusText}`)
        }

        const data = await res.json()
        if (!active) return

        setProfessor(data && typeof data === 'object' ? data : null)
        setProfessorStatus('done')
      } catch (err) {
        if (!active) return
        setProfessor(null)
        setProfessorStatus('error')
      }
    }

    loadProfessor()
    return () => { active = false }
  }, [lectureId])

  return (
    <div className="card border-0 shadow-sm">
      <div className="card-body">
        <h2 className="h5 mb-3">{STRINGS.heading}</h2>
        <p className="text-muted mb-4">
          {STRINGS.intro}
        </p>

        {status === 'error' && (
          <div className="alert alert-danger rounded-4" role="alert">
            {errorMessage || STRINGS.error}
          </div>
        )}

        <div className="row g-4">
          <div className="col-12 col-lg-6">
            <div className="border rounded-4 p-4 h-100">
              <h3 className="h6 mb-3 text-secondary fw-semibold">
                {STRINGS.gradeSectionTitle}
              </h3>
              <p className="text-muted small mb-4">
                {STRINGS.gradeSectionDescription}
              </p>
              <div className="mb-4 text-center" style={{ minHeight: '120px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                {status === 'done' ? (
                  <div style={{ fontSize: '3rem' }} className="fw-bold text-primary">
                    {gradeText || STRINGS.gradeFallback}
                  </div>
                ) : status === 'loading' ? (
                  <div className="text-muted">{STRINGS.loading}</div>
                ) : null}
              </div>
              {status !== 'done' && (
                <div className="text-center mt-5">
                  <button
                    type="button"
                    className="btn btn-primary btn-lg px-4 shadow-sm"
                    onClick={handleCheckClick}
                    disabled={status === 'loading'}
                  >
                    {status === 'loading' ? STRINGS.loading : STRINGS.cta}
                  </button>
                </div>
              )}
            </div>
          </div>
          <div className="col-12 col-lg-6">
            <div className="border rounded-4 p-4 h-100 bg-light">
              <h3 className="h6 mb-3 text-secondary fw-semibold">
                {STRINGS.professorTitle}
              </h3>
              {professorStatus === 'loading' && (
                <p className="text-muted small mb-0">{STRINGS.professorLoading}</p>
              )}
              {professorStatus === 'error' && (
                <p className="text-danger small mb-0">{STRINGS.professorError}</p>
              )}
              {professorStatus === 'done' && professor && (
                <div className="d-flex flex-column flex-md-row align-items-start justify-content-between gap-4">
                  <div className="flex-grow-1">
                    <div className="mb-3">
                      <div className="fw-semibold fs-5">
                        {professorName || STRINGS.gradeFallback}
                      </div>
                      <div className="text-muted small">
                        {professor.univDeptName || STRINGS.gradeFallback}
                      </div>
                    </div>
                    <dl className="row mb-0">
                      {professorDetails.map(({ label, value }) => (
                        <React.Fragment key={label}>
                          <dt className="col-sm-4 text-muted">{label}</dt>
                          <dd className="col-sm-8">{value || STRINGS.gradeFallback}</dd>
                        </React.Fragment>
                      ))}
                    </dl>
                  </div>
                  <div className="flex-shrink-0 d-flex justify-content-end">
                    {professorPhotoUrl ? (
                      <img
                        src={professorPhotoUrl}
                        alt={professorName ? `${professorName} Professor photo` : 'Professor photo'}
                        className="shadow-sm"
                        style={{ maxHeight: '240px', width: 'auto', borderRadius: '1rem', objectFit: 'contain' }}
                        onError={() => setPhotoError(true)}
                      />
                    ) : (
                      <div
                        className="bg-secondary-subtle border d-flex align-items-center justify-content-center text-muted fw-semibold shadow-sm"
                        style={{ width: '180px', height: '220px', borderRadius: '1rem' }}
                      >
                        {professorName ? professorName[0] : 'P'}
                      </div>
                    )}
                  </div>
                </div>
              )}
              {professorStatus === 'done' && !professor && (
                <p className="text-muted small mb-0">
                  {STRINGS.professorPlaceholder}
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
