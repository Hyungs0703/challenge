package com.twelve.challengeapp.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.twelve.challengeapp.config.JwtConfig;
import com.twelve.challengeapp.entity.RefreshToken;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.jwt.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JwtService")
@Service
public class JwtServiceImpl implements JwtService {

	private static final String REFRESH_TOKEN_COOKIE_NAME = "RefreshToken";
	private static final int COOKIE_MAX_AGE = 0;

	@Override
	public String extractUsername(String token) {
		return JwtTokenProvider.extractUsername(token, getSecretKey());
	}

	@Override
	public Object extractRole(String token) {
		return JwtTokenProvider.extractRole(token, JwtConfig.staticAuthorizationKey, getSecretKey());
	}

	@Override
	public Boolean isTokenExpired(String token) {
		return JwtTokenProvider.isTokenExpired(token, getSecretKey());
	}

	@Override
	public Boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			log.error("Invalid JWT signature.");
		} catch (ExpiredJwtException e) {
			log.error("Expired JWT token.");
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token.");
		} catch (IllegalArgumentException e) {
			log.error("JWT claims is empty.");
		}
		return false;
	}

	@Override
	public Boolean isAuthorizationHeaderMissing(HttpServletRequest request) {
		return request.getHeader(JwtConfig.staticHeader) == null;
	}

	@Override
	public RefreshToken createRefreshToken(User user) {

		String token = generateRefreshToken(user.getUsername(), user.getRole());

		RefreshToken refreshToken = RefreshToken.builder()
			.username(user.getUsername())
			.token(token)
			.expirationAt(LocalDateTime.now().plusSeconds(JwtConfig.staticRefreshTokenExpirationSecond))
			.build();

		log.info("Create Refresh Token: " + refreshToken);
		return refreshToken;
	}

	@Override
	public void updateRefreshToken(RefreshToken refreshToken, User user) {

		String token = generateRefreshToken(user.getUsername(), user.getRole());

		log.info("Update Before: " + refreshToken.getToken() + ", " + refreshToken.getExpirationAt());

		refreshToken.updateToken(token);
		refreshToken.updateExpirationAt(LocalDateTime.now().plusSeconds(JwtConfig.staticRefreshTokenExpirationSecond));

		log.info("Update After: " + refreshToken.getToken() + ", " + refreshToken.getExpirationAt());
	}

	// Access Token 생성
	@Override
	public String generateAccessToken(String username, Object role) {

		return JwtTokenProvider.generateToken(username, JwtConfig.staticAuthorizationKey, role,
			JwtConfig.staticAccessTokenExpiration, getSecretKey());

	}

	private String generateRefreshToken(String username, Object role) {

		return JwtTokenProvider.generateToken(username, JwtConfig.staticAuthorizationKey, role,
			JwtConfig.staticRefreshTokenExpiration, getSecretKey());

	}

	@Override
	public String getAccessTokenFromHeader(String header) {

		if (header.startsWith(JwtConfig.staticTokenPrefix)) {
			return header.replace(JwtConfig.staticTokenPrefix, "");
		}
		return null;
	}

	// header에서 access token 가져오기
	@Override
	public String getAccessTokenFromRequest(HttpServletRequest request) {

		return getAccessTokenFromHeader(request.getHeader(JwtConfig.staticHeader));
	}

	// cookie에서 refresh token 가져오기
	@Override
	public String getRefreshTokenFromRequest(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// Refresh Token 쿠키에 저장
	@Override
	public void setRefreshTokenAtCookie(RefreshToken refreshToken) {
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken.getToken());
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setMaxAge(JwtConfig.staticRefreshTokenExpirationSecond);
		HttpServletResponse response = ((ServletRequestAttributes)Objects.requireNonNull(
			RequestContextHolder.getRequestAttributes())).getResponse();

		if (response != null) {
			response.addCookie(cookie);
		}
	}

	@Override
	public void setHeaderWithAccessToken(HttpServletResponse response, String accessToken) {
		response.setHeader(JwtConfig.staticHeader, JwtConfig.staticTokenPrefix + accessToken);
	}

	@Override
	public void deleteRefreshTokenAtCookie() {
		// 덮어 쓰기
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		HttpServletResponse response = ((ServletRequestAttributes)Objects.requireNonNull(
			RequestContextHolder.getRequestAttributes())).getResponse();

		if (response != null) {
			response.addCookie(cookie);
		}
	}

	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(JwtConfig.staticSecretKey.getBytes(StandardCharsets.UTF_8));
	}
}
