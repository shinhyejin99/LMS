package kr.or.jsu.classroom.common.service;

import java.util.List;

import kr.or.jsu.classroom.dto.db.LectureWithScheduleDTO;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.response.lecture.LctGraderatioResp;
import kr.or.jsu.classroom.dto.response.lecture.LctWeekbyResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.ProfessorInfoResp;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.vo.UsersVO;

/**
 * 강의, 클래스룸에 대해 교수, 학생이 요청하는 정보를 가공하여 제공합니다.
 * 
 * @author 송태호
 * @since 2025. 10. 01.
 */
public interface ClassroomCommonService {
	
	/**
	 * 강의 정보(강의+과목 기본정보, 수강생 수)를 확인합니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public LectureLabelResp readLecture(String lectureId);
	
	/**
	 * 강의 담당교수의 정보를 확인합니다.
	 * 
	 * @param lectureId
	 */
	public ProfessorInfoResp readProfessor(String lectureId);
	
	/**
	 * 강의 주차계획을 확인합니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 강의ID와 강의 정보, 주차계획이 들어있는 DTO
	 */
	public List<LctWeekbyResp> readLecturePlan(String lectureId);
	
	/**
	 * 강의의 시간표를 확인합니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 강의ID와 시간표가 들어있는 DTO
	 */
	public LectureWithScheduleDTO readLectureSchedule(String lectureId);
	
	/**
	 * 강의의 성적산출비율을 확인합니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 성적산출비율 합이 100인 리스트
	 */
	public List<LctGraderatioResp> readLectureGraderatio(
		String lectureId
	);
	
	/**
	 * 사용자가 강의와 관련있는지 검증합니다.
	 * 
	 * @param userNo 로그인한 사용자 고유번호(학번, 교번)
	 * @param lectureId 강의(클래스룸) ID
	 * @return 관련 있을 경우 true
	 */
	public boolean isRelevantClassroom(
		String userNo
		, String lectureId
	);
	
	/**
	 * 강의ID와 학번으로 수강 기록을 반환합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param stuNo 학번
	 * @return null일 경우 강의ID가 없거나, 학번이 없거나, 학생이 강의를 수강하지 않은것임.
	 */
	public StuEnrollLctInfo getEnrollInfo(
		String lectureId
		, String stuNo
	);
	
	/**
	 * 강의ID, 게시글ID, 파일 순번과 현재 로그인 사용자 ID를 받아, <br>
	 * 파일 액세스 자격이 있는지 확인한 후, <br>
	 * 파일 메타데이터와 실제 파일을 반환합니다.
	 * 
	 * @param lectureId
	 * @param lctPostId
	 * @param fileOrder
	 * @param realUser
	 * @return 메타데이터 + 실제 파일
	 */
	public FileDetailInfo getPostAttachedFile(
		String lectureId
		, String lctPostId
		, int fileOrder
		, UsersVO realUser
	);
	
	/**
	 * 강의ID, 과제 타입, 과제ID, 파일 순번과 현재 로그인 사용자 ID를 받아, <br>
	 * 파일 액세스 자격이 있는지 확인한 후, <br>
	 * 파일 메타데이터와 실제 파일을 반환합니다.
	 * 
	 * @param lectureId
	 * @param lctPostId
	 * @param fileOrder
	 * @param realUser
	 * @return 메타데이터 + 실제 파일
	 */
	public FileDetailInfo getTaskAttachedFile(
		String lectureId
		, String type
		, String taskId
		, int fileOrder
		, UsersVO realUser
	);
	
	/**
	 * 요청한 사용자(교수, 학생)가 <br>
	 * 개인과제 제출에 첨부한 파일에 대한 권한이 있는지 확인하고 <br>
	 * 파일 메타데이터를 반환합니다. <br><br>
	 * 유효성 검사 목록 <br>
	 * 1. 사용자가 강의와 관계있는가? <br>
	 * 2. 요청한 과제가 강의 소속인가? <br>
	 * 3-1. (사용자가 교수일 경우) 학번에 해당하는 학생이 수강중인가? <br>
	 * 3-2. 그 학생이 해당 과제를 제출했는가? <br>
	 * 3-3. 학생이 파일을 첨부했는가? <br>
	 * 3-4. 학생의 파일이 삭제되었는가? <br>
	 * 3-4. 학생의 파일묶음에 해당하는 순번이 있는가? <br>
	 * 4-1. (사용자가 학생일 경우) 학번에 해당하는 학생이 나 자신인가? <br>
	 * 4-2. 내가 해당 과제를 제출했는가? <br>
	 * 4-3. 내가 파일을 첨부했는가? <br>
	 * 4-4. 내가 올린 파일이 삭제되었는가? <br>
	 * 4-5. 내가 올린 파일묶음에 요청한 순번이 있는가?
	 * 
	 * @param lectureId 강의ID
	 * @param indivtaskId 과제ID
	 * @param studentNo 제출한 학생 학번
	 * @param fileOrder 요청한 파일 순번
	 * @param realUser 로그인한 사용자 정보
	 * @return 파일 메타데이터
	 */
	public FileDetailInfo getIndivtaskSubmitAttachedFile(
		String lectureId
		, String indivtaskId
		, String studentNo
		, int fileOrder
		, UsersVO realUser
	);
	
	/**
	 * 강의ID와 학번으로 학생의 사진 파일을 요청하면 <br>
	 * 학생인 경우 같은 / 수강한 강의 담당 교수인지 확인한 후 <br>
	 * 파일 메타데이터와 실제 파일을 반환합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param targetStudentNo 학번
	 * @param realuser 로그인한 사용자 정보
	 * @return
	 */
	public FileDetailInfo getStudentIdPhoto(
		String lectureId
		, String targetStudentNo
		, UsersVO realUser
	);
	
	/**
	 * 교번으로 교수의 사진 파일을 요청하면 <br>
	 * 파일 메타데이터와 실제 파일을 반환합니다.
	 * 
	 * @param professorNo 교번
	 * @return 파일 메타데이터
	 */
	public FileDetailInfo getProfessorIdPhoto(
		String professorNo
	);
}