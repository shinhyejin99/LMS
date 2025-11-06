package kr.or.jsu.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import kr.or.jsu.vo.TimeblockVO;

@Mapper
public interface TimeblockMapper {

    int insertTimeblock(TimeblockVO timeblock);
    
    TimeblockVO selectTimeblockByCd(String timeblockCd);
    
    List<TimeblockVO> selectAllTimeblocks();
    
    int updateTimeblock(TimeblockVO timeblock);
    
    int deleteTimeblock(String timeblockCd);
}
