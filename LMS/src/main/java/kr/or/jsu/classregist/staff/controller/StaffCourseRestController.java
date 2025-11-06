package kr.or.jsu.classregist.staff.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.classregist.dto.LectureEnrollStatsDTO;
import kr.or.jsu.classregist.dto.StaffCourseStatsDTO;
import kr.or.jsu.classregist.dto.StudentListResponseDTO;
import kr.or.jsu.classregist.professor.service.ProfLectRegistService;
import kr.or.jsu.classregist.staff.service.StaffCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 교직원 수강신청 관리 rest controller
 *
 * @author 김수현
 * @since 2025. 10. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 27.     	김수현	          최초 생성
 *
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/lms/staff/rest/course")
@RequiredArgsConstructor
public class StaffCourseRestController {

	private final StaffCourseService service;
	private final ProfLectRegistService profLectRegistService;

    /**
     * 강의 통계 조회
     */
    @GetMapping("/stats/{yeartermCd}")
    public ResponseEntity<?> getCourseStats(@PathVariable String yeartermCd) {
        try {
            StaffCourseStatsDTO stats = service.getCourseStats(yeartermCd);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("통계 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "통계 조회 실패"));
        }
    }


    /**
     * 수강신청 통계 조회 (확정용)
     */
    @GetMapping("/apply-stats/{yeartermCd}")
    public ResponseEntity<?> getApplyStatistics(@PathVariable String yeartermCd) {
        try {
            Map<String, Object> stats = service.getApplyStatistics(yeartermCd);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("통계 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "통계 조회 실패"));
        }
    }

    /**
     * 수강신청 확정
     */
    @PostMapping("/confirm-enrollment/{yeartermCd}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<?> confirmEnrollment(@PathVariable String yeartermCd) {
    	try {
            log.info("수강신청 확정 요청: {}", yeartermCd);

            int confirmedCount = service.confirmEnrollment(yeartermCd);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "수강신청이 확정되었습니다.",
                "confirmedCount", confirmedCount
            ));

        } catch (IllegalStateException e) {
            // 중복 확정 시도
            if ("ALREADY_CONFIRMED".equals(e.getMessage())) {
                log.warn("중복 확정 시도: {}", yeartermCd);
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "alreadyConfirmed", true,
                    "message", "이미 수강신청이 확정되었습니다."
                ));
            }

            log.error("수강신청 확정 실패", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));

        } catch (Exception e) {
            log.error("수강신청 확정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "message", "수강신청 확정 중 오류가 발생했습니다."
                ));
        }
    }

    // =====================================================
    // 강의 상세 (학생 목록 모달용) => 교수쪽 참고함
    // =====================================================

    /**
     * 강의별 수강생 목록 조회 (페이징)
     */
    @GetMapping("/lectures/{lectureId}/students")
    public ResponseEntity<?> getLectureStudents(
        @PathVariable String lectureId,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            log.info("강의 수강생 조회: lectureId={}, page={}", lectureId, page);

            // 교수 Service 재사용
            StudentListResponseDTO response = profLectRegistService.getLectureStudentsWithPaging(
                lectureId,
                page,
                pageSize
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("수강생 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "수강생 목록 조회 실패"));
        }
    }

    /**
     * 강의 정원 정보 조회
     */
    @GetMapping("/lectures/{lectureId}/enroll-info")
    public ResponseEntity<?> getLectureEnrollInfo(@PathVariable String lectureId) {
        try {
            log.info("강의 정원 정보 조회: lectureId={}", lectureId);

            // 교수 Service 재사용
            LectureEnrollStatsDTO info = profLectRegistService.getLectureEnrollInfo(lectureId);

            return ResponseEntity.ok(info);

        } catch (Exception e) {
            log.error("정원 정보 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "정원 정보 조회 실패"));
        }
    }
}
