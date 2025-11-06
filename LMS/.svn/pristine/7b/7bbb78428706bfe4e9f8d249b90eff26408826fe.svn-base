package kr.or.jsu.lms.student.controller.financialAid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
 *	2025. 10. 4. 		김수현			등록금 고지서 미리보기, 다운로드 메소드 틀 추가
 *	2025. 10. 21.		김수현			납부내역, 장학금 조회 메서드 추가
 *	2025. 10. 22.		김수현			컨트롤러 분리
 * </pre>
 */
@Controller
@RequestMapping("/lms/student/tuition")
@RequiredArgsConstructor
public class StuTuitionViewController {

	// 등록금 페이지
	@GetMapping
	public String getList() {
		return "student/financialAid/studentTuition";
	}
}
