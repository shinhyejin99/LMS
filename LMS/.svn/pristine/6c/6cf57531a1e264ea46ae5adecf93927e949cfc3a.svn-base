package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class UsersMapperTest {

	@Autowired
	UsersMapper mapper;

	@Test
	void testSelectOne() {
		String userId = "USER00000000016";
		UsersVO user = mapper.selectUser(userId);
		assertNotNull(user);
		
		log.info("사용자 : {}", user);
	}
	
	@Test
	void testSelectList() {
		List<UsersVO> userList = mapper.selectUserList();
		assertTrue(userList.size() != 0);
		
		log.info("사용자 수 :  {}", userList.size());
	}	

}
