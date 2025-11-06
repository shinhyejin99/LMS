import React, { useEffect } from 'react'
import { Outlet, useParams } from 'react-router-dom'
import ClassroomLayout from '../components/layout/ClassroomLayout'
import ProfessorLectureNav from '../components/lecture/ProfessorLectureNav'
import { useLecture } from '../context/LectureContext'
import { fetchLectureStudents } from '../lib/api/student'

export default function ProfessorLectureLayout() {
  const { lectureId } = useParams()
  const { setStudents, clearLectureScoped } = useLecture()
  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        if (!lectureId) return
        const list = await fetchLectureStudents(lectureId)
        if (!alive) return
        setStudents(list)
      } catch (e) {
        // 실패해도 화면 자체는 렌더링 가능하므로 무시하거나 토스트 처리 가능
        if (!alive) return
        setStudents([])
      }
    })()
    return () => {
      // 강의 퇴장 시 강의 스코프 데이터 초기화
      clearLectureScoped()
      alive = false
    }
  }, [lectureId])
  return (
    <ClassroomLayout role="professor" lectureId={lectureId}>
      <ProfessorLectureNav />
      <Outlet />
    </ClassroomLayout>
  )
}
