package kr.or.jsu.dto.request.lms.lecture.apply;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class LctApprovalExecuteReq implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<String> approveIds;
	private String comments;
	private String approvalType; // "APPROVE" or "REJECT"

	public boolean isApproved() {
		return "APPROVE".equals(approvalType);
	}
}
