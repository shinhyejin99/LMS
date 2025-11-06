package kr.or.jsu.lms.user.service.notification;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.PushNoticeDetailDTO;
import kr.or.jsu.mybatis.mapper.PushNoticeMapper;
import kr.or.jsu.mybatis.mapper.PushNoticeTargetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationListServiceImpl implements UserNotificationListService {
	private final PushNoticeMapper pushNoticeMapper;
	private final PushNoticeTargetMapper pushNoticeTargetMapper;

	/**
	 * 알림 해당 user 한건 조회
	 */
	 public List<PushNoticeDetailDTO> readNotificationsByUserId(String userId) {
		 return pushNoticeMapper.selectPushNoticeListByTargetId(userId);
	 }

	/**
	 * 알림 내역보기
	 */
	@Override
	public PushNoticeDetailDTO readNotificationDetail(String pushId, String userId) {
		// Mapper 호출 시 매개변수 순서 확인 및 일치
		return pushNoticeMapper.selectPushNoticeDetail(pushId, userId);
	}

	/**
	 * 알림 읽음,안읽음 표시
	 */
	@Override
	public int markNotificationAsRead(String pushId, String userId) {
		// PushNoticeTargetMapper의 updateCheckAt 사용
		return pushNoticeTargetMapper.updateCheckAt(pushId, userId);
	}

	/**
	 * 읽지않은 알림 갯수
	 */
	@Override
	public int readUnreadNotificationCount(String userId) {
		return pushNoticeMapper.selectUnreadNoticeCount(userId);
	}

	/**
	 * 발신자 ID 기준으로 알림 발신 내역을 조회하고 페이징 처리.
	 */
	@Override
	public List<Map<String, Object>> readNotificationHistoryList(PaginationInfo<Map<String, Object>> pagingInfo) {

		// 1. 전체 알림 발신 건수 조회
		int totalRecordCount = pushNoticeMapper.selectNotificationHistoryCount(pagingInfo);
		// PaginationInfo에 totalRecord 설정
		pagingInfo.setTotalRecord(totalRecordCount);

		if (totalRecordCount == 0) {
			return List.of(); // 결과가 없으면 빈 리스트 반환
		}

		// 2. 현재 페이지의 알림 발신 내역 목록 조회 (페이징 정보 사용)
		// 🚨 이 쿼리가 TARGET_NAME을 제대로 반환해야 JSP에서 그룹명이 표시됩니다.
		List<Map<String, Object>> historyList = pushNoticeMapper.selectNotificationHistoryList(pagingInfo);

		return historyList;
	}

}