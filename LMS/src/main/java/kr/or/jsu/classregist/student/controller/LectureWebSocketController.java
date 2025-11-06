package kr.or.jsu.classregist.student.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import kr.or.jsu.classregist.dto.LectureEnrollUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 수강신청 실시간 정원 업데이트 웹소켓 컨트롤러
 * @author 김수현
 * @since 2025. 10. 19.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 19.     	김수현	          최초 생성
 *
 * </pre>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LectureWebSocketController {
private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * 특정 강의의 정원 변경을 모든 사용자에게 브로드캐스트
     */
    public void broadcastEnrollUpdate(String lectureId, Integer currentEnroll, Integer maxCap) {
        LectureEnrollUpdateDTO message = LectureEnrollUpdateDTO.builder()
                .lectureId(lectureId)
                .currentEnroll(currentEnroll)
                .maxCap(maxCap)
                .build();
        
        log.info("📢 [WebSocket] 정원 업데이트 브로드캐스트: {}", message);
        
        // /topic/lecture-enroll 구독자 전체에게 전송
        messagingTemplate.convertAndSend("/topic/lecture-enroll", message);
    }
}
