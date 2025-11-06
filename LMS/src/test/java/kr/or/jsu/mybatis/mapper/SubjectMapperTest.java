package kr.or.jsu.mybatis.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.utils.log.PrettyPrint;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class SubjectMapperTest {

    @Autowired
    SubjectMapper mapper;

    @Test
    void 모든학과를가져오되_소속단과대와소속학과까지() {
    	var result = mapper.selectAllSubjectWithCollegeAndDept();
    	
    	log.info("result : {}", PrettyPrint.pretty(result));
    	
//    	result.forEach(res -> log.info("단과대 : {}, 학과 : {}, 과목명 : {}", res.getCollege().getCollegeName(), res.getUnivDept().getUnivDeptName(), res.getSubject().getSubjectName()));
    	
    }
}
