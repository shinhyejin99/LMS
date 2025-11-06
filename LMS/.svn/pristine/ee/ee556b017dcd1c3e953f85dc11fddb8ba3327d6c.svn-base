package kr.or.jsu.classroom.professor.service;

import kr.or.jsu.classroom.dto.response.statistics.AttendanceStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.EnrollStuStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.TaskStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.ExamStatisticsResp;

public interface ClassPrfStatisticsService {
	
	/**
	 * 대시보드의 수강생 관련 통계를 반환합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public EnrollStuStatisticsResp readEnrollmentStat(
		String lectureId
	);
	
	/**
	 * 대시보드의 출석 관련 통계를 반환합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public AttendanceStatisticsResp readAttendanceStat(
		String lectureId
	);
	
	/**
	 * 대시보드의 과제 관련 통계를 반환합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public TaskStatisticsResp readTaskStat(
		String lectureId	
	);
	
	/**
	 * 대시보드의 과제 관련 통계를 반환합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public ExamStatisticsResp readExamStat(
		String lectureId	
	);
	
}
