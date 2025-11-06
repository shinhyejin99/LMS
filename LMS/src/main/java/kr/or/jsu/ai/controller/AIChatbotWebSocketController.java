package kr.or.jsu.ai.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import kr.or.jsu.ai.service.DocumentRAGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket으로 실시간 채팅 처리 Controller
 *
 * @author 김수현
 * @since 2025. 10. 23.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 23.     	김수현	          최초 생성
 *
 * </pre>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AIChatbotWebSocketController {
	private final DocumentRAGService ragService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 사용자 질문 받고 => RAG 처리 => 스트리밍 응답
     *
     * 클라이언트가 /app/ai/ask로 전송
     * 응답: /topic/ai/{sessionId}로 청크 단위 전송
     */
    @MessageMapping("/ai/ask")
    public void handleQuestion(Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String question = payload.get("question");
        String sessionId = payload.get("sessionId");

        // websocket 세션에서 userId 가져오기
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        String userNo = (String) headerAccessor.getSessionAttributes().get("userNo");

        if (userId == null) {
            userId = "anonymous";
        }

        log.info("===> 질문 수신: {} (userId: {}, userNo: {})", question, userId, userNo);

        try {
        	// 1. 시작 신호를 전송하고
            sendStreamStart(sessionId, question);

            // 2. RAG 기반 답변 생성 (AI에게 질문)
            String answer = ragService.answerQuestionSmart(question, userNo);

            // 3. 타이핑 효과로 답변 전송
            sendAnswerInChunks(sessionId, answer);

            // 4. 완료 신호 전송
            sendStreamEnd(sessionId, answer);

        } catch (Exception e) {
            log.error("===> 답변 생성 오류", e);
            sendError(sessionId, "답변 생성 중 오류가 발생했습니다.");
        }
    }

    /**
     * 스트리밍 시작 알림
     */
    private void sendStreamStart(String sessionId, String question) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "START");
        message.put("question", question);
        message.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/ai/" + sessionId, message);
    }

    /**
     * 답변을 청크 단위로 전송 (타이핑 효과)
     */
    private void sendAnswerInChunks(String sessionId, String answer) {
        Random random = new Random();

        for (int i = 0; i < answer.length(); i++) {
            char currentChar = answer.charAt(i);
            String chunk = String.valueOf(currentChar);

            Map<String, Object> message = new HashMap<>();
            message.put("type", "CHUNK");
            message.put("chunk", chunk);
            message.put("progress", (double) (i + 1) / answer.length() * 100);

            messagingTemplate.convertAndSend("/topic/ai/" + sessionId, message);

            // 타이핑 효과 부분
            try {
                int delay = getTypingDelay(currentChar, random);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private int getTypingDelay(char c, Random random) {
        int baseDelay = 10 + random.nextInt(20);

        if (c == ' ') {
            return baseDelay + random.nextInt(20);
        }
        if (c == '.' || c == '!' || c == '?') {
            return baseDelay + 30 + random.nextInt(30);
        }
        if (c == ',' || c == ';' || c == ':') {
            return baseDelay + 20 + random.nextInt(20);
        }
        if (c == '\n') {
            return baseDelay + 30 + random.nextInt(30);
        }

        return baseDelay;
    }

    /**
     * 스트리밍 완료 알림
     */
    private void sendStreamEnd(String sessionId, String fullAnswer) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "END");
        message.put("fullAnswer", fullAnswer);
        message.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/ai/" + sessionId, message);
    }

    /**
     * 오류 메시지 전송
     */
    private void sendError(String sessionId, String errorMessage) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "ERROR");
        message.put("error", errorMessage);
        message.put("timestamp", System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/ai/" + sessionId, message);
    }
}
