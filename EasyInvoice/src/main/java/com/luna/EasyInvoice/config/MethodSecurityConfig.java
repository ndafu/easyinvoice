package com.luna.EasyInvoice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.luna.EasyInvoice.service.MyUserDetailsService;


@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@ComponentScan(basePackages = { "com.luna.EasyInvoice.componement" })
@EnableWebSecurity
public class MethodSecurityConfig extends WebSecurityConfigurerAdapter{ 
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	@Autowired
    private AuthenticationEntryPoint authEntryPoint;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authProvider() {
		DaoAuthenticationProvider provider =  new DaoAuthenticationProvider();
		
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
		return provider;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/ressources/**");
		web.ignoring().antMatchers("/build/**");
		web.ignoring().antMatchers("/dist/**");
		web.ignoring().antMatchers("/docs/**");
		web.ignoring().antMatchers("/img/**");
		web.ignoring().antMatchers("/log/**");
		web.ignoring().antMatchers("/vendor/**");
		web.ignoring().antMatchers("/css/**");
		web.ignoring().antMatchers("/tinymce/**");
		web.ignoring().antMatchers("/lang/**");
		web.ignoring().antMatchers("/static/**");
		web.ignoring().antMatchers("/webfonts/**");
		web.ignoring().antMatchers("/plugins/**");
		web.ignoring().antMatchers("/js/**");
		
		web.ignoring().antMatchers("/403");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable() 
			.authorizeRequests().antMatchers("/login","/getConnected","/confirmAccount","/saveClient").permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.loginPage("/login").permitAll()
			.failureUrl("/login_error")
			.defaultSuccessUrl("/dashboard")
			.and()
			.logout().invalidateHttpSession(true)
			.clearAuthentication(true)
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/login").permitAll()
			.and()
			.httpBasic()
			.authenticationEntryPoint(authEntryPoint);
	}
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }
	
	

}
