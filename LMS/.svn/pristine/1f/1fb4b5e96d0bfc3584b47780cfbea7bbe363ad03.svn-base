package kr.or.jsu.classroom.dto.request;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupTaskCreateReq implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	private String grouptaskName;

	@NotBlank
	private String grouptaskDesc;

	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endAt;

	private String attachFileId;

	@Valid
	private List<Groups> groups;

	@Data
	@NoArgsConstructor
	public static class Groups implements Serializable {
		private static final long serialVersionUID = 1L;

		@NotBlank
		private String groupName;

		private String leaderEnrollId;

		// enrollId 목록
		private List<String> crews;
	}
}
