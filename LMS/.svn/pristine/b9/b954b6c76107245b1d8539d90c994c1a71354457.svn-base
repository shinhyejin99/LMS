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
 * 전체 학적 변동 현황 조회
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
 *	2025. 9. 25.		김수현			파일 이름 수정
 *	2025. 10. 13.		김수현			statusPage() 메소드 추가
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/lms/student/academic-change")
@RequiredArgsConstructor
public class StuRecordStatusViewController {

	private final StuInfoService studentService;
	private final CommonCodeService commonCodeService;
	private final DatabaseCache databaseCache;

	/**
	 * 전체 학적변동 신청 현황 페이지
	 * @param model
	 * @param userDetails
	 * @return
	 */
	@GetMapping("/status")
	public String statusPage(
		Model model
		, @AuthenticationPrincipal CustomUserDetails userDetails
	) {
		StudentVO student = (StudentVO) userDetails.getRealUser();
	    String studentNo = student.getStudentNo();
	    String studentName = databaseCache.getUserName(studentNo); // 이름

	    // 학생 상세 정보 조회 (현재 재학 상태 포함)
	    StudentDetailDTO studentInfo = studentService.readStuMyInfo(studentNo);

	    model.addAttribute("studentInfo", studentInfo);
	    model.addAttribute("studentName", studentName);

	    List<CommonCodeVO> affilList = commonCodeService.readCommonCodeList(CommonCodeSort.AFFIL_CHANGE_CD);
	    List<CommonCodeVO> applyList = commonCodeService.readCommonCodeList(CommonCodeSort.APPLY_STATUS_CD);

	    model.addAttribute("affilList", affilList);
	    model.addAttribute("applyList", applyList);

		return "student/academicChange/studentAcademicChangeStatus";
	}

}
