package kr.or.jsu.classregist.student.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classregist.common.LectureSearchCondition;
import kr.or.jsu.classregist.dto.AppliedLectureDTO;
import kr.or.jsu.classregist.dto.ApplyStatusDTO;
import kr.or.jsu.classregist.dto.LectureDetailDTO;
import kr.or.jsu.classregist.dto.LectureListDTO;
import kr.or.jsu.classregist.dto.WishlistCheckDTO;
import kr.or.jsu.classregist.dto.WishlistDTO;
import kr.or.jsu.classregist.dto.WishlistResponseDTO;
import kr.or.jsu.classregist.dto.WishlistSearchDTO;
import kr.or.jsu.classregist.student.controller.LectureWebSocketController;
import kr.or.jsu.classregist.student.service.ClassRegistWishlistService;
import kr.or.jsu.mybatis.mapper.ClassRegistWishListMapper;
import kr.or.jsu.mybatis.mapper.CommonCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 예비수강신청 서비스 구현체
 *
 * @author 김수현
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	김수현	          최초 생성
 *  2025. 10. 18.		김수현			수강신청 관련 추가
 *
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassRegistWishlistServiceImpl implements ClassRegistWishlistService {

	private final ClassRegistWishListMapper wishlistMapper;
	private final CommonCodeMapper commonCodeMapper;
	private final LectureWebSocketController webSocketController;

	/**
	 * 찜 목록 조회
	 */
	@Override
	public List<WishlistDTO> getWishlistWithPaging(WishlistSearchDTO searchDTO) {
		// 전체 개수 조회
		searchDTO.calculateOffset();
	    return wishlistMapper.selectWishlistWithPaging(searchDTO);
	}

	/**
	 * 찜 목록 전체 개수
	 */
	@Override
	public int getWishlistCount(WishlistSearchDTO searchDTO) {
	    return wishlistMapper.countWishlist(searchDTO.getStudentNo());
	}

	/**
	 * 강의 목록 조회
	 */
	@Override
	public List<LectureListDTO> getLectureListWithSearch(LectureSearchCondition searchCondition, String studentNo) {
		searchCondition.calculateOffset();
	    List<LectureListDTO> list = wishlistMapper.selectLectureListWithSearch(searchCondition, studentNo);


	    String targetGrade = (searchCondition.getGradeFilter() != null && !"ALL".equals(searchCondition.getGradeFilter()))
	                        ? searchCondition.getGradeFilter()
	                        : searchCondition.getStudentGrade();

	    list.sort((a, b) -> {
	        boolean aHasTarget = a.getTargetGrades() != null && a.getTargetGrades().contains(getGradeKorean(targetGrade));
	        boolean bHasTarget = b.getTargetGrades() != null && b.getTargetGrades().contains(getGradeKorean(targetGrade));
	        boolean aIsAll = "전학년".equals(a.getTargetGrades());
	        boolean bIsAll = "전학년".equals(b.getTargetGrades());

	        if (aHasTarget && !bHasTarget) return -1;
	        if (!aHasTarget && bHasTarget) return 1;
	        if (aIsAll && !bIsAll) return -1;
	        if (!aIsAll && bIsAll) return 1;
	        return 0;
	    });

	    return list;
	}

	/**
	 * 검색용
	 */
	@Override
	public int getLectureCount(LectureSearchCondition searchCondition) {
	    return wishlistMapper.countLectureList(searchCondition);
	}

	/**
	 * 강의 상세 조회
	 */
	@Override
	public LectureDetailDTO getLectureDetail(String lectureId) {
	    return wishlistMapper.selectLectureDetail(lectureId);
	}

	/**
	 * 찜하기
	 */
	@Override
	public WishlistResponseDTO addWishlist(String studentNo, String lectureId) {

		// 1. 이미 찜했는지 확인
	    int existsCount = wishlistMapper.existsWishlist(studentNo, lectureId);
	    if (existsCount > 0) {
	        return createFailureResponse("이미 찜한 강의입니다.", false, false, false, false);
	    }

	    // 2. 강의 정보 조회
	    WishlistCheckDTO lecture = wishlistMapper.selectLectureForWishlistCheck(lectureId);
	    if (lecture == null) {
	        return WishlistResponseDTO.builder()
	                .success(false)
	                .message("존재하지 않는 강의입니다.")
	                .build();
	    }

	    // 3. 학년 제한 체크
//	    String studentGrade = wishlistMapper.selectStudentGrade(studentNo);
//	    List<String> targetGrades = wishlistMapper.selectTargetGrades(lecture.getSubjectCd());
//
//	    if (!targetGrades.isEmpty() && !targetGrades.contains(studentGrade)) {
//	        return createFailureResponse("해당 학년은 수강할 수 없는 과목입니다.",
//	                                     false, false, false, true); // isGradeRestricted = true
//	    }

	    // 4. 같은 과목 찜 체크
	    int sameSubjectCount = wishlistMapper.existsSameSubjectInWishlist(studentNo, lecture.getSubjectCd());
	    if (sameSubjectCount > 0) {
	        return createFailureResponse("같은 과목을 이미 찜했습니다.",
	                                     true, false, false, false); // isDuplicate = true
	    }

	    // 5. 시간표 겹침 체크
	    int timeConflictCount = wishlistMapper.countTimeConflictInWishlist(studentNo, lectureId);
	    if (timeConflictCount > 0) {
	        return createFailureResponse("시간표가 겹칩니다.",
	                                     false, true, false, false); // isTimeConflict = true
	    }

	    // 6-1. 학생 학년 조회
	    String studentGrade = wishlistMapper.selectStudentGrade(studentNo);

	    // 6-2. 현재 학기 조회
	    // ===== 정석 코드 (운영용) =====
	    // String currentYearterm = commonCodeMapper.selectCurrentYearterm();
	    // String termCd = currentYearterm.substring(currentYearterm.lastIndexOf("_") + 1); // "2026_REG1" -> "REG1"

	    // ===== 시연용 코드 (2025-11-06 기준) =====
	    String termCd = "1"; // 1학기로 고정 (2026_REG1 기준)

	    // 6-3. 학년의 숫자만 추출 (예: "2ND" -> "2")
	    String gradeNum = studentGrade.replaceAll("[^0-9]", "");

	    // 6-4. 해당 학년+학기의 최대 학점 조회
	    Integer maxCredit = commonCodeMapper.selectMaxCreditByGrade(gradeNum, termCd);
	    if (maxCredit == null) {
	        maxCredit = 24; // 기본값
	    }

	    // 6-5. 현재 찜한 학점 합계
	    Integer currentCredits = wishlistMapper.sumWishlistCredits(studentNo);
	    if (currentCredits == null) currentCredits = 0;

	    // 6-6. 학점 초과 체크
	    int totalCredits = currentCredits + lecture.getCredit();
	    if (totalCredits > maxCredit) {
	        return WishlistResponseDTO.builder()
	                .success(false)
	                .isCreditOver(true)
	                .message("최대 학점(" + maxCredit + "학점)을 초과합니다.")
	                .build();
	    }

	    // 7. 찜하기 실행 (성공/DB 실패)
	    int insertResult = wishlistMapper.insertWishlist(studentNo, lectureId);

	    if (insertResult > 0) {
	        return WishlistResponseDTO.builder()
	                .success(true)
	                .message("찜 목록에 추가되었습니다.")
	                .build();
	    } else {
	        // DB 삽입 실패 시 응답 (모든 플래그 false)
	        return createFailureResponse("찜하기에 실패했습니다.", false, false, false, false);
	    }
	}

	/**
	 * 찜 취소
	 */
	@Override
	@Transactional
	public boolean removeWishlist(String studentNo, String lectureId) {
		int deleteResult = wishlistMapper.deleteWishlist(studentNo, lectureId);
        return deleteResult > 0;
	}


	/**
	 * 찜한 강의들의 총 학점 조회
	 */
	@Override
	public Integer sumWishlistCredits(String studentNo) {
		return wishlistMapper.sumWishlistCredits(studentNo);
	}

	//=========================
	// 헬퍼 메서드
	//=========================

	private static WishlistResponseDTO createFailureResponse(
		String message, boolean isDuplicate,
        boolean isTimeConflict, boolean isCreditOver,
        boolean isGradeRestricted
	) {
		return WishlistResponseDTO.builder()
	            .success(false)
	            .message(message)
	            .isDuplicate(isDuplicate)
	            .isTimeConflict(isTimeConflict)
	            .isCreditOver(isCreditOver)
	            .isGradeRestricted(isGradeRestricted)
	            .build();
	}

	private String getGradeKorean(String gradeCd) {
	    switch(gradeCd) {
	        case "1ST": return "1학년";
	        case "2ND": return "2학년";
	        case "3RD": return "3학년";
	        case "4TH": return "4학년";
	        default: return "";
	    }
	}

	// ============ 수강신청 ================
	// ===== 수강신청 기간 체크용 상수 =====
	// 시연용 (하드코딩)
	private static final LocalDateTime APPLY_START = LocalDateTime.of(2025, 1, 1, 9, 0);
	private static final LocalDateTime APPLY_END = LocalDateTime.of(2025, 12, 31, 18, 0);

	/**
	 * 수강신청 기간 체크
	 */
	private boolean isApplyPeriod() {
	    // ===== 시연용 코드 =====
	    LocalDateTime now = LocalDateTime.now();
	    return now.isAfter(APPLY_START) && now.isBefore(APPLY_END);

	    // ===== 정석 코드 (운영용) =====
	    // String currentYearterm = commonCodeMapper.selectCurrentYearterm();
	    // ApplyPeriod period = commonCodeMapper.selectApplyPeriod(currentYearterm);
	    // LocalDateTime now = LocalDateTime.now();
	    // return now.isAfter(period.getStartAt()) && now.isBefore(period.getEndAt());
	}

	/**
	 * LOG ID 생성
	 */
	private String generateLogId() {
	    // 시퀀스 사용
	    Long seq = wishlistMapper.getNextLogSeq();
	    return "LCTLOG" + String.format("%09d", seq);
	}

	/**
	 * 수강신청
	 */
	@Override
	@Transactional
	public WishlistResponseDTO applyLecture(String studentNo, String lectureId) {
		// 0. 수강신청 기간 체크
	    if (!isApplyPeriod()) {
	        return WishlistResponseDTO.builder()
	                .success(false)
	                .message("수강신청 기간이 아닙니다.")
	                .build();
	    }

	    String logId = generateLogId();
	    char conflictYn = 'N';
	    char successYn = 'N';

	    try {
	        // 1. 이미 신청했는지 확인
	        int existsCount = wishlistMapper.existsAppliedLecture(studentNo, lectureId);
	        if (existsCount > 0) {
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'I');
	            return createFailureResponse("이미 신청한 강의입니다.", false, false, false, false);
	        }

	        // 2. 강의 정보 조회 (비관적 락)
	        WishlistCheckDTO lecture = wishlistMapper.selectLectureForApplyWithLock(lectureId);
	        if (lecture == null) {
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'I');
	            return WishlistResponseDTO.builder()
	                    .success(false)
	                    .message("존재하지 않는 강의입니다.")
	                    .build();
	        }

	        // 3. 정원 초과 체크
	        int currentEnroll = wishlistMapper.countCurrentEnroll(lectureId);
	        if (currentEnroll >= lecture.getMaxCap()) {
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'I');
	            return WishlistResponseDTO.builder()
	                    .success(false)
	                    .message("정원이 마감되었습니다.")
	                    .isFullCapacity(true)
	                    .build();
	        }

	        // 4. 학년 제한 체크
	        String studentGrade = wishlistMapper.selectStudentGrade(studentNo);
//	        List<String> targetGrades = wishlistMapper.selectTargetGrades(lecture.getSubjectCd());
//
//	        if (!targetGrades.isEmpty() && !targetGrades.contains(studentGrade)) {
//	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'I');
//	            return createFailureResponse("해당 학년은 수강할 수 없는 과목입니다.",
//	                                         false, false, false, true);
//	        }

	        // 5. 같은 과목 중복 신청 체크
	        int sameSubjectCount = wishlistMapper.existsSameSubjectInApplied(studentNo, lecture.getSubjectCd());
	        if (sameSubjectCount > 0) {
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'I');
	            return createFailureResponse("같은 과목을 이미 신청했습니다.",
	                                         true, false, false, false);
	        }

	        // 6. 시간표 겹침 체크
	        int timeConflictCount = wishlistMapper.countTimeConflictInApplied(studentNo, lectureId);
	        if (timeConflictCount > 0) {
	            conflictYn = 'Y';
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'Y', 'N', 'I');
	            return createFailureResponse("시간표가 겹칩니다.",
	                                         false, true, false, false);
	        }

	        // 7. 학점 초과 체크
	        String gradeNum = studentGrade.replaceAll("[^0-9]", "");
	        String termCd = "1"; // 시연용
	        Integer maxCredit = commonCodeMapper.selectMaxCreditByGrade(gradeNum, termCd);
	        if (maxCredit == null) {
	            maxCredit = 24;
	        }

	        Integer currentCredits = wishlistMapper.sumAppliedCredits(studentNo);
	        if (currentCredits == null) currentCredits = 0;

	        int totalCredits = currentCredits + lecture.getCredit();
	        if (totalCredits > maxCredit) {
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'I');
	            return WishlistResponseDTO.builder()
	                    .success(false)
	                    .isCreditOver(true)
	                    .message("최대 학점(" + maxCredit + "학점)을 초과합니다.")
	                    .build();
	        }

	        // 8. 수강신청 실행
	        int insertResult = wishlistMapper.insertApplyLecture(studentNo, lectureId);

	        if (insertResult > 0) {
	            successYn = 'Y';

	            // 9. 찜 목록에서 제거 (있다면)
	            wishlistMapper.deleteWishlist(studentNo, lectureId);

	            // 10. 성공 로그
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'Y', 'I');

	            // 11. 웹소켓으로 정원 업데이트 브로드캐스트
	            int updatedEnroll = wishlistMapper.countCurrentEnroll(lectureId);
	            webSocketController.broadcastEnrollUpdate(lectureId, updatedEnroll, lecture.getMaxCap());


	            return WishlistResponseDTO.builder()
	                    .success(true)
	                    .message("수강신청이 완료되었습니다.")
	                    .build();
	        } else {
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'I');
	            return createFailureResponse("수강신청에 실패했습니다.", false, false, false, false);
	        }

	    } catch (Exception e) {
	        log.error("수강신청 실패: {}", e.getMessage());
	        wishlistMapper.insertApplyLog(logId, studentNo, lectureId, conflictYn, 'N', 'I');
	        throw e;
	    }
	}

	/**
	 * 수강신청 취소
	 */
	@Override
	@Transactional
	public WishlistResponseDTO cancelApplyLecture(String studentNo, String lectureId) {
		// 0. 수강신청 기간 체크
	    if (!isApplyPeriod()) {
	        return WishlistResponseDTO.builder()
	                .success(false)
	                .message("수강신청 기간이 아닙니다.")
	                .build();
	    }

	    String logId = generateLogId();

	    try {
	        // 1. 신청 여부 확인
	        int existsCount = wishlistMapper.existsAppliedLecture(studentNo, lectureId);
	        if (existsCount == 0) {
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'D');
	            return WishlistResponseDTO.builder()
	                    .success(false)
	                    .message("신청하지 않은 강의입니다.")
	                    .build();
	        }

	        // 2. 수강신청 취소
	        int deleteResult = wishlistMapper.deleteApplyLecture(studentNo, lectureId);

	        if (deleteResult > 0) {
	            // 3. 성공 로그
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'Y', 'D');

	            // 4. 웹소켓으로 정원 업데이트 브로드캐스트
	            int updatedEnroll = wishlistMapper.countCurrentEnroll(lectureId);
	            WishlistCheckDTO lecture = wishlistMapper.selectLectureForWishlistCheck(lectureId);
	            webSocketController.broadcastEnrollUpdate(lectureId, updatedEnroll, lecture.getMaxCap());

	            return WishlistResponseDTO.builder()
	                    .success(true)
	                    .message("수강신청이 취소되었습니다.")
	                    .build();
	        } else {
	            wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'D');
	            return WishlistResponseDTO.builder()
	                    .success(false)
	                    .message("취소에 실패했습니다.")
	                    .build();
	        }

	    } catch (Exception e) {
	        log.error("수강신청 취소 실패: {}", e.getMessage());
	        wishlistMapper.insertApplyLog(logId, studentNo, lectureId, 'N', 'N', 'D');
	        throw e;
	    }
	}

	/**
	 * 수강신청 목록 조회
	 */
	@Override
	public List<AppliedLectureDTO> getAppliedLectureList(String studentNo, int page, int pageSize) {
		int offset = (page - 1) * pageSize;
	    return wishlistMapper.selectAppliedLectureList(studentNo, offset, pageSize);
	}

	/**
	 * 수강신청 전체 개수
	 */
	@Override
	public int getAppliedLectureCount(String studentNo) {
		return wishlistMapper.countAppliedLecture(studentNo);
	}

	/**
	 * 신청 현황 조회
	 */
	@Override
	public ApplyStatusDTO getApplyStatus(String studentNo) {
		// 수강신청 내역
	    int appliedCredits = wishlistMapper.countAppliedCredits(studentNo);
	    int appliedSubjects = wishlistMapper.countAppliedSubjects(studentNo);

	    // 찜 목록
	    Integer wishlistCreditsObj = wishlistMapper.sumWishlistCredits(studentNo);
	    int wishlistCredits = (wishlistCreditsObj != null) ? wishlistCreditsObj : 0;
	    int wishlistSubjects = wishlistMapper.countWishlistSubjects(studentNo);

	    // 최대값
	    String studentGrade = wishlistMapper.selectStudentGrade(studentNo);
	    String gradeNum = studentGrade.replaceAll("[^0-9]", "");
	    Integer maxCredit = commonCodeMapper.selectMaxCreditByGrade(gradeNum, "1");

	    return ApplyStatusDTO.builder()
	            .appliedCredits(appliedCredits)
	            .appliedSubjects(appliedSubjects)
	            .wishlistCredits(wishlistCredits)
	            .wishlistSubjects(wishlistSubjects)
	            .maxCredit(maxCredit != null ? maxCredit : 24)
	            // ❌ maxSubject 제거!
	            .build();
	}
}
