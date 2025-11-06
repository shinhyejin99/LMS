import React, { useEffect, useMemo, useRef, useState } from 'react'
import { fetchLecture, parseScheduleJson } from '../../lib/api/lecture'
import { useLecture } from '../../context/LectureContext'

export default function LectureInfoHover({ lectureId }) {
  const btnRef = useRef(null)
  const popRef = useRef(null)
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const { lecture, setLecture, formatYearTerm } = useLecture()

  const DAY_LABEL = useMemo(() => ({ MO: '월', TU: '화', WE: '수', TH: '목', FR: '금', SA: '토', SU: '일' }), [])
  const fmtTime = (hhmm) => {
    if (!hhmm || String(hhmm).length < 4) return '--:--'
    const v = String(hhmm)
    return `${v.slice(0, 2)}:${v.slice(2, 4)}`
  }
  const escapeHtml = (v) => String(v ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

  const human = (d) => {
    if (!d) return null
    const yearTerm = formatYearTerm(d.yeartermCd)
    const schedules = parseScheduleJson(d.scheduleJson)
    const cap = (d.currentCap != null || d.maxCap != null) ? `${d.currentCap ?? '-'} / ${d.maxCap ?? '-'}` : null
    return {
      subjectName: d.subjectName,
      professorName: d.professorName,
      univDeptName: d.univDeptName,
      completionName: d.completionName,
      subjectTypeName: d.subjectTypeName,
      yearTerm,
      credit: d.credit,
      hour: d.hour,
      capacity: cap,
      ended: !!d.ended,
      endAt: d.endAt,
      schedules,
    }
  }

  const buildContent = (d) => {
    if (loading) return '<div class="d-flex align-items-center"><div class="spinner-border spinner-border-sm me-2" role="status"></div>불러오는 중…</div>'
    if (error) return `<div class="text-danger small">${escapeHtml(String(error.message || error))}</div>`
    if (!d) return '<div class="text-muted small">정보 없음</div>'

    const mainInfo = [
      ['담당 교수', d.professorName],
      ['개설학과', d.univDeptName],
    ].filter(([, v]) => v != null && String(v).trim().length > 0)

    const subInfo = [
      ['이수구분', d.completionName],
      ['평가방식', d.subjectTypeName],
      ['학점/시수', (d.credit != null || d.hour != null) ? `${d.credit ?? '-'} / ${d.hour ?? '-'}` : null],
      ['수강정원', d.capacity],
      ['학기', d.yearTerm],
    ].filter(([, v]) => v != null && String(v).trim().length > 0)

    const scheduleHtml = d.schedules && d.schedules.length
      ? d.schedules.map((s, idx) => {
          const place = s.placeName || s.placeCd || '장소 미정'
          return (Array.isArray(s.slots) ? s.slots : []).map((slot, slotIdx) => (
            `<div class="lecture-schedule-line d-flex justify-content-between">
               <span>${escapeHtml(DAY_LABEL[slot.day] ?? slot.day ?? '')} ${fmtTime(slot.start)} ~ ${fmtTime(slot.end)}</span>
               <span>${escapeHtml(place)}</span>
             </div>`
          )).join('')
        }).join('')
      : '<div class="text-muted small">시간표 정보 없음</div>'

    const mainInfoHtml = mainInfo.map(([k, v]) => {
      const icon = k === '담당 교수' ? '<i class="bi bi-person-fill"></i>' : '<i class="bi bi-building"></i>'
      return `
        <div class="lecture-info-item">
          <strong class="lecture-info-label">${icon} ${escapeHtml(k)}</strong>
          <span class="lecture-info-value">${escapeHtml(String(v))}</span>
        </div>
      `
    }).join('')

    const subInfoHtml = subInfo.map(([k, v]) => `
      <div class="lecture-sub-info-item">
        <strong class="lecture-sub-info-label">${escapeHtml(k)}</strong>
        <span class="lecture-sub-info-value">${escapeHtml(String(v))}</span>
      </div>
    `).join('')

    return `
      <div class="lecture-info-popover-content">
        <div class="lecture-info-main">${mainInfoHtml}</div>
        <hr class="my-2">
        <div class="lecture-info-sub">${subInfoHtml}</div>
        <hr class="my-2">
        <div class="lecture-schedule-section">
          <strong class="lecture-info-label"><i class="bi bi-clock-fill"></i> 시간표</strong>
          <div class="lecture-schedule-list mt-1">${scheduleHtml}</div>
        </div>
      </div>
    `
  }

  // 필요 시 최초 진입 시점에 전역에도 저장
  useEffect(() => {
    let alive = true
    if (!lectureId) return
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const json = await fetchLecture(lectureId)
        if (!alive) return
        setData(json)
        // 전역 강의 데이터 저장
        setLecture(json)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, setLecture])

  useEffect(() => {
    const el = btnRef.current
    if (!el || !window.bootstrap) return
    // dispose existing
    if (popRef.current?.dispose) {
      popRef.current.dispose()
      popRef.current = null
    }
    const instance = new window.bootstrap.Popover(el, {
      trigger: 'hover',
      placement: 'bottom',
      html: true,
      title: '강의 정보',
      content: () => buildContent(human(data)),
      customClass: 'lecture-info-popover',
    })
    popRef.current = instance
    return () => {
      if (popRef.current?.dispose) {
        popRef.current.dispose()
        popRef.current = null
      }
    }
  }, [data, loading, error])

  return (
    <button ref={btnRef} type="button" className="btn btn-link nav-link text-white-50 p-0 ms-1" aria-label="강의 정보 보기">
      <i className="bi bi-info-circle" aria-hidden="true"></i> 강의 정보
    </button>
  )
}

