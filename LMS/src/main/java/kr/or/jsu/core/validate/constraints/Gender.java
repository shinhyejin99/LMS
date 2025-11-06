package kr.or.jsu.core.validate.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;

@Target(ElementType.FIELD) // 어노테이션 사용 위치 결정 -> gender 라는 "필드"! 에 사용
/**
 *  Custom Constraint annotation 정의 단계
 *  1. 검증 조건을 표현할 어노테이션 정의
 *  	1) @Target, @Retention 메타어노테이션 필수
 *  	2) @Contraint 메타어노테이션으로 한세트의 ConstraintValidator 의 연결(validatedBy)을 표현함
 *  	3) message, groups, payload 속성이 반드시 필요함.
 *  2. 실제 검증을 수행할 ConstraintValidator 구현체 정의
 *  	1) 한세트의 검증 어노테이션은 generic type 으로 표현함.
 *  	2) 실제 검증은 isValid 메소드에 정의됨.
 */
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 유지 시점 결정 - 런타임 동안~
@Constraint(validatedBy = GenderValidator.class) // 검증은 <- 요 클래스에서 담당한다 라는 뜻으로 이해하기~
public @interface Gender {
	// default를 안 쓰면 vo에서 message를 써줘야함
	String message() default "성별은 M/F 중 선택";
	Class<?>[] groups() default {};
	Class<?>[] payload() default {};
}
