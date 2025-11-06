package kr.or.jsu.lms.staff.service.approval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.ApprovalLineRequestDetailDTO;
import kr.or.jsu.mybatis.mapper.ApprovalMapper;
import kr.or.jsu.vo.ApprovalVO;
import lombok.extern.slf4j.Slf4j;

/**
 * StaffApprovalServiceImpl에 대한 단위/통합 테스트 클래스 (JUnit 5 + SpringBootTest)
 */
@Slf4j
@SpringBootTest 
@Transactional 
//@ExtendWith(MockitoExtension.class)
class StaffApprovalServiceImplTest {

//     @InjectMocks 
//    @Autowired 
    private StaffApprovalServiceImpl staffApprovalService;


    @Mock
    private ApprovalMapper mapper; 
    
    private ApprovalLineRequestDetailDTO createDto;
    private ApprovalLineRequestDetailDTO pendingVo;
    private String testApproveId = "APPR00000000002";
    private String testDocId = "DOCU001";

    @BeforeEach
    void setUp() {
        createDto = new ApprovalLineRequestDetailDTO();
        createDto.setUserId("USER00000000001");
        createDto.setDocId(testDocId);

        pendingVo = new ApprovalLineRequestDetailDTO();
        pendingVo.setApproveId(testApproveId);
        pendingVo.setUserId("STAFF001");
        pendingVo.setApproveYnnull(null);
    }

    // ==========================================================
    // 1. 생성 (CREATE) 테스트
    // ==========================================================
    @Test
    @DisplayName("1-1. 결재선 생성 성공 테스트")
    void createStaffApproval_Success() {
        // Given
        when(mapper.insertApproval(any(Map.class))).thenReturn(1);

        // When
        staffApprovalService.createStaffApproval(createDto);

        // Then
        verify(mapper, times(1)).insertApproval(any(Map.class));
    }
    
    @Test
    @DisplayName("1-2. 결재선 생성 실패 테스트: 승인자 ID 누락 시 예외 발생")
    void createStaffApproval_MissingUserId_ThrowsException() {
        // Given
        createDto.setUserId(null); 

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            staffApprovalService.createStaffApproval(createDto);
        });
        verify(mapper, never()).insertApproval(any());
    }

    // ==========================================================
    // 2. 조회 (READ) 테스트
    // ==========================================================
    @Test
    @DisplayName("2-1. 결재 목록 조회 성공 테스트 (전체 조회)")
    void readStaffApprovalList_Success() {
        // Given
        // Service 메서드가 Map<String, Object>를 인자로 받으므로, Map을 준비합니다.
        PaginationInfo<Map<String, Object>> pagingInfo = new PaginationInfo<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pagingInfo", pagingInfo); // PaginationInfo를 Map에 담아 전달

        // Service 코드와 Mapper의 메서드 이름을 'selectApprovalCount'로 통일한다고 가정하고 수정합니다.
        // Mockito 인자는 Service에서 실제로 호출될 인자 타입인 Map.class로 변경합니다.
        
        // 1. Total Count를 Mocking: 50건을 반환하도록 설정
        // ⭐️ any(Map.class)로 인자 타입을 변경합니다.
        when(mapper.selectApprovalCount(any(Map.class))).thenReturn(50);
        
        // 2. List 조회를 Mocking: 비어있지 않은 List를 반환하도록 설정
        // ⭐️ any(Map.class)로 인자 타입을 변경합니다.
        when(mapper.selectApprovalList(any(Map.class))).thenReturn(List.of(
            Map.of("APPROVE_ID", "AP00000001", "APPLICANT_NAME", "홍길동")
        ));

        // When
        // Service 메서드 호출 시 Map 객체를 전달합니다.
        List<Map<String, Object>> resultList = staffApprovalService.readStaffApprovalList(paramMap);

        // Then
        // 1. 결과 목록이 비어있지 않은지 확인
        assertFalse(resultList.isEmpty(), "결과 목록은 비어있지 않아야 합니다.");
        
        // 2. Total Count가 PaginationInfo에 올바르게 설정되었는지 확인
        // (PagingInfo는 paramMap에서 가져와 업데이트되었으므로, original pagingInfo 객체를 확인합니다)
        assertEquals(50, pagingInfo.getTotalRecord(), "총 레코드 수는 50이어야 합니다.");
        
        // 3. Mapper 메서드 호출 횟수 검증 (Count와 List가 각각 1번씩 호출)
        // ⭐️ verify 인자도 any(Map.class)로 변경합니다.
        verify(mapper, times(1)).selectApprovalCount(any(Map.class));
        verify(mapper, times(1)).selectApprovalList(any(Map.class));
    }
    
    @Test
    @DisplayName("2-2. 결재 상세 조회 성공 테스트 (단건 조회)")
    void readStaffApproval_Success() {
        // Given
        ApprovalLineRequestDetailDTO detailVO = new ApprovalLineRequestDetailDTO();
        detailVO.setApproveId(testApproveId);
        when(mapper.selectApproval(testApproveId)).thenReturn(detailVO);

        // When
        ApprovalLineRequestDetailDTO resultVO = staffApprovalService.readStaffApproval(testApproveId);

        // Then
        assertNotNull(resultVO);
        assertEquals(testApproveId, resultVO.getApproveId());
    }

    @Test
    @DisplayName("2-3. 결재 상세 조회 실패 테스트 - 데이터 없음")
    void readStaffApproval_NotFound_ThrowsException() {
        // Given
        when(mapper.selectApproval("NONEXISTID")).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            staffApprovalService.readStaffApproval("NONEXISTID");
        });
    }
    
    // ==========================================================
    // 3. 수정 (MODIFY) 테스트
    // ==========================================================
    
    // DOC_ID를 포함하는 Mock Map 생성
    private Map<String, Object> getDocInfoMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("DOC_ID", testDocId);
        return map;
    }

    @Test
    @DisplayName("3-1. 최종 결재자 승인 성공 테스트: 다음 결재선 없음 -> 최종 승인")
    void modifyStaffApproval_FinalApprover_Success() {
        // Given
        when(mapper.selectApproval(testApproveId)).thenReturn(pendingVo);
        when(mapper.selectApprovalWithDocId(testApproveId)).thenReturn(getDocInfoMap()); // ⭐️ DocId 조회 Mock 추가
        when(mapper.updateApproval(any(ApprovalVO.class))).thenReturn(1);
        when(mapper.selectNextApprovalLines(testApproveId)).thenReturn(Collections.emptyList());
        
        ApprovalLineRequestDetailDTO approveDto = new ApprovalLineRequestDetailDTO();
        approveDto.setApproveId(testApproveId);
        approveDto.setApproveYnnull("Y"); 
        approveDto.setDocId(testDocId);
        
        // When
        staffApprovalService.modifyStaffApproval(approveDto);
        
        // Then
        verify(mapper, times(1)).selectApprovalWithDocId(eq(testApproveId));
        verify(mapper, times(1)).updateApproval(argThat(vo -> "Y".equals(vo.getApproveYnnull())));
        verify(mapper, times(1)).selectNextApprovalLines(eq(testApproveId));
    }

    @Test
    @DisplayName("3-2. 결재선 반려 처리 성공 테스트")
    void modifyStaffApproval_Reject_Success() {
        // Given
        when(mapper.selectApproval(testApproveId)).thenReturn(pendingVo);
        when(mapper.selectApprovalWithDocId(testApproveId)).thenReturn(getDocInfoMap()); // ⭐️ DocId 조회 Mock 추가
        when(mapper.updateApproval(any(ApprovalVO.class))).thenReturn(1);
        
        ApprovalLineRequestDetailDTO rejectDto = new ApprovalLineRequestDetailDTO();
        rejectDto.setApproveId(testApproveId);
        rejectDto.setApproveYnnull("N"); // 반려
        rejectDto.setDocId(testDocId);
        rejectDto.setComments(null);
        
        // When
        staffApprovalService.modifyStaffApproval(rejectDto);

        // Then
        verify(mapper, times(1)).selectApprovalWithDocId(eq(testApproveId));
        verify(mapper).updateApproval(argThat(vo -> "N".equals(vo.getApproveYnnull())));
        verify(mapper, never()).selectNextApprovalLines(any());
    }
    
    @Test
    @DisplayName("3-3. 결재 처리 실패 테스트: 이미 승인된 건 재처리 시 예외 발생")
    void modifyStaffApproval_AlreadyProcessed_ThrowsException() {
        // Given
        pendingVo.setApproveYnnull("Y"); // 이미 처리된 상태
        when(mapper.selectApproval(testApproveId)).thenReturn(pendingVo);
        
        ApprovalLineRequestDetailDTO approveDto = new ApprovalLineRequestDetailDTO();
        approveDto.setApproveId(testApproveId);
        approveDto.setApproveYnnull("Y");
        approveDto.setComments(null);
        
        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            staffApprovalService.modifyStaffApproval(approveDto);
        });
        
        verify(mapper, never()).updateApproval(any());
    }
}