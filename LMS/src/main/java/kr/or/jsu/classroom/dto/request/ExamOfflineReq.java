package kr.or.jsu.classroom.dto.request;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ExamOfflineReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private String examName;
	private String examDesc;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Integer weightValue;
	
	private List<EachResult> eachResultList;
	
	@Data
	public static class EachResult {
		private String enrollId;
		private int score;
	}
}
