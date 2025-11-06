package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StuEnrollLctVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class G_StuEnrollDummyGenerator3 {

	@Autowired
	DummyDataMapper ddMapper;

	// ===== 공통 설정 =====
	private static final String ENROLL_STATUS = "ENR_DONE";
	private static final String RETAKE_YN = "N";
	private static final LocalDateTime STATUS_CHANGE_AT = LocalDateTime.of(2025, 6, 22, 0, 0);
	private static final double[] GRADE_POOL = { 4.5, 4.0, 3.5, 3.0, 2.5 };

	// ===== ENROLL_ID 더미 시퀀스 (직전 작업 다음 값으로 시작값 조정) =====
	private long enrollSeq = 900003080L; // ← 필요 시 최신값으로 수정

	private String nextEnrollId() {
		return "STENRL" + String.format("%09d", enrollSeq++);
	}

	// ===== 대상 학생(컴공 80명) =====
	private static final int CSE_START = 202500001;
	private static final int CSE_END = 202500080; // inclusive

	// ===== 과목군(각 10개 분반) =====
	private static final List<String> MATH = Arrays.asList("LECT90000000220", "LECT90000000221", "LECT90000000222",
			"LECT90000000223", "LECT90000000224", "LECT90000000225", "LECT90000000226", "LECT90000000227",
			"LECT90000000228", "LECT90000000229");
	private static final List<String> PHYS = Arrays.asList("LECT90000000230", "LECT90000000231", "LECT90000000232",
			"LECT90000000233", "LECT90000000234", "LECT90000000235", "LECT90000000236", "LECT90000000237",
			"LECT90000000238", "LECT90000000239");
	private static final List<String> PYTH = Arrays.asList("LECT90000000240", "LECT90000000241", "LECT90000000242",
			"LECT90000000243", "LECT90000000244", "LECT90000000245", "LECT90000000246", "LECT90000000247",
			"LECT90000000248", "LECT90000000249");

	private double pickGrade() {
		int i = ThreadLocalRandom.current().nextInt(GRADE_POOL.length);
		return GRADE_POOL[i];
	}

	private int insertOne(String stuNo, String lectureId) {
		StuEnrollLctVO vo = new StuEnrollLctVO();
		vo.setEnrollId(nextEnrollId()); // 더미 ID 직접 세팅
		vo.setStudentNo(stuNo);
		vo.setLectureId(lectureId);
		vo.setEnrollStatusCd(ENROLL_STATUS);
		vo.setRetakeYn(RETAKE_YN);
		vo.setStatusChangeAt(STATUS_CHANGE_AT);
		vo.setAutoGrade(pickGrade());
		vo.setFinalGrade(pickGrade());
		vo.setChangeReason(null);
		return ddMapper.insertOneDummyEnrollment(vo);
	}

	/** 특정 과목군(10분반)에 students 전원을 고르게 배정: 총 N명을 분반당 N/10명씩(여기서는 8명) */
	private int assignAllToCourseGroup(List<String> students, List<String> lectures) {
		int n = students.size();
		int m = lectures.size(); // 10
		int base = n / m; // 8 (여기선 정확히 나눠떨어짐)
		int rem = n % m; // 0

		int inserted = 0;
		int idx = 0;

		// 고르게 분배(앞에서부터 base명씩, rem있으면 앞 분반부터 +1)
		for (int i = 0; i < m; i++) {
			int take = base + (i < rem ? 1 : 0);
			String lec = lectures.get(i);
			for (int k = 0; k < take; k++) {
				inserted += insertOne(students.get(idx++), lec);
			}
		}
		return inserted;
	}

	@Test
	void insertCSE80IntoAllThreeSubjects() {
		// 1) CSE 80명 수집
		List<String> cse = new ArrayList<>(80);
		for (int n = CSE_START; n <= CSE_END; n++) {
			cse.add(String.format("%09d", n));
		}
		assertEquals(80, cse.size(), "CSE 학생 수는 80이어야 합니다.");

		// 2) 공정성 위해 셔플(재현가능 시드)
		Collections.shuffle(cse, new Random(2025));

		// 3) 세 과목군 모두에 80명씩 등록 (각 과목군에서 분반당 8명)
		int inserted = 0;
		inserted += assignAllToCourseGroup(cse, MATH);
		inserted += assignAllToCourseGroup(cse, PHYS);
		inserted += assignAllToCourseGroup(cse, PYTH);

		log.info("Inserted STU_ENROLL_LCT rows (Gen3 CSE×3 subjects) = {}", inserted);
		assertEquals(240, inserted, "총 240건이 삽입되어야 합니다.");

		log.info("Last ENROLL_ID used = STENRL{}", String.format("%09d", enrollSeq - 1));
	}
}
