package kr.or.jsu.classroom.dto.response.ender;

import java.io.Serializable;

import lombok.Data;

@Data
public class ScoreAndGPAResp implements Serializable {
	private static final long serialVersionUID = 1L;

	private String studentNo;
	private String lectureId;
	private String gpaCd;
	private Double autoGrade;
	private Double finalGrade;
	private String changeReason;
}
