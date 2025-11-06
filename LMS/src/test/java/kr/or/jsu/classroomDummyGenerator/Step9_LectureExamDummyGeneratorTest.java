package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.ExamSubmitVO;
import kr.or.jsu.vo.LctExamVO;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 9: generate offline exam metadata and submissions.
 * Adjust prefix constants before running.
 */
@SpringBootTest
@Slf4j
class Step9_LectureExamDummyGeneratorTest {

    // === Direct input values ===
    private static final String LECTURE_PREFIX = "218"; // professor-based prefix
    private static final String STUDENT_PREFIX = "318"; // student-based prefix

    // === Fixed values ===
    private static final String LECTURE_ID = "LECT" + LECTURE_PREFIX + "00000001";
    private static final String DELETE_YN = "N";
    private static final ExamSpec[] EXAM_SPECS = {
            new ExamSpec(
                    "LCTEXM" + LECTURE_PREFIX + "000001",
                    "중간 평가",
                    new String[] {
                            "웹 프로그래밍 강의의 중간 학습 내용을 확인하는 오프라인 시험입니다.",
                            "HTML/CSS 기본 구조와 접근성, 반응형 설계 개념을 평가합니다.",
                            "간단한 레이아웃 구현 문제와 서술형 개념 질문이 포함되어 있습니다.",
                            "필기구와 학생증을 지참하고 시험 시작 15분 전에 입실하세요."
                    },
                    LocalDateTime.of(2025, 9, 23, 15, 0),
                    LocalDateTime.of(2025, 9, 22, 10, 0),
                    LocalDateTime.of(2025, 9, 22, 10, 0),
                    40
            ),
            new ExamSpec(
                    "LCTEXM" + LECTURE_PREFIX + "000002",
                    "기말 오프라인 평가",
                    new String[] {
                            "과정 전체를 종합 점검하는 오프라인 시험입니다.",
                            "REST API 연동, JWT 인증, 배포 자동화 등 심화 내용을 다룹니다.",
                            "서술형 문제와 시스템 설계 요약 문제를 포함합니다.",
                            "시험 당일 노트북은 사용하지 않으며, 팀 프로젝트 내용은 참고만 가능합니다."
                    },
                    LocalDateTime.of(2025, 10, 29, 15, 0),
                    LocalDateTime.of(2025, 10, 28, 10, 0),
                    LocalDateTime.of(2025, 10, 28, 10, 0),
                    60
            )
    };

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertOfflineExams() {
        List<LctExamVO> exams = buildExams();
        int insertedExams = dummyDataMapper.insertDummyExams(exams);
        assertEquals(EXAM_SPECS.length, insertedExams, "LCT_EXAM must receive 2 rows.");

        List<ExamSubmitVO> submissions = buildExamSubmissions(exams);
        int insertedSubmissions = dummyDataMapper.insertDummyExamSubmits(submissions);
        assertEquals(submissions.size(), insertedSubmissions, "EXAM_SUBMIT must receive 58 rows.");

        log.info("Inserted exam data: exams={}, submissions={}", insertedExams, insertedSubmissions);
    }

    private List<LctExamVO> buildExams() {
        List<LctExamVO> exams = new ArrayList<>(EXAM_SPECS.length);
        for (ExamSpec spec : EXAM_SPECS) {
            LctExamVO exam = new LctExamVO();
            exam.setLctExamId(spec.getId());
            exam.setLectureId(LECTURE_ID);
            exam.setExamName(spec.getName());
            exam.setExamDesc(String.join(System.lineSeparator(), spec.getDescriptionLines()));
            exam.setExamType("OFF");
            exam.setCreateAt(spec.getCreateAt());
            exam.setStartAt(spec.getStartAt());
            exam.setEndAt(spec.getEndAt());
            exam.setWeightValue(spec.getWeight());
            exam.setDeleteYn(DELETE_YN);
            exams.add(exam);
        }
        return exams;
    }

    private List<ExamSubmitVO> buildExamSubmissions(List<LctExamVO> exams) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<ExamSubmitVO> submissions = new ArrayList<>(exams.size() * 30);
        for (LctExamVO exam : exams) {
            LocalDateTime submitAt = exam.getStartAt();
            for (int studentIndex = 1; studentIndex <= 30; studentIndex++) {
                ExamSubmitVO submit = new ExamSubmitVO();
                submit.setEnrollId(buildEnrollId(studentIndex));
                submit.setLctExamId(exam.getLctExamId());
                submit.setSubmitAt(submitAt);
                int score = resolveScore(studentIndex, random);
                submit.setAutoScore(score);
                submit.setModifiedScore(score);
                submit.setModifyReason(null);
                submissions.add(submit);
            }
        }
        return submissions;
    }

    private int resolveScore(int studentIndex, ThreadLocalRandom random) {
        if (studentIndex == 1) {
            return 95;
        }
        return random.nextInt(30, 91);
    }

    private String buildEnrollId(int sequence) {
        return "STENRL" + STUDENT_PREFIX + String.format(Locale.ROOT, "%06d", sequence);
    }

    @Value
    private static class ExamSpec {
        String id;
        String name;
        String[] descriptionLines;
        LocalDateTime createAt;
        LocalDateTime startAt;
        LocalDateTime endAt;
        int weight;
    }
}
