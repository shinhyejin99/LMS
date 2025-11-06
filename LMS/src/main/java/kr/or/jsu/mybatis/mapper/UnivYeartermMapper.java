package kr.or.jsu.mybatis.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.UnivYeartermVO;

@Mapper
public interface UnivYeartermMapper {

    public int insertUnivYearterm(UnivYeartermVO univYearterm);

    public UnivYeartermVO selectUnivYeartermByCd(String yeartermCd);

    public List<UnivYeartermVO> selectAvailableYearTerms(LocalDate limitDate);

    public int updateUnivYearterm(UnivYeartermVO univYearterm);

    public int deleteUnivYearterm(String yeartermCd);
}
