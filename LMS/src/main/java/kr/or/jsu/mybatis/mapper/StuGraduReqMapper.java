package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.vo.StuGraduReqVO;

@Mapper
public interface StuGraduReqMapper {

    // 단건 조회
    StuGraduReqVO selectStuGraduReq(String graduReqSubmitId);

    // 전체 조회
    List<StuGraduReqVO> selectStuGraduReqList();

    // 교수 번호로 졸업 요청 목록 조회 (페이지네이션)
    List<StuGraduReqVO> selectStuGraduReqListByProfessorNoPaginated(Map<String, Object> params);

    // 교수의 총 학생 수 조회
    int countStudentsByProfessorNo(Map<String, Object> params);

    // 교수 번호, 학년도, 학기, 졸업요건코드로 졸업 과제 제출 목록 조회
    List<StuGraduReqVO> selectGraduationAssignmentSubmissions(Map<String, Object> paramMap);

    // 학생 번호로 단일 졸업 과제 제출 조회
    List<StuGraduReqVO> selectGraduationAssignmentByStudentNo(Map<String, Object> params);

    // 등록
    int insertStuGraduReq(StuGraduReqVO stuGraduReq);

    // 수정
    int updateStuGraduReq(StuGraduReqVO stuGraduReq);

    // 졸업 요청 상태 업데이트
    int updateStuGraduReqStatus(@Param("graduReqSubmitId") String graduReqSubmitId, @Param("status") String status);

    // 졸업 요청 상태 및 사유 업데이트
    int updateStuGraduReqStatusWithReason(@Param("graduReqSubmitId") String graduReqSubmitId, @Param("status") String status, @Param("reason") String reason);

    // 삭제
    int deleteStuGraduReq(String graduReqSubmitId);

    // 다음 졸업요건 제출 ID 가져오기
    String getNextGraduReqSubmitId();}