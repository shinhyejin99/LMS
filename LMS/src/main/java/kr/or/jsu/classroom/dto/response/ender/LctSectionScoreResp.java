package kr.or.jsu.classroom.dto.response.ender;

import java.io.Serializable;

import lombok.Data;

@Data
public class LctSectionScoreResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String gradeCriteriaCd; // EXAM, ATTD, TASK, PRAC, MISC
	private String enrollId;
	private double rawScore;
}
