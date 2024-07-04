package com.twelve.challengeapp.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UserRequestDtoTest {

	private Validator validator;
	private static final String VALID_USERNAME = "user123";
	private static final String VALID_PASSWORD = "Password123!";
	private static final String VALID_NICKNAME = "Nick";
	private static final String VALID_INTRO = "Hello";
	private static final String VALID_EMAIL = "user@example.com";

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testRegisterDto_ValidInput() {
		UserRequestDto.Register registerDto = createValidRegisterDto(VALID_USERNAME);
		Set<ConstraintViolation<UserRequestDto.Register>> violations = validator.validate(registerDto);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testRegisterDto_InvalidUsername() {
		UserRequestDto.Register registerDto = createValidRegisterDto("User123");
		Set<ConstraintViolation<UserRequestDto.Register>> violations = validator.validate(registerDto);
		assertFalse(violations.isEmpty());
		assertTrue(hasViolationForField(violations, "username"));
	}

	@Test
	void testLoginDto_ValidInput() {
		UserRequestDto.Login loginDto = UserRequestDto.Login.builder()
			.username(VALID_USERNAME)
			.password(VALID_PASSWORD)
			.build();
		Set<ConstraintViolation<UserRequestDto.Login>> violations = validator.validate(loginDto);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testLoginDto_InvalidPassword() {
		UserRequestDto.Login loginDto = UserRequestDto.Login.builder()
			.username(VALID_USERNAME)
			.password("password")
			.build();
		Set<ConstraintViolation<UserRequestDto.Login>> violations = validator.validate(loginDto);
		assertFalse(violations.isEmpty());
		assertTrue(hasViolationForField(violations, "password"));
	}

	@Test
	void testEditUserInfoDto_ValidInput() {
		UserRequestDto.EditInfo editInfoDto = UserRequestDto.EditInfo.builder()
			.password(VALID_PASSWORD)
			.nickname(VALID_NICKNAME)
			.introduce(VALID_INTRO)
			.build();
		Set<ConstraintViolation<UserRequestDto.EditInfo>> violations = validator.validate(editInfoDto);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testEditUserInfoDto_InvalidNickname() {
		UserRequestDto.EditInfo editInfoDto = UserRequestDto.EditInfo.builder()
			.password(VALID_PASSWORD)
			.nickname("")
			.introduce(VALID_INTRO)
			.build();
		Set<ConstraintViolation<UserRequestDto.EditInfo>> violations = validator.validate(editInfoDto);
		assertFalse(violations.isEmpty());
		assertTrue(hasViolationForField(violations, "nickname"));
	}

	@Test
	void testWithdrawalDto_ValidInput() {
		UserRequestDto.Withdrawal withdrawalDto = UserRequestDto.Withdrawal.builder()
			.username(VALID_USERNAME)
			.password(VALID_PASSWORD)
			.build();
		Set<ConstraintViolation<UserRequestDto.Withdrawal>> violations = validator.validate(withdrawalDto);
		assertTrue(violations.isEmpty());
	}

	@Test
	void testWithdrawalDto_InvalidUsername() {
		UserRequestDto.Withdrawal withdrawalDto = UserRequestDto.Withdrawal.builder()
			.username("us")
			.password(VALID_PASSWORD)
			.build();
		Set<ConstraintViolation<UserRequestDto.Withdrawal>> violations = validator.validate(withdrawalDto);
		assertFalse(violations.isEmpty());
		assertTrue(hasViolationForField(violations, "username"));
	}

	private UserRequestDto.Register createValidRegisterDto(String username) {
		return UserRequestDto.Register.builder()
			.username(username)
			.password(VALID_PASSWORD)
			.nickname(VALID_NICKNAME)
			.introduce(VALID_INTRO)
			.email(VALID_EMAIL)
			.build();
	}

	private <T> boolean hasViolationForField(Set<ConstraintViolation<T>> violations, String fieldName) {
		return violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(fieldName));
	}
}
