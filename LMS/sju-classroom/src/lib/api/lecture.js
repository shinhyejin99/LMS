export async function fetchLecture(lectureId) {
  const base = '/classroom/api/v1/common'
  const url = `${base}/${encodeURIComponent(lectureId)}`
  const res = await fetch(url, {
    headers: { Accept: 'application/json' },
    credentials: 'include',
  })
  if (!res.ok) throw new Error(`(${res.status}) 강의 정보를 불러오지 못했습니다.`)
  return res.json()
}

export function parseScheduleJson(value) {
  if (!value) return []
  try {
    if (Array.isArray(value)) return value
    if (typeof value === 'string') return JSON.parse(value)
  } catch {}
  return []
}

