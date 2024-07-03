package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.CommentLike;
import com.twelve.challengeapp.entity.Post;
import com.twelve.challengeapp.entity.PostLike;
import com.twelve.challengeapp.exception.CommentNotFoundException;
import com.twelve.challengeapp.exception.PostNotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.CommentLikeRepository;
import com.twelve.challengeapp.repository.CommentRepository;
import com.twelve.challengeapp.repository.PostLikeRepository;
import com.twelve.challengeapp.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;


    @Override
    @Transactional
    public void addLikeToPost(Long postId, UserDetailsImpl userDetails) {
        Post post = findPostById(postId);

        if (postLikeRepository.existsByUserAndPost(userDetails.getUser(), post)) {
            throw new IllegalArgumentException("You have already liked this post");
        }

        if (post.getUser().getId().equals(userDetails.getUserId())) {
            throw new IllegalArgumentException("You cannot like your own post");
        }

        post.addLike(userDetails.getUser());
    }


    @Override
    @Transactional
    public void deleteLikeFromPost(Long postId, UserDetailsImpl userDetails) {
        Post post = findPostById(postId);

        PostLike postLike = postLikeRepository.findByPostAndUser(post, userDetails.getUser());

        if (postLike == null) {
            throw new IllegalArgumentException("Like not found for the given post and user");
        }

        post.removeLike(userDetails.getUser());
    }

    @Override
    @Transactional
    public void addLikeToComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        postExistById(postId);
        Comment comment = commentFindById(commentId);

        if (commentLikeRepository.existsByUserAndComment(userDetails.getUser(), comment)) {
            throw new IllegalArgumentException("You have already liked this post");
        }

        if (comment.getUser().getId().equals(userDetails.getUserId())) {
            throw new IllegalArgumentException("You cannot like your own comment");
        }

        comment.addLike(userDetails.getUser());
    }

    @Override
    @Transactional
    public void deleteLikeFromComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        postExistById(postId);
        Comment comment = commentFindById(commentId);

        CommentLike commentLike = commentLikeRepository.findByCommentAndUser(comment, userDetails.getUser());

        if (commentLike == null) {
            throw new IllegalArgumentException("Like not found for the given comment and user");
        }

        comment.removeLike(userDetails.getUser());
    }

    @Override
    public List<Post> getPosts(UserDetailsImpl userDetails, int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByPostLikesUserOrderByCreatedAtDesc(
            userDetails.getUser(), pageable);

        if (postPage.isEmpty()) {
            return Collections.emptyList(); // 데이터가 없으면 빈 리스트 반환
        } else {
            return postPage.getContent(); // 페이지네이션된 데이터 반환
        }
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
            new PostNotFoundException("Post not found with given ID: " + postId));
    }

    private void postExistById(Long postId){
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with given ID: " + postId);
        }
    }

    private Comment commentFindById(Long commentId) {
        return  commentRepository.findById(commentId).orElseThrow(() ->
            new CommentNotFoundException("Comment not found with given ID: " + commentId));
    }


}
