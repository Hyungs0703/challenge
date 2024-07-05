package com.twelve.challengeapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.twelve.challengeapp.exception.NotFoundException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.twelve.challengeapp.entity.RefreshToken;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

	@InjectMocks
	private RefreshTokenServiceImpl refreshTokenService;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private JwtServiceImpl jwtService;

	private RefreshToken testRefreshToken;

	private User testUser;

	@BeforeEach
	void setUp() {

		testUser = User.builder().username("testuser").role(UserRole.USER).build();

		testRefreshToken = RefreshToken.builder().username("testuser").token("testRefreshToken").build();
	}

	@Test
	void testFindByToken() {
		// Given
		String token = "testRefreshToken";
		when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(testRefreshToken));

		// When
		RefreshToken result = refreshTokenService.findByToken(token);

		// Then
		assertNotNull(result);
		assertEquals(testRefreshToken, result);
	}

	@Test
	void testFindByTokenNotFound() {
		// Given
		String token = "nonExistentToken";
		when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(NotFoundException.class, () -> refreshTokenService.findByToken(token));
	}

	@Test
	void testUpdateRefreshTokenExisting() {
		// Given
		testUser = User.builder().username("testuser").build();

		RefreshToken existingToken = RefreshToken.builder().username("testuser").token("oldToken").build();

		when(refreshTokenRepository.existsByUsername(testUser.getUsername())).thenReturn(true);
		when(refreshTokenRepository.findByUsername(testUser.getUsername())).thenReturn(existingToken);

		RefreshToken updatedToken = RefreshToken.builder().username("testuser").token("newToken").build();

		doNothing().when(jwtService).updateRefreshToken(any(RefreshToken.class), eq(testUser));
		when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(updatedToken);

		// When
		RefreshToken result = refreshTokenService.updateRefreshToken(testUser);

		// Then
		assertNotNull(result);
		assertEquals("newToken", result.getToken());
		verify(jwtService).updateRefreshToken(any(RefreshToken.class), eq(testUser));
		verify(refreshTokenRepository).save(any(RefreshToken.class));
	}

	@Test
	void testUpdateRefreshTokenNew() {
		// Given
		when(refreshTokenRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
		when(jwtService.createRefreshToken(testUser)).thenReturn(testRefreshToken);
		when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

		// When
		RefreshToken result = refreshTokenService.updateRefreshToken(testUser);

		// Then
		assertNotNull(result);
		verify(jwtService).createRefreshToken(testUser);
		verify(refreshTokenRepository).save(testRefreshToken);
	}

	@Test
	void testDeleteRefreshToken() {
		// Given
		String username = "testuser";

		// When
		refreshTokenService.deleteRefreshToken(username);

		// Then
		verify(refreshTokenRepository).deleteByUsername(username);
	}
}
