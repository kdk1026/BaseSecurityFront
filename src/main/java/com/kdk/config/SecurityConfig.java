package com.kdk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.kdk.app.common.security.service.UserDetailsServiceImpl;
import com.kdk.app.common.util.spring.SpringBootPropertyUtil;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 27. kdk	최초작성
 * </pre>
 *
 * JavaDoc에 나온 Example 대로 람다를 안쓰면 너무 복잡해짐...
 *
 * @author kdk
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@SuppressWarnings("unused")
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorizeHttpRequests) ->
				authorizeHttpRequests
					.requestMatchers("/").permitAll()
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
					.requestMatchers("/test/**").permitAll()
					.requestMatchers("/js/**", "/css/**", "/upload/**").permitAll()
					.requestMatchers("/actuator/**").permitAll()
					.anyRequest().authenticated()
			)
			.formLogin((formLogin) ->
				formLogin
					.loginPage("/login")
					.permitAll()
					.failureUrl("/login?error=true")
					.defaultSuccessUrl("/main", true)
			)
			.logout((logout) ->
				logout
					.invalidateHttpSession(true)
					.logoutSuccessUrl("/login")
			)
			.sessionManagement((sessionManagement) ->
				sessionManagement
					.sessionConcurrency((sessionConcurrency) ->
						sessionConcurrency
							.maximumSessions(1)
							.maxSessionsPreventsLogin(true)
							.expiredUrl("/login")
					)
			);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
//        return new MessageDigestPasswordEncoder("SHA-256");	// MD5, SHA-256, SHA-512 등 사용 시
		return new BCryptPasswordEncoder();
	}

	@Bean
	HttpSessionListener httpSessionListener() {
		return new HttpSessionListener() {

			@Override
			public void sessionCreated(HttpSessionEvent se) {
				String sSecuritySessionTimeout = SpringBootPropertyUtil.getProperty("security.session.timeout");
				se.getSession().setMaxInactiveInterval( Integer.parseInt(sSecuritySessionTimeout) );
			}

		};
	}

}
