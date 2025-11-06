// Attendance.mine.jsx
import React, { useEffect, useMemo, useState } from 'react'
import { createPortal } from 'react-dom'
import { useParams, useNavigate } from 'react-router-dom'
import { useLecture } from '../../context/LectureContext'
import { Chart as ChartJS, ArcElement, CategoryScale, LinearScale, BarElement, Tooltip, Legend } from 'chart.js'
import { Doughnut, Bar } from 'react-chartjs-2'
import Swal from 'sweetalert2'
import '../../styles/attendance.css'

ChartJS.register(ArcElement, CategoryScale, LinearScale, BarElement, Tooltip, Legend)

const pad = (n) => String(n).padStart(2, '0')
const fmtDateDay = (iso) => {
  if (!iso) return '-'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return '-'
  const yy = String(d.getFullYear()).slice(-2)
  return `${yy}년 ${pad(d.getMonth() + 1)}월 ${pad(d.getDate())}일`
}

export default function Attendance() {
  const { lectureId } = useParams()
  const navigate = useNavigate()
  const { students: allStudents } = useLecture()
  const apiBase = '/classroom/api/v1/professor/attendance'

  const [rounds, setRounds] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [deleting, setDeleting] = useState(null)
  const [refreshCounter, setRefreshCounter] = useState(0)

  // 모달 상태
  const [showCreate, setShowCreate] = useState(false)
  const [showStudents, setShowStudents] = useState(false)
  const [currentRound, setCurrentRound] = useState(null)

  // 학생/체커 상태
  const [students, setStudents] = useState([])
  const [studentsLoading, setStudentsLoading] = useState(false)
  const [studentsError, setStudentsError] = useState(null)
  const [curIdx, setCurIdx] = useState(0)
  const [pending, setPending] = useState(new Map()) // enrollId -> { attStatusCd, attComment }

  // 특별 관리 학생
  const [summary, setSummary] = useState({ loading: true, error: null, data: [] })
  const [filterType, setFilterType] = useState('no') // 'no', 'late', 'early'
  const [filterCount, setFilterCount] = useState(2)

  // 목록 로딩
  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const res = await fetch(`${apiBase}/${encodeURIComponent(lectureId)}/all`, { credentials: 'include', headers: { Accept: 'application/json' } })
        if (!res.ok) throw new Error(`(${res.status}) 출석 차수 목록을 불러오지 못했습니다.`) 
        const data = await res.json()
        if (!alive) return
        if (!Array.isArray(data)) throw new Error('서버 응답 형식이 올바르지 않습니다.')
        setRounds(data)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId, refreshCounter])

  // 특별 관리 학생 데이터 로딩
  useEffect(() => {
    let alive = true
    const loadSummary = async () => {
      setSummary({ loading: true, error: null, data: [] })
      const summaryUri = `${apiBase}/${encodeURIComponent(lectureId)}/summary`
      try {
        const res = await fetch(summaryUri, { credentials: 'include', headers: { Accept: 'application/json' } })
        if (!alive) return
        if (!res.ok) throw new Error(`출석 요약 정보 로딩 실패 (${res.status})`)
        const data = await res.json()
        if (alive) setSummary({ loading: false, error: null, data: Array.isArray(data) ? data : [] })
      } catch (e) {
        if (alive) setSummary({ loading: false, error: e, data: [] })
      }
    }
    loadSummary()
    return () => { alive = false }
  }, [lectureId, refreshCounter])

  const totals = useMemo(() => {
    const sum = { total: 0, ok: 0, no: 0, early: 0, late: 0, excp: 0 }
    for (const r of rounds) {
      sum.total += r.totalCnt ?? 0
      sum.ok += r.okCnt ?? 0
      sum.no += r.noCnt ?? 0
      sum.early += r.earlyCnt ?? 0
      sum.late += r.lateCnt ?? 0
      sum.excp += r.excpCnt ?? 0
    }
    return sum
  }, [rounds])

  const specialCareStudents = useMemo(() => {
    if (!summary.data.length || !Array.isArray(allStudents)) return []
    const studentMap = new Map(allStudents.map(s => [s.enrollId, s]))
    return summary.data
      .map(item => ({ ...item, student: studentMap.get(item.enrollId) }))
      .filter(item => {
        if (!item.student) return false
        if (filterType === 'no') return item.noCnt >= filterCount
        if (filterType === 'late') return item.lateCnt >= filterCount
        if (filterType === 'early') return item.earlyCnt >= filterCount
        return false
      })
      .sort((a, b) => {
        if (filterType === 'no') return (b.noCnt || 0) - (a.noCnt || 0)
        if (filterType === 'late') return (b.lateCnt || 0) - (a.lateCnt || 0)
        if (filterType === 'early') return (b.earlyCnt || 0) - (a.earlyCnt || 0)
        return 0
      })
  }, [summary.data, allStudents, filterType, filterCount])

  const percent = (num, den) => (den > 0 ? Math.round((num / den) * 1000) / 10 : 0)

  // 라운드 삭제
  const handleDelete = async (round) => {
    if (!Number.isFinite(round)) return

    const result = await Swal.fire({
      title: '출석회차 삭제',
      html: `<strong>#${round} 차수</strong>를 삭제할까요?<br/>되돌릴 수 없습니다.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      reverseButtons: true
    })

    if (!result.isConfirmed) return

    try {
      setDeleting(round)
      const res = await fetch(`${apiBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(round)}`, { method: 'DELETE', credentials: 'include' })
      if (!res.ok) throw new Error(`(${res.status}) 삭제에 실패했습니다.`)
      const listRes = await fetch(`${apiBase}/${encodeURIComponent(lectureId)}/all`, { credentials: 'include', headers: { Accept: 'application/json' } })
      const list = await listRes.json().catch(() => [])
      setRounds(Array.isArray(list) ? list : [])

      await Swal.fire({
        title: '삭제 완료',
        text: `#${round} 차수가 삭제되었습니다.`,
        icon: 'success',
        confirmButtonText: '확인'
      })
    } catch (e) {
      alert(e.message || String(e))
    } finally {
      setDeleting(null)
    }
  }

  // 차수 생성
  const createRound = async (defaultStatus) => {
    try {
      const res = await fetch(`${apiBase}/${encodeURIComponent(lectureId)}?default_status=${encodeURIComponent(defaultStatus)}`, { method: 'POST', credentials: 'include' })
      if (!res.ok) throw new Error(`(${res.status}) 차수 생성 실패`)
      const round = await res.json()
      if (typeof round !== 'number') throw new Error('서버 응답 오류: round 값이 비정상')
      const listRes = await fetch(`${apiBase}/${encodeURIComponent(lectureId)}/all`, { credentials: 'include', headers: { Accept: 'application/json' } })
      const list = await listRes.json().catch(() => [])
      setRounds(Array.isArray(list) ? list : [])
      setShowCreate(false)

      const result = await Swal.fire({
        title: '출석회차 생성 완료',
        text: '출석체크를 진행할까요?',
        icon: 'success',
        showCancelButton: true,
        confirmButtonColor: '#0d6efd',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '출석체크 진행',
        cancelButtonText: '취소',
        reverseButtons: true
      })

      if (result.isConfirmed) {
        await openStudents(round)
      }
    } catch (e) {
      alert(e.message || String(e))
    }
  }

  // 학생 목록 로딩 & 모달 열기
  const openStudents = async (round) => {
    try {
      setStudentsLoading(true)
      setStudentsError(null)
      setShowStudents(true)
      setCurrentRound(round)
      setPending(new Map())
      setCurIdx(0)
      const res = await fetch(`${apiBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(round)}/manual`, { credentials: 'include', headers: { Accept: 'application/json' } })
      if (!res.ok) throw new Error(`(${res.status}) 학생 목록 조회 실패`)
      const list = await res.json()
      setStudents(Array.isArray(list) ? list : [])
    } catch (e) {
      setStudentsError(e)
    } finally {
      setStudentsLoading(false)
    }
  }

  const closeStudents = () => {
    setShowStudents(false)
    setCurrentRound(null)
    setStudents([])
    setPending(new Map())
    setCurIdx(0)
  }

  const setPendingFor = (enrollId, next) => {
    setPending((prev) => {
      const m = new Map(prev)
      m.set(String(enrollId), { attStatusCd: next.attStatusCd, attComment: next.attComment })
      return m
    })
  }

  const scrollToRow = (idx) => {
    setTimeout(() => {
      const row = document.querySelector(`tr[data-enroll-id="${students[idx]?.enrollId}"]`)
      if (row) {
        row.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
      }
    }, 50)
  }

  const skipStudent = () => {
    setCurIdx((i) => {
      const nextIdx = Math.min(students.length - 1, i + 1)
      scrollToRow(nextIdx)
      return nextIdx
    })
  }

  // 제출
  const submitChanges = async () => {
    try {
      if (!currentRound) {
        await Swal.fire({
          title: '오류',
          text: '차수 정보가 없습니다.',
          icon: 'error',
          confirmButtonText: '확인'
        })
        return
      }
      const payload = Array.from(pending, ([enrollId, v]) => ({ enrollId, attStatusCd: v.attStatusCd, attComment: v.attComment ?? null }))
      const res = await fetch(`${apiBase}/${encodeURIComponent(lectureId)}/${encodeURIComponent(currentRound)}/manual`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        body: JSON.stringify(payload),
      })
      if (!res.ok) throw new Error(`(${res.status}) 제출 실패`)

      await Swal.fire({
        title: '제출 완료',
        text: '출석 정보가 제출되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      })

      setRefreshCounter(c => c + 1)
      closeStudents()
    } catch (e) {
      await Swal.fire({
        title: '제출 실패',
        text: e.message || String(e),
        icon: 'error',
        confirmButtonText: '확인'
      })
    }
  }

  const sortedRounds = useMemo(() => {
    return rounds.slice().sort((a, b) => (b.lctRound || 0) - (a.lctRound || 0));
  }, [rounds]);

  return (
    <section id="attendance-root" className="container py-0" data-lecture-id={lectureId}>
      <div className="d-flex align-items-center gap-2 mb-3">
        <h1 className="h3 mb-0">출석 관리</h1>
        {/* 차수 생성 버튼 */}
        <div className="d-flex flex-wrap gap-2 align-items-center">
          <button id="btn-open-create-modal" className="btn btn-sm btn-primary py-1 px-2" onClick={() => setShowCreate(true)}>출석회차 생성</button>
        </div>
      </div>

      {/* 출석 통계 시각화 */}
      <div className="row g-4 mb-4" id="summary-cards">
        <div className="col-12 col-lg-3">
          <div className="card h-100">
            <div className="card-header">
              <h5 className="card-title mb-0">📊 출석 현황</h5>
            </div>
            <div className="card-body d-flex align-items-center justify-content-center" style={{ height: '176px', overflow: 'visible' }}>
              <div style={{ width: '100%', maxWidth: '280px', height: '152px' }}>
                <Doughnut
                  data={{
                    labels: ['출석', '결석', '지각', '조퇴', '공결'],
                    datasets: [{
                      data: [totals.ok, totals.no, totals.late, totals.early, totals.excp],
                      backgroundColor: [
                        '#28a745',
                        '#dc3545',
                        '#ffc107',
                        '#fd7e14',
                        '#17a2b8',
                      ],
                      borderWidth: 2,
                      borderColor: '#fff',
                    }],
                  }}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: {
                        position: 'right',
                        labels: {
                          padding: 12,
                          font: { size: 12 },
                          boxWidth: 14,
                          generateLabels: (chart) => {
                            const data = chart.data;
                            const total = data.datasets[0].data.reduce((a, b) => a + b, 0);
                            return data.labels.map((label, i) => {
                              const value = data.datasets[0].data[i];
                              const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
                              return {
                                text: `${label} ${value} (${percentage}%)`,
                                fillStyle: data.datasets[0].backgroundColor[i],
                                hidden: false,
                                index: i,
                              };
                            });
                          },
                        },
                      },
                      tooltip: {
                        callbacks: {
                          label: (context) => {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
                            return `${label}: ${value}명 (${percentage}%)`;
                          },
                        },
                      },
                    },
                  }}
                />
              </div>
            </div>
          </div>
        </div>
        <div className="col-12 col-lg-9">
          <div className="card h-100">
            <div className="card-header">
              <h5 className="card-title mb-0">📈 최근 출석 추이 (최근 4회)</h5>
            </div>
            <div className="card-body" style={{ height: '176px', padding: '0.25rem' }}>
              {(() => {
                const recentRounds = rounds.slice().sort((a, b) => (b.lctRound || 0) - (a.lctRound || 0)).slice(0, 4).reverse();
                const maxStudents = Math.max(...recentRounds.map(r => r.totalCnt || 0), 1);
                return (
                  <Bar
                    data={{
                      labels: recentRounds.map(r => {
                        const roundNo = r.lctPrintRound ?? r.lctRound ?? '-';
                        const attDate = r.attDay || r.startAt || r.endAt;
                        if (!attDate) return `#${roundNo}`;
                        const d = new Date(attDate);
                        if (Number.isNaN(d.getTime())) return `#${roundNo}`;
                        const mm = String(d.getMonth() + 1).padStart(2, '0');
                        const dd = String(d.getDate()).padStart(2, '0');
                        return `#${roundNo} (${mm}/${dd})`;
                      }),
                      datasets: [
                        { label: '출석', data: recentRounds.map(r => r.okCnt || 0), backgroundColor: '#28a745' },
                        { label: '결석', data: recentRounds.map(r => r.noCnt || 0), backgroundColor: '#dc3545' },
                        { label: '지각', data: recentRounds.map(r => r.lateCnt || 0), backgroundColor: '#ffc107' },
                        { label: '조퇴', data: recentRounds.map(r => r.earlyCnt || 0), backgroundColor: '#fd7e14' },
                        { label: '공결', data: recentRounds.map(r => r.excpCnt || 0), backgroundColor: '#17a2b8' },
                      ],
                    }}
                    options={{
                      indexAxis: 'y',
                      responsive: true,
                      maintainAspectRatio: false,
                      scales: { x: { stacked: true, max: maxStudents }, y: { stacked: true } },
                      plugins: {
                        legend: { display: true, position: 'bottom' },
                        tooltip: {
                          mode: 'index',
                          intersect: false,
                          callbacks: {
                            label: function(context) {
                              let label = context.dataset.label || '';
                              if (label) {
                                label += ': ';
                              }
                              if (context.parsed.x !== null) {
                                const value = context.parsed.x;
                                const percentage = maxStudents > 0 ? Math.round((value / maxStudents) * 100) : 0;
                                label += `${value}명 (${percentage}%)`;
                              }
                              return label;
                            },
                          },
                        },
                      },
                    }}
                  />
                );
              })()}
            </div>
          </div>
        </div>
      </div>

      {loading && <div className="text-muted">로딩 중…</div>}
      {error && <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>}

      {!loading && !error && (
        <section aria-label="출석회차 목록">
          <div className="row g-3">
            <div className="col-12 col-xl-8">
              <div className="card h-100">
                <div className="card-header">
                  <h5 className="card-title mb-0">출석 회차 목록</h5>
                </div>
                <div className="card-body" style={{ padding: 0 }}>
                  {rounds.length === 0 ? (
                    <div className="text-center text-muted py-4">등록된 출석회차가 없습니다.</div>
                  ) : (
                    <>
                      <div className="attendance-table-container" style={{ border: 'none', borderRadius: '0.375rem', overflow: 'hidden' }}>
                        <div className="attendance-table-scroll">
                          <table className="table table-sm align-middle mb-0" id="round-table">
                            <thead className="table-light">
                              <tr>
                                <th style={{ width: 80 }} className="text-center">출석회차</th>
                                <th style={{ width: 120 }} className="text-center">출석일시</th>
                                <th className="text-center">출석</th>
                                <th className="text-center">결석</th>
                                <th className="text-center">조퇴</th>
                                <th className="text-center">지각</th>
                                <th className="text-center">공결</th>
                                <th className="text-center">미정</th>
                                <th style={{ width: 80 }} className="text-center">삭제</th>
                              </tr>
                            </thead>
                            <tbody id="round-tbody">
                              {sortedRounds.map((r) => {
                                const roundNo = r.lctPrintRound ?? r.lctRound ?? '-'
                                const attLabel = fmtDateDay(r.attDay || r.startAt || r.endAt)
                                return (
                                  <tr key={r.lctRound ?? roundNo}>
                                    <td className="text-center">
                                      <button type="button" className="btn btn-sm btn-outline-secondary" onClick={(e) => { e.preventDefault(); openStudents(r.lctRound) }}>#{roundNo}</button>
                                    </td>
                                    <td className="small text-muted text-center">{attLabel}</td>
                                    <td className="text-center">{(r.okCnt > 0) && <span className="badge bg-success-subtle text-success border border-success-subtle">{r.okCnt}</span>}</td>
                                    <td className="text-center">{(r.noCnt > 0) && <span className="badge bg-danger-subtle text-danger border border-danger-subtle">{r.noCnt}</span>}</td>
                                    <td className="text-center">{(r.earlyCnt > 0) && <span className="badge bg-warning text-dark">{r.earlyCnt}</span>}</td>
                                    <td className="text-center">{(r.lateCnt > 0) && <span className="badge bg-warning text-dark">{r.lateCnt}</span>}</td>
                                    <td className="text-center">{(r.excpCnt > 0) && <span className="badge bg-info-subtle text-info border border-info-subtle">{r.excpCnt}</span>}</td>
                                    <td className="text-center">{(r.tbdCnt > 0) && <span className={`badge bg-secondary ${r.tbdCnt > 0 ? 'blink-tbd' : ''}`}>{r.tbdCnt}</span>}</td>
                                    <td className="text-center">
                                      <button className="btn btn-sm border-0" disabled={deleting === r.lctRound} onClick={() => handleDelete(r.lctRound)}>
                                        <span style={{ color: 'red' }}>✂️</span>
                                      </button>
                                    </td>
                                  </tr>
                                )
                              })}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </>
                  )}
                </div>
              </div>
            </div>

            <div className="col-12 col-xl-4">
              {/* 특별 관리 학생 검색 */}
              <div className="card h-100">
                <div className="card-header">
                  <h5 className="card-title mb-0">⚠️ 특별 관리 학생</h5>
                </div>
                <div className="card-body d-flex flex-column" style={{ padding: 0 }}>
                  <div className="px-2 pt-1 pb-1 border-bottom">
                    <div className="d-flex align-items-center justify-content-end gap-2">
                      <div className="btn-group flex-shrink-0" role="group">
                        <button
                          type="button"
                          className={`btn btn-sm ${filterType === 'no' ? 'btn-danger' : 'btn-outline-danger'}`}
                          onClick={() => setFilterType('no')}
                          style={{ minWidth: '50px' }}
                        >
                          결석
                        </button>
                        <button
                          type="button"
                          className={`btn btn-sm ${filterType === 'late' ? 'btn-warning text-dark' : 'btn-outline-warning'}`}
                          onClick={() => setFilterType('late')}
                          style={{ minWidth: '50px' }}
                        >
                          지각
                        </button>
                        <button
                          type="button"
                          className={`btn btn-sm ${filterType === 'early' ? 'btn-warning text-dark' : 'btn-outline-warning'}`}
                          onClick={() => setFilterType('early')}
                          style={{ minWidth: '50px' }}
                        >
                          조퇴
                        </button>
                      </div>
                      <input
                        type="text"
                        id="filter-count"
                        className="form-control form-control-sm flex-shrink-0"
                        style={{ width: 55, height: '31px' }}
                        value={filterCount}
                        onChange={(e) => {
                          const value = e.target.value.replace(/[^0-9]/g, '');
                          setFilterCount(value === '' ? 0 : Math.max(0, Number(value)));
                        }}
                      />
                      <span className="small text-muted text-nowrap">회 이상</span>
                    </div>
                  </div>

                  <div className="flex-grow-1" style={{ minHeight: '200px', overflowY: 'auto', maxHeight: '350px' }}>
                    {summary.loading ? (
                      <div className="text-center text-muted py-3">조회 중…</div>
                    ) : summary.error ? (
                      <div className="alert alert-danger mb-0 small">오류: {String(summary.error?.message)}</div>
                    ) : (
                      <div className="list-group list-group-flush">
                        {specialCareStudents.length === 0 ? (
                          <div className="text-center text-muted small py-4">조건에 해당하는 학생이 없습니다.</div>
                        ) : (
                          specialCareStudents.map(({ enrollId, student, noCnt, lateCnt, earlyCnt }) => (
                            <div key={enrollId} className="list-group-item list-group-item-action" onClick={() => navigate(`/classroom/professor/${lectureId}/student/${student.studentNo}`)} style={{ cursor: 'pointer' }}>
                              <div className="d-flex w-100 justify-content-between">
                                <h6 className="mb-1">{student.lastName}{student.firstName} <small className="text-muted">({student.studentNo})</small></h6>
                                <small>{
                                  filterType === 'no' ? `${noCnt}회 결석` :
                                  filterType === 'late' ? `${lateCnt}회 지각` :
                                  `${earlyCnt}회 조퇴`
                                }</small>
                              </div>
                              <p className="mb-1 small text-muted">{student.univDeptName}</p>
                            </div>
                          ))
                        )}
                      </div>
                    )}
                  </div>
                </div>
                <div className="card-footer text-end small text-muted">
                  총 {specialCareStudents.length} 명
                </div>
              </div>
            </div>
          </div>
        </section>
      )}

      {/* 차수 생성 모달 (Portal) */}
      {showCreate && createPortal(
        <div
          className="d-flex align-items-center justify-content-center"
          tabIndex={-1}
          role="dialog"
          aria-modal="true"
          style={{ position: 'fixed', inset: 0, zIndex: 2000, backgroundColor: 'rgba(0,0,0,0.5)' }}
          onClick={() => setShowCreate(false)}
        >
          <div className="modal-dialog modal-xl" style={{ maxHeight: '90vh' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-content rounded-4 h-100 d-flex flex-column" style={{ backgroundColor: '#fff' }}>
              <div className="modal-header border-bottom p-3">
                <h5 className="modal-title">출석회차 생성</h5>
                <button type="button" className="btn-close ms-auto" aria-label="닫기" onClick={() => setShowCreate(false)} />
              </div>
              <div className="modal-body p-4">
                <div className="row g-4 justify-content-center" style={{ maxWidth: '900px', margin: '0 auto' }}>
                  {/* 전원 출석 */}
                  <div className="col-4">
                    <div
                      role="button"
                      className="d-flex flex-column align-items-center justify-content-center border rounded-3 mode-card shadow-sm"
                      style={{
                        width: '100%',
                        aspectRatio: '1/1',
                        cursor: 'pointer',
                        transition: 'all 0.2s',
                        backgroundColor: '#f8f9fa'
                      }}
                      onClick={() => createRound('OK')}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.transform = 'translateY(-4px)';
                        e.currentTarget.style.boxShadow = '0 0.5rem 1rem rgba(0,0,0,0.15)';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.transform = 'translateY(0)';
                        e.currentTarget.style.boxShadow = '';
                      }}
                    >
                      <div style={{ fontSize: '4.5rem', marginBottom: '0.5rem', color: '#28a745', lineHeight: 1 }}>✓</div>
                      <div className="h5 mb-1 fw-semibold">전원 출석</div>
                      <div className="text-muted small">모든 인원 출석 처리</div>
                    </div>
                  </div>
                  {/* 직접 출석 */}
                  <div className="col-4">
                    <div
                      role="button"
                      className="d-flex flex-column align-items-center justify-content-center border rounded-3 mode-card shadow-sm"
                      style={{
                        width: '100%',
                        aspectRatio: '1/1',
                        cursor: 'pointer',
                        transition: 'all 0.2s',
                        backgroundColor: '#f8f9fa'
                      }}
                      onClick={() => createRound('TBD')}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.transform = 'translateY(-4px)';
                        e.currentTarget.style.boxShadow = '0 0.5rem 1rem rgba(0,0,0,0.15)';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.transform = 'translateY(0)';
                        e.currentTarget.style.boxShadow = '';
                      }}
                    >
                      <div style={{ fontSize: '4.5rem', marginBottom: '0.5rem', color: '#007bff', lineHeight: 1 }}>✎</div>
                      <div className="h5 mb-1 fw-semibold">직접 출석</div>
                      <div className="text-muted small">체커에서 개별 체크</div>
                    </div>
                  </div>
                  {/* 전원 결석 */}
                  <div className="col-4">
                    <div
                      role="button"
                      className="d-flex flex-column align-items-center justify-content-center border rounded-3 mode-card shadow-sm"
                      style={{
                        width: '100%',
                        aspectRatio: '1/1',
                        cursor: 'pointer',
                        transition: 'all 0.2s',
                        backgroundColor: '#f8f9fa'
                      }}
                      onClick={() => createRound('NO')}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.transform = 'translateY(-4px)';
                        e.currentTarget.style.boxShadow = '0 0.5rem 1rem rgba(0,0,0,0.15)';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.transform = 'translateY(0)';
                        e.currentTarget.style.boxShadow = '';
                      }}
                    >
                      <div style={{ fontSize: '4.5rem', marginBottom: '0.5rem', color: '#dc3545', lineHeight: 1 }}>✗</div>
                      <div className="h5 mb-1 fw-semibold">전원 결석</div>
                      <div className="text-muted small">모든 인원 결석 처리</div>
                    </div>
                  </div>
                </div>
              </div>
              <div className="modal-footer border-top p-3 d-flex justify-content-end gap-2">
                <button type="button" className="btn btn-outline-secondary" onClick={() => setShowCreate(false)}>취소</button>
              </div>
            </div>
          </div>
        </div>,
        document.body,
      )}

      {/* 출석 모달 (Portal) */}
      {showStudents && createPortal(
        <div
          id="studentsModal"
          className="d-flex align-items-center justify-content-center"
          tabIndex={-1}
          role="dialog"
          aria-modal="true"
          style={{ position: 'fixed', inset: 0, zIndex: 2000, backgroundColor: 'rgba(0,0,0,0.5)' }}
          onClick={closeStudents}
        >
          <div className="modal-dialog modal-xl" style={{ maxHeight: '90vh' }} onClick={(e) => e.stopPropagation()}>
            <div className="modal-content rounded-4 h-100 d-flex flex-column" style={{ backgroundColor: '#fff' }}>
              <div className="modal-header border-bottom p-3">
                <h5 className="modal-title">출석 체크 <small className="text-muted">#{currentRound}</small></h5>
                <button type="button" className="btn-close ms-auto" aria-label="닫기" onClick={closeStudents} />
              </div>
              <div className="modal-body d-flex flex-column flex-grow-1" style={{ overflow: 'hidden' }}>
                {/* 체커 패널 */}
                <div id="checker-panel" className={`card mb-0 ${students.length ? '' : 'd-none'}`}>
                  <div className="card-body">
                    <div className="row g-3 align-items-stretch">
                      <div className="col-12 col-md-3">
                        <div className="d-flex h-100 align-items-center justify-content-center">
                          <img id="chk-photo" alt="증명사진" className="d-block rounded-3" style={{ width: 135, height: 135, objectFit: 'cover', objectPosition: 'top', border: '1px solid #dee2e6' }} src={students[curIdx]?.studentNo ? `/classroom/api/v1/common/photo/student/${encodeURIComponent(lectureId)}/${encodeURIComponent(students[curIdx].studentNo)}` : undefined} />
                        </div>
                      </div>
                      <div className="col-12 col-md-9">
                        <div className="row g-3 align-items-end">
                          <div className="col-12 col-lg-5">
                            <div>
                              <div className="d-flex flex-wrap gap-3">
                                <div>
                                  <div className="small text-muted">학번</div>
                                  <div id="chk-studentNo" className="fw-semibold">{students[curIdx]?.studentNo || '-'}</div>
                                </div>
                                <div>
                                  <div className="small text-muted">이름</div>
                                  <div id="chk-name" className="fw-semibold">{`${students[curIdx]?.lastName || ''}${students[curIdx]?.firstName || ''}`}</div>
                                </div>
                              </div>
                              <div className="d-flex flex-wrap gap-3 mt-2">
                                <div>
                                  <div className="small text-muted">학과</div>
                                  <div id="chk-dept" className="fw-semibold">{students[curIdx]?.univDeptName || '-'}</div>
                                </div>
                                <div>
                                  <div className="small text-muted">학년</div>
                                  <div id="chk-grade" className="fw-semibold">{students[curIdx]?.gradeName || students[curIdx]?.grade || '-'}</div>
                                </div>
                              </div>
                            </div>
                          </div>
                          <div className="col-12 col-lg-7">
                            <div className="row g-2">
                              <div className="col-12">
                                <label htmlFor="chk-comment" className="form-label small text-muted mb-1">비고</label>
                                <input
                                  id="chk-comment"
                                  type="text"
                                  className="form-control form-control-sm"
                                  placeholder="메모"
                                  value={(pending.get(String(students[curIdx]?.enrollId))?.attComment) || students[curIdx]?.attComment || ''}
                                  onChange={(e) => {
                                    const s = students[curIdx]; if (!s) return
                                    const p = pending.get(String(s.enrollId)) || {}
                                    setPendingFor(s.enrollId, { attStatusCd: p.attStatusCd || s.attStatusCd || 'ATTD_TBD', attComment: e.target.value })
                                  }}
                                />
                              </div>
                              <div className="col-12">
                                <div className="d-flex flex-nowrap gap-1">
                                  <button type="button" className="btn btn-outline-secondary btn-sm text-nowrap" onClick={() => { const s=students[curIdx]; if(!s) return; const cm = (pending.get(String(s.enrollId))?.attComment) || s.attComment || ''; setPendingFor(s.enrollId, { attStatusCd: 'ATTD_TBD', attComment: cm }); skipStudent(); }}>미정</button>
                                  <button type="button" className="btn btn-success btn-sm text-nowrap" onClick={() => { const s=students[curIdx]; if(!s) return; const cm = (pending.get(String(s.enrollId))?.attComment) || s.attComment || ''; setPendingFor(s.enrollId, { attStatusCd: 'ATTD_OK', attComment: cm }); skipStudent(); }}>출석</button>
                                  <button type="button" className="btn btn-danger btn-sm text-nowrap" onClick={() => { const s=students[curIdx]; if(!s) return; const cm = (pending.get(String(s.enrollId))?.attComment) || s.attComment || ''; setPendingFor(s.enrollId, { attStatusCd: 'ATTD_NO', attComment: cm }); skipStudent(); }}>결석</button>
                                  <button type="button" className="btn btn-warning btn-sm text-dark text-nowrap" onClick={() => { const s=students[curIdx]; if(!s) return; const cm = (pending.get(String(s.enrollId))?.attComment) || s.attComment || ''; setPendingFor(s.enrollId, { attStatusCd: 'ATTD_EARLY', attComment: cm }); skipStudent(); }}>조퇴</button>
                                  <button type="button" className="btn btn-warning btn-sm text-dark text-nowrap" onClick={() => { const s=students[curIdx]; if(!s) return; const cm = (pending.get(String(s.enrollId))?.attComment) || s.attComment || ''; setPendingFor(s.enrollId, { attStatusCd: 'ATTD_LATE', attComment: cm }); skipStudent(); }}>지각</button>
                                  <button type="button" className="btn btn-info btn-sm text-nowrap" onClick={() => { const s=students[curIdx]; if(!s) return; const cm = (pending.get(String(s.enrollId))?.attComment) || s.attComment || ''; setPendingFor(s.enrollId, { attStatusCd: 'ATTD_EXCP', attComment: cm }); skipStudent(); }}>공결</button>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* 명단 테이블 */}
                  <div className="table-responsive flex-grow-1" style={{ maxHeight: '45vh', overflow: 'auto' }}>
                    <table className="table table-sm table-bordered text-center align-middle mb-0">
                      <thead className="table-light" style={{ position: 'sticky', top: '-1px', zIndex: 10 }}>
                        <tr>
                          <th style={{ width: '64px', paddingTop: '12px', paddingBottom: '12px' }}>#</th>
                          <th style={{ width: '120px', paddingTop: '12px', paddingBottom: '12px' }}>학번</th>
                          <th style={{ width: '80px', paddingTop: '12px', paddingBottom: '12px' }}>이름</th>
                          <th style={{ width: '60px', paddingTop: '12px', paddingBottom: '12px' }}>학년</th>
                          <th style={{ minWidth: '120px', paddingTop: '12px', paddingBottom: '12px' }}>학과</th>
                          <th style={{ width: '80px', paddingTop: '12px', paddingBottom: '12px' }}>출결</th>
                          <th style={{ minWidth: '200px', paddingTop: '12px', paddingBottom: '12px' }}>비고</th>
                        </tr>
                      </thead>
                      <tbody id="students-tbody">
                        {studentsLoading ? (
                          <tr><td colSpan={7} className="text-center text-muted">불러오는 중</td></tr>
                        ) : studentsError ? (
                          <tr><td colSpan={7} className="text-center text-danger">학생 정보를 불러오지 못했습니다.</td></tr>
                        ) : students.length === 0 ? (
                          <tr><td colSpan={7} className="text-center text-muted">출석 대상 학생이 없습니다.</td></tr>
                        ) : (
                          students.map((s, idx) => {
                            const p = pending.get(String(s.enrollId)) || {}
                            const st = p.attStatusCd || s.attStatusCd || 'ATTD_TBD'
                            const cm = p.attComment || s.attComment || ''
                            return (
                              <tr key={s.enrollId} data-enroll-id={s.enrollId} className={idx === curIdx ? 'table-active' : ''} onClick={() => setCurIdx(idx)} style={{ cursor: 'pointer' }}>
                                <td>
                                  <button className="btn btn-sm btn-outline-secondary" onClick={(e) => { e.preventDefault(); setCurIdx(idx) }}>#{idx + 1}</button>
                                </td>
                                <td className="text-center">{s.studentNo}</td>
                                <td className="text-center">{`${s.lastName || ''}${s.firstName || ''}`}</td>
                                <td className="text-center">{s.gradeName || s.grade || '-'}</td>
                                <td className="text-center">{s.univDeptName || '-'}</td>
                                <td>
                                  {(() => {
                                    const map = {
                                      ATTD_TBD: { cls: 'badge bg-secondary status-pill', label: '미정' },
                                      ATTD_OK: { cls: 'badge bg-success status-pill', label: '출석' },
                                      ATTD_NO: { cls: 'badge bg-danger status-pill', label: '결석' },
                                      ATTD_EARLY: { cls: 'badge bg-warning text-dark status-pill', label: '조퇴' },
                                      ATTD_LATE: { cls: 'badge bg-warning text-dark status-pill', label: '지각' },
                                      ATTD_EXCP: { cls: 'badge bg-info status-pill', label: '공결' },
                                    }
                                    const v = map[st] || map.ATTD_TBD
                                    return <span className={v.cls}>{v.label}</span>
                                  })()}
                                </td>
                                <td>
                                  <input className="form-control form-control-sm" value={cm} onChange={(e) => setPendingFor(s.enrollId, { attStatusCd: st, attComment: e.target.value }) } />
                                </td>
                              </tr>
                            )
                          })
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>
                <div className="modal-footer border-top p-3 d-flex justify-content-end gap-2">
                  <button type="button" className="attendance-btn attendance-btn-default" onClick={closeStudents}>닫기</button>
                  <button id="btn-submit-changes" type="button" className="attendance-btn attendance-btn-save" onClick={submitChanges}>제출</button>
                </div>
              </div>
            </div>
          </div>
        </div>,
        document.body,
      )}
    </section>
  )
}
