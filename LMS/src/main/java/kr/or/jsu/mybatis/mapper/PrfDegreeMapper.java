package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.PrfDegreeVO;

@Mapper
public interface PrfDegreeMapper {

	// 학위 등록
	public int insertPrfDegree(PrfDegreeVO prfDegree);

	// 학위 전체 목록 조회
	public List<PrfDegreeVO> selectPrfDegreeList();

	// 특정 학위 조회 
	public PrfDegreeVO selectPrfDegree(String degreeRegiId);

	// 학위 정보 수정
	public int updatePrfDegree(PrfDegreeVO prfDegree);

	// 학위 삭제 
	public int deletePrfDegree(String degreeRegiId);
}
