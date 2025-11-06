package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SpringBootTest
@Slf4j
class UnivAffilHistoryMapperTest2 {

    @Autowired
    UnivAffilHistoryMapper mapper;

    @Test
    void testSelectAllUnivAffilHistories() {
        assertDoesNotThrow(() -> mapper.selectAllUnivAffilHistories());
    }
}
