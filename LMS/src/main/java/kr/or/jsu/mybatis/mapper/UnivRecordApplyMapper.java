package kr.or.jsu.mybatis.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.core.paging.PaginationInfo;
import kr.or.jsu.vo.UnivRecordApplyVO;

@Mapper
public interface UnivRecordApplyMapper {

    int insertUnivRecordApply(UnivRecordApplyVO univRecordApply);
    
    UnivRecordApplyVO selectUnivRecordApplyById(String applyId);
    
    List<UnivRecordApplyVO> selectAllUnivRecordApplies();
    
    int updateUnivRecordApply(UnivRecordApplyVO univRecordApply);
    
    int deleteUnivRecordApply(String applyId);
    
    //학적변동 전체 조회
    public List<Map<String, Object>>selectProfAcademicChangeStatusList(PaginationInfo<?> pagingInfo);

	int selectProfAcademicChangeStatusCount(PaginationInfo<Map<String, Object>> pagingInfo);
}
