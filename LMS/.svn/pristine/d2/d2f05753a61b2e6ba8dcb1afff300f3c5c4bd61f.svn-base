package kr.or.jsu.lms.user.controller.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import kr.or.jsu.dto.PushNoticeDetailDTO;
// 🚨 수정: Controller에서 주입하는 WebSocketController 클래스가 필요합니다.

@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController { // 이 이름으로 Controller에 주입됩니다.

    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트 → 서버
    @MessageMapping("/send-notification") // 클라이언트가 /app/send-notification 으로 보냄
    public void handleNotification(PushNoticeDetailDTO message) {
        log.info("📨 WebSocket 알림 요청: {}", message);

        // 모든 구독자에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/notifications", message);
    }

    // 특정 사용자에게만 (1:1)
    public void sendToUser(String userId, PushNoticeDetailDTO message) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", message);
    }
}