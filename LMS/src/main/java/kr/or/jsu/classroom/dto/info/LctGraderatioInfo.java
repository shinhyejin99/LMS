package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"lectureId", "gradeCriteriaCd"})
public class LctGraderatioInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private String gradeCriteriaCd;
	private int ratio;
}
