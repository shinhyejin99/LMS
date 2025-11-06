package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.classroom.dto.info.LctSectionScoreInfo;

@Mapper
public interface LctSectionScoreMapper {
	
	/**
	 * 강의 종강할 때, 최종평점 산출을 위한 점수 데이터를 저장합니다.
	 * 
	 * @param scores 강의의, 학생별, 산출항목에 대한 점수를 기록한 객체
	 * @return 생성된 레코드 수
	 */
	public int upsertSectionScore(
		List<LctSectionScoreInfo> scores
	);
	
	public List<LctSectionScoreInfo> selectSectionScoreList(
		String lectureId
		, String gradeCriteriaCd
	);
	
}
