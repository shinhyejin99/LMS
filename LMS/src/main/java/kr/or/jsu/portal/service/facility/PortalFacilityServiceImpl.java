package kr.or.jsu.portal.service.facility;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.mybatis.mapper.PortalFacilityMapper;
import kr.or.jsu.mybatis.mapper.UserReserveMapper;
import kr.or.jsu.vo.LctRoomScheduleVO;
import kr.or.jsu.vo.PlaceVO;
import kr.or.jsu.vo.TimeblockVO;
import kr.or.jsu.vo.UserReserveVO;
import lombok.RequiredArgsConstructor;

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
 *  -----------	   	-------------    ---------------------------
 *  2025. 10. 20.     	정태일	          최초 생성
 *  2025. 10. 21.     	정태일	          예약 생성 및 조회 메서드 구현
 *  2025. 10. 30.     	정태일	          건물 목록 조회 추가
 *
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class PortalFacilityServiceImpl implements PortalFacilityService {

    private final PortalFacilityMapper facilityMapper;
    private final UserReserveMapper userReserveMapper;

    /**
     * 사용 가능한 모든 시설 목록을 조회합니다.
     * @return 시설 목록
     */
//    @Override
//    public List<PlaceVO> retrievePlacesByUsage(String placeUsageCd) {
//        return facilityMapper.selectPlacesByUsage(placeUsageCd);
//    }
    
    @Override
    public List<PlaceVO> retrieveBuildings() {
        return facilityMapper.selectBuildings();
    }

    @Override
    public List<Map<String, String>> retrieveFacilityTypesByBuilding(String parentCd, String userRole) {
        boolean isStudent = userRole.contains("ROLE_STUDENT");
        return facilityMapper.selectFacilityTypesByBuilding(parentCd, isStudent);
    }

    @Override
    public List<PlaceVO> retrieveFacilitiesByBuildingAndType(String parentCd, String placeUsageCd, PaginationInfo paginationInfo, String userRole) {
        boolean isStudent = userRole.contains("ROLE_STUDENT");
        paginationInfo.setTotalRecord(facilityMapper.selectTotalFacilitiesByBuildingAndType(parentCd, placeUsageCd, isStudent));
        return facilityMapper.selectFacilitiesByBuildingAndType(parentCd, placeUsageCd, paginationInfo, isStudent);
    }

    @Override
    public List<PlaceVO> retrieveFacilitiesByBuilding(String parentCd, PaginationInfo paginationInfo, String userRole) {
        boolean isStudent = userRole.contains("ROLE_STUDENT");
        paginationInfo.setTotalRecord(facilityMapper.selectTotalFacilitiesByBuilding(parentCd, isStudent));
        return facilityMapper.selectFacilitiesByBuilding(parentCd, paginationInfo, isStudent);
    }

    /**
     * 특정 시설의 지정된 기간 동안의 모든 예약 목록(일반 예약 + 강의 시간표)을 조회합니다. (수정됨)
     * @param placeCd 시설 코드
     * @param startAt 조회 시작 일시
     * @param endAt 조회 종료 일시
     * @return 통합된 예약 목록
     */
    @Override
    public List<UserReserveVO> retrieveReservationList(String placeCd, LocalDateTime startAt, LocalDateTime endAt, String currentUserId) {
        // 1. 일반 사용자 예약 조회
        List<UserReserveVO> userReservations = facilityMapper.selectReservationsByDateRange(placeCd, startAt, endAt, currentUserId);

        // 2. 강의 시간표 예약 조회 및 변환
        // 2-1. 모든 타임블록 정보를 Map으로 변환 (효율적인 조회를 위해)
        Map<String, TimeblockVO> timeblockMap = facilityMapper.selectAllTimeblocks().stream()
                .collect(Collectors.toMap(TimeblockVO::getTimeblockCd, Function.identity()));

        // 2-2. 학기 코드 결정
        String yeartermCd = determineYearterm(startAt.toLocalDate());

        // 2-3. 강의 시간표 조회
        List<LctRoomScheduleVO> lectureSchedules = facilityMapper.selectLectureSchedulesByPlace(placeCd, yeartermCd);
        
        // 2-4. 강의 시간표를 일반 예약 형태로 변환
        List<UserReserveVO> lectureReservations = convertSchedulesToReservations(lectureSchedules, timeblockMap, startAt.toLocalDate(), endAt.toLocalDate());

        // 3. 두 목록을 합쳐서 반환
        List<UserReserveVO> allReservations = new ArrayList<>();
        allReservations.addAll(userReservations);
        allReservations.addAll(lectureReservations);

        return allReservations;
    }

    @Override
    public PlaceVO retrieveFacility(String placeCd) {
        return facilityMapper.selectFacility(placeCd);
    }

    @Override
    public UserReserveVO retrieveReservation(String reserveId, String userId) {
        return facilityMapper.selectReservation(reserveId, userId);
    }

    @Override
    @Transactional
    public boolean modifyReservation(UserReserveVO reservation, String userId) {
        // 예약 소유자 확인 및 취소되지 않은 예약인지 확인은 매퍼 쿼리에서 처리
        int updatedRows = facilityMapper.updateReservation(reservation);
        return updatedRows > 0;
    }

    @Override
    @Transactional
    public boolean cancelReservation(String reserveId, String userId) {
        int updatedRows = facilityMapper.updateReservationCancelStatus(reserveId, userId);
        return updatedRows > 0;
    }

    /**
     * 새로운 시설 예약을 등록합니다.
     * @param reservation 등록할 예약 정보
     * @return 등록 성공 여부 (true: 성공, false: 실패)
     */
    @Override
    @Transactional
    public boolean createReservation(UserReserveVO reservation) {
    	
        // 1. 시설 정보 조회하여 수용 인원 확인
        PlaceVO facility = facilityMapper.selectFacility(reservation.getPlaceCd());
        if (facility == null) {
            throw new IllegalStateException("존재하지 않는 시설입니다.");
        }
        if (reservation.getHeadcount() > facility.getCapacity()) {
            throw new IllegalStateException(String.format("예약 인원(%d명)이 수용 인원(%d명)을 초과할 수 없습니다.", reservation.getHeadcount(), facility.getCapacity()));
        }
    	
    	
    	
    	// 2. 겹치는 예약이 있는지 확인
        List<UserReserveVO> conflictingReservations = facilityMapper.selectReservationsByDateRange(
                reservation.getPlaceCd(),
                reservation.getStartAt(),
                reservation.getEndAt(),
                null // 모든 사용자의 예약을 대상으로 확인
        );

        // 2. 겹치는 예약이 있으면 예외 발생
        if (!conflictingReservations.isEmpty()) {
            throw new IllegalStateException("선택하신 시간에 이미 다른 예약이 존재합니다.");
        }

        // 3. 겹치는 예약이 없으면 새로운 예약 생성
    	reservation.setCreateAt(LocalDateTime.now());
    	reservation.setCancelYn("N");

    	 int insertedRows = userReserveMapper.insertUserReserve(reservation);
        return insertedRows > 0;
    }

    /**
     * 현재 사용자의 모든 예약 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 예약 목록
     */
    @Override
    public List<UserReserveVO> retrieveMyReservations(String userId) {
        return userReserveMapper.selectMyReservations(userId);
    }

    /**
     * 날짜를 기반으로 학기 코드를 결정하는 임시 메소드
     * @param date
     * @return 예: "2025_REG1"
     */
    private String determineYearterm(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        // 3월~7월: 1학기, 9월~12월: 2학기 (임시 규칙)
        String semester = (month >= 3 && month <= 7) ? "REG1" : "REG2";
        return String.format("%d_%s", year, semester);
    }

    /**
     * 주간 강의 시간표 목록을 특정 기간 내의 일회성 예약 목록으로 변환합니다.
     * @param schedules 주간 강의 시간표 목록
     * @param timeblockMap 타임블록 정보 맵
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 변환된 예약 목록
     */
    private List<UserReserveVO> convertSchedulesToReservations(List<LctRoomScheduleVO> schedules, Map<String, TimeblockVO> timeblockMap, LocalDate startDate, LocalDate endDate) {
        List<UserReserveVO> reservations = new ArrayList<>();

        for (LctRoomScheduleVO schedule : schedules) {
            TimeblockVO timeblock = timeblockMap.get(schedule.getTimeblockCd());
            if (timeblock == null) continue;

            DayOfWeek scheduleDay = parseDayOfWeek(timeblock.getWeekday());
            LocalTime startTime = LocalTime.parse(timeblock.getStartHhmm().substring(0, 2) + ":" + timeblock.getStartHhmm().substring(2, 4));
            LocalTime endTime = LocalTime.parse(timeblock.getEndHhmm().substring(0, 2) + ":" + timeblock.getEndHhmm().substring(2, 4));

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (date.getDayOfWeek() == scheduleDay) {
                    UserReserveVO vo = new UserReserveVO();
                    vo.setReserveId("LECTURE_" + schedule.getLectureId()); // 임시 ID
                    vo.setPlaceCd(schedule.getPlaceCd());
                    vo.setReserveReason("정규 강의"); // 임시 사유
                    vo.setStartAt(LocalDateTime.of(date, startTime));
                    vo.setEndAt(LocalDateTime.of(date, endTime));
                    vo.setCancelYn("N");
                    reservations.add(vo);
                }
            }
        }
        return reservations;
    }

    /**
     * 요일 코드(MO, TU...)를 DayOfWeek 객체로 변환합니다.
     * @param weekdayCode
     * @return
     */
    private DayOfWeek parseDayOfWeek(String weekdayCode) {
        switch (weekdayCode.toUpperCase()) {
            case "MO": return DayOfWeek.MONDAY;
            case "TU": return DayOfWeek.TUESDAY;
            case "WE": return DayOfWeek.WEDNESDAY;
            case "TH": return DayOfWeek.THURSDAY;
            case "FR": return DayOfWeek.FRIDAY;
            case "SA": return DayOfWeek.SATURDAY;
            case "SU": return DayOfWeek.SUNDAY;
            default: throw new IllegalArgumentException("Invalid weekday code: " + weekdayCode);
        }
    }
}

