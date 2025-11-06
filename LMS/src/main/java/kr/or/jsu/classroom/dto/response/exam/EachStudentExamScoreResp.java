package kr.or.jsu.classroom.dto.response.exam;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class EachStudentExamScoreResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	private List<StudentExamScoreResp> scoreList;	
}

@Data
class StudentExamScoreResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctExamId;
	private String isTarget;
	private LocalDateTime submitAt;
	private Integer score;
}