package kr.or.jsu.lms.api;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.core.utils.log.PrettyPrint;
import kr.or.jsu.dto.request.lms.lecture.assign.ScheduleAssignReq;
import kr.or.jsu.dto.response.lms.lecture.schedule.BuildingWithClassroomResp;
import kr.or.jsu.dto.response.lms.lecture.schedule.UnAssignedLectureResp;
import kr.or.jsu.lms.staff.service.staffLectureRoom.StaffLectureRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 28.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 28.     	송태호	          최초 생성, 강의개설신청 로직 일부 분리하여 가져옴
 *
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/lms/api/v1/staff")
@RequiredArgsConstructor
public class LMSStaffRESTController {
	
	private final StaffLectureRoomService scheduleSerive;
	
	/**
	 * 강의실 배정 작업이 필요한 강의 숫자만 가져옵니다.
	 * 
	 * @return
	 */
	@GetMapping("/lecture/schedule/unassigned/{yeartermCd}/count")
	public Map<String, Integer> readUnassignedLectureCount(
		@PathVariable String yeartermCd
	) {
		List<UnAssignedLectureResp> list = scheduleSerive.unAssignedLectureList(yeartermCd);
		
		return Map.of("lecture", list.size());
	}
	
	/**
	 * 강의실 배정 작업이 필요한 강의들을 가져옵니다.
	 * 
	 * @return
	 */
	@GetMapping("/lecture/schedule/unassigned/{yeartermCd}")
	public List<UnAssignedLectureResp> readUnassignedLectureList(
		@PathVariable String yeartermCd
	) {
		return scheduleSerive.unAssignedLectureList(yeartermCd);
	}
		
	/**
	 * 사용 가능한 강의실이 있는 건물과, 그 건물의 강의실 목록을 가져옵니다.
	 * 
	 * @return
	 */
	@GetMapping("/lecture/schedule/available-classroom/{yeartermCd}")
	public List<BuildingWithClassroomResp> readAvailableClassroom(
		@PathVariable String yeartermCd
	) {
		return scheduleSerive.readAllBuildingHavingClassroom(yeartermCd);
	}
	
	/**
	 * 특정 강의실의 특정 학기에 대한 시간표를 가져옵니다.
	 * 
	 * @param yeartermCd
	 * @return
	 */
	@GetMapping("/lecture/schedule/classroom/{placeCd}/{yeartermCd}")
	public List<LectureScheduleResp> showMySchedule(
		@PathVariable String placeCd
		, @PathVariable String yeartermCd
	) {
		return scheduleSerive.readClassroomSchedule(yeartermCd, placeCd);
	}
	
	
	/**
	 * 강의 시간표를 일괄배정합니다.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/lecture/schedule/assign")
	public ResponseEntity<Void> assignSchedule(
		@RequestBody List<ScheduleAssignReq> request
	){
		
		log.info("강의 시간표 일괄배정 : {}", PrettyPrint.pretty(request));
		
		scheduleSerive.createClassSchedule(request);
		
		return ResponseEntity.noContent().build();
	}
	
	
	
}