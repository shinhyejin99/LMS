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
class CertificateDetailMapperTest {

	@Autowired
	CertificateDetailMapper mapper;

//	@Test
	void testInsertCertificateDetail() {
		fail("Not yet implemented");
	}

	@Test
	void testSelectCertificateDetailList() {
//		assertNotNull(mapper.selectCertificateDetailList());
		assertDoesNotThrow(() -> mapper.selectCertificateDetailList());
	}

//	@Test
	void testUpdateCertificateDetail() {
		fail("Not yet implemented");
	}

//	@Test
	void testDeleteCertificateDetail() {
		fail("Not yet implemented");
	}

}
