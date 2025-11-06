package kr.or.jsu.dto.info.lecture.apply;

import java.io.Serializable;

import lombok.Data;

@Data
public class LctApplyGraderatioInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String gradeCriteriaCd;
	private String lctApplyId;
	private Integer ratio;
}