import React, { useEffect, useState, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Swal from 'sweetalert2';
import { useLecture } from '../../context/LectureContext';

export default function ProfessorGradeManagement() {
  const { lectureId } = useParams();
  const navigate = useNavigate();
  const { students } = useLecture();
  const [endProgress, setEndProgress] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [exams, setExams] = useState([]);
  const [attendance, setAttendance] = useState([]);
  const [gradeRatio, setGradeRatio] = useState([]);
  const [attendanceSummary, setAttendanceSummary] = useState([]);
  const [lectureInfo, setLectureInfo] = useState(null);
  const [evaluateInterval, setEvaluateInterval] = useState(null);
  const [attendanceScoreDefaults, setAttendanceScoreDefaults] = useState(null); // 출석 점수 기본값
  const [calculatedAttendanceScores, setCalculatedAttendanceScores] = useState([]); // 계산된 출석 점수
  const [savedAttendanceScores, setSavedAttendanceScores] = useState(null); // 저장된 출석 점수 (Map)
  const [attendanceScoreStatus, setAttendanceScoreStatus] = useState(null); // 'all', 'partial', 'none'
  const [enrolledStudents, setEnrolledStudents] = useState([]); // 강의 학생 목록

  // 과제 관련 상태
  const [savedTaskScores, setSavedTaskScores] = useState(null); // 저장된 과제 점수 (Map)
  const [taskScoreStatus, setTaskScoreStatus] = useState(null); // 'all', 'partial', 'none'
  const [calculatedTaskScores, setCalculatedTaskScores] = useState([]); // 계산된 과제 점수

  // 시험 관련 상태
  const [savedExamScores, setSavedExamScores] = useState(null); // 저장된 시험 점수 (Map)
  const [examScoreStatus, setExamScoreStatus] = useState(null); // 'all', 'partial', 'none'
  const [calculatedExamScores, setCalculatedExamScores] = useState([]); // 계산된 시험 점수

  // 실습 관련 상태
  const [savedPracScores, setSavedPracScores] = useState(null); // 저장된 실습 점수 (Map)
  const [pracScoreStatus, setPracScoreStatus] = useState(null); // 'all', 'partial', 'none'
  const [inputPracScores, setInputPracScores] = useState({}); // 입력 중인 실습 점수 (enrollId -> score)

  // 기타 관련 상태
  const [savedMiscScores, setSavedMiscScores] = useState(null); // 저장된 기타 점수 (Map)
  const [miscScoreStatus, setMiscScoreStatus] = useState(null); // 'all', 'partial', 'none'
  const [inputMiscScores, setInputMiscScores] = useState({}); // 입력 중인 기타 점수 (enrollId -> score)

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [modalStep, setModalStep] = useState(1); // 1: 성적 산출/입력, 2: 평점구간 설정, 3: 최종확인 및 확정
  const [activeTab, setActiveTab] = useState(null); // 현재 활성화된 성적 기준 탭 (null: 선택 안 됨)
  const [totalScorePreview, setTotalScorePreview] = useState([])
  const [totalScoreSections, setTotalScoreSections] = useState([])
  // 세부 평점 구분자 인덱스 (각 구간별로 구분선 위치)
  const [gradeDividers, setGradeDividers] = useState({ A: null, B: null, CD: [] });
  // 드래그 상태
  const [draggingDivider, setDraggingDivider] = useState(null); // { interval: 'A'|'B'|'C~D', index: number }
  const [totalScoreLoading, setTotalScoreLoading] = useState(false)
  const [totalScoreError, setTotalScoreError] = useState(null)

  // 고정된 탭 구조 정의
  const TABS = [
    { code: 'ATTD', name: '출석' },
    { code: 'EXAM', name: '시험' },
    { code: 'TASK', name: '과제' },
    { code: 'PRAC', name: '실습' },
    { code: 'MISC', name: '기타' },
    { code: 'TOTAL', name: '총점검토' }
  ];

  // 출석 점수 기본값 불러오기 및 계산 함수
  const calculateAttendanceScores = async () => {
    if (!endProgress || !attendanceSummary) {
      return;
    }

    try {
      // 출석 점수 기본값이 없으면 먼저 불러오기
      let scoreDefaults = attendanceScoreDefaults;
      if (!scoreDefaults) {
        const response = await fetch('/classroom/api/v1/common/lecture/evaluate/score-by-attendance-status', {
          headers: { Accept: 'application/json' },
          credentials: 'include'
        });

        if (!response.ok) {
          throw new Error(`(${response.status}) 출석 점수 기본값을 불러오지 못했습니다.`);
        }

        scoreDefaults = await response.json();
        setAttendanceScoreDefaults(scoreDefaults);
      }

      const scores = attendanceSummary.map(student => {
        // 학생별 총 출석 회차 계산 (서로 다를 수 있음)
        const studentTotalRounds = (student.okCnt || 0) +
                                   (student.noCnt || 0) +
                                   (student.earlyCnt || 0) +
                                   (student.lateCnt || 0) +
                                   (student.excpCnt || 0);

        if (studentTotalRounds === 0) {
          return {
            enrollId: student.enrollId,
            score: 0,
            isFailed: false,
            details: {
              ok: 0,
              excp: 0,
              early: 0,
              late: 0,
              no: 0,
              totalRounds: 0
            }
          };
        }

        // 결석 가능 최소선 계산 (학생별 전체 회차 * 0.3 올림)
        const maxAbsenceThreshold = Math.ceil(studentTotalRounds * 0.3);
        const absenceCount = (student.noCnt || 0) + (student.excpCnt || 0);

        // F 처리 여부 확인
        if (absenceCount > maxAbsenceThreshold) {
          return {
            enrollId: student.enrollId,
            score: 0,
            isFailed: true,
            reason: `결석+공결(${absenceCount}회) > 최소선(${maxAbsenceThreshold}회)`,
            details: {
              ok: student.okCnt || 0,
              excp: student.excpCnt || 0,
              early: student.earlyCnt || 0,
              late: student.lateCnt || 0,
              no: student.noCnt || 0,
              totalRounds: studentTotalRounds
            }
          };
        }

        // 각 출석 상태별 점수 계산
        const okScore = (student.okCnt || 0) * (scoreDefaults.ATTD_OK || 100);
        const excpScore = (student.excpCnt || 0) * (scoreDefaults.ATTD_EXCP || 80);
        const earlyScore = (student.earlyCnt || 0) * (scoreDefaults.ATTD_EARLY || 50);
        const lateScore = (student.lateCnt || 0) * (scoreDefaults.ATTD_LATE || 50);
        const noScore = (student.noCnt || 0) * (scoreDefaults.ATTD_NO || 0);

        // 총점 계산
        const totalScore = okScore + excpScore + earlyScore + lateScore + noScore;

        // 평균 점수 (총점 / 학생별 전체 회차)
        const averageScore = totalScore / studentTotalRounds;

        return {
          enrollId: student.enrollId,
          score: Math.round(averageScore * 100) / 100, // 소수점 2자리
          isFailed: false,
          details: {
            ok: student.okCnt || 0,
            excp: student.excpCnt || 0,
            early: student.earlyCnt || 0,
            late: student.lateCnt || 0,
            no: student.noCnt || 0,
            totalRounds: studentTotalRounds
          }
        };
      });

      setCalculatedAttendanceScores(scores);
    } catch (error) {
      console.error('출석 점수 계산 오류:', error);
      alert(error.message || '출석 점수 계산 중 오류가 발생했습니다.');
    }
  };

  // 과제 점수 계산 함수
  const calculateTaskScores = async () => {
    if (!endProgress || !enrolledStudents || enrolledStudents.length === 0) {
      alert('학생 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.');
      return;
    }

    try {
      // 1. 가중치 있는 과제 목록 가져오기
      const weightResponse = await fetch(
        `/classroom/api/v1/professor/task/${encodeURIComponent(lectureId)}/weight`,
        { headers: { Accept: 'application/json' }, credentials: 'include' }
      );

      if (!weightResponse.ok) {
        throw new Error(`(${weightResponse.status}) 과제 가중치 정보를 불러오지 못했습니다.`);
      }

      const weightData = await weightResponse.json();
      const weightedTasks = Array.isArray(weightData) ? weightData.filter(t => t.weightValue > 0) : [];

      console.log('가중치 있는 과제:', weightedTasks);

      if (weightedTasks.length === 0) {
        alert('가중치가 설정된 과제가 없습니다.');
        return;
      }

      // 2. 개인과제와 조별과제 분리
      const indivTasks = weightedTasks.filter(t => t.taskType === 'INDIV');
      const groupTasks = weightedTasks.filter(t => t.taskType === 'GROUP');

      // 3. 개인과제/조별과제 점수 요약 가져오기
      const summaryPromises = [];

      if (indivTasks.length > 0) {
        summaryPromises.push(
          fetch(`/classroom/api/v1/professor/task/${encodeURIComponent(lectureId)}/indiv/summary`, {
            headers: { Accept: 'application/json' },
            credentials: 'include'
          })
        );
      } else {
        summaryPromises.push(Promise.resolve(null));
      }

      if (groupTasks.length > 0) {
        summaryPromises.push(
          fetch(`/classroom/api/v1/professor/task/${encodeURIComponent(lectureId)}/group/summary`, {
            headers: { Accept: 'application/json' },
            credentials: 'include'
          })
        );
      } else {
        summaryPromises.push(Promise.resolve(null));
      }

      const [indivResponse, groupResponse] = await Promise.all(summaryPromises);

      // 4. 응답 데이터 파싱
      const indivSummary = indivResponse && indivResponse.ok ? await indivResponse.json() : [];
      const groupSummary = groupResponse && groupResponse.ok ? await groupResponse.json() : [];

      console.log('개인과제 점수:', indivSummary);
      console.log('조별과제 점수:', groupSummary);

      // 5. 학생별 과제 점수 계산
      const taskScores = enrolledStudents.map(student => {
        const enrollId = student.enrollId;
        let totalScore = 0;

        // 개인과제 점수 계산
        indivTasks.forEach(task => {
          const taskId = task.taskId;
          const weight = task.weightValue;
          const submission = Array.isArray(indivSummary)
            ? indivSummary.find(s => s.enrollId === enrollId && s.indivtaskId === taskId)
            : null;

          // 대상 여부 확인
          const isTarget = submission?.isTarget === 'Y';
          const hasSubmission = submission?.submitAt != null;

          let score = 0;
          if (isTarget && hasSubmission) {
            score = submission?.score || 0;
          }

          totalScore += (score * weight) / 100;
        });

        // 조별과제 점수 계산
        groupTasks.forEach(task => {
          const taskId = task.taskId;
          const weight = task.weightValue;
          const submission = Array.isArray(groupSummary)
            ? groupSummary.find(s => s.enrollId === enrollId && s.grouptaskId === taskId)
            : null;

          // 조별과제는 grouptaskId가 있어야 대상임
          const isTarget = submission?.grouptaskId != null;
          const hasSubmission = submission?.submitAt != null;

          let score = 0;
          if (isTarget && hasSubmission) {
            score = submission?.score || 0;
          }

          totalScore += (score * weight) / 100;
        });

        return {
          enrollId,
          score: Math.round(totalScore * 100) / 100,
          details: {
            indivTasks: indivTasks.map(t => {
              const submission = Array.isArray(indivSummary)
                ? indivSummary.find(s => s.enrollId === enrollId && s.indivtaskId === t.taskId)
                : null;
              const isTarget = submission?.isTarget === 'Y';
              const hasSubmission = submission?.submitAt != null;

              return {
                taskId: t.taskId,
                taskName: t.taskName,
                weight: t.weightValue,
                score: (isTarget && hasSubmission) ? (submission?.score || 0) : 0,
                isTarget,
                hasSubmission
              };
            }),
            groupTasks: groupTasks.map(t => {
              const submission = Array.isArray(groupSummary)
                ? groupSummary.find(s => s.enrollId === enrollId && s.grouptaskId === t.taskId)
                : null;
              // 조별과제는 grouptaskId가 있어야 대상임
              const isTarget = submission?.grouptaskId != null;
              const hasSubmission = submission?.submitAt != null;

              return {
                taskId: t.taskId,
                taskName: t.taskName,
                weight: t.weightValue,
                score: (isTarget && hasSubmission) ? (submission?.score || 0) : 0,
                isTarget,
                hasSubmission
              };
            })
          }
        };
      });

      console.log('계산된 과제 점수:', taskScores);
      setCalculatedTaskScores(taskScores);
    } catch (error) {
      console.error('과제 점수 계산 오류:', error);
      alert(error.message || '과제 점수 계산 중 오류가 발생했습니다.');
    }
  };

  // 시험 점수 계산 함수
  const calculateExamScores = async () => {
    if (!endProgress || !enrolledStudents || enrolledStudents.length === 0) {
      alert('학생 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.');
      return;
    }

    try {
      // 1. 가중치 있는 시험 목록 가져오기 (exams 상태에서)
      const weightedExams = Array.isArray(exams) ? exams.filter(e => e.weightValue > 0) : [];

      console.log('가중치 있는 시험:', weightedExams);

      if (weightedExams.length === 0) {
        alert('가중치가 설정된 시험이 없습니다.');
        return;
      }

      // 2. 시험 점수 요약 가져오기
      const summaryResponse = await fetch(
        `/classroom/api/v1/professor/exam/${encodeURIComponent(lectureId)}/summary`,
        { headers: { Accept: 'application/json' }, credentials: 'include' }
      );

      if (!summaryResponse.ok) {
        throw new Error(`(${summaryResponse.status}) 시험 점수 요약 정보를 불러오지 못했습니다.`);
      }

      const examSummary = await summaryResponse.json();
      console.log('시험 점수 요약:', examSummary);

      // 3. 학생별 시험 점수 계산
      const examScores = enrolledStudents.map(student => {
        const enrollId = student.enrollId;
        let totalScore = 0;

        // 해당 학생의 점수 데이터 찾기
        const studentData = Array.isArray(examSummary)
          ? examSummary.find(s => s.enrollId === enrollId)
          : null;

        // 가중치 있는 시험별 점수 계산
        const examDetails = weightedExams.map(exam => {
          // exams 배열의 examId를 사용 (summary의 lctExamId와 매칭)
          const examId = exam.examId || exam.lctExamId;
          const weight = exam.weightValue;
          const examScore = studentData?.scoreList?.find(s => s.lctExamId === examId);

          // 대상 여부 및 제출 여부 확인
          const isTarget = examScore?.isTarget === 'Y';
          const hasSubmission = examScore?.submitAt != null;

          let score = 0;
          if (isTarget && hasSubmission) {
            score = examScore?.score || 0;
          }

          totalScore += (score * weight) / 100;

          return {
            examId,
            examName: exam.examName,
            weight,
            score,
            isTarget,
            hasSubmission
          };
        });

        return {
          enrollId,
          score: Math.round(totalScore * 100) / 100,
          details: examDetails
        };
      });

      console.log('계산된 시험 점수:', examScores);
      setCalculatedExamScores(examScores);
    } catch (error) {
      console.error('시험 점수 계산 오류:', error);
      alert(error.message || '시험 점수 계산 중 오류가 발생했습니다.');
    }
  };
  const calculateTotalScores = async () => {
    if (!enrolledStudents || enrolledStudents.length === 0) {
      alert('수강중인 학생 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.');
      return;
    }

    const sectionOrder = ['ATTD', 'EXAM', 'TASK', 'PRAC', 'MISC'];
    const labelFallback = {
      ATTD: '출석',
      EXAM: '시험',
      TASK: '과제',
      PRAC: '실습',
      MISC: '기타'
    };

    const ratioMap = new Map(gradeRatio.map(item => [item.gradeCriteriaCd, item]));
    const sections = sectionOrder
      .map((code) => {
        const ratioItem = ratioMap.get(code);
        if (!ratioItem) return null;
        return {
          code,
          label: ratioItem.gradeCriteriaName || labelFallback[code] || code,
          ratio: Number(ratioItem.ratio ?? 0)
        };
      })
      .filter(Boolean);

    if (sections.length === 0) {
      alert('성적 산출 항목 정보가 없습니다. 성적 비율을 먼저 확인해주세요.');
      return;
    }

    try {
      setTotalScoreLoading(true);
      setTotalScoreError(null);
      setTotalScorePreview([]);
      setTotalScoreSections([]);

      const sectionResults = await Promise.all(sections.map(async (section) => {
        const sectionUri = '/classroom/api/v1/professor/'
          + encodeURIComponent(lectureId)
          + '/end/score?section='
          + encodeURIComponent(section.code);

        const response = await fetch(sectionUri, {
          headers: { Accept: 'application/json' },
          credentials: 'include'
        });

        if (!response.ok) {
          throw new Error('(' + response.status + ') ' + section.label + ' 점수를 불러오지 못했습니다.');
        }

        const rows = await response.json();
        const scoreMap = {};
        if (Array.isArray(rows)) {
          rows.forEach((item) => {
            if (!item) return;
            const key = String(item.enrollId);
            if (!key) return;
            const rawValue = item.rawScore ?? item.score;
            if (rawValue === null || rawValue === undefined) {
              return;
            }
            const parsed = Number(rawValue);
            scoreMap[key] = Number.isFinite(parsed) ? parsed : 0;
          });
        }

        return { ...section, scores: scoreMap };
      }));

      const preview = enrolledStudents.map((student) => {
        const enrollId = String(student.enrollId);
        const breakdown = sectionResults.map((section) => {
          const hasScore = Object.prototype.hasOwnProperty.call(section.scores, enrollId);
          const rawScore = hasScore ? section.scores[enrollId] : null;
          const weightedScore = rawScore !== null
            ? Math.round(((rawScore * section.ratio) / 100) * 100) / 100
            : null;

          return {
            code: section.code,
            label: section.label,
            ratio: section.ratio,
            rawScore,
            weightedScore
          };
        });

        const hasMissing = breakdown.some((section) => section.rawScore === null || section.rawScore === undefined);
        const totalScoreRaw = breakdown.reduce((acc, cur) => {
          if (cur.rawScore === null || cur.rawScore === undefined) {
            return acc;
          }
          return acc + ((cur.rawScore * cur.ratio) / 100);
        }, 0);

        return {
          enrollId: student.enrollId,
          totalScore: Math.round(totalScoreRaw * 100) / 100,
          breakdown,
          hasMissing
        };
      });

      const sortedPreview = preview.slice().sort((a, b) => {
        if (a.hasMissing !== b.hasMissing) {
          return a.hasMissing ? 1 : -1;
        }

        const scoreA = Number.isFinite(a.totalScore) ? a.totalScore : -Infinity;
        const scoreB = Number.isFinite(b.totalScore) ? b.totalScore : -Infinity;
        if (scoreA !== scoreB) {
          return scoreB - scoreA;
        }

        const studentA = studentMap.get(a.enrollId);
        const studentB = studentMap.get(b.enrollId);
        const noA = (studentA?.studentNo || studentA?.userId || '').toString();
        const noB = (studentB?.studentNo || studentB?.userId || '').toString();
        if (noA && noB && noA !== noB) {
          return noA.localeCompare(noB);
        }

        return String(a.enrollId).localeCompare(String(b.enrollId));
      });

      setTotalScoreSections(sectionResults.map(({ scores, ...rest }) => rest));
      setTotalScorePreview(sortedPreview);
    } catch (error) {
      console.error('총점 계산 오류:', error);
      setTotalScoreError(error);
      alert(error.message || '총점 계산 중 오류가 발생했습니다.');
    } finally {
      setTotalScoreLoading(false);
    }
  };

  // 최종 성적 확정 및 저장 함수
  const confirmAndSaveGrades = async () => {
    if (totalScorePreview.length === 0) {
      await Swal.fire({
        title: '알림',
        text: '총점 데이터가 없습니다. 먼저 총점을 계산해주세요.',
        icon: 'warning',
        confirmButtonText: '확인'
      });
      return;
    }

    const result = await Swal.fire({
      title: '성적 확정',
      text: '모든 학생의 성적을 확정하시겠습니까?\n확정 후에는 수정할 수 없습니다.',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: '확정',
      cancelButtonText: '취소'
    });

    if (!result.isConfirmed) {
      return;
    }

    try {
      // 학생별 평점 계산
      const gradeRecords = totalScorePreview.map(item => {
        // 출석 점수로 F 등급 판단
        const attendanceSection = item.breakdown.find(b => b.code === 'ATTD');
        const attendanceScore = attendanceSection?.rawScore || 0;
        const isFail = attendanceScore === 0;

        let gpaCd = '-';

        if (isFail) {
          gpaCd = 'F';
        } else if (lectureInfo?.subjectTypeCd === 'SUBJ_RELATIVE') {
          // 상대평가 평점 계산
          const regularStudents = totalScorePreview
            .map(it => {
              const attSec = it.breakdown.find(b => b.code === 'ATTD');
              return {
                enrollId: it.enrollId,
                totalScore: it.totalScore,
                isFail: (attSec?.rawScore || 0) === 0
              };
            })
            .filter(s => !s.isFail)
            .sort((a, b) => b.totalScore - a.totalScore);

          const myIndex = regularStudents.findIndex(s => s.enrollId === item.enrollId);
          if (myIndex !== -1) {
            const aCount = Math.round((lectureInfo.currentCap * (evaluateInterval?.A || 0)) / 100);
            const bCount = Math.round((lectureInfo.currentCap * (evaluateInterval?.B || 0)) / 100);

            let interval = 'C~D';
            if (myIndex < aCount) interval = 'A';
            else if (myIndex < aCount + bCount) interval = 'B';

            // 세부 평점 계산
            const defaultGradeDividers = {
              A: gradeDividers.A !== null ? gradeDividers.A : Math.floor(aCount / 2),
              B: gradeDividers.B !== null ? gradeDividers.B : Math.floor(bCount / 2),
              CD: gradeDividers.CD.length > 0 ? gradeDividers.CD : [
                Math.floor((lectureInfo.currentCap - aCount - bCount) / 4),
                Math.floor((lectureInfo.currentCap - aCount - bCount) / 2),
                Math.floor(((lectureInfo.currentCap - aCount - bCount) * 3) / 4)
              ]
            };

            if (interval === 'A') {
              const aStudents = regularStudents.slice(0, aCount);
              const aIndex = aStudents.findIndex(s => s.enrollId === item.enrollId);
              gpaCd = aIndex <= defaultGradeDividers.A ? 'A+' : 'A0';
            } else if (interval === 'B') {
              const bStudents = regularStudents.slice(aCount, aCount + bCount);
              const bIndex = bStudents.findIndex(s => s.enrollId === item.enrollId);
              gpaCd = bIndex <= defaultGradeDividers.B ? 'B+' : 'B0';
            } else {
              const cdStudents = regularStudents.slice(aCount + bCount);
              const cdIndex = cdStudents.findIndex(s => s.enrollId === item.enrollId);
              const dividers = defaultGradeDividers.CD;
              if (cdIndex <= (dividers[0] || 0)) gpaCd = 'C+';
              else if (cdIndex <= (dividers[1] || 0)) gpaCd = 'C0';
              else if (cdIndex <= (dividers[2] || 0)) gpaCd = 'D+';
              else gpaCd = 'D0';
            }
          }
        } else if (lectureInfo?.subjectTypeCd === 'SUBJ_ABSOLUTE') {
          // 절대평가 - 아직 구현되지 않음
          gpaCd = '-';
        } else if (lectureInfo?.subjectTypeCd === 'SUBJ_PASSFAIL') {
          // PASS/FAIL - 아직 구현되지 않음
          gpaCd = '-';
        }

        return {
          enrollId: String(item.enrollId),
          gpaCd
        };
      });

      // '-' 등급이 있는지 확인 (미완성 평점)
      const hasIncomplete = gradeRecords.some(r => r.gpaCd === '-');
      if (hasIncomplete) {
        await Swal.fire({
          title: '알림',
          text: '일부 학생의 평점이 계산되지 않았습니다. 평가 방식을 확인해주세요.',
          icon: 'warning',
          confirmButtonText: '확인'
        });
        return;
      }

      // API 호출
      const response = await fetch(
        `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/record`,
        {
          method: 'PATCH',
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json'
          },
          credentials: 'include',
          body: JSON.stringify(gradeRecords)
        }
      );

      if (!response.ok) {
        throw new Error(`성적 저장 실패 (${response.status})`);
      }

      await Swal.fire({
        title: '확정 완료',
        text: '모든 학생의 성적이 성공적으로 확정되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      });

      // 최종 성적 수정/확정 탭으로 이동
      navigate(`/classroom/professor/${encodeURIComponent(lectureId)}/finalize`);
    } catch (error) {
      console.error('성적 확정 오류:', error);
      await Swal.fire({
        title: '오류',
        text: error.message || '성적 확정 중 오류가 발생했습니다.',
        icon: 'error',
        confirmButtonText: '확인'
      });
    }
  };



  // 출석 점수 저장 함수
  const saveAttendanceScores = async () => {
    if (calculatedAttendanceScores.length === 0) {
      await Swal.fire({
        title: '알림',
        text: '먼저 출석 점수를 계산해주세요.',
        icon: 'warning',
        confirmButtonText: '확인'
      });
      return;
    }

    const result = await Swal.fire({
      title: '출석 점수 저장',
      text: '계산된 출석 점수를 저장하시겠습니까?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: '저장',
      cancelButtonText: '취소'
    });

    if (!result.isConfirmed) {
      return;
    }

    try {
      // 평가점수 테이블에 저장할 데이터 준비
      const saveData = calculatedAttendanceScores.map(scoreData => ({
        enrollId: scoreData.enrollId,
        gradeCriteriaCd: 'ATTD',
        rawScore: scoreData.score
      }));

      const response = await fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(saveData)
      });

      if (!response.ok) {
        throw new Error(`(${response.status}) 출석 점수 저장에 실패했습니다.`);
      }

      await Swal.fire({
        title: '저장 완료',
        text: '출석 점수가 성공적으로 저장되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      });
      // Update status to 'all' immediately after successful save
      setAttendanceScoreStatus('all');
    } catch (error) {
      console.error('출석 점수 저장 오류:', error);
      await Swal.fire({
        title: '오류',
        text: error.message || '출석 점수 저장 중 오류가 발생했습니다.',
        icon: 'error',
        confirmButtonText: '확인'
      });
    }
  };

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        setLoading(true);
        setError(null);
        const base = `/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end`;
        const [progressRes, taskRes, examRes, attendanceRes, ratioRes, attendanceSummaryRes, lectureRes, intervalRes] = await Promise.all([
          fetch(`${base}/progress`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/task`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/exam`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`${base}/attendance`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`/classroom/api/v1/common/${encodeURIComponent(lectureId)}/ratio`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`/classroom/api/v1/professor/attendance/${encodeURIComponent(lectureId)}/summary`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`/classroom/api/v1/common/${encodeURIComponent(lectureId)}`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
          fetch(`/classroom/api/v1/common/subject/evaluate/interval`, { headers: { Accept: 'application/json' }, credentials: 'include' }),
        ]);

        if (!progressRes.ok) throw new Error(`(${progressRes.status}) 진행률 정보를 불러오지 못했습니다.`);
        if (!taskRes.ok) throw new Error(`(${taskRes.status}) 과제 정보를 불러오지 못했습니다.`);
        if (!examRes.ok) throw new Error(`(${examRes.status}) 시험 정보를 불러오지 못했습니다.`);
        if (!attendanceRes.ok) throw new Error(`(${attendanceRes.status}) 출석 정보를 불러오지 못했습니다.`);
        if (!ratioRes.ok) throw new Error(`(${ratioRes.status}) 성적비율 정보를 불러오지 못했습니다.`);
        if (!attendanceSummaryRes.ok) throw new Error(`(${attendanceSummaryRes.status}) 출석 요약 정보를 불러오지 못했습니다.`);
        if (!lectureRes.ok) throw new Error(`(${lectureRes.status}) 강의 정보를 불러오지 못했습니다.`);
        if (!intervalRes.ok) throw new Error(`(${intervalRes.status}) 평가구간 정보를 불러오지 못했습니다.`);

        const progressData = await progressRes.json();
        const taskData = await taskRes.json();
        const examData = await examRes.json();
        const attendanceData = await attendanceRes.json();
        const ratioData = await ratioRes.json();
        const attendanceSummaryData = await attendanceSummaryRes.json();
        const lectureData = await lectureRes.json();
        const intervalData = await intervalRes.json();

        if (!alive) return;
        setEndProgress(progressData);
        setTasks(Array.isArray(taskData) ? taskData : []);
        setExams(Array.isArray(examData) ? examData : []);
        setAttendance(Array.isArray(attendanceData) ? attendanceData : []);
        setGradeRatio(Array.isArray(ratioData) ? ratioData : []);
        setAttendanceSummary(Array.isArray(attendanceSummaryData) ? attendanceSummaryData : []);
        setLectureInfo(lectureData);
        setEvaluateInterval(intervalData);
      } catch (e) {
        if (!alive) return;
        setError(e);
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => { alive = false };
  }, [lectureId]);

  // 모달이 열릴 때 저장된 점수 확인 (출석 + 과제 + 시험 + 실습 + 기타)
  useEffect(() => {
    if (showModal && savedAttendanceScores === null && savedTaskScores === null && savedExamScores === null && savedPracScores === null && savedMiscScores === null) {
      (async () => {
        try {
          console.log('=== 성적 점수 상태 확인 시작 ===');
          console.log('전체 학생 수:', students?.length);

          // 학생 데이터 구조 확인
          if (students && students.length > 0) {
            console.log('첫 번째 학생 데이터 샘플:', students[0]);
            console.log('학생들의 enrollStatusCd 값들:', students.map(s => s.enrollStatusCd));
          }

          // 1. 수강중인 학생 목록 가져오기
          const lectureStudentsList = Array.isArray(students) ? students : [];
          console.log('학생 수:', lectureStudentsList.length);

          setEnrolledStudents(lectureStudentsList);

          const lectureEnrollIds = new Set(lectureStudentsList.map(s => s.enrollId));

          // 2. 출석, 과제, 시험, 실습, 기타 저장된 점수 병렬로 불러오기
          const [attdResponse, taskResponse, examResponse, pracResponse, miscResponse] = await Promise.all([
            fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score?section=ATTD`, {
              headers: { Accept: 'application/json' },
              credentials: 'include'
            }),
            fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score?section=TASK`, {
              headers: { Accept: 'application/json' },
              credentials: 'include'
            }),
            fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score?section=EXAM`, {
              headers: { Accept: 'application/json' },
              credentials: 'include'
            }),
            fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score?section=PRAC`, {
              headers: { Accept: 'application/json' },
              credentials: 'include'
            }),
            fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score?section=MISC`, {
              headers: { Accept: 'application/json' },
              credentials: 'include'
            })
          ]);

          // 2-1. 출석 점수 처리
          console.log('=== 출석 점수 확인 ===');
          if (!attdResponse.ok) {
            console.warn(`출석 점수 불러오기 실패 (${attdResponse.status})`);
          }

          const attdData = attdResponse.ok ? await attdResponse.json() : [];
          console.log('저장된 출석 점수:', attdData.length, '개');

          const attdScoresMap = new Map(
            Array.isArray(attdData) ? attdData.map(item => [item.enrollId, item.rawScore]) : []
          );
          setSavedAttendanceScores(attdScoresMap);

          let attdSavedCount = 0;
          lectureEnrollIds.forEach(enrollId => {
            if (attdScoresMap.has(enrollId)) attdSavedCount++;
          });

          if (attdSavedCount === lectureEnrollIds.size && lectureEnrollIds.size > 0) {
            console.log('출석 상태: all');
            setAttendanceScoreStatus('all');
          } else if (attdSavedCount > 0) {
            console.log('출석 상태: partial');
            setAttendanceScoreStatus('partial');
          } else {
            console.log('출석 상태: none');
            setAttendanceScoreStatus('none');
          }

          // 2-2. 과제 점수 처리
          console.log('=== 과제 점수 확인 ===');
          if (!taskResponse.ok) {
            console.warn(`과제 점수 불러오기 실패 (${taskResponse.status})`);
          }

          const taskData = taskResponse.ok ? await taskResponse.json() : [];
          console.log('저장된 과제 점수:', taskData.length, '개');

          const taskScoresMap = new Map(
            Array.isArray(taskData) ? taskData.map(item => [item.enrollId, item.rawScore]) : []
          );
          setSavedTaskScores(taskScoresMap);

          let taskSavedCount = 0;
          lectureEnrollIds.forEach(enrollId => {
            if (taskScoresMap.has(enrollId)) taskSavedCount++;
          });

          if (taskSavedCount === lectureEnrollIds.size && lectureEnrollIds.size > 0) {
            console.log('과제 상태: all');
            setTaskScoreStatus('all');
          } else if (taskSavedCount > 0) {
            console.log('과제 상태: partial');
            setTaskScoreStatus('partial');
          } else {
            console.log('과제 상태: none');
            setTaskScoreStatus('none');
          }

          // 2-3. 시험 점수 처리
          console.log('=== 시험 점수 확인 ===');
          if (!examResponse.ok) {
            console.warn(`시험 점수 불러오기 실패 (${examResponse.status})`);
          }

          const examData = examResponse.ok ? await examResponse.json() : [];
          console.log('저장된 시험 점수:', examData.length, '개');

          const examScoresMap = new Map(
            Array.isArray(examData) ? examData.map(item => [item.enrollId, item.rawScore]) : []
          );
          setSavedExamScores(examScoresMap);

          let examSavedCount = 0;
          lectureEnrollIds.forEach(enrollId => {
            if (examScoresMap.has(enrollId)) examSavedCount++;
          });

          if (examSavedCount === lectureEnrollIds.size && lectureEnrollIds.size > 0) {
            console.log('시험 상태: all');
            setExamScoreStatus('all');
          } else if (examSavedCount > 0) {
            console.log('시험 상태: partial');
            setExamScoreStatus('partial');
          } else {
            console.log('시험 상태: none');
            setExamScoreStatus('none');
          }

          // 2-4. 실습 점수 처리
          console.log('=== 실습 점수 확인 ===');
          if (!pracResponse.ok) {
            console.warn(`실습 점수 불러오기 실패 (${pracResponse.status})`);
          }

          const pracData = pracResponse.ok ? await pracResponse.json() : [];
          console.log('저장된 실습 점수:', pracData.length, '개');

          const pracScoresMap = new Map(
            Array.isArray(pracData) ? pracData.map(item => [item.enrollId, item.rawScore]) : []
          );
          setSavedPracScores(pracScoresMap);

          let pracSavedCount = 0;
          lectureEnrollIds.forEach(enrollId => {
            if (pracScoresMap.has(enrollId)) pracSavedCount++;
          });

          if (pracSavedCount === lectureEnrollIds.size && lectureEnrollIds.size > 0) {
            console.log('실습 상태: all');
            setPracScoreStatus('all');
          } else if (pracSavedCount > 0) {
            console.log('실습 상태: partial');
            setPracScoreStatus('partial');
            // partial인 경우 기존 점수를 입력폼에 미리 채워넣기
            const initialScores = {};
            pracScoresMap.forEach((score, enrollId) => {
              initialScores[enrollId] = score;
            });
            setInputPracScores(initialScores);
          } else {
            console.log('실습 상태: none');
            setPracScoreStatus('none');
          }

          // 2-4. 기타 점수 처리
          console.log('=== 기타 점수 확인 ===');
          if (!miscResponse.ok) {
            console.warn(`기타 점수 불러오기 실패 (${miscResponse.status})`);
          }

          const miscData = miscResponse.ok ? await miscResponse.json() : [];
          console.log('저장된 기타 점수:', miscData.length, '개');

          const miscScoresMap = new Map(
            Array.isArray(miscData) ? miscData.map(item => [item.enrollId, item.rawScore]) : []
          );
          setSavedMiscScores(miscScoresMap);

          let miscSavedCount = 0;
          lectureEnrollIds.forEach(enrollId => {
            if (miscScoresMap.has(enrollId)) miscSavedCount++;
          });

          if (miscSavedCount === lectureEnrollIds.size && lectureEnrollIds.size > 0) {
            console.log('기타 상태: all');
            setMiscScoreStatus('all');
          } else if (miscSavedCount > 0) {
            console.log('기타 상태: partial');
            setMiscScoreStatus('partial');
            // partial인 경우 기존 점수를 입력폼에 미리 채워넣기
            const initialScores = {};
            miscScoresMap.forEach((score, enrollId) => {
              initialScores[enrollId] = score;
            });
            setInputMiscScores(initialScores);
          } else {
            console.log('기타 상태: none');
            setMiscScoreStatus('none');
          }

          console.log('=== 성적 점수 상태 확인 완료 ===');
        } catch (error) {
          console.error('저장된 점수 불러오기 오류:', error);
          setSavedAttendanceScores(new Map());
          setAttendanceScoreStatus('none');
          setSavedTaskScores(new Map());
          setTaskScoreStatus('none');
          setSavedExamScores(new Map());
          setExamScoreStatus('none');
          setSavedPracScores(new Map());
          setPracScoreStatus('none');
          setSavedMiscScores(new Map());
          setMiscScoreStatus('none');
        }
      })();
    }
  }, [showModal, lectureId, students]);

  // 출석 탭이 활성화되면 상태에 따라 데이터 준비
  useEffect(() => {
    if (activeTab === 0 && attendanceScoreStatus !== null && calculatedAttendanceScores.length === 0) {
      if (attendanceScoreStatus === 'none') {
        // 점수가 없으면 자동 계산
        calculateAttendanceScores();
      } else {
        // 'all' 또는 'partial'일 때는 저장된 점수 표시
        const scores = enrolledStudents.map(student => {
          const savedScore = savedAttendanceScores.get(student.enrollId);
          return {
            enrollId: student.enrollId,
            score: savedScore !== undefined ? savedScore : null,
            isFailed: savedScore === 0
          };
        });
        setCalculatedAttendanceScores(scores);
      }
    }
  }, [activeTab, attendanceScoreStatus, calculatedAttendanceScores.length]);

  // 진행률 계산
  const progressMetrics = endProgress ? (() => {
    const totalRounds = endProgress.weekCnt * endProgress.weeklyBlockCnt;
    const progressRate = totalRounds > 0 ? ((endProgress.recordedRoundCnt / totalRounds) * 100).toFixed(1) : 0;
    return { totalRounds, progressRate };
  })() : null;

  // 성적비율에서 과제/시험 비율 확인
  const taskRatio = gradeRatio.find(r => r.gradeCriteriaCd === 'TASK')?.ratio || 0;
  const examRatio = gradeRatio.find(r => r.gradeCriteriaCd === 'EXAM')?.ratio || 0;

  // 과제 유효성 검사
  const taskValidation = (() => {
    // 과제 비율이 0이면 검증 건너뛰기
    if (taskRatio === 0) {
      return {
        hasOngoingWeightedTasks: false,
        ongoingTasks: [],
        taskWeightSum: 0,
        isTaskWeightValid: true,
        isTaskValid: true,
        skipValidation: true
      };
    }

    const ongoingTasks = tasks.filter(t => t.closedYn === 'N' && t.weightValue > 0);
    const hasOngoingWeightedTasks = ongoingTasks.length > 0;
    const taskWeightSum = tasks.reduce((sum, t) => sum + (t.weightValue || 0), 0);
    const isTaskWeightValid = taskWeightSum === 100;

    return {
      hasOngoingWeightedTasks,
      ongoingTasks,
      taskWeightSum,
      isTaskWeightValid,
      isTaskValid: !hasOngoingWeightedTasks && isTaskWeightValid,
      skipValidation: false
    };
  })();

  // 시험 유효성 검사
  const examValidation = (() => {
    // 시험 비율이 0이면 검증 건너뛰기
    if (examRatio === 0) {
      return {
        hasOngoingWeightedExams: false,
        ongoingExams: [],
        examWeightSum: 0,
        isExamWeightValid: true,
        isExamValid: true,
        skipValidation: true
      };
    }

    const ongoingExams = exams.filter(e => e.closedYn === 'N' && e.weightValue > 0);
    const hasOngoingWeightedExams = ongoingExams.length > 0;
    const examWeightSum = exams.reduce((sum, e) => sum + (e.weightValue || 0), 0);
    const isExamWeightValid = examWeightSum === 100;

    return {
      hasOngoingWeightedExams,
      ongoingExams,
      examWeightSum,
      isExamWeightValid,
      isExamValid: !hasOngoingWeightedExams && isExamWeightValid,
      skipValidation: false
    };
  })();

  // 출석 유효성 검사
  const attendanceValidation = (() => {
    const hasIncompleteAttendance = attendance.length > 0;
    return {
      hasIncompleteAttendance,
      incompleteRounds: attendance,
      isAttendanceValid: !hasIncompleteAttendance
    };
  })();

  // enrollId로 학생 정보 매핑
  const studentMap = useMemo(() =>
    new Map((students || []).map(s => [s.enrollId, s])),
    [students]
  );

  // 출석 위험군 학생 계산 (공결 + 결석이 30% 이상)
  const atRiskStudents = (() => {
    if (!progressMetrics) return [];
    const totalRounds = progressMetrics.totalRounds;
    if (totalRounds === 0) return [];

    return attendanceSummary.filter(student => {
      const absenceCount = (student.noCnt || 0) + (student.excpCnt || 0);
      const absenceRate = (absenceCount / totalRounds) * 100;
      return absenceRate >= 30;
    });
  })();

  // 진행률 검증
  const progressValidation = progressMetrics ? progressMetrics.progressRate >= 100 : false;

  // 종강 가능 여부
  const canFinalize = progressValidation && taskValidation.isTaskValid && examValidation.isExamValid && attendanceValidation.isAttendanceValid;

  return (
    <section className="container py-3">
      {loading && <div className="text-muted">불러오는 중…</div>}
      {error && <div className="alert alert-danger" role="alert">{String(error.message || error)}</div>}

      {!loading && !error && (
        <div className="vstack gap-4">
          {/* 종강 처리와 성적비율 섹션 */}
          <div className="row g-4">
            {/* 종강 조건 섹션 - 왼쪽 50% */}
            <div className="col-md-6">
              <div className="card h-100">
                <div className="card-header">
                  <h2 className="h5 mb-0">종강 조건</h2>
                </div>
                <div className="card-body">
                  {/* 진행률 */}
                  {progressMetrics && (
                    <div className="mb-4">
                      <h6 className="fw-semibold mb-3">📊 강의 진행률</h6>
                      <div className="vstack gap-2">
                        <div>전체 {progressMetrics.totalRounds}회 중 <strong className="text-primary">{endProgress.recordedRoundCnt}회 완료</strong></div>
                        <div className="progress" style={{ height: '30px' }}>
                          <div
                            className={`progress-bar ${progressValidation ? 'bg-success' : 'bg-warning'}`}
                            role="progressbar"
                            style={{ width: `${progressMetrics.progressRate}%` }}
                            aria-valuenow={progressMetrics.progressRate}
                            aria-valuemin="0"
                            aria-valuemax="100"
                          >
                            {progressMetrics.progressRate}%
                          </div>
                        </div>
                        {!progressValidation && (
                          <div className="alert alert-warning mb-0 small py-2">
                            ⚠️ 종강 처리를 위해서는 강의 진행률이 100%여야 합니다.
                          </div>
                        )}
                      </div>
                    </div>
                  )}

              <hr />

              {/* 과제 체크리스트 */}
              {!taskValidation.skipValidation && (
                <div className="mb-3">
                  <h6 className="fw-semibold mb-2">✅ 과제 확인</h6>
                  <ul className="list-group">
                    <li className={`list-group-item d-flex justify-content-between align-items-center py-2 ${taskValidation.hasOngoingWeightedTasks ? 'list-group-item-danger' : 'list-group-item-success'}`}>
                      <div className="d-flex align-items-center gap-2">
                        <strong>
                          {taskValidation.hasOngoingWeightedTasks
                            ? `마감되지 않은 반영 과제가 있습니다`
                            : '모든 과제가 마감되었습니다.'
                          }
                        </strong>
                        {taskValidation.hasOngoingWeightedTasks && (
                          <a href={`/classroom/professor/${lectureId}/task`} className="text-decoration-none small">과제 탭으로 →</a>
                        )}
                      </div>
                      <span style={{ fontSize: '1.5rem' }}>
                        {taskValidation.hasOngoingWeightedTasks ? '❌' : '✅'}
                      </span>
                    </li>
                    <li className={`list-group-item d-flex justify-content-between align-items-center py-2 ${taskValidation.isTaskWeightValid ? 'list-group-item-success' : 'list-group-item-danger'}`}>
                      <div>
                        <strong>가중치 합계가 {taskValidation.taskWeightSum}입니다.</strong>
                      </div>
                      <span style={{ fontSize: '1.5rem' }}>
                        {taskValidation.isTaskWeightValid ? '✅' : '❌'}
                      </span>
                    </li>
                  </ul>
                </div>
              )}

              {/* 시험 체크리스트 */}
              {!examValidation.skipValidation && (
                <div className="mb-3">
                  <h6 className="fw-semibold mb-2">✅ 시험 확인</h6>
                  <ul className="list-group">
                    <li className={`list-group-item d-flex justify-content-between align-items-center py-2 ${examValidation.hasOngoingWeightedExams ? 'list-group-item-danger' : 'list-group-item-success'}`}>
                      <div className="d-flex align-items-center gap-2">
                        <strong>
                          {examValidation.hasOngoingWeightedExams
                            ? `마감되지 않은 반영 시험이 있습니다`
                            : '모든 시험이 마감되었습니다.'
                          }
                        </strong>
                        {examValidation.hasOngoingWeightedExams && (
                          <a href={`/classroom/professor/${lectureId}/exam`} className="text-decoration-none small">시험 탭으로 →</a>
                        )}
                      </div>
                      <span style={{ fontSize: '1.5rem' }}>
                        {examValidation.hasOngoingWeightedExams ? '❌' : '✅'}
                      </span>
                    </li>
                    <li className={`list-group-item d-flex justify-content-between align-items-center py-2 ${examValidation.isExamWeightValid ? 'list-group-item-success' : 'list-group-item-danger'}`}>
                      <div>
                        <strong>가중치 합계가 {examValidation.examWeightSum}입니다.</strong>
                      </div>
                      <span style={{ fontSize: '1.5rem' }}>
                        {examValidation.isExamWeightValid ? '✅' : '❌'}
                      </span>
                    </li>
                  </ul>
                </div>
              )}

              {/* 출석 체크리스트 */}
              <div className="mb-3">
                <h6 className="fw-semibold mb-2">✅ 출석 확인</h6>
                <ul className="list-group">
                  <li className={`list-group-item d-flex justify-content-between align-items-center py-2 ${attendanceValidation.hasIncompleteAttendance ? 'list-group-item-danger' : 'list-group-item-success'}`}>
                    <div className="d-flex align-items-center gap-2">
                      <strong>
                        {attendanceValidation.hasIncompleteAttendance
                          ? `미정 상태인 출석이 있습니다`
                          : '모든 출석이 처리되었습니다.'
                        }
                      </strong>
                      {attendanceValidation.hasIncompleteAttendance && (
                        <a href={`/classroom/professor/${lectureId}/attendance`} className="text-decoration-none small">출석 탭으로 →</a>
                      )}
                    </div>
                    <span style={{ fontSize: '1.5rem' }}>
                      {attendanceValidation.hasIncompleteAttendance ? '❌' : '✅'}
                    </span>
                  </li>
                  {attendanceValidation.hasIncompleteAttendance && (
                    <li className="list-group-item list-group-item-warning py-2">
                      <div className="small">
                        <strong>미정 상태인 회차:</strong>
                        <div className="mt-1">
                          {attendanceValidation.incompleteRounds.map((round, idx) => (
                            <span key={idx} className="badge bg-warning text-dark me-1">
                              {round.lctRound}회차
                            </span>
                          ))}
                        </div>
                      </div>
                    </li>
                  )}
                </ul>
              </div>

              <hr />

                  <div className="d-flex justify-content-between align-items-center">
                    <div>
                      {canFinalize ? (
                        <span className="text-success">✅ 종강 절차를 시작할 수 있습니다.</span>
                      ) : (
                        <span className="text-danger">❌ 종강 조건을 확인해 주세요.</span>
                      )}
                    </div>
                    <button
                      className="btn btn-primary"
                      disabled={!canFinalize}
                      onClick={() => setShowModal(true)}
                    >
                      종강 절차 시작
                    </button>
                  </div>
                </div>
              </div>
            </div>

            {/* 성적 섹션 - 오른쪽 50% */}
            <div className="col-md-6">
              <div className="card h-100">
                <div className="card-header">
                  <h2 className="h5 mb-0">성적</h2>
                </div>
                <div className="card-body">
                  {gradeRatio.length > 0 ? (
                    <div className="vstack gap-3">
                      <div>
                        <h6 className="fw-semibold mb-2">📊 성적산출비율</h6>
                        <div className="progress" style={{ height: '30px' }}>
                          {gradeRatio.map((item, idx) => {
                            const colors = ['#3498db', '#e74c3c', '#f39c12', '#2ecc71', '#9b59b6'];
                            const bgColor = colors[idx % colors.length];

                            return (
                              <div
                                key={idx}
                                className="progress-bar d-flex align-items-center justify-content-center fw-semibold"
                                role="progressbar"
                                style={{
                                  width: `${item.ratio}%`,
                                  backgroundColor: bgColor,
                                  color: '#fff'
                                }}
                                aria-valuenow={item.ratio}
                                aria-valuemin="0"
                                aria-valuemax="100"
                              >
                                {item.gradeCriteriaName} {item.ratio}%
                              </div>
                            );
                          })}
                        </div>
                        <div className="mt-2 small text-muted">
                          ⚠️ 공결 + 결석이 회차의 30% 이상일 경우 자동 F 처리됩니다.
                        </div>
                      </div>

                      {atRiskStudents.length > 0 && (
                        <div className="alert alert-danger mb-0 py-2">
                          <div className="small">
                            <strong>⚠️ 출석 위험군: {atRiskStudents.length}명</strong>
                            <div className="mt-1">
                              {atRiskStudents.map((student, idx) => {
                                const totalRounds = progressMetrics.totalRounds;
                                const absenceCount = (student.noCnt || 0) + (student.excpCnt || 0);
                                const absenceRate = ((absenceCount / totalRounds) * 100).toFixed(1);
                                const studentInfo = studentMap.get(student.enrollId);
                                const studentName = studentInfo
                                  ? `${studentInfo.lastName || ''}${studentInfo.firstName || ''}`.trim() || studentInfo.studentName || student.enrollId
                                  : student.enrollId;
                                return (
                                  <div key={idx} className="text-muted">
                                    {studentName}: {absenceCount}회 결석 ({absenceRate}%)
                                  </div>
                                );
                              })}
                            </div>
                          </div>
                        </div>
                      )}

                      {/* 평가방식 설명 */}
                      {lectureInfo && (
                        <div className="border-top pt-3">
                          <h6 className="fw-semibold mb-2">
                            📋 평가방법: {lectureInfo.subjectTypeName || lectureInfo.subjectTypeCd}
                          </h6>

                          {lectureInfo.subjectTypeCd === 'SUBJ_RELATIVE' && (
                            <div className="small">
                              <p className="mb-2">학칙으로 정한 비율에 따라 A, B, C~D 구간을 정합니다.</p>
                              {evaluateInterval && lectureInfo.currentCap && (
                                <div className="bg-light p-2 rounded">
                                  <div className="fw-semibold mb-1">이 강의에 적용될 구간별 학생 수:</div>
                                  {(() => {
                                    const aCount = Math.round((lectureInfo.currentCap * (evaluateInterval.A || 0)) / 100);
                                    const bCount = Math.round((lectureInfo.currentCap * (evaluateInterval.B || 0)) / 100);
                                    const cdCount = lectureInfo.currentCap - aCount - bCount;
                                    return (
                                      <ul className="mb-1 ps-3">
                                        <li>A 구간: {aCount}명 ({evaluateInterval.A || 0}%)</li>
                                        <li>B 구간: {bCount}명 ({evaluateInterval.B || 0}%)</li>
                                        <li>C~D 구간: {cdCount}명 ({100 - (evaluateInterval.A || 0) - (evaluateInterval.B || 0)}%)</li>
                                      </ul>
                                    );
                                  })()}
                                  <div className="text-muted" style={{fontSize: '0.85em'}}>
                                    각 구간 내 평점 세부구간을 설정할 수 있습니다.
                                  </div>
                                </div>
                              )}
                            </div>
                          )}

                          {lectureInfo.subjectTypeCd === 'SUBJ_ABSOLUTE' && (
                            <div className="small">
                              <p className="mb-2">교수 재량으로 점수별 평점 구간을 설정합니다.</p>
                              <div className="bg-light p-2 rounded">
                                <div className="fw-semibold mb-1">추천 구간:</div>
                                <ul className="mb-0 ps-3">
                                  <li>A+: 95점 이상</li>
                                  <li>A0: 90~94점</li>
                                  <li>B+: 85~89점</li>
                                  <li>B0: 80~84점</li>
                                  <li>C+: 75~79점</li>
                                  <li>C0: 70~74점</li>
                                  <li>D+: 65~69점</li>
                                  <li>D0: 60~64점</li>
                                  <li>F: 60점 미만</li>
                                </ul>
                              </div>
                            </div>
                          )}

                          {lectureInfo.subjectTypeCd === 'SUBJ_PASSFAIL' && (
                            <div className="small">
                              <p className="mb-1">학생별로 PASS 또는 FAIL을 체크합니다.</p>
                              <div className="text-muted">성적 입력 시 각 학생에 대해 P(PASS) 또는 F(FAIL)을 선택합니다.</div>
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  ) : (
                    <div className="text-center py-5">
                      <i className="bi bi-file-earmark-text display-4 d-block mb-3 opacity-25"></i>
                      <p className="text-muted mb-0">성적비율 정보가 없습니다.</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 종강 절차 모달 */}
      {showModal && (
        <div
          className="modal fade show d-block"
          style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
          tabIndex="-1"
        >
          <div
            className="modal-dialog modal-xl modal-dialog-centered"
            style={{ maxWidth: '1140px' }}
          >
            <div className="modal-content" style={{ height: '85vh', display: 'flex', flexDirection: 'column' }}>
              <div className="modal-header py-2">
                <h6 className="modal-title mb-0">종강 절차</h6>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => {
                    setShowModal(false);
                    setModalStep(1);
                    setActiveTab(null);
                  }}
                  aria-label="Close"
                ></button>
              </div>
              <div className="modal-body" style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
                {/* 진행 단계 프로그레스 바 */}
                <div className="mb-3">
                  <div className="d-flex justify-content-between mb-2">
                    <span className={`small ${modalStep >= 1 ? 'fw-bold text-primary' : 'text-muted'}`}>
                      1) 성적 산출/입력
                    </span>
                    <span className={`small ${modalStep >= 2 ? 'fw-bold text-primary' : 'text-muted'}`}>
                      2) 평점구간 설정
                    </span>
                    <span className={`small ${modalStep >= 3 ? 'fw-bold text-primary' : 'text-muted'}`}>
                      3) 최종확인 및 확정
                    </span>
                  </div>
                  <div className="progress" style={{ height: '8px' }}>
                    <div
                      className="progress-bar"
                      style={{ width: `${(modalStep / 3) * 100}%` }}
                    ></div>
                  </div>
                </div>


                {modalStep === 1 ? (
                  <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
                    {/* 성적 기준별 점수 계산 섹션 - 탭 */}
                    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
                      {/* 탭 네비게이션 */}
                      <ul className="nav nav-tabs mb-3" role="tablist" style={{ flexShrink: 0 }}>
                        {TABS.map((tab, idx) => {
                          // gradeRatio에서 해당 탭의 정보 찾기
                          const criterion = gradeRatio.find(c => c.gradeCriteriaCd === tab.code);
                          const isEnabled = tab.code === 'TOTAL' || (criterion && criterion.ratio > 0);
                          const isActive = activeTab === idx;

                          return (
                            <li key={idx} className="nav-item" role="presentation">
                              <button
                                className={`nav-link ${isActive ? 'active' : ''} ${!isEnabled ? 'disabled' : ''}`}
                                type="button"
                                onClick={() => isEnabled && setActiveTab(idx)}
                                disabled={!isEnabled}
                                style={
                                  isActive
                                    ? {
                                        backgroundColor: '#0d6efd',
                                        color: '#fff',
                                        borderColor: '#0d6efd',
                                        borderBottomLeftRadius: 0,
                                        borderBottomRightRadius: 0,
                                        borderBottom: 'none'
                                      }
                                    : isEnabled
                                    ? {
                                        backgroundColor: '#e7f3ff',
                                        color: '#0d6efd',
                                        borderColor: '#b6d4fe'
                                      }
                                    : {
                                        color: '#6c757d',
                                        backgroundColor: '#e9ecef',
                                        cursor: 'not-allowed'
                                      }
                                }
                              >
                                {tab.name} {criterion ? `(${criterion.ratio}%)` : ''}
                              </button>
                            </li>
                          );
                        })}
                      </ul>

                      {/* 탭 콘텐츠 */}
                      <div className="tab-content" style={{ flex: 1, overflowY: 'auto', minHeight: 0 }}>
                        {/* 초기 상태 메시지 */}
                        {activeTab === null && (
                          <div className="alert alert-info text-center py-5">
                            <h5 className="mb-3">📋 각 탭을 선택해 점수를 확정하세요</h5>
                            <p className="mb-0 text-muted">위의 탭을 클릭하여 각 성적 기준별 점수 계산을 시작하세요.</p>
                          </div>
                        )}

                        {TABS.map((tab, idx) => {
                          const criterion = gradeRatio.find(c => c.gradeCriteriaCd === tab.code);
                          const isActive = activeTab === idx;

                          return (
                            <div
                              key={idx}
                              className={`tab-pane fade ${isActive ? 'show active' : ''}`}
                            >
                              {isActive && (
                                <div className="card">
                                  <div className="card-header bg-light">
                                    <div className="d-flex justify-content-between align-items-center">
                                      <h6 className="mb-0">
                                        {tab.name} {criterion ? `(${criterion.ratio}%)` : ''}
                                      </h6>
                                      <span className="badge bg-primary">{tab.code}</span>
                                    </div>
                                  </div>
                                  <div className="card-body p-3">
                                    {tab.code === 'ATTD' && (
                                      <div>
                                        {attendanceScoreStatus === null ? (
                                          <div className="text-center py-5">
                                            <div className="spinner-border text-primary mb-3" role="status">
                                              <span className="visually-hidden">로딩 중...</span>
                                            </div>
                                            <p className="text-muted mb-0">출석 점수 정보를 불러오는 중입니다...</p>
                                          </div>
                                        ) : attendanceScoreStatus === 'none' ? (
                                          <div>
                                            {calculatedAttendanceScores.length === 0 ? (
                                              <div className="alert alert-warning text-center py-5">
                                                <i className="bi bi-exclamation-circle fs-1 mb-3 d-block"></i>
                                                <h5>출석 점수 기록이 없습니다</h5>
                                                <p className="mb-0">아래에서 출석 점수를 계산하고 저장하세요.</p>
                                                <button className="btn btn-primary mt-3" onClick={calculateAttendanceScores}>
                                                  <i className="bi bi-calculator me-2"></i>
                                                  자동 계산
                                                </button>
                                              </div>
                                            ) : (
                                              <div>
                                                {/* 출석 점수 기본값 설정 표시 */}
                                                {attendanceScoreDefaults && (
                                                  <div className="bg-light p-3 rounded mb-3">
                                                    <div className="d-flex align-items-center justify-content-between gap-3 flex-wrap">
                                                      <div className="d-flex align-items-center gap-3 flex-wrap">
                                                        <strong className="me-2">출석 상태별 점수:</strong>
                                                        <div className="d-flex align-items-center gap-2">
                                                          <span className="small">출석</span>
                                                          <input
                                                            type="number"
                                                            className="form-control form-control-sm text-center"
                                                            value={attendanceScoreDefaults.ATTD_OK || 100}
                                                            disabled
                                                            style={{ width: '60px', backgroundColor: '#e9ecef', padding: '0' }}
                                                          />
                                                        </div>
                                                        <div className="d-flex align-items-center gap-2">
                                                          <span className="small">공결</span>
                                                          <input
                                                            type="number"
                                                            className="form-control form-control-sm text-center"
                                                            value={attendanceScoreDefaults.ATTD_EXCP || 80}
                                                            onChange={(e) => {
                                                              const newValue = parseInt(e.target.value) || 0;
                                                              setAttendanceScoreDefaults({
                                                                ...attendanceScoreDefaults,
                                                                ATTD_EXCP: newValue
                                                              });
                                                            }}
                                                            style={{ width: '60px', padding: '0' }}
                                                          />
                                                        </div>
                                                        <div className="d-flex align-items-center gap-2">
                                                          <span className="small">지각</span>
                                                          <input
                                                            type="number"
                                                            className="form-control form-control-sm text-center"
                                                            value={attendanceScoreDefaults.ATTD_LATE || 50}
                                                            onChange={(e) => {
                                                              const newValue = parseInt(e.target.value) || 0;
                                                              setAttendanceScoreDefaults({
                                                                ...attendanceScoreDefaults,
                                                                ATTD_LATE: newValue
                                                              });
                                                            }}
                                                            style={{ width: '60px', padding: '0' }}
                                                          />
                                                        </div>
                                                        <div className="d-flex align-items-center gap-2">
                                                          <span className="small">조퇴</span>
                                                          <input
                                                            type="number"
                                                            className="form-control form-control-sm text-center"
                                                            value={attendanceScoreDefaults.ATTD_EARLY || 50}
                                                            onChange={(e) => {
                                                              const newValue = parseInt(e.target.value) || 0;
                                                              setAttendanceScoreDefaults({
                                                                ...attendanceScoreDefaults,
                                                                ATTD_EARLY: newValue
                                                              });
                                                            }}
                                                            style={{ width: '60px', padding: '0' }}
                                                          />
                                                        </div>
                                                        <div className="d-flex align-items-center gap-2">
                                                          <span className="small">결석</span>
                                                          <input
                                                            type="number"
                                                            className="form-control form-control-sm text-center"
                                                            value={attendanceScoreDefaults.ATTD_NO || 0}
                                                            disabled
                                                            style={{ width: '60px', backgroundColor: '#e9ecef', padding: '0' }}
                                                          />
                                                        </div>
                                                      </div>
                                                      <button
                                                        className="custom-btn custom-btn-primary custom-btn-sm"
                                                        onClick={() => {
                                                          setCalculatedAttendanceScores([]);
                                                          // 점수가 비워지면 useEffect에서 자동 재계산
                                                        }}
                                                      >
                                                        <i className="bi bi-arrow-clockwise me-1"></i>
                                                        재채점
                                                      </button>
                                                    </div>
                                                  </div>
                                                )}

                                                {/* F 처리 경고 문구 */}
                                                <div className="alert alert-warning py-2 mb-3">
                                                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                                                  결석+공결이 학생별 총 출석 회차의 30% 이상이면 자동으로 F 처리됩니다.
                                                </div>

                                                {/* 상세 테이블 */}
                                                <div className="table-responsive">
                                                  <table className="table table-hover align-middle mb-0">
                                                    <thead>
                                                      <tr className="table-primary">
                                                        <th className="text-center" style={{ width: '100px' }}>학번</th>
                                                        <th className="text-center" style={{ width: '100px' }}>이름</th>
                                                        <th className="text-center" style={{ width: '70px' }}>출석</th>
                                                        <th className="text-center" style={{ width: '70px' }}>공결</th>
                                                        <th className="text-center" style={{ width: '70px' }}>지각</th>
                                                        <th className="text-center" style={{ width: '70px' }}>조퇴</th>
                                                        <th className="text-center" style={{ width: '70px' }}>결석</th>
                                                        <th className="text-center" style={{ width: '80px' }}>총 회차</th>
                                                        <th className="text-center" style={{ width: '100px' }}>출석점수</th>
                                                        <th className="text-center">비고</th>
                                                      </tr>
                                                    </thead>
                                                    <tbody>
                                                      {calculatedAttendanceScores.map((scoreData, idx) => {
                                                        const studentInfo = studentMap.get(scoreData.enrollId);
                                                        const studentName = studentInfo
                                                          ? `${studentInfo.lastName || ''}${studentInfo.firstName || ''}`.trim() || studentInfo.studentName
                                                          : '';
                                                        const studentNo = studentInfo?.studentNo || studentInfo?.userId || '';

                                                        return (
                                                          <tr key={idx} className={scoreData.isFailed ? 'table-danger' : ''}>
                                                            <td className="text-center small">{studentNo}</td>
                                                            <td className="text-center fw-semibold">{studentName || scoreData.enrollId}</td>
                                                            <td className="text-center">{scoreData.details?.ok || 0}</td>
                                                            <td className="text-center">{scoreData.details?.excp || 0}</td>
                                                            <td className="text-center">{scoreData.details?.late || 0}</td>
                                                            <td className="text-center">{scoreData.details?.early || 0}</td>
                                                            <td className="text-center">{scoreData.details?.no || 0}</td>
                                                            <td className="text-center fw-bold">{scoreData.details?.totalRounds || 0}</td>
                                                            <td className="text-center">
                                                              <span className={`badge ${scoreData.isFailed ? 'bg-danger' : 'bg-primary'} fs-6 px-3 py-2`}>
                                                                {scoreData.isFailed ? 'F' : scoreData.score.toFixed(2)}
                                                              </span>
                                                            </td>
                                                            <td className="text-center">
                                                              {scoreData.isFailed && scoreData.reason && (
                                                                <span className="badge bg-danger small">{scoreData.reason}</span>
                                                              )}
                                                            </td>
                                                          </tr>
                                                        );
                                                      })}
                                                    </tbody>
                                                  </table>
                                                </div>

                                                {/* 저장 버튼 */}
                                                <div className="d-flex justify-content-end mt-3">
                                                  <button
                                                    className="btn btn-success"
                                                    onClick={saveAttendanceScores}
                                                  >
                                                    <i className="bi bi-save me-2"></i>
                                                    점수 저장
                                                  </button>
                                                </div>
                                              </div>
                                            )}
                                          </div>
                                        ) : (
                                          <div>
                                            {/* all 또는 partial 상태 */}
                                            {(attendanceScoreStatus === 'all' || attendanceScoreStatus === 'partial') && (
                                              <div className="alert alert-info mb-3">
                                                <i className="bi bi-info-circle me-2"></i>
                                                기존에 작성한 출석 점수가 존재합니다.
                                              </div>
                                            )}

                                            {/* partial 상태일 때 점수 없는 학생 표시 */}
                                            {attendanceScoreStatus === 'partial' && (
                                              <div className="alert alert-warning mb-3">
                                                <strong>점수가 없는 학생:</strong>
                                                <div className="mt-2">
                                                  {enrolledStudents
                                                    .filter(s => !savedAttendanceScores.has(s.enrollId))
                                                    .map((s, idx) => {
                                                      const name = `${s.lastName || ''}${s.firstName || ''}`.trim() || s.studentName;
                                                      const studentNo = s.studentNo || s.userId;
                                                      return (
                                                        <span key={idx} className="badge bg-warning text-dark me-2">
                                                          {studentNo} {name}
                                                        </span>
                                                      );
                                                    })}
                                                </div>
                                              </div>
                                            )}

                                            {/* 저장된 점수 간단 테이블 */}
                                            <div className="table-responsive">
                                              <table className="table table-hover align-middle mb-0">
                                                <thead>
                                                  <tr className="table-primary">
                                                    <th className="text-center">학번</th>
                                                    <th className="text-center">이름</th>
                                                    <th className="text-center">출석점수</th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  {enrolledStudents.map((student, idx) => {
                                                    const savedScore = savedAttendanceScores.get(student.enrollId);
                                                    const name = `${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName;
                                                    const studentNo = student.studentNo || student.userId;

                                                    return (
                                                      <tr key={idx}>
                                                        <td className="text-center">{studentNo}</td>
                                                        <td className="text-center fw-semibold">{name}</td>
                                                        <td className="text-center">
                                                          {savedScore !== undefined ? (
                                                            <span className="badge bg-primary fs-6 px-3 py-2">
                                                              {savedScore.toFixed(2)}
                                                            </span>
                                                          ) : (
                                                            <span className="badge bg-secondary fs-6 px-3 py-2">
                                                              저장된 점수 없음
                                                            </span>
                                                          )}
                                                        </td>
                                                      </tr>
                                                    );
                                                  })}
                                                </tbody>
                                              </table>
                                            </div>

                                            {/* 수정 버튼 */}
                                            <div className="d-flex justify-content-end mt-3">
                                              <button
                                                className="btn btn-warning"
                                                onClick={() => {
                                                  // 수정 모드로 전환
                                                  setAttendanceScoreStatus('none');
                                                  setCalculatedAttendanceScores([]);
                                                }}
                                              >
                                                <i className="bi bi-pencil me-2"></i>
                                                점수 수정
                                              </button>
                                            </div>
                                          </div>
                                        )}
                                      </div>
                                    )}
                                    {tab.code === 'EXAM' && (
                                      <div>
                                        {examScoreStatus === null ? (
                                          <div className="text-center py-5">
                                            <div className="spinner-border text-primary mb-3" role="status">
                                              <span className="visually-hidden">로딩 중...</span>
                                            </div>
                                            <p className="text-muted mb-0">시험 점수 정보를 불러오는 중입니다...</p>
                                          </div>
                                        ) : examScoreStatus === 'none' ? (
                                          <div>
                                            {calculatedExamScores.length === 0 ? (
                                              <div className="alert alert-warning text-center py-5">
                                                <i className="bi bi-exclamation-circle fs-1 mb-3 d-block"></i>
                                                <h5>시험 점수 기록이 없습니다</h5>
                                                <p className="mb-0">아래에서 시험 점수를 계산하고 저장하세요.</p>
                                                <button className="btn btn-primary mt-3" onClick={calculateExamScores} style={{ backgroundColor: '#0d6efd', paddingLeft: '8px', paddingRight: '8px' }}>
                                                  <i className="bi bi-calculator me-2"></i>
                                                  자동 계산
                                                </button>
                                              </div>
                                            ) : (
                                              <div>
                                                {/* 계산된 시험 점수 상세 테이블 */}
                                                <div className="table-responsive">
                                                  <table className="table table-hover align-middle mb-0" style={{ fontSize: '0.9rem' }}>
                                                    <thead>
                                                      <tr className="table-primary">
                                                        <th className="text-center" style={{ minWidth: '80px' }}>학번</th>
                                                        <th className="text-center" style={{ minWidth: '80px' }}>이름</th>
                                                        {calculatedExamScores[0]?.details?.map((exam, idx) => (
                                                          <th key={idx} className="text-center" style={{ minWidth: '100px' }}>
                                                            {exam.examName}<br/>
                                                            <small className="text-muted">({exam.weight}%)</small>
                                                          </th>
                                                        ))}
                                                        <th className="text-center bg-light" style={{ minWidth: '100px' }}>
                                                          <strong>시험점수</strong>
                                                        </th>
                                                      </tr>
                                                    </thead>
                                                    <tbody>
                                                      {calculatedExamScores.map((scoreData, idx) => {
                                                        const student = enrolledStudents.find(s => s.enrollId === scoreData.enrollId);
                                                        const name = student ? (`${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName) : '';
                                                        const studentNo = student ? (student.studentNo || student.userId) : '';

                                                        return (
                                                          <tr key={idx}>
                                                            <td className="text-center">{studentNo}</td>
                                                            <td className="text-center fw-semibold">{name}</td>
                                                            {scoreData.details?.map((exam, examIdx) => (
                                                              <td key={examIdx} className="text-center">
                                                                {!exam.isTarget ? (
                                                                  <span className="badge bg-secondary text-white">대상아님</span>
                                                                ) : !exam.hasSubmission ? (
                                                                  <span className="badge bg-warning text-dark">제출없음</span>
                                                                ) : (
                                                                  <span className="badge bg-info text-white">
                                                                    {exam.score.toFixed(2)}
                                                                  </span>
                                                                )}
                                                              </td>
                                                            ))}
                                                            <td className="text-center bg-light">
                                                              <strong
                                                                className="fs-6 text-primary"
                                                                style={{ cursor: 'help' }}
                                                                title={(() => {
                                                                  const details = [];
                                                                  scoreData.details?.forEach(exam => {
                                                                    if (exam.isTarget && exam.hasSubmission) {
                                                                      const contribution = (exam.score * exam.weight) / 100;
                                                                      details.push(`${exam.examName}: ${exam.score} × ${exam.weight}% = ${contribution.toFixed(2)}`);
                                                                    }
                                                                  });
                                                                  return details.length > 0 ? details.join('\n') : '점수 없음';
                                                                })()}
                                                              >
                                                                {scoreData.score.toFixed(2)}
                                                              </strong>
                                                            </td>
                                                          </tr>
                                                        );
                                                      })}
                                                    </tbody>
                                                  </table>
                                                </div>

                                                {/* 저장 버튼 */}
                                                <div className="d-flex justify-content-end mt-3">
                                                  <button
                                                    className="btn btn-success"
                                                    onClick={async () => {
                                                      if (calculatedExamScores.length === 0) {
                                                        await Swal.fire({
                                                          title: '알림',
                                                          text: '먼저 시험 점수를 계산해주세요.',
                                                          icon: 'warning',
                                                          confirmButtonText: '확인'
                                                        });
                                                        return;
                                                      }

                                                      const result = await Swal.fire({
                                                        title: '시험 점수 저장',
                                                        text: '계산된 시험 점수를 저장하시겠습니까?',
                                                        icon: 'question',
                                                        showCancelButton: true,
                                                        confirmButtonText: '저장',
                                                        cancelButtonText: '취소'
                                                      });

                                                      if (!result.isConfirmed) {
                                                        return;
                                                      }

                                                      try {
                                                        const saveData = calculatedExamScores.map(scoreData => ({
                                                          enrollId: scoreData.enrollId,
                                                          gradeCriteriaCd: 'EXAM',
                                                          rawScore: scoreData.score
                                                        }));

                                                        const response = await fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score`, {
                                                          method: 'PATCH',
                                                          headers: {
                                                            'Content-Type': 'application/json',
                                                            'Accept': 'application/json'
                                                          },
                                                          credentials: 'include',
                                                          body: JSON.stringify(saveData)
                                                        });

                                                        if (!response.ok) {
                                                          throw new Error(`(${response.status}) 시험 점수 저장에 실패했습니다.`);
                                                        }

                                                        await Swal.fire({
                                                          title: '저장 완료',
                                                          text: '시험 점수가 성공적으로 저장되었습니다.',
                                                          icon: 'success',
                                                          confirmButtonText: '확인'
                                                        });

                                                        // 저장 후 상태 업데이트
                                                        const newSavedScores = new Map();
                                                        calculatedExamScores.forEach(s => {
                                                          newSavedScores.set(s.enrollId, s.score);
                                                        });
                                                        setSavedExamScores(newSavedScores);
                                                        setExamScoreStatus('all');
                                                        setCalculatedExamScores([]);
                                                      } catch (error) {
                                                        console.error('시험 점수 저장 오류:', error);
                                                        await Swal.fire({
                                                          title: '오류',
                                                          text: error.message || '시험 점수 저장 중 오류가 발생했습니다.',
                                                          icon: 'error',
                                                          confirmButtonText: '확인'
                                                        });
                                                      }
                                                    }}
                                                  >
                                                    <i className="bi bi-save me-2"></i>
                                                    점수 저장
                                                  </button>
                                                </div>
                                              </div>
                                            )}
                                          </div>
                                        ) : (
                                          <div>
                                            {/* all 또는 partial 상태 */}
                                            {(examScoreStatus === 'all' || examScoreStatus === 'partial') && (
                                              <div className="alert alert-info mb-3">
                                                <i className="bi bi-info-circle me-2"></i>
                                                기존에 작성한 시험 점수가 존재합니다.
                                              </div>
                                            )}

                                            {/* partial 상태일 때 점수 없는 학생 표시 */}
                                            {examScoreStatus === 'partial' && (
                                              <div className="alert alert-warning mb-3">
                                                <strong>점수가 없는 학생:</strong>
                                                <div className="mt-2">
                                                  {enrolledStudents
                                                    .filter(s => !savedExamScores.has(s.enrollId))
                                                    .map((s, idx) => {
                                                      const name = `${s.lastName || ''}${s.firstName || ''}`.trim() || s.studentName;
                                                      const studentNo = s.studentNo || s.userId;
                                                      return (
                                                        <span key={idx} className="badge bg-warning text-dark me-2">
                                                          {studentNo} {name}
                                                        </span>
                                                      );
                                                    })}
                                                </div>
                                              </div>
                                            )}

                                            {/* 저장된 점수 테이블 표시 */}
                                            <div className="table-responsive">
                                              <table className="table table-hover align-middle mb-0">
                                                <thead>
                                                  <tr className="table-primary">
                                                    <th className="text-center">학번</th>
                                                    <th className="text-center">이름</th>
                                                    <th className="text-center">시험점수</th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  {enrolledStudents.map((student, idx) => {
                                                    const savedScore = savedExamScores.get(student.enrollId);
                                                    const name = `${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName;
                                                    const studentNo = student.studentNo || student.userId;

                                                    return (
                                                      <tr key={idx}>
                                                        <td className="text-center">{studentNo}</td>
                                                        <td className="text-center fw-semibold">{name}</td>
                                                        <td className="text-center">
                                                          {savedScore !== undefined ? (
                                                            <span className="badge bg-primary fs-6 px-3 py-2">
                                                              {savedScore.toFixed(2)}
                                                            </span>
                                                          ) : (
                                                            <span className="badge bg-secondary fs-6 px-3 py-2">
                                                              저장된 점수 없음
                                                            </span>
                                                          )}
                                                        </td>
                                                      </tr>
                                                    );
                                                  })}
                                                </tbody>
                                              </table>
                                            </div>

                                            {/* 수정 버튼 */}
                                            <div className="d-flex justify-content-end mt-3">
                                              <button
                                                className="btn btn-warning"
                                                onClick={async () => {
                                                  // 수정 모드로 전환하면서 바로 계산
                                                  setExamScoreStatus('none');
                                                  await calculateExamScores();
                                                }}
                                              >
                                                <i className="bi bi-pencil me-2"></i>
                                                점수 수정
                                              </button>
                                            </div>
                                          </div>
                                        )}
                                      </div>
                                    )}
                                    {tab.code === 'TASK' && (
                                      <div>
                                        {taskScoreStatus === null ? (
                                          <div className="text-center py-5">
                                            <div className="spinner-border text-primary mb-3" role="status">
                                              <span className="visually-hidden">로딩 중...</span>
                                            </div>
                                            <p className="text-muted mb-0">과제 점수 정보를 불러오는 중입니다...</p>
                                          </div>
                                        ) : taskScoreStatus === 'none' ? (
                                          <div>
                                            {calculatedTaskScores.length === 0 ? (
                                              <div className="alert alert-warning text-center py-5">
                                                <i className="bi bi-exclamation-circle fs-1 mb-3 d-block"></i>
                                                <h5>과제 점수 기록이 없습니다</h5>
                                                <p className="mb-0">아래에서 과제 점수를 계산하고 저장하세요.</p>
                                                <button className="btn btn-primary mt-3" onClick={calculateTaskScores} style={{ backgroundColor: '#0d6efd', paddingLeft: '8px', paddingRight: '8px' }}>
                                                  <i className="bi bi-calculator me-2"></i>
                                                  자동 계산
                                                </button>
                                              </div>
                                            ) : (
                                              <div>
                                                {/* 계산된 과제 점수 상세 테이블 */}
                                                <div className="table-responsive">
                                                  <table className="table table-hover align-middle mb-0" style={{ fontSize: '0.9rem' }}>
                                                    <thead>
                                                      <tr className="table-primary">
                                                        <th className="text-center" style={{ minWidth: '80px' }}>학번</th>
                                                        <th className="text-center" style={{ minWidth: '80px' }}>이름</th>
                                                        {calculatedTaskScores[0]?.details?.indivTasks?.map((task, idx) => (
                                                          <th key={`indiv-${idx}`} className="text-center" style={{ minWidth: '100px' }}>
                                                            {task.taskName}<br/>
                                                            <small className="text-muted">({task.weight}%)</small>
                                                          </th>
                                                        ))}
                                                        {calculatedTaskScores[0]?.details?.groupTasks?.map((task, idx) => (
                                                          <th key={`group-${idx}`} className="text-center" style={{ minWidth: '100px' }}>
                                                            {task.taskName}<br/>
                                                            <small className="text-muted">({task.weight}%)</small>
                                                          </th>
                                                        ))}
                                                        <th className="text-center bg-light" style={{ minWidth: '100px' }}>
                                                          <strong>과제점수</strong>
                                                        </th>
                                                      </tr>
                                                    </thead>
                                                    <tbody>
                                                      {calculatedTaskScores.map((scoreData, idx) => {
                                                        const student = enrolledStudents.find(s => s.enrollId === scoreData.enrollId);
                                                        const name = student ? (`${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName) : '';
                                                        const studentNo = student ? (student.studentNo || student.userId) : '';

                                                        return (
                                                          <tr key={idx}>
                                                            <td className="text-center">{studentNo}</td>
                                                            <td className="text-center fw-semibold">{name}</td>
                                                            {scoreData.details?.indivTasks?.map((task, taskIdx) => (
                                                              <td key={`indiv-${taskIdx}`} className="text-center">
                                                                {!task.isTarget ? (
                                                                  <span className="badge bg-secondary text-white">대상아님</span>
                                                                ) : !task.hasSubmission ? (
                                                                  <span className="badge bg-warning text-dark">제출없음</span>
                                                                ) : (
                                                                  <span className="badge bg-info text-white">
                                                                    {task.score.toFixed(2)}
                                                                  </span>
                                                                )}
                                                              </td>
                                                            ))}
                                                            {scoreData.details?.groupTasks?.map((task, taskIdx) => (
                                                              <td key={`group-${taskIdx}`} className="text-center">
                                                                {!task.isTarget ? (
                                                                  <span className="badge bg-secondary text-white">대상아님</span>
                                                                ) : !task.hasSubmission ? (
                                                                  <span className="badge bg-warning text-dark">제출없음</span>
                                                                ) : (
                                                                  <span className="badge bg-info text-white">
                                                                    {task.score.toFixed(2)}
                                                                  </span>
                                                                )}
                                                              </td>
                                                            ))}
                                                            <td className="text-center bg-light">
                                                              <strong
                                                                className="fs-6 text-primary"
                                                                style={{ cursor: 'help' }}
                                                                title={(() => {
                                                                  const details = [];
                                                                  scoreData.details?.indivTasks?.forEach(task => {
                                                                    if (task.isTarget && task.hasSubmission) {
                                                                      const contribution = (task.score * task.weight) / 100;
                                                                      details.push(`${task.taskName}: ${task.score} × ${task.weight}% = ${contribution.toFixed(2)}`);
                                                                    }
                                                                  });
                                                                  scoreData.details?.groupTasks?.forEach(task => {
                                                                    if (task.isTarget && task.hasSubmission) {
                                                                      const contribution = (task.score * task.weight) / 100;
                                                                      details.push(`${task.taskName}: ${task.score} × ${task.weight}% = ${contribution.toFixed(2)}`);
                                                                    }
                                                                  });
                                                                  return details.length > 0 ? details.join('\n') : '점수 없음';
                                                                })()}
                                                              >
                                                                {scoreData.score.toFixed(2)}
                                                              </strong>
                                                            </td>
                                                          </tr>
                                                        );
                                                      })}
                                                    </tbody>
                                                  </table>
                                                </div>

                                                {/* 저장 버튼 */}
                                                <div className="d-flex justify-content-end mt-3">
                                                  <button
                                                    className="btn btn-success"
                                                    onClick={async () => {
                                                      if (calculatedTaskScores.length === 0) {
                                                        await Swal.fire({
                                                          title: '알림',
                                                          text: '먼저 과제 점수를 계산해주세요.',
                                                          icon: 'warning',
                                                          confirmButtonText: '확인'
                                                        });
                                                        return;
                                                      }

                                                      const result = await Swal.fire({
                                                        title: '과제 점수 저장',
                                                        text: '계산된 과제 점수를 저장하시겠습니까?',
                                                        icon: 'question',
                                                        showCancelButton: true,
                                                        confirmButtonText: '저장',
                                                        cancelButtonText: '취소'
                                                      });

                                                      if (!result.isConfirmed) {
                                                        return;
                                                      }

                                                      try {
                                                        const saveData = calculatedTaskScores.map(scoreData => ({
                                                          enrollId: scoreData.enrollId,
                                                          gradeCriteriaCd: 'TASK',
                                                          rawScore: scoreData.score
                                                        }));

                                                        const response = await fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score`, {
                                                          method: 'PATCH',
                                                          headers: {
                                                            'Content-Type': 'application/json',
                                                            'Accept': 'application/json'
                                                          },
                                                          credentials: 'include',
                                                          body: JSON.stringify(saveData)
                                                        });

                                                        if (!response.ok) {
                                                          throw new Error(`(${response.status}) 과제 점수 저장에 실패했습니다.`);
                                                        }

                                                        await Swal.fire({
                                                          title: '저장 완료',
                                                          text: '과제 점수가 성공적으로 저장되었습니다.',
                                                          icon: 'success',
                                                          confirmButtonText: '확인'
                                                        });

                                                        // 저장 후 상태 업데이트
                                                        const newSavedScores = new Map();
                                                        calculatedTaskScores.forEach(s => {
                                                          newSavedScores.set(s.enrollId, s.score);
                                                        });
                                                        setSavedTaskScores(newSavedScores);
                                                        setTaskScoreStatus('all');
                                                        setCalculatedTaskScores([]);
                                                      } catch (error) {
                                                        console.error('과제 점수 저장 오류:', error);
                                                        await Swal.fire({
                                                          title: '오류',
                                                          text: error.message || '과제 점수 저장 중 오류가 발생했습니다.',
                                                          icon: 'error',
                                                          confirmButtonText: '확인'
                                                        });
                                                      }
                                                    }}
                                                  >
                                                    <i className="bi bi-save me-2"></i>
                                                    점수 저장
                                                  </button>
                                                </div>
                                              </div>
                                            )}
                                          </div>
                                        ) : (
                                          <div>
                                            {/* all 또는 partial 상태 */}
                                            {(taskScoreStatus === 'all' || taskScoreStatus === 'partial') && (
                                              <div className="alert alert-info mb-3">
                                                <i className="bi bi-info-circle me-2"></i>
                                                기존에 작성한 과제 점수가 존재합니다.
                                              </div>
                                            )}

                                            {/* partial 상태일 때 점수 없는 학생 표시 */}
                                            {taskScoreStatus === 'partial' && (
                                              <div className="alert alert-warning mb-3">
                                                <strong>점수가 없는 학생:</strong>
                                                <div className="mt-2">
                                                  {enrolledStudents
                                                    .filter(s => !savedTaskScores.has(s.enrollId))
                                                    .map((s, idx) => {
                                                      const name = `${s.lastName || ''}${s.firstName || ''}`.trim() || s.studentName;
                                                      const studentNo = s.studentNo || s.userId;
                                                      return (
                                                        <span key={idx} className="badge bg-warning text-dark me-2">
                                                          {studentNo} {name}
                                                        </span>
                                                      );
                                                    })}
                                                </div>
                                              </div>
                                            )}

                                            {/* 저장된 점수 테이블 표시 */}
                                            <div className="table-responsive">
                                              <table className="table table-hover align-middle mb-0">
                                                <thead>
                                                  <tr className="table-primary">
                                                    <th className="text-center">학번</th>
                                                    <th className="text-center">이름</th>
                                                    <th className="text-center">과제점수</th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  {enrolledStudents.map((student, idx) => {
                                                    const savedScore = savedTaskScores.get(student.enrollId);
                                                    const name = `${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName;
                                                    const studentNo = student.studentNo || student.userId;

                                                    return (
                                                      <tr key={idx}>
                                                        <td className="text-center">{studentNo}</td>
                                                        <td className="text-center fw-semibold">{name}</td>
                                                        <td className="text-center">
                                                          {savedScore !== undefined ? (
                                                            <span className="badge bg-primary fs-6 px-3 py-2">
                                                              {savedScore.toFixed(2)}
                                                            </span>
                                                          ) : (
                                                            <span className="badge bg-secondary fs-6 px-3 py-2">
                                                              저장된 점수 없음
                                                            </span>
                                                          )}
                                                        </td>
                                                      </tr>
                                                    );
                                                  })}
                                                </tbody>
                                              </table>
                                            </div>

                                            {/* 수정 버튼 */}
                                            <div className="d-flex justify-content-end mt-3">
                                              <button
                                                className="btn btn-warning"
                                                onClick={async () => {
                                                  // 수정 모드로 전환하면서 바로 계산
                                                  setTaskScoreStatus('none');
                                                  await calculateTaskScores();
                                                }}
                                              >
                                                <i className="bi bi-pencil me-2"></i>
                                                점수 수정
                                              </button>
                                            </div>
                                          </div>
                                        )}
                                      </div>
                                    )}
                                    {tab.code === 'PRAC' && (
                                      <div>
                                        {pracScoreStatus === null ? (
                                          <div className="text-center py-5">
                                            <div className="spinner-border text-primary mb-3" role="status">
                                              <span className="visually-hidden">로딩 중...</span>
                                            </div>
                                            <p className="text-muted mb-0">실습 점수 정보를 불러오는 중입니다...</p>
                                          </div>
                                        ) : pracScoreStatus === 'all' ? (
                                          <div>
                                            <div className="alert alert-info mb-3">
                                              <i className="bi bi-info-circle me-2"></i>
                                              기존에 작성한 실습 점수가 존재합니다.
                                            </div>

                                            <div className="table-responsive">
                                              <table className="table table-hover align-middle mb-0">
                                                <thead>
                                                  <tr className="table-primary">
                                                    <th className="text-center">학번</th>
                                                    <th className="text-center">이름</th>
                                                    <th className="text-center">실습점수</th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  {enrolledStudents.map((student, idx) => {
                                                    const savedScore = savedPracScores.get(student.enrollId);
                                                    const name = `${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName;
                                                    const studentNo = student.studentNo || student.userId;

                                                    return (
                                                      <tr key={idx}>
                                                        <td className="text-center">{studentNo}</td>
                                                        <td className="text-center fw-semibold">{name}</td>
                                                        <td className="text-center">
                                                          <span className="badge bg-primary fs-6 px-3 py-2">
                                                            {savedScore.toFixed(2)}
                                                          </span>
                                                        </td>
                                                      </tr>
                                                    );
                                                  })}
                                                </tbody>
                                              </table>
                                            </div>

                                            <div className="d-flex justify-content-end mt-3">
                                              <button
                                                className="btn btn-warning"
                                                onClick={() => {
                                                  // 기존 점수를 입력 폼에 채워넣기
                                                  const initialScores = {};
                                                  savedPracScores.forEach((score, enrollId) => {
                                                    initialScores[enrollId] = score;
                                                  });
                                                  setInputPracScores(initialScores);
                                                  setPracScoreStatus('none');
                                                }}
                                              >
                                                <i className="bi bi-pencil me-2"></i>
                                                점수 수정
                                              </button>
                                            </div>
                                          </div>
                                        ) : (
                                          <div>
                                            {pracScoreStatus === 'partial' && (
                                              <div className="alert alert-warning mb-3">
                                                <strong>일부 학생의 점수가 저장되어 있습니다.</strong> 기존 점수를 확인하고 나머지 학생의 점수를 입력하세요.
                                              </div>
                                            )}

                                            <div className="alert alert-info mb-3">
                                              <i className="bi bi-info-circle me-2"></i>
                                              실습 점수를 직접 입력하여 설정할 수 있습니다. (0-100점)
                                            </div>

                                            <div className="table-responsive">
                                              <table className="table table-hover align-middle mb-0">
                                                <thead>
                                                  <tr className="table-primary">
                                                    <th className="text-center" style={{ width: '150px' }}>학번</th>
                                                    <th className="text-center" style={{ width: '150px' }}>이름</th>
                                                    <th className="text-center">실습점수</th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  {enrolledStudents.map((student, idx) => {
                                                    const name = `${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName;
                                                    const studentNo = student.studentNo || student.userId;
                                                    const currentValue = inputPracScores[student.enrollId] ?? '';

                                                    return (
                                                      <tr key={idx}>
                                                        <td className="text-center">{studentNo}</td>
                                                        <td className="text-center fw-semibold">{name}</td>
                                                        <td className="text-center">
                                                          <input
                                                            type="number"
                                                            className="form-control text-center"
                                                            placeholder="0-100"
                                                            min="0"
                                                            max="100"
                                                            step="0.01"
                                                            value={currentValue}
                                                            onChange={(e) => {
                                                              const value = e.target.value;
                                                              setInputPracScores(prev => ({
                                                                ...prev,
                                                                [student.enrollId]: value
                                                              }));
                                                            }}
                                                            style={{ maxWidth: '120px', margin: '0 auto' }}
                                                          />
                                                        </td>
                                                      </tr>
                                                    );
                                                  })}
                                                </tbody>
                                              </table>
                                            </div>

                                            <div className="d-flex justify-content-end mt-3">
                                              <button
                                                className="btn btn-success"
                                                onClick={async () => {
                                                  if (Object.keys(inputPracScores).length === 0) {
                                                    await Swal.fire({
                                                      title: '알림',
                                                      text: '먼저 실습 점수를 입력해주세요.',
                                                      icon: 'warning',
                                                      confirmButtonText: '확인'
                                                    });
                                                    return;
                                                  }

                                                  const result = await Swal.fire({
                                                    title: '실습 점수 저장',
                                                    text: '입력한 실습 점수를 저장하시겠습니까?',
                                                    icon: 'question',
                                                    showCancelButton: true,
                                                    confirmButtonText: '저장',
                                                    cancelButtonText: '취소'
                                                  });

                                                  if (!result.isConfirmed) {
                                                    return;
                                                  }

                                                  try {
                                                    const saveData = enrolledStudents.map(student => ({
                                                      enrollId: student.enrollId,
                                                      gradeCriteriaCd: 'PRAC',
                                                      rawScore: parseFloat(inputPracScores[student.enrollId] || 0)
                                                    }));

                                                    const response = await fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score`, {
                                                      method: 'PATCH',
                                                      headers: {
                                                        'Content-Type': 'application/json',
                                                        'Accept': 'application/json'
                                                      },
                                                      credentials: 'include',
                                                      body: JSON.stringify(saveData)
                                                    });

                                                    if (!response.ok) {
                                                      throw new Error(`(${response.status}) 실습 점수 저장에 실패했습니다.`);
                                                    }

                                                    await Swal.fire({
                                                      title: '저장 완료',
                                                      text: '실습 점수가 성공적으로 저장되었습니다.',
                                                      icon: 'success',
                                                      confirmButtonText: '확인'
                                                    });

                                                    const newSavedScores = new Map();
                                                    saveData.forEach(s => {
                                                      newSavedScores.set(s.enrollId, s.rawScore);
                                                    });
                                                    setSavedPracScores(newSavedScores);
                                                    setPracScoreStatus('all');
                                                    setInputPracScores({});
                                                  } catch (error) {
                                                    console.error('실습 점수 저장 오류:', error);
                                                    await Swal.fire({
                                                      title: '오류',
                                                      text: error.message || '실습 점수 저장 중 오류가 발생했습니다.',
                                                      icon: 'error',
                                                      confirmButtonText: '확인'
                                                    });
                                                  }
                                                }}
                                              >
                                                <i className="bi bi-save me-2"></i>
                                                점수 저장
                                              </button>
                                            </div>
                                          </div>
                                        )}
                                      </div>
                                    )}
                                    {tab.code === 'MISC' && (
                                      <div>
                                        {miscScoreStatus === null ? (
                                          <div className="text-center py-5">
                                            <div className="spinner-border text-primary mb-3" role="status">
                                              <span className="visually-hidden">로딩 중...</span>
                                            </div>
                                            <p className="text-muted mb-0">기타 점수 정보를 불러오는 중입니다...</p>
                                          </div>
                                        ) : miscScoreStatus === 'all' ? (
                                          <div>
                                            <div className="alert alert-info mb-3">
                                              <i className="bi bi-info-circle me-2"></i>
                                              기존에 작성한 기타 점수가 존재합니다.
                                            </div>

                                            <div className="table-responsive">
                                              <table className="table table-hover align-middle mb-0">
                                                <thead>
                                                  <tr className="table-primary">
                                                    <th className="text-center">학번</th>
                                                    <th className="text-center">이름</th>
                                                    <th className="text-center">기타점수</th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  {enrolledStudents.map((student, idx) => {
                                                    const savedScore = savedMiscScores.get(student.enrollId);
                                                    const name = `${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName;
                                                    const studentNo = student.studentNo || student.userId;

                                                    return (
                                                      <tr key={idx}>
                                                        <td className="text-center">{studentNo}</td>
                                                        <td className="text-center fw-semibold">{name}</td>
                                                        <td className="text-center">
                                                          <span className="badge bg-primary fs-6 px-3 py-2">
                                                            {savedScore.toFixed(2)}
                                                          </span>
                                                        </td>
                                                      </tr>
                                                    );
                                                  })}
                                                </tbody>
                                              </table>
                                            </div>

                                            <div className="d-flex justify-content-end mt-3">
                                              <button
                                                className="btn btn-warning"
                                                onClick={() => {
                                                  // 기존 점수를 입력 폼에 채워넣기
                                                  const initialScores = {};
                                                  savedMiscScores.forEach((score, enrollId) => {
                                                    initialScores[enrollId] = score;
                                                  });
                                                  setInputMiscScores(initialScores);
                                                  setMiscScoreStatus('none');
                                                }}
                                              >
                                                <i className="bi bi-pencil me-2"></i>
                                                점수 수정
                                              </button>
                                            </div>
                                          </div>
                                        ) : (
                                          <div>
                                            {miscScoreStatus === 'partial' && (
                                              <div className="alert alert-warning mb-3">
                                                <strong>일부 학생의 점수가 저장되어 있습니다.</strong> 기존 점수를 확인하고 나머지 학생의 점수를 입력하세요.
                                              </div>
                                            )}

                                            <div className="alert alert-info mb-3">
                                              <i className="bi bi-info-circle me-2"></i>
                                              기타 점수를 직접 입력하여 설정할 수 있습니다. (0-100점)
                                            </div>

                                            <div className="table-responsive">
                                              <table className="table table-hover align-middle mb-0">
                                                <thead>
                                                  <tr className="table-primary">
                                                    <th className="text-center" style={{ width: '150px' }}>학번</th>
                                                    <th className="text-center" style={{ width: '150px' }}>이름</th>
                                                    <th className="text-center">기타점수</th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  {enrolledStudents.map((student, idx) => {
                                                    const name = `${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName;
                                                    const studentNo = student.studentNo || student.userId;
                                                    const currentValue = inputMiscScores[student.enrollId] ?? '';

                                                    return (
                                                      <tr key={idx}>
                                                        <td className="text-center">{studentNo}</td>
                                                        <td className="text-center fw-semibold">{name}</td>
                                                        <td className="text-center">
                                                          <input
                                                            type="number"
                                                            className="form-control text-center"
                                                            placeholder="0-100"
                                                            min="0"
                                                            max="100"
                                                            step="0.01"
                                                            value={currentValue}
                                                            onChange={(e) => {
                                                              const value = e.target.value;
                                                              setInputMiscScores(prev => ({
                                                                ...prev,
                                                                [student.enrollId]: value
                                                              }));
                                                            }}
                                                            style={{ maxWidth: '120px', margin: '0 auto' }}
                                                          />
                                                        </td>
                                                      </tr>
                                                    );
                                                  })}
                                                </tbody>
                                              </table>
                                            </div>

                                            <div className="d-flex justify-content-end mt-3">
                                              <button
                                                className="btn btn-success"
                                                onClick={async () => {
                                                  if (Object.keys(inputMiscScores).length === 0) {
                                                    await Swal.fire({
                                                      title: '알림',
                                                      text: '먼저 기타 점수를 입력해주세요.',
                                                      icon: 'warning',
                                                      confirmButtonText: '확인'
                                                    });
                                                    return;
                                                  }

                                                  const result = await Swal.fire({
                                                    title: '기타 점수 저장',
                                                    text: '입력한 기타 점수를 저장하시겠습니까?',
                                                    icon: 'question',
                                                    showCancelButton: true,
                                                    confirmButtonText: '저장',
                                                    cancelButtonText: '취소'
                                                  });

                                                  if (!result.isConfirmed) {
                                                    return;
                                                  }

                                                  try {
                                                    const saveData = enrolledStudents.map(student => ({
                                                      enrollId: student.enrollId,
                                                      gradeCriteriaCd: 'MISC',
                                                      rawScore: parseFloat(inputMiscScores[student.enrollId] || 0)
                                                    }));

                                                    const response = await fetch(`/classroom/api/v1/professor/${encodeURIComponent(lectureId)}/end/score`, {
                                                      method: 'PATCH',
                                                      headers: {
                                                        'Content-Type': 'application/json',
                                                        'Accept': 'application/json'
                                                      },
                                                      credentials: 'include',
                                                      body: JSON.stringify(saveData)
                                                    });

                                                    if (!response.ok) {
                                                      throw new Error(`(${response.status}) 기타 점수 저장에 실패했습니다.`);
                                                    }

                                                    await Swal.fire({
                                                      title: '저장 완료',
                                                      text: '기타 점수가 성공적으로 저장되었습니다.',
                                                      icon: 'success',
                                                      confirmButtonText: '확인'
                                                    });

                                                    const newSavedScores = new Map();
                                                    saveData.forEach(s => {
                                                      newSavedScores.set(s.enrollId, s.rawScore);
                                                    });
                                                    setSavedMiscScores(newSavedScores);
                                                    setMiscScoreStatus('all');
                                                    setInputMiscScores({});
                                                  } catch (error) {
                                                    console.error('기타 점수 저장 오류:', error);
                                                    await Swal.fire({
                                                      title: '오류',
                                                      text: error.message || '기타 점수 저장 중 오류가 발생했습니다.',
                                                      icon: 'error',
                                                      confirmButtonText: '확인'
                                                    });
                                                  }
                                                }}
                                              >
                                                <i className="bi bi-save me-2"></i>
                                                점수 저장
                                              </button>
                                            </div>
                                          </div>
                                        )}
                                      </div>
                                    )}
                                    {tab.code === 'TOTAL' && (
                                      <div>
                                        {/* 성적 기준별 상태 체크 */}
                                        {totalScorePreview.length === 0 && (
                                          <div className="table-responsive mb-4">
                                          <table className="table table-bordered align-middle">
                                            <thead className="table-light">
                                              <tr>
                                                <th className="text-center" style={{ width: '150px' }}>성적 기준</th>
                                                <th className="text-center" style={{ width: '100px' }}>반영 비율</th>
                                                <th className="text-center" style={{ width: '120px' }}>점수 산출 상태</th>
                                                <th className="text-center">설명</th>
                                              </tr>
                                            </thead>
                                            <tbody>
                                              {/* 출석 */}
                                              {gradeRatio.find(c => c.gradeCriteriaCd === 'ATTD')?.ratio > 0 && (
                                              <tr>
                                                <td className="text-center fw-semibold">출석</td>
                                                <td className="text-center">
                                                  {gradeRatio.find(c => c.gradeCriteriaCd === 'ATTD')?.ratio || 0}%
                                                </td>
                                                <td className="text-center">
                                                  {attendanceScoreStatus === null ? (
                                                    <span className="badge bg-secondary">확인중...</span>
                                                  ) : attendanceScoreStatus === 'all' ? (
                                                    <span className="badge bg-info">완료</span>
                                                  ) : attendanceScoreStatus === 'partial' ? (
                                                    <span className="badge bg-warning text-dark">미완성</span>
                                                  ) : (
                                                    <span className="badge bg-danger">데이터 없음</span>
                                                  )}
                                                </td>
                                                <td className="small">
                                                  {attendanceScoreStatus === 'all' && '모든 학생의 출석 점수가 저장되었습니다.'}
                                                  {attendanceScoreStatus === 'partial' && '일부 학생의 출석 점수가 누락되었습니다.'}
                                                  {attendanceScoreStatus === 'none' && '출석 점수를 계산하고 저장해주세요.'}
                                                </td>
                                              </tr>
                                              )}

                                              {/* 과제 */}
                                              {gradeRatio.find(c => c.gradeCriteriaCd === 'TASK')?.ratio > 0 && (
                                              <tr>
                                                <td className="text-center fw-semibold">과제</td>
                                                <td className="text-center">
                                                  {gradeRatio.find(c => c.gradeCriteriaCd === 'TASK')?.ratio || 0}%
                                                </td>
                                                <td className="text-center">
                                                  {taskScoreStatus === null ? (
                                                    <span className="badge bg-secondary">확인중...</span>
                                                  ) : taskScoreStatus === 'all' ? (
                                                    <span className="badge bg-info">완료</span>
                                                  ) : taskScoreStatus === 'partial' ? (
                                                    <span className="badge bg-warning text-dark">미완성</span>
                                                  ) : (
                                                    <span className="badge bg-danger">데이터 없음</span>
                                                  )}
                                                </td>
                                                <td className="small">
                                                  {taskScoreStatus === 'all' && '모든 학생의 과제 점수가 저장되었습니다.'}
                                                  {taskScoreStatus === 'partial' && '일부 학생의 과제 점수가 누락되었습니다.'}
                                                  {taskScoreStatus === 'none' && '과제 점수를 계산하고 저장해주세요.'}
                                                </td>
                                              </tr>
                                              )}

                                              {/* 시험 */}
                                              {gradeRatio.find(c => c.gradeCriteriaCd === 'EXAM')?.ratio > 0 && (
                                              <tr>
                                                <td className="text-center fw-semibold">시험</td>
                                                <td className="text-center">
                                                  {gradeRatio.find(c => c.gradeCriteriaCd === 'EXAM')?.ratio || 0}%
                                                </td>
                                                <td className="text-center">
                                                  {examScoreStatus === null ? (
                                                    <span className="badge bg-secondary">확인중...</span>
                                                  ) : examScoreStatus === 'all' ? (
                                                    <span className="badge bg-info">완료</span>
                                                  ) : examScoreStatus === 'partial' ? (
                                                    <span className="badge bg-warning text-dark">미완성</span>
                                                  ) : (
                                                    <span className="badge bg-danger">데이터 없음</span>
                                                  )}
                                                </td>
                                                <td className="small">
                                                  {examScoreStatus === 'all' && '모든 학생의 시험 점수가 저장되었습니다.'}
                                                  {examScoreStatus === 'partial' && '일부 학생의 시험 점수가 누락되었습니다.'}
                                                  {examScoreStatus === 'none' && '시험 점수를 계산하고 저장해주세요.'}
                                                </td>
                                              </tr>
                                              )}

                                              {/* 실습 */}
                                              {gradeRatio.find(c => c.gradeCriteriaCd === 'PRAC')?.ratio > 0 && (
                                              <tr>
                                                <td className="text-center fw-semibold">실습</td>
                                                <td className="text-center">
                                                  {gradeRatio.find(c => c.gradeCriteriaCd === 'PRAC')?.ratio || 0}%
                                                </td>
                                                <td className="text-center">
                                                  {pracScoreStatus === null ? (
                                                    <span className="badge bg-secondary">확인중...</span>
                                                  ) : pracScoreStatus === 'all' ? (
                                                    <span className="badge bg-info">완료</span>
                                                  ) : pracScoreStatus === 'partial' ? (
                                                    <span className="badge bg-warning text-dark">미완성</span>
                                                  ) : (
                                                    <span className="badge bg-danger">데이터 없음</span>
                                                  )}
                                                </td>
                                                <td className="small">
                                                  {pracScoreStatus === 'all' && '모든 학생의 실습 점수가 저장되었습니다.'}
                                                  {pracScoreStatus === 'partial' && '일부 학생의 실습 점수가 누락되었습니다.'}
                                                  {pracScoreStatus === 'none' && '실습 점수를 입력하고 저장해주세요.'}
                                                </td>
                                              </tr>
                                              )}

                                              {/* 기타 */}
                                              {gradeRatio.find(c => c.gradeCriteriaCd === 'MISC')?.ratio > 0 && (
                                              <tr>
                                                <td className="text-center fw-semibold">기타</td>
                                                <td className="text-center">
                                                  {gradeRatio.find(c => c.gradeCriteriaCd === 'MISC')?.ratio || 0}%
                                                </td>
                                                <td className="text-center">
                                                  {miscScoreStatus === null ? (
                                                    <span className="badge bg-secondary">확인중...</span>
                                                  ) : miscScoreStatus === 'all' ? (
                                                    <span className="badge bg-info">완료</span>
                                                  ) : miscScoreStatus === 'partial' ? (
                                                    <span className="badge bg-warning text-dark">미완성</span>
                                                  ) : (
                                                    <span className="badge bg-danger">데이터 없음</span>
                                                  )}
                                                </td>
                                                <td className="small">
                                                  {miscScoreStatus === 'all' && '모든 학생의 기타 점수가 저장되었습니다.'}
                                                  {miscScoreStatus === 'partial' && '일부 학생의 기타 점수가 누락되었습니다.'}
                                                  {miscScoreStatus === 'none' && '기타 점수를 입력하고 저장해주세요.'}
                                                </td>
                                              </tr>
                                              )}
                                            </tbody>
                                          </table>
                                        </div>
                                        )}

                                        {/* 전체 상태 메시지 */}
                                        {totalScorePreview.length === 0 && (() => {
                                          // 비율이 0보다 큰 항목들만 확인
                                          const requiredCriteria = gradeRatio.filter(c => c.ratio > 0);
                                          const statusMap = {
                                            'ATTD': attendanceScoreStatus,
                                            'TASK': taskScoreStatus,
                                            'EXAM': examScoreStatus,
                                            'PRAC': pracScoreStatus,
                                            'MISC': miscScoreStatus
                                          };

                                          const allComplete = requiredCriteria.every(c => statusMap[c.gradeCriteriaCd] === 'all');
                                          const hasPartial = requiredCriteria.some(c => statusMap[c.gradeCriteriaCd] === 'partial');
                                          const hasNone = requiredCriteria.some(c => statusMap[c.gradeCriteriaCd] === 'none');

                                          return (
                                            <div className="mb-4">
                                              {allComplete ? (
                                                <div className="alert alert-success">
                                                  <i className="bi bi-check-circle-fill me-2"></i>
                                                  <strong>모든 성적 산출이 완료되었습니다!</strong> 총점을 계산하고 확정할 수 있습니다.
                                                </div>
                                              ) : (
                                                <div className="alert alert-warning">
                                                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                                                  <strong>일부 성적 산출이 미완료되었습니다.</strong> 모든 항목을 완료한 후 총점을 계산할 수 있습니다.
                                                </div>
                                              )}
                                            </div>
                                          );
                                        })()}

                                        {/* 총점 계산 버튼 */}
                                        {totalScorePreview.length === 0 && (
                                          <div className="d-flex justify-content-end">
                                          <button
                                            className="btn btn-success btn-lg"
                                            onClick={calculateTotalScores}
                                            disabled={(() => {
                                              const requiredCriteria = gradeRatio.filter(c => c.ratio > 0);
                                              const statusMap = {
                                                'ATTD': attendanceScoreStatus,
                                                'TASK': taskScoreStatus,
                                                'EXAM': examScoreStatus,
                                                'PRAC': pracScoreStatus,
                                                'MISC': miscScoreStatus
                                              };
                                              const allReady = requiredCriteria.every(c => statusMap[c.gradeCriteriaCd] === 'all');
                                              return !allReady || totalScoreLoading;
                                            })()}
                                          >
                                            {totalScoreLoading ? (
                                              <>
                                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                                계산 중...
                                              </>
                                            ) : (
                                              <>
                                                <i className="bi bi-calculator me-2"></i>
                                                총점 및 순위 확인
                                              </>
                                            )}
                                          </button>
                                        </div>
                                        )}

                                        {totalScoreError && (
                                          <div className="alert alert-danger mt-3" role="alert">
                                            {totalScoreError.message || String(totalScoreError)}
                                          </div>
                                        )}

                                        {totalScorePreview.length > 0 ? (
                                          <div className="mt-4">
                                            <h6 className="fw-semibold mb-3">학생별 총점 미리보기</h6>
                                            <div className="table-responsive">
                                              <table className="table table-hover align-middle mb-0" style={{ fontSize: '0.9rem' }}>
                                                <thead>
                                                  <tr className="table-success">
                                                    <th className="text-center" style={{ minWidth: '90px' }}>학번</th>
                                                    <th className="text-center" style={{ minWidth: '90px' }}>이름</th>
                                                    {totalScoreSections.map((section) => (
                                                      <th key={section.code} className="text-center" style={{ minWidth: '120px' }}>
                                                        {section.label}
                                                        <br />
                                                        <small className="text-muted">({section.ratio}%)</small>
                                                      </th>
                                                    ))}
                                                    <th className="text-center bg-light" style={{ minWidth: '120px' }}>
                                                      <strong>총점</strong>
                                                    </th>
                                                  </tr>
                                                </thead>
                                                <tbody>
                                                  {totalScorePreview.map((item) => {
                                                    const student = studentMap.get(item.enrollId);
                                                    const name = student ? (`${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName || '-') : '-';
                                                    const studentNo = student ? (student.studentNo || student.userId || '-') : '-';
                                                    const hasMissingSection = item.hasMissing ?? item.breakdown.some(section => section.rawScore === null || section.rawScore === undefined);
                                                    return (
                                                      <tr key={item.enrollId} className={hasMissingSection ? 'table-warning' : ''}>
                                                        <td className="text-center">{studentNo}</td>
                                                        <td className="text-center fw-semibold">{name}</td>
                                                        {item.breakdown.map((section) => {
                                                          const hasScore = section.rawScore !== null && section.rawScore !== undefined;
                                                          const weighted = typeof section.weightedScore === 'number' ? section.weightedScore : 0;
                                                          return (
                                                            <td key={section.code} className="text-center">
                                                              {!hasScore ? (
                                                                <span className="badge bg-warning text-dark">미저장</span>
                                                              ) : (
                                                                <div className="d-flex flex-column align-items-center gap-1">
                                                                  <span className="badge bg-primary-subtle text-primary border border-primary-subtle">
                                                                    {section.rawScore.toFixed(2)}
                                                                  </span>
                                                                  <small className="text-muted">
                                                                    × {section.ratio}% = {weighted.toFixed(2)}
                                                                  </small>
                                                                </div>
                                                              )}
                                                            </td>
                                                          );
                                                        })}
                                                        <td className="text-center">
                                                          <span className={`badge ${hasMissingSection ? 'bg-warning text-dark' : 'bg-success text-white'} fs-6`}>
                                                            {item.totalScore.toFixed(2)}
                                                          </span>
                                                        </td>
                                                      </tr>
                                                    );
                                                  })}
                                                </tbody>
                                              </table>
                                            </div>
                                          </div>
                                        ) : (
                                          !totalScoreLoading && (
                                            <div className="text-muted mt-3">
                                              총점 계산을 실행하면 학생별 결과가 표시됩니다.
                                            </div>
                                          )
                                        )}
                                      </div>
                                    )}
                                  </div>
                                </div>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  </div>
                ) : modalStep === 2 ? (
                  <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
                    <div style={{ flex: 1, overflowY: 'auto', padding: '1rem' }}>
                      {(() => {
                        console.log('=== modalStep 2 데이터 확인 ===');
                        console.log('totalScorePreview:', totalScorePreview);
                        console.log('totalScorePreview.length:', totalScorePreview.length);
                        console.log('studentMap:', studentMap);
                        console.log('studentMap.size:', studentMap?.size);
                        console.log('lectureInfo:', lectureInfo);
                        console.log('evaluateInterval:', evaluateInterval);
                        return null;
                      })()}
                      {!lectureInfo || lectureInfo.subjectTypeCd !== 'SUBJ_RELATIVE' ? (
                        <div className="alert alert-info">
                          <i className="bi bi-info-circle me-2"></i>
                          <strong>구현 중입니다</strong>
                          <p className="mb-0 mt-2">
                            {lectureInfo?.subjectTypeCd === 'SUBJ_ABSOLUTE' && '절대평가'}
                            {lectureInfo?.subjectTypeCd === 'SUBJ_PASSFAIL' && 'PASS or FAIL'}
                            {!lectureInfo && '평가방법을 불러오는 중'}
                          </p>
                        </div>
                      ) : (
                        <div>
                          <h5 className="mb-4">평점 구간 설정 (상대평가)</h5>
                          {totalScorePreview.length === 0 ? (
                            <div className="alert alert-warning">
                              <i className="bi bi-exclamation-triangle-fill me-2"></i>
                              <strong>총점 데이터가 없습니다</strong>
                              <p className="mb-0 mt-2">
                                이전 단계로 돌아가서 "총점검토" 탭에서 "총점 및 순위 확인" 버튼을 먼저 눌러주세요.
                              </p>
                            </div>
                          ) : (() => {
                          console.log('=== 평점 구간 계산 시작 ===');
                          console.log('totalScorePreview 데이터:', totalScorePreview);

                          // F 학생 필터링 (출석점수가 0인 학생)
                          const studentsWithGrades = totalScorePreview.map(item => {
                            const student = studentMap.get(item.enrollId);
                            console.log(`enrollId ${item.enrollId}:`, student);
                            const name = student ? (`${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName || '-') : '-';
                            const studentNo = student ? (student.studentNo || student.userId || '-') : '-';

                            // 출석 점수 찾기
                            const attendanceSection = item.breakdown.find(b => b.code === 'ATTD');
                            const attendanceScore = attendanceSection?.rawScore || 0;

                            const studentData = {
                              enrollId: item.enrollId,
                              studentNo,
                              name,
                              totalScore: item.totalScore,
                              attendanceScore,
                              isFail: attendanceScore === 0
                            };
                            console.log('매핑된 학생 데이터:', studentData);
                            return studentData;
                          });

                          console.log('studentsWithGrades:', studentsWithGrades);

                          // F 학생과 일반 학생 분리
                          const failStudents = studentsWithGrades.filter(s => s.isFail);
                          const regularStudents = studentsWithGrades.filter(s => !s.isFail);

                          console.log('failStudents:', failStudents);
                          console.log('regularStudents:', regularStudents);

                          // 일반 학생을 총점 순으로 정렬 (높은 순)
                          regularStudents.sort((a, b) => b.totalScore - a.totalScore);

                          console.log('정렬 후 regularStudents:', regularStudents);

                          // 평점 구간 계산
                          const aCount = Math.round((lectureInfo.currentCap * (evaluateInterval?.A || 0)) / 100);
                          const bCount = Math.round((lectureInfo.currentCap * (evaluateInterval?.B || 0)) / 100);

                          console.log('평점 구간 계산:', {
                            currentCap: lectureInfo.currentCap,
                            evaluateInterval,
                            aCount,
                            bCount
                          });

                          // 학생들에게 평점 구간 부여
                          const studentsWithInterval = regularStudents.map((student, index) => {
                            let interval = 'C~D';
                            if (index < aCount) {
                              interval = 'A';
                            } else if (index < aCount + bCount) {
                              interval = 'B';
                            }
                            return { ...student, interval, rank: index + 1 };
                          });

                          // 구간별로 분류
                          const aStudents = studentsWithInterval.filter(s => s.interval === 'A');
                          const bStudents = studentsWithInterval.filter(s => s.interval === 'B');
                          const cdStudents = studentsWithInterval.filter(s => s.interval === 'C~D');

                          console.log('구간별 학생 분류:', {
                            aStudents,
                            bStudents,
                            cdStudents
                          });

                          // 디폴트 구분자 위치 계산 (초기화는 하지 않고 계산만)
                          const defaultGradeDividers = {
                            A: gradeDividers.A !== null ? gradeDividers.A : Math.floor(aStudents.length / 2),
                            B: gradeDividers.B !== null ? gradeDividers.B : Math.floor(bStudents.length / 2),
                            CD: gradeDividers.CD.length > 0 ? gradeDividers.CD : [
                              Math.floor(cdStudents.length / 4),
                              Math.floor(cdStudents.length / 2),
                              Math.floor((cdStudents.length * 3) / 4)
                            ]
                          };

                          // 세부 평점 계산 함수
                          const getDetailedGrade = (student, index, interval) => {
                            if (interval === 'A') {
                              // 구분선 위(인덱스가 작거나 같음)는 A+, 아래는 A0
                              return index <= (defaultGradeDividers.A || 0) ? 'A+' : 'A0';
                            } else if (interval === 'B') {
                              return index <= (defaultGradeDividers.B || 0) ? 'B+' : 'B0';
                            } else if (interval === 'C~D') {
                              const dividers = defaultGradeDividers.CD || [];
                              if (index <= (dividers[0] || 0)) return 'C+';
                              if (index <= (dividers[1] || 0)) return 'C0';
                              if (index <= (dividers[2] || 0)) return 'D+';
                              return 'D0';
                            }
                            return interval;
                          };

                          const renderStudentTable = (students, title, bgColor, interval) => {
                            const handleDragStart = (idx, interval) => (e) => {
                              e.dataTransfer.effectAllowed = 'move';
                              setDraggingDivider({ interval, originalIdx: idx });
                            };

                            const handleDragOver = (e) => {
                              e.preventDefault();
                              e.dataTransfer.dropEffect = 'move';
                            };

                            const handleDrop = (targetIdx) => (e) => {
                              e.preventDefault();
                              if (!draggingDivider) return;

                              const { interval: dragInterval, originalIdx } = draggingDivider;

                              if (interval === 'A' && dragInterval === 'A') {
                                setGradeDividers(prev => ({ ...prev, A: targetIdx }));
                              } else if (interval === 'B' && dragInterval === 'B') {
                                setGradeDividers(prev => ({ ...prev, B: targetIdx }));
                              } else if (interval === 'C~D' && dragInterval === 'C~D') {
                                const cdIdx = defaultGradeDividers.CD.indexOf(originalIdx);
                                if (cdIdx !== -1) {
                                  const newCD = [...defaultGradeDividers.CD];
                                  newCD[cdIdx] = targetIdx;
                                  setGradeDividers(prev => ({ ...prev, CD: newCD.sort((a, b) => a - b) }));
                                }
                              }

                              setDraggingDivider(null);
                            };

                            const handleDragEnd = () => {
                              setDraggingDivider(null);
                            };

                            return (
                              <div className="mb-3">
                                <h6 className="fw-semibold mb-2">{title} ({students.length}명)</h6>
                                <div className="table-responsive">
                                  <table className="table table-sm table-bordered table-hover mb-0" style={{ fontSize: '0.85rem' }}>
                                    <thead className={bgColor}>
                                      <tr>
                                        <th className="text-center" style={{ width: '60px' }}>순위</th>
                                        <th className="text-center" style={{ width: '100px' }}>학번</th>
                                        <th className="text-center" style={{ width: '80px' }}>이름</th>
                                        <th className="text-center" style={{ width: '80px' }}>총점</th>
                                        <th className="text-center" style={{ width: '80px' }}>평점</th>
                                      </tr>
                                    </thead>
                                    <tbody>
                                    {students.length === 0 ? (
                                      <tr>
                                        <td colSpan="5" className="text-center text-muted py-3">
                                          해당 구간에 학생이 없습니다.
                                        </td>
                                      </tr>
                                    ) : (
                                      <>
                                        {students.map((student, idx) => {
                                          const detailedGrade = getDetailedGrade(student, idx, interval);

                                          // 구분선을 그려야 하는 위치 확인
                                          const shouldShowDivider = (
                                            (interval === 'A' && idx === defaultGradeDividers.A) ||
                                            (interval === 'B' && idx === defaultGradeDividers.B) ||
                                            (interval === 'C~D' && defaultGradeDividers.CD.includes(idx))
                                          );

                                          return (
                                            <React.Fragment key={student.enrollId}>
                                              <tr
                                                style={{
                                                  position: 'relative',
                                                  borderBottom: shouldShowDivider ? '3px solid #dc3545' : undefined
                                                }}
                                                onDragOver={handleDragOver}
                                                onDrop={handleDrop(idx)}
                                              >
                                                <td className="text-center">{student.rank || '-'}</td>
                                                <td className="text-center">{student.studentNo}</td>
                                                <td className="text-center">{student.name}</td>
                                                <td className="text-center" style={{ position: 'relative' }}>
                                                  {student.totalScore.toFixed(2)}
                                                  {shouldShowDivider && (
                                                    <div
                                                      draggable
                                                      onDragStart={handleDragStart(idx, interval)}
                                                      onDragEnd={handleDragEnd}
                                                      style={{
                                                        position: 'absolute',
                                                        right: '-12px',
                                                        top: '50%',
                                                        transform: 'translateY(-50%)',
                                                        width: '24px',
                                                        height: '24px',
                                                        borderRadius: '50%',
                                                        padding: 0,
                                                        display: 'flex',
                                                        alignItems: 'center',
                                                        justifyContent: 'center',
                                                        fontSize: '14px',
                                                        lineHeight: 1,
                                                        border: '2px solid white',
                                                        boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
                                                        backgroundColor: '#dc3545',
                                                        color: 'white',
                                                        cursor: 'grab',
                                                        userSelect: 'none',
                                                        zIndex: 100
                                                      }}
                                                      onMouseDown={(e) => e.currentTarget.style.cursor = 'grabbing'}
                                                      onMouseUp={(e) => e.currentTarget.style.cursor = 'grab'}
                                                    >
                                                      ⋮
                                                    </div>
                                                  )}
                                                </td>
                                                <td className="text-center">
                                                  <span className={`badge ${
                                                    detailedGrade.startsWith('A') ? 'bg-info' :
                                                    detailedGrade.startsWith('B') ? 'bg-primary' :
                                                    detailedGrade.startsWith('C') || detailedGrade.startsWith('D') ? 'bg-warning' :
                                                    'bg-danger'
                                                  }`}>
                                                    {detailedGrade}
                                                  </span>
                                                </td>
                                              </tr>
                                            </React.Fragment>
                                          );
                                        })}
                                      </>
                                    )}
                                  </tbody>
                                </table>
                              </div>
                            </div>
                            );
                          };

                          return (
                            <div>
                              {/* F 학생 표시 (맨 위) */}
                              {failStudents.length > 0 && (
                                <div className="mb-4 p-3 bg-danger bg-opacity-10 border border-danger rounded">
                                  <h6 className="fw-semibold mb-2 text-danger">
                                    <i className="bi bi-exclamation-triangle-fill me-2"></i>
                                    F 등급 (출석점수 0점) ({failStudents.length}명)
                                  </h6>
                                  <div className="table-responsive">
                                    <table className="table table-sm table-bordered table-hover mb-0" style={{ fontSize: '0.85rem' }}>
                                      <thead className="table-danger">
                                        <tr>
                                          <th className="text-center" style={{ width: '100px' }}>학번</th>
                                          <th className="text-center" style={{ width: '80px' }}>이름</th>
                                          <th className="text-center" style={{ width: '80px' }}>총점</th>
                                          <th className="text-center" style={{ width: '80px' }}>평점</th>
                                        </tr>
                                      </thead>
                                      <tbody>
                                        {failStudents.map((student) => (
                                          <tr key={student.enrollId}>
                                            <td className="text-center">{student.studentNo}</td>
                                            <td className="text-center">{student.name}</td>
                                            <td className="text-center">{student.totalScore.toFixed(2)}</td>
                                            <td className="text-center">
                                              <span className="badge bg-danger">F</span>
                                            </td>
                                          </tr>
                                        ))}
                                      </tbody>
                                    </table>
                                  </div>
                                </div>
                              )}

                              {/* 구간별 학생 수 요약 */}
                              <div className="alert alert-info mb-3">
                                <div className="fw-semibold mb-2">평점 구간별 학생 수</div>
                                <div className="d-flex gap-3">
                                  <span>A: {aStudents.length}명</span>
                                  <span>B: {bStudents.length}명</span>
                                  <span>C~D: {cdStudents.length}명</span>
                                  {failStudents.length > 0 && <span className="text-danger">F: {failStudents.length}명</span>}
                                </div>
                              </div>

                              {/* A, B, C~D 구간을 가로로 3등분 */}
                              <div className="row">
                                <div className="col-md-4">
                                  {renderStudentTable(aStudents, 'A 구간', 'table-success', 'A')}
                                </div>
                                <div className="col-md-4">
                                  {renderStudentTable(bStudents, 'B 구간', 'table-primary', 'B')}
                                </div>
                                <div className="col-md-4">
                                  {renderStudentTable(cdStudents, 'C~D 구간', 'table-info', 'C~D')}
                                </div>
                              </div>
                            </div>
                          );
                        })()}
                        </div>
                      )}
                    </div>
                  </div>
                ) : (
                  <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
                    <div style={{ flex: 1, overflowY: 'auto', padding: '1rem' }}>
                      <h5 className="mb-4">최종 확인 및 확정</h5>

                      {/* 1. 성적평가방식 */}
                      <div className="card mb-3">
                        <div className="card-header bg-light">
                          <h6 className="mb-0">성적 평가 방식</h6>
                        </div>
                        <div className="card-body">
                          <div className="row">
                            <div className="col-md-6">
                              <strong>평가 방법:</strong> {lectureInfo?.subjectTypeName || '-'}
                            </div>
                            <div className="col-md-6">
                              <strong>수강 인원:</strong> {lectureInfo?.currentCap || 0}명
                            </div>
                          </div>
                        </div>
                      </div>

                      {/* 2. 성적산출비율 */}
                      <div className="card mb-3">
                        <div className="card-header bg-light">
                          <h6 className="mb-0">성적 산출 비율</h6>
                        </div>
                        <div className="card-body">
                          <div className="d-flex gap-3 flex-wrap">
                            {gradeRatio.filter(c => c.ratio > 0).map(criterion => (
                              <div key={criterion.gradeCriteriaCd} className="badge bg-primary fs-6">
                                {criterion.gradeCriteriaName}: {criterion.ratio}%
                              </div>
                            ))}
                          </div>
                        </div>
                      </div>

                      {/* 3. 평점구간 설정 정보 (상대평가인 경우) */}
                      {lectureInfo?.subjectTypeCd === 'SUBJ_RELATIVE' && evaluateInterval && (
                        <div className="card mb-3">
                          <div className="card-header bg-light">
                            <h6 className="mb-0">평점 구간 비율</h6>
                          </div>
                          <div className="card-body">
                            <div className="d-flex gap-3">
                              <span className="badge bg-info fs-6">A 구간: {evaluateInterval.A || 0}%</span>
                              <span className="badge bg-primary fs-6">B 구간: {evaluateInterval.B || 0}%</span>
                              <span className="badge bg-warning fs-6">C~D 구간: {100 - (evaluateInterval.A || 0) - (evaluateInterval.B || 0)}%</span>
                            </div>
                          </div>
                        </div>
                      )}

                      {/* 4. 학생별 최종 성적 목록 */}
                      <div className="card">
                        <div className="card-header bg-light">
                          <h6 className="mb-0">학생별 최종 성적 (총점 내림차순)</h6>
                        </div>
                        <div className="card-body">
                          {(() => {
                            if (totalScorePreview.length === 0) {
                              return (
                                <div className="alert alert-warning">
                                  총점 데이터가 없습니다. 이전 단계로 돌아가서 총점을 계산해주세요.
                                </div>
                              );
                            }

                            // 학생 데이터 매핑 및 평점 계산
                            const studentsWithGrades = totalScorePreview.map(item => {
                              const student = studentMap.get(item.enrollId);
                              const name = student ? (`${student.lastName || ''}${student.firstName || ''}`.trim() || student.studentName || '-') : '-';
                              const studentNo = student ? (student.studentNo || student.userId || '-') : '-';

                              // 출석 점수로 F 등급 판단
                              const attendanceSection = item.breakdown.find(b => b.code === 'ATTD');
                              const attendanceScore = attendanceSection?.rawScore || 0;
                              const isFail = attendanceScore === 0;

                              // 평점 계산 (상대평가인 경우)
                              let finalGrade = '-';
                              if (isFail) {
                                finalGrade = 'F';
                              } else if (lectureInfo?.subjectTypeCd === 'SUBJ_RELATIVE') {
                                // modalStep 2에서 계산한 로직 재사용
                                const regularStudents = totalScorePreview
                                  .map(it => {
                                    const attSec = it.breakdown.find(b => b.code === 'ATTD');
                                    return {
                                      enrollId: it.enrollId,
                                      totalScore: it.totalScore,
                                      isFail: (attSec?.rawScore || 0) === 0
                                    };
                                  })
                                  .filter(s => !s.isFail)
                                  .sort((a, b) => b.totalScore - a.totalScore);

                                const myIndex = regularStudents.findIndex(s => s.enrollId === item.enrollId);
                                if (myIndex !== -1) {
                                  const aCount = Math.round((lectureInfo.currentCap * (evaluateInterval?.A || 0)) / 100);
                                  const bCount = Math.round((lectureInfo.currentCap * (evaluateInterval?.B || 0)) / 100);

                                  let interval = 'C~D';
                                  if (myIndex < aCount) interval = 'A';
                                  else if (myIndex < aCount + bCount) interval = 'B';

                                  // 세부 평점 계산
                                  const defaultGradeDividers = {
                                    A: gradeDividers.A !== null ? gradeDividers.A : Math.floor(aCount / 2),
                                    B: gradeDividers.B !== null ? gradeDividers.B : Math.floor(bCount / 2),
                                    CD: gradeDividers.CD.length > 0 ? gradeDividers.CD : [
                                      Math.floor((lectureInfo.currentCap - aCount - bCount) / 4),
                                      Math.floor((lectureInfo.currentCap - aCount - bCount) / 2),
                                      Math.floor(((lectureInfo.currentCap - aCount - bCount) * 3) / 4)
                                    ]
                                  };

                                  if (interval === 'A') {
                                    const aStudents = regularStudents.slice(0, aCount);
                                    const aIndex = aStudents.findIndex(s => s.enrollId === item.enrollId);
                                    finalGrade = aIndex <= defaultGradeDividers.A ? 'A+' : 'A0';
                                  } else if (interval === 'B') {
                                    const bStudents = regularStudents.slice(aCount, aCount + bCount);
                                    const bIndex = bStudents.findIndex(s => s.enrollId === item.enrollId);
                                    finalGrade = bIndex <= defaultGradeDividers.B ? 'B+' : 'B0';
                                  } else {
                                    const cdStudents = regularStudents.slice(aCount + bCount);
                                    const cdIndex = cdStudents.findIndex(s => s.enrollId === item.enrollId);
                                    const dividers = defaultGradeDividers.CD;
                                    if (cdIndex <= (dividers[0] || 0)) finalGrade = 'C+';
                                    else if (cdIndex <= (dividers[1] || 0)) finalGrade = 'C0';
                                    else if (cdIndex <= (dividers[2] || 0)) finalGrade = 'D+';
                                    else finalGrade = 'D0';
                                  }
                                }
                              }

                              return {
                                enrollId: item.enrollId,
                                studentNo,
                                name,
                                totalScore: item.totalScore,
                                finalGrade,
                                isFail,
                                breakdown: item.breakdown
                              };
                            });

                            // F 학생과 일반 학생 분리 후 정렬
                            const failStudents = studentsWithGrades.filter(s => s.isFail);
                            const regularStudents = studentsWithGrades.filter(s => !s.isFail);

                            // 정렬: 일반 학생은 총점 내림차순, F는 맨 아래
                            const sortedStudents = [...regularStudents, ...failStudents];

                            return (
                              <div className="table-responsive">
                                <table className="table table-sm table-hover align-middle mb-0" style={{ fontSize: '0.9rem' }}>
                                  <thead className="table-light">
                                    <tr>
                                      <th className="text-center" style={{ width: '60px' }}>순번</th>
                                      <th className="text-center" style={{ width: '100px' }}>학번</th>
                                      <th className="text-center" style={{ width: '80px' }}>이름</th>
                                      {totalScoreSections.map(section => (
                                        <th key={section.code} className="text-center" style={{ width: '80px' }}>
                                          {section.label}
                                          <br />
                                          <small className="text-muted">({section.ratio}%)</small>
                                        </th>
                                      ))}
                                      <th className="text-center bg-light" style={{ width: '80px' }}>
                                        <strong>총점</strong>
                                      </th>
                                      <th className="text-center bg-success bg-opacity-10" style={{ width: '80px' }}>
                                        <strong>평점</strong>
                                      </th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    {sortedStudents.map((student, idx) => (
                                      <tr key={student.enrollId} className={student.isFail ? 'table-danger' : ''}>
                                        <td className="text-center">{idx + 1}</td>
                                        <td className="text-center">{student.studentNo}</td>
                                        <td className="text-center">{student.name}</td>
                                        {student.breakdown.map(section => (
                                          <td key={section.code} className="text-center">
                                            {section.rawScore !== null && section.rawScore !== undefined
                                              ? section.rawScore.toFixed(1)
                                              : '-'}
                                          </td>
                                        ))}
                                        <td className="text-center bg-light">
                                          <strong>{student.totalScore.toFixed(2)}</strong>
                                        </td>
                                        <td className="text-center">
                                          <span className={`badge fs-6 ${
                                            student.finalGrade.startsWith('A') ? 'bg-info' :
                                            student.finalGrade.startsWith('B') ? 'bg-primary' :
                                            student.finalGrade.startsWith('C') || student.finalGrade.startsWith('D') ? 'bg-warning' :
                                            student.finalGrade === 'F' ? 'bg-danger' :
                                            'bg-secondary'
                                          }`}>
                                            {student.finalGrade}
                                          </span>
                                        </td>
                                      </tr>
                                    ))}
                                  </tbody>
                                </table>
                              </div>
                            );
                          })()}
                        </div>
                      </div>
                    </div>
                  </div>
                )}
              </div>
              <div className="modal-footer py-2">
                {modalStep > 1 && (
                  <button
                    type="button"
                    className="btn btn-outline-secondary btn-sm"
                    onClick={() => setModalStep(modalStep - 1)}
                  >
                    이전 작업으로
                  </button>
                )}
                <div className="ms-auto">
                  {modalStep < 3 ? (
                    <button
                      type="button"
                      className="btn btn-primary btn-sm"
                      onClick={() => setModalStep(modalStep + 1)}
                      disabled={modalStep === 1 && (() => {
                        const requiredCriteria = gradeRatio.filter(c => c.ratio > 0);
                        const statusMap = {
                          'ATTD': attendanceScoreStatus,
                          'TASK': taskScoreStatus,
                          'EXAM': examScoreStatus,
                          'PRAC': pracScoreStatus,
                          'MISC': miscScoreStatus
                        };
                        const allReady = requiredCriteria.every(c => statusMap[c.gradeCriteriaCd] === 'all');
                        // 모든 성적이 준비되고 총점도 계산되어야 함
                        return !allReady || totalScorePreview.length === 0;
                      })()}
                      style={{ padding: '6px 12px' }}
                    >
                      다음 작업
                    </button>
                  ) : (
                    <button
                      type="button"
                      className="btn btn-primary btn-sm"
                      onClick={confirmAndSaveGrades}
                      style={{ padding: '6px 12px' }}
                    >
                      확정
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}
