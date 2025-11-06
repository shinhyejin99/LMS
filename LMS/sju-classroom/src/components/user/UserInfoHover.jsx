import React, { useEffect, useMemo, useRef, useState } from 'react'
import { useUser } from '../../context/UserContext'

export default function UserInfoHover({ role = 'student' }) {
  const liRef = useRef(null)
  const textRef = useRef(null)
  const popoverRef = useRef(null)
  const [user, setUser] = useState(null)
  const [error, setError] = useState(null)
  const userCtx = useUser()

  const apiPath = role === 'professor' ? '/classroom/api/v1/professor/me' : '/classroom/api/v1/student/me'

  const keyMap = useMemo(() => ({
    lastName: '성',
    firstName: '이름',
    engLname: '영문 성',
    engFname: '영문 이름',
    mobileNo: '연락처',
    email: '이메일',
    professorNo: '교번',
    studentNo: '학번',
    univDeptName: '소속',
    gradeName: '학년',
    stuStatusName: '학적상태',
    prfStatusName: '상태',
    prfAppntName: '채용구분',
    prfPositName: '직책',
  }), [])

  const buildContentHtml = (u, role) => {
    if (!u) return '<div class="text-muted">정보 없음</div>'

    const last = u.lastName || ''
    const first = u.firstName || ''
    const fullName = `${last}${first}`.trim()

    let html = '<div class="container-fluid p-0">'

    const addItem = (label, value, isEmail = false) => {
      if (!value) return '';
      if (isEmail) {
        return `
          <div class="d-flex mb-1">
            <div class="text-muted small me-2">${escapeHtml(label)}</div>
            <div class="fw-semibold text-nowrap">${escapeHtml(String(value))}</div>
          </div>
        `;
      }
      return `
        <div class="row g-0 mb-1">
          <div class="col-4 text-muted small">${escapeHtml(label)}</div>
          <div class="col-8 fw-semibold">${escapeHtml(String(value))}</div>
        </div>
      `;
    };

    // Name
    if (fullName) {
      html += addItem('이름', fullName);
    }

    // Student ID (학번) or Professor ID (교번)
    if (u.studentNo) {
      html += addItem('학번', u.studentNo);
    } else if (u.professorNo) {
      html += addItem('교번', u.professorNo);
    }

    // Department (소속)
    if (u.univDeptName) {
      html += addItem('소속', u.univDeptName);
    }

    // Year (학년) - only for student
    if (u.gradeName && role === 'student') {
      html += addItem('학년', u.gradeName);
    }

    // Academic Status (학적상태) or Professor Status (상태)
    if (u.stuStatusName) {
      html += addItem('학적상태', u.stuStatusName);
    } else if (u.prfStatusName) {
      html += addItem('상태', u.prfStatusName);
    }

    // Contact (연락처)
    if (u.mobileNo) {
      html += addItem('연락처', u.mobileNo);
    }

    // Email (이메일)
    if (u.email) {
      html += addItem('이메일', u.email, true);
    }

    html += '</div>';
    return html;
  }

  function escapeHtml(value) {
    return String(value)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;')
  }

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        const res = await fetch(apiPath, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        const json = await res.json()
        if (!alive) return
        setUser(json)
        // 전역 컨텍스트에도 보관하여 다른 컴포넌트에서 userId 비교 등에 활용
        try {
          userCtx.setUser(json)
          userCtx.setRole(role)
        } catch {}
      } catch (e) {
        if (!alive) return
        setError(e)
      }
    })()
    return () => { alive = false }
  }, [apiPath])

  useEffect(() => {
    // Update display text - (이전에 이름이 합쳐지도록 이미 수정된 상태)
    if (textRef.current) {
      if (error) {
        textRef.current.textContent = '정보 로딩 실패'
      } else if (user) {
        const last = user.lastName || ''
        const first = user.firstName || ''
        // ❗ 성과 이름을 합침 (Header 텍스트에 표시)
        const fullName = `${last}${first}`.trim() 
        const dept = user.univDeptName || ''
        
        if (role === 'professor') {
          textRef.current.innerHTML = `<i class="bi bi-person-circle me-1"></i> ${escapeHtml(`${dept} ${fullName}`.trim())} 교수`
        } else {
          const grade = user.gradeName || ''
          textRef.current.innerHTML = `<i class="bi bi-person-circle me-1"></i> ${escapeHtml(`${dept} ${grade} ${fullName}`.trim())}`
        }
      }
    }
  }, [user, error, role])

  useEffect(() => {
    // Initialize Bootstrap Popover on hover
    const el = liRef.current
    if (!el || !window.bootstrap) return

    // Dispose previous
    if (popoverRef.current && popoverRef.current.dispose) {
      popoverRef.current.dispose()
      popoverRef.current = null
    }

    const instance = new window.bootstrap.Popover(el, {
      trigger: 'hover',
      placement: 'bottom',
      html: true,
      title: '내 정보',
      content: () => buildContentHtml(user, role), // ❗ 수정된 함수 사용
    })
    popoverRef.current = instance

    return () => {
      if (popoverRef.current && popoverRef.current.dispose) {
        popoverRef.current.dispose()
        popoverRef.current = null
      }
    }
  }, [user])

  return (
    <li
      ref={liRef}
      className="nav-item"
      id="user-info-container"
      style={{ cursor: 'pointer' }}
      data-user-role={role}
    >
      <span className="navbar-text" id="user-info-text" ref={textRef}>
        <i className="bi bi-person-circle me-1"></i> 내 정보
      </span>
    </li>
  )
}