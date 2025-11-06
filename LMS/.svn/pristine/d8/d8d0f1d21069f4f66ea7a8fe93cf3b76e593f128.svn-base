package kr.or.jsu.classroom.dto.db.studentManage;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GrouptaskAndStuSubmitDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// 개인과제에 대한
	private String grouptaskId;
	private String grouptaskName;	
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private String attachFileId;
	
	private String groupId;
	private String groupName;
	
	// 제출 & 평가 내용
	private String submitDesc;
	private LocalDateTime submitAt;
	private Integer evaluScore;
	private LocalDateTime evaluAt;
	private String evaluDesc;
	private String submitFileId;
}