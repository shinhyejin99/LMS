package kr.or.jsu.lms.professor.service.lecture;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.mybatis.mapper.SubjectMapper;
import kr.or.jsu.mybatis.mapper.lms.professor.lctapply.LctOpenApplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
 *  -----------   		-------------    ---------------------------
 *  2025. 9. 25.     		최건우	          최초 생성
 *  2025. 9. 30.            최건우  			  내용 수정
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfLectureRegistServiceImpl implements ProfLectureRegistService {

	private final LctOpenApplyMapper lctOpenApplyMapper;
	private final SubjectMapper subjectMapper;
	@Override
	public String registerLecture(Map<String, Object> lectureInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Map<String, Object>> getSubjectCodes(String professorNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Object> getSubjectDetails(String subjectCode) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Map<String, Object>> getLctOpenApplyList(Map<String, Object> paramMap,
			PaginationInfo<Map<String, Object>> pagingInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Object> getLectureApplicationDetail(String lctApplyId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Object> getProfessorPosition(String professorNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Object> getProfessorDepartment(String professorNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String createApprovalChain(String professorNo, String lctApplyId, String userType) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int updateLectureApplicationApprovalId(String lctApplyId, String approveId) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void approveApplication(String lctApplyId, String approveId, String userId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void rejectApplication(String lctApplyId, String approveId, String professorNo, String rejectionReason) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isLectureApplicationOwner(String lctApplyId, String professorNo) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void cancelApplication(String lctApplyId) {
		// TODO Auto-generated method stub
		
	}

//	@Transactional
//	@Override
//	public String registerLecture(Map<String, Object> lectureInfo) {
//	    log.info("registerLecture: lectureInfo = {}", lectureInfo);
//
//	    // =============================
//	    // 1️⃣ 학년도/학기 정보 조합
//	    // =============================
//	    String academicYear = (String) lectureInfo.get("academicYear");
//	    String semester = (String) lectureInfo.get("semester");
//
//	    // 프론트엔드에서 넘어온 학기값("1","2","sumseasonal" 등)을 백엔드의 YEARTERM_CD 형식으로 변환
//	    String semesterCode;
//	    switch (semester) {
//	        case "1": semesterCode = "REG1"; break;       // 1학기 → REG1
//	        case "2": semesterCode = "REG2"; break;       // 2학기 → REG2
//	        case "sumseasonal": semesterCode = "SUB1"; break; // 하계 계절학기 → SUB1
//	        case "winseasonal": semesterCode = "SUB1"; break; // 동계 계절학기도 SUB1로 통일
//	        default: semesterCode = ""; // 예외 처리
//	    }
//	    lectureInfo.put("yeartermCd", academicYear + "_" + semesterCode); // ex) 2025_REG1
//
//	    // =============================
//	    // 2️⃣ 결재(승인) 초안 생성
//	    // =============================
//	    // 강의 개설신청은 “결재 프로세스(Approval)”와 연동되므로,
//	    // 먼저 결재 테이블에 임시(초안) 결재 건을 하나 생성
//	    Map<String, Object> draftApprovalParams = new HashMap<>();
//	    String professorNo = (String) lectureInfo.get("professorNo");
//
//	    // 교수번호로 사용자ID 조회 (결재 테이블에 userId 필요)
//	    String userId = lctOpenApplyMapper.getUserIdByProfessorNo(professorNo);
//	    draftApprovalParams.put("userId", userId);
//	    draftApprovalParams.put("applicantUserId", userId);
//	    draftApprovalParams.put("applyTypeCd", "LCT_OPEN"); // 결재유형: 강의개설신청
//
//	    // APPROVAL 테이블에 초안 insert → 내부적으로 approveId가 생성됨
//	    lctOpenApplyMapper.insertDraftApproval(draftApprovalParams);
//	    String draftApproveId = (String) draftApprovalParams.get("approveId"); // 생성된 결재ID 획득
//
//	    // 강의개설신청 정보에 approveId 추가
//	    lectureInfo.put("approveId", draftApproveId);
//	    log.info("preferredDayTime: {}, preferredClassroom: {}", 
//	        lectureInfo.get("preferredDayTime"), lectureInfo.get("preferredClassroom"));
//
//	    // =============================
//	    // 3️⃣ LCT_OPEN_APPLY 테이블에 강의신청 기본정보 INSERT
//	    // =============================
//	    int result = lctOpenApplyMapper.insertLctOpenApply(null);
//	    String lctApplyId = null;
//
//	    if (result > 0) {
//	        // 매퍼에서 selectKey로 생성된 강의신청ID(LCT_APPLY_ID) 획득
//	        lctApplyId = (String) lectureInfo.get("lctApplyId");
//
//	        // =============================
//	        // 4️⃣ LCT_APPLY_WEEKBY 테이블에 주차별 계획 등록
//	        // =============================
//	        // 강의계획서의 "주차별 목표/내용" 데이터 (List로 전달받음)
//	        List<Integer> weeklyItemWeek = (List<Integer>) lectureInfo.get("weeklyItemWeek");
//	        List<String> weeklyItemObjective = (List<String>) lectureInfo.get("weeklyItemObjective");
//	        List<String> weeklyItemContent = (List<String>) lectureInfo.get("weeklyItemContent");
//
//	        if (weeklyItemWeek != null && !weeklyItemWeek.isEmpty()) {
//	            for (int i = 0; i < weeklyItemWeek.size(); i++) {
//	                Map<String, Object> weeklyPlan = new HashMap<>();
//	                weeklyPlan.put("lctApplyId", lctApplyId);
//	                weeklyPlan.put("lectureWeek", weeklyItemWeek.get(i)); // N주차
//	                weeklyPlan.put("weekGoal", weeklyItemObjective.get(i)); // 학습목표
//	                weeklyPlan.put("weekDesc", weeklyItemContent.get(i));  // 세부 내용
//	                lctOpenApplyMapper.insertLctApplyWeekby(weeklyPlan);
//	            }
//	        }
//
//	        // =============================
//	        // 5️⃣ LCT_APPLY_GRADERATIO 테이블에 평가비율 등록
//	        // =============================
//	        // 출석/과제/시험 등의 평가항목별 비율 정보
//	        List<String> gradingItemType = (List<String>) lectureInfo.get("gradingItemType");
//	        List<Integer> gradingItemPercentage = (List<Integer>) lectureInfo.get("gradingItemPercentage");
//
//	        if (gradingItemType != null && !gradingItemType.isEmpty()) {
//	            for (int i = 0; i < gradingItemType.size(); i++) {
//	                Map<String, Object> gradeRatio = new HashMap<>();
//	                gradeRatio.put("lctApplyId", lctApplyId);
//	                gradeRatio.put("gradeCriteriaCd", gradingItemType.get(i)); // 평가항목코드 (ATTD, TASK 등)
//	                gradeRatio.put("ratio", gradingItemPercentage.get(i));     // 비율 (%)
//	                lctOpenApplyMapper.insertLctApplyGradeRatio(gradeRatio);
//	            }
//	        }
//	    }
//
//	    // =============================
//	    // 6️⃣ 최종적으로 강의신청ID 반환
//	    // =============================
//	    return lctApplyId;
//	}
//
//
//    @Override
//    public List<Map<String, Object>> getSubjectCodes(String professorNo) {
//        log.info("getSubjectCodes called for professorNo: {}", professorNo);
//        Map<String, Object> professorDept = lctOpenApplyMapper.getProfessorDepartment(professorNo);
//        String univDeptCd = null;
//        if (professorDept != null) {
//            univDeptCd = (String) professorDept.get("UNIV_DEPT_CD");
//        }
//        return subjectMapper.selectAllSubjectCodes(univDeptCd);
//    }
//
//    @Override
//    public Map<String, Object> getSubjectDetails(String subjectCode) {
//        log.info("getSubjectDetails called with subjectCode: {}", subjectCode);
//        // TODO: Implement actual call to subjectMapper
//        Map<String, Object> details = subjectMapper.selectSubjectDetails(subjectCode);
//        if (details == null) {
//            // Return an empty map instead of null to prevent JSON parsing errors on the client side
//            return new HashMap<>();
//        }
//        return details;
//    }
//
//    @Override
//    public List<Map<String, Object>> getLctOpenApplyList(Map<String, Object> paramMap, PaginationInfo<Map<String, Object>> pagingInfo) {
//        log.info("Executing getLctOpenApplyList with parameters: {}, pagingInfo: {}", paramMap, pagingInfo);
//        
//        // 1. 전체 레코드 수 조회
//        int totalRecords = lctOpenApplyMapper.selectLctOpenApplyCount(paramMap);
//        pagingInfo.setTotalRecord(totalRecords);
//        
//        // 2. 페이징 정보와 검색 조건을 paramMap에 추가
//        paramMap.put("startRow", pagingInfo.getStartRow());
//        paramMap.put("endRow", pagingInfo.getEndRow());
//        
//        return lctOpenApplyMapper.selectLctOpenApplyList(paramMap);
//    }
//
//    @Override
//    public Map<String, Object> getLectureApplicationDetail(String lctApplyId) {
//        log.info("getLectureApplicationDetail called with lctApplyId: {}", lctApplyId);
//        Map<String, Object> detail = lctOpenApplyMapper.selectLectureApplicationDetail(lctApplyId);
//        if (detail != null) {
//            detail.put("weeklyPlans", lctOpenApplyMapper.selectLctApplyWeekbyList(lctApplyId));
//            detail.put("gradeRatios", lctOpenApplyMapper.selectLctApplyGradeRatioList(lctApplyId));
//        }
//        return detail;
//    }
//
//    @Override
//    public Map<String, Object> getProfessorPosition(String professorNo) {
//        return lctOpenApplyMapper.selectProfessorPosition(professorNo);
//    }
//
//    @Override
//    public Map<String, Object> getProfessorDepartment(String professorNo) {
//        return lctOpenApplyMapper.getProfessorDepartment(professorNo);
//    }
//
//    @Override
//    @Transactional
//    public String createApprovalChain(String professorNo, String lctApplyId, String userType) {
//        String initialApproveId = null;
//        String submittingProfessorUserId = lctOpenApplyMapper.getUserIdByProfessorNo(professorNo);
//        Map<String, Object> professorPosition = lctOpenApplyMapper.selectProfessorPosition(professorNo);
//        String prfPositCd = null;
//        if (professorPosition != null) {
//            prfPositCd = (String) professorPosition.get("PRF_POSIT_CD");
//        }
//
//        try {
//            // Get professor's department
//            Map<String, Object> professorDept = lctOpenApplyMapper.getProfessorDepartment(professorNo);
//            String univDeptCd = (String) professorDept.get("UNIV_DEPT_CD");
//
//            // Get Department Head ID
//            String departmentHeadId = lctOpenApplyMapper.getDepartmentHead(univDeptCd);
//            if (departmentHeadId == null) {
//                throw new RuntimeException("Department Head not found for department: " + univDeptCd);
//            }
//            String departmentHeadUserId = lctOpenApplyMapper.getUserIdByProfessorNo(departmentHeadId);
//            if (departmentHeadUserId == null) {
//                throw new RuntimeException("User ID not found for Department Head: " + departmentHeadId);
//            }
//
//            // Get Generic Staff ID
//            String genericStaffId = lctOpenApplyMapper.getGenericStaffId();
//            if (genericStaffId == null) {
//                throw new RuntimeException("Generic Staff not found for approval.");
//            }
//            String genericStaffUserId = lctOpenApplyMapper.getUserIdByStaffNo(genericStaffId);
//            if (genericStaffUserId == null) {
//                throw new RuntimeException("User ID not found for Generic Staff: " + genericStaffId);
//            }
//
//            // 1. Get next sequence for initial approval
//            // String firstApproveId = lctOpenApplyMapper.getNextApprovalSequence(); // Removed, now generated inside blocks
//            // initialApproveId = firstApproveId; // Set at the end of each branch
//
//            // Determine approval flow based on professor type
//            if ("PRF_POSIT_HEAD".equals(prfPositCd)) { // Department Head
//                // Department Head -> Staff Approval
//                // First approval is by Staff
//
//                // First approval is by Staff
//                Map<String, Object> staffApproval = new HashMap<>();
//                staffApproval.put("userId", genericStaffUserId);
//                staffApproval.put("applicantUserId", submittingProfessorUserId);
//                staffApproval.put("applyTypeCd", "LCT_OPEN");
//                lctOpenApplyMapper.insertApprovalChain(staffApproval);
//                initialApproveId = (String) staffApproval.get("approveId");
//
//            } else { // General Professor or position not found/null
//                // General Professor -> Department Head -> Staff Approval
//                // First approval is by Department Head
//                Map<String, Object> deptHeadApproval = new HashMap<>();
//                deptHeadApproval.put("userId", departmentHeadUserId);
//                deptHeadApproval.put("applicantUserId", submittingProfessorUserId);
//                deptHeadApproval.put("applyTypeCd", "LCT_OPEN");
//                lctOpenApplyMapper.insertApprovalChain(deptHeadApproval);
//                initialApproveId = (String) deptHeadApproval.get("approveId");
//
//                // Second approval is by Staff, linked to the first approval
//                Map<String, Object> staffApproval = new HashMap<>();
//                staffApproval.put("prevApproveId", initialApproveId);
//                staffApproval.put("userId", genericStaffUserId);
//                staffApproval.put("applicantUserId", submittingProfessorUserId);
//                staffApproval.put("applyTypeCd", "LCT_OPEN");
//                lctOpenApplyMapper.insertApprovalChain(staffApproval);
//            }
//        } catch (Exception e) {
//            log.error("Error creating approval chain for lctApplyId: {}", lctApplyId, e);
//            throw new RuntimeException("Failed to create approval chain.", e);
//        }
//
//        return initialApproveId;
//    }
//
//    @Override
//    public int updateLectureApplicationApprovalId(String lctApplyId, String approveId) {
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("lctApplyId", lctApplyId);
//        paramMap.put("approveId", approveId);
//        return lctOpenApplyMapper.updateLctOpenApplyApprovalId(paramMap);
//    }
//
//    @Override
//    @Transactional
//    public void approveApplication(String lctApplyId, String approveId, String professorNo) {
//        // 1. Security Check: Verify the current user is the designated approver for this step
//        String userId = lctOpenApplyMapper.getUserIdByProfessorNo(professorNo);
//        Map<String, Object> currentApproval = lctOpenApplyMapper.getApprovalById(approveId);
//        if (currentApproval == null || !userId.equals(currentApproval.get("USER_ID"))) {
//            throw new SecurityException("You are not authorized to approve this application.");
//        }
//
//        // 2. Update the current approval step to 'Approved'
//        Map<String, Object> params = new HashMap<>();
//        params.put("approveId", approveId);
//        params.put("status", "Y"); // 'Y' for Approved
//        params.put("comment", "승인됨"); // Generic comment
//        lctOpenApplyMapper.updateApprovalStatus(params);
//
//        // 3. Find the next approval step
//        String nextApproveId = lctOpenApplyMapper.findNextApprovalId(approveId);
//
//        // 4. If there is a next step, update the main application to point to it
//        if (nextApproveId != null) {
//            updateLectureApplicationApprovalId(lctApplyId, nextApproveId);
//        }
//        // If there is no next step, the process is fully approved.
//        // The LCT_OPEN_APPLY.APPROVE_ID will point to the final, now-approved step.
//    }
//
//    @Override
//    @Transactional
//    public void rejectApplication(String lctApplyId, String approveId, String professorNo, String rejectionReason) {
//        log.info("Rejecting application: lctApplyId={}, approveId={}, professorNo={}, rejectionReason={}", lctApplyId, approveId, professorNo, rejectionReason);
//
//        // 1. Initial Security Check: Verify the current user is the designated approver for this step
//        String loggedInUserId = lctOpenApplyMapper.getUserIdByProfessorNo(professorNo);
//        Map<String, Object> currentApproval = lctOpenApplyMapper.getApprovalById(approveId);
//        String expectedApproverUserId = (currentApproval != null) ? (String) currentApproval.get("USER_ID") : null;
//
//        boolean isAuthorized = false;
//
//        if (loggedInUserId.equals(expectedApproverUserId)) {
//            isAuthorized = true;
//            log.info("Security check passed: Logged-in user is the designated approver.");
//        } else {
//            // Additional check: Is the logged-in user a department head and is the application from their department?
//            Map<String, Object> loggedInProfessorPosition = lctOpenApplyMapper.selectProfessorPosition(professorNo);
//            String loggedInPrfPositCd = (loggedInProfessorPosition != null) ? (String) loggedInProfessorPosition.get("PRF_POSIT_CD") : null;
//
//            if ("PRF_POSIT_HEAD".equals(loggedInPrfPositCd)) {
//                // Get submitting professor's details from the lecture application
//                Map<String, Object> lectureApplicationDetail = lctOpenApplyMapper.selectLectureApplicationDetail(lctApplyId);
//                if (lectureApplicationDetail != null) {
//                    String submittingProfessorNo = (String) lectureApplicationDetail.get("PROFESSOR_NO");
//                    Map<String, Object> submittingProfessorDept = lctOpenApplyMapper.getProfessorDepartment(submittingProfessorNo);
//                    String submittingProfessorUnivDeptCd = (submittingProfessorDept != null) ? (String) submittingProfessorDept.get("UNIV_DEPT_CD") : null;
//
//                    Map<String, Object> loggedInProfessorDept = lctOpenApplyMapper.getProfessorDepartment(professorNo);
//                    String loggedInProfessorUnivDeptCd = (loggedInProfessorDept != null) ? (String) loggedInProfessorDept.get("UNIV_DEPT_CD") : null;
//
//                    if (submittingProfessorUnivDeptCd != null && submittingProfessorUnivDeptCd.equals(loggedInProfessorUnivDeptCd)) {
//                        isAuthorized = true;
//                        log.info("Security check passed: Logged-in user is department head and application is from their department.");
//                    }
//                }
//            }
//        }
//
//        if (!isAuthorized) {
//            log.warn("Unauthorized rejection attempt. Logged in user ID: {}, Expected approver ID: {}", loggedInUserId, expectedApproverUserId);
//            throw new SecurityException("You are not authorized to reject this application.");
//        }
//        log.info("Security check passed for rejection.");
//
//        // 2. Update the current approval step to 'Rejected'
//        Map<String, Object> params = new HashMap<>();
//        params.put("approveId", approveId);
//        params.put("status", "N"); // 'N' for Rejected
//        params.put("comment", rejectionReason);
//        log.info("Updating approval status to Rejected for approveId: {}", approveId);
//        lctOpenApplyMapper.updateApprovalStatus(params);
//        log.info("Approval status updated.");
//
//        // 3. Update the main application to point to this rejected approval step
//        // The LCT_OPEN_APPLY.APPROVE_ID will point to the final, now-rejected step.
//        log.info("Updating LCT_OPEN_APPLY approval ID for lctApplyId: {} with approveId: {}", lctApplyId, approveId);
//        updateLectureApplicationApprovalId(lctApplyId, approveId);
//        log.info("LCT_OPEN_APPLY approval ID updated.");
//    }
//
//    @Override
//    public boolean isLectureApplicationOwner(String lctApplyId, String professorNo) {
//        Map<String, Object> detail = lctOpenApplyMapper.selectLectureApplicationDetail(lctApplyId);
//        return detail != null && professorNo.equals(detail.get("PROFESSOR_NO"));
//    }
//
//    @Override
//    @Transactional
//    public void cancelApplication(String lctApplyId) {
//        // 1. Get the current approval ID for the lecture application
//        Map<String, Object> lectureApplicationDetail = lctOpenApplyMapper.selectLectureApplicationDetail(lctApplyId);
//        if (lectureApplicationDetail == null) {
//            throw new IllegalArgumentException("Lecture application not found: " + lctApplyId);
//        }
//        String approveId = (String) lectureApplicationDetail.get("APPROVE_ID");
//
//        // 2. Update the LCT_OPEN_APPLY status to CANCELED
//        lctOpenApplyMapper.updateLectureApplicationStatus(lctApplyId, "Y");
//        log.info("Lecture application {} CANCEL_YN updated to 'Y'.", lctApplyId);
//
//        // 3. Update the associated APPROVAL status to CANCELED (if exists)
//        // Removed update to APPROVAL table to avoid violating check constraint, as CANCEL_YN in LCT_OPEN_APPLY handles cancellation status.
//        // if (approveId != null) {
//        //     Map<String, Object> params = new HashMap<>();
//        //     params.put("approveId", approveId);
//        //     params.put("status", "C"); // 'C' for Canceled
//        //     params.put("comment", "신청 취소됨"); // Generic comment
//        //     lctOpenApplyMapper.updateApprovalStatus(params);
//        // }
//    }
}