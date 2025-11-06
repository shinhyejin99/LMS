package kr.or.jsu.dto.info.approval;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "approveId")
@Data
public class ApprovalInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String approveId; // 대체키, 시퀀스
	private String prevApproveId; // 이전 승인ID
	private String applyTypeCd; // 뭐에 대한 승인인지? 타입 코드
	private String userId; // 승인자 ID
	private String applicantUserId; // 신청자 ID
	private String approveYnnull; // 승인 : 'Y', 반려 : 'N', 대기 : null
	private LocalDateTime approveAt; // 언제 처리했는가?
	private String comments; // 처리할 때 쓴 내용
	private String attachFileId; // 신청자가 첨부한 파일
}