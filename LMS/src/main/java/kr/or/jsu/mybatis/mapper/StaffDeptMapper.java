package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.StaffDeptVO;

@Mapper
public interface StaffDeptMapper {

    // 단건 조회
    StaffDeptVO selectStaffDept(String stfDeptCd);

    // 전체 조회
    List<StaffDeptVO> selectStaffDeptList();

    // 등록
    int insertStaffDept(StaffDeptVO staffDept);

    // 수정
    int updateStaffDept(StaffDeptVO staffDept);

    // 삭제
    int deleteStaffDept(String stfDeptCd);
}
