package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "grouptaskId")
public class GrouptaskLabelResp_STU implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String grouptaskId;
	private String grouptaskName;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private boolean hasAttachFile;
	
	private boolean isTarget;
	// 내가 이 과제 제출했나?
	private boolean isSubmitted;
}