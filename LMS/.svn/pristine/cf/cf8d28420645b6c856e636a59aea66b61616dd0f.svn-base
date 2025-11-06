package kr.or.jsu.portal.service.notice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.vo.PortalNoticeVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 정태일
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	정태일	          최초 생성
 *
 * </pre>
 */
@Transactional
@SpringBootTest
@Slf4j
class PortalNoticeServiceImplTest {

    @Autowired
    private PortalNoticeService service;

    /** 각 테스트에서 공통으로 사용할 테스트 공지의 PK 보관 */
    private String testNoticeId;

    @BeforeEach
    void setup() {
        // 사전 1건 삽입 (selectKey BEFORE로 noticeId 자동 생성)
        PortalNoticeVO vo = new PortalNoticeVO();
        vo.setStaffNo("2023001");
        vo.setNoticeTypeCd("GENERAL");
        vo.setTitle("서비스-사전데이터-" + System.nanoTime()); // 유니크 보장
        vo.setContent("서비스 테스트용 사전 데이터");
        vo.setDeleteYn("N");
        vo.setCreateAt(LocalDateTime.now());

        int cnt = service.createPortalNotice(vo);
        assertEquals(1, cnt, "사전 insert 실패");
        assertNotNull(vo.getNoticeId(), "insert 후 noticeId 미세팅 (selectKey 매핑 확인 필요)");
        this.testNoticeId = vo.getNoticeId();

        log.info("사전 삽입된 테스트 공지 PK = {}", testNoticeId);
    }

    @Test
    @DisplayName("공지 등록")
    void testCreatePortalNotice() {
        PortalNoticeVO vo = new PortalNoticeVO();
        vo.setStaffNo("2023001");
        vo.setNoticeTypeCd("GENERAL");
        vo.setTitle("서비스-등록-" + System.nanoTime());
        vo.setContent("서비스 계층 등록 테스트");
        vo.setDeleteYn("N");
        vo.setCreateAt(LocalDateTime.now());

        int cnt = assertDoesNotThrow(() -> service.createPortalNotice(vo), "create 중 예외 발생");
        assertEquals(1, cnt, "create 결과 카운트 불일치");
        assertNotNull(vo.getNoticeId(), "create 이후 noticeId 미세팅");

        // 등록 즉시 상세로 검증
        PortalNoticeVO created = service.readPortalNoticeDetail(vo.getNoticeId());
        assertNotNull(created, "등록 후 상세 null");
        assertEquals(vo.getTitle(), created.getTitle());
        assertEquals(vo.getContent(), created.getContent());
    }

    @Test
    @DisplayName("공지 목록 조회")
    void testReadPortalNoticeList() {
//        Object list = assertDoesNotThrow(() -> service.readPortalNoticeList(), "목록 조회 중 예외 발생");
//        assertNotNull(list, "목록 null");
//        assertTrue(list.size() > 0, "공지사항 목록이 비어있습니다.");
    }

    @Test
    @DisplayName("공지 상세 조회")
    void testReadPortalNoticeDetail() {
    	PortalNoticeVO detail = service.readPortalNoticeDetail("N0000000000011 ");
    	
    	log.info("공지상세 : {}", PrettyPrint.pretty(detail));
    	
//        assertNotNull(testNoticeId, "testNoticeId 미세팅");
//        PortalNoticeVO detail = assertDoesNotThrow(
//            () -> service.readPortalNoticeDetail(testNoticeId),
//            "상세 조회 중 예외 발생"
//        );
//        assertNotNull(detail, "상세 결과 null");
//        assertEquals(testNoticeId, detail.getNoticeId(), "상세 PK 불일치");
    }

    @Test
    @DisplayName("공지 수정")
    void testModifyPortalNotice() {
        assertNotNull(testNoticeId, "testNoticeId 미세팅");

        // 대상 존재 확인
        PortalNoticeVO before = service.readPortalNoticeDetail(testNoticeId);
        assertNotNull(before, "업데이트 대상 행이 없습니다.");

        // 수정
        PortalNoticeVO updateVO = new PortalNoticeVO();
        updateVO.setNoticeId(testNoticeId);
        updateVO.setStaffNo("2023001");
        updateVO.setNoticeTypeCd("GENERAL");
        updateVO.setTitle("서비스-수정된 제목");
        updateVO.setContent("서비스-수정된 내용입니다.");
        updateVO.setModifyAt(LocalDateTime.now());
        updateVO.setDeleteYn("N");

        int cnt = assertDoesNotThrow(() -> service.modifyPortalNotice(updateVO), "update 중 예외 발생");
        assertEquals(1, cnt, "update 결과 카운트 불일치");

        // 조회로 검증 (update 재호출 금지)
        PortalNoticeVO after = service.readPortalNoticeDetail(testNoticeId);
        assertNotNull(after);
        assertEquals("서비스-수정된 제목", after.getTitle());
        assertEquals("서비스-수정된 내용입니다.", after.getContent());
    }

    @Test
    @DisplayName("공지 삭제 (Soft delete)")
    void testRemovePortalNotice() {
        // 이 테스트 전용 데이터 생성
        PortalNoticeVO vo = new PortalNoticeVO();
        vo.setStaffNo("2023001");
        vo.setNoticeTypeCd("GENERAL");
        vo.setTitle("서비스-삭제대상-" + System.nanoTime());
        vo.setContent("서비스 삭제 테스트용");
        vo.setDeleteYn("N");
        vo.setCreateAt(LocalDateTime.now());
        assertEquals(1, service.createPortalNotice(vo), "삭제 전 insert 실패");
        assertNotNull(vo.getNoticeId());

        int cnt = assertDoesNotThrow(() -> service.removePortalNotice(vo.getNoticeId()), "delete 중 예외 발생");
        assertEquals(1, cnt, "delete 결과 카운트 불일치");

        // Soft delete 검증: DELETE_YN='Y'
        PortalNoticeVO after = service.readPortalNoticeDetail(vo.getNoticeId());
        assertNotNull(after, "Soft delete라면 레코드는 남아야 합니다.");
        assertEquals("Y", after.getDeleteYn(), "DELETE_YN이 'Y'로 변경되어야 합니다.");
    }
}
