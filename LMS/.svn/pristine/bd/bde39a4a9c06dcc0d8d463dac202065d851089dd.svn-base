package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.PrfRoomVO;

@Mapper
public interface PrfRoomMapper {

	// 교수실 등록
	public int insertPrfRoom(PrfRoomVO prfRoom);

	// 교수실 목록 조회
	public List<PrfRoomVO> selectPrfRoomList();

	// 특정 교수실 조회 
	public PrfRoomVO selectPrfRoom(String placeCd);

	// 교수실 정보 수정
	public int updatePrfRoom(PrfRoomVO prfRoom);

	// 교수실 삭제
	public int deletePrfRoom(String placeCd);
}
