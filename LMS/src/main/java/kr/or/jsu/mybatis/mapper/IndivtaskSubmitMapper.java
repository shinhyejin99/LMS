package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.IndivtaskSubmitVO;

@Mapper
public interface IndivtaskSubmitMapper {
	
	public List<IndivtaskSubmitVO> selectSubmitList();
	
	public List<IndivtaskSubmitVO> selectGraduationAssignmentSubmissions(Map<String, Object> paramMap);

//	public int insertSubmit(IndivtaskSubmitVO newSubmit);
//	
//	public IndivtaskSubmitVO selectSubmit();
//	
//	public int updateSubmit();
//	
//	public int deleteSubmitSoft();
//	
//	public int deleteSubmitHard();
}
