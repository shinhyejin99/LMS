package kr.or.jsu.classroom.dto.response.exam;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lctExamId")
public class LctExamLabelResp_STU implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctExamId;
	private String examName;
	private String examType;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Integer weightValue;
	
	private boolean isSubmitted;
}