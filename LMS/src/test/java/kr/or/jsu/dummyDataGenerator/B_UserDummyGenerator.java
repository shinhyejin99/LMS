package kr.or.jsu.dummyDataGenerator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class B_UserDummyGenerator {

	@Autowired
	DummyDataMapper ddMapper;

	long num = 80000000000L;

	String idGenerator() {
        // "USER" + 11자리 zero-padding
        String id = "USER" + String.format("%011d", num);
        num++;
        return id;
    }

	String firstNameGeneratorForStu() {
        // 2글자 이름: 앞글자/뒷글자 풀에서 하나씩 뽑아 결합
        // (간단한 더미 풀, 50개까지는 과하니 20개 내외로 구성)
		final String[] firstPoolFront = {
		    "민","서","지","현","하","도","유","윤","예","태",
		    "수","은","준","영","나","재","소","시","희","아"
		};
        final String[] firstPoolBack = {
            "민","서","윤","진","우","현","빈","주","연","린",
            "훈","영","은","성","혁","아","율","원","빈","후"
        };
        ThreadLocalRandom r = ThreadLocalRandom.current();
        return firstPoolFront[r.nextInt(firstPoolFront.length)]
             + firstPoolBack[r.nextInt(firstPoolBack.length)];
    }

	String firstNameGeneratorForPrf() {
	    final String[] firstPoolFront = {
	        "영","종","경","성","태","현","동","선","병","상",
	        "정","순","미","숙","희","자","남","재","형","광"
	    };
	    final String[] firstPoolBack = {
	        "자","숙","희","미","순","옥","영","호","철","수",
	        "석","준","식","남","재","숙","자","호","민","길"
	    };

	    ThreadLocalRandom r = ThreadLocalRandom.current();
	    return firstPoolFront[r.nextInt(firstPoolFront.length)]
	         + firstPoolBack[r.nextInt(firstPoolBack.length)];
	}

	String lastNameGenerator() {
        // 상위 20개 성씨 풀
        final String[] lastNames = {
            "김","이","박","최","정","강","조","윤","장","임",
            "오","한","신","서","권","황","안","송","유","홍"
        };
        return lastNames[ThreadLocalRandom.current().nextInt(lastNames.length)];
    }

	String regiNoGenerator(int year) {
	    ThreadLocalRandom r = ThreadLocalRandom.current();

	    // 출생 연도 (yyyy)
	    int yy = year % 100;

	    // 실제 월(1~12)
	    int month = r.nextInt(1, 13);

	    // 해당 월의 최대 일수 계산
	    int maxDay;
	    switch (month) {
	        case 2:
	            // 윤년 판정
	            boolean leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
	            maxDay = leap ? 29 : 28;
	            break;
	        case 4: case 6: case 9: case 11:
	            maxDay = 30;
	            break;
	        default:
	            maxDay = 31;
	    }

	    // 1~maxDay 사이에서 랜덤 날짜
	    int day = r.nextInt(1, maxDay + 1);

	    // 앞 6자리 (YYMMDD)
	    String front6 = String.format("%02d%02d%02d", yy, month, day);

	    // 성별/세기 구분 (7번째 자리)
	    int genderDigit;
	    if (year < 2000) {
	        genderDigit = r.nextBoolean() ? 1 : 2;
	    } else {
	        genderDigit = r.nextBoolean() ? 3 : 4;
	    }

	    // 뒷 6자리는 더미로 0 채움
	    return front6 + genderDigit + "000000";
	}

	String photoIdGeneratorForStu(int genderDigit) {
        // 남자(1,3) => FILE00000000018 / 여자(2,4) => FILE00000000020
        boolean male = (genderDigit == 1 || genderDigit == 3);
        return male ? "FILE00000000018" : "FILE00000000020";
    }

	String photoIdGeneratorForPrf(int genderDigit) {
		// 남자(1,3) => FILE00000000018 / 여자(2,4) => FILE00000000020
		boolean male = (genderDigit == 1 || genderDigit == 3);
		return male ? "FILE00000000033" : "FILE00000000034";
	}

	String addrIdGenerator() {
        // 직전에 생성된 USER 숫자부를 그대로 쓰되 접두어만 ADDR로
        long current = num - 1; // 마지막으로 발급된 USER 번호
        return "ADDR" + String.format("%011d", current);
    }

	String emailGeneratorForStu(int i) {
        // dummyStudent + 5자리 0패딩 + @example.com
        return String.format("dummyStudent%05d@example.com", i);
    }

	String emailGeneratorForPrf(int i) {
		// dummyStudent + 5자리 0패딩 + @example.com
		return String.format("dummyProfessor%05d@example.com", i);
	}

	@Test
	@Transactional
	void insertOneUser() {

		UsersVO oneDummy = new UsersVO();
		oneDummy.setUserId("ADDR89999999999");
		oneDummy.setPwHash("1234");
		oneDummy.setFirstName("길동");
		oneDummy.setLastName("홍");
		oneDummy.setRegiNo("1234561234567");
		oneDummy.setPhotoId("FILE00000000018");
		oneDummy.setAddrId("FILE00000000018");
		oneDummy.setEmail("student@email.com");
		oneDummy.setAddrId("ADDR90000000000");

		int row = ddMapper.insertOneDummyUser(oneDummy);
		assertTrue(row == 1);
	}

	@Test
    void insertUsersStudentDummy() {
        List<UsersVO> dummyList = new ArrayList<>();

        int it = 1000;

        for (int i = 0; i < it; i++) {
            String userId = idGenerator();

            int year = 2002;
            String rrn = regiNoGenerator(year);
            int genderDigit = rrn.charAt(6) - '0';

            UsersVO dummy = new UsersVO();
            dummy.setUserId(userId);
            dummy.setFirstName(firstNameGeneratorForStu());
            dummy.setLastName(lastNameGenerator());
            dummy.setRegiNo(rrn);
            dummy.setPhotoId(photoIdGeneratorForStu(genderDigit));
            dummy.setAddrId(addrIdGenerator());
            dummy.setEmail(emailGeneratorForStu(i));

            dummyList.add(dummy);
        }

        int row = ddMapper.insertDummyUser(dummyList);
        log.info("넣은 더미사용자 수 : {}", row);
    }

	@Test
	void insertUsersProfessorDummy() {
		List<UsersVO> dummyList = new ArrayList<>();

		int it = 100;

		for (int i = 0; i < it; i++) {
			String userId = idGenerator();

			int year = 1976;
			String rrn = regiNoGenerator(year);
			int genderDigit = rrn.charAt(6) - '0';

			UsersVO dummy = new UsersVO();
			dummy.setUserId(userId);
			dummy.setFirstName(firstNameGeneratorForPrf());
			dummy.setLastName(lastNameGenerator());
			dummy.setRegiNo(rrn);
			dummy.setPhotoId(photoIdGeneratorForPrf(genderDigit));
			dummy.setAddrId(addrIdGenerator());
			dummy.setEmail(emailGeneratorForPrf(i));

			dummyList.add(dummy);
		}

		int row = ddMapper.insertDummyUser(dummyList);
		log.info("넣은 더미사용자 수 : {}", row);
	}
}
