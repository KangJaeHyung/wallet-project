package com.wallet.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wallet.base.web.ApiLoggingInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final List<String> allowedOrigins;
	private final ApiLoggingInterceptor apiLoggingInterceptor;

	public WebConfig(
			@Value("${app.cors.allowed-origins:}") String allowedOrigins,
			ApiLoggingInterceptor apiLoggingInterceptor) {
		this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
				.map(String::trim)
				.filter(StringUtils::hasText)
				.toList();
		this.apiLoggingInterceptor = apiLoggingInterceptor;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		if (allowedOrigins.isEmpty()) {
			return;
		}

		registry.addMapping("/**")
				.allowedOrigins(allowedOrigins.toArray(String[]::new))
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(apiLoggingInterceptor)
				.addPathPatterns("/api/**")
				.excludePathPatterns("/error");
	}
}
