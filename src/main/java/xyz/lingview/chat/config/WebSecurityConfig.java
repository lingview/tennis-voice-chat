package xyz.lingview.chat.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.lingview.chat.security.CachedBodyHttpServletRequestFilter;
import xyz.lingview.chat.security.JwtRequestFilter;
import xyz.lingview.chat.security.SecurityInterceptor;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {

    @Autowired
    private SecurityInterceptor securityInterceptor;

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }


    @Bean
    public FilterRegistrationBean<CachedBodyHttpServletRequestFilter> cachedBodyFilter() {
        FilterRegistrationBean<CachedBodyHttpServletRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CachedBodyHttpServletRequestFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtFilterRegistration(JwtRequestFilter jwtRequestFilter) {
        FilterRegistrationBean<JwtRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(jwtRequestFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(2);
        return registration;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register");
    }
}
