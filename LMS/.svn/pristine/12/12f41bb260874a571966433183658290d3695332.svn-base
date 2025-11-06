package kr.or.jsu.lms.staff.controller.staffLectureRoom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.or.jsu.dto.LctApplyDetailDTO;
import kr.or.jsu.dto.PlaceDetailDTO;
import kr.or.jsu.dto.RoomScheduleDetailDTO;
import kr.or.jsu.dto.info.place.PlaceInfo;
import kr.or.jsu.lms.staff.service.staffLectureRoom.StaffLectureRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lms/staff/classroom/assignment")
@RequiredArgsConstructor
public class StaffLectureRoomController {

    private final StaffLectureRoomService service;

    // -------------------------------------------
    // 강의실 전체 조회 (관리용)
    // -------------------------------------------
    @GetMapping("/list")
    public String StaffLectureRoomList(Model model) {
        try {
            List<PlaceInfo> roomList = service.readAllRooms();
            model.addAttribute("roomList", roomList);
            return "staff/lectureRoom/lectureRoomInfoList";
        } catch (RuntimeException e) {
            log.error("강의실 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "강의실 목록을 불러오는 중 오류가 발생했습니다.");
            return "staff/lectureRoom/lectureRoomInfoList";
        }
    }

    // -------------------------------------------
    // 강의실 배정 폼
    // -------------------------------------------
    @GetMapping("/form")
    public String showLectureAssignmentForm(@RequestParam("lctApplyId") String lctApplyId,
                                           @RequestParam("approvalId") String approvalId,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {

        if (lctApplyId == null || lctApplyId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "강의 신청 ID가 올바르지 않아 배정 폼을 열 수 없습니다.");
            return (approvalId != null && !approvalId.trim().isEmpty())
                    ? "redirect:/lms/staff/approvals/" + approvalId
                    : "redirect:/lms/staff/approvals";
        }

        try {
            LctApplyDetailDTO lectureAssignmentInfo = service.readLectureAssignmentInfo(lctApplyId);
            if (lectureAssignmentInfo == null) {
                redirectAttributes.addFlashAttribute("errorMessage", lctApplyId + "에 대한 강의 신청 정보를 찾을 수 없습니다.");
                return "redirect:/lms/staff/approvals/" + approvalId;
            }

            // ⭐ 1. 요청된 강의 시간표 데이터를 서비스에서 조회합니다.
            // 강의 시간표 정보는 별도의 DTO 리스트로 존재한다고 가정합니다.
            // RoomScheduleDetailDTO를 재활용하거나, 별도의 LectureTimeDTO 리스트일 수 있습니다.
            // 여기서는 `RoomScheduleDetailDTO`가 요일(dayOfWeek), 교시(timeSlot)를 포함한다고 가정합니다.
            List<RoomScheduleDetailDTO> requiredScheduleList = service.readRequiredLectureTimes(lctApplyId);

            List<PlaceInfo> lectureRooms = service.readAllRooms();

            model.addAttribute("lectureAssignmentInfo", lectureAssignmentInfo);

            ObjectMapper mapper = new ObjectMapper();

            // -----------------------------
            // ✅ 강의실 목록 JSON 안전하게 전달
            // -----------------------------
            String lectureRoomsJson = "[]";
            try {
                lectureRoomsJson = mapper.writeValueAsString(lectureRooms);
            } catch (Exception e) {
                log.error("강의실 JSON 변환 실패", e);
            }
            model.addAttribute("lectureRoomsJson", lectureRoomsJson);

            // -----------------------------
            // ⭐ 2. 요청 시간표 JSON 안전하게 전달 (새로운 Model 속성)
            // -----------------------------
            String requiredScheduleJson = "[]";
            try {
                requiredScheduleJson = mapper.writeValueAsString(requiredScheduleList);
            } catch (Exception e) {
                log.error("요청 시간표 JSON 변환 실패", e);
            }
            model.addAttribute("requiredScheduleJson", requiredScheduleJson);

            return "staff/lectureRoom/assignmentDashboard";

        } catch (Exception e) {
            log.error("강의실 배정 폼 로드 중 오류 발생 - LctApplyId: {}", lctApplyId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "강의실 배정 폼 로드 중 시스템 오류가 발생했습니다.");
            return "redirect:/lms/staff/approvals/" + approvalId;
        }
    }

    // -------------------------------------------
    // 강의실 배정 임시 저장
    // -------------------------------------------
    @PostMapping("/saveAssignment")
    public ResponseEntity<Map<String, Object>> saveAssignment(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        String lctApplyId = (String) requestData.get("lctApplyId");
        String placeCd = (String) requestData.get("placeCd");
        String timeblockCdsString = (String) requestData.get("timeblockCdsString");

        if (lctApplyId == null || placeCd == null || timeblockCdsString == null ||
            lctApplyId.isEmpty() || placeCd.isEmpty() || timeblockCdsString.isEmpty()) {

            response.put("status", "FAIL");
            response.put("message", "필수 배정 정보가 누락되었습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            boolean success = service.saveAssignmentInfo(requestData);

            if (success) {
                response.put("status", "SUCCESS");
                response.put("message", "강의실 및 시간대 배정 정보가 성공적으로 임시 저장되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "FAIL");
                response.put("message", "배정 정보 저장에 실패했습니다. (DB 오류 또는 중복 배정)");
                return ResponseEntity.internalServerError().body(response);
            }
        } catch (Exception e) {
            log.error("강의실 배정 임시 저장 중 예외 발생: LctApplyId={}", lctApplyId, e);
            response.put("status", "FAIL");
            response.put("message", "서버 처리 중 예외가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // -------------------------------------------
    // 강의실 CRUD (AJAX/API)
    // -------------------------------------------
    @GetMapping("/rooms/{placeCd}")
    @ResponseBody
    public ResponseEntity<?> readRoomDetail(@PathVariable String placeCd) {
        try {
            PlaceDetailDTO roomDetail = service.readRoomDetail(placeCd);
            return roomDetail != null ? ResponseEntity.ok(roomDetail) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("강의실 상세 정보 조회 오류: {}", placeCd, e);
            return ResponseEntity.internalServerError().body("강의실 상세 정보를 불러오는 중 오류 발생.");
        }
    }

    @PostMapping("/rooms")
    @ResponseBody
    public ResponseEntity<String> registerRoom(@RequestBody PlaceDetailDTO placeDetailDTO) {
        try {
            service.createNewRoom(placeDetailDTO);
            return ResponseEntity.ok("강의실이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("강의실 등록 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("강의실 등록 실패: " + e.getMessage());
        }
    }

    @PutMapping("/rooms/{placeCd}")
    @ResponseBody
    public ResponseEntity<String> updateRoom(@PathVariable String placeCd, @RequestBody PlaceDetailDTO placeDTO) {
        try {
            placeDTO.setPlaceCd(placeCd);
            service.modifyRoom(placeDTO);
            return ResponseEntity.ok("강의실 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("강의실 수정 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("강의실 정보 수정 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/rooms/{placeCd}")
    @ResponseBody
    public ResponseEntity<String> deleteRoom(@PathVariable String placeCd) {
        try {
            service.deleteRoom(placeCd);
            return ResponseEntity.ok("강의실이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("강의실 삭제 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("강의실 삭제 실패: " + e.getMessage());
        }
    }

    @GetMapping("/rooms/{placeCd}/schedule")
    @ResponseBody
    public ResponseEntity<List<RoomScheduleDetailDTO>> getRoomSchedule(@PathVariable String placeCd,
                                                                       @RequestParam("yearTermCd") String yearTermCd) {
        try {
            List<RoomScheduleDetailDTO> scheduleList = service.readRoomSchedule(placeCd, yearTermCd);
            return ResponseEntity.ok(scheduleList);
        } catch (Exception e) {
            log.error("강의실 시간표 조회 오류: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
