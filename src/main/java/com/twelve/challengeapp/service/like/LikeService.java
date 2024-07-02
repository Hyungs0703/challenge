package com.twelve.challengeapp.service.like;

import com.twelve.challengeapp.entity.User;
import com.twelve.challengeapp.jwt.UserDetailsImpl;
import jakarta.transaction.Transactional;
import java.util.List;

public interface LikeService {
    void postAddLikeCount(Long postId, UserDetailsImpl userDetails);
    List<User> postLikeUserList(Long postId);
    void deletePostLike(Long postId, UserDetailsImpl userDetails);
}

