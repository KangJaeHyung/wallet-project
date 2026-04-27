package com.wallet.config;

import com.wallet.base.security.JwtAuthenticationFilter;
import com.wallet.base.security.RestAccessDeniedHandler;
import com.wallet.base.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	private final RestAccessDeniedHandler restAccessDeniedHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint,
			RestAccessDeniedHandler restAccessDeniedHandler,
			JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
		this.restAccessDeniedHandler = restAccessDeniedHandler;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	private static final String[] PUBLIC_PATHS = {
			"/error",
			"/actuator/health"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.formLogin(form -> form.disable())
				.httpBasic(basic -> basic.disable())
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(restAuthenticationEntryPoint)
						.accessDeniedHandler(restAccessDeniedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(auth -> auth


					.requestMatchers(PUBLIC_PATHS).permitAll()
						.requestMatchers("/api/auth/signup").permitAll()
						.requestMatchers("/api/auth/login").permitAll()
						.requestMatchers("/api/auth/logout").permitAll()
						.requestMatchers("/api/auth/refresh").permitAll()
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.anyRequest().authenticated());

		return http.build();
	}
}
