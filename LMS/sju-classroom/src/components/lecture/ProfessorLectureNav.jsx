import React from 'react'
import { NavLink, useParams } from 'react-router-dom'
import { useLecture } from '../../context/LectureContext'

export default function ProfessorLectureNav() {
  const { lectureId } = useParams()
  const { lecture } = useLecture()

  const base = `/classroom/professor/${encodeURIComponent(lectureId || '')}`
  const isEnded = Boolean(lecture?.ended)
  const isFinalized = Boolean(lecture?.finalized)
  const finalizeTooltip = '성적 확정이 완료되어 해당 기능을 사용할 수 없습니다.'

  const renderItem = (to, label, opts = {}) => {
    const { end = false, disabled = false, tooltip } = opts

    if (disabled) {
      return (
        <li className="nav-item" key={to}>
          <span
            className="nav-link disabled text-muted"
            title={tooltip}
            aria-disabled="true"
            style={{ cursor: 'not-allowed' }}
          >
            {label}
          </span>
        </li>
      )
    }

    return (
      <li className="nav-item" key={to}>
        <NavLink end={end} className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`} to={to}>
          {label}
        </NavLink>
      </li>
    )
  }

  return (
    <ul className="nav nav-pills mb-3">
      {renderItem(base, '강의홈', { end: true })}
      {renderItem(`${base}/summary`, '강의 요약')}
      {renderItem(`${base}/board`, '게시판')}
      {renderItem(`${base}/attendance`, '출결')}
      {renderItem(`${base}/task`, '과제')}
      {renderItem(`${base}/exam`, '시험')}
      {renderItem(`${base}/student`, '수강생 관리')}
      {renderItem(`${base}/grades`, '성적/종강 관리', {
        disabled: isEnded || isFinalized,
        tooltip: isEnded ? '종강 처리가 완료되어 해당 기능을 사용할 수 없습니다.' : (isFinalized ? finalizeTooltip : undefined),
      })}
      {isEnded
        ? renderItem(`${base}/finalize`, '최종 성적 수정/확정')
        : null}
    </ul>
  )
}
