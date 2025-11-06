package kr.or.jsu.classroom.student.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.db.EnrollSimpleDTO;
import kr.or.jsu.classroom.dto.db.LectureSimpleDTO;
import kr.or.jsu.classroom.dto.db.LectureWithScheduleDTO;
import kr.or.jsu.classroom.dto.db.StudentAndEnrollDTO;
import kr.or.jsu.classroom.dto.db.studentManage.LctAttRoundAndStuAttendacneDTO;
import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.classroom.dto.response.classroom.StudentMyEnrollInfoResp;
import kr.or.jsu.classroom.dto.response.classroom.StudentMyInfoResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureLabelResp;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.classroom.dto.response.student.ClassmateResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllAttendanceResp;
import kr.or.jsu.classroom.student.service.ClassroomStudentService;
import kr.or.jsu.core.dto.info.StudentInfo;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.mybatis.mapper.LectureMapper;
import kr.or.jsu.mybatis.mapper.StuEnrollLctMapper;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import kr.or.jsu.mybatis.mapper.classroom.ClassroomStudentManagementMapper;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 학생과 관련된 강의에 대한 정보를 가공하여 컨트롤러에 전달합니다.
 * 
 * @author 송태호
 * @since 2025. 9. 30.
 */
@Service
@RequiredArgsConstructor
public class ClassroomStudentServiceImpl implements ClassroomStudentService {
	
	private final StudentMapper studentMapper;
	private final LectureMapper lectureMapper;
	private final StuEnrollLctMapper enrollMapper;
	private final ClassroomStudentManagementMapper stuManageMapper;
	
	private final ClassroomCommonService commonService;
	private final DatabaseCache cache;
	
	/**
	 * 내 개인정보 일부를 가져옵니다.
	 * 
	 * @param user 접속한 사용자
	 * @return
	 */
	@Override
	public StudentMyInfoResp getMyInfo(
		UsersVO user
	) {
		StudentDetailDTO result = studentMapper.selectStudentDetailInfo(user.getUserNo());
		StudentMyInfoResp resp = new StudentMyInfoResp();
		BeanUtils.copyProperties(result, resp);
		
		return resp;
	}
	
	/**
	 * 학생이 수강한 모든 강의를 가져옵니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @return 학생이 수강한 (폐강되지 않은) 모든 강의
	 */
	@Override
	public List<LectureLabelResp> readMyLectureList(
		UsersVO user
	) {
		String studentNo = user.getUserNo();
		
		List<LectureSimpleDTO> resultList = lectureMapper.selectEnrolledLectureList(studentNo);
		
		// 검색한 거 없으면 null 반환
		// TODO 대신 예외처리
		if(resultList==null || resultList.isEmpty()) return null;
		
		// 검색된 게 있으면, 응답용 DTO로 변환.
		List<LectureLabelResp> result = resultList.stream().map(dto -> {
			LectureLabelResp resp = new LectureLabelResp();
			
			LectureInfo li = dto.getLectureInfo();
		    SubjectInfo si = dto.getSubjectInfo();
			
		    resp.setLectureId(li.getLectureId());
		    resp.setSubjectName(si.getSubjectName());
		    resp.setProfessorNo(li.getProfessorNo());
		    resp.setProfessorName(cache.getUserName(li.getProfessorNo()));
		    resp.setUnivDeptName(cache.getUnivDeptName(si.getUnivDeptCd()));
		    resp.setCompletionName(cache.getCodeName(si.getCompletionCd()));
		    resp.setYeartermCd(li.getYeartermCd());
		    resp.setSubjectTypeName(cache.getCodeName(si.getSubjectTypeCd()));
		    resp.setCredit(si.getCredit());
		    resp.setHour(si.getHour());
		    resp.setCurrentCap(dto.getCurrentCap());
		    resp.setMaxCap(li.getMaxCap());
		    
		    if(cache.getCurrentYearterm().equals(resp.getYeartermCd()))
		    	resp.setStarted(true);
		    
		    if(dto.getLectureInfo().getEndAt() != null) {
		    	resp.setStarted(true);
		    	resp.setEnded(true);
		    	resp.setEndAt(dto.getLectureInfo().getEndAt());
		    }
		    
		    return resp;
		}).collect(Collectors.toList());
		
		// 아이디만 빼기
		List<String> ids = result.stream().map(r -> r.getLectureId()).collect(Collectors.toList());
		
		// 뺀 아이디로 스케줄 가져오기
		List<LectureWithScheduleDTO> schedules = lectureMapper.selectScheduleListJson(ids);
		
		// 스케줄을 넣기 편하게 맵으로 변경
		Map<String, String> scheduleById = 
				schedules.stream()
			    .collect(Collectors.toMap(
			        LectureWithScheduleDTO::getLectureId,
			        LectureWithScheduleDTO::getScheduleJson
			    ));
		
		result.forEach(lct -> {
			String eachSchedule = scheduleById.get(lct.getLectureId());
			lct.setScheduleJson(eachSchedule);
		});
		
		return result;
	}
	
	/**
	 * 특정 학년도학기의 사용자 학생에 대한 강의 시간표를 반환합니다.
	 * 
	 * @param yeartermCd 학년도학기코드
	 * @return
	 */
	public List<LectureScheduleResp> readMySchedule(
		UsersVO user
		, String yeartermCd
	) {
		if(yeartermCd == null) yeartermCd = cache.getCurrentYearterm();
		String stuNo = user.getUserNo();
		
		return lectureMapper.selectStuLectureSchedule(yeartermCd, stuNo);
	}
	
	/**
	 * 특정 강의에서 사용되는 내 수강정보를 가져옵니다. <br>
	 * 해당 강의를 수강한 기록이 없는 경우 예외가 발생합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @return 해당 강의에 대한 자신의 수강정보
	 * @throws 수강한 적 없는 강의에 대한 수강정보를 요청한 경우
	 */
	@Override
	public StuEnrollLctInfo checkRelevantAndGetMyEnrollInfo(
		UsersVO user
		, String lectureId
	) throws RuntimeException {
		String stuNo = user.getUserNo();
		StuEnrollLctInfo enroll = commonService.getEnrollInfo(lectureId, stuNo);
		if(enroll == null) throw new RuntimeException("수강한 강의에만 접근할 수 있습니다.");
		return enroll;
	};
	
	/**
	 * 내 개인정보 + 특정 강의에서 사용되는 내 수강정보를 가져옵니다. <br>
	 * 해당 강의를 수강한 기록이 없는 경우 예외가 발생합니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 강의ID
	 * @return 개인정보 + 해당 강의에 대한 자신의 수강정보
	 * @throws 수강한 적 없는 강의에 대한 수강정보를 요청한 경우
	 */
	public StudentMyEnrollInfoResp getMyInfoAndEnrollment(
		UsersVO user
		, String lectureId
	) throws RuntimeException {
		String stuNo = user.getUserNo();
		EnrollSimpleDTO enroll = enrollMapper.selectEnrollingStudent(lectureId, stuNo);
		if(enroll == null) throw new RuntimeException("수강한 강의에만 접근할 수 있습니다.");
		
		StudentMyEnrollInfoResp resp = new StudentMyEnrollInfoResp();
		BeanUtils.copyProperties(enroll.getStudentInfo(), resp);
		BeanUtils.copyProperties(enroll.getStuEnrollLctInfo(), resp);
		
		resp.setUnivDeptName(cache.getUnivDeptName(resp.getUnivDeptCd()));
		resp.setGradeName(cache.getCodeName(resp.getGradeCd()));
		resp.setStuStatusName(cache.getCodeName(resp.getStuStatusCd()));
		resp.setEnrollStatusName(cache.getCodeName(resp.getEnrollStatusCd()));
		
		return resp;
	}
	
	/**
	 * 동료 수강생 정보를 가져옵니다.
	 * 
	 * @param user CustomUserDetails에서 꺼낸 UsersVO
	 * @param lectureId 대상 강의
	 */
	@Override
	public List<ClassmateResp> readClassmates(
		UsersVO user
		, String lectureId
	) {
		// 1. 수강한 강의인지 확인
		checkRelevantAndGetMyEnrollInfo(user, lectureId); 
		
		// 2. 같은 강의 수강하는 학생들 목록 가져오기
		List<StudentAndEnrollDTO> stuList =
			lectureMapper.selectLectureWithStudent(lectureId, true);
		
		// 3. 캐시로 학년, 재학상태, 소속학과 이름을 채워넣으며 응답용 객체로 변환
		List<ClassmateResp> result =		
			stuList.stream().map(s -> {
				ClassmateResp oneResp = new ClassmateResp();
				
				StudentInfo studentInfo = s.getStudentInfo();
				StuEnrollLctInfo enrollInfo = s.getStuEnrollLctInfo();
				
				BeanUtils.copyProperties(studentInfo, oneResp);
				BeanUtils.copyProperties(enrollInfo, oneResp);
				
				String univDeptName = cache.getUnivDeptName(studentInfo.getUnivDeptCd());
				String gradeName = cache.getCodeName(studentInfo.getGradeCd());
				
				oneResp.setUnivDeptName(univDeptName);
				oneResp.setGradeName(gradeName);
				
				return oneResp;
			}).collect(Collectors.toList());
		
		return result;
	}

	/**
	 * 강의의 모든 출석회차와 그에 대한 자신의 출석정보를 가져옵니다.
	 * 
	 * @param user
	 * @param lectureId
	 * @return
	 */
	@Override
	public List<StudentsAllAttendanceResp> readMyAttendacneList(
		UsersVO user
		, String lectureId
	) {
		StuEnrollLctInfo enroll = checkRelevantAndGetMyEnrollInfo(user, lectureId);
		
		// 2. 강의에서 생성된 모든 출석회차와 그에 대한 학생의 출결기록 가져오기
		List<LctAttRoundAndStuAttendacneDTO> resultList = stuManageMapper.selectStuAttendance(lectureId, enroll.getEnrollId());
		
		// 2-1. 옮겨담고 내보내기
		return resultList.stream().map(result -> {
			StudentsAllAttendanceResp resp = new StudentsAllAttendanceResp();
			
			BeanUtils.copyProperties(result, resp);
			BeanUtils.copyProperties(result.getAttRoundInfo(), resp);
			if(result.getStuAttInfo() != null) {// LEFT JOIN이라 학생 출결정보가 없을수도 있음
				BeanUtils.copyProperties(result.getStuAttInfo(), resp);
				resp.setRecord(true);
			}
			
			resp.setAttStatusName(cache.getCodeName(resp.getAttStatusCd()));
			
			return resp;
		}).collect(Collectors.toList());
	}
}