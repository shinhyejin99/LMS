package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StuApplyLctVO;

@Mapper
public interface StuApplyLctMapper {

    // 단건 조회
    StuApplyLctVO selectStuApplyLct(String applyId);

    // 전체 조회
    List<StuApplyLctVO> selectStuApplyLctList();

    // 등록
    int insertStuApplyLct(StuApplyLctVO stuApplyLct);

    // 수정
    int updateStuApplyLct(StuApplyLctVO stuApplyLct);

    // 삭제
    int deleteStuApplyLct(String applyId);
}
