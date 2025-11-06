package kr.or.jsu.classroom.student.service;

import java.util.List;

import kr.or.jsu.classroom.dto.response.ender.ScoreAndGPAResp;
import kr.or.jsu.classroom.dto.response.review.LectureReviewQuestionResp;
import kr.or.jsu.vo.UsersVO;

public interface ClassStuEnderService {
	
	/**
	 * 특정 학생이 수강완료했지만 강의평가를 작성하지 않은 강의ID를 반환합니다.
	 * 
	 * @param user
	 * @return
	 */
	public List<String> reviewNeededLectures(
		UsersVO user
	);
	
	/**
	 * 수강 완료한 강의에 대한 강의평가 질문 목록을 가져옵니다.
	 * 
	 * @param user
	 * @param lectureId
	 * @return
	 */
	public List<LectureReviewQuestionResp> readReviewQuestionList(
		UsersVO user
		, String lectureId
	);
	
	/**
	 * 사용자가 강의 수강생인지 확인 후, 작성한 강의평가를 저장합니다. 
	 * 
	 * @param user
	 * @param lectureId 강의ID
	 * @param resultJson 강의평가 JSON
	 */
	public void createReview(
		UsersVO user
		, String lectureId
		, String resultJson	
	);
	
	/**
	 * 학생이 해당 강의를 수강완료했는지 확인 후 <br>
	 * 평점(코드+숫자), 변경된 평점과 변경 이유를 반환합니다.
	 * 
	 * @param user
	 * @param lectureId
	 * @return
	 */
	public ScoreAndGPAResp readLectureScore(
		UsersVO user
		, String lectureId
	);
	
}
