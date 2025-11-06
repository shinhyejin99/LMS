package kr.or.jsu.portal.service.job;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.db.FilesBundleDTO;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.response.FileDetailResp;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.core.utils.job.JobMatchingUtil;
import kr.or.jsu.dto.JobDetailDTO;
import kr.or.jsu.dto.JobListItemDTO;
import kr.or.jsu.dto.SchRecruitDetailDTO;
import kr.or.jsu.mybatis.mapper.PortalRecruitMapper;
import kr.or.jsu.mybatis.mapper.StaffMapper;
import kr.or.jsu.vo.PortalRecruitVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 김수현
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	송태호	          최초 생성
 *  2025. 10. 2.		김수현			게시글 등록에 컨트롤러에서 분리한 파일 처리 추가
 *	2025. 10. 15.		김수현			맞춤형 공고 검색조건 추가
 *	2025. 10. 21.		김수현			공고 마감일 순 정렬 메서드 추가
 *	2025. 10. 31.		김수현			파일 처리 메서드 추가
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortalJobServiceImpl implements PortalJobService {

	private final PortalRecruitMapper mapper;
	private final RestTemplate restTemplate; 	// 웹 애플리케이션이 다른 서버에 있는 REST API를 호출(사용)할 수 있도록 도와주는 도구
	private final XmlMapper xmlMapper; 			// Work24 API의 XML 텍스트를 Java 객체(DTO)로 변환해주는 역할 - jackson 라이브러리 모듈

	private final StaffMapper staffMapper;
	private final DatabaseCache dbCache;
//	private final FilesUploadService filesUploadService;
	private final LMSFilesService filesService;

	@Value("${work24.api.authKey}")
    private String authKey;

    @Value("${work24.api.list.url}")
    private String listApiUrl;

    @Value("${work24.api.detail.url}")
    private String detailApiUrl;

    // 추가함
    @Value("${file-info.repository.path}")
    private String fileRepositoryPath;

	/**
	 * 공공채용 목록 조회
	 */
	@Override
	public List<JobListItemDTO> readPublicRecruitList(PaginationInfo<PortalRecruitVO> paging) {
		try {
            // 검색어 확인
            boolean hasSearch = paging.getSimpleSearch() != null &&
                               paging.getSimpleSearch().getSearchWord() != null &&
                               !paging.getSimpleSearch().getSearchWord().trim().isEmpty();
            String url;

            if (hasSearch) {
                // 검색 시: 최근 300개 가져와서 필터링
                url = String.format(
                    "%s?authKey=%s&callTp=L&returnType=XML&startPage=1&display=300",
                    listApiUrl,
                    authKey
                );
            } else {
                // 검색 안 할 때: 기존처럼 페이징으로 가져오기
                url = String.format(
                    "%s?authKey=%s&callTp=L&returnType=XML&startPage=%d&display=%d",
                    listApiUrl,
                    authKey,
                    paging.getCurrentPage(),
                    paging.getScreenSize()
                );
            }

            log.info("고용24 API 호출 (검색: {}) - {}", hasSearch, url);

            String xmlResponse = restTemplate.getForObject(url, String.class);

            // 1. 빈 응답 체크
            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                log.error("고용24 API 응답이 비어있습니다");
                throw new RuntimeException("API 응답이 비어있습니다");
            }

            // 2. XML 형식인지 확인
            String trimmedResponse = xmlResponse.trim();
            if (!trimmedResponse.startsWith("<")) {
                log.error("고용24 API 응답이 XML 형식이 아닙니다. 응답 내용: {}",
                    trimmedResponse.substring(0, Math.min(100, trimmedResponse.length())));
                throw new RuntimeException("API 응답이 XML 형식이 아닙니다: " + trimmedResponse);
            }

            log.debug("고용24 API XML 응답 수신 성공");

            // 3. XML 파싱
            JobListResponse response = xmlMapper.readValue(xmlResponse, JobListResponse.class);

            // 4. 페이징 정보 설정 (검색 없을 때)
            if (!hasSearch && response.getTotal() != null) {
                paging.setTotalRecord(response.getTotal());
            }

            // 5. DTO 변환 및 검색 필터링
            List<JobListItemDTO> jobList = new ArrayList<>();

            if (response.getJobs() != null) {
                String searchWord = hasSearch ?
                    paging.getSimpleSearch().getSearchWord().toLowerCase().trim() : null;

                for (JobInfoXml job : response.getJobs()) {
                    JobListItemDTO dto = convertToDTO(job);

                    // 검색어가 있으면 OR 검색 (제목 또는 기업명)
                    if (hasSearch) {
                        boolean matches = false;

                        // 제목 또는 기업명 중 하나라도 포함되면 매칭
                        if ((dto.getTitle() != null &&
                             dto.getTitle().toLowerCase().contains(searchWord)) ||
                            (dto.getOrganization() != null &&
                             dto.getOrganization().toLowerCase().contains(searchWord))) {
                            matches = true;
                        }

                        if (!matches) {
                            continue;
                        }
                    }

                    jobList.add(dto);
                }
            }

            // 6. D-Day 최우선 정렬
            sortRecruitList(jobList, JobListItemDTO::getRecEndDay); // 마감일
            log.info("공공채용 목록 D-Day 최우선 정렬");

            // 7. 검색 시 페이징 처리
            if (hasSearch) {
                paging.setTotalRecord(jobList.size());

                int start = (paging.getCurrentPage() - 1) * paging.getScreenSize();
                int end = Math.min(start + paging.getScreenSize(), jobList.size());

                if (start >= jobList.size()) {
                    log.info("공공채용 검색 완료: 해당 페이지에 데이터 없음");
                    return new ArrayList<>();
                }

                jobList = new ArrayList<>(jobList.subList(start, end));
            }

            log.info("공공채용 목록 조회 성공: {} 건", jobList.size());
            return jobList;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("고용24 API 목록 조회 실패", e);
            throw new RuntimeException("API 호출 실패: " + e.getMessage(), e);
        }
	}

	/**
	 * 공공채용 상세조회
	 */
	@Override
    public JobDetailDTO readPublicRecruitDetail(String recruitId) {
		try {
            String url = String.format(
                "%s?authKey=%s&returnType=XML&callTp=D&empSeqno=%s",
                detailApiUrl,
                authKey,
                recruitId
            );

            log.info("고용24 상세 API 호출: {}", url);

            String xmlResponse = restTemplate.getForObject(url, String.class);

            // 1. 빈 응답 체크
            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                log.error("고용24 API 응답이 비어있습니다");
                throw new RuntimeException("API 응답이 비어있습니다");
            }

            // 2. XML 형식인지 확인
            String trimmedResponse = xmlResponse.trim();
            if (!trimmedResponse.startsWith("<")) {
                log.error("고용24 API 응답이 XML 형식이 아닙니다. 응답: {}",
                    trimmedResponse.substring(0, Math.min(100, trimmedResponse.length())));
                throw new RuntimeException("API 응답이 XML 형식이 아닙니다: " + trimmedResponse);
            }

            log.debug("고용24 API XML 응답 수신 성공");

            // 3. XML 파싱
            JobDetailResponse response = xmlMapper.readValue(xmlResponse, JobDetailResponse.class);

            // 4. DTO 변환
            JobDetailDTO dto = convertToDetailDTO(response);

            log.info("고용24 API 상세 조회 성공: {}", recruitId);
            return dto;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("고용24 API 상세 조회 실패", e);
            throw new RuntimeException("API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * JobInfoXml -> JobListItemDTO 변환
     */
    private JobListItemDTO convertToDTO(JobInfoXml job) {
        return JobListItemDTO.builder()
            .recruitId(job.getEmpSeqno())
            .title(job.getEmpWantedTitle())
            .organization(job.getEmpBusiNm())
            .jobType("public")
            .recStartDay(parseDate(job.getEmpWantedStdt()))
            .recEndDay(parseDate(job.getEmpWantedEndt()))
            .employmentType(job.getEmpWantedTypeNm())
            .companyScale(job.getCoClcdNm())
            .logoUrl(job.getRegLogImgNm())
            .detailUrl(job.getEmpWantedHomepgDetail())
            .viewCnt(0)
            .createDate(null)
            .build();
    }

    /**
     * JobDetailResponse -> JobDetailDTO 변환
     */
    private JobDetailDTO convertToDetailDTO(JobDetailResponse response) {
        return JobDetailDTO.builder()
            .recruitId(response.getEmpSeqno())
            .title(response.getEmpWantedTitle())
            .organization(response.getEmpBusiNm())
            .jobType("public")
            .recStartDay(parseDate(response.getEmpWantedStdt()))
            .recEndDay(parseDate(response.getEmpWantedEndt()))
            .employmentType(response.getEmpWantedTypeNm())
            .companyScale(response.getCoClcdNm())
            .logoUrl(response.getRegLogImgNm())
            .homepageUrl(response.getEmpWantedHomepg())
            .recruitUrl(response.getEmpWantedHomepgDetail())
            .recrCommCont(response.getRecrCommCont())
            .empSubmitDocCont(response.getEmpSubmitDocCont())
            .empRcptMthdCont(response.getEmpRcptMthdCont())
            .inqryCont(response.getInqryCont())
            .empnEtcCont(response.getEmpnEtcCont())
            .content(null)
            .viewCnt(0)
            .build();
    }

    /**
     * String(yyyyMMdd) -> LocalDate 변환
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.length() != 8) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateStr, e);
            return null;
        }
    }

    /**
     * 학내 채용정보 등록 - 파일처리도
     */
    @Override
    public boolean createSchRecruit(PortalRecruitVO portalRecruit, List<MultipartFile> files, String uploaderUserId) {
        int result = mapper.insertSchRecruit(portalRecruit);

        // 게시글 등록 성공했으면 파일 처리함
        if (result > 0) {
            // 파일 업로드 및 파일 정보 DB 등록
            if (files != null && !files.isEmpty()) {
                try {
                    // 파일 저장 경로 설정 + 서브 경로 추가
                    FileUploadDirectory directory = FileUploadDirectory.DEVTEMP;
                    String subPath = "/recruit"; // 채용 공고 전용 하위 폴더

                    // 파일 공개 여부 설정 (학내 채용 정보 첨부파일은 비공개(false))
                    boolean isPublic = false;

                    // 1. 파일들을 디스크에 저장 (서브 경로 포함)
                    List<FileDetailInfo> fileMetaDatas = filesService.saveAtDirectory(
                        files,
                        directory,
                        subPath  // 새로 추가된 매개변수
                    );

                    // 2. 파일 메타데이터를 DB에 저장, 파일 묶음 ID(FileId) 반환
                    String fileId = filesService.saveAtDB(fileMetaDatas, uploaderUserId, isPublic);

                    // 3. 파일 ID 업데이트
                    portalRecruit.setAttachFileId(fileId);
                    mapper.updateSchRecruit(portalRecruit);

                    // 4. 파일 사용 상태 활성화 (신규 추가)
                    if (fileId != null) {
                        filesService.changeUsingStatus(fileId, true);
                        log.info("파일 업로드 완료 및 사용 상태 활성화 - recruitId: {}, fileId: {}",
                            portalRecruit.getRecruitId(), fileId);
                    }

                } catch (Exception e) {
                    log.error("채용정보 등록({}) 후 파일 업로드 실패. 트랜잭션 롤백 시작.",
                        portalRecruit.getRecruitId(), e);
                    // 파일 처리 실패 시 런타임 던지기
                    throw new RuntimeException("파일 업로드 처리 중 오류가 발생했습니다.", e);
                }
            }
        }
        return result > 0;
    }

	/**
	 * 학내 채용정보 목록 조회
	 */
	@Override
	public List<PortalRecruitVO> readSchRecruitList(PaginationInfo<PortalRecruitVO> paging) {
		int totalRecord = mapper.selectTotalRecord(paging);
		paging.setTotalRecord(totalRecord);
		List<PortalRecruitVO> schRecruitList = mapper.selectSchRecruitList(paging);
		return schRecruitList;
	}

	/**
	 * 학내 채용정보 상세보기 (파일 정보 포함)
	 */
	@Override
	public SchRecruitDetailDTO readSchRecruitDetail(String recruitId) {
		// 1. 기본 채용 정보 조회
	    PortalRecruitVO portalRecruit = mapper.selectSchRecruitDetail(recruitId);

	    if (portalRecruit == null) {
	        throw new RuntimeException("존재하지 않는 채용 공고입니다.");
	    }

	    // 2. VO => DTO 변환
	    SchRecruitDetailDTO detailDTO = new SchRecruitDetailDTO();
	    BeanUtils.copyProperties(portalRecruit, detailDTO); // ⭐ 한 줄로 끝!

	    // 3. 첨부파일 정보 조회 및 설정
	    String fileId = portalRecruit.getAttachFileId();
	    if (fileId != null && !fileId.trim().isEmpty()) {
	        try {
	            FilesBundleDTO bundle = filesService.readFileBundle(fileId);

	            // FileDetailInfo → FileDetailResp 변환
	            List<FileDetailResp> files = bundle.getFileDetailInfo().stream()
	                .map(f -> {
	                    FileDetailResp resp = new FileDetailResp();
	                    BeanUtils.copyProperties(f, resp);
	                    return resp;
	                }).toList();

	            detailDTO.setAttachFiles(files);

	            log.debug("첨부파일 조회 성공 - recruitId: {}, fileId: {}, 파일 수: {}",
	                recruitId, fileId, files.size());

	        } catch (RuntimeException e) {
	            log.warn("첨부파일 조회 실패 - recruitId: {}, fileId: {}", recruitId, fileId, e);
	            detailDTO.setAttachFiles(null);
	        }
	    }

	    return detailDTO;
	}

	/**
	 * 학내 채용정보 수정하기
	 */
	@Override
	public boolean modifySchRecruit(PortalRecruitVO portalRecruit) {
		return mapper.updateSchRecruit(portalRecruit) > 0;
	}

	/**
	 * 학내 채용정보 삭제하기(날짜만 변경)
	 */
	@Override
	public boolean removeSchRecruit(String recruitId) {
	    // 1. 채용 공고 조회 (파일 ID 가져오기)
	    PortalRecruitVO recruit = mapper.selectSchRecruitDetail(recruitId);

	    if (recruit == null) {
	        log.warn("삭제 시도한 채용 공고가 존재하지 않음 - recruitId: {}", recruitId);
	        return false;
	    }

	    // 2. 첨부파일 비활성화
	    String fileId = recruit.getAttachFileId();
	    if (fileId != null && !fileId.trim().isEmpty()) {
	        try {
	            filesService.changeUsingStatus(fileId, false);
	            log.info("채용 공고 삭제 시 파일 비활성화 완료 - recruitId: {}, fileId: {}",
	                recruitId, fileId);
	        } catch (Exception e) {
	            log.warn("파일 비활성화 실패 - recruitId: {}, fileId: {}", recruitId, fileId, e);
	            // 파일 비활성화 실패해도 게시글 삭제는 진행
	        }
	    }

	    // 3. 게시글 삭제 (실제로는 deleteAt 날짜 업데이트)
	    boolean deleted = mapper.deleteSchRecruit(recruitId) > 0;

	    if (deleted) {
	        log.info("채용 공고 삭제 완료 - recruitId: {}", recruitId);
	    }

	    return deleted;
	}

	/**
	 * 학내 채용 첨부파일 다운로드 처리
	 */
	@Override
	public FileDetailInfo getRecruitFile(String recruitId, int fileOrder, String userId) {
	    // 1. 채용 공고 조회
	    PortalRecruitVO recruit = mapper.selectSchRecruitDetail(recruitId);

	    if (recruit == null) {
	        throw new RuntimeException("존재하지 않는 채용 공고입니다.");
	    }

	    // 2. 파일 ID 확인
	    String fileId = recruit.getAttachFileId();
	    if (fileId == null || fileId.trim().isEmpty()) {
	        throw new RuntimeException("첨부파일이 없습니다.");
	    }

	    // 3. 파일 번들 조회
	    FilesBundleDTO fileBundle = filesService.readFileBundle(fileId);

	    // 4. 요청된 순서의 파일 찾기
	    FileDetailInfo targetFile = fileBundle.getFileDetailInfo().stream()
	        .filter(f -> f.getFileOrder() == fileOrder)
	        .findFirst()
	        .orElseThrow(() -> new RuntimeException("존재하지 않는 파일입니다."));

	    // 5. =========== saveDir 경로 보정 ======
        String saveDir = targetFile.getSaveDir();
        if (saveDir != null && saveDir.startsWith("/")) {
            // "file:D:/LMSFileRepository/" → "D:/LMSFileRepository"
            String rootPath = fileRepositoryPath.replace("file:", "");
            if (rootPath.endsWith("/") || rootPath.endsWith("\\")) {
                rootPath = rootPath.substring(0, rootPath.length() - 1);
            }

            // 절대 경로로 변환
            String correctedPath = rootPath + saveDir;
            targetFile.setSaveDir(correctedPath);

            log.info("파일 경로 보정 완료 - 원본: {}, 보정: {}", saveDir, correctedPath);
        }


	    log.info("채용 공고 파일 다운로드 - recruitId: {}, fileOrder: {}, fileName: {}, userId: {}",
	        recruitId, fileOrder, targetFile.getOriginName(), userId);

	    return targetFile;
	}

	/**
	 * 조회수 증가
	 */
	@Override
	public boolean modifyIncrementViewCount(String recruitId) {
		return mapper.incrementViewCount(recruitId) > 0;
	}

	/////////////////////////////////////////////
	//===========================================================
    // XML 응답 매핑용 내부 클래스들
    //===========================================================

    /**
     * 목록 조회 응답
     */
    @lombok.Data
    @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement(localName = "dhsOpenEmpInfoList")
    public static class JobListResponse {
        private Integer total;
        private Integer startPage;
        private Integer display;

        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "dhsOpenEmpInfo")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper(useWrapping = false)
        private List<JobInfoXml> jobs;
    }

    /**
     * 채용 정보 (목록용)
     */
    @lombok.Data
    public static class JobInfoXml {
        private String empSeqno;
        private String empWantedTitle;
        private String empBusiNm;
        private String coClcdNm;
        private String empWantedStdt;
        private String empWantedEndt;
        private String empWantedTypeNm;
        private String regLogImgNm;
        private String empWantedHomepgDetail;

        // 채용분야 추가
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty(localName = "empJobsListInfo")
        @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper(useWrapping = false)
        private List<JobsInfo> jobsList;

        @lombok.Data
        public static class JobsInfo {
            private String jobsCd;
            private String jobsCdKorNm;
        }
    }

    /**
     * 상세 조회 응답
     */
    @lombok.Data
    @com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement(localName = "dhsOpenEmpInfoDetailRoot")
    public static class JobDetailResponse {
        private String empSeqno;
        private String empWantedTitle;
        private String empBusiNm;
        private String coClcdNm;
        private String empWantedStdt;
        private String empWantedEndt;
        private String empWantedTypeNm;
        private String empWantedHomepg;
        private String empWantedHomepgDetail;
        private String regLogImgNm;
        private String recrCommCont;
        private String empSubmitDocCont;
        private String empRcptMthdCont;
        private String inqryCont;
        private String empnEtcCont;
    }

	@Override
	public String readStfDeptNameByCode(String stfDeptCd) {
		return staffMapper.selectStfDeptNameByCode(stfDeptCd);
	}

	/**
     * 학생 맞춤형 채용 목록 조회 (제목으로 매칭)
     */
	@Override
	public List<JobListItemDTO> readStudentRecruitList(
	    PaginationInfo<PortalRecruitVO> paging,
	    String univDeptCd
	) {
		try {
	        // 1. 학생 학과명 조회
	        String majorName = dbCache.getUnivDeptName(univDeptCd);

	        // 디버깅 로그 추가
	        log.info("[DEBUG] univDeptCd: {}", univDeptCd);
	        log.info("[DEBUG] 조회된 majorName: {}", majorName);

	        // 학과명이 제대로 조회되지 않은 경우 처리
	        if (majorName == null || majorName.equals(univDeptCd)) {
	            log.error("학과명 조회 실패 - univDeptCd: {}, 반환값: {}", univDeptCd, majorName);
	            log.error("DB 캐시가 초기화되지 않았거나 데이터가 없습니다.");
	            return new ArrayList<>();
	        }

	        // 2. 학과명으로 관련 키워드 조회
	        List<String> keywords = JobMatchingUtil.getKeywordsByMajor(majorName);
	        log.info("학생 맞춤형 채용 조회 - 학과: {}, 키워드: {}", majorName, keywords);

	        // 키워드가 비어있는지 확인
	        if (keywords == null || keywords.isEmpty()) {
	            log.warn("매칭 키워드가 없음 - majorName: {}", majorName);
	            return new ArrayList<>();
	        }

	        // 3. API에서 100개 조회
	        String url = String.format(
	            "%s?authKey=%s&callTp=L&returnType=XML&startPage=1&display=100",
	            listApiUrl, authKey
	        );

	        String xmlResponse = restTemplate.getForObject(url, String.class);

	        if (xmlResponse == null || !xmlResponse.trim().startsWith("<")) {
	            log.error("API 응답이 비정상입니다.");
	            return new ArrayList<>();
	        }

	        JobListResponse response = xmlMapper.readValue(xmlResponse, JobListResponse.class);

	        log.info("API에서 가져온 총 공고 수: {}",
	            response.getJobs() != null ? response.getJobs().size() : 0);

	        // 4. 키워드 매칭(제목, 기업명)
	        List<JobListItemDTO> matchedJobs = new ArrayList<>();

	        String userSearchWord = null;
	        if (paging.getSimpleSearch() != null &&
	        	    paging.getSimpleSearch().getSearchWord() != null) {
	        	    userSearchWord = paging.getSimpleSearch().getSearchWord().toLowerCase().trim();
	        }

	        if (response.getJobs() != null) {
	            for (JobInfoXml job : response.getJobs()) {
	                String title = job.getEmpWantedTitle();

	                if (title != null) {
	                    boolean isMatchedByKeyword = false;
	                    // 1차 필터링: 학과 키워드 매칭 로직
	                    for (String keyword : keywords) {
	                        if (title.contains(keyword)) {
	                            isMatchedByKeyword = true;
	                            break;
	                        }
	                    }

	                    if (isMatchedByKeyword) {
	                        JobListItemDTO dto = convertToDTO(job);

	                        // 2차 필터링: 사용자 입력 검색어 필터링
		                    if (userSearchWord != null && !userSearchWord.isEmpty()) {

		                        boolean matchesSearch = false; // 2차 필터링 만족 여부 플래그

		                        // OR 검색 : 제목 또는 기업명 중 하나라도 포함되면 검색됨
		                        if ((dto.getTitle() != null &&
	                                dto.getTitle().toLowerCase().contains(userSearchWord)) ||
	                               (dto.getOrganization() != null &&
	                                dto.getOrganization().toLowerCase().contains(userSearchWord))) {
	                               matchesSearch = true;
		                        }
		                        // 2차 필터링 통과 시 최종 목록에 추가
		                        if (matchesSearch) {
		                            matchedJobs.add(dto); // 1차, 2차 모두 만족
		                        }

		                    } else {
		                        // 검색어가 없으면 1차 필터링된 모든 항목 추가
		                        matchedJobs.add(dto);
		                    }
	                    }
	                }
	            }
	        }

	        log.info("학생 맞춤형 채용 매칭 완료: {} 건", matchedJobs.size());

	        // 5. D-Day 최우선 정렬
 			sortRecruitList(matchedJobs, JobListItemDTO::getRecEndDay);
 			log.info("학생 맞춤형 채용 매칭 완료: {} 건", matchedJobs.size());

	        // 6. 페이징
	        paging.setTotalRecord(matchedJobs.size());
	        int start = (paging.getCurrentPage() - 1) * paging.getScreenSize();
	        int end = Math.min(start + paging.getScreenSize(), matchedJobs.size());

	        if (start >= matchedJobs.size()) {
	            return new ArrayList<>();
	        }

	        return matchedJobs.subList(start, end);

	    } catch (Exception e) {
	        log.error("학생 맞춤형 채용 조회 실패", e);
	        return new ArrayList<>();
	    }
	}

	@Override
	public JobDetailDTO readStudentRecruitDetail(String recruitId) {
		try {
            String url = String.format(
                "%s?authKey=%s&returnType=XML&callTp=D&empSeqno=%s",
                detailApiUrl,
                authKey,
                recruitId
            );

            log.info("고용24 상세 API 호출: {}", url);

            String xmlResponse = restTemplate.getForObject(url, String.class);

            // 1. 빈 응답 체크
            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                log.error("고용24 API 응답이 비어있습니다");
                throw new RuntimeException("API 응답이 비어있습니다");
            }

            // 2. XML 형식인지 확인
            String trimmedResponse = xmlResponse.trim();
            if (!trimmedResponse.startsWith("<")) {
                log.error("고용24 API 응답이 XML 형식이 아닙니다. 응답: {}",
                    trimmedResponse.substring(0, Math.min(100, trimmedResponse.length())));
                throw new RuntimeException("API 응답이 XML 형식이 아닙니다: " + trimmedResponse);
            }

            log.debug("고용24 API XML 응답 수신 성공");

            // 3. XML 파싱
            JobDetailResponse response = xmlMapper.readValue(xmlResponse, JobDetailResponse.class);

            // 4. DTO 변환
            JobDetailDTO dto = convertToDetailDTO(response);

            log.info("고용24 API 상세 조회 성공: {}", recruitId);
            return dto;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("고용24 API 상세 조회 실패", e);
            throw new RuntimeException("API 호출 실패: " + e.getMessage(), e);
        }
	}

	/**
	 * 맞춤형 채용 목록 => D-Day/마감일 우선 정렬 메서드
	 *
	 * 1순위: 마감된 공고를 최하단으로 내리고
	 * 2순위: D-Day 공고 최상단으로 배치
	 * 3순위: 그 다음, 마감일이 가까운 순(오름차순) 정렬함
	 *
	 * @param <T>
	 * @param list 정렬할 채용 목록 (JobListItemDTO)
	 * @param dateExtractor T 객체에서 LocalDate 형태의 마감일을 추출하는 함수
	 */
	private <T> void sortRecruitList(List<T> list, Function<T, LocalDate> dateExtractor) {
		LocalDate today = LocalDate.now(); // 현재 날짜

		Comparator<T> comparator = Comparator
			// 1. 공고 유효성 체크: 0:유효(오늘 또는 미래), 1:마감(과거), 2:마감일 없음(Null) 순으로 정렬 (오름차순)
			.comparing((T item) -> {
				LocalDate endDate = dateExtractor.apply(item);  // 마감일 추출
				if (endDate == null) return 2; 					// 마감일 없는 공고는 맨 뒤
				if (endDate.isBefore(today)) return 1; 			// 마감된 공고는 두 번째 (뒤쪽)
				return 0; 										// 유효한 공고는 첫 번째 (앞쪽)
			})
			// 2. 유효한 공고(0) 중에서 D-Day 우선 정렬: recEndDay가 오늘과 같은지(true = D-Day)를 기준으로 역순 정렬 (true가 앞으로 옴)
			.thenComparing((T item) -> today.isEqual(dateExtractor.apply(item)), Comparator.reverseOrder())
			// 3. 마감일에 따른 정렬: 마감일이 가까운 순으로 정렬 (오름차순)
			.thenComparing(dateExtractor, Comparator.nullsLast(LocalDate::compareTo));

		list.sort(comparator);
	}
}
