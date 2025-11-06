package kr.or.jsu.mybatis.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classregist.dto.LectureDetailDTO;
import kr.or.jsu.vo.LectureVO;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class ClassRegistWishlistMapperTest {
	@Autowired
	ClassRegistWishListMapper mapper;

	// == 테스트용 더미 데이터 ==
    private final String TEST_STUDENT_NO = "202500001"; // 존재하는 학생
    private final String EXIST_LECTURE_ID = "LECT70000001010"; // 존재하는 강의 (컴공) => 3학점
    //private final String ANOTHER_LECTURE_ID = "LECT70000001102"; // 다른 강의 (공공행정) => 2학점
    private final String SAME_SUBJECT_LECTURE_ID = "LECT70000001106"; // 동일 과목의 다른 분반 강의
//    private final String TEST_YEAR_TERM = "2026_REG1";
    private final String TEST_SUBJECT_CD = "SUBJ00000000010"; // EXIST_LECTURE_ID의 과목코드

    /**
     * 찜 목록 조회 => O
     */
//    @Test
//	void testSelectWishlistWithPaging() {
//    	PaginationInfo<?> paging = new PaginationInfo<>(10, 5);
//        paging.setCurrentPage(1);
//
//        List<WishlistDTO> result = mapper.selectWishlistWithPaging(paging, "202500001");
//
//        assertNotNull(result); // null이 아닌지 먼저 확인
//        assertTrue(result.isEmpty(), "데이터가 없는 경우 빈 리스트여야 합니다.");
//        log.info("현재는 빈 리스트가 나오는 게 맞음 : {}", result);
//	}

	/**
	 * 찜 목록 개수 => O
	 */
	@Test
	void testCountWishlist() {
		// 찜 목록이 비어있는 상태
		int initialCount = mapper.countWishlist(TEST_STUDENT_NO);
        log.info("현재 장바구니 상태 : {}", initialCount); // 0

        // 찜 목록 1개 추가
        mapper.insertWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID);
        int countAfterInsert = mapper.countWishlist(TEST_STUDENT_NO);

        assertEquals(initialCount + 1, countAfterInsert);
        log.info("현재 장바구니 상태 : {}", countAfterInsert); // 1
	}

	/**
	 * 찜 하기 => O
	 */
	@Test
	void testInsertWishlist() {
		// 찜 목록에 해당 강의가 없는 상태
        int result = mapper.insertWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID);

        // 1개 행 삽입, 찜 목록에 존재하는지 확인
        assertEquals(1, result);
        assertEquals(1, mapper.existsWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID));
	}

	/**
	 * 찜 취소 => O
	 */
	@Test
	void testDeleteWishlist() {
		// 찜 목록에 강의 존재
        mapper.insertWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID);

        // 찜 취소
        int result = mapper.deleteWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID);

        // 1개 행 삭제, 찜 목록에 존재하지 않는지 확인
        assertEquals(1, result);
        assertEquals(0, mapper.existsWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID));
	}

	/**
	 * 찜 중복 확인 => 0
	 */
	@Test
	void testExistsWishlist() {
        // 삽입 전 -> 0
        int existsBefore = mapper.existsWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID);
        mapper.insertWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID);

        // 삽입 후 -> 1
        int existsAfter = mapper.existsWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID);

        assertEquals(0, existsBefore);
        assertEquals(1, existsAfter);
	}

	/**
	 * 같은 과목 찜 여부 체크 => O
	 */
	@Test
	void testExistsSameSubjectInWishlist() {
		// GIVEN: 찜 목록에 같은 과목이 없는 상태 가정

        // WHEN 1: 같은 과목의 강의를 찜
        mapper.insertWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID);

        // THEN: 같은 과목 코드(TEST_SUBJECT_CD)로 체크 시 1 반환
        int count = mapper.existsSameSubjectInWishlist(TEST_STUDENT_NO, "SUBJ00000000006");
        assertEquals(1, count);
	}

	/**
	 * 시간표 겹침 체크 => O
	 */
	@Test
	void testCountTimeConflictInWishlist() {

        mapper.insertWishlist(TEST_STUDENT_NO, "LECT70000001011");
        int conflictCount = mapper.countTimeConflictInWishlist(TEST_STUDENT_NO, "LECT70000001023");

        // 충돌이 발생해야 함 (1)
        log.info("시간 충돌 개수 (충돌 예상): {}", conflictCount);
        assertTrue(conflictCount > 0, "충돌 예상 강의는 1개 이상의 충돌을 반환해야 합니다.");
	}

	/**
	 * 찜한 강의들 총 학점 조회 => O
	 */
	@Test
	void testSumWishlistCredits() {
        mapper.insertWishlist(TEST_STUDENT_NO, EXIST_LECTURE_ID); // 3학점
        mapper.insertWishlist(TEST_STUDENT_NO, SAME_SUBJECT_LECTURE_ID); // 2학점

        Integer sumCredits = mapper.sumWishlistCredits(TEST_STUDENT_NO);

        log.info("찜 목록 총 학점: {}", sumCredits);
        assertTrue(sumCredits >= 3); // 5학점
	}

	/**
	 * 강의 정보 조회(과목코드 포함) => O
	 */
	@Test
	void testSelectLectureWithSubject() {
        LectureVO vo = mapper.selectLectureWithSubject(EXIST_LECTURE_ID);
        assertNotNull(vo);
        assertNotNull(vo.getSubjectCd());
        log.info("조회된 강의 정보: {}", vo);
	}

	/**
	 * 강의 목록 조회 => O
	 */
//	@Test
//	void testSelectLectureListWithSearch() {
//	    PaginationInfo<LectureListDTO> paging = new PaginationInfo<>();
//	    paging.setCurrentPage(1);
//	    paging.setScreenSize(10);
//
//	    SimpleSearch searchParam = new SimpleSearch("SUBJECT", "자료구조"); // 자료구조: 2개, 컴퓨터 => 8개, 컴퓨터구조 => 6개
//	    paging.setSimpleSearch(searchParam);
//
//	    List<LectureListDTO> list = mapper.selectLectureListWithSearch(paging, TEST_STUDENT_NO, TEST_YEAR_TERM);
//
//	    assertNotNull(list);
//	    log.info("강의 목록 조회 결과: {}개", list.size());
//	    log.info("해당 강의 : {}", list);
//	}
//
//	/**
//	 * 강의 목록 수 (페이지네이션) => O
//	 */
//	@Test
//	void testCountLectureList() {
//		// '컴퓨터' 과목명을 포함하는 강의 개수 조회
//        int count = mapper.countLectureList("SUBJECT", "컴퓨터네트워크", "2026_REG1"); // 컴퓨터그래픽스는 없음
//
//        assertTrue(count > 0);
//        log.info("강의 목록 전체 개수(8개 나와야함): {}", count);
//	}

	/**
	 * 강의 상세 조회 => O
	 */
	@Test
	void testSelectLectureDetail() {
		LectureDetailDTO dto = mapper.selectLectureDetail(EXIST_LECTURE_ID);

	    assertNotNull(dto);
	    assertNotNull(dto.getProfessorNo());
	    assertNotNull(dto.getSubjectCd());
	    assertNotNull(dto.getTimeInfo());  // 시간 정보 확인!

	    log.info("강의 상세 정보: {}", dto);
	    log.info("시간 정보: {}", dto.getTimeInfo());
	}

	/**
	 * 학생 학년 코드 조회 => O
	 */
	@Test
	void testSelectStudentGrade() {
        String grade = mapper.selectStudentGrade(TEST_STUDENT_NO);
        assertNotNull(grade);
        log.info("{} 학생의 학년 코드: {}", TEST_STUDENT_NO, grade); // 1ST
	}

	/**
	 * 과목의 대상학년 목록 조회 => O
	 */
	@Test
	void testSelectTargetGrades() {
        List<String> grades = mapper.selectTargetGrades(TEST_SUBJECT_CD);

        assertNotNull(grades);
        assertTrue(grades.contains("3RD")); // 3학년, 4학년 대상 과목
        log.info("{} 과목의 대상 학년: {}", TEST_SUBJECT_CD, grades);
	}
	

}
