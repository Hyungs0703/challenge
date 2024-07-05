package com.twelve.challengeapp.service.like;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
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
    void deleteLikeFromPost() {
    }
}
