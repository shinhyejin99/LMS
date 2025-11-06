package kr.or.jsu.classregist.professor.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import kr.or.jsu.classregist.dto.LectureEnrollStatsDTO;
import kr.or.jsu.classregist.dto.ProfLectureStuListDTO;
import kr.or.jsu.classregist.dto.StudentListResponseDTO;
import kr.or.jsu.vo.UnivYeartermVO;

/**
 * 교수) 수강신청 관련 service
 * @author 김수현
 * @since 2025. 10. 20.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 20.     	김수현	          최초 생성
 *	2025. 10. 21.		김수현			차트 시각화에 필요한 메서드 추가, 학년도 학기 동적 생성 위한 코드 추가
 * </pre>
 */
public interface ProfLectRegistService {
	/**
     * 교수의 개설 강의 목록 조회
     */
    public List<ProfLectureStuListDTO> getProfessorLectures(String professorNo, String yeartermCd);

    /**
     * 특정 강의의 수강 학생 목록 조회
     */
    public StudentListResponseDTO getLectureStudentsWithPaging(String lectureId, int page, int pageSize);

    /**
     * 교수의 강의 통계 조회
     */
    public LectureEnrollStatsDTO getLectureStats(String professorNo, String yeartermCd);

    /**
     * 특정 강의의 현재 수강 인원 조회
     */
    public LectureEnrollStatsDTO getLectureEnrollInfo(String lectureId);

    /**
     * D3 시각화에 필요한 통계 및 강의 목록 데이터를 모두 취합하여 반환하는 메소드
     * @param professorNo 교번
     * @param yeartermCd 학기코드
     * @return
     */
    public Map<String, Object> getProfessorLectureStats(String professorNo, String yeartermCd);

    /**
     * 학년도 학기 가져오기(현재 학년도 학기보다 1학기 앞선 것까지만)
     * @param limitDate
     * @return
     */
    public List<UnivYeartermVO> getAvailableYearTerms(LocalDate limitDate);
}
