package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.SubjectVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class E_SubjectDummyGenerator2 {

	@Autowired
    DummyDataMapper ddMapper;

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2021, 1, 15, 0, 0);

    // 컴공 제외 10개 학과
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

    // 학과별 교양-핵심 4과목(타 학과 학생도 듣기 쉬운 주제)
    private static final Map<String, List<String>> GE_CORE_4 = new LinkedHashMap<>();
    static {
        GE_CORE_4.put("DEP-SOC-PUBA", List.of(
            "정책으로 보는 일상",
            "공공데이터 리터러시",
            "정부와 시민의 소통",
            "생활 속 규제와 권리"
        ));
        GE_CORE_4.put("DEP-SOC-POLS", List.of(
            "국제뉴스 읽기",
            "선거와 여론의 이해",
            "외교 이슈 한눈에",
            "정치 커뮤니케이션 입문"
        ));
        GE_CORE_4.put("DEP-SOC-ECON", List.of(
            "일상 속 경제학",
            "돈의 흐름과 금융 기초",
            "데이터로 보는 경제",
            "행동경제학 입문"
        ));
        GE_CORE_4.put("DEP-HUM-JPN", List.of(
            "생활 일본어와 문화",
            "일본 대중문화 읽기",
            "일본 현대사 한걸음",
            "여행자를 위한 일본어"
        ));
        GE_CORE_4.put("DEP-HUM-CHN", List.of(
            "생활 중국어와 문화",
            "중국 사회의 이해",
            "한중교류의 현재",
            "여행자를 위한 중국어"
        ));
        GE_CORE_4.put("DEP-ENGI-ME", List.of(
            "일상 공학의 발견",
            "메이킹으로 배우는 기초공학",
            "생활 속 에너지 이해",
            "디자인 씽킹 입문"
        ));
        GE_CORE_4.put("DEP-ENGI-IE", List.of(
            "문제해결과 프로세스 생각",
            "엑셀로 배우는 데이터 사고",
            "일 잘하는 업무설계",
            "서비스 디자인 기초"
        ));
        GE_CORE_4.put("DEP-ENGI-EE", List.of(
            "생활 전자공학 이야기",
            "전기·전자 기초 이해",
            "스마트기기의 원리",
            "디지털 세상 읽기"
        ));
        GE_CORE_4.put("DEP-ENGI-CHE", List.of(
            "생활 화학의 이해",
            "먹거리와 화학안전",
            "친환경 소재 이야기",
            "기초 분자이야기"
        ));
        GE_CORE_4.put("DEP-ENGI-CE", List.of(
            "도시와 인프라의 이해",
            "다리와 건축의 원리",
            "물이 만드는 도시",
            "안전한 사회를 위한 구조물"
        ));
    }

    // 과목코드: SUBJ + 11자리 시퀀스
    private long subjSeq = 90000000160L;
    private String nextSubjectCd() {
        return "SUBJ" + String.format("%011d", subjSeq++);
    }

    @Test
    @Transactional
    void insertGeneralEduCoreSubjects() {
        int subjectCount = 0;

        for (var entry : DEPTS.entrySet()) {
            String deptCd = entry.getKey();
            String deptName = entry.getValue();

            for (String title : GE_CORE_4.get(deptCd)) {
                String subjectCd = nextSubjectCd();
                SubjectVO s = new SubjectVO();
                s.setSubjectCd(subjectCd);
                s.setUnivDeptCd(deptCd);
                // 학과명을 붙여 충돌 최소화(선택사항): 예) "행정학과-정책으로 보는 일상(교핵)"
                s.setSubjectName(deptName + "-" + title + "(교핵)");
                s.setCompletionCd("GE_CORE"); // 교양-핵심
                s.setCredit(2);
                s.setHour(2);
                s.setCreateAt(CREATED_AT);
                s.setDeleteAt(null);

                subjectCount += ddMapper.insertOneDummySubject(s);

                // 교양-핵심은 전 학년 수강 가능 규칙 → SBJ_TARGET 미삽입
            }
        }

        log.info("Inserted GE_CORE SUBJECT rows = {}", subjectCount); // 기대: 10개 학과 × 4과목 = 40
        assertTrue(subjectCount == 10 * 4);
    }
}


/*
SUBJ00000000017	DEP-ENGI-CSE	코딩 리터러시 with Python	GE_CORE	2	2	2021-01-15 00:00:00.000	
SUBJ00000000018	DEP-ENGI-CSE	컴퓨팅적 사고와 JavaScript	GE_CORE	2	2	2021-01-15 00:00:00.000	
SUBJ00000000019	DEP-ENGI-CSE	파이썬으로 배우는 데이터 기초	GE_CORE	2	2	2021-01-15 00:00:00.000	
SUBJ00000000020	DEP-ENGI-CSE	생활 코딩 자동화	GE_CORE	2	2	2021-01-15 00:00:00.000	

이건 교양-핵심임. 특이사항으로는 모든 학년이 수강 가능해서, sbj_target에는 아무것도 안 등록해도 됨(그 테이블에 데이터가 없으면 전 학년 수강 가능한 규칙)
10학과가 "다른 학과 학생도 비교적 부담없이 들을 수 있을 만한 주제로" 각각 4과목씩 내놓는 데이터 만드는 테스트 코드.
*/
