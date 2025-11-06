package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"grouptaskId", "groupId"})
public class GrouptaskSubmitInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String grouptaskId;
	private String groupId;
	private String submitDesc;
	private String submitFileId;
	private LocalDateTime submitAt;
}