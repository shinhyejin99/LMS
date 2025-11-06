package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StuEntranceVO;
import kr.or.jsu.vo.StudentVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class C_StudentDummyGenerator {
	
	@Autowired
	DummyDataMapper ddMapper;
	
	int entranceYear = 2021;      // 입학년도(4자리)
    int count        = 1000;       // 몇 명 만들지
    String gradeCd   = "4TH";     // 이번 배치의 학년/학적코드
	
    private int startStudentNo() { // 시작하는 학번
        return entranceYear * 100000 + 90000; // ex) 2025 -> 202590000
    }
	
    // userId: 'USER' + (90000000000 + (studentNo - 시작학번)) [11자리 zero-padding]
    private String userIdFor(int studentNo) {
        long idx = 90000000000L + (studentNo - startStudentNo());
        return "USER" + String.format("%011d", idx);
    }
    
 // 학과코드/한글명 매핑 (주어진 10개)
    private static final String[] DEPT_CODES = {
        "DEP-ENGI-EE",   // 전자공학과
        "DEP-ENGI-ME",   // 기계공학과
        "DEP-ENGI-CE",   // 토목공학과
        "DEP-ENGI-CHE",  // 화학공학과
        "DEP-ENGI-IE",   // 산업공학과
        "DEP-HUM-JPN",   // 일본학과
        "DEP-HUM-CHN",   // 중어중문학과
        "DEP-SOC-ECON",  // 경제학과
        "DEP-SOC-POLS",  // 정치외교학과
        "DEP-SOC-PUBA"   // 행정학과
    };
    private static final String[] DEPT_NAMES = {
        "전자공학과",
        "기계공학과",
        "토목공학과",
        "화학공학과",
        "산업공학과",
        "일본학과",
        "중어중문학과",
        "경제학과",
        "정치외교학과",
        "행정학과"
    };
    
    
    @Test
    void insertStudentsWithEntrance() {
        int start = startStudentNo();
        int successStu = 0;
        int successEnt = 0;

        for (int i = 0; i < count; i++) {
            int studentNo = start + i;
            String userId  = userIdFor(studentNo);

            // 학과코드/학과명 순환
            int di = i % DEPT_CODES.length;
            String univDeptCd = DEPT_CODES[di];
            String targetDept = DEPT_NAMES[di];

            // 1) STUDENT 단건 INSERT
            StudentVO s = new StudentVO();
            s.setStudentNo(String.valueOf(studentNo));
            s.setUserId(userId);
            s.setUnivDeptCd(univDeptCd);
            s.setGradeCd(gradeCd);
            s.setStuStatusCd("ENROLLED");  // 재학 상태 더미
            s.setEngLname("Doe");
            s.setEngFname("John");
            // 보호자/지도교수는 예시 그대로 null
            s.setGuardName(null);
            s.setGuardRelation(null);
            s.setGuardPhone(null);
            s.setProfessorId(null);

            int r1 = ddMapper.insertOneDummyStudent(s);
            assertTrue(r1 == 1);
            successStu += r1;

            // 2) STU_ENTRANCE 단건 INSERT
            StuEntranceVO e = new StuEntranceVO();
            e.setStudentNo(String.valueOf(studentNo));
            e.setEntranceTypeCd("SU-GJ");          // 전형코드 더미
            e.setGradHighschool("대덕인재고등학교");
            e.setGradYear(String.valueOf(entranceYear)); // 입학년도와 동일
            e.setGradExamYn("N");
            e.setTargetDept(targetDept);           // 한글 학과명 입력

            int r2 = ddMapper.insertOneDummyStuEntrance(e);
            assertTrue(r2 == 1);
            successEnt += r2;
        }

        log.info("생성된 학생 rows: {}", successStu);
        log.info("생성된 입학 rows: {}", successEnt);
    }
}
