package kr.or.jsu.classroom.dto.db;

import java.io.Serializable;
import java.util.List;

import kr.or.jsu.classroom.dto.info.LctWeekbyInfo;
import kr.or.jsu.classroom.dto.info.LectureInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author 송태호
 * @since 2025.10.01
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025.10.01	     	송태호	          최초 생성
 *
 * </pre>
 */
@Data
@EqualsAndHashCode(of = "lectureId")
public class LectureWithWeekbyDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lectureId;
	private LectureInfo lectureInfo;
	private List<LctWeekbyInfo> weekbyList;
}
