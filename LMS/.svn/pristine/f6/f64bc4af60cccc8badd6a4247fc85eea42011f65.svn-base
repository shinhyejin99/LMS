package kr.or.jsu.aiDummyDataGenerator.lecture;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.core.dto.info.CollegeInfo;
import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.dto.db.subject.SubjectWithCollegeAndDeptDTO;
import kr.or.jsu.mybatis.mapper.ProfessorMapper;
import kr.or.jsu.mybatis.mapper.SbjTargetMapper;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import kr.or.jsu.mybatis.mapper.SubjectMapper;
import kr.or.jsu.mybatis.mapper.UnivDeptMapper;
import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.ApprovalVO;
import kr.or.jsu.vo.LctApplyGraderatioVO;
import kr.or.jsu.vo.LctApplyWeekbyVO;
import kr.or.jsu.vo.LctOpenApplyVO;
import kr.or.jsu.vo.ProfessorVO;
import kr.or.jsu.vo.SbjTargetVO;
import kr.or.jsu.vo.StudentVO;
import kr.or.jsu.vo.SubjectVO;
import kr.or.jsu.vo.UnivDeptVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class LectureOpenApplyDummyGeneratorTest {

    private static final Set<String> REGULAR_TERMS = Set.of("REG1", "REG2");
    private static final List<String> ALL_GRADES = List.of("1ST", "2ND", "3RD", "4TH");
    private static final Set<String> ACTIVE_STUDENT_STATUS = Set.of("ENROLLED", "DEFERRED");
    private static final double TARGET_CREDITS_PER_STUDENT = 15.0;
    private static final double SUPPLY_BUFFER_RATIO = 1.10;
    private static final String APPLY_TYPE_LCT_OPEN = "LCT_OPEN";

    private static final Random RANDOM = new Random(2025_10_31_1200L);
    private static final AtomicLong APPROVAL_SEQ = new AtomicLong(1);
    private static final AtomicLong APPLY_SEQ = new AtomicLong(1);
    private static final int MIN_ASSIGNMENTS_PER_PROFESSOR = 2;
    private static final int MAX_ASSIGNMENTS_PER_PROFESSOR = 4;
    private static final List<YearTerm> YEAR_TERMS = List.of(
            new YearTerm("2024", "REG1"),
            new YearTerm("2024", "REG2"),
            new YearTerm("2025", "REG1"),
            new YearTerm("2025", "REG2")
    );

    private static final String[] DAY_COMBOS = {
            "월·수 2-3교시",
            "화·목 3-4교시",
            "월·수 5-6교시",
            "화·목 1-2교시",
            "금요일 집중",
            "월요일 저녁"
    };

    private static final String[] ROOM_HINTS = {
            "ENGR-3층 세미나실",
            "NATS-2층 대형강의실",
            "HUMN-1층 토론실",
            "SOCS-4층 프로젝트룸",
            "EDUC-5층 실습실",
            "ARTS-소공연장",
            "GENR-대강당"
    };

    private static final String[] DESIRE_NOTES = {
            "실험 장비 사용이 가능한 강의실 요청",
            "녹화 장비가 있는 강의실 선호",
            "토론형 좌석 배치 필요",
            "프로젝트 전용 실습공간 요청",
            "피아노 반주 가능한 강의실 희망",
            "체육관 바닥 점검 요청"
    };

    private static final String[] WEEK_THEMES = {
            "핵심 이론 정리",
            "사례 연구",
            "주제별 발표",
            "실습 및 피드백",
            "중간 점검",
            "심화 토론",
            "프로젝트 설계",
            "성과 공유"
    };

    @Autowired
    private DummyDataMapper dummyDataMapper;
    @Autowired
    private ProfessorMapper professorMapper;
    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private SbjTargetMapper sbjTargetMapper;
    @Autowired
    private UnivDeptMapper univDeptMapper;
    @Autowired
    private StudentMapper studentMapper;

    
    
    @Test
    void insertLectureOpenApplicationsFor2025() {
        var professors = professorMapper.selectProfessorList()
                .stream()
                .filter(this::isActiveProfessor)
                .filter(this::isHiredBefore2024)
                .collect(Collectors.toList());
        if (professors.isEmpty()) {
            log.warn("교수 데이터가 없어 강의개설 신청 더미 생성을 건너뜁니다.");
            return;
        }

        Map<String, String> deptToCollege = loadDeptToCollegeMap();
        StudentStats studentStats = buildStudentStats(deptToCollege);

        var subjects = subjectMapper.selectAllSubjectWithCollegeAndDept();
        if (subjects.isEmpty()) {
            log.warn("과목 데이터가 없어 강의개설 신청 더미 생성을 건너뜁니다.");
            return;
        }

        Map<String, List<SbjTargetVO>> targetsBySubject = sbjTargetMapper.selectSbjTargetList()
                .stream()
                .collect(Collectors.groupingBy(SbjTargetVO::getSubjectCd));

        Map<String, ProfessorVO> deptHeadByDept = findDeptHeads(professors);

        List<SubjectOffering> offerings = buildSubjectOfferings(subjects, targetsBySubject);
        if (offerings.isEmpty()) {
            log.warn("���� ���ǰ��� ��û�� ������ ���� Ȯ���ϼ���.");
            return;
        }

        List<LectureApplication> applications = assignOfferingsToProfessors(
                professors, offerings, deptToCollege, studentStats, deptHeadByDept);

        if (applications.isEmpty()) {
            log.warn("생성된 강의개설 신청 건이 없습니다. 조건을 확인하세요.");
            return;
        }

        adjustCapacityForSupply(applications, studentStats.totalStudents());

        int approvalRows = 0;
        int applyRows = 0;
        int weekRows = 0;
        int ratioRows = 0;

        for (LectureApplication application : applications) {
            approvalRows += dummyDataMapper.insertDummyApproval(application.approval());
            applyRows += dummyDataMapper.insertDummyLctOpenApply(application.apply());
            if (!application.weeks().isEmpty()) {
                weekRows += dummyDataMapper.insertDummyLctApplyWeekby(application.weeks());
            }
            if (!application.ratios().isEmpty()) {
                ratioRows += dummyDataMapper.insertDummyLctApplyGraderatio(application.ratios());
            }
        }

        Map<String, Integer> supplyByTerm = calculateSupply(applications);
        supplyByTerm.forEach((term, credits) ->
                log.info("년도학기 {} 공급 학점 총합: {}", term, credits));

        log.info("삽입 결과 요약 - Approval: {}, LCT_OPEN_APPLY: {}, WEEKBY rows: {}, GRADERATIO rows: {}",
                approvalRows, applyRows, weekRows, ratioRows);
    }

    private boolean isActiveProfessor(ProfessorVO professor) {
        return professor != null && "PRF_STATUS_ACTV".equals(professor.getPrfStatusCd());
    }

    private boolean isHiredBefore2024(ProfessorVO professor) {
        if (professor == null) {
            return false;
        }
        String professorNo = professor.getProfessorNo();
        if (professorNo == null || professorNo.length() < 4) {
            return false;
        }
        try {
            int hireYear = Integer.parseInt(professorNo.substring(0, 4));
            return hireYear < 2024;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private Map<String, String> loadDeptToCollegeMap() {
        return univDeptMapper.selectAllUnivDepts()
                .stream()
                .collect(Collectors.toMap(UnivDeptVO::getUnivDeptCd, UnivDeptVO::getCollegeCd));
    }

    private StudentStats buildStudentStats(Map<String, String> deptToCollege) {
        List<StudentVO> students = studentMapper.selectStudentList();
        Map<String, Map<String, Integer>> deptGradeCounts = new HashMap<>();
        Map<String, Map<String, Integer>> collegeGradeCounts = new HashMap<>();
        Map<String, Integer> gradeCounts = new HashMap<>();
        Map<String, Integer> deptCounts = new HashMap<>();
        Map<String, Integer> collegeCounts = new HashMap<>();

        int total = 0;
        for (StudentVO student : students) {
            if (student == null || student.getStuStatusCd() == null) {
                continue;
            }
            if (!ACTIVE_STUDENT_STATUS.contains(student.getStuStatusCd())) {
                continue;
            }
            String deptCd = student.getUnivDeptCd();
            String gradeCd = Optional.ofNullable(student.getGradeCd()).orElse("1ST");
            String collegeCd = deptToCollege.getOrDefault(deptCd, "UNASSIGNED");

            deptGradeCounts
                    .computeIfAbsent(deptCd, ignore -> new HashMap<>())
                    .merge(gradeCd, 1, Integer::sum);
            collegeGradeCounts
                    .computeIfAbsent(collegeCd, ignore -> new HashMap<>())
                    .merge(gradeCd, 1, Integer::sum);
            gradeCounts.merge(gradeCd, 1, Integer::sum);
            deptCounts.merge(deptCd, 1, Integer::sum);
            collegeCounts.merge(collegeCd, 1, Integer::sum);
            total++;
        }
        return new StudentStats(total, gradeCounts, deptCounts, collegeCounts, deptGradeCounts, collegeGradeCounts);
    }

    private List<SubjectOffering> buildSubjectOfferings(List<SubjectWithCollegeAndDeptDTO> subjects,
            Map<String, List<SbjTargetVO>> targetsBySubject) {
        List<SubjectOffering> offerings = new ArrayList<>();
        for (SubjectWithCollegeAndDeptDTO subjectDto : subjects) {
            SubjectInfo subject = subjectDto.getSubject();
            Map<String, List<String>> termGrades = resolveTermGrades(subject.getSubjectCd(), targetsBySubject);
            for (YearTerm yearTerm : YEAR_TERMS) {
                List<String> grades = termGrades.get(yearTerm.termSuffix());
                if (grades == null || grades.isEmpty()) {
                    continue;
                }
                offerings.add(new SubjectOffering(subjectDto, yearTerm, List.copyOf(grades)));
            }
        }
        return offerings;
    }

    private List<LectureApplication> assignOfferingsToProfessors(List<ProfessorVO> professors,
            List<SubjectOffering> offerings, Map<String, String> deptToCollege,
            StudentStats studentStats, Map<String, ProfessorVO> deptHeadByDept) {
        if (offerings.isEmpty()) {
            return Collections.emptyList();
        }
        List<ProfessorVO> shuffled = new ArrayList<>(professors);
        Collections.shuffle(shuffled, RANDOM);
        int maxProfessors = Math.min(shuffled.size(), offerings.size() / MIN_ASSIGNMENTS_PER_PROFESSOR);
        if (maxProfessors == 0) {
            return Collections.emptyList();
        }
        List<ProfessorVO> selected = new ArrayList<>(shuffled.subList(0, maxProfessors));
        LinkedHashMap<ProfessorVO, Integer> targets = new LinkedHashMap<>();
        selected.forEach(prof -> targets.put(prof, MIN_ASSIGNMENTS_PER_PROFESSOR));

        int capacity = Math.min(offerings.size(), maxProfessors * MAX_ASSIGNMENTS_PER_PROFESSOR);
        int assignedBase = maxProfessors * MIN_ASSIGNMENTS_PER_PROFESSOR;
        int remaining = capacity - assignedBase;
        while (remaining > 0) {
            boolean progressed = false;
            for (Map.Entry<ProfessorVO, Integer> entry : targets.entrySet()) {
                if (remaining == 0) {
                    break;
                }
                if (entry.getValue() < MAX_ASSIGNMENTS_PER_PROFESSOR) {
                    entry.setValue(entry.getValue() + 1);
                    remaining--;
                    progressed = true;
                }
            }
            if (!progressed) {
                break;
            }
        }

        int totalRequired = targets.values().stream().mapToInt(Integer::intValue).sum();
        List<SubjectOffering> pool = new ArrayList<>(offerings);
        Collections.shuffle(pool, RANDOM);
        if (pool.size() > totalRequired) {
            pool = new ArrayList<>(pool.subList(0, totalRequired));
        }

        Map<String, List<SubjectOffering>> offeringsByDept = new HashMap<>();
        Map<String, List<SubjectOffering>> offeringsByCollege = new HashMap<>();
        for (SubjectOffering offering : pool) {
            String deptCd = offering.subjectDto().getSubject().getUnivDeptCd();
            if (deptCd != null) {
                offeringsByDept.computeIfAbsent(deptCd, ignore -> new ArrayList<>()).add(offering);
            }
            String collegeCd = Optional.ofNullable(offering.subjectDto().getCollege())
                    .map(CollegeInfo::getCollegeCd)
                    .orElse(null);
            if (collegeCd != null) {
                offeringsByCollege.computeIfAbsent(collegeCd, ignore -> new ArrayList<>()).add(offering);
            }
        }

        List<LectureApplication> applications = new ArrayList<>();
        for (Map.Entry<ProfessorVO, Integer> entry : targets.entrySet()) {
            ProfessorVO professor = entry.getKey();
            int quota = entry.getValue();
            for (int i = 0; i < quota; i++) {
                SubjectOffering offering = claimOfferingForProfessor(professor, offeringsByDept, offeringsByCollege,
                        pool, deptToCollege);
                if (offering == null) {
                    log.warn("교수 {} 에게 배정할 강의 신청 후보가 부족하여 할당을 중단합니다.", professor.getProfessorNo());
                    break;
                }
                applications.add(createApplication(offering, professor, deptHeadByDept, studentStats, deptToCollege));
            }
        }
        return applications;
    }

    private SubjectOffering claimOfferingForProfessor(ProfessorVO professor,
            Map<String, List<SubjectOffering>> offeringsByDept,
            Map<String, List<SubjectOffering>> offeringsByCollege,
            List<SubjectOffering> remaining, Map<String, String> deptToCollege) {
        SubjectOffering offering = pollFirst(offeringsByDept.get(professor.getUnivDeptCd()));
        if (offering == null) {
            String collegeCd = deptToCollege.get(professor.getUnivDeptCd());
            if (collegeCd != null) {
                offering = pollFirst(offeringsByCollege.get(collegeCd));
            }
        }
        if (offering == null && !remaining.isEmpty()) {
            offering = remaining.get(0);
        }
        if (offering != null) {
            removeOffering(offering, offeringsByDept, offeringsByCollege, remaining);
        }
        return offering;
    }

    private SubjectOffering pollFirst(List<SubjectOffering> offerings) {
        if (offerings == null || offerings.isEmpty()) {
            return null;
        }
        return offerings.remove(0);
    }

    private void removeOffering(SubjectOffering offering,
            Map<String, List<SubjectOffering>> offeringsByDept,
            Map<String, List<SubjectOffering>> offeringsByCollege,
            List<SubjectOffering> remaining) {
        remaining.remove(offering);
        String deptCd = offering.subjectDto().getSubject().getUnivDeptCd();
        if (deptCd != null) {
            List<SubjectOffering> list = offeringsByDept.get(deptCd);
            if (list != null) {
                list.remove(offering);
            }
        }
        String collegeCd = Optional.ofNullable(offering.subjectDto().getCollege())
                .map(CollegeInfo::getCollegeCd)
                .orElse(null);
        if (collegeCd != null) {
            List<SubjectOffering> list = offeringsByCollege.get(collegeCd);
            if (list != null) {
                list.remove(offering);
            }
        }
    }

    private LectureApplication createApplication(SubjectOffering offering, ProfessorVO professor,
            Map<String, ProfessorVO> deptHeadByDept, StudentStats studentStats,
            Map<String, String> deptToCollege) {
        SubjectWithCollegeAndDeptDTO subjectDto = offering.subjectDto();
        SubjectInfo subject = subjectDto.getSubject();
        List<String> grades = offering.grades();
        int expectCap = estimateCapacity(subjectDto, grades, offering.yearTerm().termSuffix(), studentStats, deptToCollege);
        String lctApplyId = nextId("LCTAPLY", APPLY_SEQ, 7);
        String approveId = nextId("APPR", APPROVAL_SEQ, 10);
        String yeartermCd = offering.yearTerm().year() + "_" + offering.yearTerm().termSuffix();
        LocalDateTime applyAt = randomApplyTime(yeartermCd);
        String lectureIndex = buildLectureIndex(subject.getSubjectName(), offering.yearTerm().termSuffix());
        String lectureGoal = buildLectureGoal(subject.getSubjectName(), grades);
        String prereqSubject = buildPrerequisite(subjectDto, grades);
        String desireOption = buildDesireOption();

        ApprovalVO approval = buildApproval(approveId, professor, deptHeadByDept, applyAt);
        LctOpenApplyVO lctApply = buildOpenApply(lctApplyId, approveId, subjectDto, professor, yeartermCd,
                lectureIndex, lectureGoal, prereqSubject, desireOption, expectCap, applyAt);
        List<LctApplyWeekbyVO> weeks = buildWeeks(lctApplyId, subject.getSubjectName());

        SubjectVO subjectVO = new SubjectVO();
        BeanUtils.copyProperties(subject, subjectVO);
        List<LctApplyGraderatioVO> ratios = buildGradeRatios(lctApplyId, subjectVO, grades);

        return new LectureApplication(lctApply, approval, weeks, ratios, subjectVO, offering.yearTerm().termSuffix());
    }

    private Map<String, ProfessorVO> findDeptHeads(List<ProfessorVO> professors) {
        return professors.stream()
                .filter(p -> "PRF_POSIT_HEAD".equals(p.getPrfPositCd()))
                .collect(Collectors.toMap(ProfessorVO::getUnivDeptCd, p -> p, (a, b) -> a));
    }

    private Map<String, List<String>> resolveTermGrades(String subjectCd,
            Map<String, List<SbjTargetVO>> targetsBySubject) {
        Map<String, List<String>> termGrades = new HashMap<>();
        List<SbjTargetVO> targets = targetsBySubject.getOrDefault(subjectCd, Collections.emptyList());
        if (targets.isEmpty()) {
            for (String term : REGULAR_TERMS) {
                termGrades.put(term, new ArrayList<>(ALL_GRADES));
            }
            return termGrades;
        }
        for (SbjTargetVO target : targets) {
            String term = target.getTermCd();
            if (!REGULAR_TERMS.contains(term)) {
                continue;
            }
            termGrades.computeIfAbsent(term, ignore -> new ArrayList<>()).add(target.getGradeCd());
        }
        for (String term : REGULAR_TERMS) {
            termGrades.computeIfAbsent(term, ignore -> new ArrayList<>());
        }
        for (Map.Entry<String, List<String>> entry : termGrades.entrySet()) {
            List<String> grades = entry.getValue();
            if (grades.isEmpty()) {
                grades.addAll(ALL_GRADES);
            } else {
                grades.sort(Comparator.comparingInt(LectureOpenApplyDummyGeneratorTest::gradeOrder));
            }
        }
        return termGrades;
    }

    private int estimateCapacity(SubjectWithCollegeAndDeptDTO subjectDto, List<String> grades, String termSuffix,
            StudentStats studentStats, Map<String, String> deptToCollege) {
        SubjectInfo subject = subjectDto.getSubject();
        String completion = subject.getCompletionCd();
        String deptCd = subject.getUnivDeptCd();
        String collegeCd = Optional.ofNullable(subjectDto.getCollege())
                .map(CollegeInfo::getCollegeCd)
                .orElseGet(() -> deptToCollege.getOrDefault(deptCd, "UNASSIGNED"));

        int eligibleStudents = switch (completion) {
            case "MAJ_BASIC" -> estimateCollegeStudents(collegeCd, grades, studentStats.collegeGradeCounts());
            case "GE_BASIC" -> estimateDeptStudents(deptCd, grades, studentStats.deptGradeCounts());
            case "MAJ_CORE", "MAJ_ELEC" -> estimateDeptStudents(deptCd, grades, studentStats.deptGradeCounts());
            case "GE_CORE" -> estimateGeneralCoreStudents(subjectDto, grades, studentStats, deptToCollege);
            case "GE_ELEC" -> estimateGeneralElectiveStudents(grades, studentStats.gradeCounts());
            default -> studentStats.totalStudents();
        };

        double multiplier = switch (completion) {
            case "MAJ_BASIC" -> 0.85;
            case "MAJ_CORE" -> 0.55;
            case "MAJ_ELEC" -> 0.40;
            case "GE_BASIC" -> 1.00;
            case "GE_CORE" -> 0.50;
            case "GE_ELEC" -> 0.55;
            default -> 0.60;
        };

        int base = (int) Math.ceil(eligibleStudents * multiplier);
        base = Math.max(base, baseCapacityFloor(completion));
        base = Math.min(base, 420);

        // 계절학기 대상은 정규학기 수요를 보전하기 위해 다소 줄인다.
        if (!"REG1".equals(termSuffix) && !"REG2".equals(termSuffix)) {
            base = (int) Math.round(base * 0.7);
        }
        return Math.max(base, 25);
    }

    private int baseCapacityFloor(String completion) {
        return switch (completion) {
            case "GE_BASIC" -> 120;
            case "MAJ_BASIC" -> 90;
            case "GE_ELEC" -> 70;
            case "GE_CORE" -> 60;
            case "MAJ_CORE" -> 60;
            case "MAJ_ELEC" -> 45;
            default -> 50;
        };
    }

    private int estimateDeptStudents(String deptCd, List<String> grades,
            Map<String, Map<String, Integer>> deptGradeCounts) {
        Map<String, Integer> gradeCounts = deptGradeCounts.getOrDefault(deptCd, Collections.emptyMap());
        return grades.stream()
                .mapToInt(grade -> gradeCounts.getOrDefault(grade, 0))
                .sum();
    }

    private int estimateCollegeStudents(String collegeCd, List<String> grades,
            Map<String, Map<String, Integer>> collegeGradeCounts) {
        Map<String, Integer> gradeCounts = collegeGradeCounts.getOrDefault(collegeCd, Collections.emptyMap());
        return grades.stream()
                .mapToInt(grade -> gradeCounts.getOrDefault(grade, 0))
                .sum();
    }

    private int estimateGeneralCoreStudents(SubjectWithCollegeAndDeptDTO subjectDto, List<String> grades,
            StudentStats stats, Map<String, String> deptToCollege) {
        String deptCd = subjectDto.getSubject().getUnivDeptCd();
        if (deptCd != null && deptCd.endsWith("-COM")) {
            String collegeCd = Optional.ofNullable(subjectDto.getCollege())
                    .map(CollegeInfo::getCollegeCd)
                    .orElseGet(() -> deptToCollege.getOrDefault(deptCd, "UNASSIGNED"));
            return estimateCollegeStudents(collegeCd, grades, stats.collegeGradeCounts());
        }
        return estimateGeneralElectiveStudents(grades, stats.gradeCounts());
    }

    private int estimateGeneralElectiveStudents(List<String> grades, Map<String, Integer> gradeCounts) {
        return grades.stream()
                .mapToInt(grade -> gradeCounts.getOrDefault(grade, 0))
                .sum();
    }

    private ApprovalVO buildApproval(String approveId, ProfessorVO applicant,
            Map<String, ProfessorVO> deptHeadByDept, LocalDateTime applyAt) {
        ApprovalVO approval = new ApprovalVO();
        approval.setApproveId(approveId);
        approval.setPrevApproveId(null);
        approval.setApplyTypeCd(APPLY_TYPE_LCT_OPEN);
        approval.setApplicantUserId(applicant.getUserId());

        ProfessorVO head = deptHeadByDept.get(applicant.getUnivDeptCd());
        String approverUserId = head != null ? head.getUserId() : applicant.getUserId();
        if (approverUserId == null) {
            approverUserId = applicant.getUserId();
        }

        approval.setUserId(approverUserId);
        approval.setApproveYnnull("Y");
        approval.setApproveAt(applyAt.plusDays(3 + RANDOM.nextInt(5)));
        approval.setComments("학과장 승인 완료");
        approval.setAttachFileId(null);
        return approval;
    }

    private LctOpenApplyVO buildOpenApply(String lctApplyId, String approveId, SubjectWithCollegeAndDeptDTO subjectDto,
            ProfessorVO professor, String yeartermCd, String lectureIndex, String lectureGoal,
            String prereqSubject, String desireOption, int expectCap, LocalDateTime applyAt) {
        SubjectInfo subject = subjectDto.getSubject();
        LctOpenApplyVO apply = new LctOpenApplyVO();
        apply.setLctApplyId(lctApplyId);
        apply.setSubjectCd(subject.getSubjectCd());
        apply.setProfessorNo(professor.getProfessorNo());
        apply.setYeartermCd(yeartermCd);
        apply.setLectureIndex(lectureIndex);
        apply.setLectureGoal(lectureGoal);
        apply.setPrereqSubject(prereqSubject);
        apply.setExpectCap(expectCap);
        apply.setDesireOption(desireOption);
        apply.setCancelYn("N");
        apply.setApproveId(approveId);
        apply.setApplyAt(applyAt);
        return apply;
    }

    private List<LctApplyWeekbyVO> buildWeeks(String lctApplyId, String subjectName) {
        List<LctApplyWeekbyVO> weeks = new ArrayList<>(15);
        for (int week = 1; week <= 15; week++) {
            String theme = WEEK_THEMES[(week - 1) % WEEK_THEMES.length];
            String goal = String.format(Locale.ROOT, "%d주차 학습목표: %s 중심의 %s 학습",
                    week, subjectName, theme);
            String desc = String.format(Locale.ROOT, "%s 관련 사례 분석과 토의를 통해 실전 감각을 익힌다.", theme);
            LctApplyWeekbyVO vo = new LctApplyWeekbyVO();
            vo.setLectureWeek(week);
            vo.setLctApplyId(lctApplyId);
            vo.setWeekGoal(goal);
            vo.setWeekDesc(desc);
            weeks.add(vo);
        }
        return weeks;
    }

    private List<LctApplyGraderatioVO> buildGradeRatios(String lctApplyId, SubjectVO subject, List<String> grades) {
        List<LctApplyGraderatioVO> ratios = new ArrayList<>();
        String completion = subject.getCompletionCd();
        String subjectType = subject.getSubjectTypeCd();

        if ("SUBJ_PASSFAIL".equals(subjectType)) {
            ratios.add(new LctApplyGraderatioVO("PRAC", lctApplyId, 70));
            ratios.add(new LctApplyGraderatioVO("ATTD", lctApplyId, 30));
            return ratios;
        }

        int exam = 0;
        int task = 0;
        int attd = 0;
        int etc = 0;

        switch (completion) {
            case "MAJ_BASIC" -> {
                exam = 40;
                task = 30;
                attd = 20;
                etc = 10;
            }
            case "MAJ_CORE" -> {
                exam = 35;
                task = 35;
                attd = 20;
                etc = 10;
            }
            case "MAJ_ELEC" -> {
                exam = 30;
                task = 40;
                attd = 20;
                etc = 10;
            }
            case "GE_BASIC" -> {
                exam = 30;
                task = 30;
                attd = 30;
                etc = 10;
            }
            case "GE_CORE" -> {
                exam = 30;
                task = 40;
                attd = 20;
                etc = 10;
            }
            case "GE_ELEC" -> {
                exam = 25;
                task = 45;
                attd = 20;
                etc = 10;
            }
            default -> {
                exam = 30;
                task = 40;
                attd = 20;
                etc = 10;
            }
        }

        ratios.add(new LctApplyGraderatioVO("EXAM", lctApplyId, exam));
        ratios.add(new LctApplyGraderatioVO("TASK", lctApplyId, task));
        ratios.add(new LctApplyGraderatioVO("ATTD", lctApplyId, attd));
        ratios.add(new LctApplyGraderatioVO("MISC", lctApplyId, etc));
        return ratios;
    }

    private String buildLectureIndex(String subjectName, String termSuffix) {
        String termText = switch (termSuffix) {
            case "REG1" -> "1학기";
            case "REG2" -> "2학기";
            default -> termSuffix;
        };
        return String.format(Locale.ROOT,
                "%s %s 운영 계획: 최신 사례와 실습을 결합하여 핵심 역량을 강화한다.",
                termText, subjectName);
    }

    private String buildLectureGoal(String subjectName, List<String> grades) {
        String gradeText = grades.stream()
                .map(LectureOpenApplyDummyGeneratorTest::gradeDisplayName)
                .collect(Collectors.joining(", "));
        return String.format(Locale.ROOT,
                "수강 대상(%s)이 %s의 이론과 실습을 균형 있게 습득하도록 한다.",
                gradeText, subjectName);
    }

    private String buildPrerequisite(SubjectWithCollegeAndDeptDTO subjectDto, List<String> grades) {
        SubjectInfo subject = subjectDto.getSubject();
        if (grades.contains("1ST")) {
            return null;
        }
        String deptName = Optional.ofNullable(subjectDto.getUnivDept())
                .map(UnivDeptInfo::getUnivDeptName)
                .orElse("해당 학과");
        return String.format(Locale.ROOT, "권장 선수 과목: %s 기초 교과 이수자 우대", deptName);
    }

    private String buildDesireOption() {
        String room = ROOM_HINTS[RANDOM.nextInt(ROOM_HINTS.length)];
        String day = DAY_COMBOS[RANDOM.nextInt(DAY_COMBOS.length)];
        String note = DESIRE_NOTES[RANDOM.nextInt(DESIRE_NOTES.length)];
        return String.format(Locale.ROOT, "희망 강의실: %s / 희망 시간: %s / 비고: %s", room, day, note);
    }

    private LocalDateTime randomApplyTime(String yeartermCd) {
        boolean isSpring = yeartermCd.endsWith("REG1");
        int year = Integer.parseInt(yeartermCd.substring(0, 4));
        LocalDateTime base = isSpring
                ? LocalDateTime.of(year - 1, Month.NOVEMBER, 10, 9, 0)
                : LocalDateTime.of(year, Month.MAY, 5, 9, 0);
        int dayOffset = RANDOM.nextInt(40);
        int hour = 9 + RANDOM.nextInt(8);
        int minute = RANDOM.nextBoolean() ? 0 : 30;
        return base.plusDays(dayOffset).withHour(hour).withMinute(minute);
    }

    private static int gradeOrder(String gradeCd) {
        return switch (gradeCd) {
            case "1ST" -> 1;
            case "2ND" -> 2;
            case "3RD" -> 3;
            case "4TH" -> 4;
            default -> 5;
        };
    }

    private static String gradeDisplayName(String gradeCd) {
        return switch (gradeCd) {
            case "1ST" -> "1학년";
            case "2ND" -> "2학년";
            case "3RD" -> "3학년";
            case "4TH" -> "4학년";
            default -> gradeCd;
        };
    }

    private void adjustCapacityForSupply(List<LectureApplication> applications, int totalStudents) {
        if (totalStudents <= 0) {
            log.warn("재학생 수를 확인할 수 없어 정원 보정 없이 데이터를 삽입합니다.");
            return;
        }
        Map<String, Integer> currentSupply = calculateSupply(applications);
        int requiredCredits = (int) Math.round(totalStudents * TARGET_CREDITS_PER_STUDENT * SUPPLY_BUFFER_RATIO);

        for (var entry : currentSupply.entrySet()) {
            String yearterm = entry.getKey();
            int supplied = entry.getValue();
            if (supplied >= requiredCredits) {
                continue;
            }
            double scale = Math.ceil((double) requiredCredits / Math.max(1, supplied));
            for (LectureApplication application : applications) {
                if (!application.yearterm().equals(yearterm)) {
                    continue;
                }
                int scaled = (int) Math.round(application.apply().getExpectCap() * scale);
                application.apply().setExpectCap(Math.min(scaled, 480));
            }
            log.info("년도학기 {} 공급 학점이 부족하여 정원을 {}배로 보정했습니다. (요구:{} / 공급:{})",
                    yearterm, (int) scale, requiredCredits, supplied);
        }
    }

    private Map<String, Integer> calculateSupply(List<LectureApplication> applications) {
        Map<String, Integer> supply = new LinkedHashMap<>();
        for (LectureApplication application : applications) {
            SubjectVO subject = application.subject();
            int credits = Optional.ofNullable(subject.getCredit()).orElse(3);
            int cap = application.apply().getExpectCap();
            supply.merge(application.yearterm(), credits * cap, Integer::sum);
        }
        return supply;
    }

    private static String nextId(String prefix, AtomicLong seq, int numericLength) {
        long value = seq.getAndIncrement();
        String number = String.format(Locale.ROOT, "%0" + numericLength + "d", value);
        return prefix + "7" + number;
    }

    private record YearTerm(String year, String termSuffix) {
    }

    private record SubjectOffering(SubjectWithCollegeAndDeptDTO subjectDto, YearTerm yearTerm, List<String> grades) {
    }

    @RequiredArgsConstructor
    private static class LectureApplication {
        private final LctOpenApplyVO apply;
        private final ApprovalVO approval;
        private final List<LctApplyWeekbyVO> weeks;
        private final List<LctApplyGraderatioVO> ratios;
        private final SubjectVO subject;
        private final String termSuffix;

        public LctOpenApplyVO apply() {
            return apply;
        }

        public ApprovalVO approval() {
            return approval;
        }

        public List<LctApplyWeekbyVO> weeks() {
            return weeks;
        }

        public List<LctApplyGraderatioVO> ratios() {
            return ratios;
        }

        public SubjectVO subject() {
            return subject;
        }

        public String yearterm() {
            return apply.getYeartermCd();
        }

        public String termSuffix() {
            return termSuffix;
        }
    }

    private record StudentStats(
            int totalStudents,
            Map<String, Integer> gradeCounts,
            Map<String, Integer> deptCounts,
            Map<String, Integer> collegeCounts,
            Map<String, Map<String, Integer>> deptGradeCounts,
            Map<String, Map<String, Integer>> collegeGradeCounts
    ) {
    }
}
