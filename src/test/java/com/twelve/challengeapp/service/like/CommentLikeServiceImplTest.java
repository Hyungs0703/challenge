package com.twelve.challengeapp.service.like;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
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
            .commentLikes(new HashSet<>())
            .user(userDetails.getUser())
            .build();


    }

    @Test
    @DisplayName("댓글 좋아요 성공 테스트")
    public void addLikeToComment() {
        //Given
        //새로운 유저생성
        User newUser = User.builder().id(2L).build();
        UserDetailsImpl userDetails1 = new UserDetailsImpl(newUser);
//        기존 유저 삭제
//        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        given(commentLikeRepository.existsByUserAndComment(any(),any())).willReturn(false);
        given(userRepository.findById(2L)).willReturn(Optional.of(newUser));
        //when
//        //댓글 생성한 사용자는 좋아요 불가능
//        commentLikeService.addLikeToComment(1L, 1L, userDetails);

        commentLikeService.addLikeToComment(1L, 1L, userDetails1);

        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getContent()).isEqualTo("testcontent");
        assertThat(comment.getCount()).isEqualTo(1);
    }

}
