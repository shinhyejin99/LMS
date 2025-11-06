package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"taskType", "taskId"})
public class LctTaskWeightInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String taskType;
	private String taskId;
	private String taskName;
	private Integer weightValue;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
