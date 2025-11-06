package kr.or.jsu.aiDummyDataGenerator.step6_subject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.SbjTargetVO;
import kr.or.jsu.vo.SubjectVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class SubjectAndTargetDummyGeneratorTest {

    private static final LocalDateTime CREATE_AT = LocalDateTime.of(2025, 2, 1, 9, 0);
    private static final AtomicLong SUBJECT_SEQ = new AtomicLong(1);

    private static final String REG1 = "REG1";
    private static final String REG2 = "REG2";
    private static final String SUB1 = "SUB1";
    private static final String SUB2 = "SUB2";

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertSubjectsAndTargets() {
        List<SubjectBlueprint> blueprints = new ArrayList<>();
        blueprints.addAll(buildMajorBasicSubjects());
        blueprints.addAll(buildMajorCoreSubjects());
        blueprints.addAll(buildMajorElectiveSubjects());
        blueprints.addAll(buildGeneralBasicSubjects());
        blueprints.addAll(buildGeneralCoreSubjects());
        blueprints.addAll(buildGeneralElectiveSubjects());
        blueprints.addAll(buildAdditionalSubjects());
        blueprints.addAll(buildCareerDesignSubjects());

        Map<String, Long> blueprintCountByDept = blueprints.stream()
                .collect(Collectors.groupingBy(SubjectBlueprint::univDeptCd, LinkedHashMap::new, Collectors.counting()));
        log.info("Blueprint subject count by department: {}", blueprintCountByDept);
        int plannedTargets = blueprints.stream().mapToInt(bp -> bp.targets().size()).sum();
        log.info("Planned totals - subjects: {}, targets: {}", blueprints.size(), plannedTargets);

        int subjectCount = 0;
        int targetCount = 0;

        for (SubjectBlueprint blueprint : blueprints) {
            String subjectCd = nextSubjectCd();
            SubjectVO subject = new SubjectVO();
            subject.setSubjectCd(subjectCd);
            subject.setUnivDeptCd(blueprint.univDeptCd());
            subject.setSubjectName(blueprint.subjectName());
            subject.setCompletionCd(blueprint.completionCd());
            subject.setSubjectTypeCd(blueprint.subjectTypeCd());
            subject.setCredit(blueprint.credit());
            subject.setHour(blueprint.hour());
            subject.setCreateAt(CREATE_AT);
            subject.setDeleteAt(null);

            subjectCount += dummyDataMapper.insertOneDummySubject(subject);

            for (GradeTerm target : blueprint.targets()) {
                SbjTargetVO targetVO = new SbjTargetVO();
                targetVO.setSubjectCd(subjectCd);
                targetVO.setGradeCd(target.gradeCd());
                targetVO.setTermCd(target.termCd());
                targetCount += dummyDataMapper.insertOneDummySbjTarget(targetVO);
            }
        }

        log.info("Inserted subjects={}, sbjTargets={}", subjectCount, targetCount);
    }

    private static String nextSubjectCd() {
        return "SUBJ7" + String.format(Locale.ROOT, "%010d", SUBJECT_SEQ.getAndIncrement());
    }

    private static List<GradeTerm> rangeTargets(int startGrade, int endGrade, String... termCds) {
        List<GradeTerm> targets = new ArrayList<>();
        for (int grade = startGrade; grade <= endGrade; grade++) {
            String gradeCd = toGradeCd(grade);
            for (String term : termCds) {
                targets.add(new GradeTerm(gradeCd, term));
            }
        }
        return targets;
    }

    private static List<GradeTerm> gradeTermList(GradeTerm... items) {
        return List.of(items);
    }

    private static GradeTerm gt(int grade, String termCd) {
        return new GradeTerm(toGradeCd(grade), termCd);
    }

    private static String toGradeCd(int grade) {
        return switch (grade) {
            case 1 -> "1ST";
            case 2 -> "2ND";
            case 3 -> "3RD";
            case 4 -> "4TH";
            default -> throw new IllegalArgumentException("Unsupported grade: " + grade);
        };
    }

    private List<SubjectBlueprint> buildMajorBasicSubjects() {
        List<SubjectBlueprint> subjects = new ArrayList<>();

        subjects.add(bp("DEPT-ENGR-COM", "MAJ_BASIC", "공학기초수학", 3, 4, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG1, REG2)));
        subjects.add(bp("DEPT-ENGR-COM", "MAJ_BASIC", "기초물리실험", 2, 3, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-ENGR-COM", "MAJ_BASIC", "공학설계입문", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(2, 2, REG1)));

        subjects.add(bp("DEPT-NATS-COM", "MAJ_BASIC", "자연과학사고", 3, 3, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG1, REG2)));
        subjects.add(bp("DEPT-NATS-COM", "MAJ_BASIC", "실험데이터해석", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG2)));
        subjects.add(bp("DEPT-NATS-COM", "MAJ_BASIC", "통합과학워크숍", 2, 3, "SUBJ_ABSOLUTE",
                rangeTargets(2, 2, REG1)));

        subjects.add(bp("DEPT-HUMN-COM", "MAJ_BASIC", "인문학글쓰기", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-HUMN-COM", "MAJ_BASIC", "고전읽기세미나", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG2)));
        subjects.add(bp("DEPT-HUMN-COM", "MAJ_BASIC", "인문통합프로젝트", 2, 3, "SUBJ_ABSOLUTE",
                rangeTargets(2, 2, REG1)));

        subjects.add(bp("DEPT-SOCS-COM", "MAJ_BASIC", "사회조사통계", 3, 3, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-SOCS-COM", "MAJ_BASIC", "정책사례연구", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG2)));
        subjects.add(bp("DEPT-SOCS-COM", "MAJ_BASIC", "사회문제현장실습", 2, 3, "SUBJ_ABSOLUTE",
                rangeTargets(2, 2, REG1)));

        subjects.add(bp("DEPT-EDUC-COM", "MAJ_BASIC", "교육철학기초", 3, 3, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-EDUC-COM", "MAJ_BASIC", "교육심리탐구", 3, 3, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG2)));
        subjects.add(bp("DEPT-EDUC-COM", "MAJ_BASIC", "수업설계입문", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(2, 2, REG1)));

        subjects.add(bp("DEPT-ARTS-COM", "MAJ_BASIC", "예술창의워크숍", 2, 3, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-ARTS-COM", "MAJ_BASIC", "공연예술감상", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG2)));
        subjects.add(bp("DEPT-ARTS-COM", "MAJ_BASIC", "창작스튜디오실습", 2, 3, "SUBJ_ABSOLUTE",
                rangeTargets(2, 2, REG1)));

        return subjects;
    }

    private List<SubjectBlueprint> buildMajorCoreSubjects() {
        List<SubjectBlueprint> subjects = new ArrayList<>();

        subjects.add(bp("DEPT-KORE", "MAJ_CORE", "한국문학사Ⅰ", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1, REG2)));
        subjects.add(bp("DEPT-KORE", "MAJ_CORE", "현대소설분석", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));
        subjects.add(bp("DEPT-KORE", "MAJ_CORE", "국어학세미나", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(4, 4, REG1)));

        subjects.add(bp("DEPT-ENGL", "MAJ_CORE", "영미문학입문", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(1, 1, REG1, REG2)));
        subjects.add(bp("DEPT-ENGL", "MAJ_CORE", "영어학개론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-ENGL", "MAJ_CORE", "영미희곡연구", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));

        subjects.add(bp("DEPT-HIST", "MAJ_CORE", "세계사연구방법", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-HIST", "MAJ_CORE", "한국근현대사특강", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1, REG2)));
        subjects.add(bp("DEPT-HIST", "MAJ_CORE", "사료해석워크숍", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(4, 4, REG1)));

        subjects.add(bp("DEPT-ECON", "MAJ_CORE", "미시경제이론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1, REG2)));
        subjects.add(bp("DEPT-ECON", "MAJ_CORE", "거시경제모형", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));
        subjects.add(bp("DEPT-ECON", "MAJ_CORE", "계량경제실습", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));

        subjects.add(bp("DEPT-POLS", "MAJ_CORE", "비교정치론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-POLS", "MAJ_CORE", "국제정치전략", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));
        subjects.add(bp("DEPT-POLS", "MAJ_CORE", "정치이론세미나", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(4, 4, REG1)));

        subjects.add(bp("DEPT-PSYC", "MAJ_CORE", "발달심리", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1, REG2)));
        subjects.add(bp("DEPT-PSYC", "MAJ_CORE", "인지심리실험", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));
        subjects.add(bp("DEPT-PSYC", "MAJ_CORE", "상담이론과기법", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));

        subjects.add(bp("DEPT-MATH", "MAJ_CORE", "선형대수학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-MATH", "MAJ_CORE", "미분방정식", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG2)));
        subjects.add(bp("DEPT-MATH", "MAJ_CORE", "추상대수론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));

        subjects.add(bp("DEPT-PHYS", "MAJ_CORE", "고전역학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-PHYS", "MAJ_CORE", "전자기학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG2)));
        subjects.add(bp("DEPT-PHYS", "MAJ_CORE", "양자역학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));

        subjects.add(bp("DEPT-CHEM", "MAJ_CORE", "물리화학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-CHEM", "MAJ_CORE", "유기화학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG2)));
        subjects.add(bp("DEPT-CHEM", "MAJ_CORE", "분석화학실험", 3, 4, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));

        subjects.add(bp("DEPT-BIOS", "MAJ_CORE", "세포생물학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-BIOS", "MAJ_CORE", "분자생물학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));
        subjects.add(bp("DEPT-BIOS", "MAJ_CORE", "생명정보학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));

        subjects.add(bp("DEPT-CIVL", "MAJ_CORE", "구조역학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1, REG2)));
        subjects.add(bp("DEPT-CIVL", "MAJ_CORE", "토질역학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));
        subjects.add(bp("DEPT-CIVL", "MAJ_CORE", "교량설계", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(4, 4, REG1)));

        subjects.add(bp("DEPT-MECH", "MAJ_CORE", "열역학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-MECH", "MAJ_CORE", "기계진동", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));
        subjects.add(bp("DEPT-MECH", "MAJ_CORE", "기계설계프로젝트", 3, 4, "SUBJ_RELATIVE",
                rangeTargets(4, 4, REG1)));

        subjects.add(bp("DEPT-ELIT", "MAJ_CORE", "회로이론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-ELIT", "MAJ_CORE", "신호및시스템", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1, REG2)));
        subjects.add(bp("DEPT-ELIT", "MAJ_CORE", "반도체소자", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));

        subjects.add(bp("DEPT-COMP", "MAJ_CORE", "자료구조", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-COMP", "MAJ_CORE", "운영체제", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));
        subjects.add(bp("DEPT-COMP", "MAJ_CORE", "소프트웨어공학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));

        subjects.add(bp("DEPT-EDUC", "MAJ_CORE", "교육과정이론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-EDUC", "MAJ_CORE", "교육평가방법", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1, REG2)));
        subjects.add(bp("DEPT-EDUC", "MAJ_CORE", "교육행정세미나", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(4, 4, REG1)));

        subjects.add(bp("DEPT-CHED", "MAJ_CORE", "초등국어교육론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-CHED", "MAJ_CORE", "창의융합수업설계", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1, REG2)));
        subjects.add(bp("DEPT-CHED", "MAJ_CORE", "교실경영세미나", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(4, 4, REG1)));

        subjects.add(bp("DEPT-SPEC", "MAJ_CORE", "특수교육개론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-SPEC", "MAJ_CORE", "장애유형별교육과정", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1, REG2)));
        subjects.add(bp("DEPT-SPEC", "MAJ_CORE", "개별화교육계획", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));

        subjects.add(bp("DEPT-MUSI", "MAJ_CORE", "서양음악사", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(1, 1, REG1, REG2)));
        subjects.add(bp("DEPT-MUSI", "MAJ_CORE", "합창지휘기법", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG2)));
        subjects.add(bp("DEPT-MUSI", "MAJ_CORE", "실내악세미나", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));

        subjects.add(bp("DEPT-FINE", "MAJ_CORE", "회화기초이론", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-FINE", "MAJ_CORE", "재료와표현기법", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1, REG2)));
        subjects.add(bp("DEPT-FINE", "MAJ_CORE", "현대미술세미나", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));

        subjects.add(bp("DEPT-PEPE", "MAJ_CORE", "체육측정평가", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 2, REG1, REG2)));
        subjects.add(bp("DEPT-PEPE", "MAJ_CORE", "스포츠심리학", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG1)));
        subjects.add(bp("DEPT-PEPE", "MAJ_CORE", "스포츠교육과정", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 3, REG2)));

        return subjects;
    }

    private List<SubjectBlueprint> buildMajorElectiveSubjects() {
        List<SubjectBlueprint> subjects = new ArrayList<>();

        subjects.add(bp("DEPT-KORE", "MAJ_ELEC", "문학콘텐츠제작워크숍", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, REG2, SUB1)));
        subjects.add(bp("DEPT-ENGL", "MAJ_ELEC", "영문학번역프로젝트", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, SUB2)));
        subjects.add(bp("DEPT-HIST", "MAJ_ELEC", "지역사현장연구", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG2, SUB1)));
        subjects.add(bp("DEPT-ECON", "MAJ_ELEC", "데이터경제분석", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, REG2, SUB1)));
        subjects.add(bp("DEPT-POLS", "MAJ_ELEC", "갈등조정시뮬레이션", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, SUB2)));
        subjects.add(bp("DEPT-PSYC", "MAJ_ELEC", "임상사례세미나", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG2)));
        subjects.add(bp("DEPT-MATH", "MAJ_ELEC", "수리모델링프로젝트", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1)));
        subjects.add(bp("DEPT-PHYS", "MAJ_ELEC", "재료물리세미나", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG2)));
        subjects.add(bp("DEPT-CHEM", "MAJ_ELEC", "그린케미스트리실험", 3, 4, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, SUB1)));
        subjects.add(bp("DEPT-BIOS", "MAJ_ELEC", "유전체데이터분석", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, REG2)));
        subjects.add(bp("DEPT-CIVL", "MAJ_ELEC", "스마트인프라설계", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG2, SUB1)));
        subjects.add(bp("DEPT-MECH", "MAJ_ELEC", "로봇시스템설계", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, REG2)));
        subjects.add(bp("DEPT-ELIT", "MAJ_ELEC", "차세대반도체공정", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, SUB2)));
        subjects.add(bp("DEPT-COMP", "MAJ_ELEC", "인공지능캡스톤", 3, 4, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, REG2, SUB1)));
        subjects.add(bp("DEPT-EDUC", "MAJ_ELEC", "교육컨설팅실습", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG2)));
        subjects.add(bp("DEPT-CHED", "MAJ_ELEC", "놀이기반수업연구", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, REG2)));
        subjects.add(bp("DEPT-SPEC", "MAJ_ELEC", "보조공학활용", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, SUB2)));
        subjects.add(bp("DEPT-MUSI", "MAJ_ELEC", "현대음악워크숍", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG2)));
        subjects.add(bp("DEPT-FINE", "MAJ_ELEC", "디지털아트프로젝트", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG1, REG2)));
        subjects.add(bp("DEPT-PEPE", "MAJ_ELEC", "스포츠이벤트기획", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(3, 4, REG2, SUB2)));

        return subjects;
    }

    private List<SubjectBlueprint> buildGeneralBasicSubjects() {
        List<SubjectBlueprint> subjects = new ArrayList<>();

        subjects.add(bp("DEPT-GENR", "GE_BASIC", "기초글쓰기Ⅰ", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG1)));
        subjects.add(bp("DEPT-GENR", "GE_BASIC", "기초글쓰기Ⅱ", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(2, 2, REG1)));
        subjects.add(bp("DEPT-GENR", "GE_BASIC", "대학영어Ⅰ", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(1, 1, REG2)));
        subjects.add(bp("DEPT-GENR", "GE_BASIC", "대학영어Ⅱ", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(2, 2, REG2)));
        subjects.add(bp("DEPT-GENR", "GE_BASIC", "세계시민리더십", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(3, 3, REG1)));
        subjects.add(bp("DEPT-GENR", "GE_BASIC", "글로벌커뮤니케이션", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(3, 3, REG2)));
        subjects.add(bp("DEPT-GENR", "GE_BASIC", "논리와토론", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(4, 4, REG1)));
        subjects.add(bp("DEPT-GENR", "GE_BASIC", "리더십세미나", 2, 2, "SUBJ_ABSOLUTE",
                rangeTargets(4, 4, REG2)));

        return subjects;
    }

    private List<SubjectBlueprint> buildGeneralCoreSubjects() {
        List<SubjectBlueprint> subjects = new ArrayList<>();

        subjects.add(bp("DEPT-SOCS-COM", "GE_CORE", "지속가능발전과사회", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 3, REG1)));
        subjects.add(bp("DEPT-ENGR-COM", "GE_CORE", "인공지능과윤리", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 4, REG2)));
        subjects.add(bp("DEPT-HUMN-COM", "GE_CORE", "동아시아문화탐방", 3, 3, "SUBJ_RELATIVE",
                gradeTermList(gt(2, REG1), gt(3, REG1), gt(3, SUB1))));
        subjects.add(bp("DEPT-NATS-COM", "GE_CORE", "도시와환경", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 4, REG1)));
        subjects.add(bp("DEPT-ARTS-COM", "GE_CORE", "문화예술경영", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 4, REG2)));
        subjects.add(bp("DEPT-ENGR-COM", "GE_CORE", "데이터시각화", 3, 3, "SUBJ_RELATIVE",
                rangeTargets(2, 3, REG1, REG2)));

        return subjects;
    }

    private List<SubjectBlueprint> buildGeneralElectiveSubjects() {
        List<SubjectBlueprint> subjects = new ArrayList<>();

        subjects.add(bp("DEPT-GENR", "GE_ELEC", "힐링요가", 1, 2, "SUBJ_PASSFAIL", List.of()));
        subjects.add(bp("DEPT-GENR", "GE_ELEC", "창의적문제해결실습", 2, 2, "SUBJ_RELATIVE",
                rangeTargets(1, 3, REG2)));
        subjects.add(bp("DEPT-GENR", "GE_ELEC", "영화와사회", 2, 2, "SUBJ_RELATIVE",
                rangeTargets(2, 4, REG1, REG2)));
        subjects.add(bp("DEPT-GENR", "GE_ELEC", "코딩기초워크숍", 2, 2, "SUBJ_PASSFAIL",
                rangeTargets(1, 4, REG1, SUB1)));
        subjects.add(bp("DEPT-GENR", "GE_ELEC", "테니스실기", 1, 2, "SUBJ_PASSFAIL",
                rangeTargets(1, 4, REG2, SUB2)));
        subjects.add(bp("DEPT-GENR", "GE_ELEC", "뮤지컬감상", 2, 2, "SUBJ_RELATIVE",
                rangeTargets(2, 4, REG2)));

        return subjects;
    }

    private List<SubjectBlueprint> buildAdditionalSubjects() {
        List<SubjectBlueprint> subjects = new ArrayList<>();

        // Civil Engineering (18)
        subjects.addAll(series("DEPT-CIVL", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "고급구조해석", "프리스트레스트콘크리트설계", "지반동역학특론", "토목CAD실무", "수리구조설계", "지능형교통체계개론"));
        subjects.addAll(series("DEPT-CIVL", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG1, SUB1 },
                "스마트인프라모니터링", "도시터널설계기술", "교량건전성평가", "지반안정화특론", "친환경도시수자원", "재난안전공학실습"));
        subjects.addAll(series("DEPT-CIVL", "MAJ_CORE", "SUBJ_RELATIVE", 3, 4, 4, 4,
                new String[] { REG2, SUB2 },
                "토목프로젝트관리", "국토개발정책세미나", "BIM토목설계캡스톤", "건설사업관리워크숍", "지속가능교통계획", "스마트시티캡스톤디자인"));

        // Mechanical Engineering (18)
        subjects.addAll(series("DEPT-MECH", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "고급열전달", "유체시스템설계", "동역학응용", "기계요소설계실무", "공압시스템공학", "에너지변환공학"));
        subjects.addAll(series("DEPT-MECH", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG1, SUB1 },
                "스마트제조공정", "미세유체역학", "자율주행시스템", "복합재료분석", "기계구조건전성", "열유체수치해석"));
        subjects.addAll(series("DEPT-MECH", "MAJ_CORE", "SUBJ_RELATIVE", 3, 4, 4, 4,
                new String[] { REG2, SUB2 },
                "기계종합설계프로젝트", "산업로봇캡스톤", "열시뮬레이션워크숍", "에너지시스템설계", "지속가능기계설계", "모빌리티혁신세미나"));

        // Electrical & Electronic Engineering (18)
        subjects.addAll(series("DEPT-ELIT", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "회로설계자동화", "전자기장응용", "디지털통신이론", "반도체측정실습", "전력전자시스템", "통신신호처리"));
        subjects.addAll(series("DEPT-ELIT", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG1, SUB1 },
                "RF회로설계", "임베디드시스템보안", "모바일통신프로토콜", "전력망자동화", "광전자소자", "딥러닝하드웨어가속"));
        subjects.addAll(series("DEPT-ELIT", "MAJ_CORE", "SUBJ_RELATIVE", 3, 4, 4, 4,
                new String[] { REG2, SUB2 },
                "반도체공정캡스톤", "차세대통신프로젝트", "고신뢰회로설계", "스마트그리드응용", "전자시스템창업세미나", "집적회로설계워크숍"));

        // Computer Engineering (24)
        subjects.addAll(series("DEPT-COMP", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "자료구조응용", "컴파일러설계", "시스템프로그램", "웹서비스아키텍처", "데이터시각화프로그래밍", "네트워크보안기초"));
        subjects.addAll(series("DEPT-COMP", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 3,
                new String[] { REG1, SUB1 },
                "병렬컴퓨팅", "클라우드네이티브설계", "데이터마이닝실습", "머신러닝이론", "고급데이터베이스", "오픈소스기여워크숍"));
        subjects.addAll(series("DEPT-COMP", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB2 },
                "딥러닝응용", "자연어처리", "그래픽스엔진설계", "보안코딩실습", "IoT플랫폼프로젝트", "게임엔진프로그래밍"));
        subjects.addAll(series("DEPT-COMP", "MAJ_ELEC", "SUBJ_RELATIVE", 3, 4, 4, 4,
                new String[] { REG1, REG2, SUB1 },
                "블록체인시스템설계", "AI서비스캡스톤", "빅데이터플랫폼구축", "지능형로봇소프트웨어", "컴퓨터비전프로젝트",
                "산학연계소프트웨어설계"));

        // Mathematics (12)
        subjects.addAll(series("DEPT-MATH", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "실해석", "수치해석", "확률과정론", "수리통계특론", "편미분방정식", "위상수학입문"));
        subjects.addAll(series("DEPT-MATH", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG1, SUB1 },
                "수리최적화", "금융수학", "수학적모형화세미나", "미분기하", "정수론특강", "대수적암호학"));

        // Physics (12)
        subjects.addAll(series("DEPT-PHYS", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "열및통계역학", "광학", "전자기학응용", "현대물리실험", "물리수학", "실험데이터분석법"));
        subjects.addAll(series("DEPT-PHYS", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "양자광학", "고체물리", "핵물리개론", "재료물리실험", "플라즈마물리", "고급실험실프로젝트"));

        // Chemistry (12)
        subjects.addAll(series("DEPT-CHEM", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "고급유기화학", "물리화학실험", "분석화학특론", "화학공정안전", "화학결정학", "화학스펙트로스코피"));
        subjects.addAll(series("DEPT-CHEM", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "촉매화학", "재료화학", "그린화학공정", "계면화학", "고분자화학", "의약화학세미나"));

        // Biological Sciences (10)
        subjects.addAll(series("DEPT-BIOS", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "유전공학", "생화학실험", "미생물학심화", "면역학", "발생생물학"));
        subjects.addAll(series("DEPT-BIOS", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "신경생물학", "생명정보학프로젝트", "환경생명과학", "합성생물학", "의생명세포공학"));

        // Economics (10)
        subjects.addAll(series("DEPT-ECON", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "거시경제정책", "국제무역이론", "산업조직경제", "노동경제분석", "경제사례연구"));
        subjects.addAll(series("DEPT-ECON", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "계량경제모형", "행동경제학", "금융시장분석", "경제데이터사이언스", "환경경제학"));

        // Political Science (8)
        subjects.addAll(series("DEPT-POLS", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "정책분석방법론", "동아시아정치", "선거와여론"));
        subjects.addAll(series("DEPT-POLS", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "국제안보전략", "지방정부와정책", "정치커뮤니케이션실습", "비교정당정치", "갈등중재워크숍"));

        // English Language & Literature (8)
        subjects.addAll(series("DEPT-ENGL", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "영미시분석", "사회언어학", "영어담화분석", "번역이론과실제"));
        subjects.addAll(series("DEPT-ENGL", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "영어교육평가", "영미소설세미나", "문화연구방법론", "영문학비평이론"));

        // History (5)
        subjects.addAll(series("DEPT-HIST", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "동아시아근현대사", "세계경제사", "역사자료분석법"));
        subjects.addAll(series("DEPT-HIST", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "도시사특강", "박물관교육실습"));

        // Education (9)
        subjects.addAll(series("DEPT-EDUC", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "교육철학세미나", "교수학습모형연구", "교육행정현안분석", "교육정책평가"));
        subjects.addAll(series("DEPT-EDUC", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "교육컨설팅프로젝트", "교육리더십워크숍", "교육데이터분석", "미래교육디자인", "교육현장실천연구"));

        // Elementary Education (6)
        subjects.addAll(series("DEPT-CHED", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "초등사회과교육논의", "통합교과설계", "창의과학수업모형"));
        subjects.addAll(series("DEPT-CHED", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "디지털리터러시교육", "프로젝트기반수업연구", "초등교육실습세미나"));

        // Special Education (5)
        subjects.addAll(series("DEPT-SPEC", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "특수교육평가", "보조공학기초", "발달장애지원전략"));
        subjects.addAll(series("DEPT-SPEC", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "전환교육프로그램", "특수교육행정세미나"));

        // Music (8)
        subjects.addAll(series("DEPT-MUSI", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "음악분석세미나", "합창편곡법", "음향기술기초", "음악교육워크숍"));
        subjects.addAll(series("DEPT-MUSI", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "오페라연출실습", "실내악프로젝트", "현대작곡워크숍", "음악창업세미나"));

        // Fine Arts (8)
        subjects.addAll(series("DEPT-FINE", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "회화재료실험", "현대미술비평", "미디어아트제작", "입체조형실습"));
        subjects.addAll(series("DEPT-FINE", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "전시기획워크숍", "공공미술프로젝트", "커뮤니티아트실천", "예술창업세미나"));

        // Physical Education (8)
        subjects.addAll(series("DEPT-PEPE", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "스포츠마케팅", "헬스케어프로그래밍", "스포츠과학데이터", "운동손상예방"));
        subjects.addAll(series("DEPT-PEPE", "MAJ_CORE", "SUBJ_RELATIVE", 3, 3, 3, 4,
                new String[] { REG2, SUB1 },
                "스포츠행정실무", "체육교수역량개발", "스포츠이벤트운영", "체육현장실습세미나"));

        // College-level commons
        subjects.addAll(series("DEPT-ENGR-COM", "MAJ_BASIC", "SUBJ_ABSOLUTE", 2, 2, 1, 2,
                new String[] { REG1, REG2 },
                "공학수학워크숍", "창의공학도구활용", "공학데이터리터러시", "융합공학교육", "공학문제해결세미나", "공학윤리토론"));
        subjects.addAll(series("DEPT-ENGR-COM", "GE_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "공학디자인씽킹", "스마트팩토리이해", "산업안전융합", "기후위기와공학", "공학스타트업전략", "캡스톤도입세미나"));

        subjects.addAll(series("DEPT-NATS-COM", "MAJ_BASIC", "SUBJ_ABSOLUTE", 2, 2, 1, 2,
                new String[] { REG1, REG2 },
                "과학글쓰기", "기초통계실습", "과학데이터시각화", "현장과학탐구"));
        subjects.addAll(series("DEPT-NATS-COM", "GE_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "과학기술윤리", "환경데이터분석", "바이오융합개론", "기후변화과학"));

        subjects.addAll(series("DEPT-HUMN-COM", "MAJ_BASIC", "SUBJ_ABSOLUTE", 2, 2, 1, 2,
                new String[] { REG1, REG2 },
                "인문자료해석", "디지털휴머니티"));
        subjects.addAll(series("DEPT-HUMN-COM", "GE_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "세계문화정책", "스토리텔링워크숍"));

        subjects.addAll(series("DEPT-SOCS-COM", "MAJ_BASIC", "SUBJ_ABSOLUTE", 2, 2, 1, 2,
                new String[] { REG1, REG2 },
                "사회데이터읽기", "공공문제탐구"));
        subjects.addAll(series("DEPT-SOCS-COM", "GE_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "도시사회세미나", "글로벌시민프로젝트"));

        subjects.addAll(series("DEPT-ARTS-COM", "MAJ_BASIC", "SUBJ_ABSOLUTE", 2, 2, 1, 2,
                new String[] { REG1, REG2 },
                "예술기초드로잉", "퍼포먼스기초실습"));
        subjects.addAll(series("DEPT-ARTS-COM", "GE_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "융합예술프로젝트", "문화산업트렌드"));

        subjects.addAll(series("DEPT-EDUC-COM", "MAJ_BASIC", "SUBJ_ABSOLUTE", 2, 2, 1, 2,
                new String[] { REG1, REG2 },
                "교육자료리터러시", "학습자이해워크숍"));
        subjects.addAll(series("DEPT-EDUC-COM", "GE_CORE", "SUBJ_RELATIVE", 3, 3, 2, 3,
                new String[] { REG1, REG2 },
                "미래교원역량세미나"));

        // General Education additional (2)
        subjects.addAll(series("DEPT-GENR", "GE_ELEC", "SUBJ_RELATIVE", 2, 2, 2, 4,
                new String[] { REG1, REG2 },
                "창의리빙랩탐구"));
        subjects.add(single("DEPT-GENR", "GE_ELEC", "SUBJ_PASSFAIL", "국제자원봉사워크숍", 1, 2, 1, 4,
                REG1, SUB1));

        return subjects;
    }

    private List<SubjectBlueprint> buildCareerDesignSubjects() {
        List<SubjectBlueprint> subjects = new ArrayList<>();
        Map<String, String> deptNames = new LinkedHashMap<>();
        deptNames.put("DEPT-KORE", "국어국문학과");
        deptNames.put("DEPT-ENGL", "영어영문학과");
        deptNames.put("DEPT-HIST", "역사학과");
        deptNames.put("DEPT-ECON", "경제학과");
        deptNames.put("DEPT-POLS", "정치외교학과");
        deptNames.put("DEPT-PSYC", "심리학과");
        deptNames.put("DEPT-MATH", "수학과");
        deptNames.put("DEPT-PHYS", "물리학과");
        deptNames.put("DEPT-CHEM", "화학과");
        deptNames.put("DEPT-BIOS", "생명과학과");
        deptNames.put("DEPT-CIVL", "토목공학과");
        deptNames.put("DEPT-MECH", "기계공학과");
        deptNames.put("DEPT-ELIT", "전기전자공학부");
        deptNames.put("DEPT-COMP", "컴퓨터공학부");
        deptNames.put("DEPT-EDUC", "교육학과");
        deptNames.put("DEPT-CHED", "초등교육과");
        deptNames.put("DEPT-SPEC", "특수교육과");
        deptNames.put("DEPT-MUSI", "음악학과");
        deptNames.put("DEPT-FINE", "미술학과");
        deptNames.put("DEPT-PEPE", "체육교육과");

        for (Map.Entry<String, String> entry : deptNames.entrySet()) {
            String subjectName = entry.getValue() + " 진로설계세미나";
            subjects.add(single(entry.getKey(), "GE_BASIC", "SUBJ_PASSFAIL", subjectName, 1, 2, 1, 2,
                    REG1, REG2));
        }

        return subjects;
    }

    private static List<SubjectBlueprint> series(String dept, String completionCd, String subjectTypeCd, int credit,
            int hour, int startGrade, int endGrade, String[] termCds, String... subjectNames) {
        List<SubjectBlueprint> list = new ArrayList<>();
        for (String name : subjectNames) {
            list.add(single(dept, completionCd, subjectTypeCd, name, credit, hour, startGrade, endGrade, termCds));
        }
        return list;
    }

    private static SubjectBlueprint single(String dept, String completionCd, String subjectTypeCd, String subjectName,
            int credit, int hour, int startGrade, int endGrade, String... termCds) {
        List<GradeTerm> targets = (termCds == null || termCds.length == 0)
                ? List.of()
                : new ArrayList<>(rangeTargets(startGrade, endGrade, termCds));
        return new SubjectBlueprint(dept, completionCd, subjectName, credit, hour, subjectTypeCd, targets);
    }

    private static SubjectBlueprint bp(String dept, String completionCd, String name, int credit, int hour,
            String subjectTypeCd, List<GradeTerm> targets) {
        return new SubjectBlueprint(dept, completionCd, name, credit, hour, subjectTypeCd, targets);
    }

    @RequiredArgsConstructor
    private static class GradeTerm {
        private final String gradeCd;
        private final String termCd;

        public String gradeCd() {
            return gradeCd;
        }

        public String termCd() {
            return termCd;
        }
    }

    @RequiredArgsConstructor
    private static class SubjectBlueprint {
        private final String univDeptCd;
        private final String completionCd;
        private final String subjectName;
        private final int credit;
        private final int hour;
        private final String subjectTypeCd;
        private final List<GradeTerm> targets;

        public String univDeptCd() {
            return univDeptCd;
        }

        public String completionCd() {
            return completionCd;
        }

        public String subjectName() {
            return subjectName;
        }

        public int credit() {
            return credit;
        }

        public int hour() {
            return hour;
        }

        public String subjectTypeCd() {
            return subjectTypeCd;
        }

        public List<GradeTerm> targets() {
            return targets;
        }
    }
}
