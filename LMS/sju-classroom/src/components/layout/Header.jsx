import React from 'react'
import { Link } from 'react-router-dom'
import UserInfoHover from '../user/UserInfoHover'
import LectureInfoHover from '../lecture/LectureInfoHover'
import ProfessorScheduleTimetable from '../professor/ScheduleTimetable'
import StudentScheduleTimetable from '../student/ScheduleTimetable'
import { useLecture } from '../../context/LectureContext'

export default function Header({ role = 'student', lectureId }) {
  const { lecture } = useLecture()
  return (
    <nav className="navbar navbar-dark app-header">
      <div className="container-fluid d-flex align-items-center">
        <div className="d-flex align-items-center me-auto gap-2">
          <Link className="navbar-brand fw-bold m-0" to={`/classroom/${role}`} style={{ fontSize: '1.25em' }}>Classroom</Link>
          {lectureId ? (
            <div className="d-flex align-items-center gap-2" style={{ position: 'absolute', left: '260px' }}>
              <span className="navbar-text text-white fw-semibold" style={{ fontSize: '1.5em' }} data-lecture-subject-label="true">
                {lecture?.subjectName || '강의 정보'}
              </span>
              <LectureInfoHover lectureId={lectureId} />
            </div>
          ) : null}
        </div>

        <ul className="navbar-nav flex-row ms-auto align-items-center gap-3">
          <UserInfoHover role={role} />
          <li className="nav-item">
            {role === 'professor' ? <ProfessorScheduleTimetable /> : <StudentScheduleTimetable />}
          </li>
          <li className="nav-item dropdown">
            <a className="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              바로가기
            </a>
            <ul className="dropdown-menu dropdown-menu-dark dropdown-menu-end">
              <li><a className="dropdown-item" href="http://localhost/portal" target="_blank" rel="noopener noreferrer">포털 사이트</a></li>
              <li><hr className="dropdown-divider" /></li>
              {role === 'professor' ? (
                <li><a className="dropdown-item" href="http://localhost/professor" target="_blank" rel="noopener noreferrer">학사관리</a></li>
              ) : (
                <li><a className="dropdown-item" href="http://localhost/student" target="_blank" rel="noopener noreferrer">학사관리</a></li>
              )}
            </ul>
          </li>
          <li className="nav-item">
            <a className="nav-link p-0 text-danger" href="/logout">로그아웃</a>
          </li>
        </ul>
      </div>
    </nav>
  )
}
