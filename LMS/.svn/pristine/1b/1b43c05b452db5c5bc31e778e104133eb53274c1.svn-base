package kr.or.jsu.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalLineRequestDetailDTO {

	private String userId; // 승인ID
	private String prevApproveId; // 이전 승인 ID
	private String docId; // 관련 문서 ID
	private String approveId;
	private String approveYnnull;

	private LocalDateTime approveAt;
	private String comments;
	private String attachFileId;

	private String prevLastName; // 이전 승인자 성
	private String prevFirstName; // 이전 승인자 이름

	private String applicantLastName; // 신청자 성
	private String applicantFirstName;// 신청자 이름

	private String currentLastName;// 최종승인자 성
	private String currentFirstName;// 최종승인자 이름

	private String applyTypeCd; // 문서 제목 (결재 문서의 코드)
	private String applyTypeName;// 문서 제목 (결재 문서의 제목)
	private String applicantUserId;// 신청자

	private String subjectName;// 강의명
	private String subjectCd;// 기반 과목 코드
	private String completionCdName;// 이수구분
	private String credit;// 학점
	private String hours;// 시수
	private String applicantuprofessorNameserid;// 신청 교수
	private String yeartermCd;// 학년도
	private String termNum;// 학기
	private String termDisplay;//
	private String lectureIndex;// 강의 개요
	private String lectureGoal;// 강의 목표
	private String prereqSubject;// 선수 학습 과목
	private String expectCap;// 예상 강의 정원
	private String cancelYn;// 신청자
	private List<?> weeklyPlans;// 주차별 학습계획
	private String lectureWeek;// 주차
	private String weekGoal;// 학습목표
	private String weekDesc;// 설명

	private String rejectionReason;
	private LocalDateTime lctApplyAt;
	private String professorName;
	private List<?> gradeRatios;

	private String lctApplyId;
	private String allocateYn;
	//교수 소속
	private String collegeName;
	private String departmentName;
	private String employmentStatus;
}
