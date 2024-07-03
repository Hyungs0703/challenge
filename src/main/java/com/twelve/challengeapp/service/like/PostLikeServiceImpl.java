package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.exception.PostNotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.PostRepository;
import com.twelve.challengeapp.repository.like.PostLikeRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService{

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    @Transactional
    public void addLikeToPost(Long postId, UserDetailsImpl userDetails) {
        Post post = findPostById(postId);
        validatePostLike(userDetails, post);
        post.addLike(userDetails.getUser());
    }

    @Override
    @Transactional
    public void deleteLikeFromPost(Long postId, UserDetailsImpl userDetails) {
        Post post = findPostById(postId);
        postLikeRepository.findByPostAndUser(post, userDetails.getUser())
            .orElseThrow(() -> new IllegalArgumentException("Like not found for the given post and user"));
        post.removeLike(userDetails.getUser());
    }

    @Override
    public List<Post> getPosts(UserDetailsImpl userDetails, int page) {
        return getLikedPostsByUser(userDetails, page, postRepository);
    }

    private void validatePostLike(UserDetailsImpl userDetails, Post post) {
        if (postLikeRepository.existsByUserAndPost(userDetails.getUser(), post)) {
            throw new IllegalArgumentException("You have already liked this post");
        }

        if (post.getUser().getId().equals(userDetails.getUserId())) {
            throw new IllegalArgumentException("You cannot like your own post");
        }
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
            new PostNotFoundException("Post not found with given ID: " + postId));
    }

    private List<Post> getLikedPostsByUser(UserDetailsImpl userDetails, int page, PostRepository postRepo) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepo.findByPostLikesUserOrderByCreatedAtDesc(userDetails.getUser(), pageable);

        return postPage.getContent();
    }

}