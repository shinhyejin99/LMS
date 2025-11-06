package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.UserReserveVO;

/**
 *
 * @author 정태일
 * @since 2025. 10. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 27.     	정태일	       최초 생성
 *
 * </pre>
 */
@Mapper
public interface UserReserveMapper {

    int insertUserReserve(UserReserveVO userReserve);

    UserReserveVO selectUserReserve(String reserveId);

    /**
     * 모든 사용자 시설 예약 정보를 조회합니다. (StaffUserReserveService용)
     *
     * @return 모든 사용자 시설 예약 정보 목록
     */
    List<UserReserveVO> selectUserReservesList(String searchTerm); // searchTerm 파라미터 추가

    int updateUserReserve(UserReserveVO userReserve);

    int deleteUserReserve(String reserveId);

    List<UserReserveVO> selectMyReservations(String userId);

    /**
	 * 전체 예약 가능 슬롯/시간 수를 조회
	 * @return 총 예약 가능 슬롯/시간 수
	 */
	int selectTotalReservableSlots();

	/**
	 * 현재 예약이 확정된 슬롯/시간 수를 조회
	 * @return 예약이 확정된 슬롯/시간 수
	 */
	int selectConfirmedReservationSlots();
}
