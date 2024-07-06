package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.exception.AlreadyException;
import com.twelve.challengeapp.exception.NotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.PostRepository;
import com.twelve.challengeapp.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addLikeToPost(Long postId, UserDetailsImpl userDetails) {
        Post post = findPostById(postId);
        validatePostLike(userDetails, post);
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() ->
            new NotFoundException("Not Found User"));
        user.addPostLikeCount(post);
        post.addLike(userDetails.getUser());
    }

    @Override
    @Transactional
    public void deleteLikeFromPost(Long postId, UserDetailsImpl userDetails) {
        Post post = findPostById(postId);
        postLikeRepository.findByPostAndUser(post, userDetails.getUser())
            .orElseThrow(() -> new IllegalArgumentException("Like not found for the given post and user"));
        post.removeLike(userDetails.getUser());
//        postLikeRepository.deleteById(postLikeId);
    }

    @Override
    public List<Post> getPosts(UserDetailsImpl userDetails, int page) {
        return getLikedPostsByUser(userDetails, page, postRepository);
    }

    private void validatePostLike(UserDetailsImpl userDetails, Post post) {
        if (postLikeRepository.existsByUserAndPost(userDetails.getUser(), post)) {
            throw new AlreadyException("You have already liked this post");
        }

        if (post.getUser().getId().equals(userDetails.getUserId())) {
            throw new IllegalArgumentException("You cannot like your own post");
        }
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
            new NotFoundException("Post not found with given ID: " + postId));
    }

    private List<Post> getLikedPostsByUser(UserDetailsImpl userDetails, int page, PostRepository postRepo) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepo.findByPostLikesUserOrderByCreatedAtDesc(userDetails.getUser(), pageable);

        return postPage.getContent();
    }

}
