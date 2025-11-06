package kr.or.jsu.lms.staff.service.approval;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.dto.ApprovalLineRequestDetailDTO;
import kr.or.jsu.dto.RecordApplyRequestDTO;
import kr.or.jsu.mybatis.mapper.ApprovalMapper;
import kr.or.jsu.mybatis.mapper.LctRoomScheduleMapper;
import kr.or.jsu.mybatis.mapper.LectureMapper;
import kr.or.jsu.mybatis.mapper.StuMilitaryMapper;
import kr.or.jsu.vo.ApprovalVO;
import kr.or.jsu.vo.StuMilitaryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
* @author 신혜진
* @since 2025. 10. 15.
* @see
*
* <pre>
* << 개정이력(Modification Information) >>
*
* 수정일      			수정자           수정내용
* -----------   	-------------    ---------------------------
* 2025. 10. 15.     	김수현	          군휴학 처리 시 사용할 메소드 추가
* </pre>
*
*/
@Slf4j
@Service
@RequiredArgsConstructor
class StaffApprovalServiceImpl implements StaffApprovalService {

    private final ApprovalMapper mapper;
    private final StuMilitaryMapper militaryMapper;

    // 강의관련
    private final LctRoomScheduleMapper lctRoomScheduleMapper;
    private final LectureMapper lectureMapper;
    
    @Override
    @Transactional
    public void createStaffApproval(ApprovalLineRequestDetailDTO approval) {
        log.info("결재선 생성 요청: {}", approval);

        if (!StringUtils.hasText(approval.getUserId())) {
            throw new RuntimeException("승인자 ID는 필수 입니다.");
        }

        Map<String, Object> paramMap = convertDtoToMap(approval);
        paramMap.put("APPROVE_YNNULL", null);

        int result = mapper.insertApproval(paramMap);
        if (result != 1) {
            throw new RuntimeException("결재선 등록에 실패 했습니다");
        }
    }
    
    @Override
    public List<Map<String, Object>> readStaffApprovalList(Map<String, Object> paramMap) {

        int totalRecords = mapper.selectApprovalCount(paramMap);

        PaginationInfo<Map<String, Object>> pagingInfo = (PaginationInfo<Map<String, Object>>) paramMap
                .get("pagingInfo");

        pagingInfo.setTotalRecord(totalRecords);

        List<Map<String, Object>> approvalList = mapper.selectApprovalList(paramMap);

        return approvalList;
    }

    /**
     * 데이터를 찾지 못해도 RuntimeException을 던지지 않고 null을 반환합니다.
     */
    @Override
    public ApprovalLineRequestDetailDTO readStaffApproval(String approveId) {
        ApprovalLineRequestDetailDTO approvalDetail = mapper.selectApproval(approveId);
        return approvalDetail;
    }

    @Override
    @Transactional
    public void modifyStaffApproval(ApprovalLineRequestDetailDTO approval) {
        // 이 메서드 대신 modifyStaffApprovalProcess를 사용하도록 유도
    }

    /**
     * 결재 상태 변경 및 문서 종류별 최종 처리 (강의 개설의 경우 강의 확정 처리 포함)
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void modifyStaffApprovalProcess(Map<String, Object> paramMap) {

        String approveId = (String) paramMap.get("approveId");
        String approveYnnull = (String) paramMap.get("approveYnnull");

        // 1. 현재 결재선 상태 및 문서 상세 정보 조회
        ApprovalLineRequestDetailDTO currentDetail = mapper.selectApproval(approveId);

        if (currentDetail == null) {
            throw new RuntimeException(String.format("처리할 결재선을 찾을 수 없습니다: %s", approveId));
        }
        if (StringUtils.hasText(currentDetail.getApproveYnnull())) {
            throw new IllegalStateException("이미 처리된 결재선입니다. 상태: " + currentDetail.getApproveYnnull());
        }

        // 2. 일반 결재선 처리 (APPROVAL 테이블 업데이트)
        ApprovalVO currentLine = new ApprovalVO();
        currentLine.setApproveId(approveId);
        currentLine.setApproveYnnull(approveYnnull);
        currentLine.setComments((String) paramMap.get("comments"));
        currentLine.setAttachFileId((String) paramMap.get("attachFileId"));
        currentLine.setApproveAt(LocalDateTime.now());

        int updateResult = mapper.updateApproval(currentLine);
        if (updateResult != 1) {
            throw new RuntimeException("결재 상태 업데이트에 실패했습니다. (DB 갱신 오류)");
        }

        // 3. 다음 결재선 진행 여부 확인
        List<ApprovalVO> nextLines = mapper.selectNextApprovalLines(approveId);

        // 4. 최종 승인 시, 문서 종류별 최종 로직 수행
        if ("Y".equals(approveYnnull) && nextLines.isEmpty()) {

            // 4-1. 강의 개설 문서(APPLY_TYPE_CD = 'LCT_OPEN')인 경우
            if ("LCT_OPEN".equals(currentDetail.getApplyTypeCd())) {
                log.info("최종 승인: 강의 개설 문서. 강의 확정 프로세스를 시작합니다.");

                // **[수정]** 강의 확정 및 시간표 충돌 검사 로직 호출 (배정 정보는 LCT_OPEN_APPLY에서 직접 조회)
                finalizeLectureAssignment(currentDetail); 

                // LCT_OPEN_APPLY 테이블의 최종 상태 업데이트 로직 추가
                Map<String, Object> lectureApplyUpdateParam = new HashMap<>();
                lectureApplyUpdateParam.put("lctApplyId", currentDetail.getLctApplyId()); 
                lectureApplyUpdateParam.put("approveYn", "Y");
                
                int applyUpdateResult = lectureMapper.updateLectureOpenApplyStatus(lectureApplyUpdateParam); 
                if (applyUpdateResult == 0) {
                    throw new RuntimeException("강의 신청 상태 업데이트에 실패했습니다.");
                }
                
            }
            log.info("문서 최종 승인 완료: {}", currentDetail.getApplyTypeName());

        } else if ("N".equals(approveYnnull)) {
            // 4-2. 반려 처리 시, 문서 종류별 최종 로직 수행
            if ("LCT_OPEN".equals(currentDetail.getApplyTypeCd())) {
                // LCT_OPEN_APPLY 테이블의 최종 상태 업데이트 로직 추가 (반려)
                Map<String, Object> lectureApplyUpdateParam = new HashMap<>();
                lectureApplyUpdateParam.put("lctApplyId", currentDetail.getLctApplyId());
                lectureApplyUpdateParam.put("approveYn", "N");
                
                int applyUpdateResult = lectureMapper.updateLectureOpenApplyStatus(lectureApplyUpdateParam);
                if (applyUpdateResult == 0) {
                    // 롤백 유도
                    throw new RuntimeException("강의 신청 상태 업데이트(반려)에 실패했습니다.");
                }
            }
            log.info("문서 반려 처리 완료: {}", currentDetail.getApplyTypeName());
        }
    }

    /**
     * 강의 개설 최종 승인 시, 강의 확정 및 시간표 삽입을 처리하는 전용 메서드
     * **[수정]** placeCd, timeblockCdsString 매개변수를 제거하고 내부에서 조회합니다.
     */
    private void finalizeLectureAssignment(ApprovalLineRequestDetailDTO approvalDetail) {

        String lctApplyId = approvalDetail.getLctApplyId();
        
        // 1. LCT_OPEN_APPLY 테이블에서 임시 배정된 정보를 직접 조회
        // 💡 이 로직을 위한 lectureMapper.selectLectureAssignmentDetails 쿼리 필요
        Map<String, Object> assignmentInfo = lectureMapper.selectLectureAssignmentDetails(lctApplyId);

        if (assignmentInfo == null) {
            throw new RuntimeException(String.format("LctApplyId [%s]에 대한 강의 신청 상세 정보(배정 정보 포함)를 찾을 수 없습니다.", lctApplyId));
        }
        
        String placeCd = (String) assignmentInfo.get("ASSIGN_ROOM_CD");
        String timeblockCdsString = (String) assignmentInfo.get("ASSIGN_TIME_CDS");

        if (!StringUtils.hasText(placeCd) || !StringUtils.hasText(timeblockCdsString)) {
            throw new IllegalStateException("강의실 배정 정보가 누락되어 강의를 확정할 수 없습니다. 배정 단계(saveAssignment)를 확인하세요.");
        }

        // 2. timeblockCds 문자열을 List<String>으로 변환
        List<String> timeblockCds = Arrays.asList(timeblockCdsString.split(","));

        // 3. 시간표 충돌 검사
        Map<String, Object> conflictParamMap = new HashMap<>();
        conflictParamMap.put("yearTermCd", approvalDetail.getYeartermCd());
        conflictParamMap.put("placeCd", placeCd);
        conflictParamMap.put("timeblockCds", timeblockCds);

        int conflictCount = lctRoomScheduleMapper.selectConflictingSchedule(conflictParamMap);

        if (conflictCount > 0) {
            // 충돌 발생 시, 트랜잭션 롤백 유도
            throw new IllegalStateException("배정된 강의실/시간이 기존 확정 강의와 충돌하여 강의 확정 처리가 중단되었습니다.");
        }

        // 4. LECTURE 테이블에 강의 확정 정보 삽입
        Map<String, Object> lectureInsertParam = new HashMap<>();
        lectureInsertParam.put("lctApplyId", lctApplyId); 
        lectureInsertParam.put("placeCd", placeCd);
        lectureInsertParam.put("timeblockCds", timeblockCds); 

        // [XML ID: insertLecture] 호출 (LECTURE_ID가 Map에 반환됨)
        lectureMapper.insertLecture(lectureInsertParam); 
        
        Integer newLectureIdObj = (Integer) lectureInsertParam.get("LECTURE_ID");
        int newLectureId = (newLectureIdObj != null) ? newLectureIdObj : 0;
 
        if (newLectureId <= 0) {
            throw new RuntimeException("강의 확정(LECTURE 테이블 삽입)에 실패했습니다. LECTURE_ID가 생성되지 않았습니다.");
        }
        
        // 5. LCT_ROOM_SCHEDULE 테이블에 시간표 정보 삽입
        for (String timeblockCd : timeblockCds) {
            Map<String, Object> scheduleMap = new HashMap<>();
            scheduleMap.put("LECTURE_ID", newLectureId);
            scheduleMap.put("PLACE_CD", placeCd);
            scheduleMap.put("TIMEBLOCK_CD", timeblockCd);

            lctRoomScheduleMapper.insertLctRoomSchedule(scheduleMap);
        }

        log.info("강의 확정 완료. LECTURE_ID: {}", newLectureId);
    }

    private Map<String, Object> convertDtoToMap(ApprovalLineRequestDetailDTO dto) {

        Map<String, Object> map = new HashMap<>();

        map.put("PREV_APPROVE_ID", dto.getPrevApproveId());
        map.put("USER_ID", dto.getUserId());
        map.put("APPROVE_YNNULL", dto.getApproveYnnull());

        return map;
    }

    /**
	 * 군입대 정보 처리 => 군휴학 시 사용해야하는 메소드
	 */
	private void processMilitaryInfo(RecordApplyRequestDTO request) {
		String studentNo = request.getStudentNo();

		// 기존 병역 정보 조회
		StuMilitaryVO existingMilitary = militaryMapper.selectMilitary(studentNo);

		// 병역 정보 VO 생성
		StuMilitaryVO military = new StuMilitaryVO();
		military.setStudentNo(studentNo);
		military.setMilitaryTypeCd(request.getMilitaryTypeCd());// 입대구분

		// LocalDateTime 변환 (DTO는 String, VO는 LocalDateTime)
		military.setJoinAt(LocalDateTime.parse(request.getJoinAt() + "T00:00:00"));

		if (request.getExitAt() != null) {
			military.setExitAt(LocalDateTime.parse(request.getExitAt() + "T00:00:00"));
		}

		if (existingMilitary == null) {
			// 첫 군휴학  INSERT
			militaryMapper.insertMilitary(military);
			log.info("병역 정보 등록 - studentNo: {}", studentNo);
		} else {
			// 기존 정보 있음  UPDATE
			militaryMapper.updateMilitary(military);
			log.info("병역 정보 수정 - studentNo: {}", studentNo);
		}
	}

    @Override
    public Map<String, Integer> readApprovalStatusCounts(String currentUserId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("currentUserId", currentUserId);

        // Mapper에서 상태별 건수를 조회하는 메소드를 호출
        Map<String, Object> counts = mapper.selectApprovalStatusCounts(paramMap);

        if (counts == null) {
            // 데이터가 없을 경우 기본값 반환
            return Map.of(
                "pendingCount", 0,
                "rejectedCount", 0, 
                "approvedCount", 0,
                "totalCount", 0
            );
        }

        // Map<String, Object>를 Map<String, Integer>로 변환하여 Controller에 전달
        Map<String, Integer> result = new HashMap<>();
        result.put("pendingCount", ((Number) counts.getOrDefault("PENDING_COUNT", 0)).intValue());
        result.put("rejectedCount", ((Number) counts.getOrDefault("REJECTED_COUNT", 0)).intValue());
        result.put("approvedCount", ((Number) counts.getOrDefault("APPROVED_COUNT", 0)).intValue());
        result.put("totalCount", ((Number) counts.getOrDefault("TOTAL_COUNT", 0)).intValue());

        result.put("completedCount", 0); // 사용하지 않으므로 0으로 설정 유지
        return result;
    }
}