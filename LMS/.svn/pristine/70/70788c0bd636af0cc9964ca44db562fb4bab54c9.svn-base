package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "enrollId")
public class StuEnrollLctInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String enrollId;
	private String studentNo;
	private String lectureId;
	private String enrollStatusCd;
	private String retakeYn;
	private LocalDateTime statusChangeAt;
	private Double autoGrade;
	private Double finalGrade;
	private String changeReason;
}