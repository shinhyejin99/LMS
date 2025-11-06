package kr.or.jsu.mybatis.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.paging.SimpleSearch;
import kr.or.jsu.vo.PortalRecruitVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 송태호
 * @since 2025. 9. 29.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 29.     	김수현	         조회수 테스트 추가, 조회 메소드 수정(페이징, 검색), 파일 업로드
 *
 * </pre>
 */
@Slf4j
@Transactional
@SpringBootTest
class PortalRecruitMapperTest {

	@Autowired
	private PortalRecruitMapper mapper;
	
    private static final String TEST_RECRUIT_ID = "SCRECR000000030"; 
	
    // 조회수 증가 테스트 위한
//    @BeforeEach
    void setUp() {
        // 테스트 레코드를 DB에 삽입하여 독립적인 환경을 구축합니다.
        PortalRecruitVO vo = new PortalRecruitVO();
        vo.setRecruitId(TEST_RECRUIT_ID);
        vo.setTitle("조회수 테스트 레코드");
        vo.setContent("조회수 테스트용입니다.");
        vo.setStfDeptName("테스트부서");
        vo.setViewCnt(10); // ⭐️ 초기 조회수를 10으로 설정 (0이 아닌 값으로 테스트)
        vo.setRecStartDay(LocalDate.of(2025, 10, 1));
        vo.setRecEndDay(LocalDate.of(2025, 10, 15));
        vo.setAgencyName("테스트기관");
        vo.setRecTarget("테스트대상");
        vo.setCreateAt(LocalDateTime.now());
        
        mapper.insertSchRecruit(vo); 
    }
    
    
	@Test
	void testInsertSchRecruit() {

		PortalRecruitVO vo = new PortalRecruitVO();
		vo.setTitle("JSU대학교 컴퓨터공학과 연구교수 채용 공고");
	    vo.setContent("컴퓨터공학과에서 연구교수를 채용합니다. 자세한 내용은 첨부파일을 확인하세요.");
	    vo.setStfDeptName("컴퓨터공학과");

        // Mapper 테스트 : 부모 키를 직접 생성할 수 없음
	    vo.setAttachFileId(null); 
	    
	    vo.setViewCnt(0);
	    vo.setRecStartDay(LocalDate.of(2025, 10, 1));
	    vo.setRecEndDay(LocalDate.of(2025, 10, 15));
	    vo.setAgencyName("JSU대학교");
	    vo.setRecTarget("연구교수");
	    vo.setCreateAt(LocalDateTime.now());
	    vo.setDeleteAt(null);
	    
        assertDoesNotThrow(() -> {
            int cnt = mapper.insertSchRecruit(vo); 
            assertEquals(1, cnt);
        });
        
        // 쿼리가 두 번 실행되는 문제를 수정합니다.
        log.info("====> 등록 완료 (생성된 ID: {})", vo.getRecruitId());

//		assertDoesNotThrow(() -> mapper.insertSchRecruit(vo));
//		assertEquals(1, mapper.insertSchRecruit(vo));
//		log.info("====> cnt : {}", mapper.insertSchRecruit(vo));
	}

	@Test
	void testSelectSchoolRecruitList() {
		
		PaginationInfo<PortalRecruitVO> paging = new PaginationInfo<>(5, 5); 
        paging.setCurrentPage(1); // 1페이지 요청
        
        SimpleSearch search = new SimpleSearch("title", "채용"); 
        paging.setSimpleSearch(search);
        
        int totalRecord = mapper.selectTotalRecord(paging);
        paging.setTotalRecord(totalRecord);
		
        List<PortalRecruitVO> list = mapper.selectSchRecruitList(paging);
        
        assertDoesNotThrow(() -> mapper.selectSchRecruitList(paging));
        assertNotNull(list);
     
        log.info("검색 조건에 맞는 총 건수: {}", totalRecord);
        
        log.info("시작 RNUM: {}", paging.getStartRow()); // 예상 값: 1
        log.info("종료 RNUM: {}", paging.getEndRow());   // 예상 값: 5
        assertEquals(1, paging.getStartRow());
        
        // 5. 조회된 정보 출력
        log.info("====> 조회된 목록 (1페이지, 5개) : {}", list);
        
        
//		assertDoesNotThrow(() -> mapper.selectSchRecruitList());
//		assertNotNull(mapper.selectSchRecruitList());
//		log.info("====> 조회된 정보 : {}", mapper.selectSchRecruitList());
	}
	
//	@Test
	void testSelectSchRecruitDetail() {
		assertDoesNotThrow(() -> mapper.selectSchRecruitDetail("SCRECR000000003"));
		assertNotNull(mapper.selectSchRecruitDetail("SCRECR000000003"));
		log.info("=======> 조회된 정보 : {}", mapper.selectSchRecruitDetail("SCRECR000000003"));
	}
	
//	@Test
	void testUpdateSchRecruit() {
        String recruitId = "SCRECR000000029"; 
        PortalRecruitVO updateVO = new PortalRecruitVO();
        
        updateVO.setRecruitId(recruitId); 
        updateVO.setStfDeptName("교무처_수정");
        updateVO.setTitle("2025학년도 직원 채용 공고 (수정)");
        updateVO.setContent("<h2>수정된 공고 내용입니다.</h2><p>마감일이 변경되었습니다.</p>");
        updateVO.setAgencyName("JSU 대학교");
        updateVO.setRecTarget("일반 직원");
        updateVO.setRecStartDay(LocalDate.of(2025, 10, 1)); 
        updateVO.setRecEndDay(LocalDate.of(2025, 10, 31)); 

        int cnt = mapper.updateSchRecruit(updateVO);
        
        assertDoesNotThrow(() -> mapper.updateSchRecruit(updateVO));
        assertEquals(1, cnt);
        log.info("====> 조회된 결과 : {}", mapper.updateSchRecruit(updateVO));
	}
	
//	@Test
	void testDeleteSchRecruit() {
		String recruitId = "SCRECR000000029";
		assertDoesNotThrow(() -> mapper.deleteSchRecruit(recruitId));
		assertEquals(1, mapper.deleteSchRecruit(recruitId));
		log.info("====> 조회된 결과 : {}", mapper.deleteSchRecruit(recruitId));
		log.info("====> 수정된 결과 : {}", mapper.selectSchRecruitDetail(recruitId)); // null 이 나와야 잘 된 것
		
	}
  
//	@Test
	void testUpdateIncrementViewCount() {
		String recruitId = TEST_RECRUIT_ID;
		int initialViewCnt = mapper.selectSchRecruitDetail(recruitId).getViewCnt();

		int updatedRows = mapper.incrementViewCount(recruitId);
		
		// 업데이트된 레코드 수가 1인지 확인
		assertThat(updatedRows).isEqualTo(1);
		
		// 다시 DB에서 해당 레코드를 조회하여 viewCnt가 1 증가했는지 확인
		PortalRecruitVO updatedVo = mapper.selectSchRecruitDetail(recruitId); 
		assertThat(updatedVo.getViewCnt()).isEqualTo(initialViewCnt + 1);
		log.info("====> 초기 조회수: {}, 증가 후 조회수: {}", initialViewCnt, updatedVo.getViewCnt());
	}
}
