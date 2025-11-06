package kr.or.jsu.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StuMilitaryVO;

@Mapper
public interface StuMilitaryMapper {

	/**
     * 학생 병역 정보 등록
     */
    public int insertMilitary(StuMilitaryVO military);

    /**
     * 학생 병역 정보 조회
     */
    public StuMilitaryVO selectMilitary(String studentNo);

    /**
     * 학생 병역 정보 수정
     */
    public int updateMilitary(StuMilitaryVO military);
}
