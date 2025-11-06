package kr.or.jsu.ai.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import kr.or.jsu.ai.dto.ConversationContextDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 질문을 받아서 어떻게 처리할지 판단하는 중앙 관제탑 같은 service
 * => 일반 대화(AI만) vs 학교 관련 규칙(RAG) 판단
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
 *	2025. 10. 24.		김수현			대화 맥락 메서드 추가
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentRAGService {
	private final VectorStoreService vectorStoreService;
    private final AIAnswerGeneratorService answerGenerator;
    private final CommonCodeSearchService commonCodeSearchService; // DB 조회용
    private final CollegeUniveDeptSearchService collegeUniveDeptSearchService;

    // ======================
    // 혜진 추가 - 단과대학 관련 service 추가
    // ======================


    // 사용자별 대화 맥락 저장 (메모리)
    private final Map<String, ConversationContextDTO> contextMap = new ConcurrentHashMap<>();

    /**
     * 스마트 모드: 판단 후 답변
     */
    public String answerQuestionSmart(String question, String userNo) {
        log.info("===> Smart 모드 시작: {}", question);

        // 이전 대화 맥락 가져오기
        ConversationContextDTO context = contextMap.get(userNo);

        if (context != null && context.isValid()) {
            log.info(" ====> 이전 맥락 발견 - 주제: {}, 시간: {}",
                context.getLastTopic(), context.getTimestamp());
        }

        // 2. 맥락 기반 질문 확장
        String enhancedQuestion = enhanceWithContext(question, context);

        if (!question.equals(enhancedQuestion)) {
            log.info("=====> 질문 확장: '{}' → '{}'", question, enhancedQuestion);
        }

        // 3. 답변 생성
        String answer;
        String topic;
        String gradeCd = null;


        // 1순위: 공통코드 DB 조회
        if (needsDBSearch(enhancedQuestion)) {
            log.info("=====> 공통코드 DB 검색 모드");
            answer = commonCodeSearchService.search(enhancedQuestion, userNo, context);
            topic = "수강신청";
            gradeCd = extractGrade(enhancedQuestion);

        // ======================
        // 혜진 추가 - else if 형식으로 추가
        // ======================
        } else if (needsCollegeDeptSearch(enhancedQuestion)) { 
            log.info("=====> 단과대학/학과 DB 검색 모드");
            answer = collegeUniveDeptSearchService.search(enhancedQuestion, userNo, context);
            topic = "단과대학";
            
        } else {
            log.info("=====> 문서 검색 모드");
            answer = answerWithRAG(enhancedQuestion, context);
            topic = detectTopic(enhancedQuestion);
        }

        // 4. 새로운 맥락 저장
        ConversationContextDTO newContext = new ConversationContextDTO();
        newContext.setLastQuestion(question);  // 원본 질문 저장
        newContext.setLastAnswer(answer);
        newContext.setLastTopic(topic);
        newContext.setLastGradeCd(gradeCd);
        newContext.setTimestamp(LocalDateTime.now());

        contextMap.put(userNo, newContext);

        log.info("=====> 맥락 저장 완료 - 주제: {}", topic);

        return answer;
    }

    /**
     * 맥락 기반 질문 확장
     */
    private String enhanceWithContext(String question, ConversationContextDTO context) {
        // 맥락 없으면 원본 그대로
        if (context == null || !context.isValid()) {
            return question;
        }

        String lower = question.toLowerCase();

        // 패턴 1: "그럼", "그러면" 등으로 시작
        if (lower.matches("^(그럼|그러면|그리고|또|그래서|그런데|근데).*")) {
            log.info("=====> 접속사 패턴 감지");
            return context.getLastTopic() + " " + question;
        }

        // ======================
        // 혜진 추가
        // ======================
        if ("단과대학".equals(context.getLastTopic()) && lower.length() < 15) {
            if (lower.contains("학과장") || lower.contains("대학장") || lower.contains("연락처") || lower.contains("위치") || lower.contains("사무실")) {
                log.info("=====> 단과대학/학과 후속 질문 패턴 감지");
                return context.getLastTopic() + " " + question; 
            }
        }

        // 패턴 2: 학년만 언급 ("3학년은?", "4학년 알려줘")
        if (lower.matches(".*(\\d)학년.*") && lower.length() < 15) {
            if ("수강신청".equals(context.getLastTopic())) {
                log.info("=====> 학년 변경 패턴 감지");
                return "수강신청 최대 학점 " + question;
            }
        }

        // 패턴 3: 짧은 질문 ("몇 점?", "몇 학점?")
        if (lower.length() < 10 &&
            (lower.contains("몇") || lower.contains("얼마") || lower.contains("언제"))) {
            log.info("=====> 짧은 질문 패턴 감지");
            return context.getLastTopic() + " " + question;
        }

        // 패턴 4: "2학기는?" 같은 학기 변경
        if (lower.contains("학기") && lower.length() < 15) {
            if ("수강신청".equals(context.getLastTopic()) && context.getLastGradeCd() != null) {
                log.info("=====> 학기 변경 패턴 감지");
                return context.getLastGradeCd() + "학년 " + question + " 수강신청 최대 학점";
            }
        }

        return question;
    }

    /**
     * 주제 파악
     */
    private String detectTopic(String question) {
        String lower = question.toLowerCase();

        if (lower.contains("학점") || lower.contains("수강")) return "수강신청";

        // ======================
        // 혜진 추가
        // ======================
        if (lower.contains("단과대학") || lower.contains("학과") || lower.contains("학부") || lower.contains("소속")) return "단과대학";
        
        if (lower.contains("전과")) return "전과";
        if (lower.contains("휴학")) return "휴학";
        if (lower.contains("복학")) return "복학";
        if (lower.contains("졸업")) return "졸업";
        
        return "일반";
    }

    /**
     * 학년 추출
     */
    private String extractGrade(String question) {
        if (question.contains("1ST")) return "1";
        if (question.contains("2ND")) return "2";
        if (question.contains("3RD")) return "3";
        if (question.contains("4TH")) return "4";
        return null;
    }

    /**
     * DB 검색 필요 여부 판단
     */
    private boolean needsDBSearch(String question) {
        String lower = question.toLowerCase();
        String[] keywords = {
            "학점", "수강신청", "최대", "최소",
            "몇 학점", "학점은", "신청", "수강", "학기", "몇 점",
      
        };

        for (String keyword : keywords) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    // ======================
    // 혜진 추가
    // ======================
    private boolean needsCollegeDeptSearch(String question) {
        String lower = question.toLowerCase();
        String[] keywords = {
                "단과대학", "학과", "어디", "연락처", 
                "학과장", "소속 대학",
                "목록", "전체", "모든", "우리", "내", "단과"
        };

        for (String keyword : keywords) {
            if (lower.contains(keyword)) {
                log.debug("===> needsCommonCodeSearch TRUE. 키워드: {}", keyword);
                return true;
            }
        }
        return false;
    }

    /**
     * RAG 기반 답변 (JSU대학교 관련)
     */
    public String answerWithRAG(String question, ConversationContextDTO context) {
        log.info("===> 질문 수신: {}", question);

        // 유사 문서 검색
        List<Document> relevantDocs = vectorStoreService
            .searchSimilarDocuments(question, 10, 0.3);

        // 문서 있으면 RAG, 없으면 일반 대화 모드
        if (relevantDocs.isEmpty() || isGeneralConversation(question)) {
            log.info("===> 일반 대화 모드");
            return answerGenerator.generateGeneralAnswer(question, context);
        } else {
            log.info("===> RAG 모드 - {}개 문서 발견", relevantDocs.size());
            return answerGenerator.generateRAGAnswer(question, relevantDocs, context);
        }
    }

    /**
     * 일반 대화 판단
     */
    private boolean isGeneralConversation(String question) {
        String lower = question.toLowerCase().trim();
        String[] greetings = {"안녕", "hello", "hi", "감사", "고마워", "날씨"};

        for (String greeting : greetings) {
            if (lower.contains(greeting)) {
                return true;
            }
        }
        return question.length() < 10;
    }
}
