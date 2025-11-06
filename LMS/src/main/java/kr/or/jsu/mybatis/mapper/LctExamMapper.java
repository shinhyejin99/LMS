package kr.or.jsu.mybatis.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.db.exam.StudentAndExamSubmitDTO;
import kr.or.jsu.classroom.dto.info.ExamSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctExamInfo;
import kr.or.jsu.classroom.dto.response.exam.EachStudentExamScoreResp;

@Mapper
public interface LctExamMapper {
	
	/**
	 * 오프라인 시험 결과를 저장할 레코드를 생성합니다.
	 * 
	 * @param newExam
	 * @return 생성된 레코드 수
	 */
	public int insertOfflineExam(
		LctExamInfo newExam
	);
	
	/**
	 * 오프라인 시험 결과를 일괄 저장합니다.
	 * 
	 * @param lctExamId 대상 시험ID
	 * @param eachResult 수강ID, 점수 필드를 채운 리스트
	 * @param submitAt 제출일시. 오프라인일 경우 시험종료시간
	 * @return
	 */
	public int insertOfflineExamSubmit(
		String lctExamId
		, List<ExamSubmitInfo> eachResult
		, LocalDateTime submitAt
	);
	
	/**
	 * 특정 강의의 시험에 대한 정보 + 대상자 수, 응시자 수 목록을 조회합니다.
	 * 
	 * @param lectureId 대상 강의ID
	 * @return 시험 목록
	 */
	public List<LctExamInfo> selectExamList_PRF(
		String lectureId
	);
	
	/**
	 * 특정 강의의 시험에 대한 정보 + 수강생 한 명의 응시 여부를 조회합니다.
	 * 
	 * @param lectureId 대상 강의ID
	 * @return 시험 목록
	 */
	public List<LctExamInfo> selectExamList_STU(
		String lectureId
		, String enrollId
	);
	
	/**
	 * 시험 1~n건의 가중치를 수정합니다.
	 * 
	 * @param weightValues 시험ID, 가중치 리스트
	 * @return 수정된 레코드 수
	 */
	public int updateWeightValue(
		List<LctExamInfo> weightValues
	);
	
	/**
	 * 특정 시험에 대한 기본 정보를 가져옵니다.
	 * 
	 * @param lctExamId 시험ID
	 * @return
	 */
	public LctExamInfo selectExam(
		String lctExamId
	);
	
	/**
	 * 특정 시험에 대한 특정 학생의 응시 기록을 가져옵니다.
	 * 
	 * @param lctExamId 시험ID
	 * @param enrollId 학생별 수강ID
	 * @return
	 */
	public ExamSubmitInfo selectSubmit(
		String lctExamId
		, String enrollId
	);
	
	/**
	 * 특정 시험에 대한 학생들의 응시 기록을 가져옵니다. <br>
	 * 수강생 중 응시 기록이 없는 학생이 있을 수 있습니다. (LEFT JOIN) <br>
	 * enrollIds는 null이어서는 안 되며, 요소가 1개 이상이어야 합니다.
	 * 
	 * @param lctExamId
	 * @param enrollIds
	 * @return
	 */
	public List<StudentAndExamSubmitDTO> selectSubmitList(
		String lctExamId
		, List<String> enrollIds
	);
	
	/**
	 * 특정 제출에 대해 기입했던 점수를 수정하고, <br>
	 * 수정 이유를 기록합니다.
	 * 
	 * @param modifiedScore 대상 제출을 특정할 enrollId, lctExamId와 <br>
	 * 수정할 점수(modifiedScore), 이유(modifyReason)를 기입한 객체 
	 * @return 수정된 레코드 수
	 */
	public int updateScore(
		ExamSubmitInfo modifiedScore
	);
	
	public int updateExam(
		LctExamInfo modifiedExam
	);
	
	public int deleteExamSoftly(
		String lctExamId
	);
	
	/**
	 * 종강 시 필요한 학생들의 시험 점수를 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<EachStudentExamScoreResp> selectExamAndEachStudentScore(
		String lectureId
	);
}