package kr.or.jsu.lms.professor.service.approval;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import kr.or.jsu.core.dto.request.AutoNotificationRequest;
import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.lms.professor.approval.mapper.ProfApprovalMapper;
import kr.or.jsu.lms.user.service.notification.UserNotificationCreateService;
import kr.or.jsu.vo.ApprovalVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("profApprovalService")
@RequiredArgsConstructor
public class ProfApprovalServiceImpl implements ProfApprovalService {

    private final UserNotificationCreateService notificationService;

    @Resource(name = "profApprovalMapper")
    private ProfApprovalMapper profApprovalMapper;

    @Override
    public List<Map<String, Object>> readProfApprovalList(PaginationInfo<Map<String, Object>> pagingInfo) {
        int totalRecords = profApprovalMapper.selectProfApprovalCount(pagingInfo);
        pagingInfo.setTotalRecord(totalRecords);
        return profApprovalMapper.selectProfApprovalList(pagingInfo);
    }

    @Override
    public Map<String, Object> readProfApprovalDetail(String approveId) {
        return profApprovalMapper.selectProfApprovalDetail(approveId);
    }

    private String getApplyTypeName(String applyTypeCd) {
        return switch (applyTypeCd) {
            case "UNIV_RECORD_CHANGE" -> "학적변동 신청";
            case "UNIV_AFFIL_CHANGE" -> "소속변경 신청";
            default -> "신청 건";
        };
    }

    /** ✅ 결재 반려 처리 */
    @Override
    @Transactional
    public void rejectDocument(String approveId, String comments, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getRealUser().getUserId();

        ApprovalVO current = profApprovalMapper.selectApprovalById(approveId);
        if (current == null) throw new IllegalStateException("존재하지 않는 결재 문서입니다.");
        if (current.getApproveYnnull() != null) throw new IllegalStateException("이미 처리된 결재 건입니다.");
        if (!current.getUserId().equals(userId)) throw new IllegalStateException("결재 권한이 없습니다.");

        // DB update
        ApprovalVO vo = new ApprovalVO();
        vo.setApproveId(approveId);
        vo.setUserId(userId);
        vo.setComments(comments);
        profApprovalMapper.updateApprovalReject(vo);

        // 신청자 정보
        Map<String, Object> detail = profApprovalMapper.selectProfApprovalDetail(approveId);
        String applicantId = (String) detail.get("APPLICANTUSERID");
        String applyTypeCd = (String) detail.get("APPLYTYPECD");
        String applyTypeName = getApplyTypeName(applyTypeCd);

        // 상태 변경
        updateApplicationStatus(approveId, applyTypeCd, "REJECTED");

        // 알림
        String sender = userDetails.getRealUser().getLastName() + userDetails.getRealUser().getFirstName();
        sendProApproval(applicantId, applyTypeName, sender, false);

        // 지도교수에게도 반려 알림
        String supervisorId = profApprovalMapper.selectSupervisorIdByStudent(applicantId);
        if (supervisorId != null && !supervisorId.isEmpty()) {
            sendProApproval(supervisorId, applyTypeName + " - 담당 학생 반려 알림", sender, false);
        }
    }

    /** ✅ 공용 알림 메서드 */
    private void sendProApproval(String receiverId, String applyTypeName, String senderName, boolean isApproved) {
        String title = isApproved ? "✅ " + applyTypeName + " 승인 완료" : "❌ " + applyTypeName + " 반려 처리";
        String content = isApproved ?
                applyTypeName + "이(가) 승인되었습니다." :
                applyTypeName + "이(가) 반려되었습니다.";

        AutoNotificationRequest alert = AutoNotificationRequest.builder()
                .receiverId(receiverId)
                .title(title)
                .content(content)
                .senderName(senderName)
                .pushUrl("/lms/portal/certificate/history")
                .build();

        notificationService.sendAutoNotification(alert);
        log.info("📨 알림 발송 완료 → 수신자: {}, 제목: {}", receiverId, title);
    }

    /** ✅ 결재 승인 및 다음 결재자 전달 */
    @Override
    @Transactional
    public void approveAndForward(String approveId, String comments, Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String userId = user.getRealUser().getUserId();

        ApprovalVO current = profApprovalMapper.selectApprovalById(approveId);
        if (current == null) throw new IllegalStateException("존재하지 않는 결재 문서입니다.");
        if (current.getApproveYnnull() != null) throw new IllegalStateException("이미 처리된 결재 건입니다.");

        // 승인처리
        Map<String, Object> params = new HashMap<>();
        params.put("approveId", approveId);
        params.put("userId", userId);
        params.put("comments", comments);
        params.put("approveYnnull", "Y");
        profApprovalMapper.updateApprovalStatus(params);

        // 신청 정보
        Map<String, Object> detail = profApprovalMapper.selectProfApprovalDetail(approveId);
        String applicantId = (String) detail.get("APPLICANTUSERID");
        String deptCd = (String) detail.get("APPLICANTDEPTCD");
        String applyTypeCd = (String) detail.get("APPLYTYPECD");
        String applyTypeName = getApplyTypeName(applyTypeCd);

        // 다음 결재자 (학과장)
        Map<String, Object> deptHead = profApprovalMapper.findDepartmentHead(deptCd);

        boolean isFinal = false;
        if (deptHead != null && deptHead.get("USER_ID") != null) {
            String deptHeadId = (String) deptHead.get("USER_ID");

            if (!userId.equals(deptHeadId)) {
                // 지도교수 → 학과장으로 전달
                updateApplicationStatus(approveId, applyTypeCd, "IN_PROGRESS");

                ApprovalVO next = new ApprovalVO();
                next.setPrevApproveId(approveId);
                next.setUserId(deptHeadId);
                next.setApplicantUserId(applicantId);
                next.setApplyTypeCd(applyTypeCd);
                profApprovalMapper.insertNextApproval(next);

                log.info("📤 지도교수 → 학과장 전달 완료 ({})", deptHeadId);
                // 🔔 학과장에게 결재 요청 알림
                sendProApproval(deptHeadId, applyTypeName + " 결재 요청", "지도교수", false);

            } else {
                // 학과장 → 최종 승인
                updateApplicationStatus(approveId, applyTypeCd, "APPROVED");
                isFinal = true;
            }
        } else {
            // 학과장 없음 = 바로 승인 처리
            updateApplicationStatus(approveId, applyTypeCd, "APPROVED");
            isFinal = true;
        }

        // ✅ 최종 승인 시: 지도교수 & 학생에게 알림
        if (isFinal) {
            String sender = user.getRealUser().getLastName() + user.getRealUser().getFirstName();

            // 학생에게 알림
            sendProApproval(applicantId, applyTypeName, sender, true);

            // 지도교수 알림
            String supervisorId = profApprovalMapper.selectSupervisorIdByStudent(applicantId);
            if (supervisorId != null && !supervisorId.isEmpty()) {
                sendProApproval(supervisorId, applyTypeName + " - 담당 학생 승인 알림", sender, true);
            }

            log.info("✅ 학과장 최종 승인 → 지도교수 & 학생에게 알림 발송 완료");
        }
    }

    /** ✅ 상태 갱신 */
    private void updateApplicationStatus(String approveId, String applyTypeCd, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("approveId", approveId);
        params.put("applyStatusCd", status);

        if ("UNIV_RECORD_CHANGE".equals(applyTypeCd)) {
            profApprovalMapper.updateRecordApplyStatus(params);
        } else if ("UNIV_AFFIL_CHANGE".equals(applyTypeCd)) {
            profApprovalMapper.updateAffilApplyStatus(params);
        }
    }
}
