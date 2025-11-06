package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "indivtaskId")
public class LctIndivtaskInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String indivtaskId;
	private String lectureId;
	private String indivtaskName;
	private String indivtaskDesc;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private String deleteYn;
	private String attachFileId;
	
	// 계산되어 나오는 컬럼.
	
	// 교수 입장에서 조회 시 :
	private Integer submittedTask; // 몇 명이 과제를 제출했는가?
	private Integer targetStudentCnt; // 몇 명에게 과제가 출제되었는가?
	
	// 학생 입장에서 조회 시 :
	private Integer isSubmitted;
}
