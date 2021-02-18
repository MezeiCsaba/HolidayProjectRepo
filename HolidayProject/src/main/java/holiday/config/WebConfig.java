package holiday.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("auth/login");
//		registry.addViewController("userInfoPage").setViewName("/userInfoPage");
//		registry.addViewController("/new_userevent").setViewName("new_userevent");
//		registry.addViewController("/registration").setViewName("registration");
//		registry.addViewController("/holidayEventCalendar").setViewName("holidayEventCalendar");
//		registry.addViewController("/userInfoPage").setViewName("userInfoPage");
		
		
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
	}
}
