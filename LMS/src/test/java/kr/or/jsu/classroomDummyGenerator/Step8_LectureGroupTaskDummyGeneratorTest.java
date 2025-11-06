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
import kr.or.jsu.vo.GrouptaskCrewVO;
import kr.or.jsu.vo.GrouptaskGroupVO;
import kr.or.jsu.vo.GrouptaskSubmitVO;
import kr.or.jsu.vo.LctGrouptaskVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 8: generate group task, groups, crew assignments, and submissions.
 * Replace prefix constants before running.
 */
@SpringBootTest
@Slf4j
class Step8_LectureGroupTaskDummyGeneratorTest {

    // === Direct input values ===
    private static final String LECTURE_PREFIX = "218"; // professor-based prefix
    private static final String STUDENT_PREFIX = "318"; // student-based prefix
    private static final String GROUPTASK_ATTACH_FILE_ID = "FILE00000000489"; // 조별과제에 넣을 파일 정해지면 수정할 것

    // === Fixed values ===
    private static final String LECTURE_ID = "LECT" + LECTURE_PREFIX + "00000001";
    private static final String GROUPTASK_ID = "TSKGRUP" + LECTURE_PREFIX + "00001";
    private static final int GROUP_COUNT = 6;
    private static final int STUDENT_COUNT = 30;

    private static final String[] GROUP_NAMES = {
            "프론트엔드 연구회",
            "알고리즘 탐험대",
            "API 설계팀",
            "JWT 보안반",
            "데이터 아키텍처",
            "품질 개선소"
    };

    private static final String[] SUBMIT_DESCS = {
            "최종 보고서와 발표 자료를 첨부합니다.",
            "시연 영상 링크와 실행 방법을 기술했습니다.",
            "테스트 자동화 결과 보고서를 포함했습니다.",
            "설계 다이어그램과 데이터 모델을 함께 제출합니다.",
            "사용자 인터뷰 피드백을 정리했습니다.",
            "향후 개선 계획과 일정표를 공유했습니다."
    };

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertGroupTaskData() {
        LctGrouptaskVO task = buildGroupTask();
        int insertedTask = dummyDataMapper.insertDummyGrouptasks(List.of(task));
        assertEquals(1, insertedTask, "LCT_GROUPTASK must receive 1 row.");

        List<GrouptaskGroupVO> groups = buildGroups();
        int insertedGroups = dummyDataMapper.insertDummyGrouptaskGroups(groups);
        assertEquals(GROUP_COUNT, insertedGroups, "GROUPTASK_GROUP must receive 6 rows.");

        List<GrouptaskCrewVO> crews = buildCrews(groups);
        int insertedCrews = dummyDataMapper.insertDummyGrouptaskCrews(crews);
        assertEquals(crews.size(), insertedCrews, "GROUPTASK_CREW must receive all crew rows.");

        List<GrouptaskSubmitVO> submits = buildGroupSubmits(groups);
        int insertedSubmits = dummyDataMapper.insertDummyGrouptaskSubmits(submits);
        assertEquals(GROUP_COUNT, insertedSubmits, "GROUPTASK_SUBMIT must receive 6 rows.");

        log.info("Inserted group task data: task={}, groups={}, crew={}, submits={}",
                insertedTask, insertedGroups, insertedCrews, insertedSubmits);
    }

    private LctGrouptaskVO buildGroupTask() {
        LctGrouptaskVO task = new LctGrouptaskVO();
        task.setGrouptaskId(GROUPTASK_ID);
        task.setLectureId(LECTURE_ID);
        task.setGrouptaskName("웹 프로그래밍 팀 프로젝트");
        task.setGrouptaskDesc(multilineDescription(
                "팀원들과 협업하여 축제 관리 웹 서비스의 핵심 기능을 설계하세요.",
                "강의에서 제시한 일정에 맞춰 스프린트를 계획하고 매주 진행 상황을 공유합니다.",
                "역할 분담표를 작성하여 각 조원이 맡을 API와 화면을 명확히 합니다.",
                "공통 UI 컴포넌트는 디자인 가이드에 맞춰 라이브러리 형태로 정리하세요.",
                "단위 테스트와 통합 테스트 자동화를 구축하고 실행 방법을 문서화합니다.",
                "범위, 일정, 인력 위험에 대한 대응 계획을 프로젝트 문서에 포함합니다.",
                "주요 사용자 시나리오를 시연하는 3분 내외의 데모 영상을 녹화하세요.",
                "최종 보고서, 발표 자료, 배포 체크리스트를 하나의 압축 파일로 제출합니다."
        ));
        task.setCreateAt(LocalDateTime.of(2025, 10, 1, 15, 0));
        task.setStartAt(LocalDateTime.of(2025, 10, 2, 0, 0, 1));
        task.setEndAt(LocalDateTime.of(2025, 10, 31, 23, 59, 59));
        task.setDeleteYn("N");
        task.setAttachFileId(GROUPTASK_ATTACH_FILE_ID);
        return task;
    }

    private List<GrouptaskGroupVO> buildGroups() {
        List<GrouptaskGroupVO> groups = new ArrayList<>(GROUP_COUNT);
        LocalDateTime createAt = LocalDateTime.of(2025, 10, 2, 0, 0, 1);
        for (int i = 0; i < GROUP_COUNT; i++) {
            GrouptaskGroupVO group = new GrouptaskGroupVO();
            group.setGroupId(buildGroupId(i + 1));
            group.setReaderEnrollId(buildLeaderEnrollId(i));
            group.setGrouptaskId(GROUPTASK_ID);
            group.setGroupName(GROUP_NAMES[i % GROUP_NAMES.length]);
            group.setCreateAt(createAt);
            group.setUsingYn("Y");
            groups.add(group);
        }
        return groups;
    }

    private List<GrouptaskCrewVO> buildCrews(List<GrouptaskGroupVO> groups) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<GrouptaskCrewVO> crews = new ArrayList<>(STUDENT_COUNT);
        int studentIndex = 1;
        for (GrouptaskGroupVO group : groups) {
            int start = studentIndex;
            int end = Math.min(studentIndex + 4, STUDENT_COUNT);
            for (int idx = start; idx <= end; idx++) {
                String enrollId = buildEnrollId(idx);
                GrouptaskCrewVO crew = new GrouptaskCrewVO();
                crew.setGroupId(group.getGroupId());
                crew.setEnrollId(enrollId);
                crew.setEvaluScore(resolveCrewScore(idx, random));
                crew.setEvaluAt(LocalDateTime.of(2025, 11, 1, 15, 0));
                crew.setEvaluDesc("검토 완료");
                crews.add(crew);
            }
            studentIndex += 5;
        }
        return crews;
    }

    private List<GrouptaskSubmitVO> buildGroupSubmits(List<GrouptaskGroupVO> groups) {
        List<GrouptaskSubmitVO> submits = new ArrayList<>(GROUP_COUNT);
        LocalDateTime submitAt = LocalDateTime.of(2025, 10, 30, 15, 0);
        for (GrouptaskGroupVO group : groups) {
            GrouptaskSubmitVO submit = new GrouptaskSubmitVO();
            submit.setGrouptaskId(GROUPTASK_ID);
            submit.setGroupId(group.getGroupId());
            submit.setSubmitDesc(pickSubmitDesc());
            submit.setSubmitFileId(null);
            submit.setSubmitAt(submitAt.toLocalDate());
            submits.add(submit);
        }
        return submits;
    }

    private int resolveCrewScore(int studentIndex, ThreadLocalRandom random) {
        if (studentIndex == 1) {
            return 95;
        }
        return random.nextInt(70, 91);
    }

    private String buildGroupId(int sequence) {
        return "TASKJO" + STUDENT_PREFIX + String.format(Locale.ROOT, "%06d", sequence);
    }

    private String buildLeaderEnrollId(int groupIndex) {
        int leaderNumber = groupIndex * 5 + 1;
        return buildEnrollId(leaderNumber);
    }

    private String buildEnrollId(int sequence) {
        return "STENRL" + STUDENT_PREFIX + String.format(Locale.ROOT, "%06d", sequence);
    }

    private String pickSubmitDesc() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return SUBMIT_DESCS[random.nextInt(SUBMIT_DESCS.length)];
    }

    private static String multilineDescription(String... lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append("<p>").append(line).append("</p>");
        }
        return sb.toString();
    }
}
