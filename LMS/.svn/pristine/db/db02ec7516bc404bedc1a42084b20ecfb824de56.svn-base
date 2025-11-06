package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.dto.FileBundleDTO;
import kr.or.jsu.vo.FileDetailVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class FilesMapperTest {
	
	@Autowired
	FilesMapper fMapper;
	
	@Test
	void selectFileDetailTest() {
		FileDetailVO oneFile = fMapper.selectFileDetail("FILE00000000016", 1);
		assertNotNull(oneFile);
		
		log.info("가져온 파일 하나의 메타데이터 : {}", PrettyPrint.pretty(oneFile));
	}
	
	@Test
	void selectFileBundleTest() {
		FileBundleDTO fileBundle = fMapper.selectFileBundle("FILE00000000016");
		assertNotNull(fileBundle);
		
		log.info("가져온 파일 묶음의 메타데이터 : {}", PrettyPrint.pretty(fileBundle));
	}
	

}
