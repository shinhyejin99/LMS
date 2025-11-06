package kr.or.jsu.lms.professor.service.lecture.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.core.dto.info.ProfessorInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.dto.db.subject.LctOpenApplyDetailDTO;
import kr.or.jsu.dto.db.subject.LctOpenApplySimpleDTO;
import kr.or.jsu.dto.db.subject.SubjectWithCollegeAndDeptDTO;
import kr.or.jsu.dto.info.approval.ApprovalInfo;
import kr.or.jsu.dto.info.lecture.apply.LctApplyGraderatioInfo;
import kr.or.jsu.dto.info.lecture.apply.LctApplyWeekbyInfo;
import kr.or.jsu.dto.info.lecture.apply.LctOpenApplyInfo;
import kr.or.jsu.dto.request.lms.lecture.apply.LctApprovalExecuteReq;
import kr.or.jsu.dto.request.lms.lecture.apply.LctOpenApplyReq;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApplyDetailResp;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApplyLabelResp;
import kr.or.jsu.dto.response.lms.lecture.apply.LectureOpenApprovalLabelResp;
import kr.or.jsu.dto.response.lms.lecture.apply.SubjectWithCollegeAndDeptResp;
import kr.or.jsu.lms.professor.service.lecture.ProfessorLectureApplyService;
import kr.or.jsu.mybatis.mapper.CommonCodeMapper;
import kr.or.jsu.mybatis.mapper.LectureMapper;
import kr.or.jsu.mybatis.mapper.ProfessorMapper;
import kr.or.jsu.mybatis.mapper.SubjectMapper;
import kr.or.jsu.mybatis.mapper.UsersMapper;
import kr.or.jsu.mybatis.mapper.lms.professor.approval.LctApplyApprovalMapper;
import kr.or.jsu.mybatis.mapper.lms.professor.lctapply.LctOpenApplyMapper;
import kr.or.jsu.vo.CommonCodeVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessorLectureApplyServiceImpl implements ProfessorLectureApplyService {
	
	private final UsersMapper usersMapper;
	private final ProfessorMapper professorMapper;
	private final SubjectMapper subjectMapper;
	private final CommonCodeMapper commonCodeMapper;
	private final LectureMapper lectureMapper;
	private final LctApplyApprovalMapper approvalMapper;
	private final LctOpenApplyMapper applyMapper;
	
	private final DatabaseCache cache;
	
	/**
	 * 삭제되지 않은 모든 과목을 가져와서, 단과대학 / 학과별 소속으로 구분합니다.
	 * 
	 * @return
	 */
	@Override
	public List<SubjectWithCollegeAndDeptResp> readAllSubject() {
		// 1. 모든 과목을 일단 가져오기
		var resultList = subjectMapper.selectAllSubjectWithCollegeAndDept();
		
		// 2. 삭제된 과목은 빼버리기
		var filtered = resultList.stream()
			    .filter(dto -> dto.getSubject() != null
			                && dto.getSubject().getDeleteAt() == null)
			    .collect(Collectors.toList());
		
		// 3. 응답으로 내보낼 객체로 변경 (단과대로 묶고, 또 학과로 묶은 과목 목록)
		return filtered.stream()
	        .collect(Collectors.groupingBy(dto -> dto.getCollege()))
	        .entrySet().stream()
	        .map(collegeEntry -> {
	            var collegeResp = new SubjectWithCollegeAndDeptResp();
	            collegeResp.setCollegeCd(collegeEntry.getKey().getCollegeCd());
	            collegeResp.setCollegeName(collegeEntry.getKey().getCollegeName());
	
	            // 2️⃣ 단과대 안에서 학과별 그룹핑
	            var deptList =
	                collegeEntry.getValue().stream()
	                    .collect(Collectors.groupingBy(SubjectWithCollegeAndDeptDTO::getUnivDept))
	                    .entrySet().stream()
	                    .map(deptEntry -> {
	                        var deptResp = new SubjectWithCollegeAndDeptResp.SubjectWithDeptResp();
	                        deptResp.setUnivDeptCd(deptEntry.getKey().getUnivDeptCd());
	                        deptResp.setUnivDeptName(deptEntry.getKey().getUnivDeptName());
	
	                        // 3️⃣ 학과별로 과목 리스트 매핑
	                        var subjectList =
	                            deptEntry.getValue().stream()
	                                .map(dto -> {
	                                    var subjectResp = new SubjectWithCollegeAndDeptResp.SubjectResp();
	                                    SubjectInfo s = dto.getSubject();
	                                    
	                                    BeanUtils.copyProperties(s, subjectResp);
	                                    subjectResp.setCompletionName(cache.getCodeName(s.getCompletionCd()));
	                                    subjectResp.setSubjectTypeName(cache.getCodeName(s.getSubjectTypeCd()));
	                                    
	                                    return subjectResp;
	                                })
	                                .collect(Collectors.toList());
	
	                        deptResp.setSubjectList(subjectList);
	                        return deptResp;
	                    })
	                    .collect(Collectors.toList());
	
	            collegeResp.setDeptList(deptList);
	            return collegeResp;
	        })
        .collect(Collectors.toList());
	}

	/**
	 * 현재 사용하는 강의 성적 평가 기준을 가져옵니다.
	 * 
	 * @return
	 */
	@Override
	public Map<String, String> readLectureEvaluateCriteria() {
		List<CommonCodeVO> result = commonCodeMapper.selectCommonCodeList(CommonCodeSort.GRADE_CRITERIA_CD.toString());
		
		return result.stream().collect(Collectors.toMap(k -> k.getCommonCd(), v -> v.getCdName()));
	}

	/**
	 * 강의를 신청하는 교수 정보, 강의 신청과 관련된 요청 객체를 받아 <br>
	 * 1. 해당 과목이 소속된 학과의 학과장을 대상으로 하는 승인 요청 생성 <br>
	 * 2. 해당 과목 신청서 생성 <br>
	 * 3. 해당 과목의 주차계획 생성 <br>
	 * 4. 해당 과목의 성적산출항목&비율 생성 <br>
	 * 모든 과정이 완료되었을 경우에 메서드를 종료합니다.
	 * 
	 * @param realUser 로그인한, 강의 신청하는 교수 정보
	 * @param request 강의 신청서 내용
	 */
	@Transactional
	@Override
	public String createLectureApply(
		UsersVO realUser
		, LctOpenApplyReq request
	) {
		// 1. 교수가 강의를 신청할 수 있는 상태인지 확인합니다.
		// 재직 중인 교수만 가능합니다.
		String professorNo = realUser.getUserNo();
		
		var professor = professorMapper.selectProfessor(professorNo);
		if("PRF_STATUS_RETD".equals(professor.getPrfStatusCd())) // 퇴직
			throw new RuntimeException("재직 상태로 인하여 현재 강의 개설이 불가능합니다.");
		if("PRF_STATUS_DECD".equals(professor.getPrfStatusCd())) // 사망
			throw new RuntimeException("재직 상태로 인하여 현재 강의 개설이 불가능합니다.");
		
		// 2. 승인을 생성합니다.		
		String userCurrentUnivDeptCd = professor.getUnivDeptCd();
		
		// 과목이 "소속된 학과"의 "학과장"을 찾아야 합니다.
		ProfessorInfo chief = professorMapper.selectDeptCheifProfessor(userCurrentUnivDeptCd);
		
		// 해당 학과장이 없다면, "담당 직원" 에게 승인이 직통으로 들어갑니다.
		String userId = (chief == null) ? "USER00000000001" : chief.getUserId();
		
		var newApproval = new ApprovalInfo();
		newApproval.setApplyTypeCd("LCT_OPEN");
		newApproval.setUserId(userId); // 승인자 ID
		newApproval.setApplicantUserId(realUser.getUserId()); // 신청자 ID
		
		approvalMapper.insertNewApproval(newApproval); // 신청 생성 (교수 -> 학과장)
		String approveId = newApproval.getApproveId(); // 셀렉트키로 생성된 기본키 가져오기
		
		// 3. 강의개설 신청을 생성합니다.
		var openApply = new LctOpenApplyInfo();
		
		// 옮겨담기
		BeanUtils.copyProperties(request, openApply);
		openApply.setProfessorNo(professorNo);
		openApply.setApproveId(approveId);
		
		applyMapper.insertLctOpenApply(openApply); // 신청 생성
		String lctApplyId = openApply.getLctApplyId();
		
		// 4. 강의 주차별계획을 기록합니다.
		
		var weekbyList = request.getWeekbyList();
		// DB와 소통할 entity List 형식으로 바꿔서
		List<LctApplyWeekbyInfo> dbWeekbyList = weekbyList.stream().map(weekby -> {
			var dbWeekby = new LctApplyWeekbyInfo();
			BeanUtils.copyProperties(weekby, dbWeekby);
			dbWeekby.setLctApplyId(lctApplyId);
			
			return dbWeekby;
		}).toList();
		
		applyMapper.insertLctApplyWeekbyList(dbWeekbyList);
		
		// 5. 강의 성적산출항목을 기록합니다.
		
		var criteriaList = request.getGraderatioList();
		// DB와 소통할 entity List 형식으로 바꿔서
		List<LctApplyGraderatioInfo> dbCriteriaList = criteriaList.stream().map(criteria -> {
			var dbCriteria = new LctApplyGraderatioInfo();
			BeanUtils.copyProperties(criteria, dbCriteria);
			dbCriteria.setLctApplyId(lctApplyId);
			
			return dbCriteria;
		}).toList();
		
		applyMapper.insertLctApplyGraderatioList(dbCriteriaList);
		
		return lctApplyId;
		// 여기까지 오류 없이 왔으면 승인요청과 신청, 따까리 데이터까지 잘 들어간거임.
	}

	/**
	 * 교수 자신이 신청한 강의신청 목록을 반환합니다.
	 * 
	 * @param realUser 로그인한, 강의 신청하는 교수 정보
	 * @return
	 */
	@Override
	public List<LectureOpenApplyLabelResp> readMyLectureApplyList(
		UsersVO realUser
	) {
		String professorNo = realUser.getUserNo();
		
		// 1. 강의신청, 과목, 승인에 대한 정보 가져오기
		var result = applyMapper.selectLctApplyList(professorNo);
		
		// 2. 응답에 필요한 데이터로 바꾸기
		var labelList = result.stream().map(res -> {
			var resp = new LectureOpenApplyLabelResp();
			resp.setLctApplyId(res.getLctOpenApplyInfo().getLctApplyId());

			// 어떤 학기에 개설하는 강의임?
			resp.setYeartermCd(res.getLctOpenApplyInfo().getYeartermCd());
			resp.setYeartermName(cache.getYeartermName(resp.getYeartermCd()));
			
			// 2-1. 응답에 필요한 "과목" 쪽 데이터 옮겨담기
			var subject = res.getSubjectInfo();
			resp.setSubjectName(subject.getSubjectName());
			resp.setUnivDeptName(cache.getUnivDeptName(subject.getUnivDeptCd()));
			resp.setCompletionName(cache.getCodeName(subject.getCompletionCd()));
			resp.setCredit(subject.getCredit());
			resp.setHour(subject.getHour());
			
			// 2-2. 응답에 필요한 "승인" 쪽 데이터 옮겨담기
			var approval = res.getApprovalInfo();
			resp.setApprovalId(approval.getApproveId());
			
			String approved = approval.getApproveYnnull();
			if(approved == null) resp.setApplyStatus("대기중");
			else if("Y".equals(approved)) resp.setApplyStatus("승인");
			else resp.setApplyStatus("반려");
			
			// 2-3. "신청일" 옮겨담기
			var apply = res.getLctOpenApplyInfo();
			resp.setApplyAt(apply.getApplyAt());
			
			// 2-4. 승인자 정보
			String approverUserId = approval.getUserId();
			
			UsersVO approverUserInfo = usersMapper.selectUser(approverUserId);
			String approverName = approverUserInfo.getLastName() + approverUserInfo.getFirstName();
			
			resp.setApproverId(approverUserId);
			resp.setApproverName(approverName);
			
			if(approverUserInfo.getUserId().length() == 7) // 직원이면
				resp.setApproverRole("인사처 직원");
			else resp.setApproverRole("학과장");
			
			return resp;
		}).toList();
		
		return labelList;
	}

	/**
	 * 열람 권한이 있는 강의신청 상세를 조회합니다. <br>
	 * "작성자 본인"과 "승인자" 에게만 열람 권한이 있습니다.
	 * 
	 * @param realUser 로그인한 교수 정보
	 * @param lctOpenApplyId 열람할 강의신청ID
	 * @return
	 */
	@Override
	public LectureOpenApplyDetailResp readLectureApplyDetail(
		UsersVO realUser
		, String lctOpenApplyId
	) {
		
		// 1. 일단 강의신청데이터 갖고오기
		LctOpenApplyDetailDTO result = applyMapper.selectLctApplyDetail(lctOpenApplyId);
		if(result == null)
			throw new RuntimeException("존재하지 않는 강의신청ID입니다.");
		
		// 2. 사용자가 직원이거나, 신청자 or 승인자인가?
		String userType = realUser.getUserType();
		
		// 2-1. 직원은 다 볼 수 있음
		if(!"ROLE_STAFF".equals(userType)) {
			// 2-2. 직원이 아니면, 신청자or승인자만 볼 수 있음
			String userId = realUser.getUserId();
			
			var approval = result.getApprovalInfo();
			boolean isApprover = userId.equals(approval.getUserId());
			boolean isApplicant = userId.equals(approval.getApplicantUserId());
			
			if(!isApprover && !isApplicant)
				throw new RuntimeException("강의 신청서 열람 권한이 없습니다..");
		}
		
		// 3. 응답 만들기
		LectureOpenApplyDetailResp resp = new LectureOpenApplyDetailResp();

		/* ------------ LctOpenApplyResp ------------ */
		var lctApplyInfo = result.getLctOpenApplyInfo();
		var lctApplyResp = new LectureOpenApplyDetailResp.LctOpenApplyResp();
		BeanUtils.copyProperties(lctApplyInfo, lctApplyResp,
			"subjectCd", "yeartermCd", "approveId"); // 제외 필드
		
		lctApplyResp.setYeartermName(cache.getYeartermName(lctApplyInfo.getYeartermCd()));
		resp.setLctOpenApplyResp(lctApplyResp);

		/* ------------ SubjectResp ------------ */
		var subj = result.getSubjectInfo();
		var subjResp = new LectureOpenApplyDetailResp.SubjectResp();
		BeanUtils.copyProperties(subj, subjResp,
			"univDeptCd", "completionCd", "subjectTypeCd"); // 코드 필드 제외
		subjResp.setUnivDeptName(
			cache.getUnivDeptName(subj.getUnivDeptCd())
		);
		subjResp.setCompletionName(
			cache.getCodeName(subj.getCompletionCd())
		);
		subjResp.setSubjectTypeName(
			cache.getCodeName(subj.getSubjectTypeCd())
		);
		resp.setSubjectResp(subjResp);

		/* ------------ ApprovalResp ------------ */
		var appr = result.getApprovalInfo();
		var apprResp = new LectureOpenApplyDetailResp.ApprovalResp();
		BeanUtils.copyProperties(appr, apprResp,
			"applyTypeCd", "approveYnnull", "attachFileId"); // 제외 필드
		apprResp.setApproveStatus(
			(appr.getApproveYnnull() == null) ? "대기중" : "Y".equals(appr.getApproveYnnull()) ? "승인" : "반려"
		);
		
		UsersVO approverUserInfo = usersMapper.selectUser(appr.getUserId());
		String approverName = approverUserInfo.getLastName() + approverUserInfo.getFirstName();
		apprResp.setApproverUserName(approverName);
		
		if(approverUserInfo.getUserId().length() == 7) // 직원이면
			apprResp.setApproverUserRole("직원");
		else apprResp.setApproverUserRole("교수");
		
		UsersVO applicantUserInfo = usersMapper.selectUser(appr.getApplicantUserId());
		String applicantName = applicantUserInfo.getLastName() + applicantUserInfo.getFirstName();
		apprResp.setApplicantUserName(applicantName);
		
		if(applicantUserInfo.getUserId().length() == 7) // 직원이면
			apprResp.setApplicantUserRole("직원");
		else apprResp.setApplicantUserRole("교수");
		
		resp.setApprovalResp(apprResp);

		/* ------------ 주차별 계획 ------------ */
		List<LectureOpenApplyDetailResp.LctApplyWeekbyResp> weekbyList =
			result.getLctApplyWeekbyInfoList().stream().map(src -> {
				var dst = new LectureOpenApplyDetailResp.LctApplyWeekbyResp();
				BeanUtils.copyProperties(src, dst, "lctApplyId");
				return dst;
			}).toList();
		resp.setLctApplyWeekbyRespList(weekbyList);

		/* ------------ 성적 비율 ------------ */
		List<LectureOpenApplyDetailResp.LctApplyGraderatioResp> ratioList =
			result.getApplyGraderatioInfoList().stream().map(src -> {
				var dst = new LectureOpenApplyDetailResp.LctApplyGraderatioResp();
				BeanUtils.copyProperties(src, dst, "lctApplyId");
				return dst;
			}).toList();
		resp.setApplyGraderatioRespList(ratioList);

		return resp;
	}

	@Override
	public List<LectureOpenApprovalLabelResp> readMyLectureApprovalList(
		UsersVO realUser
	) {
		String userId = realUser.getUserId();
		
		// 1. 강의신청, 과목, 승인에 대한 정보 가져오기
		List<LctOpenApplySimpleDTO> result = applyMapper.selectLctApprovalList(userId);
		
		// 2. 응답에 필요한 데이터로 바꾸기
		var labelList = result.stream().map(res -> {
			var resp = new LectureOpenApprovalLabelResp();
			resp.setLctApplyId(res.getLctOpenApplyInfo().getLctApplyId());
			
			// 어떤 학기에 개설하는 강의임?
			resp.setYeartermCd(res.getLctOpenApplyInfo().getYeartermCd());
			resp.setYeartermName(cache.getYeartermName(resp.getYeartermCd()));
			
			// 2-1. 응답에 필요한 "과목" 쪽 데이터 옮겨담기
			var subject = res.getSubjectInfo();
			resp.setSubjectName(subject.getSubjectName());
			resp.setUnivDeptName(cache.getUnivDeptName(subject.getUnivDeptCd()));
			resp.setCompletionName(cache.getCodeName(subject.getCompletionCd()));
			resp.setCredit(subject.getCredit());
			resp.setHour(subject.getHour());
			
			// 2-2. 응답에 필요한 "승인" 쪽 데이터 옮겨담기
			var approval = res.getApprovalInfo();
			resp.setApprovalId(approval.getApproveId());
			
			String approved = approval.getApproveYnnull();
			if(approved == null) resp.setApplyStatus("대기중");
			else if("Y".equals(approved)) resp.setApplyStatus("승인");
			else resp.setApplyStatus("반려");
			
			// 2-3. "신청일" 옮겨담기
			var apply = res.getLctOpenApplyInfo();
			resp.setApplyAt(apply.getApplyAt());
			
			// 2-4. 신청자 정보
			String applicantUserId = approval.getApplicantUserId();
			
			UsersVO applicantUserInfo = usersMapper.selectUser(applicantUserId);
			String applicantId = applicantUserInfo.getUserId();
			String applicantName = applicantUserInfo.getLastName() + applicantUserInfo.getFirstName();
			
			resp.setApplicantId(applicantId);
			resp.setApplicantName(applicantName);
			
			var applicantProfessor = professorMapper.selectBasicProfessorInfoByUserId(userId);
			if(applicantProfessor == null)
				resp.setApplicantUnivDeptName("소속 불명");
			else
				resp.setApplicantUnivDeptName(cache.getUnivDeptName(applicantProfessor.getUnivDeptCd()));
			
			return resp;
		}).toList();
		
		return labelList;
	}
	
	/**
	 * 1~n개의 강의개설신청 승인에 대한 처리 요청을 받아, <br>
	 * 사용자 교수가 승인자로 등록되었는지 확인하고, <br>
	 * 모든 승인을 요청에 따라 "승인" 합니다.
	 * 
	 * @param realUser 로그인한 교수 정보
	 * @param request 일괄 처리할 승인ID와 처리할 액션(승인or반려)
	 */
	@Transactional
	@Override
	public void approveLctApply(
		UsersVO realUser
		, LctApprovalExecuteReq request
	) {
		String userId = realUser.getUserId();
		
		// TODO 이건 컨트롤러에서 검증해야 함.
		List<String> approvalIds = request.getApproveIds();
		if(approvalIds.size() == 0)
			throw new RuntimeException("처리할 신청이 없습니다.");
		
		List<ApprovalInfo> approvalList = approvalMapper.selectApprovalList(approvalIds);
		
		for(var eachApproval : approvalList) {
			// 승인자로 등록되었는지 확인하고, 아니면 예외 던지기
			if(!userId.equals(eachApproval.getUserId()))
				throw new RuntimeException("승인 권한이 없는 요청이 포함되어 있습니다.");
			// 이미 승인하거나 반려한 것은 처리 불가능
			if(eachApproval.getApproveYnnull() != null)
				throw new RuntimeException("대기중인 신청만 처리할 수 있습니다.");
		}
		
		// 일괄 승인 처리, 반복문으로 하나씩.
		try {
			for(String approveId : approvalIds) {
				
				// 1. 승인 상태를 처리하고,
				ApprovalInfo appr = new ApprovalInfo();
				appr.setApproveId(approveId);
				appr.setApproveYnnull("Y");
				appr.setComments(request.getComments());
				
				approvalMapper.updateApprovalStatus(appr);
				
				// 2. 강의 신청을 강의로 옮기기.
				LctOpenApplyDetailDTO res = applyMapper.selectLctApplyByApproveId(approveId);
				
				LctOpenApplyInfo lctApply = res.getLctOpenApplyInfo();
				LectureInfo lecture = new LectureInfo();
				
				// 강의정원은 교수가 작성한 예상정원대로.
				lecture.setMaxCap(lctApply.getExpectCap());
				
				// 2-1. 신청서대로 강의 레코드 생성
				lectureMapper.insertLectureWithApply(approveId, lecture);
				
				// 2-2. 셀렉트키로 생성한 강의ID
				String newLectureId = lecture.getLectureId();
				
				// 2-3. 두 테이블은 기본키 컬럼을 제외한 구조가 똑같으므로 복사가 가능
				lectureMapper.insertLctWeekbyWithApply(approveId, newLectureId);
				lectureMapper.insertLctGraderatioWithApply(approveId, newLectureId);
			}
		} catch(Exception e) {
			e.printStackTrace();
			
			throw new RuntimeException("강의신청 일괄승인 중 오류가 발생하였습니다. 한 건씩 처리해 보시고, 오류가 지속된다면 교직원실에 문의해 주세요.");
		}
	}
	
	/**
	 * 1~n개의 강의개설신청 승인에 대한 처리 요청, <br>
	 * 사용자 교수가 승인자로 등록되었는지 확인하고, <br>
	 * 모든 승인을 일괄 "반려" 합니다.
	 * 
	 * @param realUser 로그인한 교수 정보
	 * @param request 일괄 처리할 승인ID와 처리할 액션(승인or반려)
	 */
	@Transactional
	@Override
	public void rejectLctApply(
		UsersVO realUser
		, LctApprovalExecuteReq request
	) {
		String userId = realUser.getUserId();
		
		// TODO 이건 컨트롤러에서 검증해야 함.
		List<String> approvalIds = request.getApproveIds();
		if(approvalIds.size() == 0)
			throw new RuntimeException("처리할 신청이 없습니다.");
		
		List<ApprovalInfo> approvalList = approvalMapper.selectApprovalList(approvalIds);
		
		for(var eachApproval : approvalList) {
			// 승인자로 등록되었는지 확인하고, 아니면 예외 던지기
			if(!userId.equals(eachApproval.getUserId()))
				throw new RuntimeException("승인 권한이 없는 요청이 포함되어 있습니다.");
			// 이미 승인하거나 반려한 것은 처리 불가능
			if(eachApproval.getApproveYnnull() != null)
				throw new RuntimeException("대기중인 신청만 처리할 수 있습니다.");
		}
		
		// 일괄 승인or반려 처리, 반복문으로 하나씩.
		try {
			for(String approveId : approvalIds) {
				
				// 1. 승인 상태를 처리하고,
				ApprovalInfo appr = new ApprovalInfo();
				appr.setApproveId(approveId);
				appr.setApproveYnnull("N");
				appr.setComments(request.getComments());
				
				approvalMapper.updateApprovalStatus(appr);
			}
		} catch(Exception e) {
			throw new RuntimeException("강의신청 일괄반려 중 오류가 발생하였습니다. 한 건씩 처리해 보시고, 오류가 지속된다면 교직원실에 문의해 주세요.");
		}
	}
}