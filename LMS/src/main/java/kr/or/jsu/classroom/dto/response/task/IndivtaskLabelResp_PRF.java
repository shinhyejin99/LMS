package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "indivtaskId")
public class IndivtaskLabelResp_PRF implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String indivtaskId;
	private String indivtaskName;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private boolean hasAttachFile;
	
	// 몇 명이 과제를 제출했는가?
	private int submittedTask;
	// 몇 명에게 과제가 출제되었는가?
	private int targetStudentCnt;
}