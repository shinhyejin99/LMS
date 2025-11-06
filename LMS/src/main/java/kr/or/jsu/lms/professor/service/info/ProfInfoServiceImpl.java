package kr.or.jsu.lms.professor.service.info;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.jsu.dto.ProfessorInfoDTO;
import kr.or.jsu.mybatis.mapper.ProfessorMapper;
import kr.or.jsu.mybatis.mapper.UsersMapper;
import kr.or.jsu.vo.LectureVO;
import kr.or.jsu.vo.ProfessorVO;
import kr.or.jsu.vo.UsersVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfInfoServiceImpl implements ProfInfoService {

    private final ProfessorMapper professorMapper;
    private final UsersMapper usersMapper;

    @Override
    public ProfessorInfoDTO getProfessorInfoView(String professorNo) {
        return professorMapper.selectProfessorInfoView(professorNo);
    }

    @Override
    public ProfessorVO getProfessorInfo(String professorNo) {
        return professorMapper.selectProfessor(professorNo);
    }

    @Transactional
    @Override
    public boolean updateProfessorInfo(ProfessorVO professorVO) {
        int usersUpdated = usersMapper.updateUser((UsersVO) professorVO);
        int professorUpdated = professorMapper.updateProfessor(professorVO);
        return usersUpdated > 0 || professorUpdated > 0;
    }

	@Override
	public int getTotalAdvisedStudentsCount(String professorNo) {
		return professorMapper.selectTotalAdvisedStudentsCount(professorNo);
	}

	@Override
	public int getLeaveOfAbsenceAdvisedStudentsCount(String professorNo) {
		return professorMapper.selectLeaveOfAbsenceAdvisedStudentsCount(professorNo);
	}

	@Override
	public int getWithdrawalAdvisedStudentsCount(String professorNo) {
		return professorMapper.selectWithdrawalAdvisedStudentsCount(professorNo);
	}

	@Override
	public List<LectureVO> getRecentConfirmedLectures(String professorNo) {
		return professorMapper.selectRecentConfirmedLectures(professorNo);
	}

    @Override
    public Map<String, Integer> getAdviseeGradeDistribution(String professorNo) {
        List<Map<String, Object>> grades = professorMapper.selectAdviseeGrades(professorNo);
        log.info("Advisee grades from mapper: {}", grades); // Add this log statement
        Map<String, Integer> gradeDistribution = new LinkedHashMap<>();

        // Initialize all possible grades to 0
        gradeDistribution.put("A+", 0);
        gradeDistribution.put("A0", 0);
        gradeDistribution.put("B+", 0);
        gradeDistribution.put("B0", 0);
        gradeDistribution.put("C+", 0);
        gradeDistribution.put("C0", 0);
        gradeDistribution.put("D+", 0);
        gradeDistribution.put("D0", 0);
        gradeDistribution.put("F", 0);

        for (Map<String, Object> gradeInfo : grades) {
            String finalGrade = (String) gradeInfo.get("FINAL_GRADE");
            if (finalGrade != null) {
                gradeDistribution.merge(finalGrade, 1, Integer::sum);
            }
        }
        log.info("Calculated grade distribution: {}", gradeDistribution); // Add this log statement
        return gradeDistribution;
    }

    @Override
    public Map<String, Integer> getAdviseeGradeByYearDistribution(String professorNo) {
        List<Map<String, Object>> gradeYearCounts = professorMapper.selectAdviseeGradeYearCounts(professorNo);
        Map<String, Integer> distribution = new LinkedHashMap<>();

        // Initialize all possible grade years to 0
        for (int i = 1; i <= 4; i++) {
            distribution.put(i + "학년", 0);
        }

        for (Map<String, Object> countInfo : gradeYearCounts) {
            String gradeName = (String) countInfo.get("GRADE_NAME");
            Integer count = ((Number) countInfo.get("COUNT")).intValue(); // Handle potential BigDecimal from Oracle
            if (gradeName != null && count != null) {
                distribution.put(gradeName, count);
            }
        }
        log.info("Calculated advisee grade year distribution: {}", distribution);
        return distribution;
    }

    @Override
    public List<kr.or.jsu.classroom.dto.response.statistics.LectureGradeDistributionResp> getLectureGradeDistributions(String professorNo) {
        List<Map<String, Object>> rawData = professorMapper.selectLectureGradeDistributions(professorNo);
        log.info("Raw lecture grade data from mapper: {}", rawData);

        Map<String, kr.or.jsu.classroom.dto.response.statistics.LectureGradeDistributionResp> lectureMap = new LinkedHashMap<>();

        // Initialize all possible grades to 0 for each lecture
        String[] grades = {"A+", "A0", "B+", "B0", "C+", "C0", "D+", "D0", "F"};

        for (Map<String, Object> item : rawData) {
            String lectureId = (String) item.get("LECTURE_ID");
            String lectureName = (String) item.get("SUBJECT_NAME");
            String finalGrade = (String) item.get("FINAL_GRADE");
            Integer count = ((Number) item.get("COUNT")).intValue();

            lectureMap.computeIfAbsent(lectureId, k -> {
                Map<String, Integer> initialGrades = new LinkedHashMap<>();
                for (String grade : grades) {
                    initialGrades.put(grade, 0);
                }
                return kr.or.jsu.classroom.dto.response.statistics.LectureGradeDistributionResp.builder()
                        .lectureId(lectureId)
                        .lectureName(lectureName)
                        .gradeDistribution(initialGrades)
                        .build();
            }).getGradeDistribution().put(finalGrade, count);
        }
        
        List<kr.or.jsu.classroom.dto.response.statistics.LectureGradeDistributionResp> result = new java.util.ArrayList<>(lectureMap.values());
        log.info("Calculated lecture grade distributions: {}", result);
        return result;
    }
}
