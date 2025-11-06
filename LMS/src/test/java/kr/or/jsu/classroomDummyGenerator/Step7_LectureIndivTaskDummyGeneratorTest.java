package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.IndivtaskSubmitVO;
import kr.or.jsu.vo.LctIndivtaskVO;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 7: register individual tasks and submissions for a lecture.
 * Replace the prefix constants before running.
 */
@SpringBootTest
@Slf4j
class Step7_LectureIndivTaskDummyGeneratorTest {

    // === Direct input values ===
    private static final String LECTURE_PREFIX = "218"; // professor-based prefix
    private static final String STUDENT_PREFIX = "318"; // student-based prefix
    private static final String[] INDIVTASK_ATTACH_FILE_IDS = { // 각 첨부할 파일들 정해지면 수정할 것
            "FILE00000000488",
            "FILE00000000487",
            "FILE00000000488"
    };

    // === Fixed values ===
    private static final String LECTURE_ID = "LECT" + LECTURE_PREFIX + "00000001";
    private static final int STUDENT_COUNT = 30;
    private static final String DELETE_YN = "N";

    private static final TaskSpec[] TASK_SPECS = {
            TaskSpec.builder()
                    .index(1)
                    .name("웹 프로그래밍 실습 과제")
                    .description(multilineDescription(
                            "강의에서 다룬 반응형 레이아웃 예제를 참고하여 HTML과 CSS로 메인 페이지를 구현하세요.",
                            "공유된 와이어프레임을 기반으로 헤더, 본문, 푸터를 동일한 구조로 배치합니다.",
                            "시각장애인 접근성을 위해 주요 구역마다 의미 있는 시맨틱 태그를 사용하세요.",
                            "내비게이션 앵커를 최소 3개 이상 배치하고 각 섹션에 제목을 부여합니다.",
                            "레이아웃 정렬은 flex 혹은 grid를 활용하며 absolute 위치 지정을 남용하지 않습니다.",
                            "이미지와 아이콘은 용량을 최소화하고 alt 속성에 설명을 기입합니다.",
                            "디자인 선택과 참고 자료는 DESIGN_NOTE.md 파일에 간단히 정리하세요.",
                            "최종 산출물은 STUDENTNO_HW1 형태의 폴더명으로 압축하여 제출합니다."
                    ))
                    .createAt(LocalDateTime.of(2025, 8, 1, 15, 0))
                    .startAt(LocalDateTime.of(2025, 8, 2, 0, 0, 1))
                    .endAt(LocalDateTime.of(2025, 8, 14, 23, 59, 59))
                    .attachFileId(INDIVTASK_ATTACH_FILE_IDS[0])
                    .build(),
            TaskSpec.builder()
                    .index(2)
                    .name("REST API 클라이언트 제작")
                    .description(multilineDescription(
                            "교내 시간표 서비스를 대상으로 하는 REST API 클라이언트를 구현하세요.",
                            "강의에서 다룬 GET, POST, DELETE 흐름을 동일한 규격으로 작성합니다.",
                            "HTTP 오류 응답을 수집하여 로그로 남기고 사용자 메시지를 한글로 제공합니다.",
                            "요청 본문을 검증하여 필수 필드 누락 시 서버 호출을 차단합니다.",
                            "각 API 호출을 재현할 수 있는 curl 스크립트를 scripts 폴더에 포함하세요.",
                            "성공·실패 경로를 모두 다루는 단위 테스트 코드를 작성하고 결과를 캡처합니다.",
                            "제약 사항과 향후 개선 아이디어는 SUBMISSION_NOTES.md에 기록합니다.",
                            "최종 커밋 태그는 STUDENTNO_HW2로 지정하고 커밋 해시를 보고서에 명시하세요."
                    ))
                    .createAt(LocalDateTime.of(2025, 9, 1, 15, 5))
                    .startAt(LocalDateTime.of(2025, 9, 2, 0, 0, 1))
                    .endAt(LocalDateTime.of(2025, 9, 14, 23, 59, 59))
                    .attachFileId(INDIVTASK_ATTACH_FILE_IDS[1])
                    .build(),
            TaskSpec.builder()
                    .index(3)
                    .name("JWT 인증 모듈 구현")
                    .description(multilineDescription(
                            "강의실 포털을 위한 JWT 기반 인증 모듈을 설계하고 구현하세요.",
                            "학생 권한 정보를 담은 커스텀 클레임을 포함하여 액세스 토큰을 발급합니다.",
                            "리프레시 토큰은 안전한 저장소에 보관하고 재사용 시 즉시 회전시키세요.",
                            "최소 3개의 API 엔드포인트에 인증 미들웨어를 적용하여 접근을 제한합니다.",
                            "서명 검증 실패나 만료 등 이상 징후는 모두 감사 로그로 남깁니다.",
                            "보안 가정과 정책은 SECURITY_NOTES.md 파일에서 명확히 설명하세요.",
                            "토큰 만료, 리프레시, 로그아웃 시나리오를 시뮬레이션하여 기대 동작을 정리합니다.",
                            "시연을 위한 단계별 스크립트를 작성하고 README에 링크를 추가하세요."
                    ))
                    .createAt(LocalDateTime.of(2025, 10, 1, 15, 10))
                    .startAt(LocalDateTime.of(2025, 10, 2, 0, 0, 1))
                    .endAt(LocalDateTime.of(2025, 10, 14, 23, 59, 59))
                    .attachFileId(INDIVTASK_ATTACH_FILE_IDS[2])
                    .build()
    };

    private static final String[] SUBMIT_DESCRIPTIONS = {
            "요구사항대로 모든 기능을 완성했습니다.",
            "테스트 코드 결과와 함께 제출합니다.",
            "추가 참고 문서는 README에 정리했습니다.",
            "협업한 동료의 리뷰를 반영했습니다.",
            "배포 URL과 로그인 정보를 첨부했습니다.",
            "문제 발생 시 복구 절차를 안내했습니다."
    };

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertTasksAndSubmissions() {
        List<LctIndivtaskVO> tasks = buildTasks();
        int insertedTasks = dummyDataMapper.insertDummyIndivtasks(tasks);
        assertEquals(TASK_SPECS.length, insertedTasks, "LCT_INDIVTASK must receive 3 rows.");

        List<IndivtaskSubmitVO> submissions = buildSubmissions(tasks);
        int insertedSubmits = dummyDataMapper.insertDummyIndivtaskSubmits(submissions);
        assertEquals(submissions.size(), insertedSubmits, "INDIVTASK_SUBMIT must receive 87 rows.");

        log.info("Inserted individual tasks and submissions: tasks={}, submits={}", insertedTasks, insertedSubmits);
    }

    private List<LctIndivtaskVO> buildTasks() {
        List<LctIndivtaskVO> tasks = new ArrayList<>(TASK_SPECS.length);
        for (TaskSpec spec : TASK_SPECS) {
            LctIndivtaskVO vo = new LctIndivtaskVO();
            vo.setIndivtaskId(buildTaskId(spec.getIndex()));
            vo.setLectureId(LECTURE_ID);
            vo.setIndivtaskName(spec.getName());
            vo.setIndivtaskDesc(spec.getDescription());
            vo.setCreateAt(spec.getCreateAt());
            vo.setStartAt(spec.getStartAt());
            vo.setEndAt(spec.getEndAt());
            vo.setDeleteYn(DELETE_YN);
            vo.setAttachFileId(spec.getAttachFileId() != null ? spec.getAttachFileId() : INDIVTASK_ATTACH_FILE_IDS[0]);
            tasks.add(vo);
        }
        return tasks;
    }

    private List<IndivtaskSubmitVO> buildSubmissions(List<LctIndivtaskVO> tasks) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<IndivtaskSubmitVO> submissions = new ArrayList<>(tasks.size() * STUDENT_COUNT);
        for (LctIndivtaskVO task : tasks) {
            LocalDate monthBase = task.getCreateAt().toLocalDate();
            for (int studentIndex = 1; studentIndex <= STUDENT_COUNT; studentIndex++) {
                String enrollId = buildEnrollId(studentIndex);
                IndivtaskSubmitVO submit = new IndivtaskSubmitVO();
                submit.setEnrollId(enrollId);
                submit.setIndivtaskId(task.getIndivtaskId());
                submit.setSubmitDesc(pickSubmitDesc(random));
                submit.setSubmitFileId(null);
                submit.setSubmitAt(randomSubmitAt(monthBase, random));
                submit.setEvaluScore(resolveScore(studentIndex, random));
                submit.setEvaluAt(monthBase.withDayOfMonth(16).atTime(15, 0));
                submit.setEvaluDesc("검토 완료");
                submissions.add(submit);
            }
        }
        return submissions;
    }

    private String pickSubmitDesc(ThreadLocalRandom random) {
        return SUBMIT_DESCRIPTIONS[random.nextInt(SUBMIT_DESCRIPTIONS.length)];
    }

    private LocalDateTime randomSubmitAt(LocalDate monthBase, ThreadLocalRandom random) {
        int day = random.nextInt(12, 15); // 12~14 inclusive
        LocalTime time = LocalTime.of(random.nextInt(9, 22), random.nextInt(0, 60));
        return monthBase.withDayOfMonth(day).atTime(time);
    }

    private static String multilineDescription(String... lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append("<p>").append(line).append("</p>");
        }
        return sb.toString();
    }

    private Integer resolveScore(int studentIndex, ThreadLocalRandom random) {
        if (studentIndex == 1) {
            return 95;
        }
        return random.nextInt(10, 91);
    }

    private String buildTaskId(int index) {
        return "TSKINDV" + LECTURE_PREFIX + String.format("%05d", index);
    }

    private String buildEnrollId(int sequence) {
        return "STENRL" + STUDENT_PREFIX + String.format("%06d", sequence);
    }

    @Value
    @Builder
    private static class TaskSpec {
        int index;
        String name;
        String description;
        LocalDateTime createAt;
        LocalDateTime startAt;
        LocalDateTime endAt;
        String attachFileId;
    }
}
