package kr.or.jsu.lms.professor.service.info;

import kr.or.jsu.dto.ProfessorInfoDTO;
import kr.or.jsu.vo.ProfessorVO;

public interface ProfInfoService {
    
    public ProfessorInfoDTO getProfessorInfoView(String professorNo);
    
    ProfessorVO getProfessorInfo(String professorNo);
    
    boolean updateProfessorInfo(ProfessorVO professorVO);

    int getTotalAdvisedStudentsCount(String professorNo);

    int getLeaveOfAbsenceAdvisedStudentsCount(String professorNo);

    int getWithdrawalAdvisedStudentsCount(String professorNo);

    public java.util.List<kr.or.jsu.vo.LectureVO> getRecentConfirmedLectures(String professorNo);

    public java.util.Map<String, Integer> getAdviseeGradeDistribution(String professorNo);

    public java.util.Map<String, Integer> getAdviseeGradeByYearDistribution(String professorNo);

    public java.util.List<kr.or.jsu.classroom.dto.response.statistics.LectureGradeDistributionResp> getLectureGradeDistributions(String professorNo);
}