package kr.or.jsu.mybatis.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import kr.or.jsu.vo.StuTuitionDetailVO;

@Mapper
public interface StuTuitionDetailMapper {

    int insertStuTuitionDetail(StuTuitionDetailVO stuTuitionDetail);
    
    StuTuitionDetailVO selectStuTuitionDetailByKey(String yeartermCd, String studentNo); // 복합 키 가정, 필요 시 수정
    
    List<StuTuitionDetailVO> selectAllStuTuitionDetails();
    
    int updateStuTuitionDetail(StuTuitionDetailVO stuTuitionDetail);
    
    int deleteStuTuitionDetail(String yeartermCd, String studentNo); // 복합 키 가정
}
