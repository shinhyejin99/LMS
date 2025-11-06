package kr.or.jsu.classroom.professor.service;

import java.util.List;

import kr.or.jsu.classroom.dto.response.student.LectureEnrollingStudentResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllAttendanceResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllExamSubmitResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllGrouptaskSubmitResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllIndivtaskSubmitResp;

/**
 * 클래스룸에서 교수가 학생 개인에 대한 정보를 다루는 기능에 대한 서비스입니다.
 * 
 * @author 송태호
 * @since 2025. 10. 14.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      	수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 14.   송태호	         최초 생성
 *
 * </pre>
 */
public interface ClassPrfStuManageService {

	/**
	 * 강의의 수강생 목록과 정보를 확인합니다.<br>
	 * 강의 담당교수만 확인할 수 있습니다.
	 * 
	 * @param lectureId 강의ID
	 * @return 강의ID와 수강생 일반 정보가 들어있는 DTO
	 */
	public List<LectureEnrollingStudentResp> readStudentList(String lectureId);
	
	// 1. 학생별 출결 관리
	
	/**
	 * 특정 강의에 대해 생성된 모든 출석회차와, <br>
	 * 그 출석회차에 대한 특정 학생의 출석 기록을 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @param studentNo 학번
	 * @return 위에서 설명했음
	 */
	public List<StudentsAllAttendanceResp> readStudentAttendanceList(
		String lectureId
		, String studentNo
	);
	
	// 1-3. 특정 회차의 출결을 바꿔줄 수 있어야 함
	
	// 2. 학생별 개인과제 제출 관리
	
	/**
	 * 특정 강의에서 출제된 진행중인+마감된 개인과제와 <br>
	 * 그 개인과제에 대한 특정 학생의 제출 기록을 가져옵니다.
	 * 
	 * @param lectureId
	 * @param studentNo
	 * @return 위에서 설명했음
	 */
	public List<StudentsAllIndivtaskSubmitResp> readStudentIndivtaskSubmitList(
		String lectureId
		, String studentNo
	);
	
	// 3. 학생별 조별과제 제출 관리
	
	/**
	 * 특정 강의에서 출제된 진행중인+마감된 조별과제와 <br>
	 * 그 조별과제에 대한 특정 학생의 제출 기록을 가져옵니다.
	 * 
	 * @param lectureId
	 * @param studentNo
	 * @return 위에서 설명했음
	 */
	public List<StudentsAllGrouptaskSubmitResp> readStudentGrouptaskSubmitList(
		String lectureId
		, String studentNo
	);
	
	// 3-1. 마감된 조별과제별로 소속된 조, 제출 파일, 개별 평가 확인
	
	// 3-2. 조별과제별 평가 수정할 수 있어야 함
	
	// 4. 학생별 시험 응시 관리
	
	/**
	 * 특정 강의에 대해 출제된 모든 완료되고 삭제되지 않은 시험 목록과, <br>
	 * 그 시험에 대한 특정 학생의 응시 기록을 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @param studentNo 학번
	 * @return 위에서 설명했음
	 */
	public List<StudentsAllExamSubmitResp> readStudentExamSubmitList(
		String lectureId
		, String studentNo	
	);
	
	// 4-1. 완료된 시험별로 점수 확인
	
	// 4-2. 완료된 시험별로 최종점수 수정하고, 수정이유 넣을 수 있어야 함

}
