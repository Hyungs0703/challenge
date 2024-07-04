package com.twelve.challengeapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twelve.challengeapp.config.TestConfig;
import com.twelve.challengeapp.dto.UserRequestDto;
import com.twelve.challengeapp.dto.UserResponseDto;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.UserRole;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.service.UserService;

@WebMvcTest(controllers = UserController.class)
@Import(TestConfig.class)
class UserControllerTest {

	private static final String BASE_URL = "/api/users";
	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_PASSWORD = "TestPass123!";
	private static final String TEST_NICKNAME = "TestNick";
	private static final String TEST_INTRO = "Hello, I'm a test user";
	private static final String TEST_EMAIL = "test@example.com";
	private static final String NEW_NICKNAME = "NewNick";
	private static final String NEW_INTRO = "Updated introduction";
	private static final String NEW_PASSWORD = "NewPass123!";
	private static final Long TEST_POST_LIKE_COUNT = 5L;
	private static final Long TEST_COMMENT_LIKE_COUNT = 3L;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	private UserRequestDto.Register registerDto;
	private UserRequestDto.EditInfo editDto;
	private UserRequestDto.Withdrawal withdrawalDto;
	private UserRequestDto.ChangePassword changePasswordDto;
	private UserResponseDto userResponseDto;
	private User user;
	private UserDetailsImpl userDetails;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.username(TEST_USERNAME)
			.password(TEST_PASSWORD)
			.nickname(TEST_NICKNAME)
			.introduce(TEST_INTRO)
			.email(TEST_EMAIL)
			.role(UserRole.USER)
			.postLikeCount(TEST_POST_LIKE_COUNT)
			.commentLikeCount(TEST_COMMENT_LIKE_COUNT)
			.build();

		userDetails = new UserDetailsImpl(user);

		registerDto = UserRequestDto.Register.builder()
			.username(TEST_USERNAME)
			.password(TEST_PASSWORD)
			.nickname(TEST_NICKNAME)
			.introduce(TEST_INTRO)
			.email(TEST_EMAIL)
			.build();

		editDto = UserRequestDto.EditInfo.builder()
			.password(TEST_PASSWORD)
			.nickname(NEW_NICKNAME)
			.introduce(NEW_INTRO)
			.build();

		withdrawalDto = UserRequestDto.Withdrawal.builder()
			.username(TEST_USERNAME)
			.password(TEST_PASSWORD)
			.build();

		changePasswordDto = UserRequestDto.ChangePassword.builder()
			.username(TEST_USERNAME)
			.password(TEST_PASSWORD)
			.changePassword(NEW_PASSWORD)
			.build();

		userResponseDto = UserResponseDto.builder()
			.user(user)
			.build();
	}

	@Test
	@DisplayName("회원 가입 성공")
	void registerUser_Success() throws Exception {
		// Given
		doNothing().when(userService).registerUser(any(UserRequestDto.Register.class));

		// When
		ResultActions resultActions = mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(registerDto)));

		// Then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("The request has been successfully processed."))
			.andExpect(jsonPath("$.status").value(200));

		verify(userService).registerUser(any(UserRequestDto.Register.class));
	}

	@Test
	@DisplayName("회원 정보 조회")
	void getUser_Success() throws Exception {
		// Given
		when(userService.getUser(any(UserDetailsImpl.class))).thenReturn(userResponseDto);

		// When
		ResultActions resultActions = mockMvc.perform(
			get(BASE_URL).with(user(userDetails)).contentType(MediaType.APPLICATION_JSON));

		// Then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
			.andExpect(jsonPath("$.data.nickname").value(TEST_NICKNAME))
			.andExpect(jsonPath("$.data.introduce").value(TEST_INTRO))
			.andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
			.andExpect(jsonPath("$.data.postLikeCount").value(TEST_POST_LIKE_COUNT))
			.andExpect(jsonPath("$.data.commentLikeCount").value(TEST_COMMENT_LIKE_COUNT));

		verify(userService).getUser(any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("회원 정보 수정 성공")
	void editUser_Success() throws Exception {
		// Given: user 객체의 정보 업데이트
		user.editUserInfo(NEW_NICKNAME, NEW_INTRO);

		// UserResponseDto 객체에 갱신된 user 정보를 담음
		UserResponseDto updatedUserResponseDto = UserResponseDto.builder().user(user).build();

		when(userService.editUser(any(UserRequestDto.EditInfo.class), any(UserDetailsImpl.class))).thenReturn(updatedUserResponseDto);

		// When: 실제 API 요청
		ResultActions resultActions = mockMvc.perform(put(BASE_URL).with(user(userDetails))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(editDto)));

		// Then: 응답과 JSON 검증
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
			.andExpect(jsonPath("$.data.nickname").value(NEW_NICKNAME))
			.andExpect(jsonPath("$.data.introduce").value(NEW_INTRO))
			.andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
			.andExpect(jsonPath("$.data.postLikeCount").value(TEST_POST_LIKE_COUNT))
			.andExpect(jsonPath("$.data.commentLikeCount").value(TEST_COMMENT_LIKE_COUNT));

		// 서비스 메소드 호출 검증
		verify(userService).editUser(any(UserRequestDto.EditInfo.class), any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("회원 탈퇴 성공")
	void withdraw_Success() throws Exception {
		// Given
		doNothing().when(userService).withdraw(any(UserRequestDto.Withdrawal.class), any(UserDetailsImpl.class));

		// When
		ResultActions resultActions = mockMvc.perform(delete(BASE_URL).with(user(userDetails))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(withdrawalDto)));

		// Then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("The request has been successfully processed."))
			.andExpect(jsonPath("$.status").value(200));

		verify(userService).withdraw(any(UserRequestDto.Withdrawal.class), any(UserDetailsImpl.class));
	}

	@Test
	@DisplayName("비밀번호 수정 성공")
	void Change_Password_Success() throws Exception {
		// Given
		doNothing().when(userService).userPasswordChange(any(UserRequestDto.ChangePassword.class),
			any(UserDetailsImpl.class));

		// When
		ResultActions resultActions = mockMvc.perform(put("/api/users/password").with(user(userDetails))
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(changePasswordDto)));

		// Then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("The request has been successfully processed."))
			.andExpect(jsonPath("$.status").value(200));

		verify(userService).userPasswordChange(any(UserRequestDto.ChangePassword.class),
			any(UserDetailsImpl.class));
	}
}
