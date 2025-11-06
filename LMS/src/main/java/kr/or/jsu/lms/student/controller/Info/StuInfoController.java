package kr.or.jsu.lms.student.controller.Info;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.core.validate.groups.UpdateGroup;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.vo.CommonCodeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 9. 25. 		김수현			파일 이름 수정
 *	2025. 9. 27.		김수현			로그인 정보 가져오기 추가
 *	2025. 10. 15.		김수현			증명사진 불러오기 추가
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/lms/student/info")
@RequiredArgsConstructor
public class StuInfoController {

	private final StuInfoService service;
	private final DatabaseCache databaseCache;
	private final CommonCodeService commonCodeService;

	public static final String MODELNAME = "student";

	/**
	 * 로그인에서 정보 가져오기
	 *
	 * @return
	 */
	private String getLoggedInStudentNo() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof CustomUserDetails) {
			return ((CustomUserDetails) principal).getRealUser().getUserNo();
		}

		return null;
	}


	/**
	 * 학생 인적 & 학적 정보 조회
	 * @param model
	 * @return
	 */
	@GetMapping
	public String getStuMyInfo(
		Model model
	) {
		String studentNo = getLoggedInStudentNo();
		if (studentNo == null) {
			model.addAttribute("error", "로그인이 필요합니다.");
			return "student/info/studentInfo";
		}
		// jsp에서 은행 select 위해서
		List<CommonCodeVO> bankList = commonCodeService.readCommonCodeList(CommonCodeSort.BANK_CODE);
	    model.addAttribute("bankList", bankList);
	    log.info("===> 은행 리스트 : {}", bankList);
		StudentDetailDTO student = service.readStuMyInfo(studentNo);
		String colleagName = databaseCache.getCollegeName(student.getCollegeCd());
		log.info("===> 단과대명 : {}", colleagName);
		student.setCollegeName(colleagName);
		model.addAttribute("student", student);
		return "student/info/studentInfo";
	}

	/**
	 * 증명사진 불러오기
	 */
	@GetMapping("/photo")
	public ResponseEntity<Resource> getMyIdPhoto() {
	    String studentNo = getLoggedInStudentNo();

	    if (studentNo == null) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
	    }

	    try {
	        FileDetailInfo fileInfo = service.getStudentIdPhoto(studentNo);

	        Resource file = fileInfo.getRealFile();

	        String ext = fileInfo.getExtension() == null ? "" : fileInfo.getExtension().toLowerCase();

	        MediaType mediaType = switch (ext) {
	            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
	            case "png" -> MediaType.IMAGE_PNG;
	            case "gif" -> MediaType.IMAGE_GIF;
	            case "webp" -> MediaType.parseMediaType("image/webp");
	            case "bmp" -> MediaType.parseMediaType("image/bmp");
	            default -> MediaType.APPLICATION_OCTET_STREAM;
	        };

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(mediaType);

	        return new ResponseEntity<>(file, headers, HttpStatus.OK);

	    } catch (RuntimeException e) {
	        log.error("증명사진 조회 실패: {}", e.getMessage());
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 또는 500
	    }
	}

	/**
	 * 학생) 인적정보 수정
	 * @param student
	 * @param errors
	 * @param nameValue
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("/modify")
	public String modifyStuInfo (
		@Validated(UpdateGroup.class) @ModelAttribute(MODELNAME) StudentDetailDTO student
		, BindingResult errors
		, @RequestParam("name") String nameValue // jsp에서 받아온 이름 (ex. 김 새봄 / Kim SaeBom)
		, RedirectAttributes redirectAttributes
	) {
		if(errors.hasErrors()) {
	        // 에러가 있으면 다시 폼으로
	        return "student/info/studentInfo";
	    }

	    try {
	        service.updateStuMyInfo(student, nameValue);
	        redirectAttributes.addFlashAttribute("successMessage", "정보가 수정되었습니다.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "수정 중 오류가 발생했습니다.");
	        log.error("인적정보 수정 실패", e);
	    }

	    return "redirect:/lms/student/info";  // 조회 페이지로 리다이렉트
	}

}
