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
@EqualsAndHashCode(of = "lctPostId")
public class LctPostVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String lctPostId;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 15)
	private String lectureId;

	@NotBlank
	@Size(max = 100)
	private String title;

	@NotBlank
	@Size(max = 1000)
	private String content;

	@NotNull
	private LocalDateTime createAt;

	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String attachFileId;

	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String deleteYn;
	
	@NotNull
	private String postType;
	
	@NotNull
	@Pattern(regexp = "^[YN]$")
	private String tempSaveYn;
	
	private LocalDateTime revealAt;
}
