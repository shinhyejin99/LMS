import React, { useEffect } from 'react'
import { Outlet, useParams } from 'react-router-dom'
import StudentLectureNav from '../components/lecture/StudentLectureNav'
import ClassroomLayout from '../components/layout/ClassroomLayout'
import { useLecture } from '../context/LectureContext'

export default function StudentLectureLayout() {
  const { lectureId } = useParams()
  const { setLecture, clearLectureScoped } = useLecture()

  useEffect(() => {
    let alive = true
    const fetchLecture = async () => {
      try {
        const res = await fetch(`/classroom/api/v1/common/lecture/${lectureId}`, {
          headers: { Accept: 'application/json' },
          credentials: 'include'
        })

        // HTML 응답 체크 (로그인 페이지로 리다이렉트된 경우)
        const contentType = res.headers.get('content-type')
        if (contentType && contentType.includes('text/html')) {
          console.warn('Received HTML instead of JSON - possible authentication issue')
          if (alive) setLecture(null)
          return
        }

        if (!res.ok) throw new Error('Failed to fetch lecture info')
        const data = await res.json()
        if (alive) {
          setLecture(data)
        }
      } catch (err) {
        console.error(err)
        if (alive) {
          setLecture(null)
        }
      }
    }

    if (lectureId) {
      fetchLecture()
    }

    return () => {
      alive = false
      clearLectureScoped()
      setLecture(null)
    }
  }, [lectureId, setLecture, clearLectureScoped])

  return (
    <ClassroomLayout role="student" lectureId={lectureId}>
      <StudentLectureNav />
      <Outlet />
    </ClassroomLayout>
  )
}