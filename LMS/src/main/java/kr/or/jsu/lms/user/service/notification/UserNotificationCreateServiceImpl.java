package kr.or.jsu.lms.user.service.notification;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.dto.PushNoticeDetailDTO;
import kr.or.jsu.mybatis.mapper.PushNoticeMapper;
import kr.or.jsu.mybatis.mapper.PushNoticeTargetMapper;
import kr.or.jsu.vo.PushNoticeTargetVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationCreateServiceImpl implements UserNotificationCreateService {

	private final PushNoticeMapper pushNoticeMapper;
	private final PushNoticeTargetMapper pushNoticeTargetMapper;
	private final SimpMessagingTemplate messagingTemplate;

	/*
	 * ─────────────────────────────────────────────────────────────── ✅ 1. 단일 알림 생성
	 * 및 DB 저장 (Controller에서 호출되어 웹소켓 전송)
	 * ───────────────────────────────────────────────────────────────
	 */
	@Transactional
	@Override
	public PushNoticeDetailDTO createAndSendNotification(String senderId, String targetStaffId, String title,
			String content) {

		// 1. 알림 기본 정보 DTO 생성 및 설정
		PushNoticeDetailDTO notice = new PushNoticeDetailDTO();
		notice.setSender(senderId);
		notice.setPushTitle(title);
		notice.setPushDetail("[" + title + "] " + content);
		notice.setPushUrl("/lms/user/notifications/list");

		// 2. [DB] 알림 마스터 정보 등록 (PushNotice 테이블)
		pushNoticeMapper.insertPushNotice(notice);

		// 3. 알림 수신 대상 DTO 생성 및 설정
		PushNoticeDetailDTO target = new PushNoticeDetailDTO();
		target.setPushId(notice.getPushId());
		target.setUserId(targetStaffId);

		// 4. [DB] 알림 수신 대상 정보 등록 (PushNoticeTarget 테이블)
		pushNoticeTargetMapper.insertPushNoticeTarget(target);

		// 💡 Controller 반환용 DTO에 수신자 ID와 최종 PushId 설정
		notice.setUserId(targetStaffId);

		final String DESTINATION = "/queue/notifications";

		// 웹소켓 페이로드에 필요한 정보를 복사
		PushNoticeDetailDTO websocketPayload = new PushNoticeDetailDTO();
		BeanUtils.copyProperties(notice, websocketPayload);
		websocketPayload.setUserId(targetStaffId); // 수신자 ID 설정

		// 🚨 단일 알림 전송 로직 (targetStaffId가 로그인 ID 형태라 가정)
		messagingTemplate.convertAndSendToUser(targetStaffId, DESTINATION, websocketPayload);
		log.debug("웹소켓 전송 완료: PushId: {}, Recipient: {}", notice.getPushId(), targetStaffId);
		log.info("DB에 단일 알림 저장 완료. PushId: {}, Recipient: {}", notice.getPushId(), targetStaffId);

		return notice;
	}

	/*
	 * ─────────────────────────────────────────────────────────────── ✅ 2. 발신자 ID로
	 * 부서명 조회 ───────────────────────────────────────────────────────────────
	 */
	@Override
	public String readSenderDeptName(String senderId) {
	    String deptName = pushNoticeTargetMapper.selectSenderDepartmentName(senderId);
	    return (deptName != null && !deptName.trim().isEmpty()) ? deptName : "소속 부서 없음"; // ⭐️ "소속 부서 없음" 반환 로직
	}
	/*
	 * ─────────────────────────────────────────────────────────────── ✅ 3. 엑셀 일괄 알림
	 * 전송 (Controller에서 호출됨)
	 * ───────────────────────────────────────────────────────────────
	 */
	@Transactional
	@Override
	public int createAndSendBatchNotificationByExcel(PushNoticeDetailDTO notificationDTO, MultipartFile excelFile) {
		if (excelFile == null || excelFile.isEmpty())
			return 0;

		List<PushNoticeTargetVO> targetList = new ArrayList<>();
		Set<String> uniqueRecipients = new HashSet<>();
		Set<String> finalUserIds = new HashSet<>();

		try (InputStream is = excelFile.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			final int FULL_NAME_COL = 0;
			final int MOBILE_NO_COL = 1;

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				String fullName = readCellValue(row.getCell(FULL_NAME_COL));
				String mobileNo = readCellValue(row.getCell(MOBILE_NO_COL));
				if (fullName.isEmpty() || mobileNo.isEmpty())
					continue;

				String cleanMobileNo = mobileNo.replaceAll("[^0-9]", "");
				String key = fullName.trim() + "|" + cleanMobileNo;

				if (!uniqueRecipients.add(key)) {
					log.warn("엑셀 중복 수신자 스킵: {}", fullName);
					continue;
				}

				String lastName = fullName.substring(0, 1);
				String firstName = fullName.length() > 1 ? fullName.substring(1).trim() : "";

				Map<String, String> params = Map.of("lastName", lastName, "firstName", firstName, "mobileNo",
						cleanMobileNo);

				String userId = pushNoticeMapper.findUserIdByUserDetail(params);

				if (userId != null && finalUserIds.add(userId)) {
					PushNoticeTargetVO target = new PushNoticeTargetVO();
					target.setUserId(userId);
					targetList.add(target);
				}
			}
		} catch (Exception e) {
			log.error("엑셀 파싱 오류", e);
			throw new RuntimeException("엑셀 파일 처리 실패", e);
		}

		if (targetList.isEmpty())
			return 0;
		return processNotificationBatch(notificationDTO, targetList);
	}

	/*
	 * ─────────────────────────────────────────────────────────────── ✅ 4. 수동 입력
	 * 수신자 일괄 처리 (Controller에서 호출됨)
	 * ───────────────────────────────────────────────────────────────
	 */
	@Transactional
	@Override
	public int createAndSendIndividualNotificationBatch(List<PushNoticeDetailDTO> individualRecipients,
			String content) {
		if (individualRecipients == null || individualRecipients.isEmpty())
			return 0;

		PushNoticeDetailDTO baseDTO = individualRecipients.get(0);
		baseDTO.setPushDetail(content);

		List<PushNoticeTargetVO> targetList = new ArrayList<>();
		Set<String> finalUserIds = new HashSet<>();

		log.info("수동 입력 수신자 수: {}", individualRecipients.size());

		for (PushNoticeDetailDTO recipient : individualRecipients) {
			String fullName = recipient.getRecipientName();
			String mobileNo = recipient.getMobileNo();

			if (!StringUtils.hasText(fullName) || !StringUtils.hasText(mobileNo)) {
				log.warn("수신자 정보 누락 → 스킵 ({} / {})", fullName, mobileNo);
				continue;
			}

			String cleanMobileNo = mobileNo.replaceAll("[^0-9]", "");
			String lastName = fullName.substring(0, 1);
			String firstName = fullName.length() > 1 ? fullName.substring(1).trim() : "";

			Map<String, String> params = Map.of("lastName", lastName, "firstName", firstName, "mobileNo",
					cleanMobileNo);

			String userId = pushNoticeMapper.findUserIdByUserDetail(params);

			if (userId != null && finalUserIds.add(userId)) {
				PushNoticeTargetVO target = new PushNoticeTargetVO();
				target.setUserId(userId);
				targetList.add(target);
				log.info("추가된 수신자 → {} ({})", fullName, userId);
			} else if (userId == null) {
				log.warn("수신자 조회 실패 → {} / {}", fullName, cleanMobileNo);
			}
		}

		if (targetList.isEmpty())
			return 0;
		return processNotificationBatch(baseDTO, targetList);
	}

	/*
	 * ─────────────────────────────────────────────────────────────── ✅ 5. 공통 처리 로직 (핵심 수정)
	 * ───────────────────────────────────────────────────────────────
	 */
	@Transactional
	private int processNotificationBatch(PushNoticeDetailDTO notificationDTO, List<PushNoticeTargetVO> targetList) {
	    // 1. 알림 마스터 DTO 설정 및 DB 저장
	    PushNoticeDetailDTO notice = new PushNoticeDetailDTO();
	    BeanUtils.copyProperties(notificationDTO, notice);
	    // ... (pushDetail, pushUrl 설정) ...
	    pushNoticeMapper.insertPushNotice(notice);
	    String pushId = notice.getPushId();

	    // 2. 수신 대상 VO에 PushId 설정 및 DB 일괄 등록
	    for (PushNoticeTargetVO target : targetList) {
	        target.setPushId(pushId);
	    }
	    int insertedCount = pushNoticeTargetMapper.insertPushNoticeTargets(targetList);
	    log.info("DB에 {}명의 수신자 등록 완료. PushId: {}", insertedCount, pushId);

	    // 실시간 전송 (웹소켓)
	    final String DESTINATION = "/queue/notifications";

	    // 클라이언트에 보낼 최종 알림 객체 준비
	    PushNoticeDetailDTO websocketPayload = new PushNoticeDetailDTO();
	    BeanUtils.copyProperties(notice, websocketPayload);
	    websocketPayload.setPushTitle(notificationDTO.getPushTitle());
	    websocketPayload.setPushDetail(notificationDTO.getPushDetail());

	    //  발신 부서명(예: "행정처")을 페이로드에 포함. (클라이언트에서 발신자 표시용)
	    // notificationDTO는 수동 입력 시 발신자 정보(senderDeptName)를 담고 있을 것으로 예상
	    websocketPayload.setSenderDeptName(notificationDTO.getSenderDeptName());

	    // 각 수신자에게 메시지 전송
	    for (PushNoticeTargetVO target : targetList) {
	        String dbUserId = target.getUserId();
	        // ⭐️ List<String> 대신 단일 String으로 최종 로그인 ID를 저장할 변수
	        String finalLoginId = null;
	        List<String> userNumbers; // Mapper에서 List<String>을 반환할 것으로 가정

	        // 직원 번호 조회
	        userNumbers = pushNoticeTargetMapper.findStaffNoByUserId(dbUserId);
	        if (userNumbers != null && !userNumbers.isEmpty()) {
	            finalLoginId = userNumbers.get(0); // ⭐️ 첫 번째 값 사용
	        }

	        // 직원이 아니면 교수 번호 조회
	        if (finalLoginId == null) {
	            userNumbers = pushNoticeTargetMapper.findProfNoByUserId(dbUserId);
	            if (userNumbers != null && !userNumbers.isEmpty()) {
	                finalLoginId = userNumbers.get(0); // ⭐️ 첫 번째 값 사용
	            }
	        }

	        // 교수도 아니면 학생 번호 조회
	        if (finalLoginId == null) {
	            userNumbers = pushNoticeTargetMapper.findStudentNoByUserId(dbUserId);
	            if (userNumbers != null && !userNumbers.isEmpty()) {
	                finalLoginId = userNumbers.get(0); // ⭐️ 첫 번째 값 사용
	            }
	        }

	        // 조회된 ID가 있는 경우에만 전송
	        if (finalLoginId != null) {
	            log.info("📢 웹소켓 전송 시도: Recipient(Login): {}, Payload: {}", finalLoginId, websocketPayload);

	            // ⭐️ List<String> 대신 단일 String 변수 finalLoginId 사용
	            messagingTemplate.convertAndSendToUser(finalLoginId, DESTINATION, websocketPayload);

	            log.debug("웹소켓 전송 성공: PushId: {}, Recipient(DB): {}, Recipient(Login): {}",
	                          pushId, dbUserId, finalLoginId);
	        } else {
	            log.warn("웹소켓 전송 실패: ID 변환 불가 또는 세션 없음. DB User ID: {}", dbUserId);
	        }
	    }
	    return insertedCount;
	}
	/**
     * **[추가]** 그룹 대상 조건에 맞는 학생의 총 인원수를 조회합니다.
     */
    @Override
    public int countGroupNotificationRecipients(String targetType, String targetCode, String gradeCode) {

        List<String> studentUserIds;

        // 1. 그룹 타입에 따라 수신 대상 User ID 목록 조회 (Mapper 호출)
        switch (targetType) {
            case "ALL":
                studentUserIds = pushNoticeTargetMapper.selectStudentUserIdsAll();
                log.info("인원수 조회: 전체 학생. {}명 조회됨.", studentUserIds.size());
                break;
            case "GRADE":
                if (!StringUtils.hasText(targetCode)) { // targetCode는 학년 코드
                    log.warn("인원수 조회 실패: 학년 코드 누락");
                    return 0;
                }
                studentUserIds = pushNoticeTargetMapper.selectStudentUserIdsByGrade(targetCode);
                log.info("인원수 조회: 학년 ({}). {}명 조회됨.", targetCode, studentUserIds.size());
                break;
            case "DEPARTMENT":
                // targetCode는 학과 코드, gradeCode도 필요
                if (!StringUtils.hasText(targetCode) || !StringUtils.hasText(gradeCode)) {
                    log.warn("인원수 조회 실패: 학과 또는 학년 코드 누락");
                    return 0;
                }
                studentUserIds = pushNoticeTargetMapper.selectStudentUserIdsByDepartment(targetCode, gradeCode);
                log.info("인원수 조회: 학과 ({}), 학년 ({}). {}명 조회됨.", targetCode, gradeCode, studentUserIds.size());
                break;
            default:
                log.warn("인원수 조회 실패: 알 수 없는 그룹 타입: {}", targetType);
                return 0;
        }

        return studentUserIds.size();
    }


	@Transactional
	@Override
	public int createAndSendGroupNotification(PushNoticeDetailDTO baseNotificationDTO, String targetType,
			String targetCode, String gradeCode) {

		List<String> studentUserIds;

		// 1. 그룹 타입에 따라 수신 대상 User ID 목록 조회 (countGroupNotificationRecipients 로직과 동일)
		// ⭐️ targetCode와 gradeCode의 의미가 Controller에서 정의한 대로 명확하게 전달되어야 합니다.
		switch (targetType) {
			case "ALL":
				studentUserIds = pushNoticeTargetMapper.selectStudentUserIdsAll();
				log.info("그룹 알림 대상: 전체 학생. {}명 조회됨.", studentUserIds.size());
				break;
			case "GRADE":
				if (!StringUtils.hasText(targetCode)) { // targetCode = gradeCode
					log.warn("학년 코드 누락: 그룹 알림 발송 중단");
					return 0;
				}
				studentUserIds = pushNoticeTargetMapper.selectStudentUserIdsByGrade(targetCode);
				log.info("그룹 알림 대상: 학년 ({}). {}명 조회됨.", targetCode, studentUserIds.size());
				break;
			case "DEPARTMENT":
				if (!StringUtils.hasText(targetCode) || !StringUtils.hasText(gradeCode)) { // targetCode = deptCode
					log.warn("학과 또는 학년 코드 누락: 그룹 알림 발송 중단");
					return 0;
				}
				studentUserIds = pushNoticeTargetMapper.selectStudentUserIdsByDepartment(targetCode, gradeCode);
				log.info("그룹 알림 대상: 학과 ({}), 학년 ({}). {}명 조회됨.", targetCode, gradeCode, studentUserIds.size());
				break;
			default:
				log.warn("알 수 없는 그룹 타입: {}. 알림 발송 중단.", targetType);
				return 0;
		}

		if (studentUserIds.isEmpty()) {
			log.info("조회된 알림 수신 대상이 없습니다.");
			return 0;
		}

		// 2. PushNoticeTargetVO 목록 생성
		List<PushNoticeTargetVO> targetList = new ArrayList<>();
		for (String userId : studentUserIds) {
			PushNoticeTargetVO target = new PushNoticeTargetVO();
			target.setUserId(userId);
			targetList.add(target);
		}

		// 3. 일괄 처리 로직 호출 (DB 저장 및 웹소켓 전송)
		try {
			return processNotificationBatch(baseNotificationDTO, targetList);
		} catch (Exception e) {
			log.error("그룹 알림 배치 처리 중 오류 발생 (PushId: {}): {}", baseNotificationDTO.getPushId(), e.getMessage(), e);
			throw new RuntimeException("그룹 알림 배치 처리 실패", e);
		}
	}

	/*
	 * ─────────────────────────────────────────────────────────────── ✅ 6. 자동 시스템
	 * 알림 발송 (순수 DB 저장만 남김)
	 * ───────────────────────────────────────────────────────────────
	 */
	@Transactional
	@Override
	public void sendAutoNotification(AutoNotificationRequest request) {
		// 1. 알림 기본 정보 DTO 설정
		PushNoticeDetailDTO notice = new PushNoticeDetailDTO();

		// AutoNotificationRequest의 senderName을 sender로 임시 사용하거나, 별도 SYSTEM ID 정의 필요
		notice.setSender(request.getSenderName() != null ? request.getSenderName() : "SYSTEM");
		notice.setPushTitle(request.getTitle());
		notice.setPushDetail(request.getContent());
		notice.setPushUrl(request.getPushUrl() != null ? request.getPushUrl() : "/lms/user/notifications/list");

		// 2. [DB] 알림 마스터 정보 등록
		pushNoticeMapper.insertPushNotice(notice);

		// 3. 알림 수신 대상 등록
		PushNoticeDetailDTO target = new PushNoticeDetailDTO();
		target.setPushId(notice.getPushId());
		target.setUserId(request.getReceiverId());

		// 4. [DB] 알림 수신 대상 정보 등록
		pushNoticeTargetMapper.insertPushNoticeTarget(target);

		log.info("DB에 자동 알림 저장 완료. PushId: {}, Recipient: {}", notice.getPushId(), request.getReceiverId());

		// 5. ✅ 실시간 전송 로직 추가
		final String DESTINATION = "/queue/notifications";
		final String dbUserId = request.getReceiverId(); // T_USERS.USER_ID (DB ID)

		// 클라이언트 전송용 페이로드 준비
		PushNoticeDetailDTO websocketPayload = new PushNoticeDetailDTO();
		BeanUtils.copyProperties(notice, websocketPayload);
		websocketPayload.setUserId(dbUserId); // DB User ID를 잠시 저장

		// ⭐️ DB User ID를 실제 로그인 ID(학번/교번/직원번호)로 변환
		String finalLoginId = null;
		List<String> userNumbers;

		// 직원 번호 조회
		userNumbers = pushNoticeTargetMapper.findStaffNoByUserId(dbUserId);
		if (userNumbers != null && !userNumbers.isEmpty()) {
			finalLoginId = userNumbers.get(0);
		}

		// 직원이 아니면 교수 번호 조회
		if (finalLoginId == null) {
			userNumbers = pushNoticeTargetMapper.findProfNoByUserId(dbUserId);
			if (userNumbers != null && !userNumbers.isEmpty()) {
				finalLoginId = userNumbers.get(0);
			}
		}

		// 교수도 아니면 학생 번호 조회
		if (finalLoginId == null) {
			userNumbers = pushNoticeTargetMapper.findStudentNoByUserId(dbUserId);
			if (userNumbers != null && !userNumbers.isEmpty()) {
				finalLoginId = userNumbers.get(0);
			}
		}

		// 조회된 ID가 있는 경우에만 전송
		if (finalLoginId != null) {
			log.info("📢 자동 알림 웹소켓 전송 시도: Recipient(Login): {}, Payload: {}", finalLoginId, websocketPayload);

			// 최종 로그인 ID를 사용하여 웹소켓 전송
			messagingTemplate.convertAndSendToUser(finalLoginId, DESTINATION, websocketPayload);

			log.debug("웹소켓 전송 성공: PushId: {}, Recipient(DB): {}, Recipient(Login): {}",
					notice.getPushId(), dbUserId, finalLoginId);
		} else {
			log.warn("자동 알림 웹소켓 전송 실패: ID 변환 불가 또는 세션 없음. DB User ID: {}", dbUserId);
			}
	}

	/*
	 * ─────────────────────────────────────────────────────────────── ✅ 7. 엑셀 Cell
	 * 안전 읽기 ───────────────────────────────────────────────────────────────
	 */
	private String readCellValue(Cell cell) {
		// ... (로직 유지)
		if (cell == null)
			return "";
		try {
			switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue().trim();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell))
					return cell.getDateCellValue().toString();
				double val = cell.getNumericCellValue();
				return (val == Math.floor(val)) ? String.valueOf((long) val) : String.valueOf(val);
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getStringCellValue().trim();
			default:
				return "";
			}
		} catch (Exception e) {
			log.warn("Cell 변환 오류: {}", e.getMessage());
			return "";
		}
	}




	@Override
	public void sendAutoNotificationToUser(String receiverId, String senderDeptName, String title, String content) {
		// TODO Auto-generated method stub

	}
}
