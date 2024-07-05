package com.twelve.challengeapp.service.like;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.entity.like.PostLike;
import com.twelve.challengeapp.exception.AlreadyException;
import com.twelve.challengeapp.exception.NotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.PostRepository;
import com.twelve.challengeapp.repository.UserRepository;
import com.twelve.challengeapp.repository.like.PostLikeRepository;
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
class PostLikeServiceImplTest {

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostLikeServiceImpl postLikeService;

    Post post;
    UserDetailsImpl userDetails;
    User user;
    PostLike postLike;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id(1L)
            .build();

        userDetails = new UserDetailsImpl(user);

        post = Post.builder()
            .id(1L)
            .user(user)
            .count(1L)
            .postLikes(new HashSet<>())
            .build();

        postLike = PostLike.builder()
            .post(post)
            .user(userDetails.getUser())
            .build();
    }

    @Test
    @DisplayName("게시글 좋아요 성공 테스트")
    void addLikeToPost() {

        User user1 = User.builder().id(2L).build();
        UserDetailsImpl newUserDetails = new UserDetailsImpl(user1);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.existsByUserAndPost(any(), any())).willReturn(false);
        given(userRepository.findById(2L)).willReturn(Optional.of(user1));

        //When
        postLikeService.addLikeToPost(1L, newUserDetails);

        assertThat(post.getCount()).isEqualTo(2L);

    }

    @Test
    @DisplayName("게시글 좋아요 삭제 성공 테스트")
    void deleteLikeFromPost() {
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostAndUser(post, userDetails.getUser())).willReturn(
            Optional.of(postLike));

        //when
        postLikeService.deleteLikeFromPost(1L, userDetails);

        assertThat(post.getCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("해당 게시글 존재하지 않는 테스트")
    void test_NotFoundPost() {
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        //Then
        assertThrows(NotFoundException.class, () ->
            postLikeService.addLikeToPost(1L, userDetails));
    }

    @Test
    @DisplayName("해당 게시물에 이미 좋아요를 누른 경우 실패하는 테스트")
    void test_AlreadyPostLike() {
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        when(postLikeRepository.existsByUserAndPost(any(), any())).thenReturn(true);

        assertThrows(AlreadyException.class, () ->
            postLikeService.addLikeToPost(1L, userDetails));
    }

    @Test
    @DisplayName("본인 게시물의 좋아요를 누르면 실패하는 테스트")
    void test_OwnPostsNotLiked() {
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));

        when(postLikeRepository.existsByUserAndPost(any(), any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            postLikeService.addLikeToPost(1L, userDetails));
    }

}
