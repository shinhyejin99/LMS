package kr.or.jsu.classregist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 교수) 강의 수강 통계 DTO
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
public class LectureEnrollStatsDTO {
	private int totalLectures;      // 총 강의 수
    private int totalStudents;      // 총 학생 수
    private double avgEnrollRate;       // 평균 수강률
    private int currentEnroll;      // 현재 수강 인원 (특정 강의용)
    private int maxCap;             // 정원 (특정 강의용)
}
