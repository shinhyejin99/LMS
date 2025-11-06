package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.ExamSubmitDetailVO;

@Mapper
public interface ExamSubmitDetailMapper {

	public int insertExamSubmitDetail(ExamSubmitDetailVO examSubmitDetail);
	public List<ExamSubmitDetailVO> selectExamSubmitDetailList();
	public int updateExamSubmitDetail(ExamSubmitDetailVO examSubmitDetail);
	public int deleteExamSubmitDetail(String lctExamId);
}
