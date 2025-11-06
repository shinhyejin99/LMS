package kr.or.jsu.portal.controller.job;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.JobDetailDTO;
import kr.or.jsu.dto.JobListItemDTO;
import kr.or.jsu.dto.SchRecruitDetailDTO;
import kr.or.jsu.portal.service.job.PortalJobService;
import kr.or.jsu.vo.PortalRecruitVO;
import kr.or.jsu.vo.StudentVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 9. 25.		김수현			학내 채용정보 조회 생성
 *	2025. 9. 27.		정태일			페이징 관련 코드 추가
 *	2025. 9. 28.		김수현			조회수 증가, 검색 추가
 *	2025. 9. 30. 		김수현			공공채용, 맞춤 채용 관련 코드 추가
 *	2025. 10. 31.		김수현			파일 처리 수정
 *      </pre>
 */
@Slf4j
@RestController
@RequestMapping("/rest/portal/job")
@RequiredArgsConstructor
public class PortalJobRestController {

	private final PortalJobService service;

	/**
	 * jobType에 따라 해당되는 분류의 채용정보 목록을 조회
	 * @param jobType
	 * @param model
	 * @return
	 * @return
	 */
	@GetMapping("/{jobType}")
	public Map<String, Object> getJobList(
		@PathVariable String jobType,
		@ModelAttribute("paging") PaginationInfo<PortalRecruitVO> paging,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		log.info("채용정보 목록 조회 - jobType: {}, page: {}", jobType, paging.getCurrentPage());

		Map<String, Object> result = new HashMap<>();

		switch (jobType) {
		// 공공 채용 케이스 (jobType = public)
		case "public" :
			List<JobListItemDTO> publicList = service.readPublicRecruitList(paging);
			result.put("success", true);
			result.put("list", publicList);
			result.put("jobType", jobType);
			result.put("totalCount", paging.getTotalRecord());
			break;

		case "internal" :
			List<PortalRecruitVO> internalList = service.readSchRecruitList(paging);
			result.put("success", true);
			result.put("list", internalList);
			result.put("jobType", jobType);
			result.put("totalCount", paging.getTotalRecord());
			break;

		// 학생 맞춤 채용
		case "student" :
			if (userDetails != null && userDetails.getRealUser() instanceof StudentVO) {
	            StudentVO student = (StudentVO) userDetails.getRealUser();
	            String univDeptCd = student.getUnivDeptCd();

	            log.info("학생 맞춤형 채용 조회 요청 - 학번: {}, 학과코드: {}",
	                    student.getStudentNo(), univDeptCd);

	            List<JobListItemDTO> studentList = service.readStudentRecruitList(paging, univDeptCd);
	            result.put("success", true);
	            result.put("list", studentList);
	            result.put("jobType", jobType);
	            result.put("totalCount", paging.getTotalRecord());
	        } else {
	            result.put("success", false);
	            result.put("message", "학생만 접근 가능합니다");
	        }
	        break;

		default:
			throw new IllegalArgumentException("존재하지 않는 채용분류 타입 : " + jobType);
		}

	    // 프론트에서 페이징 버튼 렌더링에 필요한 메타 정보
	    result.put("paging", Map.of(
	        "totalPage",   paging.getTotalPage(),
	        "currentPage", paging.getCurrentPage(),
	        "startPage",   paging.getStartPage(),
	        "endPage",     paging.getEndPage()
	    ));
        return result;
	}

	/**
	 * 채용정보 상세보기
	 * @param jobType
	 * @param recruitId
	 * @return
	 */
	@GetMapping("/{jobType}/{recruitId}")
	public  Map<String, Object> getPubJobDetail(
		@PathVariable String jobType,
		@PathVariable String recruitId
	) {
		log.info("채용정보 상세 조회 - jobType: {}, recruitId: {}", jobType, recruitId);

		Map<String, Object> result = new HashMap<>();
		try {
			switch (jobType) {
			case "public":
				JobDetailDTO publicDetail = service.readPublicRecruitDetail(recruitId);
				result.put("success", true);
				result.put("detail", publicDetail);
				result.put("jobType", jobType);
				break;

			case "internal":
				SchRecruitDetailDTO internalDetail  = service.readSchRecruitDetail(recruitId);
				result.put("success", true);
				result.put("detail", internalDetail);
				result.put("jobType", jobType);
				break;

				// 맞춤 채용 (추후 구현)
	//			case "student":
	//				Object studentDetail = service.readStudentRecruitDetail(recruitId);
	//				result.put("success", true);
	//				result.put("detail", studentDetail);
	//				result.put("jobType", jobType);
	//				break;

			default:
				throw new IllegalArgumentException("존재하지 않는 채용분류 타입 : " + jobType);
			}
		} catch(Exception e) {
			log.error("채용정보 상세 조회 실패 - recruitId: {}", recruitId, e);
			result.put("success", false);
			result.put("message", "채용정보를 찾을 수 없습니다.");
		}
		return result;
	}

	/**
	 * 채용정보 조회수 1 증가 메소드
	 *
	 * @param jobType 채용 분류 (public, internal 등)
	 * @param recruitId 채용 정보 ID
	 * @return 성공 여부를 담은 Map
	 */
	@PostMapping("/{jobType}/{recruitId}/view-count")
	public Map<String, Object> incrementJobViewCount(
		@PathVariable String jobType,
		@PathVariable String recruitId
	) {
		Map<String, Object> result = new HashMap<>();

		// jobType 유효성 검증
		if (!List.of("public", "internal", "student").contains(jobType)) {
			result.put("success", false);
			result.put("message", "존재하지 않는 채용분류 타입입니다.");
			return result;
		}

		try {
			// 서비스 메서드를 호출: 조회수를 증가시키고 DB에 반영
			service.modifyIncrementViewCount(recruitId);

			result.put("success", true);
			result.put("message", "조회수가 성공적으로 증가했습니다.");

		} catch (Exception e) {
			// 예외 처리
			System.err.println("조회수 증가 중 오류 발생: " + e.getMessage());
			result.put("success", false);
			result.put("message", "조회수 증가 처리 중 오류가 발생했습니다.");
		}

		return result;
	}

	/**
	 * 학내 채용 공고 첨부파일 다운로드
	 * @param recruitId 채용 공고 ID
	 * @param fileOrder 파일 순번
	 * @param authentication 인증 정보
	 * @return 파일 다운로드 응답
	 */
	@GetMapping("/internal/{recruitId}/file/{fileOrder}")
	public ResponseEntity<Resource> downloadRecruitFile(
	    @PathVariable("recruitId") String recruitId,
	    @PathVariable("fileOrder") int fileOrder,
	    Authentication authentication
	) {
	    log.info("파일 다운로드 요청 - recruitId: {}, fileOrder: {}", recruitId, fileOrder);

	    // 1. 사용자 정보 검증 (선택사항 - 로그용)
	    String userId = null;
	    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        UsersVO user = userDetails.getRealUser();
	        if (user != null) {
	            userId = user.getUserId();
	        }
	    }

	    try {
	        // 2. 파일 정보 조회
	        FileDetailInfo fileInfo = service.getRecruitFile(recruitId, fileOrder, userId);

	        // 3. 실제 파일 로드
	        Resource file = fileInfo.getRealFile();

	        // 4. 파일명 구성
	        String fileName = fileInfo.getOriginName();
	        String ext = fileInfo.getExtension();
	        String originalFileName = (ext == null || ext.isBlank())
	            ? fileName
	            : fileName + "." + ext;

	        // 5. 헤더 설정
	        HttpHeaders headers = new HttpHeaders();
	        try {
	            String encodedFileName = URLEncoder.encode(originalFileName, "UTF-8")
	                .replaceAll("\\+", "%20");
	            headers.add(HttpHeaders.CONTENT_DISPOSITION,
	                "attachment; filename=\"" + encodedFileName + "\"");
	        } catch (UnsupportedEncodingException e) {
	            log.error("파일명 인코딩 실패 - fileName: {}", originalFileName, e);
	            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
	                "파일명 인코딩 실패");
	        }

	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

	        log.info("파일 다운로드 성공 - recruitId: {}, fileOrder: {}, fileName: {}",
	            recruitId, fileOrder, originalFileName);

	        return new ResponseEntity<>(file, headers, HttpStatus.OK);

	    } catch (RuntimeException e) {
	        log.error("파일 다운로드 실패 - recruitId: {}, fileOrder: {}", recruitId, fileOrder, e);
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
	    }
	}
}
