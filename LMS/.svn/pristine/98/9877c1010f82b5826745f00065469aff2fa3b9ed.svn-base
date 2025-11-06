package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.classregist.dto.LectureEnrollStatsDTO;
import kr.or.jsu.classregist.dto.LectureStudentDTO;
import kr.or.jsu.classregist.dto.ProfLectureStuListDTO;

/**
 * 교수) 수강신청 관리 mapper
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
 *
 * </pre>
 */
@Mapper
public interface ProfLectRegistMapper {
	/**
     * 교수의 개설 강의 목록 조회
     */
    public List<ProfLectureStuListDTO> selectProfessorLectures(
            @Param("professorNo") String professorNo,
            @Param("yeartermCd") String yeartermCd);

    /**
     * 학생 수 카운트
     * @param lectureId
     * @return
     */
    public int countLectureStudents(String lectureId);

    /**
     * 특정 강의의 수강 학생 목록 조회
     */
    public List<LectureStudentDTO> selectLectureStudentsPaging(@Param("lectureId") String lectureId,@Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 교수의 강의 통계 조회
     */
    public LectureEnrollStatsDTO selectLectureStats(
            @Param("professorNo") String professorNo,
            @Param("yeartermCd") String yeartermCd);

    /**
     * 특정 강의의 현재 수강 인원 조회
     */
    public LectureEnrollStatsDTO selectLectureEnrollInfo(@Param("lectureId") String lectureId);
}
