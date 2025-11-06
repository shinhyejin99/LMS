package kr.or.jsu.classroom.dto.response.ender;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "enrollId")
public class EnrollingStudentsAndScoreResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	
	private Double autoGrade;
	private Double finalGrade;
	private String gpaCd;
	private String changeReason;
	
	private List<LctSectionScoreResp> sectionScores;
}
