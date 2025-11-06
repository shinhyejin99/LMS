package kr.or.jsu.classroom.dto.db;

import java.io.Serializable;

import kr.or.jsu.classroom.dto.info.StuEnrollLctInfo;
import kr.or.jsu.core.dto.info.StudentInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author 송태호
 * @since 2025.10.01
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      			수정자           수정내용
 *  -----------   	-------------    ---------------------------
 *  2025.10.01	     	송태호	          최초 생성
 *
 * </pre>
 */
@Data
@EqualsAndHashCode(of = "studentInfo")
public class StudentAndEnrollDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private StudentInfo studentInfo;
	private StuEnrollLctInfo stuEnrollLctInfo;
}