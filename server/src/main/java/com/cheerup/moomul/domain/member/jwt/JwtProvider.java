package com.cheerup.moomul.domain.member.jwt;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JwtProvider {

	private static final String secretKey = "4f9c90be9fcbdc27809c3fcdf19d9fca27a8a713af2b360d1753c3f3680fe825d30ca30de7df1b2318907cd0da174a8dd3d049b0772bace55e57347cd2cfa0f4";

	private static SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public static boolean validateToken(String token) {
		try {
			Claims claims = getClaims(token);
			return !claims.getExpiration().before(new Date());
		} catch (MalformedJwtException e) {
			throw new JwtException("지원되지않는 토큰");
		} catch (ExpiredJwtException e) {
			throw new JwtException("만료된 토큰");
		} catch (IllegalArgumentException e) {
			throw new JwtException("이상한 값의 토큰");
		}
	}

	public String createToken(Long userId, Long validTime){
		Date now=new Date();
		Date expiration=new Date(now.getTime()+validTime);

		Claims claims=Jwts.claims();
		claims.put("userId",userId);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(expiration)
			.signWith(getSigningKey())
			.compact();
	}

	public static Integer getUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("userId", Integer.class);
	}

	private static Claims getClaims(String token) {

		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}




}
