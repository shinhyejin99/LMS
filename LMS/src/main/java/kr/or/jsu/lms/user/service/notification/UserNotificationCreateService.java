package kr.or.jsu.lms.user.service.notification;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.dto.PushNoticeDetailDTO;


public interface UserNotificationCreateService {

	 /**
     * 알림 생성, DB 저장 및 실시간 푸시 전송을 처리.
     * PUSH_NOTICE, PUSH_NOTICE_TARGET 등록은 트랜잭션.
     * @param senderId 발신자 (직원) ID
     * @param targetStaffId 수신자 (다른 직원) ID
     * @param title 알림 제목
     * @param content 알림 내용
	 * @return
     */
    PushNoticeDetailDTO createAndSendNotification(String senderId, String targetStaffId, String title, String content);

    /**
     * 엑셀 파일을 읽어 수신자 목록을 확보하고 알림을 일괄 전송.
     * @param notificationDTO 발신자 ID, 제목, 내용 등 메타데이터
     * @param excelFile 업로드된 엑셀 파일
     * @return 성공적으로 전송된 알림 건수
     */
    int createAndSendBatchNotificationByExcel(PushNoticeDetailDTO notificationDTO, MultipartFile excelFile);

    /**
     * 발신자 ID(userId)를 사용하여 해당 직원의 소속 부서명을 조회
     * @param senderId 직원 ID
     * @return 부서명 (String)
     */
    String readSenderDeptName(String senderId);

    /**
     * 업무 완료 시점에 자동으로 일반 사용자에게 알림을 발송하고 DB에 기록
     * 발신자는 시스템 또는 처리 직원의 정보로 자동 설정됩니다.
     * @param userId 수신자 (일반 사용자/학생) ID
     * @param senderDeptName 발신 주체
     * @param title 알림 제목 (예: "증명서 발급 완료")
     * @param content 알림 내용
     */
    void sendAutoNotificationToUser(String receiverId, String senderDeptName, String title, String content);


    /**
     * 폼에서 수동으로 입력된 수신자 목록(이름/전화번호)을 받아 알림을 일괄 생성하고 전송합니다.
     * 이 메서드 내에서 이름과 전화번호를 기반으로 수신자 ID를 조회해야 합니다.
     * @param individualRecipients 이름, 전화번호, 메시지 등을 포함하는 DTO 목록 (sender, pushTitle, pushDetail도 포함)
     * @param content 알림 메시지 내용
     * @return 성공적으로 전송된 알림 건수
     */
    int createAndSendIndividualNotificationBatch(List<PushNoticeDetailDTO> individualRecipients, String content);

    /**
     * [통합] 시스템/비즈니스 로직에서 특정 사용자에게 알림을 자동으로 발송하는 공통 메서드.
     * @param request 알림 요청 정보 (수신자 ID, 제목, 내용 등)
     */
    void sendAutoNotification(AutoNotificationRequest request);

    /**
	 * 그룹 대상(전체, 학년별, 학과별)에 따라 수신자 목록을 조회하고 알림을 일괄 생성하여 전송합니다.
	 * @param baseNotificationDTO 발신자 정보, 제목, 내용을 포함하는 기본 DTO
	 * @param targetType 수신 대상 그룹 타입 (ALL, GRADE, DEPARTMENT)
	 * @param targetCode 수신 대상 그룹 코드 (학년 코드, 학과 코드 등. ALL인 경우 null)
	 * @return 성공적으로 전송된 알림 건수
	 */
	int createAndSendGroupNotification(PushNoticeDetailDTO baseNotificationDTO, String targetType, String targetCode, String gradeCode);

    /**
     * **[추가]** 그룹 대상 조건에 맞는 학생의 총 인원수를 조회합니다.
     * 이 메서드는 알림 발송 전 확인 모달에 사용됩니다.
     * @param targetType 수신 대상 그룹 타입 (ALL, GRADE, DEPARTMENT)
     * @param targetCode 수신 대상 코드 (학년 코드 또는 학과 코드)
     * @param gradeCode 학년 코드 (DEPARTMENT 타입에서 학과와 함께 사용)
     * @return 해당 조건에 맞는 학생 수
     */
    int countGroupNotificationRecipients(String targetType, String targetCode, String gradeCode);
}

