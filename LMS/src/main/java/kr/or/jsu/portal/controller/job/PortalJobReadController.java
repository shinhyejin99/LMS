package kr.or.jsu.portal.controller.job;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.dto.JobDetailDTO;
import kr.or.jsu.dto.SchRecruitDetailDTO;
import kr.or.jsu.portal.service.job.PortalJobService;
import kr.or.jsu.vo.FileDetailVO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
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
 *  2025. 9. 26.     	김수현	          최초 생성
 *	2025. 9. 29.		김수현			파일 다운로드 추가
 *	2025. 9. 30.		김수현			권한 체크 추가, 맞춤형 채용 코드 추가
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/job")
@RequiredArgsConstructor
public class PortalJobReadController {

	private final PortalJobService service;
	private final FilesUploadService fileService;

	/**
     * 채용정보 페이지 뷰 반환
     * @return
     */
    @GetMapping("/{jobType}")
    public String getJobList(
    	@PathVariable String jobType,
    	Model model,
    	@AuthenticationPrincipal CustomUserDetails userDetails

    ) {
    	if (!List.of("public", "internal", "student").contains(jobType)) {
    		throw new IllegalArgumentException("잘못된 " + jobType);
        }

    	// 교직원일 때만 부서명 조회
        if (userDetails != null && userDetails.getRealUser() instanceof StaffVO) {
            StaffVO staff = (StaffVO) userDetails.getRealUser();
            String stfDeptName = service.readStfDeptNameByCode(staff.getStfDeptCd());
            model.addAttribute("stfDeptName", stfDeptName);
        }

        // 학생 맞춤형일 때 학과코드 전달
        if (jobType.equals("student") && userDetails != null &&
        								userDetails.getRealUser() instanceof StudentVO) {
            StudentVO student = (StudentVO) userDetails.getRealUser();
            model.addAttribute("univDeptCd", student.getUnivDeptCd());
        }

        model.addAttribute("jobType", jobType);

        return "portal/portalJobList";
    }

    /**
     * 채용정보 상세화면 뷰 반환
     * @param jobType
     * @param recruitId
     * @param model
     * @return
     */
    @GetMapping("{jobType}/{recruitId}")
	public String getJobDetail(
		@PathVariable String jobType,
		@PathVariable String recruitId,
		Model model,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
    	try {
			switch (jobType) {
			// 공공 채용 상세
			case "public":
				JobDetailDTO publicJob = service.readPublicRecruitDetail(recruitId);

				if (publicJob == null) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채용 정보를 찾을 수 없습니다");
				}

				model.addAttribute("job", publicJob);
				model.addAttribute("jobType", jobType);
				model.addAttribute("recruitId", recruitId);

				// 공공채용은 첨부파일이 없으므로 빈 리스트 전달
				model.addAttribute("fileList", List.of());

				return "portal/portalPubJobDetail"; // 공공채용 상세 JSP

			// 학내 채용 상세
			case "internal":
				SchRecruitDetailDTO internalJob = service.readSchRecruitDetail(recruitId);

				if (internalJob == null) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "학내채용 정보를 찾을 수 없음");
				}

				// 수정/삭제 권한 체크 (게시글은 작성한 부서에서만 수정/삭제할 수 있다.)
	            boolean canEdit = false;
	            if (userDetails != null && userDetails.getRealUser() instanceof StaffVO) {
	                StaffVO staff = (StaffVO) userDetails.getRealUser();
	                String currentDeptCd = staff.getStfDeptCd(); // 현재 로그인한 교직원의 부서코드
	                String currentDeptName = service.readStfDeptNameByCode(currentDeptCd); // 현재 로그인한 교직원의 부서명
	                String writerDeptName = internalJob.getStfDeptName(); // 게시글을 작성한 부서명
	                // 현재 로그인한 교직원의 부서와 게시글 작성 부서를 비교
	                canEdit = currentDeptName != null && currentDeptName.equals(writerDeptName);
	            }

				model.addAttribute("job", internalJob);
				model.addAttribute("fileList", internalJob.getAttachFiles());
				model.addAttribute("jobType", jobType);
				model.addAttribute("recruitId", recruitId);
				model.addAttribute("canEdit", canEdit);

				return "portal/portalScJobDetail"; // 학내채용 상세 JSP

			// 맞춤 채용 상세 (추후 구현)
			case "student":
				JobDetailDTO studentJob = service.readStudentRecruitDetail(recruitId);

				if (studentJob == null) {
					throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채용 정보를 찾을 수 없습니다");
				}

				model.addAttribute("job", studentJob);
				model.addAttribute("jobType", jobType);
				model.addAttribute("recruitId", recruitId);

				// 공공채용은 첨부파일이 없으므로 빈 리스트 전달
				model.addAttribute("fileList", List.of());

				return "portal/portalPubJobDetail";

			default:
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 채용분류 타입");
			}

		} catch (Exception e) {
			log.error("채용정보 상세 페이지 로드 실패", e);
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다");
		}
	}
}
