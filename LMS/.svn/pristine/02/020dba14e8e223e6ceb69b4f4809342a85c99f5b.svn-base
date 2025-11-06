package kr.or.jsu.lms.student.service.financialAid;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.ScholarshipDistributionDTO;
import kr.or.jsu.dto.ScholarshipHistoryDTO;
import kr.or.jsu.dto.ScholarshipResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class StuScholarshipServiceImplTest {

	@Autowired
	private StuScholarshipService service;

	private static final String TEST_STUDENT_NO = "202590994";

	@Test
	void testGetScholarshipInfo() {
		log.info("testGetScholarshipInfo 시작 - 학번: {}", TEST_STUDENT_NO);
        
        // When: getScholarshipInfo 한 번만 호출하여 모든 정보 조회
		ScholarshipResponseDTO response = service.getScholarshipInfo(TEST_STUDENT_NO);

		assertNotNull(response, "ScholarshipResponseDTO는 null 아님");

        // 1. 총 수혜 금액 (totalScholarship)
        Integer totalAmount = response.getTotalScholarship();
		assertNotNull(totalAmount, "총 수혜 금액 필드는 null이 아님");
		assertTrue(totalAmount >= 0, "총 수혜 금액은 0 이상");
		log.info("Service 결과 - 총 수혜 금액: {}원", totalAmount);

        // 2. 종류별 분포 (distribution)
        List<ScholarshipDistributionDTO> distributionList = response.getScholarshipDistribution();
		assertNotNull(distributionList, "분포 리스트는 null이 아님");
		log.info("Service 결과 - 조회된 장학금 종류 수: {}", distributionList.size());

		if (!distributionList.isEmpty()) {
			ScholarshipDistributionDTO firstDist = distributionList.get(0);
			assertNotNull(firstDist.getScholarshipName(), "분포 DTO: 장학금 이름 필드가 null이 아님");
			log.info("분포 첫 항목: {} - 총액: {}", firstDist.getScholarshipName(), firstDist.getTotalAmount());
		}

        // 3. 수혜 내역 (history)
        List<ScholarshipHistoryDTO> historyList = response.getScholarshipHistory();
		assertNotNull(historyList, "수혜 내역 리스트는 null이 아님");
		log.info("Service 결과 - 조회된 수혜 내역 건수: {}", historyList.size());

		if (!historyList.isEmpty()) {
			ScholarshipHistoryDTO firstHist = historyList.get(0);
			assertNotNull(firstHist.getSemesterName(), "내역 DTO: 학기 이름 필드가 null이 아님");
			assertNotNull(firstHist.getScholarshipName(), "내역 DTO: 장학금 이름 필드가 null이 아님");
			log.info("내역 첫 항목: {} - 금액: {}", firstHist.getSemesterName(), firstHist.getAmount());
		}
	}
}
