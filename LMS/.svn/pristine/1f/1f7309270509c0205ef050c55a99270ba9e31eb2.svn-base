package kr.or.jsu.ai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import kr.or.jsu.ai.dto.ConversationContextDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OpenAI GPT 에게 질문하고 답변 받는 Service
 * => 답변 받는 서비스임
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
 *	2025. 10. 24.		김수현			대화 맥락 기억 추가
 *	2025. 10. 27		신혜진			단과/학과 기억 추가
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIAnswerGeneratorService {
	private final ChatModel chatModel;

    /**
     * RAG 기반 답변 생성 (+대화 맥락 기억)
     */
    public String generateRAGAnswer(String question, List<Document> relevantDocs, ConversationContextDTO context) {
    	// 1. 검색된 문서들을 하나의 텍스트로 합침
        String contextStr  = relevantDocs.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n\n"));

        // 2. 프롬프트 작성
        // 참고 문서 : 제 1장 총칙 등등 말그대로 참고 문서임
        String promptString = String.format("""
            당신은 JSU 대학교 학사 정보 안내 AI입니다.

            아래 참고 문서를 반드시 읽고 정확하게 답변하세요.

            === 참고 문서 (중요!) ===
            %s

            === 질문 ===
            %s

            === 답변 규칙 ===
            1. 위 참고 문서의 내용을 바탕으로 명확하게 답변하세요
            2. 문서에 있는 내용은 "명시되어 있지 않습니다"라고 하지 마세요
            3. 조 번호(제12조, 제13조 등)가 있으면 인용하세요
            4. 구체적인 숫자나 조건을 정확히 포함하세요
            5. 이모티콘 사용 금지
            6. 마크다운 대신 일반 텍스트 사용
            7. 목록은 하이픈(-) 또는 번호 사용
            8. 정중하고 친절한 어투로 답변
            9. 이전 대화 맥락을 고려하여 자연스럽게 답변하세요

            반드시 참고 문서에 있는 내용을 기반으로 답변하세요!
            """, contextStr, question);

        // 3. 답변 반환
        return callAI(promptString, question, context);
    }

    /**
     * 일반 대화 답변 생성(+ 대화 맥락 포함)
     */
    public String generateGeneralAnswer(String question, ConversationContextDTO context) {
        String promptString = String.format("""
            당신은 JSU대학교 학사 정보 안내 AI입니다.

            === 질문 ===
            %s

            === 답변 규칙 ===
            - 이모티콘 사용 금지
            - 친절하고 정중한 어투
            - 간단명료하게 답변
            - 이전 대화 맥락을 고려하세요.
            """, question);

        return callAI(promptString, question, context);
    }

    /**
     * AI 답변 생성 메서드 (검색된 문서 + 질문을 합쳐서 AI) (+대화 맥락 포함)
     * @param systemPrompt
     * @param userQuestion
     * @return
     */
    private String callAI(String systemPrompt, String userQuestion, ConversationContextDTO context) {
        try {
        	List<Message> messages = new ArrayList<>();

            // 1. 시스템 메시지
            messages.add(new SystemMessage(systemPrompt));

            // 2. 이전 대화 히스토리 추가
            if (context != null && context.isValid()) {
                log.info("====> 대화 맥락 포함 - 주제: {}", context.getLastTopic()); // 이전 주제 가져오고

                // 이전 질문
                if (context.getLastQuestion() != null) {
                    messages.add(new UserMessage(context.getLastQuestion())); // 이전 질문 가져오고
                }

                // 이전 답변
                if (context.getLastAnswer() != null) {
                    messages.add(new AssistantMessage(context.getLastAnswer())); // 이전 답변 가져옴
                }
            }

            // 3. 현재 질문
            messages.add(new UserMessage(userQuestion));

            // 4. API 호출
            Prompt prompt = new Prompt(messages);
            ChatResponse response = chatModel.call(prompt);

            return response.getResult().getOutput().getText();

        } catch (Exception e) {
            log.error("===> AI 호출 오류", e);
            return "답변 생성 중 오류가 발생했습니다.";
        }
    }

    /**
     * 공통코드 조회 결과 기반 답변
     */
    public String generateCommonCodeAnswer(String question, String codeData, ConversationContextDTO context) {
        String promptString = String.format("""
            당신은 JSU 대학교 학사 정보 안내 AI입니다.

            === DB 조회 결과 ===
            %s

            === 질문 ===
            %s

            === 답변 규칙 ===
            1. 위 학사 정보를 바탕으로 정확하게 답변하세요
            2. 구체적인 숫자를 명확히 포함하세요
            3. 이모티콘 사용 금지
            4. 마크다운 대신 일반 텍스트
            5. 간결하고 명확하게 답변
            6. "DB 조회", "데이터베이스", "시스템" 같은 기술 용어 사용 금지
        	7. 자연스럽고 친근한 어투로 답변
        	8. 이전 대화 맥락을 고려하여 자연스럽게 답변하세요.

            DB 조회 결과를 기반으로 답변하세요!
            예시:
        	좋은 답변: "2학년 2학기 최대 수강 학점은 24학점입니다."
        	나쁜 답변: "DB 조회 결과에 따르면 24학점입니다."
            """, codeData, question);

        return callAI(promptString, question, context);
    }

    /**
     * 단과대학 및 학과 정보 검색 결과 기반 답변(DB 조회)
     */
    public String generateUnivDeptAnswer(String question, String univDeptData, ConversationContextDTO context) {

        // ======================
        // 혜진 수정
        // ======================

    	String promptString = String.format("""
            당신은 JSU대학교 학사 정보 안내 AI입니다.

            === DB 조회 결과 ===
            %s

            === 질문 ===
            %s

            === 답변 규칙 ===
            1. 위 학사 정보를 바탕으로 정확하게 답변하세요
            2. 구체적인 숫자를 명확히 포함하세요
            3. 이모티콘 사용 금지
            4. 마크다운 대신 일반 텍스트
            5. 간결하고 명확하게 답변
            6. "DB 조회", "데이터베이스", "시스템" 같은 기술 용어 사용 금지
        	7. 자연스럽고 친근한 어투로 답변
        	8. 이전 대화 맥락을 고려하여 자연스럽게 답변하세요.

            DB 조회 결과를 기반으로 답변하세요!
            예시:
        	좋은 답변: "김새봄님의 학과는 컴퓨터공학과 입니다."
        	나쁜 답변: "당신은 컴퓨터공학과 입니다."
            """, univDeptData, question);

        return callAI(promptString, question, context);
    }

}
