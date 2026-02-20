package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.smart.UserDetailsService.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;

	public SecurityConfig(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());

		return provider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/user/**").hasRole("USER").anyRequest().permitAll())
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login") // handled by Spring Security
						.defaultSuccessUrl("/user/index", true).failureUrl("/login?error") // 👈 THIS enables error
																							// popup
						.permitAll())
				.authenticationProvider(authenticationProvider())
				.logout(logout -> logout.logoutSuccessUrl("/login?logout"));

		return http.build();
	}
}
