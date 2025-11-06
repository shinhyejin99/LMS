package kr.or.jsu.mybatis.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.RecordApplyInfoDTO;
import kr.or.jsu.dto.RecordApplyResponseDTO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class RecordApplyMapperTest {

    @Autowired
    private RecordApplyMapper recordApplyMapper;
    
    @Autowired
    private ApprovalMapper approvalMapper;  // 승인 Mapper 추가!

    private RecordApplyInfoDTO setupRecordApplyInfo;

    // 테스트용 상수 데이터
    private final String TEST_STUDENT_NO = "202500001"; // 김새봄
    private final String TEST_DEPT_CD = "DEP-ENGI-CSE"; // 컴퓨터공학과
    private final String RECORD_CHANGE_REST = "REST"; // 휴학 코드
    private final String RECORD_CHANGE_DROP = "DROP"; // 자퇴 코드
    private final String PENDING_STATUS_CD = "PENDING";
    private final String APPROVED_STATUS_CD = "APPROVED";

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 공통 신청 정보 객체 설정
        setupRecordApplyInfo = new RecordApplyInfoDTO();
        setupRecordApplyInfo.setStudentNo(TEST_STUDENT_NO);
        setupRecordApplyInfo.setRecordChangeCd(RECORD_CHANGE_REST); // 휴학 신청
        setupRecordApplyInfo.setDisireTerm("2025_REG2");
        setupRecordApplyInfo.setApplyReason("개인 사정으로 인한 휴학");
        setupRecordApplyInfo.setAttachFileId(null);
        
        // 승인 라인
        String approvalId = createTestApprovalLine();
        setupRecordApplyInfo.setApprovalLine(approvalId);
    }
    
    /**
     * 테스트용 승인 라인 생성
     */
    private String createTestApprovalLine() {
        // 1. 지도교수 USER_ID 조회
        String professorUserId = recordApplyMapper.selectProfessorUserId(TEST_STUDENT_NO);
        
        if (professorUserId == null) {
            throw new RuntimeException("지도교수 정보를 찾을 수 없습니다.");
        }
        
        // 2. 첫 번째 승인자 생성 (지도교수)
        Map<String, Object> approval = new HashMap<>();
        approval.put("PREV_APPROVE_ID", null);
        approval.put("USER_ID", professorUserId);
        approval.put("APPROVE_YNNULL", null);
        
        approvalMapper.insertApproval(approval);
        
        String approvalId = (String) approval.get("APPROVE_ID");
        log.info("생성된 승인 라인 ID: {}", approvalId);
        
        return approvalId;
    }

    @Test
    void testInsertRecordApply() {

        log.info(">>>> INSERT 시도 ApprovalLine: {}", setupRecordApplyInfo.getApprovalLine());
    	
        // 신청 등록
        int insertedCount = recordApplyMapper.insertRecordApply(setupRecordApplyInfo);

        // 등록 성공 및 생성된 APPLY_ID 확인
        assertThat(insertedCount).isEqualTo(1);
        String generatedApplyId = setupRecordApplyInfo.getApplyId();

        assertThat(generatedApplyId)
            .isNotNull()
            .startsWith("APPLY");

        log.info("생성된 신청 ID: {}", generatedApplyId);
    }

    @Test
    void testSelectApplyDetail() {
        //  먼저 신청 데이터 등록
        recordApplyMapper.insertRecordApply(setupRecordApplyInfo);
        String applyId = setupRecordApplyInfo.getApplyId();

        //  상세 조회
        RecordApplyResponseDTO result = recordApplyMapper.selectApplyDetail(applyId);

        //  조회 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getApplyId()).isEqualTo(applyId);
        assertThat(result.getStudentName()).isNotNull();
        assertThat(result.getRecordChangeName()).isNotNull();
        assertThat(result.getApplyStatusName()).isNotNull();
        
        log.info("조회된 신청 정보: {}", result);
    }

    @Test
    void testSelectApplyListByStudent() {
        // 같은 학생으로 여러 건 신청 등록
        // 1. 휴학 신청
        recordApplyMapper.insertRecordApply(setupRecordApplyInfo);
        
        // 2. 자퇴 신청
        RecordApplyInfoDTO dropApply = new RecordApplyInfoDTO();
        dropApply.setStudentNo(TEST_STUDENT_NO);
        dropApply.setRecordChangeCd(RECORD_CHANGE_DROP);
        dropApply.setApplyReason("자퇴 사유");
        dropApply.setApprovalLine(createTestApprovalLine()); // 승인 라인 생성!
        recordApplyMapper.insertRecordApply(dropApply);

        // 학생의 신청 목록 조회
        List<RecordApplyResponseDTO> applyList = recordApplyMapper.selectApplyListByStudent(TEST_STUDENT_NO);

        // 목록 조회 결과 검증
        assertThat(applyList)
            .isNotNull()
            .hasSizeGreaterThanOrEqualTo(2);
        
        log.info("조회된 신청 목록 개수: {}", applyList.size());
        applyList.forEach(apply -> log.info("신청: {}", apply));
    }

    @Test
    void testDeleteApply() {
        // 신청 등록
        recordApplyMapper.insertRecordApply(setupRecordApplyInfo);
        String applyId = setupRecordApplyInfo.getApplyId();

        // PENDING 상태일 때 삭제
        int deletedCount = recordApplyMapper.deleteApply(applyId);

        // 삭제 성공
        assertThat(deletedCount).isEqualTo(1);

        // 삭제된 데이터 조회
        RecordApplyResponseDTO deletedApply = recordApplyMapper.selectApplyDetail(applyId);

        // 조회되지 않음
        assertThat(deletedApply).isNull();
    }

    @Test
    void testDeleteApply_NotPending() {
        // 신청 등록 후 상태를 APPROVED로 변경
        recordApplyMapper.insertRecordApply(setupRecordApplyInfo);
        String applyId = setupRecordApplyInfo.getApplyId();
        recordApplyMapper.updateApplyStatus(applyId, APPROVED_STATUS_CD);

        // APPROVED 상태일 때 삭제 시도
        int deletedCount = recordApplyMapper.deleteApply(applyId);

        // 삭제 실패 (PENDING이 아니므로)
        assertThat(deletedCount).isEqualTo(0);

        // 데이터 조회
        RecordApplyResponseDTO apply = recordApplyMapper.selectApplyDetail(applyId);

        // 여전히 존재함
        assertThat(apply).isNotNull();
    }

    @Test
    void testUpdateApplyStatus() {
        // 신청 등록
        recordApplyMapper.insertRecordApply(setupRecordApplyInfo);
        String applyId = setupRecordApplyInfo.getApplyId();

        // 상태를 APPROVED로 변경
        int updatedCount = recordApplyMapper.updateApplyStatus(applyId, APPROVED_STATUS_CD);

        // 업데이트 성공
        assertThat(updatedCount).isEqualTo(1);

        // 상세 조회
        RecordApplyResponseDTO updated = recordApplyMapper.selectApplyDetail(applyId);

        // 상태가 변경되었는지 확인
        assertThat(updated.getApplyStatusCd()).isEqualTo(APPROVED_STATUS_CD);
        
        log.info("변경된 상태: {} -> {}", PENDING_STATUS_CD, updated.getApplyStatusCd());
    }

    @Test
    void testSelectStudentStatus() {
        // 학생의 현재 학적 상태 조회
        String status = recordApplyMapper.selectStudentStatus(TEST_STUDENT_NO);

        // 상태가 조회됨
        assertThat(status)
            .isNotNull()
            .isNotEmpty();
        
        log.info("학생 {} 의 학적 상태: {}", TEST_STUDENT_NO, status);
    }

    @Test
    void testCountPendingApply() {
        // 같은 타입으로 PENDING 상태 신청 2건 등록
        recordApplyMapper.insertRecordApply(setupRecordApplyInfo);
        
        RecordApplyInfoDTO secondApply = new RecordApplyInfoDTO();
        secondApply.setStudentNo(TEST_STUDENT_NO);
        secondApply.setRecordChangeCd(RECORD_CHANGE_REST);
        secondApply.setApplyReason("두 번째 휴학 신청");
        secondApply.setApprovalLine(createTestApprovalLine());
        recordApplyMapper.insertRecordApply(secondApply);

        // 진행중인 휴학 신청 개수 조회
        int count = recordApplyMapper.countPendingApply(TEST_STUDENT_NO, RECORD_CHANGE_REST);

        // 2건 이상
        assertThat(count).isGreaterThanOrEqualTo(2);
        
        log.info("학생 {} 의 진행중인 {} 신청 개수: {}", TEST_STUDENT_NO, RECORD_CHANGE_REST, count);
    }

    @Test
    void testCountPendingApply_DifferentType() {
        // 휴학 신청 등록
        recordApplyMapper.insertRecordApply(setupRecordApplyInfo);

        // 자퇴 신청 개수 조회 (다른 타입)
        int count = recordApplyMapper.countPendingApply(TEST_STUDENT_NO, RECORD_CHANGE_DROP);

        // 0건 (휴학만 등록했으므로)
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testSelectProfessorUserId() {
        // 학생의 지도교수 USER_ID 조회
        String professorUserId = recordApplyMapper.selectProfessorUserId(TEST_STUDENT_NO);

        // 지도교수 USER_ID가 조회됨
        assertThat(professorUserId)
            .isNotNull()
            .startsWith("USER");
        
        log.info("학생 {} 의 지도교수 USER_ID: {}", TEST_STUDENT_NO, professorUserId);
    }

//    @Test
    void testSelectDeptHeadUserId() {
        // When: 학과장 USER_ID 조회
        String deptHeadUserId = recordApplyMapper.selectDeptHeadUserId(TEST_DEPT_CD);

        // Then: 학과장 USER_ID가 조회됨
        assertThat(deptHeadUserId)
            .isNotNull()
            .startsWith("USER");
        
        log.info("학과 {} 의 학과장 USER_ID: {}", TEST_DEPT_CD, deptHeadUserId);
    }

    @Test
    void testSelectDeptHeadUserId_NotExists() {
        // 존재하지 않는 학과 코드
        String invalidDeptCd = "INVALID_DEPT";
        // 학과장 조회
        String deptHeadUserId = recordApplyMapper.selectDeptHeadUserId(invalidDeptCd);
        assertThat(deptHeadUserId).isNull();
    }
    
    /**
     * 승인 라인 생성 통합 테스트
     */
    @Test
    void testCreateApprovalLine() {
        // 승인 라인 생성
        String approvalLineId = createTestApprovalLine();
        
        // 승인 라인 ID 생성 확인
        assertThat(approvalLineId)
            .isNotNull()
            .startsWith("APPR");
        
        log.info("생성된 승인 라인 ID: {}", approvalLineId);
    }
}