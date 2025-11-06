package kr.or.jsu.lms.staff.controller.student;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.or.jsu.core.common.service.CommonCodeService;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.dto.ProfessorInfoDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.staff.service.professor.StaffProfessorInfoService;
import kr.or.jsu.lms.staff.service.student.StaffStudentInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lms/staff/students")
@RequiredArgsConstructor
public class StaffStudentInfoController {

	public static final String MODELNAME = "student";
	private final StaffStudentInfoService service;
	private final CommonCodeService commonCodeService;
	private final DatabaseCache databaseCache;
	private final StaffProfessorInfoService staffProfessorInfoService;

	// 공통 코드 목록을 Model에 추가하는 보조 메서드
	private void commonData(Model model) {
		model.addAttribute("bankList", commonCodeService.readCommonCodeList(CommonCodeSort.BANK_CODE));
		model.addAttribute("gradeList", commonCodeService.readCommonCodeList(CommonCodeSort.GRADE_CD));
		model.addAttribute("statusList", commonCodeService.readCommonCodeList(CommonCodeSort.STU_STATUS_CD));
		model.addAttribute("militaryList", commonCodeService.readCommonCodeList(CommonCodeSort.MILITARY_TYPE_CD));
		model.addAttribute("entranceTypeList", commonCodeService.readCommonCodeList(CommonCodeSort.ENTRANCE_TYPE_CD));
		model.addAttribute("univDeptList", databaseCache.getUnivDeptList());
		model.addAttribute("collegeList", databaseCache.getCollegeList());
	}

	/**
	 * 학생 성별 통계
	 *
	 * @return
	 */
	@GetMapping("/stats/overall/gender")
	@ResponseBody
	public Map<String, Integer> getOverallGenderStats() {
		return service.getOverallGenderStatistics();
	}

	/**
	 *
	 * 전체 학생의 학년별 통계
	 */
	@GetMapping("/stats/overall/grade")
	@ResponseBody
	public Map<String, Integer> getOverallGradeStats() {
		return service.getOverallGradeStatistics();

	}

	/**
	 * 전체 학생 정보 가져오기
	 */
	@GetMapping({ "", "/", "/list" })
	public String selectStudentList(@ModelAttribute("pagingInfo") PaginationInfo<Map<String, Object>> pagingInfo,
			@RequestParam(value = "filterStatus", required = false) String filterStatus,
			@RequestParam(value = "filterCollege", required = false) String filterCollege,
			@RequestParam(value = "filterDepartment", required = false) String filterDepartment,
			@RequestParam(value = "filterGrade", required = false) String filterGrade,
			@RequestParam(value = "searchKeyword", required = false) String searchKeyword, Model model) {
		// ... (목록 조회 로직 생략)
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

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("pagingInfo", pagingInfo);
		paramMap.put("searchKeyword", cleanKeyword);

		paramMap.put("filterStatus", filterStatus);
		paramMap.put("filterCollege", filterCollege);
		paramMap.put("filterDepartment", filterDepartment);
		paramMap.put("filterGrade", filterGrade);

		log.info("Filter Status: {}, College: {}, Department: {}, Grade: {}, SearchKeyword: {}", filterStatus,
				filterCollege, filterDepartment, filterGrade, cleanKeyword);

		List<Map<String, Object>> studentList = service.readStaffStudentInfoList(paramMap);

		// ⭐️ 1. 상단 재적 상태 카드 카운트 (필터 무시 - 전체 기준) ⭐️
		Map<String, Long> statusCountsMap = service.readStudentStatusCounts();
		model.addAttribute("statusCountsMap", statusCountsMap);

		// ⭐️ 2. 차트 데이터 추가 (필터 무시 - 전체 기준) ⭐️
		Map<String, Integer> genderStatsMap = getOverallGenderStats();
		Map<String, Integer> gradeStatsMap = getOverallGradeStats();

		model.addAttribute("genderStatsMap", genderStatsMap);
		model.addAttribute("gradeStatsMap", gradeStatsMap);

		model.addAttribute("searchKeyword", searchKeyword);
		model.addAttribute("studentList", studentList);

		model.addAttribute("filterStatus", filterStatus);
		model.addAttribute("filterCollege", filterCollege);
		model.addAttribute("filterDepartment", filterDepartment);
		model.addAttribute("filterGrade", filterGrade);

		return "staff/student/staffStudentInfoList";
	}

	/**
	 * 한명의 학생 정보 가져오기 (상세 뷰)
	 */
	@GetMapping("/{studentNo}")
	public String selectStudentDetailInfo(@PathVariable("studentNo") String studentNo, Model model) {
		StudentDetailDTO student = service.readStaffStudentInfo(studentNo);
		model.addAttribute("student", student);
		commonData(model);
		return "staff/student/staffStudentInfoDetail";
	}

	/**
	 * 지도교수 검색 팝업 화면을 제공합니다.
	 */
	@GetMapping("/professor/search")
	@ResponseBody
	public List<ProfessorInfoDTO> searchProfessorList(@RequestParam(name = "deptCd", required = false) String deptCd,
			@RequestParam(name = "searchKeyword", required = false) String searchKeyword) {

		log.info("교수 검색 요청: Keyword=[{}], DeptCd=[{}]", searchKeyword, deptCd);

		// 1. Service로 전달할 파라미터 Map 생성
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("searchKeyword", searchKeyword);
		paramMap.put("deptCd", deptCd);

		// 2. 🔴 실제 Service 호출하여 DB 데이터 반환 🔴
		return staffProfessorInfoService.readProfessorListForStudentMapping(paramMap);
	}

	/**
	 * 학생 정보 등록하기 전 보여주는 폼
	 */
	@GetMapping("/create")
	public String createStudentInfoForm(Model model) {
		if (!model.containsAttribute(MODELNAME)) {
			model.addAttribute(MODELNAME, new StudentDetailDTO());
		}
		commonData(model);
		return "staff/student/staffStudentInfoForm";
	}

	/**
	 * 학생 정보 등록 프로세스 메서드
	 */
	@PostMapping("/create")
	public String createStudentInfo(@Valid @ModelAttribute(MODELNAME) StudentDetailDTO student, BindingResult errors,
			RedirectAttributes redirectAttributes, Model model) {

		if (errors.hasErrors()) {
			log.warn("학생 등록 유효성 검사 실패: {} 개 오류 발생", errors.getErrorCount());
			errors.getAllErrors().forEach(error -> log.warn("필드 에러: {}", error.getDefaultMessage()));

			commonData(model);
			return "staff/student/staffStudentInfoForm";
		} else {
			service.createStaffStudentInfo(student);
			redirectAttributes.addFlashAttribute("message", "학생 정보가 성공적으로 등록되었습니다.");
			return "redirect:/lms/staff/students";
		}
	}

	/**
	 * 학생 정보 수정하기 전 보여주는 폼
	 */
	@GetMapping("/modify/{studentNo}")
	public String modifyStudentForm(@PathVariable("studentNo") String studentNo, Model model) {
		StudentDetailDTO student = service.readStaffStudentInfo(studentNo);
		commonData(model);
		model.addAttribute("student", student);
		return "staff/student/staffStudentInfoEdit";
	}

	/**
	 * 학생 정보 수정(업데이트) 프로세스
	 */
	@PostMapping("/modify/{studentNo}")
	public String modifyStudentInfo(@PathVariable("studentNo") String studentNo,
			@Valid @ModelAttribute(MODELNAME) StudentDetailDTO student, BindingResult errors,
			RedirectAttributes redirectAttributes, Model model) {

		student.setStudentNo(studentNo);

		if (student.getStudentNo() == null || !studentNo.equals(student.getStudentNo())) {
			log.error("URL 경로 학번({})과 DTO 학번({}) 불일치 또는 누락.", studentNo, student.getStudentNo());
			redirectAttributes.addFlashAttribute("error", "잘못된 요청입니다. 학번 정보가 일치하지 않습니다.");
			return "redirect:/lms/staff/students/" + studentNo;
		}

		if (errors.hasErrors()) {
			log.warn("학생 정보 수정 유효성 검사 실패: {} 개 오류 발생", errors.getErrorCount());
			errors.getAllErrors().forEach(error -> log.warn("필드 에러: {}", error.getDefaultMessage()));
			commonData(model);
			return "staff/student/staffStudentInfoEdit";
		}

		try {
			service.modifyStaffStudentInfo(student);
			redirectAttributes.addFlashAttribute("message", "학번 [" + studentNo + "] 학생 정보가 성공적으로 수정되었습니다.");
			return "redirect:/lms/staff/students/" + studentNo;

		} catch (Exception e) {
			log.error("학생 정보 수정 실패: 학번={}", studentNo, e);
			model.addAttribute("error", "학생 정보 수정 중 오류가 발생했습니다.");
			commonData(model);
			model.addAttribute(MODELNAME, student);
			return "staff/student/staffStudentInfoEdit";
		}
	}

	/**
	 * 엑셀 일괄 등록 미리보기 (학과별 카운트 포함)
	 */

	// StaffStudentInfoController.java 내의 해당 메서드

	@PostMapping("/batch-excel-preview")
	@ResponseBody
	public Map<String, Object> previewBatchStudentInfo(@RequestParam("excelFile") MultipartFile excelFile) {

		Map<String, Object> response = new HashMap<>();

		// Service Layer의 readCommonCodeMaps()를 호출하여 codeMaps 준비
		Map<String, Map<String, String>> codeMaps = service.readCommonCodeMaps();

		if (excelFile.isEmpty()) {
			response.put("success", false);
			response.put("message", "파일을 선택해 주세요.");
			return response;
		}

		try {
			// Service로 codeMaps 전달 (Service는 Map<String(학과명), Integer(인원수)>를 반환한다고 가정)
			Map<String, Integer> previewCountsByDept = service.previewBatchStudentsByExcel(excelFile, codeMaps);
			int totalCount = previewCountsByDept.values().stream().mapToInt(Integer::intValue).sum();

			response.put("success", true);
			response.put("totalCount", totalCount);
			// 클라이언트에서 목록 출력을 위해 Map 객체 그대로 반환
			response.put("detailCounts", previewCountsByDept);
			response.put("message", "미리보기 성공: 총 " + totalCount + "건");

		} catch (RuntimeException e) {
			// log.error("엑셀 파일 분석 실패: {}", e.getMessage());
			response.put("success", false);
			response.put("message", "파일 분석 중 오류 발생: " + e.getMessage());
		}

		return response;
	}

	/**
	 * 엑셀 일괄 등록 처리 (학과별 카운트 포함)
	 */
	@PostMapping("/batch-excel-create")
	@ResponseBody
	public Map<String, Object> createBatchStudentInfo(@RequestParam("excelFile") MultipartFile excelFile) {

		Map<String, Object> response = new HashMap<>();

		if (excelFile.isEmpty()) {
			response.put("success", false);
			response.put("message", "파일을 선택해 주세요.");
			return response;
		}

		try {
			// Service Layer의 readCommonCodeMaps()를 호출하여 공통 코드 맵 확보
			Map<String, Map<String, String>> codeMaps = service.readCommonCodeMaps();

			// Service로 codeMaps 전달
			Map<String, Integer> insertedCountsByDept = service.createBatchStudentsByExcel(excelFile, codeMaps);
			int totalCount = insertedCountsByDept.values().stream().mapToInt(Integer::intValue).sum();

			// 학과별 등록 완료 인원을 문자열로 포맷팅
			String detailMessage = insertedCountsByDept.entrySet().stream()
					.map(entry -> entry.getKey() + " : " + entry.getValue() + "명").collect(Collectors.joining("  ·  "));

			response.put("success", true);
			response.put("totalCount", totalCount);
			response.put("detailCounts", insertedCountsByDept);
			response.put("detailMessage", detailMessage); // 클라이언트에서 이 값을 사용해야 함
			response.put("message", "총 " + totalCount + "명의 학생 등록이 완료되었습니다.");

		} catch (RuntimeException e) {
			log.error("엑셀 일괄 등록 실패: {}", e.getMessage());

			response.put("success", false);
			response.put("message", "엑셀 등록 실패: " + e.getMessage());
		}

		return response;
	}

	/**
	 * 엑셀 파일 다운로드 엔드포인트
	 */
	@GetMapping("/downloadExcel")
	public void downloadStudentExcel(HttpServletResponse response) {

		List<StudentDetailDTO> emptyList = java.util.Collections.emptyList();
		Workbook workbook = service.createStudentExcel(emptyList);
		response.setContentType("application/vnd.ms-excel");

		response.setHeader("Content-Disposition", "attachment;filename=학생 일괄 등록 양식.xlsx");

		try (ServletOutputStream outputStream = response.getOutputStream()) {
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			log.error("학생 등록 양식 다운로드 중 IOException 발생: {}", e.getMessage(), e);
		}
	}

	/**
	 * 통계 엔드포인트
	 */
	@GetMapping("/stats/college")
	@ResponseBody
	public List<Map<String, Object>> getCollegeCountsByStatus(@RequestParam("stuStatusName") String stuStatusName) {
		log.info("AJAX Request to /stats/college received. stuStatusName: [{}]", stuStatusName);
		return service.readStudentCountsByCollege(stuStatusName);
	}

	@GetMapping("/stats/department")
	@ResponseBody
	public List<Map<String, Object>> getDepartmentCountsByCollege(@RequestParam("stuStatusName") String stuStatusName,
			@RequestParam("collegeName") String collegeName) {
		log.info("AJAX Request to /stats/department received. stuStatusName: [{}], collegeName: [{}]", stuStatusName,
				collegeName);
		return service.readStudentCountsByDepartment(stuStatusName, collegeName);
	}

	@GetMapping("/stats/grade")
	@ResponseBody
	public List<Map<String, Object>> getGradeCountsByDepartment(@RequestParam("stuStatusName") String stuStatusName,
			@RequestParam("collegeName") String collegeName, @RequestParam("univDeptName") String univDeptName) {
		log.info("AJAX Request to /stats/grade received. stuStatusName: [{}], collegeName: [{}], univDeptName: [{}]",
				stuStatusName, collegeName, univDeptName);
		return service.readStudentCountsByGrade(stuStatusName, collegeName, univDeptName);
	}
}