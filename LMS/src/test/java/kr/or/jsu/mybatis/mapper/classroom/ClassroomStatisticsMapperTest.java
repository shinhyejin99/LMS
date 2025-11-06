package kr.or.jsu.mybatis.mapper.classroom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.response.statistics.AttendanceStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.EnrollStuStatisticsResp;
import kr.or.jsu.classroom.dto.response.statistics.TaskStatisticsResp;
import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
@SpringBootTest
class ClassroomStatisticsMapperTest {
	
	@Autowired
	ClassroomStatisticsMapper mapper;
	
	@Test
	void 수강생통계데이터() {
		String lectureId1 = "LECT00000000001";
		String lectureId2 = "LECT90000000220";
		
		EnrollStuStatisticsResp resp1 = new EnrollStuStatisticsResp();
		
		resp1.setStats(mapper.getEnrollStat(lectureId1));
		resp1.setByGrade(mapper.getGradeStat(lectureId1));
		resp1.setByDept(mapper.getDeptStat(lectureId1));
		
		log.info("강의1 : {}", PrettyPrint.pretty(resp1));
		
		EnrollStuStatisticsResp resp2 = new EnrollStuStatisticsResp();
		
		resp2.setStats(mapper.getEnrollStat(lectureId2));
		resp2.setByGrade(mapper.getGradeStat(lectureId2));
		resp2.setByDept(mapper.getDeptStat(lectureId2));
		
		log.info("강의2 : {}", PrettyPrint.pretty(resp2));
	}
	
	@Test
	void 전통계데이터강도에요출석관련통계데이터를주세요() {
		String lectureId1 = "LECT00000000001";
		
		AttendanceStatisticsResp resp1 = new AttendanceStatisticsResp();
		
		resp1.setTypeCount(mapper.getTotalAttData(lectureId1));
		resp1.setAttCountList(mapper.getEachStuAttData(lectureId1));
		
		log.info("강의1 : {}", PrettyPrint.pretty(resp1));
	}
	
	@Test
	void 전통계데이터강도에요과제통계를주세요() {
		String lectureId = "LECT00000000001";
		
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

        log.info("강의1 : {}", PrettyPrint.pretty(resp));
	}
	
}
