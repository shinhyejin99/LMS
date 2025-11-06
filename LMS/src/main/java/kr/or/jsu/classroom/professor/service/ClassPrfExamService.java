package kr.or.jsu.classroom.professor.service;

import java.util.List;

import kr.or.jsu.classroom.dto.request.ExamOfflineReq;
import kr.or.jsu.classroom.dto.request.ExamWeightValueModReq;
import kr.or.jsu.classroom.dto.request.exam.ExamModifyReq;
import kr.or.jsu.classroom.dto.request.exam.ScoreModifyReq;
import kr.or.jsu.classroom.dto.response.exam.EachStudentExamScoreResp;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.LctExamLabelResp_PRF;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 13.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      	수정자            수정내용
 *  -----------   	-------------    ---------------------------
 *  2025.10.13.    	송태호	          최초 생성	
 *
 * </pre>
 */ 
public interface ClassPrfExamService {
	
	/**
	 * 오프라인 시험 결과를 기록합니다. <br>
	 * 결과를 기록한 후 해당 페이지로 리다이렉트하기 위해 생성된 시험ID를 반환합니다.
	 * 
	 * @param newExam 시험에 대한 정보
	 * @param eachResult 각 학생의 수강ID & 시험점수를 기록
	 * @return 셀렉트키로 생성된 시험ID
	 */
	public String createOfflineExamRecord(
		ExamOfflineReq examAndEachResults
	);
	
	/**
	 * 교수 사용자의 요청으로 특정 강의의 모든 시험 목록을 조회합니다. <br>
	 * 현재 제출자 수 + 응시 대상자 수를 추가 조회할 수 있습니다. 
	 * 
	 * @param lectureId 대상 강의ID
	 * @return 강의 목록
	 */
	public List<LctExamLabelResp_PRF> readExamList(
		String lectureId
	);
	
	/**
	 * 요청한 시험들의 가중치를 일괄 변경합니다. <br>
	 * 수정에 따라 수정한 후, 가중치 합계가 100이 되지 않을 경우 수정을 취소하며 경고합니다. 
	 * 
	 * @param lectureId 강의ID
	 * @param request 해당 강의에서 출제한 시험과 변경할 가중치 목록
	 */
	public void modifyAllWeightValues(
		String lectureId
		, List<ExamWeightValueModReq> request
	);
	
	/**
	 * 강의에서 출제된 특정 시험에 대한 내용과 함께 <br>
	 * 중간에 하차한 수강생을 포함한 모든 학생들의 응시 기록과 점수를 반환합니다. 
	 * 
	 * @param lectureId 강의ID
	 * @param lctExamId 시험ID
	 */
	public ExamAndEachStudentsSubmitResp readExamDetail(
		String lectureId
		, String lctExamId
	);
	
	/**
	 * 1. 사용자가 해당 강의 담당 교수인가? <br>
	 * 2. 시험이 해당 강의 소속인가? <br>
	 * 3. 해당 수강ID가 강의 소속인가? <br>
	 * 4. 수강ID의 해당 시험 응시 기록이 있는가? <br>
	 * 이상의 요청에 대한 유효성 검사 이후 <br>
	 * 특정 시험의 특정 수강생에 대한 수정된 점수와 그 이유를 기록합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param lctExamId 시험ID
	 * @param request 대상 학생 학번, 수정된 점수, 수정 이유
	 */
	public void modifyScore(
		String lectureId
		, String lctExamId
		, ScoreModifyReq request
	);
	
	/**
	 * 1. 사용자가 해당 강의 담당 교수인가? <br>
	 * 2. 시험이 해당 강의 소속인가? <br>
	 * 유효성 검사 이후 시험에 대한 정보를 수정합니다.
	 * 
	 * @param lectureId
	 * @param lctExamId
	 * @param request
	 */
	public void modifyExam(
		String lectureId
		, String lctExamId
		, ExamModifyReq request
	);
	
	/**
	 * 1. 사용자가 해당 강의 담당 교수인가? <br>
	 * 2. 시험이 해당 강의 소속인가? <br>
	 * 유효성 검사 이후 시험을 삭제 상태로 변경합니다.
	 * 
	 * @param lectureId
	 * @param lctExamId
	 */
	public void removeExam(
		String lectureId
		, String lctExamId
	);
	
	/**
	 * 특정 강의의 (삭제되지 않은) 시험과, 모든 시험별 학생의 점수를 가져옵니다. <br>
	 * 응시하지 않은 경우 0점, 응시했으나 교수가 채점하지 않은 경우 null
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<EachStudentExamScoreResp> readAllExamAndEachStudentScore(
		String lectureId
	);
	
}
