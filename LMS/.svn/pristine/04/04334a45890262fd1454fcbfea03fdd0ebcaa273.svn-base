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
@EqualsAndHashCode(of = "enrollId")
public class StuObjectionVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String enrollId;
	
	@NotBlank
	@Size(max = 1000)
	private String objectionDetail;
	
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String attachFileId;
	
	@NotNull
	private LocalDateTime objectionAt;
	
	@Size(max = 1000)
	private String replyComment;
}
