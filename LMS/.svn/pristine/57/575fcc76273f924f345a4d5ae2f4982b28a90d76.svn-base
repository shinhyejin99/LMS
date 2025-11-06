package kr.or.jsu.dto.response.lms.lecture.apply;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 개설 승인 권한이 있는 사람이 <br>
 * 자신의 승인을 기다리는 강의 개설 신청에 대한 목록 페이지에서 <br>
 * 한 목록을 구성하는 정보를 모아놓은 객체 <br>
 * 
 * @author 송태호
 */
@Data
public class LectureOpenApprovalLabelResp implements Serializable {
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
	private String applicantId; // 신청자 ID
	private String applicantName; // 신청자 이름
	private String applicantUnivDeptName; // 신청자 소속학과
}
