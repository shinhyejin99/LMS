package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "enrollId")
@Data
public class EnrollGPAInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String enrollId;
	private String gpaCd;
	private LocalDateTime createAt;
	private LocalDateTime updateAt;
}