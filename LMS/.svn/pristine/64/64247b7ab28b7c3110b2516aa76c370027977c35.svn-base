export async function fetchLectureStudents(lectureId) {
  const url = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/students`
  const res = await fetch(url, { headers: { Accept: 'application/json' }, credentials: 'include' })
  if (!res.ok) throw new Error(`(${res.status}) 학생 목록을 불러오지 못했습니다.`)
  const data = await res.json()
  if (!Array.isArray(data)) throw new Error('서버 응답 형식이 올바르지 않습니다.')
  return data
}

export function studentPhotoUrl(lectureId, studentNo) {
  if (!lectureId || !studentNo) return null
  return `/classroom/api/v1/common/photo/student/${encodeURIComponent(lectureId)}/${encodeURIComponent(studentNo)}`
}

export async function fetchStudentPhotoBlob(lectureId, studentNo) {
  const url = studentPhotoUrl(lectureId, studentNo)
  if (!url) throw new Error('잘못된 매개변수')
  const res = await fetch(url, { credentials: 'include' })
  if (!res.ok) throw new Error(`(${res.status}) 학생 사진을 불러오지 못했습니다.`)
  return await res.blob()
}

