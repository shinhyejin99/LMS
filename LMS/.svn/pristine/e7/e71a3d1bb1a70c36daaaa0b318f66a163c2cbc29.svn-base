package kr.or.jsu.aiDummyDataGenerator.step1_user;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.or.jsu.mybatis.mapper.dummy.DummyDataMapper;
import kr.or.jsu.vo.AddressVO;
import kr.or.jsu.vo.UsersVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class UsersDummyGeneratorTest {

	private static final String PW_HASH = "{bcrypt}$2a$10$1vrhrg0sCry19KPz/pyJ5OmwYQZNw805uL9lYF8dFiVMWgCbodQwi";
	private static final String[] BANK_CODES = { "BANK_NH", "BANK_SH", "BANK_KB", "BANK_WR", "BANK_HN", "BANK_IBK",
			"BANK_SC", "BANK_BN", "BANK_DG", "BANK_GJ", "BANK_JB", "BANK_JJ", "BANK_KKO", "BANK_KBANK", "BANK_TOSS",
			"BANK_POST", "BANK_SAEMAUL" };
	private static final int[] TARGET_YEARS = { 25, 24, 23, 22, 21, 20, 19 };

	private static final int STUDENTS_PER_YEAR = 2000;
	private static final int PROFESSORS_PER_YEAR = 500;
	private static final int STAFF_PER_YEAR = 100;

	private static final AtomicLong ADDRESS_SEQ = new AtomicLong(70000000000L);

	private static final String CITY = "대전";
	private static final String DISTRICT = "중구";
	private static final String[] NEIGHBORHOODS = { "대흥동", "은행동", "선화동", "문화동", "태평동", "중촌동", "용두동", "목동", "호동",
			"용전동" };
	private static final String[] ROAD_PREFIXES = { "대흥", "문화", "선화", "중앙", "은행", "대전천", "보문", "태평", "계룡", "중촌" };
	private static final String[] ROAD_SUFFIXES = { "로", "길", "거리" };
	private static final String[] BUILDING_PREFIXES = { "한밭", "대흥", "선화", "은행", "문화", "중앙", "태평", "보문", "중촌", "목동" };
	private static final String[] BUILDING_TYPES = { "아파트", "빌라", "주상복합", "트윈하우스", "센트럴", "힐스", "스카이뷰", "팰리스",
			"리버파크", "시티파크" };
	private static final String[] UNIT_DETAILS = { "101동 1001호", "102동 203호", "103동 804호", "201동 1105호", "202동 502호",
			"301동 1603호", "미래타워 4층 402호", "프라자 5층 503호", "리버뷰 3층 305호", "센트럴 7층 701호" };

	private static final String[] FAMILY_NAMES = { "김", "이", "박", "최", "정", "강", "조", "윤", "장", "임" };

	private static final String[] STUDENT_FIRST_SYLLABLES = { "서", "민", "하", "지", "준", "윤", "도", "채", "현", "유" };
	private static final String[] STUDENT_SECOND_SYLLABLES = { "준", "우", "윤", "서", "빈", "아", "현", "율", "후", "린" };

	private static final String[] PROFESSOR_FIRST_SYLLABLES = { "영", "정", "성", "미", "재", "선", "경", "광", "도", "은" };
	private static final String[] PROFESSOR_SECOND_SYLLABLES = { "자", "숙", "희", "호", "수", "철", "진", "자", "옥", "호" };

	private static final String[] STAFF_FIRST_SYLLABLES = { "지", "혜", "민", "수", "성", "경", "준", "서", "태", "하" };
	private static final String[] STAFF_SECOND_SYLLABLES = { "현", "영", "빈", "호", "윤", "민", "희", "린", "아", "진" };

	@Autowired
	private DummyDataMapper dummyDataMapper;

	private String nextAddressId() {
		return "ADDR" + String.format("%011d", ADDRESS_SEQ.getAndIncrement());
	}

	private AddressVO createRandomAddress() {
		ThreadLocalRandom r = ThreadLocalRandom.current();
		String dong = NEIGHBORHOODS[r.nextInt(NEIGHBORHOODS.length)];
		String road = ROAD_PREFIXES[r.nextInt(ROAD_PREFIXES.length)] + ROAD_SUFFIXES[r.nextInt(ROAD_SUFFIXES.length)];
		int building = r.nextInt(1, 401);
		String base = CITY + " " + DISTRICT + " " + dong + " " + road + " " + building;
		String buildingName = BUILDING_PREFIXES[r.nextInt(BUILDING_PREFIXES.length)]
				+ BUILDING_TYPES[r.nextInt(BUILDING_TYPES.length)];
		String detail = buildingName + " " + UNIT_DETAILS[r.nextInt(UNIT_DETAILS.length)];
		String zip = String.format("%05d", r.nextInt(35000, 36000));
		return new AddressVO(nextAddressId(), base, detail, zip, null, null, "Y");
	}

	private List<AddressVO> prepareAddresses(int count) {
		List<AddressVO> addresses = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			addresses.add(createRandomAddress());
		}
		return addresses;
	}

	private int batchInsertAddresses(List<AddressVO> addresses, int batchSize) {
		int total = 0;
		for (int start = 0; start < addresses.size(); start += batchSize) {
			int end = Math.min(start + batchSize, addresses.size());
			total += dummyDataMapper.insertDummyAddress(new ArrayList<>(addresses.subList(start, end)));
		}
		return total;
	}

	private int batchInsertUsers(List<UsersVO> users, int batchSize) {
		int total = 0;
		for (int start = 0; start < users.size(); start += batchSize) {
			int end = Math.min(start + batchSize, users.size());
			total += dummyDataMapper.insertDummyUser(new ArrayList<>(users.subList(start, end)));
		}
		return total;
	}

	private String buildUserId(char categoryDigit, int year, int sequence) {
		return "USER" + categoryDigit + String.format("%02d", year) + String.format("%08d", sequence);
	}

	private String randomFamilyName() {
		return FAMILY_NAMES[ThreadLocalRandom.current().nextInt(FAMILY_NAMES.length)];
	}

	private String randomGivenName(String[] firstPool, String[] secondPool) {
		ThreadLocalRandom r = ThreadLocalRandom.current();
		return firstPool[r.nextInt(firstPool.length)] + secondPool[r.nextInt(secondPool.length)];
	}

	private String randomRegiNo() {
		ThreadLocalRandom r = ThreadLocalRandom.current();
		return String.format("%06d-%07d", r.nextInt(0, 1_000_000), r.nextInt(0, 10_000_000));
	}

	private String randomMobile() {
		ThreadLocalRandom r = ThreadLocalRandom.current();
		return String.format("010-%04d-%04d", r.nextInt(1000, 10_000), r.nextInt(1000, 10_000));
	}

	private String randomBankAccount() {
		ThreadLocalRandom r = ThreadLocalRandom.current();
		return String.format("%03d-%06d-%03d", r.nextInt(100, 1000), r.nextInt(0, 1_000_000),
				r.nextInt(100, 1000));
	}

	private String randomBankCode() {
		return BANK_CODES[ThreadLocalRandom.current().nextInt(BANK_CODES.length)];
	}

	private String buildEmail(String userId, String domain) {
		return userId.toLowerCase(Locale.ROOT) + "@" + domain;
	}

	private void populateUsers(List<UsersVO> collector, List<AddressVO> addresses, AtomicInteger addressCursor,
			char categoryDigit, int year, int countPerYear, String[] givenFirstPool, String[] givenSecondPool,
			String emailDomain) {
		for (int seq = 1; seq <= countPerYear; seq++) {
			int addrIndex = addressCursor.getAndIncrement();
			AddressVO addr = addresses.get(addrIndex);

			String userId = buildUserId(categoryDigit, year, seq);

			UsersVO vo = new UsersVO();
			vo.setUserId(userId);
			vo.setPwHash(PW_HASH);
			vo.setFirstName(randomGivenName(givenFirstPool, givenSecondPool));
			vo.setLastName(randomFamilyName());
			vo.setRegiNo(randomRegiNo());
			vo.setPhotoId(null);
			vo.setAddrId(addr.getAddrId());
			vo.setMobileNo(randomMobile());
			vo.setEmail(buildEmail(userId, emailDomain));
			vo.setBankCode(randomBankCode());
			vo.setBankAccount(randomBankAccount());
			collector.add(vo);
		}
	}

	@Test
	void insertUsersByRoleAndYear() {
		int totalStudents = STUDENTS_PER_YEAR * TARGET_YEARS.length;
		int totalProfessors = PROFESSORS_PER_YEAR * TARGET_YEARS.length;
		int totalStaff = STAFF_PER_YEAR * TARGET_YEARS.length;
		int totalUsers = totalStudents + totalProfessors + totalStaff;

		List<AddressVO> addresses = prepareAddresses(totalUsers);
		int insertedAddresses = batchInsertAddresses(addresses, 500);
		log.info("Inserted prerequisite addresses for users: {}", insertedAddresses);

		List<UsersVO> users = new ArrayList<>(totalUsers);
		AtomicInteger addressCursor = new AtomicInteger(0);

		for (int year : TARGET_YEARS) {
			populateUsers(users, addresses, addressCursor, '1', year, STUDENTS_PER_YEAR, STUDENT_FIRST_SYLLABLES,
					STUDENT_SECOND_SYLLABLES, "student.lms.ac.kr");
		}
		for (int year : TARGET_YEARS) {
			populateUsers(users, addresses, addressCursor, '2', year, PROFESSORS_PER_YEAR, PROFESSOR_FIRST_SYLLABLES,
					PROFESSOR_SECOND_SYLLABLES, "professor.lms.ac.kr");
		}
		for (int year : TARGET_YEARS) {
			populateUsers(users, addresses, addressCursor, '3', year, STAFF_PER_YEAR, STAFF_FIRST_SYLLABLES,
					STAFF_SECOND_SYLLABLES, "staff.lms.ac.kr");
		}

		int insertedUsers = batchInsertUsers(users, 500);
		log.info("Inserted users: total={}, students={}, professors={}, staff={}", insertedUsers, totalStudents,
				totalProfessors, totalStaff);
	}
}
