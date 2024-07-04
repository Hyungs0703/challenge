package com.twelve.challengeapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.entity.RefreshToken;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.exception.PasswordMismatchException;
import com.twelve.challengeapp.exception.UserWithdrawalException;
import com.twelve.challengeapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtServiceImpl jwtService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private RefreshTokenServiceImpl refreshTokenService;

	@InjectMocks
	private AuthServiceImpl authService;

	private static final String USERNAME = "testuser";
	private static final String PASSWORD = "testpassword";
	private static final String ENCODED_PASSWORD = "encodedpassword";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String REFRESH_TOKEN = "refresh_token";

	private User user;
	private UserRequestDto.Login loginDto;
	private RefreshToken refreshToken;

	@BeforeEach
	void setUp() {
		user = User.builder().username(USERNAME).password(ENCODED_PASSWORD).role(UserRole.USER).build();

		loginDto = UserRequestDto.Login.builder().username(USERNAME).password(PASSWORD).build();

		refreshToken = RefreshToken.builder().username(USERNAME).token(REFRESH_TOKEN).build();
	}

	@Test
	void login_Success() {
		// Given
		when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
		when(jwtService.generateAccessToken(USERNAME, UserRole.USER)).thenReturn(ACCESS_TOKEN);
		when(refreshTokenService.updateRefreshToken(user)).thenReturn(refreshToken);

		// When
		String result = authService.login(loginDto);

		// Then
		assertEquals(ACCESS_TOKEN, result);
		verify(jwtService).setRefreshTokenAtCookie(refreshToken);
	}

	@Test
	void login_UserNotFound() {
		// Given
		when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UsernameNotFoundException.class, () -> authService.login(loginDto));
	}

	@Test
	void login_UserWithdrawn() {
		// Given
		user.withdrawal(UserRole.WITHDRAWAL);

		when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

		// When & Then
		assertThrows(UserWithdrawalException.class, () -> authService.login(loginDto));
	}

	@Test
	void login_PasswordMismatch() {
		// Given
		when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

		// When & Then
		assertThrows(PasswordMismatchException.class, () -> authService.login(loginDto));
	}

	@Test
	void logout_Success() {
		// Given
		String header = "Bearer " + ACCESS_TOKEN;
		when(jwtService.getAccessTokenFromHeader(header)).thenReturn(ACCESS_TOKEN);
		when(jwtService.extractUsername(ACCESS_TOKEN)).thenReturn(USERNAME);

		// When
		authService.logout(header);

		// Then
		verify(refreshTokenService).deleteRefreshToken(USERNAME);
		verify(jwtService).deleteRefreshTokenAtCookie();
	}
}
