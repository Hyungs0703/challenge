package com.twelve.challengeapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PostTest {
    // Test-related constants
    private static final Long POST_ID = 1L;
    private static final String TITLE = "test title";
    private static final String CONTENT = "test content";
    private static final String USERNAME = "test user";
    private static final String PASSWORD = "password";
    private static final String NICKNAME = "Test User";
    private static final String INTRO = "Hello, I'm a test user";
    private static final String EMAIL = "test@example.com";

    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(POST_ID)
            .username(USERNAME)
            .password(PASSWORD)
            .nickname(NICKNAME)
            .introduce(INTRO)
            .email(EMAIL)
            .role(UserRole.USER)
            .build();

        post = Post.builder()
            .title(TITLE)
            .content(CONTENT)
            .build();

        post.setUser(user);
    }

    @Test
    void testPostBuilder() {
        assertEquals(TITLE, post.getTitle());
        assertEquals(CONTENT, post.getContent());
        assertNull(post.getId()); // ID는 생성 전에는 null 이어야합니다
    }

    @Test
    void testUpdate() {
        String newTitle = "New Title";
        String newContent = "New Content";
        post.update(newTitle, newContent);

        assertEquals(newTitle, post.getTitle());
        assertEquals(newContent, post.getContent());
    }

    @Test
    void testSetUser() {
        User newUser = User.builder().id(2L).username("newuser").password("password").build();
        post.setUser(newUser);

        assertEquals(newUser, post.getUser());
    }

    @Test
    void testAddAndRemoveComment() {
        Comment comment = new Comment();
        post.addComment(comment);

        assertTrue(post.getComments().contains(comment));
        assertEquals(post, comment.getPost());

        post.removeComment(comment);

        assertFalse(post.getComments().contains(comment));
        assertNull(comment.getPost());
    }

    @Test
    void testEquals() {
        Post samePost1 = Post.builder().id(POST_ID).title(TITLE).content(CONTENT).build();
        samePost1.setUser(user);

        Post samePost2 = Post.builder().id(POST_ID).title(TITLE).content(CONTENT).build();
        samePost2.setUser(user);

        User differentUser = User.builder().id(2L).username("otheruser").password("otherpassword").build();
        Post differentPost = Post.builder().id(2L).title("Different Title").content("Different Content").build();
        differentPost.setUser(differentUser);

        assertEquals(samePost1, samePost2);
        assertNotEquals(samePost1, differentPost);
    }

    @Test
    void testUserAssociation() {
        assertEquals(user, post.getUser());
        assertTrue(user.getPosts().contains(post));
    }
}
