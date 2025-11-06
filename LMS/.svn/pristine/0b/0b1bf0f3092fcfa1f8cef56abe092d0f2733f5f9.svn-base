import React, { useEffect, useMemo, useState } from 'react'
import { useParams } from 'react-router-dom'
import Swal from 'sweetalert2'
import { useLecture } from '../../context/LectureContext'
import '../../styles/customButtons.css'

const SECTION_ORDER = ['ATTD', 'TASK', 'EXAM', 'PRAC', 'MISC']
const SECTION_LABELS = {
  ATTD: '출석',
  TASK: '과제',
  EXAM: '시험',
  PRAC: '실습',
  MISC: '기타',
}

const numberFormatter = new Intl.NumberFormat('ko-KR', {
  minimumFractionDigits: 0,
  maximumFractionDigits: 2,
})

function formatNumber(value) {
  if (typeof value !== 'number' || Number.isNaN(value)) return '-'
  return numberFormatter.format(value)
}

const PASS_FAIL_OPTIONS = [
  { code: 'P', label: 'P' },
  { code: 'NP', label: 'NP' },
]

const GPA_OPTIONS = [
  { code: 'A+', label: 'A+(4.5)', score: 4.5 },
  { code: 'A0', label: 'A0(4.0)', score: 4.0 },
  { code: 'B+', label: 'B+(3.5)', score: 3.5 },
  { code: 'B0', label: 'B0(3.0)', score: 3.0 },
  { code: 'C+', label: 'C+(2.5)', score: 2.5 },
  { code: 'C0', label: 'C0(2.0)', score: 2.0 },
  { code: 'D+', label: 'D+(1.5)', score: 1.5 },
  { code: 'D0', label: 'D0(1.0)', score: 1.0 },
  { code: 'F', label: 'F(0.0)', score: 0.0 },
]

const GPA_SCORE_MAP = GPA_OPTIONS.reduce((acc, item) => {
  acc[item.code] = item.score
  return acc
}, {})

export default function ProfessorLectureFinalize() {
  const { lectureId } = useParams()
  const { students, lecture } = useLecture()
  const [scores, setScores] = useState([])
  const [ratios, setRatios] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [keyword, setKeyword] = useState('')
  const [refreshKey, setRefreshKey] = useState(0)
  const [dialog, setDialog] = useState(null)
  const [dialogSaving, setDialogSaving] = useState(false)
  const [dialogError, setDialogError] = useState(null)
  const [finalizing, setFinalizing] = useState(false)

  useEffect(() => {
    let alive = true
    if (!lectureId) {
      setScores([])
      setRatios([])
      setLoading(false)
      return () => {
        alive = false
      }
    }
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const base = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}`
        const [scoreRes, ratioRes] = await Promise.all([
          fetch(`${base}/finalize/score`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`/classroom/api/v1/common/${encodeURIComponent(lectureId)}/ratio`, {
            headers: { Accept: 'application/json' },
            credentials: 'include',
          }),
        ])
        if (!scoreRes.ok) throw new Error(`(${scoreRes.status}) 최종 성적 데이터를 불러오지 못했습니다.`)
        if (!ratioRes.ok) throw new Error(`(${ratioRes.status}) 성적 반영 비율 정보를 불러오지 못했습니다.`)

        const [scoreData, ratioData] = await Promise.all([scoreRes.json(), ratioRes.json()])
        if (!alive) return
        const arr = Array.isArray(scoreData)
          ? scoreData.slice().sort((a, b) => String(a?.enrollId ?? '').localeCompare(String(b?.enrollId ?? '')))
          : []
        setScores(arr)
        setRatios(Array.isArray(ratioData) ? ratioData : [])
      } catch (e) {
        if (!alive) return
        setError(e)
        setScores([])
        setRatios([])
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => {
      alive = false
    }
  }, [lectureId, refreshKey])

  useEffect(() => {
    if (!dialog) return undefined
    const handleKey = (event) => {
      if (event.key === 'Escape') {
        event.preventDefault()
        if (!dialogSaving) {
          setDialog(null)
          setDialogError(null)
        }
      }
    }
    document.addEventListener('keydown', handleKey)
    return () => {
      document.removeEventListener('keydown', handleKey)
    }
  }, [dialog, dialogSaving])

  const studentMap = useMemo(() => {
    const map = new Map()
    if (Array.isArray(students)) {
      students.forEach((student) => {
        if (!student) return
        const key = student.enrollId != null ? String(student.enrollId) : null
        if (key) map.set(key, student)
      })
    }
    return map
  }, [students])

  const orderedRatios = useMemo(() => {
    if (!Array.isArray(ratios)) return []
    const orderMap = new Map(SECTION_ORDER.map((code, index) => [code, index]))
    return ratios
      .map((item) => {
        const code = item?.gradeCriteriaCd
        const ratio = Number(item?.ratio ?? 0)
        const label = item?.gradeCriteriaName || SECTION_LABELS[code] || code || ''
        const weight = orderMap.has(code) ? orderMap.get(code) : SECTION_ORDER.length
        return { code, label, ratio, weight }
      })
      .filter((item) => item.code)
      .sort((a, b) => (a.weight - b.weight) || a.label.localeCompare(b.label))
  }, [ratios])

  const sectionColumns = useMemo(() => {
    const ratioCodes = new Set(orderedRatios.map((item) => item.code))
    const extras = new Set()
    scores.forEach((entry) => {
      if (!entry?.sectionScores) return
      entry.sectionScores.forEach((section) => {
        if (!section?.gradeCriteriaCd) return
        const code = section.gradeCriteriaCd
        if (!ratioCodes.has(code)) extras.add(code)
      })
    })
    const extraColumns = Array.from(extras)
      .sort((a, b) => a.localeCompare(b))
      .map((code) => ({
        code,
        label: SECTION_LABELS[code] || code,
        ratio: null,
      }))
    return [...orderedRatios, ...extraColumns]
  }, [orderedRatios, scores])

  const ratioTotal = useMemo(
    () =>
      orderedRatios.reduce((acc, item) => {
        return acc + (Number.isFinite(item.ratio) ? item.ratio : 0)
      }, 0),
    [orderedRatios],
  )

  const rows = useMemo(() => {
    return scores.map((entry) => {
      const enrollId = entry?.enrollId != null ? String(entry.enrollId) : ''
      const student = enrollId ? studentMap.get(enrollId) : null
      const studentNo = student?.studentNo || student?.userId || '-'
      const rawName = student
        ? [student.lastName, student.firstName].filter(Boolean).join('') ||
          student.studentName ||
          student.userName ||
          student.name ||
          ''
        : entry?.studentName || ''
      const displayName = rawName ? `${rawName}(${studentNo})` : studentNo
      const department = student?.univDeptName || ''
      const grade = student?.gradeName || ''
      const sections = {}
      if (Array.isArray(entry?.sectionScores)) {
        entry.sectionScores.forEach((section) => {
          const code = section?.gradeCriteriaCd
          if (!code) return
          sections[code] = typeof section.rawScore === 'number' ? section.rawScore : null
        })
      }

      // 총점 계산
      let totalScore = 0
      let hasSections = false
      sectionColumns.forEach((col) => {
        const rawScore = sections[col.code]
        if (typeof rawScore === 'number' && col.ratio != null) {
          totalScore += (rawScore * col.ratio) / 100
          hasSections = true
        }
      })

      const autoGrade = typeof entry?.autoGrade === 'number' ? entry.autoGrade : null
      const finalGrade = typeof entry?.finalGrade === 'number' ? entry.finalGrade : null
      const rawReason = entry?.changeReason || ''
      const changeReason = rawReason.trim()
      const changed =
        changeReason.length > 0 ||
        (finalGrade != null &&
          (autoGrade == null || Math.abs(finalGrade - autoGrade) > Number.EPSILON * 10))
      return {
        enrollId,
        displayName,
        studentNo,
        department,
        grade,
        gpaCd: entry?.gpaCd || '',
        autoGrade,
        finalGrade,
        changeReason,
        sections,
        totalScore: hasSections ? totalScore : null,
        changed,
      }
    })
  }, [scores, studentMap, sectionColumns])

  const filteredRows = useMemo(() => {
    const term = keyword.trim().toLowerCase()
    if (!term) return rows
    return rows.filter((row) => {
      return (
        row.displayName.toLowerCase().includes(term) ||
        row.studentNo.toLowerCase().includes(term) ||
        row.enrollId.toLowerCase().includes(term)
      )
    })
  }, [rows, keyword])

  const handleKeywordChange = (event) => {
    setKeyword(event.target.value)
  }

  const handleFinalize = async () => {
    if (!lectureId || finalizing) return

    const result = await Swal.fire({
      title: '성적 최종 확정',
      text: '성적을 최종 확정하시겠습니까? 확정 후에는 변경할 수 없습니다.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: '확정',
      cancelButtonText: '취소'
    })

    if (!result.isConfirmed) return

    try {
      setFinalizing(true)
      const response = await fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/finalize`, {
        method: 'PUT',
        credentials: 'include',
      })
      if (response.status === 204) {
        await Swal.fire({
          title: '확정 완료',
          text: '성적이 최종 확정되었습니다.',
          icon: 'success',
          confirmButtonText: '확인'
        })
        const targetUrl = `/classroom/professor/${encodeURIComponent(lectureId)}`
        window.location.assign(targetUrl)
        return
      }

      const fallbackMessage = `(${response.status}) 성적을 최종 확정하지 못했습니다.`
      const responseText = await response.text().catch(() => '')
      const message = responseText?.trim() ? responseText.trim() : fallbackMessage
      await Swal.fire({
        title: '오류',
        text: message,
        icon: 'error',
        confirmButtonText: '확인'
      })
    } catch (err) {
      const message = err?.message || '성적 최종 확정 처리 중 오류가 발생했습니다.'
      await Swal.fire({
        title: '오류',
        text: message,
        icon: 'error',
        confirmButtonText: '확인'
      })
    } finally {
      setFinalizing(false)
    }
  }

  const ratioBadgeClass = ratioTotal === 100 ? 'badge bg-success' : 'badge bg-warning text-dark'
  const passFailSubject = lecture?.subjectTypeCd === 'SUBJ_PASSFAIL'
  const isFinalized = Boolean(lecture?.finalized)

  const handleOpenDialog = (row) => {
    const passFail = lecture?.subjectTypeCd === 'SUBJ_PASSFAIL'
    setDialog({
      enrollId: row.enrollId,
      name: row.displayName,
      selected: row.gpaCd || '',
      reason: row.changeReason || '',
      passFail,
      autoGrade: row.autoGrade,
    })
    setDialogError(null)
  }

  const handleDialogClose = () => {
    if (dialogSaving) return
    setDialog(null)
    setDialogError(null)
  }

  const handleSelectGpa = (code) => {
    setDialog((prev) => (prev ? { ...prev, selected: code } : prev))
  }

  const handleDialogReasonChange = (event) => {
    const { value } = event.target
    setDialog((prev) => (prev ? { ...prev, reason: value } : prev))
  }

  const handleDemoReason = () => {
    setDialog((prev) => (prev ? { ...prev, reason: '실수로 누락된 과제가 반영되어 성적 수정' } : prev))
  }

  const getGradeButtonClass = (code) => {
    if (code === 'A+' || code === 'A0') return 'grade-btn grade-btn-a'
    if (code === 'B+' || code === 'B0') return 'grade-btn grade-btn-b'
    if (code === 'C+' || code === 'C0') return 'grade-btn grade-btn-c'
    if (code === 'D+' || code === 'D0') return 'grade-btn grade-btn-d'
    if (code === 'F') return 'grade-btn grade-btn-f'
    return 'grade-btn grade-btn-default'
  }

  const handleDialogConfirm = async () => {
    if (!dialog || !lectureId) return
    const trimmedReason = dialog.reason.trim()
    if (!dialog.selected) {
      await Swal.fire({
        title: '알림',
        text: '적용할 학점을 선택해 주세요.',
        icon: 'warning',
        confirmButtonText: '확인'
      })
      return
    }
    if (!trimmedReason) {
      await Swal.fire({
        title: '알림',
        text: '변경 사유는 필수로 기록해야 합니다.',
        icon: 'warning',
        confirmButtonText: '확인'
      })
      return
    }
    try {
      setDialogSaving(true)
      setDialogError(null)
      const res = await fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/finalize/score`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Accept: 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
          enrollId: dialog.enrollId,
          gpaCd: dialog.selected,
          changeReason: trimmedReason,
        }),
      })
      if (!res.ok) {
        let message = `(${res.status}) 성적 변경 요청에 실패했습니다.`
        try {
          const data = await res.json()
          if (data && typeof data.message === 'string' && data.message.trim().length > 0) {
            message = data.message
          }
        } catch {}
        throw new Error(message)
      }
      setScores((prev) =>
        prev.map((item) => {
          if (String(item.enrollId) !== String(dialog.enrollId)) return item
          const updated = { ...item, gpaCd: dialog.selected, changeReason: trimmedReason }
          if (!dialog.passFail && Object.prototype.hasOwnProperty.call(GPA_SCORE_MAP, dialog.selected)) {
            updated.finalGrade = GPA_SCORE_MAP[dialog.selected]
          }
          return updated
        }),
      )
      setDialog(null)
      setDialogError(null)
      setRefreshKey((prev) => prev + 1)
    } catch (err) {
      setDialogError(err?.message || String(err))
      return
    } finally {
      setDialogSaving(false)
    }
  }

  const renderDialog = () => {
    if (!dialog) return null
    const options = dialog.passFail ? PASS_FAIL_OPTIONS : GPA_OPTIONS
    const selected = dialog.selected
    return (
      <div
        role="dialog"
        aria-modal="true"
        className="position-fixed top-0 start-0 end-0 bottom-0 d-flex align-items-center justify-content-center"
        style={{ backgroundColor: 'rgba(0, 0, 0, 0.45)', zIndex: 1050, padding: '1rem' }}
        onClick={(event) => {
          if (event.target === event.currentTarget) handleDialogClose()
        }}
      >
        <div
          className="card shadow-lg w-100"
          style={{ maxWidth: 480 }}
          onClick={(event) => event.stopPropagation()}
        >
          <div className="card-header d-flex align-items-center justify-content-between">
            <h2 className="h6 mb-0">{dialog.name} 성적 변경</h2>
            <button type="button" className="btn-close" aria-label="닫기" onClick={handleDialogClose} disabled={dialogSaving} />
          </div>
          <div className="card-body d-flex flex-column gap-3">
            <div>
              <div className="text-muted small mb-2">적용할 학점을 선택하세요.</div>
              <div className="d-flex flex-wrap gap-2">
                {options.map((option) => {
                  const baseClass = getGradeButtonClass(option.code)
                  const isSelected = selected === option.code
                  return (
                    <button
                      key={option.code}
                      type="button"
                      className={`${baseClass}${isSelected ? ' selected' : ''}`}
                      onClick={() => handleSelectGpa(option.code)}
                      disabled={dialogSaving}
                    >
                      {option.label}
                    </button>
                  )
                })}
              </div>
            </div>
            <div>
              <div className="d-flex align-items-center justify-content-between mb-2">
                <label htmlFor="finalize-change-reason" className="form-label small text-muted mb-0">
                  변경 사유
                </label>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-secondary"
                  onClick={handleDemoReason}
                  disabled={dialogSaving}
                >
                  시연
                </button>
              </div>
              <textarea
                id="finalize-change-reason"
                className="form-control"
                rows={3}
                value={dialog.reason}
                onChange={handleDialogReasonChange}
                placeholder="변경 사유를 입력하세요."
                disabled={dialogSaving}
              />
            </div>
            {dialogError ? (
              <div className="alert alert-danger small mb-0" role="alert">
                {dialogError}
              </div>
            ) : null}
          </div>
          <div className="card-footer d-flex justify-content-end gap-2">
            <button type="button" className="custom-btn custom-btn-outline-secondary custom-btn-sm" onClick={handleDialogClose} disabled={dialogSaving}>
              취소
            </button>
            <button type="button" className="custom-btn custom-btn-primary custom-btn-sm" onClick={handleDialogConfirm} disabled={dialogSaving}>
              {dialogSaving ? '저장 중...' : '변경 적용'}
            </button>
          </div>
        </div>
      </div>
    )
  }

  const renderBody = () => {
    if (loading) {
      return (
        <div className="card" style={{ boxShadow: '0 0.375rem 0.75rem rgba(0, 0, 0, 0.45)' }}>
          <div className="card-body text-center py-5">
            <div className="spinner-border text-primary mb-3" role="status" aria-hidden="true" />
            <span className="visually-hidden">Loading...</span>
            <div className="text-muted small">데이터를 불러오는 중입니다...</div>
          </div>
        </div>
      )
    }
    if (error) {
      const message = error?.message || String(error)
      return (
        <div className="alert alert-danger" role="alert">
          {message}
        </div>
      )
    }
    return (
      <div className="card" style={{ boxShadow: '0 0.375rem 0.75rem rgba(0, 0, 0, 0.45)' }}>
        <div className="card-body">
          <div className="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3 mb-3">
            <div className="d-flex flex-wrap align-items-center gap-2">
              <label htmlFor="finalize-filter" className="text-muted small mb-0">
                검색
              </label>
              <input
                id="finalize-filter"
                type="search"
                value={keyword}
                onChange={handleKeywordChange}
                className="form-control form-control-sm"
                placeholder="이름, 학번 또는 수강번호로 검색..."
                style={{ minWidth: '320px', padding: '8px' }}
              />
            </div>
            {isFinalized ? (
              <div className="alert alert-success mb-0" role="alert" style={{ padding: '8px 12px' }}>
                최종 성적이 확정되었습니다.
              </div>
            ) : (
              <button
                type="button"
                className="custom-btn custom-btn-primary"
                style={{ padding: '8px', fontSize: '0.875rem' }}
                onClick={handleFinalize}
                disabled={finalizing}
              >
                {finalizing ? '확정 처리 중...' : '성적 최종 확정'}
              </button>
            )}
          </div>
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th scope="col" style={{ width: 60 }}>#</th>
                  <th scope="col" style={{ minWidth: 140 }}>수강생</th>
                  {sectionColumns.map((col) => (
                    <th key={col.code} scope="col" className="text-center" style={{ minWidth: 70 }}>
                      <div className="fw-semibold">{col.label}</div>
                      {col.ratio != null && <div className="text-muted small">{col.ratio}%</div>}
                    </th>
                  ))}
                  <th scope="col" className="text-center" style={{ minWidth: 70 }}>총점</th>
                  <th scope="col" className="text-center" style={{ minWidth: 90 }}>학점</th>
                  <th scope="col" style={{ minWidth: 120 }}>변경 사유</th>
                  <th scope="col" style={{ width: 90 }} />
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 ? (
                  <tr>
                    <td colSpan={sectionColumns.length + 6} className="text-center text-muted py-5">
                      조건에 맞는 수강생이 없습니다.
                    </td>
                  </tr>
                ) : (
                  filteredRows.map((row, index) => {
                    const changed = row.changed
                    return (
                      <tr key={row.enrollId || index}>
                        <td className="text-muted">#{index + 1}</td>
                        <td>
                          <div className="fw-semibold">{row.displayName}</div>
                          {(row.department || row.grade) && (
                            <div className="text-muted small">
                              {row.department}
                              {row.department && row.grade ? ' · ' : ''}
                              {row.grade}
                            </div>
                          )}
                        </td>
                        {sectionColumns.map((col) => {
                          const value = row.sections[col.code]
                          return (
                            <td key={col.code} className="text-center">
                              {typeof value === 'number' ? (
                                <span className="fw-semibold">{formatNumber(value)}</span>
                              ) : (
                                <span className="text-muted">-</span>
                              )}
                            </td>
                          )
                        })}
                        <td className="text-center">
                          {row.totalScore != null ? (
                            <span className="fw-bold">{formatNumber(row.totalScore)}</span>
                          ) : (
                            <span className="text-muted">-</span>
                          )}
                        </td>
                        <td className="text-center">
                          {(() => {
                            const finalValue = row.finalGrade != null ? row.finalGrade : null
                            const baseValue = row.autoGrade != null ? row.autoGrade : null
                            const displayedValue = finalValue ?? baseValue
                            const numberText = displayedValue != null ? formatNumber(displayedValue) : '-'
                            const gpaLabel = passFailSubject
                              ? row.gpaCd || '-'
                              : row.gpaCd
                                ? `${row.gpaCd} (${numberText})`
                                : numberText !== '-'
                                  ? numberText
                                  : '-'
                            const baselineText = changed
                              ? `변경 전 성적 ${baseValue != null ? formatNumber(baseValue) : '-'}`
                              : '변동 없음'
                            return (
                              <>
                                <div className={`fw-semibold mb-1${changed ? ' text-danger' : ''}`}>{gpaLabel}</div>
                                <div className="text-muted small">{baselineText}</div>
                              </>
                            )
                          })()}
                        </td>
                        <td>
                          <div className="small text-break">
                            {changed
                              ? row.changeReason || '사유 미기재'
                              : '변동 없음'}
                          </div>
                        </td>
                        <td className="text-end">
                          {!isFinalized && (
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-primary"
                              onClick={() => handleOpenDialog(row)}
                            >
                              변경
                            </button>
                          )}
                        </td>
                      </tr>
                    )
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    )
  }

  return (
    <>
      <div className="d-flex flex-column gap-4">
        <div className="d-flex flex-column flex-sm-row align-items-sm-center justify-content-between gap-2">
          <h1 className="h4 mb-0">최종 성적 검토/확정</h1>
        </div>

        <div className="card" style={{ boxShadow: '0 0.375rem 0.75rem rgba(0, 0, 0, 0.45)' }}>
          <div className="card-body">
            <h2 className="h6 mb-2">성적 반영 비율 안내</h2>
            <p className="mb-3 text-muted small">
              자동 산출된 성적을 기준으로 최종 성적을 확인하고, 변경 사유를 남겨 확정할 수 있습니다.
              모든 항목을 확인한 뒤 학과 방침에 따라 한 번 더 검토한 후 확정해 주세요.
            </p>
            <p className="mb-3 text-danger small">무단결석 과다 등 규정 위반 시 별도 기준에 따라 자동 F 처리될 수 있습니다.</p>
            <div className="d-flex flex-wrap align-items-center gap-2">
              {orderedRatios.length === 0 ? (
                <span className="badge bg-light text-dark border" style={{ fontSize: '1.5em', padding: '0.5em 0.75em' }}>반영 비율 정보 없음</span>
              ) : (
                orderedRatios.map((item) => (
                  <span key={item.code} className="badge bg-light text-dark border" style={{ fontSize: '1.5em', padding: '0.5em 0.75em' }}>
                    {item.label} {Number.isFinite(item.ratio) ? `${item.ratio}%` : ''}
                  </span>
                ))
              )}
            </div>
          </div>
        </div>

        {renderBody()}
      </div>
      {renderDialog()}
    </>
  )
}


