package com.twelve.challengeapp.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

	private static final Long USER_ID = 1L;
	private static final String USERNAME = "testuser";
	private static final String PASSWORD = "password";
	private static final String NICKNAME = "Test User";
	private static final String INTRO = "Hello, I'm a test user";
	private static final String EMAIL = "test@example.com";

	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(USER_ID)
			.username(USERNAME)
			.password(PASSWORD)
			.nickname(NICKNAME)
			.introduce(INTRO)
			.email(EMAIL)
			.role(UserRole.USER)
			.build();
	}

	@Test
	void testUserBuilder() {
		assertEquals(USER_ID, user.getId());
		assertEquals(USERNAME, user.getUsername());
		assertEquals(PASSWORD, user.getPassword());
		assertEquals(NICKNAME, user.getNickname());
		assertEquals(INTRO, user.getIntroduce());
		assertEquals(EMAIL, user.getEmail());
		assertEquals(UserRole.USER, user.getRole());
	}

	@Test
	void testEditUserInfo() {
		String newNickname = "New Nickname";
		String newIntro = "New Introduction";
		user.editUserInfo(newNickname, newIntro);

		assertEquals(newNickname, user.getNickname());
		assertEquals(newIntro, user.getIntroduce());
	}

	@Test
	void testWithdraw() {
		user.withdrawal(UserRole.WITHDRAWAL);
		assertEquals(UserRole.WITHDRAWAL, user.getRole());
	}

	@Test
	void testEquals() {
		User sameUser = User.builder().id(USER_ID).build();
		User differentUser = User.builder().id(2L).build();

		assertEquals(user, sameUser);
		assertNotEquals(user, differentUser);
	}

	@Test
	void testAddAndRemovePost() {
		Post post = new Post();

		user.addPost(post);
		assertTrue(user.getPosts().contains(post));
		assertEquals(user, post.getUser());

		user.removePost(post);
		assertFalse(user.getPosts().contains(post));
		assertNull(post.getUser());
	}
}
