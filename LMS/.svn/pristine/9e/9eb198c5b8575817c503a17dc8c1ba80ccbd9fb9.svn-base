package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import kr.or.jsu.core.dto.response.FileDetailResp;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "indivtaskId")
public class LctIndivtaskDetailResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String indivtaskId;
	private String indivtaskName;
	private String indivtaskDesc;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	
	private String fileId;
	private List<FileDetailResp> attachFileList;
	
	private List<IndivtaskSubmitResp> studentSubmitList;
	
	@Data
	@EqualsAndHashCode(of = {"indivtaskId", "enrollId"})
	public static class IndivtaskSubmitResp implements Serializable {
		private static final long serialVersionUID = 1L;

		private String indivtaskId; 
		
		private String enrollId;
		private LocalDateTime submitAt;
		private String submitDesc;
		
		private Integer evaluScore;
		private String evaluDesc;
		private LocalDateTime evaluAt;
		
		private List<FileDetailResp> submitFiles;
	}
}

