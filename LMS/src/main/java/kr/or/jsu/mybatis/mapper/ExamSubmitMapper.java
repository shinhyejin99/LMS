package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.ExamSubmitVO;

@Mapper
public interface ExamSubmitMapper {

	public int insertExamSubmit(ExamSubmitVO examSubmit);
	public List<ExamSubmitVO> selectExamSubmitList();
	public int updateExamSubmit(ExamSubmitVO examSubmit);
	public int deleteExamSubmit(String enrollId);
}
