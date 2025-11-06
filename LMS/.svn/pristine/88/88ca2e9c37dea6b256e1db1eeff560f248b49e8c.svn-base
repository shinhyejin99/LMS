package kr.or.jsu.lms.staff.service.subject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.SubjectInfoDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationCreateService;
import kr.or.jsu.mybatis.mapper.SubjectMapper;
import kr.or.jsu.portal.controller.certificate.PortalCertificateType;
import kr.or.jsu.vo.SubjectVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로그 사용을 위해 추가
@Service
@RequiredArgsConstructor
public class StaffSubjectServiceImpl implements StaffSubjectService {

	private final SubjectMapper mapper;
	private final UserNotificationCreateService notificationService;

	@Override
	public void createStaffSubject(SubjectVO subject) {
		mapper.insertSubject(subject);
	}

	/**
	 * 교과목 목록 조회 및 페이징 처리
	 */
	@Override
	public List<Map<String, Object>> readStaffSubjectList(PaginationInfo<?> pagingInfo, String searchKeyword,
			String filterType) {

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("searchKeyword", searchKeyword);
		paramMap.put("filterType", filterType);

		// 1. 전체 카운트 조회 (페이징 처리를 위한 필수 단계)
		int totalCount = mapper.selectTotalSubjectCount(paramMap);
		log.info("조회된 총 교과목 수: {}", totalCount);

		// 2. PaginationInfo에 전체 레코드 수 설정 -> 페이지 정보(startPage, endPage 등) 및 ROWNUM 인덱스
		// 자동 계산 완료
		pagingInfo.setTotalRecord(totalCount);

		// 3. 계산된 ROWNUM 인덱스를 MyBatis 파라미터 맵에 추가 (쿼리 실행 조건)
		// getFirstIndex()와 getLastIndex() 대신 MyBatis 쿼리 변수명에 맞게 설정합니다.
		paramMap.put("firstIndex", pagingInfo.getStartRow());
		paramMap.put("lastIndex", pagingInfo.getEndRow());
		log.info("페이징 인덱스: {} ~ {}", pagingInfo.getStartRow(), pagingInfo.getEndRow());

		// 4. 목록 조회 (DTO 타입으로 변경 가정)
		List<Map<String, Object>> result = mapper.selectStaffSubjectList(paramMap);
		log.info(" =======================> 정원 : {}", result);
		return result;
	}

	/**
	 * 대시보드 필터용: 이수 구분별 교과목 개수를 조회합니다.
	 */
	public List<Map<String, Object>> readSubjectCountByType() {
		return mapper.selectSubjectCountByType();
	}
	// ------------------------------------------

	public SubjectInfoDetailDTO readStaffSubject(String subjectCd) {
	    return mapper.selectstaffSubjectDetail(subjectCd);
	}

	@Override
	public void modifyStaffSubject(SubjectInfoDetailDTO subject) {

		// 1. 기존 deleteAt 상태 저장
	    // deleteStatus 값이 'DELETED'인지 확인하여 deleteAt 필드를 설정
		boolean isObsolete = "DELETED".equals(subject.getDeleteStatus());

		if (isObsolete) {
	        // '폐지' 상태인 경우: 현재 날짜를 설정
	        subject.setDeleteAt(LocalDate.now());
            log.info("교과목 {} 폐지 처리 (deleteAt: {})", subject.getSubjectCd(), subject.getDeleteAt());
	    } else {
	        // 'ACTIVE' 상태인 경우: NULL로 설정
	        subject.setDeleteAt(null);
            log.info("교과목 {} 활성 처리 (deleteAt: NULL)", subject.getSubjectCd());
	    }

	    // 2. Mapper로 교과목 정보 업데이트
	    mapper.updateSubject(subject);

		// 3. 폐지 처리 시 (업데이트 성공 후) 알림 전송 로직을 별도 메서드로 분리하여 호출
		if (isObsolete) {
			sendSubjectObsoleteNotification(subject);
		}
	}

	/**
     * 교과목 폐지 시 담당 교수 및 관련 학생에게 알림을 전송합니다.
     * @param subject 폐지된 교과목 정보 DTO
     */
    private void sendSubjectObsoleteNotification(SubjectInfoDetailDTO subject) {
        final String subjectCd = subject.getSubjectCd();
        final String subjectName = subject.getSubjectName();
        final String deleteDate = subject.getDeleteAt().toString();

        // 3-A. 알림 대상 (교과목 담당 교수 및 관련 학생) ID 조회
        // 이 메서드는 교수와 학생 ID 모두를 반환한다고 가정합니다.
        List<String> userIdsToNotify = mapper.selectUsersForSubjectObsolete(subjectCd);

        if (!userIdsToNotify.isEmpty()) {

            // 알림 내용과 제목을 교수 및 학생 모두에게 적합하도록 설정합니다.
        	String title = String.format("📢 중요 안내: 교과목 폐지 처리 (%s)", subjectName);
            // ⭐️ 수정: 과목 코드(subjectCd)를 알림 내용에서 제외 ⭐️
            String content = String.format("교과목 [%s]가 관리자에 의해 폐지 처리되었습니다. 폐지일: %s. 수강 및 강의 계획 변경에 유의하시기 바랍니다.",
                                            subjectName, deleteDate);
            // 알림 클릭 시 해당 교과목의 상세 페이지(일반 사용자용)로 이동하도록 설정합니다.
            String pushUrl = "/lms/subject/detail/" + subjectCd;

            // 3-B. 각 사용자(교수 및 학생)에게 알림 요청 객체를 생성하고 전송
            for (String userId : userIdsToNotify) {
                AutoNotificationRequest alert = AutoNotificationRequest
                        .builder()
                        .receiverId(userId)                 // 알림을 받을 사용자 ID
                        .title(title)                       // 알림 제목
                        .content(content)                   // 알림 내용
                        .senderName("LMS 행정팀")               // 발신자 이름 (관리 주체)
                        .pushUrl(pushUrl)                   // 알림 클릭 시 이동할 URL
                        .build();

                // 통합된 자동 알림 메서드 호출
                notificationService.sendAutoNotification(alert);
            }

            log.info("교과목 폐지 알림 발송 완료 (대상: 교수 및 관련 학생). 교과목: {}, 대상 사용자 수: {}", subjectName, userIdsToNotify.size());
        } else {
            log.warn("교과목 폐지 알림 대상자가 없습니다 (담당 교수 및 관련 학생 없음). 교과목 코드: {}", subjectCd);
        }
    }
	/**
	 * 학과별 교과목 갯수
	 */
	@Override
	public List<Map<String, Object>> readSubjectCountByDept() {
		return mapper.selectSubjectCountByDept();
	}

	/**
	 * 학년별 교과목 평균 학점
	 */
	@Override
	public List<Map<String, Object>> readAverageCreditByGrade() {
		return mapper.selectAverageCreditByGrade();
	}

    // =========================================================================
    // ⭐ KPI 통계 구현 ⭐
    // =========================================================================

	/**
	 * 전체 활성 교과목 수 (deleteAt이 NULL인 교과목)를 조회합니다.
	 */
	@Override
	public int readTotalActiveSubjectCount() {
		return mapper.selectTotalActiveSubjectCount();
	}

	/**
	 * 전체 교과목의 평균 학점을 조회합니다.
	 */
	@Override
	public Double readGlobalAverageCredit() {

		return mapper.selectGlobalAverageCredit();
	}

	@Override
	public List<Map<String, Object>> readAverageHourByDept() {

		return mapper.selectAverageHourByDept();
	}

}