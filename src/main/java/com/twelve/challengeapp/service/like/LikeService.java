package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.jwt.UserDetailsImpl;
import jakarta.transaction.Transactional;

public interface LikeService {

    @Transactional
    void addLikeToPost(Long postId, UserDetailsImpl userDetails);

    @Transactional
    void deleteLikeFromPost(Long postId, UserDetailsImpl userDetails);

    @Transactional
    void addLikeToComment(Long postId, Long commentId, UserDetailsImpl userDetails);

    @Transactional
    void deleteLikeFromComment(Long postId, Long commentId, UserDetailsImpl userDetails);
}

