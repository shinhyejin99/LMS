package kr.or.jsu.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import kr.or.jsu.vo.UnivAffilHistoryVO;

@Mapper
public interface UnivAffilHistoryMapper {

    int insertUnivAffilHistory(UnivAffilHistoryVO univAffilHistory);
    
    UnivAffilHistoryVO selectUnivAffilHistoryByCd(String affilChangeCd); 
    
    List<UnivAffilHistoryVO> selectAllUnivAffilHistories();
    
    int updateUnivAffilHistory(UnivAffilHistoryVO univAffilHistory);
    
    int deleteUnivAffilHistory(String affilChangeCd); 
}
