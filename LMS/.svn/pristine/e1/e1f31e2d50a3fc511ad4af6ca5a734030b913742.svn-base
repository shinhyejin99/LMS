package kr.or.jsu.classroom.dto.response.student;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class StudentsAllGrouptaskSubmitResp implements Serializable {
	private static final long serialVersionUID = 1L;

	// 개인과제에 대한
	private String grouptaskId;
	private String grouptaskName;	
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	// 조별과제의 파일첨부여부
	private boolean taskFileAttached;
	
	// 제출 대상인가?
	private boolean isSubmitTarget;
	
	// 제출 & 평가 내용
	private String groupName;
	private String submitDesc;
	private LocalDateTime submitAt;
	private Integer evaluScore;
	private LocalDateTime evaluAt;
	private String evaluDesc;
	// 제출의 파일첨부여부
	private boolean submitFileAttached;
}