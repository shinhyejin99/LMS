package kr.or.jsu.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.or.jsu.vo.DeptConditionVO;

/**
 * 학과별 조건 Mapper
 * @author 김수현
 * @since 2025. 10. 14.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 14.     	김수현	          최초 생성
 *
 * </pre>
 */
@Mapper
public interface DeptConditionMapper {

	public int insertDeptCondition(DeptConditionVO deptCondition);
	public List<DeptConditionVO> selectDeptConditionList();
	public int updateDeptCondition(DeptConditionVO deptCondition);
	public int deleteDeptCondition(String deptCondId);

	/**
     * 학과별 조건 값 조회 (NEEDED_VALUE)
     * @param univDeptCd 학과코드
     * @param conditionCd 조건코드
     * @return 조건값
     */
    public String selectConditionValue(
        String univDeptCd,
        String conditionCd
    );
}
