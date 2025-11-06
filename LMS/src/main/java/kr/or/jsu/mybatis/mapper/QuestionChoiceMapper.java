package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.QuestionChoiceVO;

@Mapper
public interface QuestionChoiceMapper {

	// 선택지 등록
	public int insertQuestionChoice(QuestionChoiceVO questionChoice);

	// 선택지 전체 목록 조회
	public List<QuestionChoiceVO> selectQuestionChoiceList();

	// 특정 선택지 조회
	public QuestionChoiceVO selectQuestionChoice(String lctExamId);

	// 선택지 정보 수정
	public int updateQuestionChoice(QuestionChoiceVO questionChoice);

	// 선택지 삭제 
	public int deleteQuestionChoice(String lctExamId);
}
