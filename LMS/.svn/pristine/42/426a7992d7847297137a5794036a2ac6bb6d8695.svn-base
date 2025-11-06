package kr.or.jsu.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "studentNo")
public class StuGraduationVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]*$")
    @Size(max = 15)
    private String studentNo;

    @Size(max = 9)
    private String expectedYeartermCd;

    @Size(max = 9)
    private String graduateYeartermCd;

    private BigDecimal graduationGpa;

    @Size(max = 50)
    private String degreeNo;

    private LocalDateTime updateAt;
}
