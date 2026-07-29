package com.ironhold.config;

import com.ironhold.security.AuthInterceptor;
import com.ironhold.security.WardenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final WardenInterceptor wardenInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor, WardenInterceptor wardenInterceptor) {
        this.authInterceptor = authInterceptor;
        this.wardenInterceptor = wardenInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/", "/login", "/logout", "/about", "/status",
                        "/css/**", "/error", "/actuator/**");

        registry.addInterceptor(wardenInterceptor)
                .addPathPatterns("/admin/**");
    }
}
