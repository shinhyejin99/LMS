package kr.or.jsu.lms.user.service.notification;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.PushNoticeDetailDTO;
import java.util.List;
import java.util.Map;

public interface UserNotificationListService {

    /**
     * 개인의 알림 목록 조회 (수신함).
     * @param userId 로그인한 직원 ID
     * @return 알림 상세 정보 목록 (읽음 상태 포함)
     */
	List<PushNoticeDetailDTO> readNotificationsByUserId(String userId);

    /**
     * 특정 알림 ID와 수신자 ID로 상세 정보를 조회.
     * @param pushId 알림 ID
     * @param userId 수신자 ID
     * @return 알림 상세 DTO
     */
	PushNoticeDetailDTO readNotificationDetail(String pushId, String userId);
    /**
     * 특정 알림을 '읽음' 상태로 업데이트.
     * @param pushId 알림 ID
     * @param userId 수신자 ID
     * @return 업데이트된 행 수 (성공 시 1)
     */
	int markNotificationAsRead(String pushId, String userId);

	/**
	 * 읽지 않은 알림 갯수
	 * @param userId
	 * @return
	 */
	int readUnreadNotificationCount(String userId);

    /**
     * 발신자 ID 기준으로 알림 발신 내역을 조회하고 페이징 처리.
     * PagingInfo의 detailSearch 맵에 'userId'(발신자 ID)가 포함되어야 합니다.
     * @param pagingInfo 페이징 정보 및 검색 조건을 담은 객체
     * @return 발신 알림 이력 목록
     */
    public List<Map<String, Object>> readNotificationHistoryList(PaginationInfo<Map<String, Object>> pagingInfo);
}