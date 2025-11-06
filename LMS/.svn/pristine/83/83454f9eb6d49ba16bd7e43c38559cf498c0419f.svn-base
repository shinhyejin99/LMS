package kr.or.jsu.mybatis.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.dto.AffilApplyInfoDTO;
import kr.or.jsu.dto.AffilApplyResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class UnivAffilApplyMapperTest {

    @Autowired
    UnivAffilApplyMapper mapper;

    @Autowired
    private RecordApplyMapper recordApplyMapper;

    @Autowired
    private ApprovalMapper approvalMapper;

    private AffilApplyInfoDTO setupAffilApplyInfo;
    private String generatedApprovalId;

    // 테스트에 사용할 더미 데이터 상수 설정 (DB에 존재하는 값으로 대체해야 함!)
    private final String TEST_STUDENT_NO = "202500001"; // 김새봄 학생
    private final String AFFIL_CHANGE_CD_1 = "MJ_TRF";      // 전과 코드
    private final String AFFIL_CHANGE_CD_2 = "MJ_DBL";      // 복수전공 코드
    private final String APPLY_STATUS_PENDING = "PENDING"; // 신청 대기 상태 코드

    @BeforeEach
    void setUp() {
    	// 1. 유효한 승인 라인 생성 및 ID 저장
        this.generatedApprovalId = createTestApprovalLine();

        // 2. 테스트에 사용할 공통 신청 정보 객체 설정
        setupAffilApplyInfo = new AffilApplyInfoDTO();
        setupAffilApplyInfo.setStudentNo(TEST_STUDENT_NO);
        setupAffilApplyInfo.setTargetDeptCd("DEP-SOC-BASIC");
        setupAffilApplyInfo.setAffilChangeCd(AFFIL_CHANGE_CD_1);
        setupAffilApplyInfo.setApplyReason("테스트 전과 신청 사유.");
        setupAffilApplyInfo.setApplyStatusCd(APPLY_STATUS_PENDING);
        setupAffilApplyInfo.setAttachFileId(null);
        setupAffilApplyInfo.setApplyAt(new java.sql.Date(System.currentTimeMillis()));

        // 3. 생성된 승인 라인 ID 설정
        setupAffilApplyInfo.setApprovalLine(this.generatedApprovalId);

        log.info("setUp 완료. ApprovalLine: {}", this.generatedApprovalId);
    }

    /**
     * 신청 DTO를 복사하고 특정 필드만 변경하는 헬퍼 메소드 (재활용)
     */
    private AffilApplyInfoDTO cloneAndModifyDto(String newAffilChangeCd) {
        AffilApplyInfoDTO dto = new AffilApplyInfoDTO();
        dto.setStudentNo(setupAffilApplyInfo.getStudentNo());
        dto.setTargetDeptCd(setupAffilApplyInfo.getTargetDeptCd());
        dto.setAffilChangeCd(newAffilChangeCd); // 변경된 타입 적용
        dto.setApplyReason("테스트 신청 사유 - " + newAffilChangeCd);
        dto.setApplyStatusCd(APPLY_STATUS_PENDING);
        dto.setAttachFileId(setupAffilApplyInfo.getAttachFileId());
        dto.setApprovalLine(createTestApprovalLine()); // 새로운 승인 라인 생성
        dto.setApplyAt(new java.sql.Date(System.currentTimeMillis()));
        return dto;
    }

    /**
     * 테스트용 승인 라인 생성 및 반환
     */
    private String createTestApprovalLine() {
        // 1. 지도교수 USER_ID 조회
        String professorUserId = recordApplyMapper.selectProfessorUserId(TEST_STUDENT_NO);

        if (professorUserId == null) {
            throw new RuntimeException("지도교수 정보를 찾을 수 없습니다. (DB 설정 확인 필요)");
        }

        // 2. 승인 정보 Map 설정 (selectKey를 통해 APPROVE_ID가 Map에 담김)
        Map<String, Object> approval = new HashMap<>();
        approval.put("PREV_APPROVE_ID", null);
        approval.put("USER_ID", professorUserId);
        approval.put("APPROVE_YN", null);
        approval.put("APPLY_TYPE_CD", "PENDING");
        approval.put("APPLICANT_USER_ID", "USER00000000016");

        approvalMapper.insertApproval(approval);

        String approvalId = (String) approval.get("APPROVE_ID");
        return approvalId;
    }


    // ----------------------------------------------------------------------------------
    // 1. 등록 (INSERT) 테스트
    // ----------------------------------------------------------------------------------

    @Test
    void testInsertAffilApply() {

        int cnt = mapper.insertAffilApply(setupAffilApplyInfo);

        assertThat(cnt).isEqualTo(1);
        assertNotNull(setupAffilApplyInfo.getApplyId(), "APPLY_ID는 selectKey에 의해 생성되어야 함");
        log.info("생성된 APPLY_ID: {}", setupAffilApplyInfo.getApplyId());

        AffilApplyResponseDTO detail = mapper.selectApplyDetail(setupAffilApplyInfo.getApplyId());
        assertThat(detail.getStudentNo()).isEqualTo(TEST_STUDENT_NO);
    }

    // ----------------------------------------------------------------------------------
    // 2. 조회 (SELECT) 테스트
    // ----------------------------------------------------------------------------------

    @Test
    void testSelectApplyListByStudent() {
        mapper.insertAffilApply(setupAffilApplyInfo);
        mapper.insertAffilApply(cloneAndModifyDto(AFFIL_CHANGE_CD_2));

        List<AffilApplyResponseDTO> list = mapper.selectApplyListByStudent(TEST_STUDENT_NO);

        assertThat(list).isNotNull();
        assertThat(list.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testSelectApplyDetail() {

        mapper.insertAffilApply(setupAffilApplyInfo);
        String generatedApplyId = setupAffilApplyInfo.getApplyId();

        AffilApplyResponseDTO detail = mapper.selectApplyDetail(generatedApplyId);

        assertNotNull(detail, "신청 상세 데이터가 조회되어야 합니다.");
        assertThat(detail.getApplyId()).isEqualTo(generatedApplyId);
        assertThat(detail.getAffilChangeName()).isNotNull();
        assertThat(detail.getTargetDeptName()).isNotNull();
        assertThat(detail.getStudentName()).isNotNull();
    }

    // ----------------------------------------------------------------------------------
    // 3. 삭제 (DELETE) 및 중복 확인 (COUNT) 테스트
    // ----------------------------------------------------------------------------------

    @Test
    void testDeleteApply() {
        mapper.insertAffilApply(setupAffilApplyInfo);
        String applyIdToDelete = setupAffilApplyInfo.getApplyId();

        int deletedCount = mapper.deleteApply(applyIdToDelete);

        assertThat(deletedCount).isEqualTo(1);

        AffilApplyResponseDTO detail = mapper.selectApplyDetail(applyIdToDelete);
        assertThat(detail).isNull();
        log.info("신청 ID {} 삭제 완료 및 확인.", applyIdToDelete);
    }

    @Test
    void testCountPendingApply() {
        mapper.insertAffilApply(setupAffilApplyInfo);


        AffilApplyInfoDTO secondApply = cloneAndModifyDto(AFFIL_CHANGE_CD_1);
        mapper.insertAffilApply(secondApply);

        int count = mapper.countPendingApply(TEST_STUDENT_NO, AFFIL_CHANGE_CD_1);

        assertThat(count).isGreaterThanOrEqualTo(2);

        int countOtherType = mapper.countPendingApply(TEST_STUDENT_NO, "NON_EXIST");
        assertThat(countOtherType).isEqualTo(0);
        log.info("진행 중인 {} 신청 건수: {}", AFFIL_CHANGE_CD_1, count);
    }

    // 중복 확인 테스트
    @Test
    void testSelectSameColleageDepts() {
        List<UnivDeptInfo> depts = mapper.selectSameCollegeDepts(TEST_STUDENT_NO);

        assertThat(depts).isNotNull();
        assertThat(depts.size()).isGreaterThan(0);

        String studentCurrentDeptCd = "DEP-ENGI-CSE";
        assertThat(depts).noneMatch(dept -> dept.getUnivDeptCd().equals(studentCurrentDeptCd));

        depts.forEach(dept -> {
            assertNotNull(dept.getUnivDeptCd(), "학과 코드는 Null이 아니어야 합니다.");
            assertNotNull(dept.getCollegeName(), "단과대학 이름은 Null이 아니어야 합니다.");
        });

        log.info("같은 단과대학 내 조회된 학과 수: {}", depts.size());
    }

    @Test
    void testSelectAllDepts() {
        List<UnivDeptInfo> allDepts = mapper.selectAllDepts(TEST_STUDENT_NO);

        assertThat(allDepts).isNotNull();
        assertThat(allDepts.size()).isGreaterThan(1);
        String studentCurrentDeptCd = "DEP-ENGI-CSE";
        assertThat(allDepts).noneMatch(dept -> dept.getUnivDeptCd().equals(studentCurrentDeptCd));

        log.info("학생 본인 학과를 제외하고 조회된 전체 학과 수: {}", allDepts.size());
    }

}
