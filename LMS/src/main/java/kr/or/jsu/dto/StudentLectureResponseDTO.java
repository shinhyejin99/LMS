package kr.or.jsu.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	최건우	          최초 생성
 *
 * </pre>
 */
@Getter
@Setter
@ToString
public class StudentLectureResponseDTO {
    private String lectureId; // 강의 ID (PK)
    private String subjectName; // 강의명
    private String completionCd; // 이수구분 코드
    private String completionName; // 이수구분명
    private int credit; // 학점
    private String professorName; // 교수명
    private String yearTermCd; // 학년도 학기 코드
    private String yearTermName; // 학년도 학기명
    private String targetGradeNames; // 대상학년명
    private String enrollStatusCd; // 수강 상태 코드
    private String enrollStatusName; // 수강 상태명
}
