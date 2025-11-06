package kr.or.jsu.classroom.dto.response.ender;

import java.io.Serializable;

import lombok.Data;

@Data
public class LectureProgressResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer weekCnt;  
	private Integer weeklyBlockCnt;   
	private Integer recordedRoundCnt;
}