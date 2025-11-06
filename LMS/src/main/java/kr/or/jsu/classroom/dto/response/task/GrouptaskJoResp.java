package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import kr.or.jsu.core.dto.response.FileDetailResp;
import lombok.Data;

@Data
public class GrouptaskJoResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// 조에 대한 정보
	private String groupId;
	private String leaderEnrollId;
	private String groupName;
	
	// 조의 제출에 대한 정보
	private String submitDesc;
	private String submitFileId;
	private LocalDateTime submitAt;
	
	private List<FileDetailResp> attachFileList;
	
	// 조의 구성원들에 대한 정보
	private List<CrewResp> crewList;
	
	@Data
	public static class CrewResp implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String enrollId;
		private Integer evaluScore;
		private LocalDateTime evaluAt;
		private String evaluDesc;
	}
}