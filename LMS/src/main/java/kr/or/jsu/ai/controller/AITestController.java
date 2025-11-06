package kr.or.jsu.ai.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AITestController {
private final ChatModel chatModel;

    @GetMapping("/test")
    public String testAI(@RequestParam(defaultValue = "안녕하세요") String message) {
        log.info("===> AI 테스트 요청: {}", message);

        try {
            String response = chatModel.call(message);
            log.info("===> AI 응답: {}", response);
            return "질문: " + message + "\n\n답변: " + response;
        } catch (Exception e) {
            log.error("===> AI 호출 실패", e);
            return "오류 발생: " + e.getMessage();
        }
    }

}
