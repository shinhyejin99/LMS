package kr.or.jsu.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import kr.or.jsu.dto.StudentLectureResponseDTO;
import org.apache.ibatis.annotations.Param;
import kr.or.jsu.dto.LectureDetailDTO;

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
 *  2025. 10. 31.     	최건우            페이지네이션을 위한 메서드 추가 및 수정
 * </pre>
 */
@Mapper
public interface StuLectureMapper {
    /**
     * 학생의 수강 내역 목록을 조회합니다. (페이지네이션 적용)
     * @param studentNo 학번
     * @param startRow 시작 ROWNUM
     * @param endRow 끝 ROWNUM
     * @return 학생의 수강 내역 목록
     */
    List<StudentLectureResponseDTO> selectStudentLectures(@Param("studentNo") String studentNo, @Param("startRow") int startRow, @Param("endRow") int endRow);

    /**
     * 학생의 전체 수강 내역 수를 조회합니다.
     * @param studentNo 학번
     * @return 학생의 전체 수강 내역 수
     */
    int selectStudentLectureCount(@Param("studentNo") String studentNo);

    /**
     * 학생의 수강 내역 상세를 조회합니다.
     * @param lectureId 강의 ID
     * @param studentNo 학번
     * @return 강의 상세 정보
     */
    LectureDetailDTO selectLectureDetail(@Param("lectureId") String lectureId, @Param("studentNo") String studentNo);

    /**
     * 학생의 현재 수강중인 강의 수를 조회합니다.
     * @param studentNo 학번
     * @return 수강중인 강의 수
     */
    int countInProgressLectures(String studentNo);

    /**
     * 학생의 특정 강의 수강 상태를 업데이트합니다.
     * @param studentNo 학번
     * @param lectureId 강의 ID
     * @param enrollStatusCd 변경할 수강 상태 코드
     * @return 업데이트된 레코드 수
     */
    int updateEnrollStatus(@Param("studentNo") String studentNo, @Param("lectureId") String lectureId, @Param("enrollStatusCd") String enrollStatusCd);

    /**
     * 학생의 특정 강의 수강 상태 코드를 조회합니다.
     * @param studentNo 학번
     * @param lectureId 강의 ID
     * @return 수강 상태 코드 (String)
     */
    String getEnrollStatus(@Param("studentNo") String studentNo, @Param("lectureId") String lectureId);

    /**
     * 학생이 수강 포기한 강의 수를 조회합니다.
     * @param studentNo 학번
     * @return 수강 포기한 강의 수
     */
    int countDroppedLectures(@Param("studentNo") String studentNo);
}