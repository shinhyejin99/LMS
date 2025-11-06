package kr.or.jsu.classroomDummyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.LctPostVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Step 4: 강의별 더미 게시글(공지/자료) 15개 생성.
 * 실행 전 LECTURE_PREFIX를 변경하여 대상 강의를 지정하세요.
 */
@SpringBootTest
@Slf4j
class Step5_LecturePostDummyGeneratorTest {

    // === 직접 입력할 값 ===
    private static final String LECTURE_PREFIX = "218"; // 대상 강의의 식별 접두 3자리

    // === 고정값 ===
    private static final int POST_COUNT = 15;
    private static final String LECTURE_ID = "LECT" + LECTURE_PREFIX + "00000001";
    private static final LocalDate BASE_DATE = LocalDate.of(2025, 10, 13);

    private static final PostSeed[] POST_SEEDS = {
            new PostSeed("MATERIAL", "HTML 기본 구조 예시",
                    new String[] {
                            "수업 시간에 다룬 HTML 기본 뼈대를 정리한 자료입니다.",
                            "각 섹션에는 의미론적 태그와 접근성 관련 주의사항이 함께 포함되어 있습니다.",
                            "아래의 예시 마크업을 복사하여 본인 프로젝트 시작 템플릿으로 활용하세요.",
                            "헤더, 내비게이션, 메인, 푸터 영역별 예시 코드를 함께 비교해 보세요.",
                            "section과 article을 구분한 이유를 주석으로 설명했습니다.",
                            "CSS 리셋을 적용한 버전도 첨부되어 있으니 용도에 맞게 선택하세요.",
                            "질문은 QnA 게시판 'HTML-기초' 말머리로 올려주세요."
                    }),
            new PostSeed("NOTICE", "2주차 실습 강의실 안내",
                    new String[] {
                            "2주차 실습은 본관 502호에서 진행됩니다.",
                            "10분 일찍 도착하여 에디터와 브라우저를 미리 실행해 두시기 바랍니다.",
                            "공용 PC를 사용하는 학생은 이어폰을 지참하세요.",
                            "출석 체크는 강의실 입구에서 QR 코드로 진행합니다.",
                            "실습 교재 3장과 필기구를 반드시 지참해 주세요.",
                            "개인 노트북 사용 시 멀티탭 구역을 이용하면 전원을 공급받을 수 있습니다.",
                            "결석 예정자는 오늘 자정까지 수업조교 메일로 통보해 주세요."
                    }),
            new PostSeed("MATERIAL", "Flexbox 레이아웃 가이드",
                    new String[] {
                            "Flexbox 핵심 속성과 정렬 패턴, 자주 발생하는 실수를 정리한 요약표입니다.",
                            "gap 속성의 브라우저별 동작 차이를 비교할 수 있는 예제도 포함되어 있습니다.",
                            "이 자료를 참고하여 반응형 내비게이션 실습을 복습해 보세요.",
                            "flex-basis와 flex-grow 조합을 시각화한 다이어그램을 첨부했습니다.",
                            "정렬이 꼬일 때 확인할 디버깅 체크리스트도 함께 확인하세요.",
                            "IE 대응이 필요한 경우 display: -ms-flexbox 대체 예시를 참고하세요.",
                            "마지막 장에는 추가 추천 자료 링크와 실습 과제가 정리되어 있습니다."
                    }),
            new PostSeed("NOTICE", "과제 1 제출 안내",
                    new String[] {
                            "과제 1은 10월 20일 23시 59분까지 LMS 과제 제출함에 업로드해야 합니다.",
                            "README와 소스 코드를 하나의 압축 파일로 묶어서 제출하세요. 개별 파일 업로드는 금지입니다.",
                            "지각 제출 시 하루당 감점이 있으니 여유 있게 준비 바랍니다.",
                            "프로젝트 실행 방법과 테스트 방법을 README 상단에 명시해야 합니다.",
                            "깃 저장소 URL 제출 시 권한을 '읽기' 이상으로 열어두세요.",
                            "제출 후에는 LMS에서 업로드 여부를 다시 확인하여 누락이 없도록 합니다.",
                            "실습 시간에는 제출물에 대한 간단한 질의응답이 있을 예정입니다."
                    }),
            new PostSeed("MATERIAL", "DOM 조작 예제 모음",
                    new String[] {
                            "이 자료에는 이벤트 위임, 상태 초기화, 낙관적 렌더링 예제가 포함되어 있습니다.",
                            "콜백 기반과 async/await 기반 두 가지 버전으로 구성되어 비교 학습이 가능합니다.",
                            "자신의 UI에 맞게 선택자만 수정하여 활용하세요.",
                            "각 예제는 브라우저 콘솔에서 바로 실행 가능하도록 self-contained 되어 있습니다.",
                            "mutation observer 사용 예제도 추가되어 동적 요소 감지도 연습할 수 있습니다.",
                            "주요 함수마다 시간 복잡도와 주요 사용처를 주석으로 달아두었습니다.",
                            "추가로 공부하고 싶은 주제는 댓글로 남기면 후속 자료로 공유하겠습니다."
                    }),
            new PostSeed("NOTICE", "주간 퀴즈 범위 안내",
                    new String[] {
                            "이번 주 퀴즈는 HTML 템플릿과 Flexbox 기본 내용을 포함합니다.",
                            "퀴즈는 실습실 환경에서 자동 제출 방식으로 진행됩니다.",
                            "추가 연습 문제는 내일 오전 LMS 퀴즈 탭에 업로드될 예정입니다.",
                            "퀴즈는 총 10문항이며 객관식 7문항, 단답형 3문항으로 구성됩니다.",
                            "실습 코드에서 사용한 시맨틱 태그 의미를 묻는 문제가 출제됩니다.",
                            "교재 2장 복습 문제 중 1, 2, 4번을 풀어오면 대비에 도움이 됩니다.",
                            "모바일로 응시하는 경우 가로 모드를 권장합니다."
                    }),
            new PostSeed("MATERIAL", "REST API 요청 예시 모음",
                    new String[] {
                            "웹프로그래밍 프로젝트 실습에서 사용한 REST API 예시 모음입니다.",
                            "Postman과 HTTPie용 요청 예시를 모두 제공합니다.",
                            "하단의 JSON 본문을 수정하여 자신의 서버로 테스트해보세요.",
                            "토큰 인증이 필요한 요청은 Authorization 헤더 샘플을 참고하세요.",
                            "요청/응답에 포함된 상태 코드와 응답 시간 측정 방법을 안내했습니다.",
                            "Swagger 문서 생성 스크립트 링크도 하단에 첨부했습니다.",
                            "파라미터별 설명을 보기 쉽게 표로 정리해 PDF에 포함시켰습니다."
                    }),
            new PostSeed("NOTICE", "팀프로젝트 킥오프 일정",
                    new String[] {
                            "10월 24일 수업 직후 팀프로젝트 킥오프 미팅을 진행합니다.",
                            "팀 구성, 역할 분담, 진행 방식 등을 설명하고 질의응답 시간을 가질 예정입니다.",
                            "참석이 어려운 학생은 사전에 사유를 이메일로 보내고 역할 분배안을 제출하세요.",
                            "모든 팀은 미팅 전에 Git 저장소를 생성해 링크를 준비해 오세요.",
                            "필요한 경우 수업 후 강의실을 30분 추가로 사용할 수 있습니다.",
                            "킥오프 자료는 미팅 하루 전까지 게시판에 업로드하겠습니다.",
                            "조교가 팀별 초기 일정표 예시를 공유할 예정이니 참고해 주세요."
                    }),
            new PostSeed("MATERIAL", "Spring Boot 계층 구조 예제",
                    new String[] {
                            "Controller, Service, Repository 계층을 명확히 분리한 예제 프로젝트입니다.",
                            "간단한 단위 테스트도 포함되어 있어 자신의 모듈에 맞게 참고할 수 있습니다.",
                            "패키지 구조와 슬라이드의 설계 다이어그램을 비교해보세요.",
                            "패키지마다 README를 추가하여 역할을 빠르게 파악할 수 있습니다.",
                            "예제에서 사용한 DTO 변환 로직은 MapStruct 대안도 함께 소개했습니다.",
                            "환경 프로필별 설정 yml 파일 샘플도 포함되어 있습니다.",
                            "테스트 커버리지 리포트 캡처를 참고하여 품질 기준을 세워보세요."
                    }),
            new PostSeed("NOTICE", "데이터베이스 실습 준비 안내",
                    new String[] {
                            "다음 주 수업에는 로컬 DB 환경이 필요합니다. Docker 또는 직접 설치 중 하나를 준비하세요.",
                            "설치 매뉴얼의 관리자 계정 설정 부분을 특히 주의하세요.",
                            "문제가 발생하면 스크린샷과 로그를 QnA 게시판에 올려주세요.",
                            "로컬 설치가 어려운 경우 학교 VPN으로 연결한 원격 DB 계정도 제공됩니다.",
                            "Docker 사용 시 compose 파일에서 포트 충돌이 없는지 미리 확인하세요.",
                            "샘플 덤프 파일은 공지 첨부에서 다운로드할 수 있습니다.",
                            "설치 과정에서 발생한 오류는 실습 시작 전까지 QnA에 공유해 주세요."
                    }),
            new PostSeed("MATERIAL", "JWT 인증 요약 정리",
                    new String[] {
                            "토큰 발급, 갱신, 로그아웃 처리 과정을 단계별로 설명한 자료입니다.",
                            "배포 전 확인해야 할 체크리스트도 포함되어 있습니다.",
                            "예제 필터 설정을 참고하여 본인 프로젝트의 보안 필터에 추가해보세요.",
                            "Access 토큰과 Refresh 토큰 만료 시간을 구분하여 설계한 사례를 담았습니다.",
                            "토큰 탈취 대응 시나리오와 보안 로그 정책을 함께 정리했습니다.",
                            "쿠키 저장 방식과 로컬스토리지 저장 방식 비교표도 올려두었습니다.",
                            "의도치 않은 401 응답을 디버깅하는 체크리스트도 참고하세요."
                    }),
            new PostSeed("NOTICE", "복습 세션 안내",
                    new String[] {
                            "11월 첫째 주 강의 후 30분간 복습 세션을 진행합니다.",
                            "과제 1 피드백과 팀 프로젝트 진행 관련 Q&A 시간을 가질 예정입니다.",
                            "참석이 어려운 학생은 질문을 미리 제출하면 요약 답변을 게시하겠습니다.",
                            "세션 내용은 녹화 후 24시간 내에 LMS에 업로드할 예정입니다.",
                            "참여를 원하는 학생은 설문을 통해 다루고 싶은 주제를 미리 선택해 주세요.",
                            "현장에서는 추가 실습 시간도 15분 정도 제공합니다.",
                            "피드백 요약본은 다음 주 수업 자료에 첨부됩니다."
                    }),
            new PostSeed("MATERIAL", "프런트엔드 빌드 스크립트 예시",
                    new String[] {
                            "Vite 기반 개발/운영 환경 분리 빌드 스크립트 예제입니다.",
                            "운영체제별 환경 변수 설정 규칙을 명시하여 협업 시 혼선을 줄입니다.",
                            "CI 환경에서 추가 다운로드 없이 빌드 가능한 npm 스크립트도 포함되어 있습니다.",
                            "dotenv를 활용한 민감 정보 분리 방법을 코드로 보여줍니다.",
                            "eslint와 prettier를 pre-commit 훅으로 연결하는 방법을 포함했습니다.",
                            "SSR 환경에서 사용할 수 있는 빌드 명령도 같이 정리했습니다.",
                            "실제로 동작하는 예시는 예제 저장소 링크를 통해 확인할 수 있습니다."
                    }),
            new PostSeed("NOTICE", "보안 실습 워크숍 안내",
                    new String[] {
                            "11월 12일에 OWASP Top 10 중심의 보안 실습 워크숍을 진행합니다.",
                            "현재 진행 중인 프로젝트 브랜치를 가져오면 실제 취약점을 함께 점검합니다.",
                            "워크숍 전날까지 최신 커밋으로 동기화해두세요.",
                            "워크숍은 오전 세션과 오후 세션으로 나뉘며 원하는 시간대를 선택할 수 있습니다.",
                            "실습용 계정은 워크숍 당일 현장에서 배포됩니다.",
                            "사전 학습 링크를 통해 SQL Injection, XSS 기초를 복습하고 오세요.",
                            "워크숍 이후에는 취약점 리포트 템플릿을 배포할 예정입니다."
                    }),
            new PostSeed("MATERIAL", "배포 파이프라인 체크리스트",
                    new String[] {
                            "웹프로그래밍 스택에 맞춘 CI/CD 배포 단계 점검표입니다.",
                            "환경 변수, 권한 설정, 롤백 절차 등 자주 누락되는 항목을 정리했습니다.",
                            "팀 위키에 복사하여 릴리스 준비 상태를 관리하세요.",
                            "배포 승인 절차에 필요한 산출물 목록을 표로 정리했습니다.",
                            "Blue-Green 전략을 적용할 때 확인해야 할 모니터링 항목도 포함했습니다.",
                            "자동화 스크립트 실행 로그 예시는 문서 마지막에 첨부했습니다.",
                            "체크리스트를 활용한 피드백 루프 구성 방법도 안내했습니다."
                    })
    };

    @Autowired
    private DummyDataMapper dummyDataMapper;

    @Test
    void insertLecturePosts() {
        List<LctPostVO> posts = buildPosts();
        int inserted = dummyDataMapper.insertDummyLctPosts(posts);
        assertEquals(POST_COUNT, inserted, "LCT_POST 테이블에는 15개의 게시글이 삽입되어야 합니다.");
        log.info("강의 게시글 더미 데이터 삽입 완료: {}", inserted);
    }

    private List<LctPostVO> buildPosts() {
        List<LctPostVO> posts = new ArrayList<>(POST_COUNT);
        for (int idx = 0; idx < POST_COUNT; idx++) {
            PostSeed seed = POST_SEEDS[idx];
            LctPostVO post = new LctPostVO();
            post.setLctPostId(buildPostId(idx + 1));
            post.setLectureId(LECTURE_ID);
            post.setTitle(seed.title());
            post.setContent(buildContent(seed.paragraphs()));
            post.setCreateAt(calculateCreateAt(idx));
            post.setAttachFileId(null);
            post.setDeleteYn("N");
            post.setPostType(seed.postType());
            post.setTempSaveYn("N");
            post.setRevealAt(null);
            posts.add(post);
        }
        return posts;
    }

    private LocalDateTime calculateCreateAt(int index) {
        LocalDate date = BASE_DATE.plusDays(index);
        LocalTime time = LocalTime.of(13 + (index % 5), 0);
        return LocalDateTime.of(date, time);
    }

    private String buildContent(String[] paragraphs) {
        StringBuilder sb = new StringBuilder();
        for (String line : paragraphs) {
            sb.append("<p>").append(line).append("</p>");
        }
        return sb.toString();
    }

    private String buildPostId(int sequence) {
        return "LCTPST" + LECTURE_PREFIX + String.format("%06d", sequence);
    }

    private record PostSeed(String postType, String title, String[] paragraphs) {
    }
}
