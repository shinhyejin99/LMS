package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "grouptaskId")
public class GrouptaskLabelResp_PRF implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String grouptaskId;
	private String grouptaskName;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private boolean hasAttachFile;
	
	// 몇 개의 조가 과제를 제출했는가?
	private int submittedTask;
	// 몇 개의 조에게 과제가 출제되었는가?
	private int targetJoCnt;
}