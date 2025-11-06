package kr.or.jsu.lms.student.controller.academicChange;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.vo.CommonCodeVO;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 소속변경 페이지 렌더링 컨트롤러 - 전과, 복수전공, 부전공
 * @author 김수현
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 9. 25.		김수현			파일 이름 수정
 *	2025. 10. 14.		김수현			통합 신청 폼 추가
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/lms/student/affiliation")
@RequiredArgsConstructor
public class StuAffilViewController {

	private final DatabaseCache databaseCache;
	private final CommonCodeService commonCodeService;
	private final StuInfoService studentService;

	/**
	 * 통합 신청 페이지
	 * @return
	 */
	@GetMapping("/apply-form")
	public String getList(
		Model model
		, @AuthenticationPrincipal CustomUserDetails userDetails
	) {
		StudentVO student = (StudentVO) userDetails.getRealUser();

		// 필요한 거 : 학번, 이름, 단과대학, 학과, 학년, 총 이수학점, 총 평점

		String studentNo = student.getStudentNo(); // 학번
		String studentName = databaseCache.getUserName(studentNo); // 이름

	    StudentDetailDTO studentInfo = studentService.readStuMyInfo(studentNo);

	    model.addAttribute("studentInfo", studentInfo);
	    model.addAttribute("studentName", studentName);


		List<CommonCodeVO> affilList = commonCodeService.readCommonCodeList(CommonCodeSort.AFFIL_CHANGE_CD);
	    model.addAttribute("affilList", affilList);
	    log.info("===> 소속변동 리스트 : {}", affilList);

	    int totalCredit = studentService.readTotalCredit(studentNo);
	    Double gpa = studentService.readStudentGPA(studentNo);

	    model.addAttribute("totalCredit", totalCredit);
	    model.addAttribute("gpa", gpa);

		return "student/academicChange/studentAffiliationChange";
	}
}
