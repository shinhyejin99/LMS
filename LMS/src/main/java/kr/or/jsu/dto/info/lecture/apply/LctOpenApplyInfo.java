package kr.or.jsu.dto.info.lecture.apply;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LctOpenApplyInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String lctApplyId;
	private String subjectCd;
	private String professorNo;
	private String yeartermCd;
	private String lectureIndex;
	private String lectureGoal;
	private String prereqSubject;
	private Integer expectCap;
	private String desireOption;
	private String cancelYn;
	private String approveId;
	private LocalDateTime applyAt;
}
