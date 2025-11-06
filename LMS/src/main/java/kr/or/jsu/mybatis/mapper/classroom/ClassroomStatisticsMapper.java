package kr.or.jsu.mybatis.mapper.classroom;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.response.statistics.AttendanceStatisticsResp.StuAttendanceCount;
import kr.or.jsu.classroom.dto.response.statistics.AttendanceStatisticsResp.TypeCount;
import kr.or.jsu.classroom.dto.response.statistics.EnrollStuStatisticsResp.DeptCount;
import kr.or.jsu.classroom.dto.response.statistics.EnrollStuStatisticsResp.EnrollmentStats;
import kr.or.jsu.classroom.dto.response.statistics.EnrollStuStatisticsResp.GradeCount;
import kr.or.jsu.classroom.dto.response.statistics.TaskStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.ExamStatisticsResp;

@Mapper
public interface ClassroomStatisticsMapper {
	
	/**
	 * 특정 강의의 총원, 중탈자, 수강생을 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public EnrollmentStats getEnrollStat(
		String lectureId
	);
	
	/**
	 * 특정 강의의 학년별 학생 수를 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<GradeCount> getGradeStat(
		String lectureId
	);
	
	/**
	 * 특정 강의의 학과별 학생 수를 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<DeptCount> getDeptStat(
		String lectureId
	);
	
	/**
	 * 특정 강의의 출석상태별 기록 수를 가져옵니다. <br>
	 * (출석 100건, 결석 20건, 지각 10, 공결 5...)
	 * 
	 * @param lectureId
	 * @return
	 */
	public TypeCount getTotalAttData(
		String lectureId
	);
	
	/**
	 * 특정 강의의 수강생별 출석일을 가져옵니다. <br>
	 * 미정은 제외하며 <br>
	 * 출석/지각/조퇴는 출석 처리 <br>
	 * 결석/공결은 지각 처리
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<StuAttendanceCount> getEachStuAttData(
		String lectureId
	);
	
	/** 1) 개인/조별 과제 개수 */
    public TaskStatisticsResp.TaskTypeCount selectTaskTypeCount(
        String lectureId
    );

    /** 2) 진행중 개인과제 제출율 */
    public TaskStatisticsResp.OngoingIndivtask selectOngoingIndivSubmitRate(
        String lectureId
    );

    /** 2) 진행중 조별과제 제출율 */
    public TaskStatisticsResp.OngoingGrouptask selectOngoingGroupSubmitRate(
        String lectureId
    );

    /** 3) 마감된 개인과제 제출율 */
    public TaskStatisticsResp.ClosedIndivtask selectClosedIndivSubmitRate(
        String lectureId
    );

    /** 3) 마감된 조별과제 제출율 */
    public TaskStatisticsResp.ClosedGrouptask selectClosedGroupSubmitRate(
        String lectureId
    );

    /** 4) 마감된 과제(개인+조별) 학생별 점수 */
    public List<TaskStatisticsResp.ScoresByStudentAndTask> selectScoresByStudentAndTask(
        String lectureId
    );

    /** 시험 타입별 카운트 및 총합 */
    public ExamStatisticsResp.ExamTypeCount selectExamTypeCount(
        String lectureId
    );

    /** 진행 중 시험 제출률 */
    public ExamStatisticsResp.OngoingExam selectOngoingExamSubmitRate(
        String lectureId
    );

    /** 마감된 시험 제출률 */
    public ExamStatisticsResp.ClosedExam selectClosedExamSubmitRate(
        String lectureId
    );

    /** 시험별 학생 점수 집계 */
    public List<ExamStatisticsResp.ScoresByStudentAndExam> selectScoresByStudentAndExam(
        String lectureId
    );
}
