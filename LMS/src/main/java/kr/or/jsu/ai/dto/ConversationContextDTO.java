package kr.or.jsu.ai.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 대화 맥락 정보 DTO
 * @author 김수현
 * @since 2025. 10. 24.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 24.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
public class ConversationContextDTO {
	private String lastQuestion;        // 마지막 질문
    private String lastAnswer;          // 마지막 답변
    private String lastTopic;           // 마지막 주제 (학점, 전과 등)
    private String lastGradeCd;         // 마지막 학년
    private LocalDateTime timestamp;    // 대화 시간

    /**
     * 맥락이 유효한지 확인 (5분 이내)
     */
    public boolean isValid() {
        if (timestamp == null) {
            return false;
        }
        return timestamp.plusMinutes(5).isAfter(LocalDateTime.now());
    }
}
