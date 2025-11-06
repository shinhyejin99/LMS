package kr.or.jsu.dto.db.subject;

import java.io.Serializable;
import java.util.List;

import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.dto.info.approval.ApprovalInfo;
import kr.or.jsu.dto.info.lecture.apply.LctApplyGraderatioInfo;
import kr.or.jsu.dto.info.lecture.apply.LctApplyWeekbyInfo;
import kr.or.jsu.dto.info.lecture.apply.LctOpenApplyInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 강의신청과 신청 하위 정보를 가져옵니다. <br>
 * (강의신청 + 주차별 계획 + 성적산출기준/비율)
 * 
 * @author 송태호
 * @since 2025. 10. 29.
 *
 */
@Data
@EqualsAndHashCode(of = "lctApplyId")
public class LctOpenApplyDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String lctApplyId;
	private LctOpenApplyInfo lctOpenApplyInfo;
	private SubjectInfo subjectInfo;
	private ApprovalInfo approvalInfo;
	
	private List<LctApplyWeekbyInfo> lctApplyWeekbyInfoList;
	private List<LctApplyGraderatioInfo> applyGraderatioInfoList;
}