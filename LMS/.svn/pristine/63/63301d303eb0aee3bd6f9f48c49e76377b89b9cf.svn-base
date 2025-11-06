package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.info.LctTaskWeightInfo;
import kr.or.jsu.classroom.dto.response.task.EachStudentGrouptaskScoreResp;
import kr.or.jsu.classroom.dto.response.task.EachStudentIndivtaskScoreResp;

@Mapper
public interface LctTaskMapper {
	
	/**
	 * 강의에서 출제한 과제의 가중치를 일괄 수정합니다.
	 * 
	 * @param values
	 * @return
	 */
	public int upsertWeightValues(
		List<LctTaskWeightInfo> values
	);
	
	/**
	 * 특정 강의에서 출제된 과제들의 가중치를 조회합니다. <br>
	 * (삭제된 강의 제외)
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<LctTaskWeightInfo> selectWeightValues(
		String lectureId
	);
	
	/**
	 * 종강 시 필요한 학생들의 개인과제 점수를 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<EachStudentIndivtaskScoreResp> selectIndivtaskAndEachStudentScore(
		String lectureId
	);
	
	/**
	 * 종강 시 필요한 학생들의 조별과제 점수를 가져옵니다.
	 * 
	 * @param lectureId
	 * @return
	 */
	public List<EachStudentGrouptaskScoreResp> selectGrouptaskAndEachStudentScore(
		String lectureId
	);
	
}
