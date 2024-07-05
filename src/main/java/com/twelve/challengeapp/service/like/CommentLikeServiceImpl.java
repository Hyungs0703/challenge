package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.exception.AlreadyException;
import com.twelve.challengeapp.exception.DuplicateException;
import com.twelve.challengeapp.exception.NotFoundException;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import com.twelve.challengeapp.repository.UserRepository;
import com.twelve.challengeapp.repository.like.CommentLikeRepository;
import com.twelve.challengeapp.repository.CommentRepository;
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
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public void addLikeToComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        Comment comment = findCommentById(commentId);
        validateCommentLike(userDetails, comment);
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() ->
            new NotFoundException("Nof Found User"));

        user.addCommentLikeCount(comment);
        comment.addLike(userDetails.getUser());
    }

    @Override
    @Transactional
    public void deleteLikeFromComment(Long postId, Long commentId, UserDetailsImpl userDetails) {
        Comment comment = findCommentById(commentId);
        commentLikeRepository.findByCommentAndUser(comment, userDetails.getUser())
            .orElseThrow(() -> new IllegalArgumentException("Like not found for the given comment and user"));
        comment.removeLike(userDetails.getUser());
    }


    @Override
    public List<Comment> getComments(UserDetailsImpl userDetails, int page) {
        return getLikedCommentsByUser(userDetails, page, commentRepository);
    }

    private void validateCommentLike(UserDetailsImpl userDetails, Comment comment) {
        if (commentLikeRepository.existsByUserAndComment(userDetails.getUser(), comment)) {
            throw new AlreadyException("You have already liked this comment");
        }

        if (comment.getUser().getId().equals(userDetails.getUserId())) {
            throw new IllegalArgumentException("You cannot like your own comment");
        }
    }


    private Comment findCommentById(Long commentId) {
        return  commentRepository.findById(commentId).orElseThrow(() ->
            new NotFoundException("Comment not found with given ID: " + commentId));
    }


    private List<Comment> getLikedCommentsByUser(UserDetailsImpl userDetails, int page, CommentRepository commentRepo) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepo.findByCommentLikesUserOrderByCreatedAtDesc(userDetails.getUser(), pageable);

        return commentPage.getContent();
    }

}
