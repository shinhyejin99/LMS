package kr.or.jsu.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import kr.or.jsu.vo.StuTermTuitionVO;

@Mapper
public interface StuTermTuitionMapper {

    int insertStuTermTuition(StuTermTuitionVO stuTermTuition);
    
    StuTermTuitionVO selectStuTermTuitionByKey(String yeartermCd, String studentNo);
    
    List<StuTermTuitionVO> selectAllStuTermTuitions();
    
    int updateStuTermTuition(StuTermTuitionVO stuTermTuition);
    
    int deleteStuTermTuition(String yeartermCd, String studentNo);
}
