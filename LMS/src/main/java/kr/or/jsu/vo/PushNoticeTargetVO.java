package kr.or.jsu.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"pushId", "userId"})
public class PushNoticeTargetVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String pushId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String userId;

	@NotNull
	private LocalDateTime receiveAt;

	private LocalDateTime checkAt;
}
