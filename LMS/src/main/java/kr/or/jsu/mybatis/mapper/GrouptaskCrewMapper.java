package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.GrouptaskCrewVO;

@Mapper
public interface GrouptaskCrewMapper {

	public int insertGrouptaskCrew(GrouptaskCrewVO grouptaskCrew);
	public List<GrouptaskCrewVO> selectGrouptaskCrewList();
	public int updateGrouptaskCrew(GrouptaskCrewVO grouptaskCrew);
	public int deleteGrouptaskCrew(String groupId);
}
