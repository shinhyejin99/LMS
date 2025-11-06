package kr.or.jsu.classroom.dto.response.statistics;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureGradeDistributionResp {
    private String lectureId;
    private String lectureName;
    private Map<String, Integer> gradeDistribution;
}
