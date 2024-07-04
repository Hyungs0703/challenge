package com.twelve.challengeapp.service.like;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.like.CommentLike;
import com.twelve.challengeapp.exception.CommentNotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.CommentRepository;
import com.twelve.challengeapp.repository.UserRepository;
import com.twelve.challengeapp.repository.like.CommentLikeRepository;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceImplTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentLikeServiceImpl commentLikeService;

    User user;
    UserDetailsImpl userDetails;
    Comment comment;
    Post post;

    @BeforeEach
    void setUp () {
        user = User.builder()
            .id(1L)
            .build();

        userDetails = new UserDetailsImpl(user);

        post = Post.builder()
            .id(1L)
            .title("testTitle")
            .content("testContent")
            .build();

        comment = Comment.builder()
            .id(1L)
            .content("testcontent")
            .count(1L)
            .commentLikes(new HashSet<>())
            .user(userDetails.getUser())
            .build();
    }

    @Test
    @DisplayName("댓글 좋아요 성공 테스트")
    public void test_AddLikeToComment() {
        //Given
        User newUser = User.builder().id(2L).build();
        UserDetailsImpl userDetails1 = new UserDetailsImpl(newUser);
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(commentLikeRepository.existsByUserAndComment(any(),any())).willReturn(false);
        given(userRepository.findById(2L)).willReturn(Optional.of(newUser));

        //When
        commentLikeService.addLikeToComment(1L, 1L, userDetails1);

        //Then
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo("testcontent");
        assertThat(comment.getCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("댓글에 좋아요 삭제 성공 테스트")
    public void test_DeleteLikeFromComment(){
        //Given
        User newUser = User.builder().id(2L).build();
        UserDetailsImpl userDetails1 = new UserDetailsImpl(newUser);
        CommentLike commentLike = new CommentLike(userDetails1.getUser(), comment);
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        given(commentLikeRepository.findByCommentAndUser(comment, userDetails1.getUser())).willReturn(Optional.of(commentLike));

        //When
        commentLikeService.deleteLikeFromComment(1L, 1L, userDetails1);

        //Then
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo("testcontent");
        assertThat(comment.getCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 존재하지 않는 경우 실패")
    public void test_CommentNotFound() {
        //Given
        given(commentRepository.findById(anyLong())).willReturn(Optional.empty());

        // When, Then
        assertThrows(CommentNotFoundException.class, () -> {
            commentLikeService.addLikeToComment(1L, 1L, userDetails); // 특정 댓글 ID와 사용자 ID 전달
        });
    }

    @Test
    @DisplayName("좋아요 댓글을 이미 누른 경우 실패 테스트 코드")
    public void test_AlreadyCommentLike() {
        // Given
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        //When
        when(commentLikeRepository.existsByUserAndComment(userDetails.getUser(), comment)).thenReturn(false);

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            commentLikeService.addLikeToComment(1L, 1L, userDetails);
        }, "You have already comment like");
    }

    @Test
    @DisplayName("댓글 작성자가 좋아요 누르면 실패하는 테스트 코드")
    public void test_OwnCommentNotLiked() {
        //Given
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        //When
        when(commentLikeRepository.existsByUserAndComment(userDetails.getUser(), comment)).thenReturn(true);

        //Then
        assertThrows(IllegalArgumentException.class, () -> {
            commentLikeService.addLikeToComment(1L, 1L, userDetails);
        }, "You cannot like your own comment");

    }
}
