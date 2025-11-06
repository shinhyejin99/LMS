package kr.or.jsu.lms.student.service.academicChange;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.RecordApplyRequestDTO;
import kr.or.jsu.dto.RecordApplyResponseDTO;
import kr.or.jsu.mybatis.mapper.RecordApplyMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class StuRecordApplyServiceImplTest {

	@Autowired
	private StuRecordApplyService service;
	@Autowired
	private RecordApplyMapper recordApplyMapper;

	private final String ENROLLED_STUDENT_NO = "202500001"; // 재학 중인 학생 : 김새봄
	private final String STUDENT_USER_ID = "USER00000000016";
	private final String DEPT_CD = "2025_REG2"; // 컴퓨터공학과

	/**
     * 임시 메소드 : 자퇴 신청 데이터를 DB에 삽입하고 applyId를 반환
     */
    private String createDropApplyFixture(String studentNo, String reason) {
        RecordApplyRequestDTO requestDTO = new RecordApplyRequestDTO();
		requestDTO.setStudentNo(studentNo);
		requestDTO.setUnivDeptCd(DEPT_CD);
		requestDTO.setRecordChangeCd("DROP");
		requestDTO.setApplyReason(reason);
        return service.applyRecord(requestDTO, STUDENT_USER_ID);
    }

	@Test
	void testApplyRecord() {
		// Given
		RecordApplyRequestDTO requestDTO = new RecordApplyRequestDTO();
		requestDTO.setStudentNo(ENROLLED_STUDENT_NO); // 재학 중
		requestDTO.setUnivDeptCd(DEPT_CD);
		requestDTO.setRecordChangeCd("DROP");
		requestDTO.setApplyReason("집안 사정으로 자퇴합니다."); // 필수값

		// When
		String applyId = service.applyRecord(requestDTO, STUDENT_USER_ID);
		log.info("생성된 자퇴 신청 ID: {}", applyId);

		// Then (DB에서 생성된 데이터 검증)
		assertNotNull(applyId, "신청 ID는 반드시 생성되어야 함");

//		RecordApplyResponseDTO appliedData = service.getApplyDetail(applyId);
//		assertNotNull(appliedData, "DB에 신청 정보가 정상적으로 저장되었어야 함");
//		assertEquals("DROP", appliedData.getRecordChangeCd(), "신청 타입이 'DROP'이어야 함");
//		assertEquals("PENDING", appliedData.getApplyStatusCd(), "초기 신청 상태는 'PENDING'이어야 함");
	}

//	@Test
//	void testGetApplyDetail() {
//		// Given (자퇴 신청 데이터 생성)
//		String applyId = createDropApplyFixture(ENROLLED_STUDENT_NO, "상세 조회 테스트");
//
//		// When
//		RecordApplyResponseDTO result = service.getApplyDetail(applyId);
//
//		// Then
//		assertNotNull(result, "조회 결과는 null이 아니어야 함");
//		assertEquals(applyId, result.getApplyId(), "조회된 ID가 일치해야 함");
//		assertEquals("DROP", result.getRecordChangeCd(), "신청 타입이 'DROP'이어야 함");
//		assertEquals(ENROLLED_STUDENT_NO, result.getStudentNo(), "학생 번호가 일치해야 함");
//		assertTrue(result.getApplyReason().contains("상세 조회 테스트"), "신청 사유가 포함되어야 함");
//		log.info("====> 조회된 결과 : {}", result);
//	}

	@Test
	void testGetApplyList() {

	    // 자퇴 신청 (DROP)
	    createDropApplyFixture(ENROLLED_STUDENT_NO, "첫 번째 자퇴");

	    RecordApplyRequestDTO restRequest = new RecordApplyRequestDTO();
	    restRequest.setStudentNo(ENROLLED_STUDENT_NO);
	    restRequest.setUnivDeptCd(DEPT_CD);
	    restRequest.setRecordChangeCd("REST");
	    restRequest.setApplyReason("휴학 테스트");
	    restRequest.setLeaveType("GENERAL");
	    restRequest.setLeaveStartTerm("202710");
	    service.applyRecord(restRequest, STUDENT_USER_ID); // 두 번째 PENDING 신청 (타입이 달라 중복 허용)

//	    List<RecordApplyResponseDTO> list = service.getApplyList(ENROLLED_STUDENT_NO);
//
//	    assertNotNull(list, "목록은 null이 아님");
//	    assertEquals(2, list.size(), "DROP과 REST 신청 총 2건이 조회");
//
//	    assertTrue(list.stream().anyMatch(r -> "DROP".equals(r.getRecordChangeCd())));
//	    assertTrue(list.stream().anyMatch(r -> "REST".equals(r.getRecordChangeCd())));
	}

	@Test
	void testCancelApply() {
		// PENDING 상태의 자퇴 신청 생성
		RecordApplyRequestDTO requestDTO = new RecordApplyRequestDTO();
		requestDTO.setStudentNo(ENROLLED_STUDENT_NO);
		requestDTO.setUnivDeptCd(DEPT_CD);
		requestDTO.setRecordChangeCd("DROP");
		requestDTO.setApplyReason("취소 예정인 자퇴 신청");
		String applyId = service.applyRecord(requestDTO, STUDENT_USER_ID);

		assertDoesNotThrow(() -> {
			service.cancelApply(applyId, ENROLLED_STUDENT_NO);
		}, "신청 취소 시 예외가 발생 X");

		RecordApplyResponseDTO result = recordApplyMapper.selectApplyDetail(applyId);
		assertNull(result, "신청 취소 후 DB에서 해당 신청 정보는 삭제");
	}

}
