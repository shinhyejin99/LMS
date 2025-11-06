package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "lctExamId")
public class LctExamInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctExamId;
	private String lectureId;
	private String examName;
	private String examDesc;
	private String examType;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private Integer weightValue;
	
	// 계산되어 나오는 값
	// 몇 명이 제출했는가?
	private int submitCount;
	// 몇 명에게 출제되었는가?
	private int targetCount;
	
	// 학생 입장에서 조회 시 :
	private Integer isSubmitted;
}
