package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "grouptaskId")
public class LctGrouptaskInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String grouptaskId;
	private String lectureId;
	private String grouptaskName;
	private String grouptaskDesc;
	private LocalDateTime createAt;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private String deleteYn;
	private String attachFileId;
	
	// 계산되어 나오는 컬럼.
	
	// 교수 입장에서 조회 시 :
	private int submittedTask; // 몇 개의 조가 과제를 제출했는가?
	private int targetJoCnt; // 몇 개의 조에게 과제가 출제되었는가?
	
	// 학생 입장에서 조회 시 :
	private Integer isSubmitted;
}