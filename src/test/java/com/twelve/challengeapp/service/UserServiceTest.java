package com.twelve.challengeapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.exception.DuplicateException;
import com.twelve.challengeapp.exception.MismatchException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	private UserRequestDto.Register registerDto;
	private User user;
	private UserDetailsImpl userDetails;

	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_PASSWORD = "Password1!";
	private static final String TEST_NICKNAME = "Lee";
	private static final String TEST_INTRO = "Hi!";
	private static final String TEST_EMAIL = "test@example.com";
	private static final String ENCODED_PASSWORD = "encodedPassword";

	@BeforeEach
	void setUp() {
		registerDto = UserRequestDto.Register.builder()
			.username(TEST_USERNAME)
			.password(TEST_PASSWORD)
			.nickname(TEST_NICKNAME)
			.introduce(TEST_INTRO)
			.email(TEST_EMAIL)
			.build();

		user = User.builder()
			.id(1L)
			.username(TEST_USERNAME)
			.password(ENCODED_PASSWORD)
			.nickname(TEST_NICKNAME)
			.introduce(TEST_INTRO)
			.email(TEST_EMAIL)
			.role(UserRole.USER)
			.build();

		userDetails = new UserDetailsImpl(user);
	}

	@Test
	void registerUser_Success() {
		// Given
		when(userRepository.existsByUsername(any())).thenReturn(false);
		when(passwordEncoder.encode(any())).thenReturn(ENCODED_PASSWORD);

		// When
		userService.registerUser(registerDto);

		// Then
		verify(userRepository).save(argThat(
			user -> user.getUsername().equals(TEST_USERNAME) && user.getPassword().equals(ENCODED_PASSWORD)
				&& user.getNickname().equals(TEST_NICKNAME) && user.getIntroduce().equals(TEST_INTRO) && user.getEmail()
				.equals(TEST_EMAIL) && user.getRole() == UserRole.USER));
	}

	@Test
	void registerUser_DuplicateUsername() {
		// Given
		when(userRepository.existsByUsername(any())).thenReturn(true);

		// When & Then
		assertThrows(DuplicateException.class, () -> userService.registerUser(registerDto));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void getUser_Success() {
		// When
		UserResponseDto responseDto = userService.getUser(userDetails);

		// Then
		assertAll(() -> assertEquals(TEST_USERNAME, responseDto.getUsername()),
			() -> assertEquals(TEST_NICKNAME, responseDto.getNickname()),
			() -> assertEquals(TEST_INTRO, responseDto.getIntroduce()),
			() -> assertEquals(TEST_EMAIL, responseDto.getEmail()));
	}

	@Test
	void editUser_Success() {
		// Given
		String newNickname = "New Nickname";
		String newIntro = "New Introduction";
		UserRequestDto.EditInfo editDto = UserRequestDto.EditInfo.builder()
			.password(TEST_PASSWORD)
			.nickname(newNickname)
			.introduce(newIntro)
			.build();

		when(passwordEncoder.matches(any(), any())).thenReturn(true);
		when(userRepository.save(any(User.class))).thenReturn(user);

		// When
		UserResponseDto responseDto = userService.editUser(editDto, userDetails);

		// Then
		assertAll(() -> assertEquals(newNickname, responseDto.getNickname()),
			() -> assertEquals(newIntro, responseDto.getIntroduce()));
		verify(userRepository).save(argThat(
			savedUser -> savedUser.getNickname().equals(newNickname) && savedUser.getIntroduce().equals(newIntro)));
	}

	@Test
	void editUser_PasswordMismatch() {
		// Given
		UserRequestDto.EditInfo editDto = UserRequestDto.EditInfo.builder()
			.password("wrongpassword")
			.nickname(TEST_NICKNAME)
			.introduce(TEST_INTRO)
			.build();

		when(passwordEncoder.matches(any(), any())).thenReturn(false);

		// When & Then
		assertThrows(MismatchException.class, () -> userService.editUser(editDto, userDetails));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void withdraw_Success() {
		// Given
		UserRequestDto.Withdrawal withdrawalDto = UserRequestDto.Withdrawal.builder()
			.username(TEST_USERNAME)
			.password(TEST_PASSWORD)
			.build();

		when(passwordEncoder.matches(any(), any())).thenReturn(true);

		// When
		assertDoesNotThrow(() -> userService.withdraw(withdrawalDto, userDetails));

		// Then
		verify(userRepository).save(argThat(savedUser -> savedUser.getRole() == UserRole.WITHDRAWAL));
	}

	@Test
	void withdraw_UsernameMismatch() {
		// Given
		UserRequestDto.Withdrawal withdrawalDto = UserRequestDto.Withdrawal.builder()
			.username("wronguser")
			.password(TEST_PASSWORD)
			.build();

		// When & Then
		assertThrows(UsernameMismatchException.class, () -> userService.withdraw(withdrawalDto, userDetails));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void withdraw_PasswordMismatch() {
		// Given
		UserRequestDto.Withdrawal withdrawalDto = UserRequestDto.Withdrawal.builder()
			.username(TEST_USERNAME)
			.password("WrongPassword")
			.build();

		when(passwordEncoder.matches(any(), any())).thenReturn(false);

		// When & Then
		assertThrows(MismatchException.class, () -> userService.withdraw(withdrawalDto, userDetails));
		verify(userRepository, never()).save(any(User.class));
	}

	// 비밀번호 암호화 테스트 (단위 테스트)
	@Test
	void passwordEncryption() {
		// Given
		when(userRepository.existsByUsername(any())).thenReturn(false);
		when(passwordEncoder.encode(any())).thenReturn(ENCODED_PASSWORD);

		// When
		userService.registerUser(registerDto);

		// Then
		verify(passwordEncoder).encode(TEST_PASSWORD);
	}
}
