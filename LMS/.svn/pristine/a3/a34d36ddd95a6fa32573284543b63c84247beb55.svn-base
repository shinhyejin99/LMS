package kr.or.jsu.lms.professor.service.lecture.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.lms.professor.service.lecture.ProfessorLectureApplyService;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ProfessorLectureApplyServiceImplTest {
	
	@Autowired
	ProfessorLectureApplyService service;
	
	@Test
	void 모든활성화된과목단과대와과목별로가져오기() {
		var result = service.readAllSubject();
		
		log.info("result : {}", PrettyPrint.pretty(result));
		
		log.info("과목 있는 단과대 수 : {}", result.size());
		
		result.forEach(res -> log.info("단과대명 : {}, 과목 있는 학과 수 : {}", res.getCollegeName(), res.getDeptList().size()));
		
		var subjectCnt = 0;
		
		for(var college : result) {
			for(var dept : college.getDeptList()) {
				subjectCnt += dept.getSubjectList().size();
			}
		}
		
		
		log.info("총 과목수 : {}", subjectCnt);
	}
	
	@Test
	void 강의신청상세내용() {
		UsersVO user = new UsersVO();
		user.setUserNo("USER00000000008"); // 이 사용자가
		String lctApplyId = "LCTAPLY00000085"; // 이 신청에 대한 내용을 열람하려고 함.
		
		var result = service.readLectureApplyDetail(user, lctApplyId);
		log.info("result : {}", PrettyPrint.pretty(result));
	}
}
