package kr.or.jsu.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * 학생 추가전공 Mapper
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
public interface StuExtraMajorMapper {
	/**
     * 복수전공 여부 확인
     */
    boolean hasDoubleMajor(String studentNo);

    /**
     * 부전공 여부 확인
     */
    boolean hasMinorMajor(String studentNo);
}
