package kr.or.jsu.lms.user.service.notification; 

import java.util.Map;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service // ⭐️ Spring Bean 등록 (다른 곳에서 주입받기 위함)
@RequiredArgsConstructor
public class NotificationServiceImpl {

    private final SimpMessagingTemplate messagingTemplate;


    // 특정 사용자에게 1:1 실시간 알림을 전송    
    public void sendUserNotification(String userId, String title, String content, String userType) {
        
        Map<String, Object> payload = Map.of(
            "title", title,
            "content", content,
            "userType", userType, 
            "timestamp", System.currentTimeMillis()
        );

        // 1:1 알림 전송: /user/{userId}/queue/notifications 경로로 푸시
        messagingTemplate.convertAndSendToUser(
            userId, 
            "/queue/notifications", 
            payload
        );
        
        System.out.println("✅ WebSocket 메시지 전송 요청 완료: " + userId);
    }
}