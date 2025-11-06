package kr.or.jsu.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 강의실 시간표 상세 정보를 담는 DTO.
 *
 * @author 신혜진
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *    수정일      			수정자           수정내용
 *   -----------   	-------------    ---------------------------
 *   2025. 10. 17.     	신혜진	          최초 생성(강의실 시간표)
 * </pre>
 */

@Data
public class RoomScheduleDetailDTO {

    // 💡 오류 해결 필드: 강의실 배정 폼/스케줄 조회를 위한 ID
    private String lctApplyId;          // 강의 신청 ID (LCT_APPLY_INFO의 PK)

	private String lectCd; // 강의 코드 (LCT_OPEN_APPLY의 SUBJECT_CD)
    private String professorNo;// 담당 교수 교번
    private String yeartermCd; // 연/학기 코드
    private String lectureIndex; // 분반
    private int expectCap; // 수강 정원
    private String lectureGoal;// 강의 목표
    private String prereqSubject;// 선수 과목
    private String lectureName;
    private String professorName;

    // 배정된 정보 (PlaceDetailDTO와 RoomScheduleDetailDTO를 통합한 것으로 보임)
    private String roomCd; // 강의실 코드

    // 💡 스케줄 상세 필드 추가 (강의실 시간표 DTO의 핵심 정보)
    private String dayCd;               // 요일 코드 (예: MON, TUE)
    private String timeblockCd;         // 시간 블록 코드 (예: T01, T02)

    // 시스템 정보
    private String openStatusCd = "OPEN"; // 개설 상태 코드 (기본값 'OPEN')
    private LocalDateTime createAt;
}