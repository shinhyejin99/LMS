package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.GrouptaskSubmitVO;

@Mapper
public interface GrouptaskSubmitMapper {

//	public int insertSubmit(GrouptaskSubmitVO newSubmit);
	
	public List<GrouptaskSubmitVO> selectSubmitList();
	
//	public GrouptaskSubmitVO selectSubmit();
	
//	public int updateSubmit();
	
//	public int deleteSubmitSoft();
	
//	public int deleteSubmitHard();
}
