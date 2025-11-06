import React, { createContext, useCallback, useContext, useMemo, useRef, useState } from 'react'

const LectureContext = createContext(null)

export function LectureProvider({ children }) {
  const [lecture, setLecture] = useState(null) // 현재 강의 정보
  const [students, setStudents] = useState(null) // 현재 강의 수강생 목록
  const photoCacheRef = useRef(new Map()) // studentNo -> blobUrl

  const getLectureId = useCallback(() => lecture?.lectureId || lecture?.id || lecture?.lectureNo || null, [lecture])
  const formatYearTerm = useCallback((code) => {
      if (!code) return null
      const [year, term] = String(code).split('_')
      const map = { REG1: '1학기', REG2: '2학기', SUB1: '여름계절학기', SUB2: '겨울계절학기' }
      return `${year}년 ${map[term] || term || ''}`.trim()
    }, [])
  const getStudentPhotoUrl = useCallback((studentNo, lId) => {
    const lecId = lId || getLectureId()
    if (!lecId || !studentNo) return null
    return `/classroom/api/v1/common/photo/student/${encodeURIComponent(lecId)}/${encodeURIComponent(studentNo)}`
  }, [getLectureId])
  const getStudentPhotoBlobUrl = useCallback((studentNo) => photoCacheRef.current.get(String(studentNo)) || null, [])
  const cacheStudentPhotoBlobUrl = useCallback((studentNo, blobUrl) => {
    if (!studentNo || !blobUrl) return
    photoCacheRef.current.set(String(studentNo), String(blobUrl))
  }, [])
  const clearLectureScoped = useCallback(() => {
    setStudents(null)
    // revoke cached blob URLs
    for (const url of photoCacheRef.current.values()) {
      try { URL.revokeObjectURL(url) } catch {}
    }
    photoCacheRef.current.clear()
  }, [])

  const value = useMemo(() => {
    return {
      lecture, setLecture, getLectureId, formatYearTerm,
      students, setStudents,
      getStudentPhotoUrl, getStudentPhotoBlobUrl, cacheStudentPhotoBlobUrl,
      clearLectureScoped,
    }
  }, [lecture, students, getLectureId, formatYearTerm, getStudentPhotoUrl, getStudentPhotoBlobUrl, cacheStudentPhotoBlobUrl, clearLectureScoped])

  return <LectureContext.Provider value={value}>{children}</LectureContext.Provider>
}

export function useLecture() {
  const ctx = useContext(LectureContext)
  if (!ctx) throw new Error('useLecture must be used within <LectureProvider>')
  return ctx
}
