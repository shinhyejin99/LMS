package kr.or.jsu.core.common.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.or.jsu.core.utils.enums.CommonCodeSort;
import kr.or.jsu.vo.CommonCodeVO;

public interface CommonCodeService {
	public List<CommonCodeVO> readCommonCodeList(CommonCodeSort sortCode);

	/**
	 * 현재 학기의 YEARTERM_CD 조회
	 * @return
	 */
	public String getCurrentYearterm();

	/**
	 * 학년+학기별 최대 학점 조회
	 * @param gradeCd 학년코드 (예: "2")
	 * @param termCd 학기코드 (예: "1")
	 * @return 최대 학점
	 */
	public Integer getMaxCreditByGrade(@Param("gradeCd") String gradeCd,
	                                @Param("termCd") String termCd);


}
