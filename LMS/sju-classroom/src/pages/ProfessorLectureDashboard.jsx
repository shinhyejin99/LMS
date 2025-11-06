import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend } from 'chart.js'
import { Bar, Doughnut } from 'react-chartjs-2'
import { fetchLectureStudents } from '../lib/api/student'

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend)

export default function ProfessorLectureDashboard() {
  const { lectureId } = useParams()
  const [students, setStudents] = useState([])
  const [attendanceStats, setAttendanceStats] = useState(null)
  const [taskStats, setTaskStats] = useState(null)
  const [examStats, setExamStats] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let alive = true
    ;(async () => {
      try {
        setLoading(true)
        setError(null)
        const base = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/statis`
        const [studentsData, attendanceRes, taskRes, examRes] = await Promise.all([
          fetchLectureStudents(lectureId),
          fetch(`${base}/attendance`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/task`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/exam`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
        ])
        if (!attendanceRes.ok) throw new Error(`(${attendanceRes.status}) 출석 통계를 불러오지 못했습니다.`)
        if (!taskRes.ok) throw new Error(`(${taskRes.status}) 과제 통계를 불러오지 못했습니다.`)
        if (!examRes.ok) throw new Error(`(${examRes.status}) 시험 통계를 불러오지 못했습니다.`)

        const attendanceData = await attendanceRes.json()
        const taskData = await taskRes.json()
        const examData = await examRes.json()

        if (!alive) return
        setStudents(studentsData)
        setAttendanceStats(attendanceData)
        setTaskStats(taskData)
        setExamStats(examData)
      } catch (e) {
        if (!alive) return
        setError(e)
      } finally {
        if (alive) setLoading(false)
      }
    })()
    return () => { alive = false }
  }, [lectureId])

  // Bootstrap Popover 초기화
  useEffect(() => {
    if (!loading && !error && attendanceStats && students.length > 0) {
      // Bootstrap이 로드되어 있는지 확인
      if (typeof window.bootstrap !== 'undefined') {
        const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]')
        const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new window.bootstrap.Popover(popoverTriggerEl))

        return () => {
          popoverList.forEach(popover => popover.dispose())
        }
      }
    }
  }, [loading, error, attendanceStats, students])

  // 수강생 통계 계산 (students 배열에서 직접 계산)
  const enrollStats = students.length > 0 ? (() => {
    const totalCnt = students.length

    // 중탈: 수강철회(ENR_CANCEL), 수강포기(ENR_WITHDRAW)만 포함
    const dropoutCnt = students.filter(s =>
      s.enrollStatusCd === 'ENR_CANCEL' || s.enrollStatusCd === 'ENR_WITHDRAW'
    ).length

    // 수강: 수강중(ENR_ING) + 수강완료(ENR_DONE) - 정상적으로 수강하는/한 학생
    const activeCnt = students.filter(s =>
      s.enrollStatusCd === 'ENR_ING' || s.enrollStatusCd === 'ENR_DONE'
    ).length

    // 학년별 집계 (수강중 + 수강완료만)
    const gradeMap = {}
    students.forEach(s => {
      if (s.enrollStatusCd === 'ENR_ING' || s.enrollStatusCd === 'ENR_DONE') {
        gradeMap[s.gradeCd] = (gradeMap[s.gradeCd] || 0) + 1
      }
    })
    const byGrade = Object.entries(gradeMap).map(([gradeCd, cnt]) => ({ gradeCd, cnt }))

    // 학과별 집계 (수강중 + 수강완료만, 학과명 포함)
    const deptMap = {}
    students.forEach(s => {
      if (s.enrollStatusCd === 'ENR_ING' || s.enrollStatusCd === 'ENR_DONE') {
        const key = s.univDeptCd
        if (!deptMap[key]) {
          deptMap[key] = { univDeptCd: s.univDeptCd, univDeptName: s.univDeptName, cnt: 0 }
        }
        deptMap[key].cnt++
      }
    })
    const byDept = Object.values(deptMap)

    return {
      stats: { totalCnt, activeCnt, dropoutCnt },
      byGrade,
      byDept
    }
  })() : null

  // 학년 분포 도넛 차트
  const gradeChartData = enrollStats?.byGrade ? {
    labels: enrollStats.byGrade.map(g => {
      const gradeMap = { '1ST': '1학년', '2ND': '2학년', '3RD': '3학년', '4TH': '4학년' }
      return gradeMap[g.gradeCd] || g.gradeCd
    }),
    datasets: [{
      data: enrollStats.byGrade.map(g => g.cnt),
      backgroundColor: [
        'rgba(54, 162, 235, 0.8)',
        'rgba(255, 99, 132, 0.8)',
        'rgba(255, 206, 86, 0.8)',
        'rgba(75, 192, 192, 0.8)',
      ],
    }],
  } : null

  // 학과 분포 도넛 차트
  const deptChartData = enrollStats?.byDept ? {
    labels: enrollStats.byDept.map(d => d.univDeptName || d.univDeptCd),
    datasets: [{
      data: enrollStats.byDept.map(d => d.cnt),
      backgroundColor: [
        'rgba(153, 102, 255, 0.8)',
        'rgba(255, 159, 64, 0.8)',
        'rgba(201, 203, 207, 0.8)',
        'rgba(75, 192, 192, 0.8)',
      ],
    }],
  } : null

  // 출석 현황 계산
  const attendanceMetrics = attendanceStats?.typeCount ? (() => {
    const { totalCnt, tbdCnt, okCnt, noCnt, earlyCnt, lateCnt, excpCnt } = attendanceStats.typeCount
    const attendedCnt = okCnt + earlyCnt + lateCnt + excpCnt
    const avgRate = totalCnt > 0 ? ((attendedCnt / totalCnt) * 100).toFixed(1) : 0

    // 학생별 출석률 계산
    const studentRates = (attendanceStats.attCountList || []).map(student => {
      const rate = student.recordedCnt > 0 ? (student.nonAbsentCnt / student.recordedCnt) * 100 : 0
      const studentInfo = students.find(s => s.enrollId === student.enrollId)
      return {
        ...student,
        rate,
        name: studentInfo ? `${studentInfo.lastName}${studentInfo.firstName}` : student.enrollId
      }
    })

    const excellentList = studentRates.filter(s => s.rate >= 90)
    const goodList = studentRates.filter(s => s.rate >= 70 && s.rate < 90)
    const warningList = studentRates.filter(s => s.rate < 70)

    return {
      avgRate,
      excellent: excellentList.length,
      good: goodList.length,
      warning: warningList.length,
      excellentList,
      goodList,
      warningList,
      tbdCnt
    }
  })() : null

  // 과제 현황 계산
  const taskMetrics = taskStats ? (() => {
    const { taskTypeCount, ongoingIndivtask, ongoingGrouptask, closedIndivtask, closedGrouptask, scoresByStudentAndTask } = taskStats

    const indivCnt = taskTypeCount?.indivCnt || 0
    const groupCnt = taskTypeCount?.groupCnt || 0

    const ongoingCount = (ongoingIndivtask?.ongoingTaskCnt || 0) + (ongoingGrouptask?.ongoingTaskCnt || 0)
    const ongoingSubmissionRate = ongoingIndivtask?.submitRatePct || 0

    const closedCount = (closedIndivtask?.closedTaskCnt || 0) + (closedGrouptask?.closedTaskCnt || 0)
    const closedSubmissionRate = closedIndivtask?.submitRatePct || 0

    // 학생별 평균 점수 계산 (scoresByStudentAndTask에서)
    const studentScores = {}
    if (scoresByStudentAndTask && Array.isArray(scoresByStudentAndTask)) {
      scoresByStudentAndTask.forEach(item => {
        if (item.score !== null && item.score !== undefined) {
          if (!studentScores[item.enrollId]) {
            studentScores[item.enrollId] = { scores: [], total: 0, count: 0 }
          }
          studentScores[item.enrollId].scores.push(item.score)
          studentScores[item.enrollId].total += item.score
          studentScores[item.enrollId].count++
        }
      })
    }

    // 평균 점수 계산 및 학생 정보 매핑
    const studentAvgScores = Object.entries(studentScores).map(([enrollId, data]) => {
      const student = students.find(s => s.enrollId === enrollId)
      return {
        enrollId,
        name: student ? `${student.lastName}${student.firstName}` : enrollId,
        avgScore: data.count > 0 ? (data.total / data.count).toFixed(1) : 0
      }
    }).filter(s => s.avgScore > 0)

    // Top 3 / 낮은 3명
    const sortedByScore = [...studentAvgScores].sort((a, b) => b.avgScore - a.avgScore)
    const topStudents = sortedByScore.slice(0, 3)
    const bottomStudents = sortedByScore.slice(-3).reverse()

    // 점수 분포 계산 (10점 단위)
    const scoreDistribution = Array(10).fill(0).map((_, i) => ({ range: `${i * 10}-${(i + 1) * 10}`, count: 0 }))
    studentAvgScores.forEach(s => {
      const score = parseFloat(s.avgScore)
      const idx = Math.min(Math.floor(score / 10), 9)
      scoreDistribution[idx].count++
    })

    return {
      indivCnt,
      groupCnt,
      ongoingCount,
      ongoingSubmissionRate,
      closedCount,
      closedSubmissionRate,
      scoreDistribution,
      topStudents,
      bottomStudents
    }
  })() : null

  // 시험 현황 계산
  const examMetrics = examStats ? (() => {
    const { examTypeCount, ongoingExam, closedExam, scoresByStudentAndExam } = examStats

    const totalCnt = examTypeCount?.totalCnt || 0
    const onlineCnt = examTypeCount?.onlineCnt || 0
    const offlineCnt = examTypeCount?.offlineCnt || 0

    const ongoingCount = ongoingExam?.ongoingExamCnt || 0
    const ongoingSubmissionRate = ongoingExam?.submitRatePct || 0

    const closedCount = closedExam?.closedExamCnt || 0
    const closedSubmissionRate = closedExam?.submitRatePct || 0

    // 학생별 평균 점수 계산 (scoresByStudentAndExam에서)
    const studentScores = {}
    if (scoresByStudentAndExam && Array.isArray(scoresByStudentAndExam)) {
      scoresByStudentAndExam.forEach(item => {
        if (item.score !== null && item.score !== undefined) {
          if (!studentScores[item.enrollId]) {
            studentScores[item.enrollId] = { scores: [], total: 0, count: 0 }
          }
          studentScores[item.enrollId].scores.push(item.score)
          studentScores[item.enrollId].total += item.score
          studentScores[item.enrollId].count++
        }
      })
    }

    // 평균 점수 계산 및 학생 정보 매핑
    const studentAvgScores = Object.entries(studentScores).map(([enrollId, data]) => {
      const student = students.find(s => s.enrollId === enrollId)
      return {
        enrollId,
        name: student ? `${student.lastName}${student.firstName}` : enrollId,
        avgScore: data.count > 0 ? (data.total / data.count).toFixed(1) : 0
      }
    }).filter(s => s.avgScore > 0)

    // Top 3 / 낮은 3명
    const sortedByScore = [...studentAvgScores].sort((a, b) => b.avgScore - a.avgScore)
    const topStudents = sortedByScore.slice(0, 3)
    const bottomStudents = sortedByScore.slice(-3).reverse()

    // 점수 분포 계산 (10점 단위)
    const scoreDistribution = Array(10).fill(0).map((_, i) => ({ range: `${i * 10}-${(i + 1) * 10}`, count: 0 }))
    studentAvgScores.forEach(s => {
      const score = parseFloat(s.avgScore)
      const idx = Math.min(Math.floor(score / 10), 9)
      scoreDistribution[idx].count++
    })

    return {
      totalCnt,
      onlineCnt,
      offlineCnt,
      ongoingCount,
      ongoingSubmissionRate,
      closedCount,
      closedSubmissionRate,
      scoreDistribution,
      topStudents,
      bottomStudents
    }
  })() : null

  const pieOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom',
        labels: { font: { size: 11 }, padding: 8, boxWidth: 12 },
      },
    },
  }

  const barOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: { beginAtZero: true, ticks: { stepSize: 1 } },
    },
    plugins: {
      legend: { display: false },
    },
  }

  // 과제 점수 분포 막대 차트
  const taskScoreChartData = taskMetrics?.scoreDistribution ? {
    labels: taskMetrics.scoreDistribution.map(s => s.range),
    datasets: [{
      label: '학생 수',
      data: taskMetrics.scoreDistribution.map(s => s.count),
      backgroundColor: 'rgba(54, 162, 235, 0.7)',
    }],
  } : null

  // 시험 점수 분포 막대 차트
  const examScoreChartData = examMetrics?.scoreDistribution ? {
    labels: examMetrics.scoreDistribution.map(s => s.range),
    datasets: [{
      label: '학생 수',
      data: examMetrics.scoreDistribution.map(s => s.count),
      backgroundColor: 'rgba(255, 99, 132, 0.7)',
    }],
  } : null

  return (
    <section id="dashboard-root" className="container py-0" data-lecture-id={lectureId}>
      <style>{`
        @keyframes blink-slow {
          0%, 100% { opacity: 1; }
          50% { opacity: 0.3; }
        }
        .blink-slow {
          animation: blink-slow 2s ease-in-out infinite;
        }
      `}</style>

      {loading && <div className="text-muted">불러오는 중…</div>}
      {error && <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>}

      {!loading && !error && (
        <div className="row g-3">
          {/* 1. 수강생 현황 */}
          <div className="col-lg-6">
            <div className="card shadow-sm h-100">
              <div className="card-header py-2">
                <h2 className="h6 mb-0">수강생 현황</h2>
              </div>
              <div className="card-body pb-2">
                {enrollStats && (
                  <div className="row">
                    <div className="col-5">
                      <div className="text-center mb-2 small fw-semibold">학년 분포</div>
                      {gradeChartData && (
                        <div style={{ height: '140px', position: 'relative' }}>
                          <Doughnut data={gradeChartData} options={pieOptions} />
                        </div>
                      )}
                    </div>
                    <div className="col-5">
                      <div className="text-center mb-2 small fw-semibold">학과 분포</div>
                      {deptChartData && (
                        <div style={{ height: '140px', position: 'relative' }}>
                          <Doughnut data={deptChartData} options={pieOptions} />
                        </div>
                      )}
                    </div>
                    <div className="col-2 d-flex flex-column justify-content-center gap-3">
                      <div className="text-center">
                        <div className="text-muted small">수강</div>
                        <div className="h5 mb-0 text-success">{enrollStats.stats?.activeCnt || 0}</div>
                      </div>
                      <div className="text-center">
                        <div className="text-muted small">중탈</div>
                        <div className="h5 mb-0 text-danger">{enrollStats.stats?.dropoutCnt || 0}</div>
                      </div>
                      <div className="text-center">
                        <div className="text-muted small">총합</div>
                        <div className="h5 mb-0">{enrollStats.stats?.totalCnt || 0}</div>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* 2. 출석 현황 */}
          <div className="col-lg-6">
            <div className="card shadow-sm h-100">
              <div className="card-header d-flex align-items-center justify-content-between py-2">
                <h2 className="h6 mb-0">출석 현황</h2>
                {attendanceMetrics && attendanceMetrics.tbdCnt > 0 && (
                  <div className="text-danger small blink-slow">
                    미정 상태가 {attendanceMetrics.tbdCnt}건 존재합니다
                  </div>
                )}
              </div>
              <div className="card-body pb-2">
                {attendanceMetrics ? (
                  <>
                    <div className="text-center mb-3">
                      <div className="text-muted small">평균 출석률</div>
                      <div className="display-6 fw-bold text-primary">{attendanceMetrics.avgRate}%</div>
                    </div>
                    <div className="row g-2 text-center">
                      <div className="col-4">
                        <div
                          className="p-2 bg-success bg-opacity-10 rounded"
                          style={{ cursor: attendanceMetrics.excellentList.length > 0 ? 'pointer' : 'default' }}
                          data-bs-toggle={attendanceMetrics.excellentList.length > 0 ? 'popover' : ''}
                          data-bs-trigger="hover"
                          data-bs-placement="bottom"
                          data-bs-html="true"
                          data-bs-content={attendanceMetrics.excellentList.length > 0 ? attendanceMetrics.excellentList.map(s => `${s.name} (${s.rate.toFixed(1)}%)`).join('<br>') : ''}
                          title="90% 이상 출석 학생"
                        >
                          <div className="small text-muted">90% 이상</div>
                          <div className="h5 mb-0 text-success">{attendanceMetrics.excellent}명</div>
                        </div>
                      </div>
                      <div className="col-4">
                        <div
                          className="p-2 bg-warning bg-opacity-10 rounded"
                          style={{ cursor: attendanceMetrics.goodList.length > 0 ? 'pointer' : 'default' }}
                          data-bs-toggle={attendanceMetrics.goodList.length > 0 ? 'popover' : ''}
                          data-bs-trigger="hover"
                          data-bs-placement="bottom"
                          data-bs-html="true"
                          data-bs-content={attendanceMetrics.goodList.length > 0 ? attendanceMetrics.goodList.map(s => `${s.name} (${s.rate.toFixed(1)}%)`).join('<br>') : ''}
                          title="70~90% 출석 학생"
                        >
                          <div className="small text-muted">70~90%</div>
                          <div className="h5 mb-0 text-warning">{attendanceMetrics.good}명</div>
                        </div>
                      </div>
                      <div className="col-4">
                        <div
                          className="p-2 bg-danger bg-opacity-10 rounded"
                          style={{ cursor: attendanceMetrics.warningList.length > 0 ? 'pointer' : 'default' }}
                          data-bs-toggle={attendanceMetrics.warningList.length > 0 ? 'popover' : ''}
                          data-bs-trigger="hover"
                          data-bs-placement="bottom"
                          data-bs-html="true"
                          data-bs-content={attendanceMetrics.warningList.length > 0 ? attendanceMetrics.warningList.map(s => `${s.name} (${s.rate.toFixed(1)}%)`).join('<br>') : ''}
                          title="70% 이하 출석 학생"
                        >
                          <div className="small text-muted">70% 이하</div>
                          <div className="h5 mb-0 text-danger">{attendanceMetrics.warning}명</div>
                        </div>
                      </div>
                    </div>
                  </>
                ) : (
                  <div className="text-muted">출석 데이터가 없습니다.</div>
                )}
              </div>
            </div>
          </div>

          {/* 3. 과제 현황 */}
          <div className="col-lg-6">
            <div className="card shadow-sm h-100">
              <div className="card-header py-2">
                <h2 className="h6 mb-0">과제 현황</h2>
              </div>
              <div className="card-body pb-2">
                {taskMetrics ? (
                  <>
                    <div className="row g-2 mb-3 text-center">
                      <div className="col-3">
                        <div className="p-2 bg-primary bg-opacity-10 rounded h-100 d-flex flex-column justify-content-center">
                          <div className="small text-muted">개인과제</div>
                          <div className="h6 mb-0 text-primary">{taskMetrics.indivCnt}개</div>
                          <div className="small" style={{ visibility: 'hidden' }}>-</div>
                        </div>
                      </div>
                      <div className="col-3">
                        <div className="p-2 bg-info bg-opacity-10 rounded h-100 d-flex flex-column justify-content-center">
                          <div className="small text-muted">조별과제</div>
                          <div className="h6 mb-0 text-info">{taskMetrics.groupCnt}개</div>
                          <div className="small" style={{ visibility: 'hidden' }}>-</div>
                        </div>
                      </div>
                      <div className="col-3">
                        <div className="p-2 bg-warning bg-opacity-10 rounded h-100 d-flex flex-column justify-content-center">
                          <div className="small text-muted">진행중인 과제</div>
                          <div className="h6 mb-0 text-warning">{taskMetrics.ongoingCount}개</div>
                          <div className="small text-muted">제출률 {taskMetrics.ongoingSubmissionRate.toFixed(1)}%</div>
                        </div>
                      </div>
                      <div className="col-3">
                        <div className="p-2 bg-success bg-opacity-10 rounded h-100 d-flex flex-column justify-content-center">
                          <div className="small text-muted">마감된 과제</div>
                          <div className="h6 mb-0 text-success">{taskMetrics.closedCount}개</div>
                          <div className="small text-muted">제출률 {taskMetrics.closedSubmissionRate.toFixed(1)}%</div>
                        </div>
                      </div>
                    </div>
                    {taskScoreChartData && (
                      <div className="mb-3">
                        <div className="small fw-semibold mb-2">과제 평균 점수 분포</div>
                        <div style={{ height: '120px', position: 'relative' }}>
                          <Bar data={taskScoreChartData} options={barOptions} />
                        </div>
                      </div>
                    )}
                    {taskMetrics.topStudents.length > 0 && (
                      <div className="row">
                        <div className="col-6">
                          <div className="small fw-semibold mb-2">Top 3</div>
                          <table className="table table-sm table-borderless mb-0">
                            <tbody>
                              {taskMetrics.topStudents.map((student, idx) => (
                                <tr key={idx}>
                                  <td className="text-muted" style={{ width: '25px' }}>#{idx + 1}</td>
                                  <td className="small">{student.name}</td>
                                  <td className="text-end fw-semibold small">{student.avgScore}</td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                        <div className="col-6">
                          <div className="small fw-semibold mb-2">낮은 3명</div>
                          <table className="table table-sm table-borderless mb-0">
                            <tbody>
                              {taskMetrics.bottomStudents.map((student, idx) => (
                                <tr key={idx}>
                                  <td className="text-muted" style={{ width: '25px' }}>#{idx + 1}</td>
                                  <td className="small">{student.name}</td>
                                  <td className="text-end fw-semibold small text-danger">{student.avgScore}</td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    )}
                  </>
                ) : (
                  <div className="text-muted">과제 데이터가 없습니다.</div>
                )}
              </div>
            </div>
          </div>

          {/* 4. 시험 현황 */}
          <div className="col-lg-6">
            <div className="card shadow-sm h-100">
              <div className="card-header py-2">
                <h2 className="h6 mb-0">시험 현황</h2>
              </div>
              <div className="card-body pb-2">
                {examMetrics ? (
                  <>
                    <div className="row g-2 mb-3 text-center">
                      <div className="col-3">
                        <div className="p-2 bg-primary bg-opacity-10 rounded h-100 d-flex flex-column justify-content-center">
                          <div className="small text-muted">온라인시험</div>
                          <div className="h6 mb-0 text-primary">{examMetrics.onlineCnt}개</div>
                          <div className="small" style={{ visibility: 'hidden' }}>-</div>
                        </div>
                      </div>
                      <div className="col-3">
                        <div className="p-2 bg-info bg-opacity-10 rounded h-100 d-flex flex-column justify-content-center">
                          <div className="small text-muted">오프라인시험</div>
                          <div className="h6 mb-0 text-info">{examMetrics.offlineCnt}개</div>
                          <div className="small" style={{ visibility: 'hidden' }}>-</div>
                        </div>
                      </div>
                      <div className="col-3">
                        <div className="p-2 bg-warning bg-opacity-10 rounded h-100 d-flex flex-column justify-content-center">
                          <div className="small text-muted">진행중인 시험</div>
                          <div className="h6 mb-0 text-warning">{examMetrics.ongoingCount}개</div>
                          <div className="small text-muted">제출률 {examMetrics.ongoingSubmissionRate !== null ? examMetrics.ongoingSubmissionRate.toFixed(1) : 0}%</div>
                        </div>
                      </div>
                      <div className="col-3">
                        <div className="p-2 bg-success bg-opacity-10 rounded h-100 d-flex flex-column justify-content-center">
                          <div className="small text-muted">마감된 시험</div>
                          <div className="h6 mb-0 text-success">{examMetrics.closedCount}개</div>
                          <div className="small text-muted">제출률 {examMetrics.closedSubmissionRate !== null ? examMetrics.closedSubmissionRate.toFixed(1) : 0}%</div>
                        </div>
                      </div>
                    </div>
                    {examScoreChartData && (
                      <div className="mb-3">
                        <div className="small fw-semibold mb-2">시험 평균 점수 분포</div>
                        <div style={{ height: '120px', position: 'relative' }}>
                          <Bar data={examScoreChartData} options={barOptions} />
                        </div>
                      </div>
                    )}
                    {examMetrics.topStudents.length > 0 && (
                      <div className="row">
                        <div className="col-6">
                          <div className="small fw-semibold mb-2">Top 3</div>
                          <table className="table table-sm table-borderless mb-0">
                            <tbody>
                              {examMetrics.topStudents.map((student, idx) => (
                                <tr key={idx}>
                                  <td className="text-muted" style={{ width: '25px' }}>#{idx + 1}</td>
                                  <td className="small">{student.name}</td>
                                  <td className="text-end fw-semibold small">{student.avgScore}</td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                        <div className="col-6">
                          <div className="small fw-semibold mb-2">낮은 3명</div>
                          <table className="table table-sm table-borderless mb-0">
                            <tbody>
                              {examMetrics.bottomStudents.map((student, idx) => (
                                <tr key={idx}>
                                  <td className="text-muted" style={{ width: '25px' }}>#{idx + 1}</td>
                                  <td className="small">{student.name}</td>
                                  <td className="text-end fw-semibold small text-danger">{student.avgScore}</td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    )}
                  </>
                ) : (
                  <div className="text-muted">시험 데이터가 없습니다.</div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}
