package kr.or.jsu.lms.student.controller.financialAid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.dto.ScholarshipResponseDTO;
import kr.or.jsu.lms.student.service.financialAid.StuScholarshipService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;

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
 *	2025. 10. 9.		김수현			장학금 조회 추가
 * </pre>
 */
@Controller
@RequestMapping("/lms/student/scholarship")
@RequiredArgsConstructor
public class StuScholarshipController {

	private final StuScholarshipService service;
	
	// 장학금 페이지
	@GetMapping
	public String getList(
		@AuthenticationPrincipal CustomUserDetails userDetails
		, Model model
	) {
		StudentVO student = (StudentVO) userDetails.getRealUser();
        String studentNo = student.getStudentNo();
        
        ScholarshipResponseDTO scholarshipInfo = service.getScholarshipInfo(studentNo);
        
 		model.addAttribute("totalScholarship", scholarshipInfo.getTotalScholarship());
 		model.addAttribute("scholarshipDistribution", scholarshipInfo.getScholarshipDistribution());
 		model.addAttribute("scholarshipHistory", scholarshipInfo.getScholarshipHistory());
     		
		return "student/financialAid/studentScholarship";
	}
}
