package kr.or.jsu.lms.staff.service.staffLectureRoom;

import java.util.List;
import java.util.Map;

import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.dto.LctApplyDetailDTO;
import kr.or.jsu.dto.PlaceDetailDTO;
import kr.or.jsu.dto.RoomScheduleDetailDTO;
import kr.or.jsu.dto.info.place.PlaceInfo;
import kr.or.jsu.dto.request.lms.lecture.assign.ScheduleAssignReq;
import kr.or.jsu.dto.response.lms.lecture.schedule.BuildingWithClassroomResp;
import kr.or.jsu.dto.response.lms.lecture.schedule.UnAssignedLectureResp;

/**
 * @author 신혜진
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	신혜진	          최초 생성(강의실)
 *  2025. 10. 30.     	송태호	          구상중
 *
 * </pre>
 */
public interface StaffLectureRoomService {
	
	/**
	 * 특정 학기에 대해, <br>
	 * 1. 시간표가 아예 없거나 <br>
	 * 2. 강의 시수가 부족한 <br>
	 * 강의에 대한 정보 리스트를 반환합니다.
	 * 
	 * @param realUser 요청한 직원 정보, 직원이 아니면 막아야 함.
	 * @param yearTermCd 학년도학기코드
	 * @return
	 */
	public List<UnAssignedLectureResp> unAssignedLectureList(
		String yearTermCd
	);
	
	/**
	 * 특정 학기 코드를 입력해서 <br>
	 * 사용중인 건물과 방에 한정하여, <br>
	 * "강의실"로 사용중인 방을 포함하는 건물과 건물 내의 모든 강의실, <br>
	 * 강의실의 사용률을 가져옵니다.
	 * 
	 * @return
	 */
	public List<BuildingWithClassroomResp> readAllBuildingHavingClassroom(
    	String yeartermCd
    );
	
	/**
	 * 특정 학년도학기의 강의실에 대한 시간표를 가져옵니다.
	 * 
	 * @param yeartermCd
	 * @param placeCd
	 * @return
	 */
	public List<LectureScheduleResp> readClassroomSchedule(
		String yeartermCd
		, String placeCd
	);
	
	/**
	 * 강의실 배정 요청을 받아, <br>
	 * 유효성 검사 후 배정합니다.
	 * 
	 * @param request
	 */
	public void createClassSchedule(
		List<ScheduleAssignReq> request
	);
	
	// 모든 강의실 목록 조회
    List<PlaceInfo> readAllRooms();

    //  강의실 상세 정보 조회
    PlaceDetailDTO readRoomDetail(String placeCd);

    //  강의실 등록
    void createNewRoom(PlaceDetailDTO placeDTO) throws RuntimeException;

    // 강의실 정보 수정
    void modifyRoom(PlaceDetailDTO placeDTO) throws RuntimeException;

    // 강의실 시간표 사용 현황 조회
    List<RoomScheduleDetailDTO> readRoomSchedule(String placeCd, String yearTermCd);

    // -------------------------------------------------------------
    // 💡 강의 배정 및 승인 로직 (신규 추가)
    // -------------------------------------------------------------

    /**
     * 강의실 시간표 충돌 여부 확인
     * @param yearTermCd 학년도학기 코드O
     * @param placeCd 교직원이 선택한 강의실 코드
     * @param timeblockCds 신청 강의의 희망 시간 블록 코드 목록
     * @return 충돌 발생 시 true, 충돌 없을 시 false
     */
    boolean checkTimeTableConflict(String yearTermCd, String placeCd, List<String> timeblockCds);

    /**
     * 강의실 배정 및 최종 승인 처리
     * 충돌 검사를 통과한 후, LECTURE 및 LCT_ROOM_SCHEDULE 테이블에 확정 정보를 기록하고,
     * 강의 개설 신청 상태를 최종 승인으로 변경합니다.
     * * @param lctApplyId 강의 개설 신청 ID
     * @param assignmentData 배정 확정 정보 (PlaceCd, TimeblockCds, YearTermCd 등 포함)
     * @throws RuntimeException 데이터 처리 실패 시
     */
    void approveLectureAssignment(String lctApplyId, Map<String, Object> assignmentData) throws RuntimeException;

    /**
     * 강의실 배정 반려 처리
     * 강의 개설 신청 상태를 '반려'로 변경하고 반려 사유 등을 기록합니다.
     * * @param lctApplyId 강의 개설 신청 ID
     * @param processData 반려 정보 (반려 사유 등 포함)
     * @throws RuntimeException 데이터 처리 실패 시
     */
    void rejectLectureAssignment(String lctApplyId, Map<String, Object> processData) throws RuntimeException;

    /**
     * 강의 배정 페이지에 필요한 강의 신청 상세 정보 및 관련 데이터를 조회합니다.
     * @param lctApplyId 강의 신청 ID
     * @return 강의 신청 상세 정보가 담긴 Map
     */
    LctApplyDetailDTO readLectureAssignmentInfo(String lctApplyId);

	void modifyLectureAssignmentInfo(Map<String, Object> paramMap);

	/**
     * 강의 신청에 대한 강의실 및 시간대 배정 정보를 저장합니다.
     * @param assignmentInfo lctApplyId, placeCd, timeblockCdsString 등이 포함된 Map
     * @return 저장 성공 여부 (true/false)
     */
	boolean saveAssignmentInfo(Map<String, Object> requestData);

	void deleteRoom(String placeCd);

	List<RoomScheduleDetailDTO> readRequiredLectureTimes(String lctApplyId);

}