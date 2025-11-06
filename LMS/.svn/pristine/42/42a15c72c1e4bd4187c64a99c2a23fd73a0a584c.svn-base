package kr.or.jsu.lms.staff.controller.department;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.dto.DepartmentDetailDTO;
import kr.or.jsu.lms.staff.service.department.StaffDepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/lms/staff/departments")
public class StaffDepartmentController {

	private final StaffDepartmentService service;
	private final CommonCodeService commonCodeService;
	private final DatabaseCache databaseCache;

	private final ObjectMapper objectMapper;

	/** 공통 코드 및 캐시 데이터를 모델에 추가 */
	private void commonData(Model model) {
		model.addAttribute("completionList", commonCodeService.readCommonCodeList(CommonCodeSort.COMPLETION_CD));
		model.addAttribute("univDeptList", databaseCache.getUnivDeptList());
		model.addAttribute("gradeList", commonCodeService.readCommonCodeList(CommonCodeSort.GRADE_CD));
		model.addAttribute("termList", commonCodeService.readCommonCodeList(CommonCodeSort.TERM_CD));
	}

	/**
	 * 학과 목록 페이지 및 페이징 조회
	 */
	@GetMapping
	public String selectStaffDepartmentList(
			@RequestParam(required = false, defaultValue = "") String searchKeyword,
			@RequestParam(required = false, defaultValue = "") String filterType,
			@RequestParam(required = false, defaultValue = "1") int page, Model model) {

		// 1. 상태 카운트 조회를 위한 파라미터 맵 준비
		Map<String, Object> countParamMap = new HashMap<>();
		countParamMap.put("searchKeyword", searchKeyword);

		try {
			// 2. 전체 Active/Deleted 카운트 조회 (필터/KPI용)
			Map<String, Integer> statusCounts = service.readDepartmentStatusCounts(countParamMap);

			// 💡 차트 통계용 전체 학과 목록 조회 (80개 데이터)
			List<DepartmentDetailDTO> allChartDepts = service.selectAllDepartmentDetails();

			// 💡 JSON 데이터 준비
			String allChartDeptsJson = objectMapper.writeValueAsString(allChartDepts);
			model.addAttribute("allChartDeptsJson", allChartDeptsJson);

			// 3. Model에 카운트 값 추가 (JSP가 이 변수를 사용함)
			if (statusCounts != null) {
				model.addAttribute("activeDeptCount", statusCounts.get("ACTIVE_DEPT_COUNT"));
				model.addAttribute("deletedDeptCount", statusCounts.get("DELETED_DEPT_COUNT"));
			} else {
				model.addAttribute("activeDeptCount", 0);
				model.addAttribute("deletedDeptCount", 0);
			}

			int requestedPage = page;

			// 4. 목록 조회 및 페이징 처리 로직
			PaginationInfo<Map<String, Object>> pagingInfo = new PaginationInfo<>(10, 5);
			pagingInfo.setCurrentPage(requestedPage);

			// service.readDepartmentList 호출 시 filterType과 searchKeyword가 포함된 paramMap이 사용되어야 함
			List<Map<String, Object>> departmentList = service.readDepartmentList(pagingInfo, searchKeyword,
					filterType);

			// 페이지 복원 로직
			if (requestedPage > 0) {
				int finalPage = Math.min(requestedPage, pagingInfo.getTotalPage());
				if (finalPage < 1 && pagingInfo.getTotalRecord() > 0) {
					finalPage = 1;
				} else if (finalPage < 1) {
					finalPage = 1;
				}
				pagingInfo.setCurrentPage(finalPage);
			}

			// Model에 데이터 추가
			model.addAttribute("searchKeyword", searchKeyword);
			model.addAttribute("filterType", filterType);

			String staffunivDeptListJson = objectMapper.writeValueAsString(departmentList);
			model.addAttribute("staffunivDeptListJson", staffunivDeptListJson);
			model.addAttribute("staffunivDeptList", departmentList);
			model.addAttribute("pagingInfo", pagingInfo);

		} catch (Exception e) {
			log.error("학과 목록 조회 중 오류 발생", e);

			// 오류 발생 시에도 Model에 0을 넣어줘야 JSP/JS에서 오류 없이 동작합니다.
			model.addAttribute("activeDeptCount", 0);
			model.addAttribute("deletedDeptCount", 0);
			try {
				model.addAttribute("allChartDeptsJson", objectMapper.writeValueAsString(List.of()));
			} catch (Exception jsonE) {
				model.addAttribute("allChartDeptsJson", "[]");
			}

			model.addAttribute("pagingInfo", new PaginationInfo<>(10, 5));
			model.addAttribute("staffunivDeptList", List.of());
		}

		// 공통 데이터는 항상 추가
		commonData(model);

		return "staff/department/staffDepartmentList";
	}
// ---

	/**
	 * 💡 [추가된 부분] 학과 상세 모달의 HTML 프래그먼트를 AJAX로 로드합니다. (404 에러 해결)
	 * URL: /lms/staff/departments/detail/fragment/{univDeptCd}
	 * @param univDeptCd 조회할 학과 코드
	 * @param model 데이터 모델
	 * @return 뷰 이름 (staffDepartmentDetail_fragment.jsp)
	 */
	@GetMapping("/detail/fragment/{univDeptCd}")
	public String getDepartmentDetailFragment(@PathVariable String univDeptCd, Model model) {
	    DepartmentDetailDTO detail = service.readDepartment(univDeptCd);
	    model.addAttribute("department", detail);
	    commonData(model);
	    // 이 뷰는 모달의 BODY에 삽입될 HTML 조각만 포함합니다.
	    return "staff/department/staffDepartmentDetail_fragment";
	}
// ---

	/**
	 * 상세 조회 페이지 (Full Page View 복원)
	 * URL: /lms/staff/departments/{univDeptCd}
	 */
	@GetMapping("/{univDeptCd}")
	public String selectStaffDepartmentDetail(@PathVariable String univDeptCd, Model model) {
		DepartmentDetailDTO detail = service.readDepartment(univDeptCd);
		model.addAttribute("department", detail);
		commonData(model);
		// 🚨 뷰 이름을 'staffDepartmentDetail'로 복원합니다.
		return "staff/department/staffDepartmentDetail";
	}

	/**
	 * 등록 폼
	 */
	@GetMapping("/create")
	public String createStaffDepartmentForm(Model model) {
		commonData(model);
		// 🚨 뷰 이름을 'staffDepartmentCreate'로 복원합니다.
		return "staff/department/staffDepartmentCreate";
	}

	/**
	 * 등록 API
	 */
	@PostMapping("/api")
	@ResponseBody
	public ResponseEntity<String> createStaffDepartment(@RequestBody DepartmentDetailDTO departmentDTO) {
		log.info("학과 등록 요청: {}", departmentDTO.getUnivDeptName());
		try {
			boolean success = service.createDepartment(departmentDTO);
			// 성공 시 201 Created 반환
			return success ? ResponseEntity.status(HttpStatus.CREATED).body("학과 등록 성공")
					: ResponseEntity.status(HttpStatus.BAD_REQUEST).body("학과 등록 실패");
		} catch (IllegalArgumentException e) {
			log.error("학과 등록 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/**
	 * 수정 폼
	 */
	@GetMapping("/modify/{univDeptCd}")
	public String modifyStaffDepartmentForm(@PathVariable String univDeptCd, Model model) {
		DepartmentDetailDTO detail = service.readDepartment(univDeptCd);
		model.addAttribute("department", detail);
		commonData(model);
		// 🚨 뷰 이름을 'staffDepartmentEdit'로 복원합니다.
		return "staff/department/staffDepartmentEdit";
	}

	/**
	 * 수정 API
	 */
	@PostMapping("/api/{univDeptCd}")
	@ResponseBody
	public ResponseEntity<String> modifyStaffDepartment(@PathVariable String univDeptCd,
			@RequestBody DepartmentDetailDTO departmentDTO) {
		log.info("학과 정보 수정 요청 (POST): {} for {}", departmentDTO.getUnivDeptName(), univDeptCd);
		try {
			departmentDTO.setUnivDeptCd(univDeptCd);
			// Service에서 학과명, 학과장, 연락처, 상태(폐지일)만 수정한다고 가정
			service.modifyDepartment(departmentDTO);

			return ResponseEntity.ok("학과 정보 수정 성공");

		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("찾을 수 없음")) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}