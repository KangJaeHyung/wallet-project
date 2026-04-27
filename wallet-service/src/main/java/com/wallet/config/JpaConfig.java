package com.wallet.config;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaConfig {

	@Bean
	public AuditorAware<String> auditorAware() {
		return () -> Optional.of("system");
	}

	@Bean
	public DateTimeProvider auditingDateTimeProvider(Clock appClock) {
		return () -> Optional.of(LocalDateTime.now(appClock));
	}
}
