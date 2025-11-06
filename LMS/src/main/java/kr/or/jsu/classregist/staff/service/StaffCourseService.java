package kr.or.jsu.classregist.staff.service;

import java.util.List;
import java.util.Map;

import kr.or.jsu.classregist.dto.LectureListDTO;
import kr.or.jsu.classregist.dto.StaffCourseSearchDTO;
import kr.or.jsu.classregist.dto.StaffCourseStatsDTO;

/**
 * 교직원 수강신청 관리 Service
 * @author 김수현
 * @since 2025. 10. 27.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 27.     	김수현	          최초 생성
 *
 * </pre>
 */
public interface StaffCourseService {
	/**
     * 강의 통계 조회
     * @param yeartermCd 학기 코드
     * @return 통계 정보
     */
	 public StaffCourseStatsDTO getCourseStats(String yeartermCd);

    /**
     * 강의 목록 조회 (검색 + 페이징)
     * @param searchDTO 검색 조건
     * @return 강의 목록
     */
	 public List<LectureListDTO> getCourseList(StaffCourseSearchDTO searchDTO);

    /**
     * 강의 총 개수
     * @param searchDTO 검색 조건
     * @return 총 개수
     */
	 public int getCourseCount(StaffCourseSearchDTO searchDTO);

    /**
     * 수강신청 확정
     * @param yeartermCd 학기 코드
     * @return 확정된 건수
     */
	 public int confirmEnrollment(String yeartermCd);

    /**
     * 수강신청 통계 조회 (확정용)
     * @param yeartermCd 학기 코드
     * @return 통계 정보
     */
	 public Map<String, Object> getApplyStatistics(String yeartermCd);
}
