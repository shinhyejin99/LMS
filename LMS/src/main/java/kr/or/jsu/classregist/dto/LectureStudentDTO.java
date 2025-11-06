package kr.or.jsu.classregist.dto;

import lombok.Data;

/**
 * 교수- 강의별 수강 학생 목록 DTO
 * @author 김수현
 * @since 2025. 10. 20.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 20.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class LectureStudentDTO {
	private String studentNo;           // 학번
    private String studentName;         // 학생 이름
    private String gradeName;           // 학년명 (1학년, 2학년 등)
    private String collegeName;         // 단과대학명
    private String univDeptName;      	// 학과명
    private String applyAt;             // 신청일시
}
