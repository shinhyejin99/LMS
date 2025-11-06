package kr.or.jsu.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DepartmentDetailDTO {

	// 1. 기본 학과 정보 (UNIV_DEPT_T)
	private String univDeptCd;
	private String univDeptName;
	private String collegeCd;
	private String collegeName;
	private Integer capacity; // 정원
	private String status; // 상태
	private LocalDate createAt;
	private LocalDate deleteAt;

	// 2. 학과장 정보
	private String deptHeadId; // 학과장 ID
	private String prfPositCd;
	private String prfPositName;
	private String deptHeadName;

	// 3. 사무실/연락처 정보
	private String officeCd; // 사무실 코드
	private String officeNo;
	private String placeId;
	private String placeNo;
	private String telNo; // 연락처 (UNIV_DEPT_T)


	// 4. 통계 카운트
	private Integer studentCount; // 재학생 수
	private Integer subjectsCount; // 개설 교과목 수
	private Integer professorCount; // 개설 교과목 수

	// 5. 기타
	private String deptCondId;
	private String gradeCd;
	private String gradeName;
	private String completionCd;
}