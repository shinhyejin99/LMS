package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.PlaceEquipVO;

@Mapper
public interface PlaceEquipMapper {

	// 장비 등록
	public int insertPlaceEquip(PlaceEquipVO placeEquip);

	// 장비 목록 조회
	public List<PlaceEquipVO> selectPlaceEquipList();

	// 특정 장비 조회
	public PlaceEquipVO selectPlaceEquip(String equipCd);

	// 장비 정보 수정
	public int updatePlaceEquip(PlaceEquipVO placeEquip);

	// 장비 삭제
	public int deletePlaceEquip(String equipCd);
}
