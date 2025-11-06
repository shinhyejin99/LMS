package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StuObjectionVO;

@Mapper
public interface StuObjectionMapper {

    // 단건 조회
    StuObjectionVO selectStuObjection(String enrollId);

    // 전체 조회
    List<StuObjectionVO> selectStuObjectionList();

    // 등록
    int insertStuObjection(StuObjectionVO stuObjection);

    // 수정
    int updateStuObjection(StuObjectionVO stuObjection);

    // 삭제
    int deleteStuObjection(String enrollId);
}
