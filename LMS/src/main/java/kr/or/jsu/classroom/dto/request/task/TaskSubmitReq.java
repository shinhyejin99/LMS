package kr.or.jsu.classroom.dto.request.task;

import java.io.Serializable;

import lombok.Data;

@Data
public class TaskSubmitReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String submitDesc;
	private String submitFileId;
}