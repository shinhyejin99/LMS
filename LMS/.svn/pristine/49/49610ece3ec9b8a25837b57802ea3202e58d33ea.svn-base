package kr.or.jsu.classroom.professor.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.db.EnrollSimpleDTO;
import kr.or.jsu.classroom.dto.info.LctAttRoundInfo;
import kr.or.jsu.classroom.dto.info.LctStuAttInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.request.AttendanceRecordReq;
import kr.or.jsu.classroom.dto.response.attandance.LctAttRoundLabelResp;
import kr.or.jsu.classroom.dto.response.attandance.StudentAttLabelResp;
import kr.or.jsu.classroom.dto.response.attandance.StudentForAttendanceResp;
import kr.or.jsu.classroom.professor.service.ClassPrfAttendanceService;
import kr.or.jsu.core.dto.info.StudentInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.mybatis.mapper.LctAttendanceMapper;
import kr.or.jsu.mybatis.mapper.StuEnrollLctMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassPrfAttendanceServiceImpl implements ClassPrfAttendanceService {
	
	private final LctAttendanceMapper attendanceMapper;
	private final StuEnrollLctMapper enrollMapper;
	private final DatabaseCache cache;
	
	@Override
	public List<LctAttRoundLabelResp> readAttRoundLabel(
		String lectureId
	) {
		return attendanceMapper.selectAttRoundLabelList(lectureId);
	}
	
	@Transactional
	@Override
	public int createManualAttRound(
		String lectureId
		, String defaultStatus
	) {
		// 1. 공용(수동, QR) 출석회차 생성 메서드로 출석회차 생성 
		LctAttRoundInfo newAttRound = new LctAttRoundInfo();
		newAttRound.setLectureId(lectureId);
		attendanceMapper.insertLctAttround(newAttRound);
		
		// 1-1. 셀렉트키로 생성한 회차 확보
		int newRound = newAttRound.getLctRound();
		
		// 1-2. "TBD", "OK", "NO"로 들어온 코드 공통코드화
		defaultStatus = "ATTD_" + defaultStatus;
		
		// 2. 요청한 상태로 모든 학생의 출석 생성
		
		// 2-1. 강의 수강 중인 모든 학생 데이터 가져오기
		List<StuEnrollLctInfo> enrollInfoList = 
			enrollMapper.selectAllStudentList(lectureId, true);
		
		// 2-2. 학생마다 수강번호만 따서 리스트화
		List<String> enrollIds = 
			enrollInfoList.stream().map(stu -> {
				return stu.getEnrollId();
			}).collect(Collectors.toList());
		
		// 2-3. 출석 데이터 넣기
		attendanceMapper.insertDefaultAttendanceRecords(
			lectureId
			, newRound
			, enrollIds
			, defaultStatus
		);
		
		return newRound;
	}
	
	@Override
	public List<StudentForAttendanceResp> getStudentListForAtt(
		String lectureId
		, int lctRound
	) {
		// 3. 수동 출석체크에 필요한 데이터 생성
		// 3-1. 수강ID와 학생 정보 가져오고
		List<EnrollSimpleDTO> studentList = 
			enrollMapper.selectAttendanceRecords(lectureId, lctRound);
		
		// 3-2. 학생들의 그 주차에 대한 출석상태 가져오고
		List<LctStuAttInfo> attRecords =
			attendanceMapper.selectAttendanceRecordList(lectureId, lctRound);
		
		Map<String, LctStuAttInfo> attRecordMap = 
				attRecords.stream().collect(
					Collectors.toMap(LctStuAttInfo::getEnrollId, att -> att)
				);
		
		// 3-3. 필요한 정보만 응답용 객체에 옮겨담기
		List<StudentForAttendanceResp> respList = studentList.stream().map(stu -> {
			StudentForAttendanceResp resp = new StudentForAttendanceResp();
			
			StuEnrollLctInfo enrollInfo = stu.getStuEnrollLctInfo();
			StudentInfo basicInfo = stu.getStudentInfo();
			
			String enrollId = enrollInfo.getEnrollId();
			
			resp.setEnrollId(enrollId);
			BeanUtils.copyProperties(basicInfo, resp);
			resp.setGrade(cache.getCodeName(basicInfo.getGradeCd()));
			resp.setUnivDeptName(cache.getUnivDeptName(basicInfo.getUnivDeptCd())); 
			
			LctStuAttInfo attRecord = attRecordMap.get(enrollId);
			resp.setAttAt(attRecord.getAttAt());
			resp.setAttStatusCd(attRecord.getAttStatusCd());
			resp.setAttComment(attRecord.getAttComment());
			
			return resp;
		}).toList();
		
		return respList;
	}
	

	@Override
	@Transactional
	public void removeAttRound(String lectureId, int lctRound) {
		attendanceMapper.deleteLctAttRecord(lectureId, lctRound);
		attendanceMapper.deleteLctAttRound(lectureId, lctRound);
	}

	/**
	 * 특정 강의 출석회차에 대해 학생들의 세부 출석 변경사항을 기록합니다.
	 * 
	 * @param lectureId
	 * @param attRound
	 * @param items
	 */
	@Override
	public void modifyAttendanceRecord(
		String lectureId
		, int attRound
		, List<AttendanceRecordReq> items
	) {
		attendanceMapper.updateAttendanceRecordList(lectureId, attRound, items);
	}

	/**
	 * 특정 강의에 대한 수강생들의 출석 요약을 가져옵니다.
	 * 
	 * @param lectureId 강의ID
	 * @return
	 */
	@Override
	public List<StudentAttLabelResp> getStudentAttendanceSummary(
		String lectureId
	) {
		return attendanceMapper.selectStudentAttendanceLabel(lectureId);
	}
}
