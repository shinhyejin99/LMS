package kr.or.jsu.dto.db.subject;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.SubjectInfo;
import kr.or.jsu.dto.info.approval.ApprovalInfo;
import kr.or.jsu.dto.info.lecture.apply.LctOpenApplyInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 강의신청과 관련된 정보를 가져옵니다. <br>
 * (강의신청 + 강의신청 대상 과목 + 강의신청에 대한 승인)
 * 
 * @author 송태호
 * @since 2025. 10. 29.
 *
 */
@Data
@EqualsAndHashCode(of = "lctOpenApplyInfo")
public class LctOpenApplySimpleDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private LctOpenApplyInfo lctOpenApplyInfo;
	private SubjectInfo subjectInfo;
	private ApprovalInfo approvalInfo;
}