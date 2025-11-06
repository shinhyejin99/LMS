package kr.or.jsu.core.utils.databasecache;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.core.dto.info.CollegeInfo;
import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.mybatis.mapper.cache.CacheMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class DatabaseCacheTest {
	
	@Autowired
	private DatabaseCache cc;
	
	@Autowired
	private CacheMapper ccMapper;
	
	@Test
	void testCodeCache() {
		String a = cc.getCodeName("BANK_NH");
		log.info("a : {}", a);
		String b = cc.getUserNo("USER00000000001");
		log.info("b : {}", b);
		String c = cc.getUserName("202500001");
		log.info("c : {}", c);
		
		String d = cc.getUnivDeptName("DEP-ENGI-CSE");
		log.info("d : {}", d);
	}
	
	@Test
	void testCacheMapper() {
		List<CollegeInfo> collegeList = ccMapper.selectAllCollege();
		List<UnivDeptInfo> deptList = ccMapper.selectAllUnivDept();
		
		log.info("우리학교 단과대 목록 : {}", PrettyPrint.pretty(collegeList));
		log.info("우리학교 학과목록 : {}", PrettyPrint.pretty(deptList));
		
	}

}
