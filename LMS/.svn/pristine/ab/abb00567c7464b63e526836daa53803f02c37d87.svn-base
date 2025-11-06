package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StuEntranceVO;

@Mapper
public interface StuEntranceMapper {

    // 단건 조회
    StuEntranceVO selectStuEntrance(String studentNo);

    // 전체 조회
    List<StuEntranceVO> selectStuEntranceList();

    // 등록
    int insertStuEntrance(StuEntranceVO stuEntrance);

    // 수정
    int updateStuEntrance(StuEntranceVO stuEntrance);

    // 삭제
    int deleteStuEntrance(String studentNo);
    
    int insertBatchStuEntrance(List<StuEntranceVO> stuEntranceList);
}