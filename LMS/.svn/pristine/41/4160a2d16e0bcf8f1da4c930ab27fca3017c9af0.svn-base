package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StuReviewLctVO;

@Mapper
public interface StuReviewLctMapper {

    // 단건 조회
    StuReviewLctVO selectStuReviewLct(String enrollId);

    // 전체 조회
    List<StuReviewLctVO> selectStuReviewLctList();

    // 등록
    int insertStuReviewLct(StuReviewLctVO stuReviewLct);

    // 수정
    int updateStuReviewLct(StuReviewLctVO stuReviewLct);

    // 삭제
    int deleteStuReviewLct(String enrollId);
}
