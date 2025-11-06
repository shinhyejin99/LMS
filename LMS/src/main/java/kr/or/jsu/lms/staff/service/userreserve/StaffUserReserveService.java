package kr.or.jsu.lms.staff.service.userreserve;

import java.util.List;

import kr.or.jsu.vo.UserReserveVO;

/**
 *
 *
 * @author 신혜진
 * @since 2025. 9. 25.
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 25.     	신혜진	          최초 생성
 *  2025.10. 26.     	정태일	          시설예약정보 조회 추가
 *
 *      </pre>
 */
public interface StaffUserReserveService {

	/**
	 * 시설물 등록
	 *
	 * @param userReserve
	 * @return
	 */
	public void createStaffUserReserve(UserReserveVO userReserve);

    /**
     * 모든 사용자 시설 예약 정보를 조회합니다.
     * 검색 조건(searchTerm)이 있을 경우 해당 조건에 따라 필터링된 목록을 조회합니다.
     *
     * @param searchTerm 통합 검색어 (시설명, 사용자 이름 등으로 검색)
     * @return 모든 사용자 시설 예약 정보 목록
     */
    List<UserReserveVO> readStaffUserReserveList(String searchTerm); // searchTerm 파라미터 추가

	/**
	 * 시설물 단건 조회
	 *
	 * @param userReserve
	 * @return
	 * @throws {@link RuntimeException}
	 */
	public UserReserveVO readStaffUserReserve(String userReserveId) throws RuntimeException;

	/**
	 * 시설물 수정
	 *
	 * @param userReserve
	 * @return
	 */
	public void modifyStaffUserReserve(UserReserveVO userReserve);

	/**
     * 현재 시점의 '총 예약 가능한 시설 슬롯' 수를 조회
     * @return 총 예약 가능 슬롯/시간 수
     */
    public int readTotalReservableSlots();

    /**
     * 현재 시점의 '확정된 예약' 슬롯 수를 조회
     * @return 예약이 확정된 슬롯/시간 수
     */
    public int readConfirmedReservationSlots();
}


