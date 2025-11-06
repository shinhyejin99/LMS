package kr.or.jsu.lms.student.service.financialAid;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.vo.StudentVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class StuTuitionServiceImplTest {

	@Autowired
	private StuTuitionService service;
	// 테스트용 상수
	private static final String TEST_STUDENT_NO = "202590994";
    private static final String TEST_YEARTERM_CD = "2025_REG2";

    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        StudentVO student = new StudentVO();
        student.setStudentNo(TEST_STUDENT_NO);

        // 가짜 객체 생성
        mockUserDetails = mock(CustomUserDetails.class);
        // mockUserDetails.getRealUser()를 호출하면 student 객체를 반환함
        when(mockUserDetails.getRealUser()).thenReturn(student);

        log.info("테스트 준비 완료. 학번: {}, 학기: {}", TEST_STUDENT_NO, TEST_YEARTERM_CD);
    }

//	@Test
//	void testGetTuitionNoticeByUserDetails() {
//
//        // service 실행
//        TuitionNoticeDTO result = service.getTuitionNotice(mockUserDetails);
//        log.info(" 통합 DTO 정보 : {}", result);
//        assertThat(result)
//            .as("반환된 고지서 DTO는 null이 아님")
//            .isNotNull();
//
//        // 메인 정보 검증
//        assertThat(result.getMainInfo())
//            .as("메인 정보(TuitionDetailDTO)는 null이 아님")
//            .isNotNull();
//        assertThat(result.getMainInfo().getStudentNo())
//            .as("조회된 학생 번호가 일치하지 않음")
//            .isEqualTo(TEST_STUDENT_NO);
//
//        // 상세 내역 검증
//        assertThat(result.getTuitionDetails())
//            .as("등록금 상세 내역은 null이 아니어야 함")
//            .isNotNull()
//            .as("등록금 상세 내역은 최소 1개 이상 존재해야 함")
//            .isNotEmpty();
//
//        log.info("통합 테스트 성공: 고지서 정보가 DB에서 조회됨");
//	}
}
