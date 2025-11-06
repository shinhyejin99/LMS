package kr.or.jsu.lms.staff.controller.subject;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.dto.SubjectInfoDetailDTO;
import kr.or.jsu.lms.staff.service.subject.StaffSubjectService;
import kr.or.jsu.vo.CommonCodeVO;
import kr.or.jsu.vo.SubjectVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lms/staff/staffSubjects")
@RequiredArgsConstructor
public class StaffSubjectController {

	private final StaffSubjectService service;
	private final CommonCodeService commonCodeService;
	private final DatabaseCache databaseCache;
	private final ObjectMapper objectMapper;

	private void commonData(Model model) {
		model.addAttribute("completionList", commonCodeService.readCommonCodeList(CommonCodeSort.COMPLETION_CD));
		model.addAttribute("univDeptList", databaseCache.getUnivDeptList());
		model.addAttribute("gradeList", commonCodeService.readCommonCodeList(CommonCodeSort.GRADE_CD));
		model.addAttribute("termList", commonCodeService.readCommonCodeList(CommonCodeSort.TERM_CD));
	}

	// 1. R: 전체 조회 (페이징 및 검색 포함)
	@GetMapping
	public String selectstaffSbjectList(PaginationInfo<?> pagingInfo,
			@RequestParam(required = false) String searchKeyword, @RequestParam(required = false) String filterType,
			Model model) {

		// ⚠️ [주의] 프론트엔드 (JSP/Thymeleaf)에서 PaginationInfo의 currentPage, searchKeyword, filterType
		// 파라미터를 다음 페이지 링크에 반드시 포함하여 요청해야 페이지 전환이 정상적으로 동작합니다.

		if (pagingInfo.getCurrentPage() <= 0) {
			pagingInfo.setCurrentPage(1);
		}

		// 1. 교과목 목록 및 페이징 조회
		// ✅ [복원] 필터링 기능이 다시 동작하도록 filterType 변수를 그대로 service에 전달합니다.
		// 이전에는 null로 고정되어 필터링이 무시되었습니다.
		List<Map<String, Object>> staffSubjectList = service.readStaffSubjectList(pagingInfo, searchKeyword,
				filterType);

		// 2. 이수 구분별 카운트 및 차트 통계 조회
		// 💡 통계 데이터는 필터링 조건과 무관하게 전체 데이터를 기준으로 호출됩니다.
		List<Map<String, Object>> subjectCountMap = service.readSubjectCountByType();
		List<Map<String, Object>> deptCounts = service.readSubjectCountByDept();

		// ⭐ [추가] 학과별 시수 평균 조회 ⭐
		List<Map<String, Object>> deptAverageHour = service.readAverageHourByDept();

		// 3. KPI 통계 조회: 전체 활성 교과목 수 및 전체 평균 학점 조회
		int totalActiveCount = service.readTotalActiveSubjectCount();
		Double globalAverageCredit = service.readGlobalAverageCredit();

		// 4. List<Map>을 JSON 문자열로 변환
		String deptCountsJson;
		String creditHourDataJson;
		// ⭐ [추가] JSON 변수 선언 ⭐
		String deptAverageHourJson;

		try {
			deptCountsJson = objectMapper.writeValueAsString(deptCounts);
			creditHourDataJson = objectMapper.writeValueAsString(staffSubjectList);
			// ⭐ [추가] 학과별 시수 평균 데이터를 JSON으로 변환 ⭐
			deptAverageHourJson = objectMapper.writeValueAsString(deptAverageHour);

		} catch (Exception e) {
			log.error("통계 데이터 JSON 변환 실패", e);
			deptCountsJson = "[]";
			creditHourDataJson = "[]";
			// ⭐ [추가] 실패 시 빈 배열 JSON 문자열로 대체
			deptAverageHourJson = "[]";
		}

		// 5. 공통 데이터 로드
		commonData(model);

		// 6. 모델에 데이터 추가
		model.addAttribute("staffSubjectList", staffSubjectList);
		model.addAttribute("pagingInfo", pagingInfo);
		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("filterType", filterType);

		// JSON 문자열로 변환된 데이터를 모델에 추가
		model.addAttribute("deptCounts", deptCountsJson);
		model.addAttribute("creditHourData", creditHourDataJson);
		// ⭐ [추가] 학과별 시수 평균 JSON을 모델에 추가 ⭐
		model.addAttribute("deptAverageHour", deptAverageHourJson);

		// 7. KPI 통계 데이터를 모델에 추가
		model.addAttribute("totalActiveCount", totalActiveCount);
		model.addAttribute("globalAverageCredit", globalAverageCredit);

		// 8. 모델에 필터 목록 및 카운트 맵 추가
		model.addAttribute("subjectCountMap", subjectCountMap);
		List<CommonCodeVO> completionList = commonCodeService.readCommonCodeList(CommonCodeSort.COMPLETION_CD);
		model.addAttribute("completionList", completionList);

		return "staff/subject/staffSubjectList";
	}

	// 2-1. R: 상세조회 (풀 페이지 이동)
	@GetMapping("/page/{subjectCd}")
	public String selectstaffSbjectDetail(@PathVariable("subjectCd") String subjectCd, Model model) {
		try {
			SubjectInfoDetailDTO subject = service.readStaffSubject(subjectCd);
			model.addAttribute("subject", subject);
			commonData(model);
		} catch (ClassCastException e) {
			log.error("교과목 상세 조회 실패(ClassCastException): Service/Mapper 리턴 타입 불일치. {}", e.getMessage());
			return "redirect:/lms/staff/staffSubjects";
		} catch (RuntimeException e) {
			log.error("교과목 상세 조회 실패: {}", e.getMessage());
			return "redirect:/lms/staff/staffSubjects";
		}
		return "staff/subject/staffSubjectDetail";
	}

	// 2-2. R: 상세조회 (모달용 HTML Fragment)
	@GetMapping(value = "/detail/fragment/{subjectCd}")
	public String selectstaffSbjectDetailFragment(@PathVariable("subjectCd") String subjectCd, Model model) {
		try {
			SubjectInfoDetailDTO subject = service.readStaffSubject(subjectCd);

			model.addAttribute("subject", subject);

		} catch (ClassCastException e) {
			log.error("교과목 상세 조회 실패(ClassCastException): Service/Mapper 리턴 타입 불일치. {}", e.getMessage());
			model.addAttribute("errorTitle", "데이터 로드 오류");
			model.addAttribute("errorMessage", "상세 정보 로드 중 클래스 변환 오류가 발생했습니다.");
			return "common/error/modal_error_fragment";
		} catch (RuntimeException e) {
			log.error("교과목 상세 조회 실패: {}", e.getMessage());
			model.addAttribute("errorTitle", "데이터 로드 오류");
			model.addAttribute("errorMessage", String.format("교과목 코드 %s 에 해당하는 상세 정보를 찾을 수 없습니다.", subjectCd));
			return "common/error/modal_error_fragment";
		}
		// 모달 본문에 들어갈 Fragment JSP를 반환합니다.
		return "staff/subject/staffSubjectDetail_fragment";
	}

	// 3-1. C: 등록 폼
	@GetMapping("/create")
	public String createstaffSbjectForm(Model model) {
		model.addAttribute("subject", new SubjectVO());
		commonData(model);
		return "staff/subject/staffSubjectCreate";
	}

	// 3-2. C: 등록 프로세스
	@PostMapping("/create")
	public String createstaffSbject(@ModelAttribute SubjectVO subject, RedirectAttributes redirectAttributes) {
		try {
			service.createStaffSubject(subject);
			redirectAttributes.addFlashAttribute("message", "교과목이 성공적으로 등록되었습니다.");
			return "redirect:/lms/staff/staffSubjects";
		} catch (Exception e) {
			log.error("교과목 등록 실패: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("error", "교과목 등록에 실패했습니다. " + e.getMessage());
			return "redirect:/lms/staff/staffSubjects/create";
		}
	}

	// 4-1. U: 수정 폼 (모달용 HTML Fragment 반환)
	@GetMapping("/modify/fragment/{subjectCd}")
	public String modifystaffSbjectFormFragment(@PathVariable("subjectCd") String subjectCd, Model model) {
		try {
			SubjectInfoDetailDTO subject = service.readStaffSubject(subjectCd);
			model.addAttribute("subject", subject);
			commonData(model);
		} catch (RuntimeException e) {
			log.error("교과목 수정 폼 로드 실패: {}", e.getMessage());
			model.addAttribute("errorTitle", "데이터 로드 오류");
			model.addAttribute("errorMessage", "수정 정보를 불러오는 데 실패했습니다.");
			return "common/error/modal_error_fragment";
		}

		return "staff/subject/staffSubjectEdit_fragment";
	}

	// 4-2. U: 수정 프로세스 (모달 폼 제출 처리)
	@PostMapping("/modify")
	public String modifystaffSbject(@ModelAttribute SubjectInfoDetailDTO subject, RedirectAttributes redirectAttributes) {
	    try {
	        // Service에서 교과목 수정 및 폐지 처리 여부를 boolean으로 반환받지만, 결과를 분기하는 데 사용하지 않습니다.
	        service.modifyStaffSubject(subject);
	        log.info("교과목 수정 성공: {}", subject.getSubjectCd());

	        // 단일 성공 메시지만 설정합니다.
	        redirectAttributes.addFlashAttribute("message", "교과목이 성공적으로 수정되었습니다.");

	        // ✅ 목록 페이지로 리다이렉트
	        return "redirect:/lms/staff/staffSubjects";

	    } catch (Exception e) {
	        log.error("교과목 수정 실패: {}", e.getMessage());

	        // 3. 실패 시 목록 페이지로 리다이렉트하며 에러 메시지 전달
	        redirectAttributes.addFlashAttribute("error", "교과목 수정에 실패했습니다: " + e.getMessage());

	        // 실패하더라도 목록 화면으로 돌려보내는 것이 사용자 경험상 낫습니다.
	        return "redirect:/lms/staff/staffSubjects";
	    }
	}
}
