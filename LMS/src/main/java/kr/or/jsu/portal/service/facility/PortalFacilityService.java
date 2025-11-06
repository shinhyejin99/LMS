package kr.or.jsu.portal.service.facility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.vo.PlaceVO;
import kr.or.jsu.vo.UserReserveVO;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 20.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      		수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 20.     	정태일	          최초 생성
 *  2025. 10. 21.     	정태일	          예약 생성 및 조회 메서드 추가
 *  2025. 10. 21.     	정태일	          나의 예약 목록 조회 추가
 *  2025. 10. 30.     	정태일	          건물 목록 조회 추가
 *
 * </pre>
 */
public interface PortalFacilityService {
    /**
     * 사용 가능한 특정 용도의 시설 목록을 조회합니다.
     * @param placeUsageCd 시설 용도 코드 (예: STUDYROOM, SEMINARROOM)
     * @return 시설 목록
     */
//    public List<PlaceVO> retrievePlacesByUsage(String placeUsageCd);
    /**
     * 사용 가능한 모든 건물 목록을 조회합니다.
     * @return 건물 목록
     */
    public List<PlaceVO> retrieveBuildings();

    /**
     * 특정 건물 내의 시설 유형 목록을 조회합니다.
     * @param parentCd 건물 코드
     * @return 시설 유형 목록 (COMMON_CODE의 CD_NAME 포함)
     */
    public List<Map<String, String>> retrieveFacilityTypesByBuilding(String parentCd, String userRole);

    /**
     * 특정 건물에 속한 모든 시설 목록을 조회합니다.
     * @param parentCd 건물 코드
     * @return 시설 목록
     */
    public List<PlaceVO> retrieveFacilitiesByBuilding(String parentCd, PaginationInfo paginationInfo, String userRole);

    /**
     * 특정 건물 및 시설 유형에 해당하는 시설 목록을 조회합니다.
     * @param parentCd 건물 코드
     * @param placeUsageCd 시설 용도 코드
     * @return 시설 목록
     */
    public List<PlaceVO> retrieveFacilitiesByBuildingAndType(String parentCd, String placeUsageCd, PaginationInfo paginationInfo, String userRole);

    /**
     * 특정 시설의 지정된 기간 동안의 예약 목록을 조회합니다.
     * @param placeCd 시설 코드
     * @param startAt 조회 시작 일시
     * @param endAt 조회 종료 일시
     * @return 예약 목록
     */
    public List<UserReserveVO> retrieveReservationList(String placeCd, LocalDateTime startAt, LocalDateTime endAt, String currentUserId);

    /**
     * 특정 시설의 정보를 조회합니다.
     * @param placeCd 시설 코드
     * @return 시설 정보
     */
    public PlaceVO retrieveFacility(String placeCd);

    /**
     * 특정 예약의 상세 정보를 조회합니다.
     * @param reserveId 예약 ID
     * @param userId 사용자 ID (예약 소유자 확인용)
     * @return 예약 정보 (UserReserveVO)
     */
    public UserReserveVO retrieveReservation(String reserveId, String userId);

    /**
     * 예약을 수정합니다.
     * @param reservation 수정할 예약 정보
     * @param userId 사용자 ID (예약 소유자 확인용)
     * @return 수정 성공 여부 (true: 성공, false: 실패)
     */
    public boolean modifyReservation(UserReserveVO reservation, String userId);

    /**
     * 예약을 취소합니다.
     * @param reserveId 예약 ID
     * @param userId 사용자 ID (예약 소유자 확인용)
     * @return 취소 성공 여부 (true: 성공, false: 실패)
     */
    public boolean cancelReservation(String reserveId, String userId);

    /**
     * 새로운 시설 예약을 등록합니다.
     * @param reservation 등록할 예약 정보
     * @return 등록 성공 여부 (true: 성공, false: 실패)
     */
    public boolean createReservation(UserReserveVO reservation);
    
    /**
     * 현재 사용자의 모든 예약 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 예약 목록
     */
    public List<UserReserveVO> retrieveMyReservations(String userId);
}
