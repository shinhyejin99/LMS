package kr.or.jsu.core.common.exception;

import lombok.Getter;

/**
 * PK 검색조건으로 상세 조회시 발생할 예외
 * 
 * @author 정태일
 * @since 2025. 9. 24.

 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 24.     	정태일	       최초 생성
 *
 * </pre>
 */
public class EntityNotFoundException extends OurBusinessException{
	private static final long serialVersionUID = 1L;
	@Getter
	private final Object byPk;

	public EntityNotFoundException(Object byPk) {
		super(String.format("[%s] 조건으로 상세 조회 실패, 해당 레코드 없음.", byPk));
		this.byPk = byPk;
	}
}