package kr.or.jsu.lms.professor.controller.lecture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.lms.professor.service.lecture.ProfLectureRegistService;

@Controller
@RequestMapping("/lms/professor/lectureRegist")
public class ProfLectureRegistController {

	@Autowired
	private ProfLectureRegistService profLectureRegistService;

	// 강의 개설 신청 UI
	@GetMapping("/regist")
	public String formUI() {
		return "professor/lecture/profLectureRegistForm";
		
	}
	
	// 강의 개설 신청 처리
	@PostMapping("/regist")
	@ResponseBody
	public Map<String, String> formProcess(
			@RequestParam("academicYear") String academicYear,
			@RequestParam("semester") String semester,
			@RequestParam("subjectCode") String subjectCode,
			@RequestParam("lectureName") String lectureName,
			@RequestParam("credits") int credits,
			@RequestParam("hours") int hours,
			@RequestParam("courseType") String courseType,
			@RequestParam(value = "prerequisiteCourse", required = false) String prerequisiteCourse,
			@RequestParam("courseObjectives") String courseObjectives,
			@RequestParam("courseOverview") String courseOverview,
			@RequestParam("expectedCapacity") int expectedCapacity,
			// Weekly Plan Data - assuming they come as arrays or lists
			@RequestParam(value = "weeklyItemWeek", required = false) List<Integer> weeklyItemWeek,
			@RequestParam(value = "weeklyItemObjective", required = false) List<String> weeklyItemObjective,
			@RequestParam(value = "weeklyItemContent", required = false) List<String> weeklyItemContent,
			// Grading Criteria Data - assuming they come as arrays or lists
			@RequestParam(value = "gradingItemType", required = false) List<String> gradingItemType,
			@RequestParam(value = "gradingItemPercentage", required = false) List<Integer> gradingItemPercentage,
			@RequestParam(value = "preferredDayTime", required = false) String preferredDayTime,
			@RequestParam(value = "preferredClassroom", required = false) String preferredClassroom,
			@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		Map<String, String> response = new HashMap<>();
		String professorNo = loginUser.getRealUser().getUserNo();

		// Handle empty prerequisiteCourse
		if (prerequisiteCourse == null || prerequisiteCourse.trim().isEmpty()) {
			prerequisiteCourse = "없음";
		}

		Map<String, Object> lectureInfo = new HashMap<>();
		lectureInfo.put("academicYear", academicYear);
		lectureInfo.put("semester", semester);
		lectureInfo.put("subjectCode", subjectCode);
		lectureInfo.put("lectureName", lectureName);
		lectureInfo.put("credits", credits);
		lectureInfo.put("hours", hours);
		lectureInfo.put("courseType", courseType);
		lectureInfo.put("prerequisiteCourse", prerequisiteCourse);
		lectureInfo.put("courseObjectives", courseObjectives);
		lectureInfo.put("courseOverview", courseOverview);
		lectureInfo.put("expectedCapacity", expectedCapacity);
		lectureInfo.put("weeklyItemWeek", weeklyItemWeek);
		lectureInfo.put("weeklyItemObjective", weeklyItemObjective);
		lectureInfo.put("weeklyItemContent", weeklyItemContent);
		lectureInfo.put("gradingItemType", gradingItemType);
		lectureInfo.put("gradingItemPercentage", gradingItemPercentage);
		lectureInfo.put("preferredDayTime", preferredDayTime);
		lectureInfo.put("preferredClassroom", preferredClassroom);
		lectureInfo.put("professorNo", professorNo);

		String lctApplyId = profLectureRegistService.registerLecture(lectureInfo);

		if (lctApplyId != null) {
			try {
				String initialApproveId = profLectureRegistService.createApprovalChain(professorNo, lctApplyId, loginUser.getRealUser().getUserType());
				profLectureRegistService.updateLectureApplicationApprovalId(lctApplyId, initialApproveId);
				response.put("status", "success");
				response.put("message", "강의 등록 신청이 완료되었으며, 승인 절차가 시작되었습니다.");
			} catch (Exception e) {
				response.put("status", "error");
				response.put("message", "강의 등록 신청은 완료되었으나, 승인 절차 시작 중 오류가 발생했습니다: " + e.getMessage());
			}
		} else {
			response.put("status", "error");
			response.put("message", "강의 등록 신청에 실패했습니다.");
		}

		return response;
	}

	// 모든 과목 코드 및 이름 조회 (AJAX용)
	    @GetMapping("/getSubjectCodes")
	    @ResponseBody
	    public List<Map<String, Object>> getSubjectCodes(@AuthenticationPrincipal CustomUserDetails loginUser) {
	        String professorNo = loginUser.getRealUser().getUserNo();
	        return profLectureRegistService.getSubjectCodes(professorNo);
	    }
	// 특정 과목 상세 정보 조회 (AJAX용)
	    @GetMapping("/getSubjectDetails")	@ResponseBody
	    public Map<String, Object> getSubjectDetails(@RequestParam("subjectCode") String subjectCode) {
	    	System.out.println("********** Received subjectCode on server: [" + subjectCode + "] **********");		return profLectureRegistService.getSubjectDetails(subjectCode);
	}
	
	
	// 강의 개설 신청 내역 
	        @GetMapping(value = "/api/list", produces = "application/json;charset=UTF-8")
	        @ResponseBody
	            public Map<String, Object> getApiList(
	                @RequestParam(required = false) String academicYear,
	                @RequestParam(required = false) String semester,
	                @RequestParam(required = false) String status,
	                @AuthenticationPrincipal CustomUserDetails loginUser,
	                @ModelAttribute("pagingInfo") PaginationInfo<Map<String, Object>> pagingInfo
	            ) {
	        	
	        		if (pagingInfo.getCurrentPage() == 0) {
	            		pagingInfo.setCurrentPage(1);
	            	}
	            	if (pagingInfo.getScreenSize() == 0) {
	            		pagingInfo.setScreenSize(5);
	            	}
	            	
	                Map<String, Object> paramMap = new HashMap<>();
	                paramMap.put("academicYear", academicYear);
	                paramMap.put("semester", semester);
	                paramMap.put("status", status);

					String professorNo = loginUser.getRealUser().getUserNo();
					Map<String, Object> position = profLectureRegistService.getProfessorPosition(professorNo);
					System.out.println("### API Position Check: " + position);

					if (position != null && "PRF_POSIT_HEAD".equals(position.get("PRF_POSIT_CD"))) {
						Map<String, Object> department = profLectureRegistService.getProfessorDepartment(professorNo);
						System.out.println("### API Department Check: " + department);
						paramMap.put("univDeptCd", department.get("UNIV_DEPT_CD"));
					} else {
						paramMap.put("professorNo", professorNo);
					}
	                
	                List<Map<String, Object>> applyList = profLectureRegistService.getLctOpenApplyList(paramMap, pagingInfo);
	                
	                Map<String, Object> result = new HashMap<>();
	                result.put("applyList", applyList);
	                result.put("pagingInfo", pagingInfo);
	                
	                return result;
	            }	    
	        	        	        @GetMapping("/list")
	        	        	        public String getList(
	        	        	        		@RequestParam(required = false) String academicYear,
	        	        	                @RequestParam(required = false) String status,
	        	        	                @RequestParam(required = false) String completionCd,
	        	        	        		Model model, @AuthenticationPrincipal CustomUserDetails loginUser,
	        	        	        		@ModelAttribute("pagingInfo") PaginationInfo<Map<String, Object>> pagingInfo
	        	        	        ) {
	        	        	        	
	        	        	        	if (pagingInfo.getCurrentPage() == 0) {
	        	        	        		pagingInfo.setCurrentPage(1);
	        	        	        	}
	        	        	        	if (pagingInfo.getScreenSize() == 0) {
	        	        	        		pagingInfo.setScreenSize(10);
	        	        	        	}
	        	        	        	
	        	        		        int currentYear = java.time.Year.now().getValue();
	        	        		        if (academicYear == null || academicYear.isEmpty()) {
	        	        		            academicYear = String.valueOf(currentYear);
	        	        		        }
	        	        
	        	        		        // Generate academic years
	        	        		        List<Map<String, String>> academicYears = new java.util.ArrayList<>();
	        	        		        for (int i = 0; i < 5; i++) {
	        	        		            Map<String, String> yearMap = new java.util.HashMap<>();
	        	        		            String yearValue = String.valueOf(currentYear - i);
	        	        		            yearMap.put("value", yearValue);
	        	        		            yearMap.put("label", (currentYear - i) + "학년도");
	        	        		            if (yearValue.equals(academicYear)) {
	        	        		                yearMap.put("selected", "true");
	        	        		            }
	        	        		            academicYears.add(yearMap);
	        	        		        }
	        	        		        model.addAttribute("academicYears", academicYears);
	        	        
	        	        		        // Generate statuses
	        	        		        List<Map<String, String>> statuses = new java.util.ArrayList<>();
	        	        		        statuses.add(createOption("", "전체", status));
	        	        		        statuses.add(createOption("PENDING", "대기중", status));
	        	        		        statuses.add(createOption("APPROVED", "승인", status));
	        	        		        statuses.add(createOption("REJECTED", "반려", status));
	        	        		        statuses.add(createOption("CANCELED", "신청 취소", status));
	        	        		        model.addAttribute("statuses", statuses);
	        	        
	        	        		        // Fetch list of applications
	        	        		        String professorNo = loginUser.getRealUser().getUserNo();
	        	        		        Map<String, Object> paramMap = new HashMap<>();
	        	        		        paramMap.put("academicYear", academicYear);
	        	        		        paramMap.put("status", status);
	        	        		        paramMap.put("completionCd", completionCd);
	        	        		        
	        	        	    		Map<String, Object> position = profLectureRegistService.getProfessorPosition(professorNo);
	        	        	    		System.out.println("### Position Check: " + position);
	        	        	    		if (position != null && "PRF_POSIT_HEAD".equals(position.get("PRF_POSIT_CD"))) {
	        	        	    			Map<String, Object> department = profLectureRegistService.getProfessorDepartment(professorNo);
	        	        	    			System.out.println("### Department Check: " + department);
	        	        	    			paramMap.put("univDeptCd", department.get("UNIV_DEPT_CD"));
	        	        	    		} else {
	        	        	    			paramMap.put("professorNo", professorNo);
	        	        	    		}
	        	        		        
	        	        		        List<Map<String, Object>> applyList = profLectureRegistService.getLctOpenApplyList(paramMap, pagingInfo);
	        	        		        model.addAttribute("applyList", applyList);
	        	        		        model.addAttribute("pagingInfo", pagingInfo);
	        	        
	        	        	        	return "professor/lecture/profLectureRegistList";
	        	        	    }
	        	        	        
	        	        	    private Map<String, String> createOption(String value, String label, String selectedValue) {
	        	        	        Map<String, String> option = new java.util.HashMap<>();
	        	        	        option.put("value", value);
	        	        	        option.put("label", label);
	        	        	        if (value != null && value.equals(selectedValue)) {
	        	        	            option.put("selected", "true");
	        	        	        }
	        	        	        return option;
	        	        	    }	// 강의 개설 신청 상세보기
	@GetMapping("/detail")
	public String getDetail(@RequestParam("lctApplyId") String lctApplyId, Model model, @AuthenticationPrincipal CustomUserDetails loginUser) {
		Map<String, Object> lectureApplicationDetail = profLectureRegistService.getLectureApplicationDetail(lctApplyId);
		model.addAttribute("detail", lectureApplicationDetail);
		model.addAttribute("loggedInProfessorNo", loginUser.getRealUser().getUserNo());

		// Check if the logged-in user is a department head
		if (loginUser != null) {
			String professorNo = loginUser.getRealUser().getUserNo();
			Map<String, Object> position = profLectureRegistService.getProfessorPosition(professorNo);
			if (position != null && "PRF_POSIT_HEAD".equals(position.get("PRF_POSIT_CD"))) {
				model.addAttribute("isDepartmentHead", true);
			} else {
				model.addAttribute("isDepartmentHead", false);
			}
		}

		return "professor/lecture/profLectureRegistDetail";
	}
	
	// 강의 개설 신청 (처리상태에 따라) 수정하기 = GET, POST
	
	// 강의 개설 신청 승인 제출
	@PostMapping("/submitForApproval")
	@ResponseBody
	public Map<String, String> submitForApproval(
			@RequestParam("lctApplyId") String lctApplyId,
			@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		Map<String, String> response = new HashMap<>();
		try {
			String professorNo = loginUser.getRealUser().getUserNo();
			String initialApproveId = profLectureRegistService.createApprovalChain(professorNo, lctApplyId, loginUser.getRealUser().getUserType());
			
			// Update LCT_OPEN_APPLY with the new APPROVE_ID
			profLectureRegistService.updateLectureApplicationApprovalId(lctApplyId, initialApproveId);
			
			response.put("status", "success");
			response.put("message", "강의 신청이 승인 제출되었습니다.");
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "강의 신청 승인 제출에 실패했습니다: " + e.getMessage());
		}
		return response;
	}

	@PostMapping("/approveApplication")
	@ResponseBody
	public Map<String, String> approveApplication(
			@RequestParam("lctApplyId") String lctApplyId,
			@RequestParam("approveId") String approveId,
			@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		Map<String, String> response = new HashMap<>();
		try {
			profLectureRegistService.approveApplication(lctApplyId, approveId, loginUser.getRealUser().getUserNo());
			response.put("status", "success");
			response.put("message", "승인 처리되었습니다.");
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "승인 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
		return response;
	}

	@PostMapping("/rejectApplication")
	@ResponseBody
	public Map<String, String> rejectApplication(
			@RequestParam("lctApplyId") String lctApplyId,
			@RequestParam("approveId") String approveId,
			@RequestParam("rejectionReason") String rejectionReason,
			@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		Map<String, String> response = new HashMap<>();
		try {
			profLectureRegistService.rejectApplication(lctApplyId, approveId, loginUser.getRealUser().getUserNo(), rejectionReason);
			response.put("status", "success");			response.put("message", "반려 처리되었습니다.");
		} catch (Exception e) {
			response.put("status", "error");
				response.put("message", "반려 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
		return response;
	}

	@PostMapping("/cancelApplication")
	@ResponseBody
	public Map<String, String> cancelApplication(
			@RequestParam("lctApplyId") String lctApplyId,
			@AuthenticationPrincipal CustomUserDetails loginUser
	) {
		Map<String, String> response = new HashMap<>();
		try {
			// Ensure the logged-in user is the owner of the application
			if (!profLectureRegistService.isLectureApplicationOwner(lctApplyId, loginUser.getRealUser().getUserNo())) {
				response.put("status", "error");
				response.put("message", "본인의 강의 신청만 취소할 수 있습니다.");
				return response;
			}

			profLectureRegistService.cancelApplication(lctApplyId);
			response.put("status", "success");
			response.put("message", "강의 신청이 성공적으로 취소되었습니다.");
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "강의 신청 취소 중 오류가 발생했습니다: " + e.getMessage());
		}
		return response;
	}
}