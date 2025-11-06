package kr.or.jsu.mybatis.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.vo.LctRoomScheduleVO;
import kr.or.jsu.vo.PlaceVO;
import kr.or.jsu.vo.TimeblockVO;
import kr.or.jsu.vo.UserReserveVO;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 21.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 21.     	정태일	          최초 생성
 *  2025. 10. 30.     	정태일	          건물 목록 조회 추가
 *
 * </pre>
 */
@Mapper
public interface PortalFacilityMapper {

    /**
     * 사용 가능한 특정 용도의 시설 목록을 조회
     * @param placeUsageCd 시설 용도 코드 (예: STUDYROOM, SEMINARROOM)
     * @return 시설 목록
     */
    List<PlaceVO> selectPlacesByUsage(@Param("placeUsageCd") String placeUsageCd);

    /**
     * 사용 가능한 모든 건물 목록을 조회합니다.
     * @return 건물 목록
     */
    List<PlaceVO> selectBuildings();

    /**
     * 특정 건물 내의 시설 유형 목록을 조회합니다.
     * @param parentCd 건물 코드
     * @return 시설 유형 목록 (COMMON_CODE의 CD_NAME 포함)
     */
    List<Map<String, String>> selectFacilityTypesByBuilding(@Param("parentCd") String parentCd, @Param("isStudent") boolean isStudent);

    /**
     * 특정 건물 및 시설 유형에 해당하는 시설 목록을 조회합니다.
     * @param parentCd 건물 코드
     * @param placeUsageCd 시설 용도 코드
     * @return 시설 목록
     */
    List<PlaceVO> selectFacilitiesByBuildingAndType(@Param("parentCd") String parentCd, @Param("placeUsageCd") String placeUsageCd, @Param("paginationInfo") PaginationInfo paginationInfo, @Param("isStudent") boolean isStudent);

    /**
     * 특정 건물 및 시설 유형에 해당하는 시설의 총 개수를 조회합니다.
     * @param parentCd 건물 코드
     * @param placeUsageCd 시설 용도 코드
     * @return 시설 총 개수
     */
    int selectTotalFacilitiesByBuildingAndType(@Param("parentCd") String parentCd, @Param("placeUsageCd") String placeUsageCd, @Param("isStudent") boolean isStudent);

    /**
     * 특정 건물에 속한 모든 시설 목록을 조회합니다.
     * @param parentCd 건물 코드
     * @return 시설 목록
     */
    List<PlaceVO> selectFacilitiesByBuilding(@Param("parentCd") String parentCd, @Param("paginationInfo") PaginationInfo paginationInfo, @Param("isStudent") boolean isStudent);

    /**
     * 특정 건물에 속한 모든 시설의 총 개수를 조회합니다.
     * @param parentCd 건물 코드
     * @return 시설 총 개수
     */
    int selectTotalFacilitiesByBuilding(@Param("parentCd") String parentCd, @Param("isStudent") boolean isStudent);

    /**
     * 특정 시설의 정보를 조회
     * @param placeCd 시설 코드
     * @return 시설 정보
     */
    PlaceVO selectFacility(@Param("placeCd") String placeCd);

    /**
     * 특정 시설의 지정된 기간 동안의 예약 목록을 조회
     * @param placeCd 시설 코드
     * @param startAt 조회 시작 일시
     * @param endAt 조회 종료 일시
     * @return 예약 목록
     */
    List<UserReserveVO> selectReservationsByDateRange(
        @Param("placeCd") String placeCd, 
        @Param("startAt") LocalDateTime startAt, 
        @Param("endAt") LocalDateTime endAt,
        @Param("currentUserId") String currentUserId
    );
    
    /**
     * 특정 시설과 학기에 배정된 정규 강의 시간표 ID 목록을 조회
     * @param placeCd 시설 코드
     * @param yeartermCd 학기 코드
     * @return 강의 시간표 ID 목록
     */
    List<LctRoomScheduleVO> selectLectureSchedulesByPlace(
        @Param("placeCd") String placeCd, 
        @Param("yeartermCd") String yeartermCd
    );
    
    /**
     * 모든 시간 블록 정보를 조회
     * @return 시간 블록 목록
     */
    List<TimeblockVO> selectAllTimeblocks();
    
    
    /**
     * 특정 예약의 상세 정보를 조회
     * @param reserveId 예약 ID
     * @param userId 사용자 ID
     * @return 예약 정보
     */
    UserReserveVO selectReservation(@Param("reserveId") String reserveId, @Param("userId") String userId);

    /**
     * 예약을 수정합니다.
     * @param reservation 수정할 예약 정보
     * @return 업데이트된 레코드 수
     */
    int updateReservation(UserReserveVO reservation);

    /**
     * 예약의 취소 상태를 업데이트합니다.
     * @param reserveId 예약 ID
     * @param userId 사용자 ID
     * @return 업데이트된 레코드 수
     */
    int updateReservationCancelStatus(@Param("reserveId") String reserveId, @Param("userId") String userId);

    /**
     * 새로운 시설 예약을 등록합니다.
     * @param reservation 등록할 예약 정보
     * @return 삽입된 레코드 수
     */
    int insertReservation(UserReserveVO reservation);
}
