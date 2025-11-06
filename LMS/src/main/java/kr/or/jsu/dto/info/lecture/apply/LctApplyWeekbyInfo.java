package kr.or.jsu.dto.info.lecture.apply;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"lctApplyId", "lectureWeek"})
public class LctApplyWeekbyInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer lectureWeek;
	private String lctApplyId;
	private String weekGoal;
	private String weekDesc;
}