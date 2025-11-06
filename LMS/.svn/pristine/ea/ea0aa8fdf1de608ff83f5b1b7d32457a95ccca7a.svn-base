package kr.or.jsu.dummyDataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class A_AddressDummyGenerator {
	
	@Autowired
	DummyDataMapper ddMapper;
	
	long num = 80000000000L;
	
	String idGenerator() {
        // "ADDR" + 11자리 zero-padding
        String id = "ADDR" + String.format("%011d", num);
        num++;
        return id;
    }
	
	String baseAddrGenerator() {
        // 시: 대전 고정, 구/동/로 랜덤 조합
        String[] gus = {"동구", "중구", "서구", "대덕구", "유성구"};
        String[] roads = {"한밭대로", "계룡로", "둔산로", "가장로", "테크노중앙로", "도안대로", "문화로", "대학로"};
        String[] dongs = {"둔산동", "봉명동", "어은동", "관평동", "월평동", "탄방동", "가양동", "가수원동"};

        ThreadLocalRandom r = ThreadLocalRandom.current();
        String gu = gus[r.nextInt(gus.length)];
        String road = roads[r.nextInt(roads.length)];
        String dong = dongs[r.nextInt(dongs.length)];
        int bunji = r.nextInt(1, 501); // 1~500

        // 예: 대전 유성구 대학로 123 (봉명동)
        return "대전 " + gu + " " + road + " " + bunji + " (" + dong + ")";
    }
	
	String detailAddrGenerator() {
        String[] details = {
            "101동 1001호", "102동 203호", "201동 1203호",
            "상가 2층 205호", "○○오피스텔 7층 705호",
            "A동 3층", "B동 5층", "지하 1층 물류실", "본관 4층"
        };
        return details[ThreadLocalRandom.current().nextInt(details.length)];
    }
	
	String zipCodeGenerator() {
        // 00000 ~ 99999 (문자열 5자리)
        int n = ThreadLocalRandom.current().nextInt(0, 100000);
        return String.format("%05d", n);
    }
	
	@Test
	@Transactional
	void insertOneAddress() {
		AddressVO oneDummy = new AddressVO("ADDR89999999999", "가양동", "어딘가", "12345", null, null, "Y");
		ddMapper.insertOneDummyAddress(oneDummy);
	}
	
	@Test
	void insertAddressDummy() {
		List<AddressVO> dummyList = new ArrayList<>();
		
		int it = 100;
		
		for(int i=0; i<it; i++) {
			AddressVO dummy = new AddressVO();
			dummy.setAddrId(idGenerator());
			dummy.setBaseAddr(baseAddrGenerator());
            dummy.setDetailAddr(detailAddrGenerator());
            dummy.setZipCode(zipCodeGenerator());
            dummy.setLatitude(null);
            dummy.setLongitude(null);
            dummy.setUsingYn("Y");
            dummyList.add(dummy);
		}
		
		int row = ddMapper.insertDummyAddress(dummyList);
		
		log.info("넣은 더미주소 수 : {}", row);
	}

}
