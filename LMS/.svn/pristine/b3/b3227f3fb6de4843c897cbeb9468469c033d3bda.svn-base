package kr.or.jsu.classroom.dto.db;

import java.io.Serializable;
import java.util.List;

import kr.or.jsu.classroom.dto.info.GrouptaskCrewInfo;
import kr.or.jsu.classroom.dto.info.GrouptaskGroupInfo;
import kr.or.jsu.classroom.dto.info.GrouptaskSubmitInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "groupId")
public class GrouptaskGroupWithCrewDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String groupId;
	private GrouptaskGroupInfo groupInfo;
	private GrouptaskSubmitInfo submitInfo;
	private List<GrouptaskCrewInfo> crewInfoList;
}