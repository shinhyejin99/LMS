package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classroom.dto.db.GrouptaskGroupWithCrewDTO;
import kr.or.jsu.classroom.dto.info.LctGrouptaskInfo;
import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@SpringBootTest
class LctGrouptaskMapperTest {

	@Autowired
	LctGrouptaskMapper mapper;

	@Test
	void 조별과제목록갖고와보기() {
		String lectureId = "LECT00000000001";

		List<LctGrouptaskInfo> list = mapper.selectGrouptaskList_PRF(lectureId);

		log.info("과제들 : {}", PrettyPrint.pretty(list));
	}

	@Test
	void 조별과제단건() {
		String lectureId = "TSKGRUP00000007";

		List<LctGrouptaskInfo> list = mapper.selectGrouptaskList_PRF(lectureId);

		log.info("과제 : {}", PrettyPrint.pretty(list));
	}

	@Test
	void 조별과제_조들_교수의요청() {
		String lectureId = "TSKGRUP00000007";

		List<GrouptaskGroupWithCrewDTO> list = mapper.selectGroupList(lectureId, null);

		list.forEach(group -> log.info("조 : {}, 제출 : {}", PrettyPrint.pretty(group.getGroupInfo()),
				PrettyPrint.pretty(group.getSubmitInfo())));

		list.forEach(group -> log.info("{} 조의 조원 수 : {}", group.getGroupInfo().getGroupName(),
				group.getCrewInfoList().size()));

		log.info("조 개수 : {}", list.size());
	}

	@Test
	void 조별과제_조들_학생의요청() {
		String lectureId = "TSKGRUP00000007";
		String stuNo = "202500001";

		List<GrouptaskGroupWithCrewDTO> list = mapper.selectGroupList(lectureId, stuNo);

		list.forEach(group -> log.info("조 : {}, 제출 : {}", PrettyPrint.pretty(group.getGroupInfo()),
				PrettyPrint.pretty(group.getSubmitInfo())));

		list.forEach(group -> log.info("{} 조의 조원 수 : {}", group.getGroupInfo().getGroupName(),
				group.getCrewInfoList().size()));

		log.info("조 개수 : {}", list.size());
	}
	
	@Test
	void 내조제출내역가져오기() {
		String stuNo1 = "202500040";
		String grouptaskId = "TSKGRUP00000007";
		
		List<GrouptaskGroupWithCrewDTO> result = mapper.selectGroupList(grouptaskId, stuNo1);
		
		log.info("result : {}", PrettyPrint.pretty(result));
		log.info("리스트길이 : {}", result.size());
	}

}
