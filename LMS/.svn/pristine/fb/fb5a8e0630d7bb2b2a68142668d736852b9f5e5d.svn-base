package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.classregist.dto.LectureListDTO;
import kr.or.jsu.classregist.dto.StaffCourseSearchDTO;
import kr.or.jsu.classregist.dto.StaffCourseStatsDTO;

/**
 * 교직원) 수강신청 종료(수강데이터 옮기기) mapper
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
@Mapper
public interface StaffCourseMapper {
	/**
     * 강의 통계 조회
     * @param yeartermCd 학기 코드
     * @return 통계 정보
     */
    public StaffCourseStatsDTO selectCourseStats(@Param("yeartermCd") String yeartermCd);

    /**
     * 강의 목록 조회 (검색 + 페이징)
     * @param searchDTO 검색 조건
     * @return 강의 목록
     */
    public List<LectureListDTO> selectCourseList(@Param("searchDTO") StaffCourseSearchDTO searchDTO);

    /**
     * 강의 총 개수 (검색 결과 카운트)
     * @param searchDTO 검색 조건
     * @return 총 개수
     */
    public int countCourseList(@Param("searchDTO") StaffCourseSearchDTO searchDTO);

    /**
     * 수강신청 확정 (STU_APPLY_LCT(수강신청 테이블) => STU_ENROLL_LCT(학생_수강 테이블))
     * @param yeartermCd 학기 코드
     * @return 확정된 건수
     */
    public int confirmEnrollment(@Param("yeartermCd") String yeartermCd);

    /**
     * 수강신청 통계 조회 (확정용)
     * @param yeartermCd 학기 코드
     * @return 통계 정보 (Map)
     */
    public Map<String, Object> getApplyStatistics(@Param("yeartermCd") String yeartermCd);

    /**
     * 수강신청한 학생 조회
     * @param yeartermCd
     * @return
     */
	public List<String> selectConfirmedStudents(String yeartermCd);

	/**
	 * 수강과목 담당 교수 조회
	 * @param yeartermCd
	 * @return
	 */
	public List<String> selectProfessorsForTerm(String yeartermCd);
}
