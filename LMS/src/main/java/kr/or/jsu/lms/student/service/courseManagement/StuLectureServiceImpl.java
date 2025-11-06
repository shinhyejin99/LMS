package kr.or.jsu.lms.student.service.courseManagement;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.or.jsu.dto.LectureDetailDTO;
import kr.or.jsu.dto.StudentLectureResponseDTO;
import kr.or.jsu.dto.PaginatedResponseDTO; // Import the new DTO
import kr.or.jsu.mybatis.mapper.StuLectureMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author 송태호
 * @since 2025. 10. 31.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 31.     	최건우	         셀렉트 추가 
 *
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StuLectureServiceImpl implements StuLectureService {

    private final StuLectureMapper stuLectureMapper;

    @Override
    public PaginatedResponseDTO<StudentLectureResponseDTO> getStudentLectures(String studentNo, int currentPage, int pageSize) {
        log.debug("학생 수강 내역 조회 (페이지네이션) - studentNo: {}, currentPage: {}, pageSize: {}", studentNo, currentPage, pageSize);

        int totalElements = stuLectureMapper.selectStudentLectureCount(studentNo);
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        int startRow = (currentPage - 1) * pageSize + 1;
        int endRow = currentPage * pageSize;

        List<StudentLectureResponseDTO> content = stuLectureMapper.selectStudentLectures(studentNo, startRow, endRow);

        return new PaginatedResponseDTO<>(content, currentPage, totalPages, totalElements, pageSize);
    }

    @Override
    public LectureDetailDTO getLectureDetail(String lectureId, String studentNo) {
        return stuLectureMapper.selectLectureDetail(lectureId, studentNo);
    }

    @Override
    public void dropLecture(String studentNo, String lectureId) {
        log.info("수강 포기 요청 - studentNo: {}, lectureId: {}", studentNo, lectureId);

        // 1. 현재 강의의 수강 상태 확인 (백엔드 유효성 검사)
        String currentEnrollStatus = stuLectureMapper.getEnrollStatus(studentNo, lectureId);
        if (currentEnrollStatus == null) {
            throw new IllegalStateException("해당 강의를 찾을 수 없거나 학생이 수강중인 강의가 아닙니다.");
        }

        if ("ENR_DONE".equals(currentEnrollStatus)) {
            throw new IllegalStateException("수강 완료된 강의는 포기할 수 없습니다.");
        }

        // 2. 수강 포기 횟수 확인 및 조건 적용
        int droppedCount = stuLectureMapper.countDroppedLectures(studentNo);
        if (droppedCount >= 2) {
            throw new IllegalStateException("수강 포기는 학기당 2과목까지만 가능합니다. 현재 " + droppedCount + "개 과목을 포기했습니다.");
        }

        // 3. 수강 상태 업데이트
        int updatedRows = stuLectureMapper.updateEnrollStatus(studentNo, lectureId, "ENR_DROP");
        if (updatedRows == 0) {
            throw new IllegalStateException("수강 포기에 실패했습니다. 강의 상태를 업데이트할 수 없습니다.");
        }
        log.info("수강 포기 성공 - studentNo: {}, lectureId: {}", studentNo, lectureId);
    }
}
