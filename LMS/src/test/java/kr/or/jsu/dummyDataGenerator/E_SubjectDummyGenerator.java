package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.SbjTargetVO;
import kr.or.jsu.vo.SubjectVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class E_SubjectDummyGenerator {

    @Autowired
    DummyDataMapper ddMapper;

    // 생성일 고정
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2021, 1, 15, 0, 0);

    // 학기코드(정규 1,2학기)
    private static final List<String> TERMS_REG = List.of("REG1", "REG2");

    // ===== 학과(컴공 제외 10개) =====
    private static final Map<String, String> DEPTS = new LinkedHashMap<>();
    static {
        DEPTS.put("DEP-SOC-PUBA", "행정학과");
        DEPTS.put("DEP-SOC-POLS", "정치외교학과");
        DEPTS.put("DEP-SOC-ECON", "경제학과");
        DEPTS.put("DEP-HUM-JPN",  "일본학과");
        DEPTS.put("DEP-HUM-CHN",  "중어중문학과");
        DEPTS.put("DEP-ENGI-ME",  "기계공학과");
        DEPTS.put("DEP-ENGI-IE",  "산업공학과");
        DEPTS.put("DEP-ENGI-EE",  "전자공학과");
        DEPTS.put("DEP-ENGI-CHE", "화학공학과");
        DEPTS.put("DEP-ENGI-CE",  "토목공학과");
    }

    // ===== 학과별 과목 풀 =====
    private static final Map<String, List<String>> CORE_FRESH_2 = Map.ofEntries(
        Map.entry("DEP-SOC-PUBA", List.of("행정학입문", "공공관리론")),
        Map.entry("DEP-SOC-POLS", List.of("정치학입문", "국제관계입문")),
        Map.entry("DEP-SOC-ECON", List.of("경제학입문", "미시경제입문")),
        Map.entry("DEP-HUM-JPN",  List.of("일본학입문", "기초일본어")),
        Map.entry("DEP-HUM-CHN",  List.of("중국학입문", "기초중국어")),
        Map.entry("DEP-ENGI-ME",  List.of("기계공학개론", "공학수학(기초)")),
        Map.entry("DEP-ENGI-IE",  List.of("산업공학개론", "공학통계(기초)")),
        Map.entry("DEP-ENGI-EE",  List.of("전자공학개론", "회로이론(기초)")),
        Map.entry("DEP-ENGI-CHE", List.of("화학공학개론", "물리화학(기초)")),
        Map.entry("DEP-ENGI-CE",  List.of("토목공학개론", "측량학기초"))
    );

    private static final Map<String, List<String>> CORE_UPPER_6 = Map.ofEntries(
        Map.entry("DEP-SOC-PUBA", List.of("조직이론", "정책학", "인사행정", "재무행정", "전자정부론", "행정법총론")),
        Map.entry("DEP-SOC-POLS", List.of("비교정치", "국제정치론", "정치사상사", "한국정치론", "외교정책론", "정치경제학")),
        Map.entry("DEP-SOC-ECON", List.of("미시경제학", "거시경제학", "계량경제학", "산업조직", "국제무역", "화폐금융")),
        Map.entry("DEP-HUM-JPN",  List.of("중급일본어", "고급일본어", "일본문학개론", "일본문화론", "일본사회와경제", "일본어문법")),
        Map.entry("DEP-HUM-CHN",  List.of("중급중국어", "고급중국어", "중국문학개론", "중국문화론", "중국경제개관", "중국어문법")),
        Map.entry("DEP-ENGI-ME",  List.of("재료역학", "열역학", "유체역학", "동역학", "기계설계", "제조공정")),
        Map.entry("DEP-ENGI-IE",  List.of("품질경영", "경영과학", "생산관리", "데이터분석기초", "인간공학", "시스템시뮬레이션")),
        Map.entry("DEP-ENGI-EE",  List.of("전자회로", "신호와시스템", "전자기학", "디지털공학", "반도체소자", "통신공학")),
        Map.entry("DEP-ENGI-CHE", List.of("화공열역학", "화학공정계산", "반응공학", "유체역학(화공)", "전달현상", "공정제어")),
        Map.entry("DEP-ENGI-CE",  List.of("구조역학", "토질역학", "철근콘크리트", "유체역학(수공학)", "교통공학", "수문학"))
    );

    private static final Map<String, List<String>> ELEC_UPPER_8 = Map.ofEntries(
        Map.entry("DEP-SOC-PUBA", List.of("지방자치론", "정책평가", "규제정책", "공공데이터분석", "비교행정", "민관협력", "정부와사회", "공공갈등관리")),
        Map.entry("DEP-SOC-POLS", List.of("선거와여론", "국제안보론", "동아시아정치", "국제기구론", "분쟁해결", "공공외교", "의회정치", "정치커뮤니케이션")),
        Map.entry("DEP-SOC-ECON", List.of("경제성장이론", "공공경제학", "노동경제학", "게임이론", "환경경제학", "국제금융", "행동경제학", "데이터경제분석")),
        Map.entry("DEP-HUM-JPN",  List.of("일본어작문", "일본어회화", "일본영화와문화", "일본현대사", "비즈니스일본어", "일본어번역실습", "일본어통역입문", "일본지역연구")),
        Map.entry("DEP-HUM-CHN",  List.of("중국어작문", "중국어회화", "중국현대사", "비즈니스중국어", "중국어번역실습", "중국정치와외교", "중국지역연구", "한중통상관계")),
        Map.entry("DEP-ENGI-ME",  List.of("열전달", "CAD/CAM", "로봇공학개론", "자동차공학", "공기역학", "메카트로닉스", "고체역학특론", "에너지시스템")),
        Map.entry("DEP-ENGI-IE",  List.of("공급망관리", "최적화응용", "서비스공학", "프로젝트관리", "빅데이터분석", "통계적품질관리", "물류관리", "UX설계")),
        Map.entry("DEP-ENGI-EE",  List.of("임베디드시스템", "VLSI개론", "제어공학", "마이크로파공학", "무선통신", "컴퓨터비전개론", "센서공학", "디지털신호처리")),
        Map.entry("DEP-ENGI-CHE", List.of("생물화학공학", "고분자공학", "분리공정", "나노소재공학", "환경공정", "공정시뮬레이션", "촉매공학", "에너지화학공학")),
        Map.entry("DEP-ENGI-CE",  List.of("구조해석", "지반공학설계", "도로공학", "수리학특론", "건설관리", "BIM개론", "환경수리학", "도시계획개론"))
    );

    // 과목코드 시퀀스: SUBJ + 11자리(총 15자리)
    private long subjSeq = 90000000000L;
    private String nextSubjectCd() {
        return "SUBJ" + String.format("%011d", subjSeq++);
    }

    @Test
    void insertSubjects() {
        int subjectCount = 0;
        int targetCount  = 0;

        for (var entry : DEPTS.entrySet()) {
            String deptCd   = entry.getKey();
            String deptName = entry.getValue();

            // 1) 1학년 전핵 2과목
            for (String name : CORE_FRESH_2.get(deptCd)) {
                String subjectCd = nextSubjectCd();
                SubjectVO s = subjectOf(subjectCd, deptCd, deptName + "-" + name + "(전핵·1학년)", "MAJ_CORE");
                subjectCount += ddMapper.insertOneDummySubject(s);
                // SBJ_TARGET: 1학년 REG1/REG2
                targetCount += insertTargets(subjectCd, List.of("1ST"), TERMS_REG);
            }

            // 2) (2학년 이상) 전핵 6과목
            for (String name : CORE_UPPER_6.get(deptCd)) {
                String subjectCd = nextSubjectCd();
                SubjectVO s = subjectOf(subjectCd, deptCd, deptName + "-" + name + "(전핵)", "MAJ_CORE");
                subjectCount += ddMapper.insertOneDummySubject(s);
                // 2~4학년, REG1/REG2
                targetCount += insertTargets(subjectCd, List.of("2ND", "3RD", "4TH"), TERMS_REG);
            }

            // 3) (3학년 이상) 전선 8과목
            for (String name : ELEC_UPPER_8.get(deptCd)) {
                String subjectCd = nextSubjectCd();
                SubjectVO s = subjectOf(subjectCd, deptCd, deptName + "-" + name + "(전선)", "MAJ_ELEC");
                subjectCount += ddMapper.insertOneDummySubject(s);
                // 3~4학년, REG1/REG2
                targetCount += insertTargets(subjectCd, List.of("3RD", "4TH"), TERMS_REG);
            }
        }

        log.info("Inserted SUBJECT rows = {}", subjectCount);     // 10개 학과 × (2+6+8)=16과목 = 160건
        log.info("Inserted SBJ_TARGET rows = {}", targetCount);   // 1학년:2×2학기=2, 상위전핵:3학년수×2학기=6, 전선:2×2학기=4 → 과목당 타깃: 1학년=2, 전핵=6, 전선=4
        assertTrue(subjectCount == 10 * 16);
        // SBJ_TARGET 기대치: 10개 학과 × ( 2과목×2 + 6과목×(3×2) + 8과목×(2×2) ) = 10 × (4 + 36 + 32) = 720
        assertTrue(targetCount == 720);
    }

    // ===== Helper =====
    private SubjectVO subjectOf(String subjectCd, String deptCd, String subjectName, String completionCd) {
        SubjectVO vo = new SubjectVO();
        vo.setSubjectCd(subjectCd);
        vo.setUnivDeptCd(deptCd);
        vo.setSubjectName(subjectName);
        vo.setCompletionCd(completionCd); // MAJ_CORE / MAJ_ELEC
        vo.setCredit(3);
        vo.setHour(3);
        vo.setCreateAt(CREATED_AT);
        vo.setDeleteAt(null);
        return vo;
    }

    private int insertTargets(String subjectCd, List<String> gradeCds, List<String> termCds) {
        int cnt = 0;
        for (String g : gradeCds) {
            for (String t : termCds) {
                SbjTargetVO tv = new SbjTargetVO();
                tv.setSubjectCd(subjectCd); // PK part
                tv.setGradeCd(g);           // PK part: 1ST/2ND/3RD/4TH
                tv.setTermCd(t);            // PK part: REG1/REG2 (정규학기)
                cnt += ddMapper.insertOneDummySbjTarget(tv);
            }
        }
        return cnt;
    }
}

/*
SUBJ00000000001	DEP-ENGI-CSE	컴퓨팅사고와 문제해결(전핵·1학년)	MAJ_CORE	전공-핵심	3	3	2021-01-15 00:00:00.000	
SUBJ00000000002	DEP-ENGI-CSE	자료구조	MAJ_CORE	전공-핵심	3	3	2021-01-15 00:00:00.000	
SUBJ00000000003	DEP-ENGI-CSE	컴퓨터구조	MAJ_CORE	전공-핵심	3	3	2021-01-15 00:00:00.000	
SUBJ00000000004	DEP-ENGI-CSE	운영체제	MAJ_CORE	전공-핵심	3	3	2021-01-15 00:00:00.000	
SUBJ00000000005	DEP-ENGI-CSE	알고리즘	MAJ_CORE	전공-핵심	3	3	2021-01-15 00:00:00.000	
SUBJ00000000006	DEP-ENGI-CSE	데이터베이스	MAJ_CORE	전공-핵심	3	3	2021-01-15 00:00:00.000	
SUBJ00000000007	DEP-ENGI-CSE	컴퓨터네트워크	MAJ_CORE	전공-핵심	3	3	2021-01-15 00:00:00.000	
SUBJ00000000008	DEP-ENGI-CSE	소프트웨어공학	MAJ_CORE	전공-핵심	3	3	2021-01-15 00:00:00.000	
SUBJ00000000009	DEP-ENGI-CSE	모바일앱 프로그래밍	MAJ_ELEC	전공-선택	3	3	2021-01-15 00:00:00.000	
SUBJ00000000010	DEP-ENGI-CSE	오픈소스 SW 개발	MAJ_ELEC	전공-선택	3	3	2021-01-15 00:00:00.000	
SUBJ00000000011	DEP-ENGI-CSE	기계학습 개론	MAJ_ELEC	전공-선택	3	3	2021-01-15 00:00:00.000	
SUBJ00000000012	DEP-ENGI-CSE	컴퓨터그래픽스	MAJ_ELEC	전공-선택	3	3	2021-01-15 00:00:00.000	
SUBJ00000000013	DEP-ENGI-CSE	네트워크보안	MAJ_ELEC	전공-선택	3	3	2021-01-15 00:00:00.000	
SUBJ00000000014	DEP-ENGI-CSE	클라우드컴퓨팅	MAJ_ELEC	전공-선택	3	3	2021-01-15 00:00:00.000	
SUBJ00000000015	DEP-ENGI-CSE	웹프로그래밍	MAJ_ELEC	전공-선택	3	3	2021-01-15 00:00:00.000	
SUBJ00000000016	DEP-ENGI-CSE	임베디드시스템	MAJ_ELEC	전공-선택	3	3	2021-01-15 00:00:00.000	

DEP-ENGI-CSE	컴퓨터공학과
위는 컴퓨터공학과에서 담당하는 더미 과목 16개임.
전공-핵심은 예외를 제외하면 해당 학과의 2학년 이상이 수강하고,
전공-선택은 해당 학과의 3학년 이상이 선택적으로 수강함.

과목 번호는
SUBJ90000000000 부터 시작하게 해서, 내가 제공하는 각 학과마다 1학년 대상 전핵 2과목, 전핵 6과목, 전선 8과목을 만들게 하는 자바 테스트 코드를 줘봐.

DEP-SOC-PUBA	행정학과
DEP-SOC-POLS	정치외교학과
DEP-SOC-ECON	경제학과
DEP-HUM-JPN	일본학과
DEP-HUM-CHN	중어중문학과
DEP-ENGI-ME	기계공학과
DEP-ENGI-IE	산업공학과
DEP-ENGI-EE	전자공학과
DEP-ENGI-CHE	화학공학과
DEP-ENGI-CE	토목공학과

@SpringBootTest
@Slf4j
class E_SubjectDummyGenerator {
	
	@Autowired
	DummyDataMapper ddMapper;
	
	@Test
	void insertSubjects() {
	    
		SubjectVO dummySubject;
		
		SbjTargetVO dummySubTarget;
		
	}
}
테스트 코드는 이걸 기반으로 하고.

테이블명	교과목		테이블ID	SUBJECT	
1	SUBJECT_CD		과목코드	과목코드	VARCHAR2(15)	NOT NULL		PK	
2	UNIV_DEPT_CD		담당학과코드	학과코드	VARCHAR2(50)	NOT NULL		FK(학과)	
3	SUBJECT_NAME		과목명		VARCHAR2(50)	NOT NULL			
4	COMPLETION_CD		이수구분코드	공통코드	VARCHAR2(50)	NOT NULL			전공필수, 전공선택, 교양필수, 기초필수 등...
5	CREDIT		학점		NUMBER(1)	NOT NULL			
6	HOUR		시수		NUMBER(2)	NOT NULL			
7	CREATE_AT		등록일		DATE	NOT NULL			
8	DELETE_AT		폐지일		DATE				NULL이 아니면 활성화된 과목입니다.

테이블명	7.1 교과목별_대상학년		테이블ID	SBJ_TARGET	
1	SUBJECT_CD		과목코드	과목코드	VARCHAR2(15)	NOT NULL		PK, FK(교과목)	
2	GRADE_CD		대상학년	공통코드	VARCHAR2(50)	NOT NULL		PK	1ST, 2ND, 3RD, 4TH. 졸업유예(DEFER)는 4학년으로 취급합니다.
3	TERM_CD		대상학기	공통코드	VARCHAR2(50)	NOT NULL		PK	학기코드 : 5. LMS 자료사전

테이블은 이렇게 구성되어 있고, 각 vo들의 필드명은 컬럼명을 카멜 케이스로 바꾼 거임.

SELECT * FROM sbj_target;

각각 대상학년, 대상학기, 과목코드임. 1학년 과목은이면 1학년 1학기, 1학년 2학기 2개의 레코드를 넣으면 됨.
1ST	REG1	SUBJ00000000001
1ST	REG1	SUBJ00000000022
1ST	REG1	SUBJ00000000023
1ST	REG1	SUBJ00000000024

컴공은 제외하고, 내가 제외한 10개 학과로만 구성해.

각 과목 이름은 실제로 그 학과에서 가르칠 만한 과목 이름이어야 함.
*/
