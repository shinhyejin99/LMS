package kr.or.jsu.core.validate.constraints;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<Gender, String>{

	@Override
	public boolean isValid(String targetValue, ConstraintValidatorContext arg1) {
		// 성별 값이 blank가 아니면
		if(StringUtils.isNotBlank(targetValue)) {
			return targetValue.matches("[MF]"); // M or F 중 하나와 일치하면 그 값을 리턴함
		}else {
			return true; 
		}
	}
	
}
