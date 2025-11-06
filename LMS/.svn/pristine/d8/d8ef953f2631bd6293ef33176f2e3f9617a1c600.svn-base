package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StuApplyLctLogVO;

@Mapper
public interface StuApplyLctLogMapper {

    // 단건 조회
    StuApplyLctLogVO selectStuApplyLctLog(String lctApplyLogId);

    // 전체 조회
    List<StuApplyLctLogVO> selectStuApplyLctLogList();

    // 등록
    int insertStuApplyLctLog(StuApplyLctLogVO stuApplyLctLog);

    // 수정
    int updateStuApplyLctLog(StuApplyLctLogVO stuApplyLctLog);

    // 삭제
    int deleteStuApplyLctLog(String lctApplyLogId);
}
