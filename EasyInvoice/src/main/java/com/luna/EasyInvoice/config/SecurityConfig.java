package com.luna.EasyInvoice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.expression.EvaluationContext;

@Configuration
//@EnableWebSecurityextends WebSecurityConfigurerAdapter
//@EnableJpaRepositories
public class SecurityConfig {
	@Bean
	SecurityEvaluationContextExtension securityExtension() {
	    return new SecurityEvaluationContextExtension();
	}
}


