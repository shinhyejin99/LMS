package kr.or.jsu.classroom.professor.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.info.EnrollGPAInfo;
import kr.or.jsu.classroom.dto.info.LctSectionScoreInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.request.ender.LctGPARecordReq;
import kr.or.jsu.classroom.dto.request.ender.LctSectionScoreUpsertReq;
import kr.or.jsu.classroom.dto.request.finalize.ChangeScoreReq;
import kr.or.jsu.classroom.dto.response.ender.AttendanceStatusResp;
import kr.or.jsu.classroom.dto.response.ender.EnrollingStudentsAndScoreResp;
import kr.or.jsu.classroom.dto.response.ender.ExamWeightAndStatusResp;
import kr.or.jsu.classroom.dto.response.ender.LctSectionScoreResp;
import kr.or.jsu.classroom.dto.response.ender.LectureProgressResp;
import kr.or.jsu.classroom.dto.response.ender.TaskWeightAndStatusResp;
import kr.or.jsu.classroom.professor.service.ClassPrfEnderService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.mybatis.mapper.LctSectionScoreMapper;
import kr.or.jsu.mybatis.mapper.LectureMapper;
import kr.or.jsu.mybatis.mapper.classroom.ClassroomEnderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ClassPrfEnderServiceImpl implements ClassPrfEnderService {
	
	private final LectureMapper lectureMapper;
	private final ClassroomEnderMapper enderMapper;
	private final LctSectionScoreMapper scoreMapper;
	
	private final ClassroomCommonService commonService;
	private final DatabaseCache cache;
	
	
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
	 * 사용자가 담당 교수인지 확인하고 예외처리하는 메서드
	 * 
	 * @param lectureId 강의ID
	 */
	private void check(String lectureId) {
		String userNo = getUser().getRealUser().getUserNo();
		boolean result = commonService.isRelevantClassroom(userNo, lectureId);
		if(!result) throw new RuntimeException("강의 담당 교수가 아닙니다.");
	};
	
	@Override
	public LectureProgressResp readProgress(
		String lectureId
	) {
		check(lectureId);
		return enderMapper.getProgress(lectureId);
	}

	@Override
	public List<TaskWeightAndStatusResp> readTaskStatus(
		String lectureId
	) {
		check(lectureId);
		return enderMapper.getTaskStatus(lectureId);

	}

	@Override
	public List<ExamWeightAndStatusResp> readExamStatus(
		String lectureId
	) {
		check(lectureId);
		return enderMapper.getExamStatus(lectureId);
	}
	
	@Override
	public List<AttendanceStatusResp> readAttendanceStatus(
			String lectureId
	) {
		check(lectureId);
		return enderMapper.getAttendanceStatus(lectureId);
	}

	@Override
	public void mergeEachSectionScore(
		String lectureId
		, List<LctSectionScoreUpsertReq> request
	) {
		// 1. 점수수정 요청자가 강의 담당자인지 확인
		check(lectureId);
		
		// 3. 요청을 entity로 변경
		var scores = request.stream().map(req -> {
			
			LctSectionScoreInfo entity = new LctSectionScoreInfo();
			BeanUtils.copyProperties(req, entity);
			entity.setLectureId(lectureId);
			
			return entity;
		}).toList();
		
		scoreMapper.upsertSectionScore(scores);
	}

	@Override
	public List<LctSectionScoreResp> readEachSectionScore(
		String lectureId
		, String gradeCriteriaCd
	) {
		// 1. 점수 요청자가 강의 담당자인지 확인
		check(lectureId);
		
		var resultList = scoreMapper.selectSectionScoreList(lectureId, gradeCriteriaCd);
		
		return resultList.stream().map(result -> {
			var resp = new LctSectionScoreResp();
			BeanUtils.copyProperties(result, resp);
			
			return resp;
		}).toList();
	}
	
	/**
	 * 종강 절차 3, 순위별로 나온 학생 목록에 평점구간을 설정하여 저장합니다. <br>
	 * STU_ENROLL_LCT에는 AUTO_SCORE(숫자 점수), ENROLL_GPA에는 평점이 저장됩니다. <br>
	 * P(pass), NP(not pass)일 경우는 숫자 점수는 0점입니다.
	 * 
	 * @param lectureId
	 * @param request
	 */
	@Transactional
	@Override
	public void recordGPAAndScore(
		String lectureId
		, List<LctGPARecordReq> request
	) {
		// 1. 점수 요청자가 강의 담당자인지 확인
		check(lectureId);
		
		// 2. 이미 1차 성적확정을 마쳤는지 확인
		var lecture = lectureMapper.selectLecture(lectureId);
		if(lecture.getLectureInfo().getEndAt() != null)
			throw new RuntimeException("이미 1차 성적 확정을 마쳤습니다. 최종 성적 확정을 통해 수정하세요.");
		
		List<EnrollGPAInfo> gpaList = request.stream().map(req -> {
			var gpa = new EnrollGPAInfo();
			
			BeanUtils.copyProperties(req, gpa);
			return gpa;
		}).toList();
		
		List<StuEnrollLctInfo> scoreList = request.stream().map(req -> {
			var score = new StuEnrollLctInfo();
			
			score.setEnrollId(req.getEnrollId());
			
			String gpaCd = req.getGpaCd();
			String scoreStr = cache.getCodeName(gpaCd);
			Double gapTosScore = Double.parseDouble(scoreStr);   
			
			score.setAutoGrade(gapTosScore);
			
			return score;
		}).toList();
		
		// ENROLL_GPA 테이블에 평점 기록하고
		enderMapper.insertAllStudentGPA(gpaList);
		
		// STU_ENROLL_LCT 테이블에 AUTO_GRADE 기록
		enderMapper.updateAllStudentScore(scoreList);
		
		// 성적이의제기신청기간으로 변경(endAt 기록)
		enderMapper.updateLectureStatusToDone(lectureId);
	}

	/**
	 * 성적 확정절차 1, 모든 수강생의 성적 정보를 가져옵니다. <br>
	 * 학생별 수강ID, 산출평점, 수정평점+이유, 각 성적 산출 항목별 점수를 반환합니다. 
	 * 
	 * @param lectureId
	 * @return
	 */
	@Override
	public List<EnrollingStudentsAndScoreResp> readStudentAndScoreList(
		String lectureId
	) {
		// 1. 점수 요청자가 강의 담당자인지 확인
		check(lectureId);
		
		return enderMapper.selectStudentAndScoreList(lectureId);
	}

	@Transactional
	@Override
	public void modifyStudentGrade(
		String lectureId
		, ChangeScoreReq request
	) {
		// 1. 요청자가 강의 담당교수인지 확인
		check(lectureId);
		
		// 2. STU_ENROLL_LCT 테이블 수정
		var enrollment = new StuEnrollLctInfo();
		// 2-1. 평점(영어) 제외한 필드 담고
		BeanUtils.copyProperties(request, enrollment);
		// 2-2. 평점(영어)는 숫자 평점 (0 ~ 4.5) 로 변경한 후
		String GPAStr = cache.getCodeName(request.getGpaCd());
		enrollment.setFinalGrade(Double.parseDouble(GPAStr));
		
		enderMapper.updateEnrollmentFinalScore(enrollment);
		
		log.info("reqeust : {}", PrettyPrint.pretty(request));
		
		// 3. ENROLL_GPA 수정
		var gpa = new EnrollGPAInfo();
		BeanUtils.copyProperties(request, gpa);
		
		enderMapper.updateEnrollmentFinalGPA(gpa);	
	}

	/**
	 * 성적 확정 절차 3 <br>
	 * 1. 모든 학생들의 평점을 확정합니다. (finalGrade가 null인 경우, autoGrade를 넣음) <br>
	 * 2. 모든 학생들의 수강 상태를 변경합니다. (default 수강완료, F학점일 경우 낙제) 
	 * 3. 해당 강의의 성적확정여부를 Y로 변경합니다.
	 */
	@Transactional
	@Override
	public void finalizeLecture(
		String lectureId
	) {
		// 1. 요청자가 강의 담당교수인지 확인
		check(lectureId);
		
		// 2. 성적확인기간 중이었는지 확인
		var lecture = lectureMapper.selectLecture(lectureId);
		
		// 2-1. 강의가 끝났고 성적확인기간에 들어갔는가?
		if(lecture.getLectureInfo().getEndAt() == null)
			throw new RuntimeException("강의를 종강시키려면 먼저 성적확인기간을 거쳐야 합니다.");
		
		if("Y".equals(lecture.getLectureInfo().getCancelYn()))
			throw new RuntimeException("폐강된 강의입니다.");
		
		if("Y".equals(lecture.getLectureInfo().getScoreFinalizeYn()))
			throw new RuntimeException("이미 성적확정이 끝나 종강된 강의입니다. 교직원에게 문의해 주세요.");
		
		enderMapper.updateToFinalizeAllScore(lectureId);
		enderMapper.updateToFinalizeLecture(lectureId);
	}
}