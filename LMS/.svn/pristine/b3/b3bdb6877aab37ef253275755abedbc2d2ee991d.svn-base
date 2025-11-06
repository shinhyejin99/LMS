package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class D_ProfessorDummyGenerator {
	
	@Autowired
	DummyDataMapper ddMapper;
	
	@Test
	void insertProfessors() {
	    final int perYear = 20;            // 연도별 20명
	    final int total   = 100;           // 총 100명
	    final long userBase = 80000000000L; // USER 시작 인덱스

	    // 학생 더미에서 쓰던 학과코드 재사용 (10명마다 순환)
	    final String[] DEPT_CODES = {
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

	    int inserted = 0;
	    int seq = 0; // 0 ~ 99 (USER 인덱스)

	    for (int year = 2021; year <= 2025; year++) {
	        for (int idx = 1; idx <= perYear; idx++) {
	            if (seq >= total) break;

	            // USER80000000000 ~ USER80000000099
	            String userId = "USER" + String.format("%011d", userBase + seq);

	            // 교번: 20218001 ~ 20218020, 20228001 ~ ...
	            String professorNo = String.valueOf(year * 10000 + (8000 + idx));

	            // 학과코드: 10명 단위 순환
	            String univDeptCd = DEPT_CODES[seq % DEPT_CODES.length];

	            // VO 채우기
	            kr.or.jsu.vo.ProfessorVO p = new kr.or.jsu.vo.ProfessorVO();
	            p.setProfessorNo(professorNo);
	            p.setUserId(userId);
	            p.setUnivDeptCd(univDeptCd);
	            p.setEngLname("Doe");
	            p.setEngFname("Jane");
	            p.setPrfStatusCd("PRF_STATUS_ACTV");   // 재직중
	            p.setPrfAppntCd("PRF_APPNT_F_PROF");  // 전임교수
	            p.setPrfPositCd(null);                // 직책 없음

	            int r = ddMapper.insertOneDummyProfessor(p); // 매퍼에 맞춰 메서드명 사용
	            assertTrue(r == 1);
	            inserted += r;

	            seq++;
	        }
	    }

	    log.info("생성된 교수 rows: {}", inserted);
	}
}
