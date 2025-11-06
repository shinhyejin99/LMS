package kr.or.jsu.portal.controller.facility;


import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.or.jsu.core.security.CustomUserDetails;
import kr.or.jsu.portal.service.facility.PortalFacilityService;
import kr.or.jsu.vo.UserReserveVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 정태일
 * @since 2025. 10. 20.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 20.     	정태일	          최초 생성
 *  2025. 10. 20.     	정태일	          예약 등록 기능 추가
 *
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class PortalFacilityRestController {

    @Autowired
    private PortalFacilityService facilityService;


    
    /**
     * 새로운 시설 예약을 등록한다.
     * @param placeCd 시설 코드
     * @param reservation 예약 정보 (UserReserveVO)
     * @return ResponseEntity (성공: 201 Created, 실패: 400 Bad Request 또는 500 Internal Server Error)
     */
    @PostMapping("/facilities/{placeCd}/reservations")
    public ResponseEntity<String> createReservation(
            @PathVariable String placeCd,
            @RequestBody UserReserveVO reservation,
            @AuthenticationPrincipal CustomUserDetails customUserDetails            
	) {
        try {
            // placeCd가 URL 경로 변수와 RequestBody의 placeCd가 일치하는지 확인
            if (!placeCd.equals(reservation.getPlaceCd())) {
                return new ResponseEntity<>("시설 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
            
            // 현재 로그인한 사용자 ID를 reservation 객체에 설정 (인증 정보에서 가져와야 함)
            reservation.setUserId(customUserDetails.getRealUser().getUserId()); 

            boolean success = facilityService.createReservation(reservation);
            if (success) {
                return new ResponseEntity<>("예약이 성공적으로 등록되었습니다.", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("예약 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IllegalStateException e) {
            log.warn("중복 예약 시도: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("시설 예약 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류로 예약 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 예약을 취소한다.
     * @param placeCd 시설 코드
     * @param reserveId 예약 ID
     * @return ResponseEntity (성공: 200 OK, 실패: 400 Bad Request 또는 500 Internal Server Error)
     */
    @DeleteMapping("/facilities/{placeCd}/reservations/{reserveId}")
    public ResponseEntity<String> cancelReservation(
            @PathVariable String placeCd,
            @PathVariable String reserveId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            String userId = customUserDetails.getRealUser().getUserId();
            boolean success = facilityService.cancelReservation(reserveId, userId);
            if (success) {
                return new ResponseEntity<>("예약이 성공적으로 취소되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("예약 취소에 실패했습니다. 예약이 존재하지 않거나 소유자가 아닙니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("예약 취소 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류로 예약 취소에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 예약의 상세 정보를 조회한다.
     * @param placeCd 시설 코드
     * @param reserveId 예약 ID
     * @return 예약 정보 (UserReserveVO)
     */
    @GetMapping("/facilities/{placeCd}/reservations/{reserveId}")
    public ResponseEntity<UserReserveVO> getReservationDetails(
            @PathVariable String placeCd,
            @PathVariable String reserveId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            String userId = customUserDetails.getRealUser().getUserId();
            UserReserveVO reservation = facilityService.retrieveReservation(reserveId, userId);
            if (reservation != null) {
                return new ResponseEntity<>(reservation, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("예약 상세 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 예약을 수정한다.
     * @param placeCd 시설 코드
     * @param reserveId 예약 ID
     * @param reservation 수정할 예약 정보 (UserReserveVO)
     * @return ResponseEntity (성공: 200 OK, 실패: 400 Bad Request 또는 500 Internal Server Error)
     */
    @PutMapping("/facilities/{placeCd}/reservations/{reserveId}")
    public ResponseEntity<String> modifyReservation(
            @PathVariable String placeCd,
            @PathVariable String reserveId,
            @RequestBody UserReserveVO reservation,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            if (!reserveId.equals(reservation.getReserveId())) {
                return new ResponseEntity<>("예약 ID가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
            if (!placeCd.equals(reservation.getPlaceCd())) {
                return new ResponseEntity<>("시설 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }

            String userId = customUserDetails.getRealUser().getUserId();
            reservation.setUserId(userId); // Ensure the reservation object has the correct user ID

            boolean success = facilityService.modifyReservation(reservation, userId);
            if (success) {
                return new ResponseEntity<>("예약이 성공적으로 수정되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("예약 수정에 실패했습니다. 예약이 존재하지 않거나 소유자가 아닙니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("예약 수정 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류로 예약 수정에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 시설의 지정된 기간 동안의 예약 목록을 조회한다.
     * @param placeCd 시설 코드
     * @param startAt 조회 시작 일시 (ISO 8601 형식: yyyy-MM-ddTHH:mm:ss)
     * @param endAt 조회 종료 일시 (ISO 8601 형식: yyyy-MM-ddTHH:mm:ss)
     * @return 예약 목록 (List<UserReserveVO>)
     */
    @GetMapping("/facilities/{placeCd}/reservations")
    public ResponseEntity<List<UserReserveVO>> getReservations(
            @PathVariable String placeCd,
            @RequestParam String startAt,
            @RequestParam String endAt,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startAt);
            LocalDateTime endDateTime = LocalDateTime.parse(endAt);
            List<UserReserveVO> reservations = facilityService.retrieveReservationList(placeCd, startDateTime, endDateTime, customUserDetails.getRealUser().getUserId());
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (DateTimeParseException e) {
            log.error("날짜/시간 파싱 오류: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("예약 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
