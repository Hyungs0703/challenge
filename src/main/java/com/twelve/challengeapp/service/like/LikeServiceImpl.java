package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.PostLike;
import com.twelve.challengeapp.exception.PostNotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.CommentRepository;
import com.twelve.challengeapp.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void postAddLikeCount(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new PostNotFoundException("Duplicate Not Found Post"));

        boolean userLikePost = post.getPosts()
                                    .stream()
                                    .anyMatch(postLike ->
                                        postLike.getUser().getId().equals(userDetails.getUserId()));

        if(userLikePost) {
            throw new IllegalArgumentException("You have already Liked this post");

        }
        if(post.getUser().getId().equals(userDetails.getUserId())) {
            throw new IllegalArgumentException("You cannot like your own post");
        }

        post.addLike(userDetails.getUser());

        PostLike.builder()
            .user(userDetails.getUser())
            .post(post)
            .build();
    }

}
