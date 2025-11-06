package kr.or.jsu.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class SubjectInfoDetailDTO {

	private String subjectCd;       // 교과목 코드
    private String univDeptCd;      // 소속 학부(과) 코드
    private String subjectName;     // 교과목명
    private String completionCd;    // 이수구분 코드 (예: GE_CORE)
    private Integer credit;         // 학점
    private Integer hour;           // 시수
    private LocalDate createAt;     // 생성일자
    private LocalDate deleteAt;        // 폐지일자 (null이 아니면 폐지된 상태)
    private String deleteStatus;        // 폐지일자 (null이 아니면 폐지된 상태)

    // JOIN을 통해 추가된 필드
    private String univDeptName;    // 소속 학부(과)명 (UNIV_DEPT 테이블에서 조회)
    private String completionName;  // 이수구분 코드명 (COMMON_CODE 테이블에서 조회)
    private String professorName;
    private String officeNo;
    private String termCd;
    private String termName;
    private String yeartermName;
    private String yeartermCd;
    private String gradeCd;
    private String gradeName;
    private String collegeName;
    private String maxCap;

    // MyBatis에서 ROWNUM을 사용할 경우 필요할 수 있습니다.
    private Integer rn;

}
