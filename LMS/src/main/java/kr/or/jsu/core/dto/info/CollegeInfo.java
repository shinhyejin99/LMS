package kr.or.jsu.core.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "collegeCd")
public class CollegeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String collegeCd;
	private String collegeName;
	private LocalDateTime createAt;
	private LocalDateTime deleteAt;
}
