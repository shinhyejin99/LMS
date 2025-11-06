package kr.or.jsu.vo;

import java.io.Serializable;

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
@EqualsAndHashCode(of = {"fileOrder", "fileId"})
public class FileDetailVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	private Integer fileOrder;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]*$")
	@Size(max = 50)
	private String fileId;

	@NotBlank
	@Size(max = 400)
	private String originName;

	@NotBlank
	@Size(max = 100)
	private String extension;

	@NotBlank
	@Size(max = 400)
	private String saveDir;

	@NotNull
	private Long fileSize;

	@NotBlank
	@Size(max = 144)
	private String saveName;
}
