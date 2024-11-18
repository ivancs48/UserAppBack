package com.springboot.calderon.userapp.auth;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;

public class TokenJwtConfig {

	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

}
