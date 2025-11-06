package kr.or.jsu.classroom.dto.request.task;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GrouptaskModifyReq implements Serializable {
	private static final long serialVersionUID = 1L;

	// TODO 유효성 검사
	private String grouptaskId;
	private String grouptaskName;
	private String grouptaskDesc;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private String attachFileId;
}