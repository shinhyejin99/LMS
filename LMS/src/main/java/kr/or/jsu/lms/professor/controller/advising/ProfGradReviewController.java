package kr.or.jsu.lms.professor.controller.advising;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import java.util.Collections;
import java.util.List;
import kr.or.jsu.vo.StuGraduReqVO;
import kr.or.jsu.lms.professor.service.advising.ProfGradReviewService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.vo.UsersVO;

// ... other imports

@Controller
@RequestMapping("/lms/professor/advising/graduation")
public class ProfGradReviewController {

	private static final Logger logger = LoggerFactory.getLogger(ProfGradReviewController.class);

	@Autowired
	private ProfGradReviewService profGradReviewService;

	// 지도교수 활동 - 졸업요건 검토 컨트롤러
	@GetMapping
	public String getList(
		@RequestParam(value = "grade", required = false) String grade,
		@RequestParam(value = "semester", required = false) String semester,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model,
		Authentication authentication
	) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UsersVO usersVO = userDetails.getRealUser();
		String professorNo = usersVO.getUserNo();

		List<StuGraduReqVO> gradReviewList = Collections.emptyList();
		List<StuGraduReqVO> graduationAssignments = Collections.emptyList();
		int total = 0;

		// Always call service methods, let service/mapper handle null/empty grade for "전체"
		total = profGradReviewService.getGradReviewListCount(professorNo, grade);
		gradReviewList = profGradReviewService.getGradReviewList(professorNo, grade, page, size);
		graduationAssignments = profGradReviewService.getGraduationAssignmentSubmissions(professorNo, grade, semester);
		
		model.addAttribute("gradReviewList", gradReviewList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", (int) Math.ceil((double) total / size));
		model.addAttribute("graduationAssignments", graduationAssignments);

		model.addAttribute("selectedGrade", grade);
		model.addAttribute("selectedSemester", semester);
		
		return "professor/advising/profGradReviewList";
	}

	@PostMapping("/approve")
	public String approveReview(@RequestParam String reviewId, RedirectAttributes ra) {
		logger.info("approveReview: Received reviewId: '{}'", reviewId); // Add this line
		try {
			profGradReviewService.approveReview(reviewId);
			ra.addFlashAttribute("message", "졸업 심사가 승인되었습니다.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", "졸업 심사 승인 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/lms/professor/advising/graduation";
	}

	@PostMapping("/reject")
	public String rejectReview(@RequestParam String reviewId, @RequestParam String reason, RedirectAttributes ra) {
		try {
			profGradReviewService.rejectReview(reviewId, reason);
			ra.addFlashAttribute("message", "졸업 심사가 반려되었습니다.");
		} catch (Exception e) {
			ra.addFlashAttribute("error", "졸업 심사 반려 중 오류가 발생했습니다: " + e.getMessage());
		}
		return "redirect:/lms/professor/advising/graduation";
	}

    @GetMapping(value = "/api/assignments", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public StuGraduReqVO getGraduationAssignmentDetails(
            Authentication authentication,
            @RequestParam(value = "studentNo") String studentNo
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UsersVO realUser = userDetails.getRealUser();
        String professorNo = realUser.getUserNo();

        logger.info("getGraduationAssignmentDetails API called. professorNo: {}, studentNo: {}", professorNo, studentNo);

        StuGraduReqVO assignment = profGradReviewService.getGraduationAssignmentByStudentNo(professorNo, studentNo);

        if (assignment == null) {
            logger.info("No graduation assignment found for studentNo: {}", studentNo);
        } else {
            logger.info("Found graduation assignment for studentNo: {}", studentNo);
        }

        return assignment;
    }
}