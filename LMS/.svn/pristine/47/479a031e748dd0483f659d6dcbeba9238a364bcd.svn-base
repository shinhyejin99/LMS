package kr.or.jsu.lms.student.service.courseManagement;

import java.util.List;
import kr.or.jsu.dto.StudentLectureResponseDTO;
import kr.or.jsu.dto.LectureDetailDTO;
import kr.or.jsu.dto.PaginatedResponseDTO; // Import the new DTO

/**
 * 
 * @author 송태호
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	최건우            최초 생성
 *  2025. 10. 31.     	최건우            페이지네이션을 위한 메서드 수정
 * </pre>
 */
public interface StuLectureService {
    /**
     * 학생의 수강 내역 목록을 조회합니다. (페이지네이션 적용)
     * @param studentNo 학번
     * @param currentPage 현재 페이지 번호
     * @param pageSize 페이지당 항목 수
     * @return 페이지네이션이 적용된 학생의 수강 내역 목록
     */
    PaginatedResponseDTO<StudentLectureResponseDTO> getStudentLectures(String studentNo, int currentPage, int pageSize);

    /**
     * 학생의 수강 내역 상세를 조회합니다.
     * @param lectureId 강의 ID
     * @param studentNo 학번
     * @return 강의 상세 정보
     */
    LectureDetailDTO getLectureDetail(String lectureId, String studentNo);

    /**
     * 학생의 수강을 포기합니다.
     * @param studentNo 학번
     * @param lectureId 강의 ID
     * @throws IllegalStateException 수강 포기 조건을 만족하지 못할 경우 발생
     */
    void dropLecture(String studentNo, String lectureId);
}
