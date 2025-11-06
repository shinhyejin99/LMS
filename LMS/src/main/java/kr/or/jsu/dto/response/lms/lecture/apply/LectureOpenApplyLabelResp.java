package kr.or.jsu.dto.response.lms.lecture.apply;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LectureOpenApplyLabelResp implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctApplyId; // 신청ID
	private String subjectName; // 과목명
	private String univDeptName; // 과목 담당학과
	private String completionName; // 이수구분명 (전공-핵심 등)
	private int credit; // 학점
	private int hour; // 시수	
	private LocalDateTime applyAt; // 신청일
	
	private String yeartermCd;
	private String yeartermName;
	
	private String approvalId;
	private String applyStatus; // 처리상태 (승인 YNNull을 보고 판단)
	private String approverId;
	private String approverName;
	private String approverRole;
}
