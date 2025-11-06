package kr.or.jsu.aiDummyDataGenerator.step2_campus;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StaffDeptVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class Step2_1_StaffDeftDummyGeneratorTest {

	private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 9, 26, 20, 42, 19);

	@Autowired
	private DummyDataMapper dummyDataMapper;

	@Test
	void insertStaffDepartments() {
		List<StaffDeptVO> staffDepartments = buildStaffDepartments();
		int insertedRows = dummyDataMapper.insertStaffDepartments(staffDepartments);
		log.info("Inserted staff department rows: {}", insertedRows);
	}

	private List<StaffDeptVO> buildStaffDepartments() {
		return List.of(
				staffDept("STF-HR", "인사처", "042-123-1000"),
				staffDept("STF-ADM", "행정처", "042-123-1100"));
	}

	private StaffDeptVO staffDept(String code, String name, String tel) {
		StaffDeptVO vo = new StaffDeptVO();
		vo.setStfDeptCd(code);
		vo.setStfDeptName(name);
		vo.setDeptTeleNo(tel);
		vo.setCreateAt(CREATED_AT);
		vo.setDeleteAt(null);
		return vo;
	}
}
