package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.StuEnrollLctVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class G_StuEnrollDummyGenerator2 {

	@Autowired
	DummyDataMapper ddMapper;

	// ===== 공통 설정 =====
	private static final String ENROLL_STATUS = "ENR_DONE";
	private static final String RETAKE_YN = "N";
	private static final LocalDateTime STATUS_CHANGE_AT = LocalDateTime.of(2025, 6, 22, 0, 0);
	private static final double[] GRADE_POOL = { 4.5, 4.0, 3.5, 3.0, 2.5 };

	// ===== ENROLL_ID 더미 시퀀스 (직접 채움) =====
	// STENRL + 9자리, 시작값 900001000
	private long enrollSeq = 900001000L;

	private String nextEnrollId() {
		return "STENRL" + String.format("%09d", enrollSeq++);
	}

	// 학생 번호 범위 (이 중에서 끝자리 0~4만 공과대학으로 취급)
	private static final int RANGE_START = 202590000;
	private static final int RANGE_END = 202590994; // inclusive
	private static final int ENGI_NEED = 500;

	// 학번 끝자리 → 공과대학 학과
	private static String deptByTail(int tail) {
		switch (tail) {
		case 0:
			return "DEP-ENGI-EE";
		case 1:
			return "DEP-ENGI-ME";
		case 2:
			return "DEP-ENGI-CE";
		case 3:
			return "DEP-ENGI-CHE";
		case 4:
			return "DEP-ENGI-IE";
		default:
			throw new IllegalArgumentException("공과대학 아님 (tail=" + tail + ")");
		}
	}

	// === 과목 매핑 ===
	// 세미나(200~209): 학과 일치, 각 학과 2개 분반
	private static final Map<String, String[]> SEMINAR = new LinkedHashMap<>();
	// 윤리(210~219): 학과 일치, 각 학과 2개 분반
	private static final Map<String, String[]> ETHICS = new LinkedHashMap<>();
	// 수학/물리/파이썬: 학과 무관, 각 10개 분반을 36석씩 채움
	private static final List<String> MATH = Arrays.asList("LECT90000000220", "LECT90000000221", "LECT90000000222",
			"LECT90000000223", "LECT90000000224", "LECT90000000225", "LECT90000000226", "LECT90000000227",
			"LECT90000000228", "LECT90000000229");
	private static final List<String> PHYSICS = Arrays.asList("LECT90000000230", "LECT90000000231", "LECT90000000232",
			"LECT90000000233", "LECT90000000234", "LECT90000000235", "LECT90000000236", "LECT90000000237",
			"LECT90000000238", "LECT90000000239");
	private static final List<String> PYTHON = Arrays.asList("LECT90000000240", "LECT90000000241", "LECT90000000242",
			"LECT90000000243", "LECT90000000244", "LECT90000000245", "LECT90000000246", "LECT90000000247",
			"LECT90000000248", "LECT90000000249");

	private static final int CAP_SEM_ETH_PER_CLASS = 50; // 세미나/윤리 분반당 50
	private static final int CAP_OTHER_PER_CLASS = 36; // 수학/물리/파이썬 분반당 36

	static {
		// 공학입문세미나 200~209
		SEMINAR.put("DEP-ENGI-EE", new String[] { "LECT90000000200", "LECT90000000201" });
		SEMINAR.put("DEP-ENGI-ME", new String[] { "LECT90000000202", "LECT90000000203" });
		SEMINAR.put("DEP-ENGI-CE", new String[] { "LECT90000000204", "LECT90000000205" });
		SEMINAR.put("DEP-ENGI-CHE", new String[] { "LECT90000000206", "LECT90000000207" });
		SEMINAR.put("DEP-ENGI-IE", new String[] { "LECT90000000208", "LECT90000000209" });

		// 공학윤리와안전 210~219
		ETHICS.put("DEP-ENGI-EE", new String[] { "LECT90000000210", "LECT90000000211" });
		ETHICS.put("DEP-ENGI-ME", new String[] { "LECT90000000212", "LECT90000000213" });
		ETHICS.put("DEP-ENGI-CE", new String[] { "LECT90000000214", "LECT90000000215" });
		ETHICS.put("DEP-ENGI-CHE", new String[] { "LECT90000000216", "LECT90000000217" });
		ETHICS.put("DEP-ENGI-IE", new String[] { "LECT90000000218", "LECT90000000219" });
	}

	/** 공과대학 500명 학번(문자열) 수집: 끝자리 0~4 */
	private List<String> collectEngineeringStudents() {
		List<String> list = new ArrayList<>(ENGI_NEED);
		for (int n = RANGE_START; n <= RANGE_END && list.size() < ENGI_NEED; n++) {
			int tail = Math.floorMod(n, 10);
			if (tail >= 0 && tail <= 4) { // 공과대학
				list.add(String.format("%09d", n));
			}
		}
		if (list.size() != ENGI_NEED) {
			throw new IllegalStateException("공과대학 학생 수가 500이 아닙니다. size=" + list.size());
		}
		return list;
	}

	/** 성적 랜덤 */
	private double pickGrade() {
		int i = ThreadLocalRandom.current().nextInt(GRADE_POOL.length);
		return GRADE_POOL[i];
	}

	private int insertOne(String stuNo, String lectureId) {
		StuEnrollLctVO vo = new StuEnrollLctVO();
		vo.setEnrollId(nextEnrollId()); // ★ selectKey 대신 직접 채움
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

	/** 세미나/윤리: 학과별 100명 → 두 분반에 50/50 */
	private int assignDeptBoundCourses(List<String> engiStudents, Map<String, String[]> courseMap) {
		// 학과별 큐 구성
		Map<String, Deque<String>> byDept = new LinkedHashMap<>();
		byDept.put("DEP-ENGI-EE", new ArrayDeque<>());
		byDept.put("DEP-ENGI-ME", new ArrayDeque<>());
		byDept.put("DEP-ENGI-CE", new ArrayDeque<>());
		byDept.put("DEP-ENGI-CHE", new ArrayDeque<>());
		byDept.put("DEP-ENGI-IE", new ArrayDeque<>());

		for (String stu : engiStudents) {
			int tail = (stu.charAt(stu.length() - 1) - '0');
			String dept = deptByTail(tail);
			byDept.get(dept).add(stu);
		}

		int inserted = 0;
		// 각 학과 2분반에 50/50
		for (Map.Entry<String, String[]> e : courseMap.entrySet()) {
			String dept = e.getKey();
			String[] lectures = e.getValue(); // [0], [1]
			Deque<String> q = byDept.get(dept);
			// 분할
			for (int i = 0; i < CAP_SEM_ETH_PER_CLASS; i++) {
				String stu = q.pollFirst();
				if (stu == null)
					throw new IllegalStateException(dept + " 인원이 50 미만");
				inserted += insertOne(stu, lectures[0]);
			}
			for (int i = 0; i < CAP_SEM_ETH_PER_CLASS; i++) {
				String stu = q.pollFirst();
				if (stu == null)
					throw new IllegalStateException(dept + " 인원이 100 미만");
				inserted += insertOne(stu, lectures[1]);
			}
			if (!q.isEmpty()) {
				throw new IllegalStateException(dept + " 잔여 인원 발생: " + q.size());
			}
		}
		return inserted;
	}

	/** 수학/물리/파이썬: 학과 무관, 각 분반 36명 채움 */
	private int assignFreeCourses(List<String> engiStudents, List<String> lectures, int capPerClass) {
		int need = lectures.size() * capPerClass; // 360
		if (engiStudents.size() < need) {
			// 요구사항상 전체 500명 중에서 뽑아 채우면 됨(중복 수강 가능)
			// 여기서는 앞에서부터 need명 사용
		}
		int inserted = 0;
		int idx = 0;
		for (String lec : lectures) {
			for (int k = 0; k < capPerClass; k++) {
				String stu = engiStudents.get(idx++ % engiStudents.size());
				inserted += insertOne(stu, lec);
			}
		}
		return inserted;
	}

	@Test
	void insertEngineeringFreshmanEnrollments_V2() {
		// 1) 공과대학 500명 수집
		List<String> engi = collectEngineeringStudents();
		assertEquals(500, engi.size(), "공과대학 학생 수는 500이어야 합니다.");

		int inserted = 0;

		// 2) 공학입문세미나(학과 일치, 50/50)
		inserted += assignDeptBoundCourses(engi, SEMINAR);

		// 3) 공학윤리와안전(학과 일치, 50/50)
		inserted += assignDeptBoundCourses(engi, ETHICS);

		// 4) 공학수학(학과 무관, 각 36명)
		inserted += assignFreeCourses(engi, MATH, CAP_OTHER_PER_CLASS);

		// 5) 공학물리(학과 무관, 각 36명)
		inserted += assignFreeCourses(engi, PHYSICS, CAP_OTHER_PER_CLASS);

		// 6) 프로그래밍 기초 파이썬(학과 무관, 각 36명)
		inserted += assignFreeCourses(engi, PYTHON, CAP_OTHER_PER_CLASS);

		// 총 건수 검증: 500(세미나) + 500(윤리) + 360(수학) + 360(물리) + 360(파이썬) = 2,080
		log.info("Inserted STU_ENROLL_LCT rows (V2) = {}", inserted);
		assertEquals(2080, inserted, "총 2,080건이 삽입되어야 합니다.");
	}
}
