package kr.or.jsu.classregist.staff.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classregist.dto.LectureListDTO;
import kr.or.jsu.classregist.dto.StaffCourseSearchDTO;
import kr.or.jsu.classregist.dto.StaffCourseStatsDTO;
import kr.or.jsu.classregist.staff.service.StaffCourseService;
import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.dto.SubjectInfoDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationCreateService;
import kr.or.jsu.lms.user.service.notification.UserNotificationListService;
import kr.or.jsu.mybatis.mapper.StaffCourseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 교직원 수강신청 관리 service 구현체
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
@Service
@RequiredArgsConstructor
public class StaffCourseServiceImpl implements StaffCourseService {

private final StaffCourseMapper mapper;
private final UserNotificationCreateService notificationService;
    /**
     * 강의 통계 조회
     */
    @Override
    public StaffCourseStatsDTO getCourseStats(String yeartermCd) {
        return mapper.selectCourseStats(yeartermCd);
    }

    /**
     * 강의 목록 조회
     */
    @Override
    public List<LectureListDTO> getCourseList(StaffCourseSearchDTO searchDTO) {
        log.info("강의 목록 조회: {}", searchDTO);
        searchDTO.calculateOffset();
        return mapper.selectCourseList(searchDTO);
    }

    /**
     * 강의 수 (페이징)
     */
    @Override
    public int getCourseCount(StaffCourseSearchDTO searchDTO) {
        return mapper.countCourseList(searchDTO);
    }
    /**
     * 학기명 코드에서 명으로 변환
     * @param yeartermCd
     * @return
     */
    private String formatYearterm(String yeartermCd) {
        if (yeartermCd == null) return "";
        String[] parts = yeartermCd.split("_"); // 예: ["2026","reg1"]
        if (parts.length != 2) return yeartermCd;

        String year = parts[0];
        String term = parts[1].toLowerCase();
        String termName;
        switch (term) {
            case "reg1": termName = "1학기"; break;
            case "reg2": termName = "2학기"; break;
            case "summer": termName = "여름학기"; break;
            case "winter": termName = "겨울학기"; break;
            default: termName = term; break;
        }
        return String.format("%s년 %s", year, termName);
    }

    /**
     * 수강신청 확정 시 알림 발송
     * @param yeartermCd 학년도학기코드
     */
    private void sendEnrollmentConfirmNotification(String yeartermCd) {
        try {
            // ✅ 학기코드 변환 (예: 2026_reg1 → 2026년 1학기)
            String formattedTerm = formatYearterm(yeartermCd);

            // 1. 확정된 수강신청 학생 목록 조회
            List<String> studentIds = mapper.selectConfirmedStudents(yeartermCd);

            // 2. 해당 학기 강의 담당 교수 목록 조회
            List<String> professorIds = mapper.selectProfessorsForTerm(yeartermCd);

            // 3. 학생에게 알림 발송
            if (!studentIds.isEmpty()) {
                String studentTitle = "✅ 수강신청 확정 안내";
                String studentContent = String.format("%s 수강신청이 종료되었습니다. 학사관리에서 확정된 수강 내역을 확인하세요.", formattedTerm);
                String studentPushUrl = "/lms/student/enrollment/confirmed";

                for (String studentNo : studentIds) {
                    AutoNotificationRequest alert = AutoNotificationRequest.builder()
                            .receiverId(studentNo)
                            .title(studentTitle)
                            .content(studentContent)
                            .senderName("LMS 행정팀")
                            .pushUrl(studentPushUrl)
                            .build();

                    notificationService.sendAutoNotification(alert);
                }
                log.info("학생 수강신청 확정 알림 발송 완료: {}명", studentIds.size());
            }

            // 4. 교수에게 알림 발송
            if (!professorIds.isEmpty()) {
                String professorTitle = "📋 수강신청 확정 안내";
                String professorContent = String.format("%s 수강신청이 확정되었습니다. 수강신청 현황에서 확인바랍니다.", formattedTerm);
                String professorPushUrl = "/lms/professor/course/students";

                for (String professorId : professorIds) {
                    AutoNotificationRequest alert = AutoNotificationRequest.builder()
                            .receiverId(professorId)
                            .title(professorTitle)
                            .content(professorContent)
                            .senderName("LMS 행정팀")
                            .pushUrl(professorPushUrl)
                            .build();

                    notificationService.sendAutoNotification(alert);
                }
                log.info("교수 수강신청 확정 알림 발송 완료: {}명", professorIds.size());
            }

        } catch (Exception e) {
            log.error("수강신청 확정 알림 발송 실패: {}", yeartermCd, e);
            // 알림 발송 실패는 트랜잭션에 영향 없음
        }
    }
    /**
     * 수강신청 확정
     */
    @Override
    @Transactional
    public int confirmEnrollment(String yeartermCd) {
    	log.info("수강신청 확정 시작: {}", yeartermCd);

        try {
            // 확정 전 통계 조회(확인용)
            Map<String, Object> beforeStats = mapper.getApplyStatistics(yeartermCd);
            log.info("확정 전 통계: {}", beforeStats);

            // STU_APPLY_LCT => STU_ENROLL_LCT 복사 (NOT EXISTS로 중복 제외)
            int insertCount = mapper.confirmEnrollment(yeartermCd);

            // 0건이면 이미 확정된 것!
            if (insertCount == 0) {
                log.warn("이미 확정되어 신규 등록 건수 0건: {}", yeartermCd);
                throw new IllegalStateException("ALREADY_CONFIRMED");
            }

            log.info("확정 완료: {}건", insertCount);
            sendEnrollmentConfirmNotification(yeartermCd);
            return insertCount;

        } catch (IllegalStateException e) {
            // 중복 확정 예외는 그대로 던짐
            throw e;

        } catch (DuplicateKeyException e) {
            // DB 레벨 중복 키 예외 처리
            log.error("중복 키 오류 발생: {}", yeartermCd, e);
            throw new IllegalStateException("ALREADY_CONFIRMED");

        } catch (Exception e) {
            log.error("수강신청 확정 실패: {}", yeartermCd, e);
            throw new RuntimeException("수강신청 확정 중 오류 발생", e);
        }
    }

    /**
     * 통계용 데이터
     */
    @Override
    public Map<String, Object> getApplyStatistics(String yeartermCd) {
        return mapper.getApplyStatistics(yeartermCd);
    }

}
