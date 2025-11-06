package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.LctGraderatioVO;
import kr.or.jsu.vo.LctWeekbyVO;
import kr.or.jsu.vo.LectureVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 2: LECTURE + LCT_WEEKBY + LCT_GRADERATIO 데이터를 구성한다.
 * 사용자 입력 값과 고정 값을 분리했으니 필요한 부분만 수정한 뒤 실행하세요.
 */
@SpringBootTest
@Slf4j
class Step2_LectureDummyGeneratorTest {
	
	// === 직접 입력해야 하는 값 ===
	private static final String USER_PREFIX = "218";
	private static final String PROFESSOR_NO = "2020" + USER_PREFIX + "1";
	
	@Test
	void insertLectureHierarchy() {
		for (LectureSeed seed : LECTURE_SEEDS) {
			insertLecture(seed);
			insertLectureWeeks(seed);
			insertLectureGradeRatios(seed);
		}
	}
	
	
	private static final String SUBJECT_CD_WEB_FRAMEWORK = "SUBJ00000000015";
	private static final String SUBJECT_CD_SOFTWARE_ENGINEERING = "SUBJ00000000008";
	private static final String SUBJECT_CD_OPEN_SOURCE = "SUBJ00000000010";
	private static final List<LectureSeed> LECTURE_SEEDS;
	// === 고정 값 ===
	private static final String YEARTERM_CD = "2025_REG2";
	private static final int MAX_CAP = 30;
	private static final String LECTURE_INDEX = "대학 웹프로그래밍 수업 개요와 프로젝트 기반 학습 운영 계획";
	private static final String LECTURE_GOAL = String.join(System.lineSeparator(),
			"웹 애플리케이션의 구조와 동작 원리를 이해한다.",
			"프론트엔드와 백엔드 기술 스택을 연계하여 기본 기능을 구현한다.",
			"RESTful API 설계와 데이터 연동을 경험하며 협업 역량을 기른다.",
			"보안·배포·테스트 관점에서 서비스 품질을 점검한다.");
	private static final String PREREQ_SUBJECT = "기초프로그래밍, 자료구조";
	private static final String CANCEL_YN = "N";
	private static final String SCORE_FINALIZE_YN = "N";

	private static final String[][] DEFAULT_WEEKLY_PLAN = {
			{ "웹과 HTTP 이해", "웹 아키텍처, HTTP 요청/응답 구조, 개발 환경을 정비한다." },
			{ "HTML5 기본", "콘텐츠 구조화, 시맨틱 태그, 접근성 고려 사항을 학습한다." },
			{ "CSS 레이아웃", "Flex/Grid 설계를 활용해 반응형 레이아웃을 설계한다." },
			{ "JavaScript 기초", "ES6 문법과 DOM 조작을 통해 동적 UI를 구현한다." },
			{ "프론트엔드 빌드", "모듈 번들러와 패키지 매니저를 사용해 개발 생산성을 높인다." },
			{ "Spring Boot 입문", "스프링 부트 프로젝트 생성과 계층형 구조를 설계한다." },
			{ "데이터베이스 연동", "JPA/MyBatis로 CRUD를 구현하고 트랜잭션을 이해한다." },
			{ "REST API 설계", "URI 디자인, 상태 코드, 예외 처리 규칙을 정립한다." },
			{ "JWT 인증", "로그인 흐름과 토큰 기반 인증 절차를 실습한다." },
			{ "파일 업로드", "멀티파트 처리와 S3/로컬 저장 전략을 비교한다." },
			{ "프론트-백엔드 통합", "Fetch/axios로 API를 호출하고 응답을 렌더링한다." },
			{ "테스트 자동화", "단위·통합 테스트 전략과 테스트 코드 작성을 실습한다." },
			{ "보안 기초", "OWASP Top 10 관점에서 웹 애플리케이션 취약점을 점검한다." },
			{ "배포 파이프라인", "CI/CD 파이프라인과 클라우드 배포 전략을 설계한다." },
			{ "최종 프로젝트 발표", "팀별 웹 서비스 결과물을 공유하고 피드백을 반영한다." }
	};

	private static final String[][] SOFTWARE_ENGINEERING_WEEKLY_PLAN = {
			{ "소프트웨어공학 개요", "교과목 운영 방식과 소프트웨어 공학의 역사, 역할을 소개한다." },
			{ "요구공학 및 이해관계자 분석", "사용자 인터뷰, 페르소나, 요구사항 분류 기법을 실습한다." },
			{ "요구사항 명세와 모델링", "Use Case, 사용자 스토리를 작성하고 명세 품질 기준을 학습한다." },
			{ "설계 원칙과 UML 기초", "UML 클래스/시퀀스 다이어그램으로 설계 개념을 시각화한다." },
			{ "소프트웨어 아키텍처 패턴", "Layered, MVC, Microservices 등 주요 패턴을 비교 분석한다." },
			{ "설계 품질과 코드 스타일", "SOLID 원칙과 리팩터링 기법을 활용해 설계 개선을 연습한다." },
			{ "구현 전략과 프레임워크 선택", "기술 스택 평가 기준과 프로토타입 개발 절차를 학습한다." },
			{ "테스트 전략", "단위·통합·시스템 테스트 계획을 수립하고 테스트 케이스를 설계한다." },
			{ "품질보증과 코드 리뷰", "정적 분석, 코드 리뷰 체크리스트를 적용해 품질을 향상한다." },
			{ "형상관리와 빌드 자동화", "Git 브랜치 전략과 CI 빌드 파이프라인을 실습한다." },
			{ "프로젝트 관리 기법", "애자일 Scrum, 칸반 보드를 활용해 일정과 작업을 관리한다." },
			{ "위험관리와 비용산정", "리스크 식별, 완화 전략, 기능점수 기반 비용산정 기법을 학습한다." },
			{ "배포와 운영 전환", "배포 전략, 모니터링, 서비스 수준 지표를 설계한다." },
			{ "유지보수와 지속적 개선", "결함 분석, 패치 릴리스, 기술 부채 관리 방법을 다룬다." },
			{ "종합 사례 발표", "팀별 프로젝트 산출물을 공유하고 피드백을 반영한다." }
	};

	private static final String[][] OPEN_SOURCE_DEVELOPMENT_WEEKLY_PLAN = {
			{ "오픈소스 생태계 이해", "오픈소스 정의, 라이선스 종류, 생태계 구조를 살펴본다." },
			{ "Git 기본과 분산버전관리", "Git 구조와 브랜치, 커밋 흐름을 실습한다." },
			{ "GitHub 협업 워크플로", "Fork, Pull Request 기반 기여 절차를 연습한다." },
			{ "이슈 트래킹과 프로젝트 보드", "이슈 템플릿, 라벨링, 프로젝트 보드 관리를 실습한다." },
			{ "기여 가이드라인과 코드 스타일", "CONTRIBUTING 문서 작성과 코드 스타일 체크 도구를 적용한다." },
			{ "테스트와 지속적 통합", "CI 도구를 활용해 자동 테스트 파이프라인을 구성한다." },
			{ "문서화와 커뮤니케이션", "README, 위키, 릴리즈 노트를 작성하는 방법을 학습한다." },
			{ "패키지 배포와 버전관리", "SemVer 규칙과 패키지 레지스트리에 배포하는 절차를 실습한다." },
			{ "오픈소스 보안", "라이선스 준수, 취약점 대응, 의존성 점검 도구를 소개한다." },
			{ "커뮤니티 문화와 윤리", "메일링 리스트, 채팅 채널에서의 커뮤니케이션 규칙을 다룬다." },
			{ "글로벌 협업과 로컬라이제이션", "국제화, 번역 워크플로, 접근성 고려 사항을 학습한다." },
			{ "프로젝트 분석", "참여할 오픈소스 프로젝트의 구조와 이슈를 조사한다." },
			{ "기여 계획 수립", "기여 범위를 정의하고 작업 일정을 수립한다." },
			{ "기여 결과 공유", "Pull Request 리뷰 대응과 기여 내용을 발표한다." },
			{ "커리어와 지속 참여 전략", "오픈소스 활동 포트폴리오와 커리어 확장 전략을 정리한다." }
	};

	private static final Map<String, Integer> GRADE_RATIO_MAP;
	private static final Map<String, String[][]> WEEKLY_PLANS_BY_SUBJECT;

static {
		GRADE_RATIO_MAP = new LinkedHashMap<>();
		GRADE_RATIO_MAP.put("EXAM", 40);
		GRADE_RATIO_MAP.put("TASK", 40);
		GRADE_RATIO_MAP.put("ATTD", 20);

		WEEKLY_PLANS_BY_SUBJECT = new LinkedHashMap<>();
		WEEKLY_PLANS_BY_SUBJECT.put(SUBJECT_CD_WEB_FRAMEWORK, DEFAULT_WEEKLY_PLAN);
		WEEKLY_PLANS_BY_SUBJECT.put(SUBJECT_CD_SOFTWARE_ENGINEERING, SOFTWARE_ENGINEERING_WEEKLY_PLAN);
		WEEKLY_PLANS_BY_SUBJECT.put(SUBJECT_CD_OPEN_SOURCE, OPEN_SOURCE_DEVELOPMENT_WEEKLY_PLAN);

		List<LectureSeed> seeds = new ArrayList<>();
		seeds.add(createSeed(1, SUBJECT_CD_WEB_FRAMEWORK));
		seeds.add(createSeed(2, SUBJECT_CD_SOFTWARE_ENGINEERING));
		seeds.add(createSeed(3, SUBJECT_CD_OPEN_SOURCE));
		LECTURE_SEEDS = Collections.unmodifiableList(seeds);
	}

	private static LectureSeed createSeed(int sequence, String subjectCd) {
		String lectureId = String.format("LECT%s%08d", USER_PREFIX, sequence);
		return new LectureSeed(lectureId, PROFESSOR_NO, subjectCd);
	}

	@Autowired
	private DummyDataMapper dummyDataMapper;

	void insertLecture(LectureSeed seed) {
		LectureVO lecture = new LectureVO();
		lecture.setLectureId(seed.lectureId());
		lecture.setSubjectCd(seed.subjectCd());
		lecture.setProfessorNo(seed.professorNo());
		lecture.setYeartermCd(YEARTERM_CD);
		lecture.setMaxCap(MAX_CAP);
		lecture.setLectureIndex(LECTURE_INDEX);
		lecture.setLectureGoal(LECTURE_GOAL);
		lecture.setPrereqSubject(PREREQ_SUBJECT);
		lecture.setCancelYn(CANCEL_YN);
		lecture.setEndAt(null);
		lecture.setScoreFinalizeYn(SCORE_FINALIZE_YN);

		int inserted = dummyDataMapper.insertOneDummyLecture(lecture);
		assertEquals(1, inserted, "LECTURE 테이블에 1건이 입력되어야 합니다.");
		log.info("LECTURE 입력 완료: {} (professorNo={}, subjectCd={}, yeartermCd={})",
				lecture.getLectureId(), lecture.getProfessorNo(), lecture.getSubjectCd(), lecture.getYeartermCd());
	}

	void insertLectureWeeks(LectureSeed seed) {
		String[][] weeklyPlan = WEEKLY_PLANS_BY_SUBJECT.getOrDefault(seed.subjectCd(), DEFAULT_WEEKLY_PLAN);

		List<LctWeekbyVO> weeks = new ArrayList<>(weeklyPlan.length);
		for (int i = 0; i < weeklyPlan.length; i++) {
			LctWeekbyVO vo = new LctWeekbyVO();
			vo.setLectureWeek(i + 1);
			vo.setLectureId(seed.lectureId());
			vo.setWeekGoal(weeklyPlan[i][0]);
			vo.setWeekDesc(weeklyPlan[i][1]);
			weeks.add(vo);
		}
		int inserted = dummyDataMapper.insertDummyLctWeekby(weeks);
		assertEquals(weeks.size(), inserted, "LCT_WEEKBY는 주차 수만큼 입력되어야 합니다.");
		log.info("LCT_WEEKBY 입력 완료: {}주차", weeks.size());
	}

	void insertLectureGradeRatios(LectureSeed seed) {
		List<LctGraderatioVO> ratios = new ArrayList<>(GRADE_RATIO_MAP.size());
		GRADE_RATIO_MAP.forEach((criteriaCd, ratio) -> {
			LctGraderatioVO vo = new LctGraderatioVO();
			vo.setGradeCriteiraCd(criteriaCd);
			vo.setLectureId(seed.lectureId());
			vo.setRatio(ratio);
			ratios.add(vo);
		});
		int inserted = dummyDataMapper.insertDummyLctGraderatio(ratios);
		assertEquals(ratios.size(), inserted, "LCT_GRADERATIO는 항목 수만큼 입력되어야 합니다.");
		int total = ratios.stream().mapToInt(LctGraderatioVO::getRatio).sum();
		log.info("LCT_GRADERATIO 입력 완료: {}건 (합계 {}%)", ratios.size(), total);
	}
	private static final class LectureSeed {
		private final String lectureId;
		private final String professorNo;
		private final String subjectCd;

		private LectureSeed(String lectureId, String professorNo, String subjectCd) {
			this.lectureId = lectureId;
			this.professorNo = professorNo;
			this.subjectCd = subjectCd;
		}

		private String lectureId() {
			return lectureId;
		}

		private String professorNo() {
			return professorNo;
		}

		private String subjectCd() {
			return subjectCd;
		}
	}
}
