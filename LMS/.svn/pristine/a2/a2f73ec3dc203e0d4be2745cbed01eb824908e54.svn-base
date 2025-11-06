package kr.or.jsu.classregist.student.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.jsu.classregist.common.LectureSearchCondition;
import kr.or.jsu.classregist.dto.WishlistSearchDTO;
import kr.or.jsu.classregist.student.service.ClassRegistWishlistService;
import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.student.service.info.StuInfoService;
import kr.or.jsu.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 예비수강신청 View 컨트롤러
 * @author 김수현
 * @since 2025. 10. 16.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 16.     	김수현	          최초 생성
 *  2025. 10. 17.		김수현			메서드 추가
 *  2025. 10. 18.		김수현			수강신청 관련 추가
 *
 * </pre>
 */
@Controller
@RequestMapping("/classregist/student/wish")
@RequiredArgsConstructor
@Slf4j
public class ClassRegistViewController {

	private final ClassRegistWishlistService wishlistService;
	private final CommonCodeService commonCodeService;
	private final DatabaseCache databaseCache;
	private final StuInfoService service;

	@GetMapping
	public String wishlistMain(
		@RequestParam(required = false) String searchType
        , @RequestParam(required = false) String searchWord
        , @RequestParam(required = false) String tab // tab 파라미터
        , @RequestParam(required = false) String gradeFilter  // 학년 필터
        , @RequestParam(defaultValue = "1") int page
		, @AuthenticationPrincipal CustomUserDetails userDetails
		, Model model
	) {
		StudentVO studentVO = (StudentVO) userDetails.getRealUser();
		String studentNo = studentVO.getStudentNo();
		String studentGrade = studentVO.getGradeCd();  // 학생 학년

		// tab이 없으면 기본값 'search'
		if(tab == null || tab.isEmpty()) {
			tab = "search";
		}
		
		List<?> lectureList;
	    int totalCount;

		 if("applied".equals(tab)) {
		    // 수강신청 내역 탭
			lectureList = wishlistService.getAppliedLectureList(studentNo, page, 10);
	        totalCount = wishlistService.getAppliedLectureCount(studentNo);
	        model.addAttribute("currentTab", "applied");
		 } else if ("search".equals(tab) || (searchWord != null && !searchWord.trim().isEmpty())) {
	        // 강의 검색
			// 탭이 'search'이거나 searchWord가 있으면 강의 검색
	        LectureSearchCondition searchCondition = new LectureSearchCondition();
	        searchCondition.setSearchType(searchType != null ? searchType : "ALL");
	        searchCondition.setSearchWord(searchWord != null ? searchWord.trim() : "");
	        searchCondition.setYearTermCd("2026_REG1");

	        // 검색어가 있으면 gradeFilter를 ALL로 강제
	        if (searchWord != null && !searchWord.trim().isEmpty()) {
	            // 검색 모드: gradeFilter가 명시적으로 전달되지 않으면 ALL
	            if (gradeFilter == null || gradeFilter.isEmpty()) {
	                gradeFilter = "ALL";
	            }
	        } else {
	            // 일반 조회 모드: gradeFilter가 없으면 학생 학년
	            if (gradeFilter == null || gradeFilter.isEmpty()) {
	                gradeFilter = studentGrade;
	            }
	        }

	        searchCondition.setGradeFilter(gradeFilter);  // 학년 필터
	        searchCondition.setStudentGrade(studentGrade);  // 학생 학년
	        searchCondition.setPage(page);
	        searchCondition.setPageSize(10);
	        searchCondition.calculateOffset();

	        lectureList = wishlistService.getLectureListWithSearch(searchCondition, studentNo);
	        totalCount = wishlistService.getLectureCount(searchCondition);

	        model.addAttribute("searchType", searchType);
	        model.addAttribute("searchWord", searchWord);
	        model.addAttribute("gradeFilter", gradeFilter); // 학년 필터
	        model.addAttribute("currentTab", "search");  // 탭 상태 전달
		 } else {
	        // 찜 목록
	        WishlistSearchDTO searchDTO = new WishlistSearchDTO();
	        searchDTO.setStudentNo(studentNo);
	        searchDTO.setPage(page);
	        searchDTO.setPageSize(10);

	        lectureList = wishlistService.getWishlistWithPaging(searchDTO);
	        totalCount = wishlistService.getWishlistCount(searchDTO);

	        model.addAttribute("currentTab", "wishlist");  // 탭 상태 전달
		 }

	    int totalPages = (int) Math.ceil((double) totalCount / 10);

	    model.addAttribute("lectureList", lectureList);
	    model.addAttribute("totalCount", totalCount);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("currentPage", page);

	    // 학생 정보 추가
	    model.addAttribute("student", studentVO);

	    // 최대 학점 추가
	    String gradeNum = studentVO.getGradeCd().replaceAll("[^0-9]", "");
	    Integer maxCredit = commonCodeService.getMaxCreditByGrade(gradeNum, "1"); // 1학기로 설정(시연)
	    model.addAttribute("maxCredit", maxCredit != null ? maxCredit : 24);

	    StudentDetailDTO studentInfo = service.readStuMyInfo(studentNo);
	    String colleagName = databaseCache.getCollegeName(studentInfo.getCollegeCd());
	    String studentName = databaseCache.getUserName(studentNo); // 이름
	    studentInfo.setCollegeName(colleagName);
	    model.addAttribute("studentInfo", studentInfo);
	    model.addAttribute("studentName", studentName);

	    log.info("gradeFilter: {}", gradeFilter);
	    log.info("studentGrade: {}", studentGrade);

		return "classregistration/student/wishList";
	}
}
