package kr.or.jsu.classregist.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 예비수강신청) 찜하기/찜 취소 응답 DTO
 * @author 김수현
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
@Builder
public class WishlistResponseDTO {
	private boolean success;              // 성공 여부
    private String message;               // 메시지

    // 경고 플래그
    private boolean isDuplicate;          // 같은 과목 중복
    private boolean isTimeConflict;       // 시간표 겹침
    private boolean isCreditOver;         // 학점 초과
    private boolean isGradeRestricted;    // 학년 제한
    private boolean isFullCapacity;       // 정원 마감
}
