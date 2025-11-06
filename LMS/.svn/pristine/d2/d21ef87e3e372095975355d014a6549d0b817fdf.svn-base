package kr.or.jsu.classregist.professor.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.classregist.dto.LectureEnrollStatsDTO;
import kr.or.jsu.classregist.dto.LectureStudentDTO;
import kr.or.jsu.classregist.dto.ProfLectureStuListDTO;
import kr.or.jsu.classregist.dto.StudentListResponseDTO;
import kr.or.jsu.classregist.professor.service.ProfLectRegistService;
import kr.or.jsu.mybatis.mapper.ProfLectRegistMapper;
import kr.or.jsu.mybatis.mapper.UnivYeartermMapper;
import kr.or.jsu.vo.UnivYeartermVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 교수) 수강신청 관련 서비스 구현체
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
 *	2025. 10. 21.		김수현			d3, 학년도학키 관련 메서드 추가
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfLectRegistServiceImpl implements ProfLectRegistService {

	private final ProfLectRegistMapper profLectRegistMapper;
	private final UnivYeartermMapper univYeartermMapper;

	@Override
	public List<ProfLectureStuListDTO> getProfessorLectures(String professorNo, String yeartermCd) {
		log.info("교수 강의 목록 조회 - 교번: {}, 학기: {}", professorNo, yeartermCd);
        return profLectRegistMapper.selectProfessorLectures(professorNo, yeartermCd);
	}

	@Override
	public StudentListResponseDTO getLectureStudentsWithPaging(
	        String lectureId, int page, int pageSize) {

	    // 총 학생 수 조회
	    int totalCount = profLectRegistMapper.countLectureStudents(lectureId);
	    int totalPages = (int) Math.ceil((double) totalCount / pageSize);

	    // offset 계산(페이징)
	    int offset = (page - 1) * pageSize;

	    // 페이징된 학생 목록 조회
	    List<LectureStudentDTO> students =
	        profLectRegistMapper.selectLectureStudentsPaging(lectureId, offset, pageSize);

	    // Response DTO 생성
	    return StudentListResponseDTO.builder()
	            .students(students)
	            .currentPage(page)
	            .totalPages(totalPages)
	            .totalCount(totalCount)
	            .pageSize(pageSize)
	            .build();
	}

	@Override
	public LectureEnrollStatsDTO getLectureStats(String professorNo, String yeartermCd) {
		log.info("강의 통계 조회 - 교번: {}, 학기: {}", professorNo, yeartermCd);
        return profLectRegistMapper.selectLectureStats(professorNo, yeartermCd);
	}

	@Override
	public LectureEnrollStatsDTO getLectureEnrollInfo(String lectureId) {
		 log.info("강의 수강 정보 조회 - 강의ID: {}", lectureId);
	     return profLectRegistMapper.selectLectureEnrollInfo(lectureId);
	}

	/**
	 * D3 시각화에 필요한 통계 및 강의 목록 데이터를 모두 취합하여 반환하는 메소드
	 */
	@Override
	public Map<String, Object> getProfessorLectureStats(String professorNo, String yeartermCd) {

		// 전체 통계 조회(도넛 차트 데이터)
		LectureEnrollStatsDTO stats = profLectRegistMapper.selectLectureStats(professorNo, yeartermCd);

		// 개별 강의 목록 조회(막대 차트 데이터)
		List<ProfLectureStuListDTO> lectureList = profLectRegistMapper.selectProfessorLectures(professorNo, yeartermCd);

		Map<String, Object> result = new HashMap<>();
		result.put("stats", stats);
		result.put("lectureList", lectureList);
		return result;
	}

	/**
	 * 학년도 학기 코드 가져오기(1학기 앞선 것까지만)
	 */
	@Override
	public List<UnivYeartermVO> getAvailableYearTerms(LocalDate limitDate) {
		return univYeartermMapper.selectAvailableYearTerms(limitDate);
	}
}
