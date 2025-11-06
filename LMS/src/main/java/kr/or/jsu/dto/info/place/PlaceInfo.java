package kr.or.jsu.dto.info.place;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "placeCd")
public class PlaceInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String placeCd;
	private String parentCd;
	private String placeName;
	private LocalDateTime createAt;
	private LocalDateTime modifyAt;
	private String usingYn;
	private String addrId;
	private String placeTypeCd;
	private Integer capacity;
	private String placeUsageCd;
	
	// 계산으로 추가되는 컬럼.
	// 한 주당 100개인 시간블록이 몇 블록 이용되고 있는지.
	private int usedBlocks;
}
