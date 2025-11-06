package kr.or.jsu.ai.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.or.jsu.ai.dto.ConversationContextDTO;
import kr.or.jsu.ai.dto.CreditInfoDTO;
import kr.or.jsu.ai.dto.GradeResultDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.mybatis.mapper.CommonCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통코드 DB 조회 서비스(챗봇용) - 수강신청 등 공통코드 사용 관련 질문 할 때
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
 *	2025. 10. 24.		김수현			대화 맥락 포함 추가
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCodeSearchService {

	private final CommonCodeMapper commonCodeMapper;
    private final AIAnswerGeneratorService answerGenerator;
    private final StuInfoService stuInfoService;

    /**
     * 챗봇 질문 처리
     */
    public String search(String question, String userNo, ConversationContextDTO context) {
    	log.info("===> 공통코드 검색 시작: {} (userId: {})", question, userNo);

        // 학점 관련 질문인지 확인
        if (isCreditQuestion(question)) {
            return handleCreditQuestion(question, userNo, context);
        }

        // + 다른 공통코드 질문들...
        return "죄송합니다. 해당 정보를 찾을 수 없습니다.";
    }

    /**
     * 수강신청) 최대 수강신청 학점 관련 질문 처리
     */
    private String handleCreditQuestion(String question, String userNo, ConversationContextDTO context) {
        try {
        	// 1. 기본 정보 수집
            StudentDetailDTO student = getCurrentStudent(userNo);
            String gradeCd = extractGrade(question);

            // 2. 학년 결정
            GradeResultDTO gradeResult = determineGrade(gradeCd, student);

            // 전체 학년 조회 케이스
            if (gradeResult.isShowAll()) {
                return handleAllGradeCredit(context);
            }

            // 3. 현재 학기 정보 가져오기
            String currentYearterm = commonCodeMapper.selectCurrentYearterm();
            log.info("===> 현재 학기: {}", currentYearterm);

            if (currentYearterm == null) {
                return "현재 학기 정보를 가져올 수 없습니다.";
            }

            // 4. 현재 학기 코드 매핑 (yearterm_cd => 학기로: 2024_REG1 => 1, 2024_REG2 => 2)
            String currentTermCd = commonCodeMapper.selectTermCdMapping(currentYearterm);

            // 5. 다음 학기 정보 가져오기
            String nextYearterm = commonCodeMapper.selectNextYearterm(currentYearterm);

            if (nextYearterm == null) {
                log.info("===> 다음 학기 정보 없음, 현재 학기만 표시");
                // 다음 학기 정보 없으면 현재 학기만 조회
                return handleSingleTermCredit(
                    gradeResult.getGradeCd(),
                    currentTermCd,
                    gradeResult.getUserInfo(),
                    question,
                    context
                );
            }
            // 다음 학기 코드 매핑
            String nextTermCd = commonCodeMapper.selectTermCdMapping(nextYearterm);

            // 6. 다음 학기의 학년 계산
            String nextGradeCd = calculateNextGrade(
                gradeResult.getGradeCd(),
                currentTermCd,
                nextTermCd
            );

            log.info("===> 현재: {}학년 {}학기 ({}), 다음: {}학년 {}학기 ({})",
                gradeResult.getGradeCd(), currentTermCd, currentYearterm,
                nextGradeCd, nextTermCd, nextYearterm);

            // 7. 현재 학기 + 다음 학기 학점 조회
            List<CreditInfoDTO> creditInfos = new ArrayList<>();

            // 현재 학기 학점
            Integer currentCredit = commonCodeMapper.selectMaxCreditByGrade(
                gradeResult.getGradeCd(), currentTermCd
            );
            if (currentCredit != null) {
                creditInfos.add(new CreditInfoDTO(
                    gradeResult.getGradeCd(),
                    currentTermCd,
                    currentCredit,
                    "현재 학기",
                    currentYearterm
                ));
            }

            // 다음 학기 학점
            Integer nextCredit = commonCodeMapper.selectMaxCreditByGrade(
                nextGradeCd, nextTermCd
            );
            if (nextCredit != null) {
                creditInfos.add(new CreditInfoDTO(
                    nextGradeCd,
                    nextTermCd,
                    nextCredit,
                    "다음 학기",
                    nextYearterm
                ));
            }

            if (creditInfos.isEmpty()) {
                return "해당 학년의 수강신청 학점 정보를 찾을 수 없습니다.";
            }

            // 8. 답변 생성 (2개 학기 정보)
            String data = formatMultipleCreditData(creditInfos, gradeResult.getUserInfo());

            return answerGenerator.generateCommonCodeAnswer(question, data, context);

        } catch (Exception e) {
            log.error("===> 학점 조회 실패", e);
            return "학점 정보 조회 중 오류가 발생했습니다.";
        }
    }

    /**
     * 학년 결정 로직
     */
    private GradeResultDTO determineGrade(String gradeCd, StudentDetailDTO student) {
        // 케이스 1: 질문에 학년 명시됨
        if (gradeCd != null) {
            return new GradeResultDTO(gradeCd, "", false);
        }

        // 케이스 2: 학생 로그인 (본인 학년 사용)
        if (student != null && student.getGradeCd() != null) {
            String convertedGrade = convertGradeCode(student.getGradeCd());
            String studentName = student.getLastName() + student.getFirstName();
            String userInfo = studentName + "님은 " + student.getGradeName() + "이십니다. ";

            log.info("===> 학생 정보 사용: {} (원본: {} => 변환: {})",
                studentName, student.getGradeCd(), convertedGrade);

            return new GradeResultDTO(convertedGrade, userInfo, false);
        }

        // 케이스 3: 교수/교직원/비로그인 (전체 학년)
        log.info("===> 교수/교직원/비로그인 접근 - 전체 학년 학점 출력");
        return new GradeResultDTO(null, "", true);
    }

    /**
     * 학년 코드 변환 (1ST => 1)
     */
    private String convertGradeCode(String gradeCd) {
        if (gradeCd == null) return null;
        if (gradeCd.startsWith("1")) return "1";
        if (gradeCd.startsWith("2")) return "2";
        if (gradeCd.startsWith("3")) return "3";
        if (gradeCd.startsWith("4")) return "4";
        // 이미 숫자면 그대로
        return gradeCd;
    }

    /**
     * 현재 로그인 사용자 정보 가져오기
     */
    private StudentDetailDTO getCurrentStudent(String userNo) {
        try {
        	log.info("===> 디버깅 - userNo로 학생 정보 조회: {}", userNo);

        	if (userNo == null || "anonymous".equals(userNo)) {
                return null;
            }

            StudentDetailDTO student = stuInfoService.readStuMyInfo(userNo);

            log.info("===> 학생 개인정보 디버깅 - student: {}", student);

            return student;
        } catch (Exception e) {
            log.info("===> 학생 정보 조회 실패", e);
            return null;
        }
    }

    /**
     * 전체 학년 학점 조회 (교수/교직원/비로그인용)
     */
    private String handleAllGradeCredit(ConversationContextDTO context) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("학년별 최대 수강 학점:\n\n");

            for (int grade = 1; grade <= 4; grade++) {
                Integer credit = commonCodeMapper.selectMaxCreditByGrade(
                    String.valueOf(grade), "1"
                );

                if (credit != null) {
                    sb.append(String.format("%d학년: %d학점\n", grade, credit));
                }
            }

            return answerGenerator.generateCommonCodeAnswer(
                "학년별 수강 학점", sb.toString(), context
            );

        } catch (Exception e) {
            log.error("===> 전체 학년 학점 조회 실패", e);
            return "학점 정보 조회 중 오류가 발생했습니다.";
        }
    }

    /**
     * 단일 학기 학점 조회 (다음 학기 정보 없을 때)
     */
    private String handleSingleTermCredit(String gradeCd, String termCd,
                                         String userInfo, String question,
                                         ConversationContextDTO context) {
        Integer maxCredit = commonCodeMapper.selectMaxCreditByGrade(gradeCd, termCd);

        if (maxCredit == null) {
            return "해당 학년의 수강신청 학점 정보를 찾을 수 없습니다.";
        }

        String data = formatCreditData(gradeCd, termCd, maxCredit, userInfo);
        return answerGenerator.generateCommonCodeAnswer(question, data, context);
    }

    /**
     * 다음 학기의 학년 계산
     * 2학기 -> 1학기면 학년 증가
     */
    private String calculateNextGrade(String currentGrade,
                                     String currentTerm,
                                     String nextTerm) {
        // 2학기 -> 1학기면 학년 증가
        if ("2".equals(currentTerm) && "1".equals(nextTerm)) {
            int grade = Integer.parseInt(currentGrade);
            return String.valueOf(Math.min(grade + 1, 4)); // 최대 4학년
        }

        // 같은 학년 유지
        return currentGrade;
    }

    /**
     * 여러 학기 학점 정보 포맷팅 (현재 + 다음)
     */
    private String formatMultipleCreditData(List<CreditInfoDTO> creditInfos,
                                           String userInfo) {
        StringBuilder sb = new StringBuilder();

        // 사용자 정보
        if (!userInfo.isEmpty()) {
            sb.append(userInfo).append("\n");
        }

        sb.append("📚 수강신청 최대 학점 안내\n\n");

        for (CreditInfoDTO info : creditInfos) {
            String termName = "1".equals(info.getTermCd()) ? "1학기" : "2학기";

            sb.append(String.format("■ %s (%s학년 %s)\n",
                info.getLabel(), info.getGradeCd(), termName));
            sb.append(String.format("   최대 수강 학점: %d학점\n\n",
                info.getMaxCredit()));
        }

        return sb.toString();
    }


    /**
     * 학점 데이터 포맷팅
     */
    private String formatCreditData(String gradeCd, String termCd, Integer maxCredit, String userInfo) {
        StringBuilder sb = new StringBuilder();

        // 사용자 정보 먼저
        if (!userInfo.isEmpty()) {
            sb.append(userInfo).append("\n\n");
        }

        sb.append(String.format("학년: %s학년\n", gradeCd));
        sb.append(String.format("학기: %s학기\n", termCd));
        sb.append(String.format("최대 수강 학점: %d학점\n", maxCredit));

        return sb.toString();
    }

    /**
     * 학점 관련 질문인지 판단
     */
    private boolean isCreditQuestion(String question) {
        String lower = question.toLowerCase();
        String[] keywords = {"학점", "수강신청", "최대", "최소", "몇 학점"};

        for (String keyword : keywords) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 질문에서 학년 추출
     */
    private String extractGrade(String question) {
        if (question.contains("1ST")) return "1";
        if (question.contains("2ND")) return "2";
        if (question.contains("3RD")) return "3";
        if (question.contains("4TH")) return "4";
        return null; // 명시 안 하면 기본값 사용
    }

}
