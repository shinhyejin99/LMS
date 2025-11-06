import React from 'react'
import { useNavigate, useParams } from 'react-router-dom'

/**
 * NotFound 컴포넌트
 * 유효하지 않은 경로변수로 접근 시 표시되는 404 에러 페이지
 *
 * @param {Object} props
 * @param {string} props.message - 표시할 메시지 (기본값: "페이지를 찾을 수 없습니다")
 * @param {string} props.resourceType - 리소스 타입 (예: "게시글", "과제", "시험")
 */
function NotFound({ message = '페이지를 찾을 수 없습니다', resourceType = '페이지' }) {
  const navigate = useNavigate()
  const { lectureId } = useParams()

  const handleGoBack = () => {
    navigate(-1)
  }

  const handleGoToMain = () => {
    if (lectureId) {
      navigate(`/classroom/professor/${lectureId}`)
    } else {
      navigate('/classroom/professor')
    }
  }

  return (
    <div className="container-fluid py-5">
      <div className="row justify-content-center">
        <div className="col-12 col-md-8 col-lg-6">
          <div className="card shadow-sm">
            <div className="card-body text-center py-5">
              <div style={{ fontSize: '5rem', color: '#dc3545', marginBottom: '1.5rem' }}>
                <i className="bi bi-exclamation-triangle"></i>
              </div>

              <h2 className="mb-3" style={{ color: '#495057' }}>404 Not Found</h2>

              <p className="text-muted mb-2" style={{ fontSize: '1.1rem' }}>
                {message}
              </p>

              <p className="text-muted mb-4" style={{ fontSize: '0.95rem' }}>
                요청하신 {resourceType}을(를) 찾을 수 없습니다.
              </p>

              <div className="d-flex gap-3 justify-content-center mt-4">
                <button
                  className="btn btn-outline-secondary btn-lg"
                  onClick={handleGoBack}
                  style={{ minWidth: '140px' }}
                >
                  <i className="bi bi-arrow-left me-2"></i>
                  뒤로가기
                </button>

                <button
                  className="btn btn-primary btn-lg"
                  onClick={handleGoToMain}
                  style={{ minWidth: '140px' }}
                >
                  <i className="bi bi-house-door me-2"></i>
                  클래스룸 메인
                </button>
              </div>

              <div className="mt-5 pt-4 border-top">
                <p className="text-muted small mb-1">
                  <i className="bi bi-info-circle me-1"></i>
                  다음과 같은 경우 이 페이지가 표시될 수 있습니다:
                </p>
                <ul className="list-unstyled text-muted small">
                  <li>• 삭제되었거나 존재하지 않는 {resourceType}</li>
                  <li>• 잘못된 URL 주소</li>
                  <li>• 접근 권한이 없는 {resourceType}</li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default NotFound
