package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.dto.PlaceDetailDTO;
import kr.or.jsu.dto.RoomScheduleDetailDTO;
import kr.or.jsu.dto.db.place.PlaceBuildingDTO;
import kr.or.jsu.dto.info.place.PlaceInfo;
import kr.or.jsu.vo.PlaceVO;

@Mapper
public interface PlaceMapper {

	// 장소 등록
	public int insertPlace(PlaceVO place);

	// 특정 장소 조회
	public PlaceVO selectPlace(String placeCd);

	// 장소 정보 수정
	public int updatePlace(PlaceVO place);

	// 장소 삭제
	public int deletePlace(String placeCd);

	// 강의실 전체 목록 조회 (오버로딩된 메서드만 남김)
	public List<PlaceDetailDTO> selectPlaceList(
		@Param("placeType") String placeType
		, @Param("usageYn") String usageYn
	);
	
	/**
	 * "공간"을 "사용 목적"으로 검색합니다. <br>
	 * placeType 이런거 없어도, CLASSROOM, STUDYROOM 이런걸로 검색하면 <br>
	 * 딱 사용목적에 맞는 방만 나와요. 
	 * 
	 * @author 송태호
	 * @param placeUsageCd 사용목적코드
	 * @return
	 */
	public List<PlaceInfo> selectPlaceListByUsage(
		String placeUsageCd
	);
	
	/**
	 * 학교 내의, 특정 목적으로 이용되는 방을 가진 모든 빌딩과, <br>
	 * 해당 목적으로 사용되고 있는 방들의 목록을 가져옵니다.
	 * 
	 * @return
	 */
	public List<PlaceBuildingDTO> selectBuildingAndChildPlace(
		String yeartermCd
		, String placeUsageCd
	);

	// 강의실 상세 정보 조회
	PlaceDetailDTO selectRoomByPlaceCd(String placeCd);

	// 공간 중복 체크
	boolean isPlaceCdDuplicate(String placeCd);

	//   강의실 등록
	int insertRoom(PlaceDetailDTO placeDTO);

	// 강의실 수정
	int updateRoom(PlaceDetailDTO placeDTO);

	//   강의실 시간표 조회 (해당 강의실에 대한 확정된 시간표를 조회)
	List<RoomScheduleDetailDTO> selectRoomSchedule(@Param("placeCd") String placeCd,
			@Param("yearTermCd") String yearTermCd);
}