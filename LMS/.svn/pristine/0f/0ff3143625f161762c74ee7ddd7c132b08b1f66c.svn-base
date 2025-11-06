package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.PushNoticeDetailDTO;
import kr.or.jsu.vo.PushNoticeTargetVO;
import kr.or.jsu.vo.PushNoticeVO;

@Mapper
public interface PushNoticeMapper {

	public int insertPushNotice(PushNoticeDetailDTO noticeDTO);
	/*
	 * 실행 단계: Service 계층에서 알림 발송 요청 시 가장 먼저 호출됩니다.
	 * 역할: 알림의 마스터 정보(제목, 내용, 발신자 등)를 DB의 알림 마스터 테이블에 INSERT합니다.
	 * 반환: INSERT된 행의 수(1)를 반환하며, noticeDTO 객체에는 새로 생성된 'pushId'가 설정됩니다.
	 */

	public List<Map<String, Object>> selectNotificationHistoryList(PaginationInfo<Map<String, Object>> pagingInfo);
	/*
	 * 실행 단계: 알림 관리 페이지 등에서 호출됩니다.
	 * 역할: DB에서 모든 알림 기록 또는 특정 조건에 맞는 알림 기록 목록을 조회합니다.
	 * 반환: 조회된 알림 목록을 List<Map> 형태로 반환합니다.
	 */

	int selectPushNoticeCount(PaginationInfo<Map<String, Object>> pagingInfo);
	/*
	 * 실행 단계: 알림 목록을 페이징 처리하기 전에 호출됩니다.
	 * 역할: 페이징을 위해 전체 알림 기록의 총 개수(COUNT)를 조회합니다.
	 * 반환: 총 알림 개수(정수형)를 반환하여 PaginationInfo 객체에 설정됩니다.
	 */

	public List<PushNoticeDetailDTO> selectNoticeList(String userId);
	/**
	 * 특정 사용자의 알림 목록을 조회합니다. (PUSH_NOTICE_TARGET.USER_ID 기준)
	 * kr.or.jsu.mybatis.mapper.PushNoticeMapper.xml의 selectPushNoticeListByTargetId 또는 selectNoticeList와 연결됩니다.
	 * @param userId 수신자 ID
	 * @return 알림 상세 정보 리스트
	 */

	public PushNoticeVO selectPushNotice(String pushId);
	/*
	 * 실행 단계: 특정 알림의 상세 정보(마스터 정보)가 필요할 때 호출됩니다.
	 * 역할: 특정 고유 ID(pushId)에 해당하는 알림 마스터 정보를 DB에서 조회합니다.
	 * 반환: 조회된 알림 마스터 정보가 담긴 PushNoticeVO 객체를 반환합니다.
	 */

	public PushNoticeDetailDTO selectPushNoticeDetail(@Param("pushId") String pushId, @Param("userId") String userId);
	/*
	 * 실행 단계: 특정 사용자가 알림 상세 페이지를 열 때 호출될 수 있습니다.
	 * 역할: 특정 알림 ID(pushId)와 특정 수신자 ID(userId)를 기준으로 상세 정보(수신 기록 포함)를 조회합니다.
	 * 반환: 조회된 상세 정보가 담긴 PushNoticeDetailDTO 객체를 반환합니다.
	 */


	public List<PushNoticeDetailDTO> selectPushNoticeListByTargetId(@Param("userId") String userId);
	/*
	 * 실행 단계: 로그인한 사용자(userId)의 받은 알림 목록을 보여줄 때 호출됩니다.
	 * 역할: 특정 수신자 ID(userId)에게 전송된 모든 알림 목록을 DB에서 조회합니다.
	 * 반환: 해당 사용자가 받은 알림 목록을 List<PushNoticeDetailDTO> 형태로 반환합니다.
	 */


	public int selectUnreadNoticeCount(@Param("userId") String userId);
	/*
	 * 실행 단계: 사용자 인터페이스(예: 헤더의 알림 아이콘)에 읽지 않은 알림 개수를 표시할 때 호출됩니다.
	 * 역할: 특정 사용자 ID(userId)에 대해 DB의 수신 대상 테이블에서 '읽지 않음' 상태인 알림의 총 개수를 조회합니다.
	 * 반환: 읽지 않은 알림 개수(정수형)를 반환합니다.
	 */

	public int updatePushNotice(PushNoticeVO pushNotice);
	/*
	 * 실행 단계: 알림 관리자가 알림의 내용을 수정할 때 호출됩니다.
	 * 역할: 특정 알림 마스터 정보(PushNoticeVO)를 DB에서 UPDATE합니다.
	 * 반환: UPDATE된 행의 수를 반환합니다.
	 */

	public int deletePushNotice(String pushId);
	/*
	 * 실행 단계: 알림 관리자가 알림을 삭제할 때 호출됩니다.
	 * 역할: 특정 알림 ID(pushId)에 해당하는 알림 마스터 정보 및 관련 수신 대상 정보를 DB에서 DELETE합니다.
	 * 반환: DELETE된 행의 수를 반환합니다.
	 */

	public int updateCheckAt(@Param("pushId") String pushId, @Param("userId") String userId);
	/*
	 * 실행 단계: 사용자가 특정 알림을 클릭하거나 확인했을 때 호출됩니다.
	 * 역할: 특정 알림 ID(pushId)와 사용자 ID(userId)에 해당하는 수신 기록을 찾아 '읽음' 상태로 UPDATE합니다.
	 * 반환: UPDATE된 행의 수(1)를 반환합니다.
	 */

	public int insertPushNoticeTargets(@Param("targets") List<PushNoticeTargetVO> targets);
	/*
	 * 실행 단계: 알림 발송 로직(Service의 processNotificationBatch)에서 호출됩니다.
	 * 역할: 여러 수신자 정보(targets)를 **배치(Batch)** 형태로 DB의 알림 수신 대상 테이블에 **일괄 INSERT**합니다.
	 * 반환: INSERT된 총 행의 수(총 수신자 수)를 반환합니다.
	 */

	public String findUserIdByUserDetail(Map<String, String> params);
	/*
	 * 실행 단계: Service에서 수동 입력 또는 엑셀 목록의 유효성을 검사할 때 호출됩니다.
	 * 역할: 이름(lastName, firstName)과 전화번호(mobileNo)를 조건으로 사용하여, 이와 일치하는 **시스템 내부의 userId를 DB에서 조회**합니다.
	 * 반환: 일치하는 사용자의 userId(문자열)를 반환합니다. (일치하는 사용자가 없으면 null 반환)
	 */

	public int selectNotificationHistoryCount(PaginationInfo<Map<String, Object>> pagingInfo);
	/*
	 * 역할: 알림 발신 내역의 총 개수를 조회하여 페이징 처리를 지원합니다. (Service에서 사용)
	 */
}