package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StuCounselVO;

@Mapper
public interface StuCounselMapper {

    // 단건 조회
    StuCounselVO selectStuCounsel(String counselId);

    // 전체 조회
    List<StuCounselVO> selectStuCounselList();

    // 등록
    int insertStuCounsel(StuCounselVO stuCounsel);

    // 수정
    int updateStuCounsel(StuCounselVO stuCounsel);

    // 삭제
    int deleteStuCounsel(String counselId);

    // 교수 번호로 상담 요청 목록 조회
    List<Map<String, Object>> selectCounselingRequestListByProfessorNo(String professorNo);

    // 학생 번호로 상담 요청 목록 조회
    List<Map<String, Object>> selectCounselingRequestListByStudentNo(String studentNo);
}
