package com.springboot.calderon.userapp.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.springboot.calderon.userapp.auth.filter.JwtAuthenticationFilter;
import com.springboot.calderon.userapp.auth.filter.JwtValidationFilter;

@Configuration
public class SpringSecurityConfig {

	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;

	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(
						authz -> authz.requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}")
								.permitAll().requestMatchers(HttpMethod.GET, "/api/users/page/{id}")
								.hasAnyRole("USER", "ADMIN").requestMatchers(HttpMethod.POST, "/api/users/page/")
								.hasRole("ADMIN").requestMatchers(HttpMethod.PUT, "/api/users/page/{id}")
								.hasRole("ADMIN").requestMatchers(HttpMethod.DELETE, "/api/users/page/{id}")
								.hasRole("ADMIN").anyRequest().authenticated())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.addFilter(new JwtAuthenticationFilter(authenticationManager()))
				.addFilter(new JwtValidationFilter(authenticationManager())).csrf(config -> config.disable())
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();

	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfig = new CorsConfiguration();
		// corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
		corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
		corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		corsConfig.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);

		return source;
	}

	@Bean
	FilterRegistrationBean<CorsFilter> corsFilter() {
		FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
				new CorsFilter(corsConfigurationSource()));
		corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return corsBean;
	}

}
