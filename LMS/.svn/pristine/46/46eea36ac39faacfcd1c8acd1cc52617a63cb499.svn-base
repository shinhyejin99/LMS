package kr.or.jsu.classregist.professor.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.or.jsu.classregist.professor.service.ProfLectRegistService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.vo.ProfessorVO;
import kr.or.jsu.vo.UnivYeartermVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 교수) 수강신청 학생 조회 컨트롤러
 * @author 김수현
 * @since 2025. 10. 19.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 19.     	김수현	          최초 생성
 *	2025. 10. 21.		김수현			학년도 학기 동적 생성 위한 코드 추가
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/lms/professor")
@RequiredArgsConstructor
public class ProfRegistStuViewController {

	private final ProfLectRegistService profLectRegistService;

	/**
     * 수강신청 학생 목록 페이지
     */
    @GetMapping("/lectures")
    public String myLectures(
        @AuthenticationPrincipal CustomUserDetails userDetails
        , Model model
    ) {

        ProfessorVO professor = (ProfessorVO) userDetails.getRealUser();
        String professorNo = professor.getProfessorNo();

        log.info("교수 강의 목록 조회 - 교번: {}", professorNo);

        // 현재 날짜 기준으로 한 학기 앞까지만 가져오기
        LocalDate today = LocalDate.now();
        LocalDate oneTermAhead = today.plusMonths(6); // 6개월 후 (1학기 = 약 6개월)

        // yearTermList 조회
        List<UnivYeartermVO> yearTermList = profLectRegistService.getAvailableYearTerms(oneTermAhead);

        model.addAttribute("professorNo", professorNo);
        model.addAttribute("professorName", professor.getLastName() + professor.getFirstName());

        model.addAttribute("yearTermList", yearTermList);
        return "classregistration/professor/profRegistStuList";

    }
}
