package kr.or.jsu.classroom.professor.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import kr.or.jsu.classroom.common.service.ClassroomCommonService;
import kr.or.jsu.classroom.dto.response.statistics.AttendanceStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.EnrollStuStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.TaskStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.ExamStatisticsResp;
import kr.or.jsu.classroom.professor.service.ClassPrfStatisticsService;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.mybatis.mapper.classroom.ClassroomStatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassPrfStatisticsServiceImpl implements ClassPrfStatisticsService {
	
	private final ClassroomStatisticsMapper mapper;
	
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
	
	private void check(String lectureId) {
		String userNo = getUser().getRealUser().getUserNo();
		boolean result = commonService.isRelevantClassroom(userNo, lectureId);
		if(!result) throw new RuntimeException("강의 담당 교수가 아닙니다.");
	};
	
	@Override
	public EnrollStuStatisticsResp readEnrollmentStat(
		String lectureId
	) {
		check(lectureId);
		
		var resp = new EnrollStuStatisticsResp();
		
		resp.setStats(mapper.getEnrollStat(lectureId));
		resp.setByGrade(mapper.getGradeStat(lectureId));
		resp.setByDept(mapper.getDeptStat(lectureId));
		
		return resp;
	}

	@Override
	public AttendanceStatisticsResp readAttendanceStat(
		String lectureId
	) {
		check(lectureId);
		
		var resp = new AttendanceStatisticsResp();
		
		resp.setTypeCount(mapper.getTotalAttData(lectureId));
		resp.setAttCountList(mapper.getEachStuAttData(lectureId));
		
		return resp;
	}

	@Override
	public TaskStatisticsResp readTaskStat(
		String lectureId
	) {
		check(lectureId);
		
		var resp = new TaskStatisticsResp();

        var typeCount   = mapper.selectTaskTypeCount(lectureId);
        var onIndiv     = mapper.selectOngoingIndivSubmitRate(lectureId);
        var onGroup     = mapper.selectOngoingGroupSubmitRate(lectureId);
        var closedIndiv = mapper.selectClosedIndivSubmitRate(lectureId);
        var closedGroup = mapper.selectClosedGroupSubmitRate(lectureId);
        var scores      = mapper.selectScoresByStudentAndTask(lectureId);

        // resp에 세터 추가해놨다고 가정 (필요시 @Builder/생성자 사용)
        resp.setTaskTypeCount(typeCount);
        resp.setOngoingIndivtask(onIndiv);
        resp.setOngoingGrouptask(onGroup);
        resp.setClosedIndivtask(closedIndiv);
        resp.setClosedGrouptask(closedGroup);
        resp.setScoresByStudentAndTask(scores);
        
        return resp;
	}

	@Override
	public ExamStatisticsResp readExamStat(
		String lectureId
	) {
		check(lectureId);

		var resp = new ExamStatisticsResp();

		var typeCount = mapper.selectExamTypeCount(lectureId);
		var ongoing   = mapper.selectOngoingExamSubmitRate(lectureId);
		var closed    = mapper.selectClosedExamSubmitRate(lectureId);
		var scores    = mapper.selectScoresByStudentAndExam(lectureId);

		resp.setExamTypeCount(typeCount);
		resp.setOngoingExam(ongoing);
		resp.setClosedExam(closed);
		resp.setScoresByStudentAndExam(scores);

		return resp;
	}

}
