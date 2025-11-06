package kr.or.jsu.aiDummyDataGenerator.step3_staff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StaffVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class StaffDummyGeneratorTest {

	private static final int[] TARGET_YEARS = { 25, 24, 23, 22, 21, 20, 19 };
	private static final int STAFF_PER_YEAR = 100;

	private static final List<String> STAFF_DEPARTMENTS = List.of("STF-HR", "STF-ADM");

	@Autowired
	private DummyDataMapper dummyDataMapper;

	@Test
	void insertStaff() {
		int total = 0;
		Map<String, Integer> deptCounts = new HashMap<>();
		int deptCursor = 0;

		for (int year : TARGET_YEARS) {
			for (int seq = 0; seq < STAFF_PER_YEAR; seq++) {
				String userId = String.format("USER3%02d%08d", year, seq + 1);
				String staffNo = String.format("%04d%03d", 2000 + year, 700 + seq);
				String deptCode = STAFF_DEPARTMENTS.get(deptCursor % STAFF_DEPARTMENTS.size());
				deptCursor++;

				String teleNo = randomPhone();

				StaffVO staff = new StaffVO();
				staff.setStaffNo(staffNo);
				staff.setUserId(userId);
				staff.setStfDeptCd(deptCode);
				staff.setTeleNo(teleNo);

				dummyDataMapper.insertOneDummyStaff(staff);

				deptCounts.merge(deptCode, 1, Integer::sum);
				total++;
			}
		}

		log.info("Inserted staff rows: {}", total);
		deptCounts.forEach((dept, count) -> log.info("  - {} : {}", dept, count));
	}

	private String randomPhone() {
		ThreadLocalRandom r = ThreadLocalRandom.current();
		return String.format("042-820-%04d", r.nextInt(1000, 10000));
	}
}
