package kr.or.jsu.aiDummyDataGenerator.campus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.CollegeVO;
import kr.or.jsu.vo.StaffDeptVO;
import kr.or.jsu.vo.UnivDeptVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class CampusStructureDummyGeneratorTest {

	private static final LocalDateTime BASE_TIME = LocalDateTime.of(2014, 3, 1, 9, 0);

	@Autowired
	private DummyDataMapper dummyDataMapper;

	@Test
	void insertCampusStructures() {
		List<CollegeVO> colleges = buildColleges();
		int collegeRows = dummyDataMapper.insertColleges(colleges);
		log.info("Inserted college rows: {}", collegeRows);

		List<UnivDeptVO> departments = buildDepartments(colleges);
		int deptRows = dummyDataMapper.insertUnivDepartments(departments);
		log.info("Inserted university department rows: {}", deptRows);

		List<StaffDeptVO> staffDepts = buildStaffDepartments();
		int staffRows = dummyDataMapper.insertStaffDepartments(staffDepts);
		log.info("Inserted staff department rows: {}", staffRows);
	}

	private List<CollegeVO> buildColleges() {
		List<CollegeVO> list = new ArrayList<>();
		list.add(college("COL-GENR", "대학공통", -1));
		list.add(college("COL-HUMN", "인문대학", 0));
		list.add(college("COL-SOCS", "사회과학대학", 1));
		list.add(college("COL-NATS", "자연과학대학", 2));
		list.add(college("COL-ENGR", "공과대학", 3));
		list.add(college("COL-EDUC", "사범대학", 4));
		list.add(college("COL-ARTS", "예술체육대학", 5));
		return list;
	}

	private CollegeVO college(String code, String name, int offsetDays) {
		CollegeVO vo = new CollegeVO();
		vo.setCollegeCd(code);
		vo.setCollegeName(name);
		vo.setCreateAt(BASE_TIME.plusDays(offsetDays));
		vo.setDeleteAt(null);
		return vo;
	}

	private List<UnivDeptVO> buildDepartments(List<CollegeVO> colleges) {
		List<UnivDeptVO> list = new ArrayList<>();
		list.add(dept("DEPT-GENR", "COL-GENR", "교양학부", -1));

		list.add(dept("DEPT-HUMN-COM", "COL-HUMN", "인문대 공통과목", 0));
		list.add(dept("DEPT-SOCS-COM", "COL-SOCS", "사회과학대 공통과목", 1));
		list.add(dept("DEPT-NATS-COM", "COL-NATS", "자연과학대 공통과목", 2));
		list.add(dept("DEPT-ENGR-COM", "COL-ENGR", "공과대 공통과목", 3));
		list.add(dept("DEPT-EDUC-COM", "COL-EDUC", "사범대 공통과목", 4));
		list.add(dept("DEPT-ARTS-COM", "COL-ARTS", "예술체육대 공통과목", 5));

		list.add(dept("DEPT-KORE", "COL-HUMN", "국어국문학과", 0));
		list.add(dept("DEPT-ENGL", "COL-HUMN", "영어영문학과", 0));
		list.add(dept("DEPT-HIST", "COL-HUMN", "역사학과", 0));

		list.add(dept("DEPT-ECON", "COL-SOCS", "경제학과", 1));
		list.add(dept("DEPT-POLS", "COL-SOCS", "정치외교학과", 1));
		list.add(dept("DEPT-PSYC", "COL-SOCS", "심리학과", 1));

		list.add(dept("DEPT-MATH", "COL-NATS", "수학과", 2));
		list.add(dept("DEPT-PHYS", "COL-NATS", "물리학과", 2));
		list.add(dept("DEPT-CHEM", "COL-NATS", "화학과", 2));
		list.add(dept("DEPT-BIOS", "COL-NATS", "생명과학과", 2));

		list.add(dept("DEPT-CIVL", "COL-ENGR", "토목공학과", 3));
		list.add(dept("DEPT-MECH", "COL-ENGR", "기계공학과", 3));
		list.add(dept("DEPT-ELIT", "COL-ENGR", "전기전자공학부", 3));
		list.add(dept("DEPT-COMP", "COL-ENGR", "컴퓨터공학부", 3));

		list.add(dept("DEPT-EDUC", "COL-EDUC", "교육학과", 4));
		list.add(dept("DEPT-CHED", "COL-EDUC", "초등교육과", 4));
		list.add(dept("DEPT-SPEC", "COL-EDUC", "특수교육과", 4));

		list.add(dept("DEPT-MUSI", "COL-ARTS", "음악학과", 5));
		list.add(dept("DEPT-FINE", "COL-ARTS", "미술학과", 5));
		list.add(dept("DEPT-PEPE", "COL-ARTS", "체육교육과", 5));

		return list;
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

	private List<StaffDeptVO> buildStaffDepartments() {
		List<StaffDeptVO> list = new ArrayList<>();
		list.add(staffDept("STFACAD", "학사지원과", "042-820-1001", 0));
		list.add(staffDept("STFGEN", "총무과", "042-820-1002", 1));
		list.add(staffDept("STFSTSV", "학생복지과", "042-820-1003", 2));
		list.add(staffDept("STFRSCH", "연구지원과", "042-820-1004", 3));
		list.add(staffDept("STFINTL", "국제협력과", "042-820-1005", 4));
		return list;
	}

	private StaffDeptVO staffDept(String code, String name, String tel, int offsetDays) {
		StaffDeptVO vo = new StaffDeptVO();
		vo.setStfDeptCd(code);
		vo.setStfDeptName(name);
		vo.setDeptTeleNo(tel);
		vo.setCreateAt(BASE_TIME.plusDays(offsetDays));
		vo.setDeleteAt(null);
		return vo;
	}
}
