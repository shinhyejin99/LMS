package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.GrouptaskGroupVO;

@Mapper
public interface GrouptaskGroupMapper {

	public int insertGroup(GrouptaskGroupVO newGroup);
	
	public List<GrouptaskGroupVO> selectGroupList();
	
	public GrouptaskGroupVO selectGroup(String groupId);
	
	public int updateGroup(GrouptaskGroupVO group);
	
	public int deleteGroupSoft(String groupId);
	
	public int deleteGroupHard(String groupId);
}
