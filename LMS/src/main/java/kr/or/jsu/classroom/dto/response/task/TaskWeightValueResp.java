package kr.or.jsu.classroom.dto.response.task;

import java.io.Serializable;

import lombok.Data;

@Data
public class TaskWeightValueResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String taskType;
	private String taskId;
	private String taskName;
	private Integer weightValue;
}