package kr.or.jsu.core.common.exception.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import kr.or.jsu.core.common.Constants;
import kr.or.jsu.core.common.exception.EntityNotFoundException;
import kr.or.jsu.core.common.exception.OurBusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통 예외 처리 핸들러
 * 
 * @author 정태일
 * @since 2025. 9. 24.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025. 9. 24.     	정태일	       최초 생성
 *
 * </pre>
 */
@Slf4j
@ControllerAdvice
public class CommonExceptionHandleAdvice {
	
	@ExceptionHandler(value = OurBusinessException.class)
	public String ourExceptionHandle(Model model, OurBusinessException ex) {
		model.addAttribute(Constants.LASTEXCEPTION, ex);
		return "error/exceptionView";
	}
	@ExceptionHandler(value = EntityNotFoundException.class)
	public String entityNotFoundExceptionHandle(Model model, EntityNotFoundException ex) {
		model.addAttribute(Constants.LASTEXCEPTION, ex);
		return "forward:/error/ex/"+ex.getClass().getSimpleName();
	}
}
