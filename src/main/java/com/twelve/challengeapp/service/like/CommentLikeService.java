package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.Comment;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import jakarta.transaction.Transactional;
import java.util.List;

public interface CommentLikeService {


    @Transactional
    void addLikeToComment(Long postId, Long commentId, UserDetailsImpl userDetails);

    @Transactional
    void deleteLikeFromComment(Long postId, Long commentId, UserDetailsImpl userDetails);


    List<Comment> getComments(UserDetailsImpl userDetails, int page);
}

