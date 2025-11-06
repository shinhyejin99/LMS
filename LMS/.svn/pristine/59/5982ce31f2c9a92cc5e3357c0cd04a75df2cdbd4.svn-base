package kr.or.jsu.classroom.professor.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.db.StudentAndEnrollDTO;
import kr.or.jsu.classroom.dto.db.studentManage.GrouptaskAndStuSubmitDTO;
import kr.or.jsu.classroom.dto.db.studentManage.IndivtaskAndStuSubmitDTO;
import kr.or.jsu.classroom.dto.db.studentManage.LctAttRoundAndStuAttendacneDTO;
import kr.or.jsu.classroom.dto.db.studentManage.LctExamAndStuSubmitDTO;
import kr.or.jsu.classroom.dto.info.IndivtaskSubmitInfo;
import kr.or.jsu.classroom.dto.info.LctIndivtaskInfo;
import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.classroom.dto.response.student.LectureEnrollingStudentResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllAttendanceResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllExamSubmitResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllGrouptaskSubmitResp;
import kr.or.jsu.classroom.dto.response.student.StudentsAllIndivtaskSubmitResp;
import kr.or.jsu.classroom.professor.service.ClassPrfStuManageService;
import kr.or.jsu.core.dto.info.StudentInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.mybatis.mapper.LectureMapper;
import kr.or.jsu.mybatis.mapper.classroom.ClassroomStudentManagementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassPrfStuManageServiceImpl implements ClassPrfStuManageService {

	private final LectureMapper lectureMapper;
	private final ClassroomStudentManagementMapper stuManageMapper;

	private final ClassroomCommonService classCommonService;
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
	 * 1. 요청한 클라이언트 교수가 요청한 강의를 담당하는지 확인 <br>
	 * 2. 요청한 학생이 해당 강의를 수강중인지 확인 <br>
	 * 전부 통과 시 학생의 수강ID 반환
	 *
	 * @param targetStuNo
	 * @param lectureId
	 * @throws RuntimeException
	 */
	private String relevantCheck(
		String targetStuNo
		, String lectureId
	) throws RuntimeException {
		// 1. 이 강의가 네 강의가 맞느냐
		String prfNo = getUser().getRealUser().getUserNo();
		boolean isRelavant = classCommonService.isRelevantClassroom(prfNo, lectureId);
		if(!isRelavant) throw new RuntimeException("담당 교수만 사용 가능한 기능");

		// 2. 이 학생이 이 강의를 수강중이냐
		StuEnrollLctInfo enrollInfo = classCommonService.getEnrollInfo(lectureId, targetStuNo);
		if(enrollInfo == null) throw new RuntimeException("해당 학생이 수강중이 아님");
		return enrollInfo.getEnrollId();
	}

	@Override
	public List<LectureEnrollingStudentResp> readStudentList(String lectureId) {
		List<StudentAndEnrollDTO> stuList = lectureMapper.selectLectureWithStudent(lectureId, null);

		// 캐시로 학년, 재학상태, 소속학과 이름을 채워넣음
		List<LectureEnrollingStudentResp> result =
			stuList.stream().map(s -> {
				LectureEnrollingStudentResp oneResp = new LectureEnrollingStudentResp();

				StudentInfo studentInfo = s.getStudentInfo();
				StuEnrollLctInfo enrollInfo = s.getStuEnrollLctInfo();

				BeanUtils.copyProperties(studentInfo, oneResp);
				BeanUtils.copyProperties(enrollInfo, oneResp);

				String univDeptName = cache.getUnivDeptName(studentInfo.getUnivDeptCd());
				String gradeName = cache.getCodeName(studentInfo.getGradeCd());
				String stuStatusName = cache.getCodeName(studentInfo.getStuStatusCd());
				String EnrollStatusName = cache.getCodeName(oneResp.getEnrollStatusCd());

				oneResp.setUnivDeptName(univDeptName);
				oneResp.setGradeName(gradeName);
				oneResp.setStuStatusName(stuStatusName);
				oneResp.setEnrollStatusName(EnrollStatusName);

				return oneResp;
			}).collect(Collectors.toList());

		return result;
	}

	/**
	 * 특정 강의에 대해 생성된 모든 출석회차와, <br>
	 * 그 출석회차에 대한 특정 학생의 출석 기록을 가져옵니다.
	 *
	 * @param lectureId 강의ID
	 * @param studentNo 학번
	 */
	@Override
	public List<StudentsAllAttendanceResp> readStudentAttendanceList(
		String lectureId
		, String studentNo
	) {
		// 1. 요청한 교수와 찾는 학생이 각각 강의와 관련있는지 검사
		// 하는김에 수강ID도 가져오기
		String enrollId = relevantCheck(studentNo, lectureId);

		// 2. 강의에서 생성된 모든 출석회차와 그에 대한 학생의 출결기록 가져오기
		List<LctAttRoundAndStuAttendacneDTO> resultList = stuManageMapper.selectStuAttendance(lectureId, enrollId);

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
	
	/**
	 * 특정 강의에서 출제된 진행중인+마감된 개인과제와 <br>
	 * 그 개인과제에 대한 특정 학생의 제출 기록을 가져옵니다.
	 * 
	 * @param lectureId
	 * @param studentNo
	 * @return 위에서 설명했음
	 */
	public List<StudentsAllIndivtaskSubmitResp> readStudentIndivtaskSubmitList(
		String lectureId
		, String studentNo
	) {
		// 1. 요청한 교수와 찾는 학생이 각각 강의와 관련있는지 검사
		// 하는김에 수강ID도 가져오기
		String enrollId = relevantCheck(studentNo, lectureId);
		
		List<IndivtaskAndStuSubmitDTO> resultList = 
			stuManageMapper.selectIndivtaskWithSubmitByEnrollId(lectureId, enrollId);
		
		return resultList.stream().map(result -> {
			StudentsAllIndivtaskSubmitResp resp = new StudentsAllIndivtaskSubmitResp();
			
			LctIndivtaskInfo indivtask = result.getIndivtaskInfo();
			IndivtaskSubmitInfo submit = result.getSubmitInfo();
			
			// 과제내용 옮겨담고
			BeanUtils.copyProperties(indivtask, resp);
			// 파일첨부되어있으면 그렇다고만 넣고
			resp.setTaskFileAttached(indivtask.getAttachFileId() != null);
			
			// 제출 대상인지 확인
			boolean isSubmitTarget = (submit != null);
			resp.setSubmitTarget(isSubmitTarget);
			
			// 제출 대상이라 제출이 이미 생성되어있으면
			if(isSubmitTarget) {
				BeanUtils.copyProperties(submit, resp);
				resp.setSubmitFileAttached(submit.getSubmitFileId() != null);
			}
			
			return resp;
		}).toList();
	}
	
	/**
	 * 특정 강의에서 출제된 진행중인+마감된 조별과제와 <br>
	 * 그 조별과제에 대한 특정 학생의 제출 기록을 가져옵니다.
	 * 
	 * @param lectureId
	 * @param studentNo
	 * @return 위에서 설명했음
	 */
	@Override
	public List<StudentsAllGrouptaskSubmitResp> readStudentGrouptaskSubmitList(
		String lectureId
		, String studentNo
	) {
		// 1. 요청한 교수와 찾는 학생이 각각 강의와 관련있는지 검사
		// 하는김에 수강ID도 가져오기
		String enrollId = relevantCheck(studentNo, lectureId);
		
		List<GrouptaskAndStuSubmitDTO> resultList = 
				stuManageMapper.selectGrouptaskWithSubmitByEnrollId(lectureId, enrollId);
		
		return resultList.stream().map(result -> {
			StudentsAllGrouptaskSubmitResp resp = new StudentsAllGrouptaskSubmitResp();
			
			BeanUtils.copyProperties(result, resp);
			resp.setTaskFileAttached(result.getAttachFileId() != null);
			resp.setSubmitTarget(result.getGroupId() != null);
			resp.setSubmitFileAttached(result.getSubmitFileId() != null);
			
			return resp;
		}).toList();
	}
	
	/**
	 * 특정 강의에 대해 출제된 모든 완료되고 삭제되지 않은 시험 목록과, <br>
	 * 그 시험에 대한 특정 학생의 응시 기록을 가져옵니다.
	 *
	 * @param lectureId 강의ID
	 * @param studentNo 학번
	 * @return 위에서 설명했음
	 */
	@Override
	public List<StudentsAllExamSubmitResp> readStudentExamSubmitList(
		String lectureId
		, String studentNo
	) {
		// 1. 요청한 교수와 찾는 학생이 각각 강의와 관련있는지 검사
		// 하는김에 수강ID도 가져오기
		String enrollId = relevantCheck(studentNo, lectureId);

		// 2. 강의에서 출제된, 완료되고 삭제되지 않은 모든 시험과 그에 대한 학생의 응시기록 얻기
		List<LctExamAndStuSubmitDTO> resultList = stuManageMapper.selectExamWithSubmitByStudent(lectureId, enrollId);

		// 3. 옮겨담고 내보내기
		return resultList.stream().map(result -> {
			StudentsAllExamSubmitResp resp = new StudentsAllExamSubmitResp();
			BeanUtils.copyProperties(result.getExamInfo(), resp);
			if(result.getSubmitInfo() != null) {
				BeanUtils.copyProperties(result.getSubmitInfo(), resp);
				resp.setRecord(true);
			}

			return resp;
		}).collect(Collectors.toList());
	}



}
