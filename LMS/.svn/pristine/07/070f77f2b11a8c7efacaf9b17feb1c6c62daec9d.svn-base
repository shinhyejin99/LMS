package kr.or.jsu.classroom.professor.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.db.EnrollSimpleDTO;
import kr.or.jsu.classroom.dto.db.exam.StudentAndExamSubmitDTO;
import kr.or.jsu.classroom.dto.info.ExamSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctExamInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.request.ExamOfflineReq;
import kr.or.jsu.classroom.dto.request.ExamWeightValueModReq;
import kr.or.jsu.classroom.dto.request.exam.ExamModifyReq;
import kr.or.jsu.classroom.dto.request.exam.ScoreModifyReq;
import kr.or.jsu.classroom.dto.response.exam.EachStudentExamScoreResp;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp.ExamSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp.StudentAndSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.LctExamLabelResp_PRF;
import kr.or.jsu.classroom.professor.service.ClassPrfExamService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.mybatis.mapper.LctExamMapper;
import kr.or.jsu.mybatis.mapper.StuEnrollLctMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassPrfExamServiceImpl implements ClassPrfExamService {
	// TODO 예외처리(전역)
	// TODO 종강된 강의에는 암것도 못하게
	private final LctExamMapper examMapper;
	private final StuEnrollLctMapper enrollMapper;
	
	private final ClassroomCommonService commonService;

	private CustomUserDetails getUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null && auth.isAuthenticated()) {
			Object principal = auth.getPrincipal();
			
			if (principal instanceof CustomUserDetails) {
				CustomUserDetails user = (CustomUserDetails) principal;
				return user;
			}
		}
		
		throw new RuntimeException("사용자 정보를 가져올 수 없습니다.");
	}
	
	/**
	 * 오프라인 시험 결과를 기록합니다. <br>
	 * 결과를 기록한 후 해당 페이지로 리다이렉트하기 위해 생성된 시험ID를 반환합니다.
	 * 
	 * @param newExam 시험에 대한 정보
	 * @param eachResult 각 학생의 수강ID & 시험점수를 기록
	 * @return 셀렉트키로 생성된 시험ID
	 */
	@Transactional
	@Override
	public String createOfflineExamRecord(ExamOfflineReq examAndEachResults) {
		// 1. 관련있는 교수인지 확인 위해 강의ID, 교번 추출
		String lectureId = examAndEachResults.getLectureId();
		String prfNo = getUser().getRealUser().getUserNo();
		
		boolean relevant = commonService.isRelevantClassroom(prfNo, lectureId);
		if(!relevant) throw new RuntimeException("관련없는강의");
		
		// 2. 요청에서 데이터 꺼내서 DB에 넣을 객체로 옮겨담기
		// 2-1. 1건의 시험 데이터
		LctExamInfo examInfo = new LctExamInfo();
		BeanUtils.copyProperties(examAndEachResults, examInfo);
		
		// 2-2. n건의 학생들 응시기록 데이터
		List<ExamOfflineReq.EachResult> resultList = examAndEachResults.getEachResultList();
		List<ExamSubmitInfo> eachResult = resultList.stream().map(each -> {
			ExamSubmitInfo submit = new ExamSubmitInfo();
			submit.setEnrollId(each.getEnrollId());
			submit.setAutoScore(each.getScore());
			return submit;
		}).collect(Collectors.toList());
		
		// 3. DB에 입력
		examMapper.insertOfflineExam(examInfo);
		String lctExamId = examInfo.getLctExamId();
		examMapper.insertOfflineExamSubmit(lctExamId, eachResult, examInfo.getEndAt());
		
		return lctExamId;
	}

	@Override
	public List<LctExamLabelResp_PRF> readExamList(String lectureId) {
		// 1. 관련있는 교수인지 확인
		String prfNo = getUser().getRealUser().getUserNo();
		
		boolean relevant = commonService.isRelevantClassroom(prfNo, lectureId);
		if(!relevant) throw new RuntimeException("관련없는강의");
		
		List<LctExamInfo> resultList = examMapper.selectExamList_PRF(lectureId);
		
		return resultList.stream().map(result -> {
			LctExamLabelResp_PRF resp = new LctExamLabelResp_PRF();
			BeanUtils.copyProperties(result, resp);
			resp.setExamType(("OFF".equals(resp.getExamType())) ? "오프라인" : "온라인");
			return resp;
		}).collect(Collectors.toList());	
	}

	@Transactional
	@Override
	public void modifyAllWeightValues(
		String lectureId
		, List<ExamWeightValueModReq> request
	) {
		// 1. 관련있는 교수인지 확인
		String prfNo = getUser().getRealUser().getUserNo();
		
		boolean relevant = commonService.isRelevantClassroom(prfNo, lectureId);
		if(!relevant) throw new RuntimeException("관련없는강의");
		
		// 2. 가중치 수정하려는 시험들이 강의에 속해있는지 확인
		List<LctExamInfo> resultList = examMapper.selectExamList_PRF(lectureId);
		Set<String> ownExamIds = resultList.stream().map(res -> res.getLctExamId()).collect(Collectors.toSet());
		
		for(ExamWeightValueModReq req : request) {
			// 강의에 속하지 않은 시험ID가 요청에 들어있으면
			if(!ownExamIds.contains(req.getLctExamId()))
				throw new RuntimeException("해당 강의에서 출제된 시험이 아닙니다.");
		};
		
		// 3. 옮겨담고 가중치 수정 실행
		List<LctExamInfo> weightValues = request.stream().map(req -> {
			LctExamInfo info = new LctExamInfo();
			BeanUtils.copyProperties(req, info);
			return info;
		}).collect(Collectors.toList());
		
		// 4. 수정요청과 실제 수정된 레코드 수가 같지 않을 경우 예외발생
		int modifiedCount = examMapper.updateWeightValue(weightValues);
		if(modifiedCount != request.size()) throw new RuntimeException("가중치 수정 중 오류 발생.");
		
		// 5. 수정된 후 모든 시험 불러와서, 가중치 100이 맞춰졌는지 확인
		List<LctExamInfo> allExamList = examMapper.selectExamList_PRF(lectureId);
		
		List<String> missedExamNames = new ArrayList<>();
		int sum = 0;
		for(LctExamInfo eachExam : allExamList) {
			Integer eachWV = eachExam.getWeightValue();
			if(eachWV == null) missedExamNames.add(eachExam.getExamName());
			else sum += eachWV;
		}
		
		if(missedExamNames.size() != 0) {
			String missedExamNameString = 
				missedExamNames.stream()
								.map(name -> "\"" + name + "\"")
								.collect(Collectors.joining(", "));
			throw new RuntimeException(missedExamNameString + " 시험의 가중치를 입력하세요.");
		}
			
		if(sum != 100) throw new RuntimeException("시험 가중치 총합은 정확히 100이어야 합니다.");
	}

	/**
	 * 강의에서 출제된 특정 시험에 대한 내용과 함께 <br>
	 * 중간에 하차한 수강생을 포함한 모든 학생들의 응시 기록과 점수를 반환합니다. 
	 * 
	 * @param lectureId 강의ID
	 * @param lctExamId 시험ID
	 * @return 
	 */
	@Override
	public ExamAndEachStudentsSubmitResp readExamDetail(
		String lectureId
		, String lctExamId
	) {
		// 0. 관련있는 교수인지 확인
		String prfNo = getUser().getRealUser().getUserNo();
		
		boolean relevant = commonService.isRelevantClassroom(prfNo, lectureId);
		if(!relevant) throw new RuntimeException("관련없는강의");
		
		// 1. 시험 단건 조회
		LctExamInfo exam = examMapper.selectExam(lctExamId);
		if(exam == null) 
			throw new RuntimeException("존재하지 않는 시험ID입니다.");
		if(!lectureId.equals(exam.getLectureId()))
			throw new RuntimeException("해당 강의에서 출제된 시험이 아닙니다.");
		
		// 2. 모든 수강생(철회, 포기 포함) 정보 조회
		List<StuEnrollLctInfo> studentList = enrollMapper.selectAllStudentList(lectureId, null);
		List<String> enrollIds = studentList.stream().map(stu -> stu.getEnrollId()).collect(Collectors.toList());
		if(enrollIds == null || enrollIds.size() == 0)
			throw new RuntimeException("요청한 강의를 수강하는 학생이 없습니다."); 
		
		// 3. 그 사람들의 응시 기록 조회
		List<StudentAndExamSubmitDTO> resultList = examMapper.selectSubmitList(lctExamId, enrollIds);
		
		ExamAndEachStudentsSubmitResp resp = new ExamAndEachStudentsSubmitResp();
		
		List<StudentAndSubmitResp> submitList = resultList.stream().map(r -> {
			StudentAndSubmitResp eachSubmit = new StudentAndSubmitResp();
			eachSubmit.setEnrollId(r.getEnrollId());
			
			ExamSubmitResp respSubmit = new ExamSubmitResp();
			BeanUtils.copyProperties(r.getSubmit(), respSubmit);
			eachSubmit.setSubmit(respSubmit);
			
			return eachSubmit;
		}).collect(Collectors.toList());
		
		BeanUtils.copyProperties(exam, resp);
		resp.setSubmitList(submitList);
		
		return resp;
	}
	
	/**
	 * 1. 사용자가 해당 강의 담당 교수인가? <br>
	 * 2. 시험이 해당 강의 소속인가? <br>
	 * 3. 해당 수강ID가 강의 소속인가? <br>
	 * 4. 수강ID의 해당 시험 응시 기록이 있는가? <br>
	 * 이상의 요청에 대한 유효성 검사 이후 <br>
	 * 특정 시험의 특정 수강생에 대한 수정된 점수와 그 이유를 기록합니다.
	 * 
	 * @param lectureId 강의ID
	 * @param lctExamId 시험ID
	 * @param request 대상 수강생 수강ID, 수정된 점수, 수정 이유
	 */
	@Override
	public void modifyScore(
		String lectureId
		, String lctExamId
		, ScoreModifyReq request
	) {
		// 1. 사용자가 담당 강의인지 확인
		validateInChargeAndGetProfessorNo(lectureId);
		
		// 2. 시험이 존재하며 이 강의에서 출제되었는지 확인 후 가져오기
		LctExamInfo exam = validateExistExamAndGetExamInfo(lectureId, lctExamId);
		
		// 3. 이 학생이 강의를 수강중인지 확인 후 가져오기
		String studentNo = request.getStudentNo();
		EnrollSimpleDTO enrollment = validateStudentEnrollingAndGetEnrollment(lectureId, studentNo);
		String enrollId = enrollment.getStuEnrollLctInfo().getEnrollId();
		
		// 4. 이 학생이 시험을 응시했는지 확인 후 가져오기
		ExamSubmitInfo submit = examMapper.selectSubmit(lctExamId, enrollId);
		if(submit == null)
			throw new RuntimeException("해당 학생은 아직 시험을 응시하지 않았습니다.");
		
		// 5. 학생의 점수 수정하며 이유 기록할 객체 생성
		ExamSubmitInfo mod = new ExamSubmitInfo();
		mod.setEnrollId(enrollId);
		mod.setLctExamId(exam.getLctExamId());
		BeanUtils.copyProperties(request, mod);
		
		// 6. 수정 기록
		examMapper.updateScore(mod);
	}
	
	@Override
	public void modifyExam(
		String lectureId
		, String lctExamId
		, ExamModifyReq request
	) {
		// 1. 사용자가 담당 강의인지 확인
		validateInChargeAndGetProfessorNo(lectureId);
		
		// 2. 시험이 존재하며 이 강의에서 출제되었는지 확인 후 가져오기
		LctExamInfo exam = validateExistExamAndGetExamInfo(lectureId, lctExamId);
		
		// 3. 수정 내용 덮어쓰기
		BeanUtils.copyProperties(request, exam);
		
		// 4. 수정
		examMapper.updateExam(exam);
	}
	
	@Override
	public void removeExam(
		String lectureId
		, String lctExamId
	) {
		// 1. 사용자가 담당 강의인지 확인
		validateInChargeAndGetProfessorNo(lectureId);
		
		// 2. 시험이 존재하며 이 강의에서 출제되었는지 확인
		validateExistExamAndGetExamInfo(lectureId, lctExamId);
		
		// 3. 삭제
		examMapper.deleteExamSoftly(lctExamId);
	}
	
	/**
	 * 특정 강의의 (삭제되지 않은) 시험과, 모든 시험별 학생의 점수를 가져옵니다. <br>
	 * 응시하지 않은 경우 0점, 응시했으나 교수가 채점하지 않은 경우 null
	 * 
	 * @param lectureId
	 * @return
	 */
	@Override
	public List<EachStudentExamScoreResp> readAllExamAndEachStudentScore(
		String lectureId
	) {
		
		// 1. 사용자가 담당 강의인지 확인
		validateInChargeAndGetProfessorNo(lectureId);
		
		return examMapper.selectExamAndEachStudentScore(lectureId);
	}
	
	// TODO 예외처리
	
	/**
	 * 사용자가 강의 담당교수인지 확인하고 교번을 반환합니다.
	 * 
	 * @param lectureId
	 * @return 교번
	 * @throws 사용자가 강의 담당교수가 아닐 경우
	 */
	private String validateInChargeAndGetProfessorNo(
		String lectureId
	) throws RuntimeException {
		String prfNo = getUser().getRealUser().getUserNo();
		boolean relevant = commonService.isRelevantClassroom(prfNo, lectureId);
		if(!relevant) throw new RuntimeException("관련없는강의");
		
		return prfNo;
	}
	
	/**
	 * 1. 존재하는 시험ID인지, <br>
	 * 2. 해당 강의에서 출제된 시험인지 확인하고 <br>
	 * 시험 정보를 반환합니다.
	 * 
	 * @param lectureId 검증이 완료된, 사용자가 담당하는 강의ID
	 * @param lctExamId 대상 시험ID
	 * @throws RuntimeException
	 */
	private LctExamInfo validateExistExamAndGetExamInfo(
		String lectureId
		, String lctExamId
	) throws RuntimeException {
		LctExamInfo exam = examMapper.selectExam(lctExamId);
		if(exam == null) 
			throw new RuntimeException("존재하지 않는 시험ID입니다.");
		if(!lectureId.equals(exam.getLectureId()))
			throw new RuntimeException("해당 강의에서 출제된 시험이 아닙니다.");
		return exam;
	}
	
	/**
	 * (존재하지 않을 수도 있는)해당 학생이 강의의 수강생인지 확인하고 <br>
	 * 수강 정보를 반환합니다.
	 * 
	 * @param lectureId 검증이 완료된, 사용자가 담당하는 강의ID
	 * @param studentNo 검증할 학번
	 * @return
	 * @throws RuntimeException
	 */
	private EnrollSimpleDTO validateStudentEnrollingAndGetEnrollment(
		String lectureId
		, String studentNo
	) throws RuntimeException {
		EnrollSimpleDTO enrollment = enrollMapper.selectEnrollingStudent(lectureId, studentNo);
		if(enrollment == null)
			throw new RuntimeException("해당 강의를 수강한 학생이 아닙니다.");
		return enrollment;
	}
}
