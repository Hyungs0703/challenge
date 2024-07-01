package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.jwt.UserDetailsImpl;
import jakarta.transaction.Transactional;

public interface LikeService {

    @Transactional
    void postAddLikeCount(Long postId, UserDetailsImpl userDetails);
}
