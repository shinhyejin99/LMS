package kr.or.jsu.lms.staff.service.staffLectureRoom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.or.jsu.classroom.dto.db.LectureWithScheduleDTO;
import kr.or.jsu.classroom.dto.db.UnassignedLectureDTO;
import kr.or.jsu.classroom.dto.info.LctRoomScheduleInfo;
import kr.or.jsu.classroom.dto.response.lecture.LectureScheduleResp;
import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.core.utils.databasecache.DatabaseCache;
import kr.or.jsu.dto.LctApplyDetailDTO;
import kr.or.jsu.dto.PlaceDetailDTO;
import kr.or.jsu.dto.RoomScheduleDetailDTO;
import kr.or.jsu.dto.info.place.PlaceInfo;
import kr.or.jsu.dto.request.lms.lecture.assign.ScheduleAssignReq;
import kr.or.jsu.dto.response.lms.lecture.schedule.BuildingWithClassroomResp;
import kr.or.jsu.dto.response.lms.lecture.schedule.UnAssignedLectureResp;
import kr.or.jsu.mybatis.mapper.LctRoomScheduleMapper;
import kr.or.jsu.mybatis.mapper.LectureMapper;
import kr.or.jsu.mybatis.mapper.PlaceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffLectureRoomServiceImpl implements StaffLectureRoomService {

	private final PlaceMapper placeMapper; // PlaceMapper (강의실 관리용)
	private final LectureMapper lectureMapper;
    private final LctRoomScheduleMapper lctRoomScheduleMapper; // LctRoomScheduleMapper (강의/일정 관리용)
    
    private final DatabaseCache cache;
    
    /**
     * 시큐리티컨텍스트홀더에서 사용자 정보를 가져와서 <br>
     * 요청자가 교직원임이 확인되면 아무 일도 없고 <br>
     * 교직원이 아니면 예외를 발생시키는 메서드입니다.
     */
    private void checkStaffRole() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (auth != null && auth.isAuthenticated()) {
			Object principal = auth.getPrincipal();
			
			if (principal instanceof CustomUserDetails) {
				CustomUserDetails user = (CustomUserDetails) principal;
				if("ROLE_STAFF".equals(user.getRealUser().getUserType())) return;
			}
		}
		
		throw new RuntimeException("교직원만 사용 가능한 기능입니다.");
	}
    
    /**
	 * 특정 학기에 대해, <br>
	 * 1. 시간표가 아예 없거나 <br>
	 * 2. 강의 시수가 부족한 <br>
	 * 3. 강의 시수가 초과된 <br>
	 * 강의에 대한 정보 리스트를 반환합니다.
	 * 
	 * @param yearTermCd 학년도학기코드
	 * @return
	 */
    public List<UnAssignedLectureResp> unAssignedLectureList(
		String yearTermCd
	) {
    	// 1. 요청한 사람이 직원인지 검증
    	checkStaffRole();
    	
    	// 2. 배정된 시간표가 없거나, 시수가 부족하거나, 시수가 많은 강의 가져오기 
    	List<UnassignedLectureDTO> result = lectureMapper.selectUnassignedLectureList(yearTermCd);
    	
    	// 3. 응답용 객체로 변환
    	List<UnAssignedLectureResp> respList = result.stream().map(res -> {
    		
    		var resp = new UnAssignedLectureResp();
    		
    		var lectureInfo = res.getLectureInfo();
    		var subjectInfo = res.getSubjectInfo();
    		
    		// 3-1. 강의 정보 옮겨담기
    		BeanUtils.copyProperties(lectureInfo, resp);
    		// 3-2. 과목 정보 옮겨담기
    		BeanUtils.copyProperties(subjectInfo, resp);
    		// 3-3. 시간표 배정이 되어있는 경우 이미 배정해둔 시간표 정보도.
    		resp.setScheduledSlots(res.getScheduledSlots());
    		
    		if(res.getScheduledSlots() != 0) {
    			String lectureId = res.getLectureInfo().getLectureId();
    			
    			List<LectureWithScheduleDTO> scheduleList = lectureMapper.selectScheduleListJson(List.of(lectureId));
    			String scheduleJson = scheduleList.get(0).getScheduleJson();
    			resp.setScheduleJson(scheduleJson);
    		}
    		
    		// 3-4. DB캐시로 코드 네임으로 변경
    		resp.setProfessorName(cache.getUserName(lectureInfo.getProfessorNo()));
    		resp.setUnivDeptName(cache.getUnivDeptName(subjectInfo.getUnivDeptCd()));
    		resp.setCompletionName(cache.getCodeName(subjectInfo.getCompletionCd()));
    		resp.setSubjectTypeName(cache.getCodeName(subjectInfo.getSubjectTypeCd()));
    		
    		return resp;
    	}).toList();
    	
    	return respList;
    }
    
    @Override
    public List<BuildingWithClassroomResp> readAllBuildingHavingClassroom(
    	String yeartermCd
    ) {
    	checkStaffRole();

    	// 1. Mapper 호출
    	var result = placeMapper.selectBuildingAndChildPlace(yeartermCd, "CLASSROOM");

    	// 2. DTO → Resp 변환
    	return result.stream()
    		.map(b -> {
    			var resp = new BuildingWithClassroomResp();
    			resp.setPlaceCd(b.getBuilding().getPlaceCd());
    			resp.setPlaceName(b.getBuilding().getPlaceName());

    			var classroomList = b.getRooms().stream()
    				.map(r -> {
    					var cr = new BuildingWithClassroomResp.ClassroomResp();
    					cr.setPlaceCd(r.getPlaceCd());
    					cr.setPlaceName(r.getPlaceName());
    					cr.setCapacity(r.getCapacity());
    					cr.setPlaceUsageCd(r.getPlaceUsageCd());
    					cr.setUsedBlocks(r.getUsedBlocks());
    					cr.setUsagePercent(
    						Math.round((r.getUsedBlocks() / 100.0) * 1000) / 10.0  // 소수점 1자리
    					);
    					return cr;
    				})
    				.toList();

    			resp.setClassrooms(classroomList);
    			return resp;
    		})
    		.toList();
    }
    
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
	) {
		if(yeartermCd == null) yeartermCd = "2026_REG1";
		
		var list = lectureMapper.selectRoomLectureSchedule(yeartermCd, placeCd);
		
		list.forEach(li -> li.setProfessorName(cache.getUserName(li.getProfessorNo())));
		
		return list;
	}
    
	/**
	 * 강의실 배정 요청을 받아, <br>
	 * 유효성 검사 후 배정합니다.
	 * 
	 * @param request
	 */
	public void createClassSchedule(
		List<ScheduleAssignReq> request
	) {
		var infoList = request.stream().map(req -> {
			var info = new LctRoomScheduleInfo();
			BeanUtils.copyProperties(req, info);
			return info;
		}).toList();
		
		lectureMapper.insertClassroomSchedule(infoList);
	}
    
    @Override
    public List<PlaceInfo> readAllRooms() {
        return placeMapper.selectPlaceListByUsage("CLASSROOM");
    }

    @Override
    public PlaceDetailDTO readRoomDetail(String placeCd) {
        return placeMapper.selectRoomByPlaceCd(placeCd);
    }

    @Override
    public void createNewRoom(PlaceDetailDTO placeDTO) throws RuntimeException {
        if (placeMapper.isPlaceCdDuplicate(placeDTO.getPlaceCd())) {
           throw new RuntimeException("이미 사용 중인 강의실 코드(" + placeDTO.getPlaceCd() + ")입니다.");
        }
        int result = placeMapper.insertRoom(placeDTO);
        if (result == 0) {
            throw new RuntimeException("강의실 등록 중 데이터베이스 오류가 발생했습니다.");
        }
    }

    @Override
    public void modifyRoom(PlaceDetailDTO placeDTO) throws RuntimeException {
        int result = placeMapper.updateRoom(placeDTO);
        if (result == 0) {
            throw new RuntimeException("수정할 강의실 정보가 없거나 데이터베이스 업데이트에 실패했습니다.");
        }
    }

    @Override
    public List<RoomScheduleDetailDTO> readRoomSchedule(String placeCd, String yearTermCd) {
        return placeMapper.selectRoomSchedule(placeCd, yearTermCd);
    }

    @Override
    public boolean checkTimeTableConflict(String yearTermCd, String placeCd, List<String> timeblockCds) {
        if (timeblockCds == null || timeblockCds.isEmpty()) {
            return false;
        }
        Map<String, Object> conflictParamMap = new HashMap<>();
		conflictParamMap.put("yearTermCd", yearTermCd);
		conflictParamMap.put("placeCd", placeCd);
		conflictParamMap.put("timeblockCds", timeblockCds);
		int conflictCount = lctRoomScheduleMapper.selectConflictingSchedule(conflictParamMap);
        return conflictCount > 0;
    }

    @Override
    public LctApplyDetailDTO readLectureAssignmentInfo(String lctApplyId) {
        return lctRoomScheduleMapper.selectLectureAssignmentInfo(lctApplyId);
    }

    // =========================================================================
    // 💡 누락된 메서드 구현: 강의 신청에 대한 요청 시간표를 조회합니다.
    // =========================================================================
    /**
     * 강의 신청 ID에 해당하는 요청 시간 블록(요일 및 교시) 목록을 조회합니다.
     * @param lctApplyId 강의 신청 ID
     * @return 요청된 시간 블록 목록 (RoomScheduleDetailDTO를 재활용하거나 전용 DTO를 사용)
     */
    @Override
    public List<RoomScheduleDetailDTO> readRequiredLectureTimes(String lctApplyId) {
        if (!StringUtils.hasText(lctApplyId)) {
            log.warn("LctApplyId가 없어 요청 시간표 조회를 건너뜁니다.");
            return List.of();
        }
        // lctRoomScheduleMapper에 selectRequiredTimeblocks(String lctApplyId) 메서드가
        // RoomScheduleDetailDTO 목록을 반환하도록 MyBatis 매퍼를 구현해야 합니다.
        return lctRoomScheduleMapper.selectRequiredTimeblocks(lctApplyId);
    }


	@Override
	public void approveLectureAssignment(String lctApplyId, Map<String, Object> assignmentData)
			throws RuntimeException {
        log.warn("🚨 approveLectureAssignment 호출: StaffApprovalService에서 처리해야 할 최종 승인 로직입니다. 현재는 빈 구현입니다.");
	}

	@Override
	public void rejectLectureAssignment(String lctApplyId, Map<String, Object> processData) throws RuntimeException {
        log.warn("🚨 rejectLectureAssignment 호출: StaffApprovalService에서 처리해야 할 최종 반려 로직입니다. 현재는 빈 구현입니다.");
	}

    /**
     * 강의 신청에 대한 강의실 및 시간대 배정 정보를 임시 저장합니다. (JSP의 "배정하기" 버튼 기능)
     * LCT_OPEN_APPLY 테이블에 배정 정보를 업데이트하고, 상태를 '임시 배정'으로 변경합니다.
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean saveAssignmentInfo(Map<String, Object> requestData) {
        String lctApplyId = (String) requestData.get("lctApplyId");
	    String placeCd = (String) requestData.get("placeCd");
	    String timeblockCdsString = (String) requestData.get("timeblockCdsString"); // 예: "1_2,1_3,2_5"

        // 유효성 검사
	    if (!StringUtils.hasText(lctApplyId) || !StringUtils.hasText(placeCd) || !StringUtils.hasText(timeblockCdsString)) {
            log.error("임시 배정을 위한 필수 정보(LctApplyId, 강의실 코드, 시간 블록)가 누락되었습니다. RequestData: {}", requestData);
	        return false; // 필수 정보 누락 시 false 반환
	    }

	    // timeblockCdsString을 DB 저장을 위한 컬럼 값으로 변환 (예: 1_2,1_3 -> LctTimeblock table에 맞게)
	    // 여기서는 문자열을 그대로 LCT_OPEN_APPLY 테이블의 ASSIGNED_TIMEBLOCK 컬럼에 저장한다고 가정합니다.

	    // ASSIGNED_DAY 컬럼에 저장할 요일 추출 (중복 제거 후 콤마 구분자 문자열)
	    String assignedDay = Arrays.stream(timeblockCdsString.split(","))
	    		.map(s -> s.split("_")[0]) // 요일 코드만 추출 (예: "1" 또는 "2")
	    		.distinct()
	    		.collect(Collectors.joining(","));

	    requestData.put("assignedDay", assignedDay);
	    requestData.put("assignedTimeblock", timeblockCdsString); // DB 컬럼명에 맞춰 재지정
	    requestData.remove("timeblockCdsString"); // 매퍼에서 사용하지 않으므로 제거

        try {
            // LCT_OPEN_APPLY 테이블에 배정 정보를 업데이트
            // placeCd, assignedDay, assignedTimeblock, 상태 코드(예: 임시 배정) 업데이트
            int updateCount = lctRoomScheduleMapper.updateLctOpenApplyAssignment(requestData);

            if (updateCount == 0) {
                 log.warn("임시 배정 정보 업데이트 실패. LctApplyId: {}. 업데이트된 행 수: {}", lctApplyId, updateCount);
            } else {
                 log.info("강의 임시 배정 정보 업데이트 완료. LctApplyId: {}, PlaceCd: {}", lctApplyId, placeCd);
            }

            return updateCount > 0;

        } catch (Exception e) {
            log.error("강의 임시 배정 정보 저장 중 DB 오류 발생. LctApplyId: {}", lctApplyId, e);
            throw new RuntimeException("강의 임시 배정 정보 저장 중 오류 발생", e); // 트랜잭션 롤백 유도
        }
    }

	@Override
	public void modifyLectureAssignmentInfo(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRoom(String placeCd) {
		// TODO Auto-generated method stub

	}
}