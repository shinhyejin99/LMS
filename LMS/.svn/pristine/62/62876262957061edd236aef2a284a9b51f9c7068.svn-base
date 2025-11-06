package kr.or.jsu.aiDummyDataGenerator.step2_campus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.CollegeVO;
import kr.or.jsu.vo.UnivDeptVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class Step2_2_UnivDeftDummyGeneratorTest {

	private static final LocalDateTime BASE_TIME = LocalDateTime.of(2014, 3, 1, 9, 0);

	@Autowired
	private DummyDataMapper dummyDataMapper;

	@Test
	void insertCollegesAndDepartments() {
		List<CollegeVO> colleges = buildColleges();
		int collegeRows = dummyDataMapper.insertColleges(colleges);
		log.info("Inserted college rows: {}", collegeRows);

		List<UnivDeptVO> departments = buildDepartments();
		int deptRows = dummyDataMapper.insertUnivDepartments(departments);
		log.info("Inserted university department rows: {}", deptRows);
	}

	private List<CollegeVO> buildColleges() {
		List<CollegeVO> colleges = new ArrayList<>();
		colleges.add(college("COL-GENR", "기초교양대학", -1));
		colleges.add(college("COL-HUMN", "인문대학", 0));
		colleges.add(college("COL-SOCS", "사회과학대학", 1));
		colleges.add(college("COL-NATS", "자연과학대학", 2));
		colleges.add(college("COL-ENGR", "공과대학", 3));
		colleges.add(college("COL-EDUC", "사범대학", 4));
		colleges.add(college("COL-ARTS", "예술체육대학", 5));
		return colleges;
	}

	private CollegeVO college(String code, String name, int offsetDays) {
		CollegeVO vo = new CollegeVO();
		vo.setCollegeCd(code);
		vo.setCollegeName(name);
		vo.setCreateAt(BASE_TIME.plusDays(offsetDays));
		vo.setDeleteAt(null);
		return vo;
	}

	private List<UnivDeptVO> buildDepartments() {
		List<UnivDeptVO> departments = new ArrayList<>();
		departments.add(dept("DEPT-GENR", "COL-GENR", "기초교양학부", -1));

		departments.add(dept("DEPT-HUMN-COM", "COL-HUMN", "인문대 공통교양", 0));
		departments.add(dept("DEPT-SOCS-COM", "COL-SOCS", "사회대 공통교양", 1));
		departments.add(dept("DEPT-NATS-COM", "COL-NATS", "자연대 공통교양", 2));
		departments.add(dept("DEPT-ENGR-COM", "COL-ENGR", "공대 공통교양", 3));
		departments.add(dept("DEPT-EDUC-COM", "COL-EDUC", "사범대 공통교양", 4));
		departments.add(dept("DEPT-ARTS-COM", "COL-ARTS", "예체대 공통교양", 5));

		departments.add(dept("DEPT-KORE", "COL-HUMN", "국어국문학과", 0));
		departments.add(dept("DEPT-ENGL", "COL-HUMN", "영어영문학과", 0));
		departments.add(dept("DEPT-HIST", "COL-HUMN", "사학과", 0));

		departments.add(dept("DEPT-ECON", "COL-SOCS", "경제학과", 1));
		departments.add(dept("DEPT-POLS", "COL-SOCS", "정치외교학과", 1));
		departments.add(dept("DEPT-PSYC", "COL-SOCS", "심리학과", 1));

		departments.add(dept("DEPT-MATH", "COL-NATS", "수학과", 2));
		departments.add(dept("DEPT-PHYS", "COL-NATS", "물리학과", 2));
		departments.add(dept("DEPT-CHEM", "COL-NATS", "화학과", 2));
		departments.add(dept("DEPT-BIOS", "COL-NATS", "생명과학과", 2));

		departments.add(dept("DEPT-CIVL", "COL-ENGR", "토목공학과", 3));
		departments.add(dept("DEPT-MECH", "COL-ENGR", "기계공학과", 3));
		departments.add(dept("DEPT-ELIT", "COL-ENGR", "전자전기공학과", 3));
		departments.add(dept("DEPT-COMP", "COL-ENGR", "컴퓨터공학과", 3));

		departments.add(dept("DEPT-EDUC", "COL-EDUC", "교육학과", 4));
		departments.add(dept("DEPT-CHED", "COL-EDUC", "초등교육과", 4));
		departments.add(dept("DEPT-SPEC", "COL-EDUC", "특수교육과", 4));

		departments.add(dept("DEPT-MUSI", "COL-ARTS", "음악과", 5));
		departments.add(dept("DEPT-FINE", "COL-ARTS", "미술학과", 5));
		departments.add(dept("DEPT-PEPE", "COL-ARTS", "체육학과", 5));

		return departments;
	}

	private UnivDeptVO dept(String code, String collegeCd, String name, int offsetDays) {
		UnivDeptVO vo = new UnivDeptVO();
		vo.setUnivDeptCd(code);
		vo.setCollegeCd(collegeCd);
		vo.setUnivDeptName(name);
		vo.setCreateAt(BASE_TIME.plusDays(offsetDays));
		vo.setDeleteAt(null);
		return vo;
	}
}
