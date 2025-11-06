import React, { useEffect, useState, useMemo } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Swal from 'sweetalert2'
import './StudentReview.css'

export default function StudentReview() {
  const { lectureId } = useParams()
  const navigate = useNavigate()
  const [questions, setQuestions] = useState([])
  const [answers, setAnswers] = useState({})
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    let alive = true
    const load = async () => {
      if (!lectureId) {
        setError('강의 정보를 확인할 수 없습니다.')
        setLoading(false)
        return
      }
      try {
        setLoading(true)
        setError('')

        // 먼저 평가 대상 강의 목록을 확인
        const reviewListRes = await fetch('/classroom/api/v1/student/review', {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })

        if (reviewListRes.ok) {
          const reviewList = await reviewListRes.json()
          if (!alive) return

          // 현재 강의가 목록에 없으면 이미 작성한 것
          if (Array.isArray(reviewList) && !reviewList.includes(lectureId)) {
            await Swal.fire({
              icon: 'info',
              title: '이미 작성 완료',
              text: '이미 강의평가를 작성하셨습니다.',
              confirmButtonText: '확인'
            })
            navigate(-1)
            return
          }
        }

        // 질문 목록 조회
        const res = await fetch(`/classroom/api/v1/student/${encodeURIComponent(lectureId)}/review/question`, {
          headers: { Accept: 'application/json' },
          credentials: 'include',
        })
        if (!res.ok) {
          const text = await res.text().catch(() => '')
          throw new Error(text || `(${res.status}) 강의평가 질문을 불러오지 못했습니다.`)
        }
        const data = await res.json()
        if (!alive) return
        setQuestions(Array.isArray(data) ? data : [])
      } catch (err) {
        if (!alive) return
        setError(err instanceof Error ? err.message : '강의평가 질문을 불러오는 중 문제가 발생했습니다.')
        setQuestions([])
      } finally {
        if (alive) setLoading(false)
      }
    }
    load()
    return () => { alive = false }
  }, [lectureId, navigate])

  // 질문을 타입별로 분류
  const groupedQuestions = useMemo(() => {
    const objective = questions.filter(q => q.answerTypeCd === 'OBJ_SINGLE')
    const shortAnswer = questions.filter(q => q.answerTypeCd === 'SUBJ_SRT')
    const longAnswer = questions.filter(q => q.answerTypeCd === 'SUBJ_LONG')
    return { objective, shortAnswer, longAnswer }
  }, [questions])

  const handleAnswerChange = (questionNo, value) => {
    setAnswers(prev => ({
      ...prev,
      [questionNo]: value
    }))
  }

  const handleTextareaInput = (e) => {
    e.target.style.height = 'auto'
    e.target.style.height = e.target.scrollHeight + 'px'
  }

  const handleDebugFill = () => {
    const debugAnswers = {}

    // 객관식: 랜덤하게 3~5점
    groupedQuestions.objective.forEach(q => {
      debugAnswers[q.questionNo] = String(Math.floor(Math.random() * 3) + 3) // 3, 4, 5 중 랜덤
    })

    // 주관식 단답형
    groupedQuestions.shortAnswer.forEach(q => {
      debugAnswers[q.questionNo] = '컴퓨팅 사고력과 문제 해결 능력이 많이 향상되었습니다.'
    })

    // 주관식 서술형
    groupedQuestions.longAnswer.forEach(q => {
      debugAnswers[q.questionNo] = '이 강의는 매우 유익했으며, 교수님의 설명이 명확하고 이해하기 쉬웠습니다. 앞으로도 이런 방식의 강의를 듣고 싶습니다.'
    })

    setAnswers(debugAnswers)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    // 모든 질문에 답변했는지 확인
    const unanswered = []

    groupedQuestions.objective.forEach((q, index) => {
      if (!answers[q.questionNo] || answers[q.questionNo].toString().trim() === '') {
        unanswered.push({ section: '객관식 문항', number: index + 1 })
      }
    })

    groupedQuestions.shortAnswer.forEach((q, index) => {
      if (!answers[q.questionNo] || answers[q.questionNo].toString().trim() === '') {
        unanswered.push({ section: '주관식 단답형', number: index + 1 })
      }
    })

    groupedQuestions.longAnswer.forEach((q, index) => {
      if (!answers[q.questionNo] || answers[q.questionNo].toString().trim() === '') {
        unanswered.push({ section: '주관식 서술형', number: index + 1 })
      }
    })

    if (unanswered.length > 0) {
      // 같은 타입별로 그룹화
      const grouped = {}
      unanswered.forEach(u => {
        if (!grouped[u.section]) {
          grouped[u.section] = []
        }
        grouped[u.section].push(u.number)
      })

      // "객관식 문항 1,3,5번" 형식으로 표시
      const messages = Object.entries(grouped).map(([section, numbers]) =>
        `${section} ${numbers.join(',')}번`
      )

      await Swal.fire({
        icon: 'warning',
        title: '답변하지 않은 문항이 있습니다',
        html: messages.join('<br/>'),
        confirmButtonText: '확인'
      })
      return
    }

    const confirmResult = await Swal.fire({
      icon: 'question',
      title: '강의평가를 제출하시겠습니까?',
      text: '제출 후에는 수정할 수 없습니다.',
      showCancelButton: true,
      confirmButtonText: '제출',
      cancelButtonText: '취소'
    })

    if (!confirmResult.isConfirmed) {
      return
    }

    try {
      setSubmitting(true)
      setError('')

      const payload = Object.entries(answers).map(([questionNo, answer]) => ({
        questionNo: Number(questionNo),
        answer: answer.toString()
      }))

      console.log('강의평가 제출 데이터:')
      console.log(JSON.stringify(payload, null, 2))

      const res = await fetch(`/classroom/api/v1/student/${encodeURIComponent(lectureId)}/review`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(payload)
      })

      if (!res.ok) {
        const text = await res.text().catch(() => '')
        throw new Error(text || `(${res.status}) 강의평가 제출에 실패했습니다.`)
      }

      const result = await Swal.fire({
        icon: 'success',
        title: '제출 완료',
        text: '강의평가가 제출되었습니다.',
        showCancelButton: true,
        confirmButtonText: '성적 확인으로',
        confirmButtonColor: '#dc3545',
        cancelButtonText: '확인'
      })
      if (result.isConfirmed) {
        navigate(`/classroom/student/${lectureId}/grade`)
      } else {
        navigate(`/classroom/student/${lectureId}`)
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '강의평가 제출 중 문제가 발생했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="container">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1 className="h4 mb-0">강의평가</h1>
        <button
          type="button"
          className="btn btn-sm btn-outline-secondary"
          onClick={handleDebugFill}
          disabled={submitting || loading}
        >
          디버깅
        </button>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      )}

      {loading ? (
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">로딩 중...</span>
          </div>
        </div>
      ) : questions.length === 0 ? (
        <div className="alert alert-warning">강의평가 질문을 확인할 수 없습니다.</div>
      ) : (
        <form onSubmit={handleSubmit}>
          <div className="alert alert-info mb-4">
            <strong>안내:</strong> 모든 질문에 답변하신 후 제출해주세요. 제출 후에는 수정할 수 없습니다.
          </div>

          {groupedQuestions.objective.length > 0 && (
            <div className="review-section">
              <h2>객관식 문항</h2>
              <div className="table-responsive">
                <table className="table table-hover objective-table">
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>질문</th>
                      <th className="text-center">전혀 그렇지 않다</th>
                      <th className="text-center">그렇지 않다</th>
                      <th className="text-center">보통이다</th>
                      <th className="text-center">그렇다</th>
                      <th className="text-center">매우 그렇다</th>
                    </tr>
                  </thead>
                  <tbody>
                    {groupedQuestions.objective.map((question, index) => (
                      <tr key={question.questionNo}>
                        <td>{index + 1}</td>
                        <td>{question.question}</td>
                        {['1', '2', '3', '4', '5'].map(value => (
                          <td key={value} className="text-center">
                            <label className="form-check-label">
                              <input
                                type="radio"
                                name={`question-${question.questionNo}`}
                                value={value}
                                checked={answers[question.questionNo] === value}
                                onChange={(e) => handleAnswerChange(question.questionNo, e.target.value)}
                                disabled={submitting}
                                className="form-check-input"
                              />
                            </label>
                          </td>
                        ))}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {groupedQuestions.shortAnswer.length > 0 && (
            <div className="review-section">
              <h2>주관식 단답형</h2>
              {groupedQuestions.shortAnswer.map((question, index) => (
                <div key={question.questionNo} className="mb-3">
                  <label className="form-label">{index + 1}. {question.question}</label>
                  <input
                    type="text"
                    className="form-control"
                    value={answers[question.questionNo] || ''}
                    onChange={(e) => handleAnswerChange(question.questionNo, e.target.value)}
                    placeholder="답변을 입력하세요"
                    disabled={submitting}
                    maxLength={200}
                  />
                </div>
              ))}
            </div>
          )}

          {groupedQuestions.longAnswer.length > 0 && (
            <div className="review-section">
              <h2>주관식 서술형</h2>
              {groupedQuestions.longAnswer.map((question, index) => (
                <div key={question.questionNo} className="mb-3">
                  <label className="form-label">{index + 1}. {question.question}</label>
                  <textarea
                    className="form-control"
                    value={answers[question.questionNo] || ''}
                    onChange={(e) => handleAnswerChange(question.questionNo, e.target.value)}
                    onInput={handleTextareaInput}
                    placeholder="답변을 입력하세요"
                    rows={4}
                    disabled={submitting}
                    maxLength={1000}
                  />
                </div>
              ))}
            </div>
          )}

          <div className="d-flex gap-2 justify-content-end">
            <button
              type="button"
              className="attendance-btn attendance-btn-default"
              onClick={() => navigate(`/classroom/student/${lectureId}`)}
              disabled={submitting}
              style={{ minWidth: '100px' }}
            >
              취소
            </button>
            <button
              type="submit"
              className="attendance-btn attendance-btn-save"
              disabled={submitting}
              style={{ minWidth: '100px' }}
            >
              {submitting ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  제출 중...
                </>
              ) : '최종 제출'}
            </button>
          </div>
        </form>
      )}
    </div>
  );
}
