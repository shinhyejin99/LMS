export async function fetchUser(role) {
  const base = '/classroom/api/v1'
  const path = role === 'professor' ? `${base}/professor/me` : `${base}/student/me`
  const res = await fetch(path, {
    method: 'GET',
    credentials: 'include',
    headers: {
      'Accept': 'application/json',
    },
  })
  if (!res.ok) {
    const text = await res.text().catch(() => '')
    throw new Error(`사용자 정보 조회 실패 (${res.status}) ${text}`)
  }
  try {
    return await res.json()
  } catch (e) {
    // JSON이 아닐 수도 있으니 원문 반환
    return { raw: await res.text() }
  }
}

