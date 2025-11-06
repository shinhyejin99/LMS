package kr.or.jsu.lms.student.controller.academicChange;

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
import kr.or.jsu.lms.student.service.academicChange.StuRecordApplyService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 재학상태변경 페이지 렌더링 컨트롤러
 * -휴학, 복학, 졸업유예, 자퇴
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
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 9. 25.		김수현			파일 이름 수정
 *	2025. 10. 10.		김수현			기능 추가
 *	2025. 10. 13.		김수현			상세페이지 분리
 *
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/lms/student/record")
@RequiredArgsConstructor
public class StuRecordViewContoller {

	private final StuRecordApplyService service;
	private final DatabaseCache databaseCache;
	private final CommonCodeService commonCodeService;

	private void commonData(Model model) {
		model.addAttribute("milType", commonCodeService.readCommonCodeList(CommonCodeSort.MILITARY_TYPE_CD));
		model.addAttribute("recordChangeList", commonCodeService.readCommonCodeList(CommonCodeSort.RECORD_CHANGE_CD));
	}

	/**
	 * 통합 신청 페이지
	 * @param userDetails
	 * @param model
	 * @return
	 */
	@GetMapping("/apply-form")
	public String applyForm(
		Model model
		, @AuthenticationPrincipal CustomUserDetails userDetails
	) {
		commonData(model);

		StudentVO student = (StudentVO) userDetails.getRealUser();
		StudentDetailDTO studentInfo = new StudentDetailDTO();

		String univDeptCd = student.getUnivDeptCd();
		String gradeCd = student.getGradeCd();
		String stuStatusCd = student.getStuStatusCd();
				
		// 필요한 거 : 학번, 이름, 학과, 학년
		String studentNo = student.getStudentNo(); // 학번
		String studentName = databaseCache.getUserName(studentNo); // 이름
		String univDeptName = databaseCache.getUnivDeptName(univDeptCd); // 학과명
		String gradeName = databaseCache.getCodeName(gradeCd); // 학년
		String statusName = databaseCache.getCodeName(stuStatusCd);

		studentInfo.setStudentNo(studentNo);
		studentInfo.setStudentName(studentName);
		studentInfo.setUnivDeptName(univDeptName);
		studentInfo.setGradeName(gradeName);
		studentInfo.setStuStatusName(statusName);

		model.addAttribute("studentInfo", studentInfo);

		return "student/academicChange/studentEnrollmentChange";
	}
}
