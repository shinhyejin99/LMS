package kr.or.jsu;

import org.mybatis.spring.annotation.MapperScan;
import kr.or.jsu.lms.professor.service.ApprovalService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan; // Added this import

@SpringBootApplication
@MapperScan(basePackages = {"kr.or.jsu.lms.professor.approval.mapper", "kr.or.jsu.mybatis.mapper", "kr.or.jsu.lms.professor.mapper", "kr.or.jsu.lms.professor.lecture.mapper"}) // Added MapperScan
@ComponentScan(basePackages = {"kr.or.jsu", "kr.or.jsu.lms.professor.mapper"}) // Added this annotation
public class BootSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootSampleApplication.class, args);
	}

    @Bean
    ApprovalService approvalService() {
        return new ApprovalService();
    }

}
