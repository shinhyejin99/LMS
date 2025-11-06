package kr.or.jsu.classroom.dto.info;

import java.time.LocalDateTime;

import kr.or.jsu.core.dto.info.StudentInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"groupId", "enrollId"})
public class GrouptaskCrewInfo {
	private String groupId;
	private String enrollId;
	private Integer evaluScore;
	private LocalDateTime evaluAt;
	private String evaluDesc;
	
	// 필요할 때 enrollId로 찾아 넣어서 쓰면 됨. 
	private StudentInfo studentInfo;
}
