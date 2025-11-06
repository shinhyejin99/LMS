package kr.or.jsu.classroom.dto.response.ender;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ExamWeightAndStatusResp implements Serializable {
    private static final long serialVersionUID = 1L;

    private String examId;         // 시험 ID
    private String examName;       // 시험명
    private String examType;       // 시험 타입 (ON/OFF)
    private LocalDateTime startAt; // 응시 시작일시
    private LocalDateTime endAt;   // 응시 마감일시
    private Integer weightValue;// 가중치 (NULL → 0)
    private String closedYn;       // 마감 여부 (Y/N)
}
