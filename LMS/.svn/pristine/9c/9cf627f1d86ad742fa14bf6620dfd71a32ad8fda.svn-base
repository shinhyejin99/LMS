package kr.or.jsu.core.dto.request;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * 
 * @author 신혜진
 * @since 2025. 10. 16.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 16.     	신혜진	         자동알림 시스템
 *
 * </pre>
 */

/**
 * 시스템/비즈니스 로직에서 알림을 '발송'할 때 사용할 요청 DTO
 * 발신자 ID, 제목, 내용, 수신자 ID 등 핵심 정보만 포함
 */
@Data
@Builder
public class AutoNotificationRequest {
	// 필수 정보
    private String receiverId;      // 수신자 ID (학생, 직원, 교직원)
    private String title;           // 알림 제목 (예: "증명서 발급 완료")
    private String content;         // 알림 내용 (예: "발급이 완료되었습니다. 출력하세요.")

    // 발신자 정보 (SYSTEM 또는 부서명으로 대체될 수 있음)
    private String senderName;      // 발신자 명칭 (예: "SYSTEM", "교무처")

    // 선택 정보
    private String pushUrl;    
}
