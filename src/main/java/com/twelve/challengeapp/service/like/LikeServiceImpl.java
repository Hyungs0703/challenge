package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.PostLike;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.exception.PostNotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.PostLikeRepository;
import com.twelve.challengeapp.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    @Transactional
    public void postAddLikeCount(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new PostNotFoundException("Post not found with given ID: " + postId));

        boolean userLikePost = post.getPostLikes()
                                    .stream()
                                    .anyMatch(postLike ->
                                        postLike.getUser().getId().equals(userDetails.getUserId()));

        if (userLikePost) {
            throw new IllegalArgumentException("You have already liked this post");
        }

        if (post.getUser().getId().equals(userDetails.getUserId())) {
            throw new IllegalArgumentException("You cannot like your own post");
        }

        post.addCount();
        postLikeRepository.save(new PostLike(userDetails.getUser(), post));
    }

    @Override
    public List<User> postLikeUserList(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with given ID: " + postId);
        }

        List<PostLike> postLikeList = postLikeRepository.findAllByPostId(postId);


        return postLikeList.stream()
                            .map(PostLike::getUser)
                            .toList();
    }

    @Override
    @Transactional
    public void deletePostLike(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
            new PostNotFoundException("Post not found with given ID: " + postId));

        PostLike postLike = postLikeRepository.findByPostAndUser(post, userDetails.getUser());

        if (postLike == null) {
            throw new IllegalArgumentException("Like not found for the given post and user");
        }

        post.removeCount();
        postLikeRepository.delete(postLike);
    }
}
