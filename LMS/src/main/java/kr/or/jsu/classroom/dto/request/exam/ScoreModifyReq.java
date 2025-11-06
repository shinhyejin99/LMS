package kr.or.jsu.classroom.dto.request.exam;

import java.io.Serializable;

import lombok.Data;

@Data
public class ScoreModifyReq implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String studentNo;
	private int modifiedScore;
	private String modifyReason;	
}