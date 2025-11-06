package kr.or.jsu.dto;

import lombok.Data;

@Data
public class LctRoomAssignDetailDTO {
	private String assignId;            // 배정 ID (시퀀스 사용)
    private String lectCd;              // 강의 코드 (LctApplyInfoDTO.subjectCd)
    private String roomCd;              // 강의실 코드 (LctApplyInfoDTO.assignedRoomId)
    private String dayCd;               // 요일 코드 (LctApplyInfoDTO.assignedDay)
    private String timeblockCd;         // 시간 블록 코드 (LctApplyInfoDTO.assignedTimeblock)
    private String yeartermCd;          // 연/학기 코드
    
    // 기타 필요한 정보 (예: 최종 승인 결재 ID)
    private String approveId;           
}