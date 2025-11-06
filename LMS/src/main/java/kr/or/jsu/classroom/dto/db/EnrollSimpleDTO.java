package kr.or.jsu.classroom.dto.db;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.core.dto.info.StudentInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 수강 하나와, 그 수강생에 대한 정보를 가져오는 DTO
 * @author 송태호
 * @since 2025. 10. 6.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 6.     	송태호	          최초 생성
 *
 * </pre>
 */
@Data
@EqualsAndHashCode(of = "stuEnrollLctInfo")
public class EnrollSimpleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private StuEnrollLctInfo stuEnrollLctInfo;
	private StudentInfo studentInfo;
}
