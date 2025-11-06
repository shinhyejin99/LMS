package kr.or.jsu.classroom.dto.info;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"lectureId", "placeCd", "timeblockCd"})

public class LctRoomScheduleInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String lectureId;
	private String placeCd;
	private String timeblockCd;
	
	private String placeName;
}
