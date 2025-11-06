package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.SbjTargetVO;

@Mapper
public interface SbjTargetMapper {

    // 단건 조회
    SbjTargetVO selectSbjTarget(String subjectCd);

    // 전체 조회
    List<SbjTargetVO> selectSbjTargetList();

    // 등록
    int insertSbjTarget(SbjTargetVO sbjTarget);

    // 수정
    int updateSbjTarget(SbjTargetVO sbjTarget);

    // 삭제
    int deleteSbjTarget(String subjectCd);
}
