package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EachStudentIndivtaskScoreResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	private String indivtaskId;
	private String isTarget;
	private LocalDateTime submitAt;
	private Integer score;
	
}
//@Data
//public class EachStudentIndivtaskScoreResp implements Serializable {
//	private static final long serialVersionUID = 1L;
//	
//	private String enrollId;
//	private List<StudentIndivtaskScoreResp> scoreList;
//	
//	@Data
//	public static class StudentIndivtaskScoreResp implements Serializable {
//		private static final long serialVersionUID = 1L;
//		
//		private String grouptaskId;
//		private String isTarget;
//		private LocalDateTime submitAt;
//		private Integer score;
//	}
//}