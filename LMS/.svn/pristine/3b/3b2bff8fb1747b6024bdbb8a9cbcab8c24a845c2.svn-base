package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.vo.StuScholarshipVO;

@Mapper
public interface StuScholarshipMapper {

    // 단건 조회 (복합키)
    StuScholarshipVO selectStuScholarship(
        @Param("yeartermCd") String yeartermCd,
        @Param("studentNo") String studentNo
    );

    // 전체 조회
    List<StuScholarshipVO> selectStuScholarshipList();

    // 등록
    int insertStuScholarship(StuScholarshipVO stuScholarship);

    // 수정
    int updateStuScholarship(StuScholarshipVO stuScholarship);

    // 삭제 (복합키)
    int deleteStuScholarship(
        @Param("yeartermCd") String yeartermCd,
        @Param("studentNo") String studentNo
    );
}
