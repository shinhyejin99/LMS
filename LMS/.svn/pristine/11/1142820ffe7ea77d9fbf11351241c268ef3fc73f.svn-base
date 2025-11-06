package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StuScholarDetailVO;

@Mapper
public interface StuScholarDetailMapper {

    // 단건 조회
    StuScholarDetailVO selectStuScholarDetail(String scholarshipId);

    // 전체 조회
    List<StuScholarDetailVO> selectStuScholarDetailList();

    // 등록
    int insertStuScholarDetail(StuScholarDetailVO stuScholarDetail);

    // 수정
    int updateStuScholarDetail(StuScholarDetailVO stuScholarDetail);

    // 삭제
    int deleteStuScholarDetail(String scholarshipId);
}
