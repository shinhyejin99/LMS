package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.db.exam.StudentAndExamSubmitDTO;
import kr.or.jsu.classroom.dto.info.LctExamInfo;
import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class LctExamMapperTest {

	@Autowired
	LctExamMapper mapper;

	@Test
	void 시험목록조회해보기() {
		List<LctExamInfo> list = mapper.selectExamList_PRF("LECT00000000001");
		log.info("시험 목록 : {}", PrettyPrint.pretty(list));
	}
	
	@Test
	void 수강생ID와시험ID주면서응시기록가져와보기() {
		String lctExamId = "LCTEXM000000004";
		List<String> enrollIds = List.of("STENRL000000001", "STENRL000000002", "STENRL000000041");
		
		List<StudentAndExamSubmitDTO> result = mapper.selectSubmitList(lctExamId, enrollIds);
		
		log.info("요청한 수강생과 그의 특정시험에 대한 응시기록 : {}", PrettyPrint.pretty(result));
	}

}
