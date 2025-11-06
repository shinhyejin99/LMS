package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.jsu.vo.CommonCodeVO;

/**
 *
 * @author 송태호
 * @since 2025. 9. 28.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 28.     	송태호	          최초 생성
 *  2025. 10. 17.		김수현			수강신청)학년별 최대 학점 조회 추가
 *  2025. 10. 24.		김수현			챗봇) 현재 학기 및 다음 학기 조회 추가
 * </pre>
 */
@Mapper
public interface CommonCodeMapper {

	/**
	 * 공통분류코드(commonSortCode)를 넣으면 하위 공통코드와 코드데이터를 가져옵니다.
	 * @return
	 */
	public List<CommonCodeVO> selectCommonCodeList(String commonSortCode);


	/**
	 * 현재 학기의 YEARTERM_CD 조회
	 * @return
	 */
	public String selectCurrentYearterm();

	/**
	 * 학년+학기별 최대 학점 조회
	 * @param gradeCd 학년코드 (예: "2")
	 * @param termCd 학기코드 (예: "1")
	 * @return 최대 학점
	 */
	public Integer selectMaxCreditByGrade(@Param("gradeCd") String gradeCd,
	                                @Param("termCd") String termCd);

    /**
     * 다음 학기 조회
     * @param currentYearterm 현재 학기
     * @return
     */
    public String selectNextYearterm(@Param("currentYearterm") String currentYearterm);

    /**
     * yearterm_cd를 학기 코드로 변환(REG1 =>1, REG2 =>2)
     * @param yeartermCd
     * @return
     */
    public String selectTermCdMapping(@Param("yeartermCd") String yeartermCd);

}
