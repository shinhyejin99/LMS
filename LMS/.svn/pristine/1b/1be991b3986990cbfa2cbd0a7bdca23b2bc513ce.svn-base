package kr.or.jsu.classregist.dto;

import lombok.Data;

/**
 * 예비수강신청 장바구니 DTO
 * @author 김수현
 * @since 2025. 10. 16.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 16.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class WishlistDTO {
	// 기본 정보
    private String lectureId;           // 강의ID
    private String studentNo;           // 학번

    // 강의 상세 정보 (조인용)
    private String subjectName;         // 과목명
    private String lectureName;         // 강의명 (= 과목명임! 추후 수정예정)
    private String professorName;       // 교수명
    private String placeName;           // 강의실
    private String timeInfo;            // 시간 (월1,2,3 형식)
    private Integer credit;             // 학점
    private Integer hour;               // 시수
    private String completionName;      // 이수구분명 (전핵/전선/교선 등..)
    private Integer maxCap;             // 정원
    private String targetGrades;        // 대상학년
    private Integer currentEnroll;      // 현재 신청 인원
    private String wishlistAt;          // 찜한 시간 (정렬용)
}
