package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.PrfCareerVO;

@Mapper
public interface PrfCareerMapper {

	// 교수 경력 등록
	public int insertPrfCareer(PrfCareerVO prfCareer);

	// 교수 경력 목록 조회
	public List<PrfCareerVO> selectPrfCareerList();

	// 특정 교수 경력 조회 
	public PrfCareerVO selectPrfCareer(String careerName);

	// 교수 경력 수정
	public int updatePrfCareer(PrfCareerVO prfCareer);

	// 교수 경력 삭제
	public int deletePrfCareer(String careerName);
}
