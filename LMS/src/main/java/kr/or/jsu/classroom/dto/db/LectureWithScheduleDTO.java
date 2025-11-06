package kr.or.jsu.classroom.dto.db;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author 송태호
 * @since 2025. 9. 30.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 30.     	송태호	          최초 생성
 *
 * </pre>
 */
@Data
@EqualsAndHashCode(of = "lectureId")
public class LectureWithScheduleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private String scheduleJson;
}
