package kr.or.jsu.classroom.dto.response.ender;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TaskWeightAndStatusResp implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String taskType;       // 과제 유형 (INDIV / GROUP)
    private String taskId;         // 과제 ID
    private String lectureId;      // 강의 ID
    private String taskName;       // 과제명
    private LocalDateTime startAt; // 과제 시작일
    private LocalDateTime endAt;   // 과제 마감일
    private BigDecimal weightValue;// 가중치 (없으면 0)
    private String closedYn;       // 마감 여부 (Y/N)
}