package kr.or.jsu.lms.professor.service.lecture;

import java.util.List;
import java.util.Map;

/**
 *

 * @since 2025. 9. 25.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -----------   -------------    ---------------------------
 *  2025. 9. 25.     최건우          최초 생성
 *  2025. 10. 1.     최건우           교수 강의 목록 조회 메서드 추가
 * </pre>
 */
public interface ProfLectureService {

    /**
     * 교수 번호, 학년도, 학기를 기준으로 강의 목록을 조회합니다.
     * @param paramMap 교수 번호, 학년도, 학기 정보를 담은 Map
     * @return 강의 목록 (List<Map<String, Object>>)
     */
    List<Map<String, Object>> selectProfLectures(Map<String, Object> paramMap);

    /**
     * 특정 강의의 상세 정보를 조회합니다.
     * @param lectureId 강의 ID
     * @return 강의 상세 정보 (Map<String, Object>)
     */
    Map<String, Object> selectLectureDetails(String lectureId);
}