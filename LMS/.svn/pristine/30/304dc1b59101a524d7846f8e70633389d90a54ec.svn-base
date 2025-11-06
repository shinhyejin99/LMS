package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class AddressMapperTest {
	
	@Autowired
	AddressMapper mapper;

	AddressVO address;
	
	@BeforeEach
//	@Test
	void testInsertAddress() {
		address = new AddressVO("ADDR00000000066", "대전광역시 중구 중앙로", "오류동", "45678", 36.0, 127.0, "Y");
		assertEquals(1, mapper.insertAddress(address));
	}

	@Test
	void testSelectAddressList() {
		
	}

//	@Test
	void testUpdateAddress() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteAddress() {
		fail("Not yet implemented");
	}

}
