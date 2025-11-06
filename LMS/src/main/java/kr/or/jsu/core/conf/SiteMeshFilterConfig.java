package kr.or.jsu.core.conf;

import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 *
 * @author 정태일
 * @since 2025. 9. 26.
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      	수정자      수정내용
 *  -----------   	--------    ---------------------------
 *  2025. 9. 26.    김수현	    sitemesh 채용정보 게시판 제외 코드 추가
 *  2025. 9. 29.    송태호	    devtemp, classroom/api 등 제외
 *  2025. 10.22		신혜진		교과목 상세모달 제외
 * </pre>
 */
@Configuration
public class SiteMeshFilterConfig {
	@Bean
	FilterRegistrationBean<ConfigurableSiteMeshFilter> sitemeshFilter(){
		ConfigurableSiteMeshFilter filter = ConfigurableSiteMeshFilter.create(builder ->
			builder.setDecoratorPrefix("/WEB-INF/decorators/")
//					.addDecoratorPath("/classroom/**", "classroom-layout.jsp")
//					.addDecoratorPath("/portal/**", "portal-layout.jsp")
					.addDecoratorPath("/**", "main-layout.jsp")


					.addExcludedPath("/ajax/**")
					.addExcludedPath("/login")
					.addExcludedPath("/portal")
					.addExcludedPath("/portal/**")
					.addExcludedPath("/devtemp/**")
					.addExcludedPath("/classroom")
					.addExcludedPath("/classroom/**")
					.addExcludedPath("**/staffSubjects/detail/**")
					.addExcludedPath("**/staffSubjects/modify/**")

		);
		FilterRegistrationBean<ConfigurableSiteMeshFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(filter);
		registration.setOrder(Ordered.LOWEST_PRECEDENCE - 200);
		registration.addUrlPatterns("/*");
		return registration;
	}
}
