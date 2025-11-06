import React, { useState, useEffect, useMemo } from 'react';
import { useParams } from 'react-router-dom';

const StudentAttendance = () => {
  const { lectureId } = useParams();
  const [attendanceData, setAttendanceData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const contextPath = ''; // Assuming contextPath is empty based on previous fix
  const apiBase = `${contextPath}/classroom/api/v1/student`;

  useEffect(() => {
    const fetchAttendance = async () => {
      setLoading(true);
      try {
        const response = await fetch(`${apiBase}/${lectureId}/attendance`);
        if (!response.ok) {
          throw new Error('Failed to fetch attendance data');
        }
        const data = await response.json();

        const totalLectures = data.length;
        let attended = 0;
        let tardy = 0;
        let earlyLeave = 0;
        let absent = 0;
        let excused = 0;

        data.forEach(record => {
          if (record.attStatusCd === 'ATTD_OK') {
            attended++;
          } else if (record.attStatusCd === 'ATTD_LATE') {
            tardy++;
          } else if (record.attStatusCd === 'ATTD_EARLY') {
            earlyLeave++;
          } else if (record.attStatusCd === 'ATTD_NO') {
            absent++;
          } else if (record.attStatusCd === 'ATTD_EXCUSE') {
            excused++;
          }
        });

        setAttendanceData({
          summary: { totalLectures, attended, tardy, earlyLeave, absent, excused },
          details: data,
        });
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchAttendance();
  }, [lectureId, apiBase]);

  // 강의일 포맷: "07월\n28일"
  const formatDateSplit = (dateStr) => {
    if (!dateStr) return { month: '-', day: '' };
    const parts = dateStr.split('-');
    if (parts.length === 3) {
      return { month: `${parts[1]}월`, day: `${parts[2]}일` };
    }
    return { month: dateStr, day: '' };
  };

  // 15개씩 청크로 나누기
  const chunkDetails = (details) => {
    const chunks = [];
    for (let i = 0; i < details.length; i += 15) {
      chunks.push(details.slice(i, i + 15));
    }
    return chunks;
  };

  const getStatusColor = (status) => {
    switch (status) {
      case '출석': return 'success';
      case '지각': return 'warning';
      case '조퇴': return 'warning';
      case '공결': return 'info';
      case '결석': return 'danger';
      default: return 'secondary';
    }
  };

  const getStatusCustomColor = (status) => {
    if (status === '지각' || status === '조퇴') return '#fd7e14';
    return null;
  };

  const renderAttendanceSummary = () => {
    if (!attendanceData || !attendanceData.summary) return null;

    const { totalLectures, attended, tardy, earlyLeave, absent, excused } = attendanceData.summary;
    const attendanceRate = totalLectures > 0 ? ((attended / totalLectures) * 100).toFixed(1) : 0;
    const tardyRate = totalLectures > 0 ? ((tardy / totalLectures) * 100).toFixed(1) : 0;
    const earlyLeaveRate = totalLectures > 0 ? ((earlyLeave / totalLectures) * 100).toFixed(1) : 0;
    const absentRate = totalLectures > 0 ? ((absent / totalLectures) * 100).toFixed(1) : 0;
    const excusedRate = totalLectures > 0 ? ((excused / totalLectures) * 100).toFixed(1) : 0;

    const summaryItems = [
      { label: '총 강의', value: `${totalLectures}회`, color: 'primary' },
      { label: '출석', value: `${attended}회`, color: 'success' },
      { label: '지각', value: `${tardy}회`, color: 'warning' },
      { label: '조퇴', value: `${earlyLeave}회`, color: 'warning' },
      { label: '공결', value: `${excused}회`, color: 'info' },
      { label: '결석', value: `${absent}회`, color: 'danger' },
    ];

    return (
      <div className="bg-white rounded shadow-sm p-3">
        <div className="row g-3">
          {summaryItems.map(item => (
            <div className="col-lg-2" key={item.label}>
              <div className="bg-light p-3 rounded h-100">
                <div className="d-flex justify-content-between align-items-center">
                  <span className={`fs-5 text-${item.color}`}>{item.label}</span>
                  <span className="fs-4 fw-bold">{item.value}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
        <div className="mt-4 pt-3 border-top">
            <h5 className="mb-3">출석률</h5>
            <div className="d-flex align-items-center">
              <p className="fs-3 fw-bold mb-0 me-3">{attendanceRate}%</p>
              <div className="progress flex-grow-1" style={{ height: '25px' }}>
                <div
                  className="progress-bar bg-success"
                  role="progressbar"
                  style={{ width: `${attendanceRate}%` }}
                  aria-valuenow={attendanceRate}
                  aria-valuemin="0"
                  aria-valuemax="100"
                  title={`출석: ${attendanceRate}%`}
                ></div>
                <div
                  className="progress-bar bg-warning"
                  role="progressbar"
                  style={{ width: `${tardyRate}%` }}
                  aria-valuenow={tardyRate}
                  aria-valuemin="0"
                  aria-valuemax="100"
                  title={`지각: ${tardyRate}%`}
                ></div>
                <div
                  className="progress-bar"
                  role="progressbar"
                  style={{ width: `${earlyLeaveRate}%`, backgroundColor: '#fd7e14' }}
                  aria-valuenow={earlyLeaveRate}
                  aria-valuemin="0"
                  aria-valuemax="100"
                  title={`조퇴: ${earlyLeaveRate}%`}
                ></div>
                <div
                  className="progress-bar bg-info"
                  role="progressbar"
                  style={{ width: `${excusedRate}%` }}
                  aria-valuenow={excusedRate}
                  aria-valuemin="0"
                  aria-valuemax="100"
                  title={`공결: ${excusedRate}%`}
                ></div>
                <div
                  className="progress-bar bg-danger"
                  role="progressbar"
                  style={{ width: `${absentRate}%` }}
                  aria-valuenow={absentRate}
                  aria-valuemin="0"
                  aria-valuemax="100"
                  title={`결석: ${absentRate}%`}
                ></div>
              </div>
            </div>
        </div>
      </div>
    );
  };

  const renderAttendanceDetails = () => {
    if (!attendanceData || !attendanceData.details) return null;

    const chunks = chunkDetails(attendanceData.details);

    return (
      <div className="bg-white rounded shadow-sm p-3 mt-4">
        <h5 className="mb-3">상세 출석 정보</h5>
        {chunks.map((chunk, chunkIndex) => (
          <div key={chunkIndex} className={chunkIndex > 0 ? 'mt-4' : ''}>
            <div className="table-responsive">
              <table className="table table-bordered mb-0">
                <tbody>
                  {/* 회차 행 */}
                  <tr>
                    {chunk.map((record, index) => (
                      <td key={index} className="text-center fw-bold p-2" style={{ width: `${100 / chunk.length}%` }}>
                        {record.lctPrintRound}
                      </td>
                    ))}
                  </tr>

                  {/* 강의일 행 */}
                  <tr>
                    {chunk.map((record, index) => {
                      const { month, day } = formatDateSplit(record.attDay);
                      return (
                        <td key={index} className="p-2 text-muted" style={{ fontSize: '0.85rem', width: `${100 / chunk.length}%` }}>
                          <div className="text-start">{month}</div>
                          <div className="text-end">{day}</div>
                        </td>
                      );
                    })}
                  </tr>

                  {/* 출석 상태 행 */}
                  <tr>
                    {chunk.map((record, index) => {
                      const statusColor = getStatusColor(record.attStatusName);
                      const customColor = getStatusCustomColor(record.attStatusName);
                      return (
                        <td key={index} className="text-center p-2" style={{ width: `${100 / chunk.length}%` }}>
                          <span
                            className={`badge ${customColor ? '' : `bg-${statusColor}`}`}
                            style={customColor ? { backgroundColor: customColor, fontSize: '0.75rem' } : { fontSize: '0.75rem' }}
                          >
                            {record.attStatusName}
                          </span>
                        </td>
                      );
                    })}
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        ))}
      </div>
    );
  };

  if (loading) return <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '200px' }}><div className="spinner-border" role="status"><span className="visually-hidden">Loading...</span></div></div>;
  if (error) return <div className="alert alert-danger" role="alert">Error: {error}</div>;

  return (
    <div className="container mt-4">
      <h2 className="mb-4">출석 현황</h2>
      {renderAttendanceSummary()}
      {renderAttendanceDetails()}
    </div>
  );
};

export default StudentAttendance;