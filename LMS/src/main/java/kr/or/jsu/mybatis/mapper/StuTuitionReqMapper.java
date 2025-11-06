package kr.or.jsu.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import kr.or.jsu.vo.StuTuitionReqVO;

@Mapper
public interface StuTuitionReqMapper {

    int insertStuTuitionReq(StuTuitionReqVO stuTuitionReq);
    
    StuTuitionReqVO selectStuTuitionReqById(String tuitionReqId);
    
    List<StuTuitionReqVO> selectAllStuTuitionReqs();
    
    int updateStuTuitionReq(StuTuitionReqVO stuTuitionReq);
    
    int deleteStuTuitionReq(String tuitionReqId);
}
