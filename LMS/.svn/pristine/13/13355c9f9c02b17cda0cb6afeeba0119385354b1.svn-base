package kr.or.jsu.classroomDummyGenerator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class Step91_TuitionDummyGeneratorTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String studentNo = "2023" + "318" + "01";  // 학생 번호
    
    /**
     * 한 학생의 한 학기 정보 (여러 장학금 가능)
     */
    static class StudentTermData {
        String studentNo;
        String yeartermCd;
        List<String> scholarshipNames;  // 여러 장학금 리스트!

        public StudentTermData(String studentNo, String yeartermCd, List<String> scholarshipNames) {
            this.studentNo = studentNo;
            this.yeartermCd = yeartermCd;
            this.scholarshipNames = scholarshipNames;
        }

        // 장학금 없는 경우
        public StudentTermData(String studentNo, String yeartermCd) {
            this.studentNo = studentNo;
            this.yeartermCd = yeartermCd;
            this.scholarshipNames = List.of();
        }
    }

    @Test
    public void createTuitionForOneStudentMultipleTerms() {
        
        List<StudentTermData> termDataList = Arrays.asList(
    		// 2023년 1학기 - 장학금 1개
    		new StudentTermData(
				studentNo, 
				"2023_REG1",
				List.of("성적우수장학금(대학교신입생, 2023년 개정)")
			),
            // 2023년 2학기 - 장학금 1개
            new StudentTermData(
                studentNo, 
                "2023_REG2",
                List.of("성적우수장학금(대학교신입생, 2023년 개정)")
            ),
            
            // 2024년 1학기 - 장학금 2개
            new StudentTermData(
                studentNo,
                "2024_REG1",
                List.of(
                    "성적우수장학금(대학교신입생, 2023년 개정)",
                    "JSU 근로 장학금(2025년 개정)"
                )
            ),
            
            // 2024년 2학기 - 장학금 3개
            new StudentTermData(
                studentNo,
                "2024_REG2",
                List.of(
                    "성적우수장학금(대학교신입생, 2023년 개정)",
                    "JSU 근로 장학금(2025년 개정)",
                    "JSU 홍보대사 장학금(2025년 개정)"
                )
            ),
            
            // 2025년 1학기 - 장학금 없음
            new StudentTermData(studentNo, "2025_REG1"),
            
            
            new StudentTermData(
                studentNo,
                "2025_REG2",
                List.of(
                    "성적우수장학금(대학교신입생, 2023년 개정)",
                    "JSU 근로 장학금(2025년 개정)",
                    "JSU 홍보대사 장학금(2025년 개정)"
                )
            )
        );

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  학생: " + studentNo + " 처리 시작       ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        for (StudentTermData data : termDataList) {
            System.out.println("========================================");
            System.out.println("학기: " + data.yeartermCd);
            System.out.println("장학금 개수: " + data.scholarshipNames.size() + "개");
            System.out.println("========================================");

            try {
                // 1. 등록금 납부 요청 프로시저 실행
                createTuitionRequest(data.studentNo, data.yeartermCd);

                // 2. 여러 장학금 추가
                if (!data.scholarshipNames.isEmpty()) {
                    for (String scholarshipName : data.scholarshipNames) {
                        addScholarship(data.studentNo, data.yeartermCd, scholarshipName);
                    }
                } else {
                    System.out.println("  → 장학금 없음");
                }

                System.out.println("✓ 완료: " + data.yeartermCd + "\n");

            } catch (Exception e) {
                System.err.println("✗ 실패: " + data.yeartermCd);
                System.err.println("에러: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  학생: " + studentNo + " 전체 완료!      ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    /**
     * 납부 완료 처리 테스트
     */
    @Test
    public void markTuitionAsPaid() {
        List<String> yeartermCds = 
        	Arrays.asList(
        			"2023_REG1"
        			, "2023_REG2"
        			, "2024_REG1"
        			, "2024_REG2"
        			, "2025_REG1"
        			, "2025_REG2"
        	);

        for (String yeartermCd : yeartermCds) {
            jdbcTemplate.update(
                "UPDATE STU_TUITION_REQ " +
                "SET PAY_DONE_YN = 'Y', PAY_DATE = SYSDATE " +
                "WHERE STUDENT_NO = ? AND YEARTERM_CD = ?",
                studentNo, yeartermCd
            );
            
            System.out.println("납부 완료 처리: " + studentNo + " - " + yeartermCd);
        }
    }

    
    
    /**
     * 등록금 납부 요청 프로시저 실행
     */
    private void createTuitionRequest(String studentNo, String yeartermCd) {
        String sql = "BEGIN PR_CREATE_TUITION_REQUEST_SINGLE(?, ?); END;";
        jdbcTemplate.update(sql, studentNo, yeartermCd);
        System.out.println("  → 등록금 납부 요청 생성 완료");
    }

    /**
     * 장학금 추가
     */
    @Transactional
    private void addScholarship(String studentNo, String yeartermCd, String scholarshipName) {
        // 1. STU_SCHOLARSHIP 존재 확인 및 추가
        ensureStudentScholarship(studentNo, yeartermCd);

        // 2. 장학금 ID 조회
        String scholarshipId = getScholarshipId(scholarshipName);
        if (scholarshipId == null) {
            System.err.println("  ✗ 장학금을 찾을 수 없음: " + scholarshipName);
            return;
        }

        // 3. 장학금 상세 중복 체크
        Integer existCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM STU_SCHOLAR_DETAIL " +
            "WHERE STUDENT_NO = ? AND YEARTERM_CD = ? AND SCHOLARSHIP_ID = ?",
            Integer.class,
            studentNo, yeartermCd, scholarshipId
        );

        if (existCount > 0) {
            System.out.println("  → 장학금 이미 존재: " + scholarshipName);
            return;
        }

        // 4. 장학금 상세 추가
        jdbcTemplate.update(
            "INSERT INTO STU_SCHOLAR_DETAIL (STUDENT_NO, YEARTERM_CD, SCHOLARSHIP_ID) " +
            "VALUES (?, ?, ?)",
            studentNo, yeartermCd, scholarshipId
        );
        System.out.println("  → 장학금 추가 완료: " + scholarshipName);

        // 5. 등록금 재계산
        recalculateTuition(studentNo, yeartermCd);
    }

    /**
     * STU_SCHOLARSHIP 존재 확인 및 추가
     */
    private void ensureStudentScholarship(String studentNo, String yeartermCd) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM STU_SCHOLARSHIP WHERE STUDENT_NO = ? AND YEARTERM_CD = ?",
            Integer.class,
            studentNo, yeartermCd
        );

        if (count == 0) {
            jdbcTemplate.update(
                "INSERT INTO STU_SCHOLARSHIP (YEARTERM_CD, STUDENT_NO, CREATE_AT) " +
                "VALUES (?, ?, SYSDATE)",
                yeartermCd, studentNo
            );
            System.out.println("  → STU_SCHOLARSHIP 추가");
        }
    }

    /**
     * 장학금 ID 조회
     */
    private String getScholarshipId(String scholarshipName) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT SCHOLARSHIP_ID FROM SCHOLARSHIP WHERE SCHOLARSHIP_NAME = ?",
                String.class,
                scholarshipName
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 등록금 재계산
     */
    private void recalculateTuition(String studentNo, String yeartermCd) {
        String sql = 
            "UPDATE STU_TUITION_REQ " +
            "SET SCHOLARSHIP_SUM = ( " +
            "    SELECT NVL(SUM(SC.AMOUNT), 0) " +
            "    FROM STU_SCHOLAR_DETAIL SSD " +
            "    INNER JOIN SCHOLARSHIP SC ON SSD.SCHOLARSHIP_ID = SC.SCHOLARSHIP_ID " +
            "    WHERE SSD.STUDENT_NO = STU_TUITION_REQ.STUDENT_NO " +
            "      AND SSD.YEARTERM_CD = STU_TUITION_REQ.YEARTERM_CD " +
            "), " +
            "FINAL_AMOUNT = TUITION_SUM - ( " +
            "    SELECT NVL(SUM(SC.AMOUNT), 0) " +
            "    FROM STU_SCHOLAR_DETAIL SSD " +
            "    INNER JOIN SCHOLARSHIP SC ON SSD.SCHOLARSHIP_ID = SC.SCHOLARSHIP_ID " +
            "    WHERE SSD.STUDENT_NO = STU_TUITION_REQ.STUDENT_NO " +
            "      AND SSD.YEARTERM_CD = STU_TUITION_REQ.YEARTERM_CD " +
            ") " +
            "WHERE STUDENT_NO = ? AND YEARTERM_CD = ?";

        jdbcTemplate.update(sql, studentNo, yeartermCd);
        System.out.println("  → 등록금 재계산 완료");
    }
}