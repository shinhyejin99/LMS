package kr.or.jsu.classregist.staff.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.jsu.classregist.dto.LectureListDTO;
import kr.or.jsu.classregist.dto.StaffCourseSearchDTO;
import kr.or.jsu.classregist.staff.service.StaffCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 교직원 수강신청 관리 view controller
 * @author 김수현
 * @since 2025. 10. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 27.     	김수현	          최초 생성
 *
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping("/lms/staff/course")
@RequiredArgsConstructor
public class StaffCourseViewController {

	private final StaffCourseService service;

	/**
	 * 수강신청 관리 페이지
	 */
	@GetMapping("/manage/{yearterm}")
	public String courseManage(
	    @PathVariable String yearterm,
	    @RequestParam(required = false, defaultValue = "") String keyword,
	    @RequestParam(defaultValue = "1") int page,
	    Model model
	) {
	    log.info("수강신청 관리 페이지 - 학기: {}, 검색어: {}, 페이지: {}", yearterm, keyword, page);

	    // 검색 조건 설정
	    StaffCourseSearchDTO searchDTO = new StaffCourseSearchDTO();
	    searchDTO.setYeartermCd(yearterm);
	    searchDTO.setSearchKeyword(keyword);
	    searchDTO.setPage(page);
	    searchDTO.setPageSize(10);

	    // 강의 목록 조회
	    List<LectureListDTO> lectureList = service.getCourseList(searchDTO);
	    int totalCount = service.getCourseCount(searchDTO);
	    int totalPages = (int) Math.ceil((double) totalCount / 10);

	    // Model에 데이터 추가
	    model.addAttribute("lectureList", lectureList);
	    model.addAttribute("totalCount", totalCount);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("currentYearterm", yearterm);
	    model.addAttribute("searchKeyword", keyword);

	    return "classregistration/staff/staffCourseManage";
	}

	/**
	 * 기본 경로 (2026_REG1로 리다이렉트)
	 */
	@GetMapping("/manage")
	public String courseManageDefault() {
	    return "redirect:/lms/staff/course/manage/2026_REG1";
	}

}
