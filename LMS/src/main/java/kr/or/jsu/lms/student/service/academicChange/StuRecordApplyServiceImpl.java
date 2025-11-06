package kr.or.jsu.lms.student.service.academicChange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.dto.RecordApplyInfoDTO;
import kr.or.jsu.dto.RecordApplyRequestDTO;
import kr.or.jsu.dto.RecordApplyResponseDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationCreateService;
import kr.or.jsu.mybatis.mapper.ApprovalMapper;
import kr.or.jsu.mybatis.mapper.RecordApplyMapper;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 학적변동(소속 변경 제외) 신청 서비스 구현체
 * 휴학/복학/졸업유예/자퇴
 * @author 정태일
 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *	2025. 10. 10.		김수현			메소드 추가
 *	2025. 10. 13.		김수현			중복 신청 방지 추가
 *	2025. 10. 25.		김수현			재학 변경 신청 시 지도교수에게 알림이 가도록 기능 추가
 * </pre>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StuRecordApplyServiceImpl implements StuRecordApplyService {

	private final RecordApplyMapper mapper;
	private final ApprovalMapper approvalMapper;
	private final LMSFilesService filesService;
	private final StudentMapper studentMapper;
	private final UserNotificationCreateService notificationService;

	private static final String ApplyTypeCd = "UNIV_RECORD_CHANGE"; // 학적변동 코드 common_sort_cd

	/**
	 * 학적변동 신청 처리 (공통)
	 */
	@Override
	@Transactional
	public String applyRecord(RecordApplyRequestDTO requestDTO, String userId) {
		String studentNo = requestDTO.getStudentNo();
		String univDeptCd = requestDTO.getUnivDeptCd();
		String recordChangeCd = requestDTO.getRecordChangeCd(); // 학적변동타입코드 (DROP/REST/RTRN/DEFR)

		//  디버깅 로그 추가
	    log.info("===== Service에서 받은 정보 =====");
	    log.info("studentNo: {}", studentNo);
	    log.info("recordChangeCd: {}", recordChangeCd);
	    log.info("==================================");

	    // 휴학 신청 시 파일 첨부 필수 검증 추가
	    if ("REST".equals(recordChangeCd)) {
	        String leaveType = requestDTO.getLeaveType();
	        List<MultipartFile> attachFiles = requestDTO.getAttachFiles();

	        // 군휴학, 질병휴학, 출산휴학은 파일 필수
	        if ("REST_MIL".equals(leaveType) || "REST_MED".equals(leaveType) || "REST_PARENT".equals(leaveType)) {
	            if (attachFiles == null || attachFiles.isEmpty() || attachFiles.get(0).isEmpty()) {
	                String typeName = getLeaveTypeName(leaveType);
	                throw new IllegalArgumentException(typeName + "은 증빙 서류 첨부가 필수입니다.");
	            }
	        }
	    }


		// 1. 기본 신청 가능 여부 검증(재학 상태, 중복 신청)
		validateApply(studentNo, recordChangeCd);

		// 2. 타입별 추가 검증 및 필수값 체크
		validateByType(requestDTO);

		// 3. 승인 라인 생성
		String firstApprovalId = createApprovalLine(studentNo, userId, univDeptCd);

		// 4. 첨부파일 처리
		String fileId = processFiles(requestDTO, userId);

		// 5. 신청 정보 생성
		RecordApplyInfoDTO applyInfo = new RecordApplyInfoDTO();
		applyInfo.setStudentNo(studentNo);

		// + recordChangeCd 설정
	    // 휴학인 경우: leaveType을 recordChangeCd로 사용 (REST_GEN, REST_MIL 등)
	    // 나머지: 그대로 사용 (DROP, RTRN, DEFR)
	    if ("REST".equals(recordChangeCd)) {
//	        applyInfo.setRecordChangeCd(requestDTO.getLeaveType()); // REST_GEN, REST_MIL 등
	    	String finalCode = requestDTO.getLeaveType();
	        log.info("휴학 신청 - 최종 저장 코드: {}", finalCode);
	        applyInfo.setRecordChangeCd(finalCode);
	    } else {
	        applyInfo.setRecordChangeCd(recordChangeCd);
	    }
		applyInfo.setApplyReason(requestDTO.getApplyReason());
		applyInfo.setAttachFileId(fileId);
		applyInfo.setApprovalLine(firstApprovalId);


		// 6. 타입별 disireTerm 설정
		setDisireTerm(applyInfo, requestDTO);

		// 7. DB에 INSERT
		mapper.insertRecordApply(applyInfo);

		// 8. 파일 사용 상태 활성화
		if (fileId != null) {
			filesService.changeUsingStatus(fileId, true);
		}

		String applyId = applyInfo.getApplyId();

		log.info("학적변동 신청 완료 - type: {}, applyId: {}, studentNo: {}",
			recordChangeCd, applyId, studentNo);

		// 지도교수에게 알림 전송 (마지막에 추가함!)
	    try {
	        sendRecordApplyNotification(studentNo, recordChangeCd, applyId);
	    } catch (Exception e) {
	        log.error("지도교수 알림 전송 실패 - studentNo: {}, applyId: {}", studentNo, applyId, e);
	        // 알림 전송 실패해도 신청은 정상 처리되도록 예외를 삼킴
	    }

		return applyId;
	}

	// 휴학 유형명 반환 헬퍼 메서드
	private String getLeaveTypeName(String leaveType) {
	    switch (leaveType) {
	        case "REST_MIL":
	            return "군입대 휴학";
	        case "REST_MED":
	            return "질병휴학";
	        case "REST_PARENT":
	            return "출산/육아휴학";
	        default:
	            return "해당 휴학";
	    }
	}


	// 승인 라인 생성
	/**
	 * @param studentNo
	 * @param univDeptCd
	 * @return 1번째 승인자의 approve_id
	 */
	private String createApprovalLine(String studentNo, String userId, String univDeptCd) {
		// 1. 지도교수 user_id 조회
		String professorUserId = mapper.selectProfessorUserId(studentNo);

		if(professorUserId == null) {
			throw new RuntimeException("지도교수 정보를 찾을 수 없습니다.");
		}

		// 2. 승인 테이블 데이터 insert
		Map<String, Object> firstApproval = new HashMap<>();
        firstApproval.put("PREV_APPROVE_ID", null); // 이전신청 ID
        firstApproval.put("USER_ID", professorUserId); // 승인자(지도교수) userId
        firstApproval.put("APPLICANT_USER_ID", userId); // 신청한 학생의 userId
        firstApproval.put("APPLY_TYPE_CD", ApplyTypeCd); // 신청한 문서 타입
        firstApproval.put("APPROVE_YNNULL", null); // 확인승인여부

        approvalMapper.insertApproval(firstApproval);
        String firstApprovalId = (String) firstApproval.get("APPROVE_ID");

		// 3. 승인 ID 반환
        return firstApprovalId;
	}



	/**
	 * 신청 취소
	 */
	@Override
	@Transactional
	public void cancelApply(String applyId, String studentNo) {
		// 1. 신청 정보 조회 (본인 확인용)
		RecordApplyResponseDTO apply = mapper.selectApplyDetail(applyId);

		if (apply == null) {
			throw new RuntimeException("존재하지 않는 신청입니다.");
		}

		// 2. 본인 확인
		if (!apply.getStudentNo().equals(studentNo)) {
			throw new RuntimeException("본인의 신청만 취소할 수 있습니다.");
		}

		// 3. PENDING 상태 확인
		if (!"PENDING".equals(apply.getApplyStatusCd())) {
			throw new RuntimeException("대기 상태의 신청만 취소할 수 있습니다.");
		}

		// 4. 첨부파일이 있으면 비활성화
		String fileId = apply.getAttachFileId();
		if (fileId != null) {
			filesService.changeUsingStatus(fileId, false);
		}

		// 5. 신청 삭제 + 승인 테이블에서도 데이터 삭제
		int deleted = mapper.deleteApply(applyId);

		String approvalLineId = apply.getApprovalLine(); // 신청ID
		int approveDeleted  = approvalMapper.deleteApproval(approvalLineId);

		if (deleted == 0 && approveDeleted == 0) {
			throw new RuntimeException("신청 취소에 실패했습니다.");
		}

		log.info("신청 취소 완료 - applyId: {}, studentNo: {}", applyId, studentNo);
	}

	// ====================
	// Private 메서드
	// ====================

	/**
	 * 기본 신청 가능 여부 검증
	 */
	private void validateApply(String studentNo, String recordChangeCd) {
		// 1. 학생의 현재 학적 상태 확인
		String status = mapper.selectStudentStatus(studentNo);

		//  디버깅 로그
	    log.info("===== validateApply 실행 =====");
	    log.info("studentNo 파라미터: [{}]", studentNo);
	    log.info("조회된 status: [{}]", status);
	    log.info("status == null? {}", status == null);
	    log.info("ENROLLED.equals(status)? {}", "ENROLLED".equals(status));
	    log.info("================================");

		// 타입별 학적 상태 검증
		switch (recordChangeCd) {
			case "DROP":   // 자퇴
			case "REST":   // 휴학
			case "DEFR":   // 졸업유예
				if (!"ENROLLED".equals(status)) {
					throw new RuntimeException("재학중인 학생만 신청할 수 있습니다.");
				}
				break;
			case "RTRN":   // 복학
				if (!status.startsWith("ON_LEAVE")) {  // ON_LEAVE, ON_LEAVE_MIL 등
					throw new RuntimeException("휴학중인 학생만 복학 신청할 수 있습니다.");
				}
				break;
		}

		// 2. 중복 신청 체크
		int count = mapper.countPendingApply(studentNo, recordChangeCd);

		if (count > 0) {
			throw new RuntimeException("이미 처리중인 신청이 있습니다.");
		}
	}

	/**
	 * 타입별 추가 검증 및 필수값 체크
	 */
	private void validateByType(RecordApplyRequestDTO request) {
		String recordChangeCd = request.getRecordChangeCd();

		switch (recordChangeCd) {
			case "DROP":  // 자퇴
				// 신청사유만 필수 (공통 필드)
				break;

			case "REST":  // 휴학
				if (request.getLeaveType() == null) {
	                throw new RuntimeException("휴학 종류를 선택해주세요.");
	            }
	            if (request.getLeaveStartTerm() == null) {
	                throw new RuntimeException("휴학 시작 학기를 선택해주세요.");
	            }

	            String leaveType = request.getLeaveType();
	            boolean hasFiles = request.getAttachFiles() != null
	                            && !request.getAttachFiles().isEmpty();

	            // 휴학 종류별 검증
	            switch (leaveType) {
	                case "REST_MIL":
	                    if (request.getMilitaryTypeCd() == null) {
	                        throw new RuntimeException("입대구분을 선택해주세요.");
	                    }
	                    if (request.getJoinAt() == null) {
	                        throw new RuntimeException("입영일을 입력해주세요.");
	                    }
	                    if (!hasFiles) {
	                        throw new RuntimeException("군입대 휴학은 입영통지서 제출이 필수입니다.");
	                    }
	                    break;

	                case "REST_MED":
	                    if (request.getLeaveDuration() == null) {
	                        throw new RuntimeException("휴학 기간을 선택해주세요.");
	                    }
	                    if (request.getLeaveDuration() < 1 || request.getLeaveDuration() > 2) {
	                        throw new RuntimeException("휴학 기간은 1학기 또는 2학기만 가능합니다.");
	                    }
	                    if (!hasFiles) {
	                        throw new RuntimeException("질병휴학은 의사 진단서(4주 이상 요양 필요) 제출이 필요합니다.");
	                    }
	                    break;

	                case "REST_PARENT":
	                case "REST_GEN":
	                    if (request.getLeaveDuration() == null) {
	                        throw new RuntimeException("휴학 기간을 선택해주세요.");
	                    }
	                    if (request.getLeaveDuration() < 1 || request.getLeaveDuration() > 2) {
	                        throw new RuntimeException("휴학 기간은 1학기 또는 2학기만 가능합니다.");
	                    }
	                    break;

	                default:
	                    throw new RuntimeException("올바르지 않은 휴학 종류입니다.");
	            }
	            break;

			case "RTRN":  // 복학
				if (request.getReturnTerm() == null) {
					throw new RuntimeException("복학 예정 학기를 선택해주세요.");
				}
				break;

			case "DEFR":  // 졸업유예
				if (request.getDeferTerm() == null) {
					throw new RuntimeException("희망 졸업 학기를 선택해주세요.");
				}

				// 졸업유예 상세 검증
	            validateDeferRequirement(request);
				break;

			default:
				throw new RuntimeException("올바르지 않은 신청 타입입니다.");
		}
	}

	/**
	 * 졸업유예 신청 요건 검증
	 */
	private void validateDeferRequirement(RecordApplyRequestDTO request) {
	    String studentNo = request.getStudentNo();

	    // 1. 학생 상세 정보 조회
	    StudentDetailDTO student = studentMapper.selectStudentDetailInfo(studentNo);

	    // 2. 4학년 확인
	    if (!"4TH".equals(student.getGradeCd())) {
	        throw new RuntimeException(
	            String.format("졸업유예는 4학년 학생만 신청 가능합니다. (현재: %s)",
	                student.getGradeName())
	        );
	    }

	    // 3. 총 이수 학점 확인
	    int totalCredit = studentMapper.selectTotalCredit(studentNo);
	    final int GRADUATION_MIN_CREDIT = 130;

	    if (totalCredit < GRADUATION_MIN_CREDIT) {
	        throw new RuntimeException(
	            String.format("졸업 학점이 부족합니다. (이수: %d학점 / 필요: %d학점)",
	                totalCredit, GRADUATION_MIN_CREDIT)
	        );
	    }

	    log.info("졸업유예 요건 충족 - studentNo: {}, 학점: {}", studentNo, totalCredit);
	}

	/**
	 * 첨부파일 처리
	 */
	private String processFiles(RecordApplyRequestDTO request, String userId) {

		// 첨부파일이 없으면 null 리턴
	    if (request.getAttachFiles() == null || request.getAttachFiles().isEmpty()) {
	        return null;
	    }

	    String recordChangeCd = request.getRecordChangeCd();
	    String subPath = "/" + recordChangeCd; // /DROP, /REST, /RTRN, /DEFR

	    try {
	        // 디스크 저장
	        List<FileDetailInfo> fileMetaDatas = filesService.saveAtDirectory(
	            request.getAttachFiles(),
	            FileUploadDirectory.DEVTEMP,
	            subPath
	        );

	        // 메타데이터 체크
	        if (fileMetaDatas == null || fileMetaDatas.isEmpty()) {
	            log.warn("파일 메타데이터가 비어있음 - studentNo: {}", request.getStudentNo());
	            return null;
	        }

	        // DB 저장
	        return filesService.saveAtDB(fileMetaDatas, userId, false);

	    } catch (Exception e) {
	        log.error("첨부파일 처리 중 오류 발생", e);
	        throw new RuntimeException("첨부파일 처리에 실패했습니다: " + e.getMessage());
	    }
	}

	/**
	 * 타입별 disireTerm 설정
	 */
	private void setDisireTerm(RecordApplyInfoDTO applyInfo, RecordApplyRequestDTO requestDTO) {
		String recordChangeCd = requestDTO.getRecordChangeCd();

		switch (recordChangeCd) {
			case "DROP":  // 자퇴
			case "REST":  // 휴학
				String leaveType = requestDTO.getLeaveType();

	            if ("REST_MIL".equals(leaveType)) {
	                // 군휴학 → 복무 개월 수
	                int months = getMonthsByMilitaryType(requestDTO.getMilitaryTypeCd());
	                applyInfo.setDisireTerm(String.valueOf(months));

	            } else if (requestDTO.getLeaveDuration() != null) {
	                // 일반/질병/출산육아 휴학 → 학기 수
	                applyInfo.setDisireTerm(requestDTO.getLeaveDuration().toString());

	            } else {
	                // 예외 상황
	                log.warn("휴학 신청인데 leaveDuration과 militaryType이 모두 없음 - studentNo: {}",
	                    requestDTO.getStudentNo());
	            }
	            break;
			case "RTRN":  // 복학
				applyInfo.setDisireTerm(requestDTO.getReturnTerm());
				break;
			case "DEFR":  // 졸업유예
				applyInfo.setDisireTerm(requestDTO.getDeferTerm());
				break;
		}
	}

	/**
	 * 병역코드별 복무 개월 수 조회
	 * @param militaryTypeCd 병역코드
	 * @return 복무 개월 수
	 */
	private int getMonthsByMilitaryType(String militaryTypeCd) {
	    switch (militaryTypeCd) {
	        case "ARMY": return 18;  // 육군
	        case "NAVY": return 20;  // 해군
	        case "AIRF": return 21;  // 공군
	        case "MARN": return 18;  // 해병대
	        case "PBLC": return 21;  // 사회복무요원
	        default: return 18;      // 기본값
	    }
	}

	/**
	 * 재학상태변경 신청 시 지도교수에게 알림 전송
	 *
	 * @param studentNo 신청한 학생 학번
	 * @param recordChangeCd 신청 유형 코드
	 * @param applyId 신청 ID
	 */
	private void sendRecordApplyNotification(String studentNo, String recordChangeCd, String applyId) {
	    // 1. 학생 정보 조회 (지도교수 ID 포함)
	    StudentDetailDTO studentDetail = studentMapper.selectStudentDetailInfo(studentNo);

	    if (studentDetail == null) {
	        log.warn("학생 정보 조회 실패 - studentNo: {}", studentNo);
	        return;
	    }

	    String advisorUserId = studentDetail.getProfessorUserId(); // 지도교수 ID

	    if (advisorUserId == null || advisorUserId.isEmpty()) {
	        log.warn("지도교수 정보 없음 - studentNo: {}", studentNo);
	        return;
	    }

	    // 2. 신청 유형별 메시지 생성
	    String applyTypeName = getRecordChangeTypeName(recordChangeCd);
	    String studentName = studentDetail.getLastName() + studentDetail.getFirstName();

	    String title = "📋 " + applyTypeName + " 신청 알림";
	    String content = String.format(
	        "%s (%s) 학생이 %s 신청을 하였습니다. 확인이 필요합니다.",
	        studentName,
	        studentNo,
	        applyTypeName
	    );

	    // 3. 알림 전송
	    AutoNotificationRequest alert = AutoNotificationRequest.builder()
	        .receiverId(advisorUserId)
	        .title(title)
	        .content(content)
	        .senderName("시스템")
	        .pushUrl("/lms/professor/academic-change/status") // 교수용 신청 현황 페이지 URL
	        .build();

	    notificationService.sendAutoNotification(alert);

	    log.info("지도교수 알림 전송 완료 - studentNo: {}, advisorId: {}, applyId: {}",
	        studentNo, advisorUserId, applyId);
	}

	/**
	 * 재학상태변경 코드를 한글명으로 변환
	 *
	 * @param recordChangeCd 재학상태변경 코드
	 * @return 한글명
	 */
	private String getRecordChangeTypeName(String recordChangeCd) {
	    switch (recordChangeCd) {
	        case "DROP":
	            return "자퇴";
	        case "REST":
	            return "휴학";
	        case "RTRN":
	            return "복학";
	        case "DEFR":
	            return "졸업유예";
	        default:
	            return "재학상태변경";
	    }
	}
}
