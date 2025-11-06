package kr.or.jsu.portal.controller.job;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.validate.groups.InsertGroup;
import kr.or.jsu.devtemp.service.FilesUploadService;
import kr.or.jsu.portal.service.job.PortalJobService;
import kr.or.jsu.vo.PortalRecruitVO;
import kr.or.jsu.vo.StaffVO;
import kr.or.jsu.vo.UsersVO;
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
 *	2025. 9. 29.		김수현			  파일처리 추가
 *	2025. 9. 30.		김수현			게시글 교직원 권한 추가
 *	2025. 10. 2. 		김수현			파일 처리 분리
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/portal/job/internal/write")
@RequiredArgsConstructor
public class PortalJobCreateController {

	private final PortalJobService service;
	private final FilesUploadService fileService; // 파일 업로드 서비스
	
	public static final String MODELNAME = "portalRecruit";

	@GetMapping
    public String formUI(
    	Model model,
    	@AuthenticationPrincipal CustomUserDetails userDetails
    ) {
		// 현재 로그인한 교직원의 부서명을 등록폼 value에 넣기 위해서
		if (userDetails != null && userDetails.getRealUser() instanceof StaffVO) {
            StaffVO staff = (StaffVO) userDetails.getRealUser();
            String currentDeptCd = staff.getStfDeptCd(); // 현재 로그인한 교직원의 부서코드
            String currentDeptName = service.readStfDeptNameByCode(currentDeptCd); // 현재 로그인한 교직원의 부서명
            model.addAttribute("currentDeptName", currentDeptName);
		}
		
        return "portal/portalSchJobForm";
    }

	@PostMapping
	@ResponseBody
	public Object formProcess(
		@Validated(InsertGroup.class) @ModelAttribute(MODELNAME) PortalRecruitVO portalRecruit,
		BindingResult errors,
		List<MultipartFile> files,
		Authentication authentication,
		RedirectAttributes redirectAttributes
	) {
		// 에러가 있을 때
		if(errors.hasErrors()) {
			log.error("채용정보 등록 폼 유효성 검사 실패.");
			return Map.of("status", "error", "message", "입력 항목을 확인해 주세요."); // 실패 메세지만
		}

		// 에러가 없을 때
		String uploaderUserId = null;
		
		// 사용자 정보 검증
		if(authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			UsersVO user = userDetails.getRealUser();
			if (user != null) {
			    uploaderUserId = user.getUserId();
			}
		} 

		// Service 실행
		try {
			boolean success = service.createSchRecruit(portalRecruit, files, uploaderUserId); // 서비스에서 파일처리
            
            if (success) {
                // 성공 시 JSON 응답 반환
                return Map.of("status", "success", "redirectUrl", "/portal/job/internal");
            } else {
                // DB 등록 실패 시 (result <= 0)
                log.error("채용정보 등록 DB 처리 실패.");
                return Map.of("status", "error", "message", "등록 처리 중 알 수 없는 오류가 발생했습니다.");
            }
		} catch(RuntimeException e) { // service에서 던진 RuntimeException (파일 처리 실패 등)
            log.error("채용정보 등록 중 오류 발생:", e);
			return Map.of("status", "error", "message", "처리 중 오류가 발생했습니다. 다시 시도해 주세요. (" + e.getMessage() + ")");
		} catch(Exception e) {
            log.error("채용정보 등록 중 일반 오류 발생:", e);
            return Map.of("status", "error", "message", "시스템 오류가 발생했습니다. 관리자에게 문의하세요.");
        }
	}
}
