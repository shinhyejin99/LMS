package kr.or.jsu.classroom.student.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.dto.db.exam.StudentAndExamSubmitDTO;
import kr.or.jsu.classroom.dto.info.LctExamInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.LctExamLabelResp_STU;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp.ExamSubmitResp;
import kr.or.jsu.classroom.dto.response.exam.ExamAndEachStudentsSubmitResp.StudentAndSubmitResp;
import kr.or.jsu.classroom.student.service.ClassStuExamService;
import kr.or.jsu.classroom.student.service.ClassroomStudentService;
import kr.or.jsu.mybatis.mapper.LctExamMapper;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassStuExamServiceImpl implements ClassStuExamService {
	
	private final LctExamMapper examMapper;
	
	private final ClassroomStudentService classRootService;
	
	
	/**
	 * 특정 강의에 대한 시험 목록 조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 목록을 반환합니다. <br>
	 * 해당 시험에 대해 자신의 응시 기록이 있는지 확인하여 추가 기록합니다.
	 * 
	 * @param user
	 * @param lectureId
	 * @return
	 */
	@Override
	public List<LctExamLabelResp_STU> readExamList(
		UsersVO user
		, String lectureId
	) {
		// 1. 관련 있는 학생인지 확인하고, 수강정보 가져오기
		StuEnrollLctInfo enroll = classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 모든 시험 + 제출 여부 목록 가져오기.
		List<LctExamInfo> searchList = examMapper.selectExamList_STU(lectureId, enroll.getEnrollId());
		
		// 3. 응답용 객체에 넣어서 반환
		return searchList.stream().map(info -> {
			LctExamLabelResp_STU resp = new LctExamLabelResp_STU();
			BeanUtils.copyProperties(info, resp);
			
			resp.setSubmitted(info.getIsSubmitted() == 1);
			
			return resp;
		}).collect(Collectors.toList());		
	}
	
	/**
	 * 특정 강의의 특정 시험 상세조회 요청을 받아, <br>
	 * 열람 권한을 확인한 뒤 조회된 정보를 반환합니다. <br>
	 * (교수가 출제한 시험에 대한 내용 + 자신의 응시 기록)
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return
	 */
	@Override
	public ExamAndEachStudentsSubmitResp readExam(
		UsersVO user
		, String lectureId
		, String lctExamId
	) {
		// 0. 관련 있는 학생인지 확인하고, 수강정보 가져오기
		StuEnrollLctInfo enroll = classRootService.checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 1. 시험 단건 조회
		LctExamInfo exam = examMapper.selectExam(lctExamId);
		if(exam == null) 
			throw new RuntimeException("존재하지 않는 시험ID입니다.");
		if(!lectureId.equals(exam.getLectureId()))
			throw new RuntimeException("해당 강의에서 출제된 시험이 아닙니다.");
		
		// 2. 내 수강ID 메서드에 맞게 넣기
		List<String> enrollIds = List.of(enroll.getEnrollId());
		
		// 3. 응시 기록 조회
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

	@Override
	public void readNotSubmittedExamList() {
		// TODO Auto-generated method stub

	}

}
