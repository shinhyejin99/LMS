package kr.or.jsu.classroom.student.service;

import java.util.List;

import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.LctExamLabelResp_STU;
import kr.or.jsu.vo.UsersVO;

/**
 * 클래스룸에서 학생의 시험에 대한 활동을 처리합니다.
 * 
 * @author 송태호
 * @since 2025. 10. 8.
 */
public interface ClassStuExamService {
	
	/**
	 * 특정 강의에 대한 시험 목록 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 목록을 반환합니다. <br>
	 * 해당 시험에 대해 자신의 응시 기록이 있는지 확인하여 추가 기록합니다.
	 * 
	 * @param user
	 * @param lectureId
	 * @return
	 */
	public List<LctExamLabelResp_STU> readExamList(
		UsersVO user
		, String lectureId
	);
	
	/**
	 * 특정 강의의 특정 시험 상세조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 정보를 반환합니다. <br>
	 * (교수가 출제한 시험에 대한 내용 + 자신의 응시 기록)
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return
	 */
	public ExamAndEachStudentsSubmitResp readExam(
		UsersVO user
		, String lectureId
		, String lctExamId
	);
	
	public void readNotSubmittedExamList(
		// TODO 온라인시험 나오면 하기
	);
	
}