package kr.or.jsu.classroom.dto.request.task;

import java.io.Serializable;

import lombok.Data;

@Data
public class IndivtaskEvaluateReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	private int evaluScore;
	private String evaluDesc;
}