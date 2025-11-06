package kr.or.jsu.dto;

import lombok.Data;
/**
 *
 *
 * @author 신혜진
 * @since 2025. 10. 17.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 10. 17.     	신혜진	          최초 생성(강의실/시설)
 *
 * </pre>
 */
@Data
public class PlaceDetailDTO {
	// 97.1 공간 테이블 컬럼
    private String placeCd;   // 공간ID
    private String placeName; // 공간명
//    private String buildingCd; // 건물코드
    private Integer capacity; // 수용인원
//    private String placeType; // 공간유형
//    private String addrId;

    // 공통 컬럼
//    private String usageYn;   // 사용여부
//    private LocalDateTime createdAt; // 등록일시
//    private LocalDateTime updatedAt; // 수정일시
}

