package kr.or.jsu.lms.staff.controller.professor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.core.validate.groups.InsertGroup;
import kr.or.jsu.dto.ProfessorInfoDTO;
import kr.or.jsu.lms.staff.service.professor.StaffProfessorInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lms/staff/professors")
@RequiredArgsConstructor
public class StaffProfessorInfoController {

	private final StaffProfessorInfoService service;
	public static final String MODELNAME = "professor";
	private final CommonCodeService commonCodeService;
	private final DatabaseCache databaseCache;
	private static final String DEFAULT_PROFESSOR_POSITION_CODE = "";
	private static final String DEFAULT_APPOINTMENT_CODE = "PRF_APPNT_REG";
	private static final String DEFAULT_DEPT_CODE = "DEP-NONE";

	private void commonData(Model model) {
		model.addAttribute("bankList", commonCodeService.readCommonCodeList(CommonCodeSort.BANK_CODE));
		model.addAttribute("prfStatusList", commonCodeService.readCommonCodeList(CommonCodeSort.PRF_STATUS_CD));
		model.addAttribute("profAppntList", commonCodeService.readCommonCodeList(CommonCodeSort.PRF_APPNT_CD));
		model.addAttribute("prfPositList", commonCodeService.readCommonCodeList(CommonCodeSort.PRF_POSIT_CD));
		model.addAttribute("univDeptList", databaseCache.getUnivDeptList());
		model.addAttribute("collegeList", databaseCache.getCollegeList());
	}

	// ⭐ 교수 목록 조회 및 필터링 (Map 기반 파라미터 전달 유지) ⭐
	@GetMapping({ "", "/list" })
	public String selectStaffProfessorInfoList(
			@ModelAttribute("pagingInfo") PaginationInfo<ProfessorInfoDTO> pagingInfo,
			@RequestParam(value = "filterEmploymentStatus", required = false) String filterEmploymentStatus,
			@RequestParam(value = "filterCollege", required = false) String filterCollege,
			@RequestParam(value = "filterDepartment", required = false) String filterDepartment,
			@RequestParam(value = "filterPosition", required = false) String filterPosition,
			@RequestParam(value = "searchKeyword", required = false) String searchKeyword, Model model) {

		// 1. Pagination 및 검색 키워드 정리
		String cleanKeyword = null;
		if (searchKeyword != null) {
			String trimmed = searchKeyword.trim();
			if (!trimmed.isEmpty() && !trimmed.equals(",,,")) {
				cleanKeyword = trimmed;
			}
		}

		if (pagingInfo.getCurrentPage() < 1) {
			pagingInfo.setCurrentPage(1);
		}

		// 2. Service Layer로 전달할 단일 Map 생성
		Map<String, Object> paramMap = new HashMap<>();

		// 페이징 정보 객체 자체와 검색 키워드를 Map에 담아 서비스로 전달
		paramMap.put("pagingInfo", pagingInfo);
		paramMap.put("searchKeyword", cleanKeyword);

		// 필터 조건 Map에 담기
		paramMap.put("filterEmploymentStatus", filterEmploymentStatus);
		paramMap.put("filterCollege", filterCollege);
		paramMap.put("filterDepartment", filterDepartment);
		paramMap.put("filterPosition", filterPosition);
		

		log.info("Filter Status: {}, College: {}, Department: {}, Position: {}, SearchKeyword: {}",
				filterEmploymentStatus, filterCollege, filterDepartment, filterPosition, cleanKeyword);

		// 3. Service Layer 호출 (단일 Map 전달)
		List<ProfessorInfoDTO> professorList = service.readStaffProfessorInfoList(paramMap);
		Map<String, Integer> employmentCounts = service.readEmploymentStatusCounts();
		// 4. 통계 데이터 조회
		Map<String, Integer> employmentCountsMap = service.readEmploymentStatusCounts();
		model.addAttribute("employmentCountsRaw", employmentCountsMap);
		model.addAttribute("employmentCountsMap", employmentCounts);
		// 5. Model에 데이터 및 필터링 상태 전달
		model.addAttribute("professorList", professorList);
		model.addAttribute("searchKeyword", searchKeyword); // 원본 검색 키워드 유지
		model.addAttribute("filterEmploymentStatus", filterEmploymentStatus);
		model.addAttribute("filterCollege", filterCollege);
		model.addAttribute("filterDepartment", filterDepartment);
		model.addAttribute("filterPosition", filterPosition);
		model.addAttribute("professorList", professorList);

		return "staff/professor/staffProfessorInfoList";
	}
	/*
	 * @GetMapping("/list-view") // ⭐ 새로운 AJAX 전용 매핑 ⭐ public String
	 * selectStaffProfessorInfoListView(
	 *
	 * @ModelAttribute("pagingInfo") PaginationInfo<ProfessorInfoDTO> pagingInfo,
	 *
	 * @RequestParam(value = "filterEmploymentStatus", required = false) String
	 * filterEmploymentStatus,
	 *
	 * @RequestParam(value = "filterCollege", required = false) String
	 * filterCollege,
	 *
	 * @RequestParam(value = "filterDepartment", required = false) String
	 * filterDepartment,
	 *
	 * @RequestParam(value = "filterPosition", required = false) String
	 * filterPosition,
	 *
	 * @RequestParam(value = "searchKeyword", required = false) String
	 * searchKeyword, Model model) {
	 *
	 * // 기존 selectStaffProfessorInfoList 메서드와 동일한 데이터 로드 로직 수행
	 *
	 * String cleanKeyword = null; if (searchKeyword != null) { String trimmed =
	 * searchKeyword.trim(); if (!trimmed.isEmpty() && !trimmed.equals(",,,")) {
	 * cleanKeyword = trimmed; } }
	 *
	 * if (pagingInfo.getCurrentPage() < 1) { pagingInfo.setCurrentPage(1); }
	 *
	 * Map<String, Object> paramMap = new HashMap<>(); paramMap.put("pagingInfo",
	 * pagingInfo); paramMap.put("searchKeyword", cleanKeyword);
	 *
	 * paramMap.put("filterEmploymentStatus", filterEmploymentStatus);
	 * paramMap.put("filterCollege", filterCollege);
	 * paramMap.put("filterDepartment", filterDepartment);
	 * paramMap.put("filterPosition", filterPosition);
	 *
	 * List<ProfessorInfoDTO> professorList =
	 * service.readStaffProfessorInfoList(paramMap); Map<String, Integer>
	 * employmentCounts = service.readEmploymentStatusCounts(); Map<String, Integer>
	 * employmentCountsMap = service.readEmploymentStatusCounts();
	 *
	 * model.addAttribute("employmentCountsRaw", employmentCountsMap);
	 * model.addAttribute("employmentCountsMap", employmentCounts);
	 * model.addAttribute("professorList", professorList);
	 * model.addAttribute("searchKeyword", searchKeyword);
	 * model.addAttribute("filterEmploymentStatus", filterEmploymentStatus);
	 * model.addAttribute("filterCollege", filterCollege);
	 * model.addAttribute("filterDepartment", filterDepartment);
	 * model.addAttribute("filterPosition", filterPosition);
	 *
	 * return "staff/professor/staffProfessorInfoList"; // 👈 순수한 콘텐츠 뷰 이름만 반환 }
	 */

	@GetMapping("/{professorNo}")
	public String selectStaffProfessorInfoDetail(@PathVariable String professorNo, Model model) {
		model.addAttribute("professor", service.readStaffProfessorInfo(professorNo));
		return "staff/professor/staffProfessorInfoDetail";
	}

	@GetMapping("/create")
	public String createStaffProfessorInfoForm(Model model) {
		commonData(model);
		model.addAttribute(MODELNAME, new ProfessorInfoDTO());
		return "staff/professor/staffProfessorInfoForm";
	}

	@PostMapping("/create")
	public String createStaffInfoProfessor(
			@Validated(InsertGroup.class) @ModelAttribute(MODELNAME) ProfessorInfoDTO professor, BindingResult errors,
			Model model) {

		// 초기 비밀번호 해시 처리
		if (!StringUtils.hasText(professor.getPwHash()) && StringUtils.hasText(professor.getRegiNo())) {
			String initialPassword = professor.getRegiNo().substring(6); // 주민번호 뒷 7자리
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			professor.setPwHash(encoder.encode(initialPassword));
		}

		// 생성일자 기본값 설정
		if (professor.getCreateAt() == null) {
			professor.setCreateAt(LocalDateTime.now());
		}

		//  직위, 임용, 학과 기본값 설정 (유효성 검사 전에 실행되도록 위치 변경)
		if (!StringUtils.hasText(professor.getPrfPositCd())) {
			professor.setPrfPositCd(DEFAULT_PROFESSOR_POSITION_CODE);
		}
		if (!StringUtils.hasText(professor.getPrfAppntCd())) {
			professor.setPrfAppntCd(DEFAULT_APPOINTMENT_CODE);
		}
		// DTO의 deptCd는 Service에서 ProfessorVO의 univDeptCd로 복사됨
		if (!StringUtils.hasText(professor.getDeptCd())) {
			professor.setDeptCd(DEFAULT_DEPT_CODE);
		}

		if (!StringUtils.hasText(professor.getLastName())) {
			professor.setLastName("");
		}
		if (!StringUtils.hasText(professor.getFirstName())) {
			professor.setFirstName("");
		}

		// ⭐ 5. 유효성 검사 (순서 변경) ⭐
		if (errors.hasErrors()) {
			log.info("교수 등록 유효성 검사 실패: {} 개 오류", errors.getErrorCount());
			commonData(model);
			return "staff/professor/staffProfessorInfoForm";
		}

		// 6. DB 저장
		try {
			service.createStaffProfessorInfo(professor);
			return "redirect:/lms/staff/professors";
		} catch (Exception e) {
			log.error("교수 등록 중 DB 처리 오류", e);
			model.addAttribute("errorMessage", "교수 정보 등록 중 오류가 발생했습니다.");
			commonData(model);
			return "staff/professor/staffProfessorInfoForm";
		}
	}

	/**
	 * 재직 상태별 단과대학 통계 조회 (AJAX)
	 */
	@GetMapping("/stats/college")
	@ResponseBody
	public ResponseEntity<Map<String, Integer>> selectProfessorStatsByCollege(@RequestParam String status) {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("status", status);

		Map<String, Integer> stats = service.readProfessorStatsByCollege(paramMap);
		return ResponseEntity.ok(stats);
	}

	/**
	 * 단과대학 내 학과별 통계 조회 (AJAX)
	 */
	@GetMapping("/stats/department")
	@ResponseBody
	public ResponseEntity<Map<String, Integer>> selectProfessorStatsByDepartment(@RequestParam String status,
			@RequestParam String college) {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("status", status);
		paramMap.put("college", college);

		Map<String, Integer> stats = service.readProfessorStatsByDepartment(paramMap);
		return ResponseEntity.ok(stats);
	}

	/**
	 * 학과 내 임용별 통계 조회 (AJAX)
	 */
	@GetMapping("/stats/position")
	@ResponseBody
	public ResponseEntity<Map<String, Integer>> selectProfessorStatsByPosition(@RequestParam String status,
			@RequestParam String college, @RequestParam String department) {

		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("status", status);
		paramMap.put("college", college);
		paramMap.put("department", department);

		Map<String, Integer> stats = service.readProfessorStatsByPosition(paramMap);
		return ResponseEntity.ok(stats);
	}
}