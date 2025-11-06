package kr.or.jsu.classroom.dto.db;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.LectureInfo;
import kr.or.jsu.classroom.dto.info.SubjectInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 어떤 강의와 그 강의의 원본인 과목 정보를 가져오는 DTO.
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
@EqualsAndHashCode(of = "lectureInfo")
public class UnassignedLectureDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LectureInfo lectureInfo;
	private SubjectInfo subjectInfo;
	
	private int scheduledSlots;
}
