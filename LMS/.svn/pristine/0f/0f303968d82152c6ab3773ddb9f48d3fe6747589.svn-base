package kr.or.jsu.lms.student.service.academicChange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.common.service.LMSFilesService;
import kr.or.jsu.core.dto.info.FileDetailInfo;
import kr.or.jsu.core.dto.info.UnivDeptInfo;
import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.core.utils.enums.FileUploadDirectory;
import kr.or.jsu.dto.AffilApplyInfoDTO;
import kr.or.jsu.dto.AffilApplyRequestDTO;
import kr.or.jsu.dto.AffilApplyResponseDTO;
import kr.or.jsu.dto.StudentDetailDTO;
import kr.or.jsu.lms.user.service.notification.UserNotificationCreateService;
import kr.or.jsu.mybatis.mapper.ApprovalMapper;
import kr.or.jsu.mybatis.mapper.DeptConditionMapper;
import kr.or.jsu.mybatis.mapper.RecordApplyMapper;
import kr.or.jsu.mybatis.mapper.StuExtraMajorMapper;
import kr.or.jsu.mybatis.mapper.StudentMapper;
import kr.or.jsu.mybatis.mapper.UnivAffilApplyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 소속변경 신청 서비스 구현체
 *
 * @author 김수현
 * @since 2025. 9. 25.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	정태일	          최초 생성
 *  2025. 10. 14.		김수현			소속변경 처리 기능 추가
 *	2025. 10. 25.		김수현			지도교수에게 알림 가도록 추가
 *      </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StuAffilApplyServiceImpl implements StuAffilApplyService {

	private final UnivAffilApplyMapper affilMapper;
	private final StudentMapper studentMapper;
	private final DeptConditionMapper deptCondiMapper;
	private final StuExtraMajorMapper extraMajorMapper;
	private final ApprovalMapper approvalMapper;
	private final RecordApplyMapper recordApplyMapper;
	private final LMSFilesService filesService;
	private final UserNotificationCreateService notificationService;

	// 승인 타입 코드 => 소속변경 코드
	private static final String ApplyTypeCd = "UNIV_AFFIL_CHANGE";

	/**
	 * 소속변경 신청 처리
	 */
	@Override
	public String applyAffil(AffilApplyRequestDTO requestDTO, String userId) {
		String studentNo = requestDTO.getStudentNo();
		String univDeptCd = requestDTO.getUnivDeptCd();
		String affilChangeCd = requestDTO.getAffilChangeCd();

		log.info("===== 소속변경 신청 시작 =====");
		log.info("studentNo(학번): {}", studentNo);
		log.info("univDeptCd(현재 학과코드): {}", univDeptCd);
		log.info("affilChangeCd(변경코드): {}", affilChangeCd);
		log.info("targetDeptCd(원하는 학과): {}", requestDTO.getTargetDeptCd());

		// 1. 기본 검증
		validateBasic(requestDTO);

		// 1-1. 파일 첨부 필수 검증 추가
	    if (requestDTO.getAttachFiles() == null || requestDTO.getAttachFiles().isEmpty()
	        || requestDTO.getAttachFiles().get(0).isEmpty()) {
	        String typeName = getAffilChangeTypeName(affilChangeCd);
	        throw new IllegalArgumentException(typeName + " 신청은 증빙 서류 첨부가 필수입니다.");
	    }

		// 2. 타입별 검증
		validateByType(studentNo, univDeptCd, affilChangeCd, requestDTO.getTargetDeptCd());

		// 3. 승인 라인 생성
		String firstApprovalId = createApprovalLine(studentNo, userId, univDeptCd, affilChangeCd);

		// 4. 첨부파일 처리
		String fileId = processFiles(requestDTO, userId);

		// 5. 신청 정보 생성
		AffilApplyInfoDTO applyInfo = new AffilApplyInfoDTO();
        applyInfo.setStudentNo(studentNo);
        applyInfo.setTargetDeptCd(requestDTO.getTargetDeptCd());
        applyInfo.setAffilChangeCd(affilChangeCd);
        applyInfo.setApplyReason(requestDTO.getApplyReason());
        applyInfo.setAttachFileId(fileId);
        applyInfo.setApprovalLine(firstApprovalId);

		// 6. DB에 저장
        affilMapper.insertAffilApply(applyInfo);

		// 7. 파일 사용 상태
        if (fileId != null) {
            filesService.changeUsingStatus(fileId, true);
        }

        String applyId = applyInfo.getApplyId();

        log.info("소속변경 신청 완료 - type: {}, applyId: {}, studentNo: {}",
            affilChangeCd, applyId, studentNo);

        // 8. 지도교수에게 알림 전송
        try {
            sendAffilApplyNotification(studentNo, affilChangeCd, applyId, requestDTO.getTargetDeptCd());
        } catch (Exception e) {
            log.error("지도교수 알림 전송 실패 - studentNo: {}, applyId: {}", studentNo, applyId, e);
            // 알림 전송 실패해도 신청은 정상 처리
        }

        return applyId;
	}

	/**
	 * 소속변경 신청 취소
	 */
	@Override
	@Transactional
	public void cancelApply(String applyId, String studentNo) {

		// 1. 신청 정보 조회
        AffilApplyResponseDTO apply = affilMapper.selectApplyDetail(applyId);

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

        // 4. 첨부파일 비활성화
        String fileId = apply.getAttachFileId();
        if (fileId != null) {
            filesService.changeUsingStatus(fileId, false);
        }

        // 5. 신청 삭제 + 승인 테이블에서도 데이터 삭제
        int deleted = affilMapper.deleteApply(applyId);

        String approvalLineId = apply.getApprovalLine(); // 신청ID
        int approveDeleted  = approvalMapper.deleteApproval(approvalLineId);

        if (deleted == 0 && approveDeleted == 0) {
            throw new RuntimeException("신청 취소에 실패했습니다.");
        }

        log.info("소속변경 신청 취소 완료 - applyId: {}, studentNo: {}", applyId, studentNo);

	}

	/**
	 * 전과 가능한 학과 목록 조회
	 */
	@Override
	public List<UnivDeptInfo> getTransferableDepts(String studentNo) {
		return affilMapper.selectSameCollegeDepts(studentNo);
	}

	/**
	 * 복수전공/부전공 가능한 학과 목록 조회
	 */
	@Override
	public List<UnivDeptInfo> getAllDepts(String studentNo) {
		return affilMapper.selectAllDepts(studentNo);
	}

	// ====================
	// Private 메서드
	// ====================

	/**
	 * 기본 검증
	 *
	 * @param request
	 */
	private void validateBasic(AffilApplyRequestDTO request) {
		if (request.getAffilChangeCd() == null) {
			throw new RuntimeException("소속변경 타입을 선택해주세요.");
		}

		if (request.getTargetDeptCd() == null) {
			throw new RuntimeException("목표 학과를 선택해주세요.");
		}

		if (request.getApplyReason() == null || request.getApplyReason().trim().isEmpty()) {
			throw new RuntimeException("신청사유를 입력해주세요.");
		}
	}

	// 타입별 검증
	private void validateByType(String studentNo, String currentDeptCd, String affilChangeCd, String targetDeptCd) {

		switch (affilChangeCd) { // 신청한 소속변경 코드
			case "MJ_TRF": { // 전과
				// 전과 신청 검증
				validateTransfer(studentNo, currentDeptCd, targetDeptCd);
				break;
			}
			case "MJ_DBL": { // 복수전공
				// 복수전공 신청 검증
				validateDouble(studentNo, currentDeptCd, targetDeptCd);
				break;
			}
			case "MJ_SUB": { // 부전공
				// 부전공 신청 검증
				validateMinor(studentNo, currentDeptCd, targetDeptCd);
				break;
			}
			default:
                throw new RuntimeException("올바르지 않은 소속변경 타입입니다.");
		}

		// 중복 신청 확인
		int count = affilMapper.countPendingApply(studentNo, affilChangeCd);
		if(count > 0) {
			throw new RuntimeException("이미 신청한 내역이 있습니다.");
		}
	}

	// 전과 신청 검증
	private void validateTransfer(String studentNo, String currentDeptCd, String targetDeptCd) {
		// 1. 학년 확인 (1학년, 4학년 불가)
        String gradeCd = studentMapper.selectGradeCd(studentNo);

        if ("1ST".equals(gradeCd)) {
            throw new RuntimeException("1학년은 전과 신청이 불가능합니다.");
        }

        if ("4TH".equals(gradeCd)) {
            throw new RuntimeException("4학년은 전과 신청이 불가능합니다.");
        }
         // 2. 같은 단과대학인지 확인
        String currentCollegeCd = studentMapper.selectCollegeCd(studentNo);

        // 목표 학과의 단과대학 조회
        String targetCollegeCd = affilMapper.selectSameCollegeDepts(studentNo).stream()
            .filter(dept -> dept.getUnivDeptCd().equals(targetDeptCd))
            .findFirst()
            .map(UnivDeptInfo::getCollegeCd)
            .orElse(null);

        if (targetCollegeCd == null || !currentCollegeCd.equals(targetCollegeCd)) {
            throw new RuntimeException("전과는 같은 단과대학 내에서만 가능합니다.");
        }

        // 3. 평균 학점 확인
        validateGPA(studentNo, currentDeptCd, "전과");
	}

	// 복수전공 신청 검증
    private void validateDouble(String studentNo, String currentDeptCd, String targetDeptCd) {
        // 1. 이미 복수전공이 있는지 확인
        boolean hasDouble = extraMajorMapper.hasDoubleMajor(studentNo);
        if (hasDouble) {
            throw new RuntimeException("이미 복수전공을 이수 중입니다.");
        }

        // 2. 평균 학점 확인
        validateGPA(studentNo, currentDeptCd, "복수전공");
    }
	// 부전공 신청 검증
    private void validateMinor(String studentNo, String currentDeptCd, String targetDeptCd) {
    	// 1. 이미 복수전공이 있는지 확인
    	boolean hasDouble = extraMajorMapper.hasDoubleMajor(studentNo);
    	if(hasDouble) {
    		throw new RuntimeException("복수전공 이수 중에는 부전공 신청이 불가능합니다.");
    	}

    	// 2. 이미 부전공이 있는지 확인
    	boolean hasMinor = extraMajorMapper.hasMinorMajor(studentNo);
    	if(hasMinor) {
    		throw new RuntimeException("이미 부전공을 이수 중입니다.");
    	}

    	// 3. 평균 학점 확인
    	validateGPA(studentNo, currentDeptCd, "부전공");
    }

	// 평균 학점 검증
    private void validateGPA(String studentNo, String deptCd, String typeName) {
    	// 1. 학생 평균 학점 조회
        Double gpa = studentMapper.selectStudentGPA(studentNo);

        if (gpa == null) {
            gpa = 0.0;
        }

        // 2. 학과별 최소 학점 조건 조회
        String minGpaObj = deptCondiMapper.selectConditionValue(deptCd, "MIN_GPA_REQ");
        Double minGpa = Double.parseDouble(minGpaObj);

        // 3. 검증
        if (gpa < minGpa) {
            throw new RuntimeException(
                String.format("%s 신청은 평균 학점 %.1f 이상이어야 합니다. (현재: %.2f)",
                    typeName, minGpa, gpa)
            );
        }

        log.info("{} 신청 학점 검증 통과 - studentNo: {}, GPA: {}, 최소: {}",
            typeName, studentNo, gpa, minGpa);
    }

    /**
     * 승인 라인 생성 => 지도교수 가져오는 거는 RecordApplyMapper에 있음
     * @param studentNo
     * @param userId
     * @param univDeptCd
     * @return
     */
    private String createApprovalLine(String studentNo, String userId, String univDeptCd, String affilChangeCd) {
    	// 1. 지도교수 user_id 조회
		String professorUserId = recordApplyMapper.selectProfessorUserId(studentNo);

		if(professorUserId == null) {
			throw new RuntimeException("지도교수 정보를 찾을 수 없습니다.");
		}

		// 2. 승인 테이블 데이터 insert
		Map<String, Object> firstApproval = new HashMap<>();
        firstApproval.put("PREV_APPROVE_ID", null); // 이전신청 ID
        firstApproval.put("USER_ID", professorUserId); // 승인자(지도교수) userId
        firstApproval.put("APPLICANT_USER_ID", userId); // 신청한 학생의 userId
        firstApproval.put("APPLY_TYPE_CD", affilChangeCd); // 신청한 문서 타입
        firstApproval.put("APPROVE_YNNULL", null); // 확인승인여부

        approvalMapper.insertApproval(firstApproval);
        String firstApprovalId = (String) firstApproval.get("APPROVE_ID");

		// 3. 승인 ID 반환
        return firstApprovalId;
    }

    /**
     * 첨부파일 처리
     * @param request
     * @param userId
     * @return
     */
    private String processFiles(AffilApplyRequestDTO request, String userId) {
        if (request.getAttachFiles() == null || request.getAttachFiles().isEmpty()) {
            return null;
        }

        String affilChangeCd = request.getAffilChangeCd();
        String subPath = "/" + affilChangeCd;

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
     * 소속변경 신청 시 지도교수에게 알림 전송
     *
     * @param studentNo 신청한 학생 학번
     * @param affilChangeCd 신청 유형 코드
     * @param applyId 신청 ID
     * @param targetDeptCd 목표 학과 코드
     */
    private void sendAffilApplyNotification(String studentNo, String affilChangeCd, String applyId, String targetDeptCd) {
        // 1. 학생 정보 조회 (지도교수 ID 포함)
        StudentDetailDTO studentDetail = studentMapper.selectStudentDetailInfo(studentNo);

        if (studentDetail == null) {
            log.warn("학생 정보 조회 실패 - studentNo: {}", studentNo);
            return;
        }

        String advisorUserId = studentDetail.getProfessorUserId();

        if (advisorUserId == null || advisorUserId.isEmpty()) {
            log.warn("지도교수 정보 없음 - studentNo: {}", studentNo);
            return;
        }

        // 2. 목표 학과명 조회
        String targetDeptName = affilMapper.selectAllDepts(studentNo).stream()
            .filter(dept -> dept.getUnivDeptCd().equals(targetDeptCd))
            .findFirst()
            .map(UnivDeptInfo::getUnivDeptName)
            .orElse("학과");

        // 3. 신청 유형별 메시지 생성
        String applyTypeName = getAffilChangeTypeName(affilChangeCd);
        String studentName = studentDetail.getLastName() + studentDetail.getFirstName();

        String title = "📋 " + applyTypeName + " 신청 알림";
        String content = String.format(
            "%s (%s) 학생이 %s로 %s 신청을 하였습니다. 확인이 필요합니다.",
            studentName,
            studentNo,
            targetDeptName,
            applyTypeName
        );

        // 4. 알림 전송
        AutoNotificationRequest alert = AutoNotificationRequest.builder()
            .receiverId(advisorUserId)
            .title(title)
            .content(content)
            .senderName("학사처")
            .pushUrl("/lms/professor/academic-change/status") // 교수용 신청 현황 페이지 URL
            .build();

        notificationService.sendAutoNotification(alert);

        log.info("지도교수 알림 전송 완료 - studentNo: {}, advisorUserId: {}, applyId: {}",
            studentNo, advisorUserId, applyId);
    }

    /**
     * 소속변경 코드를 한글명으로 변환
     *
     * @param affilChangeCd 소속변경 코드
     * @return 한글명
     */
    private String getAffilChangeTypeName(String affilChangeCd) {
        switch (affilChangeCd) {
            case "MJ_TRF":
                return "전과";
            case "MJ_DBL":
                return "복수전공";
            case "MJ_SUB":
                return "부전공";
            default:
                return "소속변경";
        }
    }
}
