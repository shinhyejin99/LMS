package kr.or.jsu.classroom.student.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.info.StuReviewLctInfo;
import kr.or.jsu.classroom.dto.response.ender.ScoreAndGPAResp;
import kr.or.jsu.classroom.dto.response.review.LectureReviewQuestionResp;
import kr.or.jsu.classroom.student.service.ClassStuEnderService;
import kr.or.jsu.classroom.student.service.ClassroomStudentService;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.mybatis.mapper.classroom.ClassroomEnderMapper;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ClassStuEnderServiceImpl implements ClassStuEnderService {
	
	private final ClassroomEnderMapper enderMapper;
	
	private final ClassroomStudentService rootService;
	private final DatabaseCache cache;

	@Override
	public List<String> reviewNeededLectures(
		UsersVO user
	) {
		String studentNo = user.getUserNo();
		
		var resultList = enderMapper.selectReviewNeededLectureList(studentNo);
		
		return resultList.stream().map(LectureInfo::getLectureId).toList();
	}

	@Override
	public List<LectureReviewQuestionResp> readReviewQuestionList(
		UsersVO user
		, String lectureId
	) {
		// 1. 수강한 사람인지 확인
		rootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		var resultList = enderMapper.selectReviewQuestionList(lectureId);
		
		return resultList.stream().map(result -> {
			var resp = new LectureReviewQuestionResp();
			
			BeanUtils.copyProperties(result, resp);
			resp.setAnswerTypeName(cache.getCodeName(resp.getAnswerTypeCd()));
			
			return resp;
			
		}).toList();
	}

	/**
	 * 사용자가 강의 수강생인지 확인 후, 작성한 강의평가를 저장합니다. 
	 * 
	 * @param user
	 * @param lectureId 강의ID
	 * @param resultJson 강의평가 JSON
	 */
	public void createReview(
		UsersVO user
		, String lectureId
		, String resultJson	
	) {
		// 1. 수강한 사람인지 확인
		StuEnrollLctInfo enroll = rootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		if(!"ENR_DONE".equals(enroll.getEnrollStatusCd()))
			throw new RuntimeException("수강 완료한 수강생만 강의평가를 작성할 수 있습니다.");
		
		String enrollId = enroll.getEnrollId();
		
		// 2. 이미 강의평가 작성했는지 확인
		List<StuReviewLctInfo> reviewList = enderMapper.selectReviews(lectureId);
		
		Set<String> enrollIdSet = reviewList.stream().map(StuReviewLctInfo::getEnrollId).collect(Collectors.toSet());
		if(enrollIdSet.contains(enrollId))
			throw new RuntimeException("작성한 강의평가는 수정할 수 없습니다.");
		
		enderMapper.insertReviewJson(enrollId, resultJson);
		
	}

	@Override
	public ScoreAndGPAResp readLectureScore(
		UsersVO user
		, String lectureId
	) {
		// 1. 수강한 사람인지 확인
		StuEnrollLctInfo enroll = rootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		if(!"ENR_DONE".equals(enroll.getEnrollStatusCd()))
			throw new RuntimeException("수강 완료한 수강생만 성적을 확인할 수 있습니다.");
		
		var resp = new ScoreAndGPAResp();
		
		resp.setStudentNo(user.getUserNo());
		resp.setLectureId(lectureId);
		BeanUtils.copyProperties(enroll, resp);
		
		String gpaCd = enderMapper.selectStudentGPA(enroll.getEnrollId());
		resp.setGpaCd(gpaCd);
		
		return resp;
	}
}