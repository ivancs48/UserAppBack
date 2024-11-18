package com.springboot.calderon.userapp.auth.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.calderon.userapp.auth.SimpleGrantedAuthorityJsonCreator;
import com.springboot.calderon.userapp.auth.TokenJwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Arrays;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidationFilter extends BasicAuthenticationFilter {

	public JwtValidationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String token = request.getHeader(TokenJwtConfig.HEADER_AUTHORIZATION);

		if (token == null) {
			chain.doFilter(request, response);
			return;
		}

		token = token.replace("Bearer ", "");
		try {
			Claims claim = Jwts.parser().verifyWith(TokenJwtConfig.SECRET_KEY).build().parseSignedClaims(token)
					.getPayload();
			String username = claim.getSubject();
			// String username2 = (String) claim.get("username");
			Object authoritiesClaim = claim.get("authorities");

			Collection<? extends GrantedAuthority> roles = Arrays.asList(new ObjectMapper()
					.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
					.readValue(authoritiesClaim.toString().getBytes(), SimpleGrantedAuthority[].class));

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null,
					roles);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			chain.doFilter(request, response);

		} catch (JwtException e) {
			Map<String, String> body = new HashMap<>();
			body.put("error", e.getMessage());
			body.put("menssage", "El token es invalido!");
			
			response.getWriter().write(new ObjectMapper().writeValueAsString(body));
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}

	}

}
