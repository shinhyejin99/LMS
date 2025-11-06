package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class CertificateItemMapperTest {

	@Autowired
	CertificateItemMapper mapper;
	
//	@Test
	void testInsertCertificateItem() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectCertificateItemList() {
//		assertNotNull(mapper.selectCertificateItemList());
		assertDoesNotThrow(() -> mapper.selectCertificateItemList());
	}

//	@Test
	void testUpdateCertificateItem() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteCertificateItem() {
		fail("Not yet implemented");
	}

}
