package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.db.task.NotEvaluatedIndivtaskDTO;
import kr.or.jsu.classroom.dto.info.LctIndivtaskInfo;
import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class LctIndivtaskMapperTest {
	
	@Autowired
	LctIndivtaskMapper mapper;

	@Test
	void 학생입장에서내가제출할강의목록가져와보기() {
		String lectureId = "LECT00000000001";
		String enrollId = "STENRL000000001";
		
		List<LctIndivtaskInfo> result = mapper.selectIndivtaskList_STU(lectureId, enrollId);
		
		
		log.info("개수 : {}, 결과 : {}", result.size(), PrettyPrint.pretty(result));
	}
	
	@Test
	void 교수입장에서_내가채점해야할_과제목록과_제출수_가져와보기() {
		List<NotEvaluatedIndivtaskDTO> list = mapper.notEvaluatedTaskList("20230001");
		
		log.info("개수 : {}, 결과 : {}", list.size(), PrettyPrint.pretty(list));
		
	}
}
