package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.SbjRvwQuestionVO;

@Mapper
public interface SbjRvwQuestionMapper {

	// 과목별 설문 문항 등록
    public int insertSbjRvwQuestion(SbjRvwQuestionVO sbjRvwQuestion);

    // 과목별 설문 문항 목록 조회
    public List<SbjRvwQuestionVO> selectSbjRvwQuestionList();

    // 특정 과목별 설문 문항 조회 
    public SbjRvwQuestionVO selectSbjRvwQuestion(String subjectCd);

    // 과목별 설문 문항 수정
    public int updateSbjRvwQuestion(SbjRvwQuestionVO sbjRvwQuestion);

    // 과목별 설문 문항 삭제 
    public int deleteSbjRvwQuestion( String subjectCd);
}
