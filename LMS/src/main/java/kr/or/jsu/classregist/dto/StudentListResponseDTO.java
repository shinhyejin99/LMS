package kr.or.jsu.classregist.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 수강신청 학생 페이징 DTO
 * @author 김수현
 * @since 2025. 10. 20.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 20.     	김수현	          최초 생성
 *
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentListResponseDTO {
	private List<LectureStudentDTO> students;
    private int currentPage;
    private int totalPages;
    private int totalCount;
    private int pageSize;
}
