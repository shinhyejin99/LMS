package kr.or.jsu.dto.db.place;

import java.io.Serializable;
import java.util.List;

import kr.or.jsu.dto.info.place.PlaceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "placeCd")
public class PlaceBuildingDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String placeCd;
	private PlaceInfo building;
	private List<PlaceInfo> rooms;
}
