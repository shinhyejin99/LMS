//package kr.or.jsu.mybatis.mapper;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.time.LocalDateTime;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import kr.or.jsu.core.utils.log.PrettyPrint;
//import kr.or.jsu.vo.PortalNoticeVO;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Transactional
//@SpringBootTest
//class PortalNoticeMapperTest {
//
//	@Autowired
//	PortalNoticeMapper mapper;
//
//	private String testNoticeId; // ← 동적으로 할당
//	
//    @BeforeEach
//    void setup() {
//        
//        PortalNoticeVO vo = new PortalNoticeVO();
//        vo.setStaffNo("2023001");
//        vo.setNoticeTypeCd("GENERAL");
//        vo.setTitle("테스트용 공지");
//        vo.setContent("테스트 데이터입니다.");
//        vo.setDeleteYn("N");
//        vo.setCreateAt(LocalDateTime.now());
//
//        mapper.insertPortalNotice(vo);
//        testNoticeId = vo.getNoticeId();  // 생성된 ID 저장
//    }
//
//    @Test
//    void testInsertPortalNotice() {
//        String newTestId = "N00000009999998";
//        // 사전삭제로 중복방지
//        mapper.deletePortalNotice(newTestId);
//
//        PortalNoticeVO vo = new PortalNoticeVO();
//        vo.setNoticeId(newTestId); // 반드시 세팅!
//        vo.setStaffNo("2023001");
//        vo.setNoticeTypeCd("GENERAL");
//        vo.setTitle("별도 등록 테스트");
//        vo.setContent("새로운 공지사항 등록 테스트");
//        vo.setDeleteYn("N");
//        vo.setCreateAt(LocalDateTime.now());
//
//        int cnt = assertDoesNotThrow(() -> mapper.insertPortalNotice(vo));
//        assertEquals(1, cnt);
//        log.info("Inserted Notice Id = {}", vo.getNoticeId());
//    }
//
//    @Test
//    void testSelectPortalNoticeList() {
//        var list = assertDoesNotThrow(() -> mapper.selectPortalNoticeList());
//        assertNotNull(list);
//        
//        assertTrue(list.size() > 0, "공지사항 목록이 비어있습니다.");
//        log.info("조회된 공지사항 건수 = {}", list.size());
//        log.info("조회된 공지사항 건수 = {}", PrettyPrint.pretty(list));
//    }
//
//    @Test
//    void testSelectPortalNoticeDetail() {
//    	
//    	PortalNoticeVO vo = mapper.selectPortalNoticeDetail("NA0000000000001");
//    	log.info("===================> ,{}", PrettyPrint.pretty(vo));
//		assertNotNull(vo);
//		
//    }
//
//    
//    @Test
//    void testUpdatePortalNotice() {
//        // 1) @BeforeEach에서 insert된 행이 진짜 보이는지 검증
//        assertNotNull(testNoticeId, "testNoticeId 미세팅");
//        PortalNoticeVO before = mapper.selectPortalNoticeDetail(testNoticeId);
//        assertNotNull(before, "업데이트 대상 행이 없습니다. (@Param 미설정 가능성 높음)");
//
//        // 2) 업데이트 1회
//        PortalNoticeVO updateVO = new PortalNoticeVO();
//        updateVO.setNoticeId(testNoticeId);
//        updateVO.setStaffNo("2023001");
//        updateVO.setNoticeTypeCd("GENERAL");
//        updateVO.setTitle("수정된 제목");
//        updateVO.setContent("수정된 내용입니다.");
//        updateVO.setModifyAt(LocalDateTime.now());
//        updateVO.setDeleteYn("N");
//
//        int cnt = assertDoesNotThrow(() -> mapper.updatePortalNotice(updateVO));
//        assertEquals(1, cnt, "update 결과 카운트 불일치");
//
//        // 3) 조회로 검증 (update 재호출 금지!)
//        PortalNoticeVO after = mapper.selectPortalNoticeDetail(testNoticeId);
//        assertNotNull(after);
//        assertEquals("수정된 제목", after.getTitle());
//        assertEquals("수정된 내용입니다.", after.getContent());
//    }
//    
//    
//
//    @Test
//    void testDeletePortalNotice() {
//    	PortalNoticeVO vo = new PortalNoticeVO();
//    	vo.setStaffNo("2023001");
//        vo.setNoticeTypeCd("GENERAL");
//        vo.setTitle("삭제 대상");
//        vo.setContent("삭제 테스트용");
//        vo.setDeleteYn("N");
//        vo.setCreateAt(LocalDateTime.now());
//        assertEquals(1, mapper.insertPortalNotice(vo));
//        assertNotNull(vo.getNoticeId());
//        
//        int cnt = assertDoesNotThrow(() -> mapper.deletePortalNotice(vo.getNoticeId()));
//        assertEquals(1, cnt);
//
//        PortalNoticeVO after = mapper.selectPortalNoticeDetail(vo.getNoticeId());
//        assertNotNull(after);
//        assertEquals("Y", after.getDeleteYn()); // Soft delete 검증
//    }
//
//}
