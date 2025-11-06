package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.dto.LctApplyDetailDTO;
import kr.or.jsu.dto.RoomScheduleDetailDTO;
import kr.or.jsu.vo.LctRoomScheduleVO;

@Mapper
public interface LctRoomScheduleMapper {

	public List<LctRoomScheduleVO> selectLctRoomScheduleList();

	public int selectConflictingSchedule(Map<String, Object> conflictParamMap);

	public void insertLctRoomSchedule(Map<String, Object> scheduleMap);

	public LctApplyDetailDTO selectLectureAssignmentInfo(String lctApplyId);

	/**
     * LCT_OPEN_APPLY 테이블에 강의실 배정 정보를 업데이트합니다.
     * (임시 배정 시 사용)
     * @param requestData lctApplyId, placeCd, timeblockCdsString 포함
     * @return 업데이트된 행의 수
     */
	public int updateLctOpenApplyAssignment(Map<String, Object> requestData);

	public List<RoomScheduleDetailDTO> selectRequiredTimeblocks(String lctApplyId);


//	public LctRoomScheduleVO selectLctRoomSchedule();
//
//	public int insertLctRoomSchedule();
//
//	public int updateLctRoomSchedule();
//
//	public int deleteLctRoomSchedule();
}