import React, { useEffect, useMemo, useRef, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useLecture } from '../../context/LectureContext'

export default function StudentManage() {
  const { lectureId } = useParams()
  const { students, setStudents, getStudentPhotoUrl, getStudentPhotoBlobUrl, cacheStudentPhotoBlobUrl } = useLecture()
  const [list, setList] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [filters, setFilters] = useState({ department: 'all', grade: 'all', name: '' })
  const [photoUrls, setPhotoUrls] = useState({})
  const fetchingRef = useRef(new Set())

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        if (Array.isArray(students)) {
          setList(students)
          return
        }
        const url = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/students`
        const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
        if (!res.ok) throw new Error(`(${res.status}) 학생 목록을 불러오지 못했습니다.`)
        const data = await res.json()
        if (!alive) return
        const arr = Array.isArray(data) ? data : []
        setList(arr)
        setStudents(arr)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, students, setStudents])

  const departments = useMemo(() => {
    if (!list) return []
    const depts = list.map(s => s.univDeptName).filter(Boolean)
    return [...new Set(depts)].sort()
  }, [list])

  const grades = useMemo(() => {
    if (!list) return []
    const grds = list.map(s => s.gradeName).filter(Boolean)
    return [...new Set(grds)].sort()
  }, [list])

  const filteredList = useMemo(() => {
    if (!list) return []
    return list.filter(student => {
      const fullName = [student.lastName, student.firstName].filter(Boolean).join('')
      const nameMatch = filters.name ? fullName.includes(filters.name) : true
      const deptMatch = filters.department === 'all' ? true : student.univDeptName === filters.department
      const gradeMatch = filters.grade === 'all' ? true : student.gradeName === filters.grade
      return nameMatch && deptMatch && gradeMatch
    })
  }, [list, filters])

  const visibleStudentNos = useMemo(() => {
    return filteredList.map(s => s.studentNo).filter(Boolean)
  }, [filteredList])

  useEffect(() => {
    visibleStudentNos.forEach(studentNo => {
      if (!studentNo || fetchingRef.current.has(studentNo)) return
      const cached = getStudentPhotoBlobUrl(studentNo)
      if (cached) {
        setPhotoUrls(prev => (prev[studentNo] ? prev : { ...prev, [studentNo]: cached }))
        return
      }
      const url = getStudentPhotoUrl(studentNo, lectureId)
      if (!url) return
      fetchingRef.current.add(studentNo)
      fetch(url, { credentials: 'include' })
        .then(res => (res.ok ? res.blob() : null))
        .then(blob => {
          if (!blob) return
          const objectUrl = URL.createObjectURL(blob)
          cacheStudentPhotoBlobUrl(studentNo, objectUrl)
          setPhotoUrls(prev => ({ ...prev, [studentNo]: objectUrl }))
        })
        .catch(() => {})
        .finally(() => {
          fetchingRef.current.delete(studentNo)
        })
    })
  }, [visibleStudentNos, lectureId, getStudentPhotoBlobUrl, getStudentPhotoUrl, cacheStudentPhotoBlobUrl])

  const handleFilterChange = (e) => {
    const { name, value } = e.target
    setFilters(prev => ({ ...prev, [name]: value }))
  }

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-3">
        <h1 className="h4 mb-0">학생관리</h1>
        <div className="text-muted small">총 {filteredList.length}명</div>
      </div>

      <div className="card shadow-sm mb-4 p-0">
        <div className="card-body p-3">
          <div className="row">
            <div className="col-lg-7">
              <div className="row g-2 align-items-end">
                <div className="col-md">
                  <label htmlFor="filter-department" className="form-label small">소속</label>
                  <select id="filter-department" name="department" className="form-select form-select-sm" value={filters.department} onChange={handleFilterChange}>
                    <option value="all">전체</option>
                    {departments.map(dept => <option key={dept} value={dept}>{dept}</option>)}
                  </select>
                </div>
                <div className="col-md">
                  <label htmlFor="filter-grade" className="form-label small">학년</label>
                  <select id="filter-grade" name="grade" className="form-select form-select-sm" value={filters.grade} onChange={handleFilterChange}>
                    <option value="all">전체</option>
                    {grades.map(grade => <option key={grade} value={grade}>{grade}</option>)}
                  </select>
                </div>
                <div className="col-md">
                  <label htmlFor="filter-name" className="form-label small">이름</label>
                  <input type="text" id="filter-name" name="name" className="form-control form-control-sm" value={filters.name} onChange={handleFilterChange} placeholder="이름으로 검색..." style={{ padding: '4px' }} />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {loading && <div className="text-muted">로딩 중…</div>}
      {error && <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>}
      {!loading && !error && (
        <div className="row row-cols-2 row-cols-md-3 row-cols-lg-4 row-cols-xl-5 g-3">
          {filteredList.length === 0 ? (
            <div className="col-12"><div className="text-center text-muted py-5">해당하는 학생이 없습니다.</div></div>
          ) : filteredList.map((s, i) => {
            const studentNo = s.studentNo || s.userId || ''
            const name = [s.lastName, s.firstName].filter(Boolean).join('') || studentNo || '미상'
            const dept = s.univDeptName || '-'
            const grade = s.gradeName || ''
            const email = s.email || ''
            const imgSrc = photoUrls[studentNo] || getStudentPhotoBlobUrl(studentNo) || null
            return (
              <div className="col" key={studentNo || i}>
                <Link to={`/classroom/professor/${encodeURIComponent(lectureId)}/student/${encodeURIComponent(studentNo)}`} className="card h-100 text-decoration-none text-reset p-0">
                  {imgSrc && (
                    <div className="bg-light rounded-top" style={{ overflow: 'hidden' }}>
                      <img
                        src={imgSrc}
                        alt={`${name} 증명사진`}
                        className="w-100"
                        style={{ display: 'block', height: 'auto', objectFit: 'contain' }}
                      />
                    </div>
                  )}
                  <div className="card-body p-3">
                    <div className="d-flex align-items-center gap-2 mb-1">
                      <h3 className="h6 mb-0">{name}</h3>
                      {s.enrollStatusName && (
                        <span className={`badge ${
                          s.enrollStatusCd === 'ENR_ING' ? 'bg-primary' :
                          s.enrollStatusCd === 'ENR_DONE' ? 'bg-success' :
                          s.enrollStatusCd === 'ENR_CANCEL' ? 'bg-warning text-dark' :
                          s.enrollStatusCd === 'ENR_WITHDRAW' ? 'bg-danger' :
                          'bg-secondary'
                        }`}>
                          {s.enrollStatusName}
                        </span>
                      )}
                    </div>
                    <div className="text-muted small mb-2">학번 {studentNo || '-'}</div>
                    <dl className="row small mb-0">
                      <dt className="col-4">소속</dt><dd className="col-8 text-end text-truncate" title={dept}>{dept}</dd>
                      {grade && (<><dt className="col-4">학년</dt><dd className="col-8 text-end">{grade}</dd></>)}
                      {email && (<><dt className="col-4">이메일</dt><dd className="col-8 text-end text-truncate" title={email}>{email}</dd></>)}
                    </dl>
                  </div>
                </Link>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
