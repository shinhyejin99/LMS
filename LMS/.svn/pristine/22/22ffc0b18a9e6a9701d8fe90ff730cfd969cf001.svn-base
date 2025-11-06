package kr.or.jsu.lms.staff.controller.staff;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.core.validate.groups.InsertGroup;
import kr.or.jsu.dto.UserStaffDTO;
import kr.or.jsu.lms.staff.service.staff.StaffManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * * * @author 신혜진
 *
 * @since 2025. 9. 25.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 * * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 9. 28.     	신혜진	          최초 생성
 *      </pre>
 */
@Controller
@RequestMapping("/lms/staffs")
@RequiredArgsConstructor
@Slf4j
public class StaffManagementController {

	private final StaffManagementService service;
	public static final String MODELNAME = "staff";
	private final CommonCodeService commonCodeService;
	private final DatabaseCache databaseCache;
	private static final String DEFAULT_DEPT_CODE = "DEP-NONE";

	private void commonData(Model model) {
		model.addAttribute("bankList", commonCodeService.readCommonCodeList(CommonCodeSort.BANK_CODE));// 은행 정보 가져오기
		model.addAttribute("staffDeptList", databaseCache.getStaffDeptList());// 소속부서 코드

	}

	/**
	 * 전체 교직원 가져오기 * @param model
	 *
	 * @return
	 */
	// 전체 조회
	@GetMapping({ "", "/", "/list" })
	public String selectStaffList(
			@ModelAttribute("pagingInfo") PaginationInfo<Map<String, Object>> pagingInfo,
			@RequestParam(value = "filterDeptName", required = false) String filterDeptName,
			@RequestParam(value = "searchKeyword", required = false) String searchKeyword,
			Model model) {

		// 검색어 정리
		String cleanKeyword = null;
		if (searchKeyword != null) {
			String trimmed = searchKeyword.trim();
			if (!trimmed.isEmpty() && !trimmed.equals(",,,")) {
				cleanKeyword = trimmed;
			}
		}

		// 페이지 초기화
		if (pagingInfo.getCurrentPage() < 1) {
			pagingInfo.setCurrentPage(1);
		}

		// 파라미터 맵 구성
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("pagingInfo", pagingInfo);
		paramMap.put("searchKeyword", cleanKeyword);
		paramMap.put("filterDeptName", filterDeptName); // ✅ 핵심 수정 포인트

		log.info("🟢 FilterDeptName={}, SearchKeyword={}", filterDeptName, cleanKeyword);

		// DB 조회
		List<Map<String, Object>> staffList = service.readStaffManagementList(paramMap);
		Map<String, Object> employmentCountsMap = service.readStfDeptStatusCounts();

		model.addAttribute("staffList", staffList);
		model.addAttribute("employmentCountsMap", employmentCountsMap);
		model.addAttribute("filterDeptName", filterDeptName);
		model.addAttribute("searchKeyword", searchKeyword);

		commonData(model);
		return "staff/staff/staffInfoList";
	}

	/*
	 * @GetMapping("/list-view") // ⭐ 새로운 AJAX 전용 매핑 ⭐ public String
	 * selectStaffListView(@ModelAttribute("pagingInfo") PaginationInfo<Map<String,
	 * Object>> pagingInfo,
	 *
	 * @RequestParam(value = "filterStfDeptCd", required = false) String
	 * filterStfDeptCd,
	 *
	 * @RequestParam(value = "searchKeyword", required = false) String
	 * searchKeyword, Model model) {
	 *
	 * // 기존 selectStaffList 메서드와 동일한 데이터 로드 로직 수행
	 *
	 * String cleanKeyword = null; if (searchKeyword != null) { String trimmed =
	 * searchKeyword.trim();
	 *
	 * if (!trimmed.isEmpty() && !trimmed.equals(",,,")) { cleanKeyword = trimmed; }
	 * }
	 *
	 * if (pagingInfo.getCurrentPage() < 1) { pagingInfo.setCurrentPage(1); }
	 *
	 * Map<String, Object> paramMap = new HashMap<>(); paramMap.put("pagingInfo",
	 * pagingInfo);
	 *
	 * paramMap.put("searchKeyword", cleanKeyword); paramMap.put("searchKeyword",
	 * searchKeyword); paramMap.put("filterStfDeptCd", filterStfDeptCd);
	 *
	 * List<Map<String, Object>> staffList =
	 * service.readStaffManagementList(paramMap); Map<String, Object>
	 * employmentCountsMap = service.readStfDeptStatusCounts();
	 *
	 * model.addAttribute("staffList", staffList);
	 * model.addAttribute("employmentCountsMap", employmentCountsMap);
	 *
	 * model.addAttribute("filterStfDeptCd", filterStfDeptCd);
	 * model.addAttribute("searchKeyword", searchKeyword);
	 *
	 * // commonData(model); // 공통 데이터는 목록 뷰에서 필요 없으면 생략 가능
	 *
	 * return "staff/staff/staffInfoList"; // 👈 순수한 콘텐츠 뷰 이름만 반환 }
	 */
	/**
	 * 한명의 교직원 정보 가져오기 * @param staffNo
	 *
	 * @param model
	 * @return
	 */
	// 상세조회
	@GetMapping("/{staffNo}")
	public String selectStaffDetail(@PathVariable("staffNo") String staffNo, Model model) {
		UserStaffDTO staff = service.readStaffManagement(staffNo);
		model.addAttribute("staff", staff);

		// JSP 상세 페이지에서 은행 코드의 Selected 처리를 위해 공통 데이터 로드
		commonData(model);

		// 현재 사용자의 은행 코드 (JSP에서 <c:if test="${bank.commonCd eq userBankCd}"> 처리용)
		String userBankCd = staff.getUserInfo().getBankCode();
		model.addAttribute("userBankCd", userBankCd);

		return "staff/staff/staffInfoDetail";
	}

	// 등록 폼
	@GetMapping("/create")
	public String createStaffForm(Model model) {
		commonData(model);
		model.addAttribute(MODELNAME, new UserStaffDTO());

		return "staff/staff/staffInfoForm";
	}


	// 등록 프로세스
	@PostMapping("/create")
	public String createStaff(@Validated(InsertGroup.class) @ModelAttribute(MODELNAME) UserStaffDTO staff,
			BindingResult errors, Model model) {

		// 유효성 검사
		if (errors.hasErrors()) {
			log.info("교직원 등록 유효성 검사 실패: {} 개 오류", errors.getErrorCount());
			commonData(model);
			return "staff/staff/staffInfoForm";
		}

		// 초기 비밀번호 해시 처리
		if (!StringUtils.hasText(staff.getUserInfo().getPwHash())
				&& StringUtils.hasText(staff.getUserInfo().getRegiNo())) {
			String initialPassword = staff.getUserInfo().getRegiNo().substring(6); // 주민번호 뒷 7자리
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			staff.getUserInfo().setPwHash(encoder.encode(initialPassword));
		}

		if (staff.getUserInfo().getCreateAt() == null) {
			staff.getUserInfo().setCreateAt(LocalDateTime.now());
		}

		// 부서 기본값 설정
		if (!StringUtils.hasText(staff.getStaffDeptInfo().getStfDeptCd())) {
			staff.getStaffDeptInfo().setStfDeptCd(DEFAULT_DEPT_CODE);
		}

		// DTO → VO 변환 후 DB 저장
		try {
			// UserStaffDTO UserStaffDTO = new UserStaffDTO(); // 불필요한 객체 생성/복사 제거
			service.createStaffManagement(staff);

			return "redirect:/lms/staffs";
		} catch (Exception e) {
			log.error("교직원 등록 중 DB 처리 오류", e);
			model.addAttribute("errorMessage", "교직원 정보 등록 중 오류가 발생했습니다.");
			commonData(model); // 공통 데이터 로드

			return "staff/staff/staffInfoForm";
		}
	}


	// 수정 폼
	@GetMapping("/modify")
	public String modifyStaffForm(@RequestParam("staffNo") String staffNo, Model model) {
		UserStaffDTO staff = service.readStaffManagement(staffNo);
		model.addAttribute("staff", staff);

		commonData(model); // 공통 데이터 로드
		return "staff/staff/staffInfoEdit";
	}

	// 수정 프로세스
	@PostMapping("/modify")
	public String modifyStaff(UserStaffDTO staff) {
//		service.modifyStaffDetail(staff);
		// 수정 후 상세 페이지로 이동하도록 URL 파라미터를 정확하게 설정합니다.
		// DTO에서 StaffVO를 가져와서 StaffNo를 추출해야 합니다.
		String staffNo = staff.getStaffInfo() != null ? staff.getStaffInfo().getStaffNo() : null;

		if (staffNo != null) {
			return "redirect:/lms/staffs/" + staffNo;
		} else {
			// staffNo가 없을 경우 목록으로 리다이렉트 (에러 처리)
			log.error("교직원 수정 후 리다이렉트 실패: StaffNo가 없습니다.");
			return "redirect:/lms/staffs";
		}
	}

}
