package kr.or.jsu.portal.service.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.paging.SimpleSearch;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.dto.SchRecruitDetailDTO;
import kr.or.jsu.mybatis.mapper.PortalRecruitMapper;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.PortalRecruitVO;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 정태일
 * @since 2025. 9. 26.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 26.     	김수현	          조회 서비스 테스트 추가
 *  2025. 9. 29.		김수현			조회 테스트 수정(페이징, 검색)
 *
 * </pre>
 */
@Transactional
@SpringBootTest
@Slf4j
class PortalJobServiceImplTest {

	@Autowired
	private PortalRecruitMapper mapper;
	@Autowired
	private PortalJobService service;
	@Autowired
	private FilesUploadService fileService;

	private static final String TEST_RECRUIT_ID = "SCRECR000000030";

    // 조회수 증가 테스트 위한
    @BeforeEach
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

//        service.createSchRecruit(vo);
    }

	@Test
	void testCreateSchRecruit() {

		// DB 메타데이터
		List<FileDetailVO> mockFileDetails = Collections.singletonList(new FileDetailVO());
        mockFileDetails.get(0).setSaveName(UUID.randomUUID().toString());
        mockFileDetails.get(0).setOriginName("test_file.pdf");
        mockFileDetails.get(0).setFileSize(1024L);
        mockFileDetails.get(0).setExtension("pdf");
        mockFileDetails.get(0).setFileOrder(1);
        mockFileDetails.get(0).setSaveDir("D:/LMSFileRepository");

        String uploaderId = "USER00000000002"; // 로그인된 사용자 ID 가정
        String validFileId = fileService.saveAtDB(mockFileDetails, uploaderId, true);

        log.info("====> 부모 File ID 생성 완료: {}", validFileId);

		// 채용정보 VO에 fileId 설정
		PortalRecruitVO vo = new PortalRecruitVO();
		vo.setTitle("JSU대학교 컴퓨터공학과 연구교수 채용 공고");
	    vo.setContent("컴퓨터공학과에서 연구교수를 채용합니다. 자세한 내용은 첨부파일을 확인하세요.");
	    vo.setStfDeptName("컴퓨터공학과");

	    vo.setAttachFileId(validFileId);

	    vo.setViewCnt(0);
	    vo.setRecStartDay(LocalDate.of(2025, 10, 1));
	    vo.setRecEndDay(LocalDate.of(2025, 10, 15));
	    vo.setAgencyName("JSU대학교");
	    vo.setRecTarget("연구교수");
	    vo.setCreateAt(LocalDateTime.now());
	    vo.setDeleteAt(null);

//		assertDoesNotThrow(() -> service.createSchRecruit(vo));
		assertNotNull(vo.getRecruitId(), "등록 후 Recruit ID가 생성되어야 합니다.");

		SchRecruitDetailDTO registeredVo = service.readSchRecruitDetail(vo.getRecruitId());

        assertNotNull(registeredVo, "등록된 레코드를 조회할 수 있어야 합니다.");
        assertThat(registeredVo.getAttachFileId()).isEqualTo(validFileId);

		log.info("====> 등록 검증 완료 - 등록된 Recruilt ID: {}", vo.getRecruitId());
		log.info("====> 등록 검증 완료 - 등록에 사용된 File ID: {}", vo.getAttachFileId());
	}

	@Test
	void testReadSchRecruitList() {

		PaginationInfo<PortalRecruitVO> paging = new PaginationInfo<>(5, 5);
        paging.setCurrentPage(1); // 1페이지 요청

        SimpleSearch search = new SimpleSearch("title", "채용");
        paging.setSimpleSearch(search);

        int totalRecord = mapper.selectTotalRecord(paging);
        paging.setTotalRecord(totalRecord);

        List<PortalRecruitVO> list = service.readSchRecruitList(paging);

        assertDoesNotThrow(() -> service.readSchRecruitList(paging));
        assertNotNull(list);

        log.info("검색 조건에 맞는 총 건수: {}", totalRecord); // 9.29일 시점 11개

        log.info("시작 RNUM: {}", paging.getStartRow()); // 예상 값: 1
        log.info("종료 RNUM: {}", paging.getEndRow());   // 예상 값: 5
        assertEquals(1, paging.getStartRow());

        // 5. 조회된 정보 출력
        log.info("====> 조회된 목록 (1페이지, 5개) : {}", list);



//		assertDoesNotThrow(() -> mapper.selectSchRecruitList());
//		assertNotNull(mapper.selectSchRecruitList());
//		log.info("=====> 조회된 결과 : {}", mapper.selectSchRecruitList());
	}

	@Test
	void testReadSchRecruitDetail() {
		assertDoesNotThrow(() -> service.readSchRecruitDetail("SCRECR000000003"));
		assertNotNull(service.readSchRecruitDetail("SCRECR000000003"));
		log.info("=====> 조회된 결과(상세보기) : {}", service.readSchRecruitDetail("SCRECR000000003"));
	}

//	@Test
	void testModifySchRecruit() {
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

        boolean cnt = service.modifySchRecruit(updateVO);

        assertDoesNotThrow(() -> service.modifySchRecruit(updateVO));
        assertEquals(1, cnt);
        log.info("====> 조회된 결과 : {}", service.modifySchRecruit(updateVO));
	}

//	@Test
	void testRemoveSchRecruit() {
		String recruitId = "SCRECR000000029";
		assertDoesNotThrow(() -> service.removeSchRecruit(recruitId));
		assertEquals(true, service.removeSchRecruit(recruitId));
		log.info("====> 조회된 결과 : {}", service.removeSchRecruit(recruitId));
		log.info("====> 수정된 결과 : {}", service.removeSchRecruit(recruitId)); // null 이 나와야 잘 된 것
	}

	@Test
	void testModifyIncrementViewCount() {
		String recruitId = TEST_RECRUIT_ID;
		int initialViewCnt = service.readSchRecruitDetail(recruitId).getViewCnt();

		boolean updatedRows = service.modifyIncrementViewCount(recruitId);

		// 업데이트된 레코드 수가 1인지 확인
		assertThat(updatedRows).isEqualTo(true);

		// 다시 DB에서 해당 레코드를 조회하여 viewCnt가 1 증가했는지 확인
		SchRecruitDetailDTO updatedVo = service.readSchRecruitDetail(recruitId);
		assertThat(updatedVo.getViewCnt()).isEqualTo(initialViewCnt + 1);
		log.info("====> 초기 조회수: {}, 증가 후 조회수: {}", initialViewCnt, updatedVo.getViewCnt());
	}


}
