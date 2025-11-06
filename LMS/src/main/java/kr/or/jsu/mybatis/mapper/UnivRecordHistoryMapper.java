package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.UnivRecordHistoryVO;

@Mapper
public interface UnivRecordHistoryMapper {

    int insertUnivRecordHistory(UnivRecordHistoryVO univRecordHistory);
    
    UnivRecordHistoryVO selectUnivRecordHistoryByCd(String recordChangeCd);
    
    List<UnivRecordHistoryVO> selectAllUnivRecordHistories();
    
    int updateUnivRecordHistory(UnivRecordHistoryVO univRecordHistory);
    
    int deleteUnivRecordHistory(String recordChangeCd); 
}
