package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.LctWeekbyVO;

@Mapper
public interface LctWeekbyMapper {

	// 주차별 강의 정보 등록
	public int insertLctWeekby(LctWeekbyVO lctWeekby);

	// 주차별 강의 정보 전체 목록 조회
	public List<LctWeekbyVO> selectLctWeekbyList();

	// 특정 주차별 강의 정보 조회
	public LctWeekbyVO selectLctWeekby(String lectureId);

	// 주차별 강의 정보 수정
	public int updateLctWeekby(LctWeekbyVO lctWeekby);

	// 주차별 강의 정보 삭제
	public int deleteLctWeekby(String lectureId);
}
